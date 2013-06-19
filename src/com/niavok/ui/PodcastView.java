package com.niavok.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.niavok.podcastscience.PSTrack;

public class PodcastView extends JPanel {

	private static final String UPLOADED = "Uploaded";
	private static final String NEW = "New";
	private static final String DOWNLOADED = "Downloaded";
	private static final String ENCODED = "Encoded";
	/**
	 * 
	 */
	private static final long serialVersionUID = -1173027143755244884L;
	private PSTrack track;

	public PodcastView(final PSTrack track) {
		this.track = track;
		
		JPanel innerPanel = new JPanel();
		
		innerPanel.add(new JLabel(""+track.getNumber()));
		innerPanel.add(new JLabel(track.getSimpleTitle()));
		
		String status = NEW;
		
		
		if(track.isDownloaded()) {
			status = DOWNLOADED;
		}
		
		if(track.isEncoded()) {
			status = ENCODED;
		}
		
		
		if(track.isUploaded()) {
			status = UPLOADED;
		}
		
		
		
		
		innerPanel.add(new JLabel("Status: "+status));

		if(status.equals(UPLOADED)) {
			innerPanel.setBackground(new Color(112,223,152));
		} else if(status.equals(ENCODED)) {
			innerPanel.setBackground(new Color(167,218,223));
		} else if(status.equals(DOWNLOADED)) {
			innerPanel.setBackground(new Color(255,243,191));
		} 
		
		if(status.equals(UPLOADED)) {
			JButton openUrlButton = new JButton("Open");
			innerPanel.add(openUrlButton);
			
			openUrlButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent event) {
					if(Desktop.isDesktopSupported()) {
						try {
					        Desktop.getDesktop().browse(new URI(track.getUploadUrl()));
				      } catch (IOException | URISyntaxException e) {  }
					}
				}
			});
		} else {
			
		}
		
		
		innerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		
		this.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
		this.setLayout(new BorderLayout());
		this.add(innerPanel, BorderLayout.CENTER);
		
	}

	
	
}
