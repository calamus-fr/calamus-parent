/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import fr.calamus.common.tools.CommonDateFormats;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author haerwynn
 */
public class BaseEntityMap extends HashMap<String,Object> implements Serializable{

	public BaseEntityMap() {
	}

	public BaseEntityMap(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	public String getString(String k) {
		return get(k)==null?null:""+get(k);
	}
	public Integer getInteger(String k) {
		if(get(k)==null)return null;
		try{
			return Integer.parseInt(""+get(k));
		}catch(NumberFormatException e){
			return null;
		}
	}
	public int getInt(String k) {
		if(get(k)==null)return -1;
		if(get(k)instanceof Integer)return (Integer)get(k);
		try{
			return Integer.parseInt(""+get(k));
		}catch(NumberFormatException e){
			return -1;
		}
	}
	public Date getDate(String k){
		return getDate(k, false);
	}
	public Date getDate(String k,boolean updateStringToDate){
		if(get(k)==null)return null;
		if(get(k)instanceof Date)return (Date)get(k);
		if(get(k)instanceof String){
			Date d = CommonDateFormats.frParseDateOrNull(""+get(k));
			if(d==null)d=CommonDateFormats.pgParseDateOrNull(""+get(k));
			if(d!=null&&updateStringToDate)put(k,d);
			return d;
		}
		return null;
	}
	public Date setDate(String key, Object value){
		if(value==null){
			put(key,null);
			return null;
		}
		if(value instanceof Date){
			put(key,(Date)value);
			return (Date)value;
		}
		if(value instanceof String){
			Date d = CommonDateFormats.frParseDateOrNull(""+value);
			if(d==null)d=CommonDateFormats.pgParseDateOrNull(""+value);
			put(key,d);
			return d;
		}
		if(value instanceof Number){
			long n=((Number)value).longValue();
			Date d=new Date(n);
			put(key,d);
			return d;
		}
		return null;
	}
	public boolean getBoolean(String k, boolean defaultValue){
		Boolean b=getBooleanOrNull(k);
		if(b==null)return defaultValue;
		return b;
	}
	public Boolean getBooleanOrNull(String k){
		if("true".equalsIgnoreCase(""+get(k))||"1".equals(""+get(k)))return true;
		if("false".equalsIgnoreCase(""+get(k))||"0".equals(""+get(k)))return false;
		return null;
	}

	protected void checkDate(String key) {
		Object od = get(key);
		if(od!=null && od instanceof String){
			String sd = (String)od;
			Date d=CommonDateFormats.frParseDateOrNull(sd);
			if(d==null)d=CommonDateFormats.pgParseDateOrNull(sd);
			put(key,d);
		}
	}
	protected void checkDateTime(String key) {
		Object od = get(key);
		if(od!=null && od instanceof String){
			String sd = (String)od;
			SimpleDateFormat fmt = CommonDateFormats.frTimestampFormatter();
			Date d=null;
			try {
				d=fmt.parse(sd);
			} catch (ParseException ex) {
				//log.warn(ex);
				fmt = CommonDateFormats.pgTimestampFormatter();
				try {
					d=fmt.parse(sd);
				} catch (ParseException ex1) {
					d=null;
				}
			}
			//if(d==null)d=CommonDateFormats.pgParseDateOrNull(sd);
			put(key,d);
		}
	}
	protected void checkInteger(String key) {
		Object od = get(key);
		if(od!=null && od instanceof String){
			Integer n=getInteger(key);
			put(key,n);
		}
	}
}
