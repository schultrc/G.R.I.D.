package edu.ucdenver.cse.GRID.MAP;


import java.util.concurrent.ConcurrentHashMap;

public class Road {
	private static final Double ourDefaultValue = (double) 1;
	private Intersection from;
	private Intersection to;
	private ConcurrentHashMap<Long, Double> roadCapacity = new ConcurrentHashMap<Long, Double>();
	
	private Long Id = -1L;
	
	public Road(Long theId)
	{
		Id = theId;
	}
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	// Defined in meters
	private double Length;
	
	// Defined in km/hr
	private double maxSpeed;

	// Defined in km/hr
	private double currentSpeed;

	public Intersection getFrom() {
		return from;
	}

	public void setFrom(Intersection from) {
		this.from = from;
	}

	public Intersection getTo() {
		return to;
	}

	public void setTo(Intersection to) {
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
