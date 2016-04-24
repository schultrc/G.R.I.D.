package SmartLocationGeneration;

public class StartToDestinationLocation 
{
	public Node start;
	public Node destination;
	
	public StartToDestinationLocation(Node start, Node destination)
	{
		this.start = start;
		this.destination = destination;
	}

	public Node getStartLocation()
	{
		return start;
	}
	
	public Node getDectinationLocation()
	{
		return destination;
	}
	
	public String toString()
	{
		return "FROM: " + start.toString() + "       TO: " + destination.toString();
	}
	
}
