/* 
 * This file is part of PS2YT
 *
 * Copyright (C) 2013 Frédéric Bertolus (Niavok)
 * 
 * PS2YT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
