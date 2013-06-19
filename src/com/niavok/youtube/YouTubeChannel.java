package com.niavok.youtube;

import java.io.File;

import javax.jws.Oneway;

import com.niavok.Config;



public class YouTubeChannel {

	private static String DEVELOPER_KEY;
	private static String CLIENT_ID;
	private static String CLIENT_SECRET;
	private String deviceCode;
	private String userCode;
	private String verificationUrl;
	private int expiresIn = 5;
	private int retryInterval = 5;
	private String refreshToken;
	private String tokenType;
	private String accessToken = "ya29.AHES6ZTjzcQwz3dspjWO7VGaze4Mi88Ykcp8xo5OZuFMsIbTfiUf8Q";
	private AuthenticationListener authenticationListener;
	private boolean authenticated = false;
//	private String accessToken;
	
	
	public YouTubeChannel() {
		
		DEVELOPER_KEY = Config.getYoutubeDeveloperKey();
		CLIENT_ID = Config.getYoutubeClientId();
		CLIENT_SECRET = Config.getYoutubeClientSecret();
		
		
		accessToken = Config.getYoutubeAccessToken();
		refreshToken = Config.getYoutubeRefreshToken();
		
		if(refreshToken != null) {
			// Check token validity
			if(refreshToken()) {
				setAuthenticated();
			} else {
				authenticated = false;
				authFirstStep();
			}
		} else {
			authFirstStep();
		}
		
		
		startAuthThread();
		
		
		
		
		//https://www.youtube.com/signin?next=/create_channel		
		
		
		//www.youtube.com/activate
		
		
//			POST /o/oauth2/device/code HTTP/1.1
//			Host: accounts.google.com
//			Content-Type: application/x-www-form-urlencoded
//		
//			if(accessToken == null) {
//		
//			authFirstStep();
//			authSecondStep();
//		
//				System.out.println("AccessToken: "+accessToken);
//			
//			}
			
//			getFeed();
			
//			uploadVideo();
	}

	private void startAuthThread() {
	
		Thread authThread = new Thread() {
			public void run() {
				while(true) {
					if(authenticated) {
						try {
							sleep(1000*expiresIn/2);
						} catch (InterruptedException e) {
						}
						if(!refreshToken()) {
							authenticated = false;
							authFirstStep();
						}
					} else {
						authSecondStep();
					}
				}
				
			};
		};
		authThread.setDaemon(true);
		authThread.start();
		
	}




	public void uploadVideo(File videoToUpload) {
		String urlParameters = "<?xml version=\"1.0\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"  xmlns:media=\"http://search.yahoo.com/mrss/\"  xmlns:yt=\"http://gdata.youtube.com/schemas/2007\">"
				+ " <media:group>    <media:title type=\"plain\">Bad Wedding Toast</media:title>"
				+ "<media:description type=\"plain\">      I gave a bad toast at my friend's wedding.    </media:description>"  
				+ "<media:category      scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\">People    </media:category>"
				+"    <media:keywords>toast, wedding</media:keywords>  </media:group></entry>";
		
		String request = "http://uploads.gdata.youtube.com/resumable/feeds/api/users/default/uploads";
		
		PostQuery uploadQuery = new PostQuery(request, urlParameters);
		uploadQuery.setContentType("application/atom+xml; charset=UTF-8");
		uploadQuery.addProperty("Authorization", "Bearer "+accessToken);
		uploadQuery.addProperty("GData-Version", "2");
		uploadQuery.addProperty("X-GData-Key", "key="+DEVELOPER_KEY);
		uploadQuery.addProperty("Slug", "test.mp4");
		
	
		String result = uploadQuery.getTextResult();
		String location = uploadQuery.getLocation();
		System.out.println(result);
		System.out.println("Location: "+location);
		
		
		PutQuery uploadDataQuery = new PutQuery(location,videoToUpload);
		result = uploadDataQuery.getTextResult();
		System.out.println(result);
	
		
		
	}



	public YoutubeFeed getFeed() {
		String urlParameters = "";
		String request = "http://gdata.youtube.com/feeds/api/users/default/uploads";
		
		GetQuery deviceCodeQuery = new GetQuery(request, urlParameters);
		deviceCodeQuery.addProperty("Authorization", "Bearer "+accessToken);
		
		String result = deviceCodeQuery.getTextResult();
		return new YoutubeFeed(result);
	}




	private void authFirstStep() {
		String urlParameters = "client_id="+CLIENT_ID+"&scope=https://gdata.youtube.com";
		String request = "https://accounts.google.com/o/oauth2/device/code";
		
		PostQuery deviceCodeQuery = new PostQuery(request, urlParameters);

		JSonMap result = deviceCodeQuery.getJsonResult();
		
		
		if(result.contains("device_code")) {
			deviceCode = result.getString("device_code");
			userCode = result.getString("user_code");
			verificationUrl = result.getString("verification_url");
			expiresIn = result.getInt("expires_in"); 
			retryInterval = result.getInt("interval");
		}
		
		System.out.println("Go to "+verificationUrl+ " and type "+userCode);
	}
	
	

	private void authSecondStep() {
	
		String urlParameters = "client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&code="+deviceCode+"&grant_type=http://oauth.net/grant_type/device/1.0";
		String request = "https://accounts.google.com/o/oauth2/token";
		
		
		boolean keepRetry = true;
		while(keepRetry) {
			keepRetry = false;
			
			PostQuery deviceCodeQuery = new PostQuery(request, urlParameters);
			JSonMap result = deviceCodeQuery.getJsonResult();
			
			if(result.contains("error")) {
				String errorValue = result.getString("error");
				if(errorValue.equals("authorization_pending") || errorValue.equals("slow_down")) {
					try {
						Thread.sleep(retryInterval*1000);
					} catch (InterruptedException e) {
					}
					keepRetry = true;
				} 
			} else {
				accessToken = result.getString("access_token");
				tokenType = result.getString("token_type");
				refreshToken = result.getString("refresh_token");
				expiresIn = result.getInt("expires_in");
				Config.setAccessToken(accessToken);
				Config.setRefreshToken(refreshToken);
				setAuthenticated();
			}
		}
		
	}

	

	private void setAuthenticated() {
		authenticated = true;
		if(authenticationListener != null) {
			authenticationListener.onAuthenticated();
		}
	}

	private boolean refreshToken() {
		String urlParameters = "client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&refresh_token="+refreshToken+"&grant_type=refresh_token";
		String request = "https://accounts.google.com/o/oauth2/token";
		
		PostQuery refreshTokenQuery = new PostQuery(request, urlParameters);
		JSonMap result = refreshTokenQuery.getJsonResult();
		
		
		if(result.contains("access_token")) {
			accessToken = result.getString("access_token");
			tokenType = result.getString("token_type");
			expiresIn = result.getInt("expires_in");
			System.out.println("Refresh token sucess");
			return true;
		} else {
			System.out.println("Refresh token failed");
			authenticated = false;
			accessToken = null;
			tokenType = null;
			expiresIn = 5;
			Config.setAccessToken(null);
			Config.setRefreshToken(null);
			return false;
		}
	}





	public String getUserCode() {
		return userCode;
	}




	public void setOnAuthenticated(AuthenticationListener authenticationListener) {
		this.authenticationListener = authenticationListener;
	}




	public boolean isAuthenticated() {
		return authenticated ;
	}

	
}
