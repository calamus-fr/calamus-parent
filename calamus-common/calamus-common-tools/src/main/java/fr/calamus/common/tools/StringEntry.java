package fr.calamus.common.tools;

import java.io.Serializable;

public class StringEntry implements Serializable{

	private String key;
	private String value;

	public StringEntry(){
		key = null;
		value = null;
	}

	public StringEntry(String key, String value){
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey(){
		return key;
	}

	public void setKey(String key){
		this.key = key;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

}
