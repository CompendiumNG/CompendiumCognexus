Update Node SET Detail = 'it is 96% done', ModificationDate = 1.303161428038E+12, LastModAuthor = 'Matt Stucky' 
WHERE NodeID = '19216811041296067593583';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.303161428343E+12 WHERE NodeID = '19216811041296067593583' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.303161428459E+12 WHERE NodeID = '19216811041296067593583' AND 
UserID = '19216811051290214225597' AND State = 3;

INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) VALUES ('19216811041296067593583', 
'Matt Stucky', 1, 1.303161428038E+12, 1.303161428038E+12, 'it is 96% done');

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.30316142863E+12 WHERE NodeID = '19216811041296067593583' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.303161428782E+12 WHERE NodeID = '19216811041296067593583' AND 
UserID = '19216811051290214225597' AND State = 3;
