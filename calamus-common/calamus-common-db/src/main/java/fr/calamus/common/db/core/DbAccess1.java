package fr.calamus.common.db.core;

import java.io.Serializable;
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

public class DbAccess1 implements Serializable {

	private Statement st;
	private Connection cnx;
	private boolean permanentCnx;
	private boolean transactionMode;
	private static int counter = 0;
	private String lastExceptionMessage;

	public DbAccess1(Connection cnx, Map<String, String> map) {
		this(cnx, map, true);
	}

	public DbAccess1(Connection cnx, Map<String, String> map, boolean permanent) {
		super();
		this.cnx = cnx;
		lastExceptionMessage = null;
		permanentCnx = permanent;
		transactionMode = false;
		if (map != null) {
			checkTables(map);
		}
		if (counter < 2) {
			System.out.println("Tables : " + listTables());
		}
		counter++;
	}

	public List<String> listTables() {
		openStatement();
		ResultSet rs = null;
		try {
			String arg = "%";
			DatabaseMetaData md = cnx.getMetaData();
			//ToolBox.log("listTables:" + md.getDatabaseProductName());
			if (md.getDatabaseProductName().toLowerCase().contains("postgres")) {
				arg = null;
			} else if (md.getDatabaseProductName().toLowerCase().contains("hsql")) {
				arg = "PUBLIC";
			}
			rs = md.getTables(arg, "%", "%", new String[]{"TABLE"});
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
		List<String> tablesExistantes = new ArrayList<>();
		try {
			while (rs.next()) {
				tablesExistantes.add(rs.getString("TABLE_NAME"));
			}
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
		closeStatement();
		return tablesExistantes;
	}

	public String getLastExceptionMessage() {
		return lastExceptionMessage;
	}

	private void checkTables(Map<String, String> map) {
		List<String> tablesExistantes = listTables();
		openStatement();
		for (String tn : map.keySet()) {
			if (!tablesExistantes.contains(tn.toUpperCase())) {
				try {
					//ToolBox.log("creation de la table " + tn);
					st.execute(map.get(tn));
				} catch (SQLException e) {
					setLastExceptionMessage(e);
					e.printStackTrace();
				}
			}
		}
		closeStatement();
	}

	public void openTransaction() {
		openStatement();
		try {
			st.execute("begin transaction");
			transactionMode = true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	public void commitTransaction() {
		try {
			if(st==null||st.isClosed())openStatement();
			st.execute("commit");
			transactionMode = false;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		closeStatement();
	}
	public void openInTransactionMode() {
		transactionMode = true;
		openCnx();
	}

	public void closeTransactionMode() {
		transactionMode = false;
		try {
			cnx.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public Statement openStatement() {
		try {
			if ((!permanentCnx && !transactionMode) || cnx.isClosed()) {
				openCnx();
			}
			st = cnx.createStatement();
			return st;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void closeStatement() {
		try {
			st.close();
			if (!permanentCnx && !transactionMode) {
				cnx.close();
			}
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
	}

	public List<Map<String, Object>> selectMany(String sql) {
		return selectMany(sql, false);
	}
	public List<Map<String, Object>> selectMany(String sql,boolean inTransaction) {
		ResultSet rs = null;
		try {
			if(!inTransaction||st==null||st.isClosed())st=openStatement();
			//ToolBox.log(sql);
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
		List<Map<String, Object>> l = Parser.getMaps(rs);
		if(!inTransaction)closeStatement();
		if (l != null && l.size() > 0) {
			//ToolBox.log("columns=" + l.get(0).keySet());
		}
		return l;
	}

	public Map<String, Object> selectOne(String sql) {
		return selectOne(sql,false);
	}
	public Map<String, Object> selectOne(String sql,boolean inTransaction) {
		List<Map<String, Object>> l = selectMany(sql,inTransaction);
		if (l == null || l.size() < 1) {
			return null;
		}
		return l.get(0);
	}

	public static List<String> getStringCol(List<Map<String, Object>> list, String col) {
		List<String> l = new ArrayList<String>();
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
		try {
			//ToolBox.log(sql);
			n = openStatement().executeUpdate(sql);
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		} finally {
			closeStatement();
		}
		return n;
	}
	public int executeInTransaction(String sql) {
		int n = -1;
		try {
			//ToolBox.log(sql);
			if(st==null||st.isClosed())openStatement();
			n = st.executeUpdate(sql);
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		} finally {
			//closeStatement();
		}
		return n;
	}

	public int getMax(String table, String col) {
		return getMax(table, col, false);
	}
	public int getMax(String table, String col,boolean inTransaction) {
		int n = -1;
		Map<String, Object> map = selectOne("select max(" + col + ") as maxid from " + table,inTransaction);
		if (map != null) {
			//ToolBox.log("map=" + map);
			Object om = map.get("maxid");
			if (om != null && om instanceof Integer) {
				n = ((Integer) om).intValue();
			}
		}
		//ToolBox.log("max " + table + "." + col + "=" + n);
		return n;
	}

	public List<String> listColumns(String table) {
		openStatement();
		ResultSet rs = null;
		try {
			String arg = "%";
			DatabaseMetaData md = cnx.getMetaData();
			//ToolBox.log("listColumns:" + md.getDatabaseProductName());
			if (md.getDatabaseProductName().toLowerCase().contains("postgres")) {
				arg = null;
			} else if (md.getDatabaseProductName().toLowerCase().contains("hsql")) {
				arg = "PUBLIC";
			}
			// rs = md.getColumns(arg, "%", "%", new String[]{ "TABLE" });
			rs = md.getColumns(arg, "%", table.toUpperCase(), "%");
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
		List<String> lc = Parser.getColumnNames(rs);
		closeStatement();
		return lc;
	}

	public List<String> getPks(String table) {
		openStatement();
		ResultSet rs = null;
		try {
			String arg = "%";
			DatabaseMetaData md = cnx.getMetaData();
			//ToolBox.log("listColumns:" + md.getDatabaseProductName());
			if (md.getDatabaseProductName().toLowerCase().contains("postgres")) {
				arg = null;
			} else if (md.getDatabaseProductName().toLowerCase().contains("hsql")) {
				arg = "PUBLIC";
			}
			// rs = md.getColumns(arg, "%", "%", new String[]{ "TABLE" });
			rs = md.getPrimaryKeys(arg, "%", table.toUpperCase());
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
		List<String> l = Parser.getColumnNames(rs);
		closeStatement();
		return l;
	}

	private void openCnx() {
		try {
			cnx = DriverManager.getConnection(cnx.getMetaData().getURL());
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public List<String> listColumnTypes(String table) {
		openStatement();
		List<String> lc = new Vector<>();
		ResultSet rs = null;
		try {
			String sql = "select * from " + table + " limit 1";
			rs = st.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			for (int i = 0; i < md.getColumnCount(); i++) {
				String sqlt = md.getColumnTypeName(i + 1);
				lc.add(md.getColumnName(i + 1).toLowerCase() + ":" + sqlt);
			}
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
		closeStatement();
		return lc;
	}

	private void setLastExceptionMessage(SQLException e) {
		lastExceptionMessage = (e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getMessage()) + " state=" + e.getSQLState() + " error " + e.getErrorCode();
	}
}
