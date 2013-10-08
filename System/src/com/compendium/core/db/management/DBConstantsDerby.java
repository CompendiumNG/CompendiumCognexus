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

/*
 * This interface defines the global constants for the database management classes when using Derby.
 * These are all String objects representing various SQL statements shared by various management classes.
 *
 * @author Michelle Bachler
 */
public interface DBConstantsDerby extends java.io.Serializable {

//	 STATEMENTS TO DROP TABLES

	/** The SQL statement to drop a User table if it exists */
	public final static String DROP_USER_TABLE			= "DROP TABLE Users";

	/** The SQL statement to drop a System table if it exists */
	public final static String DROP_SYSTEM_TABLE		= "DROP TABLE System";

	/** The SQL statement to drop a Node table if it exists */
	public final static String DROP_NODE_TABLE 			= "DROP TABLE Node";

	/** The SQL statement to drop a Link table if it exists */
	public final static String DROP_LINK_TABLE			= "DROP TABLE Link";

	/** The SQL statement to drop a Code table if it exists */
	public final static String DROP_CODE_TABLE 			= "DROP TABLE Code";

	/** The SQL statement to drop a GroupCode table if it exists */
	public final static String DROP_GROUPCODE_TABLE 	= "DROP TABLE GroupCode";

	/** The SQL statement to drop a CodeGroup table if it exists */
	public final static String DROP_CODEGROUP_TABLE 	= "DROP TABLE CodeGroup";

	/** The SQL statement to drop a NodeCode table if it exists */
	public final static String DROP_NODECODE_TABLE 		= "DROP TABLE NodeCode";

	/** The SQL statement to drop a ReferenceNode table if it exists */
	public final static String DROP_REFERENCE_TABLE 	= "DROP TABLE ReferenceNode";

	/** The SQL statement to drop a ViewNode table if it exists */
	public final static String DROP_VIEWNODE_TABLE 		= "DROP TABLE ViewNode";

	/** The SQL statement to drop a ShortCutNode table if it exists */
	public final static String DROP_SHORTCUT_TABLE 		= "DROP TABLE ShortCutNode";

	/** The SQL statement to drop a NodeDetail table if it exists */
	public final static String DROP_NODEDETAIL_TABLE	= "DROP TABLE NodeDetail";

	/** The SQL statement to drop a ViewProperty table if it exists */
	public final static String DROP_VIEWPROPERTY_TABLE 	= "DROP TABLE ViewProperty";

	/** The SQL statement to drop a Favorite table if it exists */
	public final static String DROP_FAVORITE_TABLE	 	= "DROP TABLE Favorite";

	/** The SQL statement to drop a Workspace table if it exists */
	public final static String DROP_WORKSPACE_TABLE 	= "DROP TABLE Workspace";

	/** The SQL statement to drop a WorkspaceView table if it exists */
	public final static String DROP_WORKSPACEVIEW_TABLE	= "DROP TABLE WorkspaceView";

	/** The SQL statement to drop a ViewLink table if it exists */
	public final static String DROP_VIEWLINK_TABLE 		= "DROP TABLE ViewLink";

	/** The SQL statement to drop a NodeUserState table if it exists */
	public final static String DROP_NODEUSERSTATE_TABLE = "DROP TABLE NodeUserState";

	/** The SQL statement to drop a Clonetable if it exists */
	public final static String DROP_CLONE_TABLE			= "DROP TABLE Clone";

	/** The SQL statement to drop a ExtendedNodeType table if it exists */
	public final static String DROP_EXTENDEDNODE_TABLE	= "DROP TABLE ExtendedNodeType";

	/** The SQL statement to drop a ExtendedTypeCode table if it exists */
	public final static String DROP_EXTENDEDCODE_TABLE	= "DROP TABLE ExtendedTypeCode";

	/** The SQL statement to drop a Permission table if it exists */
	public final static String DROP_PERMISSION_TABLE	= "DROP TABLE Permission";

	/** The SQL statement to drop a UserGroup table if it exists */
	public final static String DROP_USERGROUP_TABLE		= "DROP TABLE UserGroup";

	/** The SQL statement to drop a GroupUser table if it exists */
	public final static String DROP_GROUPUSER_TABLE		= "DROP TABLE GroupUser";

	/** The SQL statement to drop a Audit table if it exists */
	public final static String DROP_AUDIT_TABLE			= "DROP TABLE Audit";

	/** The SQL statement to drop a ViewLayer table if it exists */
	public final static String DROP_VIEWLAYER_TABLE		= "DROP TABLE  ViewLayer";

	/** The SQL statement to drop a NodeProperty table if it exists */
	public final static String DROP_NODEPROPERTY_TABLE	= "DROP TABLE NodeProperty";

	/** The SQL statement to drop a Connection table if it exists */
	public final static String DROP_CONNECTION_TABLE	= "DROP TABLE Connections";

	/** The SQL statement to drop a Preference table if it exists */
	public final static String DROP_PREFERENCE_TABLE	= "DROP TABLE Preference";

	/** The SQL statement to drop a Meetingtable if it exists */
	public final static String DROP_MEETING_TABLE		= "DROP TABLE Meeting";

	/** The SQL statement to drop a MediaIndex table if it exists */
	public final static String DROP_MEDIAINDEX_TABLE	= "DROP TABLE MediaIndex";

// STATEMENTS TO CREATE NEW TABLES

// FOR THE LOCAL DERBY ADMINISTRATION DATABASE

	/** The SQL statement to create the projects table, which holds database information.*/
	public static final String CREATE_PROJECT_TABLE = "CREATE TABLE Project ("+
														"ProjectName VARCHAR(100) NOT NULL, "+
														"DatabaseName VARCHAR(100) NOT NULL, "+
														"CreationDate DOUBLE NOT NULL, "+
														"ModificationDate DOUBLE NOT NULL, "+
														"CONSTRAINT PK_project PRIMARY KEY (ProjectName, DatabaseName) )";

	/** The SQL statement to create the projects table, which holds database information.*/
	/*public static final String CREATE_PROJECT_TABLE = "CREATE TABLE Project ("+
														"ProjectName VARCHAR(100) NOT NULL, "+
														"DatabaseName VARCHAR(100) NOT NULL, "+
														"CreationDate DOUBLE NOT NULL, "+
														"ModificationDate DOUBLE NOT NULL, "+
														"Profile VARCHAR(255) NOT NULL, "+
														"Type INTEGER NOT NULL, "+
														"CONSTRAINT PK_project PRIMARY KEY (ProjectName, DatabaseName, Profile, Type) )";
	*/

	/** The SQL statement to create the properties table */
	public static final String CREATE_PROPERTIES_TABLE = "CREATE TABLE Properties ("+
															"Property VARCHAR(100) NOT NULL, "+
															"Contents LONG VARCHAR NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Properties PRIMARY KEY (Property))";

//"ConnectionID VARCHAR(50) NOT NULL, " +

	/** The SQL statement to create a new Connection table */
	public static final String CREATE_ADMIN_CONNECTIONS_TABLE = "CREATE TABLE Connections ("+
															"Profile VARCHAR(255) NOT NULL, "+
															"Type INTEGER NOT NULL, "+
															"Server VARCHAR(255) NOT NULL, "+
															"Login VARCHAR(255) NOT NULL, "+
															"Password VARCHAR(255) NOT NULL, "+
															"Port INTEGER, "+
															"DefaultDatabase VARCHAR(255), "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Connection PRIMARY KEY (Profile, Type))";

// FOR A COMPENDIUM DATABASE

	/** The SQL statement to create a new User table */
	public static final String CREATE_USER_TABLE = "CREATE TABLE Users ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Login VARCHAR(20) NOT NULL, " +
															"Name VARCHAR(50), " +
															"Password VARCHAR(50) NOT NULL, " +
															"Description VARCHAR(255), " +
															"HomeView VARCHAR(50) NOT NULL, "+
															"IsAdministrator VARCHAR(1) NOT NULL, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT 0, "+
															"LinkView VARCHAR(50), "+
															"CONSTRAINT PK_User PRIMARY KEY(UserID))";

	/** The SQL statement to create a new Audit table */
	public static final String CREATE_AUDIT_TABLE = "CREATE TABLE Audit ("+
															"AuditID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"ItemID VARCHAR(50) NOT NULL, "+
															"AuditDate DOUBLE NOT NULL, "+
															"Category VARCHAR(50) NOT NULL, "+
															"Action INTEGER NOT NULL, "+
															"Data LONG VARCHAR, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Audit PRIMARY KEY (AuditID))";

	/** The SQL statement to create a new Code table */
	public static final String CREATE_CODE_TABLE = "CREATE TABLE Code ("+
															"CodeID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Name VARCHAR(50) NOT NULL, " +
															"Description VARCHAR(100), " +
															"Behavior VARCHAR(255), "+
															"CONSTRAINT PK_Code PRIMARY KEY (CodeID))";

	/** The SQL statement to create a new CodeGroup table */
	public static final String CREATE_CODEGROUP_TABLE = "CREATE TABLE CodeGroup ("+
															"CodeGroupID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"Name VARCHAR(100) NOT NULL, "+
															"Description VARCHAR(255), "+
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"CONSTRAINT PK_CodeGroup PRIMARY KEY (CodeGroupID))";

	/** The SQL statement to create a new Connection table */
	public static final String CREATE_CONNECTION_TABLE = "CREATE TABLE Connections ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"Profile VARCHAR(255) NOT NULL, "+
															"Type INTEGER NOT NULL, "+
															"Server VARCHAR(255) NOT NULL, "+
															"Login VARCHAR(255) NOT NULL, "+
															"Password VARCHAR(255) NOT NULL, "+
															"Name VARCHAR(255), "+
															"Port INTEGER, "+
															"Resource VARCHAR(255), "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Connection PRIMARY KEY (UserID, Profile, Type), "+
															"CONSTRAINT FK_Connection_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE)";

	/** The SQL statement to create a new ExtendedNodeType table */
	public static final String CREATE_EXTENDEDNODE_TABLE = "CREATE TABLE ExtendedNodeType ("+
															"ExtendedNodeTypeID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Name VARCHAR(50), "+
															"Description VARCHAR(100), "+
															"BaseNodeType INTEGER NOT NULL, " +
															"Icon VARCHAR(200), "+
															"CONSTRAINT PK_ExtendedNode PRIMARY KEY (ExtendedNodeTypeID))";

	/** The SQL statement to create a new ExtendedTypeCode table */
	public static final String CREATE_EXTENDEDCODE_TABLE = "CREATE TABLE ExtendedTypeCode ("+
															"ExtendedNodeTypeID VARCHAR(50) NOT NULL, " +
															"CodeID VARCHAR(50) NOT NULL, " +
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_ExtendedCode PRIMARY KEY (ExtendedNodeTypeID, CodeID), "+
															"CONSTRAINT FK_ExtendedCode_1 FOREIGN KEY (ExtendedNodeTypeID) REFERENCES ExtendedNodeType (ExtendedNodeTypeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ExtendedCode_2 FOREIGN KEY (CodeID) REFERENCES Code (CodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Favorite table */
	public static final String CREATE_FAVORITE_TABLE = "CREATE TABLE Favorite (" +
															"UserID VARCHAR(50) NOT NULL, " +
															"NodeID VARCHAR(50) NOT NULL, " +
															"Label LONG VARCHAR NOT NULL, " +
															"NodeType INTEGER NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"ViewID VARCHAR(50), " +
															"CONSTRAINT PK_Favorite PRIMARY KEY (UserID, NodeID), "+
															"CONSTRAINT FK_Favorite_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_Favorite_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_Favorite_3 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new GroupCode table */
	public static final String CREATE_GROUPCODE_TABLE = "CREATE TABLE GroupCode ("+
															"CodeID VARCHAR(50) NOT NULL, "+
															"CodeGroupID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"CONSTRAINT PK_GroupCode PRIMARY KEY (CodeID, CodeGroupID), "+
															"CONSTRAINT FK_GroupCode_1 FOREIGN KEY (CodeID) REFERENCES Code (CodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_GroupCode_2 FOREIGN KEY (CodeGroupID) REFERENCES CodeGroup (CodeGroupID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Link table */
	public static final String CREATE_LINK_TABLE = "CREATE TABLE Link ("+
															"LinkID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"LinkType VARCHAR(50) NOT NULL, " +
															"OriginalID VARCHAR(50), "+
															"FromNode VARCHAR(50) NOT NULL, " +
															"ToNode VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL DEFAULT '0', " + // LEAVE FOR BACKWARDS COMPATIBILITY
															"Label LONG VARCHAR, "+
															"Arrow INTEGER NOT NULL, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT 0, "+
															"CONSTRAINT PK_Link PRIMARY KEY (LinkID), "+
															"CONSTRAINT FK_Link_1 FOREIGN KEY (FromNode) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_Link_2 FOREIGN KEY (ToNode) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Node table */
	public static final String CREATE_NODE_TABLE = "CREATE TABLE Node ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"NodeType INTEGER NOT NULL, " +
															"OriginalID VARCHAR(255), "+
															"ExtendedNodeType VARCHAR(50), " +
															"Label LONG VARCHAR, "+
															"Detail LONG VARCHAR, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT 0, "+
															"LastModAuthor VARCHAR(50), " +
															"CONSTRAINT PK_Node PRIMARY KEY (NodeID))";

	/** The SQL statement to create a new NodeDetail table */
	public static final String CREATE_NODEDETAIL_TABLE = "CREATE TABLE NodeDetail ("+
															"NodeID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"PageNo INTEGER NOT NULL, "+
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Detail LONG VARCHAR, "+
															"CONSTRAINT PK_NodeDetail PRIMARY KEY (NodeID, PageNo), "+
															"CONSTRAINT FK_NodeDetail_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new NodeUserState table */
	public static final String CREATE_NODEUSERSTATE_TABLE = "CREATE TABLE NodeUserState ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"UserID VARCHAR(50) NOT NULL, " +
															"State INTEGER NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_NodeUserState PRIMARY KEY (NodeID, UserID), "+
															"CONSTRAINT FK_NodeUserState_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_NodeUaerState_2 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE)";

	/** The SQL statement to create a new System table */
	public static final String CREATE_SYSTEM_TABLE = "CREATE TABLE System (Property VARCHAR(100) NOT NULL, "+
															"Contents VARCHAR(255) NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_System PRIMARY KEY (Property))";

	/** The SQL statement to create a new ReferenceNode table */
	public static final String CREATE_REFERENCE_TABLE = "CREATE TABLE ReferenceNode ("+
															"NodeID VARCHAR(50) NOT NULL, "+
															"Source VARCHAR(2500), "+
															"ImageSource VARCHAR(255), "+
															"ImageWidth INTEGER, "+
															"ImageHeight INTEGER, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_ReferenceNode PRIMARY KEY (NodeID), "+
															"CONSTRAINT FK_ReferenceNode_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new NodeCode table */
	public static final String CREATE_NODECODE_TABLE = "CREATE TABLE NodeCode ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"CodeID VARCHAR(50) NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_NodeCode PRIMARY KEY (NodeID, CodeID), "+
															"CONSTRAINT FK_NodeCode_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_NodeCode_2 FOREIGN KEY (CodeID) REFERENCES Code (CodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new ViewLink table */
	public static final String CREATE_VIEWLINK_TABLE = "CREATE TABLE ViewLink ("+
															"ViewID VARCHAR(50) NOT NULL, " +
															"LinkID VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT 0, "+
															"CONSTRAINT PK_ViewLink PRIMARY KEY (ViewID, LinkID), "+
															"CONSTRAINT viewlink_ibfk_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewLink_2 FOREIGN KEY (LinkID) REFERENCES Link (LinkID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Shortcut table */
	public static final String CREATE_SHORTCUT_TABLE = "CREATE TABLE ShortCutNode ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"ReferenceID VARCHAR(50) NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_ShortcutNode PRIMARY KEY (NodeID, ReferenceID), "+
															"CONSTRAINT FK_ShortcutNode_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new ViewNode table */
	public static final String CREATE_VIEWNODE_TABLE = "CREATE TABLE ViewNode ("+
															"ViewID VARCHAR(50) NOT NULL, " +
															"NodeID VARCHAR(50) NOT NULL, " +
															"XPos INTEGER NOT NULL DEFAULT 0, "+
															"YPos INTEGER NOT NULL DEFAULT 0, "+
															"CreationDate DOUBLE, " +
															"ModificationDate DOUBLE, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT 0, "+
															"ShowTags VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"ShowText VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"ShowTrans VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"ShowWeight VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"SmallIcon VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"HideIcon VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"LabelWrapWidth INTEGER NOT NULL DEFAULT 25, "+
															"FontSize INTEGER NOT NULL DEFAULT 12, "+
															"FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial', "+
															"FontStyle INTEGER NOT NULL DEFAULT 0, " +
															"Foreground INTEGER NOT NULL DEFAULT 0, "+
															"Background INTEGER NOT NULL DEFAULT -1, "+
															"CONSTRAINT PK_ViewNode PRIMARY KEY (ViewID, NodeID), "+
															"CONSTRAINT FK_ViewNode_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewNode_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new UserGroup table */
	public static final String CREATE_USERGROUP_TABLE = "CREATE TABLE UserGroup ("+
															"GroupID VARCHAR(50) NOT NULL, "+
															"UserID VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Name VARCHAR(100) NOT NULL, " +
															"Description VARCHAR(255), "+
															"CONSTRAINT PK_UserGroup PRIMARY KEY (GroupID))";

	/** The SQL statement to create a new GroupUser table */
	public static final String CREATE_GROUPUSER_TABLE = "CREATE TABLE GroupUser ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"GroupID VARCHAR(50) NOT NULL, " +
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_GroupUser PRIMARY KEY (UserID, GroupID), "+
															"CONSTRAINT FK_GroupUser_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_GroupUser_2 FOREIGN KEY (GroupID) REFERENCES UserGroup (GroupID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Permision table */
	public static final String CREATE_PERMISSION_TABLE = "CREATE TABLE Permission ("+
															"ItemID VARCHAR(50) NOT NULL, " +
															"GroupID VARCHAR(50) NOT NULL, " +
															"Permission INTEGER NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Permission PRIMARY KEY (ItemID, GroupID), "+
															"CONSTRAINT FK_Permission_1 FOREIGN KEY (GroupID) REFERENCES UserGroup (GroupID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Clone table */
	public static final String CREATE_CLONE_TABLE = "CREATE TABLE Clone ("+
															"ParentNodeID VARCHAR(50) NOT NULL, "+
															"ChildNodeID VARCHAR(50) NOT NULL, " +
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Clone PRIMARY KEY (ParentNodeID, ChildNodeID), "+
															"CONSTRAINT FK_Clone_1 FOREIGN KEY (ChildNodeID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Preference table */
	public static final String CREATE_PREFERENCE_TABLE = "CREATE TABLE Preference ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"Property VARCHAR(100) NOT NULL, "+
															"Contents VARCHAR(255) NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Preference PRIMARY KEY (UserID, Property), "+
															"CONSTRAINT FK_Preference_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE)";

	/** The SQL statement to create a new ViewLayer table */
	public static final String CREATE_VIEWLAYER_TABLE = "CREATE TABLE ViewLayer ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL, " +
															"Scribble LONG VARCHAR, " +
															"Background VARCHAR(255), "+
															"Grid VARCHAR(255), "+
															"Shapes LONG VARCHAR, " +
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_ViewLayer PRIMARY KEY (UserID, ViewID), "+
															"CONSTRAINT FK_ViewLayer_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewLayer_2 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new ViewProperty table */
	public static final String CREATE_VIEWPROPERTY_TABLE = "CREATE TABLE ViewProperty ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL, " +
															"HorizontalScroll INTEGER NOT NULL, " +
															"VerticalScroll INTEGER NOT NULL, " +
															"Width INTEGER NOT NULL, "+
															"Height INTEGER NOT NULL, "+
															"XPosition INTEGER NOT NULL, "+
															"YPosition INTEGER NOT NULL, "+
															"IsIcon VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"IsMaximum VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"ShowTags VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"ShowText VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"ShowTrans VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"ShowWeight VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"SmallIcons VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"HideIcons VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"LabelLength INTEGER NOT NULL DEFAULT 100, "+
															"LabelWidth INTEGER NOT NULL DEFAULT 15, "+
															"FontSize INTEGER NOT NULL DEFAULT 12, "+
															"FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial', "+
															"FontStyle INTEGER NOT NULL DEFAULT 0, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_ViewProperty PRIMARY KEY (UserID, ViewID), "+
															"CONSTRAINT FK_ViewProperty_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewProperty_2 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Workspace table */
	public static final String CREATE_WORKSPACE_TABLE = "CREATE TABLE Workspace (" +
															"WorkspaceID VARCHAR(50) NOT NULL, "+
															"UserID VARCHAR(50) NOT NULL, " +
															"Name VARCHAR(100) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"CONSTRAINT PK_Workspace PRIMARY KEY (WorkspaceID), "+
															"CONSTRAINT FK_Workspace_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE)";

	/** The SQL statement to create a new WorkspaceView table */
	public static final String CREATE_WORKSPACEVIEW_TABLE = "CREATE TABLE WorkspaceView (" +
															"WorkspaceID VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL, " +
															"HorizontalScroll INTEGER NOT NULL, "+
															"VerticalScroll INTEGER NOT NULL, "+
															"Width INTEGER NOT NULL, "  +
															"Height INTEGER NOT NULL, " +
															"XPosition INTEGER NOT NULL, " +
															"YPosition INTEGER NOT NULL, " +
															"IsIcon VARCHAR(1) NOT NULL, " +
															"IsMaximum VARCHAR(1) NOT NULL, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_WorkspaceView PRIMARY KEY (WorkspaceID, ViewID), "+
															"CONSTRAINT FK_WorkspaceView_1 FOREIGN KEY (WorkspaceID) REFERENCES Workspace (WorkspaceID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_WorkspaceView_2 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new Meeting table */
	public static final String CREATE_MEETING_TABLE = "CREATE TABLE Meeting (" +
															"MeetingID VARCHAR(255) NOT NULL, " +
															"MeetingMapID VARCHAR(50) NOT NULL, "+
															"MeetingName VARCHAR (255), "+
															"MeetingDate DOUBLE, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT 0, "+
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_Meeting PRIMARY KEY (MeetingID), "+
															"CONSTRAINT FK_Meeting_1 FOREIGN KEY (MeetingMapID) REFERENCES Node (NodeID) ON DELETE CASCADE)";

	/** The SQL statement to create a new MediaIndex table */
	public static final String CREATE_MEDIAINDEX_TABLE = "CREATE TABLE MediaIndex (" +
															"ViewID VARCHAR(50) NOT NULL, "+
															"NodeID VARCHAR(50) NOT NULL, " +
															"MeetingID VARCHAR (255) NOT NULL, " +
															"MediaIndex DOUBLE NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"CONSTRAINT PK_MediaIndex PRIMARY KEY (ViewID, NodeID, MeetingID), "+
															"CONSTRAINT FK_MediaIndex_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_MediaIndex_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_MediaIndex_3 FOREIGN KEY (MeetingID) REFERENCES Meeting (MeetingID) ON DELETE CASCADE)";

	/** The SQL statement to create a new MediaIndex table */
    //TODO: Work on locking functions has been frozen. Finish or delete.
	public static final String CREATE_NODELOCK_TABLE = "CREATE TABLE NodeLock ( " +
                                                       "LockID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                                                       "OwnerID varchar(50) NOT NULL, " +
                                                       "NodeID varchar(50) NOT NULL UNIQUE, " +
                                                       "created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
													   "ModificationDate DOUBLE, "+
                                                       "CONSTRAINT FK_NodeLock_1 FOREIGN KEY (OwnerID) REFERENCES Users (UserID) ON DELETE CASCADE, " +
                                                       "CONSTRAINT FK_NodeLock_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE)";


}

