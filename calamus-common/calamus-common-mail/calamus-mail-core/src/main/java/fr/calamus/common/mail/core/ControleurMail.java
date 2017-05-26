package fr.calamus.common.mail.core;

import fr.calamus.common.db.core.DbAccess;
import fr.calamus.common.mail.model.EMailDataBean;
import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.common.mail.model.ListeDestinataires;
import fr.calamus.common.mail.model.ListeMailing;
import fr.calamus.common.mail.model.ModeleCentralMail;
import fr.calamus.common.mail.model.ModeleMailMap;
import fr.calamus.common.mail.model.ModeleTableContacts;
import fr.calamus.common.mail.model.ParametresSmtp;
import fr.calamus.common.mail.service.IServiceMail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ControleurMail {

	// private static final String PROPFILE_DEF_MAIL="defaultMail.properties";
	// private static final String PROPFILE_MAIL2="mail2.properties";
	private static ControleurMail me;
	// private static String inlinePropertiesSeparator = "§µ§";
	private ModeleCentralMail modele;
	private IControleurConfUtilisateur controleurConf;
	private Mailer mailer;
	private IServiceMail serviceMail;
	private IControleurDestinataires controleurDests;
	private ThreadCompteurDeMails compteurDeMails;
	private List<ISensibleAConnexionMail> composantsSensiblesAConnexion;
	private static Connection tempCnx = null;
	private static final Log log=LogFactory.getLog(ControleurMail.class);

	public ControleurMail(){
		log.debug("ControleurMail(constr)");
		serviceMail = null;
		composantsSensiblesAConnexion = Collections.synchronizedList(new ArrayList<ISensibleAConnexionMail>());
		if (tempCnx == null) {
			log.debug("tempCnx == null");
			tempCnx = MailDbConnectionManager.getConnection();
			// TODO createTableMap a compléter pour chaque logiciel utilisant ce projet
			// IMailConstantes.createTableMap.put("contacts",
			// "create table contacts(id integer not null primary key,nom varchar(100),mail varchar(100))");
		}
		log.debug("fin ControleurMail(constr)");
	}

	public static void setConnexionDb(Connection cnx){
		tempCnx = cnx;
	}

	public static ControleurMail getInstance(){
		if (me == null)
			me = new ControleurMail();
		return me;
	}

	public ModeleCentralMail getModele(){
		return modele;
	}

	/*
	 * public Properties loadPropertiesMail(){
	 * return loadPropertiesMail(modele.getRepertoireUtilisateur());
	 * }
	 */

	public Properties loadPropertiesMail(int n){
		String userDir = modele.getRepertoireUtilisateur();
		ParametresSmtp paramsMail = modele.getParamsMail(n);
		//String usermail=paramsMail==null?null:paramsMail.getFromAddress();
		if(userDir==null)return null;
		log.debug("loadPropertiesMail : " + userDir + File.separator + modele.getFichierParamsMail(n));
		Properties prop = new Properties();
		File file = new File(userDir + File.separator + modele.getFichierParamsMail(n));
		if (file.exists()) {
			try {
				prop.load(new FileInputStream(file));
			} catch (IOException e) {
				log.error("",e);
			}
		}
		return prop;
	}

	public void saveParamsMail(int choixSmtp, Map<String, String> params){
		log.debug("saveParamsMail : " + params);
		if(modele.getRepertoireUtilisateur()==null)return;
		Properties props = new Properties();
		ParametresSmtp paramsMail = modele.getParamsMail(choixSmtp);
		//String usermail=paramsMail==null?null:paramsMail.getFromAddress();
		File file = new File(modele.getRepertoireUtilisateur() + File.separator
				+ modele.getFichierParamsMail(choixSmtp));
		log.debug(" file=" + file.getAbsolutePath());
		props.putAll(params);
		try {
			props.store(new FileOutputStream(file), "");
			getModele().setParamsMailPersonnels(props);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * public Properties getPropertiesDefaultMail() { Properties prop=new Properties(); //File
	 * file=this.getClass().getResourceAsStream(name)//new File(USER_DIR+File.separator+PROPFILE_MAIL);
	 * //if(file.exists()){ try { prop.load(Mailer.class.getResourceAsStream(PROPFILE_DEF_MAIL)); } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } //} return
	 * prop; }
	 */

	public void saveMail(EMailDataBean dataBean, String type, boolean ok){
		getServiceMail().saveMail(dataBean, type, ok);
	}

	public EMailDataBean getDataBean(int idMail){
		return getServiceMail().getEMailDataBean(idMail);
	}

	public List<ModeleMailMap> getModelesMail(){
		if (getServiceMail() == null)
			return new ArrayList<>();
		return getServiceMail().getModelesMail();
	}

	public Map<Integer, List<Integer>> getMapIdsDestsDansListesMailing(){
		return getServiceMail().getMapIdsSocietesDansListesMailing();
	}

	public List<ListeMailing> getListesMailing(){
		return getServiceMail().getListesMailing();
	}

	public Object[][] getMailsData(String type){
		if (getServiceMail() == null)
			return null;
		return getServiceMail().getMailsData(type);
	}

	public void setServiceMail(IServiceMail serviceMail){
		this.serviceMail = serviceMail;
	}

	public ModeleCentralMail initModeleAvecParamsCommuns(IControleurConfUtilisateur controleurConf,
			IControleurDestinataires controleurDests, Properties params, String userDir, ModeleTableContacts mtc, boolean smtp2,
			String texteCbSmtp2){
		this.controleurConf = controleurConf;
		this.controleurDests = controleurDests;
		modele = new ModeleCentralMail();
		modele.setRepertoireUtilisateur(userDir);
		File f = new File(userDir);
		if (!f.exists())
			f.mkdir();
		modele.setSmtp2Enabled(smtp2);// CustomSmtp(controleurConf.isCustomSmtp());
		modele.setTexteCbSmtp2(texteCbSmtp2);
		// mm.setMailUser(controleurConf.getMailUser());
		// mm.setParamsMailDefaut(getPropertiesDefaultMail());
		//modele.setExpediteur(controleurConf.getMailUser());
		modele.setParamsMailPersonnels(loadPropertiesMail(0));
		log.debug("params(1)="+modele.getParamsMailPersonnels());
		modele.setParamsMailPersonnels(params);
		log.debug("params(2)="+modele.getParamsMailPersonnels());
		modele.setOnlyModifyFroms(true);
		// mm.getParamsMailCustom().setFrom(mm.getParamsMailCustom().getUserSmtp().contains("@")?
		// mm.getParamsMailCustom().getUserSmtp():mm.getParamsMailCustom().getUserSmtp()+"@"+mm.getParamsMailCustom().getHoteSmtp());
		// mm.getParamsMailCustom().setFrom(controleurConf.getMailUser());
		//Session smtp=getMailer().initSession(modele.getParamsMailPersonnels());
		boolean imapOk=getMailer().checkImap();
		log.debug("imapOk="+imapOk);
		modele.setMailConnectionOk(imapOk);
		serviceMail = new MailsDataAccess(new DbAccess(tempCnx, IMailConstantes.createTableMap), mtc);
		return modele;
	}
	public ModeleCentralMail initModele(IControleurConfUtilisateur controleurConf,
			IControleurDestinataires controleurDests, String userDir, ModeleTableContacts mtc, boolean smtp2,
			String texteCbSmtp2){
		this.controleurConf = controleurConf;
		this.controleurDests = controleurDests;
		modele = new ModeleCentralMail();
		modele.setRepertoireUtilisateur(userDir);
		File f = new File(userDir);
		if (!f.exists())
			f.mkdir();
		modele.setSmtp2Enabled(smtp2);// CustomSmtp(controleurConf.isCustomSmtp());
		modele.setTexteCbSmtp2(texteCbSmtp2);
		// mm.setMailUser(controleurConf.getMailUser());
		// mm.setParamsMailDefaut(getPropertiesDefaultMail());
		//modele.setExpediteur(controleurConf.getMailUser());
		modele.setParamsMailPersonnels(loadPropertiesMail(0));
		// mm.getParamsMailCustom().setFrom(mm.getParamsMailCustom().getUserSmtp().contains("@")?
		// mm.getParamsMailCustom().getUserSmtp():mm.getParamsMailCustom().getUserSmtp()+"@"+mm.getParamsMailCustom().getHoteSmtp());
		// mm.getParamsMailCustom().setFrom(controleurConf.getMailUser());
		boolean imapOk=getMailer().checkImap();
		log.debug("imapOk="+imapOk);
		modele.setMailConnectionOk(imapOk);
		serviceMail = new MailsDataAccess(new DbAccess(tempCnx, IMailConstantes.createTableMap), mtc);
		return modele;
	}
	public ModeleCentralMail initModeleWebapp(IControleurConfUtilisateur controleurConf,
			IControleurDestinataires controleurDests, ModeleTableContacts mtc, boolean isPg, ParametresSmtp params, DbAccess baseAccess){
		this.controleurConf = controleurConf;
		this.controleurDests = controleurDests;
		modele = new ModeleCentralMail();
		modele.setRepertoireUtilisateur(null);
		/*File f = new File(userDir);
		if (!f.exists())
			f.mkdir();*/
		modele.setSmtp2Enabled(false);// CustomSmtp(controleurConf.isCustomSmtp());
		modele.setTexteCbSmtp2(null);
		// mm.setMailUser(controleurConf.getMailUser());
		// mm.setParamsMailDefaut(getPropertiesDefaultMail());
		//modele.setExpediteur(controleurConf.getMailUser());
		modele.setParamsMailPersonnels(params);
		// mm.getParamsMailCustom().setFrom(mm.getParamsMailCustom().getUserSmtp().contains("@")?
		// mm.getParamsMailCustom().getUserSmtp():mm.getParamsMailCustom().getUserSmtp()+"@"+mm.getParamsMailCustom().getHoteSmtp());
		// mm.getParamsMailCustom().setFrom(controleurConf.getMailUser());
		modele.setMailConnectionOk(getMailer().checkImap());
		serviceMail = new MailsDataAccess(new DbAccess(baseAccess, isPg?IMailConstantes.createTableMapPg:IMailConstantes.createTableMap), mtc);
		return modele;
	}

	/*
	 * public void saveCustomSMTP(boolean b){ controleurConf.saveCustomSMTP(b); }
	 */

	public IServiceMail getServiceMail(){
		return serviceMail;
	}

	public IControleurConfUtilisateur getControleurConf(){
		return controleurConf;
	}

	/*
	 * public ListeSocietes getListeSocietesParIds(List<Integer> ids){ return
	 * dispatcher.getServiceSocietes().getListeSocietesParId(ids); }
	 */

	public Mailer getMailer(){
		if (mailer == null)
			mailer = new Mailer();
		return mailer;
	}

	public ListeDestinataires getListeDestinatairesParIds(List<Integer> ids){
		return controleurDests.getListeDestinatairesParIds(ids);
	}

	public int saveModele(ModeleMailMap modeleSelect){
		return getServiceMail().saveModele(modeleSelect);
	}

	public Object[][] getMailInboxData(ParametresSmtp smtp) throws MessagingException{
		// List<String> listeMailsContacts = getServiceMail().getTousEMailsContacts();
		return getMailer().getInbox(null, smtp);
	}

	public int getNbMailsRestantsPourLHeure(){
		if (compteurDeMails == null) {
			compteurDeMails = new ThreadCompteurDeMails(getModele().getLimiteMailsHoraire());
			compteurDeMails.start();
		}
		return compteurDeMails.getRestantsPourLHeure();
	}

	public void compterMails(int nb){
		if (compteurDeMails == null) {
			compteurDeMails = new ThreadCompteurDeMails(getModele().getLimiteMailsHoraire());
			compteurDeMails.start();
		}
		compteurDeMails.compterMails(nb);
	}

	public boolean deleteModele(ModeleMailMap modeleSelect){
		return getServiceMail().deleteModele(modeleSelect);
	}

	public void connectionMailEtablie(int n){
		for (int i = 0; i < composantsSensiblesAConnexion.size(); i++) {
			composantsSensiblesAConnexion.get(i).connectionMailEtablie(n);
		}
	}

	public void connectionMailFermee(int n){
		for (int i = 0; i < composantsSensiblesAConnexion.size(); i++) {
			composantsSensiblesAConnexion.get(i).connectionMailFermee(n);
		}
	}

	public void retirerSensibleAConnexionMail(ISensibleAConnexionMail c){
		composantsSensiblesAConnexion.remove(c);
	}

	public void enregistrerSensibleAConnexionMail(ISensibleAConnexionMail c){
		composantsSensiblesAConnexion.add(c);
	}

	public void changerModeConnexionMail(int n, boolean online){
		if (online) {
			connectionMailEtablie(n);
		} else {
			connectionMailFermee(n);
		}
	}

	public List<String> getNomsListesMailing() {
		List<String> l=new ArrayList<>();
		List<ListeMailing> lms = getListesMailing();
		if(lms==null)return l;
		for (int i = 0; i < lms.size(); i++) {
			ListeMailing lm = lms.get(i);
			if(lm!=null)l.add(lm.getTitre());
		}
		return l;
	}

	public void supprimerDestinataireDesListes(Integer id) {
		if(id==null)return;
		List<ListeMailing> lms = getListesMailing();
		int[]ids=new int[1];
		ids[0]=id;
		for (int i = 0; i < lms.size(); i++) {
			ListeMailing lm = lms.get(i);
			getServiceMail().supprimerDestinatairesDeLaListe(lm, ids);
		}
	}

	public ListeMailing getListeMailingParNom(String nom) {
		return getServiceMail().getListeMailing(nom);
	}

}
