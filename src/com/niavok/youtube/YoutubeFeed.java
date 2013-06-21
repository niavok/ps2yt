package com.niavok.youtube;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.niavok.podcastscience.PSTrack;
import com.niavok.podcastscience.RessourceLoadingException;

public class YoutubeFeed {

	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	
	Map<String,YoutubeEntry> entryMap = new HashMap<String,YoutubeEntry>();

	
	public static int load(YoutubeFeed feed, String feedXml) {
		System.out.println(feedXml);
		
		try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            Document doc;
            doc = docBuilder.parse(new InputSource(new StringReader(feedXml)));

            Element root = doc.getDocumentElement();

            if (root.getNodeName().contains("feed")) {
            	System.out.println("feed found");
            	return feed.add(root);
            } else {
                throw new RessourceLoadingException("Unknown tag '"+root.getNodeName()+"'for root");
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            throw new RessourceLoadingException("Failed to parse feed ", e);
        } catch (IOException e) {
            throw new RessourceLoadingException("Failed to load feed", e);
        }	
		return 0;
	}
	
	
	public YoutubeFeed() {
	}
	
	public int add(Element element) {
		
		int addCount = 0;
		
		NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // TODO error
                continue;
            }
            Element subElement = (Element) node;
            if (subElement.getNodeName().equals("entry")) {
                YoutubeEntry entry = new YoutubeEntry(subElement);
                if(entry != null) {
                	entryMap.put(entry.getNumber(), entry);
                	addCount++;
                }
            	
            }
        }
	    
        return addCount;
		
	}

	public boolean isExistNumber(String number) {
		return entryMap.containsKey(number);
	}
	
	public YoutubeEntry getUploadUrl(String number) {
		return entryMap.get(number);
	}

	
	
}
