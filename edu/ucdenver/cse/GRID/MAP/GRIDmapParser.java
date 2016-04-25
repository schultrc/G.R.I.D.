package edu.ucdenver.cse.GRID.MAP;

import javax.swing.text.html.parser.Element;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GRIDmapParser extends DefaultHandler  {
	
	GRIDmap theMap = new GRIDmap();
	
	@Override
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
		if(elementName.equalsIgnoreCase("Node")){
			System.out.println("STARTELEMENT: FOUND ELEMENT: " + elementName + " with id: " + attributes.getValue("id") );	
			
			// What if the input is bad???
			GRIDintersection theNode = new GRIDintersection(attributes.getValue("id"), 
															Double.parseDouble(attributes.getValue("x")),
															Double.parseDouble(attributes.getValue("y")));
		
			theMap.addIntersection(theNode);
		}
		else if (elementName.equalsIgnoreCase("Link")) {
			System.out.println("STARTELEMENT: FOUND LINK: " + elementName + " with id: " + attributes.getValue("id") );
			
			// make a new link
		}
		//System.out.println("STARTELEMENT: FOUND ELEMENT: " + elementName );	
	}
	
	@Override
    public void endElement(String s, String s1, String element) throws SAXException {

		//System.out.println("ENDELEMENT: FOUND ELEMENT: " + element);	
	
	}
	
	// Handle the end of document by returning the map?
	@Override
	public void endDocument() throws SAXException {
		
		System.out.println("Found the end of the DOC");
	}
}