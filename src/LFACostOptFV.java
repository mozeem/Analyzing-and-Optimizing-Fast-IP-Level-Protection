import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;


public class LFACostOptFV {
	public HashMap<Link, Integer> linkCostafterlfaCostOpt = new HashMap<Link, Integer>();
	public DirectedSparseMultigraph<Node, Link> tempG = new DirectedSparseMultigraph<Node, Link>();
	public double originalEfficiency;
	public double localBestEfficiency;
	
	public LFACostOptFV(DirectedSparseMultigraph<Node, Link> g, int T, int T2)
	{
		
		//Identify LFA
		LFAf lfaF = new LFAf(g);
		this.originalEfficiency = lfaF.lfaEfficiency;
		this.localBestEfficiency = lfaF.lfaEfficiency;
		
		
		//Clone g to tempG
		//Add nodes
		for(Node nodeG: g.getVertices())
		{
			Node tempGNewNode = new Node(nodeG.id, tempG);
			tempGNewNode.name = nodeG.name;
			tempGNewNode.coord_x = nodeG.coord_x;
			tempGNewNode.coord_y = nodeG.coord_y;

		}
		//add links
		for(Link linkG : g.getEdges())
		{
			Node tempGHeadEnd = null;
			Node tempGTailEnd = null;
			for(Node nodeTempG: tempG.getVertices())
			{
				if(nodeTempG.id == linkG.headEnd.id)
				{
					tempGHeadEnd = nodeTempG;
				}
				if(nodeTempG.id == linkG.tailEnd.id)
				{
					tempGTailEnd = nodeTempG;
				}
			}
			if(tempGHeadEnd != null && tempGTailEnd != null){
				Link tempGNewLink = new Link(linkG.id, tempG, null, tempGHeadEnd, tempGTailEnd);
				tempGNewLink.cost = linkG.cost;
				tempGNewLink.name = linkG.name;
			}

		}
		//Add shortest Paths for tempG
		//Dijkstra algorithm calculation
		dijkstra();
		
		//Convert tempG Link collection to a vector in order to get a random link later
		Vector<Link> tempGLinkIT = new Vector<Link>(tempG.getEdges());
		Vector<Link> gLinkIT = new Vector<Link>(g.getEdges());
		
		//Random link loop 
		LFAf lfaFTempGMain = new LFAf(tempG);
		for (int i = 0; i < T2; i++) {
			if(localBestEfficiency < 100)
			{
				int randomNum = (int)(Math.random()*g.getEdgeCount());
				
				//Change the cost T times
				int originalCost = tempGLinkIT.elementAt(randomNum).cost;
				int ranLinkLocalBestCost = originalCost;
				boolean incDecCostFlg = true;
				//Flag if Cost - T/2 > 0
	//			boolean divTIncDec = false;
	//			if(tempGLinkIT.elementAt(randomNum).cost-(T/2) >1)
	//			{
	//				divTIncDec = true;
	//			}
				for (int j = 1; j < T+1; j++) {
					
					if(lfaFTempGMain.lfaEfficiency<100){
						
						//Decrease cost
						if(incDecCostFlg == true && (originalCost - j) > 0)
						{
							tempGLinkIT.elementAt(randomNum).cost = j;
							tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost = j;
							//Dijkstra algorithm calculation
							dijkstra();
							//LFA
							LFAf lfaFTempG = new LFAf(tempG);
//							//if new efficiency is better than original one
							if(lfaFTempG.lfaEfficiency>originalEfficiency && lfaFTempG.lfaEfficiency>localBestEfficiency)
							{
//								System.out.println("i "+i+" LFA Efficiency " + lfaFTempG.lfaEfficiency +" Link:"+tempGLinkIT.elementAt(randomNum)+" Cost:"+ tempGLinkIT.elementAt(randomNum).cost);
								//adjust local efficiency 
								localBestEfficiency = lfaFTempG.lfaEfficiency;
								ranLinkLocalBestCost = tempGLinkIT.elementAt(randomNum).cost;
								//Put or replace in Hashmap
								if(linkCostafterlfaCostOpt.containsKey(tempGLinkIT.elementAt(randomNum)))
								{
									linkCostafterlfaCostOpt.remove(tempGLinkIT.elementAt(randomNum));
									linkCostafterlfaCostOpt.put(tempGLinkIT.elementAt(randomNum), tempGLinkIT.elementAt(randomNum).cost);
									linkCostafterlfaCostOpt.remove(tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd));
									linkCostafterlfaCostOpt.put(tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd), tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost);
									
								}else{
									linkCostafterlfaCostOpt.put(tempGLinkIT.elementAt(randomNum), tempGLinkIT.elementAt(randomNum).cost);
									linkCostafterlfaCostOpt.put(tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd), tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost);
								}
								
							}
							else{
								tempGLinkIT.elementAt(randomNum).cost = ranLinkLocalBestCost;
								tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost = ranLinkLocalBestCost;
								
							}
							
							
						}
						//change flag
						if((originalCost - j) < 1)
						{
							incDecCostFlg = false;
						}
						//Increase cost
						if(incDecCostFlg == false)
						{
							tempGLinkIT.elementAt(randomNum).cost = j+1;
							tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost = j+1;
							//Dijkstra algorithm calculation
							dijkstra();
							//LFA
							LFAf lfaFTempG = new LFAf(tempG);
							//if new efficiency is better than original one
//							if(lfaFTempG.lfaEfficiency>originalEfficiency && lfaFTempG.lfaEfficiency>localBestEfficiency)
//							{
//								System.out.println("i "+i+" LFA Efficiency " + lfaFTempG.lfaEfficiency +" Link:"+tempGLinkIT.elementAt(randomNum)+" Cost:"+ tempGLinkIT.elementAt(randomNum).cost);
								//adjust local efficiency 
								localBestEfficiency = lfaFTempG.lfaEfficiency;
								ranLinkLocalBestCost = tempGLinkIT.elementAt(randomNum).cost;
								//Put or replace in Hashmap
								if(linkCostafterlfaCostOpt.containsKey(tempGLinkIT.elementAt(randomNum)))
								{
									linkCostafterlfaCostOpt.remove(tempGLinkIT.elementAt(randomNum));
									linkCostafterlfaCostOpt.put(tempGLinkIT.elementAt(randomNum), tempGLinkIT.elementAt(randomNum).cost);
									linkCostafterlfaCostOpt.remove(tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd));
									linkCostafterlfaCostOpt.put(tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd), tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost);
									
									
								}else{
									linkCostafterlfaCostOpt.put(tempGLinkIT.elementAt(randomNum), tempGLinkIT.elementAt(randomNum).cost);
									linkCostafterlfaCostOpt.put(tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd), tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost);
								}
								
//							}
//							else{
//								tempGLinkIT.elementAt(randomNum).cost = ranLinkLocalBestCost;
//								tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost = ranLinkLocalBestCost;
//								
//							}
							
							
						}
		//				//even odd
		//				if(divTIncDec == true)
		//				{
		//					
		//				}
					}
	
				}//End T1 loop
				
				//Apply cost change if the efficiency is better than the original one
				if(linkCostafterlfaCostOpt.containsKey(tempGLinkIT.elementAt(randomNum)))
				{
					tempGLinkIT.elementAt(randomNum).cost = linkCostafterlfaCostOpt.get(tempGLinkIT.elementAt(randomNum));
					tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd).cost = linkCostafterlfaCostOpt.get(tempG.findEdge(tempGLinkIT.elementAt(randomNum).tailEnd, tempGLinkIT.elementAt(randomNum).headEnd));
				}
			}
			
		}//EndT2 loop
		
		//print out output stuff
//		System.out.println("Efficiency " + localBestEfficiency + "changed links" + linkCostafterlfaCostOpt);
	}
	public void dijkstra()
	{
		//Dijkstra algorithm calculation
				Transformer<Link, Integer> wtTransformer = new Transformer<Link,Integer>() {
					 public Integer transform(Link link) {
						 return link.cost;
					 }
				};
				final DijkstraShortestPath<Node,Link> alg = new DijkstraShortestPath(tempG, wtTransformer);
				for(Node n1 : tempG.getVertices())
		        {
					if(!n1.shrtstPth.isEmpty())
					{
						n1.shrtstPth.clear(); //clear if not empty
					}
		        	for(Node n2 : tempG.getVertices())
		            {
		        		if(!n1.equals(n2)){
		        		List<Link> l = alg.getPath(n1, n2);
		        		
		        		//Add the shortest path list of n1-n2 to n1 hashmap
		        			
			        		n1.shrtstPth.put(n2, l);
		        		}        		
			        }
				}
				//End Dijkstra algorithm calculation
	}
	

}
