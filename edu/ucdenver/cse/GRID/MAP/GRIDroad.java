package edu.ucdenver.cse.GRID.MAP;


import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.List;
import java.math.BigDecimal;

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
	public double getWeightOverInterval(Long intervalStartTime)
	{ // vehiclesCurrentlyOnRoad
		Double theWeight = 0.0,
			   capMinusActual = maxCapacity-this.getAvgVehicleCount(intervalStartTime);
		if(getCurrentSpeed() == 0)
			return MAX_WEIGHT;

		if(capMinusActual <= 0.0) {
			theWeight = this.Length/this.getCurrentSpeed();
			theWeight += calcEmissions(this.getCurrentSpeed() + this.getEmissionsAtTime(intervalStartTime));
		}
		else {
			theWeight = this.Length/(this.getCurrentSpeed()*capMinusActual);
			theWeight += calcEmissions(this.getCurrentSpeed() + this.getEmissionsAtTime(intervalStartTime));
		}

		return theWeight;
	}

	/*
	 * This is a first "approximation" for an emissions utility function
	 * based on the graph from the emissions_graph_sketch.pdf document.
	 * The value returned represents CO2 in grams/mile (adjusted for meters).
	 */
	public double calcEmissions (Double base) {
		int exp = 0;
		Double emissionsModifier = 1609.34/this.Length,
			   emissions = 0.0,
			   veryLargeNumber = 0.0;

		veryLargeNumber = 3876685312500000000000000.0;
		emissions += (-751/veryLargeNumber)*Math.pow(base, 17);

		veryLargeNumber = 3192564375000000000000000.0;
		emissions += (510179.0/veryLargeNumber)*Math.pow(base, 16);

		veryLargeNumber = 1534886718750000000000.0;
		emissions += (-93437.0/veryLargeNumber)*Math.pow(base, 15);

		veryLargeNumber = 491163750000000000000.0;
		emissions += (6977527.0/veryLargeNumber)*Math.pow(base, 14);

		veryLargeNumber = 2806650000000000000.0;
		emissions += (-6375373.0/veryLargeNumber)*Math.pow(base, 13);

		veryLargeNumber = 28066500000000000000.0;
		emissions += (7401385339.0/veryLargeNumber)*Math.pow(base, 12);

		veryLargeNumber = 467775000000000000.0;
		emissions += (-10748594201.0/veryLargeNumber)*Math.pow(base, 11);

		veryLargeNumber = 71442000000000000.0;
		emissions += (109449918439.0/veryLargeNumber)*Math.pow(base, 10);

		veryLargeNumber = 28576800000000000.0;
		emissions += (-2256198105319.0/veryLargeNumber)*Math.pow(base, 9);

		veryLargeNumber = 57153600000000000.0;
		emissions += (180317060929469.0/veryLargeNumber)*Math.pow(base, 8);

		veryLargeNumber = 2245320000000000.0;
		emissions += (-218847480649027.0/veryLargeNumber)*Math.pow(base, 7);

		veryLargeNumber = 89812800000000.0;
		emissions += (207162673320349.0/veryLargeNumber)*Math.pow(base, 6);

		veryLargeNumber = 58378320000000.0;
		emissions += (-2400950603209441.0/veryLargeNumber)*Math.pow(base, 5);

		veryLargeNumber = 90810720000000.0;
		emissions += (48865091547708299.0/veryLargeNumber)*Math.pow(base, 4);

		veryLargeNumber = 58212000000.0;
		emissions += (-288507131120381.0/veryLargeNumber)*Math.pow(base, 3);

		veryLargeNumber = 388080000.0;
		emissions += (11651456905537.0/veryLargeNumber)*Math.pow(base, 2);

		veryLargeNumber = 673200.0;
		emissions += (-71110999427.0/veryLargeNumber)*base;

		emissions += 160885;

		return emissions/emissionsModifier;
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
