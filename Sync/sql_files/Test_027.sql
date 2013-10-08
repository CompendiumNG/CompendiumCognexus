Update Node SET Detail = 'nothing of value to be found in\n\nc:\\program files\\office\\templates', ModificationDate = 1.303159202439E12, 
LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801021297197692874' ;

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.303159202475E12 WHERE NodeID = '19216801021297197692874' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.303159202484E12 WHERE NodeID = '19216801021297197692874' AND 
UserID = '19216811051290214225597' AND State = 3;

INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) VALUES ('19216801021297197692874',
'Matt Stucky', 1, 1.303159202439E12, 1.303159202439E12, 'nothing of value to be found in\n\nc:\\program files\\office\\templates');

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.303159202552E12 WHERE NodeID = '19216801021297197692874' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.303159202554E12 WHERE NodeID = '19216801021297197692874' AND 
UserID = '19216811051290214225597' AND State = 3;