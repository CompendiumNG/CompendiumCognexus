-- updating a node on mysql

-- looks like an update to node and then an insert to nodeDetail

-- the NodeUserState updates appear to be irrelevant

Update Node SET Detail = 'adding a detail here', ModificationDate = 1.297264899882E+12, LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801021296750566706';

UPDATE NodeUserState SET State = 3 WHERE NodeID = '19216801021296750566706' AND State =  2;

UPDATE NodeUserState SET State = 2 WHERE NodeID = '19216801021296750566706' AND UserID = '19216811051290214225597' AND State = 3;

INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) VALUES ('19216801021296750566706', 'Matt Stucky', 1, 1.297264899882E+12, 1.297264899882E+12, 'adding a detail here');

UPDATE NodeUserState SET State = 3 WHERE NodeID = '19216801021296750566706' AND State =  2;

UPDATE NodeUserState SET State = 2 WHERE NodeID = '19216801021296750566706' AND UserID = '19216811051290214225597' AND State = 3;
