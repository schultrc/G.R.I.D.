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

        // test code begin
        List<Long> testSet = new ArrayList<>();

        // test code end

        // roadList forEach test 455935394177911580 106292026_0
        graph.loadRoadList(roads);

        for (String node : graph)
            entries.put(node, pq.enqueue(node, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0L));

        pq.decreaseKey(entries.get(agtFrom), 0.0, 0.0, thisTimeslice);

        // prime the while loop with the start node, which is the starting min
        GRIDfibHeap.Entry curr = pq.dequeueMin();

        while (!pq.isEmpty())
        {
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
                    Long tempTmTotal = currentPathTotal.get(curr.getValue()).getNodeTmTotal();
                    //tempTmTotal = tempTmTotal + tempNode.getNodeTmTotal();

                    // test code begin:
                    totalTravelTime = tempTmTotal;
                    //System.out.println("totalTravelTime: "+tempTmTotal);
                    // test code end

                    pq.decreaseKey(dest, 0D, (tempNode.getNodeWtTotal()+curr.getWtTotal()),tempTmTotal);

                    previousIntersections.put(dest.getValue(),curr.getValue());
                }
            }

            /* Grab the current node.  The algorithm guarantees that we now
             * have the shortest distance to it.
             */
            curr = pq.dequeueMin();
        }

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
        int counter = 0;
        // test code end

        /* Create the final path from source to destination */
        while(previousIntersections.get(step)!= null)
        {
            step = previousIntersections.get(step);
            finalPath.Intersections.add(step);
            // test code begin
            ++counter;
            finalTravelTime += currentPathTotal.get(step).getNodeTmTotal();
            // test code end
        }

        Collections.reverse(finalPath.Intersections);

        System.out.println("total time (439): "+totalTravelTime);
        System.out.println("final total time: "+finalTravelTime+" | counter: "+counter);

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

