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
	         
	         setDefaultWeights(theMap);
	         
	         return theMap;
	    } 
		catch (Exception e) {
	         e.printStackTrace();
	         
		 return null;
	    }
	}
	
	private boolean setDefaultWeights(GRIDmap theMap) {
		// This eventually should read in real world estimates
		
		int maxSecInDay = 86400;
		
		for(String roadID:theMap.getRoads().keySet()) {
			
			GRIDroad curRoad = theMap.getRoad(roadID);
			
			// Calc the default weight
			
			double theWeight = curRoad.getLength() / curRoad.getMaxSpeed();
			
			for(long i =0; i <maxSecInDay; ++i) {
				if (curRoad.setWeightAtTime(i, theWeight)) {
					//System.out.println("adding weight: " + theWeight +
					//		           " to road: " + curRoad.getId());	

				}
				else
				{
					
				}
			}
		}
		
		
		
		return true;
	}
}
