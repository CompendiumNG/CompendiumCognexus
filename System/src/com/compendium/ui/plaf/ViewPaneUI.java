/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.ui.plaf;

import static com.compendium.ProjectCompendium.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import java.util.*;

import javax.swing.*;
import javax.swing.plaf.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.io.xml.*;
//import com.compendium.io.questmap.*;
import com.compendium.ui.*;
import com.compendium.ui.edits.*;
import com.compendium.ProjectCompendium;

/**
 * The UI class for the UIViewPane Component
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public	class ViewPaneUI extends ComponentUI
				implements MouseListener, MouseMotionListener, KeyListener, ICoreConstants, IUIConstants,
				ClipboardOwner {


	/** The serial version id */
	private static final long serialVersionUID 		= -5511379762447721772L;

	/** Used by other classes to determine the left offset to use when adding node to a map.*/
	public static final int LEFTOFFSET 				= 100;

	/** Used by other classes to determine spacing between nodes in a map.*/
	public static final int INTERNODE_DISTANCE 		= 60;

	private static final int PAINT 					= 0;
	//private static final int 	XOR 				= 1;
	/** The minimum size for the list view.*/
	private static Dimension 						minSize = new Dimension(0,0);

	/** The maximum size for the list view.*/
	private static Dimension 						maxSize	= new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

	/** Component that we're going to be drawing into. */
	protected UIViewPane							oViewPane;

	/** Key code that is being generated for. */
	protected Action								oRepeatKeyAction;

	/** Set to true while keyPressed is active. */
	protected boolean								bIsKeyDown = false;

  	/** The MouseListener registered for this list.*/
	private		MouseListener						oMouseListener;

  	/** The MouseMotionListener registered for this list.*/
	private		MouseMotionListener					oMouseMotionListener;

  	/** The KeyListener registered for this list.*/
	private		KeyListener							oKeyListener;

	/** _x & _y are the mousePressed location in absolute coordinate system.*/
	private		int									_x, _y;

	/** Location of the mouse at the time a key was pressed.*/
	public		Point								ptLocationKeyPress;

	/** Location of the mouse when it was clicked.*/
	private		Point								ptLocationMouseClicked = new Point(0,0);

	/** Start point in view pane's coordinate system.*/
	private		Point								ptStart;

	/** Previous end point in dragging.*/
	private		Point								ptPrev;

	/** Defines whether dragging is started with right mouse button.*/
	private		boolean								bDragging = false;

	/** Defines whether mouse clicked (used when objects are copied to the clicked place).*/
	private		boolean								bClicked = false;

	/** Defines whether the view paen is being scrolled by the user by mouse dragging.*/
	private		boolean								bScrolling = false;

	/** Defines whether the view pane has been exited by the mouse.*/
	private		boolean								bMouseExited = false;

	/** Is a copy in progress?*/
	private		boolean								bCopyToClipboard = false;

	/** Is a cut in progress?*/
	private		boolean								bCutToClipboard = false;

	/** if on the Mac, and a mock-right mouse was initialized (control-left mouse).*/
	private		boolean								bIsMacRightMouse = false;

	/** Used when drawing the drag box.*/
	private		Rectangle							oSelectedView = new Rectangle(0,0);

	/** Used during Questmap and XML imports.*/
	private boolean 								isSmartImport = false;

	/** The shortcut key for the current platfrom.*/
	private int 									shortcutKey;

	/** The node the mouse is currently over, or null.*/
	private UINode									oNode = null;

	/**
	 * Constructor. Just calls super.
	 */
 	public ViewPaneUI() {
		super();
	}

	/**
	 * Constructor. Installs the default and listeners.
	 * @param c, the component this is the ui for.
	 */
	public ViewPaneUI(JComponent c) {

		super();
		oViewPane = (UIViewPane)c;
		shortcutKey = ProjectCompendium.APP.shortcutKey;

		installDefaults(c);
		installListeners(c);
		initializeView();
	}

	/**
	 * Create a new ListUI instance.
	 * @param c, the component this is the ui for - NOT REALLY USED AT PRESENT HERE.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new ViewPaneUI();
  	}

	/***** USER INTERFACE INITIALIZATION METHODS *****/

	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
  	public void installUI(JComponent c)   {
		super.installUI(c);
		oViewPane = (UIViewPane)c;

		installDefaults(c);
		installListeners(c);
		initializeView();
  	}

	/**
	 * Install any default - Just sets the background color at present.
	 * @param c, the component to uninstall the listeners for.
	 */
  	protected void installDefaults(JComponent c) {
		if (c.getBackground() == null || c.getBackground() instanceof UIResource) {
	      c.setBackground(Color.white);
		}
  	}

	/**
	 * Install any Listener classes required by this UI.
	 * @param c, the component to install the listeners for.
	 */
	protected void installListeners(JComponent c) {
		if ( (oMouseListener = createMouseListener( c )) != null ) {
		    c.addMouseListener( oMouseListener );
		}
		if ( (oMouseMotionListener = createMouseMotionListener( c )) != null ) {
		    c.addMouseMotionListener( oMouseMotionListener );
		}
		if ( (oKeyListener = createKeyListener( c )) != null ) {
				c.addKeyListener( oKeyListener );
		}
	}

	/**
	 * Just returns this class as the MouseListener.
	 * @param c, the component to create the MouseLisener for.
	 * @return MouseListener, the listener to use.
	 */
  	protected MouseListener createMouseListener( JComponent c ) {
		return this;
  	}

	/**
	 * Just returns this class as the MouseMotionListener.
	 * @param c, the component to create the MouseMotionLisener for.
	 * @return MouseMotionListener, the listener to use.
	 */
  	protected MouseMotionListener createMouseMotionListener( JComponent c ) {
		return this;
  	}

	/**
	 * Just returns this class as the KeyListener.
	 * @param c, the component to create the KeyLisener for.
	 * @return KeyListener, the listener to use.
	 */
  	protected KeyListener createKeyListener(JComponent c) {
		return this;
  	}

	/**
	 * Run any uninstall instructions for uninstalling this UI.
	 * @param c, the component this is the ui to uninstall for.
	 */
	public void uninstallUI(JComponent c) {
		uninstallListeners(c);
		uninstallDefaults(c);

		oMouseListener = null;
		oKeyListener = null;
		oMouseMotionListener = null;
  	}

	/**
	 * Uninstall any default - CURRENTLY DOES NOTHING.
	 * @param c, the component to uninstall the listeners for.
	 */
  	protected void uninstallDefaults(JComponent c) {} // uninstallDefaults

	/**
	 * Uninstall any Listener classes used by this UI.
	 * @param c, the component to uninstall the listeners for.
	 */
	protected void uninstallListeners( JComponent c ) {
		if ( oKeyListener!= null ) {
	    	c.removeKeyListener( oKeyListener );
		}
		if ( oMouseMotionListener!= null ) {
	    	c.removeMouseMotionListener( oMouseMotionListener );
		}
		if ( oMouseListener!= null ) {
	    	c.removeMouseListener( oMouseListener );
		}
	}

	/**
	 * Return the UIViewPane object that this is the ui for.
	 * @return com.compendium.ui.UIViewPane, the UIViewPane object that this is the ui for.
	 */
	public UIViewPane getViewPane() {
		return oViewPane;
  	}

	/**
	 * Initialize the view by retrieving all nodes and links in the
	 * view and displaying them.
	 */
	protected void initializeView() {

		View view = oViewPane.getView();
		if (view != null) {
			if (view.getType() == ICoreConstants.LISTVIEW) {
				return ;
			}
			int i = 0;

			for(Enumeration e = view.getPositions();e.hasMoreElements();) {
				NodePosition pos = (NodePosition)e.nextElement();
				addNode(pos);
			}

			for(Enumeration e = view.getLinks();e.hasMoreElements();) {
				Link link = (Link)e.nextElement();
				addLink(link);
			}
		}
	}

	/***** PAINT METHODS *****/

	/**
	 * Just calls super.paint
	 *
	 * @param g, the Graphics object for this paint method to use.
	 * @param c, the component to paint.
	 */
  	public void paint(Graphics g, JComponent c) {

		super.paint(g, c);
 	}

	/**
	 * Return the preferred size of this component - currently returns null.
	 * @param c, the component to return the preferred size for.
	 * @return Rectangle, the preferred size of this component - currently null.
	 */
  	public Dimension getPreferredSize(JComponent c) {
		return null;
	}

	/**
	 * Return the minimum size of this component.
	 * @param c, the component to return the minimum size for.
	 * @return Rectangle, the minimum size of this component.
	 */
  	public Dimension getMinimumSize(JComponent c) {
		return minSize;
	}

	/**
	 * Return the maximum size of this component.
	 * @param c, the component to return the maximum size for.
	 * @return Rectangle, the maximum size of this component.
	 */
	public Dimension getMaximumSize(JComponent c){
		return maxSize;
  	}

	/**
	 * Draws a Box in a dragging operation.
	 * @param mode, currently always PAINT mode.
	 * @param p1, the point of the previous node position.
	 * @param p2, the new node position.
	 */
	public void drawDragBox(int mode, Point p1, Point p2) {

		RepaintManager mgr = RepaintManager.currentManager(oViewPane);
		mgr.addDirtyRegion(oViewPane,	0,0, oViewPane.getWidth(),oViewPane.getHeight()); ;
		mgr.paintDirtyRegions();

		Graphics2D g = (Graphics2D)oViewPane.getGraphics();

		oSelectedView.x = 0;
		oSelectedView.y = 0;
		oSelectedView.setBounds(p1.x, p1.y, p2.x, p2.y);

		Rectangle viewRect = new Rectangle(oViewPane.getWidth(),oViewPane.getHeight());

		if(SwingUtilities.isRectangleContainingRectangle(viewRect,oSelectedView)) {

			if (g != null) {
				Color oldColor = g.getColor();
				if (mode == PAINT) {
					g.setColor(Color.black);
					//g.setPaintMode();
					int x = Math.min(p1.x, p2.x);
					int y = Math.min(p1.y, p2.y);
					int w = Math.abs(p1.x - p2.x);
					int h = Math.abs(p1.y - p2.y);
					g.drawRect(x, y, w, h);
				}
				else {
					g.setColor(Color.lightGray);
					//g.setXORMode(Color.white);
					g.setXORMode(Color.lightGray);
					int x = Math.min(p1.x, p2.x);
					int y = Math.min(p1.y, p2.y);
					int w = Math.abs(p1.x - p2.x);
					int h = Math.abs(p1.y - p2.y);
					g.drawRect(x, y, w, h);
				}
				g.setColor(oldColor);
			}
		}
	}

	/**
	 * Re-draws the viewPane.
	 */
	public void redraw() {

		RepaintManager mgr = RepaintManager.currentManager(oViewPane);
		mgr.addDirtyRegion(oViewPane,	0,0, oViewPane.getWidth(),oViewPane.getHeight());
		mgr.paintDirtyRegions();
	}

	/**
	 * Setting for Questmap or XML imports to use.
	 * @param doSmartImport, setting for Questmap or XML imports to use.
	 */
	public void setSmartImport(boolean doSmartImport) {
		isSmartImport = doSmartImport;
	}

	/**
	 * For Importing files from Questmap. Instantiates and starts the parser.
	 * @param filename, the name of the file to import.
	 *
	public void onImportFile(String filename) {

		Parser parser = new Parser(false, filename, ProjectCompendium.APP.getModel(), oViewPane.getView());
		parser.setViewPaneUI(this);
		parser.setSmartImport(isSmartImport);
		parser.start();
	}  */

	/**
	 * For Importing XML files.
	 * @param filename, the name of the file to import.
	 * @param includeInDetail, whether to include the original author and dates in the node detail.
	 */
	public void onImportXMLFile(String filename, boolean includeInDetail) {

		XMLImport xmlImport = new XMLImport(false, filename, ProjectCompendium.APP.getModel(), oViewPane.getView(), isSmartImport, includeInDetail);
		xmlImport.setViewPaneUI(this);
		xmlImport.start();
	}

	/**
	 * Used to set rollover for links.  This is done here as opposed to the LineUI class
	 * because links have a large selection space and only a small portion of that
	 * is actually part of the line, one mouse event could actually refer to multiple
	 * lines even though only one line event is generated.  Therefore it's best to
	 * handle the link rollover here.
	 *
	 * @param e, the associated MouseEvent for the link.
	 */
	private void checkLinkRollover (MouseEvent e) {
		try {
			Point pt = new Point(e.getX(), e.getY());
			Component larray[] = oViewPane.getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());

			for(int i=0;i<larray.length;i++) {

				JComponent object = (JComponent)larray[i];
				if(object instanceof UILink) {

					UILink link = (UILink)object;
					pt = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), oViewPane);
					boolean onLink = link.onLine(pt, 3);
					if (!onLink) {
						if (link.getBounds().contains(pt.x, pt.y)) {
							Rectangle labelRec = ((LinkUI)link.getUI()).getLabelRectangle();
							if (labelRec != null && labelRec.contains(e.getX(), e.getY())) {
								onLink = true;
							}
						}
					}
					link.setRollover(onLink);
				}
			}
		}
		catch (Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.checkLinkRollover) " + ex.getMessage());
		}
	}


	/**
	 * Used to set selection for links.  This is done here as opposed to the LineUI class
	 * because links have a large selection space and only a small portion of that
	 * is actually part of the line, one mouse event could actually refer to multiple
	 * lines even though only one line event is generated.  Therefore it's best to
	 * handle the link selection here.
	 *
	 * @param e, the associated MouseEvent for the link.
	 */
	private boolean handledLinkLeftClick (MouseEvent e) {

		boolean	foundClickOnLink = false;
		//look for all the links this click could apply
		// to, check to see if the click is on the line,
		// if so select it.
		// Note: Deselection of links and nodes occured when the mouse was pressed.

		UILink link = isMouseOnALink(e);
		if (link != null) {
			foundClickOnLink = true;
			if(e.getClickCount() == 1) {
				if (((e.getModifiers() & MouseEvent.CTRL_MASK) != 0) ||
					((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0)) {
					link.controlClick();
				}
				else {
					link.setSelected(true);
					link.setRollover(false);
					oViewPane.setSelectedLink(link,ICoreConstants.SINGLESELECT);
				}
			}
			else if(e.getClickCount() == 2) {
				link.showEditDialog();
			}
		}

		return (foundClickOnLink);
	}

	/**
	 * Handle a mouse right click evetn from a line/link object.
	 *
	 * @param e, the associated MouseEvent for the link.
	 */
	private boolean handledLinkRightClick(MouseEvent e) {

		boolean foundClickOnLink = false;
		int modifiers = e.getModifiers();

		UILink link = isMouseOnALink(e);
		if (link != null) {
			foundClickOnLink = true;
			if(e.getClickCount() == 1) {
				if ((modifiers & MouseEvent.SHIFT_MASK) != 0) {
					oViewPane.setSelectedLink(link,ICoreConstants.MULTISELECT);
					link.setSelected(true);
					link.setRollover(false);
					LinkUI linkui = (LinkUI)link.getUI();
					link.showPopupMenu(linkui, e.getX(), e.getY());
				}
				else {
					oViewPane.setSelectedLink(link,ICoreConstants.SINGLESELECT);
					link.setSelected(true);
					link.setRollover(false);
					LinkUI linkui = (LinkUI)link.getUI();
					link.showPopupMenu(linkui, e.getX(), e.getY());
				}
			}
		}

		return (foundClickOnLink);
	}

	/**
	 * Returns UILink object if the mouse pointer is currently over a link, else returns null.
	 *
	 * @param MouseEvent e, the mouse event to get the mouse position from.
	 */
	public UILink isMouseOnALink(MouseEvent e) {

		UILink uiLink = null;

		Point pt = new Point(e.getX(), e.getY());
		Component larray[] = oViewPane.getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());

		for(int i=0;i<larray.length;i++) {

			JComponent object = (JComponent)larray[i];
			if(object instanceof UILink) {

				UILink link = (UILink)object;
				pt = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), oViewPane);
				boolean onLink = link.onLine(pt, 3);
				if (!onLink) {
					if (link.getBounds().contains(pt.x, pt.y)) {
						Rectangle labelRec = ((LinkUI)link.getUI()).getLabelRectangle();
						if (labelRec != null && labelRec.contains(e.getX(), e.getY())) {
							uiLink = link;
							break;
						}
					}
				}
				else {
					uiLink = link;
					break;
				}
			}
		}
		return uiLink;
	}

	/**
	 * Returns UINode object if the mouse pointer is currently over a node, else returns null.
	 *
	 * @param MouseEvent e, the mouse event to get the mouse position from.
	 */
	public UINode isMouseOnANode(MouseEvent e) {

		UINode uiNode = null;

		Point pt = new Point(e.getX(), e.getY());
		Component larray[] = oViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		for(int i=0;i<larray.length;i++) {
			JComponent object = (JComponent)larray[i];
			if(object instanceof UINode) {
				UINode node = (UINode)object;
				pt = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), node);
				if (node.contains(pt)) {
					uiNode = node;
					break;
				}
			}
		}
		return uiNode;
	}

	/***** EVENT HANDLING METHODS *****/

	/**
	 * Update the local and global mouse position information.
	 * @param Point p, the mouse position.
	 */
	private void updateMousePosition(Point p ) {
		_x = p.x;
		_y = p.y;

		Point point = new Point(p.x, p.y);
		SwingUtilities.convertPointToScreen(point, oViewPane);
		ProjectCompendium.APP._x = point.x;
		ProjectCompendium.APP._y = point.y;
	}

	/**
	 * Invoked when the mouse has been clicked on a component.
	 * @param e, the associated MouseEvent.
	 */
  	public void mouseClicked(MouseEvent e) {
  		
  		if (oViewPane instanceof UIAerialViewPane) return;		// ignore clicks in Aerial view

		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);

		UILink link = isMouseOnALink(e);
		if (link != null && !isRightMouse) {
			Rectangle labelRec = ((LinkUI)link.getUI()).getLabelRectangle();
			Point ptConvert = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), oViewPane);
			Point ptNew = SwingUtilities.convertPoint(oViewPane, ptConvert.x, ptConvert.y, (Component)link);

			if (labelRec != null && labelRec.contains(ptNew.x, ptNew.y)) {
				MouseEvent newEvent = new MouseEvent(link,
		                  e.getID(),
		                  e.getWhen(),
		                  e.getModifiers(),
		                  ptNew.x,
		                  ptNew.y,
		                  e.getClickCount(),
		                  e.isPopupTrigger(),
		                  e.getButton()
		                  );

				((LinkUI)link.getUI()).mouseClicked(newEvent);
				return;
			}
		}

		int mouseX = e.getX();
		int mouseY = e.getY();

		bClicked = true;
	  	Point p = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);

	  	ptLocationMouseClicked.x = p.x;
	  	ptLocationMouseClicked.y = p.y;

		oViewPane.hideLabels();
		oViewPane.hideViews();

		if(isRightMouse) {
			// if this click refers to a link then handle it otherwise
			// assume it refers to the view.
			if (!this.handledLinkRightClick(e)) {
				if(e.getClickCount() == 1) {
					oViewPane.showPopupMenu(this,e.getX(),e.getY());
				}
			}
		}

		//bz - if left mouse click, then handle selection
		// events for links.  Since links have a large selection
		// space and only a small portion of that is actually
		// part of the line, one click could refer to multiple
		// lines.  Therefore it's best to handle the link selection here.
		else {
			if (ProjectCompendium.isMac && (e.getModifiers() & MouseEvent.CTRL_MASK) != 0) {
				if (!this.handledLinkRightClick(e)) {
					if(e.getClickCount() == 1) {
						oViewPane.showPopupMenu(this,e.getX(),e.getY());
					}
				}
			}
			else {
				//if this click isn't for a link, then handle it for the view...
				if (!this.handledLinkLeftClick(e)){
					if(e.getClickCount() == 2) {
						oViewPane.getViewFrame().showEditDialog();
					}
				}
			}
		}

		ProjectCompendium.APP.setStatus(oViewPane.getView().getLabel());
  	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();

		bClicked = false;
		if(oViewPane != null && oViewPane.isEnabled())
		    oViewPane.requestFocus();

		Point p = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);

		updateMousePosition(p);

		// start dragging if left or right mouse button is pressed
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);

		int onmask = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK | InputEvent.META_DOWN_MASK;

		if (ProjectCompendium.isMac &&
				( (e.getButton() == 3 && e.isShiftDown()) ||
				  ((e.getModifiersEx() & onmask) == onmask) ) ) {

			bIsMacRightMouse = true;

			isRightMouse = true;
			isLeftMouse = false;
		}

		ptStart = p;
		ptPrev = ptStart;

		if (isLeftMouse) {
			bDragging = true;
		}
		else if (isRightMouse ) {
			bScrolling = true;
		}
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseReleased(MouseEvent e) {

		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);

		if (ProjectCompendium.isMac && bIsMacRightMouse) {
			isRightMouse = true;
			isLeftMouse = false;
		}

		// deselect if left mouse and only if not cntl or shift click
		if ((isLeftMouse) && ((e.getModifiers() & MouseEvent.CTRL_MASK) == 0) &&
					((e.getModifiers() & MouseEvent.SHIFT_MASK) == 0))
		{
			oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
			oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
		}

		// get the selected view area and the underlying components (uinodes)
		oSelectedView.x = 0;
		oSelectedView.y = 0;

		//bz - need to calculate the top left point of box and the
		// absolute values of the width and height.

		if (ptStart == null || ptPrev == null)
			return;

		int iTopLeftX = Math.min(ptStart.x, ptPrev.x);
		int iTopLeftY = Math.min(ptStart.y, ptPrev.y);
		int iWidth = Math.abs(ptPrev.x-ptStart.x);
		int iHeight = Math.abs(ptPrev.y-ptStart.y);

		//bz - ignore cases of a click on a point - these are handled by the mouseClick event
		//		which is called right after this one.
		if ((iWidth == 0) && (iHeight ==0)) {
			bDragging = false;
			bScrolling = false;
			return;
		}


		oSelectedView.setBounds(iTopLeftX, iTopLeftY, iWidth,iHeight);

		//if a user clicks on the mouse and drags for a distance then shift the viewport in the
		//viewframe by the distance

		if(bScrolling && isRightMouse) {

			//scroll the view pane .. get the x and y displacement
			int newScrollX = 0;
			int newScrollY = 0;
			int xdisp = ptPrev.x-ptStart.x;
			int ydisp = ptPrev.y-ptStart.y;
				
			
			Point currentScrollPoint = oViewPane.getViewFrame().getViewPosition();

			// If the right-drag was in the Aerial view then we need to adjust the scroll distance
			// by the ratio of the aerial view scale to the Frame's scale since the scrollbars operate
			// in the Frame's coordinate space.  
			// NOTE: This code is currently not used because aerial Scrolling is handled by the mouseDragged() handler below
			
			if (oViewPane instanceof UIAerialViewPane) {				// The drag was happening in the Aerial view, thus at a different scale
				double dAerialScale = oViewPane.getScale();							// The Aerial pane's scale
				UIAerialViewPane vp = (UIAerialViewPane)oViewPane;
				double dFrameScale = vp.getMapFrame().getViewPane().getScale();		// The 'real' pane's scale
				double dRatio = dFrameScale / dAerialScale;
				Point scaledDisp = UIUtilities.transformPoint(xdisp, ydisp, dRatio);	
				newScrollX = currentScrollPoint.x + scaledDisp.x;
				newScrollY = currentScrollPoint.y + scaledDisp.y;
			} else {
				newScrollX = currentScrollPoint.x + xdisp;
				newScrollY = currentScrollPoint.y + ydisp;
			}
			
			if (newScrollX < 0) newScrollX = 0;
			if (newScrollY < 0) newScrollY = 0;

			oViewPane.getViewFrame().setViewPosition(new Point(newScrollX, newScrollY));
		}

		if(bDragging && isLeftMouse) {

			// CLOSE UP SPACE
			if ( ((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0) && ((e.getModifiers() & MouseEvent.ALT_MASK) != 0) ) {

				Component narray[] = oViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
				for(int i=0;i<narray.length;i++) {
					JComponent object = (JComponent)narray[i];

					if(object instanceof UINode) {
						UINode uinode = (UINode)object;

						Rectangle r = uinode.getBounds();

						if (r.y >= iTopLeftY ) {
							try {
								Point ptNew = new Point(r.x, r.y-iHeight);

								//Write to Database new position at 100% scale
								// If zoomed scale up bounds and height so stored in database correctly for 100% zoom.
							    if (uinode.getScale() != 1.0) {
							    	Point scaledPos = UIUtilities.scalePoint(r.x, r.y, uinode.getScale());
							    	Point heightPos = UIUtilities.scalePoint(iHeight, iHeight, uinode.getScale());
							    	scaledPos = new Point(scaledPos.x, scaledPos.y-heightPos.x);
									uinode.getViewPane().getView().setNodePosition(uinode.getNode().getId(), scaledPos);
									NodePosition nodepos = uinode.getNodePosition();
									nodepos.setPos(scaledPos);
								}
								else {
									uinode.getViewPane().getView().setNodePosition(uinode.getNode().getId(), ptNew);
									NodePosition nodepos = uinode.getNodePosition();
									nodepos.setPos(r.x, ptNew.y);
								}
							    
								//Set the position of the visible object to scaled value
							    uinode.setBounds(r.x, ptNew.y, uinode.getWidth(), uinode.getHeight());

							    // Refresh the links 
								uinode.updateLinks();
							}
							catch(Exception ex) {
								// ANY POINT IN TELLING THE USER?, COULD GET ANNOYING
								// AND THEY WILL SEE THAT THE NODES HAVE NOT MOVED
							}
						}
					}
				}
			}

			// MAKE SPACE
			else if ((e.getModifiers() & MouseEvent.ALT_MASK) != 0) {

				Component narray[] = oViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
				for(int i=0;i<narray.length;i++) {
					JComponent object = (JComponent)narray[i];

					if(object instanceof UINode) {
						UINode uinode = (UINode)object;

						Rectangle r = uinode.getBounds();

						if (r.y >= iTopLeftY ) {
							try {
							    Point ptNew = new Point(r.x, r.y+iHeight);

								// If zoomed scale up bounds and height so stored in database correctly for 100% zoom.
							    if (uinode.getScale() != 1.0) {
							    	Point scaledPos = UIUtilities.scalePoint(r.x, r.y, uinode.getScale());
							    	Point heightPos = UIUtilities.scalePoint(iHeight, iHeight, uinode.getScale());
							    	scaledPos = new Point(scaledPos.x, scaledPos.y+heightPos.x);
									uinode.getViewPane().getView().setNodePosition(uinode.getNode().getId(), scaledPos);
									NodePosition nodepos = uinode.getNodePosition();
									nodepos.setPos(scaledPos);
								}
								else {
									uinode.getViewPane().getView().setNodePosition(uinode.getNode().getId(), ptNew);
									NodePosition nodepos = uinode.getNodePosition();
									nodepos.setPos(r.x, ptNew.y);
								}

								//Set the position of the visible object to scaled value
								uinode.setBounds(r.x, ptNew.y, uinode.getWidth(), uinode.getHeight());

							    // Refresh the links 
								uinode.updateLinks();
							}
							catch(Exception ex) {
								// ANYPOINT IN TELLING THE USER, COULD GET ANNOYING
								// AND THEY WILL SEE THAT THE NODES HAVE NOT MOVED
							}
						}
					}
				}
			} 
			
			else {
				Component narray[] = oViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

				for(int i=0;i<narray.length;i++) {
					JComponent object = (JComponent)narray[i];

					if(object instanceof UINode) {

						UINode node = (UINode)object;
						Rectangle rectNode = node.getBounds();

						//bz - switched these around, parameters were in wrong places
						if(SwingUtilities.isRectangleContainingRectangle(oSelectedView, rectNode)||
								rectNode.intersects(oSelectedView))
						{
							// if control drag then toggle the selection,0+
							if ((e.getModifiers() & MouseEvent.CTRL_MASK) != 0){
								if (!node.isSelected()) {
									node.setSelected(true);
									oViewPane.setSelectedNode(node,ICoreConstants.MULTISELECT);
								}
								else {
									node.setSelected(false);
									oViewPane.removeNode(node);
								}
							}
							else {
								node.setSelected(true);
								oViewPane.setSelectedNode(node,ICoreConstants.MULTISELECT);
							}
						}
					}
				}

				Component larray[] = oViewPane.getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
				for(int i=0;i<larray.length;i++) {

					JComponent object = (JComponent)larray[i];
					Point[] pts;
					//for links

					if(object instanceof UILink) {

						//bz - need to check if drag box intersects line not rectangle
						// surrounding line...
						//if(SwingUtilities.isRectangleContainingRectangle(oSelectedView,rectLink) ||
						// (rectLink.intersects(oSelectedView)))
						UILink link = (UILink)object;

						pts = UILine.intersectionWithRectangle(oSelectedView, link.getFrom(), link.getTo());

						if ((pts.length > 0) && pts[0] != null) {
							if ((e.getModifiers() & MouseEvent.CTRL_MASK) != 0) {
								if (!link.isSelected()) {
									link.setSelected(true);
									oViewPane.setSelectedLink(link,ICoreConstants.MULTISELECT);
								}
								else {
									link.setSelected(false);
									oViewPane.removeLink(link);
								}
							}
							else {
								link.setSelected(true);
								oViewPane.setSelectedLink(link,ICoreConstants.MULTISELECT);
							}
						}
					}
				}
		 		//oViewManager.dndFinish(new DnDEvent(oViewPane, new Point(mouseX, mouseY), e.getModifiers(), null));
			}
		}

		ProjectCompendium.APP.setStatus("");
		ProjectCompendium.APP.stopWaitCursor(oViewPane.getViewFrame());

		//reset the flags and points
		bIsMacRightMouse=false;
		bDragging = false;
		bScrolling = false;
		ptStart = null;
		ptPrev = null;

		redraw();
  	} // mouseReleased

  	/**
 	 * Invoked when the mouse enters a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseEntered(MouseEvent e) {

		int mouseX = e.getX();
		int mouseY = e.getY();

		// check for links being entered here
	  	Point p = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);
		updateMousePosition( p );

		this.checkLinkRollover(e);
		bMouseExited = false;
	}

	/**
	 * Invoked when the mouse exits a component.
	 * @param e, the associated MouseEvent.
   	 */
	public void mouseExited(MouseEvent e) {

		int mouseX = e.getX();
		int mouseY = e.getY();

		Point p = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);
		//updateMousePosition(p);

		this.checkLinkRollover(e);
	  	bMouseExited = true;
  	}

	/**
	 * Invoked when a mouse is dragged in a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseDragged(MouseEvent e) {

		int mouseX = e.getX();
		int mouseY = e.getY();

		//System.out.println("Mouse dragged " + e.getSource() +" at "+mouseX+" , "+mouseY);
		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);

		if (ProjectCompendium.isMac && bIsMacRightMouse) {
			isRightMouse = true;
			isLeftMouse = false;
		}

		if (bDragging) {

			Point ptNew = null;
			if (isLeftMouse) {
				ptNew = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);
				drawDragBox(PAINT, ptStart, ptNew);
			}
			else if (isRightMouse) {
				ptNew = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);
			}
			ptPrev = ptNew;
		}

		if(bScrolling) {
			Point ptNew = null;
			ptNew = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);
			ptPrev = ptNew;
			
			// If scrolling in the Aerial view, update the scroll location of the 'real' viewFrame in real time

			if (oViewPane instanceof UIAerialViewPane) {
				int xdisp = ptPrev.x-ptStart.x;
				int ydisp = ptPrev.y-ptStart.y;
				Point currentScrollPoint = oViewPane.getViewFrame().getViewPosition();
				double dAerialScale = oViewPane.getScale();							// The Aerial pane's scale
				UIAerialViewPane vp = (UIAerialViewPane)oViewPane;
				double dFrameScale = vp.getMapFrame().getViewPane().getScale();		// The 'real' pane's scale
				double dRatio = dFrameScale / dAerialScale;
				Point scaledDisp = UIUtilities.transformPoint(xdisp, ydisp, dRatio);	
				int newScrollX = currentScrollPoint.x + scaledDisp.x;
				int newScrollY = currentScrollPoint.y + scaledDisp.y;
				if (newScrollX < 0) newScrollX = 0;
				if (newScrollY < 0) newScrollY = 0;
				oViewPane.getViewFrame().setViewPosition(new Point(newScrollX, newScrollY));
				ptStart = ptPrev;
			}
		}
	}


	/**
	 * Invoked when a mouse is moved in a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseMoved(MouseEvent e) {

		int mouseX = e.getX();
		int mouseY = e.getY();

		Point p = SwingUtilities.convertPoint((Component)e.getSource(), mouseX, mouseY, oViewPane);
		updateMousePosition(p);

		// check to see if mouse moved off of a link line, if so then make sure
		// it is not longer in focus
		this.checkLinkRollover(e);
	}


// KEY EVENTS

	/**
	 * Invoked when a key is pressed in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyPressed(KeyEvent evt) {

		// NOTE: IF THIS IS NOT THE SELECTED VIEW, IT SHOULD NOT HANDLE THE EVENT,
		// SO GO THROUGH THE FRAME EVENT TO FIND THE SELECTED VIEW
		// - THIS HAPPENS BECAUSE OF PROBLEMS PASSING THE FOCUS WHEN AN INTERNAL FRAME IS OPENED
		if (!oViewPane.getViewFrame().isSelected()) {
			ProjectCompendium.APP.keyPressed(evt);
			return;
		}
		
		if (oViewPane instanceof UIAerialViewPane)  return;		// ignore key presses in Aerial view

		ptLocationKeyPress = new Point(_x, _y);

		char [] key = {evt.getKeyChar()};
		String sKeyPressed = new String(key);
		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		if (modifiers == shortcutKey) {
			switch(keyCode) {
				case KeyEvent.VK_F: { // OPEN SEARCH
					ProjectCompendium.APP.onSearch();
					evt.consume();
					break;
				}
				case KeyEvent.VK_O: { // OPEN PROJECT DIALOG
					ProjectCompendium.APP.onFileOpen();
					evt.consume();
					break;
				}
				case KeyEvent.VK_N: { // NEW PROJECT DIALOG
					ProjectCompendium.APP.onFileNew();
					evt.consume();
					break;
				}
				case KeyEvent.VK_X: { // CUT
					if (oViewPane.getViewFrame().isSelected()) {
						ProjectCompendium.APP.startWaitCursor(oViewPane.getViewFrame());
						cutToClipboard(null);
						ProjectCompendium.APP.stopWaitCursor(oViewPane.getViewFrame());
					}
					else {
						ProjectCompendium.APP.onEditCut();
					}
					evt.consume();
					break;
				}
				case KeyEvent.VK_C: { // COPY
					ProjectCompendium.APP.startWaitCursor(oViewPane.getViewFrame());
					copyToClipboard(null);
					ProjectCompendium.APP.stopWaitCursor(oViewPane.getViewFrame());
					evt.consume();
					break;
				}
				case KeyEvent.VK_V: { // PASTE
					ProjectCompendium.APP.startWaitCursor(oViewPane.getViewFrame());
					pasteFromClipboard();
					ProjectCompendium.APP.stopWaitCursor(oViewPane.getViewFrame());
					evt.consume();
					break;
				}
				case KeyEvent.VK_Z: { // UNDO
					ProjectCompendium.APP.startWaitCursor(oViewPane.getViewFrame());
					ProjectCompendium.APP.onEditUndo();
					ProjectCompendium.APP.stopWaitCursor(oViewPane.getViewFrame());
					evt.consume();
					break;
				}
				case KeyEvent.VK_Y: { // REDO
					ProjectCompendium.APP.startWaitCursor(oViewPane.getViewFrame());
					ProjectCompendium.APP.onEditRedo();
					ProjectCompendium.APP.stopWaitCursor(oViewPane.getViewFrame());
					evt.consume();
					break;
				}
				case KeyEvent.VK_A: { // SELECT ALL
					onSelectAll();
					evt.consume();
					break;
				}
				case KeyEvent.VK_W: { // CLOSE WINDOW
					try {
						if (oViewPane.getView() != ProjectCompendium.APP.getHomeView() ) {
							oViewPane.getViewFrame().setClosed(true);

							JDesktopPane pane = ProjectCompendium.APP.getDesktop();
							JInternalFrame frame = pane.getSelectedFrame();
							if (frame instanceof UIMapViewFrame) {
								UIMapViewFrame mapframe = (UIMapViewFrame)frame;
								mapframe.getViewPane().requestFocus();
							} else if (frame instanceof UIListViewFrame) {
								UIListViewFrame listframe = (UIListViewFrame)frame;
								listframe.getUIList().getList().requestFocus();
							}
						}
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}
			}
		}
		if (modifiers == java.awt.Event.CTRL_MASK) {
			switch(keyCode) {
				case KeyEvent.VK_RIGHT: { // ARRANGE
					ProjectCompendium.APP.onViewArrange(IUIArrange.LEFTRIGHT);
					evt.consume();
					break;
				}
				case KeyEvent.VK_DOWN: { // ARRANGE
					ProjectCompendium.APP.onViewArrange(IUIArrange.TOPDOWN);
					evt.consume();
					break;
				}
				case KeyEvent.VK_R: { // ARRANGE
					ProjectCompendium.APP.onViewArrange(IUIArrange.LEFTRIGHT);
					evt.consume();
					break;
				}
				case KeyEvent.VK_T: { // OPEN TAG WINDOW
					ProjectCompendium.APP.onCodes();
					evt.consume();
					break;
				}
				case KeyEvent.VK_B: { // BOLD / UNBOLD THE TEXT OF ALL SELECTED NODES IN THE CURRENT MAP
					ProjectCompendium.APP.getToolBarManager().addFontStyle(Font.BOLD);
					evt.consume();
					break;
				}
				case KeyEvent.VK_I: { // ITALIC / UNITALIC THE TEXT OF ALL SELECTED NODES IN THE CURRENT MAP
					ProjectCompendium.APP.getToolBarManager().addFontStyle(Font.ITALIC);
					evt.consume();
					break;									}
				case KeyEvent.VK_ENTER: {
					try {
						if (oViewPane.getView() != ProjectCompendium.APP.getHomeView() ) {
							oViewPane.getViewFrame().setClosed(true);

							JDesktopPane pane = ProjectCompendium.APP.getDesktop();
							JInternalFrame frame = pane.getSelectedFrame();
							if (frame instanceof UIMapViewFrame) {
								UIMapViewFrame mapframe = (UIMapViewFrame)frame;
								mapframe.getViewPane().requestFocus();
							} else if (frame instanceof UIListViewFrame) {
								UIListViewFrame listframe = (UIListViewFrame)frame;
								listframe.getUIList().getList().requestFocus();
							}
						}
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}
			}
		}
		else if (modifiers == java.awt.Event.ALT_MASK) {
			switch(keyCode) {
				case KeyEvent.VK_0: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 0);
					evt.consume();
					break;
				}
				case KeyEvent.VK_1: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 1);
					evt.consume();
					break;
				}
				case KeyEvent.VK_2: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 2);
					evt.consume();
					break;
				}
				case KeyEvent.VK_3: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 3);
					evt.consume();
					break;
				}
				case KeyEvent.VK_4: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 4);
					evt.consume();
					break;
				}
				case KeyEvent.VK_5: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 5);
					evt.consume();
					break;
				}
				case KeyEvent.VK_6: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 6);
					evt.consume();
					break;
				}
				case KeyEvent.VK_7: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 7);
					evt.consume();
					break;
				}
				case KeyEvent.VK_8: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 8);
					evt.consume();
					break;
				}
				case KeyEvent.VK_9: {
					ProjectCompendium.APP.createNodeFromStencil(oViewPane, 9);
					evt.consume();
					break;
				}
			}
		}
		else if ((keyCode == KeyEvent.VK_DELETE && modifiers == 0) || (keyCode == KeyEvent.VK_BACK_SPACE && modifiers == 0)) {
			ProjectCompendium.APP.startWaitCursor(oViewPane.getViewFrame());
			onDelete();
			ProjectCompendium.APP.stopWaitCursor(oViewPane.getViewFrame());
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_UP && modifiers == 0) {
			moveCursorUp();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_DOWN && modifiers == 0) {
			moveCursorDown();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_LEFT && modifiers == 0) {
			moveCursorLeft();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_RIGHT && modifiers == 0) {
			moveCursorRight();
			evt.consume();
		}

		else if (keyCode == KeyEvent.VK_PAGE_UP && modifiers == 0) {
			Point oldPoint = oViewPane.getViewFrame().getViewPosition();
			int cCurrentHeight = oViewPane.getViewFrame().getHeight();
			// pageup operation
			// set the pagedown pixels based on the viewframe window hieght but little less
			int newX = oldPoint.x;
			int newY = oldPoint.y - (cCurrentHeight-100);
			if(newY < 0)
				newY = 0;
			oViewPane.getViewFrame().setViewPosition(new Point(newX,newY));
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_PAGE_DOWN && modifiers == 0) {
			Point oldPoint = oViewPane.getViewFrame().getViewPosition();
			int cCurrentHeight = oViewPane.getViewFrame().getHeight();
			// pagedown operation
			// set the pagedown pixels based on the viewframe window hieght but little less
			oViewPane.getViewFrame().setViewPosition(new Point(oldPoint.x, oldPoint.y + (cCurrentHeight-100)));
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_HOME && modifiers == 0) {
			oViewPane.getViewFrame().scrollHome(true);
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_ESCAPE && modifiers == 0) {
			oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
			oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F2 && modifiers == 0) {
			ProjectCompendium.APP.zoomNext();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F3 && modifiers == 0) {
			ProjectCompendium.APP.zoomFit();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F4 && modifiers == 0) {
			ProjectCompendium.APP.zoomFocused();
			evt.consume();
		}
//		else if (keyCode == KeyEvent.VK_F11 && modifiers == 0) {
//			ProjectCompendium.APP.toggleAerialView();
//			evt.consume();
//		}
		else if (keyCode == KeyEvent.VK_F12 && modifiers == 0) {
			onMarkSelectionSeen();		// Mark all selected nodes Seen - mlb
			evt.consume();
		}
		else if ((keyCode == KeyEvent.VK_F12) && (modifiers == java.awt.Event.SHIFT_MASK)) {
			onMarkSelectionUnseen();	// Mark all selected nodes Unseen - mlb
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_SPACE && modifiers == 0) {
			if (oNode != null) {
				oNode.setSelected(true);
				oNode.requestFocus();
				oNode.getUI().setEditing();
			}
			evt.consume();
		}

		bClicked = false;

		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
		if (oViewPane != null && oViewPane.hasFocus() && oViewPane.isEnabled()) {
		    if (oViewPane.getConditionForKeyStroke(keyStroke) == JComponent.WHEN_FOCUSED) {
				ActionListener listener = oViewPane.getActionForKeyStroke(keyStroke);
				if (listener instanceof Action)
					oRepeatKeyAction = (Action)listener;
				else
					oRepeatKeyAction = null;
			} else {
				oRepeatKeyAction = null;
			}

		    if (bIsKeyDown && oRepeatKeyAction != null) {
		    	oRepeatKeyAction.actionPerformed(null);
		    	evt.consume();
		    }
		    else {
		    	bIsKeyDown = true;
		    }
		}
 	}

	/**
	 * Move the mouse cursor up.
	 */
	public void moveCursorUp() {
		try {
			Point pos = new Point(ProjectCompendium.APP._x, ProjectCompendium.APP._y);
			int newY = pos.y - APP_PROPERTIES.getCursorMovementDistance();
			int newX = pos.x;
			SwingUtilities.convertPointFromScreen(pos, oViewPane);

			if (pos.y-APP_PROPERTIES.getCursorMovementDistance() >= 0) {
				JViewport viewport = oViewPane.getViewFrame().getViewport();
				Rectangle rect = viewport.getViewRect();

				if (!rect.contains(pos.x, pos.y-APP_PROPERTIES.getCursorMovementDistance(), 1, 1)) {
					Point point = viewport.getViewPosition();
					viewport.setViewPosition(new Point(point.x, point.y-APP_PROPERTIES.getCursorMovementDistance()));
					_x = pos.x;
					_y = pos.y-APP_PROPERTIES.getCursorMovementDistance();
				}
				else {
					Robot rob = new Robot();
					rob.mouseMove(newX, newY);
					updateMousePosition(new Point(pos.x, pos.y-APP_PROPERTIES.getCursorMovementDistance()));
				}
			}
		}
		catch(AWTException ex) {}
	}

	/**
	 * Move the mouse cursor down.
	 */
	public void moveCursorDown() {
		try {
			Point pos = new Point(ProjectCompendium.APP._x, ProjectCompendium.APP._y);
			int newY = pos.y+APP_PROPERTIES.getCursorMovementDistance();
			int newX = pos.x;
			SwingUtilities.convertPointFromScreen(pos, oViewPane);
			JViewport viewport = oViewPane.getViewFrame().getViewport();
			Rectangle rect = viewport.getViewRect();

			if (!rect.contains(pos.x, pos.y+APP_PROPERTIES.getCursorMovementDistance(), 1, 1)) {
				Point point = viewport.getViewPosition();
				viewport.setViewPosition(new Point(point.x, point.y+APP_PROPERTIES.getCursorMovementDistance()));
				_x = pos.x;
				_y = pos.y+APP_PROPERTIES.getCursorMovementDistance();
			}
			else {
				Robot rob = new Robot();
				rob.mouseMove(newX, newY);
				updateMousePosition(new Point(pos.x, pos.y+APP_PROPERTIES.getCursorMovementDistance()));
			}
		}
		catch(AWTException ex) {}
	}

	/**
	 * Move the mouse cursor right.
	 */
	public void moveCursorRight() {
		try {
			Point pos = new Point(ProjectCompendium.APP._x, ProjectCompendium.APP._y);
			int oldX = pos.x;
			int newY= pos.y;
			int newX = pos.x+APP_PROPERTIES.getCursorMovementDistance();

			SwingUtilities.convertPointFromScreen(pos, oViewPane);

			JViewport viewport = oViewPane.getViewFrame().getViewport();
			Rectangle rect = viewport.getViewRect();
			if (!rect.contains(pos.x+APP_PROPERTIES.getCursorMovementDistance(), pos.y, 1, 1)) {
				Point point = viewport.getViewPosition();
				viewport.setViewPosition(new Point(point.x+APP_PROPERTIES.getCursorMovementDistance(), point.y));
				_x = pos.x+APP_PROPERTIES.getCursorMovementDistance();
				_y = pos.y;
			}
			else {
				Robot rob = new Robot();
				rob.mouseMove(newX, newY);
				updateMousePosition(new Point(pos.x+APP_PROPERTIES.getCursorMovementDistance(), pos.y));
			}
		}
		catch(AWTException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Move the mouse cursor left.
	 */
	public void moveCursorLeft() {
		try {
			Point pos = new Point(ProjectCompendium.APP._x, ProjectCompendium.APP._y);
			int newX = pos.x-APP_PROPERTIES.getCursorMovementDistance();
			int newY = pos.y;

			SwingUtilities.convertPointFromScreen(pos, oViewPane);

			if (pos.x-APP_PROPERTIES.getCursorMovementDistance() >= 0) {

				JViewport viewport = oViewPane.getViewFrame().getViewport();
				Rectangle rect = viewport.getViewRect();

				if (!rect.contains(pos.x-APP_PROPERTIES.getCursorMovementDistance(), pos.y, 1, 1)) {
					Point point = viewport.getViewPosition();
					viewport.setViewPosition(new Point(point.x-APP_PROPERTIES.getCursorMovementDistance(), point.y));
					_x = pos.x-APP_PROPERTIES.getCursorMovementDistance();
					_y = pos.y;
				}
				else {
					Robot rob = new Robot();
					rob.mouseMove(newX, newY);
					updateMousePosition(new Point(pos.x-APP_PROPERTIES.getCursorMovementDistance(), pos.y));
				}
			}
		}
		catch(AWTException ex) {}
	}

	/**
	 * Create a new node of the given type from a key press event.
	 * @param nType, the type of the new node top create.
	 * @param nX, the x position for the new node.
	 * @param nY, the y position for the new node.
	 * @return com.compendium.ui.UINode, the new node.
	 */
	public UINode addNewNode(int nType, int nX, int nY) {

		// SCALE TO ACTUAL 100% size before put in database
		Point loc = UIUtilities.scalePoint(nX, nY, oViewPane.getScale());

		UINode node = createNode(nType,
								 "",
								 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
								 "",
								 "",
								 loc.x,
								 loc.y
								 );

		oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
		oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
		oViewPane.setSelectedNode(node,ICoreConstants.SINGLESELECT);
		node.setSelected(true);

		node.getUI().setEditing();

		return node;
	}

	/**
	 * Create a new node of the given type from a key press event.
	 * @param nType, the type of the new node top create.
	 * @param nX, the x position for the new node.
	 * @param nY, the y position for the new node.
	 * @return com.compendium.ui.UINode, the new node.
	 */
	public UINode addNewNode(int nType, int nX, int nY, String sLabel, String sDetail) {

		// SCALE TO ACTUAL 100% size before put in database
		Point loc = UIUtilities.scalePoint(nX, nY, oViewPane.getScale());

		UINode node = createNode(nType,
								 "",
								 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
								 sLabel,
								 sDetail,
								 loc.x,
								 loc.y
								 );

		oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
		oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
		oViewPane.setSelectedNode(node,ICoreConstants.SINGLESELECT);
		node.setSelected(true);

		node.getUI().setEditing();

		return node;
	}

	/**
	 * Invoked when a key is released in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyReleased(KeyEvent e) {
		bIsKeyDown = false;
		e.consume();
  	}


	/**
	 * Invoked when a key is typed in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyTyped(KeyEvent e) {

		if (!e.isAltDown() && !e.isControlDown() && !e.isMetaDown()) {

			char keyChar = e.getKeyChar();
			char[] key = {keyChar};
			String sKeyPressed = new String(key);

			int nType = -1;

			// IF A SINGLE NODE IS SELECTED, ASSUME THEY ARE TRYING TO TYPE INTO THE LABEL
			//if (oViewPane.getNumberOfSelectedNodes() == 1) {
			//	UINode uinode = oViewPane.getSelectedNode();
			//	uinode.getUI().keyPressed(evt);
			//	return;
			//}

			//if(sKeyPressed.equals("i") || sKeyPressed.equals("I") || sKeyPressed.equals("q") || sKeyPressed.equals("Q") || sKeyPressed.equals("?") || sKeyPressed.equals("/")) {
			//	nType = ICoreConstants.ISSUE;
			//}
			//if(sKeyPressed.equals("p") || sKeyPressed.equals("P") || sKeyPressed.equals("a") || sKeyPressed.equals("A") || sKeyPressed.equals("!") || sKeyPressed.equals("1")) {
			//	nType = ICoreConstants.POSITION;
			//}

			if(sKeyPressed.equals("p") || sKeyPressed.equals("P")) {
				nType = ICoreConstants.POSITION;
			}
			else if (sKeyPressed.equals("q") || sKeyPressed.equals("Q") || sKeyPressed.equals("?") || sKeyPressed.equals("/")) {
				nType = ICoreConstants.ISSUE;
			}
			else if (sKeyPressed.equals("i") || sKeyPressed.equals("I") || sKeyPressed.equals("a") || sKeyPressed.equals("A") || sKeyPressed.equals("!") || sKeyPressed.equals("1")) {
				nType = ICoreConstants.POSITION;
			}
			else if(sKeyPressed.equals("u") || sKeyPressed.equals("U") ) {
				nType = ICoreConstants.ARGUMENT;
			}
			else if(sKeyPressed.equals("r") || sKeyPressed.equals("R") ) {
				nType = ICoreConstants.REFERENCE;
			}
			else if(sKeyPressed.equals("d") || sKeyPressed.equals("D") ) {
				nType = ICoreConstants.DECISION;
			}
			else if(sKeyPressed.equals("n") || sKeyPressed.equals("N")) {
				nType = ICoreConstants.NOTE;
			}
			else if(sKeyPressed.equals("m") || sKeyPressed.equals("M")) {
				nType = ICoreConstants.MAPVIEW;
			}
			else if(sKeyPressed.equals("l") || sKeyPressed.equals("L")) {
				nType = ICoreConstants.LISTVIEW;
			}
			else if(sKeyPressed.equals("+") || sKeyPressed.equals("=")) {
				nType = ICoreConstants.PRO;
			}
			else if(sKeyPressed.equals("-")) {
				nType = ICoreConstants.CON;
			}

			if (nType > -1) {

				int nX = ptLocationKeyPress.x;
				int nY = ptLocationKeyPress.y;

				// IF WE DON'T KNOW WHERE THE MOUSE IS, GET THE LAST KNOW GLOBAL POSITION AND CONVERT IT
				if ( nX == 0 && nY == 0 ) {
					Point p = new Point(ProjectCompendium.APP._x, ProjectCompendium.APP._y);
					SwingUtilities.convertPointFromScreen(p, oViewPane);

					nX = p.x;
					nY = p.y;

					// CHEK THIS POINT IS ACTUALLY VISIBLE, IF NOT CENTER TO SCREEN
					Rectangle rect = oViewPane.getVisibleRect();
					if (!rect.contains(new Point(nX, nY))) {
						nX = rect.x+10;
						nY = rect.y+10;

						// NEED TO DO THIS OR MOUSE EVENTS ARE NOT PASSED TO NEW NODE!
						// ESPECIALLY NOTICABLE IF TRY TO CREATE LINK WITHOUT MOVING MOUSE
						try {
							Point pos = new Point(nX, nY);
							SwingUtilities.convertPointToScreen(pos, oViewPane);
							Robot rob = new Robot();
							rob.mouseMove(pos.x+1, pos.y+1);
						}
						catch(AWTException ex) {}
					}
				}

				// MOVE NEW NODE OUT A BIT SO MOUSEPOINTER NOT RIGHT ON EDGE
				Model oModel = (Model)ProjectCompendium.APP.getModel();
				boolean bSmallIcons = oModel.smallIcons;
				if (bSmallIcons) {
					if (nX >= 10 && nY >= 3) {
						nX -= 10;
						nY -= 3;
					}
				}
				else if (nX >= 20 && nY >= 10) {
					nX -= 20;
					nY -= 10;
				}

				UINode node = addNewNode(nType, nX, nY);

				JViewport viewport = oViewPane.getViewFrame().getViewport();
				Rectangle nodeBounds = node.getBounds();
				Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, nodeBounds.x, nodeBounds.y, viewport);
				viewport.scrollRectToVisible( new Rectangle( parentPos.x, parentPos.y, nodeBounds.width, nodeBounds.height ) );

				//if (!rect.contains(node.getBounds())) {
				//	viewport.scrollRectToVisible(node.getBounds());
				//}
			}
		}

		e.consume();
	}

	/**
	 * Set the node the mouse is currently over or null if not over a node.
	 * @param oNode the node the mouse is currently over or null.
	 */
	public void setCurrentNode(UINode oNode, boolean exited) {
		if (this.oNode == oNode && exited) {
			this.oNode = null;
		} else if (this.oNode == null || this.oNode != oNode && !exited){
			this.oNode = oNode;
		}
	}

	/**
	 * Required for the ClipboardOwner implementation.
   	 */
	public void lostOwnership(Clipboard clip, Transferable trans) {}

	/**
	 * Adds an existing NodePosition to the view pane.
	 * @param pos com.compendium.core.datamodel.NodePosition, the node to add.
	 * @return com.compendium.ui.UINode, the newly added node.
	 */
	public UINode addNode(NodePosition pos) {

		UINode uinode = new UINode(pos, oViewPane.getCurrentAuthor());
		return addNode(uinode);
	}


	/**
	 * Adds an existing UINode to the view pane.
	 * @param pos com.compendium.ui.UINode, the node to add.
	 * @return com.compendium.ui.UINode, the newly added node.
	 */
	public UINode addNode(UINode uinode) {

		// Don't add the same node twice
		UINode oldnode = (UINode)oViewPane.get(uinode.getNode().getId());
		if (oldnode != null)
			return oldnode;

		Dimension d = uinode.getPreferredSize();
		Point p = uinode.getNodePosition().getPos();
		uinode.setBounds(p.x, p.y, d.width, d.height);

		oViewPane.add(uinode, UIViewPane.NODE_LAYER);
		return uinode;
	}

	/**
	 * Remove the given UINode from the view pane.
	 * @param pos com.compendium.ui.UINode, the node to remove.
	 */
	public void removeNode(UINode uinode)  {
		oViewPane.remove(uinode);
	}

	/**
	 * Add the given UILink to the view pane.
	 * @param pos com.compendium.ui.UILink, the link to add.
	 */
	public UILink addLink(ILink link)  {

		// Don't add the same link twice
		UILink oldlink = (UILink)oViewPane.get(link.getId());
		if (oldlink != null) {
			return oldlink;
		}

		UINode uifrom = getUINode(link.getFrom().getId());
		UINode uito = getUINode(link.getTo().getId());
		if ( (uifrom == null) || (uito == null)) {
			return null;
		}

		UILink uilink = new UILink((Link)link, uifrom, uito);

		double currentScale = oViewPane.getZoom();
		AffineTransform trans=new AffineTransform();
		trans.setToScale(currentScale, currentScale);
		uilink.scaleArrow(trans);

		oViewPane.add(uilink, UIViewPane.LINK_LAYER);
		uilink.setBounds(uilink.getPreferredBounds());

		uifrom.addLink(uilink);
		uito.addLink(uilink);

		return uilink;
	}

	/**
	 * Remove the given UILink from the view pane.
	 * @param pos com.compendium.ui.UILink, the link to remove.
	 */
	public void removeLink(UILink uilink)  {
		oViewPane.removeLink(uilink);
	}

	/**
	 * Return the UINode with the given id.
	 * @param id java.lang.String.
	 * @return com.compendium.ui.UINode, the UINode for the given id if found, else null.
	 */
	public UINode getUINode(String id) {

		Component array[] = oViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		UINode uinode = null;
		int i=0;
		while(i<array.length) {
			uinode = (UINode)array[i++];
			if((uinode.getNode().getId()).equals(id)) {
				return uinode;
			}
		}

		return null;
	}

	/**
	 * Returns a UINode from the UIViewPane given a NodePosition object.
	 * @param id java.lang.String.
	 * @return com.compendium.ui.UINode, the UINode for the given id if found, else null.
	 */
	public UINode getUINode(NodePosition pos) {

		Component array[] = oViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		UINode uinode = null;
		int i=0;
		while(i<array.length) {
			uinode = (UINode)array[i++];
			if((uinode.getNode().getId()).equals((pos.getNode()).getId())) {
				return uinode;
			}
		}

		return null;
	}

  	/**
     * Creates a new node from the passed parameters.
	 * @param nodeType, the type of the new node.
	 * @param sOriginalID, the original id of this node is it has been imported.
	 * @param author, the author of this node.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in this view.
	 * @return com.compendium.ui.UINode, the newly create node.
     */
	public UINode createNode(int nodeType, String sOriginalID, String author, String label,
							String detail, int x, int y) {

		return createNode(nodeType, sOriginalID, author, label, detail, x, y, null);
  	}

  	/**
     * Creates a new node from the passed parameters.
	 * @param nodeType, the type of the new node.
	 * @param sOriginalID, the original id of this node is it has been imported.
	 * @param author, the author of this node.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in this view.
	 * @param reference, the reference to associate with the new node.
	 * @return com.compendium.ui.UINode, the newly create node.
     */
  	public UINode createNode(int nodeType, String sOriginalID,
							String author, String label,
							String detail, int x, int y, String reference) {

		return createNode(nodeType, "", sOriginalID, author, label, detail, x, y, reference);
	}

  	/**
     * Creates a new node from the passed parameters.
	 * @param nodeType, the type of the new node.
	 * @param importedId, the id given this node in the imported data.
	 * @param sOriginalID, the original id of this node.
	 * @param author, the author of this node.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in this view.
	 * @param reference, the reference to associate with the new node.
	 * @return com.compendium.ui.UINode, the newly create node.
     */
	public UINode createNode(int nodeType, String importedId, String sOriginalID,
							String author, String label,
							String detail, int x, int y, String reference) {

		UINode uinode = null;

		if (detail == null) {
			detail = "";
	  	}

	  	oViewPane = getViewPane();

	  	if((nodeType != ICoreConstants.MAPVIEW) && (nodeType != ICoreConstants.LISTVIEW)) {
			try {
				NodePosition nodePos = oViewPane.getView().addMemberNode(nodeType,			//int type
													"",						//String xNodeType,
													importedId,				//String xml imported id
													sOriginalID,			//String original id,
													author,					//String author,
													label,					//String label
													detail,					//String detail
													x,						//int x
													y
													);						//int y
				NodeSummary node = nodePos.getNode();
				if (reference != null && !reference.equals("")) {
					node.setSource(reference, "", author);
				}
				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

				//add the node to UIviewpane now to reflect new node
				uinode = (UINode)oViewPane.get(node.getId());
				if (uinode == null)
					uinode = addNode(nodePos);
			}
			catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.CreateNode.actionPerformed)\n\n "+e.getMessage());
			}
	 	}
	  	else {
			try {
				NodePosition nodePos = oViewPane.getView().addMemberNode(nodeType,			//int type
													"",						//String xNodeType,
													importedId,				//String xml imported id
													sOriginalID,			//String originalId,
													author,					//String author,
													label,					//String label
													detail,					//String detail
													x,						//int x
													y
													);

				View view = (View) nodePos.getNode();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

				//add the node to UIviewpane now to reflect new node
				uinode = addNode(nodePos);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in (ViewPaneUI.CreateNode.actionPerformed)\n\n"+e.getMessage());
			}
		}

		//the newly created node gets the focus!
		if (uinode != null) {
			uinode.setRollover(true);
			uinode.requestFocus();
		}

		ProjectCompendium.APP.refreshNodeIconIndicators(oViewPane.getView().getId());

		return uinode;
  	}

  	/**
     * Creates a new node from the passed parameters for imported data.
	 * @param nodeType, the type of the new node.
	 * @param importedId, the id given this node in the imported data.
	 * @param sOriginalID, the original id of this node.
	 * @param author, the author of this node.
	 * @param creationDate, the date the node was created.
	 * @param modDate, the date the node was last modified.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in this view.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 *
	 * @return com.compendium.ui.UINode, the newly create node.
     */
	public UINode createNode(int nodeType, String importedId, String sOriginalID,
							String author, Date creationDate, Date modDate, String label,
							String detail, int x, int y,
							Date transCreationDate, Date transModDate, String sLastModAuthor,
							boolean bShowTags, boolean bShowText, boolean bShowTrans, boolean bShowWeight,
							boolean bSmallIcon, boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
							int nFontStyle, int nForeground, int nBackground) {

		return createNode(nodeType, importedId, sOriginalID, author, creationDate, modDate, label,
							detail, x, y, "", "", transCreationDate, transModDate, sLastModAuthor,
							bShowTags, bShowText, bShowTrans,bShowWeight,
							bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace,
							nFontStyle, nForeground, nBackground);

	}

  	/**
     * Creates a new node from the passed parameters for imported data.
	 * @param nodeType the type of the new node.
	 * @param importedId the id given this node in the imported data.
	 * @param sOriginalID the original id of this node.
	 * @param author the author of this node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param label the label for this node.
	 * @param detail the main detail page for this node.
	 * @param x the x position to place this node at in this view.
	 * @param y the y position, or row to place this node at in this view.
	 * @param source the source field for this node.
	 * @param image the image field for this node.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 *
	 * @return com.compendium.ui.UINode, the newly create node.
     */
	public UINode createNode(int nodeType, String importedId, String sOriginalID,
							String author, Date creationDate, Date modDate, String label,
							String detail, int x, int y, String source, String image,
							Date transCreationDate, Date transModDate, String sLastModAuthor,
							boolean bShowTags, boolean bShowText, boolean bShowTrans, boolean bShowWeight,
							boolean bSmallIcon, boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
							int nFontStyle, int nForeground, int nBackground) {

		UINode uinode = null;

		if (detail == null) {
			detail = "";
	  	}

	  	oViewPane = getViewPane();

	  	if((nodeType != ICoreConstants.MAPVIEW) && (nodeType != ICoreConstants.LISTVIEW)) {
			try {
				NodePosition nodePos = oViewPane.getView().addMemberNode(nodeType, "",
													importedId,	sOriginalID, author, creationDate,
													modDate, label,	detail,	x, y, transCreationDate,
													transModDate, sLastModAuthor, bShowTags, bShowText,
													bShowTrans,	bShowWeight, bSmallIcon,	bHideIcon, nWrapWidth,
													nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
				NodeSummary node = nodePos.getNode();
				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

				//add the node to UIviewpane now to reflect new node
				uinode = (UINode)oViewPane.get(node.getId());

				if ((source != null && !source.equals("")) || (image != null && !image.equals(""))) {
					node.setSource(source, image, author);
				}

				if (uinode == null)
					uinode = addNode(nodePos);
			}
			catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.CreateNode.actionPerformed)\n\n "+e.getMessage());
			}
	 	}
	  	else {
			try {
				NodePosition nodePos = oViewPane.getView().addMemberNode(nodeType, "",
						importedId,	sOriginalID, author,	creationDate,
						modDate, label,	detail,	x, y, transCreationDate,
						transModDate, sLastModAuthor, bShowTags, bShowText,
						bShowTrans,	bShowWeight, bSmallIcon,	bHideIcon, nWrapWidth,
						nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

				View view = (View) nodePos.getNode();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

				if (image != null && !image.equals("")) {
					view.setSource("", image, author);
				}

				//add the node to UIviewpane now to reflect new node
				uinode = addNode(nodePos);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in (ViewPaneUI.CreateNode.actionPerformed)\n\n"+e.getMessage());
			}
		}

		//the newly created node gets the focus!
		if (uinode != null) {
			uinode.setRollover(true);
			uinode.requestFocus();
		}

		ProjectCompendium.APP.refreshNodeIconIndicators(oViewPane.getView().getId());

		return uinode;
	}

  	/**
     * Creates a new node from the passed parameters.
	 * @param sNodeID, the id to give this node.
	 * @param nodeType, the type of the new node.
	 * @param sOriginalID, the original id of this node.
	 * @param author, the author of this node.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in this view.
	 * @return com.compendium.ui.UINode, the newly create node.
     */
	public UINode createNode(String sNodeID, int nodeType, String sOriginalID,
							String author, String label,
							String detail, int x, int y) {

		UINode uinode = null;

		if (detail == null) {
			detail = "";
	  	}

	  	oViewPane = getViewPane();

	  	if((nodeType != ICoreConstants.MAPVIEW) && (nodeType != ICoreConstants.LISTVIEW)) {
			try {
				NodePosition nodePos = oViewPane.getView().addMemberNode(sNodeID,
													nodeType,				//int type
													"",						//String xNodeType,
													"",						//String xml imported id
													sOriginalID,			//String original id,
													author,					//String author,
													label,					//String label
													detail,					//String detail
													x,						//int x
													y						//int y
													);
				NodeSummary node = nodePos.getNode();

				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

				//add the node to UIviewpane now to reflect new node
				uinode = (UINode)oViewPane.get(node.getId());
				if (uinode == null)
					uinode = addNode(nodePos);
			}
			catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.CreateNode.actionPerformed)\n\n "+e.getMessage());
			}
	 	}
	  	else {
			try {
				NodePosition nodePos = oViewPane.getView().addMemberNode(sNodeID,
													nodeType,				//int type
													"",						//String xNodeType,
													""		,				//String xml imported id
													sOriginalID,			//String originalId,
													author,					//String author,
													label,					//String label
													detail,					//String detail
													x,						//int x
													y
													);

				View view = (View) nodePos.getNode();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

				//add the node to UIviewpane now to reflect new node
				uinode = addNode(nodePos);
			}
			catch (Exception e) {
				System.out.println("Error in (ViewPaneUI.CreateNode.actionPerformed)\n\n"+e.getMessage());
			}
		}

		//the newly created node gets the focus!
		if (uinode != null) {
			uinode.setRollover(true);
			uinode.requestFocus();
		}

		ProjectCompendium.APP.refreshNodeIconIndicators(oViewPane.getView().getId());

		return uinode;
  	}

  	/**
   	 * Creates a clone node to the given node
 	 *
	 * @param uinode com.compendium.uiUINode, the node to clone.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in the view.
	 * @return com.compendium.ui.UINode, the newly create cloned node.
     */
 	public UINode createCloneNode(UINode oNode, int x, int y) {

		// SCALE TO ACTUAL 100% size before put in database
		Point loc = UIUtilities.scalePoint(x, y, oViewPane.getScale());

		NodeSummary parent = oNode.getNode();
		NodePosition pos = oNode.getNodePosition();

		String author = parent.getAuthor();
		String label = parent.getLabel();
		String detail = parent.getDetail();
		int nodeType = parent.getType();

		String sAuthor = getViewPane().getCurrentAuthor();

		UINode uinode = null;

		Date date = new Date();

		if(!(parent instanceof ShortCutNodeSummary)) {
			String image = parent.getImage();
			String source = parent.getSource();

		  	uinode = createNode(nodeType, "", "", author, date, date, label, detail, loc.x, loc.y,
		  			source, image, date, date, author,	pos.getShowTags(), pos.getShowText(), pos.getShowTrans(),
		  			pos.getShowWeight(),pos.getShowSmallIcon(), pos.getHideIcon(), pos.getLabelWrapWidth(),
		  			pos.getFontSize(), pos.getFontFace(), pos.getFontStyle(), pos.getForeground(),
		  			pos.getBackground());

			NodePosition nodePos = uinode.getNodePosition();
			NodeSummary node = nodePos.getNode();

			try {
				if(nodeType == ICoreConstants.MAPVIEW) {
					String sBackground = "";
					ViewLayer layer  = ((View)oNode.getNode()).getViewLayer();
					if (layer == null) {
						try { ((View)oNode.getNode()).initializeMembers();
							sBackground = layer.getBackground();
						}
						catch(Exception ex) {}
					}
					else {
						sBackground = layer.getBackground();
					}
					if (!sBackground.equals("")) {
						((View)node).setBackground( sBackground );
						((View)node).updateViewLayer();
					}
				}

				if(node == null) {
					ProjectCompendium.APP.displayMessage("Cannot clone " + label, "Clone Error..");
					return(null);
				}

				//get the detailpages for the parent node. The cloned node has the same details

				Vector details = parent.getDetailPages(author);
				Vector newDetails = new Vector();
				int count = details.size();
				for (int i=0; i<count; i++) {
					NodeDetailPage page = (NodeDetailPage)details.elementAt(i);
					NodeDetailPage newPage = new NodeDetailPage(node.getId(), page.getAuthor(), page.getText(), page.getPageNo(), page.getCreationDate(), page.getModificationDate());
					newDetails.addElement(newPage);
				}

				if (!newDetails.isEmpty())
					node.setDetailPages(newDetails, sAuthor, sAuthor);

				//get the codes for the parent nodes.. even the cloned node has the same codes
				for(Enumeration e = parent.getCodes();e.hasMoreElements();) {
					Code code = (Code)e.nextElement();
					if(node.addCode(code))	//means the code is added
						System.out.println("Cannot add " + code.getName() + " to "  + label);
				}
			}
			catch(Exception ex) {
				System.out.println("Exception in clone node: "+ex.getMessage());
			}

		}
		else {
			ProjectCompendium.APP.displayMessage("You cannot clone a shortcut", "Clone Error..");
		}

		return uinode;
  	}

  	/**
   	 * Creates a shortcut node to the given node
 	 *
	 * @param uinode com.compendium.uiUINode, the node to create a shortcut to.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in the view.
	 * @return com.compendium.ui.UINode, the newly create shortcut node.
     */
  	public UINode createShortCutNode(UINode oNode, int x, int y) {

		// SCALE TO ACTUAL 100% size before put in database
		Point loc = UIUtilities.scalePoint(x, y, oViewPane.getScale());

		UINode uinode = null;
		NodePosition pos = oNode.getNodePosition();

		String sAuthor = getViewPane().getCurrentAuthor();
		Date date = new Date();

		String label = oNode.getText();
		String detail = "";
		int nodeType = (oNode.getNode()).getType() + ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT;

		String image = oNode.getNode().getImage();
		String source = oNode.getNode().getSource();

	  	uinode = createNode(nodeType, "", "", sAuthor, date, date, label, detail, loc.x, loc.y,
	  			source, image, date, date, sAuthor, pos.getShowTags(), pos.getShowText(), pos.getShowTrans(),
	  			pos.getShowWeight(),pos.getShowSmallIcon(), pos.getHideIcon(), pos.getLabelWrapWidth(),
	  			pos.getFontSize(), pos.getFontFace(), pos.getFontStyle(), pos.getForeground(),
	  			pos.getBackground());

		NodeSummary node = uinode.getNode();

		try {
			(oNode.getNode()).addShortCutNode(node);
			((ShortCutNodeSummary)node).setReferredNode(oNode.getNode());
		}
		catch(Exception ex) {
			System.out.println("Exception: in create shortcut: "+ex.getMessage());
		}
		return uinode;
  	}

  	/**
     * Add the given node to this view with the given format. Doesnt create any new nodes.
     * Some parameters are passed for easy importing.
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to add.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in the view.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 *
	 * @return com.compendium.ui.UINode, the newly created node.
     */
	public UINode addNodeToView(NodeSummary node, int x, int y,
							boolean bShowTags,
							boolean bShowText,
							boolean bShowTrans,
							boolean bShowWeight,
							boolean bSmallIcon,
							boolean bHideIcon,
							int 	nWrapWidth,
							int 	nFontSize,
							String 	sFontFace,
							int 	nFontStyle,
							int 	nForeground,
							int 	nBackground) {

		NodePosition nodePos = null;
		UINode oUINode = null;
		int nodeType = node.getType();

		oViewPane = getViewPane();
		try {
			//add the node to this view
			nodePos = oViewPane.getView().addNodeToView(node, x,y, bShowTags, bShowText, bShowTrans, bShowWeight,
					bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace, nFontStyle,
					nForeground, nBackground);

			if (nodePos != null) {
				//add the node to UIviewpane now to reflect new node
				oUINode = addNode(nodePos);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Error in 'ViewPaneUI.CreateNodeAction.actionPerformed'"+e.getMessage());
		}

		return oUINode;
  	}

  	/**
     * Add the given node to this view. Doesnt create any new nodes.
     * Some parameters are passed for easy importing.
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to add.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in the view.
	 * @return com.compendium.ui.UINode, the newly created node.
     */
	public UINode addNodeToView(NodeSummary node, int x, int y) {

		NodePosition nodePos = null;
		UINode oUINode = null;
		int nodeType = node.getType();

		oViewPane = getViewPane();
		try {
			//add the node to this view
			nodePos = oViewPane.getView().addNodeToView(node, x,y);

			if (nodePos != null) {
				//add the node to UIviewpane now to reflect new node
				oUINode = addNode(nodePos);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Error in 'ViewPaneUI.CreateNodeAction.actionPerformed'"+e.getMessage());
		}

		return oUINode;
  	}


  	/**
     * Adds the given node to this view. Doesnt create any new nodes
     * Some parameters are passed for easy importing.
	 *
	 * @param uinode com.compendium.uiUINode, the node to add.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in the view.
	 * @return com.compendium.ui.UINode, the newly created node.
     */
	public UINode addNodeToView(UINode uinode, int x, int y) {
		NodeSummary node = uinode.getNode();
		return(addNodeToView(node, x, y));
	}

	/**
	 * Creates a link from the given parameters.
	 *
	 * @param uifrom com.compendium.ui.UINode, the originating node for this link.
	 * @param uito com.compendium.ui.UINode, the destination node for this link.
	 * @param type, the type of this link.
	 * @param arrow, the type of arrow heads to draw.
	 * @return com.compendium.ui.UILink, the newly created link.
	 */
	public UILink createLink(UINode uifrom, UINode uito, String type, int arrow) {

		oViewPane = getViewPane();
		UILink uilink = null;

		if(oViewPane.getView().getType() != ICoreConstants.LISTVIEW) {

			String id = oViewPane.getView().getModel().getUniqueID();
			NodeSummary from = uifrom.getNode();
			NodeSummary to	= uito.getNode();
			int permission = ICoreConstants.WRITE;
			String sOriginalID = id;

			try {
				//add the link to the datamodel view
				Link link = (Link)oViewPane.getView().addMemberLink(type,
															sOriginalID,
															ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
															from,
															to,
															arrow);

				link.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

				//create a link in UI layer
				uilink = addLink(link);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				ProjectCompendium.APP.displayError("Error: could not create new link in "+oViewPane.getView().getLabel()+" (ViewPaneUI.createLink)\n\n"+ex.getMessage());
			}
		}
		return uilink;
  	}

 	/**
   	 * Creates a new link from a link copied from an external database (to the currently opened one).
	 * @param link, the link to create.
	 * @param uifrom, the originating node for this link.
	 * @param uito, the destination node for this link.
   	 */
  	private ILink createLink(Link link, NodeSummary from, NodeSummary to) {

		oViewPane = getViewPane();
		UILink uilink = null;

        Date creationDate 	= new Date();
  	  	Date modificationDate = new Date();

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		ILinkService oLinkService = model.getLinkService();

   	    Link newlink = null;
       	try {
           	newlink = oLinkService.createLink(session,
                                link.getId(),
                                creationDate,
                                modificationDate,
                                link.getAuthor(),
                                link.getType(),
								"",
                                link.getOriginalID(),
                                from.getId(),
                                to.getId(),
								link.getLabel(),
                                link.getArrow());

			newlink.initialize(session, model);
	    	newlink.setFrom(from);
  	   		newlink.setTo(to);
       	}
       	catch (Exception ex) {
           	ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.createLink) \n\n"+ex.getMessage());
       	}

		return (ILink)newlink;
  	}

  	/**
     * Add the given link to this view. Doesnt create any new links.
     * Some parameters are passed for easy importing.
	 *
	 * @param node com.compendium.core.datamodel.link, the link to add.
	 * @return com.compendium.ui.UILink, the newly created link.
     */
	public UILink addLinkToView(ILink link) {

		if (link == null) {
			return null;
		}

		UILink oUILink = null;
		oViewPane = getViewPane();
		try {
			//add the node to this view
			boolean added = oViewPane.getView().addLinkToView((Link)link);
			if (added) {
				oUILink = addLink((Link)link);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Error in 'ViewPaneUI.addLinktoView'"+e.getMessage());
		}

		return oUILink;
  	}

	/**
	 * Mark all nodes in the selection as read/seen		MLB: Feb. '08
	 */
	public void onMarkSelectionSeen() {
		if (oViewPane.getNumberOfSelectedNodes() > 0) {
			oViewPane.markSelectionSeen();
		}
	}

	/**
	 * Mark all nodes in the selection as unread/unseen		MLB: Feb. '08
	 */
	public void onMarkSelectionUnseen() {
		if (oViewPane.getNumberOfSelectedNodes() > 0) {
			oViewPane.markSelectionUnseen();
		}
	}

	/**
	 * Deletes the selected objects.
	 */
	public void onDelete() {
	  	UINode node = oViewPane.getSelectedNode();
		if (node != null) {
			NodeUI nodeui = (NodeUI)node.getUI();
			if (nodeui != null && nodeui.isEditing()) {
				nodeui.delete();
				return;
			}
		}

		// record the effect of the deletion
		// need to pass to this method the info you need to recreate the nodes/links
		DeleteEdit edit = new DeleteEdit(oViewPane.getViewFrame());

		oViewPane.deleteSelectedNodesAndLinks(edit);

		// notify the listeners
		oViewPane.getViewFrame().getUndoListener().postEdit(edit);
		oViewPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		//Thread thread = new Thread() {
		//	public void run() {
				ProjectCompendium.APP.setTrashBinIcon();
				ProjectCompendium.APP.refreshNodeIconIndicators(oViewPane.getView().getId());
		//	}
		//};
		//thread.start();
	}

  	/**
   	 * Selects all the objects in the view.
   	 */
  	public void onSelectAll() {

	  	UINode node = oViewPane.getSelectedNode();
		if (node != null) {
			NodeUI nodeui = (NodeUI)node.getUI();
			if (nodeui != null && nodeui.isEditing()) {
				nodeui.selectAll();
				return;
			}
		}

		getViewPane().selectAll();
  	}

  	/**
   	 * Return if a copy to the Clipbaord is currently underway.
	 * @return boolean, true is a copy is underway, else false.
   	 */
	public boolean isCopyToClipboard() {
		return bCopyToClipboard;
	}

  	/**
   	 * Return if a cut to the Clipbaord is currently underway.
	 * @return boolean, true is a cut is underway, else false.
   	 */
	public boolean isCutToClipboard() {
		return bCutToClipboard;
	}

	/**
	 * This method copies the selected nodes to the clipboard and also
	 * stores all view children for potential subsequent pasting into another database.
	 * @param nodeui com.compendium.ui.plaf.NodeUI, if copy is activated from node right-click menu,
	 * then this is the node it was activated on.
	 */
	public void externalCopyToClipboard(NodeUI nodeui, String userID) {

		bCopyToClipboard = true;
	  	ClipboardTransferables clips = new ClipboardTransferables();

	  	// copy the objects selected
	  	if ((oViewPane.getNumberOfSelectedLinks() == 0) && (oViewPane.getNumberOfSelectedNodes() == 0)) {
			if (nodeui != null) {
				clips.addTransferables(nodeui.getUINode().getNodePosition());
				if (nodeui.getUINode().getNode() instanceof View) {
					View view = (View)nodeui.getUINode().getNode();
					view.storeChildren(new Hashtable(51));
				}
			}
	  	}
	  	else {
			for(Enumeration e = oViewPane.getSelectedLinks();e.hasMoreElements();) {
				UILink uilink = (UILink)e.nextElement();
				clips.addTransferables(uilink.getUI());
		 	}
		 	for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				clips.addTransferables(uinode.getNodePosition());
				uinode.setCut(true);
				if (uinode.getNode() instanceof View) {
					View view = (View)uinode.getNode();
					view.storeChildren(new Hashtable(51));
				}
			}
	  	}

	  	ProjectCompendium.APP.getClipboard().setContents(clips,this);
	}

	/**
	 * This method copies the selected nodes to the clipboard.
	 * @param nodeui com.compendium.ui.plaf.NodeUI, if copy is activated from node right-click menu,
	 * then this is the node it was activated on.
	 */
	public void copyToClipboard(NodeUI nodeui) {

	  	UINode node = oViewPane.getSelectedNode();
		if (node != null) {
			NodeUI ui = (NodeUI)node.getUI();
			if (ui != null && ui.isEditing()) {
				ui.copy();
				bCopyToClipboard = true;
				ProjectCompendium.APP.setPasteEnabled(true);
				return;
			}
		}

		bCopyToClipboard = true;
	  	ClipboardTransferables clips = new ClipboardTransferables();

		String sInBox = ProjectCompendium.APP.getInBoxID();

	  	// copy the objects selected
	  	if ((oViewPane.getNumberOfSelectedLinks() == 0) && (oViewPane.getNumberOfSelectedNodes() == 0)) {
			if (nodeui != null) {
				if (nodeui.oNode.getType() != ICoreConstants.TRASHBIN
						&& !nodeui.oNode.getNode().getId().equals(sInBox)) {
					clips.addTransferables(nodeui);
				}
			}
	  	}
	  	else {
			for(Enumeration e = oViewPane.getSelectedLinks();e.hasMoreElements();) {
				UILink uilink = (UILink)e.nextElement();
				clips.addTransferables(uilink.getUI());
		 	}
		 	for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				if (uinode.getType() != ICoreConstants.TRASHBIN
						&& !uinode.getNode().getId().equals(sInBox)) {

					clips.addTransferables(uinode.getUI());
					uinode.setCut(true);
				}
			}
	  	}

		if (clips.getTransferables().hasMoreElements()) {
			ProjectCompendium.APP.getClipboard().setContents(clips,this);
			ProjectCompendium.APP.setPasteEnabled(true);
		}
	}

	/**
	 * This method cuts the selected nodes to the clipboard, and deletes them from the view.
	 * @param nodeui com.compendium.ui.plaf.NodeUI, if cut is activated from node right-click menu,
	 * then this is the node it was activated on.
	 */
	public void cutToClipboard(NodeUI nodeui) {

		String userID = ProjectCompendium.APP.getModel().getUserProfile().getId();
	  	UINode node = oViewPane.getSelectedNode();
		if (node != null) {
			NodeUI ui = (NodeUI)node.getUI();
			if (ui != null && ui.isEditing()) {
				ui.cut();
				bCutToClipboard = true;
				ProjectCompendium.APP.setPasteEnabled(true);
				return;
			}
		}

		bCutToClipboard = true;

		ClipboardTransferables clips = new ClipboardTransferables();

		// record the effect of the deletion
		// need to pass to this method the info you need to recreate the nodes/links
		CutEdit edit = new CutEdit(oViewPane.getViewFrame());

		String sInBox = ProjectCompendium.APP.getInBoxID();

		if ((oViewPane.getNumberOfSelectedLinks() == 0) && (oViewPane.getNumberOfSelectedNodes() == 0)) {
			if (nodeui != null) {
				oViewPane.setSelectedNode(nodeui.getUINode(), ICoreConstants.SINGLESELECT);
				oViewPane.deleteSelectedNodesAndLinks(edit);

				if (nodeui.oNode.getType() != ICoreConstants.TRASHBIN
						&& !nodeui.oNode.getNode().getId().equals(sInBox)) {

					clips.addTransferables(nodeui);
				}
			}
		}
		else {
			// STORE LINKS TO CLIPBOARD
			for(Enumeration e = oViewPane.getSelectedLinks();e.hasMoreElements();) {
				UILink uilink = (UILink)e.nextElement();
				clips.addTransferables(uilink.getUI());
			}

			// STORE NODES TO CLIPBOARD
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();

				if (uinode.getType() != ICoreConstants.TRASHBIN
						&& !uinode.getNode().getId().equals(sInBox)) {

					clips.addTransferables(uinode.getUI());
					uinode.setCut(true);
				}
			}

			// MARK NODES AS DELETED
			oViewPane.deleteSelectedNodesAndLinks(edit);
	  	}

		// notify the listeners
		if (edit.vtUndoNodes.size() > 0 || edit.vtUndoLinks.size()>0) {
			oViewPane.getViewFrame().getUndoListener().postEdit(edit);
			ProjectCompendium.APP.getClipboard().setContents(clips, this);
			ProjectCompendium.APP.setPasteEnabled(true);
		}

		// update node inidicators
		//Thread thread = new Thread() {
		//	public void run() {
				ProjectCompendium.APP.setTrashBinIcon();
				ProjectCompendium.APP.refreshIconIndicators();
		//	}
		//};
		//thread.start();
	}

	/**
	 * This routine gets the clipboard contents, and paste it into this view.
	 */
	public void pasteFromClipboard() {

		String sAuthor = getViewPane().getCurrentAuthor();

	  	UINode node = oViewPane.getSelectedNode();
		if (node != null) {
			NodeUI ui = (NodeUI)node.getUI();
			if (ui != null && ui.isEditing()) {
				ui.paste();
				bCopyToClipboard = false;
				bCutToClipboard = false;
				return;
			}
		}

		ProjectCompendium.APP.setWaitCursor();

		ClipboardTransferables clipui = null;
		PasteEdit.nodeList = new Hashtable();
		boolean bViewportSet = false;

		IModel model = oViewPane.getView().getModel();
		PCSession session = model.getSession();

		if((clipui = (ClipboardTransferables)(ProjectCompendium.APP.getClipboard().getContents(this))) != null) {

			try {
				// record the effect of the paste
				// need to pass to this method the info you need to delete/recreate the nodes&links
				PasteEdit edit = new PasteEdit(oViewPane.getViewFrame());

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
				String sViewID = oViewPane.getView().getId();
				INodeService nodeService = model.getNodeService();

				Point pdisp = getDisplacement(clipui);

				// NEED TO SCALE THIS POINT
				//Point loc = oViewPane.transformCoordinates(pdisp.x, pdisp.y);

				int xdisp = pdisp.x;
				int ydisp = pdisp.y;
				int i = 0;

				// DRAW NODES
				for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {
					i++;

					Object o = e.nextElement();

					if((o instanceof NodeUI) || (o instanceof NodePosition)) {

						NodeSummary pasteNodeSummary = null;
						int xpos = 0;
						int ypos = 0;
						NodeUI nodeui = null;
						NodePosition np = null;

						if (o instanceof NodePosition) {
							np = (NodePosition)o;
							pasteNodeSummary = np.getNode();
							UINode uiNode = new UINode(np, sAuthor);
							nodeui = uiNode.getUI();
							xpos = np.getXPos();
							ypos = np.getYPos();
						}
						else {
							nodeui = (NodeUI)o;
							np = nodeui.getUINode().getNodePosition();
							pasteNodeSummary = nodeui.getUINode().getNode();
							xpos = nodeui.getUINode().getNodePosition().getXPos();
							ypos = nodeui.getUINode().getNodePosition().getYPos();
						}

						// MAKE SURE NOT TRYING TO PASTE NODE INTO VIEW IT IS ALREADY IN
						if (oViewPane.getView().containsNodeSummary(pasteNodeSummary)) {
							UINode uinode = (UINode)oViewPane.get(pasteNodeSummary.getId());
							uinode.setSelected(true);
							oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
							continue;
						}

						// IF NODE WAS DELETED, RESTORE NODE
						String sNodeID = pasteNodeSummary.getId();
						if (nodeService.isMarkedForDeletion(session, sNodeID)) {
							nodeService.restoreNode(session, sNodeID);
						}

						// PASTE CREATES A NEW OBJECT, SO PURGE ANY OLD VIEWNODE RECORDS FOR THIS VIEW/NODE COMBO
						nodeService.purgeViewNode(session, sViewID, sNodeID);

						// PURGE ANY PREVIOUS LINKS THE NODE HAD IN THIS VIEW
						model.getLinkService().purgeViewNode(session, sViewID, sNodeID);

						//NodeSummary newPasteNodeSummary = nodeService.getNodeSummary(session, sNodeID);
						//UINode uiNodeInView = (UINode)getViewPane().get(pasteNodeSummary.getId());

						UINode newUINode = addNodeToView(pasteNodeSummary, xpos+xdisp, ypos+ydisp, np.getShowTags(),
													np.getShowText(), np.getShowTrans(), np.getShowWeight(), np.getShowSmallIcon(),
													np.getHideIcon(), np.getLabelWrapWidth(), np.getFontSize(), np.getFontFace(),
													np.getFontStyle(), np.getForeground(), np.getBackground());
						if (newUINode != null) {

							edit.AddNodeToEdit(newUINode);
							newUINode.setSelected(true);
							oViewPane.setSelectedNode(newUINode,ICoreConstants.MULTISELECT);

							if (pasteNodeSummary instanceof View) {
								ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, newUINode.getNode().getId());

								View deletedView = (View)pasteNodeSummary;
								View newView = (View)newUINode.getNode();
								UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(newView, newView.getLabel());
								if (deletedView.getType() == ICoreConstants.LISTVIEW) {
									((UIListViewFrame)deletedUIViewFrame).getUIList().getListUI().restoreDeletedNodes(deletedView);
								}
								else {
									((UIMapViewFrame)deletedUIViewFrame).getViewPane().getViewPaneUI().restoreDeletedNodesAndLinks(deletedView);
								}
							}
							newUINode.getUI().refreshBounds();
						}
						else {
							System.out.println("Node unable to be pasted: "+pasteNodeSummary.getLabel());
						}
					}
				}

				// DRAW LINKS
				// go through contents again to handle links cannot be drawn until nodes are in place.
				for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {
					Object o = e.nextElement();
					if(o instanceof LinkUI) {

						LinkUI linkui = (LinkUI)o;
						Link pasteLink = linkui.getUILink().getLink();
						String sLinkID = pasteLink.getId();

						//add the link to the view if it isn't already in there
						UILink uiLinkInView = (UILink)getViewPane().get(sLinkID);
						if (uiLinkInView == null) {

							String newFromId = pasteLink.getFrom().getId();
							String newToId = pasteLink.getTo().getId();

							UINode newFromNode = getUINode(newFromId);
							UINode newToNode = getUINode(newToId);

							if((newFromNode != null) && (newToNode != null)) {

								// MAKE SURE THESE TWO NODES ARE NOT ALREADY LINKED
							    if (!newFromNode.containsLink(newToNode)) {

									// IF LINK WAS DELETED, RESTORE LINK
									ILinkService linkService = model.getLinkService();
									if (linkService.isMarkedForDeletion(session, sLinkID)) {
										linkService.restoreLink(session, sLinkID);
									}

									uiLinkInView = addLinkToView(pasteLink);
									if (uiLinkInView != null) {
										edit.AddLinkToEdit(uiLinkInView);
										uiLinkInView.setSelected(true);
										oViewPane.setSelectedLink(uiLinkInView,ICoreConstants.MULTISELECT);
									}
									else {
										System.out.println("Link unable to be pasted: "+pasteLink.getId());
									}
								}
							}
							else {
								System.out.println("Link not pasted as to or from node was null: "+pasteLink.getId());
							}
						}
					}
				}

				// notify the listeners of the Paste (for undo/redo)
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			  	ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.pasteFromClipboard-2) \n" + ex.getMessage());
			}
		}

		// update node inidicators
		ProjectCompendium.APP.refreshIconIndicators();

		bCopyToClipboard = false;
		bCutToClipboard = false;
		bViewportSet = false;

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * This routine gets the clipboard contents and paste them into the view as an external paste.
	 */
	public void externalPasteFromClipboard() {

		ProjectCompendium.APP.setWaitCursor();

		ClipboardTransferables clipui = null;
		PasteEdit.nodeList = new Hashtable();
		boolean bViewportSet = false;

		IModel model = oViewPane.getView().getModel();
		PCSession session = model.getSession();
		oViewPane = getViewPane();
		String sAuthor = oViewPane.getCurrentAuthor();

		if((clipui = (ClipboardTransferables)(ProjectCompendium.APP.getClipboard().getContents(this))) != null) {

			try {
				// record the effect of the paste
				// need to pass to this method the info you need to delete/recreate the nodes&links
				PasteEdit edit = new PasteEdit(oViewPane.getViewFrame());

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

				Point pdisp = getDisplacement(clipui);

				// NEED TO SCALE THIS POINT
				Point loc = UIUtilities.transformPoint(pdisp.x, pdisp.y, oViewPane.getScale());

				int xdisp = loc.x;
				int ydisp = loc.y;
				int i = 0;

				// DRAW NODES
				for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {
					i++;

					Object o = e.nextElement();

					if(o instanceof NodePosition) {

						NodeSummary pasteNodeSummary = null;
						int xpos = 0;
						int ypos = 0;
						NodeUI nodeui = null;
						NodePosition np = (NodePosition)o;
						pasteNodeSummary = np.getNode();
						UINode uiNode = new UINode(np, sAuthor);
						nodeui = uiNode.getUI();
						xpos = np.getXPos();
						ypos = np.getYPos();

						//check if node is already present(if paste is from another DB)
						INodeService nodeService = model.getNodeService();

						// IF NODE WAS DELETED, RESTORE NODE
						String sNodeID = pasteNodeSummary.getId();

						if (nodeService.isMarkedForDeletion(session, sNodeID)) {
							nodeService.restoreNode(session, sNodeID);
						}

						// PASTE CREATES A NEW OBJECT, SO PURGE ANY OLD VIEWNODE RECORDS FOR THIS VIEW/NODE COMBO
						String sViewID = getViewPane().getView().getId();
						nodeService.purgeViewNode(session, sViewID, sNodeID);

						// PURGE ANY PREVIOUS LINKS THE NODE HAD IN THIS VIEW
						model.getLinkService().purgeViewNode(session, sViewID, sNodeID);

						NodeSummary newPasteNodeSummary = nodeService.getNodeSummary(session, sNodeID);
						UINode uiNodeInView = (UINode)getViewPane().get(pasteNodeSummary.getId());

						UINode newUINode = null;

						if (newPasteNodeSummary == null) {
							//check if this node has been created in this paste operation
							String existingNode = (String)PasteEdit.nodeList.get(sNodeID);
							if (existingNode != null) {
								newPasteNodeSummary = nodeService.getNodeSummary(session, existingNode);
								if (newPasteNodeSummary != null) {
									newUINode = addNodeToView(pasteNodeSummary,xpos+xdisp,ypos+ydisp, np.getShowTags(),
																	np.getShowText(), np.getShowTrans(), np.getShowWeight(), np.getShowSmallIcon(),
																	np.getHideIcon(), np.getLabelWrapWidth(), np.getFontSize(), np.getFontFace(),
																	np.getFontStyle(), np.getForeground(), np.getBackground());
								}
							}
							if (existingNode == null || (existingNode != null && newPasteNodeSummary == null)) {
								newUINode = createNode(sNodeID,
										pasteNodeSummary.getType(),
										pasteNodeSummary.getOriginalID(),
										pasteNodeSummary.getAuthor(),
										pasteNodeSummary.getLabel(),
										pasteNodeSummary.getDetail(),
										xpos+xdisp, ypos+ydisp
										);

								nodeui = newUINode.getUI();

								newPasteNodeSummary = newUINode.getNode();
								int nodeType = pasteNodeSummary.getType();
								if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
									String source = pasteNodeSummary.getSource();
									String image = pasteNodeSummary.getImage();
									newPasteNodeSummary.setSource(source, image, sAuthor);
									if (image == null || image.equals(""))
										newUINode.setReferenceIcon(pasteNodeSummary.getSource());
									else
										newUINode.setReferenceIcon(image);
								}
								else if (nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.MAP_SHORTCUT ||
										nodeType == ICoreConstants.LISTVIEW || nodeType == ICoreConstants.LIST_SHORTCUT) {

									String image = pasteNodeSummary.getImage();
									newPasteNodeSummary.setSource("", image, sAuthor);
									if (image != null && !image.equals(""))
										newUINode.setReferenceIcon(image);
								}

								PasteEdit.nodeList.put(sNodeID, newUINode.getNode().getId());

								edit.AddNodeToEdit(newUINode);
								newUINode.setSelected(true);
								oViewPane.setSelectedNode(newUINode,ICoreConstants.MULTISELECT);
								newUINode.setRollover(false);
							}
						}
						else if (uiNodeInView == null) {
							newUINode = addNodeToView(pasteNodeSummary,xpos+xdisp,ypos+ydisp, np.getShowTags(),
															np.getShowText(), np.getShowTrans(), np.getShowWeight(), np.getShowSmallIcon(),
															np.getHideIcon(), np.getLabelWrapWidth(), np.getFontSize(), np.getFontFace(),
															np.getFontStyle(), np.getForeground(), np.getBackground());
							edit.AddNodeToEdit (newUINode);
							newUINode.setSelected(true);
							oViewPane.setSelectedNode(newUINode,ICoreConstants.MULTISELECT);
							newUINode.setRollover(false);
							nodeui = newUINode.getUI();
						}

						if (pasteNodeSummary instanceof View) {
							ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, newUINode.getNode().getId());

							View deletedView = (View)pasteNodeSummary;
							View newView = (View)newUINode.getNode();
							UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(newView, newView.getLabel());
							if (deletedView.getType() == ICoreConstants.LISTVIEW) {
								((UIListViewFrame)deletedUIViewFrame).getUIList().getListUI().externalRestoreDeletedNodes(deletedView);
							} else {
								((UIMapViewFrame)deletedUIViewFrame).getViewPane().getViewPaneUI().externalRestoreDeletedNodesAndLinks(deletedView);
							}
						}
						nodeui.refreshBounds();
					}
				}

				// DRAW LINKS
				// go through contents again to handle links bc links cannot
				// be drawn until nodes are in place.
				for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {
					Object o = e.nextElement();
					if(o instanceof LinkUI) {
						LinkUI linkui = (LinkUI)o;
						Link pasteLink = linkui.getUILink().getLink();
						String sLinkID = pasteLink.getId();

						ILinkService linkService = model.getLinkService();
						// IF LINK WAS DELETED, RESTORE LINK
						if (linkService.isMarkedForDeletion(session, sLinkID)) {
							linkService.restoreLink(session, sLinkID);
						}

						//add the link to the view if it isn't already in there
						UILink uiLinkInView = (UILink)getViewPane().get(sLinkID);

						if (uiLinkInView == null) {

							String newFromId = pasteLink.getFrom().getId();
							String newToId = pasteLink.getTo().getId();

							Link newLink = linkService.getLink(session, sLinkID);
							if (newLink != null) {
								newFromId = newLink.getFrom().getId();
								newToId = newLink.getTo().getId();
							}

							UINode newFromNode = (UINode)oViewPane.get(newFromId);
							UINode newToNode =  (UINode)oViewPane.get(newToId);

							if (newLink == null) {
								// IF THE LINKID HAS CHANGED IN THIS DATABASE
								// SHOULD NOT HAPPEN NOW THAT LINK IS A PRIMARY OBJECT
								if (newFromNode == null) {
									newFromId = (String)PasteEdit.nodeList.get(pasteLink.getFrom().getId());
									newFromNode = (UINode)oViewPane.get(newFromId);
								}
								if (newToNode == null) {
									newToId = (String)PasteEdit.nodeList.get(pasteLink.getTo().getId());
									newToNode = (UINode)oViewPane.get(newToId);
								}

								if((newFromNode != null) && (newToNode != null)) {
									// MAKE SURE THESE TWO NODES ARE NOT ALREADY LINKED
									if (!newFromNode.containsLink(newToNode)) {
										ILink link = createLink(pasteLink, newFromNode.getNode(), newToNode.getNode());
										uiLinkInView = addLinkToView(link);
										edit.AddLinkToEdit (uiLinkInView);
									}
								}
								else {
									System.out.println("Unable to create new link "+pasteLink.getId());
								}
							}
							else {
								if((newFromNode != null) && (newToNode != null)) {
									// MAKE SURE THESE TWO NODES ARE NOT ALREADY LINKED
									if (!newFromNode.containsLink(newToNode)) {
										uiLinkInView = addLinkToView(newLink);
										edit.AddLinkToEdit(uiLinkInView);
									}
								}
							}
						}
					}
				}

				// notify the listeners of the Paste (for undo/redo)
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			  	ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.pasteFromClipboard-2) \n" + ex.getMessage());
			}
		}

		bCopyToClipboard = false;
		bCutToClipboard = false;
		bViewportSet = false;

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Used by the paste methods to calculates node displacement when cut/copied from one view to another.
	 * @param clipui, the clipboard data.
	 */
	private Point getDisplacement(ClipboardTransferables clipui) {

		int minX = -1;
	 	int minY = -1;
	  	int maxX = -1;
	  	int maxY = -1;
	  	int diffX, diffY;

		//	Rectangle rect = getViewFrame().getViewport().getViewRect();
		//	int screenWidth = rect.width;
		//	int screenHeight = rect.height;
		//	screenWidth = rect.x + screenWidth;
		//	screenHeight = rect.y + screenHeight;

	  	// get the boundaries of the viewpane
	  	Dimension s = oViewPane.getSize();
	  	int pWidth = s.width;
	  	int pHeight = s.height;
	  	UINode oUINode = null;
	  	double scale = 1.0;

		// calculate the size of the area bounding the objects being pasted.
	  	for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {

			Object componentui = e.nextElement();

			if (componentui instanceof NodeUI) {
				NodeUI nodeui = (NodeUI)componentui;
				oUINode = nodeui.getUINode();
				scale = oUINode.getScale();

				oViewPane.scaleNode(oUINode, 1.0);
				Rectangle oStartingBounds = oUINode.getBounds();
				oViewPane.scaleNode(oUINode, scale);

				Point loc = oUINode.getNodePosition().getPos();
				int xpos = loc.x;
				int ypos = loc.y;

				if ((minX<0) || (xpos<minX))
					minX = xpos;
				if ((minY<0) || (ypos<minY))
					minY = ypos;
				if ((maxX<0) || (xpos + oStartingBounds.width>maxX))
					maxX = xpos + oStartingBounds.width;
				if ((maxY<0) || (ypos + oStartingBounds.height>maxY))
					maxY = ypos + oStartingBounds.height;
			}
		}

		//use minX and minY as the points to position the clipboard selection in.
		//BUG FIX - Lakshmi (10/13/06)
	  	//if mouse coordinates lies in UIViewPane then paste in that location and reset the bClicked flag

	  	UIViewFrame frame = ProjectCompendium.APP.getInternalFrame(oViewPane.getView());
	  	//Default Visible Area
	  	Rectangle rect = new Rectangle(0,0,472,449);
	  	if(frame != null){
	  		rect = frame.getViewport().getVisibleRect();
	  	}
	  	Point viewportP = UIUtilities.scalePoint(rect.width, rect.height, oViewPane.currentScale);

		Point scaledXY = UIUtilities.scalePoint(_x, _y, oViewPane.currentScale);		// Scaled mouse position
		int scaledMouseX = scaledXY.x;
		int scaledMouseY = scaledXY.y;
		
		Point p = oViewPane.getViewFrame().getViewPosition();
		p = UIUtilities.scalePoint(p.x, p.y, oViewPane.currentScale);					// Scaled view origin
		
	  	if((scaledMouseX > 0 && scaledMouseX < (p.x +viewportP.x)) && (scaledMouseY > 0 && scaledMouseY < (p.y +viewportP.y))) {
			diffX = scaledMouseX - minX;
			diffY = scaledMouseY - minY;
			bClicked = false;
		}

		// if mouse not clicked and so the mouse co-ordinates lies outside the pane then place items on clipboard
		// in the middle of the viewpane.
	  	else {
	  		int width = rect.width;
			int height = rect.height;
			p = oViewPane.getViewFrame().getViewPosition();
			diffX = (p.x+ width/2) - minX;
			diffY = (p.y + height/2) - minY;
	  	}

		//calculate new position of the clipboard items
	  	//(using the top leftmost position of the rectangle that encompasses
	  	// all the nodes on the clipboard).
	  	int newXPositionMin = minX + diffX;
	  	int newYPositionMin =  minY + diffY;
	  	int newXPositionMax = maxX + diffX;
	  	int newYPositionMax =  maxY + diffY;

	    // Make sure we stay in-bounds
		if (newXPositionMin < 0) {
			diffX = diffX - newXPositionMin;
		}
		if (newYPositionMin < 0) {
			diffY = diffY - newYPositionMin;
		}

		if (newXPositionMax > pWidth) {
			diffX = diffX - (newXPositionMax - pWidth);
		}
		if (newYPositionMax > pHeight) {
			diffY = diffY - (newYPositionMax - pWidth);
		}

		Point p1 = new Point(diffX, diffY);
		return (p1);
	}

	/**
	 * Used by view paste operations to restore child nodes and links.
	 * @param deletedView com.compendium.core.datamodel.View
	 */
	public void restoreDeletedNodesAndLinks(View deletedView) {

		try {
			IModel model = oViewPane.getView().getModel();
			PCSession session = model.getSession();
			INodeService nodeService = model.getNodeService();

			String userID = model.getUserProfile().getId();
			Vector deletedNodes = deletedView.getDeletedNodes();
			ViewPaneUI deletedViewPaneUI = getViewPane().getViewPaneUI();
			String sViewID = deletedView.getId();

			for (int i = 0; i < deletedNodes.size(); i++) {

				NodePosition np = (NodePosition)deletedNodes.elementAt(i);
				NodeSummary pasteNodeSummary = np.getNode();
				String sNodeID = pasteNodeSummary.getId();

				//Restore, incase deleted
				boolean restored = model.getNodeService().restoreNode(session, sNodeID);
				if (restored) {
					np = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
				}

				if (np != null) {
					deletedViewPaneUI.addNode(np);
					deletedView.addMemberNode(np);

					if (np.getNode() instanceof View) {

						// IF NODE ALREADY RESTORED, DON'T TRY AND RESTORE IT AGAIN
						if (!ProjectCompendium.APP.ht_pasteCheck.containsKey(sNodeID)) {
							ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, np.getNode());

							View view = (View)np.getNode();
							UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
							if (view.getType() == ICoreConstants.LISTVIEW)
								((UIListViewFrame)deletedUIViewFrame).getUIList().getListUI().restoreDeletedNodes((View)pasteNodeSummary);
							else {
								((UIMapViewFrame)deletedUIViewFrame).getViewPane().getViewPaneUI().restoreDeletedNodesAndLinks((View)pasteNodeSummary);
							}
						}
					}
				}
			}

			Vector deletedLinks = deletedView.getDeletedLinks();
			for (int i = 0; i < deletedLinks.size(); i++) {

				Link link = (Link)deletedLinks.elementAt(i);
				String sLinkID = link.getId();

				//Restore, incase deleted
				boolean restored = model.getLinkService().restoreLink(session, sLinkID);
				if (restored)
					restored = model.getViewService().restoreLink(session, sViewID, sLinkID);

				//add the link to the view if it isn't already in there
				UILink newuilink = (UILink)oViewPane.get(sLinkID);
				if (newuilink == null) {
					newuilink = addLink(link);
				}
				if (newuilink != null && newuilink.isSelected()) {
					oViewPane.setSelectedLink(newuilink, ICoreConstants.MULTISELECT);
				}
			}

			deletedNodes.removeAllElements();
			deletedLinks.removeAllElements();
		}
		catch(Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.restoreDeletedNodesAndLinks) \n" + e.getMessage());
		}
	}

	/**
	 * Used by view external paste operations to restore child nodes and links.
	 * @param deletedView com.compendium.core.datamodel.View
	 */
	public void externalRestoreDeletedNodesAndLinks(View deletedView) {

		try {
			IModel model = ProjectCompendium.APP.getModel();
			PCSession session = model.getSession();
			String sAuthor = getViewPane().getCurrentAuthor();

			INodeService nodeService = model.getNodeService();

			Vector deletedNodes = deletedView.getDeletedNodes();
			String sViewID = deletedView.getId();
			String sNodeID = "";

			View thisView = oViewPane.getView();
			if (thisView.getModel() == null) {
				thisView.initialize( session, model );
			}
			thisView.initializeMembers();

			for (int i = 0; i < deletedNodes.size(); i++) {

				NodePosition np = (NodePosition)deletedNodes.elementAt(i);
				NodeSummary pasteNodeSummary = np.getNode();
				sNodeID = pasteNodeSummary.getId();

				UINode newUINode = null;
				int xpos = np.getXPos();
				int ypos = np.getYPos();

				NodeSummary newPasteNodeSummary = null;

				String existingNode = null;

				// IF NODE ALREADY IN DATABASE
				if (nodeService.doesNodeExist(session, sNodeID)) {
					//Restore, incase deleted
					boolean restored = model.getNodeService().restoreNode(session, sNodeID);
					NodePosition oPos = null;
					if (restored) {
						oPos = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
					}

					if (oPos != null) {
						// ADD THE NODE IF NOT ALREADY THERE
						newUINode = (UINode)oViewPane.get(sNodeID);
						if (newUINode == null) {
							oPos = thisView.addMemberNode(oPos);
							newUINode = addNode(oPos);
						}
					}
					else {
						newUINode = addNodeToView(pasteNodeSummary,np.getXPos(), np.getYPos(), np.getShowTags(),
													np.getShowText(), np.getShowTrans(), np.getShowWeight(), np.getShowSmallIcon(),
													np.getHideIcon(), np.getLabelWrapWidth(), np.getFontSize(), np.getFontFace(),
													np.getFontStyle(), np.getForeground(), np.getBackground());
					}
				}
				else  { // CREATE A NEW ONE
					//check if this node has been created in this paste operation
					existingNode = (String)PasteEdit.nodeList.get(sNodeID);

					if (existingNode != null) {
						newPasteNodeSummary = nodeService.getNodeSummary(session, existingNode);

						if (newPasteNodeSummary != null) {
							String sNewView = (String)PasteEdit.nodeList.get(sViewID);
							if (sNewView == null || sNewView.equals(""))
								sNewView = sViewID;

							NodePosition oPos = model.getViewService().getNodePosition(session, sNewView, existingNode);
							if (oPos != null) {
								// ADD THE NODE IF NOT ALREADY THERE
								newUINode = (UINode)oViewPane.get(existingNode);
								if (newUINode == null) {
									oPos = thisView.addMemberNode(oPos);
									newUINode = addNode(oPos);
								}
							}
							else {
								newUINode = addNodeToView(pasteNodeSummary, np.getXPos(), np.getYPos(), np.getShowTags(),
										np.getShowText(), np.getShowTrans(), np.getShowWeight(), np.getShowSmallIcon(),
										np.getHideIcon(), np.getLabelWrapWidth(), np.getFontSize(),
										np.getFontFace(), np.getFontStyle(), np.getForeground(), np.getBackground()
										);
							}
						}
					} else if (existingNode == null || (existingNode != null && newPasteNodeSummary == null) ) {
						int nodeType = pasteNodeSummary.getType();
						newUINode = createNode(sNodeID,
								nodeType,
								pasteNodeSummary.getOriginalID(),
								pasteNodeSummary.getAuthor(),
								pasteNodeSummary.getLabel(),
								pasteNodeSummary.getDetail(),
								xpos, ypos);

						if (newUINode != null) {
							newPasteNodeSummary = newUINode.getNode();
							if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
								String source = pasteNodeSummary.getSource();
								String image = pasteNodeSummary.getImage();
								newPasteNodeSummary.setSource(source, image, sAuthor);
								if (image == null || image.equals(""))
									newUINode.setReferenceIcon(pasteNodeSummary.getSource());
								else
									newUINode.setReferenceIcon(image);
							} else if(nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.MAP_SHORTCUT ||
									nodeType == ICoreConstants.LISTVIEW || nodeType == ICoreConstants.LIST_SHORTCUT) {

								String image = pasteNodeSummary.getImage();
								newPasteNodeSummary.setSource("", image, sAuthor);
								if (image != null && !image.equals(""))
									newUINode.setReferenceIcon(image);
							}

							PasteEdit.nodeList.put(sNodeID, newPasteNodeSummary.getId());
							newUINode.setRollover(false);
						}
					}
				}

				// IF NODE ALREADY RESTORED, DON'T TRY AND RESTORE IT AGAIN
				if (!ProjectCompendium.APP.ht_pasteCheck.containsKey(sNodeID) && newUINode != null) {
					ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, newUINode.getNode().getId());
					if (newUINode.getNode() instanceof View
							&& pasteNodeSummary instanceof View) {

						View deletedView2 = (View)pasteNodeSummary;
						View view = (View)newUINode.getNode();
						UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
						if (view.getType() == ICoreConstants.LISTVIEW)
							((UIListViewFrame)deletedUIViewFrame).getUIList().getListUI().externalRestoreDeletedNodes(deletedView2);
						else {
							((UIMapViewFrame)deletedUIViewFrame).getViewPane().getViewPaneUI().externalRestoreDeletedNodesAndLinks(deletedView2);
						}
					}
				}
			}

			Vector deletedLinks = deletedView.getDeletedLinks();
			int count = deletedLinks.size();
			for (int i = 0; i < count; i++) {
				Link link = (Link)deletedLinks.elementAt(i);
				String type = link.getType();
				String sLinkID = link.getId();

				boolean restored = model.getLinkService().restoreLink(session, sLinkID);
				if (restored)
					restored = model.getViewService().restoreLink(session, sViewID, sLinkID);

				//add the link to the view if it isn't already in there
				if (!thisView.containsLink(sLinkID)) {
					Link newLink = model.getLinkService().getLink(session, sLinkID);
					if (newLink == null) {
						NodeSummary fromNode = thisView.getNode(link.getFrom().getId());
						NodeSummary toNode = thisView.getNode(link.getTo().getId());
						if((fromNode != null) && (toNode != null)) {
							newLink = (Link)createLink(link, fromNode, toNode);
						}
						else {
							System.out.println("Unable to create new link "+link.getId());
						}
					}
					if (newLink != null) {
						addLinkToView(newLink);
					}
				}
			}

			deletedNodes.removeAllElements();
			deletedLinks.removeAllElements();
		}
		catch(Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (ViewPaneUI.externalRestoreDeletedNodesAndLinks) \n" + e.getMessage());
		}
	}
}
