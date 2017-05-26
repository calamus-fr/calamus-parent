package fr.calamus.common.mail.model;

public class ModeleTableContacts {
	private String tableContacts;
	private String colIdContact;
	private String colNomContact;
	private String colMailContact;
	public ModeleTableContacts(String tableContacts, String colIdContact,
			String colNomContact, String colMailContact){
		super();
		this.tableContacts = tableContacts;
		this.colIdContact = colIdContact;
		this.colNomContact = colNomContact;
		this.colMailContact = colMailContact;
	}
	public String getTableContacts(){
		return tableContacts;
	}
	public String getColIdContact(){
		return colIdContact;
	}
	public String getColNomContact(){
		return colNomContact;
	}
	public String getColMailContact(){
		return colMailContact;
	}

}
