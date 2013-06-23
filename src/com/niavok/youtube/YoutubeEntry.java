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
package com.niavok.youtube;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.niavok.podcast.Podcast;
import com.niavok.podcast.RessourceLoadingException;

public class YoutubeEntry {

	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private String url;
	private String title;
	
	
	public YoutubeEntry(Element element) {
		
		NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // TODO error
                continue;
            }
            Element subElement = (Element) node;
            if (subElement.getNodeName().equals("title")) {
            	
            	title = subElement.getTextContent();
            	
            } else if (subElement.getNodeName().equals("link")) {
            	if(subElement.getAttribute("rel").equals("alternate")) {
            		url = subElement.getAttribute("href");
            	}
            }
            
            
        }
	}

	public static YoutubeEntry load(String entryXml) {
		System.out.println(entryXml);
		
		try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            Document doc;
            doc = docBuilder.parse(new InputSource(new StringReader(entryXml)));

            Element root = doc.getDocumentElement();

            if (root.getNodeName().contains("entry")) {
            	System.out.println("entry found");
                return new YoutubeEntry(root);
            } else {
                throw new RessourceLoadingException("Unknown tag '"+root.getNodeName()+"'for root");
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
        	 e.printStackTrace();
        } catch (IOException e) {
        	 e.printStackTrace();
        }	
		return null;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public String getNumber(Podcast podcast) {
		Pattern youtubeNumberRegex = podcast.getYoutubeNumberRegex();
		int youtubeNumberRegexIndex = podcast.getYoutubeNumberRegexIndex();
		
		Matcher numberMatcher = youtubeNumberRegex.matcher(title);
		
		if(numberMatcher.find()) {
			String number = numberMatcher.group(youtubeNumberRegexIndex);
			return number;
		}
		return null;
	}
	
}
