package fr.calamus.common.mail.view;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.common.tools.ListsAndArrays;
import fr.calamus.view.base.ComboChoixEtLabel;

public class PanneauMailsEnregistres extends JPanel {
	private static final long serialVersionUID = 3432619852470408187L;
	private ComboChoixEtLabel choixTypeMails;
	private SousPanneauMailsEnregistres pEnvoyes;
	private SousPanneauMailsEnregistres pBrouillons;

	public PanneauMailsEnregistres(boolean check){
		super(new BorderLayout(3, 3));
		pEnvoyes = new SousPanneauMailsEnregistres(IMailConstantes.MAILS_ENVOYES, check);
		pBrouillons = new SousPanneauMailsEnregistres(IMailConstantes.MAILS_BROUILLONS, check);
		choixTypeMails = new ComboChoixEtLabel("Dossier : ", ListsAndArrays.arrayToList(new String[]{
				IMailConstantes.MAILS_ENVOYES, IMailConstantes.MAILS_BROUILLONS }), new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e){
				String item = e == null ? null : (String) e.getItem();
				changerPanneau(item);
			}
		});
		add(choixTypeMails, BorderLayout.NORTH);
		add(pEnvoyes, BorderLayout.CENTER);
	}

	protected void changerPanneau(String cle){
		switch (cle) {
			case IMailConstantes.MAILS_BROUILLONS:
				remove(pEnvoyes);
				add(pBrouillons, BorderLayout.CENTER);
				repaint();
				break;

			case IMailConstantes.MAILS_ENVOYES:
				remove(pBrouillons);
				add(pEnvoyes, BorderLayout.CENTER);
				repaint();
				break;

			default:
				break;
		}
	}
}
