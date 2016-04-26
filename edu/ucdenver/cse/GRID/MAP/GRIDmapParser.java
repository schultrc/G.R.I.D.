package edu.ucdenver.cse.GRID.MAP;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GRIDmapParser extends DefaultHandler  {
	
	GRIDmap theMap = new GRIDmap();
	
	public GRIDmap getMap() {
		return theMap;
	}
	
	@Override
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
		if(elementName.equalsIgnoreCase("Node")){
			
			// What if the input is bad???
			GRIDintersection theNode = new GRIDintersection(attributes.getValue("id"), 
															Double.parseDouble(attributes.getValue("x")),
															Double.parseDouble(attributes.getValue("y")));
		
			theMap.addIntersection(theNode);
		}
		else if (elementName.equalsIgnoreCase("Link")) {
		
			GRIDroad theRoad = new GRIDroad(attributes.getValue("id"));
			
			theRoad.setFrom(attributes.getValue("from"));
			theRoad.setTo(attributes.getValue("to"));
			
			theMap.addRoad(theRoad);
		}
	}
	
	@Override
    public void endElement(String s, String s1, String element) throws SAXException {

	}
	
	// Handle the end of document by returning the map?
	@Override
	public void endDocument() throws SAXException {
		
	}
}