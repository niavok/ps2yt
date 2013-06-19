package com.niavok.podcastscience;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PSManager {
	
	private static List<PSTrack> cachedTrackList;
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	
	
	public static List<PSTrack> getTracks() {
		
		if(cachedTrackList == null) {
			generateList();
		}
		return cachedTrackList;
	}
		
		
	private static void generateList() {

		cachedTrackList = new ArrayList<PSTrack>();

		String url = "http://feeds.feedburner.com/PodcastScience";
		URL website;
		try {
			website = new URL(url);
		} catch (MalformedURLException e) {
			throw new RessourceLoadingException("Malformed url '" + url, e);
		}
	    
	    
	    try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            Document doc;
            doc = docBuilder.parse(website.openStream());

            Element root = doc.getDocumentElement();

            if (root.getNodeName().contains("rss")) {
            	System.out.println("rss");
                parseRss(root);
            } else {
                throw new RessourceLoadingException("Unknown tag '"+root.getNodeName()+"'for root");
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            throw new RessourceLoadingException("Failed to parse url '" + url, e);
        } catch (IOException e) {
            throw new RessourceLoadingException("Failed to load url '" + url, e);
        }
	    
	}


	private static void parseRss(Element element) {
		NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // TODO error
                continue;
            }
            Element subElement = (Element) node;
            if (subElement.getNodeName().equals("channel")) {
                parseChannel(subElement);
            } else {
                throw new RessourceLoadingException("Unknown tag '"+subElement.getNodeName()+"'for element '"+element.getNodeName()+"'");
            }
        }
        
	}


	private static void parseChannel(Element element) {
		NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // TODO error
                continue;
            }
            Element subElement = (Element) node;
            if (subElement.getNodeName().equals("item")) {
                parseItem(subElement);
            }
        }
		
	}


	private static void parseItem(Element element) {
		PSTrack track = new PSTrack();
		cachedTrackList.add(track);
		
		NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // TODO error
                continue;
            }
            Element subElement = (Element) node;
            if (subElement.getNodeName().equals("title")) {
                track.setTitle(subElement.getTextContent());
            } else if (subElement.getNodeName().equals("media:content")) {
                track.setAudioUrl(subElement.getAttribute("url"));
            } else if (subElement.getNodeName().equals("image")) {
            	track.setImageUrl(subElement.getElementsByTagName("url").item(0).getTextContent());
            }
            
            
        }
		
	}
}
