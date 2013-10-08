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

import java.sql.SQLException;
import java.util.Vector;

import com.compendium.core.SearchParams;
import com.compendium.core.datamodel.PCSession;

/**
 *	The interface for the QueryService class
 *	The QueryService class provides services to run general node search queries of the database.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */

public interface IQueryService extends IService {

	/**
	 * Returns a Vector of nodes given a keyword
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param SearchParams searchParams, the parameters of the user search.
	 *
	 * @return Vector, of NodeSummary objects resulting from executing the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchNode(PCSession session, SearchParams searchParams) throws SQLException;

	/**
	 * Return all nodes whose labels match exactly the given text.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sText, the text to match node label to.
	 * @param int nType, the node type to confine the match to.
 	 * @return Vector, of NodeSummary objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchExactNodeLabel(PCSession session, String sText, int nType) throws SQLException;

	/**
	 *	Return all nodes whose labels EQUAL the passed text and are in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sText, the text to match.
	 * 	@param nType, the type of the node to filter the search on.
	 * 	@param sViewID, the id of the view to search in.
	 * 	@return Vector, of <code>NodePosition</code> objects whose label matches the given text.
	 *	@throws java.sql.SQLException
	 */
	public Vector searchExactNodeLabelInView(PCSession session, String sText, int nType, String sViewID) throws SQLException;

	/**
	 *	Return all nodes whose labels starts with the passed text and are in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sText, the text to match.
	 * 	@param nType, the type of the node to filter the search on.
	 * 	@param sViewID, the id of the view to search in.
	 * 	@return Vector, of <code>NodePosition</code> objects whose label matches the given text.
	 *	@throws java.sql.SQLException
	 */
	public Vector searchNodeLabelInView(PCSession session, String sText, int nType, String sViewID) throws SQLException;

	/**
	 * Return all nodes whose labels start with the passed text, and are of the passed node type, but are not the given node.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sText, the text to match to the start of node labels.
	 * @param String sNodeID, the node to exclude from the results.
	 * @param int nType, the node type to confine the match to.
 	 * @return Vector, of NodeSummary objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchTransclusions(PCSession session, String text, String nodeid, int type) throws SQLException;

	/**
	 * Return all nodes whose labels start with the passed text and are not the given node.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sText, the text to match to the start of node labels.
	 * @param String sNodeID, the node to exclude from the results.
 	 * @return Vector, of NodeSummary objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchTransclusions(PCSession session, String text, String nodeid) throws SQLException;

	/**
	 * Return all nodes who have a triplestore original id and are in the given view.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the id of the view the node is in.
 	 * @return Vector, of NodePosition objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchForTripleStoreNodes(PCSession session, String sViewID) throws SQLException;
}
