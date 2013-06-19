package com.niavok.youtube;

import java.util.HashMap;
import java.util.Map;

public class JSonMap {

	Map<String,String> keyMap = new  HashMap<String, String>();
	
	public int getInt(String key) {
		
		
		return Integer.parseInt(keyMap.get(key));
	}

	public boolean contains(String key) {
		return keyMap.containsKey(key);
	}

	public String getString(String key) {
		return keyMap.get(key);
	}

	public void putString(String key, String value) {
		keyMap.put(key, value);
	}

}
