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

    private GRIDintersection from = new GRIDintersection("test",1d,2d);
    private GRIDintersection to = new GRIDintersection("test",1d,2d);


    @Test
    public void runTest()
    {
        from.setId("01");
        to.setId("10");

        GRIDselfishAlg test = new GRIDselfishAlg(myMap);
        GRIDroute outRoute = new GRIDroute();

        addRoad("01","01","02",10.0,50.0,myMap);
        addRoad("02","02","03",11.0,50.0,myMap);
        addRoad("03","02","04",10.0,50.0,myMap);
        addRoad("04","03","05",10.0,50.0,myMap);
        addRoad("05","04","05",10.0,50.0,myMap);

        addRoad("06","05","06",10.0,50.0,myMap);
        addRoad("07","05","07",11.0,50.0,myMap);
        addRoad("08","06","08",10.0,50.0,myMap);
        addRoad("09","07","08",10.0,50.0,myMap);
        addRoad("10","08","10",12.0,50.0,myMap);
        addRoad("11","08","09",5.0,50.0,myMap);
        addRoad("12","09","10",6.0,50.0,myMap);

        outRoute = test.findPath(from.getId(),to.getId());
        ListIterator<String> pathIterator = outRoute.nodes.listIterator();

        assertNotNull(myMap);
        assertNotNull(outRoute);
        assertTrue(outRoute.nodes.size() > 0);

        System.out.print("\nPath: ");
        for (String node : outRoute.nodes)
        {
            System.out.print(node);
            if(!node.equals(to.getId()))
                System.out.print(",");
        }

        System.out.print("\n\nAnd we're done.");
    }

    private void addRoad(String rdId, String fromId, String toId, Double length, Double currSpeed, GRIDmap testMap)
    {
        GRIDintersection tempFrom = new GRIDintersection("test",1d,2d);
        tempFrom.setId(fromId);
        GRIDintersection tempTo   = new GRIDintersection("test",1d,2d);
        tempTo.setId(toId);

        GRIDroad tempRoad = new GRIDroad(rdId);

        tempRoad.setFrom(tempFrom.getId());
        tempRoad.setTo(tempTo.getId());
        tempRoad.setLength(length);
        tempRoad.setCurrentSpeed(currSpeed);
        testMap.addRoad(tempRoad);
    }
}