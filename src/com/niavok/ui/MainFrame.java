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
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.niavok.Config;
import com.niavok.podcast.PodcastManager;
import com.niavok.youtube.YouTubeChannel;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5007828175042512167L;
	private YouTubeChannel youTubeChannel;

	
	public MainFrame() {
		
		youTubeChannel = YouTubeChannel.getInstance();
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
		
        BufferedImage image;
		try {
			image = ImageIO.read(this.getClass().getResource("podcastscience.png"));
			setIconImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		 
		
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
	void gotoPodcastListPage() {
		getContentPane().removeAll();
		getContentPane().add(new PodcastListPage(this, PodcastManager.getPodcast(Config.getDefaultPodcast())), BorderLayout.CENTER);
		pack();
		setSize(600, 800);
		centerFrame();
	}
	
}
