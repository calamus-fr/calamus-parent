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
public class EntityMapWithIntId extends EntityMapWithId<Integer>{

	public EntityMapWithIntId(String cols) {
		super(cols);
	}

	public EntityMapWithIntId(String cols, String labels) {
		super(cols, labels);
	}

	public EntityMapWithIntId(Map<? extends String, ? extends Object> m, String cols) {
		super(m, cols);
	}

	public EntityMapWithIntId(Map<? extends String, ? extends Object> m, String cols, String labels) {
		super(m, cols, labels);
	}

	public EntityMapWithIntId(List<String> cols) {
		super(cols);
	}

	public EntityMapWithIntId(List<String> cols, List<String> labels) {
		super(cols, labels);
	}

	public EntityMapWithIntId(Map<? extends String, ? extends Object> m, List<String> cols) {
		super(m, cols);
	}

	public EntityMapWithIntId(Map<? extends String, ? extends Object> m, List<String> cols, List<String> labels) {
		super(m, cols, labels);
	}

	/**
	 * Won't return null
	 * @return
	 */
	@Override
	public Integer getId() {
		return getInt(getIdKey());
	}

	@Override
	public void setId(Integer id) {
		put(getIdKey(), id == null ? -1 : id);
	}

}
