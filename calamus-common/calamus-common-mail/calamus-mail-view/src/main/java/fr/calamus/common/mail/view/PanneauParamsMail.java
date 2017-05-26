package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.ParametresSmtp;
import fr.calamus.common.mail.model.ParametresSmtp.Cles;
import fr.calamus.ctrl.CalamusViewController;
import fr.calamus.view.base.IPanneauRedimensionnable;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PanneauParamsMail extends JPanel implements ActionListener, IPanneauRedimensionnable, IReceveurChoixSmtp {
	private static final long serialVersionUID = 3721871329217266569L;
	// private ArrayList<String> ligneClient;
	private ArrayList<JLabel> labels;
	private ArrayList<JTextField> champs;
	private JButton boutonSauvegarder;
	private JButton boutonRestaurer;
	private JDialog dialogue;
	private ComboChoixSmtp cbSmtp;
	@SuppressWarnings("unused")
	private int choixSmtp;
	private static final int GAP = 5;
	private static final int PADDING_COTE = 20;
	private static final int PADDING_HAUT = 20;
	private static final int HAUT_ELT = 30;
	private static final int LARG_LABEL = 120;
	private static final int LARG_BOUTON = 110;
	private static final Log log = LogFactory.getLog(PanneauParamsMail.class);

	public PanneauParamsMail(){
		super();
		choixSmtp = 0;
		dialogue = null;
		setLayout(null);
		creerEtAjouterComposants();
		afficherDonnees(ControleurMail.getInstance().getModele().getParamsMailPersonnels());
	}

	private void afficherDonnees(ParametresSmtp p){
		for (int i = 0; i < labels.size(); i++) {
			champs.get(i).setText(p.get(ParametresSmtp.Cles.valueOf(labels.get(i).getName())));
		}
		repaint();
	}

	private void creerEtAjouterComposants(){
		// labelHaut=new JLabel("Paramétrage SMTP");
		labels = new ArrayList<JLabel>();
		labels.add(creerLabel(ParametresSmtp.Cles.host));
		labels.add(creerLabel(ParametresSmtp.Cles.user));
		labels.add(creerLabel(ParametresSmtp.Cles.pwd));
		labels.add(creerLabel(ParametresSmtp.Cles.ssl));
		labels.add(creerLabel(ParametresSmtp.Cles.portSmtp));
		labels.add(creerLabel(ParametresSmtp.Cles.portImap));
		labels.add(creerLabel(ParametresSmtp.Cles.delai));
		labels.add(creerLabel(ParametresSmtp.Cles.nbDests));
		labels.add(creerLabel(ParametresSmtp.Cles.fromAddress));
		labels.add(creerLabel(ParametresSmtp.Cles.fromPersonal));

		KeyListener ecModif = new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e){

			}

			@Override
			public void keyReleased(KeyEvent e){
				if ("Fermer".equalsIgnoreCase(boutonSauvegarder.getText())) {
					boutonSauvegarder.setText("Sauvegarder");
				}
			}

			@Override
			public void keyPressed(KeyEvent e){

			}
		};
		champs = new ArrayList<>();
		for (int i = 0; i < labels.size(); i++) {
			champs.add(CalamusViewController.getInstance().getViewTools().creerTextField());
			champs.get(i).addKeyListener(ecModif);
			add(labels.get(i));
			add(champs.get(i));
		}
		boutonSauvegarder = new JButton("Sauvegarder");
		boutonRestaurer = new JButton("Restaurer");
		boutonSauvegarder.addActionListener(this);
		boutonRestaurer.addActionListener(this);
		add(boutonRestaurer);
		add(boutonSauvegarder);
		if (ControleurMail.getInstance().getModele().isSmtp2Enabled()) {
			cbSmtp = new ComboChoixSmtp(this);
			add(cbSmtp);
		}
		griserChamps(false);
	}

	private JLabel creerLabel(Cles cle){
		JLabel l = new JLabel(cle.getLabel());
		l.setName(cle.toString());
		return l;
	}

	@Override
	public void redimensionner(int larg, int haut){
		Insets ins = getInsets();
		larg -= (ins.right + ins.left);
		haut -= (ins.top + ins.bottom);
		setBounds(0, 0, larg, haut);
		int x = PADDING_COTE;
		int y = PADDING_HAUT;
		int l1 = (larg - 3 * PADDING_COTE) / 2;
		int x2 = 2 * PADDING_COTE + l1;
		if (ControleurMail.getInstance().getModele().isSmtp2Enabled()) {
			cbSmtp.setBounds(x, y, larg - 2 * PADDING_COTE, HAUT_ELT);
			y += HAUT_ELT + GAP;
		}
		for (int i = 0; i < labels.size() / 2; i++) {// ça va tant que labels.size() est pair
			labels.get(i * 2).setBounds(x, y, LARG_LABEL, HAUT_ELT);
			champs.get(i * 2).setBounds(x + LARG_LABEL, y, l1 - LARG_LABEL, HAUT_ELT);
			labels.get(i * 2 + 1).setBounds(x2, y, LARG_LABEL, HAUT_ELT);
			champs.get(i * 2 + 1).setBounds(x2 + LARG_LABEL, y, l1 - LARG_LABEL, HAUT_ELT);
			y += HAUT_ELT + GAP;
		}
		boutonRestaurer.setBounds(larg - 3 * GAP - LARG_BOUTON, y, LARG_BOUTON, HAUT_ELT);
		boutonSauvegarder.setBounds(larg - 4 * GAP - 2 * LARG_BOUTON, y, LARG_BOUTON, HAUT_ELT);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource() instanceof JButton) {
			String txt = ((JButton) e.getSource()).getText();
			if (txt.equals("Sauvegarder")) {
				sauvegarder();
			} else if (txt.equals("Restaurer")) {
				restaurer();
			} else if (txt.equals("Fermer")) {
				dialogue.dispose();
			}
		} else if (e.getSource() instanceof JCheckBox) {
			boolean b = ((JCheckBox) e.getSource()).isSelected();
			griserChamps(!b);
			// ParametresEnvoi.getInstance().
		}
	}

	private void griserChamps(boolean b){
		for (int i = 0; i < champs.size(); i++) {
			champs.get(i).setEnabled(!b);
		}
		repaint();
	}

	private void restaurer(){
		log.debug("restaurer : " + ControleurMail.getInstance().getModele().getParamsMail(choixSmtp));
		for (int i = 0; i < labels.size(); i++) {
			champs.get(i).setText(
					ControleurMail.getInstance().getModele().getParamsMail(choixSmtp)
							.get(ParametresSmtp.Cles.valueOf(labels.get(i).getName())));
			log.debug(labels.get(i).getName() + " = " + champs.get(i).getText());
		}
		repaint();
	}

	private void sauvegarder(){
		HashMap<String, String> h = new HashMap<String, String>();
		for (int i = 0; i < labels.size(); i++) {
			h.put(labels.get(i).getName(), champs.get(i).getText());
		}
		log.debug("save: " + h);
		ControleurMail.getInstance().saveParamsMail(choixSmtp, h);
		// ControleurMail.getInstance().saveCustomSMTP(cbCustom.isSelected());
		boutonSauvegarder.setText("Fermer");
	}

	@Override
	public JPanel getThisPanel(){
		return this;
	}

	@Override
	public void setDialogue(JDialog f){
		dialogue = f;
	}

	@Override
	public int getPreferredWidth(){
		return 700;
	}

	@Override
	public int getPreferredHeight(){
		return 260;
	}

	@Override
	public void changerChoix(int n){
		afficherDonnees(ControleurMail.getInstance().getModele().getParamsMail(n));
		choixSmtp = n;
	}

}
