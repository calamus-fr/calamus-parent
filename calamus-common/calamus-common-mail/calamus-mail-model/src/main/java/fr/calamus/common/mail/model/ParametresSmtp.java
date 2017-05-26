package fr.calamus.common.mail.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParametresSmtp extends EnumMap<ParametresSmtp.Cle, String> {

	private static final long serialVersionUID = -3644288525060238123L;
	private static final Log log = LogFactory.getLog(ParametresSmtp.class);

	public static enum Cle {
		user("Nom d'utilisateur",null), pwd("Mot de passe",null),
		fromAddress("Adresse d'expédition",null), fromPersonal("Nom de l'expéditeur",null),
		hoteSmtp("Hôte SMTP",null), hoteImap("Hôte IMAP",null),
		sslSmtp("Connexion SSL pour SMTP","1"), sslImap("Connexion SSL pour SMTP","1"),
		portSmtp("Port SMTP","465"), portImap("Port IMAP","993"),
		delai("Delai entre deux e-mails (s)","30"), nbDests("Nombre maximal de destinataires par message","30");
		private String label;
		private String defaultValue;

		private Cle(String label, String defaultValue){
			this.label = label;
			this.defaultValue = defaultValue;
		}

		public String getLabel(){
			return label;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

	}

	public ParametresSmtp(){
		super(Cle.class);
	}

	public ParametresSmtp(String hoteSmtp,String hoteImap,String user,String pwd,boolean sslImap,boolean sslSmtp,int portImap,int portSmtp,int delai,int nbDests,String fromAddress,String fromPersonal){
		this();
		put(Cle.hoteSmtp,hoteSmtp);
		put(Cle.hoteImap,hoteImap);
		put(Cle.user,user);
		put(Cle.pwd,pwd);
		put(Cle.fromAddress,fromAddress);
		put(Cle.fromPersonal,fromPersonal);
		put(Cle.portImap,""+portImap);
		put(Cle.portSmtp,""+portSmtp);
		put(Cle.delai,""+delai);
		put(Cle.nbDests,""+nbDests);
		put(Cle.sslImap,""+sslImap);
		put(Cle.sslSmtp,""+sslSmtp);
	}
	public ParametresSmtp(Properties props){
		this();
		setAttributs(props);
	}
	public ParametresSmtp(Map<String,String> props){
		this();
		setAttributs(props);
	}

	public void setAttributs(Properties peProps){
		log.debug(peProps.toString());
		Cle[] keys = Cle.values();
		for (int i = 0; i < keys.length; i++) {
			if (peProps.containsKey(keys[i].toString())) {
				put(keys[i], peProps.getProperty(keys[i].toString()));
			}
		}
	}
	public void setAttributs(Map<String,String> peProps){
		log.debug(peProps.toString());
		for (int i = 0; i < Cle.values().length; i++) {
			if (peProps.containsKey(Cle.values()[i].toString())) {
				put(Cle.values()[i], peProps.get(Cle.values()[i].toString()));
			}
		}
	}

	public String getHote(){
		return get(Cle.hoteSmtp);
	}

	public String getUser(){
		return get(Cle.user);
	}

	public String getPwd(){
		return get(Cle.pwd);
	}

	public String getPortSmtp(){
		return get(Cle.portSmtp);
	}

	public String getPortImap(){
		return get(Cle.portImap);
	}

	public String getFromAddress(){
		return get(Cle.fromAddress);
	}

	public String getFromPersonal(){
		return get(Cle.fromPersonal);
	}

	public String getFrom(){
		if(getFromPersonal()==null){
			return getFromAddress();
		}
		return getFromPersonal()+" <"+getFromAddress()+">";
	}
	/*
	 * public String get(String key){
	 * if(key==null)return null;
	 * if(key.equals(SMTP_HOST))return getHoteSmtp();
	 * else if(key.equals(SMTP_USER))return getUserSmtp();
	 * else if(key.equals(SMTP_PWD))return getPwdSmtp();
	 * else if(key.equals(SMTP_PORT))return getPortSmtp();
	 * else if(key.equals(NB_DESTS))return ""+getNbDestsParMail();
	 * else if(key.equals(DELAI))return ""+getDelai();
	 * else return null;
	 * }
	 */

	public int getDelai(){
		return Integer.parseInt(get(Cle.delai));
	}

	public int getNbDestsParMail(){
		return Integer.parseInt(get(Cle.nbDests));
	}

	public boolean parametresTousRemplisSmtp(){
		//log.debug("parametresTousRemplis?", this);
		boolean b = true;
		for (int i = 0; i < Cle.values().length; i++) {
			Cle c = Cle.values()[i];
			boolean b2=get(c) == null || "".equals(get(c).trim());
			if (!c.toString().startsWith("ssl") && !c.toString().endsWith("Imap") && b2){
				b = false;
				log.debug("  pas bon :");
			}
			log.debug("  " + c + " : " +get(c)+ "->" + !b2);
		}
		return b;
	}
	public boolean parametresTousRemplisImap(){
		//log.debug("parametresTousRemplis?", this);
		boolean b = true;
		for (int i = 0; i < Cle.values().length; i++) {
			Cle c = Cle.values()[i];
			boolean b2=get(c) == null || "".equals(get(c).trim());
			if (!c.toString().startsWith("ssl") && !c.toString().endsWith("Smtp") && b2)
				b = false;
			log.debug("  " + c + " : " +get(c)+ "->" + !b2);
		}
		return b;
	}

	public boolean sslSmtpOk(){
		String s = get(Cle.sslSmtp);
		return s != null && (s.equalsIgnoreCase("ok") || s.equalsIgnoreCase("oui")
			|| s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1"));
	}
}
