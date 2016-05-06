package edu.ucdenver.cse.GRID.GRID_SIM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener;
import org.matsim.core.mobsim.qsim.agents.WithinDayAgentUtils;
import org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.mobsim.qsim.qnetsimengine.NetsimLink;
import org.matsim.core.router.TripRouter;
import org.matsim.withinday.utils.EditRoutes;


public class GRID_SIMlistener implements MobsimBeforeSimStepListener {

	private static final Logger log = Logger.getLogger("dummy");
	
	private TripRouter tripRouter;
	private Scenario scenario;

	GRID_SIMlistener(TripRouter tripRouter) {
		this.tripRouter = tripRouter;
	}

	@Override
	public void notifyMobsimBeforeSimStep(@SuppressWarnings("rawtypes") MobsimBeforeSimStepEvent event) {
	
		System.out.println("We got to the begining of notifyMobsimBeforeSimStep");
		Netsim mobsim = (Netsim) event.getQueueSimulation() ;
	    this.scenario = mobsim.getScenario();
	    
	    Collection<MobsimAgent> agentsToReplan = getAgentsToReplan(mobsim); 
	    for (MobsimAgent ma : agentsToReplan) {
	    	
	    	System.out.println("we found agent: " + ma.toString());
	    	
	    	//doReplanning(ma, mobsim);
	  	    
	    }        
	}
	
	private boolean doReplanning(MobsimAgent agent, Netsim mobsim ) {
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
		
		// FIX
		
		
		List<MobsimAgent> set = new ArrayList<MobsimAgent>();

		// don't do anything for most time steps:
		if (Math.floor(mobsim.getSimTimer().getTimeOfDay()) != 22000.0) {
			return set;
		}
		// find agents that are en-route (more interesting case)
		for (NetsimLink link : mobsim.getNetsimNetwork().getNetsimLinks().values()) {
			for (MobsimVehicle vehicle : link.getAllNonParkedVehicles()) {
				MobsimDriverAgent agent = vehicle.getDriver();
				System.out.println(agent.getId());
				if (true) { // some condition ...
					System.out.println("found agent");
					set.add(agent);
				}

			}
		}

		return set;
	}
}
