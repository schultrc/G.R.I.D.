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
    private GRIDroute origRoute;
    private GRIDroute newRoute;
    private GRIDroute myRoute;
    private Long x; private Long y; // Future Use for now 05/08
    private String currentLink;     // Future Use 05/08
    private String origin;
    private String destination;
    private Long fuelEconomy;
    private Double emissions;
    private Long vehOccupancy;
    private Long vehSize;
    private double departureTime;
    private boolean routeHasChanged = false;
    
    // These 2 values are for matsim integration only. Could be put into extended class
    private boolean simCalcFlag;       // Is this one of OUR agents, so we can change its route
    private boolean needsDestination;  // The event we use to create new agents does not have access to their destination

	public GRIDagent(String Id, String curLink, String origin, String destination, boolean simFlag, boolean needDest) {
        //super();

        this.Id = Id;
        this.myRoute = new GRIDroute();
        this.currentLink = curLink;
        this.origin = origin;
        this.destination = destination;
        this.simCalcFlag = simFlag;
        this.needsDestination = needDest;
    }

    public void setRoute(GRIDroute newPath){ myRoute = newPath;}
    public void setX(Long x) {
        this.x = x;
    }
    public void setY(Long y) {
        this.y = y;
    }
    public void setLink(String newLink){ this.currentLink = newLink; }
    public void setOrigin(String newOrigin){ this.origin = newOrigin; }
    public void setDestination(String newDest){ this.destination = newDest; }
    public void setFuelEconomy(Long newFuelEconomy){ this.fuelEconomy = newFuelEconomy; }
    public void setEmissions(Double mewEmissions){ this.emissions = mewEmissions; }
    public void setVehOccupancy(Long vehOccupancy){ this.vehOccupancy = vehOccupancy; }
    public void setvehSize(Long vehSize){ this.vehSize = vehSize; }
	public void setDepartureTime(double newDepartureTime) { this.departureTime = newDepartureTime; }
	public void setRouteHasChanged(boolean hasRouteChanged) { this.routeHasChanged = hasRouteChanged; }
	
	// matsim integration only. Move to extended class?
	// Should we always force to false? once the destination is set, we'll never go back to needing
	public void setNeedsDestinationFlag(boolean theFlag) { this.needsDestination = theFlag; } 

    public String getId(){return this.Id;}
    public GRIDroute getRoute(){ return this.myRoute;}
    public String getCurrentLink(){ return this.currentLink; }
    public String getOrigin(){ return this.origin; }
    public String getDestination(){ return this.destination; }
    public Long getFuelEconomy(){ return this.fuelEconomy; }
    public Long getVehOccupancy(){ return this.vehOccupancy; }
    public Long getVehSize(){ return this.vehSize; }
    public double getDepartureTime() { return this.departureTime; }
	public boolean getRouteHasChanged() { return this.routeHasChanged; }
	
	// matsim integration only. Move to extended class?
	public boolean getNeedsDestination() { return this.needsDestination; }
	public boolean getSimCalcFlag() { return this.simCalcFlag; }

    @Override
    // step through both arrays in order, do something like:
    // intx - road - intx - road - intx - road etc
    // we need a method to translate intersections into roads and store the roads
    // in the GRIDroute class object
    public String toString()
    {
        for (String intx : myRoute.getIntersections())
        {

        }
        return "GRIDagent Id=" + this.Id + " Origin: " + this.origin + " Dest: " + this.destination;
    }


}