/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import fr.calamus.common.tools.CommonDateFormats;
import java.io.Serializable;
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
}
