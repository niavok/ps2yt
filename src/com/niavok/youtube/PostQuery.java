package com.niavok.youtube;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PostQuery {

	private String request;
	private String data;
	private Map<String, String> propertiesMap = new HashMap<String, String>();
	private String contentType = "application/x-www-form-urlencoded";
	private String location; 

	public PostQuery(String request, String data) {
		this.request = request;
		this.data = data;
	}

	private void execute(QueryOutput queryOutput) {

		try {
			URL url = new URL(request);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			if(data.length() > 0) {
				connection.setRequestProperty("Content-Type",
						contentType);
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Content-Length",
						"" + Integer.toString(data.getBytes().length));
			} else {
				connection.setRequestProperty("Content-Type",
						"none");
			}
			
			for(Entry<String,String> entry: propertiesMap.entrySet()) {
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			}
			
			connection.setUseCaches(false);

			
			
			
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();

			
			for (int i = 0;; i++) {
			      String headerName = connection.getHeaderFieldKey(i);
			      String headerValue = connection.getHeaderField(i);
//			      System.out.println(headerName);
//			      System.out.println(headerValue);

			      if (headerName == null && headerValue == null) {
//			        System.out.println("No more headers");
			        break;
			      }
			      
			      if(headerName != null && headerName.equals("Location")) {
			    	  location = headerValue;
			      }
		    }
			
			if (queryOutput != null) {

				String line;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));

				while ((line = reader.readLine()) != null) {
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

	public String getLocation() {
		return location;
	}

}
