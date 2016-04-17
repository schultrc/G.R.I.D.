package edu.ucdenver.cse.GRID.GRID_SIM;


import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.api.core.v01.network.*;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.*;
import org.matsim.core.scenario.*;

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
//			Coord homeCoordinates = sc.createCoord(-1.1644353007143369E7, 4616838.252581435);
			Coord homeCoordinates = sc.createCoord(0, 0);
			Activity activity1 = 
			  populationFactory.createActivityFromCoord ("h", homeCoordinates);
			  
			Id<Link> curLinkId = createLinkId(idSeed);
			
			populationFactory.createActivityFromLinkId("h", curLinkId);
			
			// Leave at 6 am - how do we change this???
			activity1.setEndTime(21600);
			plan.addActivity(activity1);
			
			plan.addLeg(populationFactory.createLeg("car"));

//			Coord workCoordinates = sc.createCoord(-1.1646500805398736E7, 4623084.819612819);
			Coord workCoordinates = sc.createCoord(100000, 100000);

			Activity activity2 =
					  populationFactory.createActivityFromCoord("w",workCoordinates);
			
			// Finish work at 4 PM - CHANGE???
			activity2.setEndTime(57600);
			
			plan.addActivity(activity2);
			plan.addLeg(populationFactory.createLeg("car"));
			
			Activity activity3 =
					  populationFactory.createActivityFromCoord("h",homeCoordinates);
					
			plan.addActivity(activity3);
			plan.addLeg(populationFactory.createLeg("car"));
			
			
		}
		
		MatsimWriter popWriter = new PopulationWriter(population, network);
		popWriter.write(./data/SmallPopulation.xml);

	}

}
