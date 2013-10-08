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
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.geom.*;

import java.io.*;
import java.beans.*;
import java.util.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.ViewService;

import com.compendium.*;

import com.compendium.meeting.*;

import com.compendium.ui.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.linkgroups.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.panels.*;

/**
 * The UI class for the UINode Component
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public	class NodeUI
				extends ComponentUI
				implements MouseListener, MouseMotionListener, KeyListener,	PropertyChangeListener {

	/** the colour to use for a selected node.*/
	private static final Color 	SELECTED_COLOR 		= Color.yellow;

	/** The colour to use for selected text in the node label.*/
	private static final Color 	SELECTED_TEXT_COLOR = Color.black;

	/** The colour to use for the label background when editing a node label.*/
	private static final Color 	EDITING_COLOR 		= Color.white;

	/** The colour to use for the label background when text is selected.*/
	private static final Color 	TSELECTED_COLOR 	 = Color.blue;

	/** the colour to use for the text when text is selected.*/
	private static final Color 	TSELECTED_TEXT_COLOR = Color.white;

	/** The colour to use for the border around the node if it has the focus.*/
	private static final Color 	FOCUSED_COLOR 		= Color.blue;

	/** The colour to use for the node border when the node is rolled over with the mouse.*/
	private static final Color 	BORDER_COLOR		= Color.cyan;

	/** The colour to use for a map node border when it has an image in it.*/
	private static final Color 	IMAGEMAP_COLOR		= Color.darkGray;

	/** Used for the date foramt when adding the current date to a node label.*/
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

 	protected	UINode								oNode;

	protected	UIViewPane							oViewPane;

  	/** The MouseListener registered for this list.*/
	private		MouseListener						oMouseListener;

  	/** The MouseMotionListener registered for this list.*/
	private		MouseMotionListener					oMouseMotionListener;

  	/** The KeyListener registered for this list.*/
	private		KeyListener							oKeyListener;

  	/** The ComponentListener registered for this list.*/
	private		ComponentListener					oComponentListener;

  	/** The PropertyChangeListener registered for this list.*/
	private		PropertyChangeListener				oPropertyChangeListener;

	/** The rectangle defining this node oStartingBounds - used in mouse events.*/
	private		Rectangle							oStartingBounds;

	/** lastMousePosX is the last mouseDragged location in absolute coordinate system.*/
	private		int									lastMousePosX = 0;

	/** lastMousePosY is the last mouseDragged location in absolute coordinate system.*/
	private 	int									lastMousePosY = 0;

	/** previousMousePosX is the previous to last mouseDragged location in absolute coordinate system.*/
	private		int									previousMousePosX = 0;

	/** prevoiusMousePosY is the previous to last mouseDragged location in absolute coordinate system.*/
	private 	int									previousMousePosY = 0;

	/** _x & _y are the mousePressed location in absolute coordinate system.*/
	private		int									_x, _y;

	/** __x & __y are the mousePressed location in source view's coordinate system.*/
	private		int									__x, __y;

	/** Start point in view pane's coordinate system.*/
	private		Point								ptStart;

	/** Used to hold the calculated maximum width required to paint this node.*/
	private int nodeWidth 	= -1;

	/** Defines whether dragging is started with right mouse button*/
	private		boolean								bDragging = false;

	/** if on the Mac, and a mock-right mouse was initialized (control-left mouse).*/
	private		boolean								bIsMacRightMouse = false;

	/** Key code that is being generated for. */
	protected Action								oRepeatKeyAction;

	// Location of the mouse at the time a key was pressed
	private		Point								ptLocationKeyPress;

	/** Set to true while keyPressed is active. */
	protected boolean								bIsKeyDown;

	/** Current node edit dialog if opened from label editing - used to redirect key event to.*/
	private 	UINodeContentDialog 				editDialog = null;

	/** Used when creating a temporary link while dragging to create a new link.*/
	private		UILink								oUIConnectingLine = null;

	/** Used when creating one or more temporary links while dragging from multiple nodes to create new links.*/
	private		Vector								oUIConnectingLines = new Vector(51);

	/** Are we in the process of opening a NodeContentDialog?*/
	private 	boolean								openingDialog = false;

	/**
	 * Holds the letter types between reaching the label length limit
	 * and opening the node contents dialog, so no letters are missed.
	 * This buffer will be copied inot the detail box so typing can continue without interuption.
	 */
	private 	String								dialogBuffer = "";

	/** Holds the area for the node transclusion number.*/
	private		Rectangle							transRectangle = null;

	/** Holds the area for the node detail text indicator.*/
	private 	Rectangle							textRectangle = null;

	/** Holds the area for the node code indicator.*/
	private		Rectangle							codeRectangle = null;

	/** Holds the area for the node map weight indicator.*/
	private		Rectangle							weightRectangle = null;

	/** Holds the area for the node icon.*/
	private		Rectangle							iconRectangle = null;

	/** Holds the area for the node meeting replay indicator.*/
	private 	Rectangle							movieRectangle = null;

	/** The clipboard instance used when performing cut/copy/paste actions in the node label.*/
	private 	Clipboard 							clipboard = null;

	/** The are for the caret in the node label.*/
	private 	Rectangle							caretRectangle = null;

	/** The area for the node label.*/
	private		Rectangle							labelRectangle = null;

	/** The current position of the caret in the node label.*/
	private 	int									currentCaretPosition = 0;

	/** The previous contents of the label for undoind a cut/copy/paste to one level.*/
	private		String								previousString = "";

	/** Indocates if the node label is currently being edited - Is the node in edit mode?*/
	private 	boolean								editing = false;

	/** Indicates if a double-click was performed in the node label (for selecting a word).*/
	private 	boolean								doubleClicked = false;

	/** The x position the mouse was clicked to start label editing or move row.*/
	private		int									editX = 0;

	/** The y position the mouse was clicked to start label editing or move row.*/
	private 	int									editY = 0;

	/** The starting position for text selection in the node label.*/
	private		int									startSelection = -1;

	/** The stopping position for text selection in the node label.*/
	private 	int									stopSelection = -1;

	/** The current row the cursor is in, in the node label.*/
	private		int									currentRow = 0;

	/** Was the up key pressed to move the cursor up a row in the node label?*/
	private 	boolean								caretUp = false;

	/** Was the down key pressed to move the cursor down a row in the node label?*/
	private 	boolean								caretDown = false;

	/** Not currently used.*/
	private 	Thread 								cursorThread = null;

	/** Holds the last colour used for node background.*/
	private		Color								lastColor = Color.black;

	/** Used for autoscrolling when dragged nodes hit the viewport edge.*/
	private 	java.util.Timer 					timer = null;
	/**
	 * Holds the list of the <code>TextRowElement</code> objects
	 * with information on each row of text in the node label.
	 */
	private 	Vector								textRowElements = null;

	/** The area for creating plus links from an argument node.*/
	private 	Rectangle							plusRectangle = null;

	/** The area to drag from to create con links from an Argument node.*/
	private 	Rectangle							minusRectangle = null;

	/** Indicates is a plus link should be created.*/
	public	 	boolean								isPlus = false;

	/** Indicates if a con link should be created.*/
	public 		boolean								isMinus = false;

	/** Indicates if the paint methods should paint a transclucion indicator.*/
	private 	boolean								hasTrans = false;

	/** Indicates if the paint methods should paint a detail text indicator.*/
	private		boolean								hasText = false;

	/** Indicates if the paint methods should paint a Code (tag) indicator.*/
	private		boolean								hasCodes = false;

	/** Indicates if the paint methods should paint a map weight indicator.*/
	private		boolean								hasWeight = false;

	/** Indicates if the paint methods should paint a node icon.*/
	private		boolean								hasIcon = false;

	/** Indicates if the paint methods should paint a eeting replay indicator.*/
	private		boolean								hasMovie = false;

	/** The count of the number of child node if this is a map or list node.*/
	private		int 								weightCount = 0;

	/** The number of parent views this node is in.*/
	private 	int 								viewCount = 0;

	/** Used to hold extra width added to the node width depending which if any node indicator should be painted.*/
	private		int									extraIconWidth = 0;

	/** Used to store the name of the current user if the user request it to be added to the node label.*/
	private String sUser = "";

	/** Holds the last key pressed on this node.*/
	private String sKeyPressed = "";

	/** The shortcut key for the current platfrom.*/
	private int shortcutKey;

	private boolean bMiddleMouse = false;

	/**
	 * Create a new NodeUI instance.
	 * @param c, the component this is the ui for - NOT REALLY USED AT PRESENT HERE.
	 */
  	public static ComponentUI createUI(JComponent c) {

		NodeUI nodeui = new NodeUI();
	  	return nodeui;
  	}

	/**
	 * Constructor. Just calls super.
	 */
  	public NodeUI() {
		super();
 	}

	/**
	 * Return the UINode object associated with this ui.
	 * @return com.compendium.ui.UINode, the node associated with this ui.
	 */
	public void setNode(UINode node) {
		oNode = node;
	}

	/***** USER INTERFACE INITIALIZATION METHODS *****/

	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
	public void installUI(JComponent c) {
		super.installUI(c);
		oNode = (UINode)c;
		previousString = oNode.getText();
		currentCaretPosition = previousString.length();
		oViewPane = oNode.getViewPane();

		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		shortcutKey = ProjectCompendium.APP.shortcutKey;

		installListeners(c);
		installBorder(c);
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
		if ( (oPropertyChangeListener = createPropertyChangeListener( c )) != null ) {
			c.addPropertyChangeListener( oPropertyChangeListener );
		}
		if ( (oComponentListener = createComponentListener( c )) != null ) {
			c.addComponentListener( oComponentListener );
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
	 * Just returns this class as the PropertyChangeListener.
	 * @param c, the component to create the PropertyChangeLisener for.
	 * @return PropertyChangeListener, the listener to use.
	 */
  	protected PropertyChangeListener createPropertyChangeListener(JComponent c) {
		return this;
  	}

	/**
	 * Create and return a ComponentListener for use by this ui.
	 * @param c, the component to create the ComponentLisener for.
	 * @return ComponentListener, the listener to use.
	 */
 	protected ComponentListener createComponentListener(JComponent c) {
		ComponentListener comp = new ComponentAdapter() {
			@Override
            public void componentMoved(ComponentEvent evt) {
				if (APP_PROPERTIES.isAutoSearchLabel()) {
					if (oViewPane == null)
						oViewPane = oNode.getViewPane();

					if (oViewPane != null) {
						UIHintNodeLabelPanel panel = oViewPane.getLabelPanel(oNode.getNode().getId());
						if (panel != null) {
							Point p = oNode.getLocation();
							Dimension d = oNode.getSize();
							int nX = p.x;
							int nY = p.y+d.height;

							panel.setLocation(nX, nY);
						}
					}
				}
			}
		};
		return comp;
  	}

	/**
	 * Install the border used by this node.
	 * @param c, the component to install the border for.
	 * @see com.compendium.ui.plaf.NodeUI.NodeBorder
	 */
	public void installBorder(JComponent c) {
		c.setBorder(new NodeBorder());
	}

	/**
	 * Run any uninstall instructions for uninstalling this UI.
	 * @param c, the component this is the ui to uninstall for.
	 */
	public void uninstallUI(JComponent c) {
		uninstallBorder(c);
		uninstallListeners(c);
		oNode = null;
		super.uninstallUI(c);
	}

	/**
	 * Uninstall the border on this node by setting it to null.
	 * @param c, the component this is the ui to uninstall for.
	 */
	public void uninstallBorder(JComponent c) {
		c.setBorder(null);
	}

	/**
	 * Uninstall any listeners.
	 * @param c, the component to uninstall the listeners for.
	 */
	protected void uninstallListeners(JComponent c) {
		if ( oPropertyChangeListener != null ) {
	    	c.removePropertyChangeListener( oPropertyChangeListener );
		}
		if ( oMouseMotionListener!= null ) {
	    	c.removeMouseMotionListener( oMouseMotionListener );
		}
		if ( oMouseListener!= null ) {
	    	c.removeMouseListener( oMouseListener );
		}

		if ( oComponentListener!= null ) {
	    	c.removeComponentListener( oComponentListener );
		}

		oMouseListener = null;
		oMouseMotionListener = null;
		oPropertyChangeListener = null;
		oComponentListener = null;
	}

	/***** PAINT METHODS *****/

  	/**
   	 * Paint clippedText at textX, textY with the nodes foreground color.
	 *
	 * @param g, the Graphics object to use to do the paint.
	 * @param s, the String of text to paint.
     * @param textX, the x position to start the paint.
	 * @param textY, the y position to start the paint
     * @see #paint
     * @see #paintDisabledText
     */
  	protected void paintEnabledText(UINode n, Graphics g, String s, int textX, int textY) {
		//g.setColor(n.getForeground());
		BasicGraphicsUtils.drawString(g, s, '\0', textX, textY);
  	}

  	/**
     * Paint clippedText at textX, textY with background.lighter() and then
     * shifted down and to the right by one pixel with background.darker().
	 *
 	 * @param g, the Graphics object to use to do the paint.
	 * @param s, the String of text to paint.
     * @param textX, the x position to start the paint.
	 * @param textY, the y position to start the paint
     * @see #paint
     * @see #paintEnabledText
     */
  	protected void paintDisabledText(UINode n, Graphics g, String s, int textX, int textY) {
		Color background = n.getBackground();
		g.setColor(background.brighter());
		BasicGraphicsUtils.drawString(g, s, '\0', textX, textY);
		g.setColor(background.darker());
		BasicGraphicsUtils.drawString(g, s, '\0', textX + 1, textY + 1);
  	}

	/**
	 * Is this node currently being edited?
	 * @return boolean, true if it is, else false.
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * Set the node in editing mode.
	 * Make the caret position the edn of the current label text.
	 */
	public void setEditing() {
		setEditing(oNode.getText().length());
	}

	/**
	 * Set the node in editing mode.
	 * @param int caretPosition, the position to set the caret to.
	 */
	public void setEditing(int caretPosition) {

		editing = true;
		startSelection = -1;
		stopSelection = -1;

		currentCaretPosition = caretPosition;
		doubleClicked = false;

		oNode.moveToFront();
		oNode.setRollover(false);
		oNode.requestFocus();
	}

	/**
	 * Reset all the label editing variables.
	 */
	public void resetEditing() {
		editing = false;
		currentCaretPosition = -1;
		editX=0;
		editY=0;
		bDragging = false;
		doubleClicked = false;
		startSelection = -1;
		stopSelection = -1;
	}

	/**
	 * This class holds information about each row of the node label.
	 */
	private class TextRowElement {

		String text = "";
		int startPos = 0;
		Rectangle textR = null;
		boolean isRowWithCaret;

		public TextRowElement(String text, int startPos, Rectangle textR, boolean isRowWithCaret) {
			this.text = text;
			this.startPos = startPos;
			this.textR = textR;
			this.isRowWithCaret = isRowWithCaret;
		}

		public String getText() {
			return text;
		}

		public int getStartPosition() {
			return startPos;
		}

		public Rectangle getTextRect() {
			return textR;
		}

		public boolean getIsRowWithCaret() {
			return isRowWithCaret;
		}
	}

	/**
	 * Paint the node.
	 *
	 * @param g, the Graphics object to use to do the paint.
	 * @param c, the component being painted.
	 * @see #paintEnabledText
 	 * @see #paintDisabledText
	 * @see #drawText
	 */
  	public void paint(Graphics g, JComponent c) {

		// CLEAR VARIABLES
		transRectangle = null;
		textRectangle = null;
		codeRectangle = null;
		weightRectangle = null;
		labelRectangle = null;
		movieRectangle = null;
		textRowElements = new Vector();

		Color oldColor = null;

		UINode node = (UINode)c;

		String text = node.getText();
		Font nodeFont = g.getFont();

		FontMetrics fm = g.getFontMetrics();
		Rectangle iconR = new Rectangle();
		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(c.getSize());
		Insets viewInsets = c.getInsets();

		int maxWidth = nodeWidth;

		viewR.x = viewInsets.left;
		viewR.y = viewInsets.top;
		viewR.width -= (viewInsets.left + viewInsets.right);
		viewR.height -= (viewInsets.top + viewInsets.bottom);

		// DRAW ICON IF NOT HIDDEN
		int imageHeight = 0;
		int imageWidth = 0;

		ImageIcon icon = node.getIcon();
		NodePosition position = node.getNodePosition();
		boolean bSmallIcon = position.getShowSmallIcon();

		if (position.getHideIcon() || icon == null) {
			if (text == null)
				return;

			iconR.width = 0;
			iconR.height = 0;
			iconR.x = maxWidth/2;
			iconR.y = viewR.y+1;

			textR.y = iconR.y + fm.getAscent();
		}
		else {
			if ((icon == null) && (text == null))
				return;

			hasIcon = true;

			imageHeight = icon.getIconHeight();
			imageWidth = icon.getIconWidth();

			iconR.width = imageWidth+1;
			iconR.height = imageHeight+1;

			iconR.x = (maxWidth - imageWidth)/2;
			iconR.y = viewR.y+1;

			int type = node.getNode().getType();
			if (type == ICoreConstants.ARGUMENT || type == ICoreConstants.ARGUMENT_SHORTCUT) {
				plusRectangle = new Rectangle(iconR.x, iconR.y, imageWidth/2, imageHeight);
				//g.fillRect(iconR.x, iconR.y+2, imageWidth/2, imageHeight/2);
				minusRectangle = new Rectangle(iconR.x+imageWidth/2, iconR.y+imageHeight/2, imageWidth/2, imageHeight/2);
				//g.fillRect(iconR.x+imageWidth/2, iconR.y+imageHeight/2, imageWidth/2, imageHeight/2);
			}

			// icon background will always be opaque
			oldColor = g.getColor();

			if (node.isSelected()) {
				g.setColor(SELECTED_COLOR);
				g.drawRect(iconR.x-1, iconR.y-1, iconR.width, iconR.height);
			}

			g.setColor(oldColor);
			icon.paintIcon(c, g, iconR.x, iconR.y);

			//AffineTransform trans = nodeFont.getTransform();
			//Font newFont = (new Font("Dialog", Font.BOLD, 10)).deriveFont(trans);

			// work around for Mac BUG with derive Font
			AffineTransform trans=new AffineTransform();
			trans.setToScale(node.getScale(), node.getScale());

			// FONT FOR THE ICON INDICATORS
			Point p1 = new Point(10, 10);
			try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
			catch(Exception e) {System.out.println("can't convert font size (NodeUI.paint 1) \n\n"+e.getMessage()); }
			Font newFont = new Font("Dialog" , Font.BOLD, p1.x);

			g.setFont(newFont);
			FontMetrics sfm = g.getFontMetrics();

			NodeSummary nodeSumm = node.getNode();

			// IF THIS NODE IS IN A MAP ASSOCIATED WITH A VIDEAO
			// DRAW AN V
			if (hasMovie) {
				g.setColor(Color.red);
				int twidth = sfm.stringWidth("M")+3;

				int pos = 20;
				int height = 12;
				int extra = 2;
				if (bSmallIcon) {
					extra = 3;
					pos = 12;
					height = 8;
				}

				movieRectangle = new Rectangle(iconR.x+iconR.width, (iconR.y+pos-(height/2)-extra), twidth, height);
				//g.fillRect(iconR.x+iconR.width, (iconR.y+pos-(height/2)-extra), twidth, height);
				g.drawString("M", iconR.x+iconR.width+1, iconR.y+pos);
				g.setFont(new Font("Dialog", Font.BOLD, 10));
			}

			// DRAW * IF HAS DETAILS
			String detail = nodeSumm.getDetail();
			detail = detail.trim();
			if(hasText) {
				g.setColor(new Color(0, 91, 183));

				//Font tFont = (new Font("Dialog", Font.BOLD, 18)).deriveFont(trans);
				// work around for Mac BUG with deriveFont
				Point p2 = new Point(18, 18);
				try { p2 = (Point)trans.transform(p2, new Point(0, 0));}
				catch(Exception e) {System.out.println("can't convert font size (NodeUI.paint 2) \n\n"+e.getMessage());}

				Font tFont = new Font("Dialog", Font.BOLD, p2.x);

				g.setFont(tFont);
				FontMetrics rfm = g.getFontMetrics();

				int twidth = rfm.stringWidth("*")+3;

				int pos = 13;
				int height = 16;
				if (bSmallIcon) {
					pos = 11;
					height = 13;
				}

				textRectangle = new Rectangle(iconR.x+iconR.width, (iconR.y-5), twidth, height);
				//g.fillRect(iconR.x+iconR.width, (iconR.y-5), twidth, height);
				g.drawString("*", iconR.x+iconR.width+1, iconR.y+pos);

				g.setFont(newFont);
			}

			// DRAW TRANCLUSION NUMBER
			if(hasTrans) {
				int ncount = nodeSumm.getViewCount();
				if (ncount > 1) {

					g.setColor(new Color(0, 0, 106));

					String count = String.valueOf(ncount);

					int nwidth = sfm.stringWidth(count)+2;

					int extra = 2;
					int back = 8;
					int theight = 14;
					if (bSmallIcon) {
						theight = 11;
						extra =4;
						back = 5;
					}

					transRectangle = new Rectangle(iconR.x+iconR.width, iconR.y+(iconR.height-back), nwidth, theight);
					//g.fillRect(iconR.x+iconR.width, iconR.y+(iconR.height-back), nwidth, theight);
					g.drawString(count, iconR.x+iconR.width+1, iconR.y+(iconR.height)+extra);
				}
			}

			textR.y = iconR.y + iconR.height + node.getIconTextGap() + fm.getAscent();
			sfm = g.getFontMetrics();

			// DRAW VIEW WEIGHT COUNT IF REQUESTED
			if (hasWeight && node.getNode() instanceof View) {

				g.setColor(new Color(0, 91, 183));

				View view  = (View)node.getNode();
				String sCount = "";
				try { sCount = String.valueOf(view.getNodeCount()); }
				catch(Exception ex) { System.out.println("Error: (NodeUI.paint)\n\n"+ex.getMessage());}

				int w = sfm.stringWidth(sCount);
				int h = sfm.getAscent();

				int extra = 2;
				int back = 8;
				if (oNode.getNodePosition().getShowSmallIcon())  {
					extra = 4;
					back = 6;
				}

				weightRectangle = new Rectangle(iconR.x-(w+2), iconR.y+(iconR.height-back), w, h);
				//g.fillRect(iconR.x-(w+2), iconR.y+(iconR.height-back), w, h);
				g.drawString(sCount, iconR.x-(w+1), iconR.y+(iconR.height)+extra);
			}

			//if (node.getNode().getId().equals(ProjectCompendium.APP.getTrashBinID())) {
			//
			//}

			// DRAW 'T', if has Tags
			if (hasCodes) {
				g.setColor(new Color(0, 0, 106));

				int twidth = sfm.stringWidth("T")+2;
				int pos = sfm.getAscent()-3;

				int theight = 14;
				if (bSmallIcon) {
					pos = 6;
					theight = 11;
				}

				codeRectangle = new Rectangle(iconR.x-(twidth+2), (iconR.y-3), twidth, theight);
				//g.fillRect(iconR.x-(twidth+2), (iconR.y-3), twidth, theight);
				g.drawString("T", iconR.x-(twidth+1), iconR.y+pos);
			}

			if (hasText || hasTrans || hasWeight || hasCodes)
				imageWidth += extraIconWidth+1;
		}

		// This made the image rollover extend too far to the right and obscured the icon indicator hot spots.
		//iconR.width = imageWidth;

		iconRectangle = iconR;

		// DRAW TEXT
		//int textWidth = fm.stringWidth( text );
		int textWidth = text.length();

		labelRectangle = viewR;
		labelRectangle.y = textR.y-fm.getAscent();
		labelRectangle.height = viewR.height-textR.y+fm.getAscent();

		textR.width = fm.stringWidth( text );
		textR.height = fm.getAscent()+fm.getDescent();
		textR.x = viewR.x;

		int startPos = 0;
		int stopPos = 0;

		// RE_SET THE FONT AND COLOR FOR TEXT
		g.setColor(Color.black);
		g.setFont(nodeFont);

		int wrapWidth = node.getNodePosition().getLabelWrapWidth();
		if (wrapWidth <= 0) {
			wrapWidth = ((Model)ProjectCompendium.APP.getModel()).labelWrapWidth;
		}
		wrapWidth = wrapWidth+1; // Needs this for some reason.

		if (textWidth > wrapWidth) {

			int row = -1;
			String textLeft = text;
			boolean isRowWithCaret = false;

			while ( textLeft.length() > 0 ) {
				row ++;
				isRowWithCaret = false;

				startPos = stopPos;

				//int thisTextWidth = fm.stringWidth( textLeft );
				int textLen = textLeft.length();
				int curLen = wrapWidth;
				if (textLen < wrapWidth ) {
					curLen = textLen;
				}

				//int nextLen = 0;
				/*if (textLen > wrapWidth) {
					while(curLen <= textLen) {

						String next = textLeft.substring(0, curLen);
						nextLen = next.length();
						//if (fm.stringWidth(next) < maxWidth) {
						if (nextLen < wrapWidth) {
							curLen++;
						}
						//else if (fm.stringWidth(next) >= maxWidth) {
						else if (nextLen >= wrapWidth) {
							curLen--;
							break;
						}
					}
				}
				else {
					curLen = textLen;
				}*/

				String nextText = textLeft.substring(0, curLen);
				if (curLen < textLen) {
					int lastSpace = nextText.lastIndexOf(" ");
					if (lastSpace != -1 && lastSpace != textLen) {
						curLen = lastSpace+1;
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
					else {
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
				}
				else {
					if (!textLeft.equals(""))
						nextText = textLeft;
					textLeft = "";
				}

				stopPos += nextText.length();

				int mousePosition = -1;

				// for dragging mouse to select
				if (node.hasFocus() && editing && (bDragging || doubleClicked)) {

					if (editY >= textR.y-fm.getAscent() && editY < (textR.y+fm.getDescent()) ) {

						int tX = textR.x;
						int tY = textR.y;

						int tWidth = fm.stringWidth( nextText );
						if (tWidth < iconR.width && iconR.width > maxWidth) {
							tX += (iconR.width-tWidth)/2;
						}
						else if (tWidth < maxWidth) {
							tX += (maxWidth-tWidth)/2;
						}

						int caretPos = 0;
						if (editX <= tX)
							caretPos = 0;
						else if (editX >= tX+tWidth && startPos+nextText.length() == text.length()) {
							caretPos = nextText.length();
						}
						else if (editX >= tX+tWidth) {
							caretPos = nextText.length()-1;
						}
						else {
							int ind = 1;
							int prev = 0;
							while(ind <= nextText.length()) {
								String n = nextText.substring(0, ind);
								int charX = fm.stringWidth(n);
								if (editX >= (tX+prev) && editX <= (tX+charX) ) {
									if ( (tX+prev) - editX < editX - (tX+charX))
										caretPos = ind-1;
									else
										caretPos = ind;
									break;
								}
								prev = charX;
								ind++;
							}
						}

						if (bDragging) {
							if (currentCaretPosition == -1) {
								currentCaretPosition = startPos+caretPos;
								startSelection = currentCaretPosition;
								stopSelection = currentCaretPosition;
							}
							else {
								// IF DRAGGING LEFT
								if (startPos+caretPos < currentCaretPosition) {

									if (stopSelection == -1)
										stopSelection = currentCaretPosition;

									currentCaretPosition = startPos+caretPos;

									if (startSelection == -1 || startSelection >= currentCaretPosition)
										startSelection = currentCaretPosition;
									else
										stopSelection = currentCaretPosition;
								}
								// IF DRAGGING RIGHT
								else {
									if (startSelection == -1)
										startSelection = currentCaretPosition;

									currentCaretPosition = startPos+caretPos;

									if (stopSelection == -1 || stopSelection < currentCaretPosition)
										stopSelection = currentCaretPosition;
								}
							}
						}

						// DOUBLE CLICK TO SELECT WORD
						if (doubleClicked) {
							int index = nextText.indexOf(" ", caretPos);
							if (index == -1)
								stopSelection = startPos + nextText.length();
							else {
								stopSelection = startPos + index;
							}

							currentCaretPosition = stopSelection;

							String bit = nextText.substring(0, caretPos);
							index = bit.lastIndexOf(" ");
							if (index == -1)
								startSelection = startPos;
							else {
								startSelection = startPos + index+1;
							}
							doubleClicked = false;
						}
					}
				}

				drawText(g, fm, node, nextText, textR, iconR, maxWidth, startPos);

				// If mouse just clicked
				if (node.hasFocus() && currentCaretPosition == -1 && editing) {
					// IS THE CLICK IN THIS ROW
					if (editY >= textR.y-fm.getAscent() && editY < (textR.y+fm.getDescent()) ) {

						int tX = textR.x;
						int tY = textR.y;

						int tWidth = fm.stringWidth( nextText );
						if (tWidth < iconR.width && iconR.width > maxWidth) {
							tX += (iconR.width-tWidth)/2;
						}
						else if (tWidth < maxWidth) {
							tX += (maxWidth-tWidth)/2;
						}

						int caretPos = 0;
						if (editX <= tX)
							caretPos = 0;
						else if (editX >= tX+tWidth && startPos+nextText.length() == text.length()) {
							caretPos = nextText.length();
						}
						else if (editX >= tX+tWidth) {
							caretPos = nextText.length()-1;
						}
						else {
							int ind = 1;
							int prev = 0;
							while(ind <= nextText.length()) {

								String n = nextText.substring(0, ind);
								int charX = fm.stringWidth(n);
								if (editX >= (tX+prev) && editX <= (tX+charX) ) {
									if ( (tX+prev) - editX < editX - (tX+charX))
										caretPos = ind-1;
									else
										caretPos = ind;
									break;
								}
								prev = charX;
								ind++;
							}
						}

						currentCaretPosition = startPos+caretPos;
						currentRow = row;
						isRowWithCaret = true;
						setCaretRectangle(g, fm, textR, nextText, caretPos, iconR, maxWidth);
					}
				}
				else if (node.hasFocus() && editing) {
					if (caretUp) {
						// IF WE ARE ALREADY ON THE FIRST ROW
						if (currentRow == 0) {
							currentCaretPosition = 0;
							caretUp = false;
						}
					}

					if (caretDown) {
						// IF WE ARE ALREADY ON THE LAST ROW
						if (stopPos == text.length() && currentRow == row) {
							currentCaretPosition = text.length();

							if (APP_PROPERTIES.isAutoSearchLabel()) {
								UIHintNodeLabelPanel panel = oViewPane.getLabelPanel(oNode.getNode().getId());
								if (panel != null)
									panel.focusList();
							}

							caretDown = false;
						}
					}

					if (currentCaretPosition >= startPos &&
							(currentCaretPosition < stopPos || currentCaretPosition == stopPos && stopPos == text.length())) {
						int caretPos = currentCaretPosition - startPos;
						setCaretRectangle(g, fm, textR, nextText, caretPos, iconR, maxWidth);
						currentRow = row;
					}
					isRowWithCaret = true;
				}

				TextRowElement element = new TextRowElement(nextText, startPos, new Rectangle(textR.x, textR.y, textR.width, textR.height), isRowWithCaret);
				textRowElements.addElement(element);

				textR.y += fm.getAscent() + fm.getDescent();
			}

			if (caretUp) {
				if (currentRow > 0) {
					caretUp = false;
					recalculateCaretRectangle(g, fm, iconR, maxWidth, true);
				}
			}
			else if (caretDown) {
				if (currentRow < textRowElements.size()-1) {
					recalculateCaretRectangle(g, fm, iconR, maxWidth, false);

					if (APP_PROPERTIES.isAutoSearchLabel()) {
						UIHintNodeLabelPanel panel = oViewPane.getLabelPanel(oNode.getNode().getId());
						if (panel != null)
							panel.focusList();
					}

					caretDown = false;
				}
			}
		}
		else {
			// if draggin mouse to select text or double clicked to select word calculate selection
			if (node.hasFocus() && editing && (bDragging || doubleClicked)) {
				int tX = textR.x;
				int tY = textR.y;

				int tWidth = fm.stringWidth( text );
				if (tWidth < iconR.width && iconR.width > maxWidth) {
					tX += (iconR.width-tWidth)/2;
				}
				else if (tWidth < maxWidth) {
					tX += (maxWidth-tWidth)/2;
				}

				int caretPos = 0;
				if (editX <= tX)
					caretPos = 0;
				else if (editX >= tX+tWidth)
					caretPos = text.length();
				else {
					int ind = 1;
					int prev = 0;
					while(ind <= text.length()) {
						String n = text.substring(0, ind);
						int charX = fm.stringWidth(n);
						if (editX >= (tX+prev) && editX <= (tX+charX) ) {
							if ( (tX+prev) - editX < editX - (tX+charX))
								caretPos = ind-1;
							else
								caretPos = ind;
							break;
						}
						prev = charX;
						ind++;
					}
				}

				if (bDragging) {
					if (currentCaretPosition == -1) {
						currentCaretPosition = caretPos;
						startSelection = currentCaretPosition;
						stopSelection = currentCaretPosition;
					}
					else {
						// IF DRAGGING LEFT
						if (caretPos < currentCaretPosition) {

							if (stopSelection == -1)
								stopSelection = currentCaretPosition;

							currentCaretPosition = caretPos;

							if (startSelection == -1 || startSelection >= currentCaretPosition)
								startSelection = currentCaretPosition;
							else
								stopSelection = currentCaretPosition;
						}
						// IF DRAGGING RIGHT
						else {
							if (startSelection == -1)
								startSelection = currentCaretPosition;

							currentCaretPosition = caretPos;

							if (stopSelection == -1 || stopSelection < currentCaretPosition)
								stopSelection = currentCaretPosition;
						}
					}
				}

				// DOUBLE CLICK TO SELECT WORD
				if (doubleClicked) {
					int index = text.indexOf(" ", caretPos);
					if (index == -1)
						stopSelection = text.length();
					else {
						stopSelection = index;
					}

					currentCaretPosition = stopSelection;

					String bit = text.substring(0, caretPos);
					index = bit.lastIndexOf(" ");
					if (index == -1)
						startSelection = 0;
					else {
						startSelection = index+1;
					}
					doubleClicked = false;
				}
			}

			drawText(g, fm, node, text, textR, iconR, maxWidth, 0);

			// If mouse just clicked
			if (node.hasFocus() && editing && currentCaretPosition == -1) {

				int tX = textR.x;
				int tY = textR.y;

				int tWidth = fm.stringWidth( text );
				if (tWidth < iconR.width && iconR.width > maxWidth) {
					tX += (iconR.width-tWidth)/2;
				}
				else if (tWidth < maxWidth) {
					tX += (maxWidth-tWidth)/2;
				}

				int caretPos = 0;
				if (editX <= tX)
					caretPos = 0;
				else if (editX >= tX+tWidth)
					caretPos = text.length();
				else {
					int ind = 1;
					int prev = 0;
					while(ind <= text.length()) {
						String n = text.substring(0, ind);
						int charX = fm.stringWidth(n);
						if (editX >= (tX+prev) && editX <= (tX+charX) ) {
							if ( (tX+prev) - editX < editX - (tX+charX))
								caretPos = ind-1;
							else
								caretPos = ind;
							break;
						}
						prev = charX;
						ind++;
					}
				}

				currentCaretPosition = caretPos;
				currentRow = 0;
				setCaretRectangle(g, fm, textR, text, currentCaretPosition, iconR, maxWidth);
			}
			else if (node.hasFocus() && editing) {
				// IF UP/DOWN KEY PRESSED ON A SINGLE LINE LABEL GO HOME/END
				if (caretUp) {
					currentCaretPosition = 0;
					caretUp = false;
				}
				if (caretDown) {
					currentCaretPosition = text.length();

					if (APP_PROPERTIES.isAutoSearchLabel()) {
						UIHintNodeLabelPanel panel = oViewPane.getLabelPanel(oNode.getNode().getId());
						if (panel != null)
							panel.focusList();
					}

					caretDown = false;
				}
				currentRow = 0;
				setCaretRectangle(g, fm, textR, text, currentCaretPosition, iconR, maxWidth);
			}
		}

		if (editing) {
			// PAINT CARET
			if (caretRectangle != null) {
				Color oldCol = g.getColor();
				g.setColor(Color.red);
   		         g.fillRect(caretRectangle.x, caretRectangle.y, caretRectangle.width, caretRectangle.height);
				g.setColor(oldCol);
			}

			// PAINT SUROUNDING BOX
			g.setColor(Color.blue);
			g.drawRect(labelRectangle.x, labelRectangle.y-1, labelRectangle.width, labelRectangle.height+1);
		}
  	}

	/**
	 * A helper method for the <code>paint</code> method. Calulate the caretRectangle position when it is moving up or down a row.
	 *
	 * @param g, the current Graphics context.
	 * @param fm, the font metrics to use.
	 * @param iconR, the rectangle of the node icon.
	 * @param maxWidth, the maximum width for the node.
	 * @param isUp, is the caret moving up a row? True equals up a row, false equals down a row.
	 */
	private void recalculateCaretRectangle(Graphics g, FontMetrics fm, Rectangle iconR, int maxWidth, boolean isUp) {

		if (isUp)
			currentRow--;
		else
			currentRow++;

		TextRowElement element = (TextRowElement)textRowElements.elementAt(currentRow);
		Rectangle currR = element.getTextRect();
		String currText = element.getText();
		int currStart = element.getStartPosition();

 		Rectangle oldCaretRectangle = new Rectangle(caretRectangle.x, caretRectangle.y, caretRectangle.width, caretRectangle.height);

		int textWidth = fm.stringWidth( currText );
		if (textWidth < iconR.width && iconR.width > maxWidth) {
			currR.x += (iconR.width-textWidth)/2;
		}
		else if (textWidth < maxWidth) {
			currR.x += (maxWidth-textWidth)/2;
		}

		if (oldCaretRectangle.x <= currR.x) {
			caretRectangle.x = currR.x;
			currentCaretPosition = currStart;
		}
		if (oldCaretRectangle.x >= currR.x+textWidth) {
			caretRectangle.x = currR.x+textWidth;
			currentCaretPosition = currStart+currText.length();
		}
		else {
			// FIND THE CLOSEST CHAR TO THE ONE ABOVE WHERE THE CARET WAS
			int caretPos = 0;
			int ind = 1;
			int prev = 0;
			int length = currText.length();
			while(ind <= length) {
				String n = currText.substring(0, ind);
				int charX = fm.stringWidth(n);
				if ( (currR.x+charX) > oldCaretRectangle.x ) {
					if ( oldCaretRectangle.x - (currR.x+prev) < (currR.x+charX)- oldCaretRectangle.x )
						caretPos = ind-1;
					else
						caretPos = ind;
					break;
				}
				prev = charX;
				ind++;
			}
			currentCaretPosition = currStart+caretPos;
			caretRectangle.x = currR.x+fm.stringWidth(currText.substring(0, caretPos)) - 1;
		}

		caretRectangle.y =  currR.y - fm.getAscent()+1;
        caretRectangle.width = oldCaretRectangle.width;
        caretRectangle.height = oldCaretRectangle.height;
	}

	/**
	 * A helper method for the <code>paint</code> method. Calulate and set the caretRectangle.
	 *
	 * @param g, the current Graphics context.
	 * @param fm, the font metrics to use.
	 * @param textR, the rectangle of the current text area of this label row.
	 * @param text, the row of text being addressed.
	 * @param caretIndex, the current position of the caret in this row.
	 * @param iconR, the rectangle of the node icon.
	 * @param maxWidth, the maximum width for the node.
	 * @param isUp, is the caret moving up a row? True equals up a row, false equals down a row.
	 */
	private void setCaretRectangle(Graphics g, FontMetrics fm, Rectangle textR, String text, int caretIndex, Rectangle iconR, int maxWidth) {

        if (caretIndex >= 0 && caretIndex <= text.length() ) {

			caretRectangle = new Rectangle();

			int textX = textR.x;
			int textY = textR.y;

			int textWidth = fm.stringWidth( text );

			int offset = 0;

			if (textWidth < iconR.width && iconR.width > maxWidth) {
				offset = (iconR.width-textWidth)/2;
				textX += offset;
			}
			else if (textWidth < maxWidth) {
				offset = (maxWidth-textWidth)/2;
				textX += offset;
			}

	        caretRectangle.x = textX + fm.stringWidth(text.substring(0, caretIndex));
            caretRectangle.y = textY - fm.getAscent()+1;
            caretRectangle.width = 1;
            caretRectangle.height = fm.getAscent()+1;
        }
	}

	/**
	 * A helper method for the <code>paint</code> method. Paint a row of text.
	 *
	 * @param g, the current Graphics context.
	 * @param fm, the font metrics to use.
	 * @param node com.compendium.ui.UINode, the current node being painted.
	 * @param text, the row of text being painted.
	 * @param textR, the rectangle of the current text area of this label row.
	 * @param caretIndex, the current position of the caret in this row.
	 * @param iconR, the rectangle of the node icon.
	 * @param maxWidth, the maximum width for the node.
	 * @param startPos, the starting position of this row in the context of the while label.
	 * @param isUp, is the caret moving up a row? True equals up a row, false equals down a row.
	 */
	private void drawText(Graphics g, FontMetrics fm, UINode node, String text, Rectangle textR,
									Rectangle iconR, int maxWidth, int startPos) {

		Color oldColor = null;

		if (text != null) {

			int textX = textR.x;
			int textY = textR.y;

			int textWidth = fm.stringWidth( text );

			if (textWidth < iconR.width && iconR.width > maxWidth) {
				textR.width = textWidth;
				textX += (iconR.width-textWidth)/2;
			}
			else if (textWidth < maxWidth) {
				textR.width = textWidth;
				textX += (maxWidth-textWidth)/2;
			}

			// text background will always be opaque
			oldColor = g.getColor();
			int state = node.getNode().getState();

			int stopPos = startPos+text.length();
			if (node.isSelected()) {
				if (editing && (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection)
						&& (startSelection < stopPos)
						&& (startSelection >= startPos || stopSelection > startPos) ) {

					// ONLY PAINT THE PART INSIDE SELECTION
					int begin = startSelection-startPos;
					int end = stopSelection-startPos;
					if (end > text.length())
						end = text.length();
					if (begin < 0)
						begin = 0;

					String beginText = text.substring(0, begin);
					String selText = text.substring(begin, end);
					String endText = text.substring(end);
					int beginWidth = fm.stringWidth(beginText);
					int selWidth = fm.stringWidth(selText);

					if (iconR.x < textR.x) {
						g.setColor(EDITING_COLOR);
						g.fillRect(iconR.x, textY-fm.getAscent(), beginWidth, textR.height);
						g.setColor(TSELECTED_COLOR);
						g.fillRect(iconR.x+beginWidth, textY-fm.getAscent(), selWidth, textR.height);
						g.setColor(EDITING_COLOR);
						g.fillRect(iconR.x+beginWidth+selWidth, textY-fm.getAscent(), iconR.width-selWidth, textR.height);
					}
					else {
						g.setColor(EDITING_COLOR);
						g.fillRect(textX, textY-fm.getAscent(), beginWidth, textR.height);
						g.setColor(TSELECTED_COLOR);
						g.fillRect(textX+beginWidth, textY-fm.getAscent(), selWidth, textR.height);
						g.setColor(EDITING_COLOR);
						g.fillRect(textX+beginWidth+selWidth, textY-fm.getAscent(), textR.width-selWidth, textR.height);
					}

					if (node.isEnabled()) {
					/*	if (state == ICoreConstants.UNREADSTATE) {
							g.setColor(UNREAD_TEXT_COLOR);
						} else if (state == ICoreConstants.MODIFIEDSTATE) {
							g.setColor(MODIFIED_TEXT_COLOR);
						} else {
							g.setColor(SELECTED_TEXT_COLOR);
						}
					*/
						g.setColor(SELECTED_TEXT_COLOR);
						paintEnabledText(node, g, beginText, textX, textY);
						g.setColor(TSELECTED_TEXT_COLOR);
						paintEnabledText(node, g, selText, textX+beginWidth, textY);
					/*
						if (state == ICoreConstants.UNREADSTATE) {
							g.setColor(UNREAD_TEXT_COLOR);
						} else if (state == ICoreConstants.MODIFIEDSTATE) {
							g.setColor(MODIFIED_TEXT_COLOR);
						} else {
							g.setColor(SELECTED_TEXT_COLOR);
						}
						*/
						g.setColor(SELECTED_TEXT_COLOR);
						paintEnabledText(node, g, endText, textX+beginWidth+selWidth, textY);
					}
					else { // NEVER REACHED AT PRESENT
						g.setColor(SELECTED_TEXT_COLOR);
						paintDisabledText(node, g, beginText, textX, textY);
						g.setColor(TSELECTED_TEXT_COLOR);
						paintDisabledText(node, g, selText, textX+beginWidth, textY);
						g.setColor(SELECTED_TEXT_COLOR);
						paintDisabledText(node, g, endText, textX+beginWidth+selWidth, textY);
					}
					g.setColor(oldColor);
				}
				else {
					if (editing) {
						g.setColor(EDITING_COLOR);
					} else {
						g.setColor(SELECTED_COLOR);
					}

					if (iconR.x < textR.x) {
						g.fillRect(iconR.x, textY-fm.getAscent(), iconR.width, textR.height);
					}
					else {
						g.fillRect(textX, textY-fm.getAscent(), textR.width, textR.height);
					}

				/*	if (state == ICoreConstants.UNREADSTATE) {
						g.setColor(UNREAD_TEXT_COLOR);
					} else if (state == ICoreConstants.MODIFIEDSTATE) {
						g.setColor(MODIFIED_TEXT_COLOR);
					} else {
						g.setColor(SELECTED_TEXT_COLOR);
					}  */
					g.setColor(SELECTED_TEXT_COLOR);
					if (node.isEnabled()) {
						paintEnabledText(node, g, text, textX, textY);
					}
					else {
						paintDisabledText(node, g, text, textX, textY);
					}
					g.setColor(oldColor);
				}
			}
			else {
				g.setColor(new Color(node.getNodePosition().getBackground()));
				g.fillRect(textX, textY-fm.getAscent(), textR.width, textR.height);

				g.setColor(oldColor);

				if (node.isEnabled()) {
				/*	if (state == ICoreConstants.UNREADSTATE) {
						g.setColor(UNREAD_TEXT_COLOR);
					} else if (state == ICoreConstants.MODIFIEDSTATE) {
						g.setColor(MODIFIED_TEXT_COLOR);
					} else {
						g.setColor(new Color(node.getNodePosition().getForeground()));
					}
					*/
					g.setColor(new Color(node.getNodePosition().getForeground()));
					paintEnabledText(node, g, text, textX, textY);
				}
				else { // NEVER REACHED AT PRESENT
					paintDisabledText(node, g, text, textX, textY);
				}
			}
		}
	}

	/**
	 * Calculate the requred dimensions of the given node.
	 * Checks for icon and various node indicator extras when calculating.
	 * @param node com.compendium.ui.UINode, the node to calculate the dimensions for.
	 * @return Dimension, the dimension for the given node.
	 */
	private Dimension calculateDimension(UINode node) {

		hasTrans = false;
		hasText = false;
		hasCodes = false;
		hasWeight = false;
		hasMovie = false;

		String text = node.getText();
		String id = node.getNode().getId();

		Insets insets = node.getInsets();
		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;

		Font font = node.getFont();
		FontMetrics fm = node.getFontMetrics(font);

		Rectangle iconR = new Rectangle();
		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(node.getSize());

		viewR.x = insets.left;
		viewR.y = insets.top;

		NodePosition pos = node.getNodePosition();

		Icon icon = node.getIcon();
		if (pos.getHideIcon() || icon == null) {
			iconR.width=0;
			iconR.height=0;
			iconR.y = 1;
			textR.y = iconR.y + iconR.height + fm.getAscent();
		}
		else {
			iconR.width = icon.getIconWidth()+1;
			iconR.height = icon.getIconHeight()+1;
			iconR.y = viewR.y+1;
			textR.y = iconR.y + iconR.height + node.getIconTextGap() + fm.getAscent();

			// FOR EXTRA BIT ON SIDE
			NodeSummary nodeSumm = node.getNode();
			String detail = nodeSumm.getDetail();
			detail = detail.trim();
			int type = node.getType();

			// ADD EXTRA WIDTH FOR BITS ON SIDE IF REQUIRED
			if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents() &&
					ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.REPLAY ) {
				hasMovie = true;
			}

			if (pos.getShowTrans()
					&&  (nodeSumm.isInMultipleViews()) && (nodeSumm.getViewCount() > 1)) {
				hasTrans = true;
			}
			if (pos.getShowText()
					&& (type != ICoreConstants.TRASHBIN
							&& !detail.equals("")
							&& !detail.equals(ICoreConstants.NODETAIL_STRING)
							&& !id.equals(ProjectCompendium.APP.getInBoxID()))) {
				hasText = true;
			}
			if  (pos.getShowWeight()
					&& (type == ICoreConstants.MAPVIEW || type == ICoreConstants.LISTVIEW)) {
				hasWeight = true;
			}
			try {
				if (pos.getShowTags() && node.getNode().getCodeCount() > 0) {
					hasCodes = true;
				}
			}
			catch(Exception ex) {
				System.out.println("Error: (NodeUI.calculateDimension) \n\n"+ex.getMessage());
			}

			if (hasMovie || hasTrans || hasText || hasWeight || hasCodes) {

				//AffineTransform trans = font.getTransform();
				//Font newFont = (new Font("Dialog", Font.BOLD, 10)).deriveFont(trans);

				// work around for Mac BUG with derive Font
				AffineTransform trans=new AffineTransform();
				trans.setToScale(node.getScale(), node.getScale());

				Point p1 = new Point(10, 10);
				try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
				catch(Exception e) {System.out.println("can't convert font size (UINode.calculateDimension)\n\n"+e.getMessage()); }

				Font newFont = new Font("Dialog" , Font.BOLD, p1.x);

				FontMetrics sfm = node.getFontMetrics(newFont);
				extraIconWidth = sfm.charWidth('W') * 5;
				iconR.width += extraIconWidth;
			}
		}

		int wrapWidth = pos.getLabelWrapWidth();
		if (wrapWidth <= 0) {
			wrapWidth = ((Model)ProjectCompendium.APP.getModel()).labelWrapWidth;
		}
		wrapWidth = wrapWidth+1; // Needs this for some reason.
		int textWidth = text.length();

		textR.width = textWidth;
		int widestLine = 0;

		textR.height = 0;
		textR.x = dx;

		if (textWidth > wrapWidth) {

			int loop = -1;
			String textLeft = text;

			while ( textLeft.length() > 0 ) {
				loop ++;
				int textLen = textLeft.length();
				int curLen = wrapWidth;
				if (textLen < wrapWidth ) {
					curLen = textLen;
				}
				String nextText = textLeft.substring(0, curLen);
				if (curLen < textLen) {
					int lastSpace = nextText.lastIndexOf(" ");
					if (lastSpace != -1 && lastSpace != textLen) {
						curLen = lastSpace+1;
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
					else {
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}

					textR.height += fm.getAscent()+fm.getDescent();
				}
				else {
					if (!textLeft.equals("")) {
						textR.height += (fm.getDescent())*2;
					}
					nextText = textLeft;
					textLeft = "";
				}

				int thisWidth = fm.stringWidth( nextText );
				if ( thisWidth > widestLine) {
					widestLine = thisWidth;
				}
			}
		}
		else {
			widestLine = fm.stringWidth( text );
			textR.height += (fm.getDescent()*2);
		}

		textR.width = widestLine;
		if (iconR.width > textR.width) {
			textR.width = iconR.width;
		}

		Dimension rv = iconR.union(textR).getSize();
		rv.width += dx+1;
		nodeWidth = rv.width;

		rv.height += dy;

		return rv;
	}

	/**
	 * Return the node's preferred size.
	 * @param c, the component to return the preferred size for.
	 * @return Dimension, the preferred size for the given node.
	 */
 	public Dimension getPreferredSize(JComponent c) {

		UINode node = (UINode)c;

		String text = node.getText();
		Icon icon = node.getIcon();
		Insets insets = node.getInsets();
		Font font = node.getFont();

		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;

		if ((icon == null) && ((text == null) || ((text != null) && (font == null)))) {
			return new Dimension(dx, dy);
		}
		else if ((text == null) || ((icon != null) && (font == null))) {
			return new Dimension(icon.getIconWidth() + dx, icon.getIconHeight() + dy);
		}
		else {
			return calculateDimension(node);
		}
  	}

  	/**
	 * Return the node's minimum size. Just calls <ocde>getPreferredSize</code>.
	 * @param c, the component to return the minimum size for.
	 * @return Dimension, the minimum size for the given node.
     * @see #getPreferredSize
     */
  	public Dimension getMinimumSize(JComponent c) {
		return getPreferredSize(c);
  	}

   	/**
	 * Return the node's maximum size. Just calls <ocde>getPreferredSize</code>.
	 * @param c, the component to return the maximum size for.
	 * @return Dimension, the maximum size for the given node.
     * @see #getPreferredSize
     */
  	public Dimension getMaximumSize(JComponent c) {
		return getPreferredSize(c);
  	}


/***** EVENT HANDLING METHODS *****/

	/**
	 * Update the local and global mouse position information.
	 * @param Point p, the mouse position.
	 */
	private void updateMousePosition(Point p ) {
		Point point = new Point(p.x, p.y);
		SwingUtilities.convertPointToScreen(point, oNode);
		ProjectCompendium.APP._x = point.x;
		ProjectCompendium.APP._y = point.y;
	}

	/**
	 * Handles the initiation of drag and drop events.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mousePressed(MouseEvent evt) {

		//if (timer != null) {
		//	timer.cancel();
		//}

		//System.out.println("Mouse pressed on " + oNode.getNode().getLabel()+" AT "+new Date().getTime());

		Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), null);
		// coordinates of the pressed event converted into the event's source object
		_x = p.x;
		_y = p.y;

		// coordinates of the event
		__x = evt.getX();
		__y = evt.getY();

		// set this up for when the mouse is dragged so coord can be updated to new position
		lastMousePosX = _x;
		lastMousePosY = _y;

		startSelection = -1;
		stopSelection = -1;
		doubleClicked = false;

		//the rectangle defining this node oStartingBounds
		oStartingBounds = oNode.getBounds();

		// start dragging if left or right mouse button is pressed
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
		boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);

		if (ProjectCompendium.isMac &&
				((evt.getButton() == 3 && evt.isShiftDown()) ||
				(evt.getButton() == 1 && evt.isAltDown()))) {

			bIsMacRightMouse = true;
			isRightMouse = true;
			isLeftMouse = false;
		}

		if (isLeftMouse || isRightMouse ) {

			if (editing && isLeftMouse) {
				if (labelRectangle != null && labelRectangle.contains(evt.getX(), evt.getY())) {
					editX = evt.getX();
					editY = evt.getY();
					currentCaretPosition = -1;
					oNode.repaint();
					return;
				}
				else {
					editing = false;
					oNode.getViewPane().hideLabels();
				}
			}
			else {
				// convert event coordinats to the view pane coord system.
				ptStart = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oNode.getViewPane());

				if (isRightMouse) {
					int modifiers = evt.getModifiers();
					if ((modifiers & MouseEvent.SHIFT_MASK) != 0) {
						oNode.setSelected(true);
						oNode.setRollover(false);
				  	}
					else {
						oNode.getViewPane().setSelectedNode(oNode,ICoreConstants.SINGLESELECT);
						oNode.setSelected(true);
						oNode.setRollover(false);
					}

					int type = oNode.getType();
					if (type == ICoreConstants.ARGUMENT || type == ICoreConstants.ARGUMENT_SHORTCUT) {
						isPlus = false;
						isMinus = false;

						if (plusRectangle != null && minusRectangle != null) {
							if (plusRectangle.contains(__x, __y)) {
								isPlus = true;
							}
							else if (minusRectangle.contains(__x, __y)) {
								isMinus = true;
							}
						}
					}
				}
			}
		}
  	}

	/*public void startCursorBlink() {

		if (cursorThread != null)
			return;

		cursorThread = new Thread() {
			public void run() {

				while(editing) {

					RepaintManager mgr = RepaintManager.currentManager(oNode.getViewPane());
					if (caretRectangle != null) {
						Graphics g = oNode.getGraphics();
						Color oldColor = g.getColor();
						if (lastColor == SELECTED_COLOR) {
							g.setColor(Color.black);
							lastColor = SELECTED_COLOR;
						}
						else {
							g.setColor(SELECTED_COLOR);
							lastColor = Color.black;
						}
            			g.fillRect(caretRectangle.x, caretRectangle.y, caretRectangle.width, caretRectangle.height);
						g.setColor(oldColor);
						mgr.addDirtyRegion(oNode, caretRectangle.x, caretRectangle.y, caretRectangle.width, caretRectangle.height);
						mgr.paintDirtyRegions();
					}
					try {
						this.wait(500);
					}
					catch(Exception ex){

					}
				}
			}
		};
		cursorThread.run();
	}*/

	/**
	 * Handles the single and double click events.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mouseClicked(MouseEvent evt) {

   		if (timer != null) {
			timer.cancel();
		}
		int clickCount = evt.getClickCount();
		int modifiers = evt.getModifiers();

		boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
		if (ProjectCompendium.isMac &&
			(evt.getButton() == 3 && evt.isShiftDown())) {
			isRightMouse = true;
			isLeftMouse = false;
		}

	  	oUIConnectingLine = null;
		oUIConnectingLines.removeAllElements();

		UIViewPane oViewPane = oNode.getViewPane();

		if ((isRightMouse || (ProjectCompendium.isMac && (modifiers & MouseEvent.CTRL_MASK) != 0)) && (!(oNode.getViewPane() instanceof UIAerialViewPane))) {
			if(clickCount == 1) {
				oNode.requestFocus();
				if ((modifiers & MouseEvent.SHIFT_MASK) != 0) {
					oViewPane.setSelectedNode(oNode,ICoreConstants.MULTISELECT);
					oNode.setSelected(true);
					oNode.setRollover(false);
					oNode.showPopupMenu(this, evt.getX(),evt.getY());
			  	}
				else {
					oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
					oViewPane.setSelectedNode(oNode,ICoreConstants.SINGLESELECT);
					oNode.setSelected(true);
					oNode.setRollover(false);
					oNode.showPopupMenu(this, evt.getX(),evt.getY());
				}
			}
		} else if (isLeftMouse) {
			//SELECT ALL ANCESTOR NODES
			if ( ((modifiers & MouseEvent.ALT_MASK) != 0) && ((modifiers & MouseEvent.SHIFT_MASK) != 0)) {
				oViewPane.selectAllAncestors(oNode);
				return;
			}
			//SELECT ALL CHILD NODES
			else if ((modifiers & MouseEvent.ALT_MASK) != 0
						&& (modifiers & MouseEvent.CTRL_MASK) == 0
							&& (modifiers & MouseEvent.SHIFT_MASK) == 0) {
				oViewPane.selectAllChildren(oNode);
				return;
			}

			int nX = evt.getX();
			int nY = evt.getY();

			// CHECK LABEL HAS BEEN CLICKED
			if (labelRectangle != null && labelRectangle.contains(nX, nY)) {
				if (clickCount == 1) {

					editing = true;
					editX = nX;
					editY = nY;
					startSelection = -1;
					stopSelection = -1;

					currentCaretPosition = -1;
					doubleClicked = false;

					oNode.moveToFront();

					if (!oNode.isSelected()) {
						oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
						oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
						oViewPane.setSelectedNode(oNode, ICoreConstants.SINGLESELECT);
						oNode.setSelected(true);
					}

					oNode.setRollover(false);
					oNode.requestFocus();

					return;
				}
				else if (clickCount == 2) {
					editing = true;
					editX = nX;
					editY = nY;
					startSelection = -1;
					stopSelection = -1;

					currentCaretPosition = -1;
					doubleClicked=true;

					oNode.moveToFront();

					if (!oNode.isSelected()) {
						oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
						oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
						oViewPane.setSelectedNode(oNode, ICoreConstants.SINGLESELECT);
						oNode.setSelected(true);
					}
					oNode.setRollover(false);
					oNode.requestFocus();

					return;
				}
			}
			else {
				editing = false;
				oViewPane.hideLabels();
				doubleClicked = false;
			}

			// CHECK IF NODE HAS A M AND IF IT HAS BEEN CLICKED
			if (hasMovie) {
				if (movieRectangle != null && movieRectangle.contains(nX, nY)) {
					//ProjectCompendium.APP.oMeetingManager.sendMeetingReplay(this);
					return;
				}
			}

			// CHECK IF NODE HAS A T AND IF IT HAS BEEN CLICKED
			if (hasText) {
				if (textRectangle != null && textRectangle.contains(nX, nY)) {
					openEditDialog(false);
					return;
				}
			}
			// CHECK IF NODE HAS A TRANSCLUSION NUMBER AND IF IT HAS BEEN CLICKED
			if (hasTrans) {
				if (transRectangle != null && transRectangle.contains(nX, nY)) {
					oNode.showViewsDialog();
					return;
				}
			}
			// CHECK IF NODE HAS CODES C AND IF IT HAS BEEN CLICKED
			if (hasCodes) {
				if (codeRectangle != null && codeRectangle.contains(nX, nY)) {
					ProjectCompendium.APP.onCodes();
					return;
				}
			}
			// CHECK IF NODE HAS WEIGHTS AND IF IT HAS BEEN CLICKED
			if (hasWeight) {
				if (weightRectangle != null && weightRectangle.contains(nX, nY)) {
					ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.ABOUT_ACTION);

					View view = null;
					int type = oNode.getNode().getType();
					if( type == ICoreConstants.MAP_SHORTCUT || type == ICoreConstants.LIST_SHORTCUT ) {
						view = (View)(((ShortCutNodeSummary)oNode.getNode()).getReferredNode());
					}
					else {
						view = (View)oNode.getNode();
					}
					ProjectCompendium.APP.addViewToDesktop(view, oNode.getText());
					return;
				}
			}

			if ((modifiers & MouseEvent.SHIFT_MASK) != 0) {
				oNode.moveToFront();
				if(oNode.isSelected()) {
					oNode.setSelected(false);
					oViewPane.removeNode(oNode);
					// USUSALLY UIViewPane.setSelectedNode does this.
					// But there is no SINGLEDESELECT mode.
					// So I need to make sure the setNodeSelected methods get called correctly for the tags view.
					if (oViewPane.getNumberOfSelectedNodes() > 0) {
						ProjectCompendium.APP.setNodeSelected(true);
					} else {
						ProjectCompendium.APP.setNodeSelected(false);
					}
				}
				else {
					oNode.setSelected(true);
					oViewPane.setSelectedNode(oNode, ICoreConstants.MULTISELECT);
				}
				oNode.requestFocus();
		  	}
	      	else {
				if ((clickCount > 1) || (APP_PROPERTIES.isSingleClick())) {
	      			openNode();
	      		} else {
					if (clickCount == 1) {
						oNode.moveToFront();
						oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
						oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
						oNode.setRollover(false);
						oNode.requestFocus();
						oViewPane.setSelectedNode(oNode, ICoreConstants.SINGLESELECT);
						oNode.setSelected(true);
					}
	      		}
			}
		}
	}

	/**
	 * Open this node depending on type.
	 * If a map/list node, open the view.
	 * If the trashbin open the Trashbin dialog.
	 * If a reference node, open any associated reference in an external application.
	 * If any other node, open the UINodeContentDialog for this node.
	 */
	public void openNode() {

		releaseFocusAndRollover();

		int type = oNode.getNode().getType();

		if ((type == ICoreConstants.MAPVIEW) ||
			(type == ICoreConstants.LISTVIEW) ||
			(type == ICoreConstants.MAP_SHORTCUT) ||
			(type == ICoreConstants.LIST_SHORTCUT))
		{
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.ABOUT_ACTION);

			View view = null;
			if( type == ICoreConstants.MAP_SHORTCUT || type == ICoreConstants.LIST_SHORTCUT ) {
				view = (View)(((ShortCutNodeSummary)oNode.getNode()).getReferredNode());
			}
			else {
				view = (View)oNode.getNode();
			}

			UIViewFrame frame = ProjectCompendium.APP.addViewToDesktop(view, oNode.getText());
			frame.setNavigationHistory(oNode.getViewPane().getViewFrame().getChildNavigationHistory());
		}
		else if(type  == ICoreConstants.TRASHBIN) {
			UITrashViewDialog dlgTrash = new UITrashViewDialog(ProjectCompendium.APP, this);
			UIUtilities.centerComponent(dlgTrash, ProjectCompendium.APP);
			dlgTrash.setVisible(true);
		}
		else if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
			try {
				oNode.getNode().setState(ICoreConstants.READSTATE);		// Mark the node read (doesn't happen elsewhere)
			}
			catch (SQLException ex) {}
			catch (ModelSessionException ex) {};
			String path = oNode.getNode().getSource();

			if (path == null || path.equals("")) {
				openEditDialog(false);
			} else if (path.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
				path = path.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
				int ind = path.indexOf("/");
				if (ind != -1) {
					String sGoToViewID = path.substring(0, ind);
					String sGoToNodeID = path.substring(ind+1);
					UIUtilities.jumpToNode(sGoToViewID, sGoToNodeID,
							oNode.getViewPane().getViewFrame().getChildNavigationHistory());
				}
			} else if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.") ||
					ProjectCompendium.isLinux && (path.startsWith("fish:") || path.startsWith("ssh:") || path.startsWith("ftp:") || path.startsWith("smb:"))) {
				if (!ExecuteControl.launch( path )) {
					openEditDialog(false);
				}
				else {
					// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
					if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
							&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
											 oNode.getNodePosition().getView(),
											 oNode.getNode()));
					}
				}
			}
			else {
				File file = new File(path);
				String sPath = path;
				if (file.exists()) {
					sPath = file.getAbsolutePath();
				}
				// It the reference is not a file, just pass the path as is, as it is probably a special type of url.
				if (!ExecuteControl.launch( sPath ))
					openEditDialog(false);
				else {
					// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
					if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
							&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
											 oNode.getNodePosition().getView(),
											 oNode.getNode()));
					}
				}
			}
		}
		else {
			openEditDialog(false);
		}
	}

	/**
	 * Handles drag and drop finish operations.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mouseReleased(MouseEvent evt) {

		if (timer != null) {
			timer.cancel();
		}

		//System.out.println("Mouse released " + oNode.getNode().getLabel()+" at x="+evt.getX()+" y="+evt.getY()+" time="+new Date());

		Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), null);

		boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);

		if (ProjectCompendium.isMac && bIsMacRightMouse) {
			isRightMouse = true;
			isLeftMouse = false;
		}

		if (bDragging) {
			if (isLeftMouse && !evt.isAltDown()) {
				if (editing) {
					if (labelRectangle != null && labelRectangle.contains(evt.getX(), evt.getY())) {
						editX = evt.getX();
						editY = evt.getY();
						oNode.repaint();
					}
				}
				else if (_x != p.x || _y != p.y) {
					UIViewPane viewPane = oNode.getViewPane();

					// if oNode is selected then confirm move in view for all seleted nodes
					if (oNode.isSelected()) {

	 				     for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
							UINode uinode = (UINode)e.nextElement();
							NodeUI nodeui = (NodeUI)uinode.getUI();

							Rectangle r = uinode.getBounds();

							//rescale the position to actual position
							double scale = viewPane.getScale();
							Point transPoint = new Point(r.x, r.y);
							//if (scale > 0.0 ) {
								transPoint = UIUtilities.scalePoint(r.x, r.y, scale);
	 				     	//}
							try {
								// This also set the value in the NodePosition object
								uinode.getViewPane().getView().setNodePosition(uinode.getNode().getId(), transPoint);
							}
							catch(Exception ex) {
								System.out.println(ex.getMessage());
							}

							//NodePosition nodepos = uinode.getNodePosition();
							//nodepos.setPos(transPoint.x, transPoint.y);
						 }
					}
					//otherwise just handle this node
					else {
						// node moved, update node's coordinates in view
						INodeSummary node = oNode.getNode();
						if (node != null) {
							Rectangle r = oNode.getBounds();

							//rescale the position to actual position
							Point transPoint = UIUtilities.scalePoint(r.x, r.y, viewPane.getScale());

							//confirm move in the view
							try {
								// This also set the value in the NodePosition object
								oNode.getViewPane().getView().setNodePosition(oNode.getNode().getId(), transPoint);
//								System.out.println("NodeUI: setNodePosition to ("+transPoint.x+", "+transPoint.y+") - INodeSummary ("+r.x+", "+r.y+")");
//								System.out.flush();
							}
							catch(Exception ex) {
								System.out.println(ex.getMessage());
							}
						}
					}
				}
			}
			else if (isRightMouse) {
				Point ptNew2 = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oNode.getViewPane());
				Component comp = SwingUtilities.getDeepestComponentAt(oNode.getViewPane(), ptNew2.x, ptNew2.y);
				if (comp instanceof UINode) {
					UINode node = (UINode)comp;
					NodeUI nodeui = node.getUI();
					nodeui.drop(oNode);
				}
				clearDummyLinks();
			}
		}

		_x = 0;
		_y = 0;
		__x = 0;
		__y = 0;
		lastMousePosX = 0;
		lastMousePosY = 0;
		previousMousePosX = 0;
		previousMousePosY = 0;
		oStartingBounds = null;
		bIsMacRightMouse=false;
		bDragging = false;
	}

  	/**
  	 * Flushed updated view coordinates to the database.  This is basically a bug fix that keeps label editing
  	 * from scooting the node's apparent X coordinate to the right or left.
  	 */
  	public void flushPosition() {

  		UIViewPane viewPane = oNode.getViewPane();
  		INodeSummary node = oNode.getNode();
		if (node != null) {
			Rectangle r = oNode.getBounds();

			//rescale the position to actual position
			Point transPoint = UIUtilities.scalePoint(r.x, r.y, viewPane.getScale());

			//confirm move in the view
			try {
				// This also set the value in the NodePosition object
				oNode.getViewPane().getView().setNodePosition(oNode.getNode().getId(), transPoint);
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
  	}

	/**
	 * Visualizes when a mouse enters the node.
	 * Also, display any detail text for the node in the status bar.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mouseEntered(MouseEvent evt) {

		if (timer != null) {
			timer.cancel();
		}
		if (oViewPane == null) {
			oViewPane = oNode.getViewPane();
		}
		if (oViewPane != null) {
			oViewPane.getUI().setCurrentNode(oNode, false);
		}
		Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oNode);
		updateMousePosition(p);

		oNode.setRollover(true);

		//Lakshmi (6/7/06) - updated status info to include author and date of creation
		String sStatus = "";
		String author = oNode.getNode().getAuthor();
		String creationDate = (UIUtilities.getSimpleDateFormat("dd-MMM-yy").format(oNode.getNode().getCreationDate()).toString());

		String showtext = creationDate +" (" + author + ") " +
						 oNode.getNode().getLabel() + " [] " +
						 oNode.getNode().getDetail();

		if (showtext != null) {
			showtext = showtext.replace('\n',' ');
			showtext = showtext.replace('\r',' ');
			showtext = showtext.replace('\t',' ');
			sStatus = showtext;
		}

		ProjectCompendium.APP.setStatus(sStatus);


	}

	/**
	 * Visualizes when a mouse exits the node.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mouseExited(MouseEvent evt) {

		if (timer != null) {
			timer.cancel();
		}
		if (oViewPane == null) {
			oViewPane = oNode.getViewPane();
		}
		if (oViewPane != null) {
			oViewPane.getUI().setCurrentNode(oNode, true);
		}

		Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oNode);
		updateMousePosition(p);

		oNode.setToolTipText(null);
	  	ProjectCompendium.APP.setStatus("");

	  	UIViewPane nodeViewPane = oNode.getViewPane();

		oNode.setRollover(false);

		if (nodeViewPane != null) {
			nodeViewPane.hideCodes();
			//nodeViewPane.hideViews();
			nodeViewPane.hideDetail();
			//nodeViewPane.hideLabels();

			if (APP_PROPERTIES.isImageRollover())
				nodeViewPane.hideImages();
		}

		//it seems this mouseExited method is invoked on a node after a node
		// has been removed from the view (but there are other instances of the
		// node so it is not moved to the trashbin).  Hard to figure out why - in
		// the meantime, only repaint if the viewpane is not null so no exception
		// is thrown.
		if (nodeViewPane != null)
			nodeViewPane.repaint();
	}

	/**
	 * Invoked when a mouse is dragged (pressed and moved).
	 * @param evt, the associated MouseEvent.
	 */
	public void mouseDragged(MouseEvent evt) {

		//System.out.println("Mouse dragged " + oNode.getNode().getLabel()+" at "+evt.getX()+" , "+evt.getY()+" time="+new Date());

		boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);

		if (ProjectCompendium.isMac && bIsMacRightMouse) {
			isRightMouse = true;
			isLeftMouse = false;
		}

		processMouseDragged(evt, isRightMouse, isLeftMouse);
	}


	private static String sDirection = "";
	private static int oXPos = 0;
	private static int oYPos = 0;

	/**
	 * Prcess a mouse is dragged (or moved on the Mac)
	 * @param evt, the associated MouseEvent.
	 * @param isRightMouse, is this a right mouse event.
	 * @param isLeftMouse, is this a left mouse event.
	 */
	private void processMouseDragged(MouseEvent evt, boolean isRightMouse, boolean isLeftMouse) {

		Point ptNew = null;
		bDragging = true;

		if (timer != null) {
			timer.cancel();
		}

		if(oNode.getViewPane().getView().getType() == ICoreConstants.MAPVIEW) {

			oViewPane = oNode.getViewPane();
			if (oViewPane instanceof UIAerialViewPane) {	// Ignore node drags in Aerial View
				return;										// Someday do it right and apply scaling to everything that follows
			}

			if ( oStartingBounds == null ) {
				return;
			}
			if (isLeftMouse && !evt.isAltDown()) {

				if (editing) {
					if (labelRectangle != null && labelRectangle.contains(evt.getX(), evt.getY())) {
						editX = evt.getX();
						editY = evt.getY();
						oNode.repaint();
						return;
					}
				}
				else {
					Point p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), null);
					Insets i = oNode.getInsets();
					Dimension s = oNode.getParent().getSize();
					int pWidth = s.width;
					int pHeight = s.height;

					int diffX = p.x - lastMousePosX;
					int diffY = p.y - lastMousePosY;

					// if this node is selected, then find out if any of the selected
					// nodes are going to go out of bounds, if so, then limit
					// the entire set of nodes so that does not happen.
					int minX, minY, maxX, maxY;
					minX = oStartingBounds.x + diffX;
					maxX = minX + oStartingBounds.width;
					minY = oStartingBounds.y + diffY;
					maxY = minY + oStartingBounds.height;

					// if the node is selected then we have to worry about all
					// the other selected nodes bc they will be moving as well.
					if (oNode.isSelected()) {

						for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
							UINode uinode = (UINode)e.nextElement();
							NodeUI nodeui = (NodeUI)uinode.getUI();

							// skip current node since we started w/this node.
							if(!nodeui.equals(this)) {
								Rectangle uinode_oStartingBounds = uinode.getBounds();
								int uinode_newX = uinode_oStartingBounds.x + diffX;
								int uinode_newY = uinode_oStartingBounds.y + diffY;

								if (uinode_newX < minX) minX = uinode_newX;
								if (uinode_newX + uinode_oStartingBounds.width > maxX)
									maxX = uinode_newX + uinode_oStartingBounds.width;
								if (uinode_newY < minY) minY = uinode_newY;
								if (uinode_newY + uinode_oStartingBounds.height > maxY)
									maxY = uinode_newY + uinode_oStartingBounds.height;
							}
						}
					}

					// Make sure we stay in-bounds
					if (minX < 0) diffX = diffX - minX;
					if (minY < 0) diffY = diffY - minY;
					if (maxX > pWidth)
						diffX = diffX - (maxX - pWidth);
					if (maxY > pHeight)
						diffY = diffY - (maxY - pWidth);

					int newX = oStartingBounds.x + diffX;
					int newY = oStartingBounds.y + diffY;

					///////////////MUTLIDRAG MOUSE LEFT BUTTON (MOVE)//////////////////////////
					oNode.setBounds(newX, newY, oNode.getWidth(), oNode.getHeight());
					ptNew = SwingUtilities.convertPoint((Component)evt.getSource(),
														evt.getX(), evt.getY(), oNode.getViewPane());

					oNode.updateLinks();
					// If this node is selected then move the other selected nodes as well
					// otherwise just move this node.
					if (oNode.isSelected()) {
						for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
							//Point ptNewOther = null;
							UINode uinode = (UINode)e.nextElement();
							NodeUI nodeui = (NodeUI)uinode.getUI();

							if(!nodeui.equals(this)) {
								Rectangle uinode_oStartingBounds = uinode.getBounds();
								int uinode_newX = uinode_oStartingBounds.x + diffX;
								int uinode_newY = uinode_oStartingBounds.y + diffY;
								uinode.setBounds(uinode_newX,
												 uinode_newY,
												 uinode.getWidth(),
												 uinode.getHeight());

								uinode.updateLinks();
							}
						}
					}

					//update positions for next call to this function:
					// coordinates of the drag event converted into the event's source object
					lastMousePosX = p.x;
					lastMousePosY = p.y;
					//the rectangle defining this node oStartingBounds
					oStartingBounds = oNode.getBounds();

					///////////////END MUTLIDRAG MOUSE LEFT BUTTON (MOVE)//////////////////////////

					// IF DRAGGING OF NODE/LINK IS LEAVING THE SCREEN - SCROLL IT
					JViewport viewport = oViewPane.getViewFrame().getViewport();
					Rectangle nodeBounds = oNode.getBounds();
					Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, nodeBounds.x, nodeBounds.y, viewport);
					viewport.scrollRectToVisible( new Rectangle( parentPos.x, parentPos.y, nodeBounds.width, nodeBounds.height ) );


					/*Point currentMousePoint = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), null);
					JViewport viewport = oViewPane.getViewFrame().getViewport();
					Rectangle oViewPort = viewport.getViewRect();

					int pWidth = oViewPort.width;
					int pHeight = oViewPort.height;

					int diffMouseMoveX = currentMousePoint.x - lastMousePosX;
					int diffMouseMoveY = currentMousePoint.y - lastMousePosY;

					lastMousePosX = currentMousePoint.x;
					lastMousePosY = currentMousePoint.y;

					// define the new bounding points of node being dragged.
					int newMinX = oStartingBounds.x + diffMouseMoveX;
					int newMinY = oStartingBounds.y + diffMouseMoveY;
					int newMaxX = newMinX + oStartingBounds.width;
					int newMaxY = newMinY + oStartingBounds.height;

					// Recalculate this to be the new bounding points for all selected nodes being dragged,
					// if there are any.
					if (oNode.isSelected()) {

						for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
							UINode uinode = (UINode)e.nextElement();
							NodeUI nodeui = (NodeUI)uinode.getUI();

							// skip current node since we started w/this node.
							if(!nodeui.equals(this)) {
								Rectangle uinode_oStartingBounds = uinode.getBounds();
								int uinode_newX = uinode_oStartingBounds.x + diffMouseMoveX;
								int uinode_newY = uinode_oStartingBounds.y + diffMouseMoveY;

								if (uinode_newX < newMinX)
									newMinX = uinode_newX;
								if (uinode_newX + uinode_oStartingBounds.width > newMaxX)
									newMaxX = uinode_newX + uinode_oStartingBounds.width;
								if (uinode_newY < newMinY)
									newMinY = uinode_newY;
								if (uinode_newY + uinode_oStartingBounds.height > newMaxY)
									newMaxY = uinode_newY + uinode_oStartingBounds.height;
							}
						}
					}

					// Make sure the new bounding points stay in-bounds of the screen edges
					if (newMinX < 0) diffMouseMoveX = diffMouseMoveX - newMinX;
					if (newMinY < 0) diffMouseMoveY = diffMouseMoveY - newMinY;

					if (newMaxX > pWidth) {
						diffMouseMoveX = diffMouseMoveX - (newMaxX - pWidth);
					}
					if (newMaxY > pHeight) {
						diffMouseMoveY = diffMouseMoveY - (newMaxY - pHeight);
					}

					moveNodes(diffMouseMoveX, diffMouseMoveY);

					Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, oStartingBounds.x, oStartingBounds.y, viewport);
					viewport.scrollRectToVisible( new Rectangle( parentPos.x, parentPos.y, oStartingBounds.width, oStartingBounds.height ) );

					//Have we hit an edge? yes, then start autoscrolling.
					if (newMinX <= oViewPort.x) { // SCROLL LEFT
						timer = new java.util.Timer();

						sDirection = "LEFT";
						oXPos = parentPos.x;
						oYPos = parentPos.y;

						ScrollNodes task = new ScrollNodes();
						timer.schedule(task, new Date(), 10);
					}
					else if (newMinY <= oViewPort.y) { //SCROLL UP
						timer = new java.util.Timer();

						sDirection = "UP";
						oXPos = parentPos.x;
						oYPos = parentPos.y;

						ScrollNodes task = new ScrollNodes();
						timer.schedule(task, new Date(), 10);
					}
					else if (newMaxX >= oViewPort.x+pWidth) { //SCROLL RIGHT
						timer = new java.util.Timer();

						sDirection = "RIGHT";
						oXPos = parentPos.x;
						oYPos = parentPos.y;

						ScrollNodes task = new ScrollNodes();
						timer.schedule(task, new Date(), 10);
					}
					else if (newMaxY >= oViewPort.y+pHeight) { // DOWN
						timer = new java.util.Timer();

						sDirection = "DOWN";
						oXPos = parentPos.x;
						oYPos = parentPos.y;

						ScrollNodes task = new ScrollNodes();
						timer.schedule(task, new Date(), 10);
					}*/
				}
			}
			else if (isRightMouse) {
				//System.out.println("Is right mouse dragging");
				Point ptNew2 = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oNode.getViewPane());
				drawDummyLinks(ptNew2);
			}

			RepaintManager mgr = RepaintManager.currentManager(oViewPane);
			mgr.addDirtyRegion(oViewPane,0,0, oViewPane.getWidth(),oViewPane.getHeight());
			mgr.paintDirtyRegions();
		}
	}


	/**
	 * This inner class is used to perform the autoscrolling of nodes when they hit the edge of the view.
	 */
	private class ScrollNodes extends TimerTask {

		public ScrollNodes() {}

		public void run() {
			JViewport oViewPort = oViewPane.getViewFrame().getViewport();
			//Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, oStartingBounds.x, oStartingBounds.y, oViewPort);

			int x=0;
			int y=0;
			if (sDirection.equals("UP")) {
				y-=1;
			}
			else if (sDirection.equals("DOWN")) {
				y+=1;
			}
			else if (sDirection.equals("LEFT")) {
				x-=1;
			}
			else if (sDirection.equals("RIGHT")) {
				x+=1;
			}

			int oX = oXPos+x;
			int oY = oYPos+y;
			oXPos = oX;
			oYPos = oY;

			Point parentPos2 = SwingUtilities.convertPoint(oViewPort, oX, oY, (Component)oViewPane);

			oViewPort.scrollRectToVisible( new Rectangle( oX, oY, oStartingBounds.width, oStartingBounds.height ) );

			moveNodes(parentPos2.x, parentPos2.y);
		}
	}

	/**
	 * Move this node and any selected nodes byt the passed amounts
	 * @param diffX the amount to move the x position by.
	 * @param diffY the amount to move the y position by.
	 */
	private void moveNodes(int diffX, int diffY) {

		int newX = oStartingBounds.x + diffX;
		int newY = oStartingBounds.y + diffY;

		oNode.setBounds(newX, newY, oNode.getWidth(), oNode.getHeight());
		oNode.updateLinks();
		oStartingBounds = oNode.getBounds();

		// If this node is selected then move the other selected nodes as well
		// otherwise just move this node.
		if (oNode.isSelected()) {
			for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				NodeUI nodeui = (NodeUI)uinode.getUI();

				if(!nodeui.equals(this)) {
					Rectangle uinode_oStartingBounds = uinode.getBounds();
					int uinode_newX = uinode_oStartingBounds.x + diffX;
					int uinode_newY = uinode_oStartingBounds.y + diffY;
					uinode.setBounds(uinode_newX,
									 uinode_newY,
									 uinode.getWidth(),
									 uinode.getHeight());

					uinode.updateLinks();
				}
			}
		}
	}

	/**
	 * Draws dummy links displayed while dragging to create links between nodes
	 * @param Point fromPoint is the point to draw the dummy link to.
	 * This point is given in the system screen co-ordinates
	 */
	public void drawDummyLinks(Point toPoint) {

		Point ptNew = toPoint;

		oNode.setRollover(false);

		String os = ProjectCompendium.platform.toLowerCase();

		oViewPane = oNode.getViewPane();

		String sAuthor = oViewPane.getCurrentAuthor();

		///////////////MUTLIDRAG MOUSE RIGHT BUTTON (LINK)//////////////////////////

		//draw drag line which is a temp link
		if(oUIConnectingLines.isEmpty()) {

			for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				NodeUI nodeui = (NodeUI)uinode.getUI();

				Rectangle rFrom = oNode.getBounds();
				Point ptFromCenter = new Point(rFrom.x+(rFrom.width/2), rFrom.y+(rFrom.height/2));

				//create a dummy UINode
				Date date = new Date();
				UINode dummyToNode = new UINode(new NodePosition(oNode.getViewPane().getView(), oNode.getNode(),
						ptFromCenter.x, ptFromCenter.y, date, date), sAuthor);

				UILink link = new UILink(uinode, dummyToNode, UIUtilities.getLinkType(uinode));
				((UILine)link).setTo(ptNew);
				oNode.getViewPane().add(link, (UIViewPane.LINK_LAYER));
				link.setBounds(link.getPreferredBounds());
				oUIConnectingLines.addElement((Object)link);
			}
		}
		else {
			int count = oUIConnectingLines.size();
			for (int j=0; j<count; j++) {
				UILink link = (UILink)oUIConnectingLines.elementAt(j);
				((UILine)link).setTo(ptNew);
				link.setBounds(link.getPreferredBounds());
				link.repaint();
			}
		}

		///////////////SINGLEDRAG MOUSE RIGHT BUTTON (LINK)//////////////////////////

		//draw drag line which is a temp link
		if(oUIConnectingLine == null) {

			Rectangle rFrom = oNode.getBounds();
			Point ptFromCenter = new Point(rFrom.x+(rFrom.width/2), rFrom.y+(rFrom.height/2));

			//create a dummy UINode
			Date date = new Date();
			UINode dummyToNode = new UINode(new NodePosition(oNode.getViewPane().getView(), oNode.getNode(),
													ptFromCenter.x, ptFromCenter.y, date, date), sAuthor);

			oUIConnectingLine = new UILink(oNode, dummyToNode, UIUtilities.getLinkType(oNode));
			((UILine)oUIConnectingLine).setTo(ptNew);
			oNode.getViewPane().add(oUIConnectingLine, (UIViewPane.LINK_LAYER));
			oUIConnectingLine.setBounds(oUIConnectingLine.getPreferredBounds());
		}
		else {
			((UILine)oUIConnectingLine).setTo(ptNew);
			oUIConnectingLine.setBounds(oUIConnectingLine.getPreferredBounds());
			oUIConnectingLine.repaint();
		}

		// IF DRAGGING OF LINK IS LEAVING THE SCREEN - SCROLL IT
		JViewport viewport = oViewPane.getViewFrame().getViewport();
		Point nodePos = ptNew;
		Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, nodePos.x, nodePos.y, viewport);

		Rectangle parentBounds = new Rectangle(parentPos);
		viewport.scrollRectToVisible( new Rectangle( parentPos.x, parentPos.y, 5, 5 ) );
	}

	/**
	 * Clears any dummy links created while node linking is in transit.
	 */
	public void clearDummyLinks() {

		//reset the connecting line used in for linking
		if(oUIConnectingLine != null) {
			oNode.getViewPane().remove(oUIConnectingLine);
			oUIConnectingLine = null;
		}

		if(oUIConnectingLines.size() > 0) {
			int j=0;
			for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				uinode.getViewPane().remove((UILink)oUIConnectingLines.elementAt(j));
				j++;
			}
			oUIConnectingLines.removeAllElements();
		}

		for(Enumeration e = oNode.getViewPane().getSelectedNodes();e.hasMoreElements();) {
			UINode uinode = (UINode)e.nextElement();
			NodeUI nodeui = (NodeUI)uinode.getUI();
		}

		bDragging = false;
	}

	/**
	 * Invoked when a mouse is moved in a component.
	 * @param evt, the associated MouseEvent.
	 */
	public void mouseMoved(MouseEvent evt) {

		//System.out.println("In mouse moved");
		Point p = new Point(0, 0);

		if (oNode != null)
			p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oNode);
		else
			p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), null);

		updateMousePosition(p);

		int nX = evt.getX();
		int nY = evt.getY();

		oViewPane = oNode.getViewPane();
		if (oViewPane != null) {

			Point p2 = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oViewPane);

			if (APP_PROPERTIES.isImageRollover() &&
							hasIcon && iconRectangle != null && iconRectangle.contains(nX, nY) ) {

				NodeSummary node = oNode.getNode();

				boolean showImage = false;
				int nodeType = node.getType();

				if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {

					String img = node.getImage();
					if (img == null || img.equals("")) {
						String ref = node.getSource();
						if ( UIImages.isImage(ref) && oNode.hasImageBeenScaled())
							showImage = true;
					}
					else {
						if (oNode.hasImageBeenScaled()) {
							showImage = true;
						}
					}
				}
				else if (nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.MAP_SHORTCUT ||
						nodeType == ICoreConstants.LISTVIEW || nodeType == ICoreConstants.LIST_SHORTCUT) {

					String img = node.getImage();
					if (img != null && !img.equals("") && oNode.hasImageBeenScaled()) {
						showImage = true;
					}
				}

				if (showImage) {
					oViewPane.hideCodes();
					oViewPane.hideViews();
					oViewPane.hideDetail();

					oViewPane.showImage(oNode.getNode(), p2.x, p2.y);
				}
			}
			else {
				oViewPane.hideDetail();
			}

			if (oNode.getViewPane() instanceof UIAerialViewPane) return;  	// Skip the node decoration popups if in Aerial View

			if (hasText && textRectangle != null && textRectangle.contains(nX, nY) ) {
				//oNode.setToolTipText("Click to open node details");
				oViewPane.hideCodes();
				oViewPane.hideViews();
				oViewPane.hideImages();
				oViewPane.showDetail(oNode.getNode(), p2.x, p2.y);
			}
			else {
				oViewPane.hideDetail();
			}

			if (hasTrans && transRectangle != null && transRectangle.contains(nX, nY) ) {
				//oNode.setToolTipText("Number of transclusion. Click to open Views list");
				oViewPane.hideCodes();
				oViewPane.hideDetail();
				oViewPane.hideImages();
//				oViewPane.hideViews();
				oViewPane.showViews(oNode.getNode(), p2.x, p2.y);
			}
			else {
				//oViewPane.hideViews();
			}

			if (hasCodes && codeRectangle != null && codeRectangle.contains(nX, nY) ) {
				//oNode.setToolTipText("Click to open codes list");
				oViewPane.hideViews();
				oViewPane.hideDetail();
				oViewPane.hideImages();
				oViewPane.showCodes(oNode.getNode(), p2.x, p2.y);
			}
			else {
				oViewPane.hideCodes();
			}
		}
	}

// DND EVENTS

    /**
     * Called if the user has modified the current drop gesture.
     * <P>
	 * HERE THE METHOD DOES NOTHING.
     * @param e the <code>DropTargetDragEvent</code>
     */
 	public void dropActionChanged(DropTargetDragEvent evt) {}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     * <p>
	 * HERE THE METHOD DOES NOTHING.
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent evt) {
		if (oNode != null)
			oNode.setRollover(false);
	}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
	 * <p>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
 	public void dragOver(DropTargetDragEvent evt) {}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
 	 * <p>
	 * HERE THE METHOD DOES NOTHING.
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent evt) {
		if (oNode != null)
			oNode.setRollover(true);
	}

	/**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
	 * Invoked when a node is dragged and dropped over this component to link them.
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
	 * // METHOD NOT USED AT PRESENT
     */
	public void drop(DropTargetDropEvent evt) {

	    oViewPane = oNode.getViewPane();
	    boolean audioPlayed = false;

	    UINode from = null;
	    try {
			Transferable trans = evt.getTransferable();
			Object obj = trans.getTransferData(UINode.nodeFlavor);

			if (obj != null && (obj instanceof String)) {
			    String nodeID = (String)obj;
			    if (oViewPane != null) {
					Object obj2 = oViewPane.get(nodeID);
					if (obj2 instanceof UINode) {
				    	from = (UINode)obj2;
					}
		    	}
			}
		}
	    catch(IOException io) {
			io.printStackTrace();
	    }
	    catch(UnsupportedFlavorException io) {
			io.printStackTrace();
	    }

	    UINode to = oNode;

	    //if either node is null, or you are trying to link to yourself, return
	    if (from == null || to == null) {
			evt.getDropTargetContext().dropComplete(true);
			return;
	    }

	    //dont link with the trashbin
	    NodeSummary fromNode = from.getNode();
	    NodeSummary toNode = to.getNode();
	    String inbox = ProjectCompendium.APP.getInBoxID();

	    if( (fromNode == null || fromNode.getType() == ICoreConstants.TRASHBIN)
				|| (toNode ==null || toNode.getType() == ICoreConstants.TRASHBIN)
				|| (fromNode.getId()).equals(toNode.getId())
	    		|| (fromNode.getId().equals(inbox))
	    		|| (toNode.getId().equals(inbox))) {

			evt.getDropTargetContext().dropComplete(true);
			return;
	    }

	    int type = from.getType();
	    if (!from.containsLink(to)) {
			//create a link in the datamodel layer
			String linktype = UIUtilities.getLinkType(from);
			UILinkType oLinkType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(linktype);
			String sLabel = "";
			if (oLinkType != null)
				sLabel = oLinkType.getLabel();
			UILink uilink = createLink(from, to, linktype, sLabel, ICoreConstants.ARROW_TO);
	    }
	    else {
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.ABORT_ACTION);
			audioPlayed = true;
	    }

	    UINode nextFrom = oNode;
	    NodeSummary nextFromNode = null;

	    if (oViewPane != null) {

			// MULTISELECT Drop (if there has been multi-selection!)//
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {

			    nextFrom = (UINode)e.nextElement();

				if (nextFrom == null || nextFrom.getNode().getId().equals(toNode.getId()))
					continue;

			    nextFromNode = nextFrom.getNode();
			    if( nextFromNode == null || nextFromNode.getType() == ICoreConstants.TRASHBIN
			    		|| nextFromNode.getId().equals(inbox)) {
					continue;
			    }

				if (!nextFrom.equals(from)) {
					if (!nextFrom.containsLink(to)) {
						String linktype = UIUtilities.getLinkType(nextFrom);
						UILinkType oLinkType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(linktype);
						String sLabel = "";
						if (oLinkType != null)
							sLabel = oLinkType.getLabel();
					    UILink uilink = createLink(nextFrom, to, linktype, sLabel, ICoreConstants.ARROW_TO);
					}
				}
		    }
		}

	    if(!audioPlayed)
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.LINKING_ACTION);

	    evt.getDropTargetContext().dropComplete(true);
	}

	/**
	 * Invoked when a node is dragged and dropped over this component to link them,
	 * @param source com.compendium.ui.UINode, the source node for the drag.
	 */
	public void drop(UINode source) {

	    oViewPane = oNode.getViewPane();

	    boolean audioPlayed = false;
	    UINode from = source;
	    UINode to = oNode;

	    //if either node is null, or you are trying to link to yourself, return
	    if (from == null || to == null) {
			return;
	    }

	    //dont link with the trashbin
	    NodeSummary fromNode = from.getNode();
	    NodeSummary toNode = to.getNode();
	    String inbox = ProjectCompendium.APP.getInBoxID();

	    if( (fromNode == null || fromNode.getType() == ICoreConstants.TRASHBIN)
				|| (toNode == null || toNode.getType() == ICoreConstants.TRASHBIN)
				|| (fromNode.getId().equals(inbox))
				|| (toNode.getId().equals(inbox))
				|| (fromNode.getId()).equals(toNode.getId())) {
			return;
	    }

	    int type = from.getType();
	    if (!from.containsLink(to)) {
			//create a link in the datamodel layer
			String linktype = UIUtilities.getLinkType(from);
			UILinkType oLinkType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(linktype);
			String sLabel = "";
			if (oLinkType != null)
				sLabel = oLinkType.getLabel();
			UILink uilink = createLink(from, to, linktype, sLabel, ICoreConstants.ARROW_TO);
	    }
	    else {
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.ABORT_ACTION);
			audioPlayed = true;
	    }

	    UINode nextFrom = oNode;
	    NodeSummary nextFromNode = null;

	    if (oViewPane != null) {

			// MULTISELECT Drop (if there has been multi-selection!)//
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {

			    nextFrom = (UINode)e.nextElement();

			    if (nextFrom == null || nextFrom.getNode().getId().equals(toNode.getId()))
					continue;

		    	nextFromNode = nextFrom.getNode();
			    if( nextFromNode == null || nextFromNode.getType() == ICoreConstants.TRASHBIN ||
						oNode.getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			    	continue;
			    }

			    if (!nextFrom.equals(from)) {
					if (!nextFrom.containsLink(to)) {
						String linktype = UIUtilities.getLinkType(nextFrom);
						UILinkType oLinkType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(linktype);
						String sLabel = "";
						if (oLinkType != null)
							sLabel = oLinkType.getLabel();
					    UILink uilink = createLink(nextFrom, to, linktype, sLabel, ICoreConstants.ARROW_TO);
					}
			    }
			}
	    }

	    if(!audioPlayed)
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.LINKING_ACTION);
	}

	/**
	 * Add the passed string to the node label at the point of the current caret position.
	 * If the user set label length has been reached, and they have request it,
	 * open the NodeContentDialog and place the text in the details box.
	 *
	 * @param key, the string to add to the label.
	 */
	private void addCharToLabel(String key) {

		if (oNode.getNode().getType() != ICoreConstants.TRASHBIN &&
				!oNode.getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {

			String oldText = oNode.getText();

			if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
				String text = oldText.substring(0, startSelection) + oldText.substring(stopSelection);
				currentCaretPosition = startSelection;
				startSelection = -1;
				stopSelection = -1;
				oldText = text;
			}

			if(oldText.equals(ICoreConstants.NOLABEL_STRING)) {
				oldText = "";
				currentCaretPosition = 0;
			}

			String newText = "";

			//newText = oldText + key;
			if (currentCaretPosition < oldText.length())
				newText = oldText.substring(0, currentCaretPosition) + key + oldText.substring(currentCaretPosition);
			else
				newText = oldText + key;

			currentCaretPosition ++;

			Model oModel = (Model)ProjectCompendium.APP.getModel();
			boolean bDetailPopup = oModel.detailPopup;
			int nLabelPopupLength = oModel.labelPopupLength;

			if(bDetailPopup
					&& newText.length() >= nLabelPopupLength) {

				if ( openingDialog ) {
					dialogBuffer += key;
				}
				else if (editDialog != null && editDialog.isVisible()) {
					JTextArea textArea = editDialog.getDetailField();
					textArea.setText(textArea.getText()+key);
					textArea.setCaretPosition(textArea.getText().length());
				}
				else {
					// TIMING HEAR WAS REALLY IMPORTANT HENCE VARIOUS ODDITIES
					openingDialog = true;
					dialogBuffer += key;
					openEditDialog(true);
				}
				return;
			}

			previousString = oldText;
			oNode.setText(newText, true);		// mlb: Database deferred update
			ProjectCompendium.APP.setStatus(newText);

			if (APP_PROPERTIES.isAutoSearchLabel() && (!bDetailPopup ||
					bDetailPopup && newText.length() < nLabelPopupLength)) {

				if (oViewPane != null && oNode != null)
					oViewPane.showLabels(oNode, newText);
			}
		}
	}

	/**
	 * If editing, do a paste from the clipboard into the node label.
	 */
	public void paste() {

		if (oNode.getNode().getType() == ICoreConstants.TRASHBIN ||
				oNode.getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return;
		}

		if (editing) {
			String oldText = oNode.getText();
			previousString = oNode.getText();

			if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
				String text = oldText.substring(0, startSelection) + oldText.substring(stopSelection);
				currentCaretPosition = startSelection;
				startSelection = -1;
				stopSelection = -1;
				oldText = text;
			}

			if (clipboard == null)
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

           	Transferable clipData = clipboard.getContents(this);

		    String s="";
    		try {
         		s = (String)(clipData.getTransferData(DataFlavor.stringFlavor));
        	}
            catch (Exception ufe) {}

			if (oldText.equals(ICoreConstants.NOLABEL_STRING)) {
				currentCaretPosition = 0;
				oldText = "";
			}

			String text = oldText.substring(0, currentCaretPosition) + s + oldText.substring(currentCaretPosition);
    		currentCaretPosition+=s.length();
			oNode.setText(text, true);		// mlb: Database deferred update
			ProjectCompendium.APP.setStatus(text);

			if (APP_PROPERTIES.isAutoSearchLabel())
				oViewPane.showLabels(oNode, text);
		}
	}

	/**
	 * If editing, do a cut to the clipboard from the node label.
	 */
	public void cut() {

		if (oNode.getNode().getType() == ICoreConstants.TRASHBIN ||
				oNode.getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return;
		}

		if (editing) {
			String text = oNode.getText();

			if (startSelection < 0 || stopSelection < 0 || stopSelection > text.length())
				return;

			if (clipboard == null)
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

         	StringSelection data;
         	String selectedText = text.substring(startSelection, stopSelection);
         	data = new StringSelection(selectedText);
         	clipboard.setContents(data, data);

			previousString = text;
			text = text.substring(0, startSelection) + text.substring(stopSelection);
         	oNode.setText(text, true);		// mlb: Database deferred update

			currentCaretPosition = startSelection;
			startSelection = -1;
			stopSelection = -1;

			oNode.repaint();

			if (APP_PROPERTIES.isAutoSearchLabel())
				oViewPane.showLabels(oNode, text);
		}
	}

	/**
	 * If editing, delete selected text from the node label.
	 */
	public void delete() {

	    //TODO: Work on locking functions has been frozen. Finish or delete.
//		if (0 != oNode.getNode().getLockID()) {
//			ProjectCompendium.APP.displayError("Sorry, that node is locked. Please unlock it before continuing.");
//			return;
//		}

		if (oNode.getNode().getType() == ICoreConstants.TRASHBIN ||
				oNode.getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return;
		}

		if (editing) {
			if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
				String text = oNode.getText();
				previousString = text;

				text = text.substring(0, startSelection) + text.substring(stopSelection);
				currentCaretPosition = startSelection;
				startSelection = -1;
				stopSelection = -1;

				oNode.setText(text, true);		// mlb: Database deferred update
				oNode.repaint();
				ProjectCompendium.APP.setStatus(text);

				if (APP_PROPERTIES.isAutoSearchLabel())
					oViewPane.showLabels(oNode, text);
			}
			else {
				String text = oNode.getText();
				previousString = text;

				if (text.equals(ICoreConstants.NOLABEL_STRING)) {
					text = " ";
					currentCaretPosition = 0;
				}
				else if (currentCaretPosition >= 0 &&  currentCaretPosition < text.length() && text != null && !text.equals("")) {
					text = text.substring(0, currentCaretPosition) + text.substring(currentCaretPosition+1);
				}

				oNode.setText(text, true);		// mlb: Database deferred update
				oNode.repaint();
				ProjectCompendium.APP.setStatus(text);

				if (APP_PROPERTIES.isAutoSearchLabel())
					oViewPane.showLabels(oNode, text);
			}
		}
	}

	/**
	 * If editing, do a copy to the clipboard from the node label.
	 */
	public void copy() {
		if (editing) {
			String text = oNode.getText();

			if (startSelection < 0 || stopSelection < 0 || stopSelection > text.length())
				return;

			if (clipboard == null)
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

             	StringSelection data;
         	String selectedText = text.substring(startSelection, stopSelection);
         	data = new StringSelection(selectedText);
         	clipboard.setContents(data, data);
		}
	}

	/**
	 * If editing, select all the text in the node label.
	 */
	public void selectAll() {
		if (editing) {
			String temp = oNode.getText();
			startSelection = 0;
			stopSelection =temp.length();
			oNode.repaint();
		}
	}

	/**
	 * Invoked when a key is pressed in a component.
	 * @param evt, the associated KeyEvent.
	 */
	// MOVED ALL REGISTERED KEYBOARD ACTIONS DOWN HERE DUE TO INHERANT MEMORY LEAKS
	public void keyPressed(KeyEvent evt) {

		if (oNode.getViewPane() instanceof UIAerialViewPane) {	// Ignore key presses over nodes in Aerial View
			return;												// Someday do it right and apply scaling to everything that follows
		}

		if (oNode != null && !oNode.hasFocus()) {
			oNode.getViewPane().getUI().keyPressed(evt);
			return;
		}
		if (oViewPane == null)
			oViewPane = oNode.getViewPane();

		char keyChar = evt.getKeyChar();
		char[] key = {keyChar};
		sKeyPressed = new String(key);

		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		ptLocationKeyPress = new Point(__x, __y);

		// IF EDITING THE TEXT AREA
		if (editing) {
			if (modifiers == java.awt.Event.ALT_MASK) {
				switch(keyCode) {
					case KeyEvent.VK_T: {
						String today = (sdf.format(new Date())).toString();
						addCharToLabel(today+" ");
						currentCaretPosition += today.length()-1;
						evt.consume();
						break;
					}
					case KeyEvent.VK_O: {
						addCharToLabel("($Open) ");
						currentCaretPosition += 7;
						evt.consume();
						break;
					}
					case KeyEvent.VK_C: {
						addCharToLabel("($Closed) ");
						currentCaretPosition += 9;
						evt.consume();
						break;
					}
					case KeyEvent.VK_LEFT: {
						if (ProjectCompendium.isMac) { // ONLY ON THE MAC
							if (currentCaretPosition > 0) {
								String text = oNode.getText();
								String section = text.substring(0, currentCaretPosition);
								int index = section.lastIndexOf(" ");

								if (index == section.length()-1) {
									section = section.substring(0, section.length()-2);
									index = section.lastIndexOf(" ") - 1;
									if (index == -1) {
										currentCaretPosition = 0;
									}
									else {
										currentCaretPosition = currentCaretPosition - ( section.length() - index);
									}
								}
								else if (index == -1) {
									currentCaretPosition = 0;
								}
								else {
									currentCaretPosition = index - 1;
								}
							}

							oNode.repaint();
							evt.consume();
						}
						break;
					}
					case KeyEvent.VK_RIGHT: {
						if (ProjectCompendium.isMac) { // ONLY ON THE MAC

							String text = oNode.getText();
							if (currentCaretPosition < text.length()) {

								String section = text.substring(currentCaretPosition);
								int index = section.indexOf(" ");

								if (index == currentCaretPosition+1) {
									section = section.substring(1);
									index = section.indexOf(" ")+1;
								}

								if (index == -1)
									currentCaretPosition = text.length();
								else
									currentCaretPosition += index+1;
							}

							oNode.repaint();
							evt.consume();
						}
						break;
					}
					// NOW USED TO CREATE NEW NODE ON RIGHT OF CURRENT NODE LINKED BACK
					/*case KeyEvent.VK_R: {
						addCharToLabel("($Requirement) ");
						currentCaretPosition += 14;
						evt.consume();
						break;
					}*/
					/*case KeyEvent.VK_N: {
						if(sUser.equals(""))
							sUser =  ProjectCompendium.APP.getModel().getUserProfile().getUserName();

						addCharToLabel(sUser);
						currentCaretPosition += sUser.length()-1;
						evt.consume();
						break;
					}*/
					/*case KeyEvent.VK_A: {
						addCharToLabel("($Action Items) ");
						currentCaretPosition += 15;
						evt.consume();
						break;
					}*/
				}
			}
			else if (modifiers == shortcutKey) {
				switch(keyCode) {
					case KeyEvent.VK_X: { // CUT
						cut();
						evt.consume();
						break;
					}
					case KeyEvent.VK_C: { // COPY
						copy();
						evt.consume();
						break;
					}
					case KeyEvent.VK_V: { // PASTE INTO LABEL
						paste();
						evt.consume();
						break;
					}
					case KeyEvent.VK_Z: { // UNDO LABEL PASTE
						String temp = oNode.getText();
						oNode.setText(previousString, true);		// mlb: Database deferred update
						currentCaretPosition = previousString.length();

						if (APP_PROPERTIES.isAutoSearchLabel())
							oViewPane.showLabels(oNode, previousString);

						previousString = temp;
						evt.consume();
						break;
					}
					case KeyEvent.VK_A: { // SELECT ALL
						selectAll();
						evt.consume();
						break;
					}
					case KeyEvent.VK_LEFT: {
						if (!ProjectCompendium.isMac) { // ONLY ON WINDOWS/LINUX
							if (currentCaretPosition > 0) {
								String text = oNode.getText();
								int i = currentCaretPosition;
								while ((i > 0) && (text.charAt(i-1) == ' ')) i--;	// Move backwards over whitespace if any
								while ((i > 0) && (text.charAt(i-1) != ' ')) i--;	// Then move back until next whitespace found
								currentCaretPosition = i;
							}

							oNode.repaint();
							evt.consume();
						}
						break;
					}
					case KeyEvent.VK_RIGHT: {
						if (!ProjectCompendium.isMac) { // ONLY ON WINDOWS/LINUX

							String text = oNode.getText();
							if (currentCaretPosition < text.length()) {

								String section = text.substring(currentCaretPosition);
								int index = section.indexOf(" ");

								if (index == currentCaretPosition+1) {
									section = section.substring(1);
									index = section.indexOf(" ")+1;
								}

								if (index == -1)
									currentCaretPosition = text.length();
								else
									currentCaretPosition += index+1;
							}

							oNode.repaint();
							evt.consume();
						}
						break;
					}
				}
			}

			// FOLLOW SECTIONS FOR MIMICING TEXT FIELD BEHAVIOUR

			// MOVEMENT
			else if (keyCode == KeyEvent.VK_HOME && modifiers == 0) {
				currentCaretPosition=0;
				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_END && modifiers == 0) {
				String text = oNode.getText();
				currentCaretPosition=text.length();
				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_UP && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;
				caretUp = true;
				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_DOWN && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;
				caretDown = true;
				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;

				if (currentCaretPosition > 0)
					currentCaretPosition--;

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == 0) {
				startSelection = -1;
				stopSelection = -1;

				String text = oNode.getText();
				if (currentCaretPosition < text.length())
					currentCaretPosition++;
				oNode.repaint();
				evt.consume();
			}


			// SELECTION
			else if (keyCode == KeyEvent.VK_HOME && modifiers == Event.SHIFT_MASK) {
				if (startSelection == -1)
					startSelection = 0;

				if (stopSelection == -1)
					stopSelection = currentCaretPosition;

				currentCaretPosition=0;

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_END && modifiers == Event.SHIFT_MASK) {
				if (startSelection == -1)
					startSelection = currentCaretPosition;

				String text = oNode.getText();
				currentCaretPosition=text.length();
				stopSelection = currentCaretPosition;

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == Event.SHIFT_MASK) {

				if (stopSelection == -1)
					stopSelection = currentCaretPosition;

				if (currentCaretPosition > 0)
					currentCaretPosition--;

				if (startSelection == -1 || (startSelection < stopSelection && startSelection >= currentCaretPosition)) {
					startSelection = currentCaretPosition;
				}
				else
					stopSelection = currentCaretPosition;

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == Event.SHIFT_MASK) {

				if (startSelection == -1)
					startSelection = currentCaretPosition;

				String text = oNode.getText();
				if (currentCaretPosition < text.length())
					currentCaretPosition++;

				if (stopSelection == -1 || stopSelection < currentCaretPosition)
					stopSelection = currentCaretPosition;

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == Event.SHIFT_MASK+Event.CTRL_MASK
						&& !ProjectCompendium.isMac) {  // ON WINDOWS AND LINUX ONLY
				String text = oNode.getText();
				if (currentCaretPosition > 0) {
					if (stopSelection == -1)
						stopSelection = currentCaretPosition;

					int i = currentCaretPosition;
					while ((i > 0) && (text.charAt(i-1) == ' ')) i--;	// Move backwards over whitespace if any
					while ((i > 0) && (text.charAt(i-1) != ' ')) i--;	// Then move back until next whitespace found
					currentCaretPosition = i;
					if ((i < startSelection) || (startSelection == -1)) {
						startSelection = i;
					} else {
						stopSelection = i;  // Moving backwards over an existing selection, so this is an undo
					}
				}

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == Event.SHIFT_MASK+Event.CTRL_MASK
						&& !ProjectCompendium.isMac) {  // ON WINDOWS AND LINUX ONLY
				String text = oNode.getText();
				if (currentCaretPosition < text.length()) {
					if (startSelection == -1)
						startSelection = currentCaretPosition;

					int i = currentCaretPosition;
					while ((i < text.length()) && (text.charAt(i) != ' ')) i++;	// Move forward over printing chars
					while ((i < text.length()) && (text.charAt(i) == ' ')) i++;	// Then move fwd over any whitespace
					currentCaretPosition = i;
					if ((i > stopSelection) || (stopSelection == -1)) {
						stopSelection = i;
					} else {
						startSelection = i;	// Moving forwards over an existing selection, so this is an undo
					}

				}

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == Event.SHIFT_MASK+Event.ALT_MASK
						&& ProjectCompendium.isMac) {  // ON MAC ONLY
				if (currentCaretPosition > 0) {
					if (stopSelection == -1)
						stopSelection = currentCaretPosition;

					String text = oNode.getText();
					String section = text.substring(0, currentCaretPosition);
					int index = section.lastIndexOf(" ");

					if (index == section.length()-1) {
						section = section.substring(0, section.length()-2);
						index = section.lastIndexOf(" ") - 1;
						if (index == -1)
							currentCaretPosition = 0;
						else
							currentCaretPosition = currentCaretPosition - ( section.length() - index);
					}
					else if (index == -1)
						currentCaretPosition = 0;
					else
						currentCaretPosition = index - 1;

					if (startSelection == -1 || startSelection > currentCaretPosition)
						startSelection = currentCaretPosition;
				}

				oNode.repaint();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == Event.SHIFT_MASK+Event.ALT_MASK
						&& !ProjectCompendium.isMac) {  // ON MAC ONLY
				String text = oNode.getText();
				if (currentCaretPosition < text.length()) {
					if (startSelection == -1)
						startSelection = currentCaretPosition;

					String section = text.substring(currentCaretPosition);
					int index = section.indexOf(" ");

					if (index == currentCaretPosition+1) {
						section = section.substring(1);
						index = section.indexOf(" ")+1;
					}

					if (index == -1)
						currentCaretPosition = text.length();
					else
						currentCaretPosition += index+1;

					stopSelection = currentCaretPosition;
				}

				oNode.repaint();
				evt.consume();
			}

			// DELETING CHARS
			else if (keyCode == KeyEvent.VK_BACK_SPACE && ( modifiers == 0 || modifiers == Event.SHIFT_MASK)) {

				if (oNode.getNode().getType() != ICoreConstants.TRASHBIN &&
						!oNode.getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {

					if (startSelection > -1 && stopSelection > -1 && stopSelection > startSelection) {
						String text = oNode.getText();
						previousString = text;

						text = text.substring(0, startSelection) + text.substring(stopSelection);
						currentCaretPosition = startSelection;
						startSelection = -1;
						stopSelection = -1;

						oNode.setText(text, true);		// mlb: Database deferred update
						oNode.repaint();
						ProjectCompendium.APP.setStatus(text);

						if (APP_PROPERTIES.isAutoSearchLabel())
							oViewPane.showLabels(oNode, text);
					}
					else {
						String text = oNode.getText();
						previousString = text;

						if (text.equals(ICoreConstants.NOLABEL_STRING)) {
							text = " ";
							currentCaretPosition = 0;
						}
						else if (currentCaretPosition > 0 && text != null && !text.equals("")) {
							text = text.substring(0, currentCaretPosition-1) + text.substring(currentCaretPosition);
							currentCaretPosition--;
						}

						if (stopSelection > -1)
							stopSelection = currentCaretPosition;

						oNode.setText(text, true);		// mlb: Database deferred update
						oNode.repaint();
						ProjectCompendium.APP.setStatus(text);

						if (APP_PROPERTIES.isAutoSearchLabel())
							oViewPane.showLabels(oNode, text);
					}
				}
				evt.consume();
			}

			else if (keyCode == KeyEvent.VK_DELETE && modifiers == 0) {
				delete();
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
				oViewPane.hideLabels();
				evt.consume();
			}
			else if (keyCode == KeyEvent.CHAR_UNDEFINED && modifiers == 0) {
				addCharToLabel("undefined");
				currentCaretPosition += 8;
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_ESCAPE && modifiers == 0) {
				resetEditing();
				oViewPane.hideLabels();
				oNode.requestFocus();
				evt.consume();
			}

			// MOVED TO KEYTYPED TO PICK UP ACCENTED CHARACTER ETC WHICH ARE DOUBLE KEYSTOKES
			/*else if ( /*modifiers != java.awt.Event.ALT_MASK + java.awt.Event.SHIFT_MASK &&
					 modifiers != java.awt.Event.ALT_MASK &&*/
						/*( Character.isLetterOrDigit(keyChar) || sKeyPressed.equals(" ")  ||
							IUIConstants.NAVKEYCHARS.indexOf(sKeyPressed) != -1) ) {
				addCharToLabel(sKeyPressed);
				evt.consume();
			}*/
		}

		//*********** NONE EDITING VARIATIONS *************//
		else {
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
						if (oNode.isSelected())
							oNode.getViewPane().getUI().cutToClipboard(null);
						else
							oNode.getViewPane().getUI().cutToClipboard(oNode.getUI());

						evt.consume();
						break;
					}
					case KeyEvent.VK_C: { // COPY
						if (oNode.isSelected())
							oNode.getViewPane().getUI().copyToClipboard(null);
						else
							oNode.getViewPane().getUI().copyToClipboard(oNode.getUI());

						evt.consume();
						break;
					}
					case KeyEvent.VK_W: { // CLOSE WINDOW
						try {
							if (oNode.getViewPane().getView() != ProjectCompendium.APP.getHomeView() )
								oNode.getViewPane().getViewFrame().setClosed(true);
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
				}
			}
			else if ((keyCode == KeyEvent.VK_DELETE && modifiers == 0)
						|| (keyCode == KeyEvent.VK_BACK_SPACE && modifiers == 0)) {

				// delete node here if key pressed is delete
				UIViewFrame viewFrame = findViewFrame();

				oNode.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

				DeleteEdit edit = new DeleteEdit(viewFrame);

				if ((oViewPane.getNumberOfSelectedNodes() > 0) || (oViewPane.getNumberOfSelectedLinks() > 0) ) {

					// delete all the selected nodes if user MULTISELECTs
					if(oViewPane.getNumberOfSelectedNodes() >= 1) {
						oViewPane.deleteSelectedNodesAndLinks(edit);
					}
				}
				else {
					oViewPane.setSelectedNode(oNode, ICoreConstants.SINGLESELECT);
					oViewPane.deleteSelectedNodesAndLinks(edit);

					// delete node and corresponding links that are attached to the node.
					//deleteNodeAndLinks(oNode, edit);
				}
				// notify the listeners
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);

				//Thread thread = new Thread() {
				//	public void run() {
						ProjectCompendium.APP.setTrashBinIcon();
				//	}
				//};
				//thread.start();

				oNode.setCursor(Cursor.getDefaultCursor());
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_HOME && modifiers == 0) {
				oViewPane.getViewFrame().scrollHome(true);
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_UP && modifiers == 0) {
				if (oNode.hasFocus()) {
					oViewPane.moveUp(oNode);
				}
				else {
					oViewPane.getUI().moveCursorUp();
				}
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_DOWN && modifiers == 0) {
				if (oNode.hasFocus()) {
					oViewPane.moveDown(oNode);
				}
				else {
					oViewPane.getUI().moveCursorDown();
				}
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_LEFT && modifiers == 0) {
				if (oNode.hasFocus()) {
					oViewPane.moveLeft(oNode);
				}
				else {
					oViewPane.getUI().moveCursorLeft();
				}
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_RIGHT && modifiers == 0) {
				if (oNode.hasFocus()) {
					oViewPane.moveRight(oNode);
				}
				else {
					oViewPane.getUI().moveCursorRight();
				}
				evt.consume();
			}
			else if (keyCode == KeyEvent.VK_ESCAPE && modifiers == 0) {
				oViewPane = oNode.getViewPane();
				oViewPane.removeNode(oNode);
				oNode.setSelected(false);

				oViewPane.getParent().requestFocus();
				oViewPane.requestFocus();

				evt.consume();
			}
			else if (modifiers == java.awt.Event.ALT_MASK) {

				int nType = -1;

				if(keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_I || keyCode == KeyEvent.VK_A) {
					nType = ICoreConstants.POSITION;
				}
				else if (keyCode == KeyEvent.VK_Q) {
					nType = ICoreConstants.ISSUE;
				}
				else if(keyCode == KeyEvent.VK_U) {
					nType = ICoreConstants.ARGUMENT;
				}
				else if(keyCode == KeyEvent.VK_R ) {
					nType = ICoreConstants.REFERENCE;
				}
				else if(keyCode == KeyEvent.VK_D ) {
					nType = ICoreConstants.DECISION;
				}
				else if(keyCode == KeyEvent.VK_N) {
					nType = ICoreConstants.NOTE;
				}
				else if(keyCode == KeyEvent.VK_M) {
					nType = ICoreConstants.MAPVIEW;
				}
				else if(keyCode == KeyEvent.VK_L) {
					nType = ICoreConstants.LISTVIEW;
				}
				else if(keyCode == KeyEvent.VK_PLUS || keyCode == KeyEvent.VK_EQUALS) {
					nType = ICoreConstants.PRO;
				}
				else if(keyCode == KeyEvent.VK_MINUS) {
					nType = ICoreConstants.CON;
				}

				if (nType > -1) {
					evt.consume();

					// Fix for Mac Tiger bug
					final int fnType = nType;
					Thread thread = new Thread("") {
						public void run() {
							UINode node = UIUtilities.createNodeAndLinkRight(oNode, fnType, 100, "", ProjectCompendium.APP.getModel().getUserProfile().getUserName());
							node.getUI().setEditing();
							node.setText("");
						}
					};
					thread.start();
				}
			}
			else if (modifiers == (java.awt.Event.ALT_MASK+java.awt.Event.SHIFT_MASK)) {

				int nType = -1;

				if(keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_I || keyCode == KeyEvent.VK_A) {
					nType = ICoreConstants.POSITION;
				}
				else if (keyCode == KeyEvent.VK_Q) {
					nType = ICoreConstants.ISSUE;
				}
				else if(keyCode == KeyEvent.VK_U) {
					nType = ICoreConstants.ARGUMENT;
				}
				else if(keyCode == KeyEvent.VK_R ) {
					nType = ICoreConstants.REFERENCE;
				}
				else if(keyCode == KeyEvent.VK_D ) {
					nType = ICoreConstants.DECISION;
				}
				else if(keyCode == KeyEvent.VK_N) {
					nType = ICoreConstants.NOTE;
				}
				else if(keyCode == KeyEvent.VK_M) {
					nType = ICoreConstants.MAPVIEW;
				}
				else if(keyCode == KeyEvent.VK_L) {
					nType = ICoreConstants.LISTVIEW;
				}
				else if(keyCode == KeyEvent.VK_PLUS || keyCode == KeyEvent.VK_EQUALS) {
					nType = ICoreConstants.PRO;
				}
				else if(keyCode == KeyEvent.VK_MINUS) {
					nType = ICoreConstants.CON;
				}

				if (nType > -1) {
					oNode.setType(nType, true, currentCaretPosition);
					evt.consume();
				}
			}
			else if (keyCode == KeyEvent.VK_ESCAPE && modifiers == 0) {

				oViewPane = oNode.getViewPane();
				oViewPane.removeNode(oNode);
				oNode.setSelected(false);

				oViewPane.getParent().requestFocus();
				oViewPane.requestFocus();
				evt.consume();
			}
		}

		//******** GENERIC KEY PRESSES ***********//

		if (modifiers == java.awt.Event.ALT_MASK && keyCode == KeyEvent.VK_H) {
			releaseFocusAndRollover();

			String keyhelpinfo = "<alt-v> - To invoke Containing Views Dialog \n";
			keyhelpinfo += "<alt-x> - To invoke Tags Dialog \n";
			keyhelpinfo += "<alt-t> - To add Today's Date to end of label \n";
			keyhelpinfo += "<alt-o> - To add \"Open\" to end of label \n";
			keyhelpinfo += "<alt-c> - To add \"Close\" to end of label \n";

   	        JOptionPane oOptionPane = new JOptionPane(keyhelpinfo);
			JDialog oDialog = oOptionPane.createDialog(ProjectCompendium.APP.getContentPane(),"Node Keyword Help..");
			oDialog.setModal(false);
			oDialog.setVisible(true);

			evt.consume();
		}
		else if (modifiers == java.awt.Event.ALT_MASK && keyCode == KeyEvent.VK_V) {
			oNode.showViewsDialog();
			evt.consume();
		}
//		else if (modifiers == java.awt.Event.ALT_MASK && keyCode == KeyEvent.VK_W) {
//			String nodeId = oNode.getNode().getId();
//			UIReadersDialog readers = new UIReadersDialog(ProjectCompendium.APP, nodeId);
//			UIUtilities.centerComponent(readers, ProjectCompendium.APP);
//			readers.setVisible(true);
//			evt.consume();
//		}
		else if (modifiers == java.awt.Event.ALT_MASK && keyCode == KeyEvent.VK_X) {
			releaseFocusAndRollover();
			ProjectCompendium.APP.onCodes();
			evt.consume();
		}
		else if (modifiers == shortcutKey && keyCode == KeyEvent.VK_P) {
			oNode.getViewPane().getUI().pasteFromClipboard();
			evt.consume();
		}
		else if (modifiers == java.awt.Event.CTRL_MASK) {
			switch(keyCode) {
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
						if (oNode.getViewPane().getView() != ProjectCompendium.APP.getHomeView() )
							oNode.getViewPane().getViewFrame().setClosed(true);
					}
					catch(Exception e) {}
					evt.consume();
					break;
				}
			}
		}
		else if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
			openNode();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_INSERT && modifiers == 0) {
			releaseFocusAndRollover();
			openEditDialog(false);
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
		else if (keyCode == KeyEvent.VK_F12 && modifiers == 0) {
			oViewPane.markSelectionSeen();		// Might have a selection but gethere from user Shift-clicking nodes
			evt.consume();
		} else if (keyCode == KeyEvent.VK_F12 && modifiers == Event.SHIFT_MASK) {
			oViewPane.markSelectionUnseen();	// Might have a selection but gethere from user Shift-clicking nodes
			evt.consume();
		}
	}

	/**
	 * Invoked when a key is released in a component.
	 * @param e, the associated KeyEvent.
	 */
  	public void keyReleased(KeyEvent e) {}

	/**
	 * Invoked when a key is typed in a component.
	 * @param e, the associated KeyEvent.
	 */
	// NEED THIS HEAR TO PICK UP KEYCHARS FROM MULTIPLE KEYS LIKE ACCENTED LETTER ON FOREIGHN KEYBOARDS.
	public void keyTyped(KeyEvent evt) {

		if (oNode != null && !oNode.hasFocus()) {
			return;
		}

		// IF EDITING THE TEXT AREA
		if (editing) {
			char keyChar = evt.getKeyChar();
			char[] key = {keyChar};
			sKeyPressed = new String(key);
			int modifiers = evt.getModifiers();

			if (ProjectCompendium.isMac && modifiers == shortcutKey) {
				evt.consume();
			} else {
				if (( Character.isLetterOrDigit(keyChar) || sKeyPressed.equals(" ")  ||
							IUIConstants.NAVKEYCHARS.indexOf(sKeyPressed) != -1) ) {
					addCharToLabel(sKeyPressed);
					evt.consume();
				}
			}
		}
	}

	/**
	 * Find the viewframe for this node. If the node does not know it get the current frame.
	 * This should not be necessary but fixes an occassional bug.
	 * This method sets the variable 'oViewPane', as well as returning the frame.
	 */
	private UIViewFrame findViewFrame() {
		UIViewFrame viewFrame = null;
		if (oNode == null) {
			viewFrame = ProjectCompendium.APP.getCurrentFrame();
			oViewPane = ((UIMapViewFrame)viewFrame).getViewPane();
		}
		else {
			oViewPane = oNode.getViewPane();
			if (oViewPane == null) {
				viewFrame = ProjectCompendium.APP.getCurrentFrame();
				oViewPane = ((UIMapViewFrame)viewFrame).getViewPane();
			}
			else
				viewFrame = oViewPane.getViewFrame();
		}

		return viewFrame;
	}

	/**
	 * Handles a property chagen to the UINode.
	 * @param evt, the associated PropertyChagenEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

 		String prop = evt.getPropertyName();

		if (prop.equals(UINode.TEXT_PROPERTY) || prop.equals(UINode.ICON_PROPERTY)) {
			refreshBounds();
		}
		else if (prop.equals(UINode.TYPE_PROPERTY)) {
			refreshBounds();
		}
		else if (prop.equals(UINode.CHILDREN_PROPERTY)) {
			//refreshBounds();
		}

		else if (prop.equals(NodeSummary.STATE_PROPERTY)) {
			refreshBounds();
		}

		// UPDATE NODE INDICATORS
		else if (prop.equals(NodeSummary.DETAIL_PROPERTY)) {
			refreshBounds();
		}
		else if (prop.equals(NodeSummary.TAG_PROPERTY)) {
			refreshBounds();
		}
		else if (prop.equals(NodeSummary.VIEW_NUM_PROPERTY)) {
			//refreshBounds();
		}
	}

	/**
	 * Refresh the bounds of this object.
	 * Calls <code>getPreferredSize</code>,
	 * which is important for the paint method to work correctly later.#
	 * @see #getPreferredSize
	 */
	public void refreshBounds() {
		if (oNode == null) {
			return;
		}

		Rectangle r = oNode.getBounds();

		int prefX = r.x;
		int prefY = r.y;

		Dimension d = getPreferredSize(oNode);

		// ADJUST X TO KEEP CENTER POINT THE SAME
		int oldCenter = r.x+(r.width/2);
		if (d.width > r.width)
			prefX = r.x - ((d.width - r.width)/2);
		else if (d.width < r.width)
			prefX = r.x + ((r.width - d.width)/2);
		int newCenter = prefX+(d.width/2);

		// ADJUST FOR SLIPPAGE DUE TO ROUNDING OF NUMBERS
		if (newCenter > oldCenter)
			prefX -= 1;
		else if (newCenter < oldCenter)
			prefX += 1;

		oNode.setBounds(prefX, prefY, d.width, d.height);
	}

	/**
	 * This border class paints the border for this node.
	 */
	private class NodeBorder extends AbstractBorder {

		public void paintBorder (Component c, Graphics g, int x, int y, int width, int height)
		{
			//System.out.println("4533 NodeUI entered paint Border");

			boolean	isHot = false;
			UINode node = null;

			if (c instanceof UINode)
			{
				node = (UINode)c;
				isHot = node.isRollover();
			}
			int type = node.getNode().getType();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			boolean bMapBorder = oModel.mapBorder;

			//if (node.getNode().getId().equals(ProjectCompendium.APP.getInBoxID()))
			//{
			//	System.out.println("4546 NodeUI found Inbox");
			//}

			//System.out.println("4544 NodeUI node.getNode().getId() is " + node.getNode().getId());
			//System.out.println("4545 NodeUI ProjectCompendium.APP.getInBoxID() is " + ProjectCompendium.APP.getInBoxID());

			if (node.hasFocus() && !editing)
			{
				//System.out.println("4557 NodeUI");
				if ((type == ICoreConstants.MAPVIEW ||
					type == ICoreConstants.LISTVIEW ||
					type == ICoreConstants.MAP_SHORTCUT ||
					type == ICoreConstants.LIST_SHORTCUT) &&
					node.getNode().getImage() != null && !node.getNode().getImage().equals("")) {

					if (bMapBorder) {
	           			paintRaisedBevel(c, g, x, y, width, height, FOCUSED_COLOR);
					}
					else {
						Color oldColor = g.getColor();
						g.setColor(FOCUSED_COLOR);
						g.draw3DRect(x, y, width-1, height-1, true);
						g.setColor(oldColor);
					}
				}
				else {
					Color oldColor = g.getColor();
					g.setColor(FOCUSED_COLOR);
					g.draw3DRect(x, y, width-1, height-1, true);
					g.setColor(oldColor);
				}
			}
			else if (isHot && !editing)
			{
				//System.out.println("4583 NodeUI");
				if ((type == ICoreConstants.MAPVIEW ||
					type == ICoreConstants.LISTVIEW ||
					type == ICoreConstants.MAP_SHORTCUT ||
					type == ICoreConstants.LIST_SHORTCUT) &&
					node.getNode().getImage() != null && !node.getNode().getImage().equals("")) {

					if (bMapBorder) {
	          			paintRaisedBevel(c, g, x, y, width, height, BORDER_COLOR);
					}
					else {
						Color oldColor = g.getColor();
						g.setColor(BORDER_COLOR);
						g.draw3DRect(x, y, width-1, height-1, true);
						g.setColor(oldColor);
					}
 				}
				else {
					Color oldColor = g.getColor();
					g.setColor(BORDER_COLOR);
					g.draw3DRect(x, y, width-1, height-1, true);
					g.setColor(oldColor);
				}
			}
			else if (node != null)
			{
				//System.out.println("4609 NodeUI");
				boolean bShowMapBorder = false;
				if ((type == ICoreConstants.MAPVIEW ||
						type == ICoreConstants.LISTVIEW ||
						type == ICoreConstants.MAP_SHORTCUT ||
						type == ICoreConstants.LIST_SHORTCUT) &&
						node.getNode().getImage() != null && !node.getNode().getImage().equals("")) {

					bShowMapBorder = true;
				}

				int state = node.getNode().getState();
				if (state == ICoreConstants.UNREADSTATE) {
					if (bShowMapBorder) {
						if (bMapBorder) {
							paintRaisedBevel(c, g, x, y, width, height, IUIConstants.UNREAD_BORDER_COLOR);
						}
					}
					else {
						Color oldColor = g.getColor();
						g.setColor(IUIConstants.UNREAD_BORDER_COLOR);
						g.draw3DRect(x, y, width-1, height-1, true);
						g.setColor(oldColor);
					}
				}
				else if (state == ICoreConstants.MODIFIEDSTATE)
				{
					//System.out.println("4636 NodeUI");

					//System.out.println("4638 NodeUI bShowMapBorder is " + bShowMapBorder);
					//System.out.println("4638 NodeUI bMapBorder is " + bMapBorder);

					if (bShowMapBorder) {
						//if (bMapBorder) {
							//This is the initial paint green border on project open
							//System.out.println("4622 NodeUI paint raised bevel Modified Border Color green");
							paintRaisedBevel(c, g, x, y, width, height, IUIConstants.MODIFIED_BORDER_COLOR);
						//}
					}
					else
					{
						//System.out.println("4647 NodeUI");

						Color oldColor = g.getColor();
						//System.out.println("4628 NodeUI setColor Modified Border Color green");
						g.setColor(IUIConstants.MODIFIED_BORDER_COLOR);
						g.draw3DRect(x, y, width-1, height-1, true);
						g.setColor(oldColor);
					}
				}
				else
				{
					//System.out.println("4658 NodeUI");

					if (node.getNode().getId().equals(ProjectCompendium.APP.getInBoxID()))
					{
						//System.out.println("4662 NodeUI");
						bMapBorder = false;
					}
					if (bShowMapBorder)
					{
						//System.out.println("4667 NodeUI");
						if (bMapBorder)
						{
							//System.out.println("4670 NodeUI");
							paintRaisedBevel(c, g, x, y, width, height, IMAGEMAP_COLOR);
						}
					}
				}
			}

			//System.out.println("4564 NodeUI exiting paint Border");

		}

	   	protected void paintRaisedBevel(Component c, Graphics g, int x, int y,
	                                    int width, int height, Color color)  {
	        Color oldColor = g.getColor();
	        int h = height;
	        int w = width;

	        g.translate(x, y);

	        g.setColor(color.brighter().brighter().brighter());
	        g.drawLine(0, 0, 0, h-2);
	        g.drawLine(1, 0, w-2, 0);

	        g.setColor(color.brighter().brighter());
	        g.drawLine(1, 1, 1, h-3);
	        g.drawLine(2, 1, w-3, 1);

	        g.setColor(color.darker().darker().darker());
	        g.drawLine(0, h-1, w-1, h-1);
	        g.drawLine(w-1, 0, w-1, h-2);

	        g.setColor(color.darker());
	        g.drawLine(1, h-2, w-2, h-2);
	        g.drawLine(w-2, 1, w-2, h-3);

	        g.translate(-x, -y);
	        g.setColor(oldColor);

	    }

	    protected void paintLoweredBevel(Component c, Graphics g, int x, int y,
	                                        int width, int height, Color color)  {
	        Color oldColor = g.getColor();
	        int h = height;
	        int w = width;

	        g.translate(x, y);

	        g.setColor(color.darker());
	        g.drawLine(0, 0, 0, h-1);
	        g.drawLine(1, 0, w-1, 0);

	        g.setColor(color.darker().darker().darker());
	        g.drawLine(1, 1, 1, h-2);
	        g.drawLine(2, 1, w-2, 1);

	        g.setColor(color.brighter().brighter().brighter());
	        g.drawLine(1, h-1, w-1, h-1);
	        g.drawLine(w-1, 1, w-1, h-2);

	        g.setColor(color.brighter().brighter());
	        g.drawLine(2, h-2, w-2, h-2);
	        g.drawLine(w-2, 2, w-2, h-3);

	        g.translate(-x, -y);
	        g.setColor(oldColor);

	    }

		public Insets getBorderInsets(Component c) {
			int type = oNode.getNode().getType();

			if ((type == ICoreConstants.MAPVIEW ||
				type == ICoreConstants.LISTVIEW ||
				type == ICoreConstants.MAP_SHORTCUT ||
				type == ICoreConstants.LIST_SHORTCUT) ) {

				Model oModel = (Model)ProjectCompendium.APP.getModel();
				boolean bMapBorder = oModel.mapBorder;
				if (bMapBorder)
					return new Insets(2,2,2,2);
				else
					return new Insets(1, 1, 1, 1);
			}
			else
				return new Insets(1, 1, 1, 1);
		}
	}

	/**
	 * If this node has the focus pass it to its parent, and set the rollover off.
	 */
  	private void releaseFocusAndRollover() {

		if (oNode.hasFocus()) {
			oNode.setRollover(false);
			oNode.getParent().requestFocus();
		}
  	}

	/**
	 * Delete all the links attached to the given node.
	 * @param node com.compendium.ui.UINode, the node to delete the links for.
	 * @param edit, the object to add deleted links to ofr potential undo/redo operations.
	 */
  	public void deleteLinksforNode(UINode node, PCEdit edit) {

 		//remove all the ui links that connect to this node
		for(Enumeration e = node.getLinks();e.hasMoreElements();) {

			UILink uilink = (UILink)e.nextElement();
			LinkUI linkui = (LinkUI)uilink.getUI();
			linkui.deleteLink(uilink);

			// save link in case operation needs to be undone.
			if (edit != null) {
				edit.AddLinkToEdit (uilink);
			}
		}
  	}

  	/**
     * Delete the given node and its links.
	 * @param node com.compendium.ui.UINode, the node to delete.
	 * @param edit the object to add deleted nodes to for potential undo/redo operations.
	 * @return true if the node was the last instalce and has been totatlly deleted, else false.
     */
 	public boolean deleteNodeAndLinks(UINode node, PCEdit edit) {

		boolean bDeleted = false;

        //TODO: Work on locking functions has been frozen. Finish or delete.
//		if (0 != node.getNode().getLockID()) {
//			ProjectCompendium.APP.displayError("Sorry, that node is locked. Please unlock it before continuing.");
//			return false;
//		}

		deleteLinksforNode(node, edit);
		bDeleted = deleteNode(node);

		if (edit != null) {
			edit.AddNodeToEdit (node);
		}

		return (bDeleted);
 	}
  	/**
     * Delete the given node from the database and the parent view.
	 * @param node com.compendium.ui.UINode, the node to delete.
     */
  	private boolean deleteNode(UINode node) {

//  	if (0 != node.getNode().getLockID()) {
//			ProjectCompendium.APP.displayError("Sorry, that node is locked. Please unlock it before continuing.");
//			return false;
//		}

		ProjectCompendium.APP.setStatus("Deleting " + node.getNode().getLabel());

		boolean nodeDeletedFromDB = removeFromDatamodel(node);
		if (nodeDeletedFromDB) {
			ProjectCompendium.APP.setTrashBinIcon();
		}

		removeFromUI(node);
		ProjectCompendium.APP.setStatus("");

		return nodeDeletedFromDB;
  	}

  	/**
     * Marks the given node as deleted in the database.
	 * @param node the node to delete.
	 * @return true if the node was the last instance nad has been totally deleted, else false.
     */
	public boolean removeFromDatamodel(UINode node) {

	    //dont delete the trashbin
	    NodeSummary oNode = node.getNode();

	    //TODO: Work on locking functions has been frozen. Finish or delete.
//	    if (0 != node.getNode().getLockID()) {
//			ProjectCompendium.APP.displayError("Sorry, that node is locked. Please unlock it before continuing.");
//			return false;
//		}

		if((oNode.getType() == ICoreConstants.TRASHBIN) ||
				oNode.getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return 	false;
		}

		boolean nodeDeletedFromDB = false;

		oViewPane = node.getViewPane();
		try {
			nodeDeletedFromDB = oViewPane.getView().removeMemberNode(node.getNode());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error "+ex.getMessage());
			return false;
		}

		if (nodeDeletedFromDB) {
			// IF THIS WAS A VIEW NODE LET THE HISTORY KNOW TO REMOVE IT
			if (oNode instanceof View) {
				View view = (View) oNode;
				ProjectCompendium.APP.removeViewFromHistory(view);
			}
		}

		if(oNode.getViewCount() > 0 ) {
            return false;
        } else {
            return true;
        }
	}

  	/**
     * Delete the given node from the ui layer.
	 * @param node com.compendium.ui.UINode, the node to delete.
     */
	public void removeFromUI(UINode node) {
	    //TODO: Work on locking functions has been frozen. Finish or delete.
//		if (0 != node.getNode().getLockID()) {
//			ProjectCompendium.APP.displayError("Sorry, that node is locked. Please unlock it before continuing.");
//			return;
//		}

		// Lakshmi - 5/25/06
		if(oViewPane == null)
			 oViewPane = node.getViewPane();

		//remove node from the UIviewpane layer
		oViewPane.remove(node);

		// paint the new updated layered pane
		oViewPane.paintImmediately(node.getBounds());

		//finalize delete in the model
		//oViewPane.getView().getModel().removeObject(node.getNode());

		//clear rollover image or any hints
		if (oViewPane != null) {
			oViewPane.hideCodes();
			oViewPane.hideDetail();
			if (APP_PROPERTIES.isImageRollover())
				oViewPane.hideImages();
		}

		//IF NODE IS A VIEW, REMOVE IT FROM DESKTOP IF OPEN
		if(node.getNode() instanceof View) {
			ProjectCompendium.APP.removeView((View)node.getNode());
		}

		// the viewpane gets the focus
		oViewPane.requestFocus();
	}

  	/**
   	 * This method Delinks a Node from all its links.
   	 */
  	public void delink() {

		//remove all the ui links that connect to this node
		for(Enumeration e = oNode.getLinks();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			LinkUI linkui = (LinkUI)link.getUI();
			linkui.deleteLink(link);
		}

		//make sure all the links are removed
		oNode.removeAllLinks();

		ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.DELINKING_ACTION);
  	}

  	/**
     * Convenience method to get the UINode by the popupmenu and other gui operations.
	 * @return com.compendium.ui.UINode, the UINode associated with this NodeUI instance.
     */
  	public UINode getUINode() {
	  	return oNode;
  	}

  	/**
     * Creates a link.
	 * @param uifrom com.compendium.ui.UINode, the originating node for the link to create.
	 * @param uito com.compendium.ui.UINode, the destination node for the link to create.
	 * @param type, the type of link to create.
	 * @param arrow, the type of arrow heads to draw.
	 * @return com.compendium.ui.UILink, the newly created link.
	 * @see com.compendium.core.ICoreConstants
     */
  	public UILink createLink(UINode uifrom, UINode uito, String type, int arrow) {

		return createLink(uifrom, uito, type, "", arrow);
  	}

  	/**
     * Creates a link.
	 * @param uifrom com.compendium.ui.UINode, the originating node for the link to create.
	 * @param uito com.compendium.ui.UINode, the destination node for the link to create.
	 * @param type, the type of link to create.
	 * @param sLabel, the labe for this node.
	 * @param arrow, the type of arrow heads to draw.
	 * @return com.compendium.ui.UILink, the newly created link.
	 * @see com.compendium.core.ICoreConstants
     */
  	public UILink createLink(UINode uifrom, UINode uito, String type, String sLabel, int arrow) {

		oViewPane = oNode.getViewPane();
		NodeSummary from = uifrom.getNode();
		NodeSummary to	= uito.getNode();

		if (oViewPane == null || from == null || to == null)
			return null;

		View view = oViewPane.getView();
		if (view == null)
			return null;

		int permission = ICoreConstants.WRITE;

		String sOriginalID = "";

		Link link = null;
		try {
			//add the link to the datamodel view
			link = (Link)view.addMemberLink(type,
											"", //ImportedID
											sOriginalID,
											ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
											from,
											to,
											sLabel,
											arrow);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (NodeUI.createLink) Unable to create link\n\n"+ex.getMessage());
			return null;
		}

		link.initialize(view.getModel().getSession(), view.getModel());

		//create a link in UI layer - NOW DONE BY PROPERTY CHANGE EVENT
		UILink uilink = (UILink)oViewPane.get(link.getId());
		if (uilink == null) {
			uilink = new UILink(link, uifrom, uito);

			double currentScale = oViewPane.getZoom();
			AffineTransform trans=new AffineTransform();
			trans.setToScale(currentScale, currentScale);
			uilink.scaleArrow(trans);

			uito.getViewPane().add(uilink, (UIViewPane.LINK_LAYER));
			uilink.setBounds(uilink.getPreferredBounds());
			uifrom.addLink(uilink);
			uito.addLink(uilink);
		}
		return uilink;
  	}

  	/**
     * Creates a link.
	 * @param sLinkID, the id to give this link.
	 * @param uifrom com.compendium.ui.UINode, the originating node for the link to create.
	 * @param uito com.compendium.ui.UINode, the destination node for the link to create.
	 * @param type, the type of link to create.
	 * @param sLabel, the labe for this node.
	 * @param arrow, the type of arrow heads to draw.
	 * @return com.compendium.ui.UILink, the newly created link.
	 * @see com.compendium.core.ICoreConstants
     */
  	public UILink createLink(String sImportedID, UINode uifrom, UINode uito, String type, String sLabel, int arrow) {

 		oViewPane = oNode.getViewPane();
		NodeSummary from = uifrom.getNode();
		NodeSummary to	= uito.getNode();

		if (oViewPane == null || from == null || to == null)
			return null;

		View view = oViewPane.getView();
		if (view == null)
			return null;

		int permission = ICoreConstants.WRITE;

		String sOriginalID = "";

		Link link = null;
		try {
			//add the link to the datamodel view
			link = (Link)view.addMemberLink(type,
											sImportedID,
											sOriginalID,
											ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
											from,
											to,
											sLabel,
											arrow);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (NodeUI.createLink) Unable to create link\n\n"+ex.getMessage());
			return null;
		}

		link.initialize(view.getModel().getSession(), view.getModel());

		//create a link in UI layer - NOW DONE BY PROPERTY CHANGE EVENT
		UILink uilink = (UILink)oViewPane.get(link.getId());
		if (uilink == null) {
			uilink = new UILink(link, uifrom, uito);

			double currentScale = oViewPane.getZoom();
			AffineTransform trans=new AffineTransform();
			trans.setToScale(currentScale, currentScale);
			uilink.scaleArrow(trans);

			uito.getViewPane().add(uilink, (UIViewPane.LINK_LAYER));
			uilink.setBounds(uilink.getPreferredBounds());
			uifrom.addLink(uilink);
			uito.addLink(uilink);
		}
		return uilink;
  	}

	/**
	 * Open the node contents dialog with the details tab showing.
	 * @param fromKeyEvent, indicates if this method was called from a key event.
	 */
	public void openEditDialog(boolean fromKeyEvent) {

		final boolean isFromKeyEvent = fromKeyEvent;
		Thread thread = new Thread("NodeUI: EditDialog") {
			public void run() {
				editDialog = oNode.showEditDialog();
				if (isFromKeyEvent) {
					JTextArea textArea = editDialog.getDetailField();
					textArea.setText(textArea.getText()+dialogBuffer);
					dialogBuffer = "";
					textArea.setCaretPosition(textArea.getText().length());

					openingDialog = false;
				}
			}
		};
		thread.start();
		Thread.currentThread().yield();
	}
}
