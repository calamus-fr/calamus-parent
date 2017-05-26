package fr.calamus.common.mail.model;

import java.util.Date;

public class ElementListeNoire {

	private String mail;
	private Date dateAjout;

	public ElementListeNoire(){
		mail=null;
		dateAjout=null;
	}

	public ElementListeNoire(String mail, Date dateAjout){
		super();
		this.mail = mail;
		this.dateAjout = dateAjout;
	}

	public String getMail(){
		return mail;
	}

	public void setMail(String mail){
		this.mail = mail;
	}

	public Date getDateAjout(){
		return dateAjout;
	}

	public void setDateAjout(Date dateAjout){
		this.dateAjout = dateAjout;
	}

}
