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

package com.compendium;

import java.net.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

import com.compendium.core.*;
import com.compendium.ui.dialogs.UIStartUp;
import com.compendium.ui.ApplicationProperties;
import com.compendium.ui.ExportProperties;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.PropertyManager;
import com.compendium.meeting.MeetingManager;
import com.compendium.meeting.remote.RecordListener;


/**
 * ProjectCompendium is the main class for running the Project Compendium application.
 * It initalises the main JFrame and creates a new logfile instance.
 *
 * @author	Michelle Bachler
  */
public class ProjectCompendium {

	/** Reference to the main application frame */
	public static ProjectCompendiumFrame APP = null;

	/** The path to the current Compendium home folder.*/
	//public static String 		sHOMEPATH	= (new File("")).getAbsolutePath();

	//public static String 		sHOMEPATH	= System.getenv("APPDATA");
	//public static String 		sSYSPATH	= System.getenv("ProgramFiles(x86)");

	public static String 		sHOMEPATH	= System.getenv("CompendiumUserPath");
	public static String 		sSYSPATH	= System.getenv("CompendiumSysPath");

	/*  Matt Stucky, Feb 2012
		New directory structure for win7.  the runnable parts of the code are
		going to c:/program files/ and the user configurable parts are going to
		APPDATA which is c:/users/owner/appdata/roaming.

		the directory structure will be:

		c:/program files/
		    Skins/*
		    Templates/
		    System/lib/*
		    System/resources/
		        toolbars.xml
		        Audio/*
		        Help/*
		        Images/*
		        LinkGroups/*
		        OutlineStyles/*
		        ReferenceNodeIcons/*
		        Stencils/*

		c:/users/public/documents/compendium
		    Backups/
		    Exports/
		    Linked Files/
		    System/resources/
		        *.properties
		        Databases/
		        Logs/
        		Meetings/
	*/

	/** A reference to the system file path separator*/
	public final static String	sFS		= System.getProperty("file.separator");

	/** A reference to the system platform */
	public static String platform = System.getProperty("os.name");

	/** The indicates the current system platform is Mac*/
	public static boolean isMac = false;

	/** The indicates the current system platform is Windows*/
	public static boolean isWindows = false;

	/** The indicates the current system platform is Linux*/
	public static boolean isLinux = false;

	/** RMI instance id for Compendium used for memetic project.*/
	public static String sCompendiumInstanceID = "";

	/** RMI Port number use for memetic project.*/
	public static int nRMIPort = 1099;

	/** Instance of the RMI listener for memetic web start stuff.*/
	public static RecordListener oRecordListener = null;

    /** The property manager which allows to read/write application properties from the "Format.properties" file.*/
    public static final PropertyManager<ApplicationProperties> APP_PROPERTY_MANAGER;

    /** The application properties from the "Format.properties" file.*/
    public static final ApplicationProperties APP_PROPERTIES;

    /** The property manager which allows to read/write application properties from the "ExportOptions.properties" file.*/
    public static final PropertyManager<ExportProperties> EXPORT_PROPERTY_MANAGER;

    /** The application properties from the "ExportOptions.properties" file.*/
    public static final ExportProperties EXPORT_PROPERTIES;

    static {
        APP_PROPERTY_MANAGER = new PropertyManager<ApplicationProperties>(
                ApplicationProperties.class, "Format.properties");
        APP_PROPERTIES = APP_PROPERTY_MANAGER.load();

        EXPORT_PROPERTY_MANAGER =
            new PropertyManager<ExportProperties>(
                    ExportProperties.class, "ExportOptions.properties");
        EXPORT_PROPERTIES = EXPORT_PROPERTY_MANAGER.load();
    }

	/**
	 * Starts Project Compendium as an application
	 *
	 * @param args Application arguments, currently none are handled
	 */
	public static void main(String [] args) {

		UIStartUp oStartDialog = new UIStartUp(null);

        oStartDialog.setLocationRelativeTo(oStartDialog.getParent());
		oStartDialog.setVisible(true);

		// MAKE SURE ALL EMPTY FOLDERS THAT SHOULD EXIST, DO
		checkDirectory(sHOMEPATH+sFS+"Exports");
		checkDirectory(sHOMEPATH+sFS+"Backups");
		checkDirectory(sHOMEPATH+sFS+"Linked Files");
		checkDirectory(sSYSPATH +sFS+"Templates");
		checkDirectory(sHOMEPATH+sFS+"System"+sFS+"resources"+sFS+"Logs");
		checkDirectory(sHOMEPATH+sFS+"System"+sFS+"resources"+sFS+"Databases");
		checkDirectory("System"+sFS+"resources"+sFS+"Meetings");

		//JOptionPane.showMessageDialog(null, "directories checked");

		try {
			Date date = new Date();
			sCompendiumInstanceID = (new Long(date.getTime()).toString());

			//JOptionPane.showMessageDialog(null, sHOMEPATH+sFS+"System"+sFS+"resources"+sFS+"Logs"+sFS+"log_"+CoreCalendar.getCurrentDateStringFull()+".txt");

			SaveOutput.start(sHOMEPATH+sFS+"System"+sFS+"resources"+sFS+"Logs"+sFS+"log_"+CoreCalendar.getCurrentDateStringFull()+".txt");

			//JOptionPane.showMessageDialog(null, "output saved");

			ProjectCompendium app = new ProjectCompendium(oStartDialog, args);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		//Map<String, String> env = System.getenv();
		//for (String envName : env.keySet())
		//{
		//	System.out.format("%s=%s%n", envName, env.get(envName));
		//}

		java.util.Date now = new java.util.Date();
		System.out.println("133 ProjectCompendium now is " + now.getTime());
	}

	/**
	 * Check if a directory with the passed path exists, and if not create it.
	 * @param String sDirectory, the directory to check/create.
	 */
	private static void checkDirectory(String sDirectory) {
		File oDirectory = new File(sDirectory);
		if (!oDirectory.isDirectory()) {
			oDirectory.mkdirs();
		}
	}

	/**
	 * Constructor, creates a new project compendium application instance.
	 */
	public ProjectCompendium(UIStartUp oStartDialog, String [] args) {

		String os = platform.toLowerCase();
		if (os.indexOf("windows") != -1) {
		    isWindows = true;
		}
		else if (os.indexOf("mac") != -1) {
		    isMac = true;
		}
		else if (os.indexOf("linux") != -1) {
		    isLinux = true;
		}

		// Get the hostname and ip address of the current machine.
		String sServer = "";
		try {
			sServer = (InetAddress.getLocalHost()).getHostName();
		}
		catch(java.net.UnknownHostException e) {}

		String sIP = "";
		try {
			sIP = (InetAddress.getLocalHost()).getHostAddress();
		}
		catch(java.net.UnknownHostException e) {
			System.out.println("Exception: UnknownHost\n\n"+e.getMessage());
		}

		// Create main frame for the application
		APP = new ProjectCompendiumFrame(this, ICoreConstants.sAPPNAME, sServer, sIP, oStartDialog);

		// Fill all variables and draw the frame contents
		if (!APP.initialiseFrame()) {
			return;
		}

		String sReplayData = "";
		String sSetupData = "";
        boolean startRecording = false;

		int count = args.length;

		if (count > 0) {

			int nPort = 0;
			String sID = "";
			String next = "";
			int index = 0;
			for (int i=0; i<count; i++) {
				next = args[i];
				if (next.startsWith("memetic-compendiuminstance")) {
					index = next.indexOf(":");
					if (index> -1) {
						sID = next.substring(index+1);
					}
				}
				else if (next.startsWith("memetic-rmiport")) {
					index = next.indexOf(":");
					if (index> -1) {
						try {
							nPort = new Integer(next.substring(index+1)).intValue();
						}
						catch(Exception e) {
							System.out.println("failed to load memetic rmi port from string = "+next);
						}
					}
				}
				else if (next.startsWith("memetic-setup")) {
					sSetupData = next;
				}
				else if (next.startsWith("memetic-replay")) {
					sReplayData = next;
				}
                else if (next.startsWith("memetic-startrecording")) {
                    startRecording = true;
                }
			}

			if (nPort > 0) {
				nRMIPort = nPort;
			}
			if (!sID.equals("")) {
				sCompendiumInstanceID = sID;
			}
		}

		oStartDialog.setVisible(false);
		oStartDialog.dispose();

		// create the project compendium panel
		APP.setVisible(true);

		APP.showFloatingToolBars();
		if (APP.isFirstTime()) {
			//APP.onFileNew(); We don't want this, because it presents a database security issue on commercial hosting.
		}

		else if (APP.shouldOpenFile()) {
			APP.onFileOpen();
		}

		if (!sSetupData.equals("")) {
			if (!sReplayData.equals("")) {
				APP.setupForReplay(sSetupData, sReplayData);
                if (startRecording) {
                    APP.oMeetingManager.startReplayRecording();
                }
			}
			else {
				APP.setupForRecording(sSetupData);
                if (startRecording) {
                    APP.oMeetingManager.startRecording();
                }
			}
		} else {
            try {
                APP.oMeetingManager = new MeetingManager(MeetingManager.RECORDING);
                APP.oMeetingManager.reloadAccessGridData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
