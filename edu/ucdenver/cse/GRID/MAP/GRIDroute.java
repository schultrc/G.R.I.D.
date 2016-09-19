package edu.ucdenver.cse.GRID.MAP;

import java.util.ArrayList;
import java.util.*;

public class GRIDroute {	
	private Long agent_ID;
	private Long calculatedTravelTime;
	private Double calculatedEmissionsTotal;

	public ArrayList<String> Intersections = new ArrayList<String>(2);
	public ArrayList<String> Roads = new ArrayList<String>(2);
	public ArrayList<GRIDrouteSegment> RouteSegments = new ArrayList<GRIDrouteSegment>();

	public void setCalculatedTravelTime(Long inTime){ this.calculatedTravelTime = inTime; }
	public void setCalculatedEmissionsTotal(){
		this.calculatedEmissionsTotal = 0.0;

		for(GRIDrouteSegment segment : RouteSegments) {
			this.calculatedEmissionsTotal += segment.getSegmentEmissions();
		}
	}

	public ArrayList<String> getIntersections(){ return this.Intersections; }
	public Long getAgent_ID(){ return this.agent_ID; }
	public Long getCalculatedTravelTime(){ return this.calculatedTravelTime; }
	public Double getCalculatedEmissionsTotal(){ return this.calculatedEmissionsTotal; }
	public ArrayList<String> getRoads() {return this.Roads; }
	public void setRoads(ArrayList<String> theRoads) { this.Roads = theRoads; }
	public ArrayList<GRIDrouteSegment> getRouteSegments() { return this.RouteSegments; }
	public void setRouteSegments(ArrayList<GRIDrouteSegment> theRouteSegments)
								{ this.RouteSegments = theRouteSegments; }
	
	public boolean equalsRoads(GRIDroute otherRoute) {
		if (this.Roads.isEmpty() || otherRoute.getRoads().isEmpty()) { return false; }
		if (this.Roads.size() != otherRoute.getRoads().size()) { return false; }

		if (this.RouteSegments.isEmpty() || otherRoute.getRouteSegments().isEmpty()) { return false; }
		if (this.RouteSegments.size() != otherRoute.getRouteSegments().size()) { return false; }
	
		for(int i = 0; i < this.Roads.size(); ++i) {
		   if (this.Roads.get(i).equals(otherRoute.getRoads().get(i))) { return false; }
		}

		for(int i = 0; i < this.RouteSegments.size(); ++i) {
			if (this.RouteSegments.get(i).equals(otherRoute.getRouteSegments().get(i))) { return false; }
		}

		return true;
	}
	
	// FIX this? 
	public boolean equalsIntersections(GRIDroute otherRoute) {
		if (this.Intersections.isEmpty() || otherRoute.getIntersections().isEmpty()) { return false; }
		if (this.Intersections.size() != otherRoute.getIntersections().size()) { return false; }
	
		for(int i = 0; i < this.Intersections.size(); ++i) {
		   if (this.Intersections.get(i).equals(otherRoute.getIntersections().get(i))) { return false; }
		}
		
		return true;
	}

	public boolean compareOldNewRoute(GRIDroute newRoute) {
		if (this.RouteSegments.isEmpty() || newRoute.RouteSegments.isEmpty()) { return false; }
		if (this.RouteSegments.size() != newRoute.RouteSegments.size()) { return false; }

		for(int i = 0; i < this.RouteSegments.size(); ++i) {
			if (!this.RouteSegments.get(i).equals(newRoute.RouteSegments.get(i))) { return false; }
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
