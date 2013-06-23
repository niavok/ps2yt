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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Semaphore;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import com.niavok.AudioDecoder;
import com.niavok.Config;
import com.niavok.VideoEncoder;
import com.niavok.podcastscience.PSTrack;
import com.niavok.youtube.YouTubeChannel;
import com.niavok.youtube.YoutubeEntry;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class PodcastView extends JPanel {

	private static Semaphore downloadSem= new Semaphore(Config.getMaxConcurrentDownload(), true);
	private static Semaphore uploadSem= new Semaphore(Config.getMaxConcurrentUpload(), true);
	private static Semaphore encodeSem= new Semaphore(Config.getMaxConcurrentEncode(), true);
	
	
	private static final String UPLOADED = "Uploaded";
	private static final String FAILED = "Failed";
	
	private static final String NEW = "New";
	private static final String DOWNLOADED = "Downloaded";
	private static final String WAITING_FOR_DOWNLOAD= "Waiting for download";
	private static final String ENCODED = "Encoded";
	private static final String WAITING_FOR_ENCODE= "Waiting for encode";
	private static final String ENCODING = "Encoding";
	private static final String DOWNLOADING = "Downloading";
	private static final String UPLOADING = "Uploading";
	private static final String WAITING_FOR_UPLOAD= "Waiting for upload";
	/**
	 * 
	 */
	private static final long serialVersionUID = -1173027143755244884L;
	private PSTrack track;
	private JButton uploadButton;
	private JLabel statusLabel;
	private JPanel innerPanel;
	private ActionListener actionListener;
	private JLabel warningLabel;

	public PodcastView(final PSTrack track) {
		this.track = track;

		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
		
		JPanel panelTop = new JPanel();
		JPanel panelBottom = new JPanel();
		
		panelTop.setLayout(new FlowLayout(FlowLayout.LEADING));
		panelTop.setOpaque(false);
		panelBottom.setLayout(new FlowLayout(FlowLayout.LEADING));
		panelBottom.setOpaque(false);
		
		innerPanel.add(panelTop);
		innerPanel.add(panelBottom);
		
		panelTop.add(new JLabel("<html><strong>" + track.getNumber()+ "</strong></html>"));
		panelTop.add(new JLabel(track.getSimpleTitle()));

		String status = NEW;

		if (track.isDownloaded()) {
			status = DOWNLOADED;
		}

		if (track.isEncoded()) {
			status = ENCODED;
		}

		if (track.isUploaded()) {
			status = UPLOADED;
		}

		statusLabel = new JLabel();
		panelBottom.add(statusLabel);

		uploadButton = new JButton("Upload");

		actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				startUploadTask();
				uploadButton.setEnabled(false);
			}

		};

		uploadButton.addActionListener(actionListener);
		panelBottom.add(uploadButton);

		warningLabel = new JLabel();
		
		panelBottom.add(warningLabel);
		setStatus(status);

		innerPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));

		this.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		this.setLayout(new BorderLayout());
		this.add(innerPanel, BorderLayout.CENTER);

	}

	private void startUploadTask() {

		Thread uploadThread = new Thread() {
			@Override
			public void run() {

				if (!track.isDownloaded()) {
					updateStatus(WAITING_FOR_DOWNLOAD);
					try {
						downloadSem.acquire();
					} catch (InterruptedException e) {
					}
					updateStatus(DOWNLOADING);
					track.dowloadAudio(false);
					downloadSem.release();
					updateStatus(DOWNLOADED);
					
				}

				if (!track.isEncoded()) {
					updateStatus(WAITING_FOR_ENCODE);
					try {
						encodeSem.acquire();
					} catch (InterruptedException e) {
					}
					updateStatus(ENCODING);
					String audioPath = track.getCachedAudioPath();

					try {
					BufferedImage image = track.getImage();

					AudioDecoder audioDecoder = new AudioDecoder(audioPath);
					String outputPath = track.getCachedEncodedPath() + ".tmp.mp4";
					VideoEncoder videoEncoder = new VideoEncoder(outputPath,
							audioDecoder.getSampleRate(), audioDecoder.getChannels());
					videoEncoder.setImage(image);
					
						audioDecoder.decodeTo(videoEncoder);
						videoEncoder.close();
					
					

					new File(outputPath).renameTo(new File(track
							.getCachedEncodedPath()));
					} catch(Exception e) {
						e.printStackTrace();
						updateStatus(FAILED);
						encodeSem.release();
						return;
					}
					
					encodeSem.release();
					updateStatus(ENCODED);
				}

				if (!track.isUploaded()) {
					updateStatus(WAITING_FOR_UPLOAD);
					try {
						uploadSem.acquire();
					} catch (InterruptedException e) {
					}
					updateStatus(UPLOADING);
					YouTubeChannel youTubeChannel = new YouTubeChannel();
					YoutubeEntry uploadVideo = youTubeChannel.uploadVideo(new File(track
							.getCachedEncodedPath()), generateOutputTitle(), generateOutputDescription());
					if(uploadVideo != null && uploadVideo.getNumber() != null && uploadVideo.getNumber().equals(track.getNumber())) {
						track.setUploadUrl(uploadVideo.getUrl());
						updateStatus(UPLOADED);
					} else {
						updateStatus(FAILED);
					}
					uploadSem.release();
				}
			}

			
		};

		uploadThread.setDaemon(true);
		uploadThread.start();
	}

	

	void updateStatus(final String status) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setStatus(status);
			}
		});
	}

	void setStatus(String status) {
		
		if (status.equals(UPLOADING) || status.equals(DOWNLOADING) || status.equals(ENCODING)) {
			statusLabel.setText("<html>Status: <strong>" + status+ "</strong></html>");
		} else {
			statusLabel.setText("Status: " + status);
		}
		
		
		
		if (status.equals(UPLOADED)) {
			innerPanel.setBackground(new Color(112, 223, 152));
			
			uploadButton.setText("Open");
			uploadButton.setEnabled(true);
			
			uploadButton.removeActionListener(actionListener);
			uploadButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					BrowserLauncher launcher;
					try {
						launcher = new BrowserLauncher();
						launcher.openURLinBrowser(track.getUploadUrl());
					} catch (BrowserLaunchingInitializingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedOperatingSystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}else if (status.equals(FAILED)) {
			uploadButton.setEnabled(true);
			innerPanel.setBackground(new Color(223, 112, 152));
		} else if (status.equals(ENCODED)) {
			innerPanel.setBackground(new Color(167, 218, 223));
		} else if (status.equals(DOWNLOADED)) {
			innerPanel.setBackground(new Color(255, 243, 191));
		} 
	}
	
	private String generateOutputTitle() {
		
		int maxSize = 100;
		String simpleTitle = track.getSimpleTitle();
		String suffixe = " - PS n°"+track.getNumber();
		String trucatedTitle = "";
		
		//Final size must be at most 100
		String[] split = simpleTitle.split(" ");
		for(int i = 0; i < split.length; i++) {
			String tempTitle = trucatedTitle + (i==0? "": " ") + split[i] + suffixe;
		
			try {
				 byte[]charArray = tempTitle.getBytes("UTF-8");
				System.out.println("charArray length "+charArray.length);
				if(charArray.length >=  maxSize) {
					warningLabel.setText("<html><strong style=\"color:orange;\">Title too long. Truncated.</strong></html>");
					break;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			trucatedTitle += (i==0? "": " ") + split[i];
		}

		
		String title = trucatedTitle+suffixe;
		
		return title;
	}
	
	protected String generateOutputDescription() {
		String description = track.getDescription() +
		"\n" +
		"Retrouvez le dossier écrit, par ici : "+track.getPaperUrl() +
		"\n\n\n" +
		"Podcast science est une émission (audio)" +
		" hebdomadaire qui parle de science, sans" +
		" prise de tête. Retrouvez-nous tous les" +
		" jeudi soirs à 20h30 en" +
		" live sur http://www.podcastscience.fm/live, ou sur" +
		" notre site web: http://www.podcastscience.fm, sur" +
		" iTunes: itpc://feeds.feedburner.com/PodcastScien­ce," +
		" ou dans votre agrégateur podcast" +
		" préféré: http://feeds.feedburner.com/PodcastScience" +
		" Promis, on va vous faire aimer la science !"+
		"\n\n" +
		"http://www.podcastscience.fm";
		
		return description; 	
	
	}
}
