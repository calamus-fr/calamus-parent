/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.db.core;

import fr.calamus.common.db.model.RequestBuilder;
import fr.calamus.common.model.BaseEntityMap;
import fr.calamus.common.model.EntityMap;
import fr.calamus.common.model.EntityMapWithIntId;
import fr.calamus.common.model.EntityMapWithStringId;
import fr.calamus.common.tools.CommonDateFormats;
import fr.calamus.common.tools.ListsAndArrays;
import fr.calamus.common.tools.ToolBox;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

/**
 *
 * @author haerwynn
 */
public class EntitiesAccess {

	//private static SimpleDateFormat pgDateFormatter;
	//private static SimpleDateFormat pgTimestampFormatter;

	protected final Log log;
	private final String table;
	private DbAccess dba;
	private int maxLines;

	public EntitiesAccess(String table, DbAccess dba) {
		log = LogFactory.getLog(getClass());
		this.table = table;
		this.dba = dba;
		maxLines=0;
	}

	public void setMaxLines(int maxLines) {
		this.maxLines = maxLines;
	}

	public int getMaxLines() {
		return maxLines;
	}

	public String escapeString(String s) {
		return ToolBox.echapperStringPourHSql(s);
	}

	protected boolean stringIsEmpty(Object s) {
		if (s == null) {
			return true;
		}
		return ("" + s).trim().length() < 1;
	}

	public RequestBuilder newRequestBuilder() {
		RequestBuilder r = new RequestBuilder(table);
		if(maxLines>0)r.setLimit(maxLines);
		return r;
	}

	protected RequestBuilder newRequestBuilder(String other) {
		RequestBuilder r = new RequestBuilder(other);
		if(maxLines>0)r.setLimit(maxLines);
		return r;
	}

	protected DbAccess dba() {
		return dba;
	}

	public String getTable() {
		return table;
	}
	protected RequestBuilder maybeAddOffsetAndLimit(RequestBuilder rb, Integer offset, Integer limit) {
		if (rb == null) {
			return null;
		}
		if (offset != null) {
			rb.setOffset(offset);
		}
		if (limit != null) {
			rb.setLimit(limit);
		}
		return rb;
	}

	protected List<BaseEntityMap> toBaseEntityMaps(List<Map<String, Object>> lm) {
		if (lm == null) {
			return null;
		}
		List<BaseEntityMap> l = new ArrayList<>();
		for (int i = 0; i < lm.size(); i++) {
			l.add(new BaseEntityMap(lm.get(i)));
		}
		return l;
	}

	protected String getInsertValues(List<String> colonnes, EntityMap e) {
		List<String> vals = new ArrayList<>();
		boolean logDates = false;
		for (int i = 0; i < colonnes.size(); i++) {
			String v;
			String c = colonnes.get(i);
			if (logDates && c.startsWith("date")) {
				log.debug("colonne " + c + " : ");
				if (e.get(c) != null) {
					log.debug("  " + e.get(c).getClass().getName());
				}
			}
			if (e.get(c) == null) {
				v = "null";
			} else if (e.get(c) instanceof Number) {
				v = "" + e.get(c);
			} else if (e.get(c) instanceof Date) {
				v = escapeString(CommonDateFormats.pgDateFormatter().format(e.get(c)));
			} else if (e.get(c) instanceof String) {
				v = escapeString((String) e.get(c));
			} else {
				v = escapeString("" + e.get(c));
			}
			vals.add(v);
		}
		return ListsAndArrays.mergeList(vals, ",");
	}

	protected String getUpdateValues(List<String> colonnes, EntityMap e) {
		List<String> vals = new ArrayList<>();
		for (int i = 0; i < colonnes.size(); i++) {
			String v;
			String c = colonnes.get(i);
			if (e.get(c) == null) {
				v = "null";
			} else if (e.get(c) instanceof Long || e.get(c) instanceof Integer) {
				v = "" + e.get(c);
			} else if (e.get(c) instanceof Boolean) {
				v = ((Boolean)e.get(c))?"true":"false";
			} else if (e.get(c) instanceof Timestamp) {
				v = escapeString(CommonDateFormats.pgTimestampFormatter().format(e.get(c)));
			} else if (e.get(c) instanceof Date) {
				v = escapeString(CommonDateFormats.pgDateFormatter().format(e.get(c)));
			} else if (e.get(c) instanceof String) {
				v = escapeString((String) e.get(c));
			} else {
				v = escapeString("" + e.get(c));
			}
			vals.add(c + "=" + v);
		}
		return ListsAndArrays.mergeList(vals, ",");
	}

	public int count(){
		RequestBuilder r = new RequestBuilder(table).select("count(*)");
		Map m = dba().selectOne(r.request());
		return extractCountFromMap(m);
	}
	public int extractCountFromMap(Map<String, Object> m) {
		try {
			return ((Long) m.get("count")).intValue();
		} catch (Exception e) {
			log.error(e);
			return 0;
		}
	}
	public EntityMap toEntityMap(JSONObject o){
		if(o==null)return null;
		EntityMap e=new EntityMap(new ArrayList<>(o.keySet()));
		for(String col:e.cols()){
			e.put(col, o.opt(col));
		}
		return e;
	}
	public EntityMap toEntityMap(JSONObject o, List<String>cols){
		if(o==null)return null;
		EntityMap e=new EntityMap(cols);
		for(String col:e.cols()){
			e.put(col, o.opt(col));
		}
		return e;
	}
	public EntityMapWithIntId toEntityMapWithIntId(JSONObject o, String pkCol){
		if(o==null)return null;
		List<String> cols = new ArrayList<>(o.keySet());
		cols.remove(pkCol);
		cols.add(0, pkCol);
		EntityMapWithIntId e=new EntityMapWithIntId(cols,cols);
		for(String col:e.cols()){
			e.put(col, o.opt(col));
		}
		return e;
	}
	public EntityMapWithStringId toEntityMapWithStringId(JSONObject o, String pkCol){
		if(o==null)return null;
		List<String> cols = new ArrayList<>(o.keySet());
		cols.remove(pkCol);
		cols.add(0, pkCol);
		EntityMapWithStringId e=new EntityMapWithStringId(cols,cols);
		for(String col:e.cols()){
			e.put(col, o.opt(col));
		}
		return e;
	}

	public String objectToWildCardedString(Object o){
		if(o==null)return "%";
		if((""+o).trim().isEmpty())return "%";
		String s = "%" + o.toString().trim().replaceAll(" +", "%") + "%";
		if(s.equals("%%%"))s="%";
		if(s.equals("%%"))s="%";
		return s;
	}

	public int stringToInt(String s, int def) {
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			return def;
		}
	}

	public double stringToDouble(String s, double def) {
		try{
			return Double.parseDouble(s);
		}catch(Exception e){
			return def;
		}
	}

	public boolean jsonHasNonEmptyValue(JSONObject o, String key) {
		return o!=null && key!=null
			&& o.has(key) && !ToolBox.isEmpty(o.optString(key));
	}
	public String objectToSimplifiedString(Object o){
		if(o==null)return "";
		if((""+o).trim().isEmpty())return "";
		String s = ToolBox.simplifierString(o.toString()).replaceAll(" +", " ");
		return s.trim();
	}
	public String objectToWildCardedSimplifiedString(Object o){
		if(o==null)return "%";
		if((""+o).trim().isEmpty())return "%";
		String s = "%" + ToolBox.simplifierString(o.toString()).replaceAll(" +", "%") + "%";
		if(s.equals("%%%"))s="%";
		if(s.equals("%%"))s="%";
		return s;
	}
	/**
	 * 
	 * @param col the column
	 * @param val the value (must be escaped if not a number nor null ! )
	 * @param where
	 * @return true if updated
	 */
	public boolean update(String col, String val, String where){
		String req="update "+getTable()+" set "+col+"="+val;
		if(where!=null)req+=" where "+where;
		return dba().executeUpdate(req)>=0;
	}

	public boolean delete(String where){
		String req="delete from "+getTable()+" where "+where;
		return dba().executeUpdate(req)>=0;
	}
	public int insert(EntityMapWithIntId e) {
		if (e.getId() < 0) {
			int id = dba().getMax(getTable(), e.getIdKey());
			if (id < 0) {
				id = 0;
			}
			id++;
			e.setId(id);
		}
		List<String>cols=e.cols();
		String req = "insert into " + getTable() + "(" + ListsAndArrays.mergeList(cols, ",") + ")values(" + getInsertValues(cols, e) + ")";
		return dba().executeUpdate(req);
	}
	public int update(EntityMapWithIntId e) {
		if (e.getId() < 0) {
			int id = dba().getMax(getTable(), e.getIdKey());
			if (id < 0) {
				id = 0;
			}
			id++;
			e.setId(id);
		}
		List<String>cols=e.colsNoId();
		String req = "update " + getTable() + " set " + getUpdateValues(cols, e) + " where "+e.getIdKey()+"="+e.getId();
		return dba().executeUpdate(req);
	}
	public int update(EntityMap e, String where) {
		List<String>cols=e.cols();
		String req = "update " + getTable() + " set " + getUpdateValues(cols, e) + " where "+where;
		return dba().executeUpdate(req);
	}
	public int update(EntityMapWithStringId e) {
		if (e.getId() == null)return -1;
		List<String>cols=e.colsNoId();
		String req = "update " + getTable() + " set " + getUpdateValues(cols, e) + " where "+e.getIdKey()+"="+escapeString(e.getId());
		return dba().executeUpdate(req);
	}
	public int insert(EntityMap e) {
		List<String>cols=e.cols();
		String req = "insert into " + getTable() + "(" + ListsAndArrays.mergeList(cols, ",") + ")values(" + getInsertValues(cols, e) + ")";
		return dba().executeUpdate(req);
	}
}
