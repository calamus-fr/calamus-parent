package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.core.ISensibleAConnexionMail;
import fr.calamus.view.base.*;
import fr.calamus.view.tools.GuiTools;
import java.awt.BorderLayout;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PanneauMailing extends JPanel implements ISensibleAConnexionMail {

	private static final long serialVersionUID = -253102010897316954L;
	private static boolean campagnesOk = true;

	public static void setCampagnesOk(boolean b) {
		campagnesOk = b;
	}
	private JTabbedPane pOnglets;
	private PanneauMailsEnregistres pMails;
	private PanneauListesMailing pListes;
	private PanneauModelesMails pMod;
	// private SousPanneauMailsInbox pInbox;
	private SousPanneauListeNoire pBlacklist;
	private PanneauCampagnes pCampagnes;
	private static final Log log = LogFactory.getLog(PanneauMailing.class);

	public PanneauMailing(boolean online) {
		super(new BorderLayout());
		creerComposants();
		afficherComposants();
		ControleurMail.getInstance().enregistrerSensibleAConnexionMail(this);
	}

	// @Override
	protected void creerComposants() {
		log.debug("creerComposants");
		MailGuiTools.checkParamsMailingOtherThread();
		pOnglets = new JTabbedPane(JTabbedPane.TOP) {
			private static final long serialVersionUID = 3864541598749246598L;

			@Override
			protected void paintComponent(Graphics g) {
				GuiTools.addAntialiasRenderingHints(g);
				//CalamusViewController.getInstance().getViewTools()
				//	.backgroundPaintingUtilsUpdateOngletsMail(g, pOnglets);
				super.paintComponent(g);
			}

			@Override
			public void setSelectedIndex(int index) {
				super.setSelectedIndex(index);
				if (getComponentAt(index) instanceof SousPanneauMailsInbox
					&& !ControleurMail.getInstance().getModele().isMailConnectionOk()) {
					SousPanneauMailsInbox sp = (SousPanneauMailsInbox) getComponentAt(index);
					sp.showOfflineNotification();
				} else if (getComponentAt(index) instanceof PanneauListesMailing) {
					((PanneauTableEtSaToolBar) ((PanneauDetailsToolBarTitreEtDateModif) ((PanneauListesMailing) getComponentAt(index))
						.getPanneauDetails()).getAutreComposant()).debug();
				}
			}
		};
		pOnglets.setOpaque(true);
		if (campagnesOk) {
			pCampagnes = new PanneauCampagnes();
			pOnglets.addTab("Campagnes", pCampagnes);
		}
		pMails = new PanneauMailsEnregistres(false);
		pOnglets.addTab("E-mails enregistrés", pMails);
		pListes = new PanneauListesMailing();
		pOnglets.addTab("Listes", pListes);
		pMod = new PanneauModelesMails();
		pOnglets.addTab("Modèles", pMod);
		// pInbox=new SousPanneauMailsInbox(online);
		// pOnglets.addTab("Bounces et réponses", pInbox);
		pBlacklist = new SousPanneauListeNoire();
		pOnglets.addTab("Liste noire", pBlacklist);
	}

	// @Override
	protected void afficherComposants() {
		add(pOnglets, BorderLayout.CENTER);
	}

	// @Override
	public void redimensionner(int l, int h) {
		pMod.redimensionner(l, h - 40);
		pListes.redimensionner(l, h - 40);
	}

	public PanneauListesMailing getPanneauListes() {
		return pListes;
	}

	public PanneauModelesMails getPanneauModeles() {
		return pMod;
	}

	@Override
	public void connectionMailEtablie(int n) {
		setOnline(true);
	}

	@Override
	public void connectionMailFermee(int n) {
		setOnline(false);
	}

	public void setOnline(boolean b) {
		// online = b;
	}

}
