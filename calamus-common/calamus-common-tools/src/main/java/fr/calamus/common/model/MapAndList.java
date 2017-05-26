package fr.calamus.common.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author haerwynn
 */
public class MapAndList<K,V>{
	private Map<K,V>map;
	private List<K>keys;

	public MapAndList() {
		setMap(new HashMap<K,V>());
	}
	public MapAndList(Map<K, V> map) {
		setMap(map);
	}

	public List<K> getKeys() {
		return keys;
	}

	public void setMap(Map<K, V> map) {
		this.map = map;
		createArrayList();
	}

	private void createArrayList() {
		keys=new ArrayList<>();
	}
}
