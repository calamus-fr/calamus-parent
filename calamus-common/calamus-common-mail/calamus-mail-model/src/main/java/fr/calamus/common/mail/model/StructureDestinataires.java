package fr.calamus.common.mail.model;


import java.io.Serializable;

public enum StructureDestinataires implements Serializable {
	id("id", "Nº", Integer.class), nom("nom", "Nom et prénom", String.class), dirigeant("dirigeant", "Dirigeant",
			String.class), adresse("adresse", "Adresse", String.class), cp("cp", "Code postal", String.class), ville(
			"ville", "Ville", String.class), mail("mail", "E-mail", String.class);

	private String colonne;
	private String label;
	private Class<?> classe;

	<T> StructureDestinataires(String colonne, String label, Class<T> classe){
		this.colonne = colonne;
		this.label = label;
		this.classe = classe;
	}

	public String getColonne(){
		return colonne;
	}

	public String getLabel(){
		return label;
	}

	public Class<?> getClasse(){
		return classe;
	}

	@SuppressWarnings("unchecked")
	public <T> T getCastedValue(Object v){
		if (v == null)
			return null;
		return (T) v;
	}

}