package com.niavok.youtube;

public class TextQueryOutput implements QueryOutput {

	String lines = "";
	
	@Override
	public void processLine(String line) {
		lines += line + "\n";
	}

	public String getOutput() {
		return lines;
	}

}
