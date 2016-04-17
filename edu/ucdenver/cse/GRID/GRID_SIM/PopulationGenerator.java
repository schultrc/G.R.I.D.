package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.ArrayList;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
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

public class PopulationGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Drivers distribution on road
		double [] hour = {2.33, 2.33, 2.33, 2.33, 2.33, 2.33, 3.68, 6.00, 7.68, 6.68, 5.33, 4.33, 4.00, 3.68, 4.33, 5.68, 7.33, 5.63, 5.00, 4.68, 4.00, 3.33, 2.33, 2.33};
		DriversDistributionOnRoad distribution = new DriversDistributionOnRoad(hour);
		Random rnd = new Random();
		int range = 1;
		int drivers = 100;
		double [] drivers_on_road_hourly = distribution.calculateDistribution(drivers, range);
		ArrayList<Integer> times = distribution.generateTimes(drivers_on_road_hourly);
		drivers = times.size();
		
		
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
//			Coord homeCoordinates = sc.createCoord(-1.1644353007143369E7, 4616838.252581435);
			Coord homeCoordinates = sc.createCoord(0, 0);
			Activity activity1 =  populationFactory.createActivityFromCoord ("h", homeCoordinates);
			  
			//Id<Link> curLinkId = createLinkId(idSeed);
			
			 // populationFactory.createActivityFromLinkId("h", Id<Link>(i));
			populationFactory.createActivityFromLinkId("h", curLinkId);
			
			// Leave at 6 am - how do we change this???
			activity1.setEndTime(times.get(idSeed));
			plan.addActivity(activity1);
			
			plan.addLeg(populationFactory.createLeg("car"));

//			Coord workCoordinates = sc.createCoord(-1.1646500805398736E7, 4623084.819612819);
			Coord workCoordinates = sc.createCoord(100000, 100000);

			Activity activity2 =
					  populationFactory.createActivityFromCoord("w",workCoordinates);
			
			// Finish work at 4 PM - CHANGE???
			activity2.setEndTime(times.get(idSeed)+distribution.generateRandom(0, 28800, rnd));
			
			plan.addActivity(activity2);
			plan.addLeg(populationFactory.createLeg("car"));
			
			Activity activity3 =
					  populationFactory.createActivityFromCoord("h",homeCoordinates);
					
			plan.addActivity(activity3);
			plan.addLeg(populationFactory.createLeg("car"));
			
			
		}
		
		MatsimWriter popWriter = new PopulationWriter(population, network);
		popWriter.write("./data/SmallPopulation.xml");

	}

}
