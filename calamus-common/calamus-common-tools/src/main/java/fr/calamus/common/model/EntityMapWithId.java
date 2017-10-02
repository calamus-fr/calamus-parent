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
public class EntityMapWithId extends EntityMap{

	private String idKey;

	public EntityMapWithId(String cols) {
		super(cols);
		//if(getId()==null)setId(-1);
	}

	public EntityMapWithId(String cols, String labels) {
		super(cols, labels);
		//if(getId()==null)setId(-1);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, String cols) {
		super(m, cols);
		//if(getId()==null)setId(-1);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, String cols, String labels) {
		super(m, cols, labels);
		//if(getId()==null)setId(-1);
	}

	public EntityMapWithId(List<String> cols) {
		super(cols);
		//if(getId()==null)setId(-1);
	}

	public EntityMapWithId(List<String> cols, List<String> labels) {
		super(cols, labels);
		//if(getId()==null)setId(-1);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, List<String> cols) {
		super(m, cols);
		//if(getId()==null)setId(-1);
	}

	public EntityMapWithId(Map<? extends String, ? extends Object> m, List<String> cols, List<String> labels) {
		super(m, cols, labels);
		//if(getId()==null)setId(-1);
	}

	//@Override
	public int getId() {
		return getInt(idKey);
	}

	//@Override
	public void setId(Integer id) {
		put(idKey, id == null ? -1 : id);
	}


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
}
