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

package com.compendium.ui.linkgroups;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * UILinkGroupDialog defines the dialog, that allows the user to create and manage a link group.
 *
 * @author	Michelle Bachler
 */
public class UILinkGroupDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane = null;

	/** The button to add a new link type.*/
	private UIButton				pbAdd 		= null;

	/** The button to edit an existing link type.*/
	private UIButton				pbEdit 		= null;

	/** The button to delete an existing link type.*/
	private UIButton				pbDelete 	= null;

	/** The button to set the currently selected link type as the default.*/
	private UIButton				pbDefault	= null;

	/** The button to save the stencil set.*/
	private UIButton				pbSave 		= null;

	/** The button to close the dialog.*/
	private UIButton				pbCancel 	= null;

	/** Activates the help opeing to the appropriate section.*/
	private UIButton				pbHelp		= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb 			= null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc 			= null;

	/** The parent frame for this dialog.*/
	private JFrame			oParent 			= null;

	/** The link group manager for this dialog.*/
	private UILinkManagementDialog	oManager 			= null;

	/** The counter for the gridbag layout y position.*/
	private int 			gridyStart 			= 0;

	/** The list holding the current stencils.*/
	private UINavList		lstLinkGroups		= null;

	/** The link group to edit / created.*/
	private UILinkGroup 	oLinkGroup			= null;

	/** The text field to hold the link group name.*/
	private JTextField		txtName				= null;

	/** The list of link group items.*/
	private Vector 			vtItems 			= null;

	/** The default link type for this group.*/
	private String			sDefaultID			= "";

	/**
	 * Constructor. Initializes and sets up the dialog.
	 *
	 * @param parent, the frame that is the parent for this dialog.
	 * @param manager, the parent managing dialog.
	 * @param set, the stencil set to edit.
	 */
	public UILinkGroupDialog(JFrame parent, UILinkManagementDialog manager, UILinkGroup group) {

		super(parent, true);
		oParent = parent;
		oManager = manager;
		oLinkGroup = group;
		sDefaultID = group.getDefaultLinkTypeID();

		setTitle("Link Group");

		oContentPane = getContentPane();
		gb = new GridBagLayout();
		oContentPane.setLayout(gb);

		drawDialog();

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblName, gc);
		oContentPane.add(lblName);

		txtName = new JTextField(oLinkGroup.getName());
		txtName.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtName.setColumns(20);
		txtName.setMargin(new Insets(2,2,2,2));
		txtName.setSize(txtName.getPreferredSize());
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth=2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtName, gc);
		oContentPane.add(txtName);

		gc.fill = GridBagConstraints.NONE;

		JLabel lbl = new JLabel("Link Types:");
		lbl.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.weightx = 0;
		gc.gridwidth=3;
		gb.setConstraints(lbl, gc);
		oContentPane.add(lbl);

		vtItems = oLinkGroup.getItems();
		vtItems = CoreUtilities.sortList(vtItems);

		lstLinkGroups = new UINavList(vtItems);
		lstLinkGroups.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        LinkGroupCellRenderer linkGroupListRenderer = new LinkGroupCellRenderer();
		lstLinkGroups.setCellRenderer(linkGroupListRenderer);
		lstLinkGroups.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					onEdit();
				}
			}
		});
		//lstLinkGroups.addMouseListener(createMouseListener());

		JScrollPane sp = new JScrollPane(lstLinkGroups);
		sp.setPreferredSize(new Dimension(180,220));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth=3;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp, gc);
		oContentPane.add(sp);

		gc.fill = GridBagConstraints.NONE;

		pbAdd = new UIButton("Add Item");
		pbAdd.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbAdd, gc);
		oContentPane.add(pbAdd);

		pbEdit = new UIButton("Edit");
		pbEdit.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbEdit, gc);
		oContentPane.add(pbEdit);

		pbDelete = new UIButton("Delete");
		pbDelete.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 2;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbDelete, gc);
		oContentPane.add(pbDelete);

		gridyStart++;

		pbDefault = new UIButton("Set/Unset Default Link Type");
		pbDefault.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth=3;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbDefault, gc);
		oContentPane.add(pbDefault);

		JSeparator sep = new JSeparator();
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridwidth = 3;
		gb.setConstraints(sep, gc);
		oContentPane.add(sep);

		gc.fill = GridBagConstraints.NONE;

		pbSave = new UIButton("Save");
		pbSave.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbSave, gc);
		oContentPane.add(pbSave);

		pbCancel = new UIButton("Cancel");
		pbCancel.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(pbCancel, gc);
		oContentPane.add(pbCancel);

		pbHelp = new UIButton("Help");
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.linkgroups", ProjectCompendium.APP.mainHS);
		gc.gridy = gridyStart;
		gc.gridx = 2;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbHelp, gc);
		oContentPane.add(pbHelp);
	}

	/**
	 * Helper class that renders the stencil set lists.
	 */
	private class LinkGroupCellRenderer extends JPanel implements ListCellRenderer {

		private JLabel label = null;
		private JTextField field = null;

		LinkGroupCellRenderer() {
        	super();
			setOpaque(true);
			field = new JTextField();
			label = new JLabel();
			setLayout(new FlowLayout(FlowLayout.LEFT));
			field.setColumns(2);
		}

		public Component getListCellRendererComponent(
        	JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus)
            {

			UILinkType type = (UILinkType)value;

			Font font = getFont();
			if (!sDefaultID.equals("") && sDefaultID.equals(type.getID())) {
				label.setFont(new Font("ARIAL", Font.ITALIC, font.getSize()));
			}
			else {
				label.setFont(new Font("ARIAL", Font.PLAIN, font.getSize()));
			}

			Color colour = type.getColour();
			field.setBackground(colour);
			String sName = type.getName();

 	 		if (isSelected) {
				label.setBackground(list.getSelectionBackground());
				setBackground(list.getSelectionBackground());
				label.setForeground(list.getSelectionForeground());
			}
			else {
				label.setBackground(list.getBackground());
				setBackground(list.getBackground());
				label.setForeground(list.getForeground());
			}

			label.setText(sName);

			add(field);
			add(label);

			return this;
		}
	}

	/**
	 * Add the given link type to the group.
	 * @param oType, the link type to add to the group.
	 */
	public void addLinkType(UILinkType oType) {
		oLinkGroup.addLinkType(oType);
		refreshLinkGroup();
	}

	/**
	 * Remove the given link type from the group.
	 * @param oIcon, the link type to remove from the group.
	 */
	public void removeLinkType(UILinkType oType) {
		oLinkGroup.removeLinkType(oType);
		refreshLinkGroup();
	}

	/**
	 * Refresh the list of link types
	 */
	public void refreshLinkGroup() {
		vtItems = oLinkGroup.getItems();
		vtItems = CoreUtilities.sortList(vtItems);
		lstLinkGroups.setListData(vtItems);
	}

	/**
	 * Check the passed link type name to see if it already exists in this group
	 * @param sName, the name to check.
	 * @return boolean, true if the name has already been used, else false;
	 */
	public boolean checkName(String sName) {
		int count = vtItems.size();
		for(int i=0; i<count; i++) {
	       	UILinkType type = (UILinkType)vtItems.elementAt(i);
			if (sName.equals(type.getName()))
				return true;
		}
		return false;
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbAdd) {
				onAdd();
			}
			else if (source == pbEdit) {
				onEdit();
			}
			else if (source == pbDelete) {
				onDelete();
			}
			else if (source == pbDefault) {
				onDefault();
			}
			else if (source == pbSave) {
				onSave();
			}
			else if (source == pbCancel) {
				oManager.loadFile(oLinkGroup);
				onCancel();
			}
		}
	}

	/**
	 * Open the dialog to create a new link type.
	 */
	public void onAdd()  {
		UILinkType oType = new UILinkType();
		String id = ProjectCompendium.APP.getModel().getUniqueID();
		oType.setID( id );
		UILinkTypeDialog dlg = new UILinkTypeDialog(oParent, this, oType);
		UIUtilities.centerComponent(dlg, oParent);
		dlg.setVisible(true);
	}

	/**
	 * Open the dialog to edit the selected link type.
	 */
	public void onEdit()  {
		int index = lstLinkGroups.getSelectedIndex();
		if (index > -1) {
			UILinkType oType = (UILinkType)lstLinkGroups.getSelectedValue();
			UILinkTypeDialog dlg = new UILinkTypeDialog(oParent, this, oType);
			UIUtilities.centerComponent(dlg, oParent);
			dlg.setVisible(true);
		}
		else {
			ProjectCompendium.APP.displayMessage("Please select a link type first", "No Selection Made");
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onDelete()  {
		int index = lstLinkGroups.getSelectedIndex();
		if (index > -1) {
			UILinkType oType = (UILinkType)lstLinkGroups.getSelectedValue();
			removeLinkType(oType);
		}
		else {
			ProjectCompendium.APP.displayMessage("Please select a link type first", "No Selection Made");
		}
	}

	/**
	 * Save the current item as the default.
	 */
	public void onDefault()  {
		int index = lstLinkGroups.getSelectedIndex();
		if (index > -1) {
			UILinkType oType = (UILinkType)lstLinkGroups.getSelectedValue();
			if (!sDefaultID.equals("") && sDefaultID.equals(oType.getID()))
				sDefaultID = "";
			else
				sDefaultID = oType.getID();
			lstLinkGroups.repaint();
		}
		else {
			ProjectCompendium.APP.displayMessage("Please select a link type first", "No Selection Made");
		}
	}


	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onSave()  {

		String newName = txtName.getText();
		String oldName = oLinkGroup.getName();

		if (!newName.equals("")) {
			if (!newName.equals(oldName) && oManager.checkName(newName)) {
				ProjectCompendium.APP.displayMessage("You already have a group with that name, please try again", "Duplicate Name");
				txtName.requestFocus();
				return;
			}
			else {

				if (sDefaultID.equals("")) {
					ProjectCompendium.APP.displayMessage("You must select the default link type for this group", "Default Link Type");
					return;
				}

				// IF YOU HAVE CHANGED THE LINK GROUP NAME, THE FILE PATH WILL NEED CHANGING
				if (!oldName.equals("") && !newName.equals(oldName)) {
					oLinkGroup.setName(newName);
					oLinkGroup.setDefaultLinkTypeID(sDefaultID);
					oLinkGroup.saveToNew(newName);
				}
				else {
					oLinkGroup.setName(newName);
					oLinkGroup.setDefaultLinkTypeID(sDefaultID);
					oLinkGroup.saveLinkGroupData();
				}
				oManager.updateData(oldName, oLinkGroup);
				onCancel();
			}
		}
		else {
			ProjectCompendium.APP.displayMessage("You must give the link group a name", "Missing Name");
			txtName.requestFocus();
			return;
		}
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
