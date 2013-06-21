package com.niavok;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.niavok.ui.MainFrame;

public class Main {

	public static void main(String[] arguments) {
		
		createCacheDir();
		
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
		
	}

	private static void createCacheDir() {
		File dir = new File("cache");
		dir.mkdirs();
	}


	
}
