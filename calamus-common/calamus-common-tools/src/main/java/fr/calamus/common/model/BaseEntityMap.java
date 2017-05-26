/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import java.io.Serializable;
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

}
