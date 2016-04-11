package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;

public class GRIDMap {
	
	private ConcurrentMap<Long, Intersection> Intersections = new ConcurrentHashMap<Long, Intersection >();
	private ConcurrentMap<Long, Road> Roads = new ConcurrentHashMap<Long, Road >();
	public ConcurrentMap<Long, Intersection > getIntersections() {
		return Intersections;
	}
	public void setIntersections(ConcurrentMap<Long, Intersection > intersections) {
		Intersections = intersections;
	}
	public ConcurrentMap<Long, Road > getRoads() {
		return Roads;
	}
	public void setRoads(ConcurrentMap<Long, Road > roads) {
		Roads = roads;
	}

	public boolean addRoad(Road addMe)
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
