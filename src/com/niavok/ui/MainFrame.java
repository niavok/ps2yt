package com.niavok.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.niavok.youtube.YouTubeChannel;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5007828175042512167L;
	private YouTubeChannel youTubeChannel;

	
	public MainFrame() {
		
		youTubeChannel = new YouTubeChannel();
		getContentPane().setLayout(new BorderLayout());
		
		if(youTubeChannel.isAuthenticated()) {
			onYouTubeAuthDone();
		} else {
			YoutubeAuthentificationPage authentificationPage = new YoutubeAuthentificationPage(this);
			getContentPane().add(authentificationPage, BorderLayout.CENTER);
		}
		
		setTitle("Podcast Science -> YouTube");
		setSize(1000, 800);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}


	public YouTubeChannel getYouTubeChannel() {
		return youTubeChannel;
	}


	public void onYouTubeAuthDone() {
		
		getContentPane().removeAll();
		getContentPane().add(new PodcastListPage(this), BorderLayout.CENTER);
		
	}
	
	
}
