Update Node SET Label = 'Matt Test Alpha - conflict server side', ModificationDate = 1.301409840009E+12, 
LastModAuthor = 'Matt Stucky' WHERE NodeID = '19216801021296417117782';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.301409840101E+12 WHERE NodeID = '19216801021296417117782' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.301409840137E+12 
WHERE NodeID = '19216801021296417117782' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewNode SET XPos = 699, YPos = 324, ModificationDate = 1.301409840155E+12 
WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296417117782' AND CurrentStatus = 0;
