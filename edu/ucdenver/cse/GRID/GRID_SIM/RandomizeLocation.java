package edu.ucdenver.cse.GRID.GRID_SIM;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Random;

public class RandomizeLocation 
{
	private ParseLink pl;
		
	public RandomizeLocation(ParseLink pl)
	{
		 this.pl = pl;
	}
	
	public ArrayList<StartToDestinationLocation> generateHomeToWorkLocations(String work_area, String home_area, int population, int randomize_type)
	{
		ArrayList<StartToDestinationLocation> trip = new ArrayList<StartToDestinationLocation>();
		
		ArrayList<Link> work_links = pl.parse(work_area);
		ArrayList<Link> home_links = pl.parse(home_area);
		
		if (randomize_type == 1)
			home_links = clearWorkNodesFromHomeNodes(work_links, home_links);
		
		Random rnd_home_node = new Random();
		Random rnd_work_node = new Random();
				
		for(int i=0; i<population; i++)
		{
			int a = rnd_home_node.nextInt(home_links.size());
			int b = rnd_work_node.nextInt(work_links.size());
			Link source_node = home_links.get(a);
			Link destination_node = work_links.get(b);
			
			trip.add(new StartToDestinationLocation(source_node, destination_node));
			
		}
		return trip;
	}
	
	/**
	 * removes the work area nodes from the home area nodes, and returns home area nodes
	 * */
	private static ArrayList<Link> clearWorkNodesFromHomeNodes(ArrayList<Link> work_links, ArrayList<Link> home_links)
		{
			for(int i=0; i<work_links.size(); i++)
			{
				for(int j=0; j<home_links.size(); j++)
				{
					if(work_links.get(i).equals(home_links.get(j)))
					{
						home_links.remove(work_links.get(i));
					}
				}
			}		
			return home_links;
		}
	
	
//	public static void main(String[] args) 
//	{
//		ParseLink pn = new ParseLink();
//		RandomizeLocation rndLoc = new RandomizeLocation(pn);
//		String work_area = "./data/PubloDowntownLinks.txt";
//		String home_area = "./data/PuebloLinks.txt";
//		ArrayList<StartToDestinationLocation> trips = rndLoc.generateHomeToWorkLocations(work_area, home_area, 100);
//		for(int i=0; i<trips.size(); i++)
//		{
//			System.out.println(trips.get(i).toString());
//		}
//		
//	}
	
	
}
