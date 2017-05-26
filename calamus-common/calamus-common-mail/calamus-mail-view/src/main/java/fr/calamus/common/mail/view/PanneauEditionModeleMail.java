package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.ModeleMailMap;
import fr.calamus.ctrl.CalamusViewController;
import fr.calamus.view.base.IReceveurEventsTBModifTable;
import fr.calamus.view.base.PanneauDetailsToolBarTitreEtDateModif;
import fr.calamus.view.base.ToolBarModifTable;
import fr.calamus.view.base.ZoneTexteRiche;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PanneauEditionModeleMail extends PanneauDetailsToolBarTitreEtDateModif implements DocumentListener {
	private static final long serialVersionUID = -7229893725725458058L;
	//private static final Log log=LogFactory.getLog(PanneauEditionModeleMail.class);
	private ModeleMailMap modeleSelect;
	private ZoneTexteRiche editor;

	public PanneauEditionModeleMail(ModeleMailMap modele){
		this();
		afficherDonnees(modele);
	}
	public PanneauEditionModeleMail(){
		super(CalamusViewController.getInstance().getViewTools(), null, null, "ce modèle");
		editor = new ZoneTexteRiche();
		editor.getSrcEditor().getDocument().addDocumentListener(this);
		editor.getWysEditor().getDocument().addDocumentListener(this);
		setAutreComposant(editor);
		getBarListe().setReceveur(new IReceveurEventsTBModifTable(){

			@Override
			public boolean update(int n){
				if (modeleSelect == null) {
					Map<String, Object> data = new HashMap<String, Object>();
					modeleSelect = new ModeleMailMap(data);
				}
				Date modif = Calendar.getInstance().getTime();
				String t = getChpTitre().getText();
				if (t == null || t.length() == 0) {
					t = JOptionPane.showInputDialog("Entrez un nom pour ce modèle :");
					getChpTitre().setText(t);
				}
				modeleSelect.put("titre", t);
				modeleSelect.put("date_modif", modif);
				modeleSelect.put("html", editor.getText());
				if (ControleurMail.getInstance().saveModele(modeleSelect) > 0) {
					getChpDateModif().setText(getDateFormatter().format(modif));
					// getBarListe().getButton(ToolBarModifTable.UPDATE).setEnabled(false);
					return true;
				}
				return false;
			}

			@Override
			public boolean delete(int[] s){
				if (ControleurMail.getInstance().deleteModele(modeleSelect)) {
					// liste.setSelectedIndices(new int[0]);
					getChpTitre().setText("");
					getChpDateModif().setText("");
					editor.setText("");
					modeleSelect = null;
					// getBarListe().getButton(ToolBarModifTable.UPDATE).setEnabled(false);
					return true;
				}
				return false;
			}

			@Override
			public boolean add(){
				getChpTitre().setText("");
				getChpDateModif().setText("");
				editor.setText("");
				modeleSelect = new ModeleMailMap(new HashMap<String, Object>());
				return true;
				// getBarListe().getButton(ToolBarModifTable.UPDATE).setEnabled(false);
			}

			@Override
			public int[] getSelection(){
				return null;
			}

			@Override
			public void refresh(){
				refreshModele();
			}
		});
		getBarListe().setButtonText(ToolBarModifTable.ADD, "Nouveau modèle");
		getBarListe().setButtonText(ToolBarModifTable.UPDATE, "Enregistrer");
		//log.debug("size=" + getSize());
	}

	public void refreshModele(){
		editor.setText(modeleSelect.getHtml());
		getChpTitre().setText(modeleSelect.getTitre());
		getChpDateModif().setText(getDateFormatter().format(modeleSelect.getDate()));
		getBarListe().getButton(ToolBarModifTable.UPDATE).setEnabled(false);
	}

	@Override
	public void afficherDonnees(Object o){
		modeleSelect = (ModeleMailMap) o;
		refreshModele();
	}

	@Override
	public void insertUpdate(DocumentEvent e){
		getBarListe().autoriserEnregistrement();
	}

	@Override
	public void removeUpdate(DocumentEvent e){
		getBarListe().autoriserEnregistrement();
	}

	@Override
	public void changedUpdate(DocumentEvent e){
		getBarListe().autoriserEnregistrement();
	}

	public ZoneTexteRiche getEditor(){
		return editor;
	}

}
