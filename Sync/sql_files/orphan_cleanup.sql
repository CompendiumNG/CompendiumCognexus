delete from ExtendedTypeCode where CODEID not in (select CODEID from Code );
delete from GroupCode where CODEID not in (select CODEID from Code );
delete from NodeCode where CODEID not in (select CODEID from Code );
delete from GroupCode where CODEGROUPID not in (select CODEGROUPID from CodeGroup );
delete from ExtendedTypeCode where EXTENDEDNODETYPEID not in (select EXTENDEDNODETYPEID from ExtendedNodeType );
delete from ViewLink where LINKID not in (select LINKID from Link );
delete from MediaIndex where MEETINGID not in (select MEETINGID from Meeting );
delete from Clone where CHILDNODEID not in (select NODEID from Node );
delete from Favorite where NODEID not in (select NODEID from Node );
delete from Favorite where VIEWID not in (select NODEID from Node );
delete from Link where FROMNODE not in (select NODEID from Node );
delete from Link where TONODE not in (select NODEID from Node );
delete from MediaIndex where VIEWID not in (select NODEID from Node );
delete from MediaIndex where NODEID not in (select NODEID from Node );
delete from Meeting where MEETINGMAPID not in (select NODEID from Node );
delete from NodeCode where NODEID not in (select NODEID from Node );
delete from NodeDetail where NODEID not in (select NODEID from Node );
delete from NodeUserState where NODEID not in (select NODEID from Node );
delete from ReferenceNode where NODEID not in (select NODEID from Node );
delete from ReferenceNode where NODEID not in (select NODEID from Node );
delete from ShortCutNode where NODEID not in (select NODEID from Node );
delete from ViewLayer where VIEWID not in (select NODEID from Node );
delete from ViewLink where VIEWID not in (select NODEID from Node );
delete from ViewNode where VIEWID not in (select NODEID from Node );
delete from ViewNode where NODEID not in (select NODEID from Node );
delete from ViewProperty where VIEWID not in (select NODEID from Node );
delete from WorkspaceView where VIEWID not in (select NODEID from Node );
delete from GroupUser where GROUPID not in (select GROUPID from UserGroup );
delete from Permission where GROUPID not in (select GROUPID from UserGroup );
delete from Connections where USERID not in (select USERID from Users );
delete from Favorite where USERID not in (select USERID from Users );
delete from GroupUser where USERID not in (select USERID from Users );
delete from NodeUserState where USERID not in (select USERID from Users );
delete from Preference where USERID not in (select USERID from Users );
delete from ViewLayer where USERID not in (select USERID from Users );
delete from ViewProperty where USERID not in (select USERID from Users );
delete from Workspace where USERID not in (select USERID from Users );
delete from WorkspaceView where WORKSPACEID not in (select WORKSPACEID from Workspace );
