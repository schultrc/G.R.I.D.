package Population;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Random;

public class RandomizeLocation 
{
	private ParseNodes pn;
	
	
	public RandomizeLocation(ParseNodes pn)
	{
		 this.pn = pn;
	}
	
	public ArrayList<StartToDestinationLocation> generateHomeToWorkLocations(String work_area, String home_area, int population)
	{
		ArrayList<StartToDestinationLocation> trip = new ArrayList<StartToDestinationLocation>();
		
		ArrayList<Node> work_node = pn.parse(work_area);
		ArrayList<Node> home_node = pn.parse(home_area);
		
//		System.out.println("Work_node size: " + work_node.size());
//		System.out.println("Home_node size: " + home_node.size());
		home_node = clearWorkNodesFromHomeNodes(work_node, home_node);
//		System.out.println("Home_node size after modification: " + home_node.size());
		
		Random rnd_home_node = new Random();
		Random rnd_work_node = new Random();
				
		for(int i=0; i<population; i++)
		{
			int a = rnd_home_node.nextInt(home_node.size());
			int b = rnd_work_node.nextInt(work_node.size());
			Node source_node = home_node.get(a);
			Node destination_node = work_node.get(b);
			
			trip.add(new StartToDestinationLocation(source_node, destination_node));
			
			//home_node.remove(a); //only one people from one home
			
			//rnd_home_node = new Random(home_node.size());
			
		}
		
		
//		for(int i=1; i<work_node.size(); i++)
//				{
//					System.out.println(work_node.get(i).toString());
//				}
		return trip;
	}
	
	/**
	 * removes the work area nodes from the home area nodes, and returns home area nodes
	 * */
	private static ArrayList<Node> clearWorkNodesFromHomeNodes(ArrayList<Node> work_node, ArrayList<Node> home_node)
		{
			for(int i=0; i<work_node.size(); i++)
			{
				for(int j=0; j<home_node.size(); j++)
				{
					if(work_node.get(i).equals(home_node.get(j)))
					{
						home_node.remove(work_node.get(i));
					}
				}
			}
		
			return home_node;
		}
	
	
//	public static void main(String[] args) 
//	{
//		ParseNodes pn = new ParseNodes();
//		RandomizeLocation rndLoc = new RandomizeLocation(pn);
//		String work_area = "./data/PuebloDowntownNodes.txt";
//		String home_area = "./data/PuebloNodes.txt";
//		ArrayList<StartToDestinationLocation> trips = rndLoc.generateHomeToWorkLocations(work_area, home_area, 100);
//		
//		for(int i=0; i<trips.size(); i++)
//				{
//						System.out.println("X: " + trips.get(i).getStartLocation().getX());
//						System.out.println("Y: " + trips.get(i).getStartLocation().getY());
//						
//						System.out.println("X: " + trips.get(i).getDectinationLocation().getX());
//						System.out.println("Y: " + trips.get(i).getDectinationLocation().getY());
//	
//				}
//	}
	
	
}
