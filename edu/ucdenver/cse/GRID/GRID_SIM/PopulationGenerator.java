package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.ArrayList;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import edu.ucdenver.cse.GRID.GRIDutils;

public class PopulationGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Drivers distribution on road
		double [] hour = {2.33, 2.33, 2.33, 2.33, 2.33, 2.33, 3.68, 6.00, 7.68, 6.68, 5.33, 4.33, 4.00, 3.68, 4.33, 5.68, 7.33, 5.63, 5.00, 4.68, 4.00, 3.33, 2.33, 2.33};
		DriversDistributionOnRoad distribution = new DriversDistributionOnRoad(hour);
		Random rnd = new Random();
		int range = 1;
		int drivers = 1000;
		double [] drivers_on_road_hourly = distribution.calculateDistribution(drivers, range);
		ArrayList<Integer> times = distribution.generateTimes(drivers_on_road_hourly);
		drivers = times.size();
		
		//=========================Smart randomizing location=========================
		ParseNodes pn = new ParseNodes();
		RandomizeLocation rndLoc = new RandomizeLocation(pn);
		String work_area = "./data/PuebloDowntownNodes.txt";
		String home_area = "./data/PuebloNodes.txt";
		ArrayList<StartToDestinationLocation> trips = rndLoc.generateHomeToWorkLocations(work_area, home_area, drivers);
		//=============================================================================
				
		
		//End of drivers distribution on road
				
		Config config = ConfigUtils.createConfig();
		Scenario sc = ScenarioUtils.createScenario(config);
		
		Network network = sc.getNetwork();
		Population population = sc.getPopulation();   
		PopulationFactory populationFactory = population.getFactory();
		
		int idSeed;
		
		for(idSeed = 1; idSeed < drivers; ++idSeed){		
			
			Person person = populationFactory.createPerson(Id.createPersonId(idSeed));
			
			Plan plan = populationFactory.createPlan();
			person.addPlan(plan);
			
			population.addPerson(person);

			// How do we make these reasonable???
						  
			
			//Id<Link> homeLinkId = Id.createLinkId(2);
			//Activity activity1 =
					  //populationFactory.createActivityFromCoord ("h", homeCoordinates);
   				      //populationFactory.createActivityFromLinkId("h", homeLinkId);
						
			
			//==============================Activity based on coordinates==================================
			Coord homeCoordinates = sc.createCoord(trips.get(idSeed).getStartLocation().getX(), trips.get(idSeed).getStartLocation().getY());
			Activity activity1 =  populationFactory.createActivityFromCoord ("h", homeCoordinates);
			
			populationFactory.createActivityFromCoord("h", homeCoordinates);
			activity1.setEndTime(times.get(idSeed)%86400);
			
			plan.addActivity(activity1);
			plan.addLeg(populationFactory.createLeg("car"));
			//=============================================================================================
						
			// Leave at 6 am - how do we change this???
			
//			Id<Link> workLinkId = Id.createLinkId(50);
//			Activity activity2 = populationFactory.createActivityFromLinkId("w", workLinkId);
			
			// Finish work at 4 PM - CHANGE???
			
			
			//==============================Activity based on coordinates==================================
			Coord workCoordinates = sc.createCoord(trips.get(idSeed).getDectinationLocation().getX(), trips.get(idSeed).getDectinationLocation().getY());
			Activity activity2 =  populationFactory.createActivityFromCoord ("h", workCoordinates);
			
			activity2.setEndTime((times.get(idSeed)+distribution.generateRandom(0, 28800, rnd))%86400);
			activity2.getCoord().setXY(trips.get(idSeed).getDectinationLocation().getX(),trips.get(idSeed).getDectinationLocation().getY());
			
			plan.addActivity(activity2);
			plan.addLeg(populationFactory.createLeg("car"));
			//=============================================================================================
			
//			Activity activity3 = populationFactory.createActivityFromLinkId("h", homeLinkId);
//				
//			plan.addActivity(activity3);
//			plan.addLeg(populationFactory.createLeg("car"));
						
		}
		
		MatsimWriter popWriter = new PopulationWriter(population, network);
		
		String popFileName = GRIDutils.chooseFile();
		
		if (popFileName != "") {
			popWriter.write(popFileName);
		}
		
		else {
			System.out.println("\n\nNO OUTPUT FILE SELECTED!!");	
		}
	}

}
