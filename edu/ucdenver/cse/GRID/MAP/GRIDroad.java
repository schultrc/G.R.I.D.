package edu.ucdenver.cse.GRID.MAP;


import edu.ucdenver.cse.GRID.GRID_ALGORITHM.GRIDutilityFunction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.List;

public class GRIDroad {
	
	// Each road is a one way
	
	private static final Double ourDefaultValue = (double) 1;
	private static final Double MAX_WEIGHT = 2000000.0;

	private String Id   = "";
	private String to   = "";
	private String from = "";
	
	// Defined in meters
	private double Length;
		
	// Defined in km/hr
	private double maxSpeed;

	// Defined in km/hr
	private double currentSpeed = -1;

	// Use a long as the key, which represents miliseconds since midnight, January 1, 1970
	// we need to enhance this map to also hold emissions on the road at that time based on
	// the number of vehicles traveling at the current speed on the road, or we could just
	// use an additional map for emissions exclusively
	private ConcurrentHashMap<Long, Double> vehiclesCurrentlyOnRoadAtTime = new ConcurrentHashMap<Long, Double>();
	private ConcurrentHashMap<Long, Double> emissionsCurrentlyOnRoadAtTime = new ConcurrentHashMap<>();
	
	// Max capacity is defined in vehicles per hour
	private double maxCapacity;
	
	public double getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(double maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public GRIDroad(String theId) { Id = theId; }
	
	public String getId() { return Id; 	}

	public void setId(String id) { Id = id; }
	
	public String getFrom() { return from; }

	public void setFrom(String from) { this.from = from; }

	public String getTo() { return to; }

	public void setTo(String to) { this.to = to; }

	@Override
	public String toString() {
		return "GRIDroad [Id=" + Id + ", to=" + to + ", from=" + from + ", Length=" + Length + ", maxSpeed=" + maxSpeed
				+ ", currentSpeed=" + currentSpeed + ", roadCapacity=" + vehiclesCurrentlyOnRoadAtTime + "]";
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
		if (this.currentSpeed < 0) {
			return this.getMaxSpeed();
		}
		return this.currentSpeed;
	}

	public void setCurrentSpeed(double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}
	
	public void addToWeight(Long time) {
		// If there is already an offset, add to it
		if(this.vehiclesCurrentlyOnRoadAtTime.containsKey(time)) {
			this.vehiclesCurrentlyOnRoadAtTime.replace(time, (this.vehiclesCurrentlyOnRoadAtTime.get(time) + 1));
		}
		else {
			this.vehiclesCurrentlyOnRoadAtTime.put(time, ourDefaultValue + 1);
		}
	}

	public void addEmmissionsAtTime(Long time, Double emissions) {
		if(this.emissionsCurrentlyOnRoadAtTime.containsKey(time)) {
			this.emissionsCurrentlyOnRoadAtTime.replace(time,
					this.emissionsCurrentlyOnRoadAtTime.get(time) + emissions);
		}
		else {
			this.emissionsCurrentlyOnRoadAtTime.put(time, emissions);
		}
	}
	
	public void subFromWeight(Long time) {
		// If there is already an offset, add to it
		
		if(this.vehiclesCurrentlyOnRoadAtTime.containsKey(time)) {
			this.vehiclesCurrentlyOnRoadAtTime.replace(time, (this.vehiclesCurrentlyOnRoadAtTime.get(time) - 1));
			if (this.vehiclesCurrentlyOnRoadAtTime.get(time) < 0) {
				this.vehiclesCurrentlyOnRoadAtTime.replace(time, 0.0);
			}
		}
		else {
			// This should never happen
			this.vehiclesCurrentlyOnRoadAtTime.put(time, 0.0);
		}
	}

	public void subEmmissionsAtTime(Long time, Double emissions) {
		if(this.emissionsCurrentlyOnRoadAtTime.containsKey(time)) {
			this.emissionsCurrentlyOnRoadAtTime.replace(time,
					this.emissionsCurrentlyOnRoadAtTime.get(time) - emissions);
			if (this.emissionsCurrentlyOnRoadAtTime.get(time) < 0) {
				this.emissionsCurrentlyOnRoadAtTime.replace(time, 0.0);
			}
		}
		else {
			// This should never happen
			this.emissionsCurrentlyOnRoadAtTime.put(time, 0.0);
		}
	}

	public double getWeightAtTime(Long time) {
		if (this.vehiclesCurrentlyOnRoadAtTime.containsKey(time) ) {
			return this.vehiclesCurrentlyOnRoadAtTime.get(time) + this.getDefaultWeight();
		}
		
		return this.getDefaultWeight();
	}

	public double getEmissionsAtTime(Long time) {
		if (this.emissionsCurrentlyOnRoadAtTime.containsKey(time) ) {
			return this.emissionsCurrentlyOnRoadAtTime.get(time) + this.getDefaultWeight();
		}

		return 0.0; // is there a default emissions level...?
	}

	/* so an agent arrives at link01 at time 0.0
	*  the link is 1000 long and fspeed is 12/5, so 80s req'd to traverse link01
	*  so we take all the weights from roadWeight[0] to roadWeight[79] and either AVG or MAX them
	*  and that is the weight for that link*/
	public double getTimeWeightOverInterval(Long intervalStartTime)
	{ // vehiclesCurrentlyOnRoad
		Double timeWeight = 0.0,
			   capMinusActual = maxCapacity-this.getAvgVehicleCount(intervalStartTime);
		GRIDutilityFunction calculator = new GRIDutilityFunction();

		if(getCurrentSpeed() == 0)
			return MAX_WEIGHT;

		if(capMinusActual <= 0.0) {
			timeWeight = this.Length/this.getCurrentSpeed();
		}
		else {
			timeWeight = this.Length/(this.getCurrentSpeed()*capMinusActual);
		}

		return timeWeight;
	}

	public double getEmissionsWeightOverInterval(Long intervalStartTime) {
		Double emissionsWeight = 0.0,
			   avgEmissions = this.getAvgEmissions(intervalStartTime);
		GRIDutilityFunction calculator = new GRIDutilityFunction();

		emissionsWeight += calculator.calcEmissions(this.getCurrentSpeed(),this.getLength()) + avgEmissions;

		return emissionsWeight;
	}
	
	public boolean setWeightAtTime(Long time, double capacity) {
		if (this.vehiclesCurrentlyOnRoadAtTime.containsKey(time)) {
			System.out.println("ERROR: Time already has a value for: " +
		                       this.Id + " at time: " +
							   time.toString());	

			return false;
		}
		else {
			this.vehiclesCurrentlyOnRoadAtTime.put(time, capacity);
			return true;
		}			
	}
	
	private double getDefaultWeight() {
		double theWeight;
		
		// using maxSpeed. Should this be currentSpeed???
		theWeight = this.Length / (this.maxSpeed*this.maxCapacity);
		
		return theWeight; }

	private double getAvgVehicleCount(Long intervalStartTime){
		int numberOfKeys = 0;
		Double avgVehicleCount = 0.0,
			   timeOnLink = this.Length/this.getCurrentSpeed(),
			   timeInterval = intervalStartTime + timeOnLink;

		for(Long i = intervalStartTime; i < timeInterval; i++)
		{
			if(this.vehiclesCurrentlyOnRoadAtTime.containsKey(i)) {
				avgVehicleCount += this.vehiclesCurrentlyOnRoadAtTime.get(i);
				numberOfKeys++;
			}
		}

		if( numberOfKeys > 1 )
			avgVehicleCount /= numberOfKeys;

		return avgVehicleCount;
	}

	private double getAvgEmissions(Long intervalStartTime){
		int numberOfKeys = 0;
		Double avgEmissions = 0.0,
				timeOnLink = this.Length/this.getCurrentSpeed(),
				timeInterval = intervalStartTime + timeOnLink;

		for(Long i = intervalStartTime; i < timeInterval; i++)
		{
			if(this.emissionsCurrentlyOnRoadAtTime.containsKey(i)) {
				avgEmissions += this.emissionsCurrentlyOnRoadAtTime.get(i);
				numberOfKeys++;
			}
		}

		if( numberOfKeys > 1 )
			avgEmissions /= numberOfKeys;

		return avgEmissions;
	}

	public Long getTravelTime()
	{
		return Math.round(this.Length/this.getCurrentSpeed());
	}


	public void fillRoadWeight(int rdID) // ConcurrentHashMap<Long, Double>
	{
		List<Long> weights = new LinkedList<>();
		ConcurrentHashMap<Long,Double> weightMap = new ConcurrentHashMap<>();

		for(Long i=0L; i<5000; i++)
		{
			if(rdID==10)
				weightMap.put(i,5.0+i);
			if(rdID==11)
				weightMap.put(i,5000.0+i);
			if(rdID==12)
				weightMap.put(i,1.0+i);
		}

		this.vehiclesCurrentlyOnRoadAtTime = weightMap;
	}
}
