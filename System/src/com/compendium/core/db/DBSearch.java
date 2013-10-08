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

package com.compendium.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.SearchParams;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.core.datamodel.View;
import com.compendium.core.db.management.DBConnection;

import static com.compendium.ProjectCompendium.*;
import static com.compendium.core.CoreUtilities.*;

/**
 *  The DBSearch class serves as the interface layer to make queries and searches into the
 *	the database.
 *
 *  @author	Rema Natarajan / Michelle Bachler / Lin Yang
 */
public class DBSearch {

	/** String to represent context -- current view or home window */
	public final static String CONTEXT_SINGLE_VIEW = "contextSingleView" ;

	/** String to represent context all the views in the database */
	public final static String CONTEXT_ALLVIEWS = "contextAllViews" ;

	/** String to represent context all views AND all deleted objects in the database */
	public final static String CONTEXT_ALLVIEWS_AND_DELETEDOBJECTS = "contextAllViewsAndDeletedObjects" ;

	/** String to represent match condition 'any' */
	public final static int MATCH_ANY = 0 ;

	/** String to represent match condition 'all' */
	public final static int MATCH_ALL = 1 ;


	/**
	 *	Return all nodes whose labels EQUAL the passed text.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param text, the text to match.
	 * 	@param type, the type of the node to filter the search on.
	 * 	@return Vector, of <code>NodeSummary</code> objects whose label matches the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchExactNodeLabel(DBConnection dbcon, String sText, int nType, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
										"ModificationDate, Label, Detail, LastModAuthor  " +
										"FROM Node WHERE Label LIKE ('"+sText+"')"+
										"AND NodeType = "+nType+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				vtNodes.addElement(node);
			}
		}
		pstmt.close();
		return vtNodes;
	}

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
	public static Vector searchNodeLabelInView(DBConnection dbcon, String sText, int nType, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		System.out.println("170 DBSearch");
		PreparedStatement pstmt = con.prepareStatement("Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
										"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, " +
										"ViewNode.XPos, ViewNode.YPos, ViewNode.CreationDate, ViewNode.ModificationDate " +
										"FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID " +
										"WHERE lower(Node.Label) LIKE ('"+sText.toLowerCase()+"%') AND Node.NodeType = "+nType+
										" AND ViewNode.ViewID LIKE ('"+sViewID+"')"+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;
		NodePosition nodePos = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				int 	nX			= rs.getInt(11);
				int 	nY			= rs.getInt(12);
				Date	oCreated	= new Date(new Double(rs.getString(13)).longValue());
				Date	oModified	= new Date(new Double(rs.getString(14)).longValue());
				// now that the node summary object is generated, create the node position object
				View view = View.getView(sViewID) ;
				nodePos = new NodePosition(view, node, nX, nY, oCreated, oModified);
				vtNodes.addElement(nodePos);
			}
		}
		pstmt.close();
		return vtNodes;
	}

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
	public static Vector searchExactNodeLabelInView(DBConnection dbcon, String sText, int nType, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
										"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, " +
										"ViewNode.XPos, ViewNode.YPos, ViewNode.CreationDate, ViewNode.ModificationDate " +
										"FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID " +
										"WHERE Node.Label LIKE ('"+sText+"') AND Node.NodeType = "+nType+
										" AND ViewNode.ViewID LIKE ('"+sViewID+"')"+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;
		NodePosition nodePos = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				int 	nX			= rs.getInt(11);
				int 	nY			= rs.getInt(12);
				Date	oCreated	= new Date(new Double(rs.getString(13)).longValue());
				Date	oModified	= new Date(new Double(rs.getString(14)).longValue());

				// now that the node summary object is generated, create the node position object
				View view = View.getView(sViewID) ;
				nodePos = new NodePosition(view, node, nX, nY, oCreated, oModified);
				vtNodes.addElement(nodePos);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 * Return all nodes with a triplestore original id in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param sViewID, the id of the view to search in.
	 * 	@return Vector, of <code>NodePosition</code> objects whose label start with the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchForTripleStoreNodes(DBConnection dbcon, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
										"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, " +
										"ViewNode.XPos, ViewNode.YPos, ViewNode.CreationDate, ViewNode.ModificationDate " +
										"FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID " +
										"WHERE ViewNode.ViewID='"+sViewID+"' "+
										"AND Node.OriginalID LIKE ('TS:%') "+
										"AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;
		NodePosition nodePos = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				int 	nX			= rs.getInt(11);
				int 	nY			= rs.getInt(12);
				Date	oCreated	= new Date(new Double(rs.getString(13)).longValue());
				Date	oModified	= new Date(new Double(rs.getString(14)).longValue());
				// now that the node summary object is generated, create the node position object
				View view = View.getView(sViewID) ;
				nodePos = new NodePosition(view, node, nX, nY, oCreated, oModified);
				vtNodes.addElement(nodePos);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 *	Return all nodes whose labels start with the passed text, and have the passed node type.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param text, the text to match.
	 * 	@param sNodeID, the id of the node being matched that therefore needs to be excluded from the results.
	 * 	@param type, the type of the node to filter the search on.
	 * 	@return Vector, of <code>NodeSummary</code> objects whose label start with the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchTransclusions(DBConnection dbcon, String text, String sNodeID, int type, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		System.out.println("313 DBSearch");
		PreparedStatement pstmt = con.prepareStatement("Select NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
										"ModificationDate, Label, Detail, LastModAuthor " +
										"FROM Node " +
										"WHERE lower(Label) LIKE('"+text.toLowerCase()+"%') AND NodeID NOT IN ('"+sNodeID+"') "+
										"AND NodeType = "+type+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				vtNodes.addElement(node);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 * Return all nodes whose labels start with the passed text.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param text, the text to match.
	 * 	@param sNodeID, the id of the node being matched that therefore needs to be excluded from the results.
	 * 	@return Vector, of <code>NodeSummary</code> objects whose label start with the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchTransclusions(DBConnection dbcon, String text, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		System.out.println("351 DBSearch");
		PreparedStatement pstmt = con.prepareStatement("Select NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
										"ModificationDate, Label, Detail, LastModAuthor  " +
										"FROM Node " +
										"WHERE lower(Label) LIKE('"+text.toLowerCase()+"%') AND NodeID NOT IN ('"+sNodeID+"') "+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				vtNodes.addElement(node);
			}
		}
		pstmt.close();
		return vtNodes;
	}


	/**
	 *	Searches the node table for nodes that satisfy user query conditions.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *  @param SearchParams searchParams, the parameters of the user search.
	 *  @param String userID, the user id from a session.
	 *	@return an Enumeration of <code>NodeSummary</code> objects that match the search query.
	 *	@throws java.sql.SQLException
	 */
	public static Enumeration searchAttribute(
			DBConnection dbcon, SearchParams params, String userID) throws SQLException
	{

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		String viewId = params.getViewId();

		//-----------------------------------------------------------------------
		//View
		Vector qv = new Vector();

		if (params.getContextCondition().equals(CONTEXT_SINGLE_VIEW))
		{

			if (viewId.equals(""))
			{
				throw new SQLException("invalid view id") ;
			}
			else
			{
				qv.add("ViewID = '" + viewId + "' ");
			}
		}

		String q = "Select distinct n.NodeID as NodeID " +
					"From ViewNode vn " +
					"join Node n on vn.NodeID = n.NodeID ";

		if (!params.getSelectedCodes().isEmpty())
		{
			q += "join NodeCode nc on nc.NodeID = n.NodeID " +
					"join Code c on nc.CodeID = c.CodeID ";
		}

		//------------------------------------------------------------------------
		// Creation and Modification dates
		if (params.getBeforeCreationDate() != null) {
			qv.add("n.CreationDate < " + doubleValue(params.getBeforeCreationDate()));
		}

		if (params.getAfterCreationDate() != null) {
			qv.add("n.CreationDate > " + doubleValue(params.getAfterCreationDate()));
		}

		if (params.getBeforeModificationDate() != null) {
			qv.add("n.ModificationDate < " + doubleValue(params.getBeforeModificationDate()));
		}

		if (params.getAfterModificationDate() != null) {
			qv.add("n.ModificationDate > " + doubleValue(params.getAfterModificationDate()));
		}

		//------------------------------------------------------------------------
		// Keywords
		// generate partial query for match condition on attrib
		if (!params.getKeywords().isEmpty()) {
			if (params.getMatchKeywordCondition() == MATCH_ALL) {
				qv.add(matchAttrib(dbcon, params, "AND"));
			}
			else if (params.getMatchCodesCondition() == MATCH_ANY) {
				qv.add(matchAttrib(dbcon, params, "OR"));
			}
			else {
				throw new SQLException("unknown match condition for Keywords") ;
			}
		}
		//------------------------------------------------------------------------
		// NodeTypes
		if (!params.getSelectedNodeTypes().isEmpty()) {
			qv.add(matchAttrib(params.getSelectedNodeTypes()));
		}
		//------------------------------------------------------------------------
		// Authors
		if (!params.getSelectedAuthors().isEmpty()) {
			qv.add(matchAttrib(dbcon, params, "OR"));
		}

		// now cover the 'delete' flag consideration
		// if context does not cover the deleted objects, exclude them from
		// the test results - limit here to only active objects.
		if (!(params.getContextCondition().equals(CONTEXT_ALLVIEWS_AND_DELETEDOBJECTS))) {
			if (params.getContextCondition().equals(CONTEXT_SINGLE_VIEW))
			{
				qv.add("( n.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
				" AND vn.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+ ") ");
			} else
			{
				qv.add("( n.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+") ");
			}
		}

		if (!params.getSelectedCodes().isEmpty())
		{
			String q2 = "c.Name IN (";
			for (int i=0 ;i<params.getSelectedCodes().size();i++)
			{
				Code code = (Code)params.getSelectedCodes().elementAt(i);
				if (i==0)
				{
					q2 += " '"+code.getName()+"'";
				}
				else
				{
					q2 += ", '"+code.getName()+"'";
				}
			}
			q2 += ")";
			qv.add(q2);
		}

		String final_query = q;

		if (qv.size() > 0)
		{
			//remove for condition when both label and detail are de-selected
			for (int i=0; i<qv.size(); i++)
			{
				if (qv.get(i).equals("()"))
					qv.remove(i);
			}

			final_query += " WHERE " + qv.get(0);
			for (int i=1; i<qv.size(); i++)
			{
				final_query += " AND " + qv.get(i);
			}
		}

		System.out.println("644 DBSearch new query is " + final_query);

		Statement	statement = con.createStatement() ;
		ResultSet	rs = statement.executeQuery(final_query) ;

		Vector vtn = new Vector();
		while (rs.next())
		{
			long start_time = System.currentTimeMillis();
			vtn.addElement(DBNode.processNode(dbcon, rs.getString("NodeID"), userID));
			long end_time = System.currentTimeMillis();
			System.out.println("476 DBSearch entered resultset iterator " + (end_time - start_time) + " msec.");

		}

		return (Enumeration)vtn.elements();
	}


	/**
	 *  Method to help generate a partial query for searching a set of attributes (columns) for any of the given keywords.
	 *  for eg. for the attribute "Label", for keywords "test1" and "test2", and the
	 *  conjunction "OR" the return value will be of the format
	 *  "Label Like 'test1' OR Label Like 'test2'"
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *  @param attrib, a list of fields to search on.
	 *	@param vKeywords, a Vector of the keywords to search on.
	 *  @param searchParams, an Object with search params like conjunction, etc...
	 * 	@param String, the partial SQL string to search on for the given parameters.
	 */
	private static String matchAttrib(DBConnection dbcon, SearchParams searchParams, String conjunction) {

		System.out.println("628 DBSearch entered matchAttrib");

		Enumeration attributes = searchParams.getAttrib().elements();
		//while(attributes.hasMoreElements())
		//	System.out.println("644 DBSearch attributes " + attributes.nextElement());

		Enumeration keywords = searchParams.getKeywords().elements();
		//while(keywords.hasMoreElements())
		//	System.out.println("647 DBSearch keywords " + keywords.nextElement());

		Enumeration authors = searchParams.getSelectedAuthors().elements();
		//while(authors.hasMoreElements())
		//	System.out.println("651 DBSearch authors " + authors.nextElement());


		System.out.println("636 DBSearch attributes " + attributes + " hasMoreElements " + attributes.hasMoreElements());
		System.out.println("637 DBSearch keywords " + keywords + " hasMoreElements " + keywords.hasMoreElements());
		System.out.println("637 DBSearch authors " + authors + " hasMoreElements " + authors.hasMoreElements());

//		if (!attributes.hasMoreElements() || !keywords.hasMoreElements())
//		{
//			System.out.println("637 DBSearch exiting matchAttrib returning null");
//			return null;
//		}

		Vector full_list = searchParams.getKeywords();
		full_list.addAll(searchParams.getSelectedAuthors());

		// attach the authors to the end of keywords?  or make a new vector of both?

		List<String> attrClauses = new ArrayList<String>();
		while (attributes.hasMoreElements()) {
			String attrib = (String) attributes.nextElement();
			System.out.println("655 attrib is " + attrib);
			List<String> keywordClauses = new ArrayList<String>();
			//keywords = searchParams.getKeywords().elements();
			keywords = full_list.elements();
			while (keywords.hasMoreElements()) {
				//attrib = "Label";
				Object keyword = keywords.nextElement() ;
				String keywordString = "";
				boolean isCode = false;
				boolean isAuthor = false;

				//get string for UserProfiles
				if (keyword instanceof UserProfile) {
					isAuthor = true;
					keywordString = ((UserProfile) keyword).getUserName();
					System.out.println("668 DBSearch java UserProfile " + keywordString);
					keywordString = CoreUtilities.cleanSQLText(keywordString, APP_PROPERTIES.getDatabaseType());	// Solves SQL err when searching for names like O'Toole
					attrib = "n.Author";
				} else if (keyword.getClass() == Code.class) {
					isCode = true;
					keywordString = ((Code) keyword).getName();
					System.out.println("673 DBSearch java Class " + keywordString);
				} else {
					keywordString = (String) keyword;
					System.out.println("676 DBSearch java 'else' " + keywordString);
				}

				//String collate = dbcon.isMySql() ? "COLLATE latin1_general_cs" : "";
				String collate = "";
				String keywordClause;
				if (searchParams.isMatchWholeWords()) {
					if (dbcon.isMySql()) {
						keywordClause = " lower(" + attrib + ") RLIKE " + "'[[:<:]]" + keywordString.toLowerCase() + "[[:>:]]' " + collate;
					} else if (dbcon.isDerby()) {
						keywordClause = " RLIKE('\\b" + keywordString.toLowerCase() + "\\b', lower(" + attrib + ")) = 1 ";
					} else {
						throw new RuntimeException("The underlying database type is not supported.");
					}
				} else {
					System.out.println("690 DBSearch not match whole words");
					String likePattern = (isCode || isAuthor ? "'" + keywordString.toLowerCase() + "' " : "lower('%" + keywordString + "%') ");
					keywordClause = " lower(" + attrib + ") Like " + likePattern + collate;
				}
				keywordClauses.add(keywordClause);
			}
			System.out.println("700 DBSearch attrClauses is " + attrClauses);
			attrClauses.add("(" + CoreUtilities.join(keywordClauses, " " + conjunction + " ") + ")");
			System.out.println("702 DBSearch attrClauses is " + attrClauses);
		}

		System.out.println("678 DBSearch exiting matchAttrib clause is " + "(" + CoreUtilities.join(attrClauses, " OR ")+ ")");

		return "(" + CoreUtilities.join(attrClauses, " OR ")+ ")";
	}

	/**
	 * This methods tests the text for the regex regular expression.
	 * The method returns 1 if there is atleast one match or 0 otherwise.
	 * <p>
	 * This method is used define RLIKE function for Derby database.
	 *
	 * @param regex The regular expression.
	 * @param text  The exemined text.
	 * @return 1 if text matches with the regex. Othewise 0.
	 */
  	public static int rlike(String regex, String text) {
  		return Pattern.compile(regex).matcher(text).find() ? 1 : 0;
  	}

	/**
	 *  Method to help generate a partial query for searching NodeType
	 *  Added to correct the NodeType mismatch.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param vKeywords, a Vector of the keywords to search on.
	 * 	@param String, the partial SQL string to search on for the given parameters.
	 */
	private static String matchAttrib(Vector vKeywords) {

		boolean firstKeyword = true;

		if ((vKeywords.elements()).hasMoreElements() == false) {
			return null ;
		}

		String partialQuery = " ( NodeType =  ";
		String operator = "OR";

		firstKeyword = true;
		Enumeration keywords = vKeywords.elements();
		do {
			Object nextKeyword = (Object)keywords.nextElement() ;
			String sNextKeyword = "";

			//get string for UserProfiles
			if (nextKeyword.getClass() == UserProfile.class) {
				sNextKeyword = ((UserProfile) nextKeyword).getLoginName();
			}
			else if (nextKeyword.getClass() == Code.class) {
				sNextKeyword = ((Code) nextKeyword).getName();
			}
			else {
				sNextKeyword = (String) nextKeyword;
			}
			if (firstKeyword) {
				partialQuery = partialQuery + sNextKeyword + " ";
				firstKeyword = false;
			}
			else {
				partialQuery = partialQuery + operator + " NodeType = " + sNextKeyword + " " ;
			}
		}
		while (keywords.hasMoreElements());

		partialQuery = partialQuery + ")";
		return partialQuery;
	}

// FOR FUTURE USE
	/*private static String matchTags(Vector vKeywords) {

		String partialQuery = " CodeNode.CodeID = ALL ( SELECT CodeID FROM Code WHERE (";

		if (vKeywords.elements().hasMoreElements() == false) {
			return null;
		}

		boolean firstKeyword = true;
		Enumeration keywords = vKeywords.elements();
		do {
			Object nextKeyword = (Object)keywords.nextElement() ;
			String sNextKeyword = "";
			sNextKeyword = ((Code) nextKeyword).getName();
			if (firstKeyword) {
				partialQuery = partialQuery + "Code.CodeName LIKE '" + sNextKeyword + "'";
				firstKeyword = false;
			}
			else {
				partialQuery = partialQuery + " AND Code.CodeName LIKE '" + sNextKeyword + "'" ;
			}
		}
		while (keywords.hasMoreElements());

		partialQuery = partialQuery + " ))";

		return partialQuery;
	}*/
}
