package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;

public class Map {
	
	private ConcurrentMap<Intersection, Long > Intersections = new ConcurrentHashMap<Intersection, Long >();
	private ConcurrentMap<Road, Long> Roads = new ConcurrentHashMap<Road, Long >();
	public ConcurrentMap<Intersection, Long > getIntersections() {
		return Intersections;
	}
	public void setIntersections(ConcurrentMap<Intersection, Long > intersections) {
		Intersections = intersections;
	}
	public ConcurrentMap<Road, Long > getRoads() {
		return Roads;
	}
	public void setRoads(ConcurrentMap<Road, Long > roads) {
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
			this.Roads.put(addMe, addMe.getId());
			System.out.printf("Successfully added road: %d \n", addMe.getId());
		}
		
		return true;
	}
}
