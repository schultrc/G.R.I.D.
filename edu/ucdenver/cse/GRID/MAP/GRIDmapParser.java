package edu.ucdenver.cse.GRID.MAP;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GRIDmapParser extends DefaultHandler  {
	
	@Override
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
		System.out.println("STARTELEMENT: FOUND ELEMENT: " + elementName);	
	}
	
	@Override
    public void endElement(String s, String s1, String element) throws SAXException {

		System.out.println("ENDELEMENT: FOUND ELEMENT: " + element);	
	
	}
}