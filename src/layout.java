import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import javax.swing.JProgressBar;

import edu.uci.ics.jung.graph.util.Pair;

/*
 * Main Class
 */

public class layout {
	private JFrame frame;
	private final ButtonGroup JUNGControl = new ButtonGroup();
	
	/**
	 * Launch the application.
	 * @return 
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					layout window = new layout();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public layout() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initialize() {
		//JUNG
		final DirectedSparseMultigraph<Node, Link> g = new DirectedSparseMultigraph<Node,Link>();		
		
		final Layout<Node, Link> JUNGlayout = new StaticLayout<Node, Link>(g);
	    JUNGlayout.setSize(new Dimension(200,200));
		
	    final VisualizationViewer<Node, Link> vv = new VisualizationViewer<Node, Link>(JUNGlayout);
	    vv.setPreferredSize(new Dimension (200,200));

	    
	    
	    /*
	     * Factories
	     */
		
	    Factory <Node> NodeFactory = new Factory<Node>() { // My vertex factory
	    	int id = g.getVertexCount();
	    	public Node create() {
            	Node newNode = new Node(Integer.toString(id), g);
            	id++;
                return newNode;
            }
	    	
        };
        
        Factory <Link> edgeFactory = new Factory<Link>() { // My edge factory
	    	int id = g.getEdgeCount();
            public Link create() {
            	Link newLink = new Link(Integer.toString(id), g);
            	id++;
            	return newLink;
            }
        };

        
		
        
	    final EditingModalGraphMouse<Node, Link> gmEdit = new EditingModalGraphMouse(vv.getRenderContext(), NodeFactory, edgeFactory);
	    final DefaultModalGraphMouse<Node, Link> gmPick = new DefaultModalGraphMouse();
	    final DefaultModalGraphMouse<Node, Link> gmTransform = new DefaultModalGraphMouse();
	    gmTransform.setMode(Mode.TRANSFORMING);
	    gmPick.setMode(Mode.PICKING);
	    gmEdit.setMode(Mode.EDITING);
	    vv.setGraphMouse(gmEdit);
//	   vv.addKeyListener(gmEdit.getModeKeyListener()); //For keyboard listing (later)
	    
	    /*
	     * Show labels
	     */
	    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
	    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
	    

	    /*
	     * Remove Popupmenu from JUNG Editing mode
	     */
        gmEdit.remove(gmEdit.getPopupEditingPlugin());
	    	    
        
        /*
         * Add custom popup menu plugin "PopupGraphMousePlugin" for Nodes and Link
         */
        
        gmPick.add(new PopupGraphMousePlugin(g));
	    
        
        /*
         * Transform Links from Quad Curve to Straight lines
         */
        
//        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        
        
        
		//End JUNG
	    
	    
	    
	    
		frame = new JFrame();
		
		final JFrame resultFrame = new JFrame("LFA coverage results");
		
		resultFrame.setVisible(false);
		resultFrame.setSize(300, 300);
		
		JPanel resultPanel = new JPanel();
		
		
		resultFrame.setBounds(100, 100, 541, 409);
		
		resultPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		resultPanel.setLayout(new BorderLayout(0, 0));
		resultFrame.setContentPane(resultPanel);
		
		
		
		frame.setBounds(100, 100, 666, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Untitled.lgf");
		
		//Start Menubar
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmClear = new JMenuItem("Clear topology");
		mnFile.add(mntmClear);
		
		JMenuItem mntmLoad = new JMenuItem("Load topology");
		mnFile.add(mntmLoad);
		
		JMenuItem mntmSave = new JMenuItem("Save topology");
		mnFile.add(mntmSave);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		mnView.setVisible(false);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		JPanel TopPanel = new JPanel();
		sl_panel.putConstraint(SpringLayout.NORTH, TopPanel, 10, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, TopPanel, 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, TopPanel, -10, SpringLayout.EAST, panel);
		panel.add(TopPanel);
		
		JPanel TopologyPanel = new JPanel();
		sl_panel.putConstraint(SpringLayout.SOUTH, TopPanel, -6, SpringLayout.NORTH, TopologyPanel);
		sl_panel.putConstraint(SpringLayout.WEST, TopologyPanel, 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.NORTH, TopologyPanel, 72, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, TopologyPanel, -10, SpringLayout.SOUTH, panel);
		panel.add(TopologyPanel);
		
		JPanel SidePanel = new JPanel();
		sl_panel.putConstraint(SpringLayout.EAST, TopologyPanel, -6, SpringLayout.WEST, SidePanel);
				
		sl_panel.putConstraint(SpringLayout.WEST, SidePanel, -185, SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.NORTH, SidePanel, 6, SpringLayout.SOUTH, TopPanel);
		sl_panel.putConstraint(SpringLayout.SOUTH, SidePanel, -10, SpringLayout.SOUTH, panel);
		SpringLayout sl_TopPanel = new SpringLayout();
		TopPanel.setLayout(sl_TopPanel);
		
		JRadioButton rdbtnPick = new JRadioButton("Pick");
		JUNGControl.add(rdbtnPick);
		
		sl_TopPanel.putConstraint(SpringLayout.WEST, rdbtnPick, 0, SpringLayout.WEST, TopPanel);
		sl_TopPanel.putConstraint(SpringLayout.SOUTH, rdbtnPick, 0, SpringLayout.SOUTH, TopPanel);
		TopPanel.add(rdbtnPick);
		
		JRadioButton rdbtnNavigate = new JRadioButton("Navigate");
		JUNGControl.add(rdbtnNavigate);
		sl_TopPanel.putConstraint(SpringLayout.WEST, rdbtnNavigate, 6, SpringLayout.EAST, rdbtnPick);
		sl_TopPanel.putConstraint(SpringLayout.SOUTH, rdbtnNavigate, 0, SpringLayout.SOUTH, rdbtnPick);
		TopPanel.add(rdbtnNavigate);
		
		JRadioButton rdbtnEdit = new JRadioButton("Edit");
		JUNGControl.add(rdbtnEdit);
		rdbtnEdit.setSelected(true);
		sl_TopPanel.putConstraint(SpringLayout.NORTH, rdbtnEdit, 0, SpringLayout.NORTH, rdbtnPick);
		sl_TopPanel.putConstraint(SpringLayout.WEST, rdbtnEdit, 6, SpringLayout.EAST, rdbtnNavigate);
		TopPanel.add(rdbtnEdit);
		
		JRadioButton rdbtnAnnotation = new JRadioButton("Annotation");
		rdbtnAnnotation.setEnabled(false);
		JUNGControl.add(rdbtnAnnotation);
		sl_TopPanel.putConstraint(SpringLayout.WEST, rdbtnAnnotation, 6, SpringLayout.EAST, rdbtnEdit);
		sl_TopPanel.putConstraint(SpringLayout.SOUTH, rdbtnAnnotation, 0, SpringLayout.SOUTH, rdbtnPick);
		TopPanel.add(rdbtnAnnotation);
		SpringLayout sl_TopologyPanel = new SpringLayout();
		sl_TopologyPanel.putConstraint(SpringLayout.NORTH, vv, 0, SpringLayout.NORTH, TopologyPanel);
		sl_TopologyPanel.putConstraint(SpringLayout.WEST, vv, 0, SpringLayout.WEST, TopologyPanel);
		sl_TopologyPanel.putConstraint(SpringLayout.SOUTH, vv, 0, SpringLayout.SOUTH, TopologyPanel);
		sl_TopologyPanel.putConstraint(SpringLayout.EAST, vv, 0, SpringLayout.EAST, TopologyPanel);
		TopologyPanel.setLayout(sl_TopologyPanel);
		TopologyPanel.add(vv);
		
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setEnabled(false);
		
		sl_TopPanel.putConstraint(SpringLayout.NORTH, progressBar, 0, SpringLayout.NORTH, TopPanel);
		sl_TopPanel.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, TopPanel);
		sl_panel.putConstraint(SpringLayout.EAST, SidePanel, -10, SpringLayout.EAST, panel);
		panel.add(SidePanel);
		SpringLayout sl_SidePanel = new SpringLayout();
		SidePanel.setLayout(sl_SidePanel);
		
		JLabel lblSideMenu = new JLabel("Side Menu");
		sl_SidePanel.putConstraint(SpringLayout.NORTH, lblSideMenu, 10, SpringLayout.NORTH, SidePanel);
		sl_SidePanel.putConstraint(SpringLayout.WEST, lblSideMenu, 54, SpringLayout.WEST, SidePanel);
		SidePanel.add(lblSideMenu);
		
		final JButton btnShortestPath = new JButton("Calculate LFA coverage");
		sl_SidePanel.putConstraint(SpringLayout.NORTH, btnShortestPath, 6, SpringLayout.SOUTH, lblSideMenu);
		sl_SidePanel.putConstraint(SpringLayout.WEST, btnShortestPath, 0, SpringLayout.WEST, SidePanel);
		sl_SidePanel.putConstraint(SpringLayout.EAST, btnShortestPath, 0, SpringLayout.EAST, SidePanel);
		
		btnShortestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		SidePanel.add(btnShortestPath);
		
		final TextArea txtCalculationResults = new TextArea();

		txtCalculationResults.setEditable(false);
		txtCalculationResults.setBackground(UIManager.getColor("Button.background"));
		final JButton lfaCostOptbtn = new JButton("LFA Cost Opt.");
		resultPanel.add(lfaCostOptbtn, BorderLayout.NORTH);
		resultPanel.add(txtCalculationResults, BorderLayout.CENTER);
		
		final Choice nodePairsList = new Choice();
		sl_SidePanel.putConstraint(SpringLayout.NORTH, nodePairsList, 14, SpringLayout.SOUTH, btnShortestPath);
		sl_SidePanel.putConstraint(SpringLayout.WEST, nodePairsList, 0, SpringLayout.WEST, btnShortestPath);
		sl_SidePanel.putConstraint(SpringLayout.EAST, nodePairsList, 0, SpringLayout.EAST, btnShortestPath);

		nodePairsList.add("");
		SidePanel.add(nodePairsList);
		
		final JTextArea unprotectedLinkStatus = new JTextArea();
		sl_SidePanel.putConstraint(SpringLayout.NORTH, unprotectedLinkStatus, 3, SpringLayout.SOUTH, nodePairsList);
		sl_SidePanel.putConstraint(SpringLayout.WEST, unprotectedLinkStatus, 0, SpringLayout.WEST, btnShortestPath);
		sl_SidePanel.putConstraint(SpringLayout.SOUTH, unprotectedLinkStatus, -10, SpringLayout.SOUTH, SidePanel);
		sl_SidePanel.putConstraint(SpringLayout.EAST, unprotectedLinkStatus, 0, SpringLayout.EAST, btnShortestPath);

		unprotectedLinkStatus.setBackground(UIManager.getColor("Button.background"));
		unprotectedLinkStatus.setLineWrap(true);
		unprotectedLinkStatus.setEditable(false);
		unprotectedLinkStatus.setRows(1);
		SidePanel.add(unprotectedLinkStatus);
		
		//End Menubar

		//Start actions
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Exit program on click
				frame.dispose();
				resultFrame.dispose();
				
			}
		});
		mntmClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				int confirmDelete = JOptionPane.showConfirmDialog(null, "You are about to clear your network, are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
        		if(confirmDelete == 0)
	        		{
					Collection<Link> Links = g.getEdges();
					Collection<Node> Nodes = g.getVertices();
					Vector<Link> LinksVec = new Vector<>(Links);
					Vector<Node> NodesVec = new Vector<>(Nodes);
					for(Link link : LinksVec)
					{
						g.removeEdge(link);
					}
					for(Node node : NodesVec)
					{
						g.removeVertex(node);
						
					}
					vv.updateUI();
        		}
			}
		});
		mntmSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NetworkFileHandler networkSave = new NetworkFileHandler(g, JUNGlayout);
				if(!networkSave.fileTitle.isEmpty())
				{
					frame.setTitle(networkSave.fileTitle);
				}
				
				
			}
		});
		mntmLoad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NetworkFileHandler networkLoad = new NetworkFileHandler(g, JUNGlayout,"");
				if(!networkLoad.fileTitle.isEmpty())
				{
					resultFrame.setVisible(false); //Close result frame window when load a new file.
					frame.setTitle(networkLoad.fileTitle);
				}
				vv.updateUI();
				
				
			}
		});
		mntmAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int about = JOptionPane.showConfirmDialog(null, "Version: 1.0\nAuthor: Mohab Saleh\nEmail: mohabmohsen@live.com\nInistitute: BME", "About", JOptionPane.CLOSED_OPTION, 1);
				
			}
		});
		rdbtnPick.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vv.setGraphMouse(gmPick);
				gmPick.setMode(Mode.PICKING);
				
			}
		});
		rdbtnNavigate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vv.setGraphMouse(gmTransform);
				gmTransform.setMode(Mode.TRANSFORMING);
				
			}
		});
		
		rdbtnEdit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vv.setGraphMouse(gmEdit);
				gmEdit.setMode(Mode.EDITING);
				
			}
		});
		rdbtnAnnotation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vv.setGraphMouse(gmTransform);
				gmTransform.setMode(Mode.ANNOTATING);
				
			}
		});
		lfaCostOptbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
				//Identify output link cost change
				String graphLinkCostChange = null;
				
				//Calculate  starTime for LFACostOpt
//				long startTimelfaCostOptF = System.currentTimeMillis();
				
				LFACostOptFV lfaCostOptF = new LFACostOptFV(g, 150, 200);
				
				//Calculate  endTime for LFACostOpt
//				long endTimelfaCostOptF = System.currentTimeMillis();
//				long elapsedTimelfaCostOptF = endTimelfaCostOptF - startTimelfaCostOptF;
//				System.out.println("elapsedTimelfaCostOptF" +elapsedTimelfaCostOptF);
				
				
				//Add LFACostOpt to the g if it optimize the efficiency
				if(lfaCostOptF.localBestEfficiency>lfaCostOptF.originalEfficiency)
				{
//					System.out.println("Applying best efficiency " + lfaCostOptF.localBestEfficiency );
					
					//Apply cost change
					
					//Starting sentence in graphLinkCostChange
					DecimalFormat df = new DecimalFormat("#.##");
					graphLinkCostChange = "LFA link coverage efficiency was optimized from " + Double.valueOf(df.format(lfaCostOptF.originalEfficiency)) + "% to " + Double.valueOf(df.format(lfaCostOptF.localBestEfficiency)) + "%\nThe following link's cost changed:\n";
					
					for(Link tempGLinkIT : lfaCostOptF.tempG.getEdges())
					{
						if(lfaCostOptF.linkCostafterlfaCostOpt.get(tempGLinkIT) != null)
						{
							for(Link gLinkIT: g.getEdges())
							{
								if(gLinkIT.id == tempGLinkIT.id)
								{
									//Add changed cost to graphLinkCostChange
									graphLinkCostChange = graphLinkCostChange + gLinkIT.name + " cost changed from"+ gLinkIT.cost + " to"+ lfaCostOptF.linkCostafterlfaCostOpt.get(tempGLinkIT) + "\n";
									//Change the cost 
									gLinkIT.cost = lfaCostOptF.linkCostafterlfaCostOpt.get(tempGLinkIT);
								}
							}
						}
					}
					
					//show LFACostOpt success message
					int about = JOptionPane.showConfirmDialog(null, graphLinkCostChange, "Information", JOptionPane.CLOSED_OPTION, 1);
					
					//refresh VV and recalculate the efficiency
					vv.updateUI();
					btnShortestPath.doClick();
				}else{
					int about = JOptionPane.showConfirmDialog(null, "LFACostOpt can't find better LFA link coverage efficiency", "Information", JOptionPane.CLOSED_OPTION, 1);
				}
				
//				LFACostOpt lfaCostOpt = new LFACostOpt(g,vv,150, 500);
//
//				if(lfaCostOpt.txtLFACostOpt !="" || lfaCostOpt == null)
//				{
////				int confirmDelete = JOptionPane.showConfirmDialog(null, lfaCostOpt.txtLFACostOpt, "Confirm", JOptionPane.YES_NO_OPTION);
////        		if(confirmDelete == 0)
////        		{
//        			for(Link link : g.getEdges())
//        			{
//        				if(lfaCostOpt.linkCostbeforelfaCostOpt.get(link.id) != lfaCostOpt.linkCostafterlfaCostOpt.get(link.id) && lfaCostOpt.linkCostafterlfaCostOpt.get(link.id) != null)
//        				{
//        					link.cost = lfaCostOpt.linkCostafterlfaCostOpt.get(link.id);
//        				}
////	        				System.out.println(link.name + lfaCostOpt.linkCostafterlfaCostOpt.get(link.id));
//					}
////	        			resultFrame.setVisible(false);//Close result frame window.
////        			LFAf lfaTRY = new LFAf(g);
////        			System.out.println(lfaTRY.lfaEfficiency);
//        			
//        			vv.updateUI();
//					btnShortestPath.doClick();
//					
////        		}
//				}
//				if(lfaCostOpt.txtLFACostOpt =="")
//				{
//					vv.updateUI();
//					
//					int about = JOptionPane.showConfirmDialog(null, "LFACostOpt can't find better cost enhancemnet", "Information", JOptionPane.CLOSED_OPTION, 1);
//					
//				}
				
				
			}
		});
		btnShortestPath.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Result restart
				if(!resultFrame.isVisible())
				{
					resultFrame.setVisible(false);//Close result frame window.
				}
				//Add progress bar
				progressBar.setValue(0);
				
				double progressBarVal = 0;
				double progressBarStep = (double)100/(3*g.getVertexCount());
				//Set default as cleared
				txtCalculationResults.setText("");
				nodePairsList.removeAll();
				unprotectedLinkStatus.setText("");
				
				//Dijkstra algorithm calculation
				Transformer<Link, Integer> wtTransformer = new Transformer<Link,Integer>() {
					 public Integer transform(Link link) {
						 return link.cost;
					 }
				};
				final DijkstraShortestPath<Node,Link> alg = new DijkstraShortestPath(g, wtTransformer);
				String txtAreaHeader = "Unprotected pairs: \n";
				String txtArea = "";
				
				//Start nodePair choice list and add shortest path to node hashmap
				int flag1st = 0;
				
				for(Node n1 : g.getVertices())
		        {
					if(!n1.shrtstPth.isEmpty())
					{
						n1.shrtstPth.clear(); //clear if not empty
					}
		        	for(Node n2 : g.getVertices())
		            {
		        		List<Link> l = alg.getPath(n1, n2);
		        		
		        		//Add the shortest path list of n1-n2 to n1 hashmap
		        		if(!n1.equals(n2)){	
			        		n1.shrtstPth.put(n2, l);
		        		}
		        		//Fill nodePair choice list
		        		if(l.size() > 1)
						{
		        			if(flag1st == 0)
		        			{
			        			nodePairsList.add("Select a pair");
			        			nodePairsList.add("");
			        			flag1st = 1;
		        			}else {
		        				
		        				nodePairsList.add(n1.name + " - " + n2.name);
		        			}
			            }
		        		
			        }
		        	//Increment progress bar
//	        		progressBarVal = progressBarStep + progressBarVal;
//	        		progressBar.setValue((int) progressBarVal);
				}
				//End nodePair choice list
				
				
				
				//Master LFA function to be used for all
				LFAf lfaf = new LFAf(g);
				
				//Start nodePair listener
				//Listing Function
				nodePairsList.addItemListener(new ItemListener() {
					
					@Override
					public void itemStateChanged(ItemEvent arg0) {
						
						//Extract n1 & n2 from nodePair
						for(Node n1 : g.getVertices())
				        {
				        	for(Node n2 : g.getVertices())
				            {
				        		
				        		
				        		if(nodePairsList.getSelectedItem().equals(n1.toString() + " - " + n2.toString()))
				        		{
				        			
			        				LFAf lfaf = new LFAf(g, n1, n2, false);
			        				LFAf lfafN = new LFAf(g, n1, n2, false, true);
			        				unprotectedLinkStatus.setText(lfaf.toString() +  "\n" + lfafN.toString());
				        		}
				            }
				        	
			            }								
					}
				});
				//End nodePair listener
				
				//Start unprotected links
				txtArea = txtArea + "\nLink failure:";
	
				for (Node n1 : g.getVertices())
				{
					for(Node n2 : g.getVertices())
		            {
						Pair<Node> nodePair = new Pair<>(n1,n2);
						if(lfaf.unprotectedNodePairList.get(nodePair) != null)
						{
							txtArea = txtArea + "\n" + n1 + "-" + n2 + ": " +lfaf.unprotectedNodePairList.get(nodePair) + " is unprotected by LFA";
						}
		            }
				}

				
				//Start unprotected Nodes
				txtArea = txtArea + "\n-----------\nNode failure:";
				for (Node n1 : g.getVertices())
				{
					for(Node n2 : g.getVertices())
		            {
						Pair<Node> nodePair = new Pair<>(n1,n2);
						if(lfaf.unprotectedNodePairListNode.get(nodePair) != null)
						{
							txtArea = txtArea + "\n" + n1 + "-" + n2 + ": " +lfaf.unprotectedNodePairListNode.get(nodePair) + " is unprotected by LFA";
						}
		            }
				}
				
				//Get Efficiency
				
				//debug
//				System.out.println("protectedNodePairListNode"+lfaf.protectedNodePairListNode.size());
//				System.out.println(lfaf.protectedNodePairListNode);
//				System.out.println("unprotectedNodePairListNode"+lfaf.unprotectedNodePairListNode.size());
//				System.out.println(lfaf.unprotectedNodePairListNode);
//				System.out.println("protectedNodePairList"+lfaf.protectedNodePairList.size());
//				System.out.println(lfaf.protectedNodePairList);
//				System.out.println("unprotectedNodePairList" + lfaf.unprotectedNodePairList.size());
//				System.out.println(lfaf.unprotectedNodePairList);
				
				if(g.getEdgeCount()>1)
				{
					double lfaEfficiency = lfaf.lfaEfficiency;
					double lfaNodeEfficiency = lfaf.lfaNodeEfficiency;
					DecimalFormat df = new DecimalFormat("#.##"); 
					txtArea = txtArea + "\n\n LFA efficiency (Link Failure): \n" + Double.valueOf(df.format(lfaEfficiency)) + "%\n-----------";
					txtArea = txtArea + "\n LFA efficiency (Node Failure): \n" + Double.valueOf(df.format(lfaNodeEfficiency)) + "%";
				}
				
				//END unprotected links

				if(g.getEdgeCount()>1)
				{
					resultFrame.setVisible(true);
					txtCalculationResults.setText(txtAreaHeader + txtArea);
				}
				
				
				
				
			}
		});
		
		
		//End actions
		
	}
}


