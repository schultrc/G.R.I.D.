package edu.ucdenver.cse.GRID.MAP;

import java.util.ArrayList;
import java.util.*;

public class GRIDroute {	
	private Long agent_ID;
	private Long calculatedTravelTime;

	ArrayList<String> Intersections = new ArrayList<String>();
	ArrayList<String> Roads = new ArrayList<String>();

	public void setcalculatedTravelTime(Long inTime){ this.calculatedTravelTime = inTime; }

	public ArrayList<String> getIntersections(){ return Intersections; }
	public Long getAgent_ID(){ return agent_ID; }
	public Long getcalculatedTravelTime(){ return calculatedTravelTime; }
}
