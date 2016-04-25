package edu.ucdenver.cse.GRID.MAP;


import java.util.concurrent.ConcurrentHashMap;

public class GRIDroad {
	
	// Each road is a one way
	
	private static final Double ourDefaultValue = (double) 1;

	private String Id = "";
	private GRIDintersection to;
	private GRIDintersection from;
	
	// Defined in meters
	private double Length;
		
	// Defined in km/hr
	private double maxSpeed;

	// Defined in km/hr
	private double currentSpeed;

	// Use a long as the key, which represents miliseconds since midnight, January 1, 1970
	private ConcurrentHashMap<Long, Double> roadCapacity = new ConcurrentHashMap<Long, Double>();
	
	
	public GRIDroad(String theId)
	{
		Id = theId;
	}
	
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	
	public GRIDintersection getFrom() {
		return from;
	}

	public void setFrom(GRIDintersection from) {
		this.from = from;
	}

	public GRIDintersection getTo() {
		return to;
	}

	public void setTo(GRIDintersection to) {
		this.to = to;
	}

	public double getLength() {
		return Length;
	}

	public void setLength(double length) {
		Length = length;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}
	
	public void addToCapacity(Long time) {
		// replace checks to ensure it already exists
			if (this.roadCapacity.replace(time, (this.roadCapacity.get(time) + 1)) == null) {
				this.roadCapacity.put(time, ourDefaultValue);
			}		
	}
	
	public double getCapacityAtTime(Long time) {
		if (this.roadCapacity.containsKey(time) ) {
			return this.roadCapacity.get(time);
		}
		
		return -1;
	}
}
