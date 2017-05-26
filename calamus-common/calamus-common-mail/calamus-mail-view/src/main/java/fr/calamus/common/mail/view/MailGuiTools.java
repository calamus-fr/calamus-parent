package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.DestinataireMap;
import fr.calamus.common.mail.model.IDestinataireMap;
import fr.calamus.common.mail.model.ListeDestinataires;
import fr.calamus.common.mail.model.ListeMailing;
import fr.calamus.common.mail.model.ParametresSmtp;
import fr.calamus.view.tools.GuiTools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MailGuiTools {

	public static final String CHOIX_LISTE_NULL = " Créer une nouvelle liste...";
	private static Log log=LogFactory.getLog(MailGuiTools.class);
	private static boolean paramsMailingOk=false;

	public static void ouvrirFenetreParamsEnvoi(){
		/*
		 * if(fParamsEnvoi==null){ fParamsEnvoi=new JFrame("Paramètres SMTP"); fParamsEnvoi.setLocationRelativeTo(null);
		 * fParamsEnvoi.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); final PanneauParamsMail pannParams = new
		 * PanneauParamsMail(); fParamsEnvoi.add(pannParams, BorderLayout.CENTER); fParamsEnvoi.pack();
		 * pannParams.redimensionner(600, 250); fParamsEnvoi.addComponentListener(new ComponentAdapter() {
		 * @Override public void componentResized(ComponentEvent e){ pannParams.redimensionner(fParamsEnvoi.getWidth(),
		 * fParamsEnvoi.getHeight()); } }); fParamsEnvoi.setSize(new Dimension(600, 250)); }
		 * fParamsEnvoi.setVisible(true);
		 */
		GuiTools.ouvrirDialogue(null, new PanneauParamsMail(), "Paramètres SMTP");
	}

	public static void ecrireMail(IDestinataireMap soc){
		String mailClient = soc.getMail();
		if (mailClient == null || mailClient.equals("")) {
			int n = JOptionPane.showConfirmDialog(null, "Le destinataire n'a pas d'adresse e-mail enregistrée.\n"
					+ "Voulez-vous tout de même ouvrir la fenêtre d'envoi d'e-mail?", "Pas d'adresse e-mail",
					JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.NO_OPTION)
				return;
		}
		// log.debug("ecrireMail: ControleurMail.getInstance().getModele()="+ControleurMail.getInstance().getModele());
		checkParamsMailing();
		GuiTools.ouvrirDialogue(null, new PanneauEcritureMail(soc), "Ecriture d'un e-mail");
	}

	public static void modifierMail(int idMail){
		/*
		 * String mailClient=soc.getMail(); if(mailClient==null || mailClient.equals("")){ int
		 * n=AAOptionPane.showConfirmDialog(null, "Le destinataire n'a pas d'adresse e-mail enregistrée.\n" +
		 * "Voulez-vous tout de même ouvrir la fenêtre d'envoi d'e-mail?", "Pas d'adresse e-mail",
		 * AAOptionPane.YES_NO_OPTION); if(n==AAOptionPane.NO_OPTION)return; }
		 */
		// log.debug("ecrireMail: ControleurMail.getInstance().getModele()="+ControleurMail.getInstance().getModele());
		checkParamsMailing();
		GuiTools.ouvrirDialogue(null, new PanneauEcritureMail(idMail), "Ecriture d'un e-mail");
	}

	/*public static boolean checkParamsMailing(){
		return checkParamsMailing(false);
	}*/
	public static void checkParamsMailingOtherThread() {
		log.debug("checkParamsMailing from "+Thread.currentThread().getName());
		new Thread("checkParamsMailing"){
			@Override
			public void run() {
				log.debug("checkParamsMailing thread starting : "+Thread.currentThread().getName());
				SwingUtilities.invokeLater(() -> {
					boolean ok = checkParamsMailing();
					paramsMailingOk=ok;
				});
				
			}
			
		}.start();
		log.debug("checkParamsMailing thread started");
	}

	public static boolean checkParamsMailing(/*boolean toFront*/){
		log.debug("checkParamsMailing");
		if (ControleurMail.getInstance().getServiceMail() == null){
			paramsMailingOk=false;
			return false;
		}
		ParametresSmtp pm = ControleurMail.getInstance().getModele().getParamsMailPersonnels();
		if (!pm.parametresTousRemplis()) {
			int n = JOptionPane.showConfirmDialog(null,
					"Vous ne pourrez pas envoyer d'e-mail si vos paramètres ne sont pas tous remplis.\n"
							+ "Voulez-vous le faire maintenant?", "Paramètres SMTP incomplets",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE/*, toFront*/);
			if (n == JOptionPane.YES_OPTION) {
				// final JDialog fe=ouvrirDialogue(new PanneauParamsMail(), "Paramètres des e-mails");
				ouvrirFenetreParamsEnvoi();
				paramsMailingOk=checkParamsMailing();
			} else{
				paramsMailingOk=false;
			}
		}else paramsMailingOk=true;
		return paramsMailingOk;
	}

	public static void ecrireMail(ListeDestinataires liste){
		DestinataireMap pseudoSociete = new DestinataireMap();
		String chaineMails = "";
		for (int i = 0; i < liste.size(); i++) {
			IDestinataireMap s = liste.getDestinataireMap(i);
			if (s.getMail() != null && !s.getMail().trim().equals("")) {
				if (chaineMails.length() > 0)
					chaineMails += "; ";
				chaineMails += s.getMail().trim();
			}
		}
		pseudoSociete.setMail(chaineMails);
		ecrireMail(pseudoSociete);
	}
	public static void ecrireMail(List<? extends IDestinataireMap> liste){
		DestinataireMap pseudoSociete = new DestinataireMap();
		String chaineMails = "";
		for (int i = 0; i < liste.size(); i++) {
			IDestinataireMap s = liste.get(i);
			if (s.getMail() != null && !s.getMail().trim().equals("")) {
				if (chaineMails.length() > 0)
					chaineMails += "; ";
				chaineMails += s.getMail().trim();
			}
		}
		pseudoSociete.setMail(chaineMails);
		ecrireMail(pseudoSociete);
	}

	public static String choixListe(){
		String titre = "Veuillez choisir une liste";
		Vector<String> v = new Vector<>();
		List<ListeMailing> listes = ControleurMail.getInstance().getListesMailing();
		v.add(CHOIX_LISTE_NULL);
		for (int i = 0; i < listes.size(); i++) {
			v.add(listes.get(i).getTitre());
		}
		JComboBox<String> combo = new JComboBox<String>(v);
		int n = JOptionPane.showConfirmDialog(null, combo, titre, JOptionPane.OK_CANCEL_OPTION);
		if (n == JOptionPane.OK_OPTION) {
			String choix = (String) combo.getSelectedItem();
			if (CHOIX_LISTE_NULL.equals(choix)) {
				return creerListeMailing(v).getTitre();
			} else
				return choix;
		}
		return null;
	}

	private static ListeMailing creerListeMailing(Vector<String> nomsExistants){
		Map<String, Object> map = new HashMap<String, Object>();
		String titre = JOptionPane.showInputDialog("Entrez un titre pour la nouvelle liste :");
		while (nomsExistants.contains(titre)) {
			titre = JOptionPane.showInputDialog("Ce titre est déjà utilisé; choisissez un autre titre :");
		}
		map.put("titre", titre);
		ListeMailing l = new ListeMailing(map, new ArrayList<Integer>());
		ControleurMail.getInstance().getServiceMail().saveListeMailing(l);
		return l;
	}


}
