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
	
	Map<String,String> urlMap = new HashMap<String,String>();
	
	public YoutubeFeed(String feedXml) {
		
		System.out.println(feedXml);
	
		try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            Document doc;
            doc = docBuilder.parse(new InputSource(new StringReader(feedXml)));

            Element root = doc.getDocumentElement();

            if (root.getNodeName().contains("feed")) {
            	System.out.println("feed found");
                parseFeed(root);
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
	    
		
	}

	public boolean isExistNumber(String number) {
		return urlMap.containsKey(number);
	}

	private void parseFeed(Element element) {
		NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // TODO error
                continue;
            }
            Element subElement = (Element) node;
            if (subElement.getNodeName().equals("entry")) {
                parseEntry(subElement);
            }
        }
        
	}
	
	private void parseEntry(Element element) {
		
		String title = null;
		String url = "";
		
		NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // TODO error
                continue;
            }
            Element subElement = (Element) node;
            if (subElement.getNodeName().equals("title")) {
            	
            	String textContent = subElement.getTextContent();
            	String[] split = textContent.split("n°");
            	if(split.length == 2) {
            		title = split[1];
                    
                    System.out.println("find n°"+split[1]);
            	}
            } else if (subElement.getNodeName().equals("link")) {
            	if(subElement.getAttribute("rel").equals("alternate")) {
            		url = subElement.getAttribute("href");
            	}
            }
            
            
        }
        
        if(title != null) {
        	urlMap.put(title, url);
        }
		
	}

	public String getUploadUrl(String number) {
		return urlMap.get(number);
	}
	
}
