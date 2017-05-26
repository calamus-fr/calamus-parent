package fr.calamus.common.mail.core;

public interface IControleurConfUtilisateur {

	boolean isCustomSmtp();

	String getMailUser();

	void saveCustomSMTP(boolean b);

	void setMailUserSansSave(String from);

}
