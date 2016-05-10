package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.junit.Assert;
//import org.matsim.api.core.*;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.withinday.trafficmonitoring.TravelTimeCollector;
import org.matsim.core.controler.AbstractModule;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.QSimUtils;
import org.matsim.core.router.TripRouter;
import org.matsim.core.router.TripRouterProviderImpl;
import org.matsim.withinday.trafficmonitoring.TravelTimeCollector;

import com.google.inject.Provider;

import edu.ucdenver.cse.GRID.*;
import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;
import edu.ucdenver.cse.GRID.MAP.GRIDmap;
import edu.ucdenver.cse.GRID.MAP.GRIDmapReader;
import edu.ucdenver.cse.GRID.MAP.GRIDroute;

public class GRID_SIM {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/* This is the primary interface between the GRID algorithm and MATSIM
		 * It will create the controller for matsim as well as the MAP and other
		 * data sources needed for GRID.
		 * 
		 */
				
		System.out.println("It's not hard--you just gotta use finesse!");
				
		// This will get filled and emptied via departure / arrival events
		final ConcurrentHashMap<String, GRIDagent> masterAgents = new ConcurrentHashMap<String, GRIDagent> ();	
		
		double totalTravelTime = 0;
		
		// The official map
		final GRIDmap ourMap;
		
		// Load our version of the map first
		GRIDmapReader masterMap = new GRIDmapReader();
				
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

			final Controler controler = new Controler( scenario ) ;
			
			// Uncomment if you want to select a different map than matsim is using
			// String mapFile = GRIDutils.getConfigFile();
			String mapFile = config.network().getInputFile();

			System.out.println("File Chosen: " + mapFile);
			ourMap = masterMap.readMapFile(mapFile);
			

			// From WithinDayReplanning
			Set<String> analyzedModes = new HashSet<String>();
			analyzedModes.add(TransportMode.car);
			final TravelTimeCollector travelTime = new TravelTimeCollector(controler.getScenario(), analyzedModes);
			controler.getEvents().addHandler(travelTime);
			// end WithinDayReplanning
			
			// Add our handler for Link Events			
			GRID_SIM_agentEventHandler theAgentHandler = new GRID_SIM_agentEventHandler(false);
			theAgentHandler.setOurMap(ourMap);
			theAgentHandler.setTheAgents(masterAgents);
			
			controler.getEvents().addHandler(theAgentHandler);
			
			
			// Add listeners for the sim steps
			controler.addOverridingModule(new AbstractModule() {
				
				@Override
			    public void install() {
					
					this.bindMobsim().toProvider(new Provider<Mobsim>() {
						public Mobsim get() {
							// construct necessary trip router:
							TripRouter router = new TripRouterProviderImpl(controler.getScenario(),
									controler.getTravelDisutilityFactory(), travelTime,
									controler.getLeastCostPathCalculatorFactory(), controler.getTransitRouterFactory()).get();

							// construct qsim and insert listeners:
							QSim qSim = QSimUtils.createDefaultQSim(controler.getScenario(), controler.getEvents());
							
							GRID_SIM_matsimEventHandler theSimHandler = new GRID_SIM_matsimEventHandler(router);
							
							// add the map to the handler
							theSimHandler.setTheMap(ourMap);
							// add the agents to the handler
							theSimHandler.setTheAgents(masterAgents);
							// Add the listener for Sim Step End 
							qSim.addQueueSimulationListeners(theSimHandler);
							
							qSim.addQueueSimulationListeners(travelTime);
							return qSim;
						}
					});
				}
			});
			
			// Everything is set up, let's run this thing
			controler.run();
			
			//System.out.println("Total travel time was: " + theAgentHandler.getTotalTravelTime());
			totalTravelTime = theAgentHandler.getTotalTravelTime();
		}
		
		catch ( Exception ee ) {
			Logger.getLogger("There was an exception: \n" + ee);
			
			// if one catches an exception, then one needs to explicitly fail the test:
			Assert.fail();
		}
		
		System.out.println("\n\nTotal travel time was: " + totalTravelTime + " seconds");
		
		System.out.println("\n\nWell, we got to the end. \n\n\n\n");	
	}
	
	
	
}
