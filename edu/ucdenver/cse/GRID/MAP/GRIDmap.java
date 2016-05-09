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

	public ArrayList<String> getPathByRoad(ArrayList<String> pathByNode)
	{
		ArrayList<String> pathByRoad = new ArrayList<String>();
		Set keys = Roads.keySet();

		for (Iterator itr = keys.iterator(); itr.hasNext();)
		{
			String key = (String) itr.next();
			GRIDroad value = (GRIDroad) Roads.get(key);

			for(int i = 0; i < pathByNode.size()-1; i++)
			{
				if(pathByNode.get(i).equals(value.getFrom())
				   && pathByNode.get(i+1).equals(value.getTo()))
				{
					pathByRoad.add(value.getId());
				}
			}
		}

		return  pathByRoad;
	}

}
