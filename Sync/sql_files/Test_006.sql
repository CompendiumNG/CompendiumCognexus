
INSERT INTO Link (LinkID, CreationDate, ModificationDate, Author, LinkType, OriginalID, FromNode, ToNode, Label, Arrow, CurrentStatus) 
VALUES ('19216801021297355879576', 1.297355879576E12, 1.297355879576E12, 'Matt Stucky', '39', '', '19216801021296417117782', '19216801021297197692874',
'', 1, 0);

UPDATE ViewLink SET CurrentStatus = 0 
WHERE ViewID = '19216811001221456251329' AND LinkID = '19216801021297355879576';

INSERT INTO ViewLink (ViewID, LinkID, CreationDate, ModificationDate, CurrentStatus) 
VALUES ('19216811001221456251329', '19216801021297355879576', 1.297355879641E12, 1.297355879641E12, 0);

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 9, Width = 1148, Height = 642, XPosition = 11, YPosition = 43, IsIcon = 'N', 
IsMaximum = 'N' 
WHERE UserID = '19216811051290214225597' AND ViewID = '19216811001221456251329';

UPDATE ViewProperty set HorizontalScroll = 0, VerticalScroll = 0, Width = 1006, Height = 576, XPosition = 89, YPosition = 132, IsIcon = 'N', 
IsMaximum = 'N' 
WHERE UserID = '19216811051290214225597' AND ViewID = '19216811051290214225612';
