package com.niavok.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.niavok.podcastscience.PSManager;
import com.niavok.podcastscience.PSTrack;
import com.niavok.youtube.YouTubeChannel;
import com.niavok.youtube.YoutubeFeed;

public class PodcastListPage extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -22513621390876310L;
	private MainFrame parent;

	public PodcastListPage(MainFrame mainFrame) {
		this.parent = mainFrame;
		
		setLayout(new BorderLayout());
		
		List<PSTrack> tracks = PSManager.getTracks();
		
		syncUploadState(tracks, parent.getYouTubeChannel());
		
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
		
		panel.setLayout(new GridLayout(tracks.size(), 1));
		
		JScrollPane scrollPane = new JScrollPane(panel);
		
		setPreferredSize(new Dimension(450, 110));
		
		
		
		
		
		add(new JLabel(""+tracks.size()+" épisodes"), BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		
		for(PSTrack track: tracks) {
			panel.add(new PodcastView(track));
//			System.out.println("- "+track.getTitle());
		}
		
	}

	private void syncUploadState(List<PSTrack> tracks,
		YouTubeChannel youTubeChannel) {
		YoutubeFeed feed = youTubeChannel.getFeed();
		
		for(PSTrack track: tracks) {
			if(feed.isExistNumber(track.getNumber())) {
				track.setUploaded(true);
				track.setUploadUrl(feed.getUploadUrl(track.getNumber()));
			}
		}
	}
	
	
	
	
}
