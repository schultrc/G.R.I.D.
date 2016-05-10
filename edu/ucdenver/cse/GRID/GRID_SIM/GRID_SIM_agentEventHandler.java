package edu.ucdenver.cse.GRID.GRID_SIM;

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
	
	// Used for debugging output
	// This doesn't work, btw is always false
	boolean outputFlag = false;

	// The list of agents we know about
	ConcurrentHashMap<String, GRIDagent> theAgents;
	
	// The map as we know it
	GRIDmap ourMap;
	
	double totalTravelTime = 0;
	
	public double getTotalTravelTime() {
		return totalTravelTime;
	}

	public void setTotalTravelTime(double totalTravelTime) {
		this.totalTravelTime = totalTravelTime;
	}

	// This should NEVER get called
	public GRIDmap getOurMap() {
		return ourMap;
	}

	public void setOurMap(GRIDmap ourMap) {
		this.ourMap = ourMap;
	}

	public ConcurrentMap<String, GRIDagent> getMyAgents() {
		return theAgents;
	}

	public void setTheAgents(ConcurrentHashMap<String, GRIDagent> myAgents) {
		this.theAgents = myAgents;
	}

	// THIS SHOULD NEVER GET CALLED
	public GRID_SIM_agentEventHandler(boolean printFlag) {
		this.outputFlag = printFlag;
	}

	@Override	
	public void reset(int iteration) {
		if (outputFlag) {
			System.out.println("reset...");
		}
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
		

		if (outputFlag) {
			System.out.println("LinkEnterEvent");
			System.out.println("Time: " + event.getTime());
			System.out.println("LinkId: " + event.getLinkId());
		}
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {

		// This is probably where we want to go get an updated route
		// so, go get your route young agent!
		if (outputFlag) {
			System.out.println("LinkLeaveEvent");
			System.out.println("Time: " + event.getTime());
			System.out.println("LinkId: " + event.getLinkId());
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
			
			System.out.println("totalTravelTime increased to: " + totalTravelTime);
		}
		
		else {
			System.out.println("ERROR!!! Attempt to remove an agent: " + event.getPersonId() + " that never started!!");
		}
		
		// Time? Do we want to track time here?
		if (outputFlag) {
			System.out.println("AgentArrivalEvent");
			System.out.println("Time: " + event.getTime());
			System.out.println("LinkId: " + event.getLinkId());
			System.out.println("PersonId: " + event.getPersonId());
		}
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		
		// If we want to track time above, we probably need to record the start time here
		
		// Here, we need to create a new agent. 
		
		/*System.out.println("Agent: " + event.getPersonId().toString() +
				           " departed on link: " + event.getLegMode().toString() +
				           " at time: " + event.getTime() );
			
		for(String theattr:event.getAttributes().keySet() ) {
			System.out.println("Attr: " + theattr +
					           " has value: " + event.getAttributes().get(theattr));
			           
		}*/

		GRIDagent newAgent = new GRIDagent(event.getPersonId().toString(),
				                           event.getLinkId().toString(),
				                           event.getLinkId().toString(),
				                           "");  
		newAgent.setDepartureTime(event.getTime());		                           
		System.out.println("Adding Agent: " + newAgent.getId() + " at time: " + event.getTime());
		
		theAgents.put(newAgent.getId(), newAgent);
		
		System.out.println("There are: " + theAgents.size() + " agents now");
				
		if (outputFlag) {
			System.out.println("AgentDepartureEvent");
			System.out.println("Time: " + event.getTime());
			System.out.println("LinkId: " + event.getLinkId());
			System.out.println("PersonId: " + event.getPersonId());
		}
	}
}

