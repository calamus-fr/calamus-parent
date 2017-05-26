package fr.calamus.common.mail.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadCompteurDeMails extends Thread {

	private final int maxHoraire;
	private Map<Long, Integer> nbMailsEnvoyes;
	private List<Long> moments;
	private boolean hasToLoop;
	private static final Log log = LogFactory.getLog(ThreadCompteurDeMails.class);

	public ThreadCompteurDeMails(int max){
		super("compteurDeMails");
		maxHoraire = max;
		nbMailsEnvoyes = Collections.synchronizedMap(new HashMap<Long, Integer>());
		moments = Collections.synchronizedList(new ArrayList<Long>());
		hasToLoop = true;
	}

	@Override
	public void run(){
		while (hasToLoop) {
			Long t0 = System.currentTimeMillis();
			Long moinsUneHeure = t0 - 3600 * 1000;
			synchronized (nbMailsEnvoyes) {
				synchronized (moments) {
					for (int i = 0; i < moments.size(); i++) {
						Long t = moments.get(0);
						if (t < moinsUneHeure) {
							nbMailsEnvoyes.remove(t);
							moments.remove(0);
						}
					}
				}
			}
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		interrupt();
	}

	public synchronized int getRestantsPourLHeure(){
		int n = 0;
		synchronized (nbMailsEnvoyes) {
			synchronized (moments) {
				for (int i = 0; i < moments.size(); i++) {
					Long t = moments.get(0);
					n += nbMailsEnvoyes.get(t);
				}
			}
		}
		log.debug(n + " mails envoyés dans l'heure");
		return maxHoraire - n;
	}

	public synchronized void compterMails(int nb){
		Long t = System.currentTimeMillis();
		moments.add(t);
		nbMailsEnvoyes.put(t, nb);
	}
}
