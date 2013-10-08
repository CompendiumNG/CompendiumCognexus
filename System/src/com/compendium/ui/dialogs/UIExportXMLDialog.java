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
import java.lang.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.ProjectCompendium;
import com.compendium.io.xml.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;


/**
 * UIExportXMLDialog defines the export dialog, that allows
 * the user to export PC Map/List Views to an XML document
 *
 * @author	Michelle Bachler
 */
public class UIExportXMLDialog extends UIDialog implements ActionListener, ItemListener {

	/** The default directory to export to.*/
	private static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports";

	/** The parent frame for this dialog.*/
	private JFrame				oParent			= null;

	/** The button to start the export.*/
	private UIButton			pbExport		= null;

	/** The button to close the dialog without exporting.*/
	private UIButton			pbClose			= null;

	/** The button to open the help.*/
	private UIButton			pbHelp			= null;


	/** Whether to export view to thier full depth.*/
	private JRadioButton		rbAllDepths 	= null;

	/** Whether to only export to the current map depth.*/
	private	JRadioButton		rbCurrentDepth 	= null;

	private	JRadioButton		rbCurrentSession 	= null;

	/** Whether to export to a zip file.*/
	private JCheckBox       	cbToZip			= null;

	/** Whether to export to a zip file with stencil and linkgroup data.*/
	private JCheckBox       	cbWithStencilsAndLinkGroups		= null;

	/** Whether to export meeting and media index data.*/
	private JCheckBox       	cbWithMeetings	= null;

	/** The file browser dialog to specify the export file.*/
	private	FileDialog			fdgExport 		= null;

	/** The view frame of the view being exported.*/
	private UIViewFrame 		uiViewFrame 	= null;


	/**
	 * Initialize and draw the dialog.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIExportXMLDialog(JFrame parent) {

		super(parent, true);

	  	oParent = parent;

	  	setTitle("Export To XML");

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		JPanel oMainPanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		oMainPanel.setLayout(gb);

		gc.insets = new Insets(5,10,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.weightx=11;
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		oMainPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

        rbAllDepths = new JRadioButton("Export map(s) to full depth");
        rbAllDepths.setSelected(false);
        rbAllDepths.addActionListener(this);
        gc.gridy = y;
        gc.weightx=1;
        y++;
        gb.setConstraints(rbAllDepths, gc);
        oMainPanel.add(rbAllDepths);

		rbCurrentDepth = new JRadioButton("Export current map depth only");
		rbCurrentDepth.setSelected(true);
		rbCurrentDepth.addActionListener(this);
		gc.gridy = y;
		gc.weightx=10;
		y++;
		gb.setConstraints(rbCurrentDepth, gc);
		oMainPanel.add(rbCurrentDepth);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbAllDepths);
		rgGroup.add(rbCurrentDepth);

		sep = new JSeparator();

		gc.gridy = y;
		gc.weightx=11;
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		oMainPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

      	cbWithMeetings = new JCheckBox("Include Meeting & Media Index data");
      	cbWithMeetings.setSelected(false);
		gc.gridy = y;
		gc.weightx=5;
		y++;
		gb.setConstraints(cbWithMeetings, gc);
      	oMainPanel.add(cbWithMeetings);

		sep = new JSeparator();

		gc.gridy = y;
		gc.weightx=11;
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		oMainPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

      	cbToZip = new JCheckBox("Export to Zip Archive with images + Referenced files");
      	cbToZip.addItemListener(this);
      	cbToZip.setSelected(false);
		gc.gridy = y;
		gc.weightx=10;
		y++;
		gb.setConstraints(cbToZip, gc);
      	oMainPanel.add(cbToZip);

      	cbWithStencilsAndLinkGroups = new JCheckBox("Include your Stencils + Link Groups in Zip Archive");
      	cbWithStencilsAndLinkGroups.setEnabled(false);
      	cbWithStencilsAndLinkGroups.setSelected(false);
		gc.gridy = y;
		gc.weightx=5;
		y++;
		gb.setConstraints(cbWithStencilsAndLinkGroups, gc);
      	oMainPanel.add(cbWithStencilsAndLinkGroups);

		gc.insets = new Insets(15,10,10,10);
		gc.weightx=1;

		oContentPane.add(oMainPanel, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();

		setResizable(false);
		return;
	}

	private JPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbExport = new UIButton("Export...");
		pbExport.setMnemonic(KeyEvent.VK_E);
		pbExport.addActionListener(this);
		getRootPane().setDefaultButton(pbExport);
		oButtonPanel.addButton(pbExport);

		pbClose = new UIButton("Close");
		pbClose.setMnemonic(KeyEvent.VK_C);
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.export_xml", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Listener for checkbox changes.
	 * @param e, the associated ItemEvent object.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == cbToZip) {
			if (cbToZip.isSelected()) {
				cbWithStencilsAndLinkGroups.setEnabled(true);
			}
			else {
				cbWithStencilsAndLinkGroups.setSelected(false);
				cbWithStencilsAndLinkGroups.setEnabled(false);
			}
		}
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {
			if (source == pbExport) {
				onExport();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Set the current view frame to export.
	 * @param view com.compendium.ui.UIViewFrame, the current view to export.
	 */
	public void setCurrentView(UIViewFrame view) {
		uiViewFrame = view;
	}

	/**
	 * Handle the export action.
	 */
	public void onExport() {

		String fileName = "";
		//String directory = "";

		View currentView = uiViewFrame.getView();
		boolean hasReferenceNodes = false;

		if (0 < currentView.getReferenceNodes().size()) {

			hasReferenceNodes = true;

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

			UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
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
						this.requestFocus();
						setCursor(new Cursor(Cursor.WAIT_CURSOR));

						setVisible(false);
						boolean allDepths = rbAllDepths.isSelected();
						boolean withStencilsAndLinkGroups = cbWithStencilsAndLinkGroups.isSelected();
						boolean withMeetings = cbWithMeetings.isSelected();

						XMLExport export = new XMLExport(uiViewFrame, fileName, allDepths, true, toZip,
								withStencilsAndLinkGroups, withMeetings, true);
						export.start();

						dispose();
					}
				}
			}
		}

		else if (hasReferenceNodes) {

			//int retval = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "If you want to export the contents of your reference nodes as well,\n" +
			//		" click Cancel. Then select Export to XML again, and check the \nbox next to Export to Zip archive with images + Referenced files.");
			int retval = JOptionPane.showConfirmDialog(ProjectCompendium.APP,
				"You have unchecked the \"Export to Zip Archive\" option.\n" +
				"Export will create an XML file without including any referenced documents in your export.\n" +
				"Are you sure you want to do this?",
				"XML Export",
				JOptionPane.YES_NO_OPTION
				);

			if (0 == retval) {

				onXMLOnlyExport(fileName);

			}

		}

		else {

			onXMLOnlyExport(fileName);

		}
	}

	public void onXMLOnlyExport (String fileName) {

		UIFileFilter filter = new UIFileFilter(new String[] {"xml"}, "XML Files");

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle("Enter the file name to Export to...");
		fileDialog.setFileFilter(filter);
		fileDialog.setApproveButtonText("Save");
		fileDialog.setRequiredExtension(".xml");

	    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
	    File file = new File(exportDirectory+ProjectCompendium.sFS);
	    if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}

		UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
		int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File fileDir = fileDialog.getCurrentDirectory();
				exportDirectory = fileDir.getPath();

				if (fileName != null) {
					if ( !fileName.toLowerCase().endsWith(".xml") ) {
						fileName = fileName+".xml";
					}
					this.requestFocus();
					setCursor(new Cursor(Cursor.WAIT_CURSOR));

					setVisible(false);
					boolean allDepths = rbAllDepths.isSelected();
					boolean withStencilsAndLinkGroups = cbWithStencilsAndLinkGroups.isSelected();
					boolean withMeetings = cbWithMeetings.isSelected();
					XMLExport export = new XMLExport(uiViewFrame, fileName, allDepths, true, false, withStencilsAndLinkGroups, withMeetings, true);
					export.start();

					dispose();
				}
			}
		}

	}

	/**
	 * Inits the values of the export options in the XML export dialog.
	 */
	public void initDefaults() {
		if(uiViewFrame == null) {
			return;
		}
		rbAllDepths.setSelected(true);
		cbToZip.setSelected(true);
	}

    /**
     * This method check that a user has selected at least one map node.
     *
     * @return True if there is at least one selected map node.
     */
    public boolean hasSelectedMapNodes() {
        Enumeration<?> enumeration;
        if (uiViewFrame instanceof UIMapViewFrame) {
            enumeration = ((UIMapViewFrame) uiViewFrame).getViewPane()
                    .getSelectedNodes();
        } else {
            enumeration = ((UIListViewFrame) uiViewFrame).getUIList()
                    .getSelectedNodes();
        }
        boolean result = false;
        while (enumeration.hasMoreElements()) {
            Object element = enumeration.nextElement();
            if ((element instanceof UINode &&
                        ((UINode) element).getType() == ICoreConstants.MAPVIEW) ||
                    (element instanceof NodePosition &&
                        ((NodePosition) element).getNode().getType() == ICoreConstants.MAPVIEW)) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }
}
