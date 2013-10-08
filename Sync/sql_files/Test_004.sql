UPDATE ViewNode SET XPos = 714, YPos = 324, ModificationDate = 1.29726970744E12 
WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296417117782' AND CurrentStatus = 0;

Update Node SET Detail = 'moved this node', ModificationDate = 1.297269715031E12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801021296417117782';

UPDATE NodeUserState SET State = 3 WHERE NodeID = '19216801021296417117782' AND State = 2;

UPDATE NodeUserState SET State = 2 WHERE NodeID = '19216801021296417117782' AND UserID = '19216811051290214225597' AND State = 3;

INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) VALUES (
'19216801021296417117782',
'Matt Stucky',
1,
1.297269715031E12,
1.297269715031E12,
'moved this node'
 );

UPDATE NodeUserState SET State = 3 WHERE NodeID = '19216801021296417117782' AND State =  2;

UPDATE NodeUserState SET State = 2 WHERE NodeID = '19216801021296417117782' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 9, Width = 1148, Height = 642, XPosition = 11, YPosition = 43, IsIcon = 'N', 
IsMaximum = 'N' WHERE UserID = '19216811051290214225597' AND ViewID = '19216811001221456251329';

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 0, Width = 1006, Height = 576, XPosition = 89, YPosition = 132, IsIcon = 'N', 
IsMaximum = 'N' WHERE UserID = '19216811051290214225597' AND ViewID = '19216811051290214225612';
