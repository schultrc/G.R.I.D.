/*
    Using code from the following website as example to follow in creation
    of our Dijkstra's implementation:
    http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
*/

package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;

public class GRIDselfishAlg {

    private ConcurrentMap<Long, GRIDintersection> nodes;
    private ConcurrentMap<Long, GRIDroad> edges;
    private Set<GRIDintersection> visited;
    private Set<GRIDintersection> unVisited;
    private ConcurrentMap<GRIDintersection, Double> currentPathTotal;
    private Map<GRIDintersection,GRIDintersection> previousNodes;

    public GRIDselfishAlg(GRIDmap selfishMap){

        this.nodes = selfishMap.getIntersections();
        this.edges = selfishMap.getRoads();
    }

    public GRIDroute findPath(GRIDintersection from, GRIDintersection to){
        visited = new HashSet<GRIDintersection>();
        unVisited = new HashSet<GRIDintersection>();
        currentPathTotal = new ConcurrentHashMap<>();
        previousNodes = new HashMap<GRIDintersection,GRIDintersection>();
        currentPathTotal.put(from,0D);
        unVisited.add(from);

        while(unVisited.size() > 0){
            GRIDintersection node = getMin(unVisited);
            visited.add(node);
            unVisited.remove(node);
            findOptimalEdges(node);
        }

        GRIDroute finalPath = new GRIDroute();
        GRIDintersection step = to;

        finalPath.nodes.add(step);
        if(previousNodes.get(step) == null)
        {
            System.out.println("I guess it's null, friend.");
            return null;
        }

        while(previousNodes.get(step)!= null)
        {
            step = previousNodes.get(step);
            finalPath.nodes.add(step);
        }

        Collections.reverse(finalPath.nodes);

        System.out.println("end of line");
        return finalPath;
    }

    private GRIDintersection getMin(Set<GRIDintersection> nodes){
        GRIDintersection min = null;
        for(GRIDintersection node : nodes){
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

    private void findOptimalEdges(GRIDintersection startNode)
    {
        ArrayList<GRIDintersection> adjNodes = getAdjNodes(startNode);

        for(GRIDintersection endNode : adjNodes)
        {
            if(getOptimalEdgeWeight(endNode) > getOptimalEdgeWeight(startNode) + calcEdgeWeight(startNode, endNode))
            {
                currentPathTotal.put(endNode,getOptimalEdgeWeight(startNode) + calcEdgeWeight(startNode, endNode));

                Set keys = previousNodes.keySet();

                for (Iterator i = keys.iterator(); i.hasNext();)
                {
                    GRIDintersection key = (GRIDintersection) i.next();
                    GRIDintersection value = (GRIDintersection) previousNodes.get(key);
                }

                previousNodes.put(endNode,startNode);
                unVisited.add(endNode);
            }
        }
        Set keys = previousNodes.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();)
        {
            GRIDintersection key = (GRIDintersection) i.next();
            GRIDintersection value = (GRIDintersection) previousNodes.get(key);
        }
    }

    private Double getOptimalEdgeWeight(GRIDintersection endNode)
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

    private Double calcEdgeWeight(GRIDintersection startNode, GRIDintersection endNode)
    {
        for(Long roadId : edges.keySet())
        {
            if(edges.get(roadId).getFrom().getId().equals(startNode.getId())
               && edges.get(roadId).getTo().getId().equals(endNode.getId()))
            {
                return (edges.get(roadId).getLength() * edges.get(roadId).getCurrentSpeed());
            }
        }

        return -1D;
    }

    private ArrayList<GRIDintersection> getAdjNodes(GRIDintersection node)
    {
        ArrayList<GRIDintersection> adjNodes = new ArrayList<GRIDintersection>();

        for(Long key : edges.keySet())
        {
            if((edges.get(key).getFrom().getId()).equals(node.getId()) && !isVisited(edges.get(key).getTo()))
            {
                adjNodes.add(edges.get(key).getTo());
            }
        }

        return adjNodes;
    }

    private boolean isVisited(GRIDintersection node)
    {
        for (GRIDintersection intrx : visited)
        {
            if(intrx.getId().equals(node.getId()))
            {
                return true;
            }
        }

        return false;
    }

    private GRIDroad evalMultipleEdges(){
        GRIDroad bestRoad = new GRIDroad(-1L);

        return bestRoad;
    }
}
