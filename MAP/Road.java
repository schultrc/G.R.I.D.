package MAP;

public class Road {
	private Intersection from;
	private Intersection to;
	
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
}
