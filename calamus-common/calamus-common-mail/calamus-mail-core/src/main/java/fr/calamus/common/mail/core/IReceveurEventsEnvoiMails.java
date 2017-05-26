package fr.calamus.common.mail.core;

import fr.calamus.common.mail.model.EtatsLivraisonMessage;
import javax.mail.event.TransportEvent;

public interface IReceveurEventsEnvoiMails {

	public void ajouterLigne(String ligne);
	public void addTransportEvent(EtatsLivraisonMessage state, TransportEvent e);
	public void failed(String cause);
	public void success();
}
