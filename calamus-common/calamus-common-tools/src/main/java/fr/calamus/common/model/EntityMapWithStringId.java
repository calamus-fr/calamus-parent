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
public class EntityMapWithStringId extends EntityMapWithId<String>{

	public EntityMapWithStringId(String cols) {
		super(cols);
	}

	public EntityMapWithStringId(String cols, String labels) {
		super(cols, labels);
	}

	public EntityMapWithStringId(Map<? extends String, ? extends Object> m, String cols) {
		super(m, cols);
	}

	public EntityMapWithStringId(Map<? extends String, ? extends Object> m, String cols, String labels) {
		super(m, cols, labels);
	}

	public EntityMapWithStringId(List<String> cols) {
		super(cols);
	}

	public EntityMapWithStringId(List<String> cols, List<String> labels) {
		super(cols, labels);
	}

	public EntityMapWithStringId(Map<? extends String, ? extends Object> m, List<String> cols) {
		super(m, cols);
	}

	public EntityMapWithStringId(Map<? extends String, ? extends Object> m, List<String> cols, List<String> labels) {
		super(m, cols, labels);
	}

	@Override
	public String getId() {
		return (String) get(getIdKey());
	}

	@Override
	public void setId(String id) {
		put(getIdKey(), id);
	}
}
