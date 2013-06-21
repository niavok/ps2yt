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
		scrollPane.getVerticalScrollBar().setUnitIncrement(64);
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
				track.setUploadUrl(feed.getUploadUrl(track.getNumber()).getUrl());
			}
		}
	}
	
	
	
	
}
