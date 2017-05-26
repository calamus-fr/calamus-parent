/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import java.util.List;
import java.util.Map;

/**
 *
 * @author haerwynn
 */
public class EntityMapWithIdAndNullableBoolean extends EntityMapWithId{

	private String booleanCol;
	public EntityMapWithIdAndNullableBoolean(String cols, String booleanCol) {
		super(cols);
		setBooleanColumn(booleanCol);
	}

	public EntityMapWithIdAndNullableBoolean(String cols, String labels, String booleanCol) {
		super(cols, labels);
		setBooleanColumn(booleanCol);
	}

	public EntityMapWithIdAndNullableBoolean(Map<? extends String, ? extends Object> m, String cols, String booleanCol) {
		super(m, cols);
		setBooleanColumn(booleanCol);
	}

	public EntityMapWithIdAndNullableBoolean(Map<? extends String, ? extends Object> m, String cols, String labels, String booleanCol) {
		super(m, cols, labels);
		setBooleanColumn(booleanCol);
	}

	public EntityMapWithIdAndNullableBoolean(List<String> cols, String booleanCol) {
		super(cols);
		setBooleanColumn(booleanCol);
	}

	public EntityMapWithIdAndNullableBoolean(List<String> cols, List<String> labels, String booleanCol) {
		super(cols, labels);
		setBooleanColumn(booleanCol);
	}

	public EntityMapWithIdAndNullableBoolean(Map<? extends String, ? extends Object> m, List<String> cols, String booleanCol) {
		super(m, cols);
		setBooleanColumn(booleanCol);
	}

	public EntityMapWithIdAndNullableBoolean(Map<? extends String, ? extends Object> m, List<String> cols, List<String> labels, String booleanCol) {
		super(m, cols, labels);
		setBooleanColumn(booleanCol);
	}

	private void setBooleanColumn(String booleanCol) {
		if(this.booleanCol==null && booleanCol!=null){
			this.booleanCol=booleanCol;
		}
	}

	public Boolean getBooleanValue() {
		Object o=get(booleanCol);
		if(o==null)return null;
		String v = ""+o;
		if(v.equals("1")||v.equalsIgnoreCase("true")||v.equalsIgnoreCase("oui"))return true;
		return false;
	}

	public void setBooleanValue(Boolean b) {
		put(booleanCol,b);
	}

}
