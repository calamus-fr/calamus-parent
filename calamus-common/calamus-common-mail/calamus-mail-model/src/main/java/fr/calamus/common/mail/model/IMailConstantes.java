package fr.calamus.common.mail.model;

import fr.calamus.common.tools.ListsAndArrays;
import fr.calamus.common.tools.ToolBox;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public interface IMailConstantes {
	public static final String MAILS_ETAT_OK = "Ok";
	public static final String MAILS_ETAT_ECHEC = "Echec";
	public static final String MAILS_ENVOYES = "E-mails envoyés";
	public static final String MAILS_BROUILLONS = "Brouillons";
	public static final String[] titresMails = { "N°", "Destinataires", "Sujet", "Date", "Etat" };
	public static final String[] titresInbox = { "Expéditeur", "Sujet", "Date", "Texte" };
	public static final String sColsMailsTableau = "id,dests,sujet,date,etat";// exp,texte,pjs,type
	public static final String sColsInboxTableau = "exp,sujet,date,html";// exp,texte,pjs,type
	public static final Map<String, String> createTableMap = new HashMap<String, String>(){
		private static final long serialVersionUID = -7305049644388398302L;
		{
			put("mails",
					"CREATE TABLE MAILS(ID INTEGER NOT NULL PRIMARY KEY,EXP VARCHAR(100),DESTS VARCHAR(1000),BCC VARCHAR(1000),SUJET VARCHAR(200),TEXTE VARCHAR(10000),"
							+ "PJS VARCHAR(1000),DATE DATE,TYPE VARCHAR(20),ETAT VARCHAR(20))");
			put("listes_mailing",
					"create table listes_mailing(id integer not null primary key,titre varchar(50) not null unique, date_modif date)");
			put("contenu_listes_mailing",
					"create table contenu_listes_mailing(id_contact integer not null, id_liste integer not null,primary key(id_contact,id_liste))");
			put("liste_noire", "create table liste_noire(mail varchar(100) not null primary key, date_ajout date)");
			put("modeles_mails",
					"create table modeles_mails(id integer not null primary key,titre varchar(50) not null unique,date_modif date,html varchar(10000))");
			put("campagnes", "create table campagnes(id integer not null primary key," + "nominative boolean,"
					+ "html varchar(20000),id_liste integer,etat_lancement varchar(20),etat_livraison varchar(50))");
			put("mails_dans_campagnes",
					"create table mails_dans_campagnes(id_campagne integer not null,id_mail integer not null,primary key(id_campagne,id_mail))");

		}
	};
	public static final Map<String, String> createTableMapPg = new HashMap<String, String>(){
		private static final long serialVersionUID = -7305049644388398302L;
		{
			put("mails",
					"CREATE TABLE MAILS(id INTEGER NOT NULL PRIMARY KEY,exp character varying(100),dests character varying(1000),bcc character varying(100),SUJET character varying(200),TEXTE character varying(10000),"
							+ "PJS character varying(1000),DATE DATE,TYPE character varying(20),ETAT character varying(20))");
			put("listes_mailing",
					"create table listes_mailing(id integer not null primary key,titre character varying(50) not null unique, date_modif date)");
			put("contenu_listes_mailing",
					"create table contenu_listes_mailing(id_contact integer not null, id_liste integer not null,primary key(id_contact,id_liste))");
			put("liste_noire", "create table liste_noire(mail character varying(100) not null primary key, date_ajout date)");
			put("modeles_mails",
					"create table modeles_mails(id integer not null primary key,titre character varying(50) not null unique,date_modif date,html character varying(10000))");
			put("campagnes", "create table campagnes(id integer not null primary key," + "nominative boolean,"
					+ "html character varying(20000),id_liste integer,etat_lancement character varying(20),etat_livraison character varying(50))");
			put("mails_dans_campagnes",
					"create table mails_dans_campagnes(id_campagne integer not null,id_mail integer not null,primary key(id_campagne,id_mail))");

		}
	};
	public static final Vector<String> titresListeNoire = new Vector<>(ListsAndArrays.arrayToList(new String[]{ "E-mail",
			"Date d'ajout en liste noire" }));
}
