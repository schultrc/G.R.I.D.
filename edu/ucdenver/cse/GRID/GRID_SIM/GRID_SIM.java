package edu.ucdenver.cse.GRID.GRID_SIM;

import org.apache.log4j.Logger;
import org.junit.Assert;
//import org.matsim.api.core.*;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

import edu.ucdenver.cse.GRID.*;
import edu.ucdenver.cse.GRID.MAP.GRIDmap;
import edu.ucdenver.cse.GRID.MAP.GRIDmapReader;

public class GRID_SIM {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("It's not hard--you just gotta use finesse!");
		
		GRIDmapReader demoMap = new GRIDmapReader();
		
		String mapFile = GRIDutils.getConfigFile();
		demoMap.readMapFile(mapFile);
		
		
		System.exit(0);
		
		String configFile = GRIDutils.getConfigFile();
	        
	    if (configFile == "") {
	    	System.out.println("You didn't chose a config file!!!");
	    	System.exit(0);
	    }
				
		try {
			Config config = new Config();
			
			ConfigUtils.loadConfig(config, configFile);
			
			config.controler().setLastIteration(0);
			config.controler().setOutputDirectory("./output");
			
			config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

			Scenario scenario = ScenarioUtils.loadScenario(config) ;

			Controler controler = new Controler( scenario ) ;

			controler.run();						
		} 
		
		catch ( Exception ee ) {
			Logger.getLogger("There was an exception: \n" + ee);
			
			// if one catches an exception, then one needs to explicitly fail the test:
			Assert.fail();
		}
		
		System.out.println("\n\n\n\nWell, we got to the end. \n\n\n\n");	
	}
	
	
	
}
