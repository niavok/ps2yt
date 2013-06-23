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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PutQuery {

	private String request;
	private Map<String, String> propertiesMap = new HashMap<String, String>();
	private String contentType = "video/mp4";
	private File videoToUpload;

	public PutQuery(String request, File videoToUpload) {
		this.request = request;
		this.videoToUpload = videoToUpload;
	}

	private void execute(QueryOutput queryOutput) {

		try {
			URL url = new URL(request);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type",
						contentType);
				connection.setRequestProperty("Content-Length",
						"" + Long.toString(videoToUpload.length()));
			
			for(Entry<String,String> entry: propertiesMap.entrySet()) {
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			}
			
			connection.setUseCaches(false);

			
			
			
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			
			FileInputStream fileInputStream = new FileInputStream(videoToUpload);
			byte[] buffer = new byte[100000];
			int readSize;
			long totalSize = videoToUpload.length();
			long uploadedSize = 0;
			
			while(true) {
				readSize = fileInputStream.read(buffer);	
				
				if(readSize <=0) {
					break;
				}
				
				wr.write(buffer, 0, readSize);
				uploadedSize += readSize;
				System.out.println("Upload "+uploadedSize+"/"+totalSize);
				
			}
			wr.flush();
			wr.close();
			
			fileInputStream.close();
			
				
			if (queryOutput != null) {

				String line;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

				while ((line = reader.readLine()) != null) {
//					System.out.println(line);
					queryOutput.processLine(line);
				}

				reader.close();
			}

			connection.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public JSonMap getJsonResult() {
		JSonQueryOutput jSonQueryOutput = new JSonQueryOutput();
		execute(jSonQueryOutput);
		return jSonQueryOutput.getOutput();
	}

	public void addProperty(String key, String value) {
		propertiesMap.put(key, value);
	}

	public String getTextResult() {
		TextQueryOutput textQueryOutput = new TextQueryOutput();
		execute(textQueryOutput);
		return textQueryOutput.getOutput();
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
