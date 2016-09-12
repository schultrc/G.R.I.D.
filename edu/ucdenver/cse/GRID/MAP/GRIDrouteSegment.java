package edu.ucdenver.cse.GRID.MAP;

public class GRIDrouteSegment {
    private String road_ID;
    private String intersection_ID;
    private Long timeAtRoadExit;

    public GRIDrouteSegment() {}
    public GRIDrouteSegment(String inputRoadID) {
        this.road_ID = inputRoadID;
    }

    public String getRoadID() { return road_ID; }
    public Long getTimeAtRoadExit() { return timeAtRoadExit; }

    public void setRoadID(String inputRoadID) { this.road_ID = inputRoadID; }
    public void setTimeAtRoadExit(Long inputStartTime) { this.timeAtRoadExit = inputStartTime; }

    public String toString() {
        return "Road: "+this.road_ID+" Time at Road Exit: "+this.timeAtRoadExit;
    }
}
