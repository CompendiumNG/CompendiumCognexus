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

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.Vector;
import java.sql.SQLException;

import com.compendium.ProjectCompendium;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.UITableHeaderRenderer;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.TableSorter;
import com.compendium.ui.dialogs.UIDialog;
import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.Meeting;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.IModel;


/**
 * Dialog to get the relevant data and create the meeting parent Map.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIMeetingRecorderDialog extends UIDialog implements ActionListener {

	/** The button to load the meeting data.*/
	private UIButton 		pbLoad 			= null;

	/** The button to cancel the dialog*/
	private UIButton 		pbCancel 		= null;

	/** The button to start recording the meeting.*/
	private UIButton 		pbStart 		= null;

	/** The button to stop recording the meeting.*/
	private UIButton 		pbStop 			= null;

	/** The button is used to open the relevant help.*/
	private UIButton 		pbHelp 			= null;

	/** The data for the list of all known meetings.*/
	private Vector 			vtMeetings		= new Vector(10);

	/** The table holding the list of unrecorded meetings.*/
	private JTable			oTable			= null;

	/** The scrollpane to put the list of meetings.*/
	private JScrollPane		oScrollpane		= new JScrollPane();

	/** The test field for entering the uri of the meeting to create a new meeting map for.*/
	private JTextField 		txtMeetingURI 	= null;

	/** The manager object for the meeting that will be recorded.*/
	private MeetingManager	oMeetingManager	= null;

	/** The title for this dialog.*/
	private String			sTitle			="Meeting Recorder";

	/**
	 * Constructor. Creates a new instance of this dialog.
	 * @param oMeetingManager the {@Link com.compendium.meeting.MeetingManager MeetingManager} instance controlling this recording session.
	 */
    public UIMeetingRecorderDialog(MeetingManager oMeetingManager) {

		super(ProjectCompendium.APP, true);

		setTitle(sTitle);

		this.oMeetingManager = oMeetingManager;

		setResizable(false);
		setBackground(new Color(255,255,255));

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(createOptions(), BorderLayout.CENTER);
		content.add(createButtonPanel(), BorderLayout.SOUTH);
		pack();
    }

	/**
	 * Create the main panel with the fields to complete.
	 * @return the newly create JPanel instance.
	 */
    private JPanel createOptions() {

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		panel.setLayout(gb);

		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JPanel innerpanel = new JPanel();
		innerpanel.setBorder(new TitledBorder("Create a New Meeting"));

		GridBagLayout gb2 = new GridBagLayout();
		GridBagConstraints gc2 = new GridBagConstraints();
		innerpanel.setLayout(gb2);

		gc2.insets = new Insets(5,5,5,5);
		gc2.anchor = GridBagConstraints.WEST;

		JLabel label = new JLabel("Enter New Meeting URI: ");
		gc2.gridy = 0;
		gc2.gridx = 0;
		gc2.gridwidth = 1;
		gb2.setConstraints(label, gc2);
		innerpanel.add(label);

		txtMeetingURI = new JTextField();
		txtMeetingURI.setColumns(40);
		txtMeetingURI.setMargin(new Insets(2,2,2,2));
		txtMeetingURI.setEditable(true);
		txtMeetingURI.setCaretPosition(0);
		gc2.gridy = 1;
		gc2.gridx = 0;
		gc2.gridwidth = 1;
		gb2.setConstraints(txtMeetingURI, gc2);
		innerpanel.add(txtMeetingURI);

		pbLoad = new UIButton("Load Meeting Data");
		pbLoad.setMnemonic(KeyEvent.VK_L);
		pbLoad.addActionListener(this);
		gc2.gridy = 2;
		gc2.gridx = 0;
		gc2.gridwidth = 1;
		gb2.setConstraints(pbLoad, gc2);
		innerpanel.add(pbLoad);

		gc.gridy = 0;
		gc.gridx = 0;
		gb.setConstraints(innerpanel, gc);
		panel.add(innerpanel);

		label = new JLabel("Select an Existing Meeting: ");
		gc.gridy = 1;
		gc.gridx = 0;
		gb.setConstraints(label, gc);
		panel.add(label);

		MeetingListTableModel model = new MeetingListTableModel();
		TableSorter sorter = new TableSorter(model);
		oTable = new JTable(sorter);
		oTable.getColumn("Name").setPreferredWidth(250);
		oTable.getColumn("Date").setPreferredWidth(100);
		oTable.getTableHeader().setReorderingAllowed(false);
		oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		oTable.clearSelection();
		sorter.addMouseListenerToHeaderInTable(oTable);
		setRenderers();

		oScrollpane = new JScrollPane(oTable);
		oScrollpane.setPreferredSize(new Dimension(350,150));
		gc.gridy = 2;
		gc.gridx = 0;
		gb.setConstraints(oScrollpane, gc);
		panel.add(oScrollpane);

		return panel;
    }

	/**
	 * Set the header renderers for the table column headers.
	 */
    public void setRenderers() {
    	int count = oTable.getModel().getColumnCount();
        for (int i = 0; i < count; i++) {
        	TableColumn aColumn = oTable.getColumnModel().getColumn(i);
        	UITableHeaderRenderer headerRenderer = new UITableHeaderRenderer();
            aColumn.setHeaderRenderer(headerRenderer);
    	}
 	}

	/**
	 * Create the buttom panel with the action buttons for this dialog.
	 * @return the newly created {@Link com.compendium.ui.UIButtonPanel UIButtonPanel} instance.
	 */
    private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbStart = new UIButton("Start");
		pbStart.setMnemonic(KeyEvent.VK_S);
		pbStart.addActionListener(this);
		getRootPane().setDefaultButton(pbStart);
		oButtonPanel.addButton(pbStart);

		pbStop = new UIButton("Stop");
		pbStop.setMnemonic(KeyEvent.VK_T);
		pbStop.addActionListener(this);
		oButtonPanel.addButton(pbStop);

		if (ProjectCompendium.APP.oMeetingManager.isRecording()) {
			pbStart.setEnabled(false);

			if (ProjectCompendium.APP.oMeetingManager.isPaused()) {
				pbStop.setEnabled(false);
			} else {
				pbStop.setEnabled(true);
			}
		} else {
			pbStop.setEnabled(false);
			pbStart.setEnabled(true);
		}

		pbCancel = new UIButton("Close");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.memetic-record", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Helper class. The data model for the list of meetings.
	 */
	class MeetingListTableModel extends AbstractTableModel {

		/** String array holding the column names for this table.*/
		private String[] columnNames = {"Name",
										"Date"};
		/** An array of arrays holding the data for this table.*/
		private Object[][] data;

		/**
		 * Constructor to create a new MeetingListTableModel instance.
		 */
		public MeetingListTableModel() {
			try {
				IModel model = ProjectCompendium.APP.getModel();
				vtMeetings = (model.getMeetingService()).getPreparedMeetings(model.getSession());
			} catch (Exception ex) {
				ProjectCompendium.APP.displayError("Meeting data could not be loaded due to:\n\n"+ex.getMessage());
			}

			data = new Object [vtMeetings.size()][2];
			Meeting meeting = null;

			int count = vtMeetings.size();
			for(int i = 0; i<count; i++) {
				meeting = (Meeting)vtMeetings.elementAt(i);
				String text = meeting.getName();
				java.util.Date date = meeting.getStartDate();
				data[i][0] = text;
				data[i][1] = date;
			}
		}

		/**
		 * Return a count of the columns in this table.
		 * @return an int representing the number of columns in this table.
		 */
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * Return a count of the rows in this table.
		 * @return an int representing the number of rows in this table.
		 */
		public int getRowCount() {
			return data.length;
		}

		/**
		 * Return the name of the column for the given column number.
		 * @param col the column whose name to return.
		 * @return a the appropriate column name.
		 */
		public String getColumnName(int col) {
			return columnNames[col];
		}

		/**
		 * Return the object at the given row and column position.
		 * @param row the row the object to return is in.
		 * @param col the column the object to return is in.
		 * @return the object at the given row and column.
		 */
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/**
		 * Return the class of the data in the column at the given column position.
		 * @param c the column whose data class to return.
		 * @return a Class fo the data in the column at the given position.
		 */
		public Class getColumnClass(int c) {
			Object obj = getValueAt(0, c);
			if (obj != null)
				return obj.getClass();

			return null;
		}
	}

	/**
	 * Update the data in the table listing the Meetings.
	 */
	public void createMeetingMap() {

		try {
			if (oMeetingManager.createMeetingMap()) {
				MeetingListTableModel tablemodel = new MeetingListTableModel();
				TableSorter sorter = new TableSorter(tablemodel);
				oTable.setModel(sorter);
				oTable.repaint();
			} else {
				ProjectCompendium.APP.displayError("Meeting map creation failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			ProjectCompendium.APP.displayError("The following error was encountered:\n\n"+ex.getMessage());
		}
	}


	/**
	 * Process the actions from a button press.
	 * @param ae the ActionEvent associated with this button push.
	 */
    public void actionPerformed(ActionEvent ae) {

		Object source = ae.getSource();

		final UIMeetingRecorderDialog dlg = this;

		if (source == pbLoad) {
			pbLoad.setEnabled(false);
			pbCancel.setEnabled(false);
			pbStart.setEnabled(false);
			pbStop.setEnabled(false);

			String sURI1 = txtMeetingURI.getText();
			final String sURI = sURI1.trim();

			Thread thread = new Thread("UIMeetingRecorderDialog.pbLoad") {
				public void run() {

					if (sURI.equals("")) {
						ProjectCompendium.APP.displayMessage("You must enter a Meeting ID", "Meeting");
						reset();
						return;
					}

					dlg.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
					ProjectCompendium.APP.setWaitCursor();

					boolean isUpdate = false;

					// CHECK THAT THE MEETING HAS NOT ALREADY BEEN ENTERED AND A MAP CREATED
					try {
						IModel model = ProjectCompendium.APP.getModel();
						String userID = model.getUserProfile().getId();
						Meeting meeting = (model.getMeetingService()).getMeeting(model.getSession(), sURI);

						if (meeting != null) {
							int answer = JOptionPane.showConfirmDialog(dlg, "This meeting has already been loaded.\n\nWould you like the map opened?\n\n", "Meeting",
									JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							if (answer == JOptionPane.YES_OPTION) {
								View view = (View)model.getNodeService().getView(model.getSession(), meeting.getMeetingMapID());
								if (view == null) {
									ProjectCompendium.APP.displayError("Could not find Meeting Map.");
								}
								else {
									view.initialize(model.getSession(), model);
									UIViewFrame oViewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
									Vector history = new Vector();
									history.addElement(new String(sTitle));
									oViewFrame.setNavigationHistory(history);
								}
							}

							reset();
							return;
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
						System.out.flush();

						ProjectCompendium.APP.displayError("Unable to check database for exisiting meeting data at this time due to:\n\n" + ex.getMessage());
						reset();
						return;
					}

					if (oMeetingManager.downloadMeetingData(sURI)) {
						Meeting oMeeting = oMeetingManager.getMeeting();
						UIMeetingMapDialog mapdlg = new UIMeetingMapDialog(oMeeting, dlg);
						UIUtilities.centerComponent(mapdlg, ProjectCompendium.APP);
						mapdlg.setVisible(true);
					} else {
						ProjectCompendium.APP.displayError("Meeting data download failure.");
					}

					reset();
				}
			};
			thread.start();
		}
		else if (source == pbStop) {
			Thread thread = new Thread("UIMeetingRecorderDialog-2") {
				public void run() {
					oMeetingManager.stopRecording();
					onCancel();
				}
			};
			thread.start();
		} else if (source == pbStart) {

			int sel = oTable.getSelectedRow();
			if (sel == -1) {
				ProjectCompendium.APP.displayError("You must selected the meeting\n\nyou wish to record events against.", "Recording a Meeting");
				return;
			}

			Meeting meeting = (Meeting)vtMeetings.elementAt(sel);
			oMeetingManager.setMeeting(meeting);

			Thread thread = new Thread("UIMeetingRecorderDialog-1") {
				public void run() {
					oMeetingManager.startRecording();
				}
			};
			thread.start();

			onCancel();
		} else if (source == pbCancel) {
			onCancel();
		}
	}

	/**
	 * Clear wait cursors and reset meeting uri field and button enablement.
	 */
	private void reset() {
		ProjectCompendium.APP.setDefaultCursor();
		setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		txtMeetingURI.setText("");
		pbLoad.setEnabled(true);
		pbCancel.setEnabled(true);
		pbStart.setEnabled(true);
		pbStop.setEnabled(true);
	}
}
