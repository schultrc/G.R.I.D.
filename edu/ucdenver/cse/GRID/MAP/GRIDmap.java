package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;
//import java.util.ArrayList;

public class GRIDmap {
	
	private ConcurrentMap<String, GRIDintersection> Intersections = new ConcurrentHashMap<String, GRIDintersection>();
	private ConcurrentMap<String, GRIDroad> Roads = new ConcurrentHashMap<String, GRIDroad >();
	

	public ConcurrentMap<String, GRIDintersection> getIntersections() {
		return Intersections;
	}
	
	public void setIntersections(ConcurrentMap<String, GRIDintersection> intersections) {
		Intersections = intersections;
	}
	public ConcurrentMap<String, GRIDroad > getRoads() {
		return Roads;
	}
	public void setRoads(ConcurrentMap<String, GRIDroad > roads) {
		Roads = roads;
	}

	@Override
	public String toString() {
		return "GRIDmap [Intersections=" + Intersections + ", Roads=" + Roads + "]";
	}

	public boolean addRoad(GRIDroad addMe)
	{		
		// Only add a road if it isn't already in the map
		if(this.Roads.containsKey(addMe.getId())) {
			System.out.println("Road ID " + addMe.getId() + " already exists");
			return false;
		}
		else
		{
			this.Roads.put(addMe.getId(), addMe);
			//System.out.println("Successfully added road: " + addMe.getId());
		}
		
		return true;
	}
	
	public boolean addIntersection(GRIDintersection addMe) {
		if(this.Intersections.containsKey(addMe.getId())) {
			System.out.printf("Road ID %d already exists", addMe.getId());
			return false;
		}
		else {
			this.Intersections.putIfAbsent(addMe.getId(), addMe);
		}
		
		return true;			
	}
	
	public GRIDroad getRoad(String theRoadID) {
		return this.Roads.get(theRoadID);
	}
	public GRIDintersection getIntersection(String theIntersection) {
		return this.Intersections.get(theIntersection);
	}
	
	public String hasRoad(String from, String to) {
		// determine if we have a road that goes from "from" to "to"
		ArrayList<String> theWantedRoad = new ArrayList<>();
		theWantedRoad.add(from);
		theWantedRoad.add(to);
		
		ArrayList<String> returnList = this.getPathByRoad(theWantedRoad);
		
		if (returnList.size() != 1) {
			System.out.println("Could not find a road from: " + from + " to: " + to);
			return "";
		}
		
		else {
			return returnList.get(0);
		}
	}

	public ArrayList<String> getPathByRoad(ArrayList<String> pathByNode)
	{
		// Turns a route made of intersections into a route made of roads
		// Can also be used to find a single road based on start / end intersections
		// Note: This will return a road, but not necessarily a specific road, in the 
		// cul-desac world where 2 or more roads have the same start / end intersections
		ArrayList<String> pathByRoad = new ArrayList<>();
		Set<String> keys = Roads.keySet();

		for(int i = 0; i < pathByNode.size()-1; i++)
		{
			GRIDroad tempRoad = new GRIDroad("temp");
			tempRoad.setFrom(pathByNode.get(i));
			tempRoad.setTo(pathByNode.get(i+1));

			for (Iterator<String> itr = keys.iterator(); itr.hasNext();)
			{
				String key = (String) itr.next();
				GRIDroad testRoad = (GRIDroad) Roads.get(key);

				if(tempRoad.getFrom().equals(testRoad.getFrom())
				   && tempRoad.getTo().equals(testRoad.getTo())) {
					pathByRoad.add(testRoad.getId());
				}
			}
		}

		return  pathByRoad;
	}
}
