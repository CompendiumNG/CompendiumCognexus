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

package com.compendium.io.xml;

import java.util.*;
import java.net.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import javax.swing.*;


import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.UIProgressDialog;
import com.compendium.ui.edits.AlignEdit;

import org.w3c.dom.*;

/**
 * Flashmeeting XMLImport imports Flashmeeting xml text file conforming to the Flashmeeting dtd.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class FlashMeetingXMLImport extends Thread {

	/** The label for the map node holding keyword information.*/
	public static final String 	KEYWORDS_LABEL		=	"Keywords";

	/** The label for the map node holding attendee information.*/
	public static final String 	ATTENDEE_LABEL		=	"Attendees";

	/** The label for the map node holding playlist information.*/
	public static final String 	PLAYLIST_LABEL		=	"Who spoke when";

	/** The label for the map node holding url information.*/
	public static final String 	URL_LABEL			=	"URLs visited";

	/** The label for the map node holding chat information.*/
	public static final String 	CHAT_LABEL			=	"Chat";

	/** The label for the map node holding whilte board information.*/
	public static final String 	WHITEBOARD_LABEL	=	"Whiteboard";

	/** The label for the map node holding file data information.*/
	public static final String 	FILEDATA_LABEL		=	"Meeting files";

	/** The label for the map node holding annotations information.*/
	public static final String 	ANNOTATIONS_LABEL	=	"Annotations";

	/** The label for the map node holding voting information.*/
	public static final String 	VOTING_LABEL		=	"Voting";

	private static final Color LABEL_COLOUR			=   new Color(102, 102, 255);


	private int nKeywordCount = 0;
	private int nPlaylistCount = 0;
	private int nAttendeeCount = 0;
	private int nChatCount = 0;
	private int nURLCount = 0;
	private int nWhiteboardCount = 0;
	private int nAnnotationCount = 0;
	private int nFiledataCount = 0;
	private int nVoteCount = 0;

	private int nCurrentKeywordCount = 0;
	private int nCurrentPlaylistCount = 0;
	private int nCurrentAttendeeCount = 0;
	private int nCurrentChatCount = 0;
	private int nCurrentURLCount = 0;
	private int nCurrentWhiteboardCount = 0;
	private int nCurrentAnnotationCount = 0;
	private int nCurrentFiledataCount = 0;
	private int nCurrentVoteCount = 0;

	/** The x offset to place AgendaItem, Document and Attendee nodes at.*/
	private static final int		X_OFFSET			=	0;

	/** The spacer between nodes on the y axis.*/;
	private static final int		Y_SPACER			=	65;

	/** The IModel object for the current database connection.*/
	private IModel				oModel 				= null;

	/** The current Session object.*/
	private PCSession 			oSession 			= null;

	/** The current map (if there is one), being imported into.*/
	private ViewPaneUI			oViewPaneUI 		= null;

	/** The current list (if there is one), being imported into.*/
	private UIList				oUIList 			= null;

	/** The file name of the xml file to import.*/
	private String				sFileName			= "";

	/** The id of the root view in the import data.*/
	private String 				sRootView 			= "";

	/** Holds the author name of the current user.*/
	private String				sAuthor		= "";

	/** Indicates if the user has cancelled the import.*/
	private boolean 			bXMLImportCancelled = false;

	// FOR PROGRESS BAR
	/** The dialog which displays the progress bar.*/
	private UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar.*/
	private JProgressBar		oProgressBar 		= null;

	/** The Thread class which runs the progress bar.*/
	private ProgressThread		oThread 			= null;

	private Vector			htChosenElements	= null;

	/** The x position of the main map.*/
	private int nMainMapX				= 200;

	/** The y position of the main map.*/
	private int nMainMapY				= 200;

	/** The main url stub for jumps.*/
	private String sMeetingReplayURLSTUB		= "";

	/** The url stub to use to access the folksonomy.*/
	private String sMeetingFolksonomyURLSTUB	= "";

	/** The url stub to use to access the files.*/
	private String sMeetingMediaURLSTUB	= "";

	/** Holds the attendee information.*/
	private Hashtable people					= null;

	/**
	 * Constructor.
	 *
	 * @param fileName, the name of the xml file to import.
	 * @param model com.compendium.core.datamodel.IModel, the current model object.
	 * @param htChosenElements a Hashtable listing which parts of the XML the user wants to import.
	 * be added to the node detail text, else false.
	 */
	public FlashMeetingXMLImport(String fileName, IModel model, Vector htChosenElements) {

		this.sFileName = fileName;
		this.htChosenElements = htChosenElements;
		this.oModel = model;
		if (oModel == null)
			oModel = ProjectCompendium.APP.getModel();

		this.sAuthor = oModel.getUserProfile().getUserName();
		this.oSession = oModel.getSession();
	}

	/**
	 * Constructor.
	 *
	 * @param fileName, the name of the xml file to import.
	 * @param model com.compendium.core.datamodel.IModel, the current model object.
	 * @param htChosenElements a Hashtable listing which parts of the XML the user wants to import.
	 * @param x the x location to put the main map (if current map not a list).
	 * @param y the y location to put the main map (if current map not a list).
	 * be added to the node detail text, else false.
	 */
	public FlashMeetingXMLImport(String fileName, IModel model, Vector htChosenElements, int x, int y) {

		this.nMainMapX = x;
		this.nMainMapY = y;
		this.sFileName = fileName;
		this.htChosenElements = htChosenElements;
		this.oModel = model;
		if (oModel == null)
			oModel = ProjectCompendium.APP.getModel();

		this.sAuthor = oModel.getUserProfile().getUserName();
		this.oSession = oModel.getSession();
	}

	/**
	 * Start the import thread and progress bar, and calls <code>createDom</code>
	 * to begin the import.
	 */
	public void run() {

  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		// MAKES IT STOP WORKING IF I USE?
		oThread = new ProgressThread();
		oThread.start();

		DBNode.setImporting(true);
        createDom( sFileName );
		DBNode.restoreImportSettings();

		ProjectCompendium.APP.setTrashBinIcon();

		oProgressDialog.setVisible(false);

		ProjectCompendium.APP.scaleAerialToFit();

		oProgressDialog.dispose();
	}

	/**
	 * The Thread class which draws the process bar dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,"XML Import Progress..", "Import completed");
	  		oProgressDialog.showDialog(oProgressBar, false);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}

	/**
	 * Load the XML from the given uri and read it.
	 * Object the loaded document and process it.
	 *
	 * @param uri, the uri of the XML document to load/process.
	 */
    private void createDom( String uri ){

        try {
			XMLReader reader = new XMLReader();
			Document document = reader.read(uri, true);
			if (document != null) {
				processDocument( document );
			} else {
				ProjectCompendium.APP.displayError("Exception: Your document cannot be imported.\n");
			}
			document = null;
        }
		catch ( Exception e ) {
			ProjectCompendium.APP.displayError("Exception: Your document cannot be imported.\n");
			e.printStackTrace();
        }
    }

	//*************************************************************************************************//

	/**
	 * Process the given XML document and create all the view, nodes, links, codes etc required.
	 * @param document, the XML Document object to process.
	 */
	private void processDocument( Document document ) {
		try {
	 		ProjectCompendium.APP.setWaitCursor();
	 		UIViewFrame oViewFrame =ProjectCompendium.APP.getCurrentFrame();

			NodeList events = document.getElementsByTagName("event");
			Node event = events.item(0);
			NamedNodeMap attrs = event.getAttributes();

			String date = ((Attr)attrs.getNamedItem("date")).getValue();
			date = date.substring(0, date.length()-6); // remove extra +0000 from end

			String title = ((Attr)attrs.getNamedItem("title")).getValue();
			String firstname = ((Attr)attrs.getNamedItem("firstnames")).getValue();
			String lastname = ((Attr)attrs.getNamedItem("lastname")).getValue();
			String sDescription = "";

			Node mediaurl = XMLReader.getFirstChildWithTagName(event, "media");
			Node replayurl = XMLReader.getFirstChildWithTagName(event, "replay");
			Node publicurl = XMLReader.getFirstChildWithTagName(event, "public");
			Node description = XMLReader.getFirstChildWithTagName(event, "description");

			Node first = mediaurl.getFirstChild();
			if (first != null) {
				sMeetingMediaURLSTUB = first.getNodeValue();
			}
			first = replayurl.getFirstChild();
			if (first != null) {
				sMeetingReplayURLSTUB = first.getNodeValue();
			}
			first = publicurl.getFirstChild();
			if (first != null) {
				sMeetingFolksonomyURLSTUB = first.getNodeValue();
			}
			first = description.getFirstChild();
			if (first != null) {
				sDescription = first.getNodeValue();
			}

			View view = null;
			UIViewPane oUIViewPane = null;
			IModel oModel = ProjectCompendium.APP.getModel();
			PCSession oSession = (PCSession)oModel.getSession();
			UINode newMap = null;

			if (oViewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame map = (UIMapViewFrame) oViewFrame;
				oUIViewPane = map.getViewPane();
				ViewPaneUI oViewPaneUI = oUIViewPane.getViewPaneUI();

				String sDetails = "";
				newMap = oViewPaneUI.createNode(ICoreConstants.MAPVIEW,
											 "",
											 sAuthor,
											 date+": "+title,
											 "Booked by "+firstname+" "+lastname+"\n\n"+sDescription,
											 nMainMapX,
											 nMainMapY);

				// GIVE IT THE SPECIAL FLASHMEETING MAP IMAGE
				try {
					newMap.getNode().initialize(oSession, oModel);
					newMap.getNode().setSource("", UIImages.getPathString(IUIConstants.FLASHMEETING_ICON), sAuthor);
				} catch(Exception e) {}
				newMap.setIcon(UIImages.get(IUIConstants.FLASHMEETING_ICON));

				view = ((View)newMap.getNode());
			}
			else {
				UIListViewFrame list = (UIListViewFrame) oViewFrame;
				UIList oUIList = list.getUIList();
				ListUI oListUI = oUIList.getListUI();

				NodePosition newMap2 = oListUI.createNode(ICoreConstants.MAPVIEW,
											 "",
											 sAuthor,
											 date+": "+title,
											 "Booked by "+firstname+" "+lastname,
											 0,
											 ((oUIList.getNumberOfNodes() + 1) * 10)
											 );

				// GIVE IT THE SPECIAL MEETING MAP IMAGE
				try {
					newMap.getNode().initialize(oSession, oModel);
					newMap.getNode().setSource("", UIImages.getPathString(IUIConstants.FLASHMEETING_ICON), sAuthor);
				} catch(Exception e) {}

				view = ((View)newMap2.getNode());
			}

			if (view != null) {
				NodeList people = document.getElementsByTagName("person");
				nAttendeeCount = people.getLength();
				preProcessAttendees(people);

				NodePosition oReplay = view.addMemberNode(ICoreConstants.REFERENCE, "", "", sAuthor, "Replay: "+title, "", 10, 20);
				oReplay.setForeground(LABEL_COLOUR.getRGB());
				oReplay.setFontStyle(Font.BOLD);
				oReplay.setFontSize(14);
				NodeSummary nodeSum = oReplay.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(sMeetingReplayURLSTUB, "", sAuthor);
				// GIVE IT THE SPECIAL FLASHMEETING MAP IMAGE
				try {
					nodeSum.initialize(oSession, oModel);
					nodeSum.setSource("", UIImages.getPathString(IUIConstants.FLASHMEETING_ICON), sAuthor);
				} catch(Exception e) {}

				Dimension dim = (new UINode(oReplay, sAuthor)).getPreferredSize();
				int y = 20+dim.height+20;
				int startY = 20+dim.height;

				NodeList keywords = null;
				NodeList playlist = null;
				NodeList urls = null;
				NodeList chats = null;
				NodeList notes = null;
				NodeList whiteboard = null;
				NodeList annotations = null;
				NodeList filedata = null;
				NodeList votes = null;

				if (htChosenElements.contains(KEYWORDS_LABEL)) {
					keywords = document.getElementsByTagName("keyword");
					nKeywordCount = keywords.getLength();
				}
				if (htChosenElements.contains(PLAYLIST_LABEL)) {
					playlist = document.getElementsByTagName("item");
					nPlaylistCount = playlist.getLength();
				}
				if (htChosenElements.contains(URL_LABEL)) {
					urls = document.getElementsByTagName("url");
					nURLCount = urls.getLength();
					chats = document.getElementsByTagName("chat");
					nChatCount = chats.getLength();
					notes = document.getElementsByTagName("note");
					nAnnotationCount = notes.getLength();
				}
				if (htChosenElements.contains(CHAT_LABEL)) {
					if (chats == null) {
						chats = document.getElementsByTagName("chat");
						nChatCount = chats.getLength();
					}
				}
				if (htChosenElements.contains(WHITEBOARD_LABEL)) {
					whiteboard = document.getElementsByTagName("snapshot");
					nWhiteboardCount = whiteboard.getLength();
				}
				if (htChosenElements.contains(ANNOTATIONS_LABEL)) {
					if (annotations == null) {
						annotations = document.getElementsByTagName("note");
						nAnnotationCount = annotations.getLength();
					}
				}
				if (htChosenElements.contains(FILEDATA_LABEL)) {
					filedata = document.getElementsByTagName("file");
					nFiledataCount = filedata.getLength();
				}
				if (htChosenElements.contains(VOTING_LABEL)) {
					votes = document.getElementsByTagName("vote");
					nVoteCount = votes.getLength();
				}

		  		oProgressBar.setMaximum(nKeywordCount+nPlaylistCount+nAttendeeCount+nURLCount+nChatCount+nAnnotationCount+nWhiteboardCount+nFiledataCount+nVoteCount);

				if (htChosenElements.contains(FILEDATA_LABEL)) {
					if (nFiledataCount > 0) {
						y = processFileData(filedata, view, y);
					}
				}
				if (htChosenElements.contains(ATTENDEE_LABEL)) {
					if (nAttendeeCount > 0) {
						y = processAttendees(people, view, y);
					}
				}
				if (htChosenElements.contains(PLAYLIST_LABEL)) {
					if (nPlaylistCount > 0) {
						y = processPlayList(playlist, view, y);
					}
				}
				if (htChosenElements.contains(CHAT_LABEL)) {
					if (nChatCount > 0) {
						y = processChats(chats, view, y);
					}
				}
				if (htChosenElements.contains(URL_LABEL)) {
					y = processURLS(urls, chats, notes, view, y);
				}
				if (htChosenElements.contains(WHITEBOARD_LABEL)) {
					if (nWhiteboardCount > 0) {
						y = processWhiteboard(whiteboard, view, y);
					}
				}
				if (htChosenElements.contains(ANNOTATIONS_LABEL)) {
					if (nAnnotationCount >0 ){
						y = processAnnotations(annotations, view, y);
					}
				}
				if (htChosenElements.contains(VOTING_LABEL)) {
					if (nVoteCount > 0) {
						y = processVotes(votes, view, y);
					}
				}
				if (htChosenElements.contains(KEYWORDS_LABEL)) {
					if (nKeywordCount > 0) {
						y = processKeywords(keywords, view, y);
					}
				}

				// set size of main map window based on extent of node positions.
				view.initializeMembers();

				Dimension size = alignCenter(view);
				String sUserID = oModel.getUserProfile().getId();
				ViewPropertyService viewserv = (ViewPropertyService)oModel.getViewPropertyService();
				int width = size.width+35;
				int height = size.height+startY;
				int nX = 0;
				int nY = 0;
				ViewProperty viewProp = new ViewProperty();
				viewProp.setUserID(sUserID);
				viewProp.setViewID(view.getId());
				viewProp.setWidth(width);
				viewProp.setHeight(height);
				viewProp.setXPosition(nX);
				viewProp.setYPosition(nY);
				viewProp.setHorizontalScrollBarPosition(0);
				viewProp.setVerticalScrollBarPosition(0);
				viewProp.setIsIcon(false);
				viewProp.setIsMaximum(false);
				try {
					PCSession session = oModel.getSession();
					viewserv.createViewProperty(session, sUserID, viewProp);
				}
				catch(Exception io) {
					io.printStackTrace();
				}
			}

			ProjectCompendium.APP.refreshIconIndicators();
			ProjectCompendium.APP.setDefaultCursor();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To perform center align
	 */
	private Dimension alignCenter(View view) {

		Dimension maindim = new Dimension(0,0);

		double sXPos = 0;
		double lXPos = 0;
		double center = 0;
		double xPos = 0;
		double yPos = 0;
		double width = 0;
		double height = 0;
        double maxWidth = 0;
        double maxHeight = 0;

		Dimension dim = null;
		int i=0;
		NodePosition nodePos = null;
		for(Enumeration e = view.getPositions(); e.hasMoreElements();){
			nodePos = (NodePosition) e.nextElement();
			xPos = nodePos.getXPos();
			yPos = nodePos.getYPos();

			dim = (new UINode(nodePos, sAuthor)).getPreferredSize();
			width = xPos+dim.getWidth();
			height = yPos+dim.getHeight();
            if( width > maxWidth){
                maxWidth = width;
            }
            if( height > maxHeight){
                maxHeight = height;
            }

			if(i == 0 || sXPos > xPos){
				sXPos = xPos;
			}
			if(i == 0 || lXPos < (xPos +width) ) {
				lXPos = xPos + width;
			}

			i++;
		}

		maindim = new Dimension( new Double(maxWidth).intValue(), new Double(maxHeight).intValue());

		center = (sXPos + lXPos) /2 ;
		for(Enumeration e = view.getPositions(); e.hasMoreElements();){
			nodePos = (NodePosition) e.nextElement();
			dim = (new UINode(nodePos, sAuthor)).getPreferredSize();
			width = dim.getWidth();
			Point p = nodePos.getPos();
			Point pt = new Point();
			pt.setLocation(center - ( width /2), p.y);
			nodePos.setPos(pt);
		}

		return maindim;
	}

	/**
	 * Return the count of the current number of elements processed
	 */
	private int getCurrentCount() {
 		return (nCurrentKeywordCount+nCurrentPlaylistCount+nCurrentURLCount+nCurrentChatCount+nCurrentAnnotationCount+nCurrentWhiteboardCount+nCurrentFiledataCount+nCurrentVoteCount);
	}

	/**
	 * Extra all the people info and store, as other nodes refer to them by id.
	 * @param items
	 */
	private void preProcessAttendees(NodeList items) {
		NamedNodeMap attrs = null;
		Node item = null;
		String name = "";
		String personid = "";
		int count = items.getLength();
		people = new Hashtable(count);
		for (int i=0; i< count; i++) {
			item = items.item(i);
			attrs = item.getAttributes();
			personid = ((Attr)attrs.getNamedItem("personid")).getValue();
			name = ((Attr)attrs.getNamedItem("name")).getValue();
			people.put(personid, name);
		}
	}

	/**
	 * Process keyword data and create nodes.
	 * @param items the keyword xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processKeywords(NodeList items, View view, int nMainY) {

		NodePosition nodePos = null;

		try {
			NodeSummary nodeSum = null;

			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, KEYWORDS_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NodePosition oFolksNode = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, "Flashmeeting Folksonomy", "",  0, ((oView.getNumberOfNodes() + 1) * 10));
			nodeSum = oFolksNode.getNode();
			nodeSum.initialize(oSession, oModel);
			nodeSum.setSource(sMeetingFolksonomyURLSTUB, "", sAuthor);

			Node item = null;
			Node first = null;
			String jumpurl = "";
			String sKeyword = "";
			String sLabel = "";

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);

				first = item.getFirstChild();
				if (first != null) {
					sKeyword = first.getNodeValue();
					sLabel = sKeyword;
				}
				try {
					sKeyword = CoreUtilities.cleanURLText(sKeyword);
				} catch (Exception e) {}

				jumpurl = sMeetingFolksonomyURLSTUB+"/key/"+sKeyword;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "",  0,
						 ((oView.getNumberOfNodes() + 1) * 10));
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(jumpurl, "", sAuthor);

				nodes.addElement(nodePos);

				nCurrentKeywordCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}

			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}

	/**
	 * Process attendees data and create nodes.
	 * @param items the attendees xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processAttendees(NodeList items, View view, int nMainY) {

		NodePosition nodePos = null;

		try {
			NodeSummary nodeSum = null;

			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, ATTENDEE_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			Node first = null;
			String jumpurl = "";
			String sKeyword = "";
			String sLabel = "";

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);
				attrs = item.getAttributes();
				sLabel = ((Attr)attrs.getNamedItem("name")).getValue();

				nodePos = oView.addMemberNode( ICoreConstants.NOTE, "", "", sAuthor, sLabel, "", 0,
						 ((oView.getNumberOfNodes() + 1) * 10));

				nodeSum = nodePos.getNode();
				nodes.addElement(nodePos);

				nCurrentKeywordCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}

			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}

	/**
	 * Process PlayList data and create nodes.
	 * @param items the playlist xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processPlayList(NodeList items, View view, int nMainY) {

		try {
			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, PLAYLIST_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			String timestamp = "";
			String personid = "";
			String jumptime = "";
			String name = "";
			String jumpurl = "";
			String sLabel = "";
			String sTime = "";
			NodeSummary nodeSum = null;
			NodePosition nodePos = null;

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);
				attrs = item.getAttributes();

				//file CDATA #REQUIRED
				//duration CDATA #REQUIRED

				timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
				personid = ((Attr)attrs.getNamedItem("personid")).getValue();
				jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();

				name = (String)people.get(personid);
				jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime;

				sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");
				sLabel = sTime+": "+name;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "",  0,
						 ((oView.getNumberOfNodes() + 1) * 10));
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(jumpurl, "", sAuthor);

				nodes.addElement(nodePos);

				nCurrentPlaylistCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}

			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			System.out.println("FAILING = "+e.getMessage());
			e.printStackTrace();
		}

		return nMainY;
	}

	/**
	 * Process PlayList data and create nodes.
	 * @param items the playlist xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processURLS(NodeList items, NodeList chats, NodeList notes, View view, int nMainY) {

		int y = 20;
		try {
			NodePosition nodePos = null;
			NodePosition nodePos2 = null;

			NodePosition oMap = view.addMemberNode(ICoreConstants.MAPVIEW, "", "", sAuthor, URL_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			String timestamp = "";
			String jumptime = "";
			String jumpurl = "";
			String sTime = "";
			String sLabel = "";
			String sURL = "";
			NodeSummary nodeSum = null;
			NodeSummary nodeSum2 = null;
			Vector nodes = new Vector(51);
			Vector nodes2 = new Vector(51);

			int counti = items.getLength();
			for (int i=0; i< counti; i++) {
				item = items.item(i);
				attrs = item.getAttributes();

				sURL = ((Attr)attrs.getNamedItem("url")).getValue();
				timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
				jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();
				jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime;
				sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");
				sLabel = sTime+": "+sURL;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "", X_OFFSET, y);
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(sURL, "", sAuthor);
				nodes.addElement(nodePos);

				nodePos2 = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, "Jump to the moment when this was visited", "", X_OFFSET+200, y);
				nodeSum2 = nodePos2.getNode();
				nodeSum2.initialize(oSession, oModel);
				nodeSum2.setSource(jumpurl, "", sAuthor);
				nodes2.addElement(nodePos2);

				oView.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, nodeSum2, nodeSum, ICoreConstants.ARROW_TO);

				y+=70;

				nCurrentURLCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}

			y+=70;

			// SEARCH CHATS FOR URLS
			counti = chats.getLength();
			Node first = null;
			String sMessage = "";
			sURL = "";
			Vector results = null;
			int countj = 0;
			for (int i=0; i< counti; i++) {
				item = chats.item(i);
				first = item.getFirstChild();
				if (first != null) {
					sMessage = first.getNodeValue();
				}

				if (sMessage != null && sMessage != "") {

					attrs = item.getAttributes();
					timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
					jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();
					jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime;
					sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");

					results = findURLs(sMessage);
					countj = results.size();
					for (int j=0; j<countj; j++) {
						sURL = (String)results.elementAt(j);
						sLabel = sTime+": "+sURL;

						nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "", X_OFFSET, y);
						nodeSum = nodePos.getNode();
						nodeSum.initialize(oSession, oModel);
						nodeSum.setSource(sURL, "", sAuthor);
						nodes.addElement(nodePos);

						nodePos2 = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, "Jump to the moment when this was discussed", "", X_OFFSET+100, y);
						nodeSum2 = nodePos2.getNode();
						nodeSum2.initialize(oSession, oModel);
						nodeSum2.setSource(jumpurl, "", sAuthor);
						nodes2.addElement(nodePos2);

						oView.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, nodeSum2, nodeSum, ICoreConstants.ARROW_TO);

						y+=70;
					}
				}
			}

			y+=70;

			// SEARCH ANNOTATIONS
			counti = notes.getLength();
			first = null;
			sMessage = "";
			sURL = "";
			results = null;
			countj = 0;
			for (int i=0; i< counti; i++) {
				item = notes.item(i);
				first = item.getFirstChild();
				if (first != null) {
					sMessage = first.getNodeValue();
				}
				if (sMessage != null && sMessage != "") {

					attrs = item.getAttributes();
					timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
					jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();
					jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime;
					sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");

					results = findURLs(sMessage);
					countj = results.size();
					for (int j=0; j<countj; j++) {
						sURL = (String)results.elementAt(j);

						sLabel = sTime+": "+sURL;

						nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "", X_OFFSET, y);
						nodeSum = nodePos.getNode();
						nodeSum.initialize(oSession, oModel);
						nodeSum.setSource(sURL, "", sAuthor);
						nodes.addElement(nodePos);

						nodePos2 = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, "Jump to the moment when this was annotated", "", X_OFFSET+100, y);
						nodeSum2 = nodePos2.getNode();
						nodeSum2.initialize(oSession, oModel);
						nodeSum2.setSource(jumpurl, "", sAuthor);
						nodes2.addElement(nodePos2);

						oView.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, nodeSum2, nodeSum, ICoreConstants.ARROW_TO);

						y+=70;
					}
				}
			}

			nMainY += Y_SPACER;

			oView.initializeMembers();
			UIArrangeLeftRight arrange = new UIArrangeLeftRight();
			arrange.arrangeView(oView, new UIMapViewFrame(oView));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}

	private Vector findURLs(String sSearchString) {
		Vector results = new Vector(10);
		String sString = sSearchString;
		String sNext = "";
		String sCheck = "";
		if (sString != null && sString != "") {
			if (sString.indexOf("http://") != -1 ||
					sString.indexOf("https://") != -1 ||
					sString.indexOf("www.") != -1) {

				int index = 0;
				int index2 = 0;
				boolean bFound = true;
				while(bFound) {
					bFound = false;
					index = sString.indexOf("http://");
					if (index != -1) {
						index2 = sString.indexOf(" ", index);
						if (index2 != -1) {
							sNext = sString.substring(index, index2);
							sNext = cleanURL(sNext);
							results.addElement(sNext);
							sString = sString.substring(0, index) + sString.substring(index2);
						} else {
							sNext = sString.substring(index);
							sNext = cleanURL(sNext);
							results.addElement(sNext);
							sString = sString.substring(0, index);
						}
						bFound = true;
					}
				}

				bFound = true;
				while(bFound) {
					bFound = false;
					index = sString.indexOf("https://");
					if (index != -1) {
						index2 = sString.indexOf(" ", index);
						if (index2 != -1) {
							sNext = sString.substring(index, index2);
							sNext = cleanURL(sNext);
							results.addElement(sNext);
							sString = sString.substring(0, index) + sString.substring(index2);
						} else {
							sNext = sString.substring(index);
							sNext = cleanURL(sNext);
							results.addElement(sNext);
							sString = sString.substring(0, index);
						}
						bFound = true;
					}
				}

				bFound = true;
				while(bFound) {
					bFound = false;
					index = sString.indexOf("www.");
					if (index != -1) {
						index2 = sString.indexOf(" ", index);
						if (index2 != -1) {
							sNext = sString.substring(index, index2);
							sNext = cleanURL(sNext);
							results.addElement(sNext);
							sString = sString.substring(0, index) + sString.substring(index2);
						} else {
							sNext = sString.substring(index);
							sNext = cleanURL(sNext);
							results.addElement(sNext);
							sString = sString.substring(0, index);
						}
						bFound = true;
					}
				}
			}
		}
		return results;
	}

	private String cleanURL(String sNext) {
		String sCheck = sNext.substring(sNext.length()-1, sNext.length());
		if (sCheck.equals(")") || sCheck.equals("]") || sCheck.equals(",")
				|| sCheck.equals(".") || sCheck.equals(";") || sCheck.equals(".")) {
			return sNext.substring(0, sNext.length()-1);
		} else {
			return sNext;
		}
	}

	/**
	 * Process Chat data and create nodes.
	 * @param items the chat xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processChats(NodeList items, View view, int nMainY) {

		NodePosition nodePos = null;

		try {
			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, CHAT_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			Node first = null;
			String timestamp = "";
			String personid = "";
			String jumptime = "";
			String name = "";
			String jumpurl = "";
			String sLabel = "";
			String sTime = "";
			String sMessage = "";
			NodeSummary nodeSum = null;

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);

				first = item.getFirstChild();
				if (first != null) {
					sMessage = first.getNodeValue();
				}

				attrs = item.getAttributes();
				timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
				personid = ((Attr)attrs.getNamedItem("personid")).getValue();
				jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();

				name = (String)people.get(personid);
				jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime;

				sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");
				sLabel = sTime+": "+name+": "+sMessage;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "", 0,
						 ((oView.getNumberOfNodes() + 1) * 10));
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(jumpurl, "", sAuthor);

				nodes.addElement(nodePos);

				nCurrentChatCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}

	/**
	 * Process Chat data and create nodes.
	 * @param items the chat xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processWhiteboard(NodeList items, View view, int nMainY) {

		NodePosition nodePos = null;

		try {
			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, WHITEBOARD_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			Node first = null;
			String timestamp = "";
			String personid = "";
			String jumptime = "";
			String name = "";
			String jumpurl = "";
			String sLabel = "";
			String sTime = "";
			String sMessage = "";
			String title = "";
			NodeSummary nodeSum = null;

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);

				attrs = item.getAttributes();
				timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
				jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();
				title = ((Attr)attrs.getNamedItem("title")).getValue();

				//id CDATA #REQUIRED
				//auto ('Y' | 'N') #REQUIRED

				name = (String)people.get(personid);
				jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime+"&fb=1";

				sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");
				sLabel = sTime+": "+title;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, sMessage, 0,
						 ((oView.getNumberOfNodes() + 1) * 10));
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(jumpurl, "", sAuthor);

				nodes.addElement(nodePos);

				nCurrentWhiteboardCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}

	/**
	 * Process notes data and create nodes.
	 * @param items the notes xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processAnnotations(NodeList items, View view, int nMainY) {

		NodePosition nodePos = null;

		try {
			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, ANNOTATIONS_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			Node first = null;
			String timestamp = "";
			String personid = "";
			String jumptime = "";
			String name = "";
			String jumpurl = "";
			String sLabel = "";
			String sTime = "";
			String sMessage = "";
			NodeSummary nodeSum = null;

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);

				first = item.getFirstChild();
				if (first != null) {
					sMessage = first.getNodeValue();
				}

				attrs = item.getAttributes();
				timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
				jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();
				jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime;

				sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");
				sLabel = sTime+": "+sMessage;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "", 0,
						 ((oView.getNumberOfNodes() + 1) * 10));
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(jumpurl, "", sAuthor);

				nodes.addElement(nodePos);

				nCurrentAnnotationCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}

	/**
	 * Process file data and create nodes.
	 * @param items the file data xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processFileData(NodeList items, View view, int nMainY) {

		NodePosition nodePos = null;

		try {
			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, FILEDATA_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setFontStyle(Font.BOLD);
			oMap.setShowSmallIcon(true);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			String name = "";
			String size = "";
			String jumpurl = "";
			String sLabel = "";
			String sTime = "";
			NodeSummary nodeSum = null;

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);
				attrs = item.getAttributes();

				name = ((Attr)attrs.getNamedItem("name")).getValue();
				size = ((Attr)attrs.getNamedItem("size")).getValue();
				jumpurl = sMeetingMediaURLSTUB+"/uploads/"+name;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, name, "", 0,
						 ((oView.getNumberOfNodes() + 1) * 10));
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(jumpurl, "", sAuthor);

				nodes.addElement(nodePos);

				nCurrentFiledataCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}

	/**
	 * Process Vote data and create nodes.
	 * @param items the vote xml items
	 * @param view the view to put the main node in.
	 * @param y the last position of a map node.
	 */
	private int processVotes(NodeList items, View view, int nMainY) {

		NodePosition nodePos = null;

		try {
			NodePosition oMap = view.addMemberNode(ICoreConstants.LISTVIEW, "", "", sAuthor, VOTING_LABEL, "", X_OFFSET, nMainY);
			oMap.setForeground(LABEL_COLOUR.getRGB());
			oMap.setShowSmallIcon(true);
			oMap.setFontStyle(Font.BOLD);
			View oView = (View)oMap.getNode();

			NamedNodeMap attrs = null;
			Node item = null;
			String timestamp = "";
			String personid = "";
			String action = "";
			String jumptime = "";
			String name = "";
			String jumpurl = "";
			String sLabel = "";
			String sTime = "";
			NodeSummary nodeSum = null;

			int counti = items.getLength();
			Vector nodes = new Vector(counti);

			for (int i=0; i< counti; i++) {
				item = items.item(i);
				attrs = item.getAttributes();

				timestamp = ((Attr)attrs.getNamedItem("timestamp")).getValue();
				personid = ((Attr)attrs.getNamedItem("personid")).getValue();
				action = ((Attr)attrs.getNamedItem("action")).getValue();
				jumptime = ((Attr)attrs.getNamedItem("jumptime")).getValue();

				//tally

				name = (String)people.get(personid);
				jumpurl = sMeetingReplayURLSTUB+"&jt="+jumptime;

				sTime = CoreCalendar.getDateString(CoreCalendar.getDateFromTime(new Long(timestamp).longValue()), "HH:mm:ss");
				sLabel = sTime+": "+name+" - "+action;

				nodePos = oView.addMemberNode( ICoreConstants.REFERENCE, "", "", sAuthor, sLabel, "", 0,
						 ((oView.getNumberOfNodes() + 1) * 10));
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.setSource(jumpurl, "", sAuthor);

				nodes.addElement(nodePos);

				nCurrentVoteCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			nMainY += Y_SPACER;
			oView.initializeMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nMainY;
	}
}