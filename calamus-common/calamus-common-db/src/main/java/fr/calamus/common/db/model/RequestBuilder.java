/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.db.model;

import fr.calamus.common.tools.ListsAndArrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author haerwynn
 */
public class RequestBuilder implements Cloneable{

	//private String req;
	private String select;
	private final String table;
	private List<String> wheres;
	private List<String> orders;
	private Integer limit;
	private Integer offset;
	private static final Log log=LogFactory.getLog(RequestBuilder.class);
	private String from;

	public RequestBuilder(String table) {
		super();
		this.table=table;
		wheres=new ArrayList<>();
		orders=new ArrayList<>();
	}

	public String request(){
		String r="select "+select+" from "+from;
		if(wheres.size()>0){
			r+=" where ";
			String w=ListsAndArrays.mergeList(wheres, " and ");
			r+=w;
		}
		if(orders.size()>0){
			r+=" order by ";
			String o=ListsAndArrays.mergeList(orders, ",");
			r+=o;
		}
		if(offset!=null){
			r+=" offset "+offset;
		}
		if(limit!=null){
			r+=" limit "+limit;
		}
		//log.debug("request="+r);
		return r;
	}

	public RequestBuilder selectCount(){
		return select("count(*)");
	}

	public RequestBuilder select(){
		return select("*");
	}

	public RequestBuilder select(String what){
		if(what==null)what="*";
		select=what;
		if(table!=null)return this.from(table);
		return this;
	}

	public RequestBuilder from(String what){
		//if(req==null)throw new RuntimeException("req=null");
		from=what;
		return this;
	}

	public RequestBuilder addWhere(String where){
		if(!wheres.contains(where)){
			wheres.add(where);
		}
		return this;
	}
	public RequestBuilder removeWhere(String where){
		if(wheres.contains(where)){
			wheres.remove(where);
		}
		return this;
	}
	public RequestBuilder addOrder(String order){
		if(!orders.contains(order)){
			orders.add(order);
		}
		return this;
	}
	public RequestBuilder removeOrder(String order){
		if(orders.contains(order)){
			orders.remove(order);
		}
		return this;
	}
	public RequestBuilder cloneMe(){
		try {
			return (RequestBuilder) this.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void setLimit(int c) {
		limit=c;
	}

	public void setOffset(int o) {
		offset=o;
	}

	public void emptyOrder(){
		orders=new ArrayList<>();
	}
}
