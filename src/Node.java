import java.util.HashMap;
import java.util.List;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

/*
 * Node class contains all Node informations
 */

public class Node {
 public String name;
 public String id;
 public double coord_x;
 public double coord_y;
 public String color;
// public String type; // ROUTER, PREFIX
 
 public DirectedSparseMultigraph<Node, Link> graph;
 public HashMap<Node, List<Link>> shrtstPth  = new HashMap<Node, List<Link>>();
 
 public Node(String id, DirectedSparseMultigraph<Node, Link> g)
 {
	 this.id = id;
	 this.name = "N"+id;
	 this.color = "black";
	 this.coord_x = 0;
	 this.coord_y = 0;
	 this.graph = g;
	 
	 addNodeToGraph();
 }
 
 
 /*
  * This function will return the name of the node
  * return - String name
  */
 public String toString()
 {
	 return name;
 }
 
 public void addNodeToGraph()
 {
	 graph.addVertex(this);
	 
 }

 
}
