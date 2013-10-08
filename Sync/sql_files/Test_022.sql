INSERT INTO Node 
(NodeID, NodeType, ExtendedNodeType, OriginalID,
Author, CreationDate, ModificationDate, 
Label, Detail, 
CurrentStatus, LastModAuthor) 
VALUES ('19216801011299168984661', 9, '', '', 
'Matt Stucky', 1.299168984661E12, 1.299168984661E12, 
'GO TO: new stuff', 
'From: Matt Stucky
Message: derby cmail to mike knowles test
-------
Link to node in ( The Commons )
', 0, 'Matt Stucky');

INSERT INTO NodeUserState (NodeID, UserID, State, ModificationDate) 
VALUES ('19216801011299168984661', '19216811051290214225597', 2, 1.29916898475E12);

INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, 
ShowTags, ShowText, ShowTrans, ShowWeight, 
SmallIcon, HideIcon, LabelWrapWidth, FontSize, FontFace, FontStyle, Foreground, Background) 
VALUES ('19216811001261426628037', '19216801011299168984661', 0, 10, 1.299168984661E12, 1.299168984661E12, 0, 
'Y', 'Y', 'Y', 'Y', 'N', 'N', 25, 14, 'Dialog' ,1, -16777216, -1);

UPDATE ReferenceNode SET Source = 'comp://19216811001221456251329/19216811091296520833803', 
ImageSource = '', ModificationDate = 1.299168984879E12 
WHERE NodeID = '19216801011299168984661';

INSERT INTO ReferenceNode (NodeID, Source, ImageSource, ModificationDate) VALUES ('19216801011299168984661', 
'comp://19216811001221456251329/19216811091296520833803', 
'', 1.299168984887E12);

Update Node SET ModificationDate = 1.299168984872E12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801011299168984661';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.299168984945E12 WHERE NodeID = '19216801011299168984661' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.299168984947E12 WHERE NodeID = '19216801011299168984661' AND 
UserID = '19216811051290214225597' AND State = 3;

DELETE FROM NodeUserState WHERE NodeID = '19216801011299168984661' AND UserID = '19216811051290214225597';

INSERT INTO NodeUserState (NodeID, UserID, State, ModificationDate) VALUES ('19216811001261426628037', '19216811051290214225597', 
3, 1.29916898497E12);