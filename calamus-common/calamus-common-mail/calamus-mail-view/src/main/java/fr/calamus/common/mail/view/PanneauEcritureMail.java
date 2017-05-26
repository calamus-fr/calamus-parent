package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.EMailDataBean;
import fr.calamus.common.mail.model.IDestinataireMap;
import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.common.mail.model.ModeleCentralMail;
import fr.calamus.common.mail.model.ParametresSmtp;
import fr.calamus.common.tools.ListsAndArrays;
import fr.calamus.ctrl.CalamusViewController;
import fr.calamus.view.base.IPanneauRedimensionnable;
import fr.calamus.view.base.ZoneTexteRiche;
import fr.calamus.view.tools.IViewTools;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class PanneauEcritureMail extends JPanel implements ActionListener, IPanneauRedimensionnable, IReceveurChoixSmtp {
	private static final long serialVersionUID = 4359271684117046213L;
	private static final int GAP = 5;
	private static final int PADDING_COTE = 20;
	private static final int PADDING_HAUT = 15;
	private static final int HAUT_ELT = 30;
	private static final int LARG_LABEL = 120;
	private static final int LARG_BOUTON = 100;
	private static final int LARG_BOUTONS_HAUT = 145;
	private JTextField tfSujet;
	private JLabel lblSujet;
	private JTextField tfTo;
	private JLabel lblTo;
	private IDestinataireMap societe;
	private JLabel lblPJ;
	private JTextField tfPJ;
	private JButton boutonEnvoyer;
	private JButton boutonEnregistrer;
	private JButton boutonAnnuler;
	private ZoneTexteRiche texte;
	private JDialog dialogue;
	private ArrayList<String> pjs;
	private JPopupMenu popupPJs;
	private JFileChooser jfc;
	private int idMail;
	private EMailDataBean dataBean;
	private JButton boutonChargerModele;
	private JButton boutonEnregistrerModele;
	private boolean autoriserChoixSmtp2;
	private int nSmtp;
	private ComboChoixSmtp cbSmtp2;
	public static final String AJ = "<html><i>Ajouter un fichier</i></html>";
	private JMenuItem jmiAj;
	//private static

	public PanneauEcritureMail(IDestinataireMap s){
		super();
		autoriserChoixSmtp2 = ControleurMail.getInstance().getModele().isSmtp2Enabled();
		this.societe = s;
		nSmtp = 0;
		pjs = new ArrayList<String>();
		setLayout(null);
		creerEtAjouterComposants();
	}

	public PanneauEcritureMail(int id){
		this(null);
		idMail = id;
		dataBean = ControleurMail.getInstance().getDataBean(idMail);
		tfSujet.setText(dataBean.getTexte());
		tfPJ.setText(ListsAndArrays.mergeList(dataBean.getPjs(), ", "));
		tfTo.setText(dataBean.getTo());
	}

	private void creerEtAjouterComposants(){
		// System.out.println("PanneauEcritureMail.creerEtAjouterComposants()");
		IViewTools vt = CalamusViewController.getInstance().getViewTools();
		jmiAj = new JMenuItem(AJ);
		jmiAj.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){
				ajouterPieceJointe();
			}
		});
		cbSmtp2 = null;
		if (autoriserChoixSmtp2) {
			cbSmtp2 = new ComboChoixSmtp(this);
			add(cbSmtp2);
		}
		dialogue = null;
		lblSujet = new JLabel("Sujet");
		tfSujet = vt.creerTextField();
		lblTo = new JLabel("Destinataire");
		tfTo = vt.creerTextField();
		tfTo.setText(societe.getMail());
		lblPJ = new JLabel("Pièces jointes");
		tfPJ = vt.creerTextField();
		tfPJ.setEditable(false);
		tfPJ.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				ouvrirPoupPjs();
			}
		});
		boutonChargerModele = new JButton("Charger un modèle");
		boutonEnregistrerModele = new JButton("Enregistrer en tant que modèle");
		boutonEnregistrer = new JButton("Enregistrer");
		boutonEnvoyer = new JButton("Envoyer");
		boutonAnnuler = new JButton("Annuler");
		boutonEnregistrer.addActionListener(this);
		boutonEnvoyer.addActionListener(this);
		boutonAnnuler.addActionListener(this);
		texte = new ZoneTexteRiche();
		add(boutonEnregistrerModele);
		add(boutonChargerModele);
		add(boutonEnvoyer);
		add(boutonAnnuler);
		add(lblPJ);
		add(lblSujet);
		add(lblTo);
		add(tfPJ);
		add(tfSujet);
		add(tfTo);
		add(texte);
	}

	public void redimensionner(int larg, int haut){
		// System.out.println("PanneauEcritureMail.redimensionner("+larg+", "+haut+")");
		Insets ins = getInsets();
		larg -= (ins.right + ins.left);
		haut -= (ins.top + ins.bottom);
		setBounds(ins.left, ins.top, larg, haut);
		int x = PADDING_COTE;
		int y = PADDING_HAUT;

		if (cbSmtp2 != null) {
			cbSmtp2.setBounds(x, y, larg - 2 * GAP, HAUT_ELT);
			y += HAUT_ELT + GAP;
		}

		boutonChargerModele.setBounds(x, y, LARG_BOUTONS_HAUT, HAUT_ELT);
		boutonEnregistrerModele.setBounds(x + LARG_BOUTONS_HAUT + GAP, y, LARG_BOUTONS_HAUT, HAUT_ELT);
		y += HAUT_ELT + GAP;

		lblTo.setBounds(x, y, LARG_LABEL, HAUT_ELT);
		tfTo.setBounds(x + LARG_LABEL, y, larg - 2 * PADDING_COTE - LARG_LABEL, HAUT_ELT);
		y += HAUT_ELT + GAP;

		lblSujet.setBounds(x, y, LARG_LABEL, HAUT_ELT);
		tfSujet.setBounds(x + LARG_LABEL, y, larg - 2 * PADDING_COTE - LARG_LABEL, HAUT_ELT);
		y += HAUT_ELT + GAP;

		lblPJ.setBounds(x, y, LARG_LABEL, HAUT_ELT);
		tfPJ.setBounds(x + LARG_LABEL, y, larg - 2 * PADDING_COTE - LARG_LABEL, HAUT_ELT);
		y += HAUT_ELT + GAP;

		texte.setBounds(x, y, larg - 2 * PADDING_COTE, haut - y - 2 * HAUT_ELT - 2 * GAP);

		boutonEnvoyer.setBounds(larg - GAP - LARG_BOUTON - PADDING_COTE, haut - 2 * HAUT_ELT - GAP, LARG_BOUTON,
				HAUT_ELT);
		boutonAnnuler.setBounds(larg - 2 * GAP - 2 * LARG_BOUTON - PADDING_COTE, haut - 2 * HAUT_ELT - GAP,
				LARG_BOUTON, HAUT_ELT);
		boutonEnregistrer.setBounds(larg - 3 * GAP - 3 * LARG_BOUTON - PADDING_COTE, haut - 2 * HAUT_ELT - GAP,
				LARG_BOUTON, HAUT_ELT);

	}

	@Override
	public void actionPerformed(ActionEvent e){
		//log.debug("actionPerformed", this);
		if (e.getSource() instanceof JButton) {
			String txt = ((JButton) e.getSource()).getText();
			//log.debug(" " + txt);
			if (txt.equals("Envoyer")) {
				envoyer();
			} else if (txt.equals("Annuler")) {
				annuler();
			} else if (txt.equals("Enregistrer")) {
				enregistrerBrouillon();
			}
		}
	}

	private void enregistrerBrouillon(){
		ControleurMail.getInstance().saveMail(getDataBean(), IMailConstantes.MAILS_BROUILLONS, true);
	}

	private void annuler(){
		if (dialogue != null)
			dialogue.dispose();
	}

	private void envoyer(){
		// Mailer m=new Mailer();
		EMailDataBean em = getDataBean();
		ControleurMail cm = ControleurMail.getInstance();
		PanneauRapportEnvoiMails rapport = new PanneauRapportEnvoiMails();
		ParametresSmtp smtp = cm.getModele().getParamsMail(nSmtp);
		cm.getMailer().envoyerMailing(em, rapport, smtp);
		JFrame fenetreRapport = new JFrame("Rapport d'envoi d'e-mails");
		fenetreRapport.setIconImage(CalamusViewController.getInstance().getViewTools().getPetiteIconeLogo().getImage());
		fenetreRapport.add(rapport);
		Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize();
		fenetreRapport.pack();
		int fw = 250;
		int fh = 150;
		fenetreRapport.setBounds(scrdim.width - fw, scrdim.height - fh, fw, fh);
		fenetreRapport.setVisible(true);
		/*
		 * boolean ok; if(ControleurMail.getInstance().getMailer().envoyerMail(em, false)){
		 * AAOptionPane.showMessageDialog(this, "Message envoyé avec succès.", "Envoi d'un mail à "+tfTo.getText(),
		 * AAOptionPane.INFORMATION_MESSAGE, GuiTools.getImageInfo()); dialogue.dispose(); ok=true; }else{ ok=false;
		 * AAOptionPane.showMessageDialog(this, "Echec de l'envoi.", "Envoi d'un mail à "+tfTo.getText(),
		 * AAOptionPane.ERROR_MESSAGE, GuiTools.getImageError()); }
		 * ControleurMail.getInstance().getServiceMail().saveMail(em, IUserDbConstantes.MAILS_ENVOYES,ok);
		 */
	}

	public void setDialogue(JDialog f){
		dialogue = f;
	}

	protected EMailDataBean getDataBean(){
		EMailDataBean em = new EMailDataBean();
		// ModeleCentral mc=ModeleCentral.getInstance();
		ModeleCentralMail mm = ControleurMail.getInstance().getModele();
		String from = mm.getExpediteur();
		if (from == null || from.equals("")) {
			from = JOptionPane.showInputDialog("Veuillez entrer votre adresse e-mail");

			ControleurMail.getInstance().getControleurConf().setMailUserSansSave(from);
		}
		em.setFrom(from);
		em.setTo(tfTo.getText());
		em.setSujet(tfSujet.getText());
		em.setTexte(texte.getText());
		em.setPjs(pjs);
		return em;
	}

	@Override
	public JPanel getThisPanel(){
		return this;
	}

	@Override
	public int getPreferredWidth(){
		return 800;
	}

	@Override
	public int getPreferredHeight(){
		return 600;
	}

	protected void ouvrirPoupPjs(){
		if (popupPJs == null) {
			popupPJs = CalamusViewController.getInstance().getViewTools().creerPopupMenu();
		}
		if (pjs.size() == 0) {
			ajouterPieceJointe();
		} else {
			popupPJs.show(tfPJ, 0, 0);
		}
	}

	private void ajouterPieceJointe(){
		if (jfc == null) {
			jfc = new JFileChooser();
			jfc.setAcceptAllFileFilterUsed(true);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		jfc.showOpenDialog(this);
		File f = jfc.getSelectedFile();
		if (f != null) {
			try {
				pjs.add(f.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			refreshItems();
			refreshTextePJs();
		}
	}

	private void refreshTextePJs(){
		String ttf = "";
		if (pjs != null) {
			ttf = pjs.toString();
			if (ttf.startsWith("[") && ttf.endsWith("]")) {
				ttf = ttf.substring(1, ttf.length() - 1);
			}
		}
		tfPJ.setText(ttf);
	}

	public void refreshItems(){
		popupPJs.removeAll();
		popupPJs.add(jmiAj);
		for (int i = 0; i < pjs.size(); i++) {
			final String path = pjs.get(i);
			JCheckBoxMenuItem cb = new JCheckBoxMenuItem(new File(path).getName());
			cb.setSelected(true);
			cb.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e){
					// System.out.println("actionPerformed");
					// remove(pjs.indexOf(path)+1);
					pjs.remove(path);
					refreshItems();
					refreshTextePJs();
				}
			});
			popupPJs.add(cb);
		}
	}

	@Override
	public void changerChoix(int n){
		nSmtp = n;
	}
}
