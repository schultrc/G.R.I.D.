package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import org.matsim.api.core.v01.events.LinkEnterEvent;

import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;
import edu.ucdenver.cse.GRID.GRID_UTILS.logWriter;
import edu.ucdenver.cse.GRID.MAP.GRIDmap;

public class GRID_SIM_agentEventHandler implements LinkEnterEventHandler, LinkLeaveEventHandler, PersonArrivalEventHandler,
		PersonDepartureEventHandler {

	// The list of agents we know about
	ConcurrentHashMap<String, GRIDagent> ourAgents;
	
	// The map as we know it
	GRIDmap ourMap;
	
	double totalTravelTime = 0;
	
	Queue<String> agentsToReplan;
	
	// This should NEVER be called
	public Queue<String> getAgentsToReplan() { return agentsToReplan; }

	public void setAgentsToReplan(Queue<String> agentsToReplan) {
		this.agentsToReplan = agentsToReplan;
	}

	public double getTotalTravelTime() { return totalTravelTime; }

	// This should NEVER get called
	public void setTotalTravelTime(double totalTravelTime) {
		this.totalTravelTime = totalTravelTime;
	}

	// This should NEVER get called
	public GRIDmap getOurMap() { return ourMap; }

	public void setOurMap(GRIDmap ourMap) { this.ourMap = ourMap; }

	public ConcurrentMap<String, GRIDagent> getOurAgents() { return ourAgents; }

	public void setOurAgents(ConcurrentHashMap<String, GRIDagent> myAgents) {
		this.ourAgents = myAgents;
	}

	@Override	
	public void reset(int iteration) {
		// Do we care if this ever happens?
		System.out.println("EVENT reset happened. ? ");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handleEvent(LinkEnterEvent event) {
		
		// If an agent enters a link, it will be there for the duration of:
		// length / current speed
		
		double timeToTransit = (ourMap.getRoad(event.getLinkId().toString()).getLength() /
				                ourMap.getRoad(event.getLinkId().toString()).getCurrentSpeed());
        
		//System.out.println("Time to transit link: " + event.getLinkId().toString() +
		//		           " is currently: " + timeToTransit);
		
		// So, from now (sim now) until sim now + timeToTransit, this Agent will be on this link
		// lets add to the weight of this link so we know this road is busier
		
		for (int i = 0; i < timeToTransit; ++i) {
			ourMap.getRoad(event.getLinkId().toString()).addToWeight((long) (event.getTime() + i));
			//System.out.println("adding to link: " + event.getLinkId() +
			//		           " at time: " + (event.getTime() + i) );
					
		}
		// Tell our agent where it is
		ourAgents.get(event.getPersonId().toString()).setLink(event.getLinkId().toString());	
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handleEvent(LinkLeaveEvent event) {

		GRIDagent tempAgent = ourAgents.get(event.getPersonId().toString());
		if (tempAgent != null) {

			// Check if this is OUR agent, and if so, add it to the replanning agents
			if (tempAgent.getSimCalcFlag()) {
				agentsToReplan.add(tempAgent.getId());
			}
			else {
				// We are good to continue our original route
			}				
		}
		else {
			// This is bad, an agent we don't know about just left a link
			System.out.println("ERROR in LinkLeaveEvent: Agent: " + event.getPersonId().toString() +
					           " does not exist in our system!");
			logWriter.log(Level.WARNING, "Agent: " + event.getPersonId().toString() + " just left a link, but we don't know that agent!");
		}
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {

		if( ourAgents.containsKey(event.getPersonId().toString())) {
			GRIDagent tempAgent = ourAgents.get(event.getPersonId().toString());
			
			double departureTime = tempAgent.getDepartureTime();			
			double travelTime = event.getTime() - departureTime;

			//System.out.println("Agent: " + tempAgent.getId() +
			//		           " took: " + travelTime +
			//		           " seconds to arrive at: " +
			//		           event.getLinkId() + " from: " +
			//		           tempAgent.getOrigin()
			//		           );
			
			totalTravelTime += travelTime;
			ourAgents.remove(event.getPersonId().toString());
		}
		
		else {
			System.out.println("ERROR!!! Attempt to remove an agent: " + event.getPersonId() + " that never started!!");
		}
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {	
		
		
		// Is this going to be one of OUR agents?  change the % value to change how many we do. %5 = 20 % of all agents
		boolean simFlag = ((Double.parseDouble(event.getPersonId().toString()) % 2) == 0);
		simFlag = true;
		// Get the destination - future MOD ?
		
		//String theOriginIntersection = ourMap.getRoad(event.getLinkId().toString()).getFrom();
		
		GRIDagent newAgent = new GRIDagent(event.getPersonId().toString(),
										   event.getLinkId().toString(),
				                           event.getLinkId().toString(),
				                           "", simFlag, true );  

		// Here, we need to create a new agent. 
		newAgent.setDepartureTime(event.getTime());			
		ourAgents.put(newAgent.getId(), newAgent);
	}
}



/*System.out.println("Agent: " + event.getPersonId().toString() +
" departed on link: " + event.getLegMode().toString() +
" at time: " + event.getTime() );

for(String theattr:event.getAttributes().keySet() ) {
System.out.println("Attr: " + theattr +
    " has value: " + event.getAttributes().get(theattr));

}*/
