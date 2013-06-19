package com.niavok.youtube;

public class JSonQueryOutput implements QueryOutput {

	private JSonMap jSonMap;

	public JSonQueryOutput() {
		jSonMap = new JSonMap();
	}
	
	@Override
	public void processLine(String line) {
		if(line.equals("{")) {
			return;
		} else if (line.equals("}")) {
			return;
		}
		
		line = line.trim();
		String[] split = line.split(":", 2);
		String key = split[0].trim();
		key = key.substring(1, key.length() -1);
		String value = split[1].trim();
		if(value.endsWith(",")) {
			value = value.substring(0, value.length()-1);
		}
		if(value.startsWith("\"")) {
			value = value.substring(1, value.length() -1);	
		}
		
		jSonMap.putString(key, value);
		
	}

	public JSonMap getOutput() {
		return jSonMap;
	}

}
