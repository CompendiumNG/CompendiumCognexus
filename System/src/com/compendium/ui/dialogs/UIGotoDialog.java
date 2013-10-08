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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.compendium.ProjectCompendium;

import com.compendium.ui.*;

/**
 * The dialog window which allows to jump to a node by internal ID.
 */
public class UIGotoDialog extends UIDialog implements ActionListener {

	/** The button to open a node by ID.*/
	private UIButton			gotoButton 		= null;

	/** The button to close this dialog.*/
	private UIButton			closeButton 	= null;

	/** The button to open the relevant help.*/
	private UIButton			helpButton	 	= null;

	/** The mainpanel which contains dialog's controls.*/
	private JPanel 				mainpanel		= null;

    /** The entry box for internal ID. */
    private JTextField idField;

	/**
	 * Constructor.
	 *
	 * @param parent, the parent frame for this dialog.
	 */
	public UIGotoDialog(JFrame parent) {
		super(parent, true);

		setResizable(false);
		setTitle("Go To internal ID");
		getContentPane().setLayout(new BorderLayout());

		drawDialog();

		pack();
	}

	/**
	 * Draw the contents of this dialog.
	 */
	private void drawDialog() {
	    Font normal = new Font("Dialog", Font.PLAIN, 12);
	    Font bold = new Font("Dialog", Font.BOLD, 12);
	    int y = 0;

		mainpanel = new JPanel();
		mainpanel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		mainpanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel idLabel = new JLabel("Paste intenal ID here:");
		idLabel.setFont(normal);
        gc.gridy = 0;
        gc.gridwidth=1;
        gb.setConstraints(idLabel, gc);
        mainpanel.add(idLabel);

        idField = new JTextField("comp://");
        idField.setColumns(37);
        idField.setMargin(new Insets(2,2,2,2));
        gc.gridy = y;
        gc.gridwidth=1;
        y++;
        gb.setConstraints(idField, gc);
        mainpanel.add(idField);

        JLabel exampleLabel = new JLabel("example:");
        exampleLabel.setFont(bold);
        gc.gridy = y;
        gc.gridwidth=1;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(exampleLabel, gc);
        mainpanel.add(exampleLabel);

        JLabel compLabel = new JLabel(
            "comp://1921681791201401412187/19216811001224649511606");
        compLabel.setFont(normal);
        gc.gridy = y;
        gc.gridwidth=1;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(compLabel, gc);
        mainpanel.add(compLabel);
        y++;
        
		UIButtonPanel oButtonPanel = new UIButtonPanel();

		gotoButton = new UIButton("Go To...");
		gotoButton.setMnemonic(KeyEvent.VK_G);
		gotoButton.addActionListener(this);
		getRootPane().setDefaultButton(gotoButton);
		oButtonPanel.addButton(gotoButton);

		closeButton = new UIButton("Close");
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(this);
		oButtonPanel.addButton(closeButton);

		helpButton = new UIButton("Help");
		helpButton.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(helpButton, "menus.map", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(helpButton);

		getContentPane().add(mainpanel, BorderLayout.CENTER);
		getContentPane().add(oButtonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Handle dialog's buttons.
	 * 
	 * @param evt, the associated ActionEvent.
	 */
	@SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if(source.equals(gotoButton)) {
		    String[] parsedId = parseInternalId(idField.getText());
		    if (parsedId != null) {
		        UIUtilities.jumpToNode(parsedId[0], parsedId[1], new Vector());
	            onCancel();
		    } else {
              ProjectCompendium.APP.displayMessage(
              "Please enter valid ID.",
              "Warning");
              SwingUtilities.invokeLater(new FocusGrabber(idField));
		    }
		}
		else if(source.equals(closeButton)) {
			onCancel();
		}
	}

    private static String[] parseInternalId(String internalId) {
        String[] result = null;
        String expression = "^comp://(\\d+)/(\\d+)$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(internalId);
        if (matcher.find()) {
            result = new String[] {matcher.group(1), matcher.group(2)};
        }
        return result;
    }
}
