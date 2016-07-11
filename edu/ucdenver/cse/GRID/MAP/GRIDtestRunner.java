package edu.ucdenver.cse.GRID.MAP;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;
import edu.ucdenver.cse.GRID.GRID_SIM.logWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;

public class GRIDtestRunner{

    private GRIDmapReader myReader = new GRIDmapReader();
    // SmallNetwork2 PuebloNetwork 5x5network RyeNetwork
    private GRIDmap myMap = myReader.readMapFile("data/PuebloNetwork.xml");
    private GRIDmap networkMap = graphMiddleware(myMap);
    private ArrayList myPathGreedy = new ArrayList();
    private ArrayList myPathDynamic = new ArrayList();

    private GRIDagent testAgent001 = getTestAgent();
    //private GRIDintersection from = new GRIDintersection("test",1d,2d);
    //private GRIDintersection to = new GRIDintersection("test",1d,2d);

    @Test
    public void runTest()
    { // Pueblo start to finish 34.97s

        logWriter testLW = new logWriter("data/test_output003.txt");

        testLW.writeOutput("this is another nother test");

	    System.out.println("\nStarting test. . .");
        // Pueblo start to finish 34.97s
        // from.setId("1040921516"); // from.setId("01"); // 1040921516 // 2
        // to.setId("864162469");   // to.setId("10"); // 864162469 // 50
    	Long startTime = System.nanoTime();

        GRIDheapAlg greedy = new GRIDheapAlg();
        GRIDheapDynamicAlg dyna = new GRIDheapDynamicAlg();
        //myPathGreedy = greedy.shortestPath(networkMap,"1040921516","864162469");

        //GRIDselfishAlg test001 = new GRIDselfishAlg(testAgent001, networkMap, 0L); // GRIDpathrecalc GRIDselfishAlg
        //GRIDpathrecalc test001 = new GRIDpathrecalc(testAgent001, networkMap, 0L); // GRIDpathrecalc GRIDselfishAlg
        GRIDroute outRoute = new GRIDroute();
        /*GRIDpathrecalc test001 = new GRIDpathrecalc(testAgent001, networkMap, 0L); // GRIDpathrecalc GRIDselfishAlg
        outRoute = test001.findPath();*/
        outRoute = dyna.shortestPath(networkMap,testAgent001, 0L);

        //ListIterator<String> pathIterator = outRoute.Intersections.listIterator();

        /*assertNotNull(myMap);
        assertNotNull(outRoute);
        assertTrue(outRoute.Intersections.size() > 0);*/

        //System.out.println("\nShortest path: "+myPathGreedy);
        //System.out.println("\nShortest path: "+myPathDynamic);

        System.out.print("\nPath:\n");
        for (String intrx : outRoute.Intersections)
        {
            System.out.print(intrx);
            if(!intrx.equals(testAgent001.getDestination()))
                System.out.print(",");
        }
        
	    ArrayList<String> tempPathList  = myMap.getPathByRoad(outRoute.Intersections);

        System.out.print("\n\nPath by Link:\n");
        for (String path : tempPathList)
        {
            System.out.print(path);
            if(!tempPathList.isEmpty()
               && !path.equals(tempPathList.get(tempPathList.size() - 1)))
                System.out.print(",");
        }

        System.out.println("\n\nCalculated Travel Time: "+outRoute.getcalculatedTravelTime());

        long stopTime = System.nanoTime();
        long timeToRun = ((stopTime - startTime)/1000000);
        
        System.out.print("\nTook " + timeToRun/1000.0 + " Seconds");
        System.out.print("\n\nAnd we're done.\n");
    }

    private GRIDagent getTestAgent()
    { // String Id, String newLink, String origin, String destination
        String agtID = "testAgent001",
                currentLink = "40963664_0", // 40963664_0
                currentIntrx = "1040921516", // 1040921516 // 2
                destIntrx = "864162469"; // 864162469 - 1400447055 // 177890694 PNet3 // 177849670 // 50

        GRIDagent myAgent = new GRIDagent(agtID,currentLink,currentIntrx,destIntrx, false, false);

        return myAgent;
    }

    private GRIDmap graphMiddleware(GRIDmap myGraph) {
        Long startTime = System.nanoTime();

        ArrayList<String> networkIntersections = new ArrayList<>(myMap.getIntersections().keySet());
        ArrayList<GRIDroad> networkRoads = new ArrayList<>(myMap.getRoads().values());

        for(int i = 0; i < networkIntersections.size(); i++)
        {
            if(i+1 == networkIntersections.size()){System.out.println("nodes: "+(i+1));}
            myGraph.addNode(networkIntersections.get(i));
        }

        for(int i = 0; i < networkRoads.size(); i++)
        {
            if(i+1 == networkRoads.size()){System.out.println("edges: "+(i+1));}
            myGraph.addEdge(networkRoads.get(i).getFrom(), networkRoads.get(i).getTo(), networkRoads.get(i).getLength());
        }

        long stopTime = System.nanoTime();
        long timeToRun = ((stopTime - startTime)/1000000);

        System.out.println(timeToRun/1000.0 + "s required for middleware\n");
        return myGraph;
    }
}
