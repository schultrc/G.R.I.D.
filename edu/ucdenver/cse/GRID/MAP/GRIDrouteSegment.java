package edu.ucdenver.cse.GRID.MAP;

public class GRIDrouteSegment {
    private String road_ID;
    private String intersection_ID;
    private Long timeAtRoadExit;

    public GRIDrouteSegment() {}
    public GRIDrouteSegment(String inputRoadID, Long exitTime) {
        this.road_ID = inputRoadID;
        this.timeAtRoadExit = exitTime;
    }

    public String getRoadID() { return road_ID; }
    public Long getTimeAtRoadExit() { return timeAtRoadExit; }

    public void setRoadID(String inputRoadID) { this.road_ID = inputRoadID; }
    public void setTimeAtRoadExit(Long inputStartTime) { this.timeAtRoadExit = inputStartTime; }

    public String toString() {
        return this.road_ID+" ("+this.timeAtRoadExit+")";
    }
}
