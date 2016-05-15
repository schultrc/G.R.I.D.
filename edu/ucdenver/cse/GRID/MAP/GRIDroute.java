package edu.ucdenver.cse.GRID.MAP;

import java.util.ArrayList;
import java.util.*;

public class GRIDroute {	
	private Long agent_ID;
	private Long calculatedTravelTime;

	ArrayList<String> Intersections = new ArrayList<String>(2);
	ArrayList<String> Roads = new ArrayList<String>(2);

	public void setcalculatedTravelTime(Long inTime){ this.calculatedTravelTime = inTime; }

	public ArrayList<String> getIntersections(){ return this.Intersections; }
	public Long getAgent_ID(){ return agent_ID; }
	public Long getcalculatedTravelTime(){ return calculatedTravelTime; }
	public ArrayList<String> getRoads() {return this.Roads; }
	public void setRoads(ArrayList<String> theRoads) { this.Roads = theRoads; }
	
	public boolean equalsRoads(GRIDroute otherRoute) {
		if (this.Roads.isEmpty() || otherRoute.getRoads().isEmpty()) { return false; }
		if (this.Roads.size() != otherRoute.getRoads().size()) { return false; }
	
		for(int i = 0; i < this.Roads.size(); ++i) {
		   if (this.Roads.get(i) != otherRoute.getRoads().get(i)) { return false; }	
		}
		
		return true;
	}
	
	// FIX this? 
	public boolean equalsIntersections(GRIDroute otherRoute) {
		if (this.Intersections.isEmpty() || otherRoute.getIntersections().isEmpty()) { return false; }
		if (this.Intersections.size() != otherRoute.getIntersections().size()) { return false; }
	
		for(int i = 0; i < this.Intersections.size(); ++i) {
		   if (this.Intersections.get(i) != otherRoute.getIntersections().get(i)) { return false; }	
		}
		
		return true;
	}
	
	public String toString() {
		String theRouteStr = "Route: ";
		
		for(String road:this.Intersections) {
			theRouteStr += " " + road;
		}
		
		return theRouteStr;
	}
}
