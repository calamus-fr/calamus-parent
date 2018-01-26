package fr.calamus.common.mail.service;

import fr.calamus.common.mail.model.EMailDataBean;
import fr.calamus.common.mail.model.ElementListeNoire;
import fr.calamus.common.mail.model.IDestinataireMap;
import fr.calamus.common.mail.model.ListeDestinataires;
import fr.calamus.common.mail.model.ListeMailing;
import fr.calamus.common.mail.model.ModeleMailMap;
import java.util.List;
import java.util.Map;

public interface IServiceMail {
	Object[][] getMailsData(String type);

	int saveMail(EMailDataBean em, String type, boolean ok);

	EMailDataBean getEMailDataBean(int idMail);

	List<ModeleMailMap> getModelesMail();

	Map<Integer, List<Integer>> getMapIdsSocietesDansListesMailing();

	List<ListeMailing> getListesMailing();

	boolean saveListeMailing(ListeMailing l);

	void ajouterAListe(String nomListe, ListeDestinataires prospects);
	void ajouterAListe(String nomListe, List<? extends IDestinataireMap> prospects);

	int saveModele(ModeleMailMap modeleSelect);

	List<String> getTousEMailsContacts();

	boolean deleteModele(ModeleMailMap modeleSelect);

	List<ElementListeNoire> getListeNoire();

	boolean deleteMails(int[] ids);

	boolean ajouterAListeNoire(String a);

	boolean supprimerMailsListeNoire(List<String> l);

	boolean modifierMailListeNoire(ElementListeNoire elt, String a);

	boolean supprimerDestinatairesDeLaListe(ListeMailing listeMailing, int[] ids);

	boolean deleteListesMailing(int[] s);

	public ListeMailing getListeMailing(String titre);

	public ListeMailing getListeMailing(int id);

}
