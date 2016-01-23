
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;




public class NetworkFileHandler {
	
	JFileChooser fc = new JFileChooser();
	FileFilter extensionFilter = new FileNameExtensionFilter("LGF (*.lgf)", "lgf");
	String fileTitle = "";
	String lineBreak = System.getProperty("line.separator");
	public DirectedSparseMultigraph<Node, Link> graph;
	/*
	 * Write (save) LGF file
	 */
	public NetworkFileHandler(DirectedSparseMultigraph<Node, Link> g, Layout<Node, Link> JUNGlayout)
	{
		 
		
		fc.addChoosableFileFilter(extensionFilter);
		fc.setFileFilter(extensionFilter);
		int returnVal = fc.showSaveDialog(fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            fileTitle = file.getName();
            try {
            	
            	File statText = null;
            	if (file.getName().toLowerCase().endsWith(".lgf") == true)
            	{
            		
            		statText = new File(file.getPath().replace(file.getPath().substring(file.getPath().length()-4), ".lgf"));
            	}else{
            		statText = new File(file.getPath()+ ".lgf");
            	}
                FileOutputStream is = new FileOutputStream(statText);
                OutputStreamWriter osw = new OutputStreamWriter(is);    
                Writer w = new BufferedWriter(osw);
                /*
                 * Start writing LGF file
                 */
                
                //Nodes header
                w.write("@nodes" + lineBreak + "label\tname\tcoordinates\t");
                //Nodes
                
                for (Node node : g.getVertices())
                {
                	w.write(lineBreak + "" + node.id + "\t"+ node.name + "\t(" + JUNGlayout.transform(node).getX() + "," + JUNGlayout.transform(node).getY() + ")\t");
                }
                //Links	 header
                w.write(lineBreak + "@arcs" + lineBreak + "\t\tlabel\tname\tcost\t");
                //Links
                for (Link link : g.getEdges())
                {
                	w.write(lineBreak + g.getSource(link).id + "\t" + g.getDest(link).id + "\t" + link.id + "\t" + link.name + "\t" +  link.cost);
                }
                
                //If attributes are needed in the future (ex. author, version, copyright, title)
                //w.write(lineBreak + "@attributes" + lineBreak + "");
                w.close();
            } catch (IOException e1) {
	    		int errorMsg = JOptionPane.showConfirmDialog(null, "Problem writing to file!", "Error", JOptionPane.CLOSED_OPTION, 0);
            }
        }
	}
	/*
	 * Read LGF file
	 */
      public  NetworkFileHandler(DirectedSparseMultigraph<Node, Link> g, Layout<Node, Link> JUNGlayout, String d)
        {
    	  this.graph = g;
			fc.addChoosableFileFilter(extensionFilter);
			fc.setFileFilter(extensionFilter);
			int returnVal = fc.showOpenDialog(fc);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
			    File file = fc.getSelectedFile();
			    
			    
			    try{
			    	BufferedReader buffer = new BufferedReader(new FileReader(file.getPath()));
			    	String line = buffer.readLine();
			    	StringBuilder fileContent = new StringBuilder();
			    	//LGF Section flags
			    	int sectionFlg = 0;
			    	int nodesetLine = -1;
			    	int edgesetLine = -1;
			    	int nodesLine = -1;
			    	int edgesLine = -1;
			    	int attributesLine = -1;
			    	//Identify arrays
			    	String nodesetArray [] [] = new String[99999][];
			    	String edgesetArray [] [] = new String[99999999][];
			    	String nodesArray [] [] = new String[99999][];
			    	
			    	int lineCnt = 1;
			    	int numberofNodes = 0;
			    	int numberofLabeledNodes = 0;
			    	int numberofLinks = 0;
			    	boolean oneLineFlg = false;
			    	//Detect one line files (Error: wrong format)
			    	if(line.contains("@nodes") && line.contains("label"))
			    	{
			    		oneLineFlg = true;	
			    		System.out.println("yes");
			    	}
			    	
			    	if(oneLineFlg == false)
			    	{
				    	while (line != null)
				    	{			
				    		
				    		
				    		//Section Found
				    		if(line.contains("@") == true)
				    		{
				    			//Section identifier
				    			String section = line.substring(line.indexOf("@")+1,line.length());
				    			if(section.contains("nodeset"))
				    			{
				    				nodesetLine = lineCnt;
				    				sectionFlg = 1;
				    			}else if (section.contains("edgeset") || section.contains("arcs")) {
				    				edgesetLine = lineCnt;
				    				sectionFlg = 2;
								}else if (section.contains("nodes")) {
									nodesLine = lineCnt;
									sectionFlg = 3;
									if(nodesetLine == -1 && nodesLine != -1)
									{
										nodesLine = -1;
										nodesetLine = lineCnt;
					    				sectionFlg = 1;
									}
								}else if (section.contains("edges"))
								{
									edgesLine = lineCnt;
									sectionFlg = 4;
								}else if (section.contains("attributes")) {
									attributesLine = lineCnt;
									sectionFlg = 5;
									}
				    			}
				    		if(!line.trim().startsWith("#"))
				    		{
					    		if(lineCnt > nodesetLine && sectionFlg == 1 )
					    		{
					    			//read header of nodeset
					    			if (lineCnt == nodesetLine+1)
					    			{
					    				nodesetArray[0] = line.split("\t");	
					    			}
					    			else
					    			{
					    				if(!line.trim().equals(""))
					    				{
						    				int k = lineCnt-nodesetLine-1;
						    				nodesetArray[k] = line.split("\t");
						    				numberofNodes++;
					    				}
					    			}
					    		}
					    		
					    		
					    		if(lineCnt > edgesetLine && sectionFlg == 2 )
					    		{
					    			//read header of edgeset
					    			if (lineCnt == edgesetLine+1)
					    			{
					    				edgesetArray[0] = line.split("\t");	
					    			}
					    			else
					    			{
					    				if(!line.trim().equals(""))
					    				{
						    				int k = lineCnt-edgesetLine-1;
						    				edgesetArray[k] = line.split("\t");
						    				numberofLinks++;
					    				}
					    			}
					    		}
					    		if(lineCnt > nodesLine && sectionFlg == 3 )
					    		{
					    			//read nodes
				    				if(!line.trim().equals(""))
				    				{
					    				int k = lineCnt-nodesLine-1;
					    				nodesArray[k] = line.split("\t");
					    				numberofLabeledNodes++;
				    				}
					    			
					    		}
				    		}
				    		//Read file from buffer memory
				    		fileContent.append(line);
				    		fileContent.append(lineBreak);
				    		line  = buffer.readLine();
				    		lineCnt++;
				    		
				    		}
			    	}
			    	
			    	/*
			    	 * Draw network topology
			    	 */
			    	
			    	//check if there are node before clearing the topology.
			    	if(numberofNodes>0)
			    	{
			    		if(g.getVertexCount() != 0)
			    		{
			    			
				    		removeTopology();
			    		}
			    	
			    	
			    	//Identify nodeset headers
			    	int nodeLabelCol = -1;
			    	int nodeLongitudeCol = -1;
			    	int nodeLatitudeCol = -1;
			    	int nodecoordinateCol = -1;
			    	int nodenameCol = -1;
			    	for (int j = 0; j < nodesetArray[0].length; j++)
			    	{
			    		if(nodesetArray[0][j].equals("label") || nodesetArray[0][j].equals("id"))
			    		{
			    			nodeLabelCol = j;
			    		}
			    		if(nodesetArray[0][j].equals("coordinates"))
			    		{
			    			nodecoordinateCol = j;
			    			
			    		}
			    		if(nodesetArray[0][j].equals("longitude") || nodesetArray[0][j].equals("coordinates_y"))
			    		{
			    			nodeLongitudeCol = j;
			    		}
			    		if(nodesetArray[0][j].equals("latitude")|| nodesetArray[0][j].equals("coordinates_x"))
			    		{
			    			nodeLatitudeCol = j;
			    		}	
			    		if(nodesetArray[0][j].equals("title")|| nodesetArray[0][j].equals("name"))
			    		{
			    			nodenameCol = j;
			    		}
			    	}
			    	//Identify edgeset headers
			    	int linkLabelCol = -1;
			    	int linkNameCol = -1;
			    	int linkCapacityCol = -1;
			    	int linkCostCol = -1;
			    	for (int j = 0; j < edgesetArray[0].length; j++)
			    	{
			    		if(edgesetArray[0][j].equals("label") || edgesetArray[0][j].equals("id"))
			    		{
			    			linkLabelCol = j;
			    		}
			    		if(edgesetArray[0][j].equals("name"))
			    		{
			    			linkNameCol = j;
			    		}
			    		if(edgesetArray[0][j].equals("length") || edgesetArray[0][j].equals("cost"))
			    		{
			    			linkCostCol = j;
			    		}		  
			    		if(edgesetArray[0][j].equals("capacity"))
			    		{
			    			linkCapacityCol = j;
			    		}		    		
			    	}
			    	//Draw nodes
			    	if(numberofNodes != 0)
			    	{
			    		for (int i = 0; i < numberofNodes; i++) {
			    			Node newNode = new Node(nodesetArray[i+1][nodeLabelCol].replaceFirst(" ", ""), g);
			    			//node name if it's not separated
			    			if(nodenameCol != -1)
			    			{
			    				newNode.name = nodesetArray[i+1][nodenameCol].replaceAll("\"", "");
			    			}
			    			//node location
			    			if(nodeLatitudeCol != -1 || nodeLongitudeCol != -1)
			    			{
				    			
				    			double coordX = Double.parseDouble(nodesetArray[i+1][nodeLatitudeCol].replaceFirst(" -", ""));
				    			double coordY = Double.parseDouble(nodesetArray[i+1][nodeLongitudeCol].replaceFirst(" -", ""));
				    			Point2D nodeP2D = new java.awt.geom.Point2D.Double(coordX,coordY);
				    			JUNGlayout.setLocation(newNode, nodeP2D);
			    			}
			    			if(nodecoordinateCol != -1)
			    			{
			    				String coordPair[] = new String[1]; 
			    				coordPair = nodesetArray[i+1][nodecoordinateCol].replace("(", "").replace(")", "").split(",");
			    				double coordX = Double.parseDouble(coordPair[0]);
			    				double coordY = Double.parseDouble(coordPair[1]);
			    				Point2D nodeP2D = new java.awt.geom.Point2D.Double(coordX,coordY);			    				
				    			JUNGlayout.setLocation(newNode, nodeP2D);
			    			}							
						}
			    		if(nodecoordinateCol == -1 && nodeLatitudeCol == -1 && nodeLongitudeCol == -1)
		    			{
			    			int confirmDelete = JOptionPane.showConfirmDialog(null, "Coordinates can't be identified!\nDo you like to set random ones?", "Confirm", JOptionPane.YES_NO_OPTION);
			    			if(confirmDelete == 0)
			    			{
			    				if(g.getVertexCount()>0)
			    				{
			    					int numVNodes=0; //Number of vertical nodes
			    					int numHNodes=0; //Number of Horizontal nodes
			    					int k = 100; //spacing between nodes
			    					double coordX = k;
				    				double coordY = k;
				    				for(Node node:g.getVertices())
				    				{
					    				Point2D nodeP2D = new java.awt.geom.Point2D.Double(coordX,coordY);
				    					JUNGlayout.setLocation(node, nodeP2D);
					    				
				    					if(numVNodes<Math.round(Math.sqrt(g.getVertexCount())))
					    				{
					    					coordY = coordY + k;
					    					numVNodes++;
					    					if(numVNodes == Math.round(Math.sqrt(g.getVertexCount())))
					    					{
					    						numVNodes =0;
					    						coordY = k;
					    						if(numHNodes<Math.round(Math.sqrt(g.getVertexCount()))+1)
							    				{
							    					coordX = coordX + k;
							    					numHNodes++;	
							    				}
					    					}
					    					
					    					
					    				}	
					    				
				    				}
			    				}
			    			}
		    				
		    			}
			    	}
			    	//Set nodes Names
			    	if(numberofLabeledNodes != 0)
	    			{
	    				for(Node node:g.getVertices())
	    				{
	    					for (int i = 0; i < numberofLabeledNodes; i++) {
		    					if(node.id.equals(nodesArray[i][1]))
		    					{
		    						node.name = nodesArray[i][0];
		    					}
	    					}
	    				}
	    			}
			    	//Draw Links
			    	if(numberofLinks != 0)
			    	{
			    		fileTitle = file.getName();
			    		for (int i = 0; i < numberofLinks; i++) {
			    			Node headEnd = null;
			    			Node tailEnd = null;
			    			for (Node j : g.getVertices()) {
								if(j.id.equals(edgesetArray[i+1][0]))
								{
									headEnd = j;
								}
								if(j.id.equals(edgesetArray[i+1][1]))
								{
									tailEnd = j;
								}
							}
			    			if(headEnd!=null && tailEnd != null)
			    			{
			    				if(linkLabelCol != -1)
			    				{
			    					Link link = new Link(edgesetArray[i+1][linkLabelCol], g, EdgeType.DIRECTED, headEnd, tailEnd);
			    				
				    				if(linkNameCol != -1)
				    				{
				    					link.name = edgesetArray[i+1][linkNameCol];
				    				}
				    				if(linkCostCol != -1)
				    				{
				    					link.cost = Integer.parseInt(edgesetArray[i+1][linkCostCol]);
				    				}
			    				}else{
			    					Link link = new Link(String.valueOf(i), g, EdgeType.DIRECTED, headEnd, tailEnd);
				    				
				    				if(linkNameCol != -1)
				    				{
				    					link.name = edgesetArray[i+1][linkNameCol];
				    				}
				    				if(linkCostCol != -1)
				    				{
				    					link.cost = Integer.parseInt(edgesetArray[i+1][linkCostCol]);
				    				}
			    					
			    				}
			    			}			    			
						}
			    	}
			    	
			    	}else if(numberofNodes == 0  && oneLineFlg == true || !file.getName().toLowerCase().endsWith(".lgf")) {
			    		int errormsg = JOptionPane.showConfirmDialog(null, "Wrong file format selected!", "Error", JOptionPane.CLOSED_OPTION, 0);
			    	}else if(numberofNodes == 0  && oneLineFlg == false){
			    		int confirmDelete = JOptionPane.showConfirmDialog(null, "Empty topology detected! Apply anyway?", "Warning", JOptionPane.OK_OPTION, 2);
			    		if(confirmDelete == 0)
			    		{
			    			fileTitle = file.getName();
			    			removeTopology();
			    		}
			    	}
			    	
			    }catch (IOException e){
		    		int errorMsg = JOptionPane.showConfirmDialog(null, "Problem reading file!", "Error", JOptionPane.CLOSED_OPTION, 0);
			    }
			    
			} else {
			    //Open command is cancelled
			}
    
		
	}
      public void removeTopology()
      {
    	  Collection<Link> Links = graph.getEdges();
    	  Collection<Node> Nodes = graph.getVertices();
    	  Vector<Link> LinksVec = new Vector<>(Links);
    	  Vector<Node> NodesVec = new Vector<>(Nodes);
    	  for(Link link : LinksVec)
    	  {
    		  graph.removeEdge(link);
    	  }
    	  for(Node node : NodesVec)
    	  {
    		  graph.removeVertex(node);
    	  }
      }
	

}
