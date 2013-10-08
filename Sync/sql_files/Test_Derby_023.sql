Update Node SET Label = 'Matt Test Alpha - conflict client side', ModificationDate = 1.301409910621E12, LastModAuthor = 'Matt Stucky' 
WHERE NodeID = '19216801021296417117782';

UPDATE NodeUserState SET State = 3 ,ModificationDate = 1.30140991067E12 
WHERE NodeID = '19216801021296417117782' AND State =  2;

UPDATE NodeUserState SET State = 2 ,ModificationDate = 1.30140991068E12 
WHERE NodeID = '19216801021296417117782' AND UserID = '19216811051290214225597' AND State = 3;

UPDATE ViewNode SET XPos = 703, YPos = 324, ModificationDate = 1.301409910691E12 
WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296417117782' AND CurrentStatus = 0;
