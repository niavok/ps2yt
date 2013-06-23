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

package com.niavok.podcast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.niavok.Config;

public class PodcastManager {

	private static List<Podcast> podcastList = new ArrayList<Podcast>();
	private static List<String> podcastNameList = new ArrayList<String>();
	
	static {
		File configDir = new File(Config.getJarPath(), "config");
		
		File[] files = configDir.listFiles();
		Arrays.sort(files);
		int index = 0;
		for(File file: files) {
			parsePodcastFile(index, file);
			index ++;
		}
		
	}
	
	public static String[] getPodcastNames() {
		return podcastNameList.toArray(new String[podcastNameList.size()]);
	}

	private static void parsePodcastFile(int index, File file) {
		
		
		Properties podcastFile = new Properties();
		try {
			podcastFile.load(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			
			String podcastName = podcastFile.getProperty("PODCAST_NAME");
			String podcastUrl = podcastFile.getProperty("PODCAST_URL");
			String podcastInputTitleRegex = podcastFile.getProperty("INPUT_TITLE_REGEX");
			int podcastInputTitleRegexIndex = Integer.parseInt(podcastFile.getProperty("INPUT_TITLE_REGEX_INDEX"));
			String podcastInputNumberRegex = podcastFile.getProperty("INPUT_NUMBER_REGEX");
			int podcastInputNumberRegexIndex = Integer.parseInt(podcastFile.getProperty("INPUT_NUMBER_REGEX_INDEX"));
			String youtubeOutputTitleFormat = podcastFile.getProperty("YT_OUTPUT_TITLE_FORMAT");
			String youtubeNumberRegex = podcastFile.getProperty("YT_NUMBER_REGEX");
			int youtubeNumberRegexIndex = Integer.parseInt(podcastFile.getProperty("YT_NUMBER_REGEX_INDEX"));
			String youtubeOutputDescriptionFormat = podcastFile.getProperty("YT_OUTPUT_DESCRIPTION_FORMAT");

			
			
			podcastNameList.add(podcastName);
			podcastList.add(new Podcast(index,
					podcastName,
					podcastUrl,
					podcastInputTitleRegex,
					podcastInputTitleRegexIndex,
					podcastInputNumberRegex,
					podcastInputNumberRegexIndex,
					youtubeOutputTitleFormat,
					youtubeNumberRegex,
					youtubeNumberRegexIndex,
					youtubeOutputDescriptionFormat));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static Podcast getPodcast(int index) {
		return podcastList.get(index);
	}
	
	
		
		
	
}
