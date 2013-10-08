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

package com.compendium.core.datamodel.services;

import java.sql.*;
import java.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The interface for the ViewLayerService class
 *	The ViewLayer service class provides services to manipuate ViewLayer objects.
 *
 *	@author Michelle Bachler
 */
public class ViewLayerService extends ClientService implements IViewLayerService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public  ViewLayerService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public ViewLayerService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param String name, the unique name of this service
 	 * @param ServiceManager sm, the current ServiceManager
	 * @param DBDtabaseManager dbMgr, the current DBDatabaseManager
	 */
	public  ViewLayerService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Adds a new view layer to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user creating ths view property.
	 * @param ViewLayer view, the ViewLayer object to create a record for.
	 * @return boolean, true if the *lastd* creation was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean createViewLayer( PCSession session, String sUserID, ViewLayer view) throws SQLException
	{

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		Connection con = dbcon.getConnection();

		boolean isSuccessful = false;

		try
		{
			Statement stmt = con.createStatement();

			//derby doesn't support the trim() function
			ResultSet tmp = stmt.executeQuery("Select distinct Background from ViewLayer " +
				"where ViewID ='" + view.getViewID() + "' and Background is not null and rtrim(ltrim(Background)) <> '' " +
				"order by Background desc");

			if (tmp.next())
			{
				showTrace("96 ViewLayerService bg was " + view.getBackground() + " bg is now " + tmp.getString("Background"));
				view.setBackground(tmp.getString("Background"));
				showTrace("99 ViewLayerService bg is now " + view.getBackground() );
			}

			// in derby scribble is apparently a LONG VARCHAR type and cannot be part of a comparison statement.
			// Q: should this return only one row?
			tmp = stmt.executeQuery("Select Scribble from ViewLayer " +
				"where ViewID ='" + view.getViewID() + "'"  //and Scribble is not null " + //and rtrim(ltrim(Scribble)) <> '' " +
				//"order by Scribble desc"
				);

			if (tmp.next())  //if there's more than one take the first one, i guess.
			{
				showTrace("scribble was " + view.getScribble() + " scribble is now " + tmp.getString("Scribble"));
				view.setScribble(tmp.getString("Scribble"));
			}
/*
			tmp = stmt.executeQuery("Select distinct Grid from ViewLayer " +
				"where ViewID ='" + view.getViewID() + "' " +
				"order by Grid desc limit 1");
			while (tmp.next())
				view.setGrid(tmp.getString("Grid"));

			tmp = stmt.executeQuery("Select distinct Shapes from ViewLayer " +
				"where ViewID ='" + view.getViewID() + "' " +
				"order by Shapes desc limit 1");
			while (tmp.next())
				view.setShapes(tmp.getString("Shapes"));
*/
			stmt.executeUpdate("update ViewLayer set ModificationDate = " +
				new Long((new java.util.Date()).getTime()).doubleValue() +
				" where ModificationDate is null");

			//get the users who don't already exist in the ViewLayer table with the view in question.
			//this should handle existing views created by another when the bg image changes

			String sql = "SELECT distinct UserID FROM Users where UserID not in " +
				"(select distinct UserID from ViewLayer where " +
				//"UserID='" + sUserID + "' and " +
				"ViewID='" + view.getViewID() + "'" +
					" group by UserID)";

			showTrace("sql is " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next())
			{
				showTrace("userID is " + rs.getString("UserID"));
				isSuccessful = DBViewLayer.insert(dbcon, rs.getString("UserID"), view);
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			System.out.println("Exception ViewLayerService 150: "+ex.getErrorCode());
			System.out.println("Exception ViewLayerService 150: "+ex.getSQLState());
			System.out.println("Exception ViewLayerService 150: "+ex.getMessage());
		}

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Update a view layer to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the userupdating this view property.
	 * @param ViewLayer view, the ViewLayer object to update the record for.
	 * @return boolean, true if the update was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean updateViewLayer( PCSession session, String sUserID, ViewLayer view) throws SQLException
	{
		showTrace("entered updateViewLayer");


		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSuccessful = DBViewLayer.update(dbcon, sUserID, view);

		//the view might not exist for all users.
		createViewLayer(session, sUserID, view);


		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		showTrace("exiting updateViewLayer with isSuccessful of " + isSuccessful);

		return isSuccessful;
	}

	/**
	 * Deletes a view layer from the database and returns true if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user updating this view property.
	 * @param String sViewID, the id of the view for the view layer to delete.
	 * @return boolean, true if the update was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteViewLayer(PCSession session, String sUserID, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBViewLayer.delete(dbcon, sUserID, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Get the view layer record for the given user and view id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user whose view layer to return.
	 * @param String sViewID, the id of the view for the view layer to return.
	 * @return ViewLayer, the ViewLayer record for the user id and view id given.
	 * @exception java.sql.SQLException
	 */
	public ViewLayer getViewLayer(PCSession session, String sUserID, String sViewID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		ViewLayer viewLayer = DBViewLayer.getViewLayer(dbcon, sUserID, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return viewLayer;
	}

	public static void showTrace(String msg)
	{
		  //if (msg.length() > 0) showTrace(msg);
		  System.out.println(
		  	       new Throwable().getStackTrace()[1].getLineNumber() +
		           " " + new Throwable().getStackTrace()[1].getFileName() +
		           " " + new Throwable().getStackTrace()[1].getMethodName() +
		           " " + msg);
	}
}
