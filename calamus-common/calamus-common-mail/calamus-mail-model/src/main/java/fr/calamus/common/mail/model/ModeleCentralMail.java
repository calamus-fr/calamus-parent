package fr.calamus.common.mail.model;

import fr.calamus.common.tools.ListsAndArrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ModeleCentralMail {

	private static final Log log = LogFactory.getLog(ModeleCentralMail.class);
	private List<ParametresSmtp> paramsMail;
	// private ParametresSmtp paramsMailPersonnels;
	private String userDir;
	private boolean smtp2Enabled;
	//private String expediteur;
	private String texteCbSmtp2;
	private boolean mailConnectionOk;
	//private static final String PROPFILE_MAIL = "mail.properties";
	public static final List<String> colonnesSocietesDansListeMailing = ListsAndArrays.splitToStringList("id,nom,mail", ",");
	public static final List<String> titresContenuListeMailing = ListsAndArrays.splitToStringList("Numéro,Nom,Adresse e-mail",
			",");
	//private List<String> fichiersParamsMail;
	private boolean onlyModifyFroms;
	private boolean canAccessMailbox;

	public ModeleCentralMail(){
		smtp2Enabled = false;
		onlyModifyFroms=false;
		canAccessMailbox=true;
		//fichiersParamsMail = new Vector<String>();
		//fichiersParamsMail.add(PROPFILE_MAIL);
		paramsMail = new ArrayList<>();
		paramsMail.add(new ParametresSmtp());
	}

	/*
	 * public ParametresSmtp getParamsMailDefaut() {
	 * return paramsParDefaut;
	 * }
	 * public void setParamsMailDefaut(Properties props) {
	 * paramsParDefaut.setAttributs(props);
	 * }
	 */

	public void addParamsMail(ParametresSmtp params){
		paramsMail.add(params);
		smtp2Enabled = true;
	}

	public int getParamsMailSize(){
		return paramsMail.size();
	}

	public void setParamsMailPersonnels(ParametresSmtp paramsMailPersonnels){
		paramsMail.set(0, paramsMailPersonnels);
	}

	public ParametresSmtp getParamsMailPersonnels(){
		return paramsMail.get(0);
	}

	public void setParamsMailPersonnels(Properties props){
		getParamsMailPersonnels().setAttributs(props);
	}

	public void setRepertoireUtilisateur(String userDir){
		log.debug("setting user dir = "+userDir);
		this.userDir = userDir;
	}

	public String getRepertoireUtilisateur(){
		return userDir;
	}

	public ParametresSmtp getParamsMail(int n){
		if(n>-1&&n<paramsMail.size())return paramsMail.get(n);
		return null;
	}

	public void addParamsMail(Properties props){
		ParametresSmtp p = new ParametresSmtp();
		p.setAttributs(props);
		paramsMail.add(p);
	}

	public void setSmtp2Enabled(boolean smtp){
		this.smtp2Enabled = smtp;
	}

	public boolean isSmtp2Enabled(){
		return smtp2Enabled;
	}

	public int getLimiteMailsHoraire(){
		return 2000;
	}

	public void setTexteCbSmtp2(String texteCbSmtp2){
		this.texteCbSmtp2 = texteCbSmtp2;
	}

	public String getTexteCbSmtp2(){
		return texteCbSmtp2;
	}

	public boolean isMailConnectionOk(){
		return mailConnectionOk;
	}

	public void setMailConnectionOk(boolean mailConnectionOk){
		this.mailConnectionOk = mailConnectionOk;
	}

	public String getFichierParamsMail(int choixSmtp){
		return "mail"+choixSmtp+".properties";
	}
	public String getFichierParamsMail(int choixSmtp, String email){
		if(email==null)email="demo";
		return "mail"+choixSmtp+"-"+email+".properties";
	}

	public void setOnlyModifyFroms(boolean onlyFroms) {
		onlyModifyFroms=onlyFroms;
	}

	public boolean isOnlyModifyFroms() {
		return onlyModifyFroms;
	}

	public boolean canAccessMailbox(){
		return canAccessMailbox;
	}

	public void setCanAccessMailbox(boolean b){
		canAccessMailbox=b;
	}
}
