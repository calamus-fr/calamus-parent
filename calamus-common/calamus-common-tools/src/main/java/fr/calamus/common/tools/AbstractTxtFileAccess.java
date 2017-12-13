/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * Reads/write lines in a text file
 * @author haerwynn
 */
public abstract class AbstractTxtFileAccess {

	//private static final Log log = LogFactory.getLog(AbstractTxtFileAccess.class);
	protected static final String CHARSET = "ISO-8859-15";
	protected String charset;

	public AbstractTxtFileAccess(String charset) {
		if(charset!=null)this.charset=charset;
		else this.charset=CHARSET;

	}

	/**
	 *
	 * @return the file
	 */
	protected abstract File getFile();

	/**
	 * Prepares the file for writing; if the file exists, deletes it before.
	 * @return
	 * @throws IOException
	 */
	protected BufferedWriter prepareBufferedWriter() throws IOException {
		File f = getFile();
		if (f.exists()) {
			f.delete();
			//log.debug("prepareBufferedWriter : " + f.getAbsolutePath() + " deleted");
		}
		f.createNewFile();
		//log.debug("prepareBufferedWriter : " + f.getAbsolutePath() + " created");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), Charset.forName(charset)));
		return bw;
	}

	/**
	 * Saves data (obtained by getLines()) in the file.
	 */
	public void save() {
		try {
			BufferedWriter bw = prepareBufferedWriter();
			List<String>lines=getLines();
			for (int i = 0; i < lines.size(); i++) {
				bw.write(lines.get(i));
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException ex) {
			//log.error("", ex);
			ex.printStackTrace();
		}
	}

	/**
	 * Loads data from file then sends it in processData().
	 */
	public void load() {
		File f = getFile();
		if (!f.exists()) {
			return;
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), Charset.forName(charset)));
			String l;
			int i=0;
			List<String>lines=new ArrayList<>();
			while ((l = br.readLine()) != null) {
				i++;
				//log.debug("reading line "+i+" : "+l);
				if(!l.startsWith("#"))lines.add(l);
			}
			br.close();
			processData(lines);
		} catch (IOException ex) {
			//log.error("", ex);
			ex.printStackTrace();
		}

	}

	/**
	 *
	 * @return data to write in the file
	 */
	protected abstract List<String> getLines();

	/**
	 * Processes the data
	 * @param lines data read in the file
	 */
	protected abstract void processData(List<String> lines);

}
