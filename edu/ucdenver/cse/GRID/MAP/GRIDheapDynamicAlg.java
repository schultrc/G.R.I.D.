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

    public GRIDroute shortestPath(GRIDmap graph, GRIDagent thisAgent, Long currentTime) {
        GRIDfibHeap pq = new GRIDfibHeap();

        Map<String, GRIDfibHeap.Entry> entries = new HashMap<String, GRIDfibHeap.Entry>();
        GRIDnodeWeightTime startNodeValues;
        currentPathTotal = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> previousIntersections = new ConcurrentHashMap<>();
        Long thisTimeslice = currentTime/1000;
        Long totalTravelTime = thisTimeslice;
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

        pq.decreaseKey(entries.get(agtFrom), 0.0, 0.0, thisTimeslice);

        // prime the while loop with the start node, which is the starting min
        GRIDfibHeap.Entry curr = pq.dequeueMin();
        System.out.println("curr: "+curr.getValue()+" wt: "+curr.getWtTotal()+ " tm: "+curr.getTmTotal());

        Long tempTmTotal = 0L;

        while (!pq.isEmpty())
        {
            //System.out.println("\nnew while iteration");
            currentPathTotal.put(curr.getValue(), tempNode);

            /* Update the priorities/weights of all of its edges.
            *   */
            for (Map.Entry<String, Double> arc : graph.edgesFrom(curr.getValue()).entrySet()) {
                if (currentPathTotal.containsKey(arc.getKey())) continue;

                /* Compute the cost of the path from the source to this node,
                 * which is the cost of this node plus the cost of this edge.
                 */
                tempNode = graph.calcWeight(curr.getValue(), arc.getKey(),
                        currentPathTotal.get(curr.getValue()).getNodeTmTotal());

                /* If the length of the best-known path from the source to
                 * this node is longer than this potential path cost, update
                 * the cost of the shortest path.
                 */
                GRIDfibHeap.Entry dest = entries.get(arc.getKey());

                if ((tempNode.getNodeWtTotal()+curr.getWtTotal()) < dest.getWtTotal())
                {
                    // test code begin
                    //System.out.println(curr.getValue()+" tmTotal: "+currentPathTotal.get(curr.getValue()).getNodeTmTotal());
                    Long tempTime = currentPathTotal.get(curr.getValue()).getNodeTmTotal();
                    //System.out.println(arc.getKey()+" TmTotal: "+tempNode.getNodeTmTotal());
                    tempNode.setNodeTmTotal(tempTime+tempNode.getNodeTmTotal());
                    tempTmTotal = tempNode.getNodeTmTotal();
                    //System.out.println("tempTmTotal: "+tempTmTotal);
                    // test code end

                    //System.out.println("decreasing key for "+dest);
                    pq.decreaseKey(dest, 0D, (tempNode.getNodeWtTotal()+curr.getWtTotal()),tempTmTotal);
                    previousIntersections.put(dest.getValue(),curr.getValue());
                }
            }

            /* Grab the current node.  The algorithm guarantees that we now
             * have the shortest distance to it.
             */

            curr = pq.dequeueMin();
            tempNode.setNodeTmTotal(curr.getTmTotal());
            //System.out.println("curr: "+curr.getValue()+" wt: "+curr.getWtTotal()+ " tm: "+curr.getTmTotal());
            if(curr.getValue().equals(agtTo)) totalTravelTime = curr.getTmTotal();
        }
        //totalTravelTime = curr.getTmTotal();

        GRIDroute finalPath = new GRIDroute();
        String step = agtTo;

        finalPath.Intersections.add(step);
        if(previousIntersections.get(step) == null)
        {
            System.out.println("\nI guess it's null, friend.");
            return null;
        }

        // test code begin
        long finalTravelTime = 0L;
        // test code end

        /* Create the final path from source to destination */
        while(previousIntersections.get(step)!= null)
        {
            // test code begin
            long tempLong = currentPathTotal.get(step).getNodeTmTotal();
            // test code end

            step = previousIntersections.get(step);
            finalPath.Intersections.add(step);

            // test code begin
            finalTravelTime += currentPathTotal.get(step).getNodeTmTotal();
            //System.out.println("step: "+(tempLong));
            //System.out.println("time: "+(tempLong-currentPathTotal.get(step).getNodeTmTotal()));
            // test code end
        }

        Collections.reverse(finalPath.Intersections);

        // test code begin
        //System.out.println("size: "+currentPathTotal.size());
        System.out.println("total time (439): "+totalTravelTime);
        System.out.println("totalCalcTime: "+totalCalcTime);
        //System.out.println("final total time: "+finalTravelTime);
        // test code end

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

        finalPath.setcalculatedTravelTime(currentPathTotal.get(agtTo).getNodeTmTotal());
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

