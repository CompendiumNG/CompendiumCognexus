# everything here must have a row_count output, that is what is searched for by the tests
#
CHECK_001_A_SQL="select count(*) as row_count from Node where NodeID = '19216801021296750566706'"
CHECK_001_B_SQL="Select count(*) as row_count from NodeUserState where NodeID='19216801021296750566706' and UserID='19216811051290214225597'"
CHECK_001_C_SQL="Select count(*) as row_count from ViewNode WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296750566706'"
# 
CHECK_002_A_SQL="select count(*) as row_count from Node where NodeID = '19216801021297197692874'"
CHECK_002_B_SQL="Select count(*) as row_count from NodeUserState where NodeID='19216801021297197692874' and UserID='19216811051290214225597'"
CHECK_002_C_SQL="Select count(*) as row_count from ViewNode WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021297197692874'"
#
CHECK_003_A_SQL="select count(*) as row_count from Node where NodeID = '19216801021296750566706' and CAST(Detail as Varchar(256)) = 'adding a detail here'"
CHECK_003_B_SQL="select count(*) as row_count from NodeDetail where NodeID = '19216801021296750566706' and CAST(Detail as Varchar(256)) = 'adding a detail here'"
#
CHECK_004_A_SQL="select count(*) as row_count from ViewNode WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296417117782' AND XPos = 714 AND YPos = 324"
CHECK_004_B_SQL="select count(*) as row_count from NodeDetail WHERE NodeID = '19216801021296417117782' and Detail = 'moved this node'"
#
CHECK_005_A_SQL="select count(*) as row_count from Link where LinkID='19216801021297352327724'"
CHECK_005_B_SQL="select count(*) as row_count from ViewLink where ViewID='19216811001221456251329' and LinkID='19216801021297352327724'"
#
CHECK_006_A_SQL="select count(*) as row_count from Link where LinkID='19216801021297355879576'"
CHECK_006_B_SQL="select count(*) as row_count from ViewLink where ViewID='19216811001221456251329' and LinkID='19216801021297355879576'"
#
CHECK_007_A_SQL="select count(*) as row_count from Link where LinkID = '19216811091296068463118' and CAST(Label as Varchar(256)) = 'upd'"
#
CHECK_008_A_SQL="select count(*) as row_count from Link where LinkID = '19216811091296068463118' and Label = 'changed' and LinkType = 41"
#
CHECK_009_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216811041296068260911' and CurrentStatus = 3"
CHECK_009_B_SQL="select count(*) as row_count from ViewNode WHERE ViewID = '19216811001221456251329' AND NodeID = '19216811041296068260911' and CurrentStatus = 3"
CHECK_009_C_SQL="select count(*) as row_count from ViewLink WHERE ViewID = '19216811001221456251329' AND LinkID = '19216811041296068276458' and CurrentStatus = 3"
#
CHECK_010_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216801021296750566706' and CurrentStatus = 3"
CHECK_010_B_SQL="select count(*) as row_count from ViewNode WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296750566706' and CurrentStatus = 3"
CHECK_010_C_SQL="select count(*) as row_count from ViewLink WHERE ViewID = '19216811001221456251329' AND LinkID = '19216801021297352327724' and CurrentStatus = 3"
#
CHECK_011_A_SQL="select count(*) as row_count from ViewLink WHERE ViewID = '19216811001221456251329' AND LinkID = '192168121296520469218' and CurrentStatus = 3"
#
CHECK_012_A_SQL="select count(*) as row_count from ViewLink WHERE ViewID = '19216811001221456251329' AND LinkID = '19216801021297355879576' and CurrentStatus = 3"
#
CHECK_013_A_SQL="select count(*) as row_count from ViewNode WHERE ViewID='19216811001221456251329' AND ViewNode.CurrentStatus = 0 AND NodeID = '19216801021296417117782' and Foreground=-16711936"
#
CHECK_014_A_SQL="select count(*) as row_count from ViewNode WHERE ViewID='19216811001221456251329' AND ViewNode.CurrentStatus = 0 AND NodeID = '19216801021297197692874' and Foreground=-65281"
#
CHECK_015_A_SQL="select count(*) as row_count from NodeUserState WHERE NodeID='192168121296520321296' and UserID='19216811051290214225597' and State=2"
#
CHECK_016_A_SQL="select count(*) as row_count from NodeUserState WHERE NodeID='19216811041296067593583' and UserID='19216811051290214225597' and State=2"
#
CHECK_017_A_SQL="select count(*) as row_count from Code WHERE CodeID = '19216801011298596339364'"
#
CHECK_018_A_SQL="select count(*) as row_count from Code WHERE CodeID = '19216801011298596783018'"
#
CHECK_019_A_SQL="select count(*) as row_count from NodeCode WHERE NodeID='19216801021296417117782' and CodeID='1282093118927906450859'"
#
CHECK_020_A_SQL="select count(*) as row_count from NodeCode WHERE NodeID='19216811041296067689223' and CodeID='19216801011298596339364'"
#
CHECK_021_A_SQL="select count(*) as row_count from Node where NodeID ='19216801011299167485106'"
CHECK_021_B_SQL="select count(*) as row_count from ReferenceNode where NodeID ='19216801011299167485106'"
#
CHECK_022_A_SQL="select count(*) as row_count from Node where NodeID ='19216801011299168984661'"
CHECK_022_B_SQL="select count(*) as row_count from ReferenceNode where NodeID ='19216801011299168984661'"
#
CHECK_023_A_MYSQL_SQL="select count(*) as row_count from Node where NodeID = '19216801021296417117782' and Label = 'Matt Test Alpha - conflict server side'"
CHECK_023_A_DERBY_SQL="select count(*) as row_count from Node where NodeID = '19216801021296417117782' and CAST(Label as Varchar(256)) = 'Matt Test Alpha - conflict server side'"
CHECK_023_B_SQL="select count(*) as row_count from ViewNode WHERE ViewID = '19216811001221456251329' AND NodeID = '19216801021296417117782' AND CurrentStatus = 0 and XPos = 699 and YPos = 324"
#
CHECK_024_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216801011301504647676' and CAST(Label as Varchar(256)) = 'new node with ''single quote'''"
CHECK_024_B_SQL="select count(*) as row_count from NodeDetail WHERE NodeID = '19216801011301504647676' and CAST(Detail as Varchar(256)) = 'some ''single quotes'' in the detail too'"
#
CHECK_025_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216801011301506083764' and Label = 'test \'node\' with single quotes'"
CHECK_025_B_SQL="select count(*) as row_count from NodeDetail WHERE NodeID = '19216801011301506083764' and Detail = 'some \'single quotes\' in the detail too.'"
#
CHECK_026_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216801011301504647676' and Detail like '%backslashes%'"
CHECK_026_B_SQL="select count(*) as row_count from NodeDetail WHERE NodeID = '19216801011301504647676' and Detail like '%backslashes%' and PageNo = 1"
#
CHECK_027_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216801021297197692874' and Detail like '%program files%'"
CHECK_027_B_SQL="select count(*) as row_count from NodeDetail WHERE NodeID = '19216801021297197692874' and Detail like '%program files%' and PageNo = 1"
#
CHECK_028_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216811041296067593583' and CAST(Detail as Varchar(256))= 'it is 96% done'"
CHECK_028_B_SQL="select count(*) as row_count from NodeDetail WHERE NodeID = '19216811041296067593583' and CAST(Detail as Varchar(256))= 'it is 96% done' and PageNo = 1"
#
CHECK_029_A_SQL="select count(*) as row_count from Node WHERE NodeID = '19216811091296068450602' and Detail = 'now it is 97% done ha!'"
CHECK_029_B_SQL="select count(*) as row_count from NodeDetail WHERE NodeID = '19216811091296068450602' and Detail = 'now it is 97% done ha!!' and PageNo = 1"
#
CHECK_030_A_SQL="select count(*) as row_count from CodeGroup WHERE CodeGroupID = '19216801981303231809914'"
CHECK_030_B_SQL="select count(*) as row_count from GroupCode WHERE CodeID = '1282093118927906458109' and CodeGroupID = '19216801981303231809914'"
#
CHECK_032_A_SQL="select count(*) as row_count from Link WHERE LinkID = '19216811041296067703442'"
CHECK_032_B_SQL="select count(*) as row_count from Audit WHERE ItemID = '19216811041296067703442'"
#
CHECK_033_A_SQL="select count(*) as row_count from Link WHERE LinkID = '19216811091296068463118'"
CHECK_033_B_SQL="select count(*) as row_count from Audit WHERE ItemID = '19216811091296068463118'"
#