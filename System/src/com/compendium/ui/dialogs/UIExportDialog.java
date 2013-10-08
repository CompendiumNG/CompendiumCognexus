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

import static com.compendium.ProjectCompendium.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.io.html.*;

import com.compendium.ui.*;
import com.compendium.ui.panels.*;

/**
 * UIExportDialog defines the export dialog, that allows
 * the user to export PC Map/List Views to a MS-Word format document
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIExportDialog extends UIDialog implements ActionListener, ItemListener, IUIConstants {

	public static String sBaseAnchorPath = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Images"+ProjectCompendium.sFS;

	/** The default directory to export to.*/
	private static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports";

	/** The button to start the export.*/
	private UIButton			pbExport	= null;

	/** The button to close the dialog.*/
	private UIButton			pbClose		= null;

	/** The button to open the help.*/
	private UIButton			pbHelp 		= null;

	/** The button to open the HTML export formatting dialog.*/
	private UIButton			pbFormatOutput = null;

	/** The button to open the view dialog.*/
	private UIButton			pbViews 	= null;

	/** The button to browse for a image to use for anchors in the export.*/
	private UIButton			pbBrowse 	= null;

	/** Indicates whether to include the author details in the export.*/
	private JCheckBox 		includeNodeAuthor = null;

	/** Indicates whether to inlucde the images in the export.*/
	private JCheckBox 		includeImage = null;

	/** Indicates whether to inlucde the links in the export.*/
	private JCheckBox 		includeLinks = null;

	/** Indicates whether the inlcude a navigation bar with the export.*/
	private JCheckBox 		includeNavigationBar = null;

	/** Indicates whether to diapl the node detail page dates on export.*/
	private JCheckBox 		displayDetailDates = null;

	/** Indicates that the node detail page dates should not be displayed.*/
	private JCheckBox 		hideNodeNoDates = null;

	/** Indicates that each view should be exported in a separate HTML file.*/
	private JCheckBox 		displayInDifferentPages = null;

	/** Indicates whether to include node anchors.*/
	private JCheckBox 		includeNodeAnchor = null;

	/** Indicates whether to include anchors on node detail pages.*/
	private JCheckBox 		includeDetailAnchor = null;

	/** Indicates whether to export all files to a zip file.*/
	private JCheckBox       cbToZip				= null;

	/** Indicates whether to include external local reference files in the export.*/
	private JCheckBox       cbWithRefs			= null;

	/** Lets the user indicate whether to open the export file after completion (only if not zipped).*/
	private JCheckBox		cbOpenAfter			= null;

	//matt stucky
	//June 2012
	/** Convert to word via macro after export */
	private JCheckBox		cbConvertToWord		= null;

	/** Indicates whether to inlcude the heading tags in the export (good for accessibility, bad for Word).*/
	private JCheckBox 		optimizeForWord = null;

	/** Holds the user assigned title for the main export file.*/
	private JTextField		titlefield = null;

	/** Holds the name of the anchor image file to use.*/
	private JTextField		anchorImage = null;

	/** Holds choice boxes to enter the from date for filtering node detail pages.*/
	private UIDatePanel 	fromPanel = null;

	/** Holds choice boxes to enter the to date for filtering node detail pages.*/
	private UIDatePanel 	toPanel = null;

	/** Should parent view data be placed in line in the main text body?*/
	private JRadioButton 	inlineView = null;

	/** Indicates whether to include tags in the export.*/
	private JCheckBox       cbIncludeTags			= null;

	/** Indicates whether to include parent views in the export.*/
	private JCheckBox		cbIncludeViews			= null;

	/** should parent view data be placed in separate files?*/
	private JRadioButton 	newView = null;

	/** Should node detail detail pages be included in the export?*/
	private JRadioButton 	noNodeDetail = null;

	/** Should node detail pages should be filtered in given dates?*/
	private JRadioButton 	includeNodeDetail = null;

	/** Should node detail pages dates be included in the export?*/
	private JRadioButton 	includeNodeDetailDate = null;

	/** Should images be used for anchors?*/
	private JRadioButton 	useAnchorImages = null;

	/** Should purple numbers be used for anchors.*/
	private JRadioButton 	useAnchorNumbers = null;

	/** Should the views being exported be exported to thier full depth?*/
	private JRadioButton	fullDepth = null;

	/** Should view being exported only export themselves and not thier child nodes?*/
	private	JRadioButton	currentDepth = null;

	/** Should views being export be export to a sinlge level of depth only?*/
	private	JRadioButton	oneDepth = null;

	/** Should all nodes in the current view be export?*/
	private JRadioButton	allNodes = null;

	/** Should only the selected views in the current view be exported.*/
	private	JRadioButton	selectedViews = null;

	/** Should only views selected through the views dialog be exported.*/
	private	JRadioButton	otherViews = null;

	/** The label for the title field.*/
	private JLabel			titleLabel = null;

	/** The text area to list the views selected for export.*/
	private JTextArea 		oTextArea  = null;

	/** Used while processing nodes for export.*/
	private Vector			nodeLevelList = null;

	/** Used while processing nodes for export.*/
	private Hashtable		htNodesLevel = new Hashtable(51);

	/** Holds nodes being processed for export.*/
	private	Hashtable		htNodes = new Hashtable(51);

	/** Used wile processing nodes for export.*/
	private Hashtable		htNodesBelow = new Hashtable(51);

	/** Used while processing nodes for export.*/
	private Hashtable		htCheckDepth = new Hashtable(51);

	/** Used while processing nodes for export.*/
	private Hashtable		htChildrenAdded = new Hashtable(51);

	/** The level to start the export at.*/
	private int				nStartExportAtLevel 	= 0;

	/** Used while processing nodes for export.*/
	private int				nodeIndex 				= -1;

	/** The file name for the main export file.*/
	private String			fileName 		= "";

	/** Holds the anchor options.*/
	private JPanel			innerAnchorPanel = null;

	/** The main pane for the dialog's contents.*/
	private Container		oContentPane = null;

	/** The class that will process the export and create the HTML files etc. for the export.*/
	private HTMLOutline		oHTMLExport = null;

	/** The current view being exported.*/
	private View			currentView = null;

	/** The frame of the current view being exported.*/
	private UIViewFrame		currentFrame = null;

	/** Used to order the nodes being exported.*/
	private IUIArrange		arrange = null;

	/** The model of the currently open database.*/
	private IModel 			model 	= null;

	/** The session for the current user in the current model*/
	private PCSession 		session = null;

	/** The IViewService instance to access the database.*/
	private IViewService 	vs = null;

	/** The font to use for labels.*/
	private Font 			font = null;

	/** The tabbedpane holding all the various option panels.*/
	private JTabbedPane		tabbedPane = null;

	/** The scrollpane holding the list of default anhor images.*/
	private JScrollPane 	imagescroll = null;

	/** The renderer used to render the list of default anchor imags.*/
	private AnchorImageCellRenderer anchorImageListRenderer = null;

	/** The list of default anchor images.*/
	private UINavList 		lstAnchorImages		= null;

	/** The dialog diaplying all views avilable to export.*/
	private UIExportMultipleViewDialog viewsDialog = null;

	/** The label which tells the user which format the export will use.*/
	private JLabel			lblFormatUsed = null;

	/** List of style names to be displayed in the choice box.*/
	private Vector 					vtStyles = new Vector();

	/** Holds a list of existing styles.*/
	private JComboBox				oStyles	= null;

	/**
	 * Initializes and sets up the dialog.
	 * @param frame, the view frame being exported.
	 */
	public UIExportDialog(UIViewFrame frame) {
		super(ProjectCompendium.APP, true);
		this.currentFrame = frame;
		this.currentView = frame.getView();
	}

	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param frame, the view frame being exported.
	 */
	public UIExportDialog(JFrame parent, UIViewFrame frame) {

		super(parent, true);

		this.currentFrame = frame;
		this.currentView = frame.getView();
	  	this.setTitle("Web Outline Export");

		font = new Font("Dialog", Font.PLAIN, 12);

		JPanel mainPanel = new JPanel(new BorderLayout());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Dialog", Font.BOLD, 12));

		JPanel contentPanel = createContentPanel();
		JPanel optionsPanel = createOptionsPanel();
		//JPanel detailPanel = createDetailPanel();
		JPanel tagPanel = createAnchorPanel();

		JPanel outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(contentPanel);
		tabbedPane.add(outer, "Node Selection");

		outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(optionsPanel);
		tabbedPane.add(outer, "Format & Content");

		//outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//outer.add(detailPanel);
		//tabbedPane.add(outer, "Node Detail Pages");

		outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(tagPanel);
		//tabbedPane.add(outer, "Node Anchors");

		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		JPanel buttonpanel = createButtonPanel();

		oContentPane.add(mainPanel, BorderLayout.CENTER);
		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		loadProperties();
		applyLoadedProperties();

		pack();
		setResizable(false);
	}

	/**
	 * Draw the button panel for the bottom of the dialog.
	 */
	private JPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbExport = new UIButton("Export...");
		pbExport.setMnemonic(KeyEvent.VK_E);
		pbExport.addActionListener(this);
		getRootPane().setDefaultButton(pbExport);
		oButtonPanel.addButton(pbExport);

		pbClose = new UIButton("Cancel");
		pbClose.setMnemonic(KeyEvent.VK_C);
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.export_html_outline", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Draw the first tabbed panel with the primary export options.
	 */
	private JPanel createContentPanel() {

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(10,10,10,10));

		//STAGE ONE

		GridBagLayout gb1 = new GridBagLayout();
		GridBagConstraints gc1 = new GridBagConstraints();
		contentPanel.setLayout(gb1);
		int y=0;
		gc1.anchor = GridBagConstraints.WEST;

		JPanel innerpanel = new JPanel(gb1);
		//innerpanel.setBorder(new TitledBorder("Views to Export"));

		JLabel lbltitle1 = new JLabel("Views to Export");
		lbltitle1.setFont(font);
		lbltitle1.setForeground(Color.blue);
		gc1.gridy = y;
		gc1.gridwidth=1;
		y++;
		gb1.setConstraints(lbltitle1, gc1);
		innerpanel.add(lbltitle1);

		allNodes = new JRadioButton("Current View only");
		allNodes.setSelected(false);
		allNodes.addItemListener(this);
		allNodes.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;
		gc1.gridheight = 1;
		gc1.gridwidth=2;
		y++;
		gb1.setConstraints(allNodes, gc1);
		innerpanel.add(allNodes);

		selectedViews = new JRadioButton("Selected Views");
		selectedViews.setSelected(false);
		selectedViews.addItemListener(this);
		selectedViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;
		gc1.gridheight = 1;
		gc1.gridwidth=2;
		y++;
		gb1.setConstraints(selectedViews, gc1);
		innerpanel.add(selectedViews);

		otherViews = new JRadioButton("Other Views: ");
		otherViews.setSelected(false);
		otherViews.addItemListener(this);
		otherViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;
		gc1.gridheight = 1;
		gc1.gridwidth=1;
		//y++;
		gb1.setConstraints(otherViews, gc1);
		innerpanel.add(otherViews);

		pbViews = new UIButton("Choose Views");
		pbViews.setEnabled(false);
		pbViews.addActionListener(this);
		pbViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 1;
		gc1.gridwidth=1;
		gc1.gridheight = 1;
		y++;
		gb1.setConstraints(pbViews, gc1);
		innerpanel.add(pbViews);

		JPanel textpanel = new JPanel(new BorderLayout());
		textpanel.setBorder(new EmptyBorder(0,10,0,0));

		JLabel label = new JLabel("Chosen Views:");
		label.setFont(font);
		label.setAlignmentX(SwingConstants.LEFT);
		textpanel.add(label, BorderLayout.NORTH);

		oTextArea = new JTextArea("");
		oTextArea.setEditable(false);
		JScrollPane scrollpane = new JScrollPane(oTextArea);
		scrollpane.setPreferredSize(new Dimension(220,120));
		textpanel.add(scrollpane, BorderLayout.CENTER);

		gc1.gridy = 0;
		gc1.gridx = 2;
		gc1.gridwidth=1;
		gc1.gridheight = 4;
		gb1.setConstraints(textpanel, gc1);
		innerpanel.add(textpanel);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(allNodes);
		group1.add(selectedViews);
		group1.add(otherViews);

		//STAGE TWO
		GridBagLayout gb2 = new GridBagLayout();
		GridBagConstraints gc2 = new GridBagConstraints();
		contentPanel.setLayout(gb2);
		y=0;
		gc2.anchor = GridBagConstraints.WEST;
		JPanel innerpanel2 = new JPanel(gb2);

		//innerpanel2.setBorder(new TitledBorder("Depth to Export Views at"));

		JSeparator sep2 = new JSeparator();
		gc2.gridy = y;
		gc2.gridwidth=2;
		gc2.insets = new Insets(5,0,2,0);
		y++;
		gc2.fill = GridBagConstraints.HORIZONTAL;
		gb2.setConstraints(sep2, gc2);
		innerpanel2.add(sep2);
		gc2.fill = GridBagConstraints.NONE;

		gc2.insets = new Insets(0,0,0,0);

		JLabel lbltitle2 = new JLabel("Depth To Export Views To");
		lbltitle2.setFont(font);
		lbltitle2.setForeground(Color.blue);
		gc2.gridy = y;
		gc2.gridwidth=2;
		y++;
		gb2.setConstraints(lbltitle2, gc2);
		innerpanel2.add(lbltitle2);

		currentDepth = new JRadioButton("Nodes in view only");
		currentDepth.setSelected(true);
		currentDepth.addItemListener(this);
		currentDepth.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		gb2.setConstraints(currentDepth, gc2);
		innerpanel2.add(currentDepth);

		JLabel lbl = new JLabel("");
		lbl.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl, gc2);
		innerpanel2.add(lbl);

		oneDepth = new JRadioButton("One level down");
		oneDepth.setSelected(true);
		oneDepth.addItemListener(this);
		oneDepth.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		gb2.setConstraints(oneDepth, gc2);
		innerpanel2.add(oneDepth);

		JLabel lbl1 = new JLabel("(nodes in view and any child view contents)");
		lbl1.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl1, gc2);
		innerpanel2.add(lbl1);

		fullDepth = new JRadioButton("Full depth");
		fullDepth.setSelected(false);
		fullDepth.addItemListener(this);
		fullDepth.setFont(font);
		gc2.gridwidth=1;
		gc2.gridy = y;
		gb2.setConstraints(fullDepth, gc2);
		innerpanel2.add(fullDepth);

		JLabel lbl2 = new JLabel("(nodes in view, child view contents, their child view contents etc..)");
		lbl2.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl2, gc2);
		innerpanel2.add(lbl2);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(currentDepth);
		rgGroup.add(oneDepth);
		rgGroup.add(fullDepth);

		// MAIN PANEL
		GridBagLayout gb = new GridBagLayout();
		contentPanel.setLayout(gb);
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		y=0;

		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(innerpanel, gc);
		contentPanel.add(innerpanel);

		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(innerpanel2, gc);
		contentPanel.add(innerpanel2);

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(5,0,2,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

		displayInDifferentPages = new JCheckBox("Export each view in a separate HTML file");
		displayInDifferentPages.addItemListener(this);
		displayInDifferentPages.setFont(font);
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(displayInDifferentPages, gc);
		//contentPanel.add(displayInDifferentPages);

		titleLabel = new JLabel("HTML title for the base web page: ");
		titleLabel.setFont(font);
		titleLabel.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(titleLabel, gc);
		//contentPanel.add(titleLabel);

		titlefield = new JTextField("");
		titlefield.setEditable(false);
		titlefield.setColumns(20);
		titlefield.setMargin(new Insets(2,2,2,2));
		titlefield.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		y++;
		gb.setConstraints(titlefield, gc);
		//contentPanel.add(titlefield);

		sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		//contentPanel.add(sep);

		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0,0,0,0);

      	cbWithRefs = new JCheckBox("Include referenced files?");
      	cbWithRefs.setSelected(false);
		cbWithRefs.addItemListener(this);
		cbWithRefs.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbWithRefs, gc);
      	//contentPanel.add(cbWithRefs);

      	cbToZip = new JCheckBox("Export to Zip Archive?");
      	cbToZip.setSelected(false);
		cbToZip.addItemListener(this);
		cbToZip.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbToZip, gc);
      	//contentPanel.add(cbToZip);

      	cbOpenAfter = new JCheckBox("Open Export after completion?");
      	cbOpenAfter.setSelected(false);
		cbOpenAfter.addItemListener(this);
		cbOpenAfter.setFont(font);
		gc.gridy = y;
		gb.setConstraints(cbOpenAfter, gc);
      	//contentPanel.add(cbOpenAfter);

      	cbConvertToWord = new JCheckBox("Convert To Word doc after export?");
      	cbConvertToWord.setSelected(true);
		cbConvertToWord.addItemListener(this);
		cbConvertToWord.setFont(font);
		gc.gridy = y;
		gb.setConstraints(cbConvertToWord, gc);
      	contentPanel.add(cbConvertToWord);

		return contentPanel;
	}

	/**
	 *	Create a panel holding the node detail page export options.
	 */
	//private JPanel createDetailPanel() {

		/*JPanel detailPanel = new JPanel();
		detailPanel.setBorder(new EmptyBorder(10,10,10,10));
		detailPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		detailPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;

		int y=0;

		// CREATE DATE PANEL FIRST FOR REFERENCE REASONS
		JPanel datePanel = createDatePanel();*/

		//JLabel label = new JLabel("Node Details");
		//label.setFont(new Font("Arial", Font.BOLD, 12));
		//gc.gridy = y;
		//y++;
		//gb.setConstraints(label, gc);
		//detailPanel.add(label);

		/*noNodeDetail = new JRadioButton("No node detail pages");
		noNodeDetail.addItemListener(this);
		noNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(noNodeDetail, gc);
		detailPanel.add(noNodeDetail);

		includeNodeDetail = new JRadioButton("Include all node detail pages");
		includeNodeDetail.addItemListener(this);
		includeNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(includeNodeDetail, gc);
		detailPanel.add(includeNodeDetail);

		includeNodeDetailDate = new JRadioButton("Include node detail pages for Dates: ");
		includeNodeDetailDate.addItemListener(this);
		includeNodeDetailDate.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(includeNodeDetailDate, gc);
		detailPanel.add(includeNodeDetailDate);

		ButtonGroup detailGroup = new ButtonGroup();
		detailGroup.add(noNodeDetail);
		detailGroup.add(includeNodeDetail);
		detailGroup.add(includeNodeDetailDate);

		// ADD DATE PANEL
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(datePanel, gc);
		detailPanel.add(datePanel);

		JLabel other = new JLabel(" ");
		gc.gridy = y;
		y++;
		gb.setConstraints(other, gc);
		detailPanel.add(other);

		displayDetailDates = new JCheckBox("Display detail page dates");
		displayDetailDates.addItemListener(this);
		displayDetailDates.setSelected(false);
		displayDetailDates.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(displayDetailDates, gc);
		detailPanel.add(displayDetailDates);*/

		/*
		hideNodeNoDates = new JCheckBox("Hide nodes outside of dates");
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(hideNodeNoDates, gc);
		hideNodeNoDates.addItemListener(this);
		hideNodeNoDates.setSelected(false);
		detailPanel.add(hideNodeNoDates);
		*/

		//return detailPanel;
	//}

	/**
	 *	Create a panel holding the anchor export options (i.e. purple numbers stuff).
	 */
	private JPanel createAnchorPanel() {

		JPanel anchorPanel = new JPanel();
		anchorPanel.setLayout(new BorderLayout());
		anchorPanel.setBorder(new EmptyBorder(10,10,10,10));
		anchorPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		JPanel innerAnchorPanelTop = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		innerAnchorPanelTop.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);

		int y=0;

		includeNodeAnchor = new JCheckBox("Include anchors on node labels");
		includeNodeAnchor.addItemListener(this);
		includeNodeAnchor.setFont(font);
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(includeNodeAnchor, gc);
		innerAnchorPanelTop.add(includeNodeAnchor);

		includeDetailAnchor = new JCheckBox("Include anchors on node detail pages");
		includeDetailAnchor.addItemListener(this);
		includeDetailAnchor.setFont(font);
		gc.gridy = y;
		gc.gridx = 1;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(includeDetailAnchor, gc);
		innerAnchorPanelTop.add(includeDetailAnchor);

		useAnchorImages = new JRadioButton("Use images for anchors");
		useAnchorImages.addItemListener(this);
		useAnchorImages.setFont(font);
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(useAnchorImages, gc);
		innerAnchorPanelTop.add(useAnchorImages);

		useAnchorNumbers = new JRadioButton("Use purple numbers");
		useAnchorNumbers.addItemListener(this);
		useAnchorNumbers.setFont(font);
		gc.gridy = y;
		gc.gridx = 1;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(useAnchorNumbers, gc);
		innerAnchorPanelTop.add(useAnchorNumbers);

		ButtonGroup anchorGroup = new ButtonGroup();
		anchorGroup.add(useAnchorImages);
		anchorGroup.add(useAnchorNumbers);

		innerAnchorPanel = new JPanel();
		innerAnchorPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		innerAnchorPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);

		y=0;

		createAnchorImageList();
		gc.gridy = y;
		//y++;
		gc.gridwidth=2;
		gb.setConstraints(lstAnchorImages, gc);
		innerAnchorPanel.add(lstAnchorImages);

		JTextArea area = new JTextArea("Select one of the default anchor images from this list or use the browse button below to select your own anchor image");
		area.setBackground(innerAnchorPanel.getBackground());
		area.setColumns(20);
		area.setRows(7);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEnabled(false);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(area, gc);
		innerAnchorPanel.add(area);

		JLabel label = new JLabel("Anchor image: ");
		label.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		innerAnchorPanel.add(label);

		anchorImage = new JTextField("");
		anchorImage.setEditable(false);
		anchorImage.setColumns(25);
		anchorImage.setMargin(new Insets(2,2,2,2));
		anchorImage.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(anchorImage, gc);
		innerAnchorPanel.add(anchorImage);

		pbBrowse = new UIButton("Browse");
		pbBrowse.addActionListener(this);
		pbBrowse.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(pbBrowse, gc);
		innerAnchorPanel.add(pbBrowse);

		anchorPanel.add(innerAnchorPanelTop, BorderLayout.NORTH);
		anchorPanel.add(innerAnchorPanel, BorderLayout.CENTER);

		return anchorPanel;
	}

	/**
	 *	Create a panel holding other export options.
	 */
	private JPanel createOptionsPanel() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new EmptyBorder(10,10,10,10));
		optionsPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		optionsPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		int y=0;

		lblFormatUsed = new JLabel("Outline Format   ");
		lblFormatUsed.setFont(font);
		gc.gridy = y;
		gc.gridwidth = 1;
		gb.setConstraints(lblFormatUsed, gc);
		optionsPanel.add(lblFormatUsed);

		this.createStylesChoiceBox();
		gc.gridy = y;
		gc.gridwidth = 1;
		gb.setConstraints(oStyles, gc);
		optionsPanel.add(oStyles);

		pbFormatOutput = new UIButton("Create/Edit Format...");
		pbFormatOutput.setMnemonic(KeyEvent.VK_F);
		pbFormatOutput.addActionListener(this);
		gc.gridy = y;
		//gc.weightx = 10;
		gb.setConstraints(pbFormatOutput, gc);
		optionsPanel.add(pbFormatOutput);
		y++;

		gc.gridwidth = 3;

		optimizeForWord = new JCheckBox("Optimise for Word");
		optimizeForWord.addItemListener(this);
		optimizeForWord.setSelected(false);
		optimizeForWord.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(optimizeForWord, gc);
		//optionsPanel.add(optimizeForWord);

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		optionsPanel.add(sep);
		gc.insets = new Insets(0,0,0,0);

		includeNavigationBar = new JCheckBox("Include a navigation menu");
		includeNavigationBar.addItemListener(this);
		includeNavigationBar.setSelected(false);
		includeNavigationBar.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNavigationBar, gc);
		//optionsPanel.add(includeNavigationBar);

		JLabel label = new JLabel(" ");
		gc.gridy = y;
		y++;
		gb.setConstraints(label, gc);
		optionsPanel.add(label);

		includeLinks = new JCheckBox("Include link labels");
		includeLinks.addItemListener(this);
		includeLinks.setSelected(false);
		includeLinks.setFont(font);
		gc.gridy = y;
		y++;
		//gb.setConstraints(includeLinks, gc);
		//optionsPanel.add(includeLinks);

		includeNodeAuthor = new JCheckBox("Include node authors");
		includeNodeAuthor.addItemListener(this);
		includeNodeAuthor.setSelected(false);
		includeNodeAuthor.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeAuthor, gc);
		optionsPanel.add(includeNodeAuthor);

		includeImage = new JCheckBox("Include images");
		includeImage.addItemListener(this);
		includeImage.setSelected(true);
		includeImage.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeImage, gc);
		optionsPanel.add(includeImage);

		cbIncludeTags = new JCheckBox("Include tags");
		cbIncludeTags.addItemListener(this);
		cbIncludeTags.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeTags, gc);
		optionsPanel.add(cbIncludeTags);

		cbIncludeViews = new JCheckBox("Include views");
		cbIncludeViews.addItemListener(this);
		cbIncludeViews.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeViews, gc);
		optionsPanel.add(cbIncludeViews);

		ButtonGroup bg = new ButtonGroup();

		inlineView = new JRadioButton("   Include tags / views as inline text");
		inlineView.addItemListener(this);
		inlineView.setSelected(false);
		inlineView.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(inlineView, gc);
		bg.add(inlineView);
		optionsPanel.add(inlineView);

		newView = new JRadioButton("   Show tags / views in new window");
		newView.addItemListener(this);
		newView.setSelected(false);
		newView.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(newView, gc);
		bg.add(newView);
		optionsPanel.add(newView);

		sep = new JSeparator();
		gc.gridy = y;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		optionsPanel.add(sep);
		gc.insets = new Insets(0,0,0,0);

		// DETAIL PAGES SECTION

		JPanel detailPanel = new JPanel();
		detailPanel.setBorder(new EmptyBorder(10,10,10,10));
		detailPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		// CREATE DATE PANEL FIRST FOR REFERENCE REASONS
		JPanel datePanel = createDatePanel();

		noNodeDetail = new JRadioButton("No node detail pages");
		noNodeDetail.addItemListener(this);
		noNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(noNodeDetail, gc);
		optionsPanel.add(noNodeDetail);

		includeNodeDetail = new JRadioButton("Include all node detail pages");
		includeNodeDetail.addItemListener(this);
		includeNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeDetail, gc);
		optionsPanel.add(includeNodeDetail);

		includeNodeDetailDate = new JRadioButton("Include node detail pages for Dates: ");
		includeNodeDetailDate.addItemListener(this);
		includeNodeDetailDate.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeDetailDate, gc);
		optionsPanel.add(includeNodeDetailDate);

		ButtonGroup detailGroup = new ButtonGroup();
		detailGroup.add(noNodeDetail);
		detailGroup.add(includeNodeDetail);
		detailGroup.add(includeNodeDetailDate);

		// ADD DATE PANEL
		gc.gridy = y;
		y++;
		gb.setConstraints(datePanel, gc);
		optionsPanel.add(datePanel);

		JLabel other = new JLabel(" ");
		gc.gridy = y;
		y++;
		gb.setConstraints(other, gc);
		optionsPanel.add(other);

		displayDetailDates = new JCheckBox("Display detail page dates");
		displayDetailDates.addItemListener(this);
		displayDetailDates.setSelected(false);
		displayDetailDates.setFont(font);
		gc.gridy = y;
		gb.setConstraints(displayDetailDates, gc);
		optionsPanel.add(displayDetailDates);

		/*
		hideNodeNoDates = new JCheckBox("Hide nodes outside of dates");
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(hideNodeNoDates, gc);
		hideNodeNoDates.addItemListener(this);
		hideNodeNoDates.setSelected(false);
		detailPanel.add(hideNodeNoDates);
		*/

		return optionsPanel;
	}

	/**
	 * Create the styles choicebox.
	 */
	private JComboBox createStylesChoiceBox() {

		oStyles = new JComboBox();
		oStyles.setOpaque(true);
		oStyles.setEditable(false);
		oStyles.setEnabled(true);
		oStyles.setMaximumRowCount(30);
		oStyles.setFont( new Font("Dialog", Font.PLAIN, 12 ));

		reloadData();

		DefaultListCellRenderer comboRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
 		 		if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				}
				else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				setText((String)value);
				return this;
			}
		};
		oStyles.setRenderer(comboRenderer);

		ActionListener choiceaction = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	Thread choiceThread = new Thread("UIHTMLFormatDialog.createStylesChoiceBox") {
                	public void run() {
						if (oStyles != null) {
							APP_PROPERTIES.setOutlineFormat((String)oStyles.getSelectedItem());
						}
                	}
               	};
	            choiceThread.start();
        	}
		};
		oStyles.addActionListener(choiceaction);

		return oStyles;
	}

	private void reloadData() {
		try {
			vtStyles.clear();
			File main = new File(UIHTMLFormatDialog.DEFAULT_FILE_PATH);
			File styles[] = main.listFiles();
			File file = null;
			String sName = "";
			String value = "";
			String sFileName = "";
			int index = 0;
			int j = 0;
			if (styles.length > 0) {
				for (int i=0; i<styles.length; i++) {
					file = styles[i];
					sFileName = file.getName();
					if (!sFileName.startsWith(".") && sFileName.endsWith(".properties")) {
						Properties styleProp = new Properties();
						styleProp.load(new FileInputStream(file));
						value = styleProp.getProperty("status");
						if (value.equals("active")) {
							value = styleProp.getProperty("name");
							if (value != null) {
								sName = value;
								if (sName.equals(APP_PROPERTIES.getOutlineFormat())) {
									index = j+1;
								}
								vtStyles.add(sName);
							}
							j++;
						}
					}
				}
				vtStyles = UIUtilities.sortList(vtStyles);
				vtStyles.insertElementAt("< Select An Outline Format >", 0);
				DefaultComboBoxModel comboModel = new DefaultComboBoxModel(vtStyles);
				oStyles.setModel(comboModel);
				oStyles.setSelectedIndex(index);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.reloadData) " + ex.getMessage());
		}
	}

	/**
	 * Crate the panel hold the node detail pages date filter options.
	 */
	private JPanel createDatePanel() {

		JPanel panel = new JPanel(new BorderLayout());

		fromPanel = new UIDatePanel("From: ");
		panel.add(fromPanel, BorderLayout.WEST);

		toPanel = new UIDatePanel("To: ");
		panel.add(toPanel, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Create the list to display anchor images.
	 */
	private void createAnchorImageList() {

   	 	String[] images = {sBaseAnchorPath+"anchor0.gif", sBaseAnchorPath+"anchor1.gif", sBaseAnchorPath+"anchor2.gif", sBaseAnchorPath+"anchor3.gif", sBaseAnchorPath+"anchor4.gif",
							sBaseAnchorPath+"anchor5.gif", sBaseAnchorPath+"anchor6.gif", sBaseAnchorPath+"anchor7.gif"};

		lstAnchorImages = new UINavList(images);
		lstAnchorImages.setEnabled(false);
		lstAnchorImages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        anchorImageListRenderer = new AnchorImageCellRenderer();
		lstAnchorImages.setCellRenderer(anchorImageListRenderer);
		lstAnchorImages.setBorder(new CompoundBorder(new LineBorder(Color.gray ,1), new EmptyBorder(5,5,5,5)));
		imagescroll = new JScrollPane(lstAnchorImages);
		imagescroll.setPreferredSize(new Dimension(150, 60));

		MouseListener fontmouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					String image = (String)lstAnchorImages.getSelectedValue();
					setAnchorImage(image);
				}
			}
		};
		KeyListener fontkey = new KeyAdapter() {
           	public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.getModifiers() == 0)) {
					String image = (String)lstAnchorImages.getSelectedValue();
					setAnchorImage(image);
				}
			}
		};

		lstAnchorImages.addKeyListener(fontkey);
		lstAnchorImages.addMouseListener(fontmouse);
	}

	/**
	 * Helper class to render the anchor image list.
	 */
	public class AnchorImageCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		public AnchorImageCellRenderer() {
			super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
		}

		public Component getListCellRendererComponent(
        	JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus)
            {

 	 		if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setText((String)value);
			setHorizontalTextPosition(SwingConstants.TRAILING);
			setIconTextGap(6);
			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			ImageIcon image = new ImageIcon((String)value);
   			setIcon(image);

			return this;
		}
	}

	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle action events coming from the buttons.
 	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbExport) {
				onExport();
			}
			else if (source == pbViews) {
				onViews();
			}
			else if (source == pbFormatOutput) {
				UIHTMLFormatDialog dialog2 = new UIHTMLFormatDialog(ProjectCompendium.APP);
				dialog2.setVisible(true);
				while (dialog2.isVisible()) {}
				reloadData();
			}
			else if (source == pbBrowse) {
				onBrowse();
			}
			else if (source == pbClose) {
				onCancel(false);
			}
		}
	}

	/**
	 * Open the file browser dialog for the user to select an anchor image.
	 */
	private void onBrowse() {

		UIFileFilter gifFilter = new UIFileFilter(new String[] {"gif"}, "GIF Image Files");

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle("Select image for anchor...");
		fileDialog.setFileFilter(gifFilter);
		fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
		fileDialog.setRequiredExtension(".gif");

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		// AND MUST USE ABSOUTE PATH, AS RELATIVE PATH REMOVES THE '/'
		/*File filepath = new File("");
		String sPath = filepath.getAbsolutePath();
		File file = new File(sPath+ProjectCompendium.sFS+"Linked Files"+ProjectCompendium.sFS);
		if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}*/

		String fileName = "";
		UIUtilities.centerComponent(fileDialog, this);
		int retval = fileDialog.showDialog(this, null);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	fileName = fileDialog.getSelectedFile().getAbsolutePath();

				if (fileName != null) {
					if ( fileName.toLowerCase().endsWith(".gif") ) {
						setAnchorImage(fileName);
					}
				}
			}
		}
	}

	/**
	 * Open the views dialog for the user to select views to export.
	 */
	private void onViews() {

		if (viewsDialog == null) {
			viewsDialog = new UIExportMultipleViewDialog(this);
			viewsDialog.setVisible(true);
		}
		else {
			viewsDialog.setVisible(true);
		}
	}

	/**
	 * Apply the export options previously saved, to the various ui elements.
	 */
	private void applyLoadedProperties() {

		displayInDifferentPages.setSelected(EXPORT_PROPERTIES.isDisplayInDifferentPages());

		fullDepth.setSelected(EXPORT_PROPERTIES.getDepth() == 2);
		oneDepth.setSelected(EXPORT_PROPERTIES.getDepth() == 1);
		currentDepth.setSelected(EXPORT_PROPERTIES.getDepth() == 0);

		anchorImage.setText(EXPORT_PROPERTIES.getAnchorImage());
		includeNodeAnchor.setSelected(EXPORT_PROPERTIES.isIncludeNodeAnchors());
		includeDetailAnchor.setSelected(EXPORT_PROPERTIES.isIncludeDetailAnchors());
		useAnchorNumbers.setSelected(EXPORT_PROPERTIES.isUseAnchorNumbers());
		useAnchorImages.setSelected(!EXPORT_PROPERTIES.isUseAnchorNumbers());

		//toPanel.setDate(toDate);
		//fromPanel.setDate(fromDate);

		includeNodeDetail.setSelected(EXPORT_PROPERTIES.isAddNodeDetail());
		includeNodeDetailDate.setSelected(EXPORT_PROPERTIES.isAddNodeDetailDate());
		if (!EXPORT_PROPERTIES.isAddNodeDetail()
		        && !EXPORT_PROPERTIES.isAddNodeDetailDate())
		{
			noNodeDetail.setSelected(true);
		}

		displayDetailDates.setSelected(EXPORT_PROPERTIES.isDisplayDetailDates());
		includeNodeAuthor.setSelected(EXPORT_PROPERTIES.isAddNodeAuthor());
		includeImage.setSelected(EXPORT_PROPERTIES.isAddNodeImage());
		includeLinks.setSelected(EXPORT_PROPERTIES.isIncludeLinks());
		optimizeForWord.setSelected(EXPORT_PROPERTIES.isOptimizeForWord());

		//hideNodeNoDates.setSelected(bHideNodeNoDates);

		cbIncludeViews.setSelected(EXPORT_PROPERTIES.isIncludeViews());
		cbIncludeTags.setSelected(EXPORT_PROPERTIES.isIncludeTags());

		includeNavigationBar.setSelected(EXPORT_PROPERTIES.isIncludeNavigationBar());
		inlineView.setSelected(EXPORT_PROPERTIES.isInlineView());
		newView.setSelected(EXPORT_PROPERTIES.isNewView());

		cbOpenAfter.setSelected(EXPORT_PROPERTIES.isOpenAfter());
		cbToZip.setSelected(EXPORT_PROPERTIES.isZip());
		cbWithRefs.setSelected(EXPORT_PROPERTIES.isIncludeRefs());

		if (!hasSelectedViews()) {
		    EXPORT_PROPERTIES.setSelectedViewsOnly(false);
		}
		selectedViews.setSelected(EXPORT_PROPERTIES.isSelectedViewsOnly());

		otherViews.setSelected(EXPORT_PROPERTIES.isOtherViews());

		if (!EXPORT_PROPERTIES.isSelectedViewsOnly()
		        && !EXPORT_PROPERTIES.isOtherViews())
		{
		    allNodes.setSelected(true);
		}

	   	lstAnchorImages.setSelectedValue(
	   	    EXPORT_PROPERTIES.getAnchorImage(), true);
	}


	/**
	 * Return the to date for filtering node detail pages.
	 * @return GregorianCalendar, the to date for filtering node detail pages.
	 */
	public GregorianCalendar getToDate() {
		return toPanel.getDateEnd();
	}

	/**
	 * Return the from date for filtering node detail pages.
	 * @return GregorianCalendar, the from date for filtering node detail pages.
	 */
	public GregorianCalendar getFromDate() {
		return fromPanel.getDate();
	}

	/**
	 * Set the anchor image to use.
 	 * @param sImage, the path of the anchor image to use.
	 */
	public void setAnchorImage(String sImage) {
		if (sImage != null && !sImage.equals("")) {
		    EXPORT_PROPERTIES.setAnchorImage(sImage);
			anchorImage.setText(sImage);
		}
	}

	/**
	 * Set the current view to being exported.
 	 * @param view com.compendium.core.datamodel.View, the current view being exported.
	 */
	public void setCurrentView(View view) {
		currentView = view;
	}

	/**
	 * Check that the dates for filtering node detail pages have been entered correctly.
	 */
	public boolean checkDates() {
		if (fromPanel.checkDate() && toPanel.checkDate())
			return true;

		return false;
	}

	/**
	 * Records the fact that a checkbox / radio button state has been changed and stores the new data.
	 * @param e, the associated ItemEvent.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();


		if (source == displayInDifferentPages) {
			EXPORT_PROPERTIES.setDisplayInDifferentPages(
			        displayInDifferentPages.isSelected());
			if (displayInDifferentPages.isSelected()) {
				if (titlefield != null) {
					titlefield.setEditable(true);
					titleLabel.setEnabled(true);
					titlefield.repaint();
				}
			}
			else {
				if (titlefield != null) {
					titlefield.setText("");
					titlefield.setEditable(false);
					titleLabel.setEnabled(false);
					titlefield.repaint();
				}
			}
		}
		else if (source == cbWithRefs) {
			EXPORT_PROPERTIES.setIncludeRefs(cbWithRefs.isSelected());
		}
		else if (source == cbToZip) {
		    EXPORT_PROPERTIES.setZip(cbToZip.isSelected());
			if (cbToZip.isSelected()) {
				cbOpenAfter.setSelected(false);
				cbOpenAfter.setEnabled(false);
				EXPORT_PROPERTIES.setOpenAfter(false);
			}
			else {
				cbOpenAfter.setEnabled(true);
			}
		}
		else if (source == cbOpenAfter) {
		    EXPORT_PROPERTIES.setOpenAfter(cbOpenAfter.isSelected());
		}
		else if (source == includeDetailAnchor) {
			EXPORT_PROPERTIES.setIncludeDetailAnchors(
			    includeDetailAnchor.isSelected());
		}
		else if (source == includeNodeAnchor) {
			EXPORT_PROPERTIES.setIncludeNodeAnchors(
			    includeNodeAnchor.isSelected());
		}
		else if (source == useAnchorNumbers) {
			EXPORT_PROPERTIES.setUseAnchorNumbers(useAnchorNumbers.isSelected());
			if (useAnchorNumbers.isSelected()) {
				pbBrowse.setEnabled(false);
				lstAnchorImages.setEnabled(false);
			}
			else if (!useAnchorNumbers.isSelected() && !useAnchorNumbers.isSelected()) {
				pbBrowse.setEnabled(true);
				lstAnchorImages.setEnabled(true);
			}
		}
		else if (source == useAnchorImages) {
			EXPORT_PROPERTIES.setUseAnchorNumbers(!useAnchorImages.isSelected());
			if (useAnchorImages.isSelected()) {
				pbBrowse.setEnabled(true);
				lstAnchorImages.setEnabled(true);
			}
			else if (!useAnchorImages.isSelected() && !useAnchorImages.isSelected()) {
				pbBrowse.setEnabled(false);
				lstAnchorImages.setEnabled(false);
			}
		}
		else if (source == fullDepth && fullDepth.isSelected()) {
			EXPORT_PROPERTIES.setDepth(2);

			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
		}
		else if (source == oneDepth && oneDepth.isSelected()) {
		    EXPORT_PROPERTIES.setDepth(1);

			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
		}
		else if (source == currentDepth && currentDepth.isSelected()) {
		    EXPORT_PROPERTIES.setDepth(0);

			if (allNodes.isSelected()) {
				displayInDifferentPages.setSelected(false);
				displayInDifferentPages.setEnabled(false);
				titlefield.setEditable(false);
				titleLabel.setEnabled(false);
			}
			else {
				displayInDifferentPages.setEnabled(true);
				displayInDifferentPages.repaint();
			}
		}

		else if (source == selectedViews && selectedViews.isSelected()) {
		    EXPORT_PROPERTIES.setOtherViews(false);
			EXPORT_PROPERTIES.setSelectedViewsOnly(true);

			pbViews.setEnabled(false);
			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
			updateViewsList();
		}
		else if (source == allNodes && allNodes.isSelected()) {
		    EXPORT_PROPERTIES.setOtherViews(false);
			EXPORT_PROPERTIES.setSelectedViewsOnly(false);

			pbViews.setEnabled(false);

			if (currentDepth.isSelected()) {
				displayInDifferentPages.setSelected(false);
				displayInDifferentPages.setEnabled(false);
				titlefield.setEditable(false);
				titleLabel.setEnabled(false);
			}
			else {
				displayInDifferentPages.setEnabled(true);
				displayInDifferentPages.repaint();
			}
			updateViewsList();
		}
		else if (source == otherViews && otherViews.isSelected()) {
		    EXPORT_PROPERTIES.setOtherViews(true);
			EXPORT_PROPERTIES.setSelectedViewsOnly(false);

			pbViews.setEnabled(true);
			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
			updateViewsList();
		}
		else if (source == includeNodeAuthor) {
		    EXPORT_PROPERTIES.setAddNodeAuthor(includeNodeAuthor.isSelected());
		}
		else if (source == displayDetailDates) {
		    EXPORT_PROPERTIES.setDisplayDetailDates(displayDetailDates.isSelected());
		}
		else if (source == hideNodeNoDates) {
		    EXPORT_PROPERTIES.setHideNodeNoDate(hideNodeNoDates.isSelected());
		}
		else if (source == noNodeDetail && noNodeDetail.isSelected()) {
		    EXPORT_PROPERTIES.setAddNodeDetail(false);
		    EXPORT_PROPERTIES.setAddNodeDetailDate(false);

			toPanel.setDateEnabled(false);
			fromPanel.setDateEnabled(false);
		}
		else if (source == includeNodeDetail && includeNodeDetail.isSelected()) {
		    EXPORT_PROPERTIES.setAddNodeDetail(true);
		    EXPORT_PROPERTIES.setAddNodeDetailDate(false);

			toPanel.setDateEnabled(false);
			fromPanel.setDateEnabled(false);
		}
		else if (source == includeNodeDetailDate && includeNodeDetailDate.isSelected()) {
		    EXPORT_PROPERTIES.setAddNodeDetail(false);
		    EXPORT_PROPERTIES.setAddNodeDetailDate(true);

			toPanel.setDateEnabled(true);
			fromPanel.setDateEnabled(true);
		}
		else if (source == includeImage) {
		    EXPORT_PROPERTIES.setAddNodeImage(includeImage.isSelected());
		}
		else if (source == optimizeForWord) {
		    EXPORT_PROPERTIES.setOptimizeForWord(optimizeForWord.isSelected());
		}

		else if (source == includeLinks) {
		    EXPORT_PROPERTIES.setIncludeLinks(includeLinks.isSelected());
		}
		else if (source == includeNavigationBar) {
		    EXPORT_PROPERTIES.setIncludeNavigationBar(
		            includeNavigationBar.isSelected());
		}
		else if (source == cbIncludeTags) {
		    EXPORT_PROPERTIES.setIncludeTags(cbIncludeTags.isSelected());
			if ((cbIncludeViews != null && !cbIncludeViews.isSelected()) && !cbIncludeTags.isSelected()) {
				inlineView.setEnabled(false);
				newView.setEnabled(false);
			} else {
				inlineView.setEnabled(true);
				newView.setEnabled(true);
			}
		}
		else if (source == cbIncludeViews) {
		    EXPORT_PROPERTIES.setIncludeViews(cbIncludeViews.isSelected());
			if (!cbIncludeViews.isSelected() && (cbIncludeTags != null && !cbIncludeTags.isSelected())) {
				inlineView.setEnabled(false);
				newView.setEnabled(false);
			} else {
				inlineView.setEnabled(true);
				newView.setEnabled(true);
			}
		}
		else if (source == inlineView) {
		    EXPORT_PROPERTIES.setInlineView(inlineView.isSelected());
		}
		else if (source == newView) {
		    EXPORT_PROPERTIES.setNewView(newView.isSelected());
		}
	}

	/******* EXPORT *******************************************************/

	/**
	 * Handle the export action. Rquest the export file be selected.
	 * @see #processExport
	 */
	public void onExport() {

		// CHECK ALL DATE INFORMATION ENTERED, IF REQUIRED
		if (EXPORT_PROPERTIES.isAddNodeDetailDate()) {
			if (!checkDates()) {
				ProjectCompendium.APP.displayMessage("Please complete all date information", "Date Error");
				return;
			}
		}

		if (otherViews.isSelected()) {
			if(viewsDialog == null || (viewsDialog.getTable().getSelectedRows()).length <= 0) {
				ProjectCompendium.APP.displayMessage("Please select at least one view to export", "Web Outline Export");
				return;
			}
		}

		boolean toZip = cbToZip.isSelected();
		if (toZip) {
			UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files");

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle("Enter the file name to Export to...");
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText("Save");
			fileDialog.setRequiredExtension(".zip");

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".zip") ) {
							fileName = fileName+".zip";
						}
					}
				}
			}
		}
		else
		{
			//NOT selected export to word, therefore export to HTML
			if (cbConvertToWord.isSelected() == false)
			{
				showTrace ("cbExportToWord is false");
				UIFileFilter filter = new UIFileFilter(new String[] {"html"}, "HTML Files");

				UIFileChooser fileDialog = new UIFileChooser();
				fileDialog.setDialogTitle("Enter the file name to Export to...");
				fileDialog.setFileFilter(filter);
				fileDialog.setApproveButtonText("Save");
				fileDialog.setRequiredExtension(".html");

				// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
				File file = new File(exportDirectory+ProjectCompendium.sFS);
				if (file.exists())
				{
					fileDialog.setCurrentDirectory(file);
				}

				int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
				if (retval == JFileChooser.APPROVE_OPTION)
				{
					if ((fileDialog.getSelectedFile()) != null)
					{

						fileName = fileDialog.getSelectedFile().getAbsolutePath();
						File fileDir = fileDialog.getCurrentDirectory();
						exportDirectory = fileDir.getPath();

						if (fileName != null)
						{
							if ( !fileName.toLowerCase().endsWith(".html") )
							{
								fileName = fileName+".html";
							}
						}
					}
				}
			}
			else
			{
				showTrace ("cbExportToWord is true");
				//export to word, therefore set default HTML export location
				//exportDirectory = System.getenv("CompendiumUserPath") + "\\Temp\\Exports\\";
				exportDirectory = System.getenv("CompendiumUserPath") + "\\Exports\\";

				fileName = exportDirectory + "temp.html";
			}
			showTrace ("exportDirectory is " + exportDirectory);
			showTrace ("fileName is " + fileName);
		}

		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		if (fileName != null && !fileName.equals("")) {
			if (!processExport())
				onCancel(false);
			else {
				if (EXPORT_PROPERTIES.isOpenAfter()) {
					ExecuteControl.launch(fileName);
				}
				onCancel(true);
			}
		}
		setCursor(Cursor.getDefaultCursor());

		//if selected word option
		if (cbConvertToWord.isSelected() == true)
		{
			try
			{
				Runtime rt = Runtime.getRuntime();
				String cmd = System.getenv("windir") + "\\system32\\cmd.exe /C START /WAIT CScript \"" + System.getenv("CompendiumSysPath") + "\\open_word_with_template.vbs\" /NoLogo";
				showTrace("cmd line is " + cmd);
				Process pr = rt.exec(cmd);

			}
			catch(Exception x)
			{
				x.printStackTrace();
			}
		}

	}


	/**
	 *	Process the export.
	 */
	public boolean processExport() {
        oHTMLExport = new HTMLOutline(EXPORT_PROPERTIES.isAddNodeDetail(),
                EXPORT_PROPERTIES.isAddNodeDetailDate(),
                EXPORT_PROPERTIES.isAddNodeAuthor(), nStartExportAtLevel,
                fileName, EXPORT_PROPERTIES.isZip());

		if (EXPORT_PROPERTIES.isAddNodeDetailDate()) {
			GregorianCalendar fDate = getFromDate();
			GregorianCalendar tDate = getToDate();
			EXPORT_PROPERTIES.setFromDate(fDate.getTime().getTime());
			EXPORT_PROPERTIES.setToDate(tDate.getTime().getTime());
			if (tDate != null && fDate != null) {
				oHTMLExport.setFromDate(fDate);
				oHTMLExport.setToDate(tDate);
			}
		}

		oHTMLExport.setIncludeLinks(EXPORT_PROPERTIES.isIncludeLinks());
		oHTMLExport.setIncludeImage(EXPORT_PROPERTIES.isAddNodeImage());
		oHTMLExport.setIncludeNodeAnchors(EXPORT_PROPERTIES.isIncludeNodeAnchors());
		oHTMLExport.setIncludeDetailAnchors(EXPORT_PROPERTIES.isIncludeDetailAnchors());
		oHTMLExport.setUseAnchorNumbers(EXPORT_PROPERTIES.isUseAnchorNumbers());
		if (!EXPORT_PROPERTIES.isUseAnchorNumbers())
			oHTMLExport.setAnchorImage(EXPORT_PROPERTIES.getAnchorImage());

		oHTMLExport.setTitle(titlefield.getText());
		oHTMLExport.setDisplayInDifferentPages(
		    EXPORT_PROPERTIES.isDisplayInDifferentPages());
		oHTMLExport.setDisplayDetailDates(EXPORT_PROPERTIES.isDisplayDetailDates());
		oHTMLExport.setHideNodeNoDates(EXPORT_PROPERTIES.isHideNodeNoDate());
		oHTMLExport.setIncludeNavigationBar(
		    EXPORT_PROPERTIES.isIncludeNavigationBar());
		oHTMLExport.setInlineView(EXPORT_PROPERTIES.isInlineView());
		oHTMLExport.setNewView(EXPORT_PROPERTIES.isNewView());
		oHTMLExport.setIncludeViews(EXPORT_PROPERTIES.isIncludeViews());
		oHTMLExport.setIncludeTags(EXPORT_PROPERTIES.isIncludeTags());
		oHTMLExport.setOptimizeForWord(EXPORT_PROPERTIES.isOptimizeForWord());

		oHTMLExport.setIncludeFiles(EXPORT_PROPERTIES.isIncludeRefs());

		boolean sucessful = false;

		if (printExport(oHTMLExport, otherViews.isSelected(),
		    EXPORT_PROPERTIES.isSelectedViewsOnly(),
		    EXPORT_PROPERTIES.getDepth()))
		{
			oHTMLExport.print();
			sucessful = true;
		}

		return sucessful;
	}

	/**
	 * Update the list of view to export;
	 */
	public void updateViewsList() {
		String sViews = "";
		Vector views = checkSelectedViews();
		int count = views.size();
		for (int i = 0; i < count; i++) {
			View view = (View)views.elementAt(i);
			sViews += view.getLabel()+"\n";
		}
		oTextArea.setText(sViews);
	}

	/** Return true if any views are selected, else false;*/
	private boolean hasSelectedViews() {

		Enumeration nodes = null;

		if (currentFrame instanceof UIMapViewFrame) {
			UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
			nodes = uiViewPane.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				UINode uinode = (UINode)en.nextElement();
				if (uinode.getNode() instanceof View) {
					return true;
				}
			}
		}
		else {
			UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
			nodes = uiList.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				NodePosition nodepos = (NodePosition)en.nextElement();
				if (nodepos.getNode() instanceof View) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the views to export depending on user options to display
	 */
	private Vector checkSelectedViews() {

		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector selectedViews = new Vector();

		// IF MULTIPLE VIEWS
		if (otherViews.isSelected()) {
			if (viewsDialog != null) {
				JTable table = viewsDialog.getTable();
				int [] selection = table.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					selectedViews.addElement(view);
				}
			}
		}
		else if (EXPORT_PROPERTIES.isSelectedViewsOnly()) {
			Enumeration nodes = null;
			Vector vtTemp = new Vector();
			if (currentFrame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
				nodes = uiViewPane.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					UINode uinode = (UINode)en.nextElement();
					if (uinode.getNode() instanceof View) {
						vtTemp.addElement(uinode.getNodePosition());
					}
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
				nodes = uiList.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					NodePosition nodepos = (NodePosition)en.nextElement();
					if (nodepos.getNode() instanceof View) {
						vtTemp.addElement(nodepos);
					}
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < vtTemp.size(); i++) {
				int yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
				for (int j = i+1; j < vtTemp.size(); j++) {
					int secondYPosition = ((NodePosition)vtTemp.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)vtTemp.elementAt(i);
						vtTemp.setElementAt(vtTemp.elementAt(j), i);
						vtTemp.setElementAt(np, j);
						yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
					}
				}
			}

			for(int j=0; j < vtTemp.size(); j++) {
				NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
				View innerview = (View)nodePos.getNode();
				selectedViews.addElement(innerview);
			}
		}
		else {
			selectedViews.addElement(currentView);
		}

		return selectedViews;
	}

	/**
	 * Get the views to export depending on user options.
	 */
	private Vector getSelectedViews(HTMLOutline oHTMLExport, boolean otherViews, boolean bSelectedViewsOnly, int depth) {
		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector selectedViews = new Vector();

		// IF MULTIPLE VIEWS
		if (otherViews) {
			oHTMLExport.setCurrentViewAsHomePage(false);

			JTable table = viewsDialog.getTable();
			int [] selection = table.getSelectedRows();
			for (int i = 0; i < selection.length; i++) {
				View view = (View)table.getModel().getValueAt(selection[i],0);
				selectedViews.addElement(view);
			}

			if (depth == 1) {
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, false);
				}
			}
			else if (depth == 2) {
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, true);
				}
			}
		}
		else if (bSelectedViewsOnly) {

			oHTMLExport.setCurrentViewAsHomePage(false);
			Enumeration nodes = null;
			Vector vtTemp = new Vector();

			if (currentFrame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
				nodes = uiViewPane.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					UINode uinode = (UINode)en.nextElement();
					if (uinode.getNode() instanceof View) {
						vtTemp.addElement(uinode.getNodePosition());
					}
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
				nodes = uiList.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					NodePosition nodepos = (NodePosition)en.nextElement();
					if (nodepos.getNode() instanceof View) {
						vtTemp.addElement(nodepos);
					}
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < vtTemp.size(); i++) {
				int yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
				for (int j = i+1; j < vtTemp.size(); j++) {
					int secondYPosition = ((NodePosition)vtTemp.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)vtTemp.elementAt(i);
						vtTemp.setElementAt(vtTemp.elementAt(j), i);
						vtTemp.setElementAt(np, j);
						yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
					}
				}
			}

			for(int j=0; j < vtTemp.size(); j++) {
				NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
				View innerview = (View)nodePos.getNode();
				selectedViews.addElement(innerview);
			}

			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			if (depth == 1) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, false);
				}
			} else if (depth == 2) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, true);
				}
			}
		}
		else {
			// IF JUST CURRENT VIEW
			oHTMLExport.setCurrentViewAsHomePage(true);

			selectedViews.addElement(currentView);

			if (depth == 1) {
				htCheckDepth.put((Object)currentView.getId(), currentView);
				selectedViews = getChildViews(currentView, selectedViews, false);
			}
			else if (depth == 2) {
				htCheckDepth.put((Object)currentView.getId(), currentView);
				selectedViews = getChildViews(currentView, selectedViews, true);
			}
		}

		return selectedViews;
	}

	/**
	 * Helper method when getting view to export.
	 * @param view com.compendium.core.datamodel.View, the view to get the child nodes for.
	 * @param childViews, the list of views aquired.
	 * @param fullDepth, are we searching to full depth?
	 */
	private Vector getChildViews(View view, Vector childViews, boolean fullDepth) {

		try {
			Vector vtTemp = vs.getNodePositions(session, view.getId());
			Vector nodePositionList = new Vector();

			//EXTRACT THE VIEWS AND ADD TO nodePositionList VECTOR
			for(Enumeration en = vtTemp.elements();en.hasMoreElements();) {
				NodePosition nodePos = (NodePosition)en.nextElement();
				NodeSummary node = nodePos.getNode();
				if (node instanceof View) {
					nodePositionList.addElement(nodePos);
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < nodePositionList.size(); i++) {
				int yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
				for (int j = i+1; j < nodePositionList.size(); j++) {
					int secondYPosition = ((NodePosition)nodePositionList.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)nodePositionList.elementAt(i);
						nodePositionList.setElementAt(nodePositionList.elementAt(j), i);
						nodePositionList.setElementAt(np, j);
						yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
					}
				}
			}

			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			for (int k = 0; k < nodePositionList.size(); k++) {
				NodePosition np = (NodePosition)nodePositionList.elementAt(k);
				View innerview = (View)np.getNode();

				if (!htCheckDepth.containsKey((Object)innerview.getId())) {
					htCheckDepth.put((Object)innerview.getId(), innerview);
					childViews.addElement(np.getNode());
				}
			}

			if (fullDepth) {
				//GET CHILD VIEWS CHILDREN
				for (int j = 0; j < nodePositionList.size(); j++) {
					NodePosition np = (NodePosition)nodePositionList.elementAt(j);
					View innerview = (View)np.getNode();

					if (!htChildrenAdded.containsKey((Object)innerview.getId())) {
						htChildrenAdded.put((Object)innerview.getId(), innerview);
						childViews = getChildViews(innerview, childViews, fullDepth);
					}
				}
			}
		}
		catch (Exception e) {
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.getChildViews) \n\n" + e.getMessage());
		}

		return childViews;
	}


	/**
	 * Create the HTML files.
	 */
	public boolean printExport(HTMLOutline oHTMLExport, boolean bOtherViews, boolean bSelectedViewsOnly, int depth) {
		ProjectCompendium.APP.setWaitCursor();
		Vector selectedViews = getSelectedViews(oHTMLExport, bOtherViews, bSelectedViewsOnly, depth);
		if (selectedViews.size() == 0)
			return true;

		arrange = new UIArrangeLeftRight();

   		// CYCLE THROUGH selectedViews VECTOR
		try {
			int count = selectedViews.size();
			for(int i=0; i < count; i++) {

				//clear the hashtables and vectors for a new export
				htNodesLevel.clear();
				htNodes.clear();
				htNodesBelow.clear();

				View view = (View)selectedViews.elementAt(i);
				if (view == null)
					continue;

				if (!view.isMembersInitialized()) {
					view.initializeMembers();
				}

				oHTMLExport.runGenerator((NodeSummary)view, 0, -1);
				ProjectCompendium.APP.setStatus("Calculating export data ......");

				if (!arrange.processView(view)) {
					return false;
				}

				htNodes = arrange.getNodes();
				htNodesLevel = arrange.getNodesLevel();
				htNodesBelow = arrange.getNodesBelow();

				nodeLevelList = arrange.getNodeLevelList();

				//now print the nodes
				ProjectCompendium.APP.setStatus("Generating export file ......");

				if (nodeLevelList.size() > 0) {
					// CYCLE THROUGH NODES SORTED BY YPOS AND PRINT THEM AND THIER CHILDREN
					for(Enumeration f = ((Vector)nodeLevelList.elementAt(0)).elements();f.hasMoreElements();) {

						String nodeToPrintId = (String)f.nextElement();
						NodeSummary nodeToPrint = (NodeSummary)htNodes.get(nodeToPrintId);
						if (view.getType() == ICoreConstants.LISTVIEW) {
							printNode(nodeToPrintId, true, oHTMLExport);
						}
						else {
							printNode(nodeToPrintId, false, oHTMLExport);
						}
					}
				}
				ProjectCompendium.APP.setStatus("Finished exporting " + view.getLabel() + " to HTML.");
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.printExport) \n\n" + ex.getMessage());
		}

		ProjectCompendium.APP.setDefaultCursor();
		ProjectCompendium.APP.setStatus("");

		return true;
	}

	/** holds the information about nodes recursed when processing the outline data*/
	private Hashtable nodesRecursed = new Hashtable(51);

	/** max number a node can recurse */
	private int recursionCount  	= 3;

	/**
	 * Create the HTML files for each given node.
 	 * @param nodeToPrintId, the id of the node to process.
	 * @param printingList, is the current view a list.
	 */
	private void printNode(String nodeToPrintId, boolean printingList, HTMLOutline oHTMLExport) {

		Integer count = new Integer(1);
		if(nodesRecursed.containsKey(nodeToPrintId)){
			count = (Integer) nodesRecursed.get(nodeToPrintId);
			if(count.intValue() > recursionCount) {
				return;
			} else {
				count = new Integer(count.intValue() + 1);
				nodesRecursed.put(nodeToPrintId, count);
			}
		} else {
			nodesRecursed.put(nodeToPrintId, count);
		}

		if (!printingList) {
			nodeIndex = -1;
		} else {
			nodeIndex++;
		}

		NodeSummary nodeToPrint = (NodeSummary)htNodes.get(nodeToPrintId);

		int lev = ((Integer)htNodesLevel.get(nodeToPrint.getId())).intValue();

		oHTMLExport.runGenerator(nodeToPrint, lev, nodeIndex);

		Vector nodeChildren = (Vector)htNodesBelow.get(nodeToPrintId);
		if (nodeChildren != null) {
			//System.out.println("printing children for "+nodeToPrint.getLabel());

			for (int i = 0; i < nodeChildren.size(); i++) {
				printNode((String)nodeChildren.elementAt(i), printingList, oHTMLExport);
			}
		}
	}

	/**
	 * Load the user saved options for exporting.
	 */
	private void loadProperties() {

		setAnchorImage(EXPORT_PROPERTIES.getAnchorImage());


	}

	/**
	 * Handle the close action. Closes the export dialog.
	 */
	public void onCancel() {
		onCancel(false);
	}

	/**
	 * Handle the close action. Saves the current setting and closes the export dialog.
	 */
	public void onCancel(boolean successful) {

		if (viewsDialog != null) {
			viewsDialog.dispose();
		}

		setVisible(false);
		dispose();
		EXPORT_PROPERTY_MANAGER.save();

		if (fileName != null && successful && !EXPORT_PROPERTIES.isOpenAfter()) {
			ProjectCompendium.APP.displayMessage("Finished exporting into " + fileName, "Export Finished");
		}
	}

	public static void showTrace(String msg)
	{
	  //if (msg.length() > 0) System.out.println(msg);
	  System.out.println(
			   new Throwable().getStackTrace()[1].getLineNumber() +
			   " " + new Throwable().getStackTrace()[1].getFileName() +
			   " " + new Throwable().getStackTrace()[1].getMethodName() +
		           " " + msg);
	}

	public void setWordDocExportOptions ()
	{
	  	this.setTitle("Word Doc Export");
	  	cbConvertToWord.setVisible(false);
	}
}