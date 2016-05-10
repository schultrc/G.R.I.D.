package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import org.matsim.core.router.TripRouter;
import org.matsim.withinday.utils.EditRoutes;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;
import edu.ucdenver.cse.GRID.MAP.GRIDmap;
import edu.ucdenver.cse.GRID.MAP.GRIDroad;


public class GRID_SIM_matsimEventHandler implements MobsimBeforeSimStepListener, MobsimAfterSimStepListener {

	GRIDmap theMap;
	
	ConcurrentHashMap<String, GRIDagent> theAgents;
	
	public GRIDmap getTheMap() {
		// This should NEVER get called
		return theMap;
	}

	public void setTheMap(GRIDmap theMap) {
		this.theMap = theMap;
	}

	public ConcurrentMap<String, GRIDagent> getTheAgents() {
		// This should NEVER get called
		return theAgents;
	}

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

		// RCS Is this working, but sending it to the console?????
		//GRIDLog.info("notifyMobsimBeforeSimStep " + event.toString() + " " + event.getSimulationTime() );
		
		//System.out.println("We got to the begining of notifyMobsimBeforeSimStep: " + event.toString() + " " + event.getSimulationTime() );
		Netsim mobsim = (Netsim) event.getQueueSimulation() ;
	    this.scenario = mobsim.getScenario();
	    
	    NetsimNetwork thesimNetwork = mobsim.getNetsimNetwork();
	    	    
	    Map<Id<Link>, NetsimLink> theLinks = (Map<Id<Link>, NetsimLink>) thesimNetwork.getNetsimLinks();
	    
	    //for(Id<Link> roadId:theLinks.keySet()) {
	    	//System.out.println("DAFUQ? ID=" + roadId.toString());
	    	//System.out.println("DAFUQ? " + thesimNetwork.getNetsimLink(roadId).getAllNonParkedVehicles().size());
	    	
	    	// This shows how many vehicles are on a link at any give time

	    //}
    
	    //this.scenario.getNetwork().getLinks()
	    Map<Id<Link>, Link> theOtherLinks = (Map<Id<Link>, Link>) this.scenario.getNetwork().getLinks();
	    
//	    for(Id<Link> roadId:theOtherLinks.keySet()) {
//	    	System.out.println("DAFUQ? " + roadId.toString());	
//	    	System.out.println("DAFUQ? " + this.scenario.getNetwork().getLinks().get(roadId).getCapacity()  );
//	    	System.out.println("DAFUQ? " + roadId.toString());	
//	    	System.out.println("DAFUQ? " + roadId.toString());	
//	    }
	    
	    for(String roadID:theMap.getRoads().keySet()) {
	    	//System.out.println("Start: " + theMap.getRoad(roadID).getCurrentSpeed());
	    	    		    	
	    	//theMap.getRoad(roadID).setCurrentSpeed(theMap.getRoad(roadID).getCurrentSpeed() + 1);	    	
	    }
	    
	    Collection<MobsimAgent> agentsToReplan = getAgentsToReplan(mobsim); 
	    for (MobsimAgent ma : agentsToReplan) {
	    	
	    	//System.out.println("we found agent: " + ma.toString());
	    	
	    	doReplanning(ma, mobsim);	  	    
	    }        
	}
	
	private boolean doReplanning(MobsimAgent agent, Netsim mobsim ) {
		double now = mobsim.getSimTimer().getTimeOfDay();

		//System.out.println("Sim Time is: " + now);

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
		final Integer planElementsIndex = WithinDayAgentUtils.getCurrentPlanElementIndex(agent);

		if (!(planElements.get(planElementsIndex + 1) instanceof Activity
				|| !(planElements.get(planElementsIndex + 2) instanceof Leg))) {
			log.error(
					"this version of withinday replanning cannot deal with plans where legs and acts do not alternate; returning ...");
			return false;
		}

		return true;
	}
	
	private static List<MobsimAgent> getAgentsToReplan(Netsim mobsim) {

		// Change this to be our list of agents
		
		// Can we see the agents we've added?
		// System.out.println("In matsimEventHandler: There are: " + theAgents.size() + " agents now");

		
		List<MobsimAgent> set = new ArrayList<MobsimAgent>();


		// don't do anything for most time steps:
		if (Math.floor(mobsim.getSimTimer().getTimeOfDay()) != 22000.0) {
			return set;
		}
		// find agents that are en-route (more interesting case)
		for (NetsimLink link : mobsim.getNetsimNetwork().getNetsimLinks().values()) {
			for (MobsimVehicle vehicle : link.getAllNonParkedVehicles()) {
				MobsimDriverAgent agent = vehicle.getDriver();
				
				//System.out.println(agent.getId());
				
				if (true) { // some condition ...
					//System.out.println("found agent" + agent.toString());
					set.add(agent);
				}
			}
		}

		return set;
	}
	

	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent event) {

		//System.out.println("We got to the beginning of notifyMobsimBeforeSimStep at time: " + event.getSimulationTime());

	}
}
