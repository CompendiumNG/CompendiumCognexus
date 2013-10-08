INSERT INTO Node (NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate, Label, Detail, CurrentStatus, LastModAuthor) VALUES ('19216801011299167485106', 9, '', '', 'Matt Stucky', 1.299167485106E+12, 1.299167485106E+12, 'GO TO: And this proves it', 'From: Matt Stucky\nMessage: cmail node msg body on mysql test\n-------\nLink to node in ( The Commons )\n\n', 0, 'Matt Stucky');

INSERT INTO NodeUserState (NodeID, UserID, State, ModificationDate) VALUES ('19216801011299167485106', '19216811051290214225597', 2, 1.299167485257E+12);

INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, FontSize, FontFace, FontStyle, Foreground, Background) VALUES ('19216811051290215635738', '19216801011299167485106', 0, 10, 1.299167485106E+12, 1.299167485106E+12, 0, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 25, 14, 'Dialog' ,1, -16777216, -1);

UPDATE ReferenceNode SET Source = 'comp://19216811001221456251329/19216811091296068450602', ImageSource = '', ModificationDate = 1.299167485334E+12 WHERE NodeID = '19216801011299167485106';

INSERT INTO ReferenceNode (NodeID, Source, ImageSource, ModificationDate) VALUES ('19216801011299167485106', 'comp://19216811001221456251329/19216811091296068450602', '', 1.299167485351E+12);

Update Node SET ModificationDate = 1.299167485334E+12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801011299167485106';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.299167485533E+12 WHERE NodeID = '19216801011299167485106' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.299167485567E+12 WHERE NodeID = '19216801011299167485106' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.299167485582E+12 WHERE NodeID = '19216801011299167485106' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.299167485628E+12 WHERE NodeID = '19216801011299167485106' AND UserID = '19216811051290214225597' AND State = 3;

DELETE FROM NodeUserState WHERE NodeID = '19216801011299167485106' AND UserID = '19216811051290214225597';

INSERT INTO NodeUserState (NodeID, UserID, State, ModificationDate) VALUES ('19216811051290215635738', '19216811051290214225597', 3, 1.299167485718E+12);
