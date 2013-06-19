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
}
