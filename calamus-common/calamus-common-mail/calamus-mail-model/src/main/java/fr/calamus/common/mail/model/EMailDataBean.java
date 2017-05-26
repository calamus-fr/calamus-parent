package fr.calamus.common.mail.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EMailDataBean implements Serializable, Cloneable{

	private static final long serialVersionUID = 4216260465257461023L;
	private String from;
	private String to;
	private String bcc;
	private List<String> pjs;
	private String sujet;
	private String texte;
	private Integer id;
	private Date date;
	private String etat;
	private String type;
	
	public EMailDataBean() {
		id=null;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public List<String> getPjs() {
		return pjs;
	}

	public void setPjs(List<String> list) {
		this.pjs = list;
	}

	public String getSujet() {
		return sujet;
	}

	public void setSujet(String sujet) {
		this.sujet = sujet;
	}

	public String getTexte() {
		return texte;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public void setDate(Date date) {
		this.date=date;
	}

	public Date getDate() {
		return date;
	}

	public void setEtat(String etat) {
		this.etat=etat;
	}

	public String getEtat() {
		return etat;
	}

	public void setType(String type) {
		this.type=type;
	}

	public String getType() {
		return type;
	}

	public EMailDataBean cloneBean(){
		try {
			return (EMailDataBean) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
