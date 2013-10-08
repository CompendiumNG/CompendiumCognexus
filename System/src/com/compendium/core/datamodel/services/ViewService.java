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
import java.util.Date;
import java.awt.*;
import java.sql.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The ViewService class provides services to manipuate view objects in the database.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */
public class ViewService extends ClientService implements IViewService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public ViewService() {
		super() ;
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param sName the name of this service.
	 */
	public ViewService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param name the unique name of this service
 	 * @param sm the current ServiceManager
	 * @param dbMgr the current DBDatabaseManager
	 */
	public  ViewService(String name, ServiceManager sm,  DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr);
	}

	/**
	 * Adds a node to this view at the given x and y coordinate without formatting data
	 * Be warned: This node will not display correctly in the Compendium UI
	 *
	 * @param session com.compendium.core.datamodel.PCSession the session object for the current database Model.
	 * @param view com.compendium.core.datamodel.View the view  to add the node to.
	 * @param node com.compendium.core.datamodel.NodeSummary the node to add
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @param creation the creation date for this nodeview record.
	 * @param modification the last modified date for this viewnode record.
	 * @return com.compendium.core.datamodel.NodePosition the nodeposition if the node was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.INodePosition
	 */
	public NodePosition addMemberNode(PCSession session, View view, NodeSummary node, int x, int y,
						java.util.Date creation, java.util.Date modification) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodePosition nodePos = DBViewNode.insert(dbcon, view, node, x, y, creation, modification, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return nodePos;
	}

	/**
	 * Adds a node to this view at the given x and y coordinate
	 *
	 * @param session com.compendium.core.datamodel.PCSession the session object for the current database Model.
	 * @param view com.compendium.core.datamodel.View the view  to add the node to.
	 * @param node com.compendium.core.datamodel.NodeSummary the node to add
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @param creation the creation date for this nodeview record.
	 * @param modification the last modified date for this viewnode record.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * @return com.compendium.core.datamodel.NodePosition the nodeposition if the node was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.INodePosition
	 */
	public NodePosition addMemberNode(PCSession session, View view, NodeSummary node, int x, int y,
						java.util.Date creation, java.util.Date modification, boolean bShowTags,
						boolean bShowText, boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodePosition nodePos = DBViewNode.insert(dbcon, view, node, x, y, creation, modification, session.getUserID(),
				bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon, nWrapWidth,
				nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return nodePos;
	}

	/**
	 *  Update the formatting properties for the given node in the given view.
	 *
	 *  @param session com.compendium.core.datamodel.PCSession the session object for the current database Model.
	 *  @param sViewID the view of the node whose formatting properties to update.
	 *  @param sNodeID the node whose formatting properties to update.
	 *  @param modification the last modified date for this viewnode record.
	 * 	@param bShowTags true if this node has the tags indicator draw.
	 *	@param bShowText true if this node has the text indicator drawn
	 * 	@param bShowTrans true if this node has the transclusion indicator drawn
	 * 	@param bShowWeight true if this node has the weight indicator displayed
	 * 	@param bSmallIcon true if this node is using a small icon
	 * 	@param bHideIcons true if this node is not displaying its icon
	 * 	@param nWrapWidth the node label wrap width used for this node in this view.
	 * 	@param nFontSize	the font size used for this node in this view
	 * 	@param sFontFace the font face used for this node in this view
	 * 	@param nFontStyle the font style used for this node in this view
	 * 	@param nForeground the foreground color used for this node in this view
	 * 	@param nBackground the background color used for this node in this view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean updateFormatting(PCSession session, String sViewID, String sNodeID,
						java.util.Date modification, boolean bShowTags,	boolean bShowText,
						boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean success = DBViewNode.updateFormatting(dbcon, sViewID, sNodeID, modification,
				bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon, nWrapWidth,
				nFontSize, sFontFace, nFontStyle, nForeground, nBackground, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return success;
	}

	/**
	 *  Update the formatting properties for the given node in all its views.
	 *
	 * @param session com.compendium.core.datamodel.PCSession the session object for the current database Model.
	 *  @param sNodeID the node id of the node to update the formatting in all view for.
	 *  @param modification the last modified date for this viewnode record.
	 * 	@param bShowTags true if this node has the tags indicator draw.
	 *	@param bShowText true if this node has the text indicator drawn
	 * 	@param bShowTrans true if this node has the transclusion indicator drawn
	 * 	@param bShowWeight true if this node has the weight indicator displayed
	 * 	@param bSmallIcon true if this node is using a small icon
	 * 	@param bHideIcons true if this node is not displaying its icon
	 * 	@param nWrapWidth the node label wrap width used for this node in this view.
	 * 	@param nFontSize	the font size used for this node in this view
	 * 	@param sFontFace the font face used for this node in this view
	 * 	@param nFontStyle the font style used for this node in this view
	 * 	@param nForeground the foreground color used for this node in this view
	 * 	@param nBackground the background color used for this node in this view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean updateTransclusionFormatting(PCSession session, String sNodeID,
						java.util.Date modification, boolean bShowTags,	boolean bShowText,
						boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean success = DBViewNode.updateTransclusionFormatting(dbcon, sNodeID, modification,
				bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon, nWrapWidth,
				nFontSize, sFontFace, nFontStyle, nForeground, nBackground, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return success;
	}
	/**
	 *<p>
	 * Marks for deletion the node with the given id from this view with the given view id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID the id of the view to remove the node from.
	 * @param sNodeID the id of the node to be removed from this view
	 * @return if deletion was successful
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean removeMemberNode(PCSession session, String sViewID, String sNodeID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean bSuccessful = DBViewNode.delete(dbcon, sViewID, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return bSuccessful;
	}

	/**
	 *<p>
	 * Purges from the database the record of the node with the given id from this view with the given view id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID the id of the view to remove the node from.
	 * @param sNodeID the id of the node to be removed from this view
	 * @return if purging was successful
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean purgeMemberNode(PCSession session, String sViewID, String sNodeID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean bSuccessful = DBViewNode.purge(dbcon, sViewID, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return bSuccessful;
	}

	/**
	 * Returns the nodeposition with the given id
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID the id of the view the node is in.
	 * @param sNodeID The id of the node to get the position for.
	 * @return com.compendium.core.datamodel.NodePosition, the nodeposition if it was found, else null.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public NodePosition getNodePosition(PCSession session, String sViewID, String sNodeID) throws NoSuchElementException , SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodePosition nodePos = DBViewNode.getNodePosition(dbcon, sViewID, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return nodePos;
	}

	/**
	 * Returns all the nodepositions in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to get the node position objects for.
	 * @return java.util.Vector, a Vector of all the nodepositions in the given view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public Vector getNodePositions(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodePos = DBViewNode.getNodePositions(dbcon, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtNodePos;
	}

	/**
	 * Returns all the nodepositionssummary objects for this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to get the node position objects for.
	 * @return java.util.Vector, a Vector of all the nodepositions in the given view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public Vector getNodePositionsSummary(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodePos = DBViewNode.getNodePositionsSummary(dbcon, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtNodePos;
	}

	/**
	 *	Returns TRUE if the given view has been modified since it was last loaded.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the View to check.
	 *	@param sUserName the current users's name
	 *	@param dLastViewModDate - date od last modification by another user that we're aware of
	 *	@return Boolean, TRUE if the view is 'dirty'.
	 *	@throws java.sql.SQLException
	 */
	public Boolean bIsViewDirty(PCSession session, String sViewID, String sUserName, Date dLastViewModDate) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Boolean bResult = DBViewNode.bIsViewDirty(dbcon, sViewID, sUserName, dLastViewModDate);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return bResult;

	}

	/**
	 * Returns nodepositions count for this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to get the node position count for.
	 * @return int, a int of the nodeposition count for the given view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public int getNodeCount(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		int count = DBViewNode.getNodeCount(dbcon, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return count;
	}

	/**
	 * Sets the position of the node with the given id in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to add the node position to.
	 * @param sNodeID, the id of the node whose position to add to the given view.
	 * @param p, The new position of the node.
	 * @return boolean, true if the node position was successfully set, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean setNodePosition(PCSession session, String sViewID, String sNodeID, Point p) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean updated = DBViewNode.setNodePosition(dbcon, sViewID, sNodeID, p, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return updated;
	}

	/**
	 * Adds a link to the View with the given view id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to add the link to.
	 * @param sLinkID, the linkid of the link to be added to the view.
	 * @return true if the link was successfully added, else false.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.ILink
	 */
	public boolean addMemberLink(PCSession session, String sViewID, String sLinkID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean added = DBViewLink.insert(dbcon, sViewID, sLinkID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return added;
	}

	/**
	 * Restores all links to this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View whose links to restore.
	 * @return boolean, true if the links were successfully restored, else false.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.ILink
	 */
	public boolean restoreViewLinks(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean added = DBViewLink.restoreViewLinks(dbcon, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return added;
	}

	/**
	 * Restores the given link in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View to restore the link in.
	 * @param sLinkID, the link id of the Link to restore.
	 * @return boolean, true if the link was successfully restored, else false.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.ILink
	 */
	public boolean restoreLink(PCSession session, String sViewID, String sLinkID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean added = DBViewLink.restore(dbcon, sViewID, sLinkID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return added;
	}

	/**
	 * Returns the link with the given id
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View the link to return is in.
	 * @param sLinkID, the link id of the Link to return.
	 * @return com.compendium.core.datamodel.Link, the link if it was found.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.Link
	 */
	public Link getLink(PCSession session, String sViewID, String sLinkID) throws NoSuchElementException , SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Link link = DBViewLink.getLink(dbcon, sViewID, sLinkID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return link;
	}

	/**
	 * Returns all the links in the View with the given id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View whose links to return.
	 * @return java.util.Vector, a vector of all the links in this view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.Link
	 */
	public Vector getLinks(PCSession session, String sViewID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtLink = DBViewLink.getLinks(dbcon, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtLink;
	}

	/**
	 * Returns all the link IDs in the View with the given id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View whose links to return.
	 * @return java.util.Vector, a vector of all the links in this view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.Link
	 */
	public Vector getLinkIDs(PCSession session, String sViewID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtLink = DBViewLink.getLinkIDs(dbcon, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtLink;
	}

	/**
	 * Does the View with the given id contain itself?
	 *
	 * @param session the session object for the current database Model.
	 * @param sViewID, the view id of the View to check.
	 * @return boolean, true if the View with the given id contains itself, else false.
	 * @exception java.sql.SQLException
 	 */
	public boolean isViewContainsItself(PCSession session, String viewId) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean viewContainsItself = DBViewNode.isViewContainsItself(dbcon, viewId);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return viewContainsItself;
	}

// NEW FORMATTING UPDATE METHODS

	/**
	 * Set the font size for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the size for
	 * @param size the font size to set
	 * @return
	 * @throws SQLException
	 */
	public boolean setFontSize(PCSession session, String sViewID, Vector vtPositions, int nFontSize) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setFontSize(dbcon, sViewID, vtPositions, nFontSize);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the font face for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the size for
	 * @param sFontFace the font face to set
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setFontFace(PCSession session, String sViewID, Vector vtPositions, String sFontFace) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setFontFace(dbcon, sViewID, vtPositions, sFontFace);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the font style for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the font style for
	 * @param nFontStyle the font style to set
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setFontStyle(PCSession session, String sViewID, Vector vtPositions, int nFontStyle) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setFontStyle(dbcon, sViewID, vtPositions, nFontStyle);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the font style for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the wrap width for
	 * @param nWidth the wrap width to set
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setWrapWidth(PCSession session, String sViewID, Vector vtPositions, int nWidth) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setWrapWidth(dbcon, sViewID, vtPositions, nWidth);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the node tags indicator for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the indicator for
	 * @param bShow true to set this indicator
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setShowTagsIndicator(PCSession session, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setShowTagsIndicator(dbcon, sViewID, vtPositions, bShow);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the node text indicator for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the indicator for
	 * @param bShow true to set this indicator
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setShowTextIndicator(PCSession session, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setShowTextIndicator(dbcon, sViewID, vtPositions, bShow);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the node transclusion indicator for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the indicator for
	 * @param bShow true to set this indicator
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setShowTransIndicator(PCSession session, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setShowTransIndicator(dbcon, sViewID, vtPositions, bShow);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the node weight indicator for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set the indicator for
	 * @param bShow true to set this indicator
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setShowWeightIndicator(PCSession session, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setShowWeightIndicator(dbcon, sViewID, vtPositions, bShow);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set showing small icons for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set small icons for
	 * @param bShow true to set using small icons
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setShowSmallIcons(PCSession session, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setShowSmallIcons(dbcon, sViewID, vtPositions, bShow);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set hiding icons for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set hiding icons for
	 * @param bHide true to hide the icons
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setHideIcons(PCSession session, String sViewID, Vector vtPositions, boolean bHide) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setHideIcons(dbcon, sViewID, vtPositions, bHide);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the foreground colour for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set text foreground colour for
	 * @param nColour the foreground colour to set
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setTextForeground(PCSession session, String sViewID, Vector vtPositions, int nColour) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setTextForeground(dbcon, sViewID, vtPositions, nColour);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Set the background colour for the given NodePosition objects
	 * @param session the session object for the current database Model.
	 * @param sViewID the id of the view the nodes are in.
	 * @param vtPositions the list of NodePosition objects to set text background colour for
	 * @param nColour the background colour to set
	 * @return true if the update was successful.
	 * @throws SQLException
	 */
	public boolean setTextBackground(PCSession session, String sViewID, Vector vtPositions, int nColour) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBViewNode.setTextBackground(dbcon, sViewID, vtPositions, nColour);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}
}
