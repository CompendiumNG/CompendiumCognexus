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

package com.compendium.ui.menus;

import static com.compendium.ProjectCompendium.*;

import java.awt.event.*;
import java.util.*;
import java.sql.SQLException;

import javax.help.*;
import javax.swing.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.UIImportFlashMeetingXMLDialog;
import com.compendium.ui.dialogs.UISystemSettingsDialog;

// ON NON-MAC PLATFORM, THIS REQUIRES AppleJavaExtensions.jar stub classes TO COMPILE
import com.apple.eawt.*;

/**
 * This class creates the file menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuFile implements IUIMenu, ActionListener, IUIConstants, ICoreConstants {

	/** The File menu.*/
	private JMenu				mnuMainMenu				= null;

	private JMenuItem			miSystemSettings		= null;

	/** The menu holding the convert database options.*/
	private JMenuItem			mnuMainMenuConvert			= null;

	/** The menu item to open a database project.*/
	private JMenuItem			miFileOpen				= null;

	/** The menu item to crate a new database project.*/
	private JMenuItem			miFileNew				= null;

	/** The menu item to convert an MySQL database to the Derby database format.*/
	private JMenuItem			miFileConvert		= null;

	private JMenuItem			miSync				= null;

	/** The menu item to convert an Derby database to the MySQL database format.*/
	//private JMenuItem			miFileConvertFromDerby		= null;

	/** The menu item to open the database management dialog.*/
	private JMenuItem			miDatabases				= null;

	/** The menu item to mark entire project as "Seen" */
	private JMenuItem			miMarkProjectSeen		= null;

	/** The menu item to open the database administration dialog.*/
	private JMenuItem			miDatabaseAdministration	= null;

	/** The menu item to open the backup dialog.*/
	private JMenuItem			miFileBackup			= null;

	/** The menu item to close the curent database project.*/
	private JMenuItem			miFileClose				= null;

// IMPORT MENU
	/** The import menu.*/
	private JMenu				mnuImport				= null;

	/** The Questmap import menu.*/
//	private JMenu				miFileImport			= null;

	/** The menu item to import from Questmap into the current view.*/
//	private JMenuItem			miImportCurrentView		= null;

	/** The menu item to import from questmap into selected views.*/
//	private JMenuItem			miImportMultipleViews	= null;

	/** The menu item to import an XML file.*/
	private JMenuItem			miImportXMLView			= null;

	/** The menu item to import an image folder.*/
	private JMenuItem			miImportImageFolder		= null;

	/** The menu item to import Flashmeeting XML.*/
	private JMenuItem			miImportXMLFlashmeeting = null;

// EXPORT MENU
	/** The Export menu.*/
	private JMenu				mnuExport				= null;

	/** The menu item to export to a HTML Outline file.*/
	private JMenuItem			miExportHTMLOutline		= null;
	private JMenuItem			miExportWordDocOutline		= null;

	/** The menu item to export to HTML Views (with image maps).*/
	private JMenuItem			miExportHTMLViews		= null;

	/** The menu item to export to XML.*/
	private JMenuItem			miExportXMLView			= null;

	/** The menu item to export a HTML view with the XML included.*/
	private JMenuItem			miExportHTMLViewXML		= null;

	/** The menu item to save current amp as a jpg.*/
	private JMenuItem			miSaveAsJpeg			= null;

	/** NOT CURRENTLY USED.*/
	private JMenuItem			miFilePageSetup			= null;

	/** The menu item to print the current map.*/
	private JMenuItem			miFilePrint				= null;

	/** The menu item to exit the application.*/
	private JMenuItem			miFileExit				= null;

// CONNECTIONS MENU
	/** The Connection menu.*/
	private JMenu				mnuConnect				= null;
	
// PEER_TO_PEER
	/** The Peer To Peer menu*.
	private JMenu				mnuPeerToPeer			= null;

	/** The menu item to open the bradcasters control dialog.*/
	private JMenuItem			miPTPbroadcast			= null;


	/** The platform specific shortcut key to use.*/
	private int shortcutKey;

	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;

	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced.
	 */
	public UIMenuFile(boolean bSimple) {
		System.out.println("entered UIMenuFile constructor");

		shortcutKey = ProjectCompendium.APP.shortcutKey;
		this.bSimpleInterface = bSimple;

		mnuMainMenu	= new JMenu(Messages.getString("UIMenuManager.0")); //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.file"); //$NON-NLS-1$
		mnuMainMenu.setMnemonic(KeyEvent.VK_F);

		createMenuItems();
		System.out.println("exiting UIMenuFile constructor");
	}

	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple
	 */public void setIsSimple(boolean isSimple) {
		bSimpleInterface = isSimple;
		recreateMenu();
	}

	/**
	 * Redraw the menu items
	 */
	private void recreateMenu() {
		mnuMainMenu.removeAll();
		createMenuItems();
		onDatabaseOpen();
	}

	/**
	 * Create and return the File menu.
	 * @return JMenu the File menu.
	 */
	private JMenu createMenuItems() {
		System.out.println("entered createMenuItems");

		miFileNew = new JMenuItem(Messages.getString("UIMenuManager.2")); //$NON-NLS-1$
		miFileNew.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_N, shortcutKey));
		miFileNew.setMnemonic(KeyEvent.VK_N);
		miFileNew.addActionListener(this);
		mnuMainMenu.add(miFileNew);

		miFileOpen = new JMenuItem(Messages.getString("UIMenuManager.3")); //$NON-NLS-1$
		miFileOpen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_O, shortcutKey));
		miFileOpen.setMnemonic(KeyEvent.VK_O);
		miFileOpen.setEnabled(false);
		miFileOpen.addActionListener(this);
		mnuMainMenu.add(miFileOpen);

		miFileClose = new JMenuItem(Messages.getString("UIMenuManager.4")); //$NON-NLS-1$
		miFileClose.setMnemonic(KeyEvent.VK_C);
		miFileClose.addActionListener(this);
		mnuMainMenu.add(miFileClose);

		miSystemSettings = new JMenuItem(Messages.getString("UIMenuManager.5")); //$NON-NLS-1$
		miSystemSettings.setMnemonic(KeyEvent.VK_S);
		miSystemSettings.addActionListener(this);
		mnuMainMenu.add(miSystemSettings);

		miFileBackup = new JMenuItem(Messages.getString("UIMenuManager.6")); //$NON-NLS-1$
		miFileBackup.setToolTipText(Messages.getString("UIMenuManager.7")); //$NON-NLS-1$
		miFileBackup.setEnabled(false);
		miFileBackup.setMnemonic(KeyEvent.VK_B);
		miFileBackup.addActionListener(this);
		mnuMainMenu.add(miFileBackup);

		mnuMainMenu.addSeparator();

		miDatabaseAdministration = new JMenuItem(Messages.getString("UIMenuManager.8")); //$NON-NLS-1$
		miDatabaseAdministration.setMnemonic(KeyEvent.VK_A);
		miDatabaseAdministration.setDisplayedMnemonicIndex(10);
		miDatabaseAdministration.addActionListener(this);
		mnuMainMenu.add(miDatabaseAdministration);

		System.out.println("entering database items");

		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			System.out.println("mysql menu item");
			miFileConvert = new JMenuItem(Messages.getString("UIMenuManager.9")); //$NON-NLS-1$
			miFileConvert.setMnemonic(KeyEvent.VK_M);
			miFileConvert.setDisplayedMnemonicIndex(6);
			miFileConvert.addActionListener(this);
			mnuMainMenu.add(miFileConvert);
		}
		else {
			System.out.println("derby menu item");
			miFileConvert = new JMenuItem(Messages.getString("UIMenuManager.10")); //$NON-NLS-1$
			miFileConvert.setMnemonic(KeyEvent.VK_M);
			miFileConvert.setDisplayedMnemonicIndex(6);
			miFileConvert.addActionListener(this);
			mnuMainMenu.add(miFileConvert);
		}

		//java.util.Map<String, String> env = System.getenv();
		//if (env.get("COMPENDIUM_DEVELOPER") != null && env.get("COMPENDIUM_DEVELOPER").equalsIgnoreCase("true"))
		//{
		//	System.out.println("C Developer!");
			miSync = new JMenuItem("Synchronize...");
			miSync.addActionListener(this);
			mnuMainMenu.add(miSync);
		//}
		//else
		//{
		//	System.out.println("not a C developer");
		//}

		miDatabases = new JMenuItem(Messages.getString("UIMenuManager.11")); //$NON-NLS-1$
		miDatabases.setMnemonic(KeyEvent.VK_M);
		miDatabases.addActionListener(this);
		mnuMainMenu.add(miDatabases);

		miMarkProjectSeen = new JMenuItem(Messages.getString("UIMenuManager.234")); //$NON-NLS-1$
		miMarkProjectSeen.addActionListener(this);
		mnuMainMenu.add(miMarkProjectSeen);

		mnuMainMenu.addSeparator();

		// create EXPORT options
		mnuExport = new JMenu(Messages.getString("UIMenuManager.12")); //$NON-NLS-1$
		mnuExport.setMnemonic(KeyEvent.VK_E);

		miExportXMLView = new JMenuItem(Messages.getString("UIMenuManager.14")); //$NON-NLS-1$
		miExportXMLView.setMnemonic(KeyEvent.VK_X);
		miExportXMLView.addActionListener(this);
		mnuExport.add(miExportXMLView);

		miExportHTMLOutline = new JMenuItem(Messages.getString("UIMenuManager.15")); //$NON-NLS-1$
		miExportHTMLOutline.setMnemonic(KeyEvent.VK_O);
		miExportHTMLOutline.addActionListener(this);
		mnuExport.add(miExportHTMLOutline);

		miExportWordDocOutline = new JMenuItem(Messages.getString("UIMenuManager.15.1")); //$NON-NLS-1$
		//miExportWordDocOutline.setMnemonic(KeyEvent.VK_O);
		miExportWordDocOutline.addActionListener(this);
		mnuExport.add(miExportWordDocOutline);

		miExportHTMLViews = new JMenuItem(Messages.getString("UIMenuManager.16")); //$NON-NLS-1$
		miExportHTMLViews.setMnemonic(KeyEvent.VK_W);
		miExportHTMLViews.addActionListener(this);
		mnuExport.add(miExportHTMLViews);

		miExportHTMLViewXML = new JMenuItem("Power Export...");
		miExportHTMLViewXML.setToolTipText("Integrated Web Map and Outline Export with XML zip export inlcuded");
		miExportHTMLViewXML.setMnemonic(KeyEvent.VK_P);
		miExportHTMLViewXML.addActionListener(this);
		mnuExport.add(miExportHTMLViewXML);

		miSaveAsJpeg = new JMenuItem(Messages.getString("UIMenuManager.13")); //$NON-NLS-1$
		miSaveAsJpeg.setMnemonic(KeyEvent.VK_J);
		miSaveAsJpeg.addActionListener(this);
		mnuExport.add(miSaveAsJpeg);

		mnuMainMenu.add(mnuExport);

		mnuMainMenu.addSeparator();

		// create IMPORT options
		mnuImport = new JMenu(Messages.getString("UIMenuManager.17")); //$NON-NLS-1$
		mnuImport.setMnemonic(KeyEvent.VK_I);

		miImportXMLView = new JMenuItem(Messages.getString("UIMenuManager.18")); //$NON-NLS-1$
		miImportXMLView.setMnemonic(KeyEvent.VK_X);
		miImportXMLView.addActionListener(this);
		mnuImport.add(miImportXMLView);

		miImportXMLFlashmeeting = new JMenuItem("FlashMeeting XML...");
		miImportXMLFlashmeeting.setMnemonic(KeyEvent.VK_F);
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);

//		miFileImport = new JMenu(Messages.getString("UIMenuManager.19")); //$NON-NLS-1$
//		miFileImport.setMnemonic(KeyEvent.VK_Q);
//		miFileImport.addActionListener(this);

		// INCASE I WANT TO PUT FILE IMAGES BACK, KEEP ON REFERENCE
		//miImportCurrentView = new JMenuItem("Current View..", UIImages.get(IUIConstants.NEW_ICON));

//		miImportCurrentView = new JMenuItem(Messages.getString("UIMenuManager.20")); //$NON-NLS-1$
//		miImportCurrentView.addActionListener(this);
//		miFileImport.add(miImportCurrentView);

//		miImportMultipleViews = new JMenuItem(Messages.getString("UIMenuManager.21")); //$NON-NLS-1$
//		miImportMultipleViews.addActionListener(this);
//		miFileImport.add(miImportMultipleViews);

//		mnuImport.add(miFileImport);

		miImportImageFolder = new JMenuItem(Messages.getString("UIMenuManager.22")); //$NON-NLS-1$
		miImportImageFolder.setMnemonic(KeyEvent.VK_I);
		miImportImageFolder.addActionListener(this);
		mnuImport.add(miImportImageFolder);

		mnuMainMenu.add(mnuImport);
		mnuMainMenu.addSeparator();


		// SEND TO

		miFilePrint = new JMenuItem(Messages.getString("UIMenuManager.29")); //$NON-NLS-1$
		miFilePrint.setMnemonic(KeyEvent.VK_P);
		miFilePrint.addActionListener(this);
		mnuMainMenu.add(miFilePrint);

		miFileExit = new JMenuItem(Messages.getString("UIMenuManager.30")); //$NON-NLS-1$
		miFileExit.addActionListener(this);

		if (!ProjectCompendium.isMac) {
			mnuMainMenu.addSeparator();
			miFileExit.setMnemonic(KeyEvent.VK_X);
			mnuMainMenu.add(miFileExit);
		}

		System.out.println("exiting createMenuItems");

		return mnuMainMenu;
	}

	/**
	 * Handles most menu action event for this application.
	 *
	 * @param evt the generated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(miSystemSettings)) {
			UISystemSettingsDialog dlg = new UISystemSettingsDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		} else if (source.equals(miFileNew)) {
			//JOptionPane.showMessageDialog(ProjectCompendium.APP, "You may not create a new Project. If you need a new Project created for you, " +
				//	"\nplease contact the System Administrator.");
			ProjectCompendium.APP.onFileNew();
		} else if (source.equals(miFileOpen)) {
			ProjectCompendium.APP.onFileOpen();
		} else if (source.equals(miFileClose)) {
			ProjectCompendium.APP.onFileClose();
		}
		else if (source.equals(miFileConvert)) {
			if (miFileConvert.getText().equals(Messages.getString("UIMenuManager.140"))) { //$NON-NLS-1$
				ProjectCompendium.APP.onFileConvertFromMySQL();
			}
			else {
				ProjectCompendium.APP.onFileConvertFromDerby();
			}
		}
		else if (source.equals(miFileBackup))
			ProjectCompendium.APP.onFileBackup();
//		else if (source.equals(miImportCurrentView))
//			ProjectCompendium.APP.onFileImport(false);
//		else if (source.equals(miImportMultipleViews))
//			ProjectCompendium.APP.onFileImport(true);
		else if (source.equals(miImportImageFolder))
			ProjectCompendium.APP.onFileImportImageFolder();

		else if (source.equals(miExportHTMLOutline))
			ProjectCompendium.APP.onFileExportHTMLOutline();
		else if (source.equals(miExportWordDocOutline))
			ProjectCompendium.APP.onFileExportWordDocOutline();
		else if (source.equals(miExportHTMLViews))
			ProjectCompendium.APP.onFileExportHTMLView();

		else if (source.equals(miExportXMLView))
			ProjectCompendium.APP.onFileXMLExport(false);
		else if (source.equals(miExportHTMLViewXML))
			ProjectCompendium.APP.onFileExportPower();
		else if (source.equals(miImportXMLView))
			ProjectCompendium.APP.onFileXMLImport();
		else if (source.equals(miImportXMLFlashmeeting)) {
			UIImportFlashMeetingXMLDialog dlg = new UIImportFlashMeetingXMLDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		}
		else if (source.equals(miSaveAsJpeg))
			ProjectCompendium.APP.onSaveAsJpeg();

		else if (source.equals(miFilePageSetup))
			ProjectCompendium.APP.onFilePageSetup();
		else if (source.equals(miFilePrint))
			ProjectCompendium.APP.onFilePrint();
		else if (source.equals(miFileExit)) {
			ProjectCompendium.APP.onExit();
		}
		else if (source.equals(miDatabaseAdministration)) {
			ProjectCompendium.APP.onFileDatabaseAdmin();
		}
		else if (source.equals(miDatabases))
			ProjectCompendium.APP.onDatabases();
		else if (source.equals(miMarkProjectSeen)) {
			try {
				ProjectCompendium.APP.onMarkProjectSeen();
			}
			catch (SQLException ex) {}
		}
		else if (source.equals(miSync))
		{
			ProjectCompendium.APP.onSynchronize();
		}


		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Updates the menu when a database project is closed.
	 */
	public void onDatabaseClose() {

		try {
			if (miFileOpen != null) {
				miFileOpen.setEnabled(true);
			}
			if (miFileNew != null) {
				miFileNew.setEnabled(true);
			}
			if (miFileClose != null) {
				miFileClose.setEnabled(false);
			}
			if (miFileBackup != null) {
				miFileBackup.setEnabled(false);
			}
			if (miFilePrint != null) {
				miFilePrint.setEnabled(false);
			}
			if (miMarkProjectSeen != null) {
				miMarkProjectSeen.setEnabled(false);
			}
			if (mnuImport != null) {
				mnuImport.setEnabled(false);
			}
			if (mnuExport != null) {
				mnuExport.setEnabled(false);
			}
			if (mnuConnect != null) {
				mnuConnect.setEnabled(false);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UIMenuFile.onDatabaseClose)\n\n" + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Updates the menu when a database project is opened.
	 */
	public void onDatabaseOpen() {

		if (miFileOpen != null) {
			miFileOpen.setEnabled(false);
		}
		if (miFileNew != null) {
			miFileNew.setEnabled(false);
		}
		if (miFileClose != null) {
			miFileClose.setEnabled(true);
		}
		if (miFileBackup != null) {
			if (ProjectCompendium.APP.getModel() != null) {
				boolean bUserAdmin = ProjectCompendium.APP.getModel().getUserProfile().isAdministrator();
				miFileBackup.setEnabled(bUserAdmin);
			}
		}
		if (miFilePrint != null) {
			miFilePrint.setEnabled(true);
		}
		if (miMarkProjectSeen != null) {
			miMarkProjectSeen.setEnabled(true);
		}
		if (mnuImport != null) {
			mnuImport.setEnabled(true);
		}
		if (mnuExport != null) {
			mnuExport.setEnabled(true);
		}
		if (mnuConnect != null) {
			mnuConnect.setEnabled(true);
		}
	}
	/**
 	 * Enable/disable  menu items when nodes or links selected.
 	 * Does nothing.
  	 * @param selected true for enabled false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}

	/**
 	 * Indicates when nodes on a view are selected and deselected.
 	 * Does Nothing.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {}
	
	/**
	 * Enable/disable the convert database menu option as appropriate.
	 */
	public void enableConvertMenuOptions() {

		if (miFileConvert != null) {
			if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.DERBY_DATABASE)
			{
				miFileConvert.setText(Messages.getString("UIMenuManager.232")); //$NON-NLS-1$

/*				java.util.Map<String, String> env = System.getenv();
				if (env.get("COMPENDIUM_DEVELOPER") != null && env.get("COMPENDIUM_DEVELOPER").equalsIgnoreCase("true"))
				{
					System.out.println("C Developer!");
					miSync = new JMenuItem("Syncrhonize...");
					miSync.addActionListener(this);
					mnuMainMenu.add(miSync);
				}
				else
				{
					System.out.println("not a C developer");
				}
*/

			}
			else
			{
				miFileConvert.setText(Messages.getString("UIMenuManager.233")); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Enable/disable the file open menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setFileOpenEnablement(boolean enabled) {
		if (miFileOpen != null) {
			miFileOpen.setEnabled( enabled );
		}
	}

	/**
	 * Exit the application.
	 */
	public void exit() {
		miFileExit.doClick();
	}

	/**
	 * Update the look and feel of the menu.
	 */
	public void updateLAF() {
		if (mnuMainMenu != null)
			SwingUtilities.updateComponentTreeUI(mnuMainMenu);
	}

	/**
	 * Return a reference to the main menu.
	 * @return JMenu a reference to the main menu.
	 */
	public JMenu getMenu() {
		return mnuMainMenu;
	}
}
