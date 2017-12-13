/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author haerwynn
 */
public class CommonDateFormats {

	private static SimpleDateFormat pgDateFormatter;
	private static SimpleDateFormat pgTimestampFormatter;
	private static SimpleDateFormat frTimestampFormatter;
	private static SimpleDateFormat pgTimestampSimplerFormatter;
	private static SimpleDateFormat frTimestampSimplerFormatter;
	private static SimpleDateFormat frDateFormatter;
	public static SimpleDateFormat pgDateFormatter() {
		if (pgDateFormatter == null) {
			pgDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		}
		return pgDateFormatter;
	}
	public static SimpleDateFormat pgTimestampFormatter() {
		if (pgTimestampFormatter == null) {
			pgTimestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return pgTimestampFormatter;
	}
	public static SimpleDateFormat pgTimestampSimplerFormatter() {
		if (pgTimestampSimplerFormatter == null) {
			pgTimestampSimplerFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		return pgTimestampSimplerFormatter;
	}
	public static SimpleDateFormat frDateFormatter() {
		if (frDateFormatter == null) {
			frDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		}
		return frDateFormatter;
	}
	public static SimpleDateFormat frTimestampFormatter() {
		if (frTimestampFormatter == null) {
			frTimestampFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
		}
		return frTimestampFormatter;
	}
	public static SimpleDateFormat frTimestampSimplerFormatter() {
		if (frTimestampSimplerFormatter == null) {
			frTimestampSimplerFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
		}
		return frTimestampSimplerFormatter;
	}
	public static Date pgParseDateOrNull(String s){
		if(s==null)return null;
		try{
			return pgDateFormatter().parse(s);
		}catch(ParseException e){
			return null;
		}
	}
	public static Date frParseDateOrNull(String s){
		if(s==null)return null;
		try{
			return frDateFormatter().parse(s);
		}catch(ParseException e){
			return null;
		}
	}
}
