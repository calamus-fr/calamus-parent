package fr.calamus.common.mail.view;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.ModeleCentralMail;
import fr.calamus.common.tools.ListsAndArrays;
import fr.calamus.view.base.ComboChoixEtLabel;
import fr.calamus.view.notification.Notifications;

public class ComboChoixSmtp extends ComboChoixEtLabel implements ItemListener {
	private static final long serialVersionUID = 7434473894086787472L;
	protected static String choix1 = "primaire";
	protected static String choix2 = "secondaire";
	private IReceveurChoixSmtp receveur;

	public ComboChoixSmtp(IReceveurChoixSmtp receveur){
		super("Utiliser le SMTP :", new Vector<String>(ListsAndArrays.arrayToList(new String[]{ choix1, choix2 })), null);
		addItemListener(this);
		this.receveur = receveur;
	}

	@Override
	public void itemStateChanged(ItemEvent e){
		String item = (String) e.getItem();
		ModeleCentralMail m = ControleurMail.getInstance().getModele();
		if (choix2.equals(item)) {
			if (m.isSmtp2Enabled())
				receveur.changerChoix(1);
			else {
				cb.removeItemListener(this);
				cb.setSelectedIndex(0);
				addItemListener(this);
				Notifications.show(this, "Un seul SMTP est disponible.");
			}
		} else
			receveur.changerChoix(0);
	}

}
