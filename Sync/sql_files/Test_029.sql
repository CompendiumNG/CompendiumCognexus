Update Node SET Detail = 'now it is 97% done ha!', ModificationDate = 1.303167635251E12, LastModAuthor = 'Matt Stucky' 
WHERE NodeID = '19216811091296068450602';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.303167635277E12 WHERE NodeID = '19216811091296068450602' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.30316763528E12 WHERE NodeID = '19216811091296068450602' AND 
UserID = '19216811051290214225597' AND State = 3;

INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) 
VALUES ('19216811091296068450602', 'Matt Stucky', 1, 1.303167635251E12, 1.303167635251E12, 'now it is 97% done ha!!');

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.303167635297E12 WHERE NodeID = '19216811091296068450602' AND 
State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.303167635299E12 WHERE NodeID = '19216811091296068450602' AND 
UserID = '19216811051290214225597' AND State = 3;

