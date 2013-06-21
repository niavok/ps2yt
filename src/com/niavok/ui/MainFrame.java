package com.niavok.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
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
			gotoPodcastListPage();
		} else {
			YoutubeAuthentificationPage authentificationPage = new YoutubeAuthentificationPage(this);
			getContentPane().add(authentificationPage, BorderLayout.CENTER);
			pack();
			setSize(600, 200);
			centerFrame();
		}
		
		setTitle("Podcast Science -> YouTube");
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}


	private void centerFrame() {
		this.setLocationRelativeTo(null); 
//		Dimension dim = Toolkit.getDefaultToolkit().getD getScreenSize();
//		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}


	public YouTubeChannel getYouTubeChannel() {
		return youTubeChannel;
	}


	public void onYouTubeAuthDone() {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				gotoPodcastListPage();
			}

		});
		
		
	}
	private void gotoPodcastListPage() {
		getContentPane().removeAll();
		getContentPane().add(new PodcastListPage(this), BorderLayout.CENTER);
		pack();
		setSize(600, 800);
		centerFrame();
	}
	
}
