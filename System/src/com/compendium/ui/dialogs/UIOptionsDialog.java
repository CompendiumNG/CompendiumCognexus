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
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.compendium.ProjectCompendium;

import com.compendium.core.CoreUtilities;
import com.compendium.ui.*;

/**
 * This class draws the options dialog and handles storing/setting the user's chosen options.
 *
 * @author	Michelle Bachler
 */
public class UIOptionsDialog extends UIDialog implements ActionListener, ItemListener {

	/** The choicebox with the zoom options.*/
	private JComboBox 		cbZoom					= null;

	/** The parent frame for this dialog.*/
	private Container		oParent					= null;

	/** The butotn to cancel this dialog.*/
	public	UIButton		pbCancel				= null;

	/** The button to update the user settings.*/
	public	UIButton		pbUpdate				= null;

	/** Activates the help opeing to the appropriate section.*/
	private UIButton		pbHelp					= null;

	/** Turn audio feedback on.*/
	private JRadioButton	rbAudioOn				= null;

	/** Turn audio feedback off.*/
	private JRadioButton	rbAudioOff				= null;

	/** Prompt user on dnd of file for transfer to Linked Files folder.*/
	private JRadioButton	rbDnDToFilePromptOn		= null;

	/** Never transfer dnd file to Linked Files folder.*/
	private JRadioButton	rbDnDToFileOff			= null;

	/** Always transfer dnd files to Linked Files folder.*/
	private JRadioButton	rbDnDToFileOn			= null;

	/** Set not to prompt when dragging and dropping text.*/
	private JCheckBox		rbDnDToText				= null;

	/** Set whether you want to add dropped folders recusively.*/
	private JCheckBox		rbDnDAddDir			= null;

	/** Should images rollover be scaled?*/
	private JCheckBox		rbImageRolloverScale 	= null;

	/** Should menu bar be at top of screenin a Mac OS?*/
	private JCheckBox		rbMenuPosition 	= null;

	/** Should menu shortcuts be displayed as underlining?*/
	private JCheckBox		rbMenuUnderline 	= null;

	/** Should an email be sent when something goes in your inbox. */
	private JCheckBox		rbInboxEmail		= null;

	/** UDig communications be enabled?*/
	private JCheckBox		rbUDig 				= null;

	/** Use kfmclient to open external references?*/
	private JCheckBox		rbKFMClient			= null;

	/** Should single click for opening nodes be enabled?*/
	private JCheckBox		rbSingleClick 		= null;

	/** Holds the detail rollover length.*/
	private JTextField		txtCursorMoveDistance = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtDetailRolloverLength = null;


	/** Holds the detail rollover length.*/
	private JTextField		txtLeftVerticalGap = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtLeftHorizontalGap = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtTopVerticalGap = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtTopHorizontalGap = null;

	/** The choicebox listing the current look and feels.*/
	private JComboBox		cbLandF			= null;

	/** The choicebox listing the current icons sets.*/
	private JComboBox		cbIconSets			= null;


	/** Holds the various panels with options.*/
	private JTabbedPane		TabbedPane			= null;

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIOptionsDialog(JFrame parent) {

		super(parent, true);
		oParent = parent;

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		TabbedPane = new JTabbedPane();

		if (ProjectCompendium.isMac) {
			setTitle("User Preferences");
		}
		else {
			setTitle("User Options");
		}

		TabbedPane.add(createDndPanel(), "DnD");
		TabbedPane.add(createRolloverPanel(), "Map & Rollover");
		TabbedPane.add(createOtherPanel(), "Audio & Zoom");
		TabbedPane.add(createArrangePanel(), "Arrange");
		TabbedPane.add(createMiscPanel(), "Misc");

		JPanel buttonpanel = createButtonPanel();

		oContentPane.add(TabbedPane, BorderLayout.CENTER);
		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
	}

	/**
	 * Create the panel with the audio and zoom options.
	 */
	public JPanel createOtherPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		JLabel lblAudio = new JLabel("Audio feedback:");
		gc.gridy = 0;
		gc.gridx = 0;
		gb.setConstraints(lblAudio, gc);
		panel.add(lblAudio);

		rbAudioOn = new JRadioButton("On");
		rbAudioOn.addActionListener(this);
		gc.gridy = 0;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(rbAudioOn, gc);
		panel.add(rbAudioOn);

		gc.anchor = GridBagConstraints.WEST;

		rbAudioOff = new JRadioButton("Off");
		rbAudioOff.addActionListener(this);
		gc.gridy = 0;
		gc.gridx = 2;
		gb.setConstraints(rbAudioOff, gc);
		panel.add(rbAudioOff);

		boolean audioOn = ProjectCompendium.APP.getAudioPlayer().getAudio();
		if(audioOn)
			rbAudioOn.setSelected(true);
		else
			rbAudioOff.setSelected(true);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbAudioOn);
		rgGroup.add(rbAudioOff);

		JLabel lbl = new JLabel("Zoom Level when Maps are first opened ");
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth=2;
		gb.setConstraints(lbl, gc);
		panel.add(lbl);

		createZoomChoiceBox();
		gc.gridx = 2;
		gc.gridwidth=1;
		gb.setConstraints(cbZoom, gc);
		panel.add(cbZoom);

		return panel;
	}

	/**
	 * Create a new choicbox for zoom options.
	 * @return JComboBox, the choicbox for the zoom options.
	 */
	public JComboBox createZoomChoiceBox() {

		cbZoom = new JComboBox();
        cbZoom.setOpaque(true);
		cbZoom.setEditable(false);
		cbZoom.setEnabled(true);
		cbZoom.setMaximumRowCount(4);
		cbZoom.setFont( new Font("Dialog", Font.PLAIN, 10 ));

		cbZoom.addItem(new String("100%"));
		cbZoom.addItem(new String("75%"));
		cbZoom.addItem(new String("50%"));
		cbZoom.addItem(new String("25%"));

		cbZoom.validate();

		double zoom = APP_PROPERTIES.getZoomLevel();
		if (zoom == 1.0)
			cbZoom.setSelectedIndex(0);
		else if (zoom == 0.75)
			cbZoom.setSelectedIndex(1);
		else if (zoom == 0.50)
			cbZoom.setSelectedIndex(2);
		else if (zoom == 0.25)
			cbZoom.setSelectedIndex(3);

		DefaultListCellRenderer zoomRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};
		cbZoom.setRenderer(zoomRenderer);

		return cbZoom;
	}

	/**
	 * Create the panel with the Drag and Drop options.
	 */
	private JPanel createDndPanel() {

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5,5,5,5));
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		int y=0;

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel label = new JLabel("External Drag and Drop behaviour:");
		label.setFont(new Font("Arial", Font.BOLD, 12));
		gc.gridy = y;
		y++;
		gc.gridwidth = 3;
		gb.setConstraints(label, gc);
		panel.add(label);

		rbDnDToFileOff = new JRadioButton("Never Drag & Drop Files to 'Linked Files' folder");
		gc.gridy = y;
		y++;
		gc.gridwidth = 3;
		gb.setConstraints(rbDnDToFileOff, gc);
		panel.add(rbDnDToFileOff);

		rbDnDToFilePromptOn = new JRadioButton("Drag & Drop Files to 'Linked Files' folder with Prompting");
		gc.gridy = y;
		y++;
		gc.gridwidth = 3;
		gb.setConstraints(rbDnDToFilePromptOn, gc);
		panel.add(rbDnDToFilePromptOn);

		rbDnDToFileOn = new JRadioButton("Drag & Drop Files to 'Linked Files' folder without Prompting");
		gc.gridy = y;
		y++;
		gc.gridwidth = 3;
		gb.setConstraints(rbDnDToFileOn, gc);
		panel.add(rbDnDToFileOn);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup = new ButtonGroup();
		rgGroup.add(rbDnDToFileOff);
		rgGroup.add(rbDnDToFilePromptOn);
		rgGroup.add(rbDnDToFileOn);

		String dndFiles = APP_PROPERTIES.getDndFiles();
		if(dndFiles.equals("on"))
			rbDnDToFileOn.setSelected(true);
		else if (dndFiles.equals("prompt"))
			rbDnDToFilePromptOn.setSelected(true);
		else
			rbDnDToFileOff.setSelected(true);

		// OPTION TO HAVE TEXT ALWAYS DROP AS TEXT
		rbDnDToText = new JCheckBox("Don't prompt when dragging and dropping text.");
		gc.gridy = y;
		y++;
		gc.gridwidth = 3;
		gb.setConstraints(rbDnDToText, gc);
		panel.add(rbDnDToText);

		if (APP_PROPERTIES.isDndNoTextChoice())
			rbDnDToText.setSelected(true);

		// OPTION TO ALWAYS IMPORT DIRECTORIES RECURSIVELY
		rbDnDAddDir = new JCheckBox("Always add directories recursively?");
		gc.gridy = y;
		y++;
		gc.gridwidth = 3;
		gb.setConstraints(rbDnDAddDir, gc);
		panel.add(rbDnDAddDir);

		if (APP_PROPERTIES.isDndAddDirRecursively())
			rbDnDAddDir.setSelected(true);

		return panel;
	}

	/**
	 * Create the panel with rollover options.
	 */
	public JPanel createRolloverPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		JLabel label = new JLabel("Cursor Movement Distance: ");
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtCursorMoveDistance = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtCursorMoveDistance, gc);
		panel.add(txtCursorMoveDistance);

		if (APP_PROPERTIES.getCursorMovementDistance() > 0)
			txtCursorMoveDistance.setText(new Integer(APP_PROPERTIES.getCursorMovementDistance()).toString());

		rbImageRolloverScale = new JCheckBox("Scale oversized images on rollover?");
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(rbImageRolloverScale, gc);
		panel.add(rbImageRolloverScale);

		if (APP_PROPERTIES.isScaleImageRollover())
			rbImageRolloverScale.setSelected(true);

		JLabel label2 = new JLabel("Detail rollover length: ");
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label2, gc);
		panel.add(label2);

		txtDetailRolloverLength = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtDetailRolloverLength, gc);
		panel.add(txtDetailRolloverLength);

		if (APP_PROPERTIES.getDetailRolloverLength() > 0)
			txtDetailRolloverLength.setText(new Integer(APP_PROPERTIES.getDetailRolloverLength()).toString());

		// check box for single click
		rbSingleClick = new JCheckBox("Enable single click for opening nodes?");
		rbSingleClick.addItemListener(this);
		gc.gridy =y;
		gc.gridwidth = 2;
		gb.setConstraints(rbSingleClick, gc);
		panel.add(rbSingleClick);

		rbSingleClick.setSelected(APP_PROPERTIES.isSingleClick());

		return panel;
	}

	/**
	 * Create the panel with arrange options.
	 */
	public JPanel createArrangePanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		JLabel label = new JLabel("Left to Right: Vertical Gap: ");
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtLeftVerticalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtLeftVerticalGap, gc);
		panel.add(txtLeftVerticalGap);

		if (APP_PROPERTIES.getArrangeLeftVerticalGap() > 0)
			txtLeftVerticalGap.setText(String.valueOf(APP_PROPERTIES.getArrangeLeftVerticalGap()));

		label = new JLabel("Left to Right: Horizontal Gap: ");
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtLeftHorizontalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtLeftHorizontalGap, gc);
		panel.add(txtLeftHorizontalGap);

		if (APP_PROPERTIES.getArrangeLeftHorizontalGap() > 0)
			txtLeftHorizontalGap.setText(String.valueOf(APP_PROPERTIES.getArrangeLeftHorizontalGap()));


		label = new JLabel("Top-Down: Vertical Gap: ");
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtTopVerticalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtTopVerticalGap, gc);
		panel.add(txtTopVerticalGap);

		if (APP_PROPERTIES.getCursorMovementDistance() > 0)
			txtTopVerticalGap.setText(String.valueOf(APP_PROPERTIES.getArrangeTopVerticalGap()));

		label = new JLabel("Top-Down: Horizontal Gap: ");
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtTopHorizontalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtTopHorizontalGap, gc);
		panel.add(txtTopHorizontalGap);

		if (APP_PROPERTIES.getCursorMovementDistance() > 0)
			txtTopHorizontalGap.setText(String.valueOf(APP_PROPERTIES.getArrangeTopHorizontalGap()));

		return panel;
	}

	/**
	 * Create the panel with the Mac OS options.
	 */
	public JPanel createMacPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		JLabel label = new JLabel("This setting only takes effect after application restart");
		gc.gridy = 0;
		gc.gridwidth=4;
		gb.setConstraints(label, gc);
		panel.add(label);

		JLabel label2 = new JLabel(" ");
		gc.gridy = 1;
		gc.gridwidth=4;
		gb.setConstraints(label2, gc);
		panel.add(label2);

		rbMenuPosition = new JCheckBox("Put Menu Bar At Top of Screen?");
		rbMenuPosition.addItemListener(this);
		gc.gridy = 2;
		gc.gridwidth=4;
		gb.setConstraints(rbMenuPosition, gc);
		panel.add(rbMenuPosition);

		JLabel label3 = new JLabel(" ");
		gc.gridy = 3;
		gc.gridwidth=1;
		gb.setConstraints(label3, gc);
		panel.add(label3);

		rbMenuUnderline = new JCheckBox("Display/Activate Menu Shortcuts?");
		gc.gridy = 3;
		gc.gridx = 2;
		gc.gridwidth=4;
		gb.setConstraints(rbMenuUnderline, gc);
		panel.add(rbMenuUnderline);

		rbMenuUnderline.setSelected(APP_PROPERTIES.isMacMenuUnderline());
		rbMenuPosition.setSelected(APP_PROPERTIES.isMacMenuBar());

		return panel;
	}

	/**
	 * Create the panel with various misc options.
	 */
	public JPanel createMiscPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;
		gc.gridx=0;
		gc.gridy=0;

		JLabel label = new JLabel("Icon Sets");
		gb.setConstraints(label, gc);
		panel.add(label);

		gc.gridx=1;
		createIconSetChoiceBox();
		gb.setConstraints(cbIconSets, gc);
		panel.add(cbIconSets);

		gc.gridy=1;
		gc.gridx=0;

		label = new JLabel("Look and Feel");
		gb.setConstraints(label, gc);
		panel.add(label);

		gc.gridx=1;
		createLandFChoiceBox();
		gb.setConstraints(cbLandF, gc);
		panel.add(cbLandF);

		gc.gridy=2;
		gc.gridx=0;
		gc.gridwidth=2;
		gc.fill = GridBagConstraints.HORIZONTAL;

		JSeparator sep = new JSeparator();
		gb.setConstraints(sep, gc);
		panel.add(sep);


		rbUDig = new JCheckBox("Enable communications with the uDig Application?");
		rbUDig.addItemListener(this);
		gc.gridy=3;
		gc.fill = GridBagConstraints.NONE;
		gb.setConstraints(rbUDig, gc);
		panel.add(rbUDig);

		rbUDig.setSelected(APP_PROPERTIES.isStartUDigCommunications());

		gc.gridy=4;
		gc.gridx=0;
		gc.gridwidth=2;
		gc.fill = GridBagConstraints.HORIZONTAL;

		sep = new JSeparator();
		gb.setConstraints(sep, gc);
		panel.add(sep);

		gc.gridy=5;
		gc.fill = GridBagConstraints.NONE;

		rbInboxEmail = new JCheckBox("Email when item goes into Inbox?");
		rbInboxEmail.addItemListener(this);
		gb.setConstraints(rbInboxEmail, gc);
		panel.add(rbInboxEmail);

		rbInboxEmail.setSelected(APP_PROPERTIES.isEmailInbox());


		if (ProjectCompendium.isLinux) {
			rbKFMClient = new JCheckBox("Use kfmclient to open files?");
			rbKFMClient.addItemListener(this);
			gc.gridy=6;
			gc.fill = GridBagConstraints.NONE;
			gb.setConstraints(rbKFMClient, gc);
			panel.add(rbKFMClient);

			rbKFMClient.setSelected(APP_PROPERTIES.isUseKFMClient());
		}
		return panel;
	}


	/**
	 * Create the Look and Feel choicebox.
	 */
	private JComboBox createLandFChoiceBox() {

		cbLandF = new JComboBox();
		cbLandF.setOpaque(true);
		cbLandF.setEditable(false);
		cbLandF.setEnabled(true);
		cbLandF.setMaximumRowCount(20);
		cbLandF.setFont( new Font("Dialog", Font.PLAIN, 12 ));

		Vector lafs = new Vector(10);
		int selectedIndex = 0;

		boolean KunstDetected = false;

		UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
		LookAndFeel current = UIManager.getLookAndFeel();
		String currentLook = "";
		if (current != null ) {
			currentLook = current.getClass().getName();
			// WHEN FIRST INSTALL ON LINUX ITS EMPTY
			if (APP_PROPERTIES.getCurrentLookAndFeel().equals("")) {
	    		APP_PROPERTIES.setCurrentLookAndFeel(currentLook);
			}
		} else {
			currentLook = APP_PROPERTIES.getCurrentLookAndFeel();
		}

		String look = "";
		for (int i=0; i< looks.length; i++) {

			if (looks[i].getName().equals("Kunststoff")) {
				KunstDetected = true;
				if (!ProjectCompendium.isMac) {
					lafs.addElement(looks[i]);
				}
			} else {
				lafs.addElement(looks[i]);
			}

			look = looks[i].getClassName();
			if (look.equals(currentLook)) {
				selectedIndex = i;
			}
		}

		if (!KunstDetected && !ProjectCompendium.isMac) {
			if (!ProjectCompendium.isMac) {
				lafs.addElement("Kunststoff");
				if ((APP_PROPERTIES.getCurrentLookAndFeel()).equals("Kunststoff")) {
					selectedIndex = lafs.size()-1;
				}
			}
		}

		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(lafs);
		cbLandF.setModel(comboModel);
		cbLandF.setSelectedIndex(selectedIndex);

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

 		 		if (value instanceof String) {
 		 			setText((String)value);
 		 		} else {
 		 			UIManager.LookAndFeelInfo look = (UIManager.LookAndFeelInfo)value;
 		 			setText(look.getName());
 		 		}

				return this;
			}
		};

		cbLandF.setRenderer(comboRenderer);

		return cbLandF;
	}


	/**
	 * Create the code group choicebox.
	 */
	private JComboBox createIconSetChoiceBox() {

		cbIconSets = new JComboBox();
		cbIconSets.setOpaque(true);
		cbIconSets.setEditable(false);
		cbIconSets.setEnabled(true);
		cbIconSets.setMaximumRowCount(20);
		cbIconSets.setFont( new Font("Dialog", Font.PLAIN, 12 ));

		File main = new File("Skins");
		File skins[] = main.listFiles();
		Vector vtSkins = new Vector(skins.length);
		for (int i=0; i< skins.length; i++) {
			vtSkins.add(skins[i]);
		}
		vtSkins = CoreUtilities.sortList(vtSkins);
		Vector vtFinalSkins = new Vector(vtSkins.size());

		int selectedItem = 0;
		int count = vtSkins.size();
		String skinName = "";
		int indexcount = 0;
		for (int i=0; i< count; i++) {
			File nextSkin = (File)vtSkins.elementAt(i);
			if (nextSkin.isDirectory()) {
				skinName = nextSkin.getName();
				if (APP_PROPERTIES.getSkin().equals(skinName)) {
					selectedItem = indexcount;
				}
				vtFinalSkins.addElement(skinName);
				indexcount++;
			}
		}

		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(vtFinalSkins);
		cbIconSets.setModel(comboModel);
		cbIconSets.setSelectedIndex(selectedItem);

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

				setText((String) value);

				return this;
			}
		};
		cbIconSets.setRenderer(comboRenderer);

		return cbIconSets;
	}

	/**
	 * Records the fact that a checkbox / radio button state has been changed and stores the new data.
	 * @param e, the associated ItemEvent.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == rbMenuPosition) {
			if (rbMenuPosition.isSelected()) {
				rbMenuUnderline.setEnabled(false);
				//rbMenuUnderline.setSelected(false);
			}
			else {
				rbMenuUnderline.setEnabled(true);
				//rbMenuUnderline.setSelected(false);
			}
		}
	}

	/**
	 * Create the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel panel = new UIButtonPanel();

		pbUpdate = new UIButton("Update");
		pbUpdate.setMnemonic(KeyEvent.VK_U);
		pbUpdate.addActionListener(this);
		getRootPane().setDefaultButton(pbUpdate);
		panel.addButton(pbUpdate);

		pbCancel = new UIButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		panel.addButton(pbCancel);

		// Add help button
		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.options", ProjectCompendium.APP.mainHS);
		panel.addHelpButton(pbHelp);

		return panel;
	}

	/**
	 * Process button pushes.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == pbCancel)
			onCancel();
		else
		if (source == pbUpdate)
			onUpdate();
	}

	/**
	 * Save the users options and update where necessary.
	 */
	public void onUpdate() {

		try {
			String dndChoice = "off";
			boolean scaleRollover = false;

    		String skinName = (String)cbIconSets.getSelectedItem();
    		if (!skinName.equals(APP_PROPERTIES.getSkin())) {
    			ProjectCompendium.APP.onFormatSkin(skinName);
    		}

    		Object obj = cbLandF.getSelectedItem();
    		String className = "";
    		if (obj instanceof String) {
       			className = (String)obj;
    		} else if (obj instanceof UIManager.LookAndFeelInfo) {
	    		UIManager.LookAndFeelInfo look = (UIManager.LookAndFeelInfo)cbLandF.getSelectedItem();
	    		className = look.getClassName();
    		}

	    	if (!className.equals(APP_PROPERTIES.getCurrentLookAndFeel())) {
	    		if (className.equals("Kunststoff")) {
	    			className = "com.incors.plaf.kunststoff.KunststoffLookAndFeel";
	    		}

	    		APP_PROPERTIES.setCurrentLookAndFeel(className);

				ProjectCompendium.APP.displayMessage("A Look and Feel update will take effect the next time you restart Compendium.", "Look And Feel Update");
     		}

			if (!txtDetailRolloverLength.getText().equals("")) {
				try {
					int len = new Integer(txtDetailRolloverLength.getText()).intValue();
					APP_PROPERTIES.setDetailRolloverLength(len);
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage("The detail rollover length is not a valid number.\nPlease try again.\n", "Options Error");
					txtDetailRolloverLength.requestFocus();
				}
			}

			if (!txtCursorMoveDistance.getText().equals("")) {
				try {
					int len = new Integer(txtCursorMoveDistance.getText()).intValue();
					APP_PROPERTIES.setCursorMovementDistance(len);
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage("The cursor movement distance rollover length is not a valid number.\nPlease try again.\n", "Options Error");
					txtCursorMoveDistance.requestFocus();
				}
			}

			if (rbImageRolloverScale.isSelected()) {
				scaleRollover = true;
				APP_PROPERTIES.setScaleImageRollover(true);
			} else {
				APP_PROPERTIES.setScaleImageRollover(false);
			}
			//TODO: clean up logic of changing ScaleImageRollover property 
			APP_PROPERTIES.setScaleImageRollover(scaleRollover);

			if (rbDnDToFilePromptOn.isSelected()) {
				dndChoice="prompt";
			}
			else if (rbDnDToFileOn.isSelected()) {
				dndChoice="on";
			}
			APP_PROPERTIES.setDndFiles(dndChoice);

			APP_PROPERTIES.setDndNoTextChoice(rbDnDToText.isSelected());
			APP_PROPERTIES.setDndAddDirRecursively(rbDnDAddDir.isSelected());
			APP_PROPERTIES.setSingleClick(rbSingleClick.isSelected());
			APP_PROPERTIES.setUseKFMClient(rbKFMClient != null && rbKFMClient.isSelected());
			APP_PROPERTIES.setEmailInbox(rbInboxEmail != null && rbInboxEmail.isSelected());

			APP_PROPERTIES.setDndFiles(dndChoice);

			int ind = cbZoom.getSelectedIndex();
			if (ind == 0) // 100%
				APP_PROPERTIES.setZoomLevel(1.0);
			else if (ind == 1) // 75%
				APP_PROPERTIES.setZoomLevel(0.75);
			else if (ind == 2) // 50%
				APP_PROPERTIES.setZoomLevel(0.50);
			else if (ind == 3) // 25%
				APP_PROPERTIES.setZoomLevel(0.25);


			if (!txtLeftHorizontalGap.getText().equals("")) {
				try {
					int len = new Integer(txtLeftHorizontalGap.getText()).intValue();
					APP_PROPERTIES.setArrangeLeftHorizontalGap(len);
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage("The Left to Right horizontal gap is not a valid number.\nPlease try again.\n", "Options Error");
					txtLeftHorizontalGap.requestFocus();
				}
			}

			if (!txtLeftVerticalGap.getText().equals("")) {
				try {
					int len = new Integer(txtLeftVerticalGap.getText()).intValue();
					APP_PROPERTIES.setArrangeLeftVerticalGap(len);
				} catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage("The Left to Right vertical gap is not a valid number.\nPlease try again.\n", "Options Error");
					txtLeftVerticalGap.requestFocus();
				}
			}

			if (!txtTopHorizontalGap.getText().equals("")) {
				try {
					int len = new Integer(txtTopHorizontalGap.getText()).intValue();
					APP_PROPERTIES.setArrangeTopHorizontalGap(len);
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage("The Top-Down horizontal gap is not a valid number.\nPlease try again.\n", "Options Error");
					txtTopHorizontalGap.requestFocus();
				}
			}

			if (!txtTopVerticalGap.getText().equals("")) {
				try {
					int len = new Integer(txtTopVerticalGap.getText()).intValue();
					APP_PROPERTIES.setArrangeTopVerticalGap(len);
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage("The Top-Down vertical gap is not a valid number.\nPlease try again.\n", "Options Error");
					txtTopVerticalGap.requestFocus();
				}
			}

			if (rbUDig.isSelected()) {
				APP_PROPERTIES.setStartUDigCommunications(true);
				ProjectCompendium.APP.startUDigConnection();

			} else {
				APP_PROPERTIES.setStartUDigCommunications(false);
				ProjectCompendium.APP.stopUDigConnection();
			}
			ProjectCompendium.APP.getMenuManager().setUDigEnablement(rbUDig.isSelected());

			ProjectCompendium.APP.onViewRefresh();
		}
		catch(Exception e) {

			e.printStackTrace();
			return;
		}

		ProjectCompendium.APP.getAudioPlayer().setAudio(rbAudioOn.isSelected());

		onCancel();
	}
}
