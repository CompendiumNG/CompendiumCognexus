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

package com.compendium.core.db.management;

import java.sql.*;
import java.sql.Connection;
import java.util.*;
import java.io.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.*;

/**
 * This class, with the help of the <code>DBConnectionManager</code>, opens connections for databases.
 * These connection objects can be requested by the services, and the services objects can
 * use these connection objects to call database operations.
 *
 * @author ? / Michelle Bachler
 */

public class DBDatabaseManager {

	/** A hashtable of database names and references to the connection manager objects for a database.*/
	//private Hashtable htDatabases = new Hashtable() ;
	private DBConnectionManager db_conn_mgr;

	private String current_db_name;

	/** The name of the file in which to find the list of any old Compendium Access database*/
	//private final String file = "database.ini";

	/** The name to use whn accessing the database */
	private String sDatabaseUserName = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the database */
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the database */
	private String sDatabaseIP = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The type of database being used */
	private int nDatabaseType = ICoreConstants.DERBY_DATABASE;

	/**
	 * This constructor is for use with Derby database.
	 */
	public DBDatabaseManager(int nDatabaseType) {
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseUserName = "";
		this.sDatabasePassword = "";
		this.sDatabaseIP = "";
	}

	/**
	 * This constructor takes a name and password to use when accessing the database.
	 *
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database.
	 */
	public DBDatabaseManager(int nDatabaseType, String sDatabaseUserName, String sDatabasePassword) {
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
	}

	/**
	 * This constructor takes a name and password to use when accessing the database,
	 * and the IP address of the MysqL server machine.
	 *
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database.
	 * @param sDatabaseIP, the IP address of the server machine. The default if 'localhost'.
	 */
	public DBDatabaseManager(int nDatabaseType, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		this.sDatabaseIP = sDatabaseIP;
	}


	/**
	 * Return the type of database this object is Managing.
	 */
	public int getDatabaseType() {
		return nDatabaseType;
	}

	/**
	 * This method opens a project by setting up a DBConnectionManager for the project.
	 *
	 * @param String sDatabaseName, the name of the database project to open a connection for.
	 * @return true, if connection opened, false if there was an error.
	 */
	public boolean openProject(String sDatabaseName) {

		//System.out.println("119 DBDatabaseManager entered openProject for name " + sDatabaseName + " current_db_name is " + current_db_name);
		//System.out.flush();

		if ( (current_db_name != null) && current_db_name.equals(sDatabaseName) )
		{
			//same db name as last time, skip
			return true;
		}

		current_db_name = sDatabaseName;

//		if (htDatabases.containsKey(sDatabaseName)) {
//			return true;
//		}
//		else {
			DBConnectionManager con = null;
			try {
				if (nDatabaseType == ICoreConstants.DERBY_DATABASE)
					con = new DBConnectionManager(nDatabaseType, sDatabaseName);
				else
					con = new DBConnectionManager(nDatabaseType, sDatabaseName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);

				db_conn_mgr = con;
			}
			catch(Exception ex) {
				System.out.println("Exception (DBDatabaseManager.openProject) \n\n"+ex.getMessage());
				System.out.flush();
				return false;
			}
//			htDatabases.put(sDatabaseName, con) ;
			System.out.println("140 DBDatabaseManager exiting openProject returning true");
			return true ;
//		}
	}

	/**
	 * This method is called by a service requesting a database connection and
	 * returns null if connection could not be obtained.
	 *
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @return DBConnection, a connection to the spcified database, or null if the connection could not be made.
	 */
	public DBConnection requestConnection(String sDatabaseName) {

		//System.out.println("154 DBDatabaseManager entered requestConnection, sDatabaseName is " + sDatabaseName);
		//System.out.flush();

		// If the database has not been opened with connections, open it
/*		if (!htDatabases.containsKey(sDatabaseName)) {
			if(!openProject(sDatabaseName))
				return null ;	//could not get connection
		}

		// returns the ste of connections
		DBConnectionManager conSet = (DBConnectionManager)htDatabases.get(sDatabaseName);

		// returns the free connection
		System.out.println("247 DBDatabaseManager exiting requestConnection for db " + sDatabaseName + ", DBConnection good ");
		return conSet.getConnection();
		*/
		openProject(sDatabaseName);

		//System.out.println("172 DBDatabaseManager exiting requestConnection, sDatabaseName is " + sDatabaseName);
		//System.out.flush();

		if (db_conn_mgr == null)
			return null;
		else
			return db_conn_mgr.getConnection();
	}

	/**
	 * This method is called by a service to release a connection that it has been using.
	 *
	 * @param String sDatabaseName, the name of the database to release the connection for.
	 * @param DBConnection dbcon, the connection to release.
	 * @return boolean, true if the database was found, else false.
	 */
	public boolean releaseConnection(String sDatabaseName, DBConnection dbcon) {

		//System.out.println("265 DBDatabaseManager entered releaseConnection, sDatabaseName is " + sDatabaseName);

/*		if (htDatabases.containsKey(sDatabaseName)) {
			DBConnectionManager conSet = (DBConnectionManager)htDatabases.get(sDatabaseName) ;
			conSet.releaseConnection(dbcon) ;
			return true;
		}
		else
			return false;
			*/
		//db_conn_mgr.releaseConnection(dbcon);
		return true;
	}

	/**
	 * Removes all the connection relating to a database project,
	 * if the service manager has no active clients connected to the project.
	 *
	 * @param String sDatabaseName, the name of the database to release all connection for.
	 * @return boolean, true if the database was found, else false.
	 */
	public boolean removeAllConnections(String sDatabaseName) {

/*		System.out.println("280 DBDatabaseManager entered removeAllConnections, sDatabaseName is " + sDatabaseName);

		if (htDatabases.containsKey(sDatabaseName)) {
			DBConnectionManager conSet = (DBConnectionManager)htDatabases.get(sDatabaseName);
			conSet.removeAllConnections();
			htDatabases.remove(sDatabaseName);
			return true;
		}
		else
			return false;
			*/
			//db_conn_mgr.removeAllConnections();
			return true;
	}
}
