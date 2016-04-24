package edu.ucdenver.cse.GRID.GRID_SIM;

public class Node 
{
	private double x;
	private double y;
	
	public Node(double d, double e)
	{
		this.x = d;
		this.y = e;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public String toString()
	{
		return "X = " + x + ", Y = " + y;
	}

	public boolean equals(Object o)
	{
		boolean answer = false;
		
		if (o instanceof Node)
		{
			if(this.getX() == (((Node) o).getX()) && this.getY() == (((Node) o).getY()))
				answer = true;
		}
		return answer;
	}
}


