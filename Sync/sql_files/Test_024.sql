INSERT INTO Node (NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate, Label, Detail, CurrentStatus, LastModAuthor) VALUES ('19216801011301504647676', 3, '', '', 'Matt Stucky', 1.301504647676E+12, 1.301504647676E+12, '', '', 0, 'Matt Stucky');

INSERT INTO NodeUserState (NodeID, UserID, State, ModificationDate) VALUES ('19216801011301504647676', '19216811051290214225597', 2, 1.301504647962E+12);

INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, FontSize, FontFace, FontStyle, Foreground, Background) VALUES ('19216811001221456251329', '19216801011301504647676', 688, 83, 1.301504647676E+12, 1.301504647676E+12, 0, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 25, 14, 'Dialog' ,1, -16777216, -1);

Update Node SET Label = 'new node with \'single quote\'', ModificationDate = 1.301504662054E+12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801011301504647676';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.30150466215E+12 WHERE NodeID = '19216801011301504647676' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.30150466219E+12 WHERE NodeID = '19216801011301504647676' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewNode SET XPos = 623, YPos = 83, ModificationDate = 1.301504662222E+12 WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801011301504647676' AND CurrentStatus = 0;

Update Node SET Detail = 'some \'single quotes\' in the detail too', ModificationDate = 1.301504759744E+12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801011301504647676';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.301504759853E+12 WHERE NodeID = '19216801011301504647676' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.301504759906E+12 WHERE NodeID = '19216801011301504647676' AND UserID = '19216811051290214225597' AND State = 3;

INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) VALUES ('19216801011301504647676', 'Matt Stucky', 1, 1.301504759744E+12, 1.301504759744E+12, 'some \'single quotes\' in the detail too');

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.301504759963E+12 WHERE NodeID = '19216801011301504647676' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.301504760066E+12 WHERE NodeID = '19216801011301504647676' AND UserID = '19216811051290214225597' AND State = 3;

