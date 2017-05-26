package fr.calamus.common.mail.view;

import fr.calamus.common.mail.core.ControleurMail;
import fr.calamus.common.mail.model.DestinataireMap;
import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.ctrl.CalamusViewController;
import fr.calamus.view.base.IReceveurEventsTBModifTable;
import fr.calamus.view.base.ToolBarModifTable;
import fr.calamus.view.tools.GuiTools;
import fr.calamus.view.tools.IViewTools;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SousPanneauMailsEnregistres extends JPanel implements IReceveurEventsTBModifTable {
	private static final long serialVersionUID = -3174368038070695053L;
	protected JTable table;
	protected String type;
	private MouseAdapter mouseListener;
	private JPopupMenu popup;
	private ToolBarModifTable toolBar;
	private boolean check;
	protected Log log;

	public SousPanneauMailsEnregistres(String type, boolean check){
		super(false);
		log = LogFactory.getLog(getClass());
		this.type = type;
		this.check = check;
		creerComposants();
		afficherComposants();
	}

	protected void creerComposants(){
		log.debug("creerComposants check="+check);
		if (check){
			MailGuiTools.checkParamsMailingOtherThread();
		}
		IViewTools vt = CalamusViewController.getInstance().getViewTools();
		table = vt.creerTable(getDataModel());
		popup = vt.creerPopupMenu();
		mouseListener = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				super.mousePressed(e);
				if (e.getButton() == 3) {
					Point p = table.getMousePosition();
					if (p != null) {
						int r = table.rowAtPoint(p);
						// int c = table.columnAtPoint(p);
						reecrireMenuPopup(r);
					} else {
						reecrireMenuPopup();
					}
					popup.show(table, e.getX(), e.getY());
				}
			}
		};
		addMouseListener(mouseListener);
		table.addMouseListener(mouseListener);
		toolBar = vt.creerToolBarModifTable(this, "un e-mail", false, false);
		toolBar.getButton(ToolBarModifTable.ADD).setIcon(vt.getIconeAddEmail());
		toolBar.setButtonText(ToolBarModifTable.ADD, "Nouvel e-mail");
		toolBar.getButton(ToolBarModifTable.UPDATE).setIcon(vt.getIconeEditEmail());
		toolBar.getButton(ToolBarModifTable.DELETE).setIcon(vt.getIconeDeleteEmail());
	}

	protected void reecrireMenuPopup(){
		popup.removeAll();
		popup.add(creerMenuItemNewMail());
	}

	private JMenuItem creerMenuItemNewMail(){
		JMenuItem mi = new JMenuItem("Nouvel e-mail");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){
				MailGuiTools.ecrireMail(new DestinataireMap());
			}
		});
		return mi;
	}

	protected void reecrireMenuPopup(int r){
		if (r < 0)
			reecrireMenuPopup();
		else {
			popup.removeAll();
			popup.add(creerMenuItemNewMail());
			popup.add(creerItemOuvrirMail(r));
		}
	}

	private JMenuItem creerItemOuvrirMail(int r){
		ArrayList<String> ligne = GuiTools.rowToArrayList(table.getModel(), r);
		final int id = Integer.parseInt(ligne.get(0));
		JMenuItem mi = new JMenuItem("Ouvrir e-mail");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e){
				PanneauEcritureMail pem = new PanneauEcritureMail(id);
				GuiTools.ouvrirDialogue(null, pem, "Ecriture d'un e-mail");
			}
		});
		return mi;
	}

	protected DefaultTableModel getDataModel(){
		return new DefaultTableModel(ControleurMail.getInstance().getMailsData(type), IMailConstantes.titresMails){
			private static final long serialVersionUID = 8314611640096311938L;

			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
	}

	protected void afficherComposants(){
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(table.getTableHeader(), BorderLayout.NORTH);
		add(toolBar, BorderLayout.EAST);
	}

	@Override
	public boolean add(){
		MailGuiTools.ecrireMail(new DestinataireMap());
		return true;
	}

	@Override
	public boolean update(int n){
		Object o = table.getValueAt(n, table.convertColumnIndexToView(0));
		int i;
		if (o instanceof Integer) {
			i = (Integer) o;
		} else {
			i = Integer.parseInt((String) o);
		}
		MailGuiTools.modifierMail(i);
		return true;
	}

	public void refresh(){
		table.setModel(getDataModel());
		table.repaint();
	}

	@Override
	public boolean delete(int[] s){
		return ControleurMail.getInstance().getServiceMail().deleteMails(s);
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

}
