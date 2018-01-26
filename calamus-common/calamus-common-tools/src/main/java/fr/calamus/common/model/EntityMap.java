/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import fr.calamus.common.tools.ListsAndArrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author haerwynn
 */
public class EntityMap extends BaseEntityMap{

	private List<String> cols;
	private List<String> labels;
	public EntityMap(String cols) {
		this(cols,cols);
	}
	public EntityMap(String cols, String labels) {
		super();
		initMeta(cols,labels);
	}

	public EntityMap(Map<? extends String, ? extends Object> m, String cols) {
		this(m, cols, cols);
	}
	public EntityMap(Map<? extends String, ? extends Object> m, String cols, String labels) {
		super(m);
		initMeta(cols,labels);
	}
	public EntityMap(List<String> cols) {
		this(cols,cols);
	}
	public EntityMap(List<String> cols, List<String> labels) {
		super();
		initMeta(cols,labels);
	}

	public EntityMap(Map<? extends String, ? extends Object> m, List<String> cols) {
		this(m, cols, cols);
	}
	public EntityMap(Map<? extends String, ? extends Object> m, List<String> cols, List<String> labels) {
		super(m);
		initMeta(cols,labels);
	}

	protected void initMeta(String cols, String labels) {
		initMeta(ListsAndArrays.splitToStringList(cols, ","),ListsAndArrays.splitToStringList(labels, ","));
	}

	protected void initMeta(List<String> cols, List<String> labels) {
		if(cols==null||labels==null||cols.size()!=labels.size())throw new IllegalArgumentException("incoherence colonnes/labels");
		this.cols=cols;
		this.labels=labels;
	}

	public List<String> cols() {
		return new ArrayList<>(cols);
	}

	public List<String> labels() {
		return new ArrayList<>(labels);
	}

}
