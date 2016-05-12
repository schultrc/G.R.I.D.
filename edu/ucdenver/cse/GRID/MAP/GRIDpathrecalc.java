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
    private ConcurrentMap<String, Double> currentPathTotal;
    private Map<String,String> previousIntersections;

    public GRIDpathrecalc(GRIDagent thisAgent, GRIDmap selfishMap, Long currentTime){
        this.intersections = selfishMap.getIntersections();
        this.roads = selfishMap.getRoads();
        agtFrom = thisAgent.getCurrentLink();
        agtTo = thisAgent.getDestination();
        this.thisTimeslice = currentTime/1000;

        /* These are for setting vehiclesCurrentlyOnRoadAtTime
         * for testing purposes; comment out to run without using
         * vehiclesCurrentlyOnRoadAtTime in program run */
        roads.get("10").fillRoadWeight(10);
        roads.get("11").fillRoadWeight(11);
        roads.get("12").fillRoadWeight(12);
    }

    public GRIDroute findPath(){
        visited = new HashSet<String>();
        unVisited = new HashSet<String>();
        currentPathTotal = new ConcurrentHashMap<>();
        previousIntersections = new HashMap<String,String>();
        currentPathTotal.put(agtFrom,0D);
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
            System.out.println("\nI guess it's null, friend.");
            return null;
        }

        while(previousIntersections.get(step)!= null)
        {
            step = previousIntersections.get(step);
            finalPath.Intersections.add(step);
        }

        Collections.reverse(finalPath.Intersections);

        System.out.println("Returning path. . .");
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

        for(String endNode : adjNodes)
        {
            if(getOptimalEdgeWeight(endNode) > getOptimalEdgeWeight(startNode) + calcEdgeWeight(startNode, endNode))
            {
                currentPathTotal.put(endNode,getOptimalEdgeWeight(startNode) + calcEdgeWeight(startNode, endNode));

                previousIntersections.put(endNode,startNode);

                unVisited.add(endNode);
            }
        }
    }

    private Double getOptimalEdgeWeight(String endNode)
    {
        Double w = currentPathTotal.get(endNode);

        if(w == null)
        {
            return Double.MAX_VALUE;
        }
        else{
            return w;
        }
    }

    private Double calcEdgeWeight(String startNode, String endNode)
    {
        Long tempTimeslice = 0L;

        for(String roadId : roads.keySet())
        {
            if(roads.get(roadId).getFrom().equals(startNode)
                    && roads.get(roadId).getTo().equals(endNode))
            {
                tempTimeslice = roads.get(roadId).getTravelTime();
                System.out.println("slice: "+tempTimeslice);
                return roads.get(roadId).getWeightOverInterval(tempTimeslice);
            }
        }

        return -1D;
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
