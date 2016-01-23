import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/*
 * Node class contains all Node informations
 */

public class Link {

	public Node headEnd;
	public Node tailEnd;
	public String name;
	public String id;
	public int cost = 1;
	public DirectedSparseMultigraph<Node, Link> g;
	public EdgeType linkType;
//	public int capacity = 0;
	
	public Link(String id, DirectedSparseMultigraph<Node, Link> g, EdgeType linkType, Node headEnd, Node tailEnd)
	{
		this.id = id;
		this.name = "L"+id;
		this.headEnd = headEnd;
		this.tailEnd = tailEnd;
		this.g = g;
		this.linkType = EdgeType.DIRECTED; //fixed
		if(linkType != null)
		{
			this.linkType = linkType;
		}
		addLinkToGraph();
		

	}
	public Link(String id, DirectedSparseMultigraph<Node, Link> g)
	{
		this.id = id;
		this.name = "L"+id;
		this.g = g;
		
	}
	 /*
	  * This function will return the name of the link
	  * return - String name
	  */
	public String toString()
	{
		return name + "(" + cost + ")";
	}
	
	public void addLinkToGraph()
	{
		g.addEdge(this, this.headEnd, this.tailEnd, this.linkType);
	}
	
}
