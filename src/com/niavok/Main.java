package com.niavok;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.niavok.podcastscience.PSTrack;
import com.niavok.ui.MainFrame;
import com.niavok.youtube.YouTubeChannel;

public class Main {

	public static void main(String[] arguments) {
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, fall back to cross-platform
		    try {
		        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		    } catch (Exception ex) {
		        // not worth my time
		    }
		}
		
		
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		
//		createCacheDir();
//		
//		List<PSTrack> tracks = PSManager.getTracks();
//		
//		System.out.println(""+tracks.size()+" épisodes :");
//		
//		for(PSTrack track: tracks) {
//			System.out.println("- "+track.getTitle());
//		}
//		
		
		//Generate first track
		
		//uploadTrack(tracks.get(2));
//		uploadTrack(tracks.get(127));
		
		
//		testYouTube();
	}

	private static void testYouTube() {
		YouTubeChannel youTubeChannel = new YouTubeChannel();
	}

	private static void createCacheDir() {
		File dir = new File("cache");
		dir.mkdirs();
	}

	private static void uploadTrack(PSTrack track) {
		
		track.dowloadAudio(false);
		String audioPath = track.getCachedAudioPath();
		
		BufferedImage image = track.getImage();
		
		AudioDecoder audioDecoder = new AudioDecoder(audioPath);
		String outputPath = track.getId()+".mp4";
		VideoEncoder videoEncoder = new VideoEncoder(outputPath, audioDecoder.getSampleRate());
		videoEncoder.setImage(image);
		audioDecoder.decodeTo(videoEncoder);
		videoEncoder.close();
		
		YouTubeChannel youTubeChannel = new YouTubeChannel();
		
		youTubeChannel.uploadVideo(new File(outputPath));
		
	}
	
}
