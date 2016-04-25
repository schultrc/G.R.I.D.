package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;

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

	public boolean addRoad(GRIDroad addMe)
	{		
		// Only add a road if it isn't already in the map
		if(this.Roads.containsKey(addMe.getId())) {
			System.out.printf("Road ID %d already exists", addMe.getId());
			return false;
		}
		else
		{
			this.Roads.put(addMe.getId(), addMe);
			System.out.printf("Successfully added road: %d \n", addMe.getId());
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
}
