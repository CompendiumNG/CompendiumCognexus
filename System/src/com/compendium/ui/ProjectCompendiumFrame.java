/********************************************************************************
 *                                                                            *
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

package com.compendium.ui;

import static com.compendium.ProjectCompendium.*;

import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.net.*;
import java.beans.PropertyVetoException;
import java.nio.channels.FileLock;

import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.tree.DefaultMutableTreeNode;

import javax.imageio.*;
import javax.imageio.stream.*;
import com.sun.image.codec.jpeg.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;
import com.compendium.core.*;

import com.compendium.*;

import com.compendium.meeting.*;

import com.compendium.ui.edits.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.toolbars.*;
import com.compendium.ui.menus.*;
import com.compendium.ui.stencils.*;
import com.compendium.ui.linkgroups.*;

import com.compendium.io.html.HTMLOutline;
import com.compendium.io.html.HTMLViews;

import com.compendium.io.udig.*;
import com.compendium.io.xml.XMLExportNoThread;

import com.compendium.sync.*;
/*
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
*/

/**
 * This is the main JFrame for the application and holds many central application variables and methods.
 *
 * @author	sajid / Michelle Bachler / Lakshmi Prabhakaran
 */
public class ProjectCompendiumFrame	extends JFrame
									implements KeyListener, IUIConstants, ICoreConstants {

    /** Computed serial version ID */
	private static final long serialVersionUID 			= 5065491272948039358L;

	/** The file used to tell if Compendium is already running */
    private static final String RUNNING_FILE 			=
			                            System.getProperty("user.home") +
			                            System.getProperty("file.separator") +
			                            ".compendium_running";

	/** The layer to add view frames to in the desktop.*/
	private static final Integer VIEWLAYER 				= JLayeredPane.DEFAULT_LAYER; //new Integer(5);

	/** The offset to use when cascading frames.*/
	private static final int INTERNALFRAMEOFFSET		= 24;

	/** The internal frame width when cascasding,*/
	private static final int INTERNALFRAMEWIDTH			= 300;

	/** The internal frame height when cascading.*/
	private static final int INTERNALFRAMEHEIGHT		= 300;

	/** The default width to open a frame.*/
	private static final int FRWIDTH					= 500;

	/** The default height to open a frame.*/
	private static final int FRHEIGHT					= 500;

	/** The default offset to open a frame.*/
	private static final int FROFFSET					= 24;


	// PUBLIC MANAGERS
	/** The manager for the stencils.*/
	public UIStencilManager		oStencilManager			= null;

	/** The manager for the link groups.*/
	public UILinkGroupManager   oLinkGroupManager		= null;

	/** The manager for the link groups.*/
	public UIRefreshManager   	oRefreshManager			= null;

	/** The service manager used by this frame to access Derby database services.*/
	public IServiceManager		oDerbyServiceManager	= null;

	/** The service manager used by this frame to access database services.*/
	public IServiceManager		oServiceManager			= null;

	/** Holds the information needed / manages Meeting Recording and Meeting Replay.*/
	public MeetingManager		oMeetingManager			= null;

	/** The Communication Manager for talking to a uDig Application.*/
	public UDigCommunicationManager oUDigCommunicationManager 	= null;


	/** Holds the currently being used MySQL connection profile details.*/
	public ExternalConnection oCurrentMySQLConnection 	= null;

	/** The platform specific shortcut key.*/
	public 	int 				shortcutKey;

	/** _x & _y last know position of mouse, updated by List and ViewPane UIs.*/
	public	int					_x, _y;

	/** The table for checking nodes in a paste operation.*/
	public Hashtable 			ht_pasteCheck			= new Hashtable(51);

	/** The user sepcified name for the currently open database.*/
	public String				sFriendlyName			= "";

	/** The top node for the code group tree.*/
	public DefaultMutableTreeNode codeGroupNode 		= null;

	/** The database administration instance used to manage default locale Derby database.*/
	public DBAdminDerbyDatabase adminDerbyDatabase  	= null;

	/** The database administration instance used to manage the current database.*/
	public DBAdminDatabase 		adminDatabase 			= null;

	/** The main split pane object.*/
	public JSplitPane 			oSplitter				= null;

	/** The main tabbed pane*/
	public JTabbedPane			oTabbedPane				= null;

	/** The current database name.*/
	//public static String sCurrentDatabase = "";

	/** The View associated with the home view for the currently open database.*/
	private View				oHomeView				= null;

	/** The current login name for the current user in the open database.*/
	private String				sUserName				= "";

	/** The current password for the current user in the open database.*/
	private String				sUserPassword			= "";

	/** The cache model for the currently open database.*/
	private IModel				oModel					= null;

	/** A reference to the trashbin node.*/
	private NodeSummary			oTrashbinNode			= null;

	/** A reference to the inbox node.*/
	private NodeSummary			oInboxNode				= null;

	/** A List of the View Frames that have been opened during this database session.*/
	private Vector 				viewFrameList 			= new Vector();


	/** The comma separated list of Access projects.*/
	//private String			sAccessProjects			= "";

	/** The list of current databsae project names.*/
	private Vector				vtProjects				= null;

	/** The manager for the menubar.*/
	private UIMenuManager		oMenuManager			= null;

	/** The manager for the tool bar.*/
	private UIToolBarManager	oToolBarManager			= null;

	/** True if a new delete operation has been started.*/
	private boolean				isNewDelete				= false;

	/** The content pane for this frame.*/
	private Container			oContentPane			= null;

	/** The main panel for this frame.*/
	private JPanel				oMainPanel				= null;

	/** The inner panel for this frame.*/
	private JPanel				oInnerPanel				= null;

	/** The parent class to this class.*/
	private ProjectCompendium	oParent					= null;

	/** The screen width when opening this frame.*/
	private int					nScreenWidth			= 0;

	/** The screen height when opening this frame.*/
	private int					nScreenHeight			= 0;

	/** The main menu bar for this frame.*/
	private JMenuBar			mbMenuBar				= null;

	/** The desktop pane for this frame.*/
	private JDesktopPane		oDesktop				= null;

	/** The status bar for this frame.*/
	private UIStatusBar			oStatusBar				= null;

	/** The view history bar for this frame.*/
	private UIViewHistoryBar	oViewHistoryBar			= null;

	/** The dialog for opening and logging in to a database project.*/
	private UILogonDialog 		oLogonDialog			= null;

	/** Indicates whether to proceed with a login.*/
	private boolean				bProceed				= false;

	/** Semaphore to prevent simultaneous timed/manual refresh operation */
	private static boolean				bReloadingProject = false;

	/** Semaphore to prevent overlapping timed refresh operations */
	private static boolean				bChecking = false;

	/** The hostname for this machine.*/
	private String				sServerName				= "";

	/** The ip address for this machine.*/
	private String				sServerIP				= "";

	/** The clipboard for this application.*/
	private Clipboard			oClipboard				= null;

	/** The class that controls the audio part of the application*/
	private UIAudio				audioThread				= null;

	/** Holds the properties saved for user import options.*/
	private ImportProfile		oImportProfile			= null;

	/** A reference to the Questmap import dialog.*/
	private UIImportDialog		dlgImport				= null;

	/** A reference to the HTML Outline export dialog.*/
	private UIExportDialog		dlgExport				= null;

	/** A reference to the XML import dialog.*/
	private UIImportXMLDialog	dlgImportXML			= null;

	/** A reference to the XML export dialog.*/
	private UIExportXMLDialog	dlgExportXML			= null;

	private UIMarkProjectSeenDialog	dlgMarkProjectSeen	= null;

	/** A reference to the HTML Views export dialog.*/
	private UIExportViewDialog 	dialog2 				= null;

	/** A reference to the Aerial view dialog for the current View.*/
	private UIAerialDialog		oAerialViewDialog		= null;

	/** A reference to the About dialog.*/
	private	UIAboutDialog		oAboutDialog			= null;


	//PROPERTIES
	/** Node label font currently being used.*/
	public static Font 			labelFont 				= new Font("Dialog", Font.PLAIN, 12);

	/** A reference to the windows look and feel string.*/
    private static String 		windowsClassName 		= "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	// CLAIMAKER
	/** The url for the ClaiMaker server search.*/
	//private static String 		claiMakerServer 		= "";

	/** Whether the CaliMaker url has been set or not.*/
//	private static boolean 		claiMakerConnected 		= false;


	// HELP
	/** The name of the helpset to load for the help.*/
    private static final String helpsetName 			= "CompendiumHelp";

	/** A reference to the HelpSet for this application.*/
    public HelpSet 				mainHS 					= null;

	/** A reference to the HelpBroker for this application.*/
    public HelpBroker 			mainHB;

	/** For tracking external opy/paste operations.*/
	private boolean 			externalCopy 			= false;

	/** The currently active tag group.*/
	private String 				activeGroup 			= "";

	/** The currently active link group.*/
	private String 				activeLinkGroup 		= "1"; //This is the id of the default link group

	/** A reference to the start up dialog.*/
	private UIStartUp 			startUpDlg 				= null;

	/**
	 * The property file holding the applications to launch reference.
	 * For use with for Mac and Linux platforms.
	 */
	private Properties		launchApplications 			= null;

	/** Indicates if this is the first time this application has been run.*/
	private boolean 		firstTime 					= false;

	/** Indicates if the login dialog should be opened after startup.*/
	private boolean 		bOpenFile 					= false;

  /** True if this process created the running file */
    private boolean 		createdRunningFile 			= false;

	/** The name of the project in use   */
	private String 			sProject					= "";

	/** Is Paste Enabled? */
	public boolean 			isPasteEnabled				= false;

	/** The UIViewOutline object to display outline view */
	public UIViewOutline 		outlineView  			= null;

	/** Holds information for the depth check.*/
	private Hashtable		htCheckDepth 		= null;

	/** Holds child data when calculating export views data.*/
	private Hashtable		htChildrenAdded 	= null;

	/**
	 * Constructor, creates a new ProjectCompendiumFrame instance.
	 * @param parent, the parent class to this frame.
	 * @param title, the title for this frame.
	 * @param serverName, the host name for this machine.
	 * @param IP, the ip address of this machine.
	 * @param dlg, a refernce to the start up dialog.
	 */
	public ProjectCompendiumFrame(ProjectCompendium parent, String title, String serverName, String IP, UIStartUp dlg) {

		super(title);

		this.startUpDlg = dlg;
		this.oParent = parent;
		this.sServerName = serverName;
		this.sServerIP = IP;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				onExit();
			}
		});

		// LOAD ANY LAUNCH APPLICATION PROPERTIES REQURIED BY MAC AND LINUX
		String file_name = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LaunchApplications.properties";

		File file = new File(file_name);
		launchApplications = new Properties();
		if (file.exists()) {
			try {
				launchApplications.load(new FileInputStream(file_name));
			}
			catch (IOException e) {}
		}

		// SET DERBY DATABASE LOCATION
		File file2 = new File(ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Databases");
		Properties p = System.getProperties();
		p.put("derby.system.home", file2.getAbsolutePath());
		if (ProjectCompendium.isMac) {
			setMacMenuBar(APP_PROPERTIES.isMacMenuBar());
			p.put("derby.storage.fileSyncTransactionLog", "true");
		}

		// SET PROXY
		setProxy();

		// CREATE UDIG CONNECTION MANAGER IF REQUIRED
		if (APP_PROPERTIES.isStartUDigCommunications()) {
			startUDigConnection();
		}
	}

	/**
	 * Create the uDig connection manager which will open a server socket.
	 */
	public void startUDigConnection() {
		try {
			oUDigCommunicationManager = new UDigCommunicationManager();
		} catch(Exception e) {
			displayError("Unable to start uDig Communications");
		}
	}

	/**
	 * Destroy the uDig connection manager and server socket.
	 */
	public void stopUDigConnection() {
		try {
			if (oUDigCommunicationManager != null) {
				oUDigCommunicationManager.sendGoodbye();
				oUDigCommunicationManager.destroyClientSocket();
				oUDigCommunicationManager.destroyServerSocket();
				oUDigCommunicationManager = null;
			}
		} catch(Exception e) {
			//displayError("Unable to stop uDig Communications");
		}
	}

	/**
	 * Set the proxy for Compendium to use for HTTP connections.
	 */
	public void setProxy() {

		File optionsFile = new File(UISystemSettingsDialog.SETUPFILE);
		Properties oConnectionProperties = new Properties();
		String sLocalProxyHost = "";
		String sLocalProxyPort = "";
		boolean bSuccessful = false;
		if (optionsFile.exists()) {
			try {
				oConnectionProperties.load(new FileInputStream(UISystemSettingsDialog.SETUPFILE));
				String value = oConnectionProperties.getProperty("localproxyhost");
				if (value != null) {
					sLocalProxyHost = value;
				}
				value = oConnectionProperties.getProperty("localproxyport");
				if (value != null) {
					sLocalProxyPort = value;
				}
				bSuccessful = true;
			} catch (IOException e) {
				System.out.println("Problems accessing system settings: "+e.getMessage());
			}
		}

		try {
			if (sLocalProxyHost == null || sLocalProxyHost.equals("") ||
					sLocalProxyPort == null || sLocalProxyPort.equals("") || !bSuccessful) {

				// THIS CODE PULLED THE PROXY OUT, BUT THEN KILLED THE CODE FURTHER ON
				// POSSIBLY IN RELATION TO MYSQL - INVESTIGATE FURTHER WHAT THIS PROPERTY DOES
				/*System.setProperty("java.net.useSystemProxies","true");

				java.util.List proxies = ProxySelector.getDefault().select(new URI("http://www.google.com/"));
				if (proxies.size() > 0) {

					Proxy proxy = (Proxy)proxies.get(0);
					InetSocketAddress proxyAddress = (InetSocketAddress)proxy.address();
					sLocalProxyHost = proxyAddress.getHostName();
					int nPort = proxyAddress.getPort();
					sLocalProxyPort = (new Integer(nPort)).toString();
					if (sLocalProxyHost != null && !sLocalProxyHost.equals("")) {
						if (optionsFile.exists()) {
							if (oConnectionProperties.isEmpty()) {
								oConnectionProperties.load(new FileInputStream(UISystemSettingsDialog.SETUPFILE));
							}
						}
						oConnectionProperties.put("localproxyhost", sLocalProxyHost);
						oConnectionProperties.put("localproxyport", sLocalProxyPort);
						oConnectionProperties.store(new FileOutputStream(UISystemSettingsDialog.SETUPFILE), "Access Grid Details");

						//System.setProperty("proxySet", "true");
						//System.setProperty("http.proxyHost", sLocalProxyHost);
						//System.setProperty("http.proxyPort", sLocalProxyPort);
					}
				}*/
			}
			else {
				System.setProperty("proxySet", "true");
				System.setProperty("http.proxyHost", sLocalProxyHost);
				System.setProperty("http.proxyPort", sLocalProxyPort);
			}

		} catch (Exception e) {
			System.out.println("Problems setting proxy due to: "+e.getMessage());
		}
	}

	/**
	 * Moves the menu bar from the top of the Application to the top of the screen, and back again.
	 */
	public void setMacMenuBar(boolean up) {

		//if (up)
		//	System.setProperty("apple.laf.useScreenMenuBar", "true");
		//else
		//	System.setProperty("apple.laf.useScreenMenuBar", "false");
	}

	/**
	 * Return the Properties class holding external file launch application data.
	 * (for Mac and Linux platforms).
	 * @return Properties, the Properties class holding external file launch application data
	 */
	public Properties getLaunchApplications() {
		return launchApplications;
	}

	/**
	 * Draw frame contents and initialises data.
	 */
	public boolean initialiseFrame() {

        /*try {
            File runningFile = new File(RUNNING_FILE);
            if (runningFile.exists()) {
                FileInputStream input = new FileInputStream(runningFile);
                FileLock lock =
                    input.getChannel().lock(0, runningFile.length(), true);
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(input));
                Vector instances = new Vector();
                String line = reader.readLine();
                while (line != null) {
                    instances.add(line);
                    line = reader.readLine();
                }
                lock.release();
                reader.close();
                input.close();
                if (instances.contains(ProjectCompendium.sHOMEPATH)) {
                    if (JOptionPane.showConfirmDialog(this,
                            "There appears to already be an instance of " +
                            "Compendium running.\nThis message could be " +
                            "appearing because an earlier instance of " +
                            "Compendium did not terminate cleanly.\n" +
                            "Would you like to try to start" +
                            " another instance?",
                            "Confirm Compendium Start",
                            JOptionPane.YES_NO_OPTION)
                            != JOptionPane.YES_OPTION) {
                        System.err.println("Quitting");
                        System.exit(0);
                    }
                }
            }
            FileOutputStream output = new FileOutputStream(RUNNING_FILE,
                                                                  true);
            FileLock lock =
                output.getChannel().lock(0, runningFile.length(), false);
            PrintWriter writer = new PrintWriter(output);
            writer.println(ProjectCompendium.sHOMEPATH);
            lock.release();
            writer.close();
            output.close();
            createdRunningFile = true;
        } catch (Exception e) {
            e.printStackTrace();
        }*/

		// HELP
		try {
		    String helpfile = ProjectCompendium.sSYSPATH+ProjectCompendium.sFS+"System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Help"+ProjectCompendium.sFS+"CompendiumHelp.hs";

			File file = new File(helpfile);
			if (file.exists()) {
				URL url = file.toURL();
		     	mainHS = new HelpSet(null, url);
		    	mainHB = mainHS.createHelpBroker();
				mainHB.enableHelpKey(ProjectCompendium.APP.getRootPane(), "top", null);
			}
			else {
				System.out.println("Can't find help file = "+helpfile);
			}
		}
		catch (Exception ee) {
		    ee.printStackTrace();
		    System.out.println ("Help Set "+helpsetName+" not found \n\n"+ee.getMessage());
		}

		if (!init()) {
			onExit();
		}

		pack();

		if (APP_PROPERTIES.getLastScreenWidth() == -1 && APP_PROPERTIES.getLastScreenHeight() == -1) {

			//determines the size of user screen in pixels
			Toolkit tk = this.getToolkit();
			Dimension screensize = tk.getScreenSize();
			nScreenWidth = screensize.width;
			nScreenHeight = screensize.height;

			int appWidth = (new Double(nScreenWidth*0.90)).intValue();
			int appHeight = (new Double(nScreenHeight*0.90)).intValue();
			int appLocHeight = nScreenHeight/2 -(new Double(appHeight*0.55)).intValue();
			int appLocWidth = nScreenWidth/2- appWidth/2;

			if (ProjectCompendium.isMac) {
				setSize(appWidth, appHeight);
				setLocation(0,0);
			}
			else {
				setSize(nScreenWidth, nScreenHeight);
				setLocation(appLocWidth, appLocHeight);
			}
		}
		else {
			nScreenWidth = APP_PROPERTIES.getLastScreenWidth();
			nScreenHeight = APP_PROPERTIES.getLastScreenHeight();

			setSize(nScreenWidth, nScreenHeight);

			int nScreenX = APP_PROPERTIES.getLastScreenX();
			int nScreenY = APP_PROPERTIES.getLastScreenY();
			int nMaxScreenX = 0;

			// Make sure entire Frame is on-screen at start-up.  This bit of code solves several problems...
			// 1. Restarting C with display set to a lower resolution than last time
			// 2. Starting C when Frame was mostly slid off-screen last time
			// 3. Starting C with one monitor when frame was on the right-side of a dual-headed display last time
			for (GraphicsDevice gs : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				GraphicsConfiguration[] gc = gs.getConfigurations();
				nMaxScreenX += gc[0].getBounds().width;
			}
			if (nScreenX+nScreenWidth > nMaxScreenX) nScreenX = nMaxScreenX-nScreenWidth;
			setLocation(nScreenX, nScreenY);
		}

		if (!ProjectCompendium.isMac) {
			ImageIcon imageicon = UIImages.get(IUIConstants.PC_ICON);
			if (imageicon != null)
				setIconImage(imageicon.getImage());
		}

		try {
			UIReferenceNodeManager.loadReferenceNodeTypes();
		} catch (Exception e) {
			System.out.println("Exception: "+e.getMessage());
		}

		return true;
	}

	/**
	 * Initialize and draw the main rame contents
	 */
	public boolean init() {

		startUpDlg.setMessage("Opening Compendium...");

		shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		//setWaitCursor();

		initLAF();

		startUpDlg.setMessage("Checking Default Administration Database...");

		if (!connectToServices())
			return false;

	    startUpDlg.setMessage("Adding Extra MySQL profiles...");
        initializeExtraMySqlProfiles();

		startUpDlg.setMessage("Checking files to be deleted...");
		CoreUtilities.checkFilesToDeleted();

		//setDefaultCursor();

		oContentPane = getRootPane().getContentPane();
		oContentPane.setBackground(Color.white);
		oContentPane.setLayout(new BorderLayout());
		oMainPanel = new JPanel(new BorderLayout());
		oInnerPanel = new JPanel(new BorderLayout());

		oContentPane.add(oMainPanel, BorderLayout.CENTER);

		// create audio thread.
		// this must be done before createMenuBar() which uses it
		audioThread = new UIAudio();
		audioThread.setAudio(APP_PROPERTIES.isAudioOn());

		// CREATE BEFORE TOOLBAR MANAGER AS IT NEEDS IT
		oRefreshManager = new UIRefreshManager();

		// CREATE BEFORE MENU MANAGER AS IT NEEDS IT
		startUpDlg.setMessage("Loading Stencils...");

		oStencilManager = new UIStencilManager(this, mainHS, mainHB);
		oStencilManager.loadStencils();
		oStencilManager.getTabbedPane().addKeyListener(this);

		oTabbedPane = new JTabbedPane();

		//oInnerPanel.add(oTabbedPane, BorderLayout.WEST);
		//oInnerPanel.add(oStencilManager.getTabbedPane(), BorderLayout.WEST);

		// CREATE BEFORE MENU MANAGER AS IT NEEDS IT
		startUpDlg.setMessage("Loading Link Groups...");
		oLinkGroupManager = new UILinkGroupManager(this, mainHS, mainHB);
		oLinkGroupManager.loadLinkGroups();

		// create and initialize the menu bar
		startUpDlg.setMessage("Creating Menus...");

		oMenuManager = new UIMenuManager(mainHS, mainHB);
		mbMenuBar = oMenuManager.createMenuBar();
		setJMenuBar(mbMenuBar);

		oMenuManager.onDatabaseClose();

		startUpDlg.setMessage("Creating Toolbars...");

		oToolBarManager = new UIToolBarManager(this, mainHB, false);
		oToolBarManager.createToolbars();
		oToolBarManager.onDatabaseClose();

		onImageRollover(APP_PROPERTIES.isImageRollover());

		// create and initialize the desktop
		createDesktop();

		// create and initialize the status bar
		createStatusBar();

		// create and initialize the view history bar
		createViewHistoryBar();

		//create the clipboard
		createClipboard();

		// install listeners for View keycode capture on menu bar
		mbMenuBar.addKeyListener(this);

		//create a default import dialog for managing import profiles
		oImportProfile = new ImportProfile();

		// NOT SURE THIS IS MAKING ANY DIFFERENCE - POSS' TAKE OUT? - MB
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				oStatusBar.repaint();
				oContentPane.validate();
			}
		});

		updateProjects();

		startUpDlg.setMessage("Checking for auto login...");

		if (oUDigCommunicationManager != null) {
			oUDigCommunicationManager.sendHello();
		}

		// IF A DEFAULT DATABASE HAS BEEN SET, AND YOU ARE CONNECTING LOCALLY
		// TRY AND LOGIN AUTOMATICALLY
		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.DERBY_DATABASE) {
			if (APP_PROPERTIES.getDefaultDatabase() != null
					&& !APP_PROPERTIES.getDefaultDatabase().equals("")
						&& !firstTime) {

				autoFileOpen(APP_PROPERTIES.getDefaultDatabase());
			}
		} else {
			if (oCurrentMySQLConnection != null) {
				oToolBarManager.selectProfile(oCurrentMySQLConnection.getProfile());

				try {
					String sDefaultDatabase = oCurrentMySQLConnection.getName();
					if (oCurrentMySQLConnection.getServer().equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS)
								&& sDefaultDatabase != null
									&& !sDefaultDatabase.equals("")) {

						autoFileOpen(sDefaultDatabase);
					}
					else {
						bOpenFile = true;
					}
				}
				catch(Exception ex) {
					displayError("Could not find the database profile: " + APP_PROPERTIES.getDatabaseProfile() + "due to:\n\n"+ex.getMessage()+"\n\nSwitching to Local Default database");
					setDerbyDatabaseProfile();
				}
			}
			else {
				setDerbyDatabaseProfile();
			}
		}

		return true;
	}

    private void initializeExtraMySqlProfiles() {
        DBAdminProperties properties = new DBAdminPropertiesProvider().get();
        properties.getExtraMsqlConnectionProfiles();
        for (ExternalConnection connection : properties.getExtraMsqlConnectionProfiles()) {
            try {
                if (adminDerbyDatabase.getConnectionByName(
                    connection.getProfile(), connection.getType()) == null ) {
                    adminDerbyDatabase.insertConnection(connection);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
	 * Set the title of the main application for derby database mode.
	 * @param sProject, the name of the project to display.
	 */
	public void setDerbyTitle(String sProject) {
		setTitle(ICoreConstants.DERBY_DATABASE, "Localhost", "Default", sProject);
	}

	/**
	 * Set the title of the main application.
	 * @param nType, the type of data source to set the title for.
	 * @param sAddress, the address of the datasource.
	 * @param sProfile, the name of the database profile to display.
	 * @param sProject, the name of the project to display.
	 */
	public void setTitle(int nType, String sAddress, String sProfile, String sProject) {
		String sTitle = "Compendium";

		if (APP_PROPERTIES.isDisplayFullPath()) {
			if (!sAddress.equals("")) {
				if (nType == ICoreConstants.MYSQL_DATABASE) {
					sTitle += ": MySQL "+ProjectCompendium.sFS+" "+sAddress+" "+ProjectCompendium.sFS+" "+sProfile+" "+ProjectCompendium.sFS+" "+sProject;
				}
				else {
					sTitle += ": Derby "+ProjectCompendium.sFS+" "+sAddress+" "+ProjectCompendium.sFS+" "+sProfile+" "+ProjectCompendium.sFS+" "+sProject;
				}
			}
		}
		else {
			if (nType == ICoreConstants.MYSQL_DATABASE) {
				if (!sProfile.equals("")) {
					sTitle += ": "+sProject+" [ "+sProfile+" ] ";
				}
			}
			else {
				if (!sProject.equals("")) {
					sTitle += ": "+sProject;
				}
			}
		}

		setTitle(sTitle);

		//Lakshmi - set the name of the project in use.
		this.sProject = sProject;
	}


    /**
     * Gets the name of the project in use
     * @return name of the project in use.
     */
	/*
	 * @author Lakshmi
	 * @date 30/1/06
	 */
	public String getProjectName() {
		return sProject;

	}

	/**
	 * Return true, if this is the first time this application has been run, else false.
	 */
	public boolean isFirstTime() {
		return firstTime;
	}

	/**
	 * Return true, if the project login dialog should be opened.
	 */
	public boolean shouldOpenFile() {
		return bOpenFile;
	}

	/**
	 * Initialize the look and feel.
	 */
	public void initLAF() {

		// If nothing set, leave as system default.
		if (APP_PROPERTIES.getCurrentLookAndFeel() == null || APP_PROPERTIES.getCurrentLookAndFeel().equals(""))
		    return;

		try {
			UIManager.setLookAndFeel(APP_PROPERTIES.getCurrentLookAndFeel());

			//added this to specifically set the scroll bar color - the scroll
			// bar was not always apparent - prob due to a swing bug in the way
			// the windows class sets the scroll bar color. - bz - 5/8/00
			if (APP_PROPERTIES.getCurrentLookAndFeel().equals(windowsClassName))
				UIManager.put("ScrollBar.track", new Color(224, 224, 224));

			// IF THERE IS A MENUBAR THEN THIS HAS NOT BEEN CALLED FROM INIT BUT FROM A LAF CHANGE OPTION
			// DO A CONTROLLED UPDATE TO PREVENT NODE DUPLICATION
			if (mbMenuBar != null) {

				SwingUtilities.updateComponentTreeUI(mbMenuBar);
				SwingUtilities.updateComponentTreeUI(oStatusBar);
				SwingUtilities.updateComponentTreeUI(oViewHistoryBar);
				SwingUtilities.updateComponentTreeUI(oSplitter);
				SwingUtilities.updateComponentTreeUI(oTabbedPane);
				SwingUtilities.updateComponentTreeUI(oDesktop);

				oToolBarManager.updateLAF();
				oStencilManager.updateLAF();
				oMenuManager.updateLAF();

				UIViewFrame viewFrame = null;
				JInternalFrame[] frames = oDesktop.getAllFrames();
				for (int i=0; i < frames.length; i++ ) {
					viewFrame = (UIViewFrame)frames[i];
					viewFrame.updateUI();
					viewFrame.getScrollPane().getHorizontalScrollBar().updateUI();
					viewFrame.getScrollPane().getVerticalScrollBar().updateUI();

					if (viewFrame instanceof UIListViewFrame) {
						JTable table = ((UIListViewFrame)viewFrame).getUIList().getList();
						SwingUtilities.updateComponentTreeUI(table);
					}
					viewFrame.repaint();
					viewFrame.validate();
				}
			}
			else {
				SwingUtilities.updateComponentTreeUI(ProjectCompendiumFrame.this);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println ("Could not swap LookAndFeel: " + APP_PROPERTIES.getCurrentLookAndFeel());
		}
	}

	/**
	 * Set up the Service manager for the Derby Database and the current database.
	 * Check the admin database(s) exists and finally load the current database's projects.
	 * return boolean, true if all went successfully, else false;
	 */
	private boolean connectToServices() {

		try {
			oDerbyServiceManager = new ServiceManager(ICoreConstants.DERBY_DATABASE);
			oServiceManager = oDerbyServiceManager;
			adminDerbyDatabase = new DBAdminDerbyDatabase(oDerbyServiceManager);
			adminDatabase = adminDerbyDatabase;
		}
		catch (Exception ex1) {
			System.out.println(ex1.getLocalizedMessage());
			ex1.printStackTrace();
			System.out.flush();
			displayError("Error creating Derby ServiceManager...\n" + ex1.getLocalizedMessage());
			return false;
		}

		// CHECK THAT COMPENDIUM ADMIN DATABASE EXISTS, IF NOT CREATE
		try {
			if (adminDerbyDatabase.firstTime())
				firstTime = true;

			if (adminDerbyDatabase.checkAdminDatabase()) {
				if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.DERBY_DATABASE) {
					adminDerbyDatabase.loadDatabaseProjects();
				}
			}
			else {
				displayError("Unable to establish connection to local Derby Administration database");
				return false;
			}
		}
		catch(Exception ex2) {
			System.out.println(ex2.getLocalizedMessage());
			ex2.printStackTrace();
			System.out.flush();
			displayError("The local Derby Administration database was unable to be opened/created...\n" + ex2.getLocalizedMessage());
			return false;
		}

		// IF THE LAST ACCESSED DATABASE WAS A MYSQL ONE LOAD AND CHECK
		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE && !APP_PROPERTIES.getDatabaseProfile().equals("")) {
			try {
				oCurrentMySQLConnection = adminDerbyDatabase.getConnectionByName(APP_PROPERTIES.getDatabaseProfile(), ICoreConstants.MYSQL_DATABASE);
				if (oCurrentMySQLConnection != null) {

					oServiceManager = new ServiceManager(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
					adminDatabase = new DBAdminDatabase(oServiceManager, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());

					if (adminDatabase.checkAdminDatabase()) {
						adminDatabase.loadDatabaseProjects();
						setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), oCurrentMySQLConnection.getProfile(), "");
					}
					else {
						System.out.println("Unable to establish connection to Administration database");
						//return false;
					}
				}
			}
			catch (Exception ex3) {
				System.out.println(ex3.getLocalizedMessage());
				ex3.printStackTrace();
				System.out.flush();

				if (oCurrentMySQLConnection != null) {
					displayError("Error Connecting to MySQL database \""+oCurrentMySQLConnection.getProfile()+"\".\n"+
							"Check your internet connection and connectivity to your MySQL server.\n"+
							"Then restart Compendium, and reselect \"MySQL: "+oCurrentMySQLConnection.getProfile()+
							"\" from the Data Connection drop-down in the bottom toolbar.\n");
//							ex3.getLocalizedMessage());
					APP_PROPERTIES.setDatabaseType(ICoreConstants.DERBY_DATABASE);
					oServiceManager = oDerbyServiceManager;
					adminDatabase = adminDerbyDatabase;
				}
				else {
					displayError("Error Loading profile details for MySQL database: "+APP_PROPERTIES.getDatabaseProfile()+"\n"+ex3.getLocalizedMessage());
					APP_PROPERTIES.setDatabaseType(ICoreConstants.DERBY_DATABASE);
					oServiceManager = oDerbyServiceManager;
					adminDatabase = adminDerbyDatabase;
				}
			}
		}

		// Get the Compendium Access databases list, if the appropriate ini file exists.
		// MB: 7th April 2005 - NOT USED ANYMORE. LEFT FOR A WHILE IN CASE NEED TO RETURN CODE.
		/*try {
			if (oServiceManager.getDatabaseManager().hasAccessDatabases())
				sAccessProjects = oServiceManager.getDatabaseManager().getAccessProjects();
		}
		catch(Exception ex4) {
			ex4.printStackTrace();
			System.out.flush();
			ProjectCompendium.APP.displayError("Error: Loading Access database list.\n\n" + ex4.getMessage());
			return false;
		}*/

		// DO WE HAVE ANY MYSQL CONNECTIONS SET UP?
		try {
			Vector connections = adminDerbyDatabase.getMySQLConnections();
			if (connections.size() == 0) {

				// IS THERE A PROPERTIES FILE WE CAN USE TO SET ONE UP FOR THE USER?
				String file_name = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"MySQL.properties";
				File file = new File(file_name);
				if (file.exists()) {
					try {
						Properties mysqlProperties = new Properties();
						mysqlProperties.load(new FileInputStream(file_name));
						String url = (String)mysqlProperties.get("url");
						if (url.equals("")) {
							url = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;
						}
						String username = (String)mysqlProperties.get("username");
						if (username.equals("")) {
							username = ICoreConstants.sDEFAULT_DATABASE_USER;
						}

						String password = (String)mysqlProperties.get("password");

						ExternalConnection connection = new ExternalConnection();
						connection.setProfile("Default");
						connection.setServer(url);
						connection.setPassword(password);
						connection.setLogin(username);
						connection.setType(ICoreConstants.MYSQL_DATABASE);

						adminDerbyDatabase.insertConnection(connection);

						//CoreUtilities.deleteFile(file);

					}
					catch (Exception ex) {
						System.out.println("Exception (ProjectCompendiumFrame.connectToServices - existing)\n\n"+ex.getMessage());
					}
				}
				else {
					// CAN WE TEST FOR A LOCALHOST/ROOT/NULL POTENTIAL CONNECTION AND COMPENDIUM DATABASE ON THAT APPLICATION
					try {
						ServiceManager oManager = new ServiceManager(ICoreConstants.MYSQL_DATABASE, "root", "", "localhost");
						DBAdminDatabase oAdminDatabase = new DBAdminDatabase(oManager, "root", "", "localhost");

						if (oAdminDatabase.checkForAdminDatabase()) {
							ExternalConnection connection = new ExternalConnection();
							connection.setProfile("Default");
							connection.setServer("localhost");
							connection.setPassword("");
							connection.setLogin("root");
							connection.setType(ICoreConstants.MYSQL_DATABASE);
							adminDerbyDatabase.insertConnection(connection);
						}
					}
					catch(SQLException ex) {
						System.out.println(ex.getLocalizedMessage());
						System.out.println("Exception (ProjectCompendiumFrame.connectToServices - localhost).");
						ex.printStackTrace();
						System.out.flush();
//						displayError("Exception (ProjectCompendiumFrame.connectToServices - localhost):\n"+ex.getLocalizedMessage());
					}
				}
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			System.out.println("Exception (ProjectCompendiumFrame.connectToServices - main).");
			ex.printStackTrace();
			System.out.flush();
			displayError("Exception (ProjectCompendiumFrame.connectToServices - main):\n"+ex.getLocalizedMessage());
		}

		return true;
	}

	/**
	 * Set the cursor on the given frame to the wait cursor.
	 * @param frame, the frame to set the wait cursor on.
	 */
	public void setWaitCursor(UIViewFrame frame) {
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIViewPane uiview = ((UIMapViewFrame)frame).getViewPane();
				if (uiview != null) {
					uiview.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
				}
			}
			else if (frame instanceof UIListViewFrame) {
				UIList uilist = ((UIListViewFrame)frame).getUIList();
				if (uilist != null) {
					uilist.getList().setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
				}
			}
		}
	}

	/**
	 * Set the cursor on the given frame to the default cursor.
	 * @param frame the frame to set the default cursor on.
	 */
	public void setDefaultCursor(UIViewFrame frame) {
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIViewPane uiview = ((UIMapViewFrame)frame).getViewPane();
				if (uiview != null) {
					uiview.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
				}
			}
			else if (frame instanceof UIListViewFrame) {
				UIList uilist = ((UIListViewFrame)frame).getUIList();
				if (uilist != null) {
					uilist.getList().setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
				}
			}
		}
	}

	/**
	 * Set the frame cursor to the wait cursor.
	 */
	public void setWaitCursor() {
		super.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
	}

	/**
	 * Set the frame cursor to the default cursor.
	 */
	public void setDefaultCursor() {
		super.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Set the frame cursor to the given cursor.
	 * @param c, the cursor to set the frame cursor to.
	 */
	public void setCursor(Cursor c) {
		super.setCursor(c);
	}

	/**
	 * Switches the type of the database being used to the given MySQL profile.
	 * If required, build a new service and load projects.
	 * @param ExternalConnection connection, the MySQL connection profile to use.
	 */
	public boolean setMySQLDatabaseProfile(ExternalConnection connection) {

		setWaitCursor();

		if (oModel != null) {
			onFileClose();
		}

		oCurrentMySQLConnection = connection;
		int nType = ICoreConstants.MYSQL_DATABASE;
		String sServer = connection.getServer();
		String sUserName = connection.getLogin();
		String sPassword = connection.getPassword();
		String sProfileName = connection.getProfile();
		String sDefaultDatabase = connection.getName();

	  	try {
			oServiceManager = new ServiceManager(nType, sUserName, sPassword, sServer);
	  	}
		catch (Exception e) {
			displayError("Exception: creating ServiceManager (ProjectCompendiumFrame.setDatabaseProfile)\n\n"+e.getMessage());
			setDefaultCursor();
			return false;
	  	}
		adminDatabase = new DBAdminDatabase(oServiceManager, sUserName, sPassword, sServer);
		try {
			if (adminDatabase.checkAdminDatabase()) {
				adminDatabase.loadDatabaseProjects();
				updateProjects();
			}
			else {
				System.out.println("Unable to establish connection to Administration database");
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			ex.printStackTrace();
			System.out.flush();
			displayError("Error Connecting to MySQL database "+oCurrentMySQLConnection.getProfile()+"\n"+ex.getLocalizedMessage());
			setDefaultCursor();
			return false;
		}

		APP_PROPERTIES.setDatabaseType(nType);
		APP_PROPERTIES.setDatabaseProfile(sProfileName);

		setTitle(ICoreConstants.MYSQL_DATABASE, sServer, sProfileName, "");

		oMenuManager.enableConvertMenuOptions();

		// IF A DEFAULT DATABASE HAS BEEN SET, TRY AND LOGIN AUTOMATICALLY
		if (sServer.equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS) && sDefaultDatabase != null
					&& !sDefaultDatabase.equals("")) {

			autoFileOpen(sDefaultDatabase);
		}
		else if (vtProjects == null || vtProjects.size() == 0) {
			onFileNew();
		}
		else {
			onFileOpen();
		}

		setDefaultCursor();

		return true;
	}

	/**
	 * Switch the type of the database being used to the default local Derby database.
	 * If required, build a new service and load projects.
	 */
	public boolean setDerbyDatabaseProfile() {

		setWaitCursor();

		if (oModel != null) {
			onFileClose();
		}

		APP_PROPERTIES.setDatabaseType(ICoreConstants.DERBY_DATABASE);
		oServiceManager = oDerbyServiceManager;
		adminDatabase = adminDerbyDatabase;
		adminDatabase.loadDatabaseProjects();
		updateProjects();

		setDerbyTitle("");

		oMenuManager.enableConvertMenuOptions();
		oToolBarManager.selectProfile("");

		// IF A DEFAULT DATABASE HAS BEEN SET, TRY AND LOGIN AUTOMATICALLY
		if (APP_PROPERTIES.getDefaultDatabase() != null
					&& !APP_PROPERTIES.getDefaultDatabase().equals("")) {
			autoFileOpen(APP_PROPERTIES.getDefaultDatabase());
		}
		else if (vtProjects == null || vtProjects.size() == 0) {
			onFileNew();
		}
		else {
			onFileOpen();
		}

		setDefaultCursor();

		return true;
	}


	/**
	 * Return the current active link group.
	 * @return String, the id of the current active link group.
	 */
	public String getActiveLinkGroup() {
		return activeLinkGroup;
	}

	/**
	 * Set the current active link group.
	 * @param sLinkGroupID, the id of the link group to make the active group.
	 */
	public boolean setActiveLinkGroup(String sLinkGroupID) {

		if (! (activeLinkGroup).equals(sLinkGroupID) ) {
			try {
				activeLinkGroup = sLinkGroupID;
				((SystemService)oModel.getSystemService()).setLinkGroup(oModel.getSession(), activeLinkGroup);
				return true;
			}
			catch(Exception ex) {
				displayError("Unable to update active link group");
			}
		}
		return false;
	}

	/**
	 * Return the current active code group.
	 * @return String, the id of the current active code group.
	 */
	public String getActiveCodeGroup() {
		return activeGroup;
	}

	/**
	 * Set the current active code group.
	 * @param sCodeGroupID, the id of the code group to make the active group.
	 */
	public boolean setActiveCodeGroup(String sCodeGroupID) {

		if (! (activeGroup).equals(sCodeGroupID) ) {
			try {
				activeGroup = sCodeGroupID;
				((SystemService)oModel.getSystemService()).setCodeGroup(oModel.getSession(), activeGroup);
				APP_PROPERTIES.setActiveCodeGroup(activeGroup);
				oToolBarManager.updateCodeChoiceBoxData();
				return true;
			}
			catch(Exception ex) {
				displayError("Unable to update active code group");
			}
		}
		return false;
	}

	/**
	 * Creates and initializes the desktop.
	 */
	protected void createDesktop() {

		oDesktop = new JDesktopPane();
		oDesktop.setPreferredSize(new Dimension(nScreenWidth-100,nScreenHeight-100));
		oDesktop.setDesktopManager(new UIDesktopManager());

		JScrollPane scrollpane = new JScrollPane(oDesktop);
		scrollpane.setBounds(0,0,nScreenWidth-100,nScreenHeight-100);

		//(scrollpane.getVerticalScrollBar()).setUnitIncrement(100);
		//(scrollpane.getHorizontalScrollBar()).setUnitIncrement(100);

		oSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, oTabbedPane, scrollpane);
		oSplitter.setOneTouchExpandable(true);
		oSplitter.setDividerSize(10);
		oSplitter.setContinuousLayout(true);
		oInnerPanel.add(oSplitter, BorderLayout.CENTER);

		//splitpane
		oMainPanel.add(oInnerPanel, BorderLayout.CENTER);
	}

	/**
	 * Creates and initializes the view history bar.
	 */
	protected void createViewHistoryBar() {

		oViewHistoryBar = new UIViewHistoryBar();
		oInnerPanel.add(oViewHistoryBar, BorderLayout.NORTH);
		displayViewHistoryBar(APP_PROPERTIES.isDisplayViewHistoryBar());
	}

	/**
	 * Creates and initializes the status bar.
	 */
	protected void createStatusBar() {

		oStatusBar = new UIStatusBar(" ");
		oStatusBar.setMinimumSize(new Dimension(0, 14));
		oContentPane.add(oStatusBar, BorderLayout.SOUTH);

		displayStatusBar(APP_PROPERTIES.isDisplayStatusBar());
	}

	/**
	 * hide/show the outline view.
	 * @author Lakshmi
	 * @date 2/3/06
	 */
	protected void createOutlineView() {
		String sDisplay = APP_PROPERTIES.getDisplayOutlineView();
		oMenuManager.addOutlineView(sDisplay, false);
	}

	/**
	 * hide/show the unread view.
	 * @author Lakshmi
	 * @throws SQLException
	 * @date 6/27/06
	 */
	protected void createUnreadView() throws SQLException {
		boolean sDisplay = APP_PROPERTIES.isDisplayUnreadView();
		if (sDisplay) {
			oMenuManager.addUnreadView(false);
		}
	}

	/**
	 * hide/show the tags view.
	 */
	protected void createTagsView() {
		boolean sDisplay = APP_PROPERTIES.isDisplayTagsView();
		if (sDisplay) {
			oMenuManager.addTagsView(false);
		}
	}

	/**
	 * hide/show the view history bar.
	 */
	public void displayViewHistoryBar(boolean bDisplay) {

		if (bDisplay) {
			oViewHistoryBar.setVisible(true);
		}
		else {
			oViewHistoryBar.setVisible(false);
		}
	}

	/**
	 * hide/show the status bar.
	 */
	public void displayStatusBar(boolean bDisplay) {

		if (bDisplay) {
			oStatusBar.setVisible(true);
		}
		else {
			oStatusBar.setVisible(false);
		}
	}

	/**
	 * Attempt to automatically login the default database with its default user.
	 */
	protected boolean processDefaultLogin(String sDatabase) {

		boolean bDefaultLoginSucessful = false;

		sFriendlyName = sDatabase;
		String sModel = adminDatabase.getDatabaseName(sDatabase);
		if (sModel == null) {
			displayError("Could not find Default Project "+sDatabase);
			return bDefaultLoginSucessful;
		}
		else {
			try {
				// CHECK IF DATABASE UP TO DATE
				try {
					startUpDlg.setMessage("checking database schema...");
					System.out.println("1504 ProjectCompendiumFrame sModel is " + sModel);
					int status = adminDatabase.getSchemaStatusForDatabase(sModel);
					if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
						if (!UIDatabaseUpdate.updateDatabase(adminDatabase, this, sModel)) {
							setDefaultCursor();
							return false;
						}
					}
					else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
						displayError("The default project "+sFriendlyName+" requires a newer version of Compendium");
						setDefaultCursor();
						return false;
					}
					startUpDlg.setMessage("checks complete...");
				}
				catch(Exception ie) {
					setDefaultCursor();
					return false;
				}

				DBDatabaseManager databaseManager = oServiceManager.getDatabaseManager();
				databaseManager.openProject(sModel);
		       	DBConnection dbcon = databaseManager.requestConnection(sModel);

				UserProfile oUser = DBSystem.getDefaultUser(dbcon);

				if (oUser != null) {
					sUserName = oUser.getLoginName();
					sUserPassword = oUser.getPassword();
					bDefaultLoginSucessful = validateUser(sModel, sUserName, sUserPassword);
					if (bDefaultLoginSucessful) {
						if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
							setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), APP_PROPERTIES.getDatabaseProfile(), sFriendlyName);
						}
						else {
							setDerbyTitle(sFriendlyName);
						}
					}
				}
				else {
					System.out.println("In processDefaultLogin: User is null");
				}

				databaseManager.releaseConnection(sModel, dbcon);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				return bDefaultLoginSucessful;
			}
		}

		return bDefaultLoginSucessful;
	}

	/**
	 * Open the logon dialog and process the results.
	 */
	protected boolean createLogonScreen() {

		if (oLogonDialog != null && oLogonDialog.isVisible()) {
			return false;
		}

		// CHECK IF ANY DATABASE SCHEMAS NEEDS UPDATING
		//Hashtable htProjectStatus = adminDatabase.getProjectSchemaStatus();

		//Hashtable htProjectStatus = new Hashtable();

		String sDatabaseServer = "";
		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE && oCurrentMySQLConnection != null)
			sDatabaseServer = oCurrentMySQLConnection.getServer();

		//oLogonDialog = new UILogonDialog(this, vtProjects, htProjectStatus, sUserName, sUserPassword, sFriendlyName, sDatabaseServer);
		oLogonDialog = new UILogonDialog(this, vtProjects, sUserName, sUserPassword, sFriendlyName, sDatabaseServer);
		oLogonDialog.setModal(true);
		oLogonDialog.setVisible(true);

		oLogonDialog.getFocusOwner();

		if(oLogonDialog.isLogout()) {
			if (oDesktop != null) {
				onFileClose();
			}
			else
				onExit();
			return false;
		}

		// get login values
		String sName = oLogonDialog.getModel();
		sFriendlyName = sName;
		String sModel = adminDatabase.getDatabaseName(sName);
		if (sModel == null) {
			displayError("Could not find Database "+sName);
			onFileClose();
			return false;
		}

		sUserName = oLogonDialog.getUserName();
		sUserPassword = oLogonDialog.getUserPassword();

		setWaitCursor();

		if(bProceed) {

			// CHECK IF DATABASE UP TO DATE
			try {
				int status = adminDatabase.getSchemaStatusForDatabase(sModel);
				if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
					if (!UIDatabaseUpdate.updateDatabase(adminDatabase, this, sModel)) {
						setDefaultCursor();
						return false;
					}
				}
				else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
					displayError("The project "+sFriendlyName+" requires a newer version of Compendium to run");
					setDefaultCursor();
					return false;
				}
			}
			catch(Exception ie) {
				setDefaultCursor();
				return false;
			}

			if(!validateUser(sModel, sUserName, sUserPassword)) {
				//popup the error message
	            JOptionPane oOptionPane = new JOptionPane("Please enter a valid User ID and Password.");
				JDialog oDialog = oOptionPane.createDialog(oContentPane,"Login Error");
				oDialog.setModal(true);
				oDialog.setVisible(true);

				//invoke the logon dialog again..
				createLogonScreen();
			}

			if (!bProceed)
				return false;
		}
		else {
			setDefaultCursor();
			return false;
		}

		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), APP_PROPERTIES.getDatabaseProfile(), sFriendlyName);
		}
		else {
			setDerbyTitle(sFriendlyName);
		}

		setDefaultCursor();
		return true;
	}

	/**
	 * Set the logon process to proceed after a successfull check if true.
	 * @return proceed, whether to proceed with the current logong attempt.
	 */
	public synchronized void proceed(boolean proceed) {
		bProceed = proceed;
	}

	/**
	 * Get the logon proceed status.
	 * @return boolean, true to proceed with the current logong attempt, else false.
	 */
	public boolean isProceed() {
		return bProceed;
	}

	/**
	 * Validate the given user details against the database and return is valid.
	 * @param model, the database name of the database to validate against.
	 * @param user, the user name to validate.
	 * @param password, the password to validate.
	 * @return boolean, whether the login was valid or not.
	 */
	protected boolean validateUser(String model, String user, String password)
	{
		System.out.println("1693 ProjectCompendiumFrame entered validate user, model user pass are " +
			model + " " + user + " " + password);

		try
		{
			System.out.println("1698 ProjectCompendiumFrame going to oServiceManager.registerUser");
			oModel = oServiceManager.registerUser(model, user, password);
			System.out.println("1700 ProjectCompendiumFrame oModel returned is " + oModel);
		}
		catch(SQLException ex)
		{
			System.out.println("Exception: (ProjectCompendiumFrame.validateUser) \n\n"+ex.getMessage());
		}

		if (oModel == null )
		{
			System.out.println("1704 ProjectCompendiumFrame oModel is null");
			return false;
		}
		//else
			//sCurrentDatabase = model;

		try {
			oModel.initialize();
		} catch(SQLException ex) {
			System.out.println("Exception: (ProjectCompendiumFrame.validateUser) \n\n"+ex.getMessage());
			//return false;
		} catch (java.net.UnknownHostException uhe) {
			System.out.println("Exception: (ProjectCompendiumFrame.validateUser) \n\n"+uhe.getMessage());
			return false;
		}

		// Store default font.
		labelFont = ((Model)oModel).labelFont;

		if(oModel != null)
			return true;
		else
			return false;

	}

	/**
	 * Set the given text in the status bar.
	 * @param text, the text to set in the status bar.
	 */
	public void setStatus(String text) {
		oStatusBar.setStatus(text);
	}

	/**
	 * Gets the current text from the status bar.
	 */
	public String getStatus() {
		return 	oStatusBar.getStatus();
	}

	/**
	 * Set the given history in the view history bar.
	 * @param vtHistory, the list of view history.
	 */
	public void setViewHistory(Vector vtHistory) {
		oViewHistoryBar.setViewHistory(vtHistory);
	}

	/**
	 * Get the status bar.
	 */
	public UIStatusBar getStatusBar() {
		return oStatusBar;
	}

// ***** Event Handlers ***** //

	/**
	 * Invoked when a key is pressed.
	 * @param e, the associated KeyEvent.
	 */
	public void keyPressed(KeyEvent evt) {

		char [] key = {evt.getKeyChar()};
		String sKeyPressed = new String(key);
		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		UIViewFrame viewFrame = getCurrentFrame();

		// IF WINDOW NOT SELECTED, KEY EVENT GOES SKEWY
		if (!viewFrame.isSelected()) {
			if (viewFrame instanceof UIMapViewFrame)
				((UIMapViewFrame)viewFrame).setSelected(true);
			else
				((UIListViewFrame)viewFrame).setSelected(true);
		}

		ViewPaneUI viewui = null;
		UIList uilist = null;
		ListUI listui = null;
		UIViewPane uiview = null;

		if (viewFrame instanceof UIMapViewFrame) {
			uiview = ((UIMapViewFrame)viewFrame).getViewPane();
			if (uiview != null)
				viewui = uiview.getUI();
		}
		else {
			uilist = ((UIListViewFrame)viewFrame).getUIList();
			if (uilist != null)
				listui = uilist.getListUI();
		}

		setWaitCursor();
		setWaitCursor(viewFrame);

		if (modifiers == java.awt.Event.ALT_MASK) {
			switch(keyCode) {
				case KeyEvent.VK_0: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 0);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_1: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 1);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_2: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 2);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_3: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 3);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_4: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 4);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_5: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 5);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_6: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 6);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_7: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 7);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_8: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 8);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_9: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 9);
						evt.consume();
					}
					break;
				}
			}
		}
		if (modifiers == shortcutKey) {
			switch(keyCode) {
				case KeyEvent.VK_F: { // OPEN SEARCH
					onSearch();
					evt.consume();
					break;
				}
				case KeyEvent.VK_O: { // OPEN PROJECT DIALOG
					onFileOpen();
					evt.consume();
					break;
				}
				case KeyEvent.VK_N: { // NEW PROJECT DIALOG
					onFileNew();
					evt.consume();
					break;
				}
				case KeyEvent.VK_X: { // CUT
					if (viewui != null)
						viewui.cutToClipboard(null);
					else
						listui.cutToClipboard();
					evt.consume();
					break;
				}
				case KeyEvent.VK_C: { // COPY
					if (viewui != null)
						viewui.copyToClipboard(null);
					else
						listui.copyToClipboard();
					evt.consume();
					break;
				}
				case KeyEvent.VK_V: { // PASTE
					if (viewui != null)
						viewui.pasteFromClipboard();
					else
						listui.pasteFromClipboard();
					evt.consume();
					break;
				}
				case KeyEvent.VK_A: { // SELECT ALL
					if (viewui != null)
						viewui.onSelectAll();
					else
						listui.onSelectAll();
					evt.consume();
					break;
				}
				case KeyEvent.VK_Z: { // UNDO
					onEditUndo();
					evt.consume();
					break;
				}
				case KeyEvent.VK_Y: { // REDO
					onEditRedo();
					evt.consume();
					break;
				}
				case KeyEvent.VK_W: { // CLOSE WINDOW
					try {
						if (viewui != null) {
							if (uiview.getView() != getHomeView()) {
								viewFrame.setClosed(true);
							}
						}
						else
							viewFrame.setClosed(true);
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}
			}
		}
		else if (modifiers == java.awt.Event.CTRL_MASK) {
			switch(keyCode) {
				case KeyEvent.VK_RIGHT: { // ARRANGE
					onViewArrange(IUIArrange.LEFTRIGHT);
					evt.consume();
					break;
				}
				case KeyEvent.VK_DOWN: { // ARRANGE
					onViewArrange(IUIArrange.TOPDOWN);
					evt.consume();
					break;
				}
				case KeyEvent.VK_R: { // ARRANGE
					onViewArrange(IUIArrange.LEFTRIGHT);
					evt.consume();
					break;
				}
				case KeyEvent.VK_T: { // OPEN TAG WINDOW
					onCodes();
					evt.consume();
					break;
				}
				case KeyEvent.VK_B: { // BOLD / UNBOLD THE TEXT OF ALL SELECTED NODES IN THE CURRENT MAP
					getToolBarManager().addFontStyle(Font.BOLD);
					evt.consume();
					break;
				}
				case KeyEvent.VK_I: { // ITALIC / UNITALIC THE TEXT OF ALL SELECTED NODES IN THE CURRENT MAP
					getToolBarManager().addFontStyle(Font.ITALIC);
					evt.consume();
					break;
				}
				case KeyEvent.VK_ENTER: { // CLOSE WINDOW
					try {
						if (viewui != null) {
							if (uiview.getView() != getHomeView() )
								viewFrame.setClosed(true);
						}
						else
							viewFrame.setClosed(true);
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}
			}
		}
		else if ((keyCode == KeyEvent.VK_DELETE && modifiers == 0)
				|| (keyCode == KeyEvent.VK_BACK_SPACE && modifiers == 0)) {

			if (viewui != null)
				viewui.onDelete();
			else
				listui.onDelete();

			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_PAGE_UP && modifiers == 0) {
			Point oldPoint = viewFrame.getViewPosition();
			int cCurrentHeight = viewFrame.getHeight();
			int newX = oldPoint.x;
			int newY = oldPoint.y - (cCurrentHeight-100);
			if(newY < 0)
				newY = 0;
			viewFrame.setViewPosition(new Point(newX,newY));
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_PAGE_DOWN && modifiers == 0) {
			Point oldPoint = viewFrame.getViewPosition();
			int cCurrentHeight = viewFrame.getHeight();
			viewFrame.setViewPosition(new Point(oldPoint.x, oldPoint.y + (cCurrentHeight-100)));
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F2 && modifiers == 0) {
			zoomNext();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F3 && modifiers == 0) {
			zoomFit();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F4 && modifiers == 0) {
			zoomFocused();
			evt.consume();
		}
		setDefaultCursor(viewFrame);
		setDefaultCursor();
  	}

	/**
	 * Create a node for the given shortcut number from the current stencil.
	 * @param uiview, the UIViewPane to create the node in.
	 * @param nShortcut, the shortcut key to create the stencil node for.
	 */
	public void createNodeFromStencil(UIViewPane uiview, int nShortcut) {
		DraggableStencilIcon oIcon = oStencilManager.getItemForShortcut(nShortcut);
		if (oIcon != null) {
			Point p = getKeyPress(uiview);
			uiview.createNodeFromStencil(oIcon, p.x, p.y);
		}
	}

	/**
	 * Take the given keypress coordinates and check and adjust for node creation.
	 * @param nX, the x position of the key press.
	 * @param nY, the y position of the key press.
	 * @param uiview, the map the key was pressed in.
	 * @return Point, the adjusted coordinates.
	 */
	private Point getKeyPress(UIViewPane uiview) {

		Point p = new Point(_x, _y);
		SwingUtilities.convertPointFromScreen(p, uiview);
		int nX = p.x;
		int nY = p.y;

		// MOVE NEW NODE OUT A BIT SO MOUSEPOINTER NOT RIGHT ON EDGE
		if (nX >= 20 && nY >= 20) {
			nX -= 20;
			nY -= 20;
		}
		return new Point(nX, nY);
	}

	/**
	 * Invoked when a key is released.
	 * @param e, the associated KeyEvent.
	 */
	public void keyReleased(KeyEvent e) {
		e.consume();
	}

	/**
	 * Invoked when a key is typed.
	 * @param e, the associated KeyEvent.
	 */
  	public void keyTyped(KeyEvent e) {

		if (!e.isAltDown() && !e.isControlDown() && !e.isMetaDown()) {

			UIViewFrame viewFrame = getCurrentFrame();

			// IF WINDOW NOT SELECTED, KEY EVENT GOES SKEWY
			if (!viewFrame.isSelected()) {
				if (viewFrame instanceof UIMapViewFrame)
					((UIMapViewFrame)viewFrame).setSelected(true);
				else
					((UIListViewFrame)viewFrame).setSelected(true);
			}

			ViewPaneUI viewui = null;
			UIList uilist = null;
			ListUI listui = null;
			UIViewPane uiview = null;

			if (viewFrame instanceof UIMapViewFrame) {
				uiview = ((UIMapViewFrame)viewFrame).getViewPane();
				if (uiview != null)
					viewui = uiview.getUI();
			}
			else {
				uilist = ((UIListViewFrame)viewFrame).getUIList();
				if (uilist != null)
					listui = uilist.getListUI();
			}

			char keyChar = e.getKeyChar();
			char[] key = {keyChar};
			String sKeyPressed = new String(key);


			int nType = -1;

			//if(sKeyPressed.equals("i") || sKeyPressed.equals("I") || sKeyPressed.equals("q") || sKeyPressed.equals("Q") || sKeyPressed.equals("?") || sKeyPressed.equals("/")) {
			//	nType = ICoreConstants.ISSUE;
			//}
			//if(sKeyPressed.equals("p") || sKeyPressed.equals("P") || sKeyPressed.equals("a") || sKeyPressed.equals("A") || sKeyPressed.equals("!") || sKeyPressed.equals("1")) {
			//	nType = ICoreConstants.POSITION;
			//}

			if(sKeyPressed.equals("p") || sKeyPressed.equals("P")) {
				nType = ICoreConstants.POSITION;
			}
			else if (sKeyPressed.equals("q") || sKeyPressed.equals("Q") || sKeyPressed.equals("?") || sKeyPressed.equals("/")) {
				nType = ICoreConstants.ISSUE;
			}
			else if (sKeyPressed.equals("i") || sKeyPressed.equals("I") || sKeyPressed.equals("a") || sKeyPressed.equals("A") || sKeyPressed.equals("!") || sKeyPressed.equals("1")) {
				nType = ICoreConstants.POSITION;
			}
			else if(sKeyPressed.equals("u") || sKeyPressed.equals("U") ) {
				nType = ICoreConstants.ARGUMENT;
			}
			else if(sKeyPressed.equals("r") || sKeyPressed.equals("R") ) {
				nType = ICoreConstants.REFERENCE;
			}
			else if(sKeyPressed.equals("d") || sKeyPressed.equals("D") ) {
				nType = ICoreConstants.DECISION;
			}
			else if(sKeyPressed.equals("n") || sKeyPressed.equals("N")) {
				nType = ICoreConstants.NOTE;
			}
			else if(sKeyPressed.equals("m") || sKeyPressed.equals("M")) {
				nType = ICoreConstants.MAPVIEW;
			}
			else if(sKeyPressed.equals("l") || sKeyPressed.equals("L")) {
				nType = ICoreConstants.LISTVIEW;
			}
			else if(sKeyPressed.equals("+") || sKeyPressed.equals("=")) {
				nType = ICoreConstants.PRO;
			}
			else if(sKeyPressed.equals("-")) {
				nType = ICoreConstants.CON;
			}

			if (viewui != null) {
				Point p = getKeyPress(uiview);
				int nX = p.x;
				int nY = p.y;
				if (nX >= 20 && nY >= 10) {
					nX -= 20;
					nY -= 10;
				}
				viewui.addNewNode(nType, nX, nY);
			} else {
				if (!uilist.getList().isEditing()) {
					listui.createNode( nType, "",
						ProjectCompendium.APP.getModel().getUserProfile().getUserName(), "",
						"", listui.ptLocationKeyPress.x, (uilist.getNumberOfNodes() + 1) * 10
						);
					uilist.updateTable();
				}
			}
		}
  		e.consume();
  	}


	/**
	 * refresh the Stencil Menu
	 * @see com.compendium.ui.UIMenuManager#createStencilMenu
	 */
	public void refreshStencilMenu() {
		oMenuManager.createStencilMenu();
	}

	/**
	 * Reset the toolobar zoom settings.
	 * @see com.compendium.ui.UIToolBarManager#resetZoom
	 */
	public void resetZoom() {
		oToolBarManager.resetZoom();
	}

	/**
	 * Zoom to the next level.
	 * @see com.compendium.ui.UIMenuManager#onZoomNext
	 */
	public void zoomNext() {
		oMenuManager.onZoomNext();
		resetZoom();
	}

	/**
	 * Zoom to current view to fit the screen.
	 * @see com.compendium.ui.UIMenuManager#onZoomToFit
	 */
	public void zoomFit() {
		oMenuManager.onZoomToFit();
		resetZoom();
	}

	/**
	 * Zoom to 100% and focus the selected node.
	 * @see com.compendium.ui.UIMenuManager#onZoomRefocused
	 */
	public void zoomFocused() {
		oMenuManager.onZoomRefocused();
		resetZoom();
	}

	/**
	 * Update the projects list from the Administration Database.
	 */
	public void updateProjects() {

		System.out.println("2232 ProjectCompendiumFrame entered updateProjects");
		vtProjects = adminDatabase.getDatabaseProjects();
		if (vtProjects == null || vtProjects.size() == 0) {
			oMenuManager.setFileOpenEnablement(false);
			oToolBarManager.setFileOpenEnablement(false);
		}
		else {
			oMenuManager.setFileOpenEnablement(true);
			oToolBarManager.setFileOpenEnablement(true);
		}
		System.out.println("2242 ProjectCompendiumFrame exiting updateProjects");

	}

	/**
	 * Return the string representation of the current database projects list.
	 * @return String, a comma separated string of the current database projects.
	 */
	public Vector getProjects() {
		return vtProjects;
	}

	/**
	 * Update the default user in the current database Database.
	 * Set the default user for the current database.
	 */
	public boolean setDefaultUser(String sUserID) {

		try {
			((SystemService)oModel.getSystemService()).setDefaultUser(oModel.getSession(), sUserID);
			return true;
		}
		catch(Exception ex) {
			displayError("Unable to update active default user");
		}
		return false;
	}

	/**
	 * Is the current database the default database.
	 * @return boolean, true if the current database the default database, else false.
	 */
	public boolean isDefaultDatabase() {
		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			if ( sFriendlyName.equals(oCurrentMySQLConnection.getName()) )
				return true;
		}
		else {
			if ( sFriendlyName.equals(APP_PROPERTIES.getDefaultDatabase()) )
				return true;
		}
		return false;
	}

	/**
	 * Return the default database value.
	 * @return String, the name of the default database.
	 */
	public String getDefaultDatabase() {
		//November 2011 - i don't think this is returning anything - matt
		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE)
			return oCurrentMySQLConnection.getName();
		else {
			return APP_PROPERTIES.getDefaultDatabase();
		}
	}

	/**
	 * Set the default database value locally and in the format properties file.
	 * @param database, the name of the default database.
	 */
	public void setDefaultDatabase(String database) {

		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.DERBY_DATABASE) {
			APP_PROPERTIES.setDefaultDatabase(database);
		}
		else {
			try {
				adminDerbyDatabase.setDefaultDatabase(database, APP_PROPERTIES.getDatabaseProfile(), ICoreConstants.MYSQL_DATABASE);
				if (oCurrentMySQLConnection != null)
					oCurrentMySQLConnection.setName(database);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				displayError("The Default database could not be set due to: \n\n"+ex.getMessage());
			}
		}
	}

//************* FILE MENU ****************//

	/**
	 * Lets the user open a database project.
	 * Note: The code in this method is run in an inner thread.
	 * @param String sDatabaseName, the default database to open.
	 */
	public void autoFileOpen(String sDatabaseName) {
		final String sDatabase = sDatabaseName;

		// THIS THREAD IS REQUIRED FOR PROGRESS DIALOG CALLED IN processDefaultLogin
		Thread thread = new Thread("ProjectCompendiumFrame.autoFileOpen") {

			public void run()  {

				// create the log on screen
				sUserName = "";
				sUserPassword = "";

				setWaitCursor();

				//System.out.println("About to try and process default login");
				if (!processDefaultLogin(sDatabase))
					return;

					initializeForProject();

				if (oUDigCommunicationManager != null) {
					oUDigCommunicationManager.openProject();
				}

				setDefaultCursor();
			}
		};
		thread.start();
	}

	/**
	 * Open a compendium database project, if you do not have a currently open project.
	 * Note: The contents of this method are run in an inner thread.
	 */
	public void onFileOpen() {

		if (isProjectOpen("Open Project"))
			return;

		Thread thread = new Thread("ProjectCompendiumFrame.onFileOpen") {
			public void run() {

				// create the log on screen
				sUserName = "";
				sUserPassword = "";

				if (createLogonScreen() == false) {
					setDefaultCursor();
					return;
				}

				initializeForProject();
				if (oUDigCommunicationManager != null) {
					oUDigCommunicationManager.openProject();
				}

				setDefaultCursor();
			}
		};
		thread.start();
	}

	/**
	 * Initialize various elements like menus and toolbars
	 * and set up the users home view for the curent project.
	 * @throws SQLException
	 */
	private void initializeForProject()  {

		if (oModel != null) {
			oMenuManager.onDatabaseOpen();
			oToolBarManager.onDatabaseOpen();

			// get home view and nodes/links..
			setNodesAndLinks();

			// Create and initialize the outline View -Lakshmi 2/2/06
			createOutlineView();

			// Create and initialize the unread View -Lakshmi 6/27/06
			try {
				createUnreadView();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			//set the trashbin icon
			setTrashBinIcon();

			refreshCodeAndMenuData();

			// Create the tags view if the user has requested it.
			createTagsView();
		}
	}

	/**
	 * Load the codes and code groups into the model and refresh the Favorites and Workspace menus.
	 */
	private void refreshCodeAndMenuData() {

		// load codes for project into model
		loadAllCodes();

		// load code groups for project into model
		loadAllCodeGroups();

		// refile the codes dropdown
		oToolBarManager.updateCodeChoiceBoxData();

		// refresh Favorites menu
		refreshFavoritesMenu();

		// refresh Workspaces menu
		refreshWorkspaceMenu();

		// refreshWindowsMenu
		refreshWindowsMenu();
	}

	/**
	 * Makes a pass through all open views to see if any are dirty (i.e., have been modified by another person)
	 * This is called by the UIRefreshManager timed-refresh thread, so is running in the 'background'.
	 */
	public void checkProjectDirty() {
		//System.out.println("2452 ProjectCompendiumFrame entered checkProjectDirty");

		boolean bInboxChecked = false;
		boolean bInboxDirty = false;


		if (!bReloadingProject && !bChecking && (oModel != null)) { //If project being manually reloaded or check already in progress then skip the timed refresh
			oToolBarManager.disableDataRefresh();					// Turn off the manual Refresh toolbar button while checking
			bChecking = true;										// Stops overlapping checking (can happen if timer is fast & connection is slow)
			JInternalFrame[] frames = getDesktop().getAllFrames();

			for(int i=0; i<frames.length; i++) {
				UIViewFrame viewFrame = (UIViewFrame)frames[i];
				View innerview = viewFrame.getView();
				if (innerview != getHomeView()) {					// Skip Home window since other people can't make it dirty
					try {
						if (innerview.isViewDirty()) {				// Had a dirty view, need to refresh the ViewFrame's contents...
							refreshViewFrame(viewFrame, innerview);
							if (innerview == getInBoxView()) {
								System.out.println("2470 ProjectCompendiumFrame setting Inbox Dirty");
								bInboxDirty = true;					// Flag to do an inbox pop-up after everything else is checked
							}
						}
					} catch (Exception ex) {}
				}
				if (innerview == getInBoxView()) bInboxChecked = true;
			}
			// Force the inbox to be examined in the case where the user did not have it open...
			if(!bInboxChecked) {
				try {
					if (getInBoxView().isViewDirty()) {
						System.out.println("2482 ProjectCompendiumFrame setting Inbox Dirty");
						bInboxDirty = true;
					}
				} catch (Exception ex) {}
			}
			//if (bInboxDirty) JOptionPane.showMessageDialog(this, "A new node has arrived in your Inbox.");

			bChecking = false;
			oToolBarManager.enableDataRefresh();		// Turn the Refresh button back on
		}

	}

	/**
	 * Redraw the view.  This is called by checkProjectDirty() after the View data has been
	 * refreshed due to a groupware update by another user.
	 */
	private void refreshViewFrame(UIViewFrame viewFrame, View innerview) {

		setWaitCursor(viewFrame);

		if (viewFrame instanceof UIMapViewFrame) {
			int xPos = viewFrame.getHorizontalScrollBarPosition();
			int yPos = viewFrame.getVerticalScrollBarPosition();
			((UIMapViewFrame)viewFrame).createViewPane((View)innerview);
			viewFrame.setHorizontalScrollBarPosition(xPos, false);
			viewFrame.setVerticalScrollBarPosition(yPos, true);
		} else {													// Destroy and recreate the Frame from scratch.  There's got to be
			UIListViewFrame frame = (UIListViewFrame)viewFrame;		// a better way to refresh the List views, but I haven't found it yet....
			String title = innerview.getLabel();
			int width = frame.getWidth();
			int height = frame.getHeight();
			int xPos = frame.getX();
			int yPos = frame.getY();
			boolean isIcon = frame.isIcon();
			boolean isMaximum = frame.isMaximum();
			int hScroll = frame.getHorizontalScrollBarPosition();
			int vScroll = frame.getVerticalScrollBarPosition();

			oDesktop.getDesktopManager().closeFrame(viewFrame);
			oDesktop.remove(viewFrame);
			viewFrame.cleanUp();
			viewFrameList.remove(viewFrame);
			viewFrame.dispose();

			viewFrame = addViewToDesktop(innerview, title, width, height, xPos, yPos, isIcon, isMaximum, hScroll, vScroll);

			viewFrameList.add(viewFrame);

		}
		validateComponents();				// Probably not necessary, but....
		setDefaultCursor(viewFrame);
	}


	/**
	 * Clear all cached data and reload from the database.
	 */
	public void reloadProjectData() {

		bReloadingProject = true;

		if (oModel != null) {

			String sHomeWindowID = oHomeView.getId();

			UIViewFrame currentView = getCurrentFrame();

			setWaitCursor();

			Code.clearList();
			Link.clearList();
			NodeSummary.clearList();
			refreshCodeAndMenuData();
			viewFrameList.removeAllElements();

			String trashbinID = getTrashBinID();
			INodeService oNodeService = oModel.getNodeService();
			PCSession oSession = oModel.getSession();

			JInternalFrame[] frames = getDesktop().getAllFrames();
			for(int i=0; i<frames.length; i++) {
				UIViewFrame viewFrame = (UIViewFrame)frames[i];
				viewFrameList.addElement(viewFrame);

				View innerview = viewFrame.getView();
				try {
					innerview = (View)oNodeService.getView(oSession, innerview.getId());
				} catch (Exception ex) {}

				if (innerview != null) {
					innerview.initialize(oSession, oModel);

					if (innerview.getId().equals(sHomeWindowID)) oHomeView = innerview;

					viewFrame.setView(innerview);
					if (viewFrame instanceof UIMapViewFrame) {

						UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
						UINode trashbin = (UINode)pane.get(trashbinID);

						try {
							innerview.reloadViewData();
						}
						catch(Exception io) {
							io.printStackTrace();
							System.out.flush();
						}

						if (trashbin != null) {
							innerview.addMemberNode(trashbin.getNodePosition());
						}
						int xPos = viewFrame.getHorizontalScrollBarPosition();
						int yPos = viewFrame.getVerticalScrollBarPosition();

						((UIMapViewFrame)viewFrame).createViewPane((View)innerview);

						viewFrame.setHorizontalScrollBarPosition(xPos, false);
						viewFrame.setVerticalScrollBarPosition(yPos, true);
					}
					else {
						UIListViewFrame frame = (UIListViewFrame)viewFrame;

						try {
							innerview.reloadViewData();
						}
						catch(Exception io) {}

						frame.createList(innerview);
						frame.getUIList().updateTable();
					}
				}
			}

			validateComponents();

			setDefaultCursor();
		}

		bReloadingProject = false;
	}

	/**
	 * Open the dialog to create a new Empty Database.
	 */
	public void onFileNew() {

		if (isProjectOpen("New Project"))
			return;

		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			UINewDatabaseDialog dialog = new UINewDatabaseDialog(this, vtProjects, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			dialog.setVisible(true);
		}
		else {
			UINewDatabaseDialog dialog = new UINewDatabaseDialog(this, vtProjects, "", "", "");
			dialog.setVisible(true);
		}
	}

	/**
	 * Refreshes the undo/redo buttons with the last action performed.
	 * @see com.compendium.ui.UIToolBarManager#refreshUndoRedo
	 * @see com.compendium.ui.UIMenuManager#refreshUndoRedo
	 */
	public void refreshUndoRedo(UndoManager oUndoManager) {
		oToolBarManager.refreshUndoRedo(oUndoManager);
		oMenuManager.refreshUndoRedo(oUndoManager);
	}

	public void onFileDatabaseAdmin() {

		if (isProjectOpen("Database Administration"))
			return;


		UIDatabaseAdministrationDialog dlg = new UIDatabaseAdministrationDialog(this, APP_PROPERTIES.getDatabaseType(), oCurrentMySQLConnection);
		UIUtilities.centerComponent(dlg, this);
		dlg.setVisible(true);
	}

	/**
	 * Open the dialog to convert a Compendium Derby database to Compendium MySQL Database.
	 */
	public void onFileConvertFromDerby() {

		if (isProjectOpen("Project Convertion"))
			return;

		//UIConvertFromDerbyDatabaseDialog dialog = new UIConvertFromDerbyDatabaseDialog(ProjectCompendium.APP, adminDerbyDatabase.getProjectSchemaStatus(), oCurrentMySQLConnection, adminDerbyDatabase.getDatabaseProjects());
		UIConvertFromDerbyDatabaseDialog dialog = new UIConvertFromDerbyDatabaseDialog(ProjectCompendium.APP, oCurrentMySQLConnection, adminDerbyDatabase.getDatabaseProjects());
		UIUtilities.centerComponent(dialog, this);
		dialog.setVisible(true);
	}

	/**
	 * Open the dialog to convert a Compendium MySQL database to Compendium Derby Database.
	 */
	public void onFileConvertFromMySQL() {

		if (isProjectOpen("Project Convertion"))
			return;

		try {
			Vector connections = adminDerbyDatabase.getMySQLConnections();
			if (connections.size() > 0) {
				UIConvertFromMySQLDatabaseDialog dialog = new UIConvertFromMySQLDatabaseDialog(ProjectCompendium.APP, connections);
				UIUtilities.centerComponent(dialog, this);
				dialog.setVisible(true);
			}
			else {
				displayMessage("In order to Convert from MySQL To Derby,\nyou first need to create a new MySQL connection profile by\nentering the MySQL database details in the following dialog...\n", "Convert Project");
				UIDatabaseAdministrationDialog dlg = new UIDatabaseAdministrationDialog(this, ICoreConstants.MYSQL_DATABASE, null);
				UIUtilities.centerComponent(dlg, this);
				dlg.setVisible(true);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception (ProjectCompendiumFrame.onFileConvertFromMySQL)\n\n"+ex.getMessage());
			displayError("Unable to determine MySQL connection profile information at this time.");
		}
	}


	/**
	 * Open the Project management dialog.
	 */
	public void onDatabases() {

		if (isProjectOpen("Project Management"))
			return;

		//Hashtable htProjectStatus = adminDatabase.getProjectSchemaStatus();
		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			//UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, htProjectStatus, adminDatabase, vtProjects, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, adminDatabase, vtProjects, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			UIUtilities.centerComponent(dialog, this);
			dialog.setVisible(true);
		}
		else {
			//UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, htProjectStatus, adminDatabase, vtProjects, "", "", "");
			UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, adminDatabase, vtProjects, "", "", "");
			UIUtilities.centerComponent(dialog, this);
			dialog.setVisible(true);
		}
	}

	public void onSynchronize ()
	{
		System.out.println("2715 ProjectCompendiumFrame entered onSynchronize");

		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE)
		{
			JOptionPane.showMessageDialog(null, "The MySQL Projects are currently loaded, please load the Derby Projects");
			return;
		}
		else
		{
			//JOptionPane.showMessageDialog(null, "derby db projects?");
		}

		if (vtProjects == null)
			System.out.println("vtProjects is null");
		else if (vtProjects.size() == 0)
			System.out.println("vtProjects size is 0");
		else
		{
			int count = vtProjects.size();
			for (int i=0; i<count; i++)
			{
				String project = (String)vtProjects.elementAt(i);

				try
				{
					String nextModel = adminDatabase.getDatabaseName(project);
					String friendlyName = adminDatabase.getFriendlyName(nextModel);
					System.out.println("2732 ProjectCompendiumFrame nextModel is " + nextModel + " friendly: " + friendlyName);
					//JOptionPane.showMessageDialog(null, "Project: " + nextModel + " friendly: " + friendlyName);

					Synchronizer sync = new Synchronizer();
					sync.setLogOnlyToSystemOut(true);

					//System.out.println("2740 ProjectCompendiumFrame parent path reports: " + oParent.sHOMEPATH.replace('\\', '/'));
					//System.out.println("2740 ProjectCompendiumFrame derby currently is    C:/Program Files/Compendium/System/resources/Databases/");
					sync.setDerbyUrl("jdbc:derby:" + oParent.sHOMEPATH.replace('\\', '/') + "/System/resources/Databases/" + nextModel + ";create=false");
					sync.setDerbyConnection();

					if (sync.getRowCount("System", sync.getDerbyConnection(), " where Property='mysql_sourced' and Contents='true'") == 1)
					{
						//sync-able project

						int answer = JOptionPane.showConfirmDialog(null, "Sync: " + friendlyName + "?");
						if (answer == JOptionPane.YES_OPTION)
						{
							// User clicked YES.
							//JOptionPane.showMessageDialog(null, "syncing...");

							JFrame f = new JFrame("Synchronization Progress");
							JTextArea _resultArea = new JTextArea(3, 60);
							_resultArea.setText("Initializing...");

							JScrollPane scrollingArea = new JScrollPane(_resultArea);

							//... Get the content pane, set layout, add to center
							JPanel content = new JPanel();
							content.setLayout(new BorderLayout());
							content.add(scrollingArea, BorderLayout.CENTER);

							//... Set window characteristics.
							f.setContentPane(content);
							f.setTitle("Synchronization Progress");
							f.pack();

							f.setVisible(true);
							f.paint(f.getGraphics());

							_resultArea.setText("Retrieving Connection Info...");
							f.paint(f.getGraphics());

							String ip = sync.getColumnValue(sync.issueSelectQuery(sync.getDerbyConnection(), "select Contents from System where Property = 'mysql_source_ip'"), "Contents");
							String db = sync.getColumnValue(sync.issueSelectQuery(sync.getDerbyConnection(), "select Contents from System where Property = 'mysql_source_db'"), "Contents");
							String u = sync.getColumnValue(sync.issueSelectQuery(sync.getDerbyConnection(), "select Contents from System where Property = 'mysql_source_user'"), "Contents");
							String p = sync.getColumnValue(sync.issueSelectQuery(sync.getDerbyConnection(), "select Contents from System where Property = 'mysql_source_password'"), "Contents");

							//String mysql_url = "jdbc:mysql://" + ip + "/" + db + "?user=" + u + "&password=" + p;
							String mysql_url = "jdbc:mysql://" + ip + "/" + db;

							System.out.println("mysql url is " + mysql_url);

							_resultArea.setText("Going to set MySQL Connection...");
							f.paint(f.getGraphics());

							sync.setMySQLUrl(mysql_url);
							sync.setMySQLConnection(u,p);

							if (sync.mysql_conn == null)
							{
								//JOptionPane.showMessageDialog(null, "Unable to Connect to the MySQL Server - cannot Synchronize.");
								_resultArea.setText("Synchronization aborted.\n(Failed MySQL connection - check your internet connection?)");
								f.paint(f.getGraphics());
							}
							else
							{
								java.util.Date now = new java.util.Date();
								long lnow = now.getTime();
								sync.log("2742 ProjectCompendiumFrame Now C time is " + lnow);

								String last_sync_time = sync.getColumnValue(sync.issueSelectQuery(sync.getDerbyConnection(), "select Contents from System where Property = 'last_sync_time'"), "Contents");

								System.out.println("2746 ProjectCompendiumFrame last sync time from db is " + last_sync_time);
								System.out.flush();

								_resultArea.setText("going to gen statements...");
								f.paint(f.getGraphics());

								sync.generateStatements(Long.parseLong(last_sync_time), f, _resultArea);

								_resultArea.setText("going to issue updates...");
								f.paint(f.getGraphics());

								sync.send(f, _resultArea);

								sync.issueUpdateQuery(sync.getDerbyConnection(), "update System set Contents = '" + lnow + "' where Property = 'last_sync_time'");

								//_resultArea.setText("reloading current project...");
								//f.paint(f.getGraphics());

								//reloadProjectData();

								String cf = sync.getConflictReport();
								_resultArea.setText("Done. " + (new java.util.Date()).toString() + ". " + cf);

								if (cf.length() > 100)
									f.setSize(900,900);

								f.paint(f.getGraphics());
							}
						}
						else if (answer == JOptionPane.NO_OPTION)
						{
							// User clicked NO.
							JOptionPane.showMessageDialog(null, "skipped");
						}

					}
				}
				catch(Exception io)
				{
					System.out.println("Exception = "+io.getMessage());
					System.out.flush();
				}
			}
		}

		System.out.println("2800 ProjectCompendiumFrame exiting onSynchronize");
	}

	/**
	 * Open the dialog to confirm marking the entire project as Seen.  This function is intended to be
	 * used when a new person joins a project and needs to 'catch up' on everything.
	 */

	public void onMarkProjectSeen() throws SQLException {
		if (isProjectOpen2()) {
			long lNodeCount = ProjectCompendium.APP.getModel().getNodeService().lGetNodeCount(ProjectCompendium.APP.getModel().getSession());
			dlgMarkProjectSeen = new UIMarkProjectSeenDialog(this, lNodeCount);
			dlgMarkProjectSeen.setVisible(true);
		}
	}

	/**
	 * Open the dialog to backup the current database.
	 */
	public void onFileBackup() {
		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			UIDatabaseManagementDialog manager = new UIDatabaseManagementDialog(this, adminDatabase, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			//manager.onBackup(sFriendlyName, oModel.getModelName(), UIDatabaseManagementDialog.RESUME_NONE, true);
			//manager.onCancel();
			UIBackupDialog dialog = new UIBackupDialog(ProjectCompendium.APP, manager, sFriendlyName, oModel.getModelName(), UIDatabaseManagementDialog.RESUME_NONE, true);
			UIUtilities.centerComponent(dialog, ProjectCompendium.APP);
			dialog.setVisible(true);

		}
		else {
			UIDatabaseManagementDialog manager = new UIDatabaseManagementDialog(this, adminDatabase, "", "", "");
			//manager.onBackup(sFriendlyName, oModel.getModelName(), UIDatabaseManagementDialog.RESUME_NONE, true);
			//manager.onCancel();
			UIBackupDialog dialog = new UIBackupDialog(ProjectCompendium.APP, manager, sFriendlyName, oModel.getModelName(), UIDatabaseManagementDialog.RESUME_NONE, true);
			UIUtilities.centerComponent(dialog, ProjectCompendium.APP);
			dialog.setVisible(true);
		}
	}

	/**
	 * Open the connection Dialog.
	 * @param sType, the type of connection dialog to open.
	 */
	public void onConnect(String sType) {
	//	UIConnectionDialog dialog = new UIConnectionDialog( this, sType );
	//	UIUtilities.centerComponent(dialog, this);
	//	dialog.setVisible(true);
	}

	/**
	 * Imports a questmap file into a user selected view from the active project compendium model.
	 * @param showViewList, true to import into multiple views else false for current view.
	 */ /*
	public void onFileImport(boolean showViewList) {

		dlgImport = new UIImportDialog(this, showViewList);
		if (!showViewList) {
			UIViewFrame viewFrame = getCurrentFrame();

			if (viewFrame instanceof UIMapViewFrame) {
				dlgImport.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI() );
			}
			else {
				if ( ((UIListViewFrame)viewFrame).getUIList() != null)
					dlgImport.setUIList( ((UIListViewFrame)viewFrame).getUIList() );
			}
		}

		dlgImport.setVisible(true);
	}
*/
	/**
	 * Imports a folder of images into the active view as reference nodes
	 */
	public void onFileImportImageFolder() {
		onFileImportImageFolder(null);
	}

	/**
	 * Imports a folder of images into the given view as reference nodes.
	 *
	 * @param viewFrame com.compendium.ui.UIViewFrame, the viewFrame to import the images into.
	 * If null use the current view.
	 */
	public void onFileImportImageFolder(UIViewFrame viewFrame) {

		ImportImageFolder img = new ImportImageFolder();

		if (viewFrame == null)
			viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			if ( ((UIMapViewFrame)viewFrame).getViewPane() != null)
				img.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI() );
		}
		else {
			if ( ((UIListViewFrame)viewFrame).getUIList() != null)
				img.setUIList( ((UIListViewFrame)viewFrame).getUIList());
		}

		img.start();
	}

	/**
	 * Convenience Method to get the import profile
	 * @return Vector, the the import profile details.
	 */
	public Vector getImportProfile() {
		return oImportProfile.getProfile();
	}

	/**
	 * Convenience Method to set the import profile from the import dialog.
	 *
	 * @param normalImport, true to preserve the importing dates and authors, false to use current date and author.
	 * @param includeInDetail, true to include node details in the export.
	 * @param preserveIDs, true to preserve importing node ids.
	 * @param transclude, true to transclude importing nodes.
	 */
	public void setImportProfile(boolean normalImport, boolean includeInDetail, boolean preserveIDs, boolean transclude) {
		oImportProfile.setProfile(normalImport, includeInDetail, preserveIDs, transclude);
	}

	/**
	 * Opens the dialog to exports Compendium views to HTML outline files.
	 */
	public void onFileExportHTMLOutline() {

		dlgExport = new UIExportDialog(this, getCurrentFrame());
		UIUtilities.centerComponent(dlgExport, this);
		dlgExport.setVisible(true);
	}

	public void onFileExportWordDocOutline()
	{

		dlgExport = new UIExportDialog(this, getCurrentFrame());
		dlgExport.setWordDocExportOptions();
		UIUtilities.centerComponent(dlgExport, this);
		dlgExport.setVisible(true);
	}
	/**
	 * Opens the dialog to exports Compendium views to HTML View.
	 */
	public void onFileExportHTMLView() {

       	dialog2 = new UIExportViewDialog(this, getCurrentFrame());
		UIUtilities.centerComponent(dialog2, this);
		dialog2.setVisible(true);
	}

	/**
	 * Export to HTML Views with XML included.
	 */
	public void onFileExportPower() {

		htCheckDepth = new Hashtable(51);
		htChildrenAdded = new Hashtable(51);

		final UIViewFrame frame = getCurrentFrame();
		final Vector selectedViews = getSelectedViews();

		if (selectedViews.size() == 0) {
			displayMessage("Please select a map to export", "Export Web Maps/Outline + XML");
			return;
		} else {
			int count = 0;
			if (frame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)frame).getViewPane();
				count = uiViewPane.getNumberOfSelectedNodes();
			}
			else {
				UIList uiList = ((UIListViewFrame)frame).getUIList();
				count = uiList.getNumberOfSelectedNodes();
			}

			if (count > 1) {
				displayMessage("You can only export one top level map.\n\nPlease ensure that you only have one map selected.\n", "Export Web Maps/Outline + XML");
				return;
			}
		}

		UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files");

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle("Enter the file name to Export to...");
		fileDialog.setFileFilter(filter);
		fileDialog.setApproveButtonText("Save");
		fileDialog.setRequiredExtension(".zip");

	    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
	    File file = new File(UIExportViewDialog.exportDirectory+ProjectCompendium.sFS);
	    if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}

	    String sDirectory = "";
	    String fileName = "";
		int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {
            	fileName = fileDialog.getSelectedFile().getName();
				File fileDir = fileDialog.getCurrentDirectory();
				if (fileName != null) {
					if ( !fileName.toLowerCase().endsWith(".zip") ) {
						fileName = fileName+".zip";
					}
					sDirectory = fileDir.getAbsolutePath();
				}
			}
		}

		if (fileName != null && !fileName.equals("")) {
			final String fFileName = fileName;
			final String fsDirectory = sDirectory;
			Thread thread = new Thread("ProjectCompendium.APP.onFileExportHTMLViewWithXML") {
				public void run() {

					// XML ZIP EXPORT
					setWaitCursor();

					boolean selectedOnly = true;
					boolean allDepths = true;
					boolean withStencilsAndLinkGroups = true;
					boolean withMeetings = false;
					boolean toZip = true;

					String zipFileName = fFileName.replaceAll(".zip", "_xml.zip");
					File xmlFile = new File(fsDirectory+ProjectCompendium.sFS+zipFileName);
					XMLExportNoThread export = new XMLExportNoThread(frame, xmlFile.getAbsolutePath(), allDepths, selectedOnly, toZip, withStencilsAndLinkGroups, withMeetings, false);

					// OUTLINE ZIP EXPORT
					boolean bPrintNodeDetail = true;
					boolean bPrintNodeDetailDate = false;
					boolean bPrintAuthor = false;
					int nExportLevel = 2;
					//String sExportFile = fsDirectory+ProjectCompendium.sFS+fFileName.replaceAll(".zip", "_outline.zip");
					String sExportFile = fsDirectory+ProjectCompendium.sFS+fFileName;
					File outlineFile = new File(sExportFile);
					boolean bToZip = true;

					HTMLOutline oHTMLExport = new HTMLOutline(bPrintNodeDetail, bPrintNodeDetailDate, bPrintAuthor, nExportLevel, outlineFile.getAbsolutePath(), bToZip);
					oHTMLExport.setIncludeImage(true);
					oHTMLExport.setIncludeNodeAnchors(true);
					oHTMLExport.setIncludeDetailAnchors(true);
					oHTMLExport.setUseAnchorNumbers(false);
					oHTMLExport.setAnchorImage(UIExportDialog.sBaseAnchorPath+"anchor0.gif");
					oHTMLExport.setTitle("Open Learn Outline Export");
					oHTMLExport.setDisplayInDifferentPages(true);
					oHTMLExport.setDisplayDetailDates(false);
					oHTMLExport.setHideNodeNoDates(false);
					oHTMLExport.setIncludeLinks(false);
					oHTMLExport.setIncludeNavigationBar(true);
					oHTMLExport.setInlineView(true);
					oHTMLExport.setNewView(false);
					oHTMLExport.setIncludeViews(true);
					oHTMLExport.setIncludeTags(true);
					oHTMLExport.setIncludeFiles(true);

					UIExportDialog dlg = new UIExportDialog(frame);
					boolean bSelectedViewsOnly = true;
					boolean bOtherViews = false;
					if (dlg.printExport(oHTMLExport, bOtherViews, bSelectedViewsOnly, nExportLevel)) {
						oHTMLExport.print();
					} else {
						displayError("Unable to include Outline in export.");
					}

					// WEB ZIP EXPORT
					String sUserTitle = "";
					boolean bIncludeReferences = true;
					boolean addMapTitles = true;
					boolean bOpenNew = true;
					bToZip = true;
					boolean bSortMenu = false;
					boolean bNoDetailPopup = false;
					boolean bNoDetailPopupAtAll = false;
					HTMLViews htmlViews = new HTMLViews(fsDirectory, fFileName, sUserTitle, bIncludeReferences, bToZip, bSortMenu, bOpenNew, bNoDetailPopup, bNoDetailPopupAtAll);
					htmlViews.processViewsWithXML(selectedViews, xmlFile.getAbsolutePath());

					setDefaultCursor();

					if (!ProjectCompendium.isLinux) {
						ExecuteControl.launch(fsDirectory);
					}
				}
			};
			thread.start();
		}
	}

	/**
	 * Get the views to export depending on user options.
	 * Vector, the list of view to export.
	 */
	private Vector getSelectedViews() {

		Vector selectedViews = new Vector();
		Enumeration nodes = null;
		Vector vtTemp = new Vector();

		UIViewFrame currentFrame = this.getCurrentFrame();

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

		//ADD THE CHILD VIEWS TO THE childViews VECTOR
		for(int j=0; j < vtTemp.size(); j++) {
			NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
			View innerview = (View)nodePos.getNode();
			selectedViews.addElement(innerview);
		}

		for (int i = 0; i < vtTemp.size(); i++) {
			NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
			View view = (View)nodePos.getNode();
			htCheckDepth.put((Object)view.getId(), view);
			selectedViews = getChildViews(view, selectedViews);
		}

		return selectedViews;
	}

	/**
	 * Return the child views for the given view.
	 * @param view com.compendium.code.datamodel.View, the view to return the child nodes to.
	 * @param childViews, the child views found.
	 */
	private Vector getChildViews(View view, Vector childViews) {
		try {
			Vector vtTemp = getModel().getViewService().getNodePositions(oModel.getSession(), view.getId());
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

			//GET CHILD VIEWS CHILDREN
			for (int j = 0; j < nodePositionList.size(); j++) {
				NodePosition np = (NodePosition)nodePositionList.elementAt(j);
				View innerview = (View)np.getNode();
				if (!htChildrenAdded.containsKey((Object)innerview.getId())) {
					htChildrenAdded.put((Object)innerview.getId(), innerview);
					childViews = getChildViews(innerview, childViews);
				}
			}
		}
		catch (Exception e) {
			ProjectCompendium.APP.displayError("Exception: (ProjectCompendiumFrame.getChildViews) \n\n" + e.getMessage());
		}

		return childViews;
	}


// XML IMPORT AND EXPORT

	/**
	 * Imports an XML file into the current view.
	 */
	public void onFileXMLImport() {

		dlgImportXML = new UIImportXMLDialog(this);
		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			if ( ((UIMapViewFrame)viewFrame).getViewPane() != null) {
				dlgImportXML.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI());
			}
		}
		else {
			if ( ((UIListViewFrame)viewFrame).getUIList() != null)
				dlgImportXML.setUIList( ((UIListViewFrame)viewFrame).getUIList());
		}
		dlgImportXML.setVisible(true);
	}

	/**
	 * Imports an XML file into a user selected view from the active project
	 * compendium model from the given filename.
	 * @param file, the file to import.
	 */
	public void onFileXMLImport(File file) {

		dlgImportXML = new UIImportXMLDialog(this, file);
		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			if ( ((UIMapViewFrame)viewFrame).getViewPane() != null) {
				dlgImportXML.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI() );
			}
		}
		else {
			if ( ((UIListViewFrame)viewFrame).getUIList() != null)
				dlgImportXML.setUIList( ((UIListViewFrame)viewFrame).getUIList() );
		}
		dlgImportXML.setVisible(true);
	}

	/**
	 * Imports an XML file into a user selected view as a Template, from the given filename.
	 * @param sXMLFile, the file to import.
	 */
	public void onTemplateImport(String sXMLFile) {

		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		if (frame instanceof UIMapViewFrame) {
			UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
			UIViewPane oViewPane = mapFrame.getViewPane();
			onTemplateImport(sXMLFile, oViewPane);
		} else {
			UIListViewFrame listFrame = (UIListViewFrame)frame;
			UIList uiList = listFrame.getUIList();
			onTemplateImport(sXMLFile, uiList);
		}
	}

	/**
	 * Imports an XML file into a user selected view as a Template, from the given filename.
	 * @param sXMLFile, the file to import.
	 */
	public void onTemplateImport(String sXMLFile, UIViewPane oViewPane) {

		boolean importAuthorAndDate = false;
		boolean includeOriginalAuthorDate = false;
		boolean preserveIDs = false;
		boolean transclude = false;
		boolean updateTranscludedNodes = false;
		boolean markSeen = true;

		File oXMLFile = new File(sXMLFile);
		if (oXMLFile.exists()) {
			DBNode.setImportAsTranscluded(transclude);
			DBNode.setPreserveImportedIds(preserveIDs);
			DBNode.setUpdateTranscludedNodes(updateTranscludedNodes);
			DBNode.setNodesMarkedSeen(markSeen);

			if (oViewPane != null) {
				ViewPaneUI oViewPaneUI = oViewPane.getUI();
				if (oViewPaneUI != null) {
					oViewPaneUI.setSmartImport(importAuthorAndDate);
					oViewPaneUI.onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
				}
			}
		}
	}

	/**
	 * Imports an XML file into a user selected view as a Template, from the given filename.
	 * @param sXMLFile the file to import.
	 * @param uiList the list to import the data into.
	 */
	public void onTemplateImport(String sXMLFile, UIList uiList) {

		boolean importAuthorAndDate = false;
		boolean includeOriginalAuthorDate = true;
		boolean preserveIDs = false;
		boolean transclude = false;
		boolean updateTranscludedNodes = false;
		boolean markSeen = true;

		File oXMLFile = new File(sXMLFile);
		if (oXMLFile.exists()) {
			DBNode.setImportAsTranscluded(transclude);
			DBNode.setPreserveImportedIds(preserveIDs);
			DBNode.setUpdateTranscludedNodes(updateTranscludedNodes);
			DBNode.setNodesMarkedSeen(markSeen);

			if (uiList != null) {
				uiList.getListUI().setSmartImport(importAuthorAndDate);
				uiList.getListUI().onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
			}
		}
	}
	/**
	 * Exports a user selected view to an XML file.
	 * @param multipleViews, false if exporting the current view, true is exporting multiple views.
	 */
	public void onFileXMLExport(boolean multipleViews) {

		dlgExportXML = new UIExportXMLDialog(this);

		if (!multipleViews) {
			UIViewFrame viewFrame = getCurrentFrame();
			dlgExportXML.setCurrentView(viewFrame);
		}

		if (dlgExportXML.hasSelectedMapNodes()) {
    		dlgExportXML.initDefaults();
    		dlgExportXML.setVisible(true);
		} else {
            displayMessage("Please select a map node to export.\n\nMulitple map nodes can be selected.", "Export To XML");
		}
	}

	/**
	 * Save the current map as a JPEG.
	 */
	public void onSaveAsJpeg() {

		UIViewFrame frame = getCurrentFrame();

		if (frame instanceof UIMapViewFrame) {
			try {
				UIFileFilter jpgFilter = new UIFileFilter(new String[] {"jpg"}, "JPEG Image Files");

				UIFileChooser fileDialog = new UIFileChooser();
				fileDialog.setDialogTitle("Enter the file name to save as...");
				fileDialog.setFileFilter(jpgFilter);
				fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
				fileDialog.setRequiredExtension(".jpg");

	    		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
	    		// AND MUST USE ABSOUTE PATH, AS RELATIVE PATH REMOVES THE '/'
	    		File filepath = new File("");
	    		String sPath = filepath.getAbsolutePath();
	    		File file = new File(sPath+ProjectCompendium.sFS+"Exports"+ProjectCompendium.sFS);
	    		if (file.exists()) {
					fileDialog.setCurrentDirectory(file);
				}

				String fileName = "";
				UIUtilities.centerComponent(fileDialog, this);
				int retval = fileDialog.showDialog(this, null);

	    		if (retval == JFileChooser.APPROVE_OPTION) {
                	if ((fileDialog.getSelectedFile()) != null) {

                    	fileName = fileDialog.getSelectedFile().getAbsolutePath();

						if (fileName != null) {
							if ( !fileName.toLowerCase().endsWith(".jpg") ) {
								fileName += ".jpg";
							}
						}

						UIViewPane pane = ((UIMapViewFrame)frame).getViewPane();
						Dimension size = pane.calculateSize();

						BufferedImage img = (pane.getGraphicsConfiguration()).createCompatibleImage(size.width, size.height, Transparency.OPAQUE);
						Graphics2D graphics = img.createGraphics();
						pane.paint(graphics);

						if (ProjectCompendium.isLinux) {
							Iterator iter = ImageIO.getImageWritersByFormatName("JPG");
							if (iter.hasNext()) {
								ImageWriter writer = (ImageWriter)iter.next();
								ImageWriteParam iwp = writer.getDefaultWriteParam();
								iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
								iwp.setCompressionQuality(1);
								File outFile = new File(fileName);
								FileImageOutputStream output = new FileImageOutputStream(outFile);
								writer.setOutput(output);
								IIOImage image = new IIOImage(img, null, null);
								writer.write(null, image, iwp);
							}
						}
						else {

							BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
							JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
							JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);

							param.setQuality(1.0f, false);

							encoder.setJPEGEncodeParam(param);
							encoder.encode(img);
							out.close();
						}
                  	}
               	}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception creating map image = "+ex.getMessage());
			}
		}
	}

	/**
	 * Setup page options for printing - CURRENTLY DOES NOTHING.
	 */
	public void onFilePageSetup() {}

	/**
	 * Prints the current active frame.
	 * @see #onPrint
	 */
	public void onFilePrint() {
		//get the active internal frame to print
		//UIViewFrame viewFrame = getCurrentFrame();
		onPrint();
	}

	/**
	 * Exits the application, and close connections and open frames.
	 */
	public void onExit() {
		System.out.println("3349 ProjectCompendiumFrame entered onExit");

		onFileClose();

		if (oUDigCommunicationManager != null) {
			oUDigCommunicationManager.sendGoodbye();
		}

		int screenX = getX();
		int screenY = getY();
		int screenWidth = getWidth();
		int screenHeight = getHeight();
		APP_PROPERTIES.setLastScreenWidth(screenWidth);
		APP_PROPERTIES.setLastScreenHeight(screenHeight);
		APP_PROPERTIES.setLastScreenX(screenX);
		APP_PROPERTIES.setLastScreenY(screenY);

	    APP_PROPERTY_MANAGER.save();
	    EXPORT_PROPERTY_MANAGER.save();

		setVisible(false);

		// SAVE CURRENT OPEN WINDOW PROPERTIES
		if(oDesktop != null && oModel != null) {

			// close all internal frames
			JInternalFrame[] frames = oDesktop.getAllFrames();
			for (int i = 0; i < frames.length; i++) {
				UIViewFrame viewframe = (UIViewFrame)frames[i];
				saveViewProperties(viewframe);
			}
		}

        if (oToolBarManager != null) {
			oToolBarManager.saveToolBarData();
        }


		cleanupServices();
		DBConnectionManager.shutdownDerby(APP_PROPERTIES.getDatabaseType());

		SaveOutput.stop();
		dispose();

        if (createdRunningFile) {
            try {
                File runningFile = new File(RUNNING_FILE);
                FileInputStream input = new FileInputStream(runningFile);
                FileLock lock =
                    input.getChannel().lock(0, runningFile.length(), true);
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(input));
                Vector instances = new Vector();
                String line = reader.readLine();
                while (line != null) {
                    instances.add(line);
                    line = reader.readLine();
                }
                lock.release();
                reader.close();
                input.close();
                while (instances.contains(ProjectCompendium.sHOMEPATH)) {
                    instances.remove(ProjectCompendium.sHOMEPATH);
                }
                if (instances.size() == 0) {
                    CoreUtilities.deleteFile(runningFile);
                } else {
                    FileOutputStream output =
                        new FileOutputStream(runningFile);
                    lock = output.getChannel().lock(0, runningFile.length(),
                            false);
                    PrintWriter writer = new PrintWriter(output);
                    for (int i = 0; i < instances.size(); i++) {
                        writer.println((String) instances.get(i));
                    }
                    lock.release();
                    writer.close();
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		System.out.println("3434 ProjectCompendiumFrame calling gc then terminating");

		System.gc();
		System.exit(0);
	}


// CLAIMAKER

	/**
	 * Set the ClaiMaker connection string.
	 *
	 * @param server, the ClaiMaker server string.
	public void openClaiMakerConnection(String server) {
		claiMakerServer = server;
		claiMakerConnected = true;
	}
*/
	/**
	 * Has a ClaiMaker server string been set?
	 * @return boolean, true if the string has been set, else false.
	 */
	//public boolean isClaiMakerConnected() {
//		return claiMakerConnected;
//	}

	/**
	 * Return the ClaiMaker server String.
	 * @return the ClaiMaker server String.
	 *
	public String getClaiMakerServer() {
		return claiMakerServer;
	}
*/
	/**
	 * Set the ClaiMaker connection to false.
	 *
	public void closeClaiMakerConnection() {
		claiMakerConnected = false;
	}
*/



//****************** EDIT MENU **********************/

	/**
	 * Undo the previous edit if any
	 */
	public void onEditUndo() {

		UIViewFrame viewFrame = getCurrentFrame();
		viewFrame.onUndo();

		setTrashBinIcon();

		refreshIconIndicators();
	}

	/**
	 * Redo the previous undo if any
	 */
	public void onEditRedo() {

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();
		viewFrame.onRedo();

		setTrashBinIcon();

		refreshIconIndicators();
	}

	/**
	 * Cuts the selected nodes and links from the current view and places it on the clipboard
	 */

	public void onEditCut() {
		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().cutToClipboard();
		}
		else {
			( ((UIMapViewFrame)viewFrame).getViewPane().getViewPaneUI() ).cutToClipboard(null);
		}

		stopWaitCursor(viewFrame);

	}

	/**
	 * Copies the selected nodes and links to the clipboard.
	 */
	public void onEditCopy() {

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().copyToClipboard();
		}
		else {
			( ((UIMapViewFrame)viewFrame).getViewPane().getViewPaneUI() ).copyToClipboard(null);
		}

		stopWaitCursor(viewFrame);
	}

	/**
	 * Copies the selected nodes and links to the clipboard with full map depth for pasting to another database
	 */
	public void onEditExternalCopy() {

		externalCopy = true;
		String userID = oModel.getUserProfile().getId();

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().externalCopyToClipboard();
		}
		else {
			( ((UIMapViewFrame)viewFrame).getViewPane().getViewPaneUI() ).externalCopyToClipboard(null, userID);
		}

		stopWaitCursor(viewFrame);
	}

	/**
	 * Pastes the contents of the clipboard into the current view (when copied from another database).
	 */
	public void onEditExternalPaste() {
		// USED ELSE WHERE FOR LOOP PREVENTION IN VIEWS CONTAINING THEMSELVES IN THEIR CHILD TREE
		ht_pasteCheck.clear();
		externalCopy = false;

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().externalPasteFromClipboard();
		}
		else {
			( ((UIMapViewFrame)viewFrame).getViewPane().getViewPaneUI() ).externalPasteFromClipboard();
		}

		stopWaitCursor(viewFrame);

		oMenuManager.setExternalPasteEnablement(false);

		setTrashBinIcon();
	}

	/**
	 * Pastes the contents of the clipboard into the current view.
	 */
	public void onEditPaste() {
		// USED ELSE WHERE FOR LOOP PREVENTION IN VIEWS CONTAINING THEMSELVES IN THEIR CHILD TREE
		ht_pasteCheck.clear();

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().pasteFromClipboard();
		}
		else {
			( ((UIMapViewFrame)viewFrame).getViewPane().getViewPaneUI() ).pasteFromClipboard();
			if (oAerialViewDialog != null)
				oAerialViewDialog.scaleToFit(); // will refresh aerial view after paste
		}

		stopWaitCursor(viewFrame);

		setTrashBinIcon();
	}

	/**
	 * Delete the selected nodes and links in the current view to the clipboard
	 */
	public void onEditDelete() {

		isNewDelete = true;
		if(UIViewOutline.me != null && UIViewOutline.me.getTree().isFocusOwner()){
			setWaitCursor();
			UIViewOutline.me.onDelete();
			setDefaultCursor();
		} else {

			// get the active frame which will give the view to be searched
			UIViewFrame viewFrame = getCurrentFrame();
			startWaitCursor(viewFrame);
			if (viewFrame instanceof UIListViewFrame) {
				((UIListViewFrame)viewFrame).getUIList().getListUI().onDelete();
			}
			else {
				( ((UIMapViewFrame)viewFrame).getViewPane().getViewPaneUI() ).onDelete();
			}

			stopWaitCursor(viewFrame);
		}
		isNewDelete = false;
	}

	/**
	 * Selects All the nodes and links.
	 */
	public void onEditSelectAll() {

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().onSelectAll();
		}
		else {
			((UIMapViewFrame)viewFrame).getViewPane().selectAll();
		}

	}

	/**
	 * Image rollover status.
	 * @param state, true to turn on image rollover, false to turn it off.
	 */
	public void onImageRollover(boolean state) {

		APP_PROPERTIES.setImageRollover(state);

		oToolBarManager.updateImageRollover(APP_PROPERTIES.isImageRollover());
		oMenuManager.updateImageRollover(APP_PROPERTIES.isImageRollover());
	}

	/**
	 * Opent the search dialog.
	 */
	public void onSearch() {

		if (oModel == null) {
   			int answer = JOptionPane.showConfirmDialog(this, "You need to open a project to perform a search.\n\nWould you like to open a project?\n\n", "Search Project",
   						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.YES_OPTION) {
				onFileOpen();
			}
			return;
		}

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		UISearchDialog dialog = new UISearchDialog(ProjectCompendium.APP, viewFrame.getView());
		UIUtilities.centerComponent(dialog, ProjectCompendium.APP);
		dialog.setVisible(true);
	}

	/**
	 * Set the cursor to the wait cursor for the given frame.
	 * @param frame com.compendium.ui.UIViewFrame, the frame to set the cursor for.
	 */
	public void startWaitCursor(UIViewFrame frame) {

		final UIViewFrame viewFrame = frame;
		Thread thread = new Thread("Start Cursor") {
			public void run() {
				viewFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
				ProjectCompendium.APP.setWaitCursor();
			}
		};
		thread.start();
		Thread.currentThread().yield();
	}

	/**
	 * Set the cursor to the default cursor for the given frame.
	 * @param frame com.compendium.ui.UIViewFrame, the frame to set the cursor for.
	 */
	public void stopWaitCursor(UIViewFrame frame) {

		final UIViewFrame viewFrame = frame;
		Thread thread = new Thread("Stop Cursor") {
			public void run() {
				viewFrame.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
				ProjectCompendium.APP.setDefaultCursor();
			}
		};
		thread.start();
		Thread.currentThread().yield();
	}

//*************** MAP MENU ******************//

	/**
	 * Displays a dialog to select the map the user wants to view.
	 */
	public void onViewMap() {
		UISelectViewDialog dlgView = new UISelectViewDialog(this);
		UIUtilities.centerComponent(dlgView, this);
		dlgView.setVisible(true);
	}

	/**
	 * Displays a dialog to select the map the user wants to view.
	 */
	public void openGotoDialog() {
	    UIGotoDialog gotoDialog = new UIGotoDialog(this);
	    UIUtilities.centerComponent(gotoDialog, this);
	    gotoDialog.setVisible(true);
	}

	/**
	 * Displays a dialog to view/select nodes in limbo - not assigned to a view.
	 */
	public void onLimboNode() {

		setWaitCursor();
		Vector limboNodes = new Vector(51);
		try {
			limboNodes = ((NodeService)oModel.getNodeService()).getLimboNodes(oModel.getSession());
		}
		catch(Exception io) {

		}
		UISearchResultDialog dlgView = new UISearchResultDialog(this, limboNodes, "Active Nodes not active in a View");
		dlgView.setVisible(true);

		setDefaultCursor();
	}

	/**
	 * Refresh all the open views.
	 * @see #validateComponents
	 */
	public void onViewRefresh() {
		validateComponents();
	}

	/**
	 * Arrange the current view and update its aerial.
	 */
	public void onViewArrange(String option)	{

		setWaitCursor();
		IUIArrange arrange = null;
		UIViewFrame viewFrame = getCurrentFrame();
		if(option.equals(IUIArrange.TOPDOWN)){
			arrange = new UIArrangeTopDown();
		}
		else if(option.equals(IUIArrange.LEFTRIGHT)) {
			arrange = new UIArrangeLeftRight();
		}
		ArrangeEdit edit = new ArrangeEdit(viewFrame);
		edit.setArrange(arrange);
		arrange.arrangeView(viewFrame.getView(), viewFrame);

	    viewFrame.getUndoListener().postEdit(edit);

		if (oAerialViewDialog != null)
			oAerialViewDialog.scaleToFit(); // will refresh aerial view after arrange

		setDefaultCursor();
	}

// Begin edit - Lakshmi 11/17/05

	/**
	 * Align the selected nodes in a view and update its aerial.
	 */
	public void onViewAlign(String option)	{

		setWaitCursor();
		UIAlign align = new UIAlign(option);
		UIViewFrame viewFrame = getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			AlignEdit edit = new AlignEdit(viewFrame);
			edit.setAlign(align);
			align.alignNodes(viewFrame);

			viewFrame.getUndoListener().postEdit(edit);
		}

		if (oAerialViewDialog != null)
			oAerialViewDialog.scaleToFit(); // will refresh aerial view after arrange

		setDefaultCursor();
	}

// End edit - Lakshmi 11/17/05

//*************** WINDOW MENU ***********************//

	/**
	 * Displays the users home window.
	 */
	public void onViewHomeWindow() {

		boolean frameFound = false;
		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame.getView().getLabel().startsWith("Home Window")) {
				((UIMapViewFrame)viewFrame).setSelected(true);
				frameFound = true;
			}
		}

		if(!frameFound) {
			//for the time being..
			setNodesAndLinks();
			//set the trashbin icon
			setTrashBinIcon();
		}
	}

	/**
	 * Reset the toolbars to default.
	 */
	public void onResetToolBars() {
		oToolBarManager.onResetToolBars();
	}

	/**
	 * Update the codes choicebox data.
	 */
	public void updateCodeChoiceBoxData() {
		oToolBarManager.updateCodeChoiceBoxData();
	}

	/**
	 * Show any floating toolbars.
	 */
	public void showFloatingToolBars() {
		oToolBarManager.showFloatingToolBars();
	}

	/**
	 * If there is an aerial view, rescale it.
	 */
	public void scaleAerialToFit() {
		if (oAerialViewDialog != null)
			oAerialViewDialog.scaleToFit();
	}

	/**
	 * Open/close the areail view when Menuitem checked/unchecked.
	 * @param selected, true to open the aerial view, false to cancel it.
	 */
	public void onAerialView(boolean selected) {

		final UIViewFrame frame=getCurrentFrame();

		setWaitCursor();
		setWaitCursor(frame);

		final boolean fselected  = selected;
		Thread th = new Thread("APP.onShowAerialView") {
			public void run() {
	            if(fselected) {
	            	APP_PROPERTIES.setAerialView(true);
					updateAerialView();
				}
	            else {
					if (oAerialViewDialog != null)
						oAerialViewDialog.onCancel();
					else {
				       	APP_PROPERTIES.setAerialView(false);
					}
				}

				setDefaultCursor();
				setDefaultCursor(frame);
			}
		};
		th.start();
	}

	/**
	 * Hide the aerial view if user close aerial view themselves.
	 */
	public void cancelAerialView() {

       	APP_PROPERTIES.setAerialView(false);

		oMenuManager.setAerialView(false);

		if (oAerialViewDialog != null) {
			oAerialViewDialog.setVisible(false);
			oAerialViewDialog.dispose();
			oAerialViewDialog = null;
		}
	}

	/**
	 * Toggle the aerial view on/off
	 */
//	public void toggleAerialView() {
//		if (APP_PROPERTIES.aerialView) {
//			cancelAerialView();
//		} else {
//			onAerialView(true);
//		}
//	}

	/**
	 * Open the aerial view for the current.
	 */
	public void updateAerialView() {

		if (APP_PROPERTIES.isAerialView()) {
			UIViewFrame frame = getCurrentFrame();
			if (frame instanceof UIMapViewFrame) {
				Rectangle dialogBounds = null;
				if (oAerialViewDialog != null) {
					dialogBounds = oAerialViewDialog.getResetSize();
					oAerialViewDialog.close();
					oAerialViewDialog.setVisible(false);
					oAerialViewDialog.dispose();
				}
				UIMapViewFrame map = (UIMapViewFrame)frame;
				oAerialViewDialog = map.showArialView(dialogBounds);
			}
			else {
				oAerialViewDialog.setVisible(false);
			}
		}
	}

	/**
	 * Cascades the Internal Frames.
	 */
	public void onWindowCascade() {

	    int n = 0;
	    JInternalFrame [] frames = oDesktop.getAllFrames();
	    for(int i=frames.length-1; i>=0; i--) {
			JInternalFrame frame = frames[i];

			try {
			    frame.setMaximum(false);
			    if (frame.isIcon())
				frame.setIcon(false);

			    frame.setBounds(n*INTERNALFRAMEOFFSET, n*INTERNALFRAMEOFFSET,INTERNALFRAMEWIDTH,INTERNALFRAMEHEIGHT);
			    frame.moveToFront();
			    frame.setSelected(true);
			    n++;
			}
			catch(Exception e) {
			    e.printStackTrace();
			}
		}
	}

	/**
	 * Expand all the Internal Frames.
	 */
	public void onWindowExpand() {

		JInternalFrame [] frames = oDesktop.getAllFrames();
		for(int i=0;i<frames.length;i++) {
			JInternalFrame frame = frames[i];

			try {
				if (frame.isIcon())
					frame.setIcon(false);

				frame.setMaximum(true);
				frame.moveToFront();
			}
			catch(PropertyVetoException e) {}
		}
	}

	/**
	 * Closes all the Internal Frames.
	 */
	public void onWindowCloseAll() {

		// close all internal frames
		JInternalFrame[] frames = oDesktop.getAllFrames();

		for (int i = 0; i < frames.length; i++) {

			// DONT DELETE HOME FRAME
			if (frames[i] != getInternalFrame(oHomeView)) {
				oDesktop.getDesktopManager().closeFrame(frames[i]);
				oDesktop.remove(frames[i]);
				frames[i].dispose();
			}
		}
	}

/******** FORMAT MENU *********/

	/**
	 * Returns the current default Font used for node labels.
	 * @return Font, the current node label font.
	 */
	public Font getLabelFont() {
		 return labelFont;
	}

	/**
	 * Set the current font used for the default node label to the passed Font.
	 * @param oFont, the new font chosen.
	 */
	public void setLabelFont( Font oFont ) {
		labelFont = oFont;
	}

	/**
	 * Change the Node icons to current skin.
	 * @param laf, the look and feel to change to.
	 */
	/*public void onFormatLAF(String laf) {
		final String look = laf;
		Thread thread = new Thread("LAF") {
			public void run() {
				APP_PROPERTIES.currentLookAndFeel = look;
				//initLAF();
				APP_PROPERTIES.setFormatProp( "LAF", APP_PROPERTIES.currentLookAndFeel );
				APP_PROPERTIES.saveFormatProps();
			}
		};
		thread.start();
	}*/

	/**
	 * Change the Node icons to given skin.
	 * @param name, the name of the skin to swap to.
	 */
	public void onFormatSkin(String name) {

		final String skinName = name;

		Thread thread = new Thread("Skin") {
			public void run() {
				APP_PROPERTIES.setSkin(skinName);
				refreshIcons(true);
				// Lakshmi 3/24/06 - Refresh outline View Icons
				if(UIViewOutline.me != null){
					UIViewOutline.me.refreshTree();
				}
				oToolBarManager.swapToobarSkin();
			}
		};
		thread.start();
	}

	/**
	 * Update the icons in all the views.
	 * @param refreshFrameIcons, true to also refresh frame icons, else false.
	 */
	public void refreshIcons(boolean refreshFrameIcons) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (refreshFrameIcons)
				viewFrame.updateFrameIcon();

			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				mapFrame.refreshAerialIcons(refreshFrameIcons);

				UIViewPane viewPane = mapFrame.getViewPane();
				Component array[] = viewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

				UINode uinode = null;
				for (int j = 0; j < array.length; j++) {
					uinode = (UINode)array[j];
					int nType = uinode.getNode().getType();
					ImageIcon icon = null;
					NodeSummary node = (NodeSummary)uinode.getNode();

					if (nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
						String image  = node.getImage();
						if ( image != null && !image.equals(""))
							uinode.setReferenceIcon( image );
						else {
							uinode.setReferenceIcon( node.getSource() );
						}
					}
					else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
							nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
						String image  = node.getImage();
						if ( image != null && !image.equals(""))
							uinode.setReferenceIcon( image );
						else {
							icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
							uinode.refreshIcon( icon );
						}
					}
					else {
						icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
						uinode.refreshIcon( icon );
					}
					uinode.updateLinks();
				}
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}

	/**
	 * Update the icons for the given node id in all the views.
	 * @param sNodeID the id of the node to refresh the icons for.
	 */
	public void refreshIcons(String sNodeID) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				UIViewPane viewPane = mapFrame.getViewPane();
				Component array[] = viewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

				UINode uinode = null;
				for (int j = 0; j < array.length; j++) {
					uinode = (UINode)array[j];
					NodeSummary node = (NodeSummary)uinode.getNode();
					if (node.getId().equals(sNodeID)) {
						int nType = uinode.getNode().getType();
						ImageIcon icon = null;

						if (nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
							String image  = node.getImage();
							if ( image != null && !image.equals(""))
								uinode.setReferenceIcon( image );
							else {
								uinode.setReferenceIcon( node.getSource() );
							}
						}
						else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
								nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
							String image  = node.getImage();
							if ( image != null && !image.equals(""))
								uinode.setReferenceIcon( image );
							else {
								icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
								uinode.refreshIcon( icon );
							}
						}
						else {
							icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
							uinode.refreshIcon( icon );
						}
						uinode.updateLinks();
					}
				}
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}

	/**
	 * Update the icon indicator of a specific nodeID
	 * @param sNodeID, the id of the node to refresh the icon indicators for.
	 */
	public void refreshNodeIconIndicators(String sNodeID) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				mapFrame.refreshAerialNodeIconIndicators(sNodeID);

				UIViewPane viewPane = mapFrame.getViewPane();
				viewPane.refreshNodeIconIndicators(sNodeID);
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}

	/**
	 * Update the icon indicators in all the views.
	 */
	public void refreshIconIndicators() {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				mapFrame.refreshAerialIconIndicators();

				UIViewPane viewPane = mapFrame.getViewPane();
				viewPane.refreshIconIndicators();
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}

/******** FAVORITES MENU *********/

	/**
	 * Transclude the given favorite to the current view for old favorites, GoTo the favorite for new ones.
	 *
	 * @param fav the Favorite to process.
	 */
	public void addFavorite(Favorite fav) {

		String sViewID = fav.getViewID();
		if (sViewID == null || sViewID.equals("")) {
			String sNodeID = fav.getNodeID();
			UIViewFrame viewFrame = getCurrentFrame();
			UIViewPane viewpane = null;
			View view  = viewFrame.getView();
			if (viewFrame instanceof UIMapViewFrame) {
				viewpane = ((UIMapViewFrame)viewFrame).getViewPane();
			}

			NodeSummary favnode = null;

			// CHECK DELETED STATUS
			boolean isDeleted = false;
			try {
				if (oModel.getNodeService().isMarkedForDeletion(oModel.getSession(), sNodeID))
					isDeleted = true;

				if (isDeleted) {
					try {
						NodeSummary node = ((NodeService)oModel.getNodeService()).getDeletedNodeSummaryId(oModel.getSession(), sNodeID);
						restoreNode(node, viewFrame.getView());
						refreshIconIndicators();
					}
					catch(Exception io) {}
				}
				else {
					try {
						favnode = ((NodeService)oModel.getNodeService()).getNodeSummary(oModel.getSession(), sNodeID);
					}
					catch(Exception io) { return; }
					if (favnode == null)
						return;

					// CHECK TO SEE IF DELETED FROM THIS VIEW ALREADY
					NodePosition pos = oModel.getNodeService().restoreNodeView(oModel.getSession(), sNodeID, view.getId());
					if (pos != null) {
						restoreNode(favnode, viewFrame.getView());
						refreshIconIndicators();
					}
					else {
						if (viewpane != null) {

							int nX = (viewFrame.getWidth()/2)-60;
							int nY = (viewFrame.getHeight()/2)-60;

							// GET CURRENT SCROLL POSITION AND ADD THIS TO POSITIONING INFO
							int hPos = viewFrame.getHorizontalScrollBarPosition();
							int vPos = viewFrame.getVerticalScrollBarPosition();

							nX = nX + hPos;
							nY = nY + vPos;

							Object exists = viewpane.get(sNodeID);
							if (exists != null) {
								UINode uinode = (UINode) exists;
								viewpane.getViewPaneUI().createShortCutNode(uinode, nX, nY);
							}
							else {

								ViewPaneUI oViewPaneUI = viewpane.getViewPaneUI();
								UINode uinode = oViewPaneUI.addNodeToView(favnode, nX, nY);
								if (uinode != null) {
									uinode.setRollover(false);
									uinode.setSelected(true);
									viewpane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);
								}
							}
						}
						else {
							if (viewFrame instanceof UIListViewFrame) {

								UIList uiList = ((UIListViewFrame)viewFrame).getUIList();

								int nodeindex = uiList.getIndexOf(sNodeID);
								if (nodeindex != -1) {
									int[] indexes = {nodeindex};
									uiList.createShortCutNodes(indexes);
									uiList.updateTable();
									uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
								}
								else {
									int nY = (uiList.getNumberOfNodes() + 1) * 10;
										int nX = 0;

									try {
										NodePosition favpos = uiList.getView().addNodeToView(favnode, nX, nY);
										uiList.insertNode(favpos, uiList.getNumberOfNodes());
										uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
									}
									catch(Exception io) {}
								}
							}
						}
					}
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				displayError("Error with loading favorites\n\n"+ex.getMessage());
			}
		} else {
			String sNodeID = fav.getNodeID();
			UIUtilities.jumpToNode(sViewID, sNodeID, "Bookmark");
		}
	}

	/**
	 * Create a new favorite with the given node is, label and node type.
	 * @param sNodeID the id of the node to create a favorite for.
	 * @param sViewID the id of the view to create a favorite for.
	 * @param sLabel the label for th favorite node.
	 * @param nType the node type of the favorite node.
	 */
	public void createFavorite(String sNodeID, String sViewID, String sLabel, int nType) {

		String sUserID = oModel.getUserProfile().getId();
		FavoriteService favserv = (FavoriteService)oModel.getFavoriteService();

		Vector favorites = null;
		try { favorites = favserv.getFavorites(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		if (favorites != null && favorites.size() > 0) {
			int count = favorites.size();
			Favorite fav = null;
			String viewID = "";
			String nodeID = "";
			for (int i=0; i< count; i++) {
				fav = (Favorite)favorites.elementAt(i);
				nodeID = fav.getNodeID();
				viewID = fav.getViewID();

				if (viewID != null && !viewID.equals("")) {
					if (nodeID.equals(sNodeID) && viewID.equals(sViewID)) {
						return;
					}
				}
			}
		}

		try {
			((FavoriteService)oModel.getFavoriteService()).createFavorite(oModel.getSession(), sUserID, sNodeID, sViewID, sLabel, nType);
		}
		catch(Exception io) {
			io.printStackTrace();
		}

		refreshFavoritesMenu();
	}

	/**
	 * Delete the favorites with the given node ids.
	 * @param vtFavorites the list of Favorites to delete.
	 */
	public void deleteFavorites(Vector vtFavorites) {

		String sUserID = oModel.getUserProfile().getId();
		try {
			((FavoriteService)oModel.getFavoriteService()).deleteFavorites(oModel.getSession(), sUserID, vtFavorites);
		}
		catch(Exception ex) {
			System.out.println("Problem deleting favorites due to:\n\n"+ex.getMessage());
		}

		refreshFavoritesMenu();
	}

	/**
	 * Refersh the items on the Favorites menu from the database.
	 */
	public void refreshFavoritesMenu() {

		String sUserID = oModel.getUserProfile().getId();
		FavoriteService favserv = (FavoriteService)oModel.getFavoriteService();

		Vector favorites = null;
		try { favorites = favserv.getFavorites(oModel.getSession(), sUserID); }
		catch(Exception io) {
			io.printStackTrace();
		}

		oMenuManager.refreshFavoritesMenu(favorites);
	}

	/**
	 * Open the Favorites Maintenance dialog.
	 */
	public void onFavoriteMaintenace() {
		UIFavoriteDialog fav = new UIFavoriteDialog(this, oModel.getUserProfile().getId(), oModel);
		fav.setVisible(true);
	}

/******** WORKSPACE MENU *********/

	/**
	 * Load the views for the workspace with the given id, for the given user.
	 * @param sWorkspaceID, the id of the workspace to add.
	 * @param sUserID, the id of the user to add it for.
	 */
	public void addWorkspace(String sWorkspaceID, String sUserID) {

		UIViewFrame homeFrame = getInternalFrame(oHomeView);
		String sHomeViewID = homeFrame.getView().getId();
		// NEED TO DO THIS, OR THEY WILL ALL BE EXPANDED
		if (homeFrame.isMaximum()) {
			try {homeFrame.setMaximum(false);
			}catch(Exception ex){}
		}

		boolean frameFound = false; int i=0;

		// CLOSE ALL FRAMES EXCEPT HOME
		JInternalFrame[] frames = oDesktop.getAllFrames();
		while(!frameFound && i<frames.length) {
			UIViewFrame viewFrame = (UIViewFrame)frames[i++];
			if (!viewFrame.equals(homeFrame)) {
				oDesktop.getDesktopManager().closeFrame(viewFrame);
				oDesktop.remove(viewFrame);
				viewFrame.dispose();
			}
		}

		oToolBarManager.clearHistory();

		validate();
		repaint();

		// LOAD GIVEN WORKSPACE
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();
		String userID = oModel.getUserProfile().getId();
		Vector workspace = null;
		Vector views = new Vector(51);
		int countk=0;
		try {
			workspace = workserv.getWorkspaceViews(oModel.getSession(), sWorkspaceID);
			Enumeration eviews = ProjectCompendium.APP.getModel().getNodeService().getAllActiveViews(ProjectCompendium.APP.getModel().getSession());
			for(Enumeration e = eviews; e.hasMoreElements();) {
				View view = (View)e.nextElement();
				views.addElement(view);
			}
			countk = views.size();
		}
		catch(Exception io) {}

		// ADD WORKSPACE VIEWS TO DESKTOP
		if (countk > 0 && workspace != null && workspace.size() > 0) {
			int count = workspace.size();
			for (int j=0; j <count; j++) {

				WorkspaceView work = (WorkspaceView)workspace.elementAt(j);
				String sViewID = work.getViewID();

				int width = work.getWidth();
				int height = work.getHeight();
				int xPos = work.getXPosition();
				int yPos = work.getYPosition();
				boolean isIcon = work.getIsIcon();
				boolean isMaximum = work.getIsMaximum();
				int HScroll = work.getHorizontalScrollBarPosition();
				int VScroll = work.getVerticalScrollBarPosition();

				if (sViewID.equals(sHomeViewID)) {
					homeFrame.setBounds(xPos, yPos, width, height);
					homeFrame.setHorizontalScrollBarPosition(HScroll, true);
					homeFrame.setVerticalScrollBarPosition(VScroll, true);
					try {
						homeFrame.setIcon(isIcon);
						homeFrame.setMaximum(isMaximum);
					}catch(Exception ex){}
					((UIMapViewFrame)homeFrame).setSelected(true);
					oDesktop.moveToFront(homeFrame);
				} else {
					for(int k=0; k<countk; k++) {
						View view = (View)views.elementAt(k);
						if (view.getId().equals(sViewID)) {
							UIViewFrame oViewFrame = addViewToDesktop(view, view.getLabel(), width, height, xPos, yPos, isIcon, isMaximum, HScroll, VScroll);
							Vector history = new Vector();
							history.addElement(new String("Workspace"));
							oViewFrame.setNavigationHistory(history);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Create a new workspace withe the given name and conatining the currently open and positioned views.
	 * @param sName, the name of the new workspace.
	 */
	public boolean createWorkspace(String sName) {

		String sUserID = oModel.getUserProfile().getId();
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();

		Vector workspaces = null;
		try { workspaces = workserv.getWorkspaces(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		boolean editing = false;
		String sWorkspaceID = "";

		if (workspaces != null && workspaces.size() > 0) {
			int count = workspaces.size();

			for (int i=0; i< count; i++) {
				Vector next = (Vector)workspaces.elementAt(i);
				String name = (String)next.elementAt(1);
				if (name.equals(sName)) {
					int response = JOptionPane.showConfirmDialog(this, "You have already have a workspace with this name\n\nDo you wish to update it with the current views?",
														"Create Workspace", JOptionPane.YES_NO_OPTION);

					if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION)
						return false;

					editing = true;
					sWorkspaceID = (String)next.elementAt(0);
				}
			}
		}

		if (!editing)
			sWorkspaceID = oModel.getUniqueID();

		try {
			JInternalFrame[] frames = oDesktop.getAllFrames();
			Vector vtWorkspace = new Vector(51);
			UIViewFrame homeFrame = getInternalFrame(oHomeView);

			for (int i=0; i < frames.length; i++) {
				UIViewFrame frame = (UIViewFrame)frames[i];

				WorkspaceView view = new WorkspaceView();
				view.setWorkspaceID(sWorkspaceID);
				view.setViewID(frame.getView().getId());
				view.setWidth(frame.getWidth());
				view.setHeight(frame.getHeight());
				view.setXPosition(frame.getX());
				view.setYPosition(frame.getY());
				view.setIsIcon(frame.isIcon());
				view.setIsMaximum(frame.isMaximum());
				view.setHorizontalScrollBarPosition(view.getHorizontalScrollBarPosition());
				view.setVerticalScrollBarPosition(view.getVerticalScrollBarPosition());

				vtWorkspace.addElement(view);
			}

			if (vtWorkspace.isEmpty()) {
				displayError("There are no active views to save");
				return false;
			}

			Workspace workspace = new Workspace(sWorkspaceID, sName, sUserID, vtWorkspace);
			workspace.initialize(oModel.getSession(), oModel);
			workspace.saveWorkspace(editing, sUserID);

			if (!editing)
				refreshWorkspaceMenu();
		}
		catch(Exception io) {
			ProjectCompendium.APP.displayError("Exception when saving Workspace: "+sName+"\n\n"+io.getMessage());
		}

		return true;
	}

	/**
	 * Update the workspace with the given id and name to hold the currently open views.
	 * @param sWorkspaceID, the id of the workspace to update.
	 * @param sName, the name of the workspace.
	 */
	public boolean updateWorkspace(String sWorkspaceID, String sName) {

		String sUserID = oModel.getUserProfile().getId();
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();

		Vector workspaces = null;
		try { workspaces = workserv.getWorkspaces(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		boolean editing = false;

		try {
			JInternalFrame[] frames = oDesktop.getAllFrames();
			Vector vtWorkspace = new Vector(51);
			UIViewFrame homeFrame = getInternalFrame(oHomeView);

			for (int i=0; i < frames.length; i++) {
				UIViewFrame frame = (UIViewFrame)frames[i];

				WorkspaceView view = new WorkspaceView();
				view.setWorkspaceID(sWorkspaceID);
				view.setViewID(frame.getView().getId());
				view.setWidth(frame.getWidth());
				view.setHeight(frame.getHeight());
				view.setXPosition(frame.getX());
				view.setYPosition(frame.getY());
				view.setIsIcon(frame.isIcon());
				view.setIsMaximum(frame.isMaximum());
				view.setHorizontalScrollBarPosition(view.getHorizontalScrollBarPosition());
				view.setVerticalScrollBarPosition(view.getVerticalScrollBarPosition());

				vtWorkspace.addElement(view);
			}
			Workspace workspace = new Workspace(sWorkspaceID, sName, sUserID, vtWorkspace);
			workspace.initialize(oModel.getSession(), oModel);

			if (vtWorkspace.isEmpty()) {
				displayError("There are no active views to save");
				return false;
			}
			else {
				workspace.saveWorkspace(true, sUserID);
			}
		}
		catch(Exception io) {
			ProjectCompendium.APP.displayError("Exception when saving Workspace: "+sName+"\n\n"+io.getMessage());
		}

		return true;
	}

	/**
	 * Delete the workspace with the given id from the database.
	 * @param sWorkspaceID, the id of the workspace to delete.
	 */
	public void deleteWorkspaces(String sWorkspaceIDs) {

		String sUserID = oModel.getUserProfile().getId();
		try {
			((WorkspaceService)oModel.getWorkspaceService()).deleteWorkspaces(oModel.getSession(), sUserID, sWorkspaceIDs);
		}
		catch(Exception io) { }

		refreshWorkspaceMenu();
	}

	/**
	 * Load the current users workspaces into the Workspace menu.
	 */
	public void refreshWorkspaceMenu() {

		final String sUserID = oModel.getUserProfile().getId();
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();

		Vector workspaces = null;

		try { workspaces = workserv.getWorkspaces(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		oMenuManager.refreshWorkspaceMenu(workspaces, sUserID);
	}


	/**
	 * Open the Workspace Maintenance dialog.
	 */
	public void onWorkspaceMaintenace() {
		UIWorkspaceDialog work = new UIWorkspaceDialog(ProjectCompendium.APP, oModel.getUserProfile().getId(), oModel);
		work.setVisible(true);
	}

/******** TOOLS MENU *********/

	/**
	 * Opens the User management dialog.
	 */
	public void onUsers() {

		UIUserManagerDialog dialog = new UIUserManagerDialog(this);
		dialog.setVisible(true);
	}

	/**
	 * Open the code (tag) maintenance dialog.
	 */
	public void onCodes() {
		oMenuManager.addTagsView(true);
	}

	/**
	 * Toggle the code (tag) view
	 */
	public void toggleCodes() {
		oMenuManager.toggleTagView();
	}

	/**
	 * Show all the code information for the current map.
	 */
	public void onShowCodes() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.showCodes();
		}
		else {
			displayError("No active maps");
		}
	}

	/**
	 * Hide all the code information for the current map.
	 */
	public void onHideCodes() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.hideCodes();
		}
		else {
			displayError("No active maps");
		}
	}

	/**
	 * Add the given code to the currently selected nodes in the current view.
	 * @param code com.compendium.core.Code, the code to add.
	 */
	public void addCode( Code code ) {

		UIViewFrame viewFrame = getCurrentFrame();

		int numSelected = 0;
		Enumeration nodes = null;
		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
			if (pane != null ) {
				numSelected = pane.getNumberOfSelectedNodes();
				nodes = pane.getSelectedNodes();
			}
		}
		else {
			UIList uilist = ((UIListViewFrame)viewFrame).getUIList();
			if (uilist != null ) {
				numSelected = uilist.getNumberOfSelectedNodes();
				nodes = uilist.getSelectedNodes();
			}
		}

		if (numSelected <= 0) {
			displayError("Please select one or more nodes first");
		}
		else {
			Object obj = null;
			NodeSummary node = null;
			for(Enumeration e = nodes; e.hasMoreElements();) {

				node = null;
				obj = e.nextElement();
				if (obj instanceof UINode) {
					UINode uinode = (UINode)obj;
					node = uinode.getNode();
				} else {
					NodePosition pos = (NodePosition)obj;
					node = pos.getNode();
				}
				if (node != null) {
					// Can't add tags to the Trashbin or the Inbox.
					if (!node.getId().equals(this.getInBoxID()) && !node.getId().equals(this.getTrashBinID())) {
						try {
							node.addCode(code);

							// IF WE ARE RECORDING or REPLAYING A MEETING, RECORD A TAG ADDED EVENT.
							if (ProjectCompendium.APP.oMeetingManager != null
										&& ProjectCompendium.APP.oMeetingManager.captureEvents()) {

								View view  = viewFrame.getView();
								ProjectCompendium.APP.oMeetingManager.addEvent(
										new MeetingEvent(oMeetingManager.getMeetingID(),
														 oMeetingManager.isReplay(),
														 MeetingEvent.TAG_ADDED_EVENT,
														 view,
														 node,
														 code));
							}

							// REFRESH TAGS WORKING AREA.
							oMenuManager.setNodeSelected(true);
						}
						catch(Exception ex) {
							displayError("Error: (ProjectCompendiumFrame.addCode)\n\n"+ex.getMessage());
							break;
						}
					}
				}
			}
		}
	}

// SCRIBBLE PAD

	/**
	 * Show the scribble pad for the current map.
	 */
	public void onShowScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			oToolBarManager.onToggleScribble();
		}
		else {
			displayError("No active maps");
		}
	}

	/**
	 * Hide the scribble pad for the current map.
	 */
	public void onHideScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			oToolBarManager.onToggleScribble();
		}
		else {
			displayError("No active maps");
		}
	}

	public boolean isScribblePadOn()
	{
			return oToolBarManager.isScribblePadOn();
	}

	/**
	 * Clear the scribble pad for the current map.
	 */
	public void onClearScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.clearScribblePad();
		}
		else {
			displayError("No active maps");
		}
	}

	/**
	 * Save the scribble pad contents.
	 */
	public void onSaveScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.saveScribblePad();
		}
		else {
			displayError("No active maps");
		}
	}

//*************** HELP MENU **************//

	/**
	 * Displays the about dialog.
	 */
	public void onHelpAbout() {
		if (oAboutDialog != null) {
			oAboutDialog.setVisible(false);
			oAboutDialog.dispose();
		}

		oAboutDialog = new UIAboutDialog(this);
		UIUtilities.centerComponent(oAboutDialog, this);
		oAboutDialog.setVisible(true);
		getAudioPlayer().playAudio(UIAudio.ABOUT_ACTION);
	}

//**************** END MENU FUNCTIONS ***************//

	/**
	 * Re-initializes the system when a database project is closed.
	 */
	public void onFileClose() {

		if(oDesktop != null) {

			// close all internal frames
			JInternalFrame[] frames = oDesktop.getAllFrames();
			for (int i = 0; i < frames.length; i++) {
				UIViewFrame viewframe = (UIViewFrame)frames[i];

				// SAVE THE CURRENT PROPERTIES OF EACH OPEN FRAME
				saveViewProperties(viewframe);

				oDesktop.getDesktopManager().closeFrame(viewframe);
				oDesktop.remove(viewframe);

				// CLEAN UP FOR MEMORY USAGE
				viewframe.cleanUp();
				if (viewframe instanceof UIMapViewFrame) {
					UIViewPane pane = ((UIMapViewFrame)viewframe).getViewPane();
					if (pane != null)
						pane.cleanUp();
				}
				viewframe.dispose();
			}

			oServiceManager.cleanUp();

			NodeSummary.clearList();
			oTrashbinNode = null;
			oInboxNode = null;

			// update menu
			oToolBarManager.onDatabaseClose();
		}

		// disable menu items
		oMenuManager.onDatabaseClose();

		refreshWindowsMenu();

		setPasteEnabled(false);
		if (externalCopy)
			oMenuManager.setExternalPasteEnablement(true);

		if (oModel != null) {
			oModel.cleanUp();
			System.out.println("5514 ProjectCompendiumFrame setting oModel to null");
			oModel= null;
		}

		if (APP_PROPERTIES.getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			if (oCurrentMySQLConnection != null)
				setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), oCurrentMySQLConnection.getProfile(), "");
			else
				setTitle(ICoreConstants.MYSQL_DATABASE, "", "", "");
		}
		else {
			setDerbyTitle("");
		}

		if (oUDigCommunicationManager != null) {
			oUDigCommunicationManager.closeProject();
		}

		setDefaultCursor();
	}

	/**
	 * Removes a view from the views list and history, and if open, closes it.
	 * @param view com.compendium.core.datamodel.View, the view to remove the frame for.
	 */
	public boolean removeViewFromHistory(View view) {

		oToolBarManager.removeFromHistory(view);

		UIViewFrame viewFrameCheck = null;
		UIViewFrame viewFrame = null;

		int count = viewFrameList.size();
		for (int j = 0; j < count; j++) {
			viewFrameCheck = (UIViewFrame)viewFrameList.elementAt(j);
			if (viewFrameCheck.getView().getId().equals(view.getId())) {
				viewFrameList.removeElementAt(j);
				JInternalFrame[] frames = oDesktop.getAllFrames();
				for (int i = 0; i < frames.length; i++) {
					viewFrame = (UIViewFrame)frames[i];

					if (viewFrame.getView().getId().equals(view.getId())) {
						oDesktop.getDesktopManager().closeFrame(frames[i]);
						oDesktop.remove(frames[i]);
						frames[i].dispose();
						refreshWindowsMenu();
					}
				}

				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a view from the desktop by closing an internal frame with the contents of the view.
	 * @param view com.compendium.core.datamodel.View, the view to remove the frame for.
	 */
	public boolean removeView(View view) {

		UIViewFrame viewFrame = null;
		UIViewFrame viewFrameCheck = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];

			if (viewFrame.getView().getId().equals(view.getId())) {
				oDesktop.getDesktopManager().closeFrame(frames[i]);
				oDesktop.remove(frames[i]);
				frames[i].dispose();
				refreshWindowsMenu();

				// refresh the opened viewFrame list
				int count = viewFrameList.size();
				for (int j = 0; j < count; j++) {
					viewFrameCheck = (UIViewFrame)viewFrameList.elementAt(j);
					if (viewFrameCheck.getView().getId().equals(view.getId())) {
						viewFrameList.removeElementAt(j);
						viewFrameList.addElement(viewFrame);
						j=count;
					}
				}

				return true;
			}
		}
		return false;
	}

//*********** GETTERS/SETTERS ***********//

	/**
	 * Get the list of opened views.
	 * @return Vector, the list of opened views.
	 */
	public Vector getOpenedViews() {
		return viewFrameList;
	}

	/**
	 * Returns the screen size.
	 * @return Dimension, the size of the current user screen.
	 */
	public Dimension getScreenSize() {
		Dimension dim = getSize();
		nScreenWidth = dim.width;
		nScreenHeight = dim.height;
		return dim;
	}

	/**
	 * Returns the active model.
	 * @return com.compendium.core.datamodel.IModel, the model for the currently open database project.
	 */
	public IModel getModel() {
		return oModel;
	}

	/**
	 * Gets the current home view.
	 * @return com.compendium.core.datamodel.View, the current home view for the current user.
	 */
	public View getHomeView() {
		return oHomeView;
	}

	/**
	 * Gets the current inbox view.
	 * @return com.compendium.core.datamodel.View the current inbox view for the current user.
	 */
	public View getInBoxView() {
		return (View)oInboxNode;
	}

	/**
	 * Returns the service manager.
	 * @param ServiceManager, the service manager for this session.
	 */
	public IServiceManager getServiceManager() {
		return oServiceManager;
	}

	/**
	 * Returns the content pane.
	 * @param Container, the content pane for the frame contents.
	 */
	public Container getContentPane() {
		return oContentPane;
	}

	/**
	 * Returns the main panel.
	 * @param JPanel, the main panel for the frame contents.
	 */
	public JPanel getMainPanel() {
		return oMainPanel;
	}

	/**
	 * Returns the menu manager.
	 * @return UIMenuManaager, the menu manager being used by this frame.
	 */
	public UIMenuManager getMenuManager() {
		return oMenuManager;
	}

	/**
	 * Returns the toolbar manager.
	 * @return UIToolBarManaager, the toolbar manager being used by this frame.
	 */
	public UIToolBarManager getToolBarManager() {
		return oToolBarManager;
	}

	/**
	 * Returns the desktop.
	 * @return JDesktop, the desktop being used by this frame.
	 */
	public JDesktopPane getDesktop() {
		return oDesktop;
	}

	/**
	 * Routine to get the Home Window of the user from the database.
	 */
	public void setNodesAndLinks() {

		setWaitCursor();

		oHomeView = null;

		if(oModel == null) {
			JOptionPane oOptionPane = new JOptionPane("Please exit Compendium and Login again! ");
			JDialog oDialog = oOptionPane.createDialog(oContentPane,"Login Information..");
			UIUtilities.centerComponent(oDialog, this);
			oDialog.setModal(true);
			oDialog.setVisible(true);
			return;
		}

		UserProfile up = oModel.getUserProfile();
		up.initialize(oModel.getSession(),oModel);

		//Get the users homeview
		oHomeView = up.getHomeView();
		Date date = new Date();
		String userName = up.getUserName();

		//if no home view try and create one
		if(oHomeView == null) {
			try {
				oHomeView = (View)oModel.getNodeService().createNode(oModel.getSession(),
																	oModel.getUniqueID(),
																	ICoreConstants.MAPVIEW,
																	"",
																	"",
																	ICoreConstants.WRITEVIEWNODE,
																	ICoreConstants.READSTATE,
																	userName,
																	"Home Window",
																	"Home Window of " + userName,
																	date,
																	date
																	);

				IModel model = oModel;
				PCSession session = oModel.getSession();
				String author = userName;
				Date creationDate = date;
				Date modificationDate = creationDate;
				String description = "No Description";
				String behavior = "No Behavior";
				String name = userName;
				String codeId = oModel.getUniqueID();

				//add to the DB
				Code code = model.getCodeService().createCode(session, codeId, author, creationDate, modificationDate, name, description, behavior);
				model.addCode(code);
				up.setHomeView(oHomeView);
			}
			catch (Exception e) {
				displayError("Error: (ProjectCompendiumFrame.setNodesAndLinks)\n\n"+e.getMessage());
				return;
			}
		}

		// If the user does not have a linkview add one
		// This will probably only happen the first time after people update the new database scheme.
		// but does not harm anything to leave this check in.
		oInboxNode = createInBox(up);

		try {
			// Make sure model updated.
			oModel.loadUsers();

			//remove old trashbin
			if(oTrashbinNode != null) {
				oHomeView.removeMemberNode(oTrashbinNode);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception: (ProjectCompendiumFrame.setNodesAndLinks-1)\n\n"+ex.getMessage());
		}

		//if home view exists then register with client event
		oHomeView.initialize(oModel.getSession(), oModel);
		try {
			oHomeView.initializeMembers();
		}
		catch(Exception ex) {
			System.out.println("Exception: (ProjectCompendiumFrame.setNodesAndLinks-2)\n\n"+ex.getMessage());
		}

		String sTrashbinId = oModel.getUniqueID();

// Lakshmi (4/21/06 ) - State of Trash bin? - Default read State.
		oTrashbinNode = NodeSummary.getNodeSummary(sTrashbinId, ICoreConstants.TRASHBIN, "", sTrashbinId , ICoreConstants.READSTATE, oModel.getUserProfile().getUserName(),
				    						date, date, "Trash Bin", "");

		NodePosition pos = oHomeView.addMemberNode(new NodePosition(oHomeView, oTrashbinNode,
				15, 5, date, date, Model.SHOW_TAGS_DEFAULT, Model.SHOW_TEXT_DEFAULT,
				Model.SHOW_TRANS_DEFAULT, Model.SHOW_WEIGHT_DEFAULT, Model.SMALL_ICONS_DEFAULT,
				Model.HIDE_ICONS_DEFAULT, Model.LABEL_WRAP_WIDTH_DEFAULT, Model.FONTSIZE_DEFAULT,
				Model.FONTFACE_DEFAULT,	Model.FONTSTYLE_DEFAULT, Model.FOREGROUND_DEFAULT.getRGB(),
				Model.BACKGROUND_DEFAULT.getRGB()));

		pos.initialize(oModel.getSession(), oModel); // Need this to set font and wrap width

		// Begin Edit - Lakshmi 5/15/06
		// By Default make home window as read
		try {
			if (oHomeView.getState() != ICoreConstants.READSTATE) {
				oModel.getNodeService().setState(oModel.getSession(), oHomeView.getId(),
						ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE, new Date());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//End edit

		oTrashbinNode.initialize(oModel.getSession(), oModel);

		// add this view to the desktop
		UIViewFrame viewFrame = addViewToDesktop(oHomeView, "  " +oModel.getUserProfile().getUserName() + "\'s " + oHomeView.getLabel());

		// SOMETIMES FAILS TO DISPLAY
		oDesktop.moveToFront(viewFrame);
		viewFrame.setVisible(true);

		// Lakshmi (10/18/06) - set the initial state of inbox.
		boolean isModified = false;
		View inbox = getInBoxView();
		try {
			inbox.initialize(oModel.getSession(), oModel);
			inbox.initializeMembers();
			Vector nodes = inbox.getMemberNodes();
			for(int i = 0; i < nodes.size(); i ++){
				NodeSummary node = (NodeSummary) nodes.get(i);
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					isModified = true;
					break ;
				}
			}
			if(isModified) {
				getInBoxView().setState(ICoreConstants.MODIFIEDSTATE);
			} else {
				getInBoxView().setState(ICoreConstants.READSTATE);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		//End edit
		setDefaultCursor();
	}

	/**
	 * Create an InBox for the given user Profile.
	 * @param up the user profile to create an inbox for
	 * @return
	 */
	public View createInBox(UserProfile up) {

		up.initialize(oModel.getSession(), oModel);

		String sLinkViewID = "";
		Date date = new Date();
		View oInboxNode = up.getLinkView();
 		if (oInboxNode == null) {
 			String userName = up.getUserName();
 	 		Model model = (Model)oModel;
			try {
				sLinkViewID = oModel.getUniqueID();
				oInboxNode = (View)oModel.getNodeService().createNode(oModel.getSession(),
						sLinkViewID,
						ICoreConstants.LISTVIEW,
						"",
						"",
						ICoreConstants.WRITEVIEWNODE,
						ICoreConstants.READSTATE,
						userName,
						"Inbox",
						"Inbox of " + userName,
						date,
						date
						);

				oInboxNode.initialize(oModel.getSession(), oModel);
		  		IViewService vs = oModel.getViewService();

				NodePosition oLinkPos = vs.addMemberNode(oModel.getSession(), up.getHomeView(),
						(NodeSummary)oInboxNode,
						0, 75, date, date, false, false, false, true, false, false,
						Model.LABEL_WRAP_WIDTH_DEFAULT, Model.FONTSIZE_DEFAULT,
						Model.FONTFACE_DEFAULT,	Model.FONTSTYLE_DEFAULT, Model.FOREGROUND_DEFAULT.getRGB(),
						Model.BACKGROUND_DEFAULT.getRGB());
				oLinkPos.initialize(oModel.getSession(), oModel);
				oInboxNode.setSource("", CoreUtilities.unixPath(UIImages.getPathString(IUIConstants.INBOX)), userName);
				oInboxNode.setState(ICoreConstants.READSTATE);
				up.setLinkView((View)oInboxNode);
			} catch (Exception e) {
				e.printStackTrace();
				displayError("(ProjectCompendiumFrame.createInBox - adding inbox)\n\n"+e.getMessage());
			}
		} else {
			try {
				if (oInboxNode.getState() != ICoreConstants.READSTATE) {
					oInboxNode.initialize(oModel.getSession(), oModel);
					oInboxNode.setState(ICoreConstants.READSTATE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

 		return oInboxNode;
	}

	/**
	 * Set the trashbin icon depending upon if there any deleted objects in the database
	 */
	public ImageIcon setTrashBinIcon() {

		ImageIcon img = null;

		try {
			String userID = oModel.getUserProfile().getId();
			PCSession session = oModel.getSession();
			int iDeletedNodeCount = oModel.getNodeService().iGetDeletedNodeCount(session);

			UIViewFrame homeFrame = getInternalFrame(oHomeView);
			if (homeFrame != null) {
				UIViewPane pane = ((UIMapViewFrame)homeFrame).getViewPane();
				if (pane != null) {
					UINode trashbin = (UINode) pane.get(oTrashbinNode.getId());
					if (trashbin != null) {
						if(iDeletedNodeCount > 0) {
							img = UIImages.getNodeIcon(IUIConstants.TRASHBINFULL_ICON);
							trashbin.setIcon(img);
						}
						else {
							img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON);
							trashbin.setIcon(img);
						}
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		return img;
	}

	/**
	 * Return the id of the trashbin node.
	 * @return String the id of the trashbin node, or null if node not set.
	 */
	public String getTrashBinID() {
		String sID = null;
		if (oTrashbinNode != null) {
			sID = oTrashbinNode.getId();
		}
		return sID;
	}

	/**
	 * Return the id of the inbox node.
	 * @return String the id of the inbox node, or null if node not set.
	 */
	public String getInBoxID() {
		String sID = null;
		if (oInboxNode != null) {
			sID = oInboxNode.getId();
		}
		return sID;
	}

	/**
	 * Set the trashbin icon to empty.
	 */
	public void setTrashBinEmptyIcon() {

		//set the trashbin icon to be empty
		UIViewFrame homeFrame = getInternalFrame(getHomeView());
		UINode trashbin = (UINode) ((UIMapViewFrame)homeFrame).getViewPane().get(oTrashbinNode.getId());

		trashbin.setIcon(UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON));
	}

	/**
	 * Set the trashbin icon to full.
	 */
	public void setTrashBinFullIcon() {

		//set the trashbin icon to be full since a node was deleted
		UIViewFrame homeFrame = getInternalFrame(getHomeView());
		UINode trashbin = (UINode) ((UIMapViewFrame)homeFrame).getViewPane().get(oTrashbinNode.getId());

		trashbin.setIcon(UIImages.getNodeIcon(IUIConstants.TRASHBINFULL_ICON));
	}

	/**
	 * Return the UIViewFrame for the given view.
	 * If not found, crate one.
	 * @param view com.compendium.core.datamode.View, the view to return the frame for.
	 * @param title, the title of the frame.
	 * @return com.compendium.ui.UIViewFrame, the frame for the given view.
	 */
	public UIViewFrame getViewFrame(View view, String title) {

		UIViewFrame viewFrame = null;
		boolean frameFound = false;
		String userID = oModel.getUserProfile().getId();

		for (int i = 0; i < viewFrameList.size(); i++) {
			viewFrame = (UIViewFrame)viewFrameList.elementAt(i);
			if (viewFrame.getView().getId().equals(view.getId())) {

				frameFound = true;
				return viewFrame;
			}
		}

		if(!frameFound) {
			if (view.getModel() == null) {
				view.initialize(oModel.getSession(), oModel);
			}
			try {
				view.initializeMembers();
			}
			catch(Exception ex) {
				System.out.println("Error (ProjectCompendiumFrame.getViewFrame) \n\n"+ex.getMessage());
			}

			UIMapViewFrame mapFrame = null;
			if(view.getType() == ICoreConstants.MAPVIEW) {
				try {
					mapFrame = new UIMapViewFrame(view, title);
					if (view.equals(oHomeView)) {
						mapFrame.setClosable(false);
					}
				}
				catch(Exception ex) {
					displayError("Cannot instantiate MapView Frame"+ ex.getMessage());
				}

				// add frame
				viewFrameList.addElement(mapFrame);
				viewFrame = mapFrame;
			}
			else if(view.getType() == ICoreConstants.LISTVIEW) {

				// invoke the view frame
				UIListViewFrame listFrame = null;
				try {
					listFrame = new UIListViewFrame(view,title);
					if (view.equals(oHomeView))
						listFrame.setClosable(false);
				}
				catch(Exception ex) {
					displayError("Cannot instantiate ListView Frame" +ex.getMessage());
				}

				// add frame
				viewFrameList.addElement(listFrame);
				viewFrame = (UIViewFrame)listFrame;
			}
		}

		return viewFrame;
	}


	//*********** END GETTER/SETTERS ***************//


	/**
	 * Check to see if a project is currently open before conituing with some earlier process.
	 * If a project is open, tell the suer thier chosen option requires all projects to be closed and
	 * ask the user if they would like to close it before proceeding.
	 * @return boolean, true if a project is still open, else false;
	 */
	private boolean isProjectOpen(String sMessage) {

		if (oModel != null) {
   			int answer = JOptionPane.showConfirmDialog(this, "You need to close your current project to use this option.\n\nWould you like us to close it for you and proceed?\n\n", sMessage,
   						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.YES_OPTION) {
				onFileClose();
			}
			else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check to see if a project is currently open before continuing with some earlier process.
	 * If a project is closed, tell the user their chosen option requires an open project.
	 * @return boolean, true if a project is open, else false;
	 */
	private boolean isProjectOpen2() {

		if (oModel == null) {
   			JOptionPane.showMessageDialog(this, "This operation requires you to have a project open.\nPlease open a project and try again.",
   					"Open project required...", JOptionPane.INFORMATION_MESSAGE);
   			return false;
		} else {
			return true;
		}
	}

	/**
	 * Add a new frame to the desktop if required for the given view and return.
	 * Load the frame properties to apply.
	 *
	 * @param view com.compendium.core.datamodel.View, the view to add.
	 * @return UIViewFrame, the frame for the given view.
	 */
	public UIViewFrame addViewToDesktop(View view, String title) {

		UIViewFrame viewFrame = null;
		JInternalFrame[] frames = oDesktop.getAllFrames();

		// CHECK IF VIEW ALREADY OPEN
		for (int i = 0; i < frames.length; i++) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame.getView().getId().equals(view.getId())) {
				try {
					if (viewFrame.isIcon())
						viewFrame.setIcon(false);
				}
				catch(Exception ex) {
					displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop) \n"+ex.getMessage());
					//System.out.println("Exception: (ProjectCompendiumFrame.addViewToDesktop) \n"+ex.getMessage());
					//ex.printStackTrace();
				}

				oDesktop.moveToFront(viewFrame);
				if(!viewFrame.isSelected()){
					if (viewFrame instanceof UIMapViewFrame)
						((UIMapViewFrame)viewFrame).setSelected(true);
					else
						((UIListViewFrame)viewFrame).setSelected(true);
				}
				return viewFrame;
			}
		}

		// TRY AND GET VIEW PROPERTIES
		int width = FRWIDTH;
		int height = FRHEIGHT;
		int xPos = FROFFSET * frames.length;
		int yPos = FROFFSET * frames.length;
		boolean isIcon = false;
		boolean isMaximum = false;
		int nHScroll = 0;
		int nVScroll = 0;

		// SBS wanted lists half height of map
		if (view.getType() == ICoreConstants.LISTVIEW)
			height = height/2;

		ViewProperty properties = restoreViewProperties(view);

		if (properties != null) {
			nHScroll = properties.getHorizontalScrollBarPosition();
			nVScroll = properties.getVerticalScrollBarPosition();
			width = properties.getWidth();
			height = properties.getHeight();
			xPos = properties.getXPosition();
			yPos = properties.getYPosition();
			isIcon = properties.getIsIcon();
			isMaximum = properties.getIsMaximum();
		}

		try {
			view.setState(ICoreConstants.READSTATE);
		}  catch(Exception ex) {}


		return addViewToDesktop(view, title, width, height, xPos, yPos, isIcon, isMaximum, nHScroll, nVScroll);
	}

	/**
	 * Add a new frame to the desktop for the given view and with the given frame properties.
	 *
	 * @param view com.compendium.core.datamodel.View, the view to add.
	 * @param width, the width to make the frame.
	 * @param height, the height to make the frame.
	 * @param xPos, the x position for the frame.
	 * @param yPos, the y position for the frame.
	 * @param isIcon, whether the frame should be iconified.
	 * @param isMaximum, whether the frame should be maximized.
	 * @param HScroll, the hosrizontal scroll bar position to set for the frame.
	 * @param VScroll the vertical scroll bar position to set for the frame.
	 * @return UIViewFrame, the frame for the given view.
	 */
	public UIViewFrame addViewToDesktop(View view, String title, int width, int height, int xPos, int yPos, boolean isIcon, boolean isMaximum, int HScroll, int VScroll) {

		UIViewFrame viewFrame = null;
		boolean frameFound = false;
		boolean wasIcon = false;
		String userID = oModel.getUserProfile().getId();

		// CHECK IF VIEW ALREADY OPEN
		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			if ( (viewFrame.getView().getId()).equals(view.getId()) ) {
				try {
					if (viewFrame.isIcon())
						viewFrame.setIcon(false);
				}
				catch(Exception ex) {
					displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop) \n"+ex.getMessage());
				}
				frameFound = true;
				break;
			}
		}

		if(!frameFound) {

			// CHECK IF VIEW HAS BEEN OPENED IN THIS SESSION
			for (int i = 0; i < viewFrameList.size(); i++) {
				viewFrame = (UIViewFrame)viewFrameList.elementAt(i);

				if (viewFrame.getView() != null && viewFrame.getView().getId().equals(view.getId())) {
					try {
						viewFrame.setBounds(xPos, yPos, width, height);
						viewFrame.setHorizontalScrollBarPosition(HScroll, true);
						viewFrame.setVerticalScrollBarPosition(VScroll, true);
						getDesktop().add(viewFrame, VIEWLAYER);
					}
					catch(Exception e) {
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\ncannot add to the desktop 1 \n" +	e.getMessage());
					}
					frameFound = true;
					break;
				}
			}

			if (!frameFound) {
				try {
					//read in members from the db
					view.initializeMembers();
				}
				catch(Exception ex) {
					ex.printStackTrace();
					displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop-1)\nCannot initialize View \n"+ex.getMessage());
				}

				if (xPos > getDesktop().getWidth()-64)  xPos = getDesktop().getWidth()-64;		// Bring off-screen maps back into view
				if (yPos > getDesktop().getHeight()-64) yPos = getDesktop().getHeight()-64;
				if (xPos < 0) xPos = 0;
				if (yPos < 0) yPos = 0;

				// CREATE NEW MAP/LIST
				UIMapViewFrame mapFrame = null;
				if(view.getType() == ICoreConstants.MAPVIEW) {
					try {
						mapFrame = new UIMapViewFrame(view, title);
						if (view.equals(oHomeView)) {
							mapFrame.setClosable(false);
						}

						mapFrame.setBounds(xPos, yPos, width, height);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\nCannot instantiate MapView Frame \n"+ex.getMessage());
						return viewFrame;
					}

					// add frame
					try {
						getDesktop().add(mapFrame,VIEWLAYER);
						mapFrame.setHorizontalScrollBarPosition(HScroll, true);
						mapFrame.setVerticalScrollBarPosition(VScroll, true);
						UIViewPane pane = mapFrame.getViewPane();
						pane.setZoom(APP_PROPERTIES.getZoomLevel());
						if (APP_PROPERTIES.getZoomLevel() != 1.0)
							pane.scale();
						viewFrame = (UIViewFrame)mapFrame;
					}
					catch(Exception e) {
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\ncannot add to the desktop 2 \n" + e.getMessage());
						return viewFrame;
					}
				}
				else if(view.getType() == ICoreConstants.LISTVIEW) {
					// invoke the view frame
					UIListViewFrame listFrame = null;
					try {
						listFrame = new UIListViewFrame(view, title);

						if (view.equals(oHomeView)) {
							listFrame.setClosable(false);
						}
						if (view.equals(getInBoxView())) {				// Sort inbox by Creation date
							listFrame.getUIList().sortByCreationDate();
						}
						listFrame.setBounds(xPos, yPos, width, height);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\nCannot instantiate ListView Frame \n"+ex.getMessage());
						return viewFrame;
					}

					// add frame
					try {
						getDesktop().add(listFrame,VIEWLAYER);
						listFrame.setHorizontalScrollBarPosition(HScroll, true);
						listFrame.setVerticalScrollBarPosition(VScroll, true);
						viewFrame = (UIViewFrame)listFrame;
					}
					catch(Exception e) {
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\ncannot add to the desktop 3 \n" + e.getMessage());
						return viewFrame;
					}
				}
			}

			//enable the view detail menu

			wasIcon = viewFrame.isIcon();
			try {
				if (wasIcon != isIcon)
					viewFrame.setIcon(isIcon);
				viewFrame.setMaximum(isMaximum);
				int count = oToolBarManager.getTextZoom();
				boolean increase = false;
				if (count > 0) {
					increase = true;
				} else if (count < 0) {
					count = count * -1;
				}
				if (count != 0) {
					UIViewPane pane = null;
					UIList list = null;
					if (viewFrame instanceof UIMapViewFrame) {
						pane = ((UIMapViewFrame)viewFrame).getViewPane();
					} else if (viewFrame instanceof UIListViewFrame) {
						list = ((UIListViewFrame)viewFrame).getUIList();
					}

					for (int i=0; i<count; i++) {
						if (pane != null) {
							if (increase) {
								pane.onIncreaseTextSize();
							} else {
								pane.onReduceTextSize();
							}
						} else if (list != null) {
							if (increase) {
								list.onIncreaseTextSize();
							} else {
								list.onReduceTextSize();
							}
						}
					}
				}
			}
			catch(Exception ex) {
				displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop) \n"+ex.getMessage());
			}
		}

		// BUG-FIX
		try {
			viewFrame.setVisible(true);
		} catch (Exception ex) {}

		// DEICONIFICATION DOES THIS ANYWAY, SO NO NEED TO DO AGAIN
		if ( !wasIcon && !isIcon ) {
			if (viewFrame instanceof UIMapViewFrame)
				((UIMapViewFrame)viewFrame).setSelected(true);
			else
				((UIListViewFrame)viewFrame).setSelected(true);
		}

		return viewFrame;
	}

	/**
	 * Return the currently selected frame.
	 * @return com.compendium.ui.UIViewFrame, the currently selected frame.
	 */
	public UIViewFrame getCurrentFrame() {

		//get the active frame to find the active view
		UIViewFrame viewFrame = null;
		boolean frameFound = false; int i=0;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		while(!frameFound && i<frames.length) {
			viewFrame = (UIViewFrame)frames[i++];
			if (viewFrame.isSelected()) {
				frameFound = true;
			}
		}

		// CHECK HOME FRAME (AS NOT ALWAYS SELECTED EVEN IF A NODE IS)
		if (!frameFound) {
			viewFrame = getInternalFrame(oHomeView);
		}

		return viewFrame;
	}

	/**
	 * Return the UIViewFrame for the given View else null.
	 * @param view com.compendium.core.datamodel.View, the view to return the frame for.
	 * @return the com.compendium.ui.UIViewFrame for the given view, else null.
	 */
	public UIViewFrame getInternalFrame(View view) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame.getView().getId().equals(view.getId())) {
				return viewFrame;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all UIViewFrame currently open.
	 * @return Vector, a list of all UIViewFrame currently open.
	 */
	public Vector getAllFrames() {

		UIViewFrame viewFrame = null;
		Vector vtFrames = new Vector(51);
		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			vtFrames.addElement(viewFrame);
		}
		return vtFrames;
	}

	/**
	 *  Creates a set of current codes for this project which can be used by the user.
	 */
	public void loadAllCodes() {

		try	{
			oModel.loadAllCodes();
		}
		catch(Exception ex)	{
			displayError("Exception: (ProjectCompendiumFrame.loadAllCodes) " + ex.getMessage());
		}
	}

	/**
	 *  Creates a set of current code group which can be used by the user.
	 * 	And loads the active link group.
	 */
	public void loadAllCodeGroups() {

		try	{
			oModel.loadAllCodeGroups();

			// INITIATE ACTIVE CODE GROUP
			SystemService service = (SystemService)oModel.getSystemService();
//			activeGroup	= service.getCodeGroup(oModel.getSession());
			activeGroup = APP_PROPERTIES.getActiveCodeGroup();
			activeLinkGroup	= service.getLinkGroup(oModel.getSession());
		}
		catch(Exception ex)	{
			displayError("Exception: (ProjectCompendiumFrame.loadAllCodeGroups) \n" + ex.getMessage());
		}
	}


	/**
	 * Cleanup the model variables and services.
	 */
	public void cleanupServices() {
		try {
			if (oServiceManager != null && oModel != null)
				oServiceManager.cleanupServices(oModel.getSession().getSessionID(), sUserName);

			if (oModel != null)
				oModel.cleanUp(); //must do this last as is required by ServiceManager
		}
		catch(Exception e) {
			System.out.println("Cleanup operation on services could not be done "+e.getMessage());
		}
	}

	/**
	 *	Create the cliboard used by this frame.
	 */
	public void createClipboard() {
		oClipboard = new Clipboard("ProjectCompendiumClipboard");
	}

	/**
	 * Set the clipboard used by this frame.
	 * @param clipboard, the clipboard used by this frame.
	 */
	public void setClipboard(Clipboard clipboard) {
		oClipboard = clipboard;
	}

	/**
	 * Return the clipboard used by this frame.
	 * @return Clipboard, the clipboard used by this frame.
	 */
	public Clipboard getClipboard() {
		return oClipboard;
	}

	/**
	 * Validate all the component in all the open views.
	 */
	public void validateComponents() {

		ProjectCompendiumFrame.this.validateTree();

		UIViewFrame viewFrame = null;
		boolean frameFound = false; int i=0;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		while(i<frames.length) {
			viewFrame = (UIViewFrame)frames[i++];
			if (viewFrame instanceof UIListViewFrame) {
				((UIListViewFrame)viewFrame).getUIList().validateComponents();
			}
			else {
				((UIMapViewFrame)viewFrame).getViewPane().validateComponents();
			}
		}
	}

	/**
	 * Returns the UIAudio thread which plays audio.
	 * @return UIAudio, the UIAudio thread which plays audio.
	 */
	public UIAudio getAudioPlayer() {
		 return audioThread;
	}

	 /**
	  * Inner class for holding the import profile for the user.
	  */
	 private class ImportProfile {

		 private boolean normalImport = true;
		 private boolean includeInDetail = false;
		 private boolean preserveIDs = false;
		 private boolean transclusion = false;

		 public ImportProfile(boolean normal, boolean include, boolean preserveids, boolean transclude) {
			normalImport = normal;
			includeInDetail = include;
			preserveIDs = preserveids;
			transclusion = transclude;
		 }

		 public ImportProfile() {}

		 public Vector getProfile() {
			Vector vtProfiles = new Vector(51);

			vtProfiles.addElement(new Boolean(normalImport));
			vtProfiles.addElement(new Boolean(includeInDetail));
			vtProfiles.addElement(new Boolean(preserveIDs));
			vtProfiles.addElement(new Boolean(transclusion));
			return vtProfiles;
		 }

		 public void setProfile(boolean normal, boolean include, boolean preserveids, boolean transclude) {
			normalImport = normal;
			includeInDetail = include;
			preserveIDs = preserveids;
			transclusion = transclude;
		 }
	 }

  	/**
   	 * Print the current view.
     */
   	public void onPrint() {

		UIViewFrame currentFrame = getCurrentFrame();
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		aset.add(OrientationRequested.LANDSCAPE);
		aset.add(MediaSizeName.ISO_A4);
		aset.add(new Copies(1));
		aset.add(new JobName(currentFrame.getView().getLabel(), null));

		if (currentFrame instanceof UIListViewFrame) {
			UIListViewFrame listFrame = (UIListViewFrame)currentFrame;
			UIList uiList = listFrame.getUIList();
			uiList.print(aset);
		} else {
	       	PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setPrintable( ((UIMapViewFrame)currentFrame).getViewPane());
			try {
				if(pj.printDialog(aset)) {
					pj.print(aset);
				}
			} catch (PrinterException pe) {
				System.err.println(pe);
			}
		}
	}

 	/**
	 * Display an error message dialog with the given message.
	 * @param error, the error message to display.
	 */
   	public void displayError(String error) {
   		System.out.println("Error:" + error);
		JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
   	}

 	/**
	 * Display an error message dialog with the given message an title.
	 * @param error, the error message to display.
	 * @param sTitle, the title for the message window.
	 */
  	public void displayError(String error, String sTitle) {
   		System.out.println("Error:" + error);
		JOptionPane.showMessageDialog(this, error, sTitle, JOptionPane.ERROR_MESSAGE);
   	}

	/**
	 * Display a dialog with the given message an title.
	 * @param message, the message to display.
	 * @param sTitle, the title for the message window.
	 */
   	public void displayMessage(String message, String sTitle) {
		JOptionPane.showMessageDialog(this, message, sTitle, JOptionPane.INFORMATION_MESSAGE);
   	}

	/**
	 * Refresh the Windows menu for the currently open frames.
	 */
	public void refreshWindowsMenu() {
		oMenuManager.refreshWindowsMenu();
	}

	/**
	 * When a window is de-iconified / activated, add it to the hitory and Windows menu.
	 * Reset its zoom level and update its aerial view.
	 * @param window com.compendium.ui.UIViewFrame, the frame that has been de-iconified / activated.
	 */
	public void activateWindow(UIViewFrame window) {

		if (window instanceof UIListViewFrame) {
			oMenuManager.setMapMenuEnabled(false);
			oMenuManager.setScribblePadEnabled(false);
			oToolBarManager.setZoomToolBarEnabled(false);
			oToolBarManager.setDrawToolBarEnabled(false);
		}
		else {
			oMenuManager.setMapMenuEnabled(true);
			oMenuManager.setScribblePadEnabled(true);
			oToolBarManager.setZoomToolBarEnabled(true);
			oToolBarManager.setDrawToolBarEnabled(true);
		}

		resetZoom();

		oToolBarManager.addToHistory(window.getView());

		refreshWindowsMenu();

		oToolBarManager.enableHistoryButtons();

		updateAerialView();
	}

/////////////////////////////////////////////////////////////////////////

/**
 * Start a recording of a meeting from data passed through web launch.
 * @param sData  the record setup data required.
 */
public void setupForRecording(String sSetupData) {

	try {
		oMeetingManager = new MeetingManager(MeetingManager.RECORDING);
		if (oMeetingManager.processSetupData(sSetupData)) {
			oMeetingManager.setupMeetingForRecording();
		}
	} catch (AccessGridDataException ex) {
		displayError(ex.getMessage());
	}
}

/**
 * Start a replay of a meeting from data passed through web launch.
 * @param sData  the replay setup data required.
 */
public void setupForReplay(String sSetupData, String sReplayData) {

	try {
		oMeetingManager = new MeetingManager(MeetingManager.REPLAY);
		if (oMeetingManager.processSetupData(sSetupData)) {
			oMeetingManager.processReplayData(sReplayData);
            oMeetingManager.setupMeetingForReplay();
		}
	} catch (AccessGridDataException ex) {
		displayError(ex.getMessage());
	}
}


/////////////////////////////////////////////////////////////////////////

// RESTORE CODE //

	/** Holds restored views.*/
	Hashtable restoredViews = new Hashtable(51);

	/** Holds the restored nodes indent level.*/
	int restoreIndent = 0;

	/**
	 * Restore the given node in the given to.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to restore.
	 * @param view com.compendium.core.datamodel.View, the view to restore the node to.
	 */
	public void restore(NodeSummary node, View view) {

		// FOR DUPLICATION CHECK
		restoredViews.clear();

		restoreIndent = 0;
		restoreNode(node, view);
	}

	/**
	 * Restore the given node in the given to.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to restore.
	 * @param view com.compendium.core.datamodel.View, the view to restore the node to.
	 */
	public void restoreNode(NodeSummary node, View view) {

		PCSession session = oModel.getSession();
		NodeService nodeService = (NodeService)oModel.getNodeService();

		UIViewFrame viewFrame = addViewToDesktop(view, view.getLabel() );
		Vector history = new Vector();
		history.addElement(new String("Restored"));
		viewFrame.setNavigationHistory(history);

		// Bug Fix  - Lakshmi (9/13/06)
		// Included check if viewFrame instanceof UIMapViewFrame to avoid ClassCastException
		// when restoring a node to a list.

		ViewPaneUI viewpaneui = null;
		if(viewFrame instanceof  UIMapViewFrame)
			viewpaneui = ((UIMapViewFrame)viewFrame).getViewPane().getViewPaneUI();

		try {
			// CHECK DELETED STATUS
			boolean wasDeleted = false;
			if (oModel.getNodeService().isMarkedForDeletion(session, node.getId())) {
				wasDeleted = true;
			}

			String sNodeID = node.getId();
			String sViewID = view.getId();

			NodePosition oPos = null;

			// RESTORE NODE RELATED VIEWNODE
			boolean restored = nodeService.restoreNode(session, sNodeID);
			if (restored)
				oPos = nodeService.restoreNodeView(session, sNodeID, sViewID);

			// IF THIS NODE IS A VIEW AND WAS DELETED, RESTORE ITS CHILDREN
			int nodeType = node.getType();
			if ( (nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.LISTVIEW )
					&& wasDeleted )
				restoreView(view, (View)node, session, nodeService);

			// IF NODE POSITION FAILED, RESTORE WITH NEW POSITION
			if (oPos == null) {
				int xpos = 0;
				int ypos = 0;
				if (view.getType() == ICoreConstants.MAPVIEW) {
					xpos = (restoreIndent+1)*20;
					ypos = 150+restoreIndent*20;
					restoreIndent++;
				}
				else {
					xpos = 0;
					ypos = ( ((UIListViewFrame)viewFrame).getUIList().getNumberOfNodes() + 1) * 10;
				}
				oPos = view.addNodeToView(node, xpos, ypos);
			}
			else {
				oPos.setNode(node);
				oPos = view.addMemberNode(oPos);
			}
			oPos.getNode().initialize(session, oModel);

			if (view.getType() == ICoreConstants.MAPVIEW) {

				UINode uinode = viewpaneui.addNode(oPos);

				// RESTORE RELATED LINKS AND VIEWLINKS FOR THIS NODE IF VIEW IS A MAP
				// NB. MUST DO THIS AFTER NODE ADDED TO LAYER OR addLink METHOD WILL FAIL
				try {
					Vector links = oModel.getLinkService().restoreNode(session, sNodeID, sViewID);
					if (links != null) {
						final int count = links.size();
						for (int i=0; i<count; i++) {
							Link link = (Link)links.elementAt(i);
							view.addMemberLink(link);
							viewpaneui.addLink(link);
						}
					}
				}
				catch(Exception ex) {
					displayError("Exception: (ProjectcompendiumFrame.restoreLinks) \n"+ex.getMessage());
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)viewFrame).getUIList();
				uiList.insertNode(oPos, uiList.getNumberOfNodes());
				uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
				uiList.updateTable();
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			displayError("Exception: (ProjectCompendiumFrame.restoreNode) \n"+ex.getMessage());
		}
	}

	/**
	 * Restore the given view from the database after a previous deletion.
	 * @param parent com.compendium.core.datamodel.View, the parent view of the view being restored.
	 * @param view com.compendium.core.datamodel.View, the view to restore.
	 */
	private void restoreView(View parent, View view, PCSession session, NodeService nodeService) throws Exception {

		String sViewID = view.getId();
		String userID = oModel.getUserProfile().getId();

		// DON'T RESTORE THE SAME VIEW TWICE :WHEN VIEW CONTAINS ITSELF SOMEWHERE IN CHILDREN TREE
		if (restoredViews.containsKey(sViewID)) {
			return;
		}
		else {
			restoredViews.put(sViewID, view);
		}

		// IF THIS VIEW IS A MAP RESTORE ALL (LINKS AND) NODES
		if (view.getType() == ICoreConstants.MAPVIEW) {
			oModel.getViewService().restoreViewLinks(session, sViewID);
		}
		nodeService.restoreView(session, sViewID);

		Vector vtNodePos = oModel.getViewService().getNodePositions(oModel.getSession(), sViewID);
		for(Enumeration en = vtNodePos.elements(); en.hasMoreElements();) {

			NodeSummary node = (NodeSummary)((NodePosition)en.nextElement()).getNode();
			String sNodeID = node.getId();
			int innerIndent = 0;

			// IF THIS NODE IS A VIEW, RESTORE ITS CHILDREN
			int nodeType = node.getType();
			if (nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.LISTVIEW ) {
				restoreView(view, (View)node, session, nodeService);
			}
		}

		// RE-FILL OBJECT WITH NEWLY RESTORED NODES AND LINKS
		view.setIsMembersInitialized(false);
		view.initializeMembers();

		// IF VIEW HAS BEEN OPENED, GET IT TO RE-FILL ITSELF WITH THE NEW DATA
		UIViewFrame viewFrame = null;
		for (int i = 0; i < viewFrameList.size(); i++) {
			viewFrame = (UIViewFrame)viewFrameList.elementAt(i);
			if (viewFrame.getView().getId().equals(view.getId())) {

				// CREATE NEW MAP/LIST
				if(view.getType() == ICoreConstants.MAPVIEW) {
					UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
					mapFrame.setView(view);
					mapFrame.getViewPane().setView(view);
					mapFrame.getViewPane().updateUI();
				}
				else if(view.getType() == ICoreConstants.LISTVIEW) {
					UIListViewFrame listFrame = (UIListViewFrame)viewFrame;
					listFrame.createList(view);
				}
			}
		}
	}

	/**
	 * Restore the view frame properties for the given view.
	 * @param view com.compendium.core.datamodel.View, the view to restore the view frame properties for.
	 */
	private ViewProperty restoreViewProperties(View view) {

		ViewProperty properties = null;
		try {
			String sUserID = oModel.getUserProfile().getId();
			ViewPropertyService viewserv = (ViewPropertyService)oModel.getViewPropertyService();

			if (view.getId() != "")
				properties = viewserv.getViewPosition(oModel.getSession(), sUserID, view.getId());
		}
		catch(Exception io) {
			io.printStackTrace();
		}

		return properties;
	}


	/**
	 * Save the properties of the given view to the database.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the view frame to save the proerpties for.
	 */
	public void saveViewProperties(UIViewFrame viewFrame) {

		String sUserID = oModel.getUserProfile().getId();
		ViewPropertyService viewserv = (ViewPropertyService)oModel.getViewPropertyService();

		String sViewID = viewFrame.getView().getId();

		Rectangle rect = viewFrame.getNormalBounds();
		int width = rect.width;
		int height = rect.height;
		int nX = rect.x;
		int nY = rect.y;

		ViewProperty view = new ViewProperty();
		view.setUserID(sUserID);
		view.setViewID(viewFrame.getView().getId());
		view.setWidth(width);
		view.setHeight(height);
		view.setXPosition(nX);
		view.setYPosition(nY);
		view.setHorizontalScrollBarPosition(viewFrame.getHorizontalScrollBarPosition());
		view.setVerticalScrollBarPosition(viewFrame.getVerticalScrollBarPosition());
		view.setIsIcon(viewFrame.isIcon());
		view.setIsMaximum(viewFrame.isMaximum());

		if (!sViewID.equals("")) {

			// CHECK IF RECORD FOR THIS VIEW AND USER ALREADY EXISTS
			// SO WE CAN DECIDE WETHER TO UPDATE OR INSERT
			try {
				PCSession session = oModel.getSession();
				ViewProperty current = viewserv.getViewPosition(session, sUserID, sViewID);

				if (current != null)
					viewserv.updateViewProperty(session, sUserID, view);
				else
					viewserv.createViewProperty(session, sUserID, view);
			}
			catch(Exception io) {
				io.printStackTrace();
			}
		}
	}

	/**
  	 * Activate menu options when a node has been selected/deselected.
  	 * @param selected, true to enable, false to disable.
	 * @see com.compendium.ui.UIToolBarManager#setNodeSelected
	 */
	public void setNodeSelected(boolean selected) {
		oToolBarManager.setNodeSelected(selected);
		oMenuManager.setNodeSelected(selected);
	}

	/**
 	 * Activate menu and toolbar options when nodes and links have been selected/deselected.
 	 * @param selected boolean.
	 * @see com.compendium.ui.UIToolBarManager#setNodeOrLinkSelected
	 * @see com.compendium.ui.UIMenuManager#setNodeOrLinkSelected
	 */
	public void setNodeOrLinkSelected(boolean selected) {
		oToolBarManager.setNodeOrLinkSelected(selected);
		oMenuManager.setNodeOrLinkSelected(selected);
	}

	/**
	 * Enable/disable the menu and toolbar paste items.
	 * @param enabled boolean, true to enable, false to disable.
	 * @see com.compendium.ui.UIToolBarManager#setPasteEnabled
	 * @see com.compendium.ui.UIMenuManager#setPasteEnabled
	 */
	public void setPasteEnabled(boolean enabled) {
		isPasteEnabled = enabled ;
		oToolBarManager.setPasteEnabled(enabled);
		oMenuManager.setPasteEnabled(enabled);
	}
}
