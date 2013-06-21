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

import com.niavok.Config;

public class PSTrack {

	private String title;
	private String id;
	private String audioUrl;
	private String imageUrl;
	private String simpleTitle;
	private String description = "";
	private String paperUrl = "";
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
	
	public void setPaperUrl(String paperUrl) {
		this.paperUrl = paperUrl;
	}
	
	public void setDescription(String description) {
		this.description = description;
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
		return Config.getCachePath(getId()+".mp3");
	}
	
	public String getCachedEncodedPath() {
		return Config.getCachePath(getId()+".mp4");
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

	public String getDescription() {
		return this.description;
	}

	public String getPaperUrl() {
		return paperUrl;
	}
}
