/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.calamus.common.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haerwynn
 */
public class PropertiesTools implements Serializable{

	private static final File propsFolder=initPropsFolder();
	private static Map<String, Properties> properties=new HashMap<String, Properties>();

	public static Properties loadPropertiesWithCreationIfNeeded(String name){
		String fileName=name.toLowerCase();
		if(!fileName.endsWith(".properties"))fileName+=".properties";
		Properties p=new Properties();
		File f=new File(propsFolder, fileName);
		System.out.println("loading properties: "+f.getAbsolutePath());
		if(!f.exists())try {
			f.createNewFile();
			System.out.println("  created");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		FileInputStream input=null;
		try {
			input = new FileInputStream(f);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		try {
			p.load(input);
			System.out.println("  loaded");
			properties.put(name, p);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return p;
	}

	private static File initPropsFolder() {
		File f=new File("properties");
		System.out.println("properties folder : "+f.getAbsolutePath());
		if(!f.exists()){
			f.mkdirs();
			System.out.println("  created");
		}else if(!f.isDirectory()){
			throw new UnsupportedOperationException(f.getAbsolutePath()+" exists but is not a directory!");
		}
		return f;
	}

	public static void saveProperties(String name, Properties props){
		if(!name.toLowerCase().endsWith(".properties"))name+=".properties";
		//Properties p=new Properties();
		File f=new File(propsFolder, name);
		System.out.println("saving properties: "+f.getAbsolutePath());
		if(!f.exists())try {
			f.createNewFile();
			System.out.println("  created");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			FileOutputStream out=new FileOutputStream(f);
			props.store(out, name);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static boolean propertiesExist(String name) {
		if(!name.toLowerCase().endsWith(".properties"))name+=".properties";
		File f=new File(propsFolder, name);
		return f.exists();
	}
}
