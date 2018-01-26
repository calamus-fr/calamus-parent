package fr.calamus.common.mail.model;

import fr.calamus.common.model.EntityMapWithIntId;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListeMailing extends EntityMapWithIntId {

	private static final long serialVersionUID = 8438179663259827912L;
	private List<Integer> idsDests;
	@SuppressWarnings("unused")
	private ListeDestinataires listeDestinataires;
	private DateFormat df;

	public ListeMailing(Map<String, Object> map, List<Integer> idsDests){
		super(map, "id,titre,date_modif", "N°,Titre,Date de modification");
		//putAll(map);
		this.idsDests = idsDests;
		df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
	}

	public List<Integer> getIdsDestinataires(){
		if (idsDests == null)
			idsDests = new ArrayList<>();
		return idsDests;
	}

	@Override
	public String toString(){
		return getTitre() + " (" + (getDateModif()==null?null:df.format(getDateModif())) + ")";
	}

	public Date getDateModif(){
		return (Date) get("date_modif");
	}

	public void setDateModif(Date d){
		put("date_modif",d);
	}

	public String getTitre(){
		return (String) get("titre");
	}

	/*public Integer getId(){
		return (Integer) get("id");
	}

	public void setId(int idl){
		put("id", idl);
	}*/

	public void setTitre(String titre){
		put("titre", titre);
	}
}
