package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.ListeDestinataires;
import fr.calamus.common.mail.model.ListeMailing;
import fr.calamus.common.mail.model.ModeleCentralMail;
import fr.calamus.ctrl.CalamusViewController;
import fr.calamus.view.base.*;
import fr.calamus.view.tools.GuiTools;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;

public class PanneauListesMailing extends PanneauListeDetails {
	private static final long serialVersionUID = -9172618073011378931L;
	private List<ListeMailing> listes;
	private ArrayList<String> affichables;
	private PanneauDetailsToolBarTitreEtDateModif details;
	protected ListeMailing listeMailing;
	private PanneauTableEtSaToolBar table;
	private JButton boutonMail;

	public PanneauListesMailing(){
		super(true);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				super.componentShown(e);
				log.debug("shown");
				actualiserListe();
			}

		});
	}

	@Override
	protected void creerPanneauDetails(){
		boutonMail = new JButton(CalamusViewController.getInstance().getViewTools().getIconeStartEmail());
		boutonMail.setToolTipText("Envoyer un e-mail à cette liste");
		boutonMail.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){
				envoyerMailATouteLaListe();
			}
		});
		table = new PanneauTableEtSaToolBar(initModele(null), "un destinataire", new IReceveurEventsTBModifTable(){

			@Override
			public boolean update(int n){
				return modifierDestinataire(n);
			}

			@Override
			public boolean delete(int[] s){
				return supprimerDestinataires(s);
			}

			@Override
			public boolean add(){
				JOptionPane.showMessageDialog(PanneauListesMailing.this,
						"Veuillez rechercher de nouveaux destinataires dans votres base de données locale",
						"Ajout de destinataires", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}

			@Override
			public int[] getSelection(){
				int[] indices = table.getTable().getSelectedRows();
				if (indices.length > 0) {
					for (int i = 0; i < indices.length; i++) {
						indices[i] = table.getTable().convertRowIndexToModel(indices[i]);
					}
				}
				return indices;
			}

			@Override
			public void refresh(){
				refreshTable();
			}
		});
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getToolbar().add(boutonMail);
		details = new PanneauDetailsToolBarTitreEtDateModif(CalamusViewController.getInstance().getViewTools(),
				new IReceveurEventsTBModifTable(){

					@Override
					public boolean update(int n){
						if (listeMailing == null) {
							Map<String, Object> data = new HashMap<String, Object>();
							listeMailing = new ListeMailing(data, new ArrayList<Integer>());
						}
						Date modif = Calendar.getInstance().getTime();
						listeMailing.put("titre", details.getChpTitre().getText());
						listeMailing.put("date_modif", modif);
						// modeleSelect.put("html", editor.getText());
						if (ControleurMail.getInstance().getServiceMail().saveListeMailing(listeMailing) > 0) {
							details.getChpDateModif().setText(details.formatDate(modif));
							// details.getBarListe().getButton(ToolBarModifTable.UPDATE).setEnabled(false);
							return true;
						}
						return false;
					}

					@Override
					public boolean delete(int[] s){
						int[] id = new int[1];
						if (listeMailing == null)
							return false;
						id[0] = listeMailing.getId();
						return ControleurMail.getInstance().getServiceMail().deleteListesMailing(id);
					}

					@Override
					public boolean add(){
						listeMailing = new ListeMailing(new HashMap<String, Object>(), new ArrayList<Integer>());
						return false;
					}

					@Override
					public int[] getSelection(){
						return null;
					}

					@Override
					public void refresh(){
						actualiserListe();
					}
				}, table, "la liste"){
			private static final long serialVersionUID = 6347479469844696596L;

			@Override
			public void afficherDonnees(Object o){
				if (o != null && !(o instanceof ListeMailing))
					return;
				ListeMailing lm = (ListeMailing) o;
				table.setModel(initModele(lm));
				if (lm == null) {
					getChpTitre().setEnabled(false);
					table.getTable().setEnabled(false);
					table.getToolbar().setEnabled(false);
					getChpDateModif().setText("");
					getChpTitre().setText("");
				} else {
					getChpDateModif().setText(formatDate(lm.getDateModif()));
					getChpTitre().setText(lm.getTitre());
					getChpTitre().setEnabled(true);
					table.setEnabled(true);
					table.getToolbar().setEnabled(true);
				}
				finCreationTable();
				table.revalidate();
				table.repaint();
			}

		};
		details.getBarListe().setButtonText(ToolBarModifTable.ADD, "Créer une liste");
	}

	protected boolean supprimerDestinataires(int[] indices){
		return ControleurMail.getInstance().getServiceMail().supprimerDestinatairesDeLaListe(listeMailing, indices);
	}

	protected boolean modifierDestinataire(int i){
		/*
		 * if(ControleurMail.getInstance().modifierDestinataireDeLaListe(listeMailing, i)){ refresh(); }
		 */
		return false;
	}

	private void refreshTable(){
		table.getTable().setModel(initModele(listeMailing));
		table.repaint();
	}

	public JTable getTable(){
		return table.getTable();
	}

	@Override
	protected void initDonnees(){
		listes = ControleurMail.getInstance().getServiceMail() == null ? new Vector<ListeMailing>() : ControleurMail
				.getInstance().getServiceMail().getListesMailing();
		affichables = new ArrayList<String>();
		for (int i = 0; i < listes.size(); i++) {
			affichables.add(listes.get(i).toString());
		}
	}

	@Override
	protected ListModel<String> initModeleListe(){
		return new ListModel<String>(){

			@Override
			public void removeListDataListener(ListDataListener l){

			}

			@Override
			public int getSize(){
				return affichables.size();
			}

			@Override
			public String getElementAt(int index){
				return affichables.get(index);
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
		listeMailing = listes.get(n);
		details.afficherDonnees(listes.get(n));
	}

	@Override
	public PanneauDetails getPanneauDetails(){
		return details;
	}

	protected void envoyerMailATouteLaListe(){
		log.debug("envoyerMailATouteLaListe - not implemented");
	}

	public ToolBarModifTable getToolBarDetailsListe(){
		return details.getBarListe();
	}

	public ToolBarModifTable getToolBarDetailsTable(){
		return table.getToolbar();
	}

	protected void envoyerMailing(){
		log.debug("envoyerMailing");/*
									 * int[] indices = table.getSelectedRows(); for (int i = 0; i < indices.length; i++)
									 * { indices[i]=table.convertRowIndexToModel( indices[i]); }
									 */
		Integer[] rowIndices = GuiTools.getConvertedToModelSelectedRows(table.getTable());
		if (rowIndices.length == 0) {
			rowIndices = new Integer[table.getTable().getRowCount()];
			for (int i = 0; i < rowIndices.length; i++) {
				rowIndices[i] = i;
			}
		}
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < rowIndices.length; i++) {
			ids.add(Integer.parseInt((String) table.getTable().getValueAt(rowIndices[i], 0)));
		}
		ListeDestinataires listeDests = ControleurMail.getInstance().getListeDestinatairesParIds(ids);
		MailGuiTools.ecrireMail(listeDests);
	}

	protected DefaultTableModel initModele(ListeMailing lm){
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		if (lm != null) {
			List<Integer> ids = lm.getIdsDestinataires();
			ListeDestinataires ls = ControleurMail.getInstance().getListeDestinatairesParIds(ids);
			for (int i = 0; i < lm.getIdsDestinataires().size(); i++) {
				Vector<String> v = new Vector<String>();
				v.add("" + ls.getDestinataireMap(i).getId());
				v.add(ls.getDestinataireMap(i).getNom());
				v.add(ls.getDestinataireMap(i).getMail());
				data.add(v);
			}
		}
		DefaultTableModel m = new DefaultTableModel(data, new Vector<String>(
				ModeleCentralMail.titresContenuListeMailing));
		return m;
	}

	@Override
	protected JPopupMenu initPopup(){
		// TODO initPopup()
		return null;
	}

	/*
	 * public void afficherDonnees(ListeMailing listeMailing){ this.listeMailing = listeMailing;
	 * table.setModel(initModele(listeMailing)); if (listeMailing == null) { chpTitre.setEnabled(false);
	 * table.setEnabled(false); barTable.setEnabled(false); } else { chpTitre.setEnabled(true); table.setEnabled(true);
	 * barTable.setEnabled(true); } table.repaint(); }
	 */
	public void finCreationTable() {
		
	}

}
