package edu.ucdenver.cse.GRID.GRID_SIM;

import org.apache.log4j.Logger;
import org.junit.Assert;
//import org.matsim.api.core.*;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;


public class GRID_SIM {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("It's not hard--you just gotta use finesse!");
		
		try {
			Config config = new Config();
			
			ConfigUtils.loadConfig(config, args[0]);
			
			config.controler().setLastIteration(1);
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


		// Here is where we would run our test algorithms
		
	}
	
	


}
