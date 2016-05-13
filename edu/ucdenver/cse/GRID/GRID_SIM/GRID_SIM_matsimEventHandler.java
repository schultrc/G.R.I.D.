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
		
		// Need to update any agent that doesn't have a destination
		for(GRIDagent agent : theAgents.values() ) {
			if (agent.getNeedsDestination()) {
				
				// EVERY agent will need a destination, not just the replan ones.
				agent.setDestination(mobsimAgents.get(agent.getId()).getDestinationLinkId().toString());
				agent.setNeedsDestinationFlag(false);
				System.out.println("Setting agent: " + agent.getId() +
						           " destination to: " + agent.getDestination() ); 
						
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
		}
		
		// Agent route updates  - every time
		while (!agentsToReplan.isEmpty() ) {
						
			// We can change this by sorting the list prior to removing
			GRIDagent tempAgent = theAgents.get(agentsToReplan.remove());
			
			if (tempAgent != null) {
				//System.out.println("Found Agent to replan: " + tempAgent.getId());
				if(mobsimAgents.containsKey("1"))
				{
					// Ok, we are sort of here, now. Do the whole replacy thingy
					System.out.println("Replacing the route for agent: " + tempAgent.getId());
					doReplanning(mobsimAgents.get("1"), mobsim, tempAgent.getCurrentLink());
				}
			}			
			// we need to ID which agents get replanned - % based			
		}
		
		// RCS Is this working, but sending it to the console?????
		//GRIDLog.info("notifyMobsimBeforeSimStep " + event.toString() + " " + event.getSimulationTime() );
	}
	
	private boolean doReplanning(MobsimAgent agent, Netsim mobsim, String currentLinkId ) {
		double now = mobsim.getSimTimer().getTimeOfDay();

		System.out.println("Sim Time is: " + now);

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
		
		List<PlanElement> planElements = plan.getPlanElements();
		//final Integer planElementsIndex = WithinDayAgentUtils.getCurrentPlanElementIndex(agent);

		//System.out.println("planElements: " + planElements.toString());
		
		Leg currentLeg = (Leg)  WithinDayAgentUtils.getCurrentPlanElement(agent);
		
		//Route tempRoute =  currentLeg.getRoute().clone();
		
	    NetworkRoute netRoute =  (NetworkRoute) currentLeg.getRoute().clone();
	    
	    List<Id<Link>> theList = new ArrayList<Id<Link>>();
	    theList.add(Id.createLinkId("2to3"));
	    theList.add(Id.createLinkId("3to8"));
	    theList.add(Id.createLinkId("8to13"));
	    theList.add(Id.createLinkId("13to18"));
	    theList.add(Id.createLinkId("18to24"));
	    	
	    //if(currentLeg.getRoute().getStartLinkId().toString().equals("2to3")) {
	    
	    if(agent.getCurrentLinkId().toString().equals("2to3")) {

			// PopulationFactoryImpl theFact = new PopulationFactoryImpl(null);
			netRoute.setLinkIds(currentLeg.getRoute().getStartLinkId(), theList, currentLeg.getRoute().getEndLinkId());

			// Route netRoute2 = new PopulationFactory.createRoute("car",
			// Id.createLinkId("2to3"), Id.createLinkId("24to25") );

			currentLeg.setRoute(netRoute);

			WithinDayAgentUtils.resetCaches(agent);

			System.out.println("planElements: " + planElements.toString());

		}
		else {
			System.out.println("Not changing for link: " + currentLinkId);
		}

		return true;
	}
	
	private static ConcurrentHashMap<String, MobsimAgent> getAgentsToReplan(Netsim mobsim, 
			                                                                ConcurrentHashMap<String, GRIDagent> theAgents ) {

		ConcurrentHashMap<String, MobsimAgent> theMobsimAgents = new ConcurrentHashMap<String, MobsimAgent>();

		// find agents that are en-route (more interesting case)
		for (NetsimLink link : mobsim.getNetsimNetwork().getNetsimLinks().values()) {
			for (MobsimVehicle vehicle : link.getAllNonParkedVehicles()) {
				MobsimDriverAgent agent = vehicle.getDriver();
				
				//Obviously, we don't want to hard code this. eventually
				
				if (theAgents.containsKey(agent.getId().toString())) {
					if (theAgents.get(agent.getId().toString()).getSimCalcFlag()) {
						theMobsimAgents.put(agent.getId().toString(), agent);
						
						System.out.println("Adding agent: " + agent.getId() + " to the list");
					}
				}
				
//				if (agent.getId().toString().equals("1")) { // some condition ...
//					System.out.println("found agent" + agent.toString());
//					theMobsimAgents.put("1", agent);
//				}
			}
		}

		return theMobsimAgents;
	}
	
	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent event) {
		
		// Not currently used.
		//System.out.println("We got to the beginning of notifyMobsimBeforeSimStep at time: " + event.getSimulationTime());
	}
}



// LEFTOVER STUFF. REMOVE WHEN HAPPY WITH ABOVE CODE



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