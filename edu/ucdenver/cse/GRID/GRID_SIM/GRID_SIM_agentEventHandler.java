package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.matsim.api.core.v01.events.LinkEnterEvent;

import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.core.mobsim.framework.events.*;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;
import edu.ucdenver.cse.GRID.MAP.GRIDmap;

public class GRID_SIM_agentEventHandler implements LinkEnterEventHandler, LinkLeaveEventHandler, PersonArrivalEventHandler,
		PersonDepartureEventHandler {

	// The list of agents we know about
	ConcurrentHashMap<String, GRIDagent> theAgents;
	
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

	public ConcurrentMap<String, GRIDagent> getMyAgents() { return theAgents; }

	public void setTheAgents(ConcurrentHashMap<String, GRIDagent> myAgents) {
		this.theAgents = myAgents;
	}

	@Override	
	public void reset(int iteration) {
		// Do we care if this ever happens?
		System.out.println("EVENT reset happened. ? ");
	}

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
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handleEvent(LinkLeaveEvent event) {
		
		
		// Somehow here we need to decide which agents get replanned
		
		GRIDagent tempAgent = theAgents.get(event.getPersonId().toString());
		if (tempAgent != null) {
			// We found the agent, see if it's route has changed
			if (tempAgent.getRouteHasChanged()) {
				// the agent's route has changed, so lets tell the sim handler to update this agent
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
		}
		
		if(tempAgent.getId().equals("1")) {
			System.out.println("Person 1 left link: " + event.getLinkId().toString() + 
					           " at time: " + event.getTime() ); 
			
			// Force this onto the list for testing
			agentsToReplan.add("1");
			
		}
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		
		//System.out.println("attempting to remove agent: " + event.getPersonId().toString());
		if( theAgents.containsKey(event.getPersonId().toString())) {
			GRIDagent tempAgent = theAgents.get(event.getPersonId().toString());
			
			double departureTime = tempAgent.getDepartureTime();
			
			double travelTime = event.getTime() - departureTime;
			//System.out.println("Agent: " + tempAgent.getId() +
			//		           " took: " + travelTime +
			//		           " seconds to arrive at: " +
			//		           event.getLinkId() + " from: " +
			//		           tempAgent.getOrigin()
			//		           );
			
			totalTravelTime += travelTime;
			theAgents.remove(event.getPersonId().toString());
		}
		
		else {
			System.out.println("ERROR!!! Attempt to remove an agent: " + event.getPersonId() + " that never started!!");
		}
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {	
		
		// Here, we need to create a new agent. 
		GRIDagent newAgent = new GRIDagent(event.getPersonId().toString(),
				                           event.getLinkId().toString(),
				                           event.getLinkId().toString(),
				                           "");  
		
		newAgent.setDepartureTime(event.getTime());		                           
		
		//System.out.println("Adding Agent: " + newAgent.getId() + " at time: " + event.getTime());
		
		theAgents.put(newAgent.getId(), newAgent);
		
		//System.out.println("There are: " + theAgents.size() + " agents now");
				
	}
}



/*System.out.println("Agent: " + event.getPersonId().toString() +
" departed on link: " + event.getLegMode().toString() +
" at time: " + event.getTime() );

for(String theattr:event.getAttributes().keySet() ) {
System.out.println("Attr: " + theattr +
    " has value: " + event.getAttributes().get(theattr));

}*/
