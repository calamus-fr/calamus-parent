package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.ElementListeNoire;
import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.common.tools.ToolBox;
import fr.calamus.view.base.IReceveurEventsTBModifTable;
import fr.calamus.view.base.PanneauTableEtSaToolBar;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class SousPanneauListeNoire extends PanneauTableEtSaToolBar {
	private static final long serialVersionUID = 7990057599861202614L;
	private List<ElementListeNoire> elements;

	public SousPanneauListeNoire(){
		super(new DefaultTableModel(null, IMailConstantes.titresListeNoire), "une adresse e-mail", null);
		initModele();
		getToolbar().setReceveur(initReceveur());
	}

	private IReceveurEventsTBModifTable initReceveur(){
		return new IReceveurEventsTBModifTable(){

			@Override
			public boolean update(int n){
				ElementListeNoire elt = elements.get(n);
				String iv = elt.getMail();
				String a = JOptionPane.showInputDialog("Modifiez l'adresse :", iv);
				if (a != null && a.contains("@") && !a.equals(iv)) {
					return ControleurMail.getInstance().getServiceMail().modifierMailListeNoire(elt, a);
				}
				return false;
			}

			@Override
			public boolean delete(int[] s){
				List<String> l = new Vector<String>();
				for (int i = 0; i < s.length; i++) {
					l.add(elements.get(s[i]).getMail());
				}
				return ControleurMail.getInstance().getServiceMail().supprimerMailsListeNoire(l);
			}

			@Override
			public boolean add(){
				String a = JOptionPane.showInputDialog("Saisissez l'adresse à ajouter à la liste noire :");
				if (a != null && a.contains("@")) {
					return ControleurMail.getInstance().getServiceMail().ajouterAListeNoire(a);
				}
				return false;
			}

			@Override
			public int[] getSelection(){
				int[] indices = table.getSelectedRows();
				if (indices.length > 0) {
					for (int i = 0; i < indices.length; i++) {
						indices[i] = table.convertRowIndexToModel(indices[i]);
					}
				}
				return indices;
			}

			@Override
			public void refresh(){
				refreshListeNoire();
			}
		};
	}

	public void refreshListeNoire(){
		initModele();
	}

	protected void initModele(){
		//
		new Thread("getListeNoire"){
			public void run(){
				elements = ControleurMail.getInstance().getServiceMail() == null ? new Vector<ElementListeNoire>()
						: ControleurMail.getInstance().getServiceMail().getListeNoire();
				final Vector<Vector<String>> data = listeNoireToVector(elements);
				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run(){
						getTable().setModel(new DefaultTableModel(data, IMailConstantes.titresListeNoire));
						repaint();
					}
				});
			}

			private Vector<Vector<String>> listeNoireToVector(List<ElementListeNoire> l){
				Vector<Vector<String>> v = new Vector<Vector<String>>();
				for (int i = 0; i < l.size(); i++) {
					ElementListeNoire e = l.get(i);
					Vector<String> v1 = new Vector<String>();
					v1.add(e.getMail());
					v1.add(ToolBox.getFormatteurDateSimpleFr().format(e.getDateAjout()));
					v.add(v1);
				}
				return v;
			};
		}.start();
	}

}
