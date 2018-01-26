/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import java.util.List;
import java.util.Map;

/**
 * First column is id
 * @author haerwynn
 */
public abstract class EntityMapWithId<T> extends EntityMap{

	private String idKey;

	public EntityMapWithId(String cols) {
		super(cols);
	}

	public EntityMapWithId(String cols, String labels) {
		super(cols, labels);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, String cols) {
		super(m, cols);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, String cols, String labels) {
		super(m, cols, labels);
	}

	public EntityMapWithId(List<String> cols) {
		super(cols);
	}

	public EntityMapWithId(List<String> cols, List<String> labels) {
		super(cols, labels);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, List<String> cols) {
		super(m, cols);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, List<String> cols, List<String> labels) {
		super(m, cols, labels);
	}

	public abstract T getId();

	public abstract void setId(T id);

	public List<String> colsNoId() {
		List<String> a = cols();
		a.remove(0);
		return a;
	}

	public List<String> labelsNoId() {
		List<String> a = labels();
		a.remove(0);
		return a;
	}

	@Override
	protected void initMeta(List<String> cols, List<String> labels) {
		super.initMeta(cols, labels);
		idKey=this.cols().get(0);
	}

	public String getIdKey(){
		return idKey;
	}
}
