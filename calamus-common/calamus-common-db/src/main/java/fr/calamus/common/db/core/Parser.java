package fr.calamus.common.db.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class Parser {
	private static final Log log = LogFactory.getLog(Parser.class);

	public static List<Map<String, Object>> getMaps(ResultSet rs){
		List<Map<String, Object>> list = null;
		int nl=0;
		try {
			list = new ArrayList<>();
			ResultSetMetaData md = rs.getMetaData();
			while (rs.next()) {
				HashMap<String, Object> h = new HashMap<>();
				for (int i = 1; i <= md.getColumnCount(); i++) {
					String cl = md.getColumnLabel(i);
					if (cl != null)
						cl = cl.toLowerCase();
					h.put(cl, rs.getObject(i));
				}
				list.add(h);
				nl++;
			}
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError line "+nl, e);
		} catch (SQLException e) {
			log.warn("SQLException line "+nl, e);
		}
		return list;
	}
	public static JSONArray getJsonArray(ResultSet rs){
		JSONArray list = new JSONArray();
		int nl=0;
		try {
			//list = new ArrayList<Map<String, Object>>();
			ResultSetMetaData md = rs.getMetaData();
			while (rs.next()) {
				JSONObject h=new JSONObject();
				for (int i = 1; i <= md.getColumnCount(); i++) {
					String cl = md.getColumnLabel(i);
					if (cl != null)
						cl = cl.toLowerCase();
					h.put(cl, rs.getObject(i));
				}
				list.put(h);
				nl++;
			}
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError line "+nl, e);
		} catch (SQLException e) {
			log.warn("SQLException line "+nl, e);
		}
		return list;
	}

	public static List<String> getTableNames(ResultSet rs){
		List<String> l = new ArrayList<>();
		try {
			while (rs.next()) {
				l.add(rs.getString("TABLE_NAME"));
			}
		} catch (SQLException e) {
			log.warn("getTableNames : SQLException", e);
		}
		return l;
	}

	public static List<String> getColumnNames(ResultSet rs){
		List<String> l = new ArrayList<>();
		try {
			while (rs.next()) {
				// String tn = rs.getString("TABLE_NAME");
				// if (tn != null && table != null && tn.equalsIgnoreCase(table))
				l.add(rs.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
			log.warn("getColumnNames : SQLException", e);
		}
		return l;
	}

}
