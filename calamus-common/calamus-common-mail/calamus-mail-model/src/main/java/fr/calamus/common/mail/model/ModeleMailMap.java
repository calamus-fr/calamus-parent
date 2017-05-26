package fr.calamus.common.mail.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModeleMailMap extends HashMap<String, Object>{
	private static final long serialVersionUID = 1594118151720226632L;

	/**
	 * id(pk),titre,date_modif,html
	 */
	public ModeleMailMap(Map<String, Object>data){
		super();
		putAll(data);
	}

	public Integer getId() {
		return (Integer) get("id");
	}
	public String getHtml() {
		return (String) get("html");
	}
	public Date getDate(){
		return (Date)get("date_modif");
	}
	public String getTitre(){
		return (String)get("titre");
	}
	@Override
	public String toString() {
		DateFormat df=DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
		return getTitre()+" ("+df.format(getDate())+")";
	}

	public void setId(int id){
		put("id", id);
	}
}
