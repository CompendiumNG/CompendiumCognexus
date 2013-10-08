
INSERT INTO Node (NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate, Label, Detail, CurrentStatus, LastModAuthor) VALUES (
'19216801021297197692874', 
4, 
'', 
'', 
'Matt Stucky', 
1.297197692874E12, 
1.297197692874E12 , 
'', 
'', 
0, 
'Matt Stucky'
);

INSERT INTO NodeUserState (NodeID, UserID, State) VALUES (
'19216801021297197692874',
'19216811051290214225597',
2 
);

INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, FontSize, FontFace, FontStyle, Foreground, Background) VALUES ( 
'19216811001221456251329',
'19216801021297197692874',
697,
421,
1.297197692874E12,
1.297197692874E12,
0,
'Y',
'Y',
'Y',
'Y',
'N',
'N',
25,
14,
'Dialog',
1,
-16777216,
-1 
);

Update Node SET Label = 'test delta - new on Derby ', ModificationDate = 1.297197702244E12 , LastModAuthor = 'Matt Stucky' 
WHERE NodeID = '19216801021297197692874';

UPDATE NodeUserState SET State = 3 WHERE NodeID = '19216801021297197692874' AND State =  2; 

UPDATE NodeUserState SET State = 2 WHERE NodeID = '19216801021297197692874' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewNode SET 
XPos = 622, 
YPos = 421, 
ModificationDate = 1.297197702271E12
WHERE 
ViewID = '19216811001221456251329' AND 
NodeID = '19216801021297197692874' AND CurrentStatus = 0;

UPDATE ViewProperty set 
HorizontalScroll = 0, 
VerticalScroll = 9, 
Width = 1148, 
Height = 642, 
XPosition = 11, 
YPosition = 43, 
IsIcon = 'N', 
IsMaximum = 'N' 
WHERE 
UserID = '19216811051290214225597' AND 
ViewID = '19216811001221456251329';
 

UPDATE ViewProperty set 
HorizontalScroll = 0, 
VerticalScroll = 0, 
Width = 1006, 
Height = 576, 
XPosition = 89, 
YPosition = 132, 
IsIcon = 'N', 
IsMaximum = 'N' 
WHERE 
UserID = '19216811051290214225597' AND 
ViewID = '19216811051290214225612';
