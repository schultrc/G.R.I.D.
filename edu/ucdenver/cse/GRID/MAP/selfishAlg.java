/*
    Using code from the following website as example to follow in creation
    of our Dijkstra's implementation:
    http://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html
*/

package edu.ucdenver.cse.GRID.MAP;

import java.util.concurrent.*;
import java.util.*;

public class selfishAlg {

    private ConcurrentMap<Long, Intersection> nodes;
    private ConcurrentMap<Long, Road> edges;
    private Set<Intersection> visited;
    private Set<Intersection> unVisited;
    private ConcurrentMap<Intersection, Intersection> previousNodes;
    private ConcurrentMap<Intersection, Double> currentPathTotal;

    public selfishAlg(Gridmap selfishMap){

        this.nodes = selfishMap.getIntersections();
        this.edges = selfishMap.getRoads();
    }

    public void findPath(Intersection source){
        visited = new HashSet<Intersection>();
        unVisited = new HashSet<Intersection>();
        currentPathTotal = new ConcurrentHashMap<>();
        previousNodes = new ConcurrentHashMap<>();
        currentPathTotal.put(source,0D);
        unVisited.add(source);

        while(unVisited.size() > 0){
            Intersection node = getMin(unVisited);
            visited.add(node);
            unVisited.remove(node);
            findOptimalEdges(node);
        }
    }

    private Intersection getMin(Set<Intersection> nodes){
        Intersection min = null;
        for(Intersection node : nodes){
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

    private Double getOptimalEdgeWeight(Intersection endNode){
        Double w = currentPathTotal.get(endNode);
        if(w == null){
            return Double.MAX_VALUE;
        }
        else{
            return w;
        }
    }

    private void findOptimalEdges(Intersection node){
        ArrayList<Intersection> adjNodes = getAdjNodes(node);
        for(Intersection targetNode : adjNodes){
            if(getOptimalEdgeWeight(targetNode) > getOptimalEdgeWeight(node) + calcEdgeWeight(node, targetNode)){
                currentPathTotal.put(targetNode,getOptimalEdgeWeight(targetNode) + calcEdgeWeight(node, targetNode));
                previousNodes.put(targetNode,node);
                unVisited.add(targetNode);
            }
        }

    }

    private Double calcEdgeWeight(Intersection startNode, Intersection endNode){
        for(Long key : this.edges.keySet()){
            if(edges.get(key).getFrom().equals(startNode) && edges.get(key).getTo().equals(endNode)){
                return (edges.get(key).getLength() * edges.get(key).getCurrentSpeed());
            }
        }

        return -1D;
    }

    private ArrayList<Intersection> getAdjNodes(Intersection node){
        ArrayList<Intersection> adjNodes = new ArrayList<Intersection>();

        for(Long key : edges.keySet()){
            if((edges.get(key).getFrom()).equals(node) && !isVisited(edges.get(key).getTo())){
                adjNodes.add(edges.get(key).getTo());
            }
        }

        return adjNodes;
    }

    private Road evalMultipleEdges(){
        Road bestRoad = new Road(-1L);

        return bestRoad;
    }

    private boolean isVisited(Intersection node){
        return visited.contains(node);
    }

    public Route returnFinalPath(){
        Route finalPath = new Route();

        return finalPath;
    }

}
