-- create a new node on mysql

-- tables inserted:
--   Node (NodeID, +ModDate)
--   NodeUserState (NodeID+UserId, -ModDate)
--   ViewNode  (ViewID+NodeID, +ModDate)
-- tables updated
--   ViewProperty  (UserID+ViewID, -ModDate)

-- Note that NodeDetail is not affected.

INSERT INTO Node (NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate, Label, Detail, CurrentStatus, LastModAuthor) VALUES ('19216801021296750566706', 3, '', '', 'Matt Stucky', 1.296750566706E+12, 1.296750566706E+12, '', '', 0, 'Matt Stucky');

INSERT INTO NodeUserState (NodeID, UserID, State, ModificationDate) VALUES ('19216801021296750566706', '19216811051290214225597', 2,1.296750566706E+12);

INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, FontSize, FontFace, FontStyle, Foreground, Background) VALUES ('19216811001221456251329', '19216801021296750566706', 927, 317, 1.296750566706E+12, 1.296750566706E+12, 0, 'Y', 'Y', 'Y', 'Y', 'N', 'N', 25, 14, 'Dialog' ,1, -16777216, -1);

Update Node SET Label = 'Matt Test Charlie', ModificationDate = 1.296750574207E+12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801021296750566706';

UPDATE NodeUserState SET State = 3 WHERE NodeID = '19216801021296750566706' AND State =  2;

UPDATE NodeUserState SET State = 2 WHERE NodeID = '19216801021296750566706' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewNode SET XPos = 880, YPos = 317, ModificationDate = 1.29675057434E+12 WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296750566706' AND CurrentStatus = 0;

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 9, Width = 1148, Height = 642, XPosition = 11, YPosition = 43, IsIcon = 'N', IsMaximum = 'N' WHERE UserID = '19216811051290214225597' AND ViewID = '19216811001221456251329';

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 0, Width = 1006, Height = 576, XPosition = 89, YPosition = 132, IsIcon = 'N', IsMaximum = 'N' WHERE UserID = '19216811051290214225597' AND ViewID = '19216811051290214225612';

