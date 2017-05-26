/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import java.io.Serializable;

/**
 *
 * @author haerwynn
 */
public class FourStrings implements Serializable{
	private String label;
	private String name;
	private String oldValue;
	private String newValue;

	public FourStrings(String label, String name, String oldValue, String newValue) {
		this.label = label;
		this.name = name;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	public FourStrings() {
		this.label = null;
		this.name = null;
		this.oldValue = null;
		this.newValue = null;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return "FourStrings{name="+name+", label="+label+", oldValue="+oldValue+", newValue="+newValue+"}";//super.toString();
	}


}
