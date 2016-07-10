/*
    Using code from the following website as example to follow in creation
    of our Dijkstra's implementation:
    http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
*/

package edu.ucdenver.cse.GRID.MAP;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;

import java.util.concurrent.*;
import java.util.*;

public class GRIDpathrecalc {

    private ConcurrentMap<String, GRIDintersection> intersections;
    private ConcurrentMap<String, GRIDroad> roads;
    private String agtFrom;
    private String agtTo;
    private Long thisTimeslice;
    private Set<String> visited;
    private Set<String> unVisited;
    private ConcurrentMap<String, GRIDnodeWeightTime> currentPathTotal;
    GRIDnodeWeightTime startNodeValues;
    private Map<String,String> previousIntersections;

    public GRIDpathrecalc(GRIDagent thisAgent, GRIDmap selfishMap, Long currentTime){
        this.intersections = selfishMap.getIntersections();
        this.roads = selfishMap.getRoads();
        
        // The agent is already on the link, so we need its endpoint
        agtFrom = selfishMap.getRoad(thisAgent.getCurrentLink()).getTo();  
        
        // The agent will end somewhere on the final link, so we need to get to its "from end"
        agtTo = selfishMap.getRoad(thisAgent.getDestination()).getFrom();
        this.thisTimeslice = currentTime/1000;

        /* These are for setting vehiclesCurrentlyOnRoadAtTime
         * for testing purposes; comment out to run without using
         * vehiclesCurrentlyOnRoadAtTime in program run */
        //roads.get("10").fillRoadWeight(10); // 177937741
        //roads.get("11").fillRoadWeight(11); // 4041297308
        //roads.get("12").fillRoadWeight(12); // 665844624
    }

    public GRIDroute findPath(){
    	
    	// DUMB check - prevent elsewhere
    	if (this.agtTo.equals(this.agtFrom)) { 
    		System.out.println("to and from are the same");
    		return null;
    	}
    		
    	
        visited = new HashSet<String>();
        unVisited = new HashSet<String>();
        currentPathTotal = new ConcurrentHashMap<>();
        previousIntersections = new HashMap<String,String>();
        startNodeValues = new GRIDnodeWeightTime();
        startNodeValues.setNodeWtTotal(0.0);
        startNodeValues.setNodeTmTotal(this.thisTimeslice);
        currentPathTotal.put(agtFrom,startNodeValues);
        unVisited.add(agtFrom);

        while(unVisited.size() > 0){
            String node = getMin(unVisited);
            visited.add(node);
            unVisited.remove(node);
            findOptimalEdges(node);
        }

        GRIDroute finalPath = new GRIDroute();
        String step = agtTo;

        finalPath.Intersections.add(step);
        if(previousIntersections.get(step) == null)
        {
            System.out.println("\nI guess it's null, friend. Tried: " + agtFrom + " to " + agtTo);
            return null;
        }

        while(previousIntersections.get(step)!= null)
        {
            step = previousIntersections.get(step);
            finalPath.Intersections.add(step);
        }

       Collections.reverse(finalPath.Intersections);

        finalPath.setcalculatedTravelTime(currentPathTotal.get(agtTo).getNodeTmTotal());

        // We need to convert this to roads, as well
        
        
        //System.out.println("Returning path. . .");
        return finalPath;
    }

    private String getMin(Set<String> nodes){
        String min = null;

        for(String node : nodes){
            if(min == null)
            {
                min = node;
            }
            else
            {
                if(getOptimalEdgeWeight(node) < getOptimalEdgeWeight(min))
                {
                    min = node;
                }
            }
        }

        return min;
    }

    private void findOptimalEdges(String startNode)
    {
        ArrayList<String> adjNodes = getAdjNodes(startNode);
        GRIDnodeWeightTime tempNode = new GRIDnodeWeightTime();

        for(String endNode : adjNodes)
        {
            tempNode = calcEdgeWeight(startNode, endNode);
            if(getOptimalEdgeWeight(endNode) > getOptimalEdgeWeight(startNode) + tempNode.getNodeWtTotal())
            {
                Double tempWeight = getOptimalEdgeWeight(startNode);
                tempNode.setNodeWtTotal(tempWeight+tempNode.getNodeWtTotal());
                Long tempTime = currentPathTotal.get(startNode).getNodeTmTotal();
                tempNode.setNodeTmTotal(tempTime+tempNode.getNodeTmTotal());
                // add getOptEdgeWeight to first part of data structure tempNode

                currentPathTotal.put(endNode,tempNode);

                previousIntersections.put(endNode,startNode);

                unVisited.add(endNode);
            }
        }
    }

    private Double getOptimalEdgeWeight(String endNode)
    {
        Double w = null;
        if(currentPathTotal.containsKey(endNode))
            w = currentPathTotal.get(endNode).getNodeWtTotal();

        if(w == null)
        {
            return Double.MAX_VALUE;
        }
        else{
            return w;
        }
    }

    private GRIDnodeWeightTime calcEdgeWeight(String startNode, String endNode)
    {
        Double tempWeight = 0.0;
        Long tempTimeslice = 0L;
        Long startTime = currentPathTotal.get(startNode).getNodeTmTotal();
        GRIDnodeWeightTime tempNode = new GRIDnodeWeightTime();

        for(String roadId : roads.keySet())
        {
            if(roads.get(roadId).getFrom().equals(startNode)
                    && roads.get(roadId).getTo().equals(endNode))
            {
                tempTimeslice = roads.get(roadId).getTravelTime();
                tempWeight = roads.get(roadId).getWeightOverInterval(startTime);
                tempNode.setNodeWtTotal(tempWeight);
                tempNode.setNodeTmTotal(tempTimeslice);

                return tempNode;
            }
        }

        tempNode.setNodeWtTotal(-1D);
        tempNode.setNodeTmTotal(0L);
        return tempNode;
    }

    private ArrayList<String> getAdjNodes(String node)
    {
        ArrayList<String> adjNodes = new ArrayList<String>();

        for(String key : roads.keySet())
        {
            if(roads.get(key).getFrom().equals(node) && !isVisited(roads.get(key).getTo()))
            {
                adjNodes.add(roads.get(key).getTo());
            }
        }

        return adjNodes;
    }

    private boolean isVisited(String node)
    {
        for (String intrx : visited)
        {
            if(intrx.equals(node))
            {
                return true;
            }
        }

        return false;
    }
}
