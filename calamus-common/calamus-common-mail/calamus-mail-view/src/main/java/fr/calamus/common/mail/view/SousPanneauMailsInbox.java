package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.core.ISensibleAConnexionMail;
import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.view.notification.Notifications;
import java.awt.BorderLayout;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class SousPanneauMailsInbox extends SousPanneauMailsEnregistres implements ISensibleAConnexionMail,
		IReceveurChoixSmtp {
	private static final long serialVersionUID = -4106367760289491802L;
	private ComboChoixSmtp choixSmtp;
	private int nSmtp = 0;

	// private JPopupMenu offlineNotification;
	public SousPanneauMailsInbox(boolean online){
		super("" + online, false);
		ControleurMail.getInstance().enregistrerSensibleAConnexionMail(this);
		if (!online) {
			showOfflineNotification();
		}
	}

	@Override
	protected void creerComposants(){
		choixSmtp = new ComboChoixSmtp(this);
		super.creerComposants();
	}

	@Override
	protected void afficherComposants(){
		super.afficherComposants();
		add(choixSmtp, BorderLayout.BEFORE_FIRST_LINE);
	}

	protected DefaultTableModel getDataModel(){
		if (Boolean.parseBoolean(type)) {
			new Thread(){
				@Override
				public void run(){
					try {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						final Object[][] data = ControleurMail.getInstance().getMailInboxData(
								ControleurMail.getInstance().getModele().getParamsMail(nSmtp));
						if (data.length > 0) {
							log.debug("mail 0: " + data[0][1] + " " + data[0][2] + " " + data[0][3] + " ");
						}
						SwingUtilities.invokeLater(new Runnable(){

							@Override
							public void run(){
								table.setModel(new DefaultTableModel(data, IMailConstantes.titresInbox){// exp,sujet,date,html
									private static final long serialVersionUID = -2322775045283484342L;

									@Override
									public boolean isCellEditable(int row, int column){
										return false;
									}
								});
								table.revalidate();
								table.repaint();
							}
						});
					} catch (MessagingException e) {
						JOptionPane.showMessageDialog(null,
								"Impossible de se connecter à la boîte de réception!", "Problème de connexion",
								JOptionPane.ERROR_MESSAGE, null);
						e.printStackTrace();
					}
				}
			}.start();
		}
		Notifications.show(this, "Récupération de la boîte de réception en cours...");
		return new DefaultTableModel(null, IMailConstantes.titresInbox){
			private static final long serialVersionUID = -5889451531130860512L;

			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
	}

	@Override
	public void connectionMailEtablie(int n){
		type = "true";
		getDataModel();
		hideOfflineNotification();
	}

	private void hideOfflineNotification(){
		Notifications.hide(this);
	}

	@Override
	public void connectionMailFermee(int n){
		type = "false";
		showOfflineNotification();
	}

	public void showOfflineNotification(){
		Notifications.show(this, "Pas de connexion à la boîte mail.");
	}

	/*
	 * private void createOfflineNotification(){ offlineNotification=new
	 * JPopupMenu("Pas de connexion à la boîte mail."); offlineNotification.pack(); }
	 */
	@Override
	public void changerChoix(int n){
		nSmtp = n;
		getDataModel();
	}

}
