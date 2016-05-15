package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Route;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.mobsim.framework.HasPerson;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener;
import org.matsim.core.mobsim.qsim.agents.WithinDayAgentUtils;
import org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.mobsim.qsim.qnetsimengine.NetsimLink;
import org.matsim.core.mobsim.qsim.qnetsimengine.NetsimNetwork;
import org.matsim.core.population.PopulationFactoryImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.router.TripRouter;
import org.matsim.withinday.utils.EditRoutes;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;
import edu.ucdenver.cse.GRID.MAP.GRIDmap;
import edu.ucdenver.cse.GRID.MAP.GRIDroad;
import edu.ucdenver.cse.GRID.MAP.GRIDroute;
import edu.ucdenver.cse.GRID.MAP.GRIDselfishAlg;
import edu.ucdenver.cse.GRID.MAP.GRIDpathrecalc;

public class GRID_SIM_matsimEventHandler implements MobsimBeforeSimStepListener, MobsimAfterSimStepListener {

	GRIDmap theMap;
	
	ConcurrentHashMap<String, GRIDagent> theAgents;
	Queue<String> agentsToReplan;
	
	// Should NEVER be called
	public Queue<String> getAgentsToReplan() { return agentsToReplan; }

	public void setAgentsToReplan(Queue<String> agentsToReplan) { 
		this.agentsToReplan = agentsToReplan;
	}

	// This should NEVER get called
	public GRIDmap getTheMap() { return theMap; }

	public void setTheMap(GRIDmap theMap) { this.theMap = theMap; }

	// This should NEVER get called
	public ConcurrentMap<String, GRIDagent> getTheAgents() { return theAgents; }

	public void setTheAgents(ConcurrentHashMap<String, GRIDagent> theAgents) {
		this.theAgents = theAgents;
	}

	// How do we use this? Can we make our own?
	private static final Logger log = Logger.getLogger("dummy");
	
	private TripRouter tripRouter;
	private Scenario scenario;

	GRID_SIM_matsimEventHandler(TripRouter tripRouter) {
		this.tripRouter = tripRouter;
	}

	@Override
	public void notifyMobsimBeforeSimStep(@SuppressWarnings("rawtypes") MobsimBeforeSimStepEvent event) {
	
		final Logger GRIDLog = Logger.getLogger("GRIDlogger");
		Netsim mobsim = (Netsim) event.getQueueSimulation() ;
		
		ConcurrentHashMap<String, MobsimAgent>  mobsimAgents = getAgentsToReplan(mobsim, theAgents);
		
		// Consider changing this to the same model as replanning, where agents get added to
		// a list, instead of checking all of them every time
		
		// Need to update any agent that doesn't have a destination
		for(GRIDagent theGridAgent : theAgents.values() ) {
			if (theGridAgent.getNeedsDestination()) {
								
				theGridAgent.setDestination(mobsimAgents.get(theGridAgent.getId()).getDestinationLinkId().toString());
				theGridAgent.setNeedsDestinationFlag(false);
			}				
		}
		
		// Map updates - Do we need anything else from the matsim map?
		if (event.getSimulationTime() % 5 == 0) {
			
			NetsimNetwork thesimNetwork = mobsim.getNetsimNetwork();
			
			Iterator<? extends Link> iter = thesimNetwork.getNetwork().getLinks().values().iterator();
			
			while (iter.hasNext()) {
				Link tempLink = iter.next();
				theMap.getRoad(tempLink.getId().toString() ).setCurrentSpeed(tempLink.getFreespeed());
			}
			
			// We should remove any info in the road.vehiclesCurrentlyOnRoad at time - 1
		}
		
		// Agent route updates  - every time
		while (!agentsToReplan.isEmpty() ) {
						
			// We can change this by sorting the list prior to removing
			GRIDagent tempAgent = theAgents.get(agentsToReplan.remove());
			
			if (tempAgent != null) {
				//System.out.println("Found Agent to replan: " + tempAgent.getId());
				if(mobsimAgents.containsKey(tempAgent.getId()))
				{
					//System.out.println("Replacing the route for agent: " + tempAgent.getId());
					doReplanning(mobsimAgents.get(tempAgent.getId()), mobsim, tempAgent.getCurrentLink());
				}
			}						
		}
		
		// RCS Is this working, but sending it to the console?????
		//GRIDLog.info("notifyMobsimBeforeSimStep " + event.toString() + " " + event.getSimulationTime() );
	}
	
	private boolean doReplanning(MobsimAgent agent, Netsim mobsim, String currentLinkId ) {
		
		double now = mobsim.getSimTimer().getTimeOfDay();
		
		GRIDagent tempGRIDagent = theAgents.get(agent.getId().toString());
		if (tempGRIDagent.getCurrentLink().equals(tempGRIDagent.getDestination()) ) {
			// We must already be at our destination!
			System.out.println("Agent: " + agent.getId().toString() + " has arrived at its destination");
			return false;
		}
			
		// Subtract 0.5 so round always goes to the current second
		Long timeNow = Math.round(now - 0.5);
		
		Plan plan = WithinDayAgentUtils.getModifiablePlan(agent);

		if (plan == null) {
			log.info(" we don't have a modifiable plan; returning ... ");
			return false;
		}
		
		if (!(WithinDayAgentUtils.getCurrentPlanElement(agent) instanceof Leg)) {
			log.info("agent not on leg; returning ... ");
			return false;
		}
		
		if (!((Leg) WithinDayAgentUtils.getCurrentPlanElement(agent)).getMode().equals(TransportMode.car)) {
			log.info("not a car leg; can only replan car legs; returning ... ");
			return false;
		}
				
		Leg currentLeg = (Leg)  WithinDayAgentUtils.getCurrentPlanElement(agent);
				
		// Is there a better way to get a Network Route???
	    NetworkRoute netRoute =  (NetworkRoute) currentLeg.getRoute().clone();
	        
	    // Keep the original so we can determine if it has changed
	    GRIDroute origRoute = tempGRIDagent.getRoute(); 
	    
	    //System.out.println("The agent is: " + tempGRIDagent.toString());
	    tempGRIDagent.setLink(agent.getCurrentLinkId().toString());
	    
	    GRIDpathrecalc theALG = new GRIDpathrecalc(tempGRIDagent, theMap, timeNow);
	    	 
	    // Recalculate the route from here to destination
	    Long startTime = System.currentTimeMillis();	    
	    
	    GRIDroute tempRoute = theALG.findPath();
	    
	    if (tempRoute == null) { return false; }
	    
	    //System.out.println("New route for agent: " + tempGRIDagent.getId());
	    //System.out.println(tempRoute.toString());
	    
	    Long stopTime = System.currentTimeMillis();	    	    
	    Long totalTime = stopTime - startTime;
	    
	    //System.out.println("Recalculating route for agent: " + tempGRIDagent.getId() +
	    //		           " took: " + (totalTime/1000) + "seconds" ); 
	    	    
	    //Compare the 2 routes    
	    if (origRoute.equalsIntersections(tempRoute)) {
	    
	    	// This doesn't work, it never gets here. 
	    	// If it does, we don't have to make any map updates
	    	// I don't think we are setting the new routes into the agent, so the new is ALWAYS diff from orig
	    	System.out.println("Routes did not change for agent: " + agent.getId());
	    	return false;
	    }
	    
	    else
	    {
	    	// If the routes were different, need to update the map, both add and remove
	    	tempRoute.setRoads(theMap.getPathByRoad(tempRoute.getIntersections()));
	    	tempGRIDagent.setRoute(tempRoute);
	    	
	    	ArrayList<String> theRoute = tempRoute.getRoads();
	    	List<Id<Link>> mobsimLinks = new ArrayList<Id<Link>>();
	    	
	    	int currentLinkIndex = WithinDayAgentUtils.getCurrentRouteLinkIdIndex(agent);
	    	
	    	//System.out.println("Agent Route Index: " + currentLinkIndex);
	    		    	
	    	// This is Kludgy, but hey
	    	// matsim keeps an internal index into it's route that we cannot change
	    	// fill the array with dummy entries
	    	for (int i = 0; i < currentLinkIndex; i++) {
	    		mobsimLinks.add(agent.getCurrentLinkId());
	    	}
	    	  	
	    	for(String ourRoad:theRoute) {
	    		// Add the road to the list for mobsim
	    		mobsimLinks.add(Id.createLinkId(ourRoad));
	    	}
	    	//System.out.println("\n\n\nAgent " + agent.getId().toString() + " start: " + tempGRIDagent.getCurrentLink() + 
	    	//		           " destination is: " + tempGRIDagent.getDestination());
	    	
	    	//System.out.print("Mobsim links are: ");
	    	//for(Id<Link> mobsimlink:mobsimLinks) {
	    	//	System.out.print(mobsimlink.toString() + " ");
	    	//}
	    	
	    	//System.out.print("\n");
	    	
	    	//System.out.println("agent is on" + agent.getCurrentLinkId().toString());
	    	
	    	netRoute.setLinkIds(agent.getCurrentLinkId(), 
	    					            mobsimLinks, 
	    					            currentLeg.getRoute().getEndLinkId());

	    	currentLeg.setRoute(netRoute);
	    			
	    	// Reset so the sim uses the new route
	    	WithinDayAgentUtils.resetCaches(agent);
	    	
	    	// Now we update the map, both removing ALL entries from before now
		    // and adding the new traffic in the future
		    for(String ourRoad:theRoute) {
		    	// Add vehicle count to the roads
		    	for (Long i = 0L; i < theMap.getRoad(ourRoad).getTravelTime(); i++) {
		    		// This isn't correct, but we aren't running matsim with a real now
		    		// Should have timeNow added to i
		    		theMap.getRoad(ourRoad).addToWeight(i);
		    	}
		    }
		    
		    for (String ourRoad:origRoute.getRoads()) {
		    	// Remove vehicles from the roads
		    	for (Long i = 0L; i < theMap.getRoad(ourRoad).getTravelTime(); i++) {
		    		// This isn't correct, but we aren't running matsim with a real now
		    		// Should have timeNow added to i
		    		theMap.getRoad(ourRoad).subFromWeight(i);
		    	}
		    }
	    }
	    
	    
		return true;
	}
	
	private static ConcurrentHashMap<String, MobsimAgent> getAgentsToReplan(Netsim mobsim, 
			                                                                ConcurrentHashMap<String, 
			                                                                GRIDagent> theAgents ) {

		ConcurrentHashMap<String, MobsimAgent> theMobsimAgents = new 
				ConcurrentHashMap<String, MobsimAgent>();

		// find agents that are en-route
		for (NetsimLink link : mobsim.getNetsimNetwork().getNetsimLinks().values()) {
			for (MobsimVehicle vehicle : link.getAllNonParkedVehicles()) {
				MobsimDriverAgent agent = vehicle.getDriver();

				theMobsimAgents.put(agent.getId().toString(), agent);
			}
		}

		return theMobsimAgents;
	}
	
	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent event) {
		
		// Not currently used. May change call to replan to here so the agents haven't entered
		// the next link. Currently, we plan from the end of the the next road
		//System.out.println("We got to the beginning of notifyMobsimAfterSimStep at time: " + event.getSimulationTime());
	}
}



// LEFTOVER STUFF. REMOVE WHEN HAPPY WITH ABOVE CODE


//List<Id<Link>> theList = new ArrayList<Id<Link>>();
//theList.add(Id.createLinkId("2to3"));
//theList.add(Id.createLinkId("3to8"));
//theList.add(Id.createLinkId("8to13"));
//theList.add(Id.createLinkId("13to18"));
//theList.add(Id.createLinkId("18to24"));
		    
//if(agent.getCurrentLinkId().toString().equals("2to3")) {

	//netRoute.setLinkIds(currentLeg.getRoute().getStartLinkId(), 
	//		            theList, 
	//		            currentLeg.getRoute().getEndLinkId());

	//currentLeg.setRoute(netRoute);
	// Reset so the sim uses the new route
	//WithinDayAgentUtils.resetCaches(agent);
//}

//else {
//	System.out.println("Not changing for link: " + currentLinkId);
//}

/*
if (ourAgents.containsKey(agent.getId().toString())) {
	if (ourAgents.get(agent.getId().toString()).getSimCalcFlag()) {
		theMobsimAgents.put(agent.getId().toString(), agent);
		
		System.out.println("Adding agent: " + agent.getId() + " to the list");
	}
}*/

//if (agent.getId().toString().equals("1")) { // some condition ...
//	System.out.println("found agent" + agent.toString());
//	theMobsimAgents.put("1", agent);
//}



//		List<PlanElement> planElements = plan.getPlanElements();

//final Integer planElementsIndex = WithinDayAgentUtils.getCurrentPlanElementIndex(agent);



//System.out.println("Sim Time is: " + now);



//int currentLinkIndex = WithinDayAgentUtils.getCurrentRouteLinkIdIndex(withinDayAgent);



//if (!(planElements.get(planElementsIndex + 1) instanceof Activity
//		|| !(planElements.get(planElementsIndex + 2) instanceof Leg))) {
//	log.error(
//			"this version of withinday replanning cannot deal with plans where legs and acts do not alternate; returning ...");
//	return false;
//}
//Iterator<? extends NetsimLink> iter = thesimNetwork.getNetsimLinks().values().iterator();


//System.out.println("We got to the begining of notifyMobsimBeforeSimStep: " + event.toString() + " " + event.getSimulationTime() );
//Netsim mobsim = (Netsim) event.getQueueSimulation() ;
//this.scenario = mobsim.getScenario();

//NetsimNetwork thesimNetwork = mobsim.getNetsimNetwork();
	    
//Map<Id<Link>, NetsimLink> theLinks = (Map<Id<Link>, NetsimLink>) thesimNetwork.getNetsimLinks();

//Iterator iter = thesimNetwork.getNetsimLinks().values().iterator();

//for(Id<Link> roadId:theLinks.keySet()) {
	//System.out.println("DAFUQ? ID=" + roadId.toString());
	//System.out.println("DAFUQ? " + thesimNetwork.getNetsimLink(roadId).getAllNonParkedVehicles().size());
	
	// This shows how many vehicles are on a link at any give time

//}

//this.scenario.getNetwork().getLinks()
//Map<Id<Link>, Link> theOtherLinks = (Map<Id<Link>, Link>) this.scenario.getNetwork().getLinks();

//for(Id<Link> roadId:theOtherLinks.keySet()) {
//	System.out.println("DAFUQ? " + roadId.toString());	
//	System.out.println("DAFUQ? " + this.scenario.getNetwork().getLinks().get(roadId).getCapacity()  );
//	System.out.println("DAFUQ? " + roadId.toString());	
//	System.out.println("DAFUQ? " + roadId.toString());	
//}

//for(String roadID:theMap.getRoads().keySet()) {
	//System.out.println("Start: " + theMap.getRoad(roadID).getCurrentSpeed());
	    		    	
	//theMap.getRoad(roadID).setCurrentSpeed(theMap.getRoad(roadID).getCurrentSpeed() + 1);	    	
// }

//Collection<MobsimAgent> agentsToReplan = getAgentsToReplan(mobsim); 
// for (MobsimAgent ma : agentsToReplan) {
	
	//System.out.println("we found agent: " + ma.toString());
	
	//doReplanning(ma, mobsim);	  	    
// }        