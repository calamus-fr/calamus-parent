package fr.calamus.common.mail.model;

import fr.calamus.common.model.EntityMapWithIntId;
import java.util.Map;

public class DestinataireMap extends EntityMapWithIntId implements IDestinataireMap {

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

}
