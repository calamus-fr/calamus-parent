package fr.calamus.common.db.model;

public interface IColonnesDb {

	public String getColonne();

	public String getLabel();

	public Class<?> getClasse();

	public <T> T getCastedValue(Object v);

}