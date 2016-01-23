import java.awt.print.Printable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;

/*
 * LFA functions
 */

public class LFAf {
	private String opString = "";
	public int protectedLinks;
	public int unprotectedLinks;
	public int protectedNodes;
	public int unprotectedNodes;
	public double lfaEfficiency;
	public double lfaNodeEfficiency;
	public HashMap<Pair<Node>,Link> protectedNodePairList;
	public HashMap<Pair<Node>,Link> unprotectedNodePairList;
	public HashMap<Pair<Node>,Node> protectedNodePairListNode;
	public HashMap<Pair<Node>,Node> unprotectedNodePairListNode;
	
	public LFAf(DirectedSparseMultigraph<Node, Link> g)
	{
		this.protectedLinks = 0;
		this.unprotectedLinks = 0;
		this.protectedNodes = 0;
		this.unprotectedNodes = 0;
		this.lfaEfficiency = 0;
		this.lfaNodeEfficiency = 0;
		this.protectedNodePairList = new HashMap<Pair<Node>,Link>();
		this.unprotectedNodePairList = new HashMap<Pair<Node>,Link>();
		this.protectedNodePairListNode = new HashMap<Pair<Node>,Node>();
		this.unprotectedNodePairListNode = new HashMap<Pair<Node>,Node>();

		for(Node n1 : g.getVertices())
        {
        	for(Node n2 : g.getVertices())
            {
        		if(n1 != n2)
        		{
	//        		System.out.println("");
	        		
					int pathCost = 0;
					int neighbourDistPathCost = 0;
					int sourceneighbourPathCost = 0;
					int FailedNNeighbourPathCost = 0;
					int FailedNDestinationPathCost = 0;
					
					//Add node pair
					Pair NodePairNode = new Pair<Node>(n1,n2);
					
					//Shortest path list
					List<Link> l = n1.shrtstPth.get(n2);
					
					//S-D  cost counter
					if(l != null)
					{
						for(Link pathIterator : l)
						{
							pathCost = pathCost + pathIterator.cost;
						}
			        
					
						//Protected link check
						if(l.size()>0)
						{
							int protectedLinkFlag = 0;
							int protectedNodeFlag = 0;
							
							Node failedN = g.getEndpoints(l.get(0)).getSecond();
							
							//Iterate through neighbors
							for(Node n1neighbour : g.getNeighbors(n1))
							{
	
								neighbourDistPathCost = 0;
								sourceneighbourPathCost = 0;
								FailedNNeighbourPathCost = 0;
								FailedNDestinationPathCost = 0;
								
								//when a path's neighbor is not selected
								if(n1neighbour != g.getEndpoints(l.get(0)).getSecond()) //g.getEndpoints(l.get(0)).getSecond() is node d
								{
									
									//N-D cost calculation
									List<Link> neighbourDistList = n1neighbour.shrtstPth.get(n2);
									if(neighbourDistList != null)
									{
										for(Link neighbourDistIT : neighbourDistList)
										{
											neighbourDistPathCost = neighbourDistPathCost + neighbourDistIT.cost;
																        						
										}
									}
									
									//S-N cost calculation
									List<Link> sourceneighbourList = n1.shrtstPth.get(n1neighbour);
									
									
									
									//FN-N cost calculation
									List<Link> FailedNNeighbourList = failedN.shrtstPth.get(n1neighbour);
									
									if (FailedNNeighbourList != null)
									{
										for(Link FailedNNeighbourIT : FailedNNeighbourList)
										{
											FailedNNeighbourPathCost = FailedNNeighbourPathCost + FailedNNeighbourIT.cost;
										}
									}
									
									//FN-D cost calculation
									List<Link> FailedNDestinationList = failedN.shrtstPth.get(n2);
									if(FailedNDestinationList != null)
									{
										for(Link FailedNDestinationIT : FailedNDestinationList)
										{
											FailedNDestinationPathCost = FailedNDestinationPathCost + FailedNDestinationIT.cost;
										}
									}
									
									
									//LFA Node protection flag condition
									
									if(neighbourDistPathCost < (FailedNNeighbourPathCost + FailedNDestinationPathCost))
									{
										protectedNodeFlag  = 1;
										protectedNodePairListNode.put(NodePairNode, g.getEndpoints(l.get(0)).getSecond());
										if(unprotectedNodePairListNode.containsKey(NodePairNode))
										{
											unprotectedNodePairListNode.remove(NodePairNode);
										}
									}else{
										protectedNodeFlag  = 0;
										if(!protectedNodePairListNode.containsKey(NodePairNode))
										{
											unprotectedNodePairListNode.put(NodePairNode, g.getEndpoints(l.get(0)).getSecond());
										}
										
									}
									
									if(sourceneighbourList != null)
									{
										for(Link sourceneighbourIT : sourceneighbourList)
										{
											sourceneighbourPathCost = sourceneighbourPathCost + sourceneighbourIT.cost;
										}
									}
									
									//Protection flag condition
									if(neighbourDistPathCost < (sourceneighbourPathCost + pathCost))
									{
	//									protectedLinkFlag  = 1;
											protectedNodePairList.putIfAbsent(NodePairNode, l.get(0));
//											System.out.println("S:"+ n1 + ", D:" +n2 + ", N:"+n1neighbour);
//											System.out.println("is P because " + neighbourDistPathCost + "<" + sourceneighbourPathCost + " + " + pathCost );
											if(unprotectedNodePairList.containsKey(NodePairNode))
											{
												unprotectedNodePairList.remove(NodePairNode);
											}
																					        						
									}else{
	//									protectedLinkFlag  = 0;
										if(!protectedNodePairList.containsKey(NodePairNode))
										{
											unprotectedNodePairList.putIfAbsent(NodePairNode, l.get(0));
//											System.out.println("S:"+ n1 + ", D:" +n2 + ", N:"+n1neighbour);
//											System.out.println("is UP because " + neighbourDistPathCost + "<" + sourceneighbourPathCost + " + " + pathCost );
										}
											
									}
								}
							
							}
							
							//Declare link protection status
	
	//						if(protectedLinkFlag == 0)
	//						{
	//							this.unprotectedLinks++;
	//						}
	//						if(protectedLinkFlag == 1)
	//						{
	//							this.protectedLinks++;
	//						}
	//
	//						
	//						//Declare node protection status
	//						if(protectedNodeFlag == 0)
	//						{
	//							this.unprotectedNodes++;
	//						}
	//						if(protectedNodeFlag == 1)
	//						{
	//							this.protectedNodes++;
	//						}
						}
					}
        		}
            }
        }
		//add efficiency
		
		
		if(g.getEdgeCount() !=0 && g.getVertexCount() != 0)
		{
//			System.out.println(protectedNodePairList.size());
			lfaEfficiency = protectedNodePairList.size() * 100.0/(g.getVertexCount()*(g.getVertexCount()-1));
			lfaNodeEfficiency = protectedNodePairListNode.size() * 100.0/(protectedNodePairListNode.size() + unprotectedNodePairListNode.size());
		}

		

	}
	/*
	 * LFA calculation (link failure)
	 */
	public LFAf(DirectedSparseMultigraph<Node, Link> g, Node source, Node destination, boolean unprotectedOnly)
	{

		
		int pathCost = 0;
		
		//Shortest path list
		List<Link> l = source.shrtstPth.get(destination);
		
		//S-D  cost counter
		if(l != null)
		{
			for(Link pathIterator : l)
			{
				pathCost = pathCost + pathIterator.cost;
			}
        
		
			//Protected link check
			if(l.size()>1)
			{
				int protectedLinkFlag = 0;
				int neighbourDistPathCost = 0;
				int sourceneighbourPathCost = 0;
				int remoteProtectedLinkFlag = 0;
	
				String pqSpaceSel = "";
				//Iterate through neighbors
				for(Node neighbour : g.getNeighbors(source))
				{
				
					//when a path's neighbor is not selected
					if(neighbour != g.getEndpoints(l.get(0)).getSecond()) //Any neighbor other than a which is in the path
					{
						
						//N-D cost calculation
						List<Link> ndList = neighbour.shrtstPth.get(destination);
						
						
						if(ndList != null)
						{
							for(Link neighbourDistIT : ndList)
							{
								neighbourDistPathCost = neighbourDistPathCost + neighbourDistIT.cost;
													        						
							}
						}
						
						//S-N cost calculation
						List<Link> sourceneighbourList = source.shrtstPth.get(neighbour);
						if(sourceneighbourList != null)
						{
							for(Link sourceneighbourIT : sourceneighbourList)
							{
								sourceneighbourPathCost = sourceneighbourPathCost + sourceneighbourIT.cost;
							}
						}

						//LFA Link protection flag condition
						if(neighbourDistPathCost < sourceneighbourPathCost + pathCost)
						{
							protectedLinkFlag  = 1;
									        						
						}else{
							protectedLinkFlag  = 0;
						}
					}
				}
				/*
				 * rLFA Link failure
				 */
				if(protectedLinkFlag == 0)
				{
					
					Vector<Node> pSpace = new Vector<>();
					Vector<Node> qSpace = new Vector<>();
					Vector<Node> pqSpace = new Vector<>();
					for (Node spaceIt : g.getVertices())
					{
						if(spaceIt != source && spaceIt != destination)
						{
							List<Link> pSpaceList = source.shrtstPth.get(spaceIt);
							List<Link> qSpaceList = spaceIt.shrtstPth.get(destination);
							//Calculate spaceIt-D & S-spaceIt costs
							int spaceDCost = 0;
							int sSpaceCost = 0;
							for (Link pSpaceIt : pSpaceList)
							{
								sSpaceCost = sSpaceCost + pSpaceIt.cost;
							}
							for (Link qSpaceIt : qSpaceList)
							{
								spaceDCost = spaceDCost + qSpaceIt.cost;
							}
							//form pSpace
							if(!pSpaceList.contains(l.get(0)) && !pSpaceList.isEmpty())
							{
								if(sSpaceCost != pathCost + spaceDCost)
								{
									pSpace.add(spaceIt);
								}
							}
							//form qSpace
							if(!qSpaceList.contains(l.get(0)) && !qSpaceList.isEmpty())
							{
								if(spaceDCost != pathCost + sSpaceCost)
								{
									qSpace.add(spaceIt);
								}
							}
						}
					}
					//form pqSpace
					if(!pSpace.isEmpty() && !qSpace.isEmpty())
					{
						for (Node pqSpaceNode : pSpace)
						{
							if(qSpace.contains(pqSpaceNode))
							{
								pqSpace.add(pqSpaceNode);
							}
						}
					}
					if(!pqSpace.isEmpty())
					{
						remoteProtectedLinkFlag  = 1;
	
						if(pqSpace.size() == 1)
						{
							pqSpaceSel = pqSpace.firstElement().name;
						}else
						{
							pqSpaceSel = pqSpace.toString();
						}
					}
					
				}
				
				//Declare link protection status
				if(protectedLinkFlag == 0)
				{
					
					
					//Output string and suggestion after that
					opString = l.get(0).name + " is unprotected by LFA";
					if(unprotectedOnly == false)
					{
						if(remoteProtectedLinkFlag == 1)
						{
							if(!pqSpaceSel.contains("["))
							{
								opString = opString + "\n" + l.get(0).name + " can be protected by adding \na tunnel " + source.name + "- " + pqSpaceSel;
							}else{
								opString = opString + "\n" + l.get(0).name + " can be protected by adding \na tunnel " + source.name + "- and any of the followings " + pqSpaceSel;
							}
						}
						
					}
				}
				if(unprotectedOnly == false)
				{
					if(protectedLinkFlag == 1)
					{
						opString = l.get(0).name + " is protected by LFA";
					}
				}
			}
		}

		

	}
	/*
	 * LFA calculation (Node failure)
	 */
	public LFAf(DirectedSparseMultigraph<Node, Link> g, Node source, Node destination, boolean unprotectedOnly, boolean nodeFailure)
	{		
		
		int pathCost = 0;
		
		//Shortest path list
		List<Link> l = source.shrtstPth.get(destination);
		
		//S-D  cost counter
		if(l != null)
		{
			for(Link pathIterator : l)
			{
				pathCost = pathCost + pathIterator.cost;
			}
       
		
			//Protected link check
			if(l.size()>1)
			{
				int protectedLinkFlag = 0;
				int neighbourDistPathCost = 0;
				int FailedNNeighbourPathCost = 0;
				int FailedNDestinationPathCost = 0;
				int remoteProtectedLinkFlag = 0;
				Node failedN = g.getEndpoints(l.get(0)).getSecond();
				//(Future work) I can make it as a list of options S-e or S-h or......
				String pqSpaceSel = "";
				//Iterate through neighbors
				for(Node neighbour : g.getNeighbors(source))
				{
					
					//when a path's neighbor is not selected
					if(neighbour != g.getEndpoints(l.get(0)).getSecond()) //Any neighbor other than a which is in the path
					{
						
						//N-D cost calculation
						List<Link> ndList = neighbour.shrtstPth.get(destination);
						if(ndList != null)
						{
							for(Link neighbourDistIT : ndList)
							{
								neighbourDistPathCost = neighbourDistPathCost + neighbourDistIT.cost;
													        						
							}
						}
						
						//FN-N cost calculation
						List<Link> FailedNNeighbourList = failedN.shrtstPth.get(neighbour);
						if(FailedNNeighbourList != null)
						{
							for(Link FailedNNeighbourIT : FailedNNeighbourList)
							{
								FailedNNeighbourPathCost = FailedNNeighbourPathCost + FailedNNeighbourIT.cost;
							}
						}
						
						//FN-D cost calculation
						List<Link> FailedNDestinationList = failedN.shrtstPth.get(destination);
						if(FailedNDestinationList != null)
						{
							for(Link FailedNDestinationIT : FailedNDestinationList)
							{
								FailedNDestinationPathCost = FailedNDestinationPathCost + FailedNDestinationIT.cost;
							}
						}
						
						//LFA Node protection flag condition
						if(neighbourDistPathCost < FailedNNeighbourPathCost + FailedNDestinationPathCost)
						{
							protectedLinkFlag  = 1;
									        						
						}
						
						/*
						 * rLFA Node failure
						 */
						
						if(protectedLinkFlag == 0)
						{	
							Vector<Node> pSpace = new Vector<>();
							Vector<Node> qSpace = new Vector<>();
							Vector<Node> pqSpace = new Vector<>();
							for (Node spaceIt : g.getVertices())
							{
								if(spaceIt != source && spaceIt != destination)
								{
									List<Link> pSpaceList = source.shrtstPth.get(spaceIt);
									List<Link> qSpaceList = spaceIt.shrtstPth.get(destination);
									//Calculate spaceIt-D & S-spaceIt costs
									int spaceDCost = 0;
									int sSpaceCost = 0;
									for (Link pSpaceIt : pSpaceList)
									{
										sSpaceCost = sSpaceCost + pSpaceIt.cost;
									}
									for (Link qSpaceIt : qSpaceList)
									{
										spaceDCost = spaceDCost + qSpaceIt.cost;
									}
									//form pSpace
									if(!pSpaceList.contains(l.get(0)) && !pSpaceList.isEmpty())
									{
										if(sSpaceCost != pathCost + spaceDCost)
										{
											pSpace.add(spaceIt);
										}
									}
									//form qSpace
									if(!qSpaceList.contains(l.get(0)) && !qSpaceList.isEmpty())
									{
										if(spaceDCost != pathCost + sSpaceCost)
										{
											qSpace.add(spaceIt);
										}
									}
								}
							}
							//form pqSpace
							if(!pSpace.isEmpty() && !qSpace.isEmpty())
							{
								for (Node pqSpaceNode : pSpace)
								{
									if(qSpace.contains(pqSpaceNode))
									{
										pqSpace.add(pqSpaceNode);
									}
								}
							}
							if(!pqSpace.isEmpty())
							{
								remoteProtectedLinkFlag  = 1;
	
								if(pqSpace.size() == 1)
								{
									pqSpaceSel = pqSpace.firstElement().name;
								}else
								{
									pqSpaceSel = pqSpace.toString();
								}
							}
						}
						
					
					}
				}
				
				//Declare link protection status
				if(protectedLinkFlag == 0)
				{
					
					
					//Output string and suggestion after that
					opString = failedN.name + " is unprotected by LFA";
					if(unprotectedOnly == false)
					{
						if(remoteProtectedLinkFlag == 1)
						{
							if(!pqSpaceSel.contains("["))
							{
								opString = opString + "\n" + failedN.name + " can be protected by adding \na tunnel " + source.name + "-" + pqSpaceSel;
							}else{
								opString = opString + "\n" + failedN.name + " can be protected by adding \na tunnel " + source.name + "- and any of the followings " + pqSpaceSel;
							}
						}
						
					}
				}
				if(unprotectedOnly == false)
				{
					if(protectedLinkFlag == 1)
					{
						opString = failedN.name + " is protected by LFA";
					}
				}
			}
		}

		

	}
	

	public String toString()
	{
		return opString;	
	}
}
