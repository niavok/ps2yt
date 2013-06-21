package com.niavok.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.niavok.youtube.AuthenticationListener;
import com.niavok.youtube.YouTubeChannel;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

public class YoutubeAuthentificationPage extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6098691315785635216L;
	private MainFrame parent;

	public YoutubeAuthentificationPage(MainFrame mainFrame) {
		
		this.parent = mainFrame;
		
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		YouTubeChannel channel = mainFrame.getYouTubeChannel();
		
		channel.setOnAuthenticated(new AuthenticationListener() {
			
			public void onAuthenticated() {
				parent.onYouTubeAuthDone();
			}
			
		});
		

		
		add(new JLabel("Copy this authentication code"));
		JTextField textField = new JTextField(channel.getUserCode());
		Font f = new Font("Arial", Font.BOLD, 50);
		textField.setFont(f);
		textField.setEditable(false);
		textField.setColumns(15);
		textField.setHorizontalAlignment(JTextField.CENTER);
//		textField.setPreferredSize(new Dimension(300, 200));
		textField.setMaximumSize( textField.getPreferredSize() );
		add(textField);
		add(new JLabel("and click to the button to autorize this application"));
		JButton openUrlButton = new JButton("http://www.youtube.com/activate");
		Font f2 = new Font("Arial", Font.BOLD, 30);
		openUrlButton.setFont(f2);
		add(openUrlButton);
		
		openUrlButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				BrowserLauncher launcher;
				try {
					launcher = new BrowserLauncher();
					launcher.openURLinBrowser("http://www.youtube.com/activate");
				} catch (BrowserLaunchingInitializingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedOperatingSystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
			
		
		
	}
	
}
