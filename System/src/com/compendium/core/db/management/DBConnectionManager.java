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
import java.util.*;
import java.io.File;

import org.apache.derby.jdbc.*;

import com.compendium.core.*;
import com.compendium.core.db.management.*;

/**
 * The class manages a set of connections for a given database
 *
 * @author ? / Michelle Bachler
 */
public class DBConnectionManager {

	/** A String representing an odcj connection url, used when creating a connection.*/
	public final static String ODBC_URL 		= "jdbc:odbc:";

	/** A String representing a MySQL database url, used when creating a connection.*/
	public final static String JDBC_MYSQL_URL 	= "jdbc:mysql:";

	/** A String representing a Derby database url, used when creating a connection.*/
	public final static String DERBY_URL 		= "jdbc:derby:";

	/** Holds references to all DBConnections currently created.*/
	//private Vector vtDBConnections = new Vector(100);
	private DBConnection db_conn = null;

	/** The name of the current database this ConnectionManager is managing connection for.*/
	private static String 	sDatabaseName 	= "";

	/** The Driver obejct used to communicate with the database.*/
	private Driver	oDriver			= null;

	/** The type of database being used */
	private int nDatabaseType = ICoreConstants.DERBY_DATABASE;

	/** The name to use when accessing the database. */
	private String sDatabaseUserName = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the database. */
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The url used to connect to a database.*/
	private String sDatabaseURL = JDBC_MYSQL_URL+"//localhost/";


// STATIC METHODS

	/**
	 * Creates a new connection to the database using the given database name, login name, and password.
	 * Create a new database with the given name
	 *
	 * @param nDatabaseType, the type of the database you want the connection for (e.g, MySQL, Derby).
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @param String sDatabaseUserName, the database username to connect with.
	 * @param String sDatabasePassword, the database password to connect with.
	 * @param String sDatabaseIP, the IP address or host name to connect with.
	 * @return Connection, the new connection created to the given database
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be created
	 */
	public static Connection getCreationConnection(int nDatabaseType, String sDatabaseName, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) throws SQLException, ClassNotFoundException {

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			return getDerbyCreationConnection(sDatabaseName);
		}
		else if (nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			return getMySQLCreationConnection(sDatabaseName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		}
		else {
			return null;
		}
	}

	/**
	 * Creates a new database connection to a new database using the given database name, login name, and password, on MySQL.
	 *
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @param String sDatabaseUserName, the database username to connect with.
	 * @param String sDatabasePassword, the database password to connect with.
	 * @param String sDatabaseIP, the IP address or host name to connect with.
	 * @return Connection, the new connection created to the given database
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be created
	 */
	public static Connection getMySQLCreationConnection(String sDatabaseName, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) throws SQLException, ClassNotFoundException {

		Connection con = getPlainConnection(ICoreConstants.MYSQL_DATABASE, "?", sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (con == null)
			return null;

		Statement stmt = con.createStatement();
		int nRowCount = stmt.executeUpdate("CREATE DATABASE "+sDatabaseName);
		if (nRowCount > 0) {
	    	return DBConnectionManager.getPlainConnection(ICoreConstants.MYSQL_DATABASE, sDatabaseName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		}

		return null;
	}

	/**
	 * Creates a new connection to create a new database of the given name for Derby.
	 *
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @return Connection, the new connection created to the given database
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be created
	 */
	public static Connection getDerbyCreationConnection(String sDatabaseName) throws SQLException, ClassNotFoundException {

		//Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		Driver oDriver = new org.apache.derby.jdbc.EmbeddedDriver();

		System.out.println(" 142 DBConnection Manager DERBY_URL is " + DERBY_URL + " and sDatabaseName is " + sDatabaseName);

		String databaseURL = DERBY_URL+sDatabaseName+";create=true;";

		//if (sDatabaseIP != null && !sDatabaseIP.equals(""))
		//	databaseURL = DERBY_URL+"//"+sDatabaseIP+sDatabaseName+";create=true";

		Connection connection = DriverManager.getConnection(databaseURL);

		return connection;
	}

	/**
	 * Creates a new connection to the appropriate database using the given database name, login name, and password.
	 *
	 * @param nDatabaseType, the type of the database you want the connection for (e.g, MySQL, Derby).
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @param String sDatabaseUserName, the database username to connect with.
	 * @param String sDatabasePassword, the database password to connect with.
	 * @param String sDatabaseIP, the IP address or host name to connect with.
	 * @return Connection, the new connection created to the given database
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be created
	 */
	public static Connection getPlainConnection(int nDatabaseType, String sDatabaseName, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) throws SQLException, ClassNotFoundException {

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			return getDerbyConnection(sDatabaseName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		}
		else if (nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			return getMySQLConnection(sDatabaseName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		}
		else
			return null;
	}

	/**
	 * Creates a new connection to a Derby database using the given database name.
	 *
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @return Connection, the new connection created to the given database
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be created
	 */
	public static Connection getDerbyConnection(String sDatabaseName) throws SQLException, ClassNotFoundException {

		Driver driver = new org.apache.derby.jdbc.EmbeddedDriver();
		System.out.println("188 reading DERBY_URL " + DERBY_URL);
		String databaseURL = DERBY_URL;
		//if (sDatabaseIP != null && !sDatabaseIP.equals(""))
		//	databaseURL += "//"+sDatabaseIP+"/";
		System.out.println("192 DBConnectionManager database name is " + sDatabaseName);
		System.out.println("193 DBConnectionManager getting connection for " + databaseURL + sDatabaseName);

		Connection connection = DriverManager.getConnection(databaseURL + sDatabaseName);
		return connection;
	}

	/**
	 * Creates a new connection to a Derby database using the given database name, login name, and password.
	 *
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @param String sDatabaseUserName, the database username to connect with.
	 * @param String sDatabasePassword, the database password to connect with.
	 * @param String sDatabaseIP, the IP address or host name to connect with.
	 * @return Connection, the new connection created to the given database
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be created
	 */
	public static Connection getDerbyConnection(String sDatabaseName, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) throws SQLException, ClassNotFoundException {

		Driver driver = new org.apache.derby.jdbc.EmbeddedDriver();
		System.out.println("212 reading DERBY_URL " + DERBY_URL);
		String databaseURL = DERBY_URL;
		//if (sDatabaseIP != null && !sDatabaseIP.equals(""))
		//	databaseURL += "//"+sDatabaseIP+"/";
		System.out.println("217 DBConnectionManager database name is " + sDatabaseName);
		Connection connection = DriverManager.getConnection(databaseURL + sDatabaseName);
		return connection;
	}

	/**
	 * Creates a new connection to a MySQL database using the given database name, login name, and password.
	 *
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @param String sDatabaseUserName, the database username to connect with.
	 * @param String sDatabasePassword, the database password to connect with.
	 * @param String sDatabaseIP, the IP address or host name to connect with.
	 * @return Connection, the new connection created to the given database
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be created
	 */
	public static Connection getMySQLConnection(String sDatabaseName, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) throws SQLException, ClassNotFoundException {

		Driver driver = new com.mysql.jdbc.Driver();
		String databaseURL = JDBC_MYSQL_URL;

		if (sDatabaseIP != null && !sDatabaseIP.equals(""))
			databaseURL += "//"+sDatabaseIP+"/";

		Connection connection = DriverManager.getConnection(databaseURL + sDatabaseName, sDatabaseUserName, sDatabasePassword);

		long lTimeout = lgetMySQLServerTimeout(connection);
		if (lTimeout > 0) {
			DBConnection.setTimeouts((lTimeout/2)*1000);	// Cut server timeout in half, convert to milliseconds
		}


		return connection;
	}

	/**
	 * Get the lesser interactive/wait timeout from the MySQL server
	 * @param connection - the database connection
	 * @return lTimeout - the lesser of the interactive_timeout & the wait_timeout vars
	 */
	private static long lgetMySQLServerTimeout(Connection connection) throws SQLException {

		long lTimeout = 0;

		PreparedStatement pstmt = connection.prepareStatement("SHOW VARIABLES LIKE 'interactive_timeout'");
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			System.out.println("DBConnectionManager Error during 'SHOW VARIABLES LIKE 'interactive_timeout''");
			e.printStackTrace();
		}
		while (rs.next()) {
	        lTimeout = Long.parseLong(rs.getString(2));
		}
		pstmt.close();

		pstmt = connection.prepareStatement("SHOW VARIABLES LIKE 'wait_timeout'");
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			System.out.println("DBConnectionManager Error during 'SHOW VARIABLES LIKE 'wait_timeout''");
			e.printStackTrace();
		}
		while (rs.next()) {
			if (lTimeout > Long.parseLong(rs.getString(2))) {
				lTimeout = Long.parseLong(rs.getString(2));
			}
		}
		pstmt.close();

		return lTimeout;
	}

	/**
	 * Shutdown the derby database application.
	 * @param nDatabaseType, the type opf the database to shutdown.
	 */
	public static void shutdownDerby(int nDatabaseType)
	{
		System.out.println("295 DBConnectionManager entered shutdownDerby, DERBY_URL is " + DERBY_URL);

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			try
			{
				DriverManager.getConnection(DERBY_URL+";shutdown=true");
			}
			catch(Exception ex) {
				System.out.println("303 DBConnectionManager Unable to shutdown Derby: "+ex.getMessage());
			}
		}

		System.out.println("307 DBConnectionManager sDatabaseName is " + sDatabaseName);

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			try
			{
				DriverManager.getConnection(DERBY_URL+sDatabaseName+";shutdown=true");
				System.out.println("Successful derby shutdown on " + DERBY_URL+sDatabaseName+";shutdown=true");
			}
			catch(Exception ex) {
				System.out.println("315 DBConnectionManager Unable to shutdown Derby: "+ex.getMessage());
			}
		}
		System.out.println("350 DBConnectionManager exiting shutdownDerby");
	}



	/**
	 * Constructor, creates a new Vector object to hold DBConnection references, and stored the database name.
	 * The database connection is created using the default name and password.
	 *
	 * @param String, the name of the database this connection manager is managing connections for.
	 * @see com.compendium.core.ICoreConstants#sDEFAULT_DATABASE_USER
	 * @see com.compendium.core.ICoreConstants#sDEFAULT_DATABASE_PASSWORD
	 */
	public DBConnectionManager(int nDatabaseType, String sDatabaseName) {
		//System.out.println("336 DBConnectionManager entered constructor, in params are " + nDatabaseType + " and " + sDatabaseName);

		this.nDatabaseType = nDatabaseType;
		this.sDatabaseName = sDatabaseName;
		//vtDBConnections = new Vector(100);

		if (nDatabaseType == ICoreConstants.MYSQL_DATABASE)
			this.sDatabaseURL = JDBC_MYSQL_URL+"//localhost/";
		else {
			System.out.println("345 DBConnectionManager constructor reading DERBY_URL " + DERBY_URL);
			this.sDatabaseURL = DERBY_URL;

		System.out.println("348 DBConnectionManager exiting constructor");
		System.out.flush();
		}
	}

	/**
	 * Constructor, creates a new Vector object to hold DBConnection references, and stored the database name.
	 * This constructor also takes a name and password to use when createing the connection to the database.
	 *
	 * @param String, the name of the database this connection manager is managing connections for.
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database
	 */
	public DBConnectionManager(int nDatabaseType, String sDatabaseName, String sDatabaseUserName, String sDatabasePassword) {
		//System.out.println("362 DBConnectionManager entered constructor 4 params, in sDatabaseName is " + sDatabaseName);
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseName = sDatabaseName;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		//vtDBConnections = new Vector(100);
		if (nDatabaseType == ICoreConstants.MYSQL_DATABASE)
			this.sDatabaseURL = JDBC_MYSQL_URL+"//localhost/";
		else {
			this.sDatabaseURL = DERBY_URL;
		}
	}


	/**
	 * Constructor, creates a new Vector object to hold DBConnection references, and stored the database name.
	 * This constructor also takes a name and password to use when createing the connection to the database.
	 *
	 * @param String, the name of the database this connection manager is managing connections for.
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database
	 * @param sDatabaseIP, the ip address or host name to use when connection to the database
	 */
	public DBConnectionManager(int nDatabaseType, String sDatabaseName, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {
		//System.out.println("382 DBConnectionManager entered constructor 5 params, in sDatabaseName is " + sDatabaseName);
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseName = sDatabaseName;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		//vtDBConnections = new Vector(100);

		if (nDatabaseType == ICoreConstants.MYSQL_DATABASE)
			this.sDatabaseURL = JDBC_MYSQL_URL+"//"+sDatabaseIP+"/";
		else {
			//System.out.println("372 reading DERBY_URL " + DERBY_URL);
			this.sDatabaseURL = DERBY_URL;
		}
	}

	/**
	 * Return the Name of the current database baing connected too.
	 *
	 * @return String, the name of the current database being connected too.
	 */
	public String getName() {
		return sDatabaseName;
	}

	/**
	 * Opens a new connection to the current database, and create a DBConnection object for it.
	 *
	 * @return DBConnection, the new DBConnection object created
	 */
	private DBConnection newConnection() throws SQLException, ClassNotFoundException {
		//System.out.println("416 DBConnectionManager entered newConnection, this.sDatabaseName is " + sDatabaseName);

		String url = sDatabaseURL + sDatabaseName;

		//System.out.println("420 DBConnectionManager url connecting to: "+url);

		Connection con = connect(url);
		DBConnection dbcon = new DBConnection(con, true, this.nDatabaseType);

		//System.out.println("425 DBConnectionManager adding to vtDBConnections");
		//vtDBConnections.addElement(dbcon);
		db_conn = dbcon;

		//System.out.println("429 DBConnectionManager exiting newConnection");

		return dbcon;
	}

	/**
	 * Creates a new connection to the database using the given url.
	 *
	 * @param url the url to use to connect to the database.
	 * @return Connection the new connection created to the given database url
	 * @exception java.sql.SQLException when the maximum number of connections exceeded
	 * @exception java.lang.ClassNotFoundException when driver class file cannot be created
	 */
	private Connection connect(String url) throws SQLException, ClassNotFoundException {

		//System.out.println("435 DBConnectionManager entered connect url is "+url);

		if (db_conn != null)
			return db_conn.getConnection();
		else
			System.out.println("449 DBConnectionManager db_conn is null");

		System.out.flush();

		// Attempt to connect to the database for which the driver is loaded.
		Driver driver = getDriver();

		Connection connection = null;
		if (driver == null)
			System.out.println("451 DBConnectionManager driver is null");

		if (driver != null) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE)
	  	        connection = DriverManager.getConnection(url);
	  	    else {
				System.out.println("464 DBConnectionManager sDatabaseUserName, sDatabasePassword are " +
					sDatabaseUserName + " and " + sDatabasePassword);
				System.out.flush();

	  	    	connection = DriverManager.getConnection(url, sDatabaseUserName, sDatabasePassword);
	  			long lTimeout = lgetMySQLServerTimeout(connection);
	  			if (lTimeout > 0) {
	  				DBConnection.setTimeouts((lTimeout/2)*1000);	// Cut server timeout in half, convert to milliseconds
	  			}
			}
		}
		//else {
		//	throw new SQLException("Maximum number of connections exceeded");
		//}

		//System.out.println("435 DBConnectionManager exiting connect");

		return connection;
	}

	/**
	 * Loads the driver used to commuicate with the database.
	 *
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException, when driver class file cannot be locateed
	 */
	public void loadDriver() throws SQLException, ClassNotFoundException {

		// USEFUL FOR DEBUGGING
		//java.sql.DriverManager.setLogStream(java.lang.System.out);

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			//Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			oDriver = new org.apache.derby.jdbc.EmbeddedDriver();
		}
		else {
			//Class.forName("org.gjt.mm.mysql.Driver");
			oDriver = new com.mysql.jdbc.Driver();
		}
	}

	/**
	 * Gets the driver used to commuicate with the database.
	 *
	 * @return Driver, the driver used to commuicate with the database.
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException
	 */
	public Driver getDriver() throws SQLException, ClassNotFoundException {

		if (oDriver == null) {
			loadDriver();
		}
		return oDriver;
	}

	/**
	 * Gets the first connection which is available
	 *
	 * @return DBConnection, an available DBConnection object
	 */
	public DBConnection getConnection() {

		DBConnection dbcon = null;
/*		int count = vtDBConnections.size();

		try {
			for(int i=0; i<count; i++) {
				if (vtDBConnections.size() == 0)
					break;

				dbcon = (DBConnection)vtDBConnections.elementAt(i);
				if (dbcon == null) {
					vtDBConnections.remove(dbcon);
					i--;
					count--;
				}
				else {
					if (dbcon.busyTimedOut()) {
						// JUST IN CASE LEFT LOCKING ROWS. FIXED DERBY BUG
						try {
							dbcon.getConnection().close();
						}
						catch(Exception ex) {
							ex.printStackTrace();
						}
						vtDBConnections.remove(dbcon);
						dbcon = null;
						i--;
						count--;
					} else if (dbcon.sessionTimedOut()) {
						vtDBConnections.remove(dbcon);
						dbcon = null;
						i--;
						count--;
					} else if(!dbcon.getIsBusy()) {
						try {
							if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
								// JUST INCASE LEFT LOCKING ROWS. FIXED DERBY BUG
								dbcon.getConnection().commit();
							}
							dbcon.setIsBusy(true);
							return dbcon;
						}
						catch(Exception e) {
							// COMMIT FAILED FOR SOME REASON JUST DROP CONNECTION
							try {
								dbcon.getConnection().close();
							}
							catch(Exception ex) {
								ex.printStackTrace();
							}
							vtDBConnections.remove(dbcon);
							dbcon = null;
							i--;
							count--;
						}
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.flush();
		}
*/
		try {

			dbcon = newConnection();

		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: (DBConnectionManager.getConnection) "+ex.getMessage());
			System.out.flush();
		}

		db_conn = dbcon;
		return dbcon;
	}


	/**
	 * Releases the given connection. If the connection has timed out, remove it.
	 *
	 * @param DBConnection dbcon, the connection object to release.
	 * @see com.compendium.core.db.management.DBConnection#timedOut
	 */
	public void releaseConnection(DBConnection dbcon) {

		if (dbcon != null) {
			if (dbcon.sessionTimedOut()) {
				//vtDBConnections.remove(dbcon);
				dbcon = null;
			} else 	if (dbcon.busyTimedOut()) {
				try {
					dbcon.getConnection().close();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				//vtDBConnections.remove(dbcon);
				dbcon = null;
			}
			else {
				dbcon.setIsBusy(false);
			}
		}
		db_conn = null;
	}

	/**
	 * Attempts to close all connections then removes them from this manager.
	 */
	public boolean removeAllConnections() {

		System.out.println("614 DBConnectionManager entered removeAllConnections");
/*
		int count = vtDBConnections.size();
		for(int i=0; i<count; i++) {
			DBConnection dbcon = (DBConnection) vtDBConnections.elementAt(i);
			try {
				if (dbcon.getConnection() != null)
					dbcon.getConnection().close();
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("633 DBConnectionManager exiting removeAllConnections with Exception, returning FALSE");
				return false;
			}
		}

		vtDBConnections.removeAllElements();
		System.out.println("633 DBConnectionManager exiting removeAllConnections ok, returning true");
*/
		db_conn = null;
		return true;
	}
}

