/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.db.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author haerwynn
 */
public abstract class DbAccessFactory{

	private static final Map<String, DbAccess> instances = Collections.synchronizedMap(new HashMap<String, DbAccess>());
	private static final Log log = LogFactory.getLog(DbAccessFactory.class);
	//private stat
	private static long timeOut=120*1000;

	public static DbAccess init(String url,String user,String pwd) {
		DbAccess db = new DbAccess(url, user, pwd, null);
		instances.put(null, db);
		return db;
	}

	public static DbAccess newInstance() {
		//return (T) ((T)getInstance()).getNewInstance();
		//DbAccessFactory t = getInstance();
		return instances.get(null).newAccess();
	}

	public static DbAccess getInstance() {
		return getInstance(null);
	}

	public static DbAccess getInstance(String id) {
		if (instances.get(null) == null) {
			throw new RuntimeException("Not inited !");
		}
		if (instances.get(id) == null || instances.get(id).isClosed()) {
			if (instances.get(id) == null) {
				log.debug("creating new instance : " + id);
				instances.put(id, newInstance());
			} else {
				log.debug("reconnecting instance : " + id);
				DbAccess db = instances.get(id);
				db.reconnect();
			}
			log.debug(" " + instances.size() + " instances");
		}
		return (DbAccess) instances.get(id);
	}

	public static long timeOut(){
		return timeOut;
	}
	private static void launchInstancesObserver() {
		/*Thread run = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.currentThread().wait(10000);
						synchronized (instances) {
							long now = System.currentTimeMillis();
							List<String> toRemove = new ArrayList<>();
							for (String id : instances.keySet()) {
								AdeDbFactory db = instances.get(id);
								if (db == null || now > db.lastUsedTime() + db.adeTimeOut()) {
									toRemove.add(id);
								}
							}
							log.debug("removing " + toRemove.size() + " instance(s)");
							for (String id : toRemove) {
								instances.remove(id);
							}
						}
					} catch (InterruptedException ex) {
						log.warn(ex);
					}
				}
			}
		};
		run.start();*/
		Timer timer = new Timer("DbAccessFactory-instancesRemover", true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				synchronized (instances) {
					long now = System.currentTimeMillis();
					List<String> toRemove = new ArrayList<>();
					for (String id : instances.keySet()) {
						DbAccess db = instances.get(id);
						if (id!=null && (db == null || now > db.lastUsedTime() + timeOut())) {
							toRemove.add(id);
						}
					}
					for (String id : toRemove) {
						instances.remove(id);
					}
					if(toRemove.size()>0)log.debug("removed " + toRemove.size() + " instance(s); remaining "+instances.size());
				}
			}
		};
		timer.schedule(task, 30000, 10000);
	}

}
