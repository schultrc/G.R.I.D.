package edu.ucdenver.cse.GRID.GRID_SIM;

public class Link 
{
	private String id;
	private double expected_weight;
	private double real_weight; 
	
	public Link(String id)
	{
		this.id = id;
	}
	
	public String getID()
	{
		return id;
	}
			
	public String toString()
	{
		return "ID: " + id;
	}

	public void setExpectedWeight(double w)
	{
		expected_weight = w;
	}
	
	public void setRealWeight(double w)
	{
		real_weight = w;
	}
	
	public Double getExpectedWeight()
	{
		return expected_weight;
	}
	
	public Double getRealWeight()
	{
		return real_weight;
	}
	
	public boolean equals(Object o)
	{
		boolean answer = false;
		
		if (o instanceof Link)
		{
			if(this.id.equals(((Link)o).id))
				answer = true;
		}
		return answer;
	}
}


