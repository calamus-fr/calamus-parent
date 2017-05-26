/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import fr.calamus.common.tools.ToolBox;
import java.io.Serializable;

/**
 *
 * @author haerwynn
 */
public class DoubleString implements Serializable{
	private String label;
	private String value;

	public DoubleString(String label, String value) {
		this.label = label;
		this.value = value;
	}
	public DoubleString() {
		this.label = null;
		this.value = null;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String l = ToolBox.echapperStringPourMySql(label);
		if(l==null)l="\"\"";
		String v = ToolBox.echapperStringPourMySql(value);
		if(v==null)v="\"\"";
		return "{\"label\":"+l+",\"value\":"+v+"}";
	}


}
