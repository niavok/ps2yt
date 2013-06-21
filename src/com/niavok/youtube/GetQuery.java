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

public class GetQuery {

	private String request;
//	private String urlParameters;
	private String contentType = "application/x-www-form-urlencoded";
	private Map<String, String> propertiesMap = new HashMap<String, String>(); 

	public GetQuery(String request, String urlParameters) {
		this.request = request;
		// TODO Auto-generated constructor stub
//		this.urlParameters = urlParameters;
	}

	private void execute(QueryOutput queryOutput) {

		
		try {
			URL url = new URL(request);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			
//			if(urlParameters.length() > 0) {
////				connection.setDoOutput(true);
//				connection.setRequestProperty("Content-Type",
//						contentType);
//				connection.setRequestProperty("charset", "UTF-8");
//				connection.setRequestProperty("Content-Length",
//						"" + Integer.toString(urlParameters.getBytes().length));
//			}
			
			for(Entry<String,String> entry: propertiesMap.entrySet()) {
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			}
			
			connection.setUseCaches(false);

//			DataOutputStream wr = new DataOutputStream(
//					connection.getOutputStream());
//			wr.writeBytes(urlParameters);
//			wr.flush();
//			wr.close();

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

}
