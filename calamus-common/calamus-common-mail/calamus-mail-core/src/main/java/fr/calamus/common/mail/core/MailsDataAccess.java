package fr.calamus.common.mail.core;

import fr.calamus.common.db.core.DbAccess;
import fr.calamus.common.mail.model.EMailDataBean;
import fr.calamus.common.mail.model.ElementListeNoire;
import fr.calamus.common.mail.model.IDestinataireMap;
import fr.calamus.common.mail.model.IMailConstantes;
import fr.calamus.common.mail.model.ListeDestinataires;
import fr.calamus.common.mail.model.ListeMailing;
import fr.calamus.common.mail.model.ModeleMailMap;
import fr.calamus.common.mail.model.ModeleTableContacts;
import fr.calamus.common.mail.service.IServiceMail;
import fr.calamus.common.tools.ListsAndArrays;
import fr.calamus.common.tools.ToolBox;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MailsDataAccess implements IServiceMail {

	private DbAccess dbAccess;
	private ModeleTableContacts modeleTableContacts;
	private static final Log log = LogFactory.getLog(MailsDataAccess.class);

	public MailsDataAccess(DbAccess dbAccess, ModeleTableContacts modeleTableContacts){
		this.dbAccess = dbAccess;
		this.modeleTableContacts = modeleTableContacts;
	}

	public Object[][] getMailsData(String type){
		String colsString = IMailConstantes.sColsMailsTableau;
		List<String> colsList = ListsAndArrays.splitToStringList(colsString, ",");
		String sql = "select " + colsString + " from mails where type=" + ToolBox.echapperStringPourHSql(type);
		List<Map<String, Object>> rl = dbAccess.selectMany(sql);
		Object[][] o = new Object[rl.size()][];
		for (int i = 0; i < rl.size(); i++) {
			o[i] = new Object[colsList.size()];
			for (int j = 0; j < colsList.size(); j++) {
				o[i][j] = rl.get(i).get(colsList.get(j));
			}
		}
		return o;
	}

	@Override
	public int saveMail(EMailDataBean em, String type, boolean ok){
		boolean insert = false;
		int id;
		log.debug("saveMail "+em);
		if (em.getId() == null) {
			Map<String,Object> mapId = dbAccess.selectOne("select max(id) from mails");
			log.debug("map max="+mapId);
			List<String> keys = new ArrayList<>(mapId.keySet());
			Object idMax = mapId.get(keys.get(0));
			if (idMax == null)
				idMax = 0;
			id = (Integer) idMax + 1;
			em.setId(id);
			insert = true;
		}
		id = em.getId();
		String sql;
		if (insert) {
			String vals = id + "," + ToolBox.echapperStringPourHSql(em.getFrom()) + ","
					+ ToolBox.echapperStringPourHSql(em.getTo()) + "," + ToolBox.echapperStringPourHSql(em.getBcc()) + "," + ToolBox.echapperStringPourHSql(em.getSujet())
					+ "," + ToolBox.echapperStringPourHSql(em.getTexte()) + ","
					+ ToolBox.echapperStringPourHSql(ListsAndArrays.mergeList(em.getPjs(), "; ")) + ",now(),"
					+ ToolBox.echapperStringPourHSql(type) + "," + ToolBox.echapperStringPourHSql(ok ? "Ok" : "Echec");
			sql = "insert into mails(id,exp,dests,bcc,sujet,texte,pjs,date,type,etat) values(" + vals + ")";
		} else {
			String vals = "exp=" + ToolBox.echapperStringPourHSql(em.getFrom()) + ",dests="
					+ ToolBox.echapperStringPourHSql(em.getTo()) + ",bcc="
					+ ToolBox.echapperStringPourHSql(em.getBcc()) + ",sujet="
					+ ToolBox.echapperStringPourHSql(em.getSujet()) + ",texte="
					+ ToolBox.echapperStringPourHSql(em.getTexte()) + ",pjs="
					+ ToolBox.echapperStringPourHSql(ListsAndArrays.mergeList(em.getPjs(), ", ")) + ",date=now(),type="
					+ ToolBox.echapperStringPourHSql(type) + ",etat=" +  ToolBox.echapperStringPourHSql(ok ? "Ok" : "Echec");
			sql = "update mails set(" + vals + ") where id=" + em.getId();
		}
		dbAccess.executeUpdate(sql);
		return id;
	}

	public EMailDataBean getEMailDataBean(int idMail){
		String sql = "select * from mails where id=" + idMail;
		Map<String, Object> map = dbAccess.selectOne(sql);
		return mapToEMailDataBean(map);
	}

	public List<EMailDataBean> getEMailDataBeans(String type){
		String sql = "select * from mails where type=" + ToolBox.echapperStringPourHSql(type);
		List<Map<String, Object>> maps = dbAccess.selectMany(sql);
		List<EMailDataBean> l = new Vector<EMailDataBean>();
		for (int i = 0; i < maps.size(); i++) {
			l.add(mapToEMailDataBean(maps.get(i)));
		}
		return l;// mapToEMailDataBean(map);
	}

	// "id,dests,sujet,date,etat";//exp,texte,pjs,type
	private EMailDataBean mapToEMailDataBean(Map<String, Object> map){
		EMailDataBean em = new EMailDataBean();
		em.setFrom((String) map.get("exp"));
		em.setTo((String) map.get("dests"));
		em.setSujet((String) map.get("sujet"));
		em.setDate((Date) map.get("date"));
		em.setEtat((String) map.get("etat"));
		em.setId((Integer) map.get("id"));
		em.setTexte((String) map.get("texte"));
		em.setPjs(ListsAndArrays.splitToStringList((String) map.get("pjs"), ", "));
		em.setType((String) map.get("type"));
		return em;
	}

	public List<ModeleMailMap> getModelesMail(){
		List<Map<String, Object>> lm = dbAccess.selectMany("select * from modeles_mails order by date_modif desc");
		List<ModeleMailMap> mm = new ArrayList<>();
		for (int i = 0; i < lm.size(); i++) {
			mm.add(new ModeleMailMap(lm.get(i)));
		}
		return mm;
	}

	public Map<Integer, List<Integer>> getMapIdsSocietesDansListesMailing(){
		List<Map<String, Object>> ml = dbAccess
				.selectMany("select id_liste,id_contact from contenu_listes_mailing order by id_liste,id_contact asc");
		Map<Integer, List<Integer>> m = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < ml.size(); i++) {
			int idl = (Integer) ml.get(i).get("id_liste");
			if (!m.containsKey(idl)) {
				m.put(idl, new ArrayList<Integer>());
			}
			int ids = (Integer) ml.get(i).get("id_contact");
			m.get(idl).add(ids);
		}
		return m;
	}
	public List<Integer> getIdsSocietesDansListeMailing(int idListe){
		List<Map<String, Object>> ml = dbAccess
				.selectMany("select id_liste,id_contact from contenu_listes_mailing where id_liste="+idListe);
		//Map<Integer, List<Integer>> m = new HashMap<Integer, List<Integer>>();
		List<Integer>l=new ArrayList<>();
		for (int i = 0; i < ml.size(); i++) {
			int ids = (Integer) ml.get(i).get("id_contact");
			l.add(ids);
		}
		return l;
	}

	public List<Integer> getIdsContactsDansListeMailingParIdListe(int idListe){
		List<Map<String, Object>> ml = dbAccess
				.selectMany("select id_liste,id_contact from contenu_listes_mailing where id_liste=" + idListe
						+ " order by id_contact asc");
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < ml.size(); i++) {
			int ids = (Integer) ml.get(i).get("id_contact");
			l.add(ids);
		}
		return l;
	}

	public List<Integer> getIdsContactsDansListeMailingParNomListe(String nomListe){
		List<Map<String, Object>> ml = dbAccess
				.selectMany("select id_liste,id_contact from contenu_listes_mailing,listes_mailing"
						+ " where contenu_listes_mailing.id_liste=listes_mailing.id and titre="
						+ ToolBox.echapperStringPourHSql(nomListe) + " order by id_contact asc");
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < ml.size(); i++) {
			int ids = (Integer) ml.get(i).get("id_contact");
			l.add(ids);
		}
		return l;
	}

	public Map<String, Integer> getMapAffichageToIdListe(){
		Map<String, Integer> m = new HashMap<String, Integer>();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
		List<Map<String, Object>> ml = dbAccess
				.selectMany("select id,titre,date_modif from listes_mailing order by date_modif desc");
		for (int i = 0; i < ml.size(); i++) {
			int idl = (Integer) ml.get(i).get("id");
			String sd = df.format(ml.get(i).get("date_modif"));
			String titre = (String) ml.get(i).get("titre");
			m.put(titre + " (" + sd + ")", idl);
		}
		return m;
	}

	@Override
	public List<ListeMailing> getListesMailing(){
		List<ListeMailing> l = new ArrayList<>();
		List<Map<String, Object>> ml = dbAccess
				.selectMany("select id,titre,date_modif from listes_mailing order by date_modif desc");
		Map<Integer, List<Integer>> m = getMapIdsSocietesDansListesMailing();
		for (int i = 0; i < ml.size(); i++) {
			int idl = (Integer) ml.get(i).get("id");
			l.add(new ListeMailing(ml.get(i), m.get(idl)));
		}
		return l;
	}

	@Override
	public int saveListeMailing(ListeMailing l){
		/*
		 * int idl; boolean insert=false; if(!l.containsKey("id") || l.get("id")==null){ /*Object idMax =
		 * dbAccess.selectOne("select max(id) from listes_mailing").get("max"); dbAccess.getMax("listes_mailing",
		 * "id")+1; if(idMax==null)idMax=0;* idl=dbAccess.getMax("listes_mailing", "id")+1; l.setId(idl); insert=true;
		 * }else{ idl=l.getId(); } String sql; if(insert){ String vals =
		 * idl+","+ToolBox.echapperStringPourHSql(l.getTitre())+",now()";
		 * //+ToolBox.echapperStringPourHSql(em.getSujet()
		 * )+","+ToolBox.echapperStringPourHSql(em.getTexte())+","+ToolBox
		 * .echapperStringPourHSql(ToolBox.mergeList(em.getPjs(), "; "))
		 * //+",now(),"+ToolBox.echapperStringPourHSql(type)+","+ToolBox.echapperStringPourHSql(ok?"Ok":"Echec");
		 * sql="insert into listes_mailing(id,titre,date_modif) values("+vals+")"; }else{ String
		 * vals="date_modif=now(),titre="+ToolBox.echapperStringPourHSql(l.getTitre());
		 * sql="update listes_mailing set("+vals+") where id="+idl; } log.debug(sql, this); try {
		 * dbAccess.openStatement().executeUpdate(sql); } catch (SQLException e) { e.printStackTrace(); }finally{
		 * dbAccess.closeStatement(); }
		 */
		return saveMapObject(l, "id", "listes_mailing", "insert into listes_mailing(id,titre,date_modif) values(", ","
				+ ToolBox.echapperStringPourHSql(l.getTitre()) + ",NOW())",
				"update listes_mailing set date_modif=NOW(),titre=" + ToolBox.echapperStringPourHSql(l.getTitre())
						+ " where id=");
	}

	@Override
	public void ajouterAListe(String nomListe, List<? extends IDestinataireMap> prospects){
		int idListe = getIdListeParSonNom(nomListe);
		log.debug("ajouterAListe idListe="+idListe);
		List<Integer> existants = getIdsContactsDansListeMailingParIdListe(idListe);
		Statement st = dbAccess.openStatement();
		try {
			for (int i = 0; i < prospects.size(); i++) {
				log.debug(" dest "+prospects.get(i).getId());
				if (!existants.contains(prospects.get(i).getId())) {
					st.executeUpdate("insert into contenu_listes_mailing(id_liste,id_contact) values(" + idListe + ","
							+ prospects.get(i).getId() + ")");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbAccess.closeStatement(st);
		}
	}

	private Integer getIdListeParSonNom(String nomListe){
		Map<String, Object> ml = dbAccess.selectOne("select id,titre,date_modif from listes_mailing where titre="
				+ ToolBox.echapperStringPourHSql(nomListe));
		if (ml == null || ml.get("id") == null)
			return null;
		return (Integer) ml.get("id");
	}

	@Override
	public int saveModele(ModeleMailMap m){
		/*
		 * int idl; boolean insert=false; if(!m.containsKey("id") || m.get("id")==null){ /*Object idMax =
		 * dbAccess.selectOne("select max(id) from listes_mailing").get("max"); dbAccess.getMax("listes_mailing",
		 * "id")+1; if(idMax==null)idMax=0;* idl=dbAccess.getMax("modeles_mails", "id")+1; m.setId(idl); insert=true;
		 * }else{ idl=m.getId(); } String sql; if(insert){ String vals =
		 * idl+","+ToolBox.echapperStringPourHSql(m.getTitre())+",now(),"+ToolBox.echapperStringPourHSql(m.getHtml());
		 * //
		 * +ToolBox.echapperStringPourHSql(em.getSujet())+","+ToolBox.echapperStringPourHSql(em.getTexte())+","+ToolBox
		 * .echapperStringPourHSql(ToolBox.mergeList(em.getPjs(), "; "))
		 * //+",now(),"+ToolBox.echapperStringPourHSql(type)+","+ToolBox.echapperStringPourHSql(ok?"Ok":"Echec");
		 * sql="insert into modeles_mails(id,titre,date_modif,html) values("+vals+")"; }else{ String
		 * vals="date_modif=now(),titre="
		 * +ToolBox.echapperStringPourHSql(m.getTitre()+",html="+ToolBox.echapperStringPourHSql(m.getHtml()));
		 * sql="update modeles_mails set("+vals+") where id="+idl; } log.debug(sql, this); try {
		 * dbAccess.openStatement().executeUpdate(sql); } catch (SQLException e) { e.printStackTrace(); }finally{
		 * dbAccess.closeStatement(); }
		 */
		return saveMapObject(
				m,
				"id",
				"modeles_mails",
				"insert into modeles_mails(id,titre,date_modif,html) values(",
				"," + ToolBox.echapperStringPourHSql(m.getTitre()) + ",now(),"
						+ ToolBox.echapperStringPourHSql(m.getHtml()) + ")",
				"update modeles_mails set(date_modif=now(),titre="
						+ ToolBox.echapperStringPourHSql(m.getTitre() + ",html="
								+ ToolBox.echapperStringPourHSql(m.getHtml())) + ") where id=");
	}

	public int saveMapObject(Map<String, Object> m, String key, String table, String sqlIns1, String sqlIns2,
			String sqlUpd){
		int id;
		boolean insert = false;
		if (!m.containsKey(key) || m.get(key) == null || new Integer(-1).equals(m.get(key))) {
			/*
			 * Object idMax = dbAccess.selectOne("select max(id) from listes_mailing").get("max");
			 * dbAccess.getMax("listes_mailing", "id")+1; if(idMax==null)idMax=0;
			 */
			id = dbAccess.getMax(table, key) + 1;
			m.put(key, id);
			insert = true;
		} else {
			id = (int) m.get(key);
		}
		String sql;
		if (insert) {
			// String vals =
			// id+","+ToolBox.echapperStringPourHSql(m.getTitre())+",now(),"+ToolBox.echapperStringPourHSql(m.getHtml());
			// +ToolBox.echapperStringPourHSql(em.getSujet())+","+ToolBox.echapperStringPourHSql(em.getTexte())+","+ToolBox.echapperStringPourHSql(ToolBox.mergeList(em.getPjs(),
			// "; "))
			// +",now(),"+ToolBox.echapperStringPourHSql(type)+","+ToolBox.echapperStringPourHSql(ok?"Ok":"Echec");
			// sql="insert into modeles_mails(id,titre,date_modif,html) values("+vals+")";
			sql = sqlIns1 + id + sqlIns2;
		} else {
			// String
			// vals="date_modif=now(),titre="+ToolBox.echapperStringPourHSql(m.getTitre()+",html="+ToolBox.echapperStringPourHSql(m.getHtml()));
			// sql="update modeles_mails set("+vals+") where id="+id;
			sql = sqlUpd + id;
		}
		log.debug(sql);
		dbAccess.executeUpdate(sql);
		return id;
	}

	@Override
	public List<String> getTousEMailsContacts(){
		if(modeleTableContacts==null)return null;
		String cm = modeleTableContacts.getColMailContact();
		return dbAccess.selectStringCol("select distinct " + cm + " from " + modeleTableContacts.getTableContacts()
				+ " where " + cm + " is not null and " + cm + "!=''", cm);
	}

	@Override
	public boolean deleteModele(ModeleMailMap m){
		return dbAccess.executeUpdate("delete from modeles_mails where id=" + m.getId()) > 0;
	}

	@Override
	public List<ElementListeNoire> getListeNoire(){
		List<Map<String, Object>> lm = dbAccess.selectMany("select * from liste_noire order by date_ajout asc");
		List<ElementListeNoire> l = new Vector<ElementListeNoire>();
		for (int i = 0; i < lm.size(); i++) {
			Map<String, Object> m = lm.get(i);
			ElementListeNoire e = new ElementListeNoire((String) m.get("mail"), (Date) m.get("date_ajout"));
			l.add(e);
		}
		return l;
	}

	@Override
	public boolean deleteMails(int[] idsa){
		List<Integer> ids = ListsAndArrays.arrayToList(idsa);
		if (ids == null || ids.size() == 0)
			return false;
		return dbAccess.executeUpdate("delete from mails where id in(" + ListsAndArrays.mergeList(ids, ", ") + ")") > 0;
	}

	@Override
	public boolean ajouterAListeNoire(String mail){
		return dbAccess.executeUpdate("insert into liste_noire(mail, date_ajout) values("
				+ ToolBox.echapperStringPourHSql(mail.trim()) + ", now())") > 0;
	}

	@Override
	public boolean supprimerMailsListeNoire(List<String> l){
		return dbAccess.executeUpdate("delete from liste_noire where mail in(" + ListsAndArrays.echapperListStringPourHSql(l)
				+ ")") > 0;
	}

	@Override
	public boolean modifierMailListeNoire(ElementListeNoire elt, String mail){
		return dbAccess.executeUpdate("update liste_noire set mail=" + ToolBox.echapperStringPourHSql(mail.trim())
				+ ", date_ajout=now() where mail=" + ToolBox.echapperStringPourHSql(elt.getMail())) > 0;
	}

	@Override
	public boolean supprimerDestinatairesDeLaListe(ListeMailing listeMailing, int[] idsArray){
		log.debug("supprimerDestinatairesDeLaListe");
		log.debug("listeMailing="+listeMailing);
		log.debug("ids="+ListsAndArrays.mergeIntArray(idsArray, ","));
		List<Integer> ids2 = listeMailing.getIdsDestinataires();
		log.debug("dests="+ListsAndArrays.mergeList(ids2, ","));
		List<Integer> ids = new ArrayList<>();
		if(idsArray==null)return false;
		if(ids2==null)return false;
		for (int i = 0; i < idsArray.length; i++) {
			int id=idsArray[i];
			if(ids2.contains(id))ids.add(id);
		}
		log.debug("ids="+ListsAndArrays.mergeList(ids, ","));
		if(ids.isEmpty())return false;
		return dbAccess.executeUpdate("delete from contenu_listes_mailing where id_liste=" + listeMailing.getId()
				+ " and id_contact in(" + ListsAndArrays.mergeList(ids, ",") + ")") >= 0;
	}

	@Override
	public boolean deleteListesMailing(int[] s){
		List<Integer> ids = ListsAndArrays.arrayToList(s);
		if (ids == null || ids.isEmpty())
			return false;
		dbAccess.executeUpdate("delete from contenu_listes_mailing where id_liste in(" + ListsAndArrays.mergeList(ids, ", ") + ")");
		return dbAccess.executeUpdate("delete from listes_mailing where id in(" + ListsAndArrays.mergeList(ids, ", ") + ")") > 0;
	}

	@Override
	public void ajouterAListe(String nomListe, ListeDestinataires prospects) {
		ajouterAListe(nomListe, prospects.getListe());
	}

	@Override
	public ListeMailing getListeMailing(String titre) {
		Map<String, Object> ml = dbAccess
				.selectOne("select id,titre,date_modif from listes_mailing where titre="+ToolBox.echapperStringPourHSql(titre));
		int idl=(int)ml.get("id");
		List<Integer> l = getIdsSocietesDansListeMailing(idl);
		ListeMailing lm = new ListeMailing(ml, l);
		return lm;
	}
}
