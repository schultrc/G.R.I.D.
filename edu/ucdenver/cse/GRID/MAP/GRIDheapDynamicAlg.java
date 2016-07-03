/*
    Using code from the following website as example to follow in creation
    of our Dijkstra's implementation:
    Keith Schwartz
*/

package edu.ucdenver.cse.GRID.MAP;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;

import java.util.concurrent.*;
import java.util.*;

public class GRIDheapDynamicAlg {

    private ConcurrentMap<String, GRIDnodeWeightTime> currentPathTotal;
    private ConcurrentMap<String, GRIDroad> roads;
    private long totalCalcTime = 0L;

    public ArrayList<String> shortestPath(GRIDmap graph, GRIDagent thisAgent, Long currentTime) {
        GRIDfibHeap pq = new GRIDfibHeap();

        Map<String, GRIDfibHeap.Entry> entries = new HashMap<String, GRIDfibHeap.Entry>();
        Map<String, Double> result = new HashMap<String, Double>();
        GRIDnodeWeightTime startNodeValues;
        currentPathTotal = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> previousIntersections = new ConcurrentHashMap<>();
        Long thisTimeslice = currentTime/1000;
        String agtFrom, agtTo;

        agtFrom = graph.getRoad(thisAgent.getCurrentLink()).getTo();
        agtTo = thisAgent.getDestination();
        roads = graph.getRoads();
        startNodeValues = new GRIDnodeWeightTime();
        startNodeValues.setNodeWtTotal(0.0);
        startNodeValues.setNodeTmTotal(thisTimeslice);
        GRIDnodeWeightTime tempNode = startNodeValues;

        // source/destination check
        System.out.println("agtFrom: "+agtFrom);
        System.out.println("agtTo: "+agtTo);

        // DUMB check - prevent elsewhere
        if (agtTo.equals(agtFrom)) {
            return null;
        }

        // roadList forEach test 455935394177911580 106292026_0
        graph.loadRoadList(roads);

        for (String node : graph)
            entries.put(node, pq.enqueue(node, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0L));

        pq.decreaseKey(entries.get(agtFrom), 0.0, 0.0);

        GRIDfibHeap.Entry curr = pq.dequeueMin();

        while (!pq.isEmpty())
        {
            result.put(curr.getValue(), curr.getPriority());
            currentPathTotal.put(curr.getValue(), tempNode);

            /* Update the priorities of all of its edges.
            *   */
            for (Map.Entry<String, Double> arc : graph.edgesFrom(curr.getValue()).entrySet()) {
                //if (result.containsKey(arc.getKey())) continue;
                if (currentPathTotal.containsKey(arc.getKey())) continue;

                /* Compute the cost of the path from the source to this node,
                 * which is the cost of this node plus the cost of this edge.
                 */
                double pathCost = curr.getPriority() + arc.getValue();
                tempNode = graph.calcWeight(curr.getValue(), arc.getKey(),
                        currentPathTotal.get(curr.getValue()).getNodeTmTotal());
                                            /*calcEdgeWeight(curr.getValue(), arc.getKey(), graph,
                                            currentPathTotal.get(curr.getValue()).getNodeTmTotal());*/

                /* If the length of the best-known path from the source to
                 * this node is longer than this potential path cost, update
                 * the cost of the shortest path.
                 */
                GRIDfibHeap.Entry dest = entries.get(arc.getKey());

                //if (pathCost < dest.getPriority())
                if ((tempNode.getNodeWtTotal()+curr.getWtTotal()) < dest.getWtTotal())
                {
                    //pq.decreaseKey(dest, pathCost);
                    pq.decreaseKey(dest, pathCost, (tempNode.getNodeWtTotal()+curr.getWtTotal()));
                    previousIntersections.put(dest.getValue(),curr.getValue());

                    /*if(curr.getValue().equals("177956314") || arc.getKey().equals("177956314") ||
                       arc.getKey().equals("177958998") || curr.getValue().equals("177926280") ||
                            curr.getValue().equals("177958998") || curr.getValue().equals("177922522")) {
                        System.out.println("start: "+curr.getValue()+"|end: "+arc.getKey());
                        System.out.println("weight: "+(tempNode.getNodeWtTotal()+curr.getWtTotal()));
                    }*/
                }
            }

            /* Grab the current node.  The algorithm guarantees that we now
             * have the shortest distance to it.
             */
            curr = pq.dequeueMin();

            if(curr.getValue() == agtTo) {
                System.out.println("This should be the destination: " + curr.getValue());
                pq.clearFibHeap();
            }
        }

        ArrayList<String> finalPath = new ArrayList<String>();
        ArrayList<String> finalPathResult = new ArrayList<String>();
        String step = agtTo;

        finalPath.add(step);
        if(previousIntersections.get(step) == null)
        {
            System.out.println("\nI guess it's null, friend.");
            return null;
        }

        /* Create the final path from source to destination */
        //System.out.println("all previous intersections:"+previousIntersections);
        while(previousIntersections.get(step)!= null)
        {
            step = previousIntersections.get(step);
            //System.out.println("step: "+step);
            finalPath.add(step);
        }
        Collections.reverse(finalPath);

        /* * * * * * * * * * * * * * * * * * * * * * * * * * *
         * START OUTPUT FOR TESTING
         * This for loop returns the keys and values for the
         * previousIntersections ConcurrentMap (unsorted)
         * * * * * * * * * * * * * * * * * * * * * * * * * * *
        Map<String,String> orderedPath = new HashMap<>();
        Set keys = previousIntersections.keySet();

        for (Iterator itr = keys.iterator(); itr.hasNext();)
        {
            String key = (String) itr.next();
            String value = (String) previousIntersections.get(key);
            String newStart = value;
            System.out.print("K: "+key+" V: "+value+" ");
            orderedPath.put(key,value);

        }

        System.out.println("\nThis List: "+orderedPath);
        System.out.println("\nFinal Path: "+finalPath);
        * END OUTPUT FOR TESTING */
        
        return finalPath;
    }

    private GRIDnodeWeightTime calcEdgeWeight(String startNode, String endNode, GRIDmap graph, long startTime)
    {
        double tempWeight = 0.0;
        long tempTimeslice = 0L;
        //long startTime = currentPathTotal.get(startNode).getNodeTmTotal();
        GRIDnodeWeightTime tempNode = new GRIDnodeWeightTime();

        long startTimeCounter = System.nanoTime();

        tempTimeslice = graph.getRoadListItem(startNode+endNode).getTravelTime();
        tempWeight = graph.getRoadListItem(startNode+endNode).getWeightOverInterval(startTime);
        tempNode.setNodeWtTotal(tempWeight);
        tempNode.setNodeTmTotal(tempTimeslice);

        long stopTimeCounter = System.nanoTime();
        long timeToRun = ((stopTimeCounter - startTimeCounter));

        totalCalcTime += timeToRun;

        return tempNode;
    }

}

