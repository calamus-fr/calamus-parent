package fr.calamus.common.mail.core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MailDbConnectionManager {
private static Connection cnx=null;
	
	public static Connection getConnection(){
		if(cnx==null){
			try {
				try {
					File fdb=new File("db");
					if(!fdb.exists()){
						fdb.mkdirs();
					}else if(!fdb.isDirectory()){
						throw new IOException("'db' isn't a directory");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				cnx=DriverManager.getConnection("jdbc:hsqldb:file:db/mailDB;shutdown=true;","SA","");
			} catch (SQLException e) {
				if(e.getMessage().startsWith("Database lock acquisition failure")){
					File f=new File("db/mailDB.lck");
					if(f.exists()){
						f.delete();
						return getConnection();
					}else e.printStackTrace();
				}else e.printStackTrace();
			}
		}
		return cnx;
	}
}
