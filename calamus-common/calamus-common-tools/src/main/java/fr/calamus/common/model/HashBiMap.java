/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.model;

import java.util.HashMap;

/**
 *
 * @author haerwynn
 */
public class HashBiMap<K, V> extends HashMap<K, V>{

	@Override
	public V put(K key, V value) {
		if(key==null)return null;
		if(!containsValue(value)){
			return super.put(key, value);
		}
		K otherKey = findByValue(value);
		remove(otherKey);
		return super.put(key, value);
	}

	public K findByValue(V value){
		if(value==null)return null;
		for(K key:keySet()){
			if(value.equals(get(key)))return key;
		}
		return null;
	}

	public V removeByValue(V value){
		K key=findByValue(value);
		return remove(key);
	}
}
