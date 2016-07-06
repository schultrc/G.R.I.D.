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
    private Hashtable<String,GRIDroad> thisRoadList;
    private long totalCalcTime;
    private long totalTravelTime;

    public GRIDpathrecalc(GRIDagent thisAgent, GRIDmap selfishMap, Long currentTime){
        this.intersections = selfishMap.getIntersections();
        this.roads = selfishMap.getRoads();
        selfishMap.loadRoadList(selfishMap.getRoads());
        this.thisRoadList = selfishMap.getRoadList();
        agtFrom = selfishMap.getRoad(thisAgent.getCurrentLink()).getTo();  // Changed from getFrom to accomodate matsim stupidity
        agtTo = selfishMap.getRoad("72823276_0_r").getTo();
        totalCalcTime = 0L;
        totalTravelTime = 0L;

        this.thisTimeslice = currentTime/1000;

        System.out.println("agtFrom: "+agtFrom);
        System.out.println("agtTo: "+agtTo);

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

        //System.out.println("all previous intersections:"+previousIntersections);
        while(previousIntersections.get(step)!= null)
        {
            // test code begin
            long tempLong = currentPathTotal.get(step).getNodeTmTotal();
            // test code end
            step = previousIntersections.get(step);
            finalPath.Intersections.add(step);
            // test code begin
            System.out.println("step: "+(tempLong));
            System.out.println("time: "+(tempLong-currentPathTotal.get(step).getNodeTmTotal()));
            totalTravelTime += currentPathTotal.get(step).getNodeTmTotal();
            // test code end
        }

        Collections.reverse(finalPath.Intersections);

        finalPath.setcalculatedTravelTime(currentPathTotal.get(agtTo).getNodeTmTotal());

        // We need to convert this to roads, as well

        System.out.println("total time (439): "+totalTravelTime);
        //System.out.println("for loop time: "+totalCalcTime/1000000000.0+"s");
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
        double tempWeight = 0.0;
        long tempTimeslice = 0L;
        long startTime = currentPathTotal.get(startNode).getNodeTmTotal();
        GRIDnodeWeightTime tempNode = new GRIDnodeWeightTime();

        long startTimeCounter = System.nanoTime();

        tempTimeslice = thisRoadList.get(startNode+endNode).getTravelTime();
        tempWeight = thisRoadList.get(startNode+endNode).getWeightOverInterval(startTime);
        tempNode.setNodeWtTotal(tempWeight);
        tempNode.setNodeTmTotal(tempTimeslice);

        long stopTimeCounter = System.nanoTime();
        long timeToRun = ((stopTimeCounter - startTimeCounter));

        //System.out.println("tempTimeslice: "+tempTimeslice);
        /*System.out.println("startTime: "+startTime);

        System.out.println("tempWeight: "+tempWeight);*/

        totalCalcTime += timeToRun;

        //System.out.println(timeToRun + "ns required for hashtable");

        return tempNode;

        /*for(String roadId : roads.keySet())
        {
            if(roads.get(roadId).getFrom().equals(startNode)
                    && roads.get(roadId).getTo().equals(endNode))
            {
                tempTimeslice = roads.get(roadId).getTravelTime();
                tempWeight = roads.get(roadId).getWeightOverInterval(startTime);
                tempNode.setNodeWtTotal(tempWeight);
                tempNode.setNodeTmTotal(tempTimeslice);

                long stopTimeCounter = System.nanoTime();
                long timeToRun = ((stopTimeCounter - startTimeCounter));

                totalCalcTime += timeToRun;
                return tempNode;
            }
        }

        tempNode.setNodeWtTotal(-1D);
        tempNode.setNodeTmTotal(0L);
        return tempNode;*/
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
