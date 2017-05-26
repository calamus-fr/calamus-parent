/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.db.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author haerwynn
 */
public class DbPool extends DbAccess {

	private static final Log log = LogFactory.getLog(DbPool.class);
	private static final String CNX_PREFIX = "cnx";
	private int nbMax;
	private Map<String, Connection> cnxMap;
	private Map<String, Statement> stMap;
	private List<String> busyCnxKeys;
	private List<String> transactionCnxKeys;
	//private String currentUsableCnx;
	private List<String> cnxKeys;

	public DbPool(Connection cnx, Map<String, String> map, boolean permanent) {
		super(cnx, map, permanent);
		nbMax = -1;
		cnxMap = new HashMap<>();
		stMap = new HashMap<>();
		transactionCnxKeys = new ArrayList<>();
		cnxKeys = new ArrayList<>();
		busyCnxKeys = new ArrayList<>();
		addCnx(cnx);
	}

	public DbPool(Connection cnx, Map<String, String> map) {
		this(cnx, map, true);
	}

	public void setNbMax(int nbMax) {
		this.nbMax = nbMax;
	}

	private String addCnx(Connection cnx) {
		int i = cnxKeys.size();
		while (cnxKeys.contains(CNX_PREFIX + i)) {
			i++;
		}
		String k = CNX_PREFIX + i;
		addCnx(cnx, k);
		return k;
	}

	private String addCnx(Connection cnx, String key) {
		if (nbMax > 0 && cnxKeys.size() < nbMax) {
			/*if (cnxKeys.isEmpty()) {
				currentUsableCnx = key;
			}*/
			cnxKeys.add(key);
			cnxMap.put(key, cnx);
		}
		return key;
	}

	public Connection createCnx() {
		try {
			return DriverManager.getConnection(dbUrl);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Connection getCnx(String key) {
		if (cnxMap.containsKey(key)) {
			boolean cl;
			try {
				cl = cnxMap.get(key).isClosed();
			} catch (SQLException ex) {
				ex.printStackTrace();
				cl = true;
			}
			if (cl) {
				addCnx(createCnx(), key);
			}
			return cnxMap.get(key);
		} else {
			addCnx(createCnx(), key);
			return cnxMap.get(key);
		}
	}

	public String findAndBindUsableCnx() {
		try {
			String usable = null;
			for (String key : cnxKeys) {
				if (!busyCnxKeys.contains(key)) {
					cnx = getCnx(key);
					usable = key;
				}
			}
			if (usable == null) {
				usable = addCnx(createCnx());
			}
			cnx = getCnx(usable);
			if (getCnx(usable) == null || getCnx(usable).isClosed()) {
				usable = addCnx(createCnx(), usable);
			}

			return usable;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Statement openStatement() {
		String usable = findAndBindUsableCnx();
		Statement st = getStatement(usable);
		return st;
	}

	private void log(String s) {
		log.debug(this + " " + s);
	}

	public String openInTransactionMode() {
		try {
			Connection cnx = null;
			String usable = null;
			for (String key : cnxKeys) {
				if (!busyCnxKeys.contains(key)) {
					cnx = getCnx(key);
					usable = key;
				}
			}
			if (usable == null) {
				usable = addCnx(createCnx());
			}
			if (getCnx(usable) == null || getCnx(usable).isClosed()) {
				usable = addCnx(createCnx(), usable);
			}
			transactionCnxKeys.add(usable);
			getStatement(usable);
			setBusy(usable);
			return usable;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> selectMany(String sql) {
		ResultSet rs = null;
		long time1 = System.currentTimeMillis();
		Statement st = null;
		String usable = null;
		try {
			log(sql);
			usable = findAndBindUsableCnx();
			st = getStatement(usable);
			setBusy(usable);
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		}
		List<Map<String, Object>> l = Parser.getMaps(rs);
		if (st != null && usable != null) {
			busyCnxKeys.remove(usable);
		}
		long time2 = System.currentTimeMillis();
		log("  time elapsed=" + (time2 - time1) + " ms");
		return l;
	}

	@Override
	public int executeUpdate(String sql) {
		return super.executeUpdate(sql);
	}

	public int executeInTransaction(String sql, String key) {
		int n = -1;
		try {
			log(sql);
			n = getStatement(key).executeUpdate(sql);
		} catch (SQLException e) {
			setLastExceptionMessage(e);
			e.printStackTrace();
		} finally {
			//closeStatement();
		}
		return n;
	}

	public void commitTransaction(String key) {
		try {
			Statement st = getStatement(key);
			if (st == null || st.isClosed()) {
				st = createStatement(key);
			}
			st.execute("commit");
			transactionCnxKeys.remove(key);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		//closeStatement();
	}

	public void closeTransactionMode(String key) {
		transactionCnxKeys.remove(key);
		try {
			getCnx(key).close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private Statement getStatement(String key) {
		try {
			Statement st = stMap.get(key);
			if (st == null || st.isClosed()) {
				log("creating statement");
				st = createStatement(key);
			} else {
				log("returning existing statement " + st.getQueryTimeout());
			}
			return st;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private Statement createStatement(String key) {
		try {
			Statement st = getCnx(key).createStatement();
			return st;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void setBusy(String key) {
		if (!busyCnxKeys.contains(key)) {
			busyCnxKeys.add(key);
		}
	}
}
