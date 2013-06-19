package com.niavok.ui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.niavok.youtube.AuthenticationListener;
import com.niavok.youtube.YouTubeChannel;

public class YoutubeAuthentificationPage extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6098691315785635216L;
	private MainFrame parent;

	public YoutubeAuthentificationPage(MainFrame mainFrame) {
		
		this.parent = mainFrame;
		YouTubeChannel channel = mainFrame.getYouTubeChannel();
		
		channel.setOnAuthenticated(new AuthenticationListener() {
			
			public void onAuthenticated() {
				parent.onYouTubeAuthDone();
			}
			
		});
		

		
		add(new JLabel("Copy this authentication code"));
		add(new JTextField(channel.getUserCode()));
		add(new JLabel("and click to the button to autorize this application"));
		JButton openUrlButton = new JButton("http://www.youtube.com/activate");
		add(openUrlButton);
		
		openUrlButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if(Desktop.isDesktopSupported()) {
					try {
				        Desktop.getDesktop().browse(new URI("http://www.youtube.com/activate"));
			      } catch (IOException | URISyntaxException e) {  }
				}
			}
		});
			
		
		
	}
	
}
