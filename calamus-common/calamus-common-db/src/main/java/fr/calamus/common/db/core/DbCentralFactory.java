/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.db.core;

import java.text.SimpleDateFormat;
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
public abstract class DbCentralFactory<T extends DbCentralFactory> implements IDbFactory<T>{
	protected static final Map<String, DbCentralFactory<?>> instances = Collections.synchronizedMap(new HashMap<String, DbCentralFactory<?>>());
	private static final Log log = LogFactory.getLog(DbCentralFactory.class);
	private static SimpleDateFormat frDateFormatter;
	private static SimpleDateFormat usDateFormatter;

	public static <T extends DbCentralFactory> T init(Class<T> factoryClass) {
		try {
			T db = (T) factoryClass.newInstance();
			instances.put(null, db);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		launchInstancesObserver();
		return null;
	}
	public static SimpleDateFormat getFrDateFormatter() {
		if (frDateFormatter == null) {
			frDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		}
		return frDateFormatter;
	}

	public boolean isConnectionOk() {
		return dbAccess()!=null&&!dbAccess().isClosed();
	}

	public static SimpleDateFormat getUsDateFormatter() {
		if (usDateFormatter == null) {
			usDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		}
		return usDateFormatter;
	}
	public static DbCentralFactory getInstance(String id) {
		if (instances.get(null) == null) {
			throw new RuntimeException("Not inited !");
		}
		if (instances.get(id) == null || instances.get(id).isClosed()) {
			if (instances.get(id) == null) {
				log.debug("creating new instance : " + id);
				instances.put(id, newInstance());
			} else {
				log.debug("reconnecting instance : " + id);
				DbCentralFactory db = instances.get(id);
				db.reconnect();
			}
			log.debug(" " + instances.size() + " instances");
		}
		return (DbCentralFactory) instances.get(id);
	}
	public boolean isClosed() {
		return dbAccess().isClosed();
	}
	public static DbCentralFactory newInstance() {
		//return (T) ((T)getInstance()).getNewInstance();
		DbCentralFactory t = getInstance();
		return (DbCentralFactory) t.getNewInstance();
	}

	public static DbCentralFactory getInstance() {
		return getInstance(null);
	}
	protected void reconnect() {
		dbAccess().reconnect();
	}

	protected abstract long cnxTimeOut();
	private static void launchInstancesObserver() {
		Timer timer = new Timer("DbFactory-instancesRemover", true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				synchronized (instances) {
					long now = System.currentTimeMillis();
					List<String> toRemove = new ArrayList<>();
					for (String id : instances.keySet()) {
						DbCentralFactory db = instances.get(id);
						if (id!=null && (db == null || now > db.lastUsedTime() + db.cnxTimeOut())) {
							toRemove.add(id);
						}
					}
					for (String id : toRemove) {
						DbCentralFactory db = instances.get(id);
						if(db!=null)db.close();
						instances.remove(id);
					}
					if(toRemove.size()>0)log.debug("removed " + toRemove.size() + " instance(s); remaining "+instances.size());
				}
			}
		};
		timer.schedule(task, 30000, 10000);
	}
	public long lastUsedTime() {
		return dbAccess().lastUsedTime();
	}

	public void close() {
		dbAccess().close();
	}

}
