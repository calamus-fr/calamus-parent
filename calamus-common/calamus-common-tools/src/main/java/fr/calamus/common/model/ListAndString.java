/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.calamus.common.model;

import java.util.List;

/**
 *
 * @author haerwynn
 */
public class ListAndString<T> {

	private List<T>list;
	private String string;

	public ListAndString(List<T> list, String string) {
		this.list = list;
		this.string = string;
	}

	public ListAndString() {
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return "["+string+":"+list+"]";
	}


	
}
