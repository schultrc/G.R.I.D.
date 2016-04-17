package edu.ucdenver.cse.GRID.GRID_SIM;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.*;
import org.matsim.core.scenario.*;

import edu.ucdenver.cse.GRID.GRIDutils;

public class PopulationGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Config config = ConfigUtils.createConfig();
		Scenario sc = ScenarioUtils.createScenario(config);
		
		Network network = sc.getNetwork();
		Population population = sc.getPopulation();   
		PopulationFactory populationFactory = population.getFactory();
		
		long idSeed;
		
		for(idSeed = 1; idSeed < 100; ++idSeed){		
			
			Person person = populationFactory.createPerson(Id.createPersonId(idSeed));
			
			Plan plan = populationFactory.createPlan();
			person.addPlan(plan);
			
			population.addPerson(person);

			// How do we make these reasonable???
						  
			Id<Link> homeLinkId = Id.createLinkId(2);
			
			Activity activity1 = 
					  //populationFactory.createActivityFromCoord ("h", homeCoordinates);
			populationFactory.createActivityFromLinkId("h", homeLinkId);
			
			// Leave at 6 am - how do we change this???
			activity1.setEndTime(21600);
			plan.addActivity(activity1);
			
			plan.addLeg(populationFactory.createLeg("car"));

			Id<Link> workLinkId = Id.createLinkId(50);
					
			Activity activity2 =
					populationFactory.createActivityFromLinkId("w", workLinkId);
			
			// Finish work at 4 PM - CHANGE???
			activity2.setEndTime(57600);
			
			plan.addActivity(activity2);
			plan.addLeg(populationFactory.createLeg("car"));
			
			Activity activity3 = populationFactory.createActivityFromLinkId("h", homeLinkId);
				
			plan.addActivity(activity3);
			plan.addLeg(populationFactory.createLeg("car"));
						
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
