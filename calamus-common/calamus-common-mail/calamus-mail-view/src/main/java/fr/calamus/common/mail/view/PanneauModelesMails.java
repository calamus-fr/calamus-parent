package fr.calamus.common.mail.view;

import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.ModeleMailMap;
import fr.calamus.view.base.PanneauDetails;
import fr.calamus.view.base.PanneauListeDetails;
import fr.calamus.view.base.ToolBarModifTable;
import fr.calamus.view.base.ZoneTexteRiche;

public class PanneauModelesMails extends PanneauListeDetails {
	private static final long serialVersionUID = -4515654654256063131L;
	// private ZoneTexteRiche editor;
	private List<ModeleMailMap> modeles;
	// private JTextField chpTitre;
	// private AALabel chpDateModif;
	private PanneauEditionModeleMail panneauDetails;

	// private ModeleMailMap modeleSelect;
	// private SimpleDateFormat df;

	public PanneauModelesMails(){
		super();
	}

	@Override
	protected void creerPanneauDetails(){
		panneauDetails = new PanneauEditionModeleMail();
	}

	@Override
	protected void initDonnees(){
		modeles = ControleurMail.getInstance().getModelesMail();
	}

	public ToolBarModifTable getToolBarDetailsListe(){
		return panneauDetails.getBarListe();
	}

	@Override
	protected ListModel<String> initModeleListe(){
		return new ListModel<String>(){

			@Override
			public void removeListDataListener(ListDataListener l){

			}

			@Override
			public int getSize(){
				return modeles.size();
			}

			@Override
			public String getElementAt(int index){
				return modeles.get(index).toString();
			}

			@Override
			public void addListDataListener(ListDataListener l){

			}
		};
	}

	@Override
	protected int getLargeurListe(){
		return 150;
	}

	@Override
	protected void afficherDetails(int n){
		// modeleSelect=;
		panneauDetails.afficherDonnees(modeles.get(n));
	}

	@Override
	public PanneauDetails getPanneauDetails(){
		return panneauDetails;
	}

	public ZoneTexteRiche getZoneTexteDetails(){
		return panneauDetails.getEditor();
	}

	@Override
	protected JPopupMenu initPopup(){
		// TODO initPopup()
		return null;
	}

}
