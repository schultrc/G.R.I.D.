package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;

public class GRIDmap {
	
	private ConcurrentMap<Long, GRIDintersection> Intersections = new ConcurrentHashMap<Long, GRIDintersection >();
	private ConcurrentMap<Long, GRIDroad> Roads = new ConcurrentHashMap<Long, GRIDroad >();
	public ConcurrentMap<Long, GRIDintersection > getIntersections() {
		return Intersections;
	}
	public void setIntersections(ConcurrentMap<Long, GRIDintersection > intersections) {
		Intersections = intersections;
	}
	public ConcurrentMap<Long, GRIDroad > getRoads() {
		return Roads;
	}
	public void setRoads(ConcurrentMap<Long, GRIDroad > roads) {
		Roads = roads;
	}

	public boolean addRoad(GRIDroad addMe)
	{		
		// Only add a road if it isn't already in the map
		if(this.Roads.containsKey(addMe.getId()))
		{
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
}
