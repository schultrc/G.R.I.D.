package Population;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ParseNodes 
{
	public ParseNodes(){}
	
	public ArrayList<Node> parse(String trace_path)
		{
			ArrayList<Node> node = new ArrayList<Node>();
			String line="";
					
			try
			{
				FileReader file_reader = new FileReader(trace_path);
				BufferedReader reader = new BufferedReader(file_reader);
				
				while((line = reader.readLine()) != null)
				{
					String[] split_line = line.split(" ");
					node.add(new Node(Double.parseDouble(split_line[1]), Double.parseDouble(split_line[2])));
				}
			
			}
			catch(Exception ex){ }
			//printNodes(node);
			return node;
		}

	private void printNodes(ArrayList<Node> node)
	{
		for(Node i: node)
			System.out.println(i.toString());
	}

	
//==================================================================
						//main
//==================================================================

	public static void main(String[] args) 
	{
		String trace_path = "./data/PuebloNodes.txt";
		ParseNodes node  = new ParseNodes();
		node.parse(trace_path);
	}
}
