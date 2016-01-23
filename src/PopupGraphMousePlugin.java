import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;


/*
 * Set Popup Menu for Canvas, Links and Nodes
 */

public class PopupGraphMousePlugin extends AbstractPopupGraphMousePlugin implements      MouseListener {
	public DirectedSparseMultigraph<Node, Link> g;
    public PopupGraphMousePlugin(DirectedSparseMultigraph<Node, Link> g) {
        this(MouseEvent.BUTTON3_MASK);
        this.g = g;
    }
    public PopupGraphMousePlugin(int modifiers) {
        super(modifiers);
    }


    @SuppressWarnings("serial")
	protected void handlePopup(MouseEvent e) {
        @SuppressWarnings("unchecked")
		final VisualizationViewer<Node, Link> vv =(VisualizationViewer<Node, Link>)e.getSource();
        final Point2D p = e.getPoint();
        JPopupMenu popup = new JPopupMenu();

        GraphElementAccessor<Node, Link> pickSupport = vv.getPickSupport();
        
        if(pickSupport != null) {
            final Node pickedNode = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            final Link pickedLink = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
            if(pickedNode != null) {

            	popup.add(new AbstractAction("Rename node") {
                    public void actionPerformed(ActionEvent e) {
                 	   String newNodeName = JOptionPane.showInputDialog(null, "New name : ", "Rename Node", 1);
                 	   String previousNodeName = pickedNode.name;
                     		     if(newNodeName != null)
                     		     {
                     		    	 if(newNodeName.isEmpty() == false)
                     		    	 {
		                     		     pickedNode.name = newNodeName;
		                     		     vv.updateUI();
		                     		     JOptionPane.showMessageDialog(null, "Node \""+ previousNodeName+"\" has been renamed to \"" + newNodeName + "\"", 
		                     		   "Rename Node", 1);
                     		    	 }
                     		    	 if(newNodeName.isEmpty())
                     		    	 {
                     		    		JOptionPane.showMessageDialog(null, "You can not leave the name empty", 
                                     		   "Rename Link", 1); 
                     		    	 }
                     		     }
                       
                    }
                   });
               popup.add(new AbstractAction("Delete node") {
                public void actionPerformed(ActionEvent e) {
                	int confirmDelete = JOptionPane.showConfirmDialog(null, "You are about to delete \"" +pickedNode.name + "\", are you sure?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            		if(confirmDelete == 0)
                   g.removeVertex(pickedNode);
                   vv.updateUI();
                }
               });
               
               
               popup.show(vv, e.getX(), e.getY());

            }
            if(pickedLink != null) {
            	popup.add(new AbstractAction("Set cost") {
                    public void actionPerformed(ActionEvent e) {   
                 	   String newLinkCost = JOptionPane.showInputDialog(null, "Set cost : ", 
                     		   "Set link cost", 1);
                 	   @SuppressWarnings("unused")
					double previousLinkName = pickedLink.cost;
                 	   if(newLinkCost != null)
                 	   {
                     		     if(newLinkCost.isEmpty() == false)
                     		     {
                     		    	pickedLink.cost = Integer.parseInt((newLinkCost));
	                     		     
	                     		     vv.updateUI();
//	                     		     JOptionPane.showMessageDialog(null, "Link cost has been set to \"" + newLinkCost + "\"", "Change link cost", 1);
                     		     }   
                     		    if(newLinkCost.isEmpty())
                    		     {
                    		     JOptionPane.showMessageDialog(null, "You can not leave the cost empty", 
                    		   "Change link cost", 1);
                    		     } 
                 	   }
                       
                    }
                   });
            	popup.add(new AbstractAction("Rename link") {
                    public void actionPerformed(ActionEvent e) {
                 	   String newLinkName = JOptionPane.showInputDialog(null, "New name : ", 
                     		   "Rename link", 1);
                 	   String previousLinkName = pickedLink.name;
                 	   if(newLinkName != null)
                 	   {
                     		     if(newLinkName.isEmpty() == false)
                     		     {
                     		    	pickedLink.name = newLinkName;
                     		     vv.updateUI();
                     		     JOptionPane.showMessageDialog(null, "Link \""+ previousLinkName+"\" has been renamed to \"" + newLinkName + "\"", 
                     		   "Rename Link", 1);
                     		     }   
                     		    if(newLinkName.isEmpty())
                    		     {
                    		     JOptionPane.showMessageDialog(null, "You can not leave the name empty", 
                    		   "Rename Link", 1);
                    		     } 
                 	   }
                       
                    }
                   });
            	if(g.getEndpoints(pickedLink).getSecond() != g.getEndpoints(pickedLink).getFirst())
            	{
            	popup.add(new AbstractAction("Reverse link") {
                	public void actionPerformed(ActionEvent e) {
                		//Confirm before reverse
//                		int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure?", "Reverse \"" +pickedLink.name + "\" direction", JOptionPane.OK_CANCEL_OPTION);
//                		if(confirmDelete == 0)
                		
                		Link reversedLink = new Link(pickedLink.id, g, g.getEdgeType(pickedLink), g.getEndpoints(pickedLink).getSecond(), g.getEndpoints(pickedLink).getFirst());
               			reversedLink.cost = pickedLink.cost;
                		g.removeEdge(pickedLink);
               			vv.updateUI();
                    }
                });
            	}
                popup.add(new AbstractAction("Delete link") {
                	public void actionPerformed(ActionEvent e) {
                		int confirmDelete = JOptionPane.showConfirmDialog(null, "You are about to delete \"" +pickedLink.name + "\", are you sure?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
                		if(confirmDelete == 0)
                		g.removeEdge(pickedLink);
                		vv.updateUI();
                    }
                });
                popup.show(vv, e.getX(), e.getY());

             }
//            if(pickedLink == null && pickedNode == null) {
//                popup.add(new AbstractAction("Test Canvas") {
//                 public void actionPerformed(ActionEvent e) {
//                    System.out.println("Action Performed");  
//                    }
//                });
//                popup.show(vv, e.getX(), e.getY());
//
//             }
        }



    }
}