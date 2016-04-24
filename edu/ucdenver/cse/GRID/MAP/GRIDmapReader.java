package edu.ucdenver.cse.GRID.MAP;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;
import java.io.File;

import edu.ucdenver.cse.GRID.GRIDutils;

import java.util.*;
import java.io.*;

public class GRIDmapReader {

	public GRIDmap readMapFile(String mapFile){
		GRIDmap returnMap = new GRIDmap();
		
		if (mapFile == "") {
			mapFile = GRIDutils.getConfigFile(); 
		}
		
		try {	
	         //File inputFile = new File(mapFile);
	         
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         
	         XMLReader xmlreader = saxParser.getXMLReader();
	         
	         
	         GRIDmapParser GMP = new GRIDmapParser();
	         xmlreader.setContentHandler(GMP);
	      
	         xmlreader.parse(new InputSource(mapFile));
	         
	         //xmlreader.parse(inputFile.toURI());
	             
	         
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		
		
		// If we get here, everything is good. Read all the Roads and intersections
		
		
		// Get the file 
		
		return returnMap;
	}
	

}
