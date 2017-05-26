package fr.calamus.common.model;

import fr.calamus.common.tools.StringEntry;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StringMap extends HashMap<String, String> implements Serializable {

	private static final long serialVersionUID = -1869906172480688425L;

	public StringMap(){
		super();
	}

	public StringMap(StringEntry... entries){
		super();
		for (StringEntry e : entries) {
			put(e.getKey(), e.getValue());
		}
	}

	public StringMap(int initialCapacity){
		super(initialCapacity);
	}

	public StringMap(Map<? extends String, ? extends String> m){
		super(m);
	}

	public StringMap(int initialCapacity, float loadFactor){
		super(initialCapacity, loadFactor);
	}

	public StringEntry getStringEntry(String key){
		if (containsKey(key))
			return new StringEntry(key, get(key));
		return null;
	}

	public void addStringEntry(StringEntry e){
		if (e != null)
			put(e.getKey(), e.getValue());
	}
}
