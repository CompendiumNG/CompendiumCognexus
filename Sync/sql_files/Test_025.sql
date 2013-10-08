INSERT INTO Node (NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate, Label, Detail, 
CurrentStatus, LastModAuthor) VALUES ('19216801011301506083764', 4, null, null, 'Matt Stucky', 1.301506083764E12, 1.301506083764E12, '', '', 
0, 'Matt Stucky');

INSERT INTO NodeUserState (NodeID, UserID, State, ModificationDate) VALUES ('19216801011301506083764', '19216811051290214225597', 2, 1.301506083851E12);   

INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, ShowTags, ShowText, ShowTrans, ShowWeight, 
SmallIcon, HideIcon, LabelWrapWidth, FontSize, FontFace, FontStyle, Foreground, Background) 
VALUES ('19216811001221456251329', '19216801011301506083764', 128, 204, 1.301506083764E12, 1.301506083764E12, 0, 
'Y', 'Y', 'Y', 'Y',
'N', 'N', 
25, 14, 'Dialog' ,1, -16777216, -1);

Update Node SET Label = 'test ''node'' with single quotes', ModificationDate = 1.301506094293E12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801011301506083764';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.301506094329E12 WHERE NodeID = '19216801011301506083764' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.301506094338E12 WHERE NodeID = '19216801011301506083764' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewNode SET XPos = 62, YPos = 204, ModificationDate = 1.301506094348E12 WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801011301506083764' AND CurrentStatus = 0;

Update Node SET Detail = 'some ''single quotes'' in the detail too.', ModificationDate = 1.301506108157E12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801011301506083764';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.301506108184E12 WHERE NodeID = '19216801011301506083764' AND State = 2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.301506108187E12 WHERE NodeID = '19216801011301506083764' AND UserID = '19216811051290214225597' AND State = 3;

INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) VALUES ('19216801011301506083764', 'Matt Stucky', 
1, 1.301506108157E12, 1.301506108157E12, 'some ''single quotes'' in the detail too.');

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.301506108204E12 WHERE NodeID = '19216801011301506083764' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.301506108206E12 WHERE NodeID = '19216801011301506083764' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 9, Width = 1148, Height = 642, XPosition = 11, YPosition = 43, IsIcon = 'N', 
IsMaximum = 'N', ModificationDate = 1.301506110861E12 WHERE UserID = '19216811051290214225597' AND ViewID = '19216811001221456251329'; 

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 0, Width = 1006, Height = 576, XPosition = 73, YPosition = 28, IsIcon = 'N', 
IsMaximum = 'N', ModificationDate = 1.301506110908E12 WHERE UserID = '19216811051290214225597' AND ViewID = '19216811051290214225612';