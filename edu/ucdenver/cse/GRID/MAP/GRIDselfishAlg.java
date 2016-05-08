/*
    Using code from the following website as example to follow in creation
    of our Dijkstra's implementation:
    http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
*/

package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;

public class GRIDselfishAlg {

    private ConcurrentMap<String, GRIDintersection> nodes;
    private ConcurrentMap<String, GRIDroad> edges;
    private Set<String> visited;
    private Set<String> unVisited;
    private ConcurrentMap<String, Double> currentPathTotal;
    private Map<String,String> previousNodes;

    public GRIDselfishAlg(GRIDmap selfishMap){

        this.nodes = selfishMap.getIntersections();
        this.edges = selfishMap.getRoads();
    }

    public GRIDroute findPath(String from, String to){
        visited = new HashSet<String>();
        unVisited = new HashSet<String>();
        currentPathTotal = new ConcurrentHashMap<>();
        previousNodes = new HashMap<String,String>();
        currentPathTotal.put(from,0D);
        unVisited.add(from);

        while(unVisited.size() > 0){
            String node = getMin(unVisited);
            visited.add(node);
            unVisited.remove(node);
            findOptimalEdges(node);
        }

        GRIDroute finalPath = new GRIDroute();
        String step = to;

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

        finalPath.nodes.add(step);
        if(previousNodes.get(step) == null)
        {
            System.out.println("\nI guess it's null, friend.");
            return null;
        }

        while(previousNodes.get(step)!= null)
        {
            step = previousNodes.get(step);
            finalPath.nodes.add(step);
        }

        Collections.reverse(finalPath.nodes);

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

                Set keys = previousNodes.keySet();

                for (Iterator i = keys.iterator(); i.hasNext();)
                {
                    String key = (String) i.next();
                    String value = (String) previousNodes.get(key);
                }

                previousNodes.put(endNode,startNode);
                unVisited.add(endNode);
            }
        }
        Set keys = previousNodes.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();)
        {
            String key = (String) i.next();
            String value = (String) previousNodes.get(key);
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
        for(String roadId : edges.keySet())
        {
            if(edges.get(roadId).getFrom().equals(startNode)
               && edges.get(roadId).getTo().equals(endNode))
            {
                return (edges.get(roadId).getLength() * edges.get(roadId).getCurrentSpeed());
            }
        }

        return -1D;
    }

    private ArrayList<String> getAdjNodes(String node)
    {
        ArrayList<String> adjNodes = new ArrayList<String>();

        for(String key : edges.keySet())
        {
            if(edges.get(key).getFrom().equals(node) && !isVisited(edges.get(key).getTo()))
            {
                adjNodes.add(edges.get(key).getTo());
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
