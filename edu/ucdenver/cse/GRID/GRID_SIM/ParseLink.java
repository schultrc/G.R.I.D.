package edu.ucdenver.cse.GRID.GRID_SIM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ParseLink 
{
	public ParseLink(){}
	
	public ArrayList<Link> parse(String trace_path)
		{
			ArrayList<Link> link = new ArrayList<Link>();
			String line="";
					
			try
			{
				FileReader file_reader = new FileReader(trace_path);
				BufferedReader reader = new BufferedReader(file_reader);
				
				while((line = reader.readLine()) != null)
				{
					String[] split_line = line.split(",");
					link.add(new Link(split_line[0]));
				}
			
			}
			catch(Exception ex){ }
			
			return link;
		}

	private void printLinks(ArrayList<Link> link)
	{
		for(Link i: link)
			System.out.println(i.toString());
	}

	
//==================================================================
						//main
//==================================================================

	public static void main(String[] args) 
	{
		String trace_path = "./data/PuebloLinks.txt";
		ParseLink parse_link  = new ParseLink();
		ArrayList<Link> link =  parse_link.parse(trace_path);
		parse_link.printLinks(link);
	}
}
