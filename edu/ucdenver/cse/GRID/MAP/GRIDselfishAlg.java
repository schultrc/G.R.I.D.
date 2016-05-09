/*
    Using code from the following website as example to follow in creation
    of our Dijkstra's implementation:
    http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
*/

package edu.ucdenver.cse.GRID.MAP;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;

import java.util.concurrent.*;
import java.util.*;

public class GRIDselfishAlg {

    private int testCounter;
    private ConcurrentMap<String, GRIDintersection> intersections;
    private ConcurrentMap<String, GRIDroad> roads;
    private String agtFrom;
    private String agtTo;
    private Long currentTime;
    private Set<String> visited;
    private Set<String> unVisited;
    private ConcurrentMap<String, Double> currentPathTotal;
    private Map<String,String> previousIntersections;

    public GRIDselfishAlg(GRIDagent thisAgent, GRIDmap selfishMap, Long currentTime){

        testCounter = 0;
        this.intersections = selfishMap.getIntersections();
        this.roads = selfishMap.getRoads();
        agtFrom = thisAgent.getOrigin();
        agtTo = thisAgent.getDestination();
        this.currentTime = currentTime;
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

        /* previousNodes Tester
        int test000 = previousNodes.size();
        System.out.print("\nTest000: "+test000);
        Set keys = previousNodes.keySet();

        for (Iterator i = keys.iterator(); i.hasNext();)
        {
            String key = (String) i.next();
            String value = (String) previousNodes.get(key);
            System.out.println("Here is the value: " + value);
        }*/

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

        System.out.println("\nend of line");
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

                Set keys = previousIntersections.keySet();

                for (Iterator i = keys.iterator(); i.hasNext();)
                {
                    String key = (String) i.next();
                    String value = (String) previousIntersections.get(key);
                }

                previousIntersections.put(endNode,startNode);
                unVisited.add(endNode);
            }
        }
        /*String key = "key", value = "val";
        Set keys = previousIntersections.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();)
        {
            key = (String) i.next();
            value = (String) previousIntersections.get(key);
        }
        System.out.println("Timestep " + testCounter++ + ": " + "(K: " + key + ")" + "(V: " + value + ")");*/
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
        for(String roadId : roads.keySet())
        {
            //roads.get(roadId).getFrom()
            if(roads.get(roadId).getFrom().equals(startNode)
               && roads.get(roadId).getTo().equals(endNode))
            {
                // return (roads.get(roadId).getLength() * roads.get(roadId).getCurrentSpeed());
                // return roads.get(roadId).getWeight(currentTime);
                //System.out.println("calcEdge: " + roads.get(roadId).getLength() +" "+ roads.get(roadId).getCurrentSpeed());
                //System.out.println("roads size: " + roads.size());
                //System.out.println("road time: " + intersections);
                //System.out.println("Weight: " + roads.get(roadId).getWeightAtTime(currentTime));
                return roads.get(roadId).getWeightAtTime(currentTime);
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

    /*private GRIDroad evalMultipleEdges(){
        GRIDroad bestRoad = new GRIDroad(-1L);

        return bestRoad;
    }*/
}
