package fr.calamus.common.mail.core;

import fr.calamus.common.mail.model.EMailDataBean;
import fr.calamus.common.mail.model.EtatsLivraisonMessage;
import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.common.mail.model.ParametresSmtp;
import fr.calamus.common.tools.ListsAndArrays;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Mailer {

	private SimpleDateFormat df;
	private final ParametresSmtp params;
	private static final Log log = LogFactory.getLog(Mailer.class);

	// Pay attention here, because in some servers you mustn't put @domain at user.
	// private static final String SMTP_AUTH_USER = "autisme.france@gmail.com";
	// private static final String SMTP_AUTH_PWD = "autisme2009";
	// protected String texte;
	// protected boolean envoiAutorise;
	// protected boolean envoiEffectue;
	// protected ArrayList<String> piecesJointes;
	// protected String sujet;
	// protected String destinataire;
	// protected String cheminImgLogo;
	// protected ArrayList<String> mailsDestinataires;
	// protected ArrayList<String> nomsDestinataires;
	/*
	 * protected String smtpHost; protected String smtpUser; protected String smtpPwd; //protected String expediteur;
	 * protected int smtpPort; protected int nbDestMax;//par mail protected int delaiMails;//secondes private
	 * ParametresEnvoi smtp;
	 */

 /*
	 * public Mailer(String instance, String sujet, String texte, ArrayList<String> piecesJointes, String host, String
	 * user, String pwd, String exp, int port) { this(instance, sujet, texte, null, piecesJointes); smtpHost = host;
	 * smtpUser = user; smtpPwd = pwd; expediteur = exp; smtpPort = port; }
	 */
	public Mailer() {
		this(null);
	}

	public Mailer(ParametresSmtp smtp) {
		df = new SimpleDateFormat("dd/MM/YYYY hh:mm");
		params = smtp;
	}

	/*
	 * public Mailer(EMail mail) { sujet = mail.get("sujet"); texte = mail.get("html"); piecesJointes = null;
	 * getParamsMailing(); parserTos(OutilsDivers.string2ArrayList(mail.get("destinataires"), "; "));
	 * parserPJs(mail.get("pjs")); } /* public Mailer(String instance, Mailing mailing) { this.instance = instance;
	 * sujet = mailing.get("sujet"); texte = mailing.get("html"); piecesJointes = null; getParamsMailing();
	 * //parserTos(OutilsDivers.string2ArrayList(mailing.get("destinataires"), "; ")); parserPJs(mailing.get("pjs")); }
	 */

 /*
	 * public boolean envoiMail(EMail mail) { sujet = mail.getSujet(); texte = mail.getTexte(); boolean b =
	 * envoiMail(OutilsDivers.string2ArrayList(mail.get("destinataires"), "; "), mail.get("pjs"), true, true); if (b) {
	 * mail.put("type_etat", "Envoyé"); mail.sauver(); } return b; }
	 */
	private String[] parseTo(String to) {
		String[] t = new String[2];
		if (to == null) {
			return t;
		}
		if (!to.contains(" <")) {
			t[0] = to;
			t[1] = to;
		} else {
			String[] s = to.split(" <", 2);
			t[0] = s[0];
			t[1] = s[1].substring(0, s[1].indexOf(">"));
			if (t[1].endsWith("\"")) {
				t[1] = t[1].substring(0, t[1].length() - 1);
			}
			if (t[1].startsWith("\"")) {
				t[1] = t[1].substring(1);
			}

		}
		return t;
	}

	public void envoyerMailing(final EMailDataBean mailing, final IReceveurEventsEnvoiMails rapport) {
		envoyerMailing(mailing, rapport, null);
	}

	public void envoyerMailing(final EMailDataBean mailing, final IReceveurEventsEnvoiMails rapport, ParametresSmtp smtp) {
		log.debug("envoyerMailing");
		if (smtp == null) {
			if (params != null) {
				smtp = params;
			} else {
				smtp = ControleurMail.getInstance().getModele().getParamsMailPersonnels();
			}
		}
		final int delai = smtp.getDelai();
		final int nbDests = smtp.getNbDestsParMail();
		final ParametresSmtp finalSmtp = smtp;
		new Thread() {
			public void run() {
				List<EMailDataBean> mails = grouperDestinataires(mailing, nbDests);
				boolean destsCaches = mails.size() > 1;
				log.debug("envoyerMailing: mails.size()=" + mails.size());
				for (int i = 0; i < mails.size(); i++) {
					envoyerMailDansThread(mails.get(i), destsCaches, rapport, finalSmtp);
					if (i < mails.size() - 1) {
						try {
							Thread.sleep(delai * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				Thread.currentThread().interrupt();
			}
		;
	}

	.start();
	}

	private List<EMailDataBean> grouperDestinataires(EMailDataBean mailing, int nbDestsParMail) {
		log.debug("grouperDestinataires");
		List<String> mailsDestinataires = ListsAndArrays.splitToStringList(mailing.getTo(), "; ");
		log.debug(" mailsDestinataires.size()=" + mailsDestinataires.size());
		List<EMailDataBean> l = new ArrayList<EMailDataBean>();
		List<String> temp = null;
		// ParametresSmtp smtp = ControleurMail.getInstance().getModele().getParamsCourants();
		for (int i = 0; i < mailsDestinataires.size(); i++) {
			log.debug("  " + i);
			log.debug("   i%params.getNbDestsParMail()==0:" + (i % nbDestsParMail == 0));
			if (i % nbDestsParMail == 0) {
				log.debug("   temp=" + temp);
				if (temp != null) {
					EMailDataBean partialMail = mailing.cloneBean();
					String to = ListsAndArrays.mergeList(temp, "; ");
					log.debug("    to=" + to);
					partialMail.setTo(to);
					l.add(partialMail);
				}
				temp = new ArrayList<String>();
			}
			temp.add(mailsDestinataires.get(i));
		}
		log.debug(" fin boucle: temp=" + temp);
		if (temp != null) {
			EMailDataBean partialMail = mailing.cloneBean();
			String to = ListsAndArrays.mergeList(temp, "; ");
			log.debug("    to=" + to);
			partialMail.setTo(to);
			l.add(partialMail);
		}
		return l;
	}

	public void envoyerMailDansThread(EMailDataBean mail, IReceveurEventsEnvoiMails rapport) {
		ParametresSmtp smtp;
		if (params != null) {
			smtp = params;
		} else {
			smtp = ControleurMail.getInstance().getModele().getParamsMailPersonnels();
		}
		envoyerMailDansThread(mail, false, rapport, smtp);
	}

	public void envoyerMailDansThread(final EMailDataBean mail, final boolean destinatairesCaches,
		final IReceveurEventsEnvoiMails rapport, ParametresSmtp smtp) {
		envoyerMailDansThread(mail, destinatairesCaches, rapport, false, smtp);
	}

	public void envoyerMailDansThread(final EMailDataBean mail, final boolean destinatairesCaches,
		final IReceveurEventsEnvoiMails rapport, final boolean silent, final ParametresSmtp params) {
		log.debug("envoyerMail");
		new Thread() {
			@Override
			public void run() {
				//getRunnable(mail, destinatairesCaches, rapport, silent, params).run();
				try{
					envoyerMailMemeThread(mail, destinatairesCaches, rapport, silent, params);
				}catch(Exception e){
					String ae="535 5.7.8 Error";
					String m=e.getLocalizedMessage()!=null?e.getLocalizedMessage():e.getMessage();
					if(m==null)m="";
					rapport.failed(m.startsWith(ae)?"échec s'authentification":m);
				}
				
				Thread.currentThread().interrupt();
			}
		}.start();
	}

	public boolean envoyerMailMemeThread(final EMailDataBean mail, final boolean destinatairesCaches,
		final IReceveurEventsEnvoiMails rapport, final boolean silent, final ParametresSmtp params) {
		List<String> mailsDestinataires = ListsAndArrays.splitToStringList(mail.getTo(), "; ");
		log.debug("  mailsDestinataires=" + mailsDestinataires);
		log.debug("  mail.to=" + mail.getTo());
		log.debug("  mail.bcc=" + mail.getBcc());
		log.debug("envoyerMail dests.size=" + mailsDestinataires.size() + ", params=" + params);
		boolean success = false;
		Calendar c = Calendar.getInstance();
		// ParametresSmtp smtp = ControleurMail.getInstance().getModele().getParamsCourants();
		if (mailsDestinataires.size() > params.getNbDestsParMail()) {
			//envoyerMailing(mail, rapport);
			return false;
		}
		log("Envoi à " + c.getTime(), rapport);
		Session mailSession = initSession(params);

		// uncomment for debugging infos to stdout
		// mailSession.setDebug(true);
		Transport transport = null;
		try {
			transport = mailSession.getTransport();
		} catch (NoSuchProviderException e) {
			log("Erreur lors de l'accès au transport", rapport);
			e.printStackTrace();
		}
		Multipart mpart = new MimeMultipart(); // Instanciation des classes de gestion du corps du mail
		BodyPart htmlPart = new MimeBodyPart();
		// String urlJoin = "./20100102132353_imagesFactures_facture1810.pdf";
		try {
			htmlPart.setContent(mail.getTexte(), "text/html");
			// Mettre toutes les parties dans le MultiPart.
			mpart.addBodyPart(htmlPart);
		} catch (MessagingException e1) {
			log("Erreur d'attribution du contenu", rapport);
			e1.printStackTrace();
		}
		boolean pluriel = mailsDestinataires.size() > 1;
		String finPhrase = "à l'adresse " + mail.getTo();
		if (pluriel) {
			finPhrase = "à " + mailsDestinataires.size() + " adresses";
		}
		/*
				 * boolean envoiEnMasse = false; if (mailsDestinataires != null && mailsDestinataires.size() > 0) { if
				 * (mailsDestinataires.size() > 1) { finPhrase = "aux adresses " +
				 * OutilsDivers.arrayList2String(mailsDestinataires, ", "); envoiEnMasse = mailsDestinataires.size() >
				 * 40; } else { finPhrase = "à l'adresse " + mailsDestinataires.get(0); } } else {
				 * JOptionPane.showMessageDialog(null, "Pas de destinataire(s) pour cet envoi!"); return false; }
		 */

		// piecesJointes.add(0, "./images/logoAF.jpg");
		try {

			if (mail.getPjs() != null) {
				for (int i = 0; i < mail.getPjs().size(); i++) {
					String urlJoin = mail.getPjs().get(i);
					if (!"".equals(urlJoin)) {
						BodyPart pjoint = new MimeBodyPart();
						DataSource dataSrc = new FileDataSource(urlJoin); // indiquer l'emplacement de votre
						// photo
						DataHandler captdata = new DataHandler(dataSrc);
						// Specifier que la photo(dataSrv) captur�e par la DataHandler est une portion du
						// message(2me portion)
						pjoint.setDataHandler(captdata);
						// Vector<String> chemin = OutilsDivers.string2Vector(urlJoin, File.separator);
						String[] chemin = urlJoin.split(File.separator);
						String fichier = chemin[chemin.length - 1];
						// if(!new File(urlJoin).exists())for(int
						// j=0;j<1000;j++)System.out.println("attente avant mail:"+j);
						File f = new File(urlJoin);
						/*
								 * int j = 0; while (!f.exists() && j < 100000) {
								 * System.out.println("attente avant mail:" + j++); }
						 */
						if (!f.exists()) {
							log("Problème lors de l'envoi du mail:\nle fichier " + urlJoin
								+ " n'a pas été trouvé.", rapport);
							return false;
						}
						// Donner un nom à la pièce jointe

						pjoint.setFileName(fichier);
						// Mettre toutes les parties dans le MultiPart.
						mpart.addBodyPart(pjoint);

					}
				}
			}
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(mail.getSujet());
			/*
					 * if (bulk) { message.addHeaderLine("Precedence: bulk"); }
			 */
			message.setContent(mpart, "text/plain");
			message.setFrom(new InternetAddress(params.get(ParametresSmtp.Cle.fromAddress), params
				.get(ParametresSmtp.Cle.fromPersonal)));
			// message.setFrom(new InternetAddress(SMTP_AUTH_USER));
			// String finPhrase;
			Message.RecipientType recType = null;
			if (destinatairesCaches && (mail.getBcc() == null || mail.getBcc().trim().isEmpty())) {
				recType = Message.RecipientType.BCC;
			} else {
				recType = Message.RecipientType.TO;
			}
			// ArrayList<String> erreurs = new ArrayList<String>();
			int nb = 0;
			int nbRestantsPourLHeure = ControleurMail.getInstance().getNbMailsRestantsPourLHeure();
			if (mail.getTo() != null) {
				for (int i = 0; i < mailsDestinataires.size(); i++) {
					InternetAddress iAddr = null;
					String[] md = parseTo(mailsDestinataires.get(i));
					try {
						// iAddr = new InternetAddress(mailsDestinataires.get(i), nomsDestinataires.get(i));
						iAddr = new InternetAddress(md[0], md[1]);
					} catch (UnsupportedEncodingException ex) {
						ex.printStackTrace();
					}
					if (iAddr != null && nb < nbRestantsPourLHeure) {
						message.addRecipient(recType, iAddr);
						log("Ajout de l'adresse " + md[0] + " (" + md[1] + ")", rapport);
						nb++;
					} else {
						log("Problème d'ajout de l'adresse " + md[0] + " (" + md[1] + ")", rapport);
						if (nb >= nbRestantsPourLHeure) {
							log(" (le quota horaire d'envoi est atteint)", rapport);
						}
					}
				}
			}
			if (mail.getBcc() != null && !mail.getBcc().trim().isEmpty()) {
				List<String> bccs = ListsAndArrays.splitToStringList(mail.getBcc(), "; ");
				for (int i = 0; i < bccs.size(); i++) {
					InternetAddress iAddr = null;
					String[] md = parseTo(bccs.get(i));
					try {
						iAddr = new InternetAddress(md[0], md[1]);
					} catch (UnsupportedEncodingException ex) {
						ex.printStackTrace();
					}
					if (iAddr != null) {
						message.addRecipient(Message.RecipientType.BCC, iAddr);
						log("Ajout de l'adresse (bcc) " + md[0] + " (" + md[1] + ")", rapport);
					} else {
						log("Problème d'ajout de l'adresse (bcc) " + md[0] + " (" + md[1] + ")", rapport);
					}
				}
			}
			/*
					 * else { message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinataire)); }
			 */
			log("Connexion...", rapport);
			transport.connect();
			log("Connexion établie", rapport);
			transport.addTransportListener(new TransportListener() {

				@Override
				public void messagePartiallyDelivered(TransportEvent te) {
					rapport.addTransportEvent(EtatsLivraisonMessage.messagePartiallyDelivered, te);
				}

				@Override
				public void messageNotDelivered(TransportEvent te) {
					rapport.addTransportEvent(EtatsLivraisonMessage.messageNotDelivered, te);
				}

				@Override
				public void messageDelivered(TransportEvent te) {
					rapport.addTransportEvent(EtatsLivraisonMessage.messageDelivered, te);
				}
			});
			transport.sendMessage(message, message.getRecipients(recType));
			log("Message envoyé", rapport);
			transport.close();
			// JOptionPane.showMessageDialog(null, "E-mail envoyé " + finPhrase + ".");
			c = new GregorianCalendar();
			log("  envoyé à " + c.getTime(), rapport);
			countMails(nb, rapport);
			/*
					 * if (erreurs.size() > 0) { for (int i = 0; i < erreurs.size(); i++) { log(erreurs.get(i),
					 * rapport); } }
			 */
			if (!silent) {
				ControleurMail.getInstance().getServiceMail()
					.saveMail(mail, IMailConstantes.MAILS_ENVOYES, true);
			}
			success = true;
			rapport.success();
		} catch (MessagingException e) {
			log("Le message n'a pas été envoyé " + finPhrase + ".", rapport);
			if (!silent) {
				ControleurMail.getInstance().getServiceMail()
					.saveMail(mail, IMailConstantes.MAILS_ENVOYES, false);
			}
			e.printStackTrace();
			//rapport.failed("Le message n'a pas été envoyé " + finPhrase + ".");
			String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getMessage();
			Throwable ec = e.getCause();
			if (ec != null) {
				msg += " : " + (ec.getLocalizedMessage() != null ? ec.getLocalizedMessage() : ec.getMessage());
			}
			rapport.failed(msg);
		} catch (UnsupportedEncodingException e) {
			log("Problème avec l'adresse expéditeur utilisée : " + params.get(ParametresSmtp.Cle.fromAddress)
				+ " \"" + params.get(ParametresSmtp.Cle.fromPersonal) + "\"", rapport);
			e.printStackTrace();
			rapport.failed("Problème avec l'adresse expéditeur utilisée : " + params.get(ParametresSmtp.Cle.fromAddress)
				+ " \"" + params.get(ParametresSmtp.Cle.fromPersonal) + "\"");
		}
		//log.debug("fin thread envoyerMail");
		return success;
	}

	protected void countMails(int nb, IReceveurEventsEnvoiMails rapport) {
		log.debug(nb + " mails envoyés");
		ControleurMail.getInstance().compterMails(nb);
		// if(rapport!=null)rapport.compterMails(nb);
	}

	protected Session initSession(ParametresSmtp smtp) {
		log.debug("initSession smtp=" + smtp);
		Properties props = new Properties();
		//	props.setProperty("mail.imap.timeout", "30000");
		//	props.setProperty("mail.imap.connectiontimeout", "30000");
		props.setProperty("mail.smtp.timeout", "60000");//30000
		props.setProperty("mail.smtp.connectiontimeout", "60000");//30000
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", smtp.get(ParametresSmtp.Cle.hoteSmtp));
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.debug.auth", "true");
		if (smtp.sslSmtpOk()) {
			// props.put("mail.smtp.ssl", "true");
			props.setProperty("mail.imaps.ssl.enable", "true");
			props.setProperty("mail.smtp.ssl.enable", "true");
			// props.put("mail.smtp.starttls.enable", "true");
		}

		props.setProperty("mail.smtp.port", smtp.get(ParametresSmtp.Cle.portSmtp));
		if (smtp.get(ParametresSmtp.Cle.portImap) != null) {
			props.setProperty("mail.imap.port", smtp.get(ParametresSmtp.Cle.portImap));
		}
		props.setProperty("mail.user", smtp.get(ParametresSmtp.Cle.user));
		log.debug("props=" + props);
		Authenticator auth = new SMTPAuthenticator(smtp);
		Session s = Session.getInstance(props, auth);
		s.setDebug(true);
		return s;
	}

	/*
	 * private void miseEnFormeTexte() { boolean
	 * html=texte.contains("<body>")||texte.contains("<html>")||texte.contains(
	 * "<p>")||texte.contains("<br/>")||texte.contains("<div>")||texte.contains("<a href");
	 * /*System.out.println("cheminImgLogo:" + cheminImgLogo); String img = ""; if (cheminImgLogo != null) { img =
	 * "<img src=\"" + cheminImgLogo + "\"></img>"; }
	 */
 /*
	 * texte = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
	 * "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"" +
	 * "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
	 * "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" + "<body>" /*+ img*
	 * +mettreEnHTML(texte) + "</body></html>";* texte = //"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
	 * "<!DOCTYPE html>\n" + "<html>\n" + "<body>" /*+ img* + (html?texte:mettreEnHTML(texte)) + "</body></html>"; }
	 * private String mettreEnHTML(String texte) { // return "<p>" + texte.replaceAll("\n", "<br/>\n") + "</p>"; return
	 * texte.replaceAll("\n", "<br/>\n"); }
	 */

 /*
	 * private void getParamsMailing() { ModeleCentralMail m = ControleurMail.getInstance().getModele();
	 * if(m.isCustomSmtp()){ smtp=m.getParamsMailCustom(); }else{ smtp=m.getParamsMailDefaut(); } smtpHost =
	 * smtp.getHoteSmtp(); smtpUser = smtp.getUserSmtp(); smtpPwd = smtp.getPwdSmtp(); if (smtp.getPortSmtp() !=
	 * null) { smtpPort = Integer.parseInt(smtp.getPortSmtp()); } else { smtpPort = 25; } }
	 */

 /*
	 * private void parserTos(ArrayList<String> tos) { mailsDestinataires = new ArrayList<>(); nomsDestinataires = new
	 * ArrayList<>(); for (int i = 0; i < tos.size(); i++) { String to = tos.get(i); System.out.println("to:" + to);
	 * String nom = null; String email = null; if (to.contains("<")) { nom = to.substring(0, to.indexOf("<")).trim();
	 * email = to.substring(to.indexOf("<") + 1, to.indexOf(">")).trim(); } else { nom = to.trim(); email = nom; }
	 * mailsDestinataires.add(email); nomsDestinataires.add(nom); } }
	 * private void parserPJs(String pjs) { if (pjs != null && !pjs.equals("")) { piecesJointes =
	 * OutilsDivers.string2ArrayList(pjs, "; "); } //piecesJointes=new ArrayList<>(); }
	 */
	private void log(String string, IReceveurEventsEnvoiMails rapport) {
		//log.debug(string);
		if (rapport != null) {
			rapport.ajouterLigne(string);
		}
	}

	public boolean envoyerMailAvecRetour(EMailDataBean mail, boolean destsCaches) {
		return envoyerMailAvecRetour(mail, destsCaches, null);
	}

	public boolean envoyerMailAvecRetour(EMailDataBean mail, boolean destsCaches, ParametresSmtp smtp) {
		return envoyerMailAvecRetour(mail, destsCaches, smtp, null);
	}

	public boolean envoyerMailAvecRetour(EMailDataBean mail, boolean destsCaches, ParametresSmtp smtp, IReceveurEventsEnvoiMails listener) {
		log.debug("envoyerMailAvecRetour params=" + smtp);
		if (smtp == null) {
			if (params != null) {
				smtp = params;
			} else {
				smtp = ControleurMail.getInstance().getModele().getParamsMailPersonnels();
			}
		}
		return envoyerMailMemeThread(mail, destsCaches, listener, destsCaches, smtp);
		/*final ArrayList<String> lignes = new ArrayList<>();
		final BooleanState b = new BooleanState();
		IReceveurEventsEnvoiMails r = new IReceveurEventsEnvoiMails() {

			private IReceveurEventsEnvoiMails l;

			@Override
			public void ajouterLigne(String ligne) {
				lignes.add(ligne);
				if (l != null) {
					l.ajouterLigne(ligne);
				}
			}

			public IReceveurEventsEnvoiMails setListener(IReceveurEventsEnvoiMails l) {
				this.l = l;
				return this;
			}

			@Override
			public void addTransportEvent(EtatsLivraisonMessage etat, TransportEvent e) {
				ajouterLigne("TransportEvent etat=" + etat.getLabel());
				if (l != null) {
					l.addTransportEvent(etat, e);
				}
			}

			@Override
			public void success() {
				ajouterLigne("Success !");
				if (l != null) {
					l.success();
				}
				b.setState(true);
			}

			@Override
			public void failed(String pb) {
				ajouterLigne(pb);
				if (l != null) {
					l.failed(pb);
				}
				b.setState(false);
			}
		}.setListener(listener);
		envoyerMail(mail, destsCaches, r, true, smtp);
		int i = 0;
		while (!b.hasState()) {
			try {
				log("(waiting " + (i++) + ")", r);
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		return b.getState();*/
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {

		private ParametresSmtp params;

		public SMTPAuthenticator(ParametresSmtp params) {
			this.params = params;
		}

		public javax.mail.PasswordAuthentication getPasswordAuthentication() {
			String username = params.get(ParametresSmtp.Cle.user);
			String password = params.get(ParametresSmtp.Cle.pwd);
			return new javax.mail.PasswordAuthentication(username, password);
		}
	}

	public Object[][] getInbox(List<String> listeMailsContacts, ParametresSmtp params) throws MessagingException {
		// ParametresSmtp smtp = ControleurMail.getInstance().getModele().getParamsCourants();
		Session session = initSession(params);
		Store store = session.getStore("imaps");
		store.connect(params.getHote(), params.getUser(), params.getPwd());
		log.debug(store.toString());
		List<Object[]> l = new ArrayList<Object[]>();
		Object[][] oa;// id,dests,sujet,date,etat

		Folder inbox = store.getFolder("inbox");
		inbox.open(Folder.READ_WRITE); // Folder.READ_ONLY
		int messageCount = inbox.getMessageCount();
		log.debug("Total Messages " + messageCount);
		int endMessage = messageCount;
		Message[] messages = inbox.getMessages(1, endMessage);
		/*
		 * inbox.search(new FromStringTerm("Mail Delivery Subsystem"));/*(new
		 * AddressStringTerm("Mail Delivery Subsystem"){
		 * @Override public boolean match(Message m){ try { Address[] frs = m.getFrom(); } catch (MessagingException e)
		 * { e.printStackTrace(); } return false; } });
		 */
		// int i=0;
		for (Message message : messages) {
			Object[] o = new Object[5];
			// boolean isMessageRead = false;

			/*
			 * for (Flags.Flag flag : message.getFlags().getSystemFlags()) { if (flag == Flags.Flag.SEEN) {
			 * isMessageRead = true; break; } }
			 */
			boolean isMailDeliverySubsystem = false;
			boolean isInListe = (listeMailsContacts == null);
			Address[] frs = message.getFrom();
			String from = "";
			if (frs != null) {
				for (int i = 0; i < frs.length; i++) {
					if (frs[i] instanceof InternetAddress) {
						InternetAddress ia = (InternetAddress) frs[i];
						String pers = ia.getPersonal();
						if (pers != null && pers.contains("Mail Delivery Subsystem")) {
							isMailDeliverySubsystem = true;
						} else if (listeMailsContacts != null && listeMailsContacts.contains(ia.getAddress())) {
							isInListe = true;
						}
						if (from.length() > 0) {
							from += "; ";
						}
						from += ia.toString();
					}
				}
			}
			if (isMailDeliverySubsystem || isInListe) {// exp,sujet,date,html
				// o[0]="";
				o[0] = from;
				o[1] = message.getSubject();
				o[2] = df.format(message.getReceivedDate());
				String t = message.getDescription();
				o[3] = t;
			}
			// message.setFlag(Flags.Flag.SEEN, true);
			// log.debug(message.getSubject() + " " + (isMessageRead ? " [READ]" : " [UNREAD]"));
			/*
			 * try { log.debug(message.getSubject() + " " +
			 * message.getContentType()+" * "+message.getContent().getClass().getSimpleName()); } catch (IOException e)
			 * { e.printStackTrace(); }
			 */
			l.add(o);
		}

		inbox.close(true);
		oa = new Object[l.size()][];
		for (int i = 0; i < l.size(); i++) {
			oa[i] = l.get(i);
		}
		System.out.println("Done....");
		store.close();
		return oa;
	}

	public boolean checkImap() {
		ParametresSmtp smtp = params == null ? ControleurMail.getInstance().getModele().getParamsMailPersonnels() : params;
		if (smtp == null || smtp.size() < ParametresSmtp.Cle.values().length || !smtp.parametresTousRemplisImap()) {
			return false;
		}
		Session session = initSession(smtp);
		Store store;
		try {
			store = session.getStore("imaps");
			store.connect(smtp.getHote(), smtp.getUser(), smtp.getPwd());
			store.close();
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}

	}

	private static class BooleanState {

		private Boolean b;

		public BooleanState() {
			b = null;
		}

		public boolean hasState() {
			return b != null;
		}

		public boolean getState() {
			return b;
		}

		public void setState(boolean state) {
			b = state;
		}
	}

}
