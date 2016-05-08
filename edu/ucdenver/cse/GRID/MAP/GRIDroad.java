package edu.ucdenver.cse.GRID.MAP;


import java.util.concurrent.ConcurrentHashMap;

public class GRIDroad {
	
	// Each road is a one way
	
	private static final Double ourDefaultValue = (double) 1;

	private String Id   = "";
	private String to   = "";
	private String from = "";
	
	// Defined in meters
	private double Length;
		
	// Defined in km/hr
	private double maxSpeed;

	// Defined in km/hr
	private double currentSpeed;

	// Use a long as the key, which represents miliseconds since midnight, January 1, 1970
	private ConcurrentHashMap<Long, Double> roadWeight = new ConcurrentHashMap<Long, Double>();
	
	// Max capacity is defined in vehicles per hour
	private double maxCapacity;
	
	public double getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(double maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

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

	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "GRIDroad [Id=" + Id + ", to=" + to + ", from=" + from + ", Length=" + Length + ", maxSpeed=" + maxSpeed
				+ ", currentSpeed=" + currentSpeed + ", roadCapacity=" + roadWeight + "]";
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
			if (this.roadWeight.replace(time, (this.roadWeight.get(time) + 1)) == null) {
				this.roadWeight.put(time, ourDefaultValue);
			}		
	}
	
	public double getCapacityAtTime(Long time) {
		if (this.roadWeight.containsKey(time) ) {
			return this.roadWeight.get(time);
		}
		
		return -1;
	}
	
	public boolean setWeightAtTime(Long time, double capacity) {
		if (this.roadWeight.containsKey(time)) {
			System.out.println("ERROR: Time already has a value for: " +
		                       this.Id + " at time: " +
							   time.toString());	

			return false;
		}
		else {
			this.roadWeight.put(time, capacity);
			return true;
		}
			
	}
}
