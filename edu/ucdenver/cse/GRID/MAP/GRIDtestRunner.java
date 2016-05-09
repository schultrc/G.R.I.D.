package edu.ucdenver.cse.GRID.MAP;

import edu.ucdenver.cse.GRID.GRID_AGENT.GRIDagent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;
import org.junit.Test;

public class GRIDtestRunner{

    private GRIDmapReader myReader = new GRIDmapReader();
    //private GRIDmap myMap = myReader.readMapFile("data/SmallNetwork3.xml"); // SmallNetwork2 PuebloNetwork
    private GRIDmap myMap = myReader.readMapFile("data/PuebloNetwork.xml"); // SmallNetwork2 PuebloNetwork

    private GRIDagent testAgent001 = getTestAgent();
    // private GRIDintersection from = new GRIDintersection("test",1d,2d);
    // private GRIDintersection to = new GRIDintersection("test",1d,2d);

    @Test
    public void runTest()
    {
        // Pueblo start to finish 34.97s
        // from.setId("1040921516"); // from.setId("01"); // 1040921516 // 2
        // to.setId("864162469");   // to.setId("10"); // 864162469 // 50
    	long startTime = System.nanoTime();
    	
        GRIDselfishAlg test001 = new GRIDselfishAlg(testAgent001, myMap, 0L);
        GRIDroute outRoute = new GRIDroute();

        outRoute = test001.findPath();
        ListIterator<String> pathIterator = outRoute.Intersections.listIterator();

        assertNotNull(myMap);
        assertNotNull(outRoute);
        assertTrue(outRoute.Intersections.size() > 0);

        System.out.print("\nPath: ");
        for (String intrx : outRoute.Intersections)
        {
            System.out.print(intrx);
            if(!intrx.equals(testAgent001.getDestination()))
                System.out.print(",");
        }

        long stopTime = System.nanoTime();
        
        long timeToRun = ((stopTime - startTime)/1000000);
        
        System.out.print("Took " + timeToRun + " Seconds");
        System.out.print("\n\nAnd we're done.");
    }

    private GRIDagent getTestAgent()
    { // String Id, String newLink, String origin, String destination
        String agtID = "testAgent001",
                currentLink = "myLink001",
                currentIntrx = "1040921516", // 1040921516 // 2
                destIntrx = "864162469";    // 864162469  // 50


        GRIDagent myAgent = new GRIDagent(agtID,currentLink,currentIntrx,destIntrx);

        return myAgent;
    }
}
