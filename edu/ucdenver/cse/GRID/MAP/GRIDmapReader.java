package edu.ucdenver.cse.GRID.MAP;

import javax.xml.parsers.*;
import org.xml.sax.*;
import javax.xml.parsers.SAXParserFactory;

import edu.ucdenver.cse.GRID.GRIDutils;

public class GRIDmapReader {

	public GRIDmap readMapFile(String mapFile){
			
		if (mapFile == "") {
			mapFile = GRIDutils.getConfigFile(); 
		}
		
		try {	
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         
	         XMLReader xmlreader = saxParser.getXMLReader();
	         	         
	         GRIDmapParser GMP = new GRIDmapParser();
	         xmlreader.setContentHandler(GMP);
	      
	         xmlreader.parse(new InputSource(mapFile));	             
	         
	         GRIDmap theMap = GMP.getMap();
	         
	         return theMap;
	    } 
		catch (Exception e) {
	         e.printStackTrace();
	         
		 return null;
	    }
	}		
	return returnMap;
}
