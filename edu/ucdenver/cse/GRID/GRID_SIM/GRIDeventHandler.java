package edu.ucdenver.cse.GRID.GRID_SIM;

import org.matsim.api.core.v01.events.LinkEnterEvent;

import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.core.mobsim.framework.events.*;

public class GRIDeventHandler  implements LinkEnterEventHandler, LinkLeaveEventHandler, PersonArrivalEventHandler, PersonDepartureEventHandler {
	@Override

	public void reset(int iteration) {

		System.out.println("reset...");

	}
	@Override
		public void handleEvent(LinkEnterEvent event) {
		System.out.println("LinkEnterEvent");
		System.out.println("Time: " + event.getTime());
		System.out.println("LinkId: " + event.getLinkId());
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		
		// This is probably where we want to go get an updated route
		System.out.println("LinkLeaveEvent");
		System.out.println("Time: " + event.getTime());
		System.out.println("LinkId: " + event.getLinkId());
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {
		System.out.println("AgentArrivalEvent");
		System.out.println("Time: " + event.getTime());
		System.out.println("LinkId: " + event.getLinkId());
		System.out.println("PersonId: " + event.getPersonId());
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		System.out.println("AgentDepartureEvent");
		System.out.println("Time: " + event.getTime());
		System.out.println("LinkId: " + event.getLinkId());
		System.out.println("PersonId: " + event.getPersonId());
	}

}

