package fr.calamus.common.mail.view;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class PanneauRapportEnvoiMails extends RapportCampagne {
	private static final long serialVersionUID = 9169128950167093821L;
	// private JTextArea textArea;
	private JButton boutonOk;

	public PanneauRapportEnvoiMails(){
		super();
		// textArea=new JTextArea();
		boutonOk = new JButton("Fermer");
		boutonOk.setEnabled(false);
		// add(new JScrollPane(textArea), BorderLayout.CENTER);
		add(boutonOk, BorderLayout.SOUTH);
		boutonOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){
				fermer();
			}
		});
	}

	/*
	 * @Override public void ajouterLigne(final String ligne){ SwingUtilities.invokeLater(new Runnable(){
	 * 
	 * @Override public void run(){ textArea.append(ligne+"\n"); repaint(); } }); }
	 */

	@Override
	public void finEnvoi(){
		super.finEnvoi();
		boutonOk.setEnabled(true);
	}

	public void fermer(){
		Window w = SwingUtilities.windowForComponent(this);
		textArea.setText("");
		boutonOk.setEnabled(false);
		w.dispose();
	}
}
