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

package com.compendium.ui.panels;

import static com.compendium.ProjectCompendium.*;

import java.util.*;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.awt.Container;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.Document;

import com.compendium.ProjectCompendium;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.INodeService;

import com.compendium.meeting.*;

import com.compendium.ui.*;
import com.compendium.ui.dialogs.UINodeContentDialog;

/**
 * Draw a panel containing various node property information depending on the node type.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UINodePropertiesPanel extends JPanel implements IUIConstants, ActionListener {

	/** Holds the UINode associated with this properties panel, if in a map.*/
	private UINode			oUINode				= null;

	/** Holds the NodeSummary associated with this properties panel.*/
	private NodeSummary		oNode				= null;

	/** Holds the NodePosition associated with this properties panel.*/
	private NodePosition	oNodePosition		= null;

	/** Holds the list of the actual node types insiode this node if it is a list or a map.*/
	private JTextArea		taTypes				= null;

	/** Used in process view contents if this node is a map or a list.*/
	private Vector			oViews				= new Vector();

	/** This displays the number of node held in the view, if this node is a map or list.*/
	private JLabel			lblCount			= null;

	/** Holds the text for the shortcut label label.*/
	private JLabel			pbShortCut			= null;

	/** Holds the text for the number of shortcuts label.*/
	private JLabel			lblShortCuts		= null;

	/** Holds the data for the shortcut label.*/
	private JLabel			lblShortCuts2		= null;

	/** This label holds the author field label.*/
	private JLabel			lblAuthor			= null;

	/** This label holds the node creation date field label.*/
	private JLabel			lblCreated			= null;

	/** This label holds the node modification date field label.*/
	private JLabel			lblModified			= null;

	/** This label holds the node id field label.*/
	private JLabel			lblId				= null;

	/** This label holds the author information.*/
	private JLabel			lblAuthor2			= null;

	/** This label holds the node creation date information.*/
	private JLabel			lblCreated2			= null;

	/** This label holds the node modification date information.*/
	private JLabel			lblModified2		= null;

	/** Holds the node type icon to diaply.*/
	private JLabel			lblIcon				= null;

	/** This label holds the author information.*/
	private JTextField		lblId2				= null;

	/** The main panel holding the gerenal node property data.*/
	private JPanel			mainpanel 			= null;

	/** This panel holds additional data for view nodes.*/
	private JPanel 			southpanel			= null;

	/** This panel holds additional data for shortcut nodes.*/
	private JPanel			shortspanel			= null;

	/** This panel holds the locking option*/
	private JPanel			lockingpanel		= null;
	
	private JRadioButton	rbLocked			= null;
	
	private JRadioButton	rbUnlocked			= null;
	
	/** This is a boolean that holds the locking state for the node.*/
	private Boolean			bLocked				= false;
	
	/** This panel holds the other panel.*/
	private JPanel			centerpanel			= null;

	/** Holds the list of the actual node types insiode this node if it is a list or a map.*/
	private JTextArea		taReaders			= null;

	/** This label holds the state field label.*/
	private JLabel			lblStateInfo		= null;

	/** This label holds the node modification date field label.*/
	private JLabel			lblModifiedBy		= null;

	/** This label holds the state information.*/
	private JLabel			lblStateInfo2			= null;

	/** This label holds the node modification date information.*/
	private JLabel			lblModifiedBy2		= null;

	/** Button to cancel the dialog this panel is in.*/
	private UIButton		pbCancel			= null;

	/** Button to save any changes to the contents/properties.*/
	private UIButton		pbOK				= null;

	/** The button to open the relevant help.*/
	private UIButton		pbHelp				= null;

	/** The parent dialog this panel sits in.*/
	private UINodeContentDialog oParentDialog	= null;

	/** Layout used to layout the panels in this panel.*/
	private GridBagLayout 	grid 				= null;

	/** The date panel used to edit the media index date/time of the node int the view.*/
	private UITimeSecondPanel		datePanel	= null;

	private MediaIndex		oMediaIndex			= null;

	/**
	 * Constructor.
	 *
	 * @param parent, the parent frame for the parent dialog to this panel.
	 * @param uinode com.compendium.ui.UINode, the node whose properties to display (if in a map).
	 * @param tabbedpane com.compendium.ui.dialogs.UINodeContentDialog, the dialog this panel is displayed in.
	 * @param isDefaultTab is this the tab set to be at the front on opening?
	 */
	/*public UINodePropertiesPanel(JFrame parent, UINode uinode, UINodeContentDialog tabbedPane, boolean isDefaultTab) {
		super();
		oParentDialog = tabbedPane;
		oNode = uinode.getNode();
		oUINode = uinode;
		oNodePosition = uinode.getNodePosition();
		drawPanel(isDefaultTab);
	}*/

	/**
	 * Constructor.
	 *
	 * @param parent, the parent frame for the parent dialog to this panel.
	 * @param nodePos com.compendium.core.datamodel.NodePosition, the node whose properties to display (if in a map).
	 * @param tabbedpane com.compendium.ui.dialogs.UINodeContentDialog, the dialog this panel is displayed in.
	 */
	public UINodePropertiesPanel(JFrame parent, NodePosition nodePos, UINodeContentDialog tabbedPane) {
		
		super();
		oParentDialog = tabbedPane;
		oNode = nodePos.getNode();
		oNodePosition = nodePos;
		
		//TODO: Work on locking functions has been frozen. Finish or delete.
//		try {
//		
//			if (0 == APP.getModel().getNodeService().
//			    iCheckNodeLock(APP.getModel().getSession(), oNode.getId())) {
//				
//				bLocked = false;
//				
//			} else {
//				
//				bLocked = true;
//				
//			}
//		
//		} catch (SQLException e) {
//			
//			e.printStackTrace();
//			
//		}
		
		drawPanel();
	}

	/**
	 * Constructor.
	 *
	 * @param parent, the parent frame for the parent dialog to this panel.
	 * @param uinode com.compendium.core.datamodel.NodeSummary, the node whose properties to display (if in a list).
	 * @param tabbedpane com.compendium.ui.dialogs.UINodeContentDialog, the dialog this panel is displayed in.
	 */
	public UINodePropertiesPanel(JFrame parent, NodeSummary node, UINodeContentDialog tabbedPane) {
		super();
		oParentDialog = tabbedPane;
		oNode = node;
		drawPanel();
	}

	/** Draw the panel gui elements and initialise the data.*/
	private void drawPanel() {

		setLayout(new BorderLayout());

		grid = new GridBagLayout();
		centerpanel = new JPanel();
		//centerpanel.setLayout(grid);
		centerpanel.setLayout( (new BorderLayout()) );

		/*GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.weightx=1;*/

		showCommonProperties();
		/*gc.gridy = 0;
		if( (!(oNode instanceof ShortCutNodeSummary)) && (!(oNode instanceof View)) )
			gc.weighty=1;
		grid.setConstraints(mainpanel, gc);*/
		centerpanel.add(mainpanel, BorderLayout.NORTH);

		//gc.weighty=0;

		JPanel oBottomPanel = new JPanel(new BorderLayout());
		oBottomPanel.add(showReadersPanel(), BorderLayout.WEST);

		JPanel oInnerBottomPanel = new JPanel(new BorderLayout());
		
		String sAuthor = oNode.getAuthor();
		
		String sUserName = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
		boolean bIsAdmin = ProjectCompendium.APP.getModel().getUserProfile().isAdministrator();

        //TODO: Work on locking functions has been frozen. Finish or delete.
//		if ((sAuthor.equals(sUserName) || bIsAdmin) &&
//		        oNode.getType() == ICoreConstants.MAPVIEW) {
//		
//			showLockingPanel();
//		
//			centerpanel.add(lockingpanel, BorderLayout.SOUTH);
//			
//		}

		if(oNode instanceof ShortCutNodeSummary) {
			showShortCutNodeEditPanel();
			//gc.gridy = 1;
			//if( !(oNode instanceof View) )
			//	gc.weighty=1;
			//grid.setConstraints(shortspanel, gc);
			oInnerBottomPanel.add(shortspanel, BorderLayout.CENTER);
		} else if(oNode instanceof View) {
			View view = ((View)oNode);
			if (!view.isMembersInitialized()) {
				try {
					view.initializeMembers();
				}
				catch(Exception ex) {
					ProjectCompendium.APP.displayError("Error: (UINodePropertiesPanel) Unable to get view data\n\n"+ex.getMessage());
				}
			}
			showViewProperties(view);
			//gc.gridy = 2;
			//if( !(oNode instanceof ShortCutNodeSummary) )
			//	gc.weighty=1;
			//grid.setConstraints(southpanel, gc);

			oInnerBottomPanel.add(southpanel, BorderLayout.CENTER);
		}

		oBottomPanel.add(oInnerBottomPanel, BorderLayout.EAST);
		centerpanel.add(oBottomPanel, BorderLayout.CENTER);

		add(centerpanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		if(oNode.getState() == ICoreConstants.READSTATE){
			lblStateInfo2.setText("Read");
		} else if(oNode.getState() == ICoreConstants.UNREADSTATE){
			lblStateInfo2.setText("Unread");
		} else if(oNode.getState() == ICoreConstants.MODIFIEDSTATE){
			lblStateInfo2.setText("Modified");
		}

		lblModifiedBy2.setText(oNode.getLastModificationAuthor());
	}

	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		if (oNodePosition != null && ProjectCompendium.APP.oMeetingManager != null) {
			pbOK = new UIButton("OK");
			pbOK.setMnemonic(KeyEvent.VK_O);
			pbOK.addActionListener(this);
			oButtonPanel.addButton(pbOK);

			pbCancel = new UIButton("Cancel");
			pbCancel.setMnemonic(KeyEvent.VK_C);
			pbCancel.addActionListener(this);
			oButtonPanel.addButton(pbCancel);
		}
		else {
			pbCancel = new UIButton("Close");
			pbCancel.setMnemonic(KeyEvent.VK_C);
			pbCancel.addActionListener(this);
			oButtonPanel.addButton(pbCancel);
		}

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.node_details-properties", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
	public void setDefaultButton() {
		if (oNodePosition != null && ProjectCompendium.APP.oMeetingManager != null) {
			oParentDialog.getRootPane().setDefaultButton(pbOK);
		} else {
			oParentDialog.getRootPane().setDefaultButton(pbCancel);
		}
	}

	/**
	 * Draw the main panel full of common properties.
	 */
	private void showCommonProperties() {

		mainpanel = new JPanel();
		mainpanel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		mainpanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		int y=0;

		if (oUINode == null) {
			lblIcon = new JLabel(UIImages.getNodeImage(oNode.getType()));
		}
		else {
			lblIcon = new JLabel(oUINode.getIcon());
		}

		gc.gridy = y;
		gc.gridx = 3;
		gc.gridheight=2;
		gc.weightx=1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(lblIcon, gc);
		mainpanel.add(lblIcon);

		lblStateInfo = new JLabel("Node State :");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridheight=1;
		gc.weightx=0;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(lblStateInfo, gc);
		mainpanel.add(lblStateInfo);

		lblStateInfo2 = new JLabel("");
		gc.gridx = 1;
		gc.gridwidth=3;
		gb.setConstraints(lblStateInfo2, gc);
		mainpanel.add(lblStateInfo2);
		y++;
		//y++;

		lblCreated = new JLabel("Created By:");
		gc.gridwidth=1;
		gc.gridy = y;
		gc.gridx = 0;
		gb.setConstraints(lblCreated, gc);
		mainpanel.add(lblCreated);

		lblAuthor2 = new JLabel("");
		gc.gridx = 1;
		gb.setConstraints(lblAuthor2, gc);
		mainpanel.add(lblAuthor2);

		lblAuthor = new JLabel("On:");
		gc.gridx = 2;
		gb.setConstraints(lblAuthor, gc);
		mainpanel.add(lblAuthor);

		lblCreated2 = new JLabel("");
		gc.gridx = 3;
		gb.setConstraints(lblCreated2, gc);
		mainpanel.add(lblCreated2);
		y++;

		lblModifiedBy = new JLabel("Last Modified By:");
		gc.gridy = y;
		gc.gridx = 0;
		gb.setConstraints(lblModifiedBy, gc);
		mainpanel.add(lblModifiedBy);

		lblModifiedBy2 	= new JLabel("");
		gc.gridx = 1;
		gb.setConstraints(lblModifiedBy2, gc);
		mainpanel.add(lblModifiedBy2);

		lblModified = new JLabel("On:");
		gc.gridx = 2;
		gb.setConstraints(lblModified, gc);
		mainpanel.add(lblModified);

		lblModified2 = new JLabel("");
		gc.gridx = 3;
		gb.setConstraints(lblModified2, gc);
		mainpanel.add(lblModified2);
		y++;

		JLabel lblShortCuts = new JLabel("Number of Shortcuts: ");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gb.setConstraints(lblShortCuts, gc);
		mainpanel.add(lblShortCuts);

		lblShortCuts2 = new JLabel("");
		gc.gridx = 1;
		gc.gridwidth=3;
		gb.setConstraints(lblShortCuts2, gc);
		mainpanel.add(lblShortCuts2);
		y++;

		lblId = new JLabel("Node Id:");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblId, gc);
		mainpanel.add(lblId);

		lblId2 = new JTextField("");
		lblId2.setEditable(false);
		gc.gridx = 1;
		gc.gridwidth=3;
		gb.setConstraints(lblId2, gc);
		mainpanel.add(lblId2);

		if (oNodePosition != null
				&& ProjectCompendium.APP.oMeetingManager != null
				&& ProjectCompendium.APP.oMeetingManager.captureEvents()) {

			datePanel = new UITimeSecondPanel("Video Index Offset: ");
			datePanel.setBorder(new EtchedBorder());

			oMediaIndex = oNodePosition.getMediaIndex(ProjectCompendium.APP.oMeetingManager.getMeetingID());
			if (oMediaIndex != null) {
				Date dIndex = oMediaIndex.getMediaIndex();

				if (dIndex != null) {
					datePanel.setDate(dIndex.getTime());
				}

				y++;
				gc.gridy = y;
				gc.gridx = 0;
				gc.gridwidth=4;
				gc.weighty=20;
				gb.setConstraints(datePanel, gc);
				mainpanel.add(datePanel);
			}
		}

		if (oNode != null) {
			if (oNode.getType() == ICoreConstants.MAPVIEW) {
				String sSource = oNode.getSource();
				if (sSource.startsWith("UDIG")) {
					JLabel lbludig = new JLabel("UDIG reference: ");
					y++;
					gc.gridy = y;
					gc.gridx = 0;
					gc.gridwidth=1;
					gc.weighty=1;
					gc.anchor = GridBagConstraints.NORTHWEST;
					gb.setConstraints(lbludig, gc);
					mainpanel.add(lbludig);

					JTextArea textArea = new JTextArea(sSource);
					textArea.setEditable(false);
					textArea.setLineWrap(true);
					textArea.setWrapStyleWord(false);
					textArea.setAutoscrolls(true);

					JScrollPane scrollpane = new JScrollPane(textArea);
					scrollpane.setPreferredSize(new Dimension(300,50));
					gc.gridx = 1;
					gc.gridwidth=3;
					gc.weightx=1;
					gc.weighty=100;
					gb.setConstraints(scrollpane, gc);
					mainpanel.add(scrollpane);
				}
			}
		}

		//set the values
		lblId2.setText(oNode.getId());
		lblAuthor2.setText(oNode.getAuthor());
		lblCreated2.setText(UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy h:mm a").format(oNode.getCreationDate()).toString());
		lblModified2.setText(UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy h:mm a").format(oNode.getModificationDate()).toString());

		if (oNode != null) {
			try {
				lblShortCuts2.setText(String.valueOf((oNode.getShortCutNodes()).size()));
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Remote Error in 'UINodeProperties.showNodeProperties'");
			}
		}
	}


	/**
	 * Handles the cancel button push.
	 * @param evt, the ActionEvent object associated with the button push.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if (source == pbCancel) {
			oParentDialog.onCancel();
		}
		else if ((source == pbOK)) {
			oParentDialog.onUpdate();
			oParentDialog.onCancel();
		} else if (source == rbLocked) {
			
			bLocked = true;
		
		} else if (source == rbUnlocked) {
			
			bLocked = false;
		}
	}

	/**
	 * Draw the panel of readers.
	 */
	private JPanel showReadersPanel() {

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(),
                "Node has been read by",
                TitledBorder.LEFT,
                TitledBorder.TOP,
				new Font("Dialog", Font.BOLD, 12) ));

		taReaders = new JTextArea("");
		taReaders.setFont(new Font("Monospaced", Font.PLAIN, 12));
		updateReadersInformation();
		taReaders.setEditable(false);

		JScrollPane scrollpane = new JScrollPane(taReaders);
		scrollpane.setPreferredSize(new Dimension(200,100));
		panel.add(scrollpane, BorderLayout.CENTER);

		return panel;
	}
	
	/**
	 * Draw the node locking panel.
	 * @author jamesl
	 */
	private void showLockingPanel () {
		
		lockingpanel = new JPanel();
		lockingpanel.setBorder(new TitledBorder(new EtchedBorder(), "Lock Node", TitledBorder.LEFT, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 12)));
		
		rbLocked = new JRadioButton("Locked", bLocked);
		rbUnlocked = new JRadioButton("Unlocked", ! bLocked);
		
		rbLocked.addActionListener(this);
		rbUnlocked.addActionListener(this);
		
		ButtonGroup bgOpts  = new ButtonGroup();
		bgOpts.add(rbLocked);
		bgOpts.add(rbUnlocked);
		
		lockingpanel.add(rbLocked);
		lockingpanel.add(rbUnlocked);
		
	}

	/**
	 * Draw the panel of additional property data for shortcuts.
	 */
	private void showShortCutNodeEditPanel() {

		// COPIED OVER FROM THE EDIT, BUT WAS NOT BEING USED
		//else if (source == pbShortCut) {
		//	UINodeTabbedPane dialog = new UINodeTabbedPane(ProjectCompendium.APP,(NodeSummary)((ShortCutNodeSummary)oNode).getReferredNode(), UINodeTabbedPane.CONTENTS_TAB);
		//	dialog.setVisible(true);
		//}

		shortspanel = new JPanel();
		shortspanel.setBorder(new TitledBorder(new EtchedBorder(),
                    "Shortcut To Node",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		shortspanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		pbShortCut = new JLabel("Label:");
		gc.gridy = 0;
		gc.gridx = 0;
		gb.setConstraints(pbShortCut, gc);
		shortspanel.add(pbShortCut);

		NodeSummary referredNode = ((ShortCutNodeSummary)oNode).getReferredNode();
		String referredNodeLabel = "";
		if(referredNode != null)
			referredNodeLabel = referredNode.getLabel();

		JTextArea tfLabel = new JTextArea(referredNodeLabel);
		tfLabel.setEditable(false);
		tfLabel.setLineWrap(true);
		tfLabel.setWrapStyleWord(true);
		tfLabel.setAutoscrolls(true);

		JScrollPane scrollpane = new JScrollPane(tfLabel);
		scrollpane.setPreferredSize(new Dimension(200,70));
		gc.gridx = 1;
		gc.weightx=1.0;
		gb.setConstraints(scrollpane, gc);
		shortspanel.add(scrollpane);

		JLabel label = new JLabel("Node ID:");
		gc.gridy = 1;
		gc.gridx = 0;
		gc.weightx=0;
		gc.weighty=1.0;
		gb.setConstraints(label, gc);
		shortspanel.add(label);

		String referredNodeID = "";
		if(referredNode != null)
			referredNodeID = referredNode.getId();

		JTextField tfNodeID = new JTextField(referredNodeID);
		gc.gridx = 1;
		gc.weightx=1.0;
		gc.weighty=100;
		gb.setConstraints(tfNodeID, gc);
		tfNodeID.setEditable(false);
		shortspanel.add(tfNodeID);
	}

	/**
	 * Draw the panel of additional data for map and list view nodes.
	 * @param view com.compendium.core.datamodel.View, the view to draw the data for.
	 */
	private void showViewProperties(View view) {

		String type = "Map";
		if (view.getType() == ICoreConstants.LISTVIEW)
			type="List";

		southpanel = new JPanel();
		southpanel.setBorder(new TitledBorder(new EtchedBorder(),
                    (type+" Contents"),
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		southpanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblCount = new JLabel("Number of Nodes:");
		gc.gridy = 0;
		gc.gridx = 0;
		gb.setConstraints(lblCount, gc);
		southpanel.add(lblCount);

		lblCount = new JLabel(String.valueOf(view.getNumberOfNodes()));
		gc.gridy = 0;
		gc.gridx = 1;
		gc.weightx=1.0;
		gb.setConstraints(lblCount, gc);
		southpanel.add(lblCount);

		JLabel lblTypes = new JLabel("Types :");
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gb.setConstraints(lblTypes, gc);
		southpanel.add(lblTypes);

		taTypes = new JTextArea("");
		taTypes.setFont(new Font("Monospaced", Font.PLAIN, 12));
		updateTypesInformation(view);
		taTypes.setEditable(false);

		JScrollPane scrollpane = new JScrollPane(taTypes);
		scrollpane.setPreferredSize(new Dimension(200,100));
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridheight=4;
		gc.weighty=500;
		gc.anchor = GridBagConstraints.NORTH;
		gb.setConstraints(scrollpane, gc);

		southpanel.add(scrollpane);
	}

	/**
	 * Set the modification date and author for this node.
	 * @param newDate the latest modification date for this node.
	 * @param sAuthor the author who made the modification
	 */
	public void setModified(String newDate, String sAuthor) {
		if (lblModified!= null)
			lblModified.setText(newDate);
		if (lblModifiedBy!= null)
			lblModifiedBy.setText(sAuthor);
	}

	/**
	 * Update the list of node contents information diaplayed for this node if it is a view (list or map).
	 * @param view comp.compendium.core.datamodel.View, the view node to update the contents information displayed for.
	 */
	private void updateTypesInformation(View view) {

		int general=0,listview=0,mapview=0,issue=0,position=0,argument=0,pro=0,con=0,decision=0,reference=0,note=0;

		String sToDisplay = "";

		for(Enumeration e = view.getPositions();e.hasMoreElements();) {

			NodeSummary node = ((NodePosition)e.nextElement()).getNode();

			switch(node.getType()) {

				case(ICoreConstants.GENERAL):
					general++;
					break;
				case(ICoreConstants.LISTVIEW):
					listview++;
					oViews.addElement(node);
					break;
				case(ICoreConstants.MAPVIEW):
					mapview++;
					oViews.addElement(node);
					break;
				case(ICoreConstants.ISSUE):
					issue++;
					break;
				case(ICoreConstants.POSITION):
					position++;
					break;
				case(ICoreConstants.ARGUMENT):
					argument++;
					break;
				case(ICoreConstants.PRO):
					pro++;
					break;
				case(ICoreConstants.CON):
					con++;
					break;
				case(ICoreConstants.DECISION):
					decision++;
					break;
				case(ICoreConstants.REFERENCE):
					reference++;
					break;
				case(ICoreConstants.NOTE):
					note++;
					break;
			}
		}

		if (general > 0)
			sToDisplay += "general   = " + String.valueOf(general) + "\n";
		if (listview > 0)
			sToDisplay += "listview  = " + String.valueOf(listview) + "\n";
		if (mapview > 0)
			sToDisplay += "mapview   = " + String.valueOf(mapview) + "\n";
		if (issue > 0)
			sToDisplay += "question  = " + String.valueOf(issue) + "\n";
		if (position > 0)
			sToDisplay += "answer    = " + String.valueOf(position) + "\n";
		if (argument > 0)
			sToDisplay += "argument  = " + String.valueOf(argument) + "\n";
		if (pro > 0)
			sToDisplay += "pro       = " + String.valueOf(pro) + "\n";
		if (con > 0)
			sToDisplay += "con       = " + String.valueOf(con) + "\n";
		if (decision > 0)
			sToDisplay += "decision  = " + String.valueOf(decision) + "\n";
		if (reference > 0)
			sToDisplay += "reference = " + String.valueOf(reference) + "\n";
		if (note > 0)
			sToDisplay += "note      = " + String.valueOf(note);

		taTypes.setText(sToDisplay);
	}

	/**
	 * Process the saving of any node contents/properties changes, the media Index date.
	 * @throws SQLException 
	 */
	public void onUpdate() throws SQLException {
	    //TODO: Work on locking functions has been frozen. Finish or delete.
		
//		INodeService nodeService = ProjectCompendium.APP.getModel().getNodeService();
//        int lockID = nodeService.iCheckNodeLock(APP.getModel().getSession(), oNode.getId());
//        if (bLocked) {
//			
//			String sUser = APP.getModel().getUserProfile().getId();
//			
//			try {
//                if (lockID == 0) {
//                    nodeService.bLockNode(APP.getModel().getSession(), sUser, oNode.getId());
//                }
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//            if (lockID != 0) {
//                nodeService.bUnLockNode(APP.getModel().getSession(), lockID);
//            }
//		}
		
		if (oNodePosition != null && APP.oMeetingManager != null && oMediaIndex != null) {

			if (datePanel.dateChanged()) {

				GregorianCalendar oDate = datePanel.getDate();
				long lDate = oDate.getTime().getTime();

				Date cal = new Date(lDate);
				try {
					oMediaIndex.setMediaIndex(cal);
				}
				catch( Exception ex ) {
					APP.displayError("Unable to set Video Index due to:\n\n"+ex.getMessage());
				}
			}
		}
	}

	/**
	 * This method returns the list names of the readers of the current object.
	 * The current algorithm is new as of v1.6 in that it uses the already-in-memory
	 * user name from the UserProfile list instead of hitting the database for this info.
	 */
	private void updateReadersInformation() {

		String readers = "";
		Vector readerIDs = new Vector();
		UserProfile up = null;

		// Get the list of readers (ID's) from the database
		try {
			readerIDs = ProjectCompendium.APP.getModel().getNodeService().getReaderIDs(ProjectCompendium.APP.getModel().getSession(), oNode.getId());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		// Get the existing list of UserProfile objects
		Vector userProfiles = ProjectCompendium.APP.getModel().getUsers();
		Vector readernames = new Vector();

		// For each ID, find its corresponding UserProfile, and extract the User Name
		for(Enumeration id = readerIDs.elements();id.hasMoreElements();) {
			String sReaderID = (String) id.nextElement();
			for(Enumeration id2 = userProfiles.elements();id2.hasMoreElements();) {
				up = (UserProfile)id2.nextElement();
				if (sReaderID.compareTo(up.getUserID())== 0) {
					readernames.addElement(up.getUserName());
//					readers = readers + up.getUserName() + "\n";
				}
			}
		}
		Collections.sort(readernames);  // Sort the readers list, then stuff it in the display
		for(Enumeration id = readernames.elements(); id.hasMoreElements();) {
			readers = readers + id.nextElement() + "\n";
		}

		taReaders.setText(readers);
	}
}
