package edu.ucdenver.cse.GRID.MAP;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;
import org.junit.Test;

public class GRIDtestRunner{

    private GRIDmapReader myReader;
    private GRIDmap myMap = new GRIDmap(); // myReader.readMapFile("..\\..\\..\\..\\..\\data\\SmallNetwork2.xml"); //

    private GRIDintersection from = new GRIDintersection();
    private GRIDintersection to = new GRIDintersection();


    @Test
    public void runTest()
    {
        from.setId(1L);
        to.setId(10L);

        GRIDselfishAlg test = new GRIDselfishAlg(myMap);
        GRIDroute outRoute = new GRIDroute();

        addRoad(1L,1L,2L,10.0,50.0,myMap);
        addRoad(2L,2L,3L,11.0,50.0,myMap);
        addRoad(3L,2L,4L,10.0,50.0,myMap);
        addRoad(4L,3L,5L,10.0,50.0,myMap);
        addRoad(5L,4L,5L,10.0,50.0,myMap);

        addRoad(6L,5L,6L,10.0,50.0,myMap);
        addRoad(7L,5L,7L,11.0,50.0,myMap);
        addRoad(8L,6L,8L,10.0,50.0,myMap);
        addRoad(9L,7L,8L,10.0,50.0,myMap);
        addRoad(10L,8L,10L,12.0,50.0,myMap);
        addRoad(11L,8L,9L,15.0,50.0,myMap);
        addRoad(12L,9L,10L,6.0,50.0,myMap);

        outRoute = test.findPath(from,to);
        ListIterator<GRIDintersection> pathIterator = outRoute.nodes.listIterator();

        assertNotNull(myMap);
        assertNotNull(outRoute);
        assertTrue(outRoute.nodes.size() > 0);

        System.out.print("\nPath: ");
        for (GRIDintersection node : outRoute.nodes)
        {
            System.out.print(node.getId());
            if(!node.getId().equals(to.getId()))
                System.out.print(",");
        }

        System.out.print("\n\nAnd we're done.");
    }

    private void addRoad(Long rdId, Long fromId, Long toId, Double length, Double currSpeed, GRIDmap testMap)
    {
        GRIDintersection tempFrom = new GRIDintersection();
        tempFrom.setId(fromId);
        GRIDintersection tempTo   = new GRIDintersection();
        tempTo.setId(toId);

        GRIDroad tempRoad = new GRIDroad(rdId);

        tempRoad.setFrom(tempFrom);
        tempRoad.setTo(tempTo);
        tempRoad.setLength(length);
        tempRoad.setCurrentSpeed(currSpeed);
        testMap.addRoad(tempRoad);
    }
}