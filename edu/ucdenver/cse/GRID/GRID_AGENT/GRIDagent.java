package edu.ucdenver.cse.GRID.GRID_AGENT;

import edu.ucdenver.cse.GRID.MAP.GRIDroute;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.controler.AbstractModule;
import edu.ucdenver.cse.GRID.*;
import edu.ucdenver.cse.GRID.MAP.GRIDmap;
import edu.ucdenver.cse.GRID.MAP.GRIDmapReader;
import java.util.*;

public class GRIDagent {
    private String Id = "";
    private GRIDroute travelPath;
    private Long x; private Long y; // Future Use for now 05/08
    private String currentLink;     // Future Use 05/08
    private String origin;
    private String destination;
    private Long fuelEconomy;
    private Long vehOccupancy;
    private Long vehSize;
    private double departureTime;

	public GRIDagent(String Id, String newLink, String origin, String destination) {
        super();

        this.Id = Id;
        travelPath = new GRIDroute();
        this.currentLink = newLink;
        this.origin = origin;
        this.destination = destination;
    }

    public void setTravelPath(GRIDroute newPath){ travelPath = newPath;}
    public void setX(Long x) {
        this.x = x;
    }
    public void setY(Long y) {
        this.y = y;
    }
    public void setLink(String newLink){ currentLink = newLink; }
    public void setOrigin(String origin){ this.origin = origin; }
    public void setDestination(String origin){ this.origin = destination; }
    public void setFuelEconomy(Long fuelEconomy){ this.fuelEconomy = fuelEconomy; }
    public void setVehOccupancy(Long vehOccupancy){ this.vehOccupancy = vehOccupancy; }
    public void setvehSize(Long vehSize){ this.vehSize = vehSize; }
	public void setDepartureTime(double departureTime) { this.departureTime = departureTime; }

    public String getId(){return Id;}
    public GRIDroute getTravelPath(){ return travelPath;}
    public String getCurrentLink(){ return currentLink; }
    public String getOrigin(){ return origin; }
    public String getDestination(){ return destination; }
    public Long getFuelEconomy(){ return fuelEconomy; }
    public Long getVehOccupancy(){ return vehOccupancy; }
    public Long getVehSize(){ return vehSize; }
    public double getDepartureTime() { return departureTime; }
    
    @Override
    // step through both arrays in order, do something like:
    // intx - road - intx - road - intx - road etc
    // we need a method to translate intersections into roads and store the roads
    // in the GRIDroute class object
    public String toString()
    {
        for (String intx : travelPath.getIntersections())
        {

        }
        return "GRIDagent [Id=" + "path goes here]";
    }


}