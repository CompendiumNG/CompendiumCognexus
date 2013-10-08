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

import java.util.*;
import java.sql.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The GroupCode service class provides remote services to manipuate GroupCode records in the database.
 *
 *	@author Michelle Bachler
 */
public class GroupCodeService extends ClientService implements IGroupCodeService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public GroupCodeService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public GroupCodeService(String sName) {
		super(sName);
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public GroupCodeService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Adds a new GroupCode record, assigning a Code to a CodeGroup and returns it if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeID, the id of the code to add to the given CodeGroup.
	 * @param String sCodeGroupID, the id of the CodeGroup the code is in.
	 * @param String sAuthor, the author of the Code to CodeGroup assignment.
	 * @param java.util.Date dCreationDate, the creation date of the new Code to CodeGroup assignment.
	 * @param java.util.Date dModificationDate, the modification date of the new Code to CodeGroup assignment.
	 *
	 * @return boolean, indicates if the new Code to CodeGroup assignment record was successfully added.
	 * @exception java.sql.SQLException
	 */
	public boolean createGroupCode(PCSession session, String sCodeID, String sCodeGroupID, String sAuthor,
									java.util.Date dCreationDate, java.util.Date dModificationDate) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSucessful = DBGroupCode.insert(dbcon, sCodeID, sCodeGroupID, sAuthor, dCreationDate, dModificationDate);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSucessful;
	}

	/**
	 * Delete the given code, groupcode record from the database and returns it if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeID, the id of the code to remove from the given CodeGroup.
	 * @param String sCodeGroupID, the id of the CodeGroup to remove the code from.
	 *
	 * @return boolean, indicates if the Code was removed from the CodeGroup successfully.
	 * @exception java.sql.SQLException
	 */
	public boolean delete(PCSession session, String sCodeID, String sCodeGroupID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean deleted = DBGroupCode.delete(dbcon, sCodeID, sCodeGroupID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Returns the CodeGroups from the Database for the given CodeID
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeID, the id of the code to return all the groups for.
	 * @return a Vector of information on the CodeGroups in the Database containing the given CodeID
	 * @exception java.sql.SQLException
	 */
	public Vector getCodeGroups(PCSession session, String sCodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtGroupCode = DBGroupCode.getCodeGroups(dbcon, sCodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtGroupCode;
	}

	/**
	 * Returns all the Codes in the Database in the CodeGroup with the given CodeGroupID
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeGroupID, the id of the CodeGroup to return the Codes for.
	 * @return a Vector of all the Codes in the Database for the given CodeGroupID
	 * @exception java.sql.SQLException
	 */
	public Vector getGroupCodes(PCSession session, String sCodeGroupID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtGroupCodes = DBGroupCode.getGroupCodes(dbcon, sCodeGroupID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtGroupCodes;
	}

	/**
	 * Returns all the Codes not in any group from the Database
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @return a Vector of all the Codes not in any code group
	 * @exception java.sql.SQLException
	 */
	public Vector getUngroupedCodes(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtCodes = DBGroupCode.getUngroupedCodes(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtCodes;
	}
}
