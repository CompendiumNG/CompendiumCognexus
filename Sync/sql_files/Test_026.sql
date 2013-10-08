Update Node SET Detail = 'some \'single quotes\' in the detail too\n\nnow it has backslashes C:\\temp\\myo.b', ModificationDate = 1.303139762428E+12,
LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801011301504647676';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.303139762748E+12 WHERE NodeID = '19216801011301504647676' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.303139762879E+12 WHERE NodeID = '19216801011301504647676' AND 
UserID = '19216811051290214225597' AND State = 3;

Update NodeDetail SET Detail = 'some \'single quotes\' in the detail too\n\nnow it has backslashes C:\\temp\\myo.b', 
CreationDate = 1.301504759744E+12, ModificationDate = 1.303139762428E+12 WHERE NodeID = '19216801011301504647676' AND PageNo = 1;

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.303139763099E+12 WHERE NodeID = '19216801011301504647676' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.303139763225E+12 WHERE NodeID = '19216801011301504647676' AND 
UserID = '19216811051290214225597' AND State = 3;
