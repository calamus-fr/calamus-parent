package fr.calamus.common.mail.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListeDestinataires implements Serializable {

	private static final long serialVersionUID = 3888626294977279852L;
	protected List<IDestinataireMap> liste;
	protected static String colId;
	protected static String colNom;
	protected static String colMail;

	public ListeDestinataires() {
		super();
		liste = new ArrayList<>();
	}
	public ListeDestinataires(List l) {
		this();
		addAll(l);
	}

	protected void addAll(List l){
		if(l!=null)for(int i=0;i<l.size();i++){
			if(l.get(i)instanceof IDestinataireMap){
				liste.add((IDestinataireMap) l.get(i));
			}
		}
	}

	public static void init(ModeleTableContacts m) {
		colId = m.getColIdContact();
		colMail = m.getColMailContact();
		colNom = m.getColNomContact();
	}

	public void add(IDestinataireMap sdb) {
		liste.add(sdb);
	}

	/*public Object[][] getObjectsArray(List<String> colonnesTableau) {
		Object[][] tto = new Object[liste.size()][];
		for (int i = 0; i < liste.size(); i++) {
			IDestinataireMap soc = liste.get(i);
			Object[] to = new Object[colonnesTableau.size()];
			for (int j = 0; j < colonnesTableau.size(); j++) {
				String col = colonnesTableau.get(j);
				String val;
				/*if(col.equalsIgnoreCase("cats")){
				 val=ToolBox.mergeList(soc.getCats(), " - ");
				 }else if(col.equalsIgnoreCase("tels")){
				 val=soc.getTelsAffichables();
				 /*ArrayList<String>a=new ArrayList<>();
				 List<String> tels = soc.getTels();
				 for(int it=0;it<3;it++){
				 String type;
				 switch (it) {
				 case 0:
				 type="Fixe";
				 break;
				 case 1:
				 type="Mobile";
				 break;
				 case 2:
				 type="Fax";
				 break;
				 default:
				 type="";
				 break;
				 }
				 if(tels.get(it)!=null){
				 a.add(type+" : "+tels.get(it));
				 }
				 }
				 val=ToolBox.mergeList(a, " - ");*
				//}else{
				val = soc.getValeurAffichable(col);
				//}
				to[j] = val;
			}
			tto[i] = to;
		}
		return tto;
	}*/

	public int size() {
		return liste.size();
	}

	public List<IDestinataireMap> getListe() {
		return liste;
	}

	public ArrayList<Integer> getIds() {
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < liste.size(); i++) {
			l.add(liste.get(i).getId());
		}
		return l;
	}

	public IDestinataireMap getDestinataireMap(int n) {
		return liste.get(n);
	}

	public ListeDestinataires getSousListe(int[] indices) {
		ListeDestinataires l = new ListeDestinataires();
		for (int i = 0; i < indices.length; i++) {
			l.add(getDestinataireMap(indices[i]));
		}
		return l;
	}
}
