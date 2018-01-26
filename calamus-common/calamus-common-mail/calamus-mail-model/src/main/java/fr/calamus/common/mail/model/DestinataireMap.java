package fr.calamus.common.mail.model;

import fr.calamus.common.model.EntityMapWithIntId;
import java.util.Map;

public class DestinataireMap extends EntityMapWithIntId implements IDestinataireMap {

	private static final long serialVersionUID = 698558271438067121L;

	/*protected Integer id;
	protected String nom;
	protected String mail;*/

	public DestinataireMap() {
		super("id,nom,mail","Id,Nom,E-mail");
	}

	public DestinataireMap(Map<String, ? extends Object> dbMap) {
		super(dbMap,"id,nom,mail","Id,Nom,E-mail");
	}

	public DestinataireMap(Map<String, ? extends Object> dbMap, String cols, String labels) {
		super(dbMap,cols, labels);
	}

	public DestinataireMap(String cols, String labels) {
		super(cols, labels);
	}

	/*public DestinataireMap(String nom, String mail) {
		super("id,nom,mail","Id,Nom,E-mail");
		setNom(nom);
		setMail(mail);
	}*/

	public DestinataireMap(Integer id, String nom, String mail) {
		super("id,nom,mail","Id,Nom,E-mail");
		setId(id);
		setNom(nom);
		setMail(mail);
	}

	@Override
	public String getNom() {
		return (String) get("nom");
	}

	@Override
	public void setNom(String nom) {
		put("nom", nom);
	}

	@Override
	public String getMail() {
		return (String) get("mail");
	}

	@Override
	public void setMail(String mail) {
		put("mail", mail);
	}

	@Override
	public String getValeurAffichable(String key) {
		return get(key)==null?"":get(key).toString();
	}

	/*private void _putAll(Map<String, ? extends Object> dbMap) {
		for (String k : dbMap.keySet()) {
			Object o = dbMap.get(k);
			if (o == null) {
				put(k, null);
			} else if (o instanceof String) {
				put(k, (String) o);
			} else {
				put(k, o.toString());
			}
		}
	}*/

}
