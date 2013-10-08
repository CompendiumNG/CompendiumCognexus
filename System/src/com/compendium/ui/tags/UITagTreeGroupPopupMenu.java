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

package com.compendium.ui.tags;

import java.awt.event.*;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;

import java.sql.SQLException;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.ui.*;

/**
 * This draws a small popup menu for right-clicks on a UITextArea.
 *
 * @author	Michelle Bachler
 */
public class UITagTreeGroupPopupMenu extends JPopupMenu implements ActionListener {

	/** The menu option to edit code label.*/
	private JMenuItem		miMenuItemEdit		= null;

	/** The menu option to delete the passed node.*/
	private JMenuItem		miMenuItemDelete		= null;

	/** The menu option to set this group as the default.*/
	private JMenuItem		miMenuItemDefault		= null;

	/** The height of this popup.*/
	private int				nHeight				= 100;

	/** The width of this popup.*/
	private int				nWidth				= 100;

	/** The UITextArea associated with this popup.*/
	private UITagTreePanel oParent				= null;

	/** The vector holding the group data.*/
	private Vector			vtGroup				= null;


	/**
	 * Constructor. Draws the popupmenu.
	 * @param panel the parent panel for this popup.
	 */
	public UITagTreeGroupPopupMenu(UITagTreePanel panel, Vector vtGroup) {
		super("Tag Group Options");

		this.vtGroup = vtGroup;
		this.oParent = panel;

		miMenuItemDefault = new JMenuItem("Set Group As Active");
		miMenuItemDefault.setToolTipText("Set this group as the active group");
		miMenuItemDefault.setMnemonic('A');
		miMenuItemDefault.addActionListener(this);
		add(miMenuItemDefault);

		miMenuItemEdit = new JMenuItem("Edit Group Name");
		miMenuItemEdit.setToolTipText("Edit the group name");
		miMenuItemEdit.setMnemonic('E');
		miMenuItemEdit.addActionListener(this);
		add(miMenuItemEdit);

		miMenuItemDelete = new JMenuItem("Delete Group");
		miMenuItemDelete.setToolTipText("Delete this tag group from the project");
		miMenuItemDelete.setMnemonic('D');
		miMenuItemDelete.addActionListener(this);
		add(miMenuItemDelete);

		pack();
		setSize(nWidth,nHeight);
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		IModel model = ProjectCompendium.APP.getModel();
		String sCodeGroupID = (String)vtGroup.elementAt(0);

		if(source.equals(miMenuItemEdit)) {
			String sOldName = (String)vtGroup.elementAt(1);
	   		String sNewName = JOptionPane.showInputDialog("Edit tag group name", sOldName);
			sNewName = sNewName.trim();
			if (!sNewName.equals("")) {
				try {
					vtGroup.setElementAt(sNewName, 1);

					String sUserID = model.getUserProfile().getId();

					// UPDATE DATABASE
					(model.getCodeGroupService()).setName(model.getSession(), sCodeGroupID, sNewName, new Date(), sUserID);

					// UPDATE MODEL
					model.replaceCodeGroupName(sCodeGroupID, sNewName);
					oParent.updateTreeData();

				} catch( SQLException ex) {
					ProjectCompendium.APP.displayError("UITagTreeGroupPopupMenu.editGroupName\n\n"+ex.getMessage());
				}
			}
		} else if(source.equals(miMenuItemDelete)) {
			try {
				// UPDATE DATABASE
				(model.getCodeGroupService()).deleteCodeGroup(model.getSession(), sCodeGroupID);

				// UPDATE MODEL
				model.removeCodeGroup(sCodeGroupID);

				//update toolbar codes box
				if (ProjectCompendium.APP.getActiveCodeGroup().equals(sCodeGroupID)) {
					ProjectCompendium.APP.setActiveCodeGroup("");
				}

				oParent.updateTreeData();
			} catch( SQLException ex) {
				ProjectCompendium.APP.displayError("UITagTreeGroupPopupMenu.deleteGroup\n\n"+ex.getMessage());
			}
		} else if (source.equals(miMenuItemDefault)) {
			onActive();
		}
	}

	/**
	 * Set the current code group as the active group.
	 */
	private void onActive() {
 		String newActive = (String)vtGroup.elementAt(0);
		String oldActive = ProjectCompendium.APP.getActiveCodeGroup();

		if (oldActive.equals(newActive)) {
			if (ProjectCompendium.APP.setActiveCodeGroup("")) {
				oParent.refresh();
				//oParent.updateTreeData();
			}
		}
		else if (ProjectCompendium.APP.setActiveCodeGroup(newActive)) {
			oParent.refresh();
			//oParent.updateTreeData();
		}
	}

	/**
	 * Handle the cancelleing of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
