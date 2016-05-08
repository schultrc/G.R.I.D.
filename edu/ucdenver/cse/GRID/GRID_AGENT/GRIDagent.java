package edu.ucdenver.cse.GRID.GRID_AGENT;

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

public class GRIDagent {
    private String Id = "";
    private Long x;
    private Long y;
    private String link;
    private String origin;
    private String destination;
    private Long fuelEconomy;
    private Long vehOccupancy;
    private Long vehSize;

    public GRIDagent(String Id, Long x, Long y, String link, String origin, String destination) {
        super();

        this.Id = Id;
        this.x = x;
        this.y = y;
        this.link = link;
        this.origin = origin;
        this.destination = destination;
    }

    public void setX(Long x) {
        this.x = x;
    }
    public void setY(Long y) {
        this.y = y;
    }
    public void setLink(String link){ this.link = link; }
    public void setOrigin(String origin){ this.origin = origin; }
    public void setDestination(String origin){ this.origin = destination; }
    public void setFuelEconomy(Long fuelEconomy){ this.fuelEconomy = fuelEconomy; }
    public void setVehOccupancy(Long vehOccupancy){ this.vehOccupancy = vehOccupancy; }
    public void setvehSize(Long vehSize){ this.vehSize = vehSize; }

    public String getId(){return Id;}
    public String getOrigin(){ return origin; }
    public String getDestination(){ return destination; }
    public Long getFuelEconomy(){ return fuelEconomy; }
    public Long getVehOccupancy(){ return vehOccupancy; }
    public Long getVehSize(){ return vehSize; }

    @Override
    public String toString() {
        return "GRIDagent [Id=" + Id + ", x=" + x + ", y=" + y + "]";
    }


}