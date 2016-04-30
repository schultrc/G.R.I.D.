package edu.ucdenver.cse.GRID.GRID_SIM;

public class StartToDestinationLocation 
{
	public Link start;
	public Link destination;
	
	public StartToDestinationLocation(Link start, Link destination)
	{
		this.start = start;
		this.destination = destination;
	}

	public Link getStartLocation()
	{
		return start;
	}
	
	public Link getDectinationLocation()
	{
		return destination;
	}
	
	public String toString()
	{
		return "FROM: " + start.toString() + "       TO: " + destination.toString();
	}
	
}
