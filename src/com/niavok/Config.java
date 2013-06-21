package com.niavok;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

	private static final String CACHE_USER_CONF = "cache/user.conf";
	static Properties configFile;
	static Properties userConfigFile;
	
	static {
		configFile = new Properties();
		userConfigFile = new Properties();
		
		try {
			configFile.load(new FileInputStream(new File("ps2yt.conf")));
			userConfigFile.load(new FileInputStream(new File(CACHE_USER_CONF)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getYoutubeDeveloperKey() {
		return configFile.getProperty("DEVELOPER_KEY");
	}

	public static String getYoutubeClientId() {
		return configFile.getProperty("CLIENT_ID");
	}

	public static String getYoutubeClientSecret() {
		return configFile.getProperty("CLIENT_SECRET");
	}

	public static String getYoutubeAccessToken() {
		return userConfigFile.getProperty("ACCESS_TOKEN");
	}

	public static String getYoutubeRefreshToken() {
		return userConfigFile.getProperty("REFRESH_TOKEN");
	}

	public static void setAccessToken(String accessToken) {
		if(accessToken == null) {
			userConfigFile.setProperty("ACCESS_TOKEN", "");
		} else {
			userConfigFile.setProperty("ACCESS_TOKEN", accessToken);
		}
		save();
	}

	public static void setRefreshToken(String refreshToken) {
		if(refreshToken == null) {
			userConfigFile.setProperty("REFRESH_TOKEN", "");
		} else {
			userConfigFile.setProperty("REFRESH_TOKEN", refreshToken);
		}
		save();
	}
	
	private static void save() {
		try {
			userConfigFile.store(new FileOutputStream(new File(CACHE_USER_CONF)), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getMaxConcurrentEncode() {
		return 2;
	}

	public static int getMaxConcurrentUpload() {
		return 1;
	}

	public static int getMaxConcurrentDownload() {
		return 1;
	}

	

}
