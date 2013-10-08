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

package com.compendium.meeting;

import java.util.Vector;
import java.util.Properties;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;

import com.compendium.ProjectCompendium;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.dialogs.UIDialog;

/**
 * UIMeetingSetupDialog defines the dialog to enter Arena and Triplstore url and port information
 *
 * @author	Michelle Bachler
 */
public class UIMeetingSetupDialog extends UIDialog implements ActionListener {

	/** The button to save data.*/
	private UIButton				pbSave		= null;

	/** The button to close the dialog without saving.*/
	private UIButton				pbCancel	= null;

	/** The button is used to open the relevant help.*/
	private UIButton				pbHelp 	= null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent	= null;

	/** The panel with the labels and textfield in.*/
	private JPanel					oDetailsPanel = null;

	/** The panel with the buttons in.*/
	private JPanel					oButtonPanel = null;

	/** The field for the local proxy host data.*/
	private JTextField				txtLocalProxyHostField = null;

	/** The field for the local proxy port data.*/
	private JTextField				txtLocalProxyPortField = null;

	/** The field for the Arena server url data.*/
	private JTextField				txtArenaURLField = null;

	/** The field for the Arena server port data.*/
	//private JTextField				txtArenaPortField = null;

	/** The field for the Triplestore server url data.*/
	private JTextField				txtTriplestoreURLField = null;

	/** The field for the Triplestore server port data.*/
	//private JTextField				txtTriplestorePortField = null;

	/** The field for the Triplestore user name.*/
	private JTextField				txtNameField = null;

	/** The field for the file upload/download url.*/
	//private JTextField				txtFileURLField = null;

	/** The field for the Triplestore password.*/
	private JPasswordField			oPasswordField = null;

	/** The field for the Triplestore password confirmation.*/
	private JPasswordField			oPasswordConfirmField = null;

	/** The AccessGridData object holding the data for this dialog.*/
	private AccessGridData 			oData	=	null;

	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param sType, the type of connection dialog to draw.
	 */
	public UIMeetingSetupDialog(JFrame parent) {

		super(parent, true);

	  	this.setTitle("Access Grid Meeting Setup");

	  	oParent = parent;

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		oData = new AccessGridData();

		oDetailsPanel = new JPanel();
		oDetailsPanel.setBorder(new EmptyBorder(10,10,10,10));
		GridBagLayout oGridBagLayout = new GridBagLayout();
		oDetailsPanel.setLayout(oGridBagLayout);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel oLabel = new JLabel("Arena URL (include port): ");
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtArenaURLField = new JTextField(oData.getArenaURL());
		txtArenaURLField.setColumns(30);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtArenaURLField, gc);
		oDetailsPanel.add(txtArenaURLField);

		/*oLabel = new JLabel("Arena Port: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtArenaPortField = new JTextField(sArenaPort);
		txtArenaPortField.setColumns(6);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtArenaPortField, gc);
		oDetailsPanel.add(txtArenaPortField);*/

		oLabel = new JLabel("Triplestore URL (include port): ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtTriplestoreURLField = new JTextField(oData.getTriplestoreURL());
		txtTriplestoreURLField.setColumns(30);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtTriplestoreURLField, gc);
		oDetailsPanel.add(txtTriplestoreURLField);

		/*oLabel = new JLabel("Triplestore Port: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtTriplestorePortField = new JTextField(sTriplestorePort);
		txtTriplestorePortField.setColumns(6);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtTriplestorePortField, gc);
		oDetailsPanel.add(txtTriplestorePortField);*/

		oLabel = new JLabel("Triplestore Username: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtNameField = new JTextField(oData.getUserName());
		txtNameField.setColumns(20);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtNameField, gc);
		oDetailsPanel.add(txtNameField);

		oLabel = new JLabel("Triplestore Password: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		oPasswordField = new JPasswordField(oData.getPassword());
		oPasswordField.setColumns(20);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(oPasswordField, gc);
		oDetailsPanel.add(oPasswordField);

		oLabel = new JLabel("Confirm Password: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		oPasswordConfirmField = new JPasswordField(oData.getPassword());
		oPasswordConfirmField.setColumns(20);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(oPasswordConfirmField, gc);
		oDetailsPanel.add(oPasswordConfirmField);

		/*oLabel = new JLabel("File Upload/Download URL: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtFileURLField = new JTextField(sFileURL);
		txtFileURLField.setColumns(30);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtFileURLField, gc);
		oDetailsPanel.add(txtFileURLField);*/

		oLabel = new JLabel("Please enter the details below if your machine is on a");
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);
		oLabel = new JLabel("Local Area Network which uses a proxy server for HTTP connections.");
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);
		oLabel = new JLabel("(Check your browser connection properties or network settings, if unsure):");
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		oLabel = new JLabel("Proxy Address: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtLocalProxyHostField = new JTextField(oData.getLocalProxyHostName());
		txtLocalProxyHostField.setColumns(30);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtLocalProxyHostField, gc);
		oDetailsPanel.add(txtLocalProxyHostField);

		oLabel = new JLabel("Proxy Port: ");
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtLocalProxyPortField = new JTextField(oData.getLocalProxyPort());
		txtLocalProxyPortField.setColumns(6);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtLocalProxyPortField, gc);
		oDetailsPanel.add(txtLocalProxyPortField);

		oContentPane.add(oDetailsPanel, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();

		setResizable(false);
	}

	/**
	 * Create the panel with the buttons for this dialog.
	 * @return a JPanel holding the buttons for this dialog.
	 */
    private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbSave = new UIButton("Save");
		pbSave.setMnemonic(KeyEvent.VK_S);
		pbSave.addActionListener(this);
		getRootPane().setDefaultButton(pbSave);
		oButtonPanel.addButton(pbSave);

		pbCancel = new UIButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.memetic-setup", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {

			if (source == pbSave) {
				onSave();
			} else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Save the Access Grid data to a property file.
	 */
	public void onSave() {

		String sArenaURL = txtArenaURLField.getText();
		//String sArenaPort = txtArenaPortField.getText();
		String sTriplestoreURL = txtTriplestoreURLField.getText();
		//String sTriplestorePort = txtTriplestorePortField.getText();
		String sUsername = txtNameField.getText();
		String sPassword = new String(oPasswordField.getPassword());
		String sPasswordConfirm = new String(oPasswordConfirmField.getPassword());
		String sLocalProxyHost = txtLocalProxyHostField.getText();
		String sLocalProxyPort = txtLocalProxyPortField.getText();
		//String sFileURL = txtFileURLField.getText();

		if (sArenaURL.equals("") || sTriplestoreURL.equals("")
			|| sUsername.equals("") || sPassword.equals("") || sPasswordConfirm.equals("")) {
				ProjectCompendium.APP.displayError("Please enter all the requested data.");
			return;
		}

		if (!sPassword.equals(sPasswordConfirm)) {
			ProjectCompendium.APP.displayError("The password and password confirmation fields do not match.\n\nPlease try again.\n\n");
			oPasswordField.requestFocus();
			return;
		}

		oData.setArenaURL(sArenaURL);
		oData.setTriplestoreURL(sTriplestoreURL);
		oData.setUserName(sUsername);
		oData.setPassword(sPassword);
		oData.setLocalProxyHostName(sLocalProxyHost);
		oData.setLocalProxyPort(sLocalProxyPort);

		try {
			oData.saveProperties();
			if (ProjectCompendium.APP.oMeetingManager != null) {
				ProjectCompendium.APP.oMeetingManager.reloadAccessGridData();
			}
		} catch(IOException e) {
			ProjectCompendium.APP.displayError("IO error occured while saving Access Grid details.\n\n"+e.getMessage());
		}

		onCancel();
	}
}
