package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;
//import java.util.ArrayList;

public final class GRIDmap implements Iterable<String> {

	private ConcurrentMap<String, GRIDintersection> Intersections = new ConcurrentHashMap<String, GRIDintersection>();
	private ConcurrentMap<String, GRIDroad> Roads = new ConcurrentHashMap<String, GRIDroad >();

	private ConcurrentMap<String, GRIDroad> roadList = new ConcurrentHashMap<>();
	private ConcurrentMap<String, Long> intersectionList = new ConcurrentHashMap<>();

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

    public void loadRoadList() {
		this.Roads.forEach((item,value)->
                roadList.put(value.getFrom()+value.getTo(),value));
		this.Intersections.forEach((item,value)->
				intersectionList.put(value.getId(),0L));
    }

    public ConcurrentMap getRoadList() { return roadList; }

    public GRIDroad getRoadListItem(String itemID) { return roadList.get(itemID); }

    public Long getIntersectionListItem(String itemID) { return intersectionList.get(itemID); }
	public void setIntersectionListItemTimeAtExit(String itemID, Long exitTime) {
		//GRIDintersection tempIntersection = new GRIDintersection();
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

    public GRIDnodeWeightTime calcWeight(String startNode, String endNode, long startTime)
    {
    	double tempEmissions = 0.0;
        double tempWeight = 0.0;
        long tempTimeslice = 0L;
        GRIDnodeWeightTime tempNode = new GRIDnodeWeightTime();
		GRIDroad tempRoad = this.getRoadListItem(startNode+endNode);

        tempTimeslice = tempRoad.getTravelTime();
		tempEmissions = tempRoad.getEmissionsWeightOverInterval(startTime);
        tempWeight = tempRoad.getTimeWeightOverInterval(startTime);

		tempNode.setNodeEmissions(tempEmissions);
        tempNode.setNodeWtTotal(tempWeight);
        tempNode.setNodeTmTotal(tempTimeslice);

        return tempNode;
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

		for(int i = 0; i < pathByNode.size()-1; i++)
		{
			GRIDroad tempRoad = this.getRoadListItem(pathByNode.get(i)+pathByNode.get(i+1));
			pathByRoad.add(tempRoad.getId());
		}

		return  pathByRoad;
	}

	public ArrayList<GRIDrouteSegment> getPathBySegment(ArrayList<String> pathByNode,
										   ConcurrentMap<String, GRIDrouteSegment> finalRouteSegments)
	{
		/* Turns a route made of intersections into a route made of roads,
		 * then into a path of segments */
		ArrayList<String> pathByRoad = getPathByRoad(pathByNode);
		ArrayList<GRIDrouteSegment> pathBySegment = new ArrayList<>();

		for(String finalPathRoadID : pathByRoad) {
			pathBySegment.add(finalRouteSegments.get(finalPathRoadID));
		}

		return  pathBySegment;
	}

	public void addVehicleToRoads(ArrayList<GRIDrouteSegment> newRouteSegments, Long startTime) {
		for(int i = 0; i < newRouteSegments.size(); ++i) {
			for(Long atTime = startTime; atTime < newRouteSegments.get(i).getTimeAtRoadExit(); ++atTime) {
				this.Roads.get(newRouteSegments.get(i).getRoadID()).addToWeight(atTime);
			}
		}
	}

	public void removeVehicleFromRoads(ArrayList<GRIDrouteSegment> oldRouteSegments, Long startTime) {
		for(int i = 0; i < oldRouteSegments.size(); ++i) {
			for(Long atTime = startTime; atTime < oldRouteSegments.get(i).getTimeAtRoadExit(); ++atTime) {
				this.Roads.get(oldRouteSegments.get(i).getRoadID()).subFromWeight(atTime);
			}
		}
	}

	/*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
	* The following functions are transplanted from the DirectedGraph class.                         *
	* These are necessary for the use of the fibHeap data structure.                                 *
	*   *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */

	private final ConcurrentMap<String, Map<String, Double>> fibHeapGraph = new ConcurrentHashMap<String, Map<String, Double>>();
	public class graphEdge{
        String start;
        String finish;
		Double weight;

		public void setStart(String val){start = val;}
		public void setFinish(String val){finish = val;}
		public void setWeight(Double val){weight = val;}

		@Override
		public String toString(){return "Edge [start: "+start+" end: "+finish+" weight: "+weight+"]";}
	}

	public graphEdge getEdge(String start, String finish)
	{
		graphEdge temp = new graphEdge();
		fibHeapGraph.containsKey(start);
		Map<String, Double> arcs = fibHeapGraph.get(start);
		for(String key : arcs.keySet())
		{
			if(key == finish)
			{
				temp.setStart(start);
				temp.setFinish(key);
				temp.setWeight(arcs.get(key));
				return temp;
			}
		}


		return null;
	}

	/**
	 * Adds a new node to the graph.  If the node already exists, this
	 * function is a no-op.
	 *
	 * @param node The node to add.
	 * @return Whether or not the node was added.
	 */
	public boolean addNode(String node) {
        /* If the node already exists, don't do anything. */
		if (fibHeapGraph.containsKey(node))
			return false;

        /* Otherwise, add the node with an empty set of outgoing edges. */
		fibHeapGraph.put(node, new HashMap<String, Double>());
		return true;
	}

	/**
	 * Given a start node, destination, and length, adds an arc from the
	 * start node to the destination of the length.  If an arc already
	 * existed, the length is updated to the specified value.  If either
	 * endpoint does not exist in the graph, throws a NoSuchElementException.
	 *
	 * @param start The start node.
	 * @param dest The destination node.
	 * @param length The length of the edge.
	 * @throws NoSuchElementException If either the start or destination nodes
	 *                                do not exist.
	 */
	public void addEdge(String start, String dest, double length) {
        /* Confirm both endpoints exist. */
		if (!fibHeapGraph.containsKey(start) || !fibHeapGraph.containsKey(dest))
			throw new NoSuchElementException("Both nodes must be in the graph.");

        /* Add the edge. */
		fibHeapGraph.get(start).put(dest, length);
	}

	/**
	 * Removes the edge from start to dest from the graph.  If the edge does
	 * not exist, this operation is a no-op.  If either endpoint does not
	 * exist, this throws a NoSuchElementException.
	 *
	 * @param start The start node.
	 * @param dest The destination node.
	 * @throws NoSuchElementException If either node is not in the graph.
	 */
	public void removeEdge(String start, String dest) {
        /* Confirm both endpoints exist. */
		if (!fibHeapGraph.containsKey(start) || !fibHeapGraph.containsKey(dest))
			throw new NoSuchElementException("Both nodes must be in the graph.");

		fibHeapGraph.get(start).remove(dest);
	}

	/**
	 * Given a node in the graph, returns an immutable view of the edges
	 * leaving that node, as a map from endpoints to costs.
	 *
	 * @param node The node whose edges should be queried.
	 * @return An immutable view of the edges leaving that node.
	 * @throws NoSuchElementException If the node does not exist.
	 */
	//public Map<String, Double> edgesFrom(String node) {
    public Map<String, Double> edgesFrom(String node) {
        /* Check that the node exists. */
		Map<String, Double> arcs = fibHeapGraph.get(node);
		if (arcs == null)
			throw new NoSuchElementException("Source node does not exist.");

		return Collections.unmodifiableMap(arcs);
	}

	/**
	 * Returns an iterator that can traverse the nodes in the graph.
	 *
	 * @return An iterator that traverses the nodes in the graph.
	 */
	public Iterator<String> iterator() {
		return fibHeapGraph.keySet().iterator();
	}
}
