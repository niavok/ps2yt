package com.niavok.podcastscience;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.imageio.ImageIO;

public class PSTrack {

	private String title;
	private String id;
	private String audioUrl;
	private String imageUrl;
	private String simpleTitle;
	private String number;
	private boolean uploaded = false;
	private String uploadUrl;

	public void setTitle(String title) {
		this.title = title;
		id = title;
		id = id.replace(' ', '_');
		id = id.replace("'", "");
		id = id.replace("/", "");
		id = id.replace("\\", "");
		
		
		String[] split = title.split(" (- )?", 2);
		number = split[0];
		simpleTitle =split[1]; 
		
		
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSimpleTitle() {
		return simpleTitle;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setAudioUrl(String audioUrl){
		this.audioUrl = audioUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void dowloadAudio(boolean force) {
		
		File cachedFile = new File(getCachedAudioPath());
		
		if(cachedFile.exists() && !force) {
			return;
		}
		
		
		URL audio;
		try {
			audio = new URL(audioUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		
	    ReadableByteChannel rbc;
		try {
			rbc = Channels.newChannel(audio.openStream());
		
	    
	    File tmpFile = new File(getCachedAudioPath()+".tmp");
			
	    
	    FileOutputStream fos = new FileOutputStream(tmpFile);
	    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	    fos.close();
	    
	    tmpFile.renameTo(cachedFile);
	    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getCachedAudioPath() {
		return "cache/"+getId()+".mp3";
	}
	
	public String getCachedEncodedPath() {
		return "cache/"+getId()+".mp4";
	}

	public String getId() {
		return id;
	}

	public BufferedImage getImage() {
		try {
			return ImageIO.read(new URL(imageUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}
	
	public boolean isUploaded() {
		return uploaded;
	}

	public boolean isDownloaded() {
		File cachedFile = new File(getCachedAudioPath());
		return cachedFile.exists();
	}

	public boolean isEncoded() {
		File cachedFile = new File(getCachedEncodedPath());
		return cachedFile.exists();
	}

	public String getUploadUrl() {
		return uploadUrl;
	}
	
	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

}
