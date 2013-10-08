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

package com.compendium.ui.dialogs;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Container;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;


/**
 * This class holds the Aerial Map view.
 *
 * @author	Michelle Bachler
 */

public class UIAerialDialog extends UIDialog implements ComponentListener {

	/** The starting width for this dialog.*/
	private int					nAerialWidth 		= 200;

	/** The starting height for this dialog.*/
	private int					nAerialHeight 		= 200;

	/** The parent frame for this dialog.*/
	private JFrame				oParent 			= null;

	/** The UIMapViewFrame that this is the aerial view for.*/
	private UIMapViewFrame		oMapFrame			= null;

	/** The view the aerial view is of.*/
	private View				oView				= null;

	/** The UIAerialViewPane instance that draws the aerial view held in this dialog.*/
	private UIAerialViewPane	oAerialViewPane 	= null;

	/** The pane to add content to for this dialog.*/
	private Container			oContentPane		= null;

	/** The main panel holding the UIAerialViewPane instance.*/
	private JPanel				oAerialPanel		= null;


	/**
	 * Constructor.
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param frame, the object instance that draws the aerial view held in this dialog.
	 * @param view, the view the aerial view is of.
	 * @param dialogBounds, the size to draw the dialog at. If null use the defaults.
	 */
	public UIAerialDialog(JFrame parent, UIMapViewFrame frame, View view, Rectangle dialogBounds) {

		super(parent, false);

		oParent = parent;
		oMapFrame = frame;
		oView = view;
		String userID = ProjectCompendium.APP.getModel().getUserProfile().getId() ;

		this.addComponentListener(this);
		setTitle("[Aerial]: "+oView.getLabel());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		oAerialPanel = new JPanel();
		oAerialPanel.setVisible(true);
		oAerialPanel.setLayout(new BorderLayout());
//		oAerialPanel.setBorder(new EmptyBorder(5,5,5,5));
		oAerialPanel.setBackground(Color.white);
		oAerialPanel.setOpaque(true);

		oAerialViewPane = new UIAerialViewPane(oView, frame);
		oAerialViewPane.setBackground(Color.white);
		oAerialPanel.add(oAerialViewPane, BorderLayout.CENTER);

		if (dialogBounds != null) {
			oAerialPanel.setPreferredSize(new Dimension(dialogBounds.width, dialogBounds.height));
			oAerialPanel.setSize(new Dimension(dialogBounds.width, dialogBounds.height));
		}
		else {
			oAerialPanel.setPreferredSize(new Dimension(nAerialWidth, nAerialHeight));
			oAerialPanel.setSize(new Dimension(nAerialWidth, nAerialHeight));
		}
		scaleToFit();

		oContentPane.add(oAerialPanel, BorderLayout.CENTER);

//		setSize(new Dimension(oAerialPanel.getWidth()+10, oAerialPanel.getHeight()+10));
		setSize(new Dimension(oAerialPanel.getWidth(), oAerialPanel.getHeight()));
		pack();
	}

	/**
	 * This returns the location of the dialog and the size of the aerial panel used to
	 * replace the next dialog when new frame opened.
	 * @return Rectangle, the location of the dialog and the size of the aerial panel.
	 */
	public Rectangle getResetSize() {
		Rectangle rec = new Rectangle(getBounds());
		rec.width = oAerialPanel.getWidth();
		rec.height = oAerialPanel.getHeight();
		return rec;
	}

	/**
	 * Update the given node in the aerail view.
	 * @param newnode, the node to refresh in the aerial view.
	 */
/*********************************************************************************************************	
	public void refreshNode(NodeSummary newnode) {
		try {
			View view = oAerialViewPane.getView();

			// UPDATE THE VIEW CACHE
			NodePosition nodePos = view.getNodePosition(newnode.getId());
			nodePos.setNode(newnode);
			view.replaceMemberNode(nodePos);

			// UPDATE THE UI
			UINode uinode = (UINode)oAerialViewPane.get(newnode.getId());
			ViewPaneUI viewpaneui = oAerialViewPane.getViewPaneUI();
			viewpaneui.removeNode(uinode);
			viewpaneui.addNode(nodePos);
		}
		catch(Exception ex) {
			System.out.println("Exception )UIMapViewPane.refreshAerialPane())\n\n"+ex.getMessage());
		}
	}
*********************************************************************************************************/

	/**
	 * Rescale and size this dialog.
	 * @param oPoint, the Point object to needed to test if rescale required.
	 * @see #scaleToFit
	 */
	public void rescale(Point oPoint) {

		if (oPoint.x > oAerialPanel.getWidth()-10 || oPoint.y > oAerialPanel.getHeight()-10) {
			scaleToFit();
		}
	}

	/**
	 * Scale the aerial map to fit the dialog size.
	 */
	public void scaleToFit() {

		double mapScale  = oMapFrame.getViewPane().getZoom();
		Dimension mapSize = oMapFrame.getViewPane().calculateSize();
		int mapWidth = (int) (mapSize.width / mapScale);
		int mapHeight = (int) (mapSize.height / mapScale);		
 		
 		Point scrollPoint = oMapFrame.getViewPosition();
 		int maxX = scrollPoint.x + oMapFrame.getViewport().getWidth();
 		int maxY = scrollPoint.y + oMapFrame.getViewport().getHeight();
 		maxX = (int) (maxX / mapScale);
 		maxY = (int) (maxY / mapScale);
		
		if (mapWidth > maxX) maxX = mapWidth;
		if (mapHeight > maxY) maxY = mapHeight;
		
		Dimension viewsize = oAerialPanel.getSize();
//		double xscale = CoreUtilities.divide(viewsize.width - 10, maxX);
//		double yscale = CoreUtilities.divide(viewsize.height - 10, maxY);
		double xscale = CoreUtilities.divide(viewsize.width, maxX);
		double yscale = CoreUtilities.divide(viewsize.height, maxY);

		double scale = xscale;
		if (yscale < xscale)
			scale = yscale;

		if (scale > 1.0)
			scale = 1.0;
		
		if (scale != oAerialViewPane.getZoom()) {
			oAerialViewPane.setZoom(scale);
			oAerialViewPane.scale();
		}
	}

	/**
	 * Return the instance of UIAerialViewPane held in this dialog.
	 * @return UIAerialViewPane, the instance of UIAerialViewPane held in this dialog.
	 */
	public UIAerialViewPane getViewPane() {
		return oAerialViewPane;
	}

// COMPONENT EVENTS - FOR BROADCASTING EVENTS ONLY

	/**
	 * Invoked when a component is resized. Rescales the contents to fit.
	 * @param evt, the associated ComponentEvent.
	 * @see #scaleToFit
	 */
	public void componentResized(ComponentEvent evt) {
		scaleToFit();
	}

	/**
	 * Invoked when a component is shown. DOES NOTHING.
	 * @param evt, the associated ComponentEvent.
	 */
	public void componentShown(ComponentEvent evt) {}

	/**
	 * Invoked when a component is moved. DOES NOTHING.
	 * @param evt, the associated ComponentEvent.
	 */
	public void componentMoved(ComponentEvent evt) {}

	/**
	 * Invoked when a component is hidden. DOES NOTHING.
	 * @param evt, the associated ComponentEvent.
	 */
	public void componentHidden(ComponentEvent evt) {}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}

	/**
	 * Close the associated aerial view.
	 */
	public void close() {
		if (oMapFrame != null) {
			oMapFrame.destroyAerialView();
		}
	}

	/**
	 *  Cancel this dialog and cancel the aerial view.
	 */
	public void onCancel() {

		if (oMapFrame != null) {
			oMapFrame.cancelAerialView();
		}
		else {
			setVisible(false);
			dispose();
		}
	}
}
