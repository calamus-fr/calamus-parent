package fr.calamus.common.db.core;

import java.io.Serializable;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class DbAccess<C extends Connection> implements Serializable, Cloneable {

	//private Statement st;
	protected C cnx;
	private boolean permanentCnx;
	private boolean transactionMode;
	private static int counter = 0;
	protected String lastExceptionMessage;
	protected String dbUrl;
	private static final Log log = LogFactory.getLog(DbAccess.class);
	private String user;
	private String pass;
	private int timeout;
	private long lastUsedTime;
	private boolean online;
	public static int logLevel = 0;

	public DbAccess(C cnx, Map<String, String> map) {
		this(cnx, map, true);
	}

	public DbAccess(DbAccess<C> base, Map<String, String> map) {
		this(base.dbUrl, base.user, base.pass, map, true);
	}

	public DbAccess(C cnx, Map<String, String> map, boolean permanent) {
		super();
		timeout = 600;//s
		online = true;
		this.cnx = cnx;
		try {
			dbUrl = cnx.getMetaData().getURL();
			user = cnx.getMetaData().getUserName();
		} catch (SQLException ex) {
			logException(ex);
		}
		lastExceptionMessage = null;
		permanentCnx = permanent;
		transactionMode = false;
		if (map != null) {
			checkTables(map);
		}
		if (counter < 2) {
			//System.out.println("Tables : " + listTables());
		}
		counter++;
		setLastUsedTime();
	}

	public DbAccess(String url, String user, String pass, Map<String, String> map) {
		this(url, user, pass, map, true);
	}

	public DbAccess(String url, String user, String pass, Map<String, String> map, boolean permanent) {
		super();
		this.dbUrl = url;
		this.user = user;
		this.pass = pass;
		online = true;
		//openCnx();
		lastExceptionMessage = null;
		permanentCnx = permanent;
		transactionMode = false;
		if (map != null) {
			checkTables(map);
		}
		if (counter < 2) {
			//System.out.println("Tables : " + listTables());
		}
		counter++;
		setLastUsedTime();
	}

	public C getCnx() {
		if (cnx == null) {
			reconnect();
		}
		return cnx;
	}

	public List<String> listTables() {
		//openStatement();
		ResultSet rs = null;
		try {
			String arg = "%";
			DatabaseMetaData md = getCnx().getMetaData();
			log("listTables:" + md.getDatabaseProductName(),1);
			if (md.getDatabaseProductName().toLowerCase().contains("postgres")) {
				arg = null;
			} else if (md.getDatabaseProductName().toLowerCase().contains("hsql")) {
				arg = "PUBLIC";
			}
			rs = md.getTables(arg, "%", "%", new String[]{"TABLE"});
		} catch (SQLException e) {
			logException(e);
		}
		List<String> tablesExistantes = new ArrayList<>();
		try {
			while (rs.next()) {
				tablesExistantes.add(rs.getString("TABLE_NAME"));
			}
		} catch (SQLException e) {
			logException(e);
		}
		//closeStatement();
		return tablesExistantes;
	}

	public String getLastExceptionMessage() {
		return lastExceptionMessage;
	}

	private void checkTables(Map<String, String> map) {
		List<String> tablesExistantes = listTables();
		if (logLevel > 0) {
			log.debug("checkTables : tablesExistantes = " + tablesExistantes);
		}
		if (logLevel > 0) {
			log.debug(" map.keyset : " + map.keySet());
		}
		Statement st = openStatement();
		for (String tn : map.keySet()) {
			log(" checking " + tn,1);
			if (!tablesExistantes.contains(tn.toUpperCase()) && !tablesExistantes.contains(tn.toLowerCase())) {
				log("  creation de la table " + tn);
				try {
					st.execute(map.get(tn));
				} catch (SQLException e) {
					logException(e);
				}
			} else
				log("  ok",1);
		}
		closeStatement(st);
	}

	public Statement openTransaction() {
		Statement st = openStatement();
		try {
			log("openTransaction",1);
			st.execute("begin transaction");
			transactionMode = true;
			return st;
		} catch (SQLException ex) {
			logException(ex);
		}
		return null;
	}

	public void commitTransaction(Statement st) {
		try {
			log("commitTransaction",1);
			st.execute("commit");
			transactionMode = false;
		} catch (SQLException ex) {
			logException(ex);
			try {
				st.execute("rollback");
			} catch (SQLException ex1) {
				logException(ex1);
			}
		}
		closeStatement(st);
		setLastUsedTime();
	}

	public String openInTransactionMode() {
		transactionMode = true;
		openCnx();
		return null;
	}

	public void close() {
		log("closing cnx",0);
		try {
			getCnx().close();
		} catch (SQLException ex) {
			logException(ex);
		}
	}

	public void closeTransactionMode(String key) {
		transactionMode = false;
		close();
	}

	public boolean isClosed() {
		try {
			return cnx == null || cnx.isClosed();// || cnx.isValid(0);
		} catch (SQLException ex) {
			log.warn(ex);
			return true;
		}
	}

	public Statement openStatement() {
		try {
			if ((!permanentCnx && !transactionMode) || getCnx().isClosed()) {
				openCnx();
			}
			/*if (st == null || st.isClosed()) {
				log("creating statement");
				st = getCnx().createStatement();
			} else {
				log("returning existing statement " + st.getQueryTimeout() + " ");
			}*/
			return getCnx().createStatement();//st;
		} catch (SQLException ex) {
			logException(ex);
			return null;
		}
	}

	public void closeStatement(Statement st) {
		try {
			log("closing statement",2);
			st.close();
			if (!permanentCnx && !transactionMode) {
				getCnx().close();
			}
		} catch (SQLException e) {
			logException(e);
		}
		setLastUsedTime();
	}

	public List<Map<String, Object>> selectMany(String sql) {
		return selectMany(null, sql);
	}

	public List<Map<String, Object>> selectMany(Statement s, String sql) {
		ResultSet rs = null;
		long time1 = System.currentTimeMillis();
		log(sql,0);
		if (isClosed()) {
			reconnect();
		}
		boolean dontClose = true;
		if (s == null) {
			dontClose = false;
			s = openStatement();
		}
		try {
			rs = s.executeQuery(sql);
		} catch (SQLException e) {
			if (e.getCause() != null && e.getCause() instanceof SocketException) {
				reconnect();
				try {
					rs = s.executeQuery(sql);
				} catch (SQLException ex) {
					logException(ex);
				}
			} else {
				logException(e);
			}

		}
		long time2 = System.currentTimeMillis();
		log("  request time=" + (time2 - time1) + " ms",2);
		List<Map<String, Object>> l = Parser.getMaps(rs);
		if (!dontClose) {
			closeStatement(s);
		}
		//if(!transactionMode)closeStatement();
		/*if (l != null && l.size() > 0) {
			log("columns=" + l.get(0).keySet());
		}*/
		time2 = System.currentTimeMillis();
		log(" -> "+l.size()+" lines",0);
		log("  total time elapsed=" + (time2 - time1) + " ms (" + l.size() + " lines)",1);
		setLastUsedTime();
		return l;
	}

	public JSONArray selectManyJson(String sql) {
		return selectManyJson(null, sql);
	}

	public JSONArray selectManyJson(Statement s, String sql) {
		ResultSet rs = null;
		long time1 = System.currentTimeMillis();
		log(sql,0);
		if (isClosed()) {
			reconnect();
		}
		boolean dontClose = true;
		if (s == null) {
			dontClose = false;
			s = openStatement();
		}
		try {
			rs = s.executeQuery(sql);
		} catch (SQLException e) {
			logException(e);
		}
		JSONArray l = Parser.getJsonArray(rs);
		if (!dontClose) {
			closeStatement(s);
		}
		//if(!transactionMode)closeStatement();
		/*if (l != null && l.size() > 0) {
			log("columns=" + l.get(0).keySet());
		}*/
		long time2 = System.currentTimeMillis();
		log("  time elapsed=" + (time2 - time1) + " ms",0);
		setLastUsedTime();
		return l;
	}

	public Map<String, Object> selectOne(String sql) {
		return selectOne(null, sql);
	}

	public Map<String, Object> selectOne(Statement s, String sql) {
		List<Map<String, Object>> l = selectMany(s, sql);
		if (l == null || l.size() < 1) {
			return null;
		}
		return l.get(0);
	}

	public JSONObject selectOneJson(String sql) {
		return selectOneJson(null, sql);
	}

	public JSONObject selectOneJson(Statement s, String sql) {
		List<Map<String, Object>> l = selectMany(s, sql);
		if (l == null || l.size() < 1) {
			return null;
		}
		return new JSONObject(l.get(0));
	}

	public static List<String> getStringCol(List<Map<String, Object>> list, String col) {
		List<String> l = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			l.add("" + list.get(i).get(col));
		}
		return l;
	}

	public List<String> selectStringCol(String sql, String col) {
		List<Map<String, Object>> lh = selectMany(sql);
		if (lh == null) {
			return null;
		}
		return getStringCol(lh, col);
	}

	public int executeUpdate(String sql) {
		int n = -1;
		Statement st = openStatement();
		try {
			log(sql,0);
			n = st.executeUpdate(sql);
		} catch (SQLException e) {
			logException(e);
		} finally {
			closeStatement(st);
			setLastUsedTime();
			log(" -> " + n,0);
		}
		return n;
	}

	public int executeInTransaction(Statement st, String sql) {
		int n = -1;
		try {
			log(sql,0);
			n = st.executeUpdate(sql);
		} catch (SQLException e) {
			logException(e);
		} finally {
			setLastUsedTime();
			log(" -> " + n,0);
		}
		return n;
	}

	public int getNextId(String table) {
		int lastId = getMax(table, "id");
		if (lastId <= 0) {
			return 1;
		}
		return lastId + 1;
	}

	public int getNextId(Statement st, String table) {
		int lastId = getMax(st, table, "id");
		if (lastId <= 0) {
			return 1;
		}
		return lastId + 1;
	}

	public int getMax(String table, String col) {
		return getMax(null, table, col);
	}

	public int getMax(Statement st, String table, String col) {
		int n = -1;
		Map<String, Object> map = selectOne(st, "select max(" + col + ") as maxid from " + table);
		if (map != null) {
			log("map=" + map,1);
			Object om = map.get("maxid");
			if (om != null && om instanceof Number) {
				n = ((Number) om).intValue();
			}
		}
		log("max " + table + "." + col + "=" + n,1);
		return n;
	}

	public List<String> listColumns(String table) {
		//Statement st = openStatement();
		ResultSet rs = null;
		try {
			String arg = "%";
			DatabaseMetaData md = getCnx().getMetaData();
			log("listColumns:" + md.getDatabaseProductName(),1);
			if (md.getDatabaseProductName().toLowerCase().contains("postgres")) {
				arg = null;
			} else if (md.getDatabaseProductName().toLowerCase().contains("hsql")) {
				arg = "PUBLIC";
			}
			// rs = md.getColumns(arg, "%", "%", new String[]{ "TABLE" });
			rs = md.getColumns(arg, "%", table.toUpperCase(), "%");
		} catch (SQLException e) {
			logException(e);
		}
		List<String> lc = Parser.getColumnNames(rs);
		//closeStatement(st);
		return lc;
	}

	public List<String> getPks(String table) {
		//st=openStatement();
		ResultSet rs = null;
		try {
			String arg = "%";
			DatabaseMetaData md = getCnx().getMetaData();
			log("getPks:" + md.getDatabaseProductName(),1);
			if (md.getDatabaseProductName().toLowerCase().contains("postgres")) {
				arg = null;
			} else if (md.getDatabaseProductName().toLowerCase().contains("hsql")) {
				arg = "PUBLIC";
			}
			// rs = md.getColumns(arg, "%", "%", new String[]{ "TABLE" });
			rs = md.getPrimaryKeys(arg, "%", table.toUpperCase());
		} catch (SQLException e) {
			logException(e);
		}
		List<String> l = Parser.getColumnNames(rs);
		//closeStatement();
		return l;
	}

	protected void openCnx() {
		cnx = newConnexion();
	}

	public List<String> listColumnTypes(String table) {
		List<String> lc = new Vector<>();
		Statement st = openStatement();
		ResultSet rs = null;
		try {
			String sql = "select * from " + table + " limit 1";
			rs = openStatement().executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			for (int i = 0; i < md.getColumnCount(); i++) {
				String sqlt = md.getColumnTypeName(i + 1);
				lc.add(md.getColumnName(i + 1).toLowerCase() + ":" + sqlt);
			}
		} catch (SQLException e) {
			logException(e);
		}
		closeStatement(st);
		return lc;
	}

	protected void setLastExceptionMessage(SQLException e) {
		lastExceptionMessage = (e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getMessage()) + " state=" + e.getSQLState() + " error " + e.getErrorCode();
	}

	private void log(String s) {
		log(s, -1);
	}

	private void log(String s, int n) {
		if (logLevel > n) {
			if (s != null && s.length() > 500) {
				s = s.substring(0, 497) + "...";
			}
			log.debug(getHashId() + " - " + s);
		}
	}

	/*public Statement getStatement() {
		return st;
	}*/
	public String getHashId() {
		return Integer.toHexString(hashCode());
	}

	public C newConnexion() {
		C c = null;
		try {
			c = (C) DriverManager.getConnection(dbUrl, user, pass);
			log("newConnexion ok 1",1);
		} catch (SQLException ex) {
			log("newConnexion SQLException 1",1);
			logException(ex);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex1) {
				//log.debug("newConnexion InterruptedException");
				log.warn(ex1);
			}
			try {
				c = (C) DriverManager.getConnection(dbUrl, user, pass);
				log("newConnexion ok 2",1);
			} catch (SQLException ex1) {
				//log.debug("newConnexion SQLException 2");
				logException(ex1);
				Throwable cause = ex1.getCause();
				if (cause != null && cause instanceof SocketException) {
					online = false;
				}
				return null;
			}
		}
		online = true;
		return c;
	}

	private void logException(SQLException e) {
		setLastExceptionMessage(e);
		log.warn(getHashId() + " - " + lastExceptionMessage, e);
	}

	public long lastUsedTime() {
		return lastUsedTime;
	}

	private void setLastUsedTime() {
		lastUsedTime = System.currentTimeMillis();
	}

	public void reconnect() {
		if (!isClosed()) {
			close();
		}
		openCnx();
	}

	DbAccess newAccess() {
		return new DbAccess(dbUrl, user, pass, null);
	}

}
