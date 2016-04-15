package edu.ucdenver.cse.GRID.MAP;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;

public class GRIDmapReader {

	public GRIDmap readMapFile(String mapFile){
		GRIDmap returnMap = new GRIDmap();
		
		
		File fXmlFile = new File("/Users/mkyong/staff.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			
			try {
				Document doc = dBuilder.parse(fXmlFile);
			} catch (SAXException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			} catch (IOException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// If we get here, everything is good. Read all the Roads and intersections
		
		
		// Get the file 
		
		return returnMap;
	}
}
