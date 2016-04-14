/*
    Using code from the following website as example to follow in creation
    of our Dijkstra's implementation:
    http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
*/

package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;

public class selfishAlg {

    private ConcurrentMap<Long, GRIDintersection> nodes;
    private ConcurrentMap<Long, GRIDroad> edges;
    private Set<GRIDintersection> visited;
    private Set<GRIDintersection> unVisited;
    private ConcurrentMap<GRIDintersection, GRIDintersection> previousNodes;
    private ConcurrentMap<GRIDintersection, Double> currentPathTotal;

    public selfishAlg(GRIDMap selfishMap){

        this.nodes = selfishMap.getIntersections();
        this.edges = selfishMap.getRoads();
    }

    public void findPath(GRIDintersection source){
        visited = new HashSet<GRIDintersection>();
        unVisited = new HashSet<GRIDintersection>();
        currentPathTotal = new ConcurrentHashMap<>();
        previousNodes = new ConcurrentHashMap<>();
        currentPathTotal.put(source,0D);
        unVisited.add(source);

        while(unVisited.size() > 0){
            GRIDintersection node = getMin(unVisited);
            visited.add(node);
            unVisited.remove(node);
            findOptimalEdges(node);
        }
    }

    private GRIDintersection getMin(Set<GRIDintersection> nodes){
        GRIDintersection min = null;
        for(GRIDintersection node : nodes){
            if(min == null){
                min = node;
            }
            else{
                if(getOptimalEdgeWeight(node) < getOptimalEdgeWeight(min)){
                    min = node;
                }
            }
        }

        return min;
    }

    private Double getOptimalEdgeWeight(GRIDintersection endNode){
        Double w = currentPathTotal.get(endNode);
        if(w == null){
            return Double.MAX_VALUE;
        }
        else{
            return w;
        }
    }

    private void findOptimalEdges(GRIDintersection node){
        ArrayList<GRIDintersection> adjNodes = getAdjNodes(node);
        for(GRIDintersection targetNode : adjNodes){
            if(getOptimalEdgeWeight(targetNode) > getOptimalEdgeWeight(node) + calcEdgeWeight(node, targetNode)){
                currentPathTotal.put(targetNode,getOptimalEdgeWeight(targetNode) + calcEdgeWeight(node, targetNode));
                previousNodes.put(targetNode,node);
                unVisited.add(targetNode);
            }
        }

    }

    private Double calcEdgeWeight(GRIDintersection startNode, GRIDintersection endNode){
        for(Long key : this.edges.keySet()){
            if(edges.get(key).getFrom().equals(startNode) && edges.get(key).getTo().equals(endNode)){
                return (edges.get(key).getLength() * edges.get(key).getCurrentSpeed());
            }
        }

        return -1D;
    }

    private ArrayList<GRIDintersection> getAdjNodes(GRIDintersection node){
        ArrayList<GRIDintersection> adjNodes = new ArrayList<GRIDintersection>();

        for(Long key : edges.keySet()){
            if((edges.get(key).getFrom()).equals(node) && !isVisited(edges.get(key).getTo())){
                adjNodes.add(edges.get(key).getTo());
            }
        }

        return adjNodes;
    }

    private GRIDroad evalMultipleEdges(){
        GRIDroad bestRoad = new GRIDroad(-1L);

        return bestRoad;
    }

    private boolean isVisited(GRIDintersection node){
        return visited.contains(node);
    }

    public GRIDroute returnFinalPath(){
        GRIDroute finalPath = new GRIDroute();

        return finalPath;
    }

}
