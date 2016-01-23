import java.util.HashMap;
import java.util.Vector;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/*
 * NOT USED
 */

public class LFACostOpt {
	private int randomNum;
	private DirectedSparseMultigraph<Node, Link> optG;
	public DirectedSparseMultigraph<Node, Link> tempG;
	public HashMap<String, Integer> linkCostbeforelfaCostOpt = new HashMap<String, Integer>();
	public HashMap<String, Integer> linkCostafterlfaCostOpt = new HashMap<String, Integer>();
	public String txtLFACostOpt;
	
	public LFACostOpt(DirectedSparseMultigraph<Node, Link> g, VisualizationViewer<Node, Link> vv, int T, int T2)
	{
		this.txtLFACostOpt = "";
		this.tempG = g;
		this.optG = g;
		int minimum = 0;
		int maximum = optG.getEdgeCount()-1;
		double lfaEfficiency = 0;
		double OriginalLfaEfficiency = 0;
		
		
		
		//Get Efficiency before LFACostOpt
		vv.updateUI();
		LFAf lfaf = new LFAf(g);
		if(g.getEdgeCount()>1 && lfaf.protectedLinks > 0)
		{
			OriginalLfaEfficiency = lfaf.lfaEfficiency;
		}
		
		//Get link cost before LFA cost Opt
		Vector<Link> linksVect = new Vector<Link>();
		for (Link link : optG.getEdges())
		{
			linksVect.add(link);
			linkCostbeforelfaCostOpt.put(link.id, link.cost);
		}
		
		
		
		//start selecting links
		int originalT = T;
		double localLfaEfficiency =0;

			for (int i = 0; i < T2; i++) 
			{
				
				if(localLfaEfficiency<100)
				{
				
					this.randomNum = minimum + (int)(Math.random()*maximum); 
					
					Link randomLink = linksVect.elementAt(randomNum);
					Link optRandomLink = randomLink;
					Link optRandomLinkRev = tempG.findEdge(randomLink.tailEnd, randomLink.headEnd);
					int originalRandomLinkCost = linkCostbeforelfaCostOpt.get(randomLink.id);
					int stopDecFlag = 0;
					int bestRandomLinkCost = originalRandomLinkCost;
					int incDecDividerFlag = 0;
					
					T = originalT;
					if(((originalT/2)-originalRandomLinkCost) <= 1)
					{
						incDecDividerFlag = 1;
					}		
					
					//
					if(!linkCostafterlfaCostOpt.containsKey(optRandomLink.id))
					{
						while (T>0 && lfaEfficiency<100) 
						{
							
							if(incDecDividerFlag == 0)
							{
								if(stopDecFlag == 0)
								{
									optRandomLink.cost = optRandomLink.cost - 1;
									
								}
								if(optRandomLink.cost == 0)
								{
									stopDecFlag =1;
									optRandomLink.cost = originalRandomLinkCost;
								}
								if(stopDecFlag == 1)
								{
									optRandomLink.cost = optRandomLink.cost + 1;
									
								}
								
								tempG.findEdge(optRandomLink.headEnd, optRandomLink.tailEnd).cost = optRandomLink.cost;
								tempG.findEdge(optRandomLink.tailEnd, optRandomLink.headEnd).cost = optRandomLink.cost;
								vv.updateUI();
								LFAf lfafOpt = new LFAf(tempG);
								
								
								lfaEfficiency = lfafOpt.lfaEfficiency;
								
								if(lfaEfficiency>OriginalLfaEfficiency && optRandomLink.cost != 0 && lfaEfficiency>localLfaEfficiency)
								{
									bestRandomLinkCost = optRandomLink.cost;
									localLfaEfficiency = lfaEfficiency;						
									
								}
							}//end incDecDividerFlag == 0
							
							//make the graph
							
							//when higher than cost is 150/2
							if(incDecDividerFlag == 1)
							{
								if(stopDecFlag == 0)
								{
									optRandomLink.cost = optRandomLink.cost - 1;
									
								}
								if(Math.round((originalT/2)) >= T)
								{
									stopDecFlag =1;
									optRandomLink.cost = originalRandomLinkCost;
								}
								if(stopDecFlag == 1)
								{
									optRandomLink.cost = optRandomLink.cost + 1;
									
								}
								
								tempG.findEdge(optRandomLink.headEnd, optRandomLink.tailEnd).cost = optRandomLink.cost;
								tempG.findEdge(optRandomLink.tailEnd, optRandomLink.headEnd).cost = optRandomLink.cost;
								vv.updateUI();
								LFAf lfafOpt1 = new LFAf(tempG);
								lfaEfficiency = lfafOpt1.lfaEfficiency;
								if(lfaEfficiency>OriginalLfaEfficiency && optRandomLink.cost != 0 && lfaEfficiency>localLfaEfficiency)
								{
									bestRandomLinkCost = optRandomLink.cost;
									localLfaEfficiency=lfaEfficiency;								
								}
							}//end incDecDividerFlag ==1
							
							
							T = T-1;
						}//end while
						if (localLfaEfficiency>OriginalLfaEfficiency){
							linkCostafterlfaCostOpt.put(optRandomLink.id, bestRandomLinkCost);
							linkCostafterlfaCostOpt.put(optRandomLinkRev.id, bestRandomLinkCost);
						}
					}//end if deal with a link once
		
				}//end if local eff <100	
			

				//Start send output beginning sentence
				if (localLfaEfficiency>OriginalLfaEfficiency)
				{
					this.txtLFACostOpt = "LFACostOpt is suggesting an efficiency of " + localLfaEfficiency + "% instead of the current " + OriginalLfaEfficiency +"%, by changing the costs of the following links:\n";
				}
				
				for(Link link : g.getEdges())
				{
					//set back the graph to default
					link.cost = linkCostbeforelfaCostOpt.get(link.id);
					
					//Print output suggestion
					if (localLfaEfficiency>OriginalLfaEfficiency)
					{
						int costBefore = linkCostbeforelfaCostOpt.get(link.id);
						int costAfter = costBefore;
						if(linkCostafterlfaCostOpt.get(link.id) != null && linkCostafterlfaCostOpt.get(link.id)>0){
						 costAfter = linkCostafterlfaCostOpt.get(link.id);
						}
						if (linkCostafterlfaCostOpt.get(link.id) != null && costAfter>costBefore)
						{
							this.txtLFACostOpt = txtLFACostOpt + link.name + " from " + linkCostbeforelfaCostOpt.get(link.id) + " to " + linkCostafterlfaCostOpt.get(link.id) + "\n";
						}
					}
				}
				if (localLfaEfficiency>OriginalLfaEfficiency)
				{
					this.txtLFACostOpt = txtLFACostOpt + "\nDo you like to apply the new optimization plan? or click \"No\" and try again!";
				}//end if
			}//end for loop
	}//end of class
	

}
