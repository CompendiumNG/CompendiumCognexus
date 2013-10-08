# Test harness for the sync functionality
# This is expecting to be run from within a Cygwin Bash shell.

echo Starting test harness...

rm *sync_*.log

echo Checking run with defined last_sync and new_now
SYNC_CLASSPATH="./;../dist/System/lib/sync.jar;./lib/mysql.jar;./lib/derby.jar;./;"

echo $CLASSPATH
echo $SYNC_CLASSPATH

SYNC_PROPERTIES=./sync.properties

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES 

# test not puking on missing parameters
# date sometime in 2033
# i.e. should sync nothing

LAS_SYN=2000000000000
NEW_NOW=2000000000001

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN 

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

derby_test_zero ()
{
	# java -cp $SYNC_CLASSPATH dq $SYNC_PROPERTIES "$1"

	TEST_RESULT=`java -cp $SYNC_CLASSPATH dq $SYNC_PROPERTIES "$1" | grep ROW_COUNT`
	
	# echo $TEST_RESULT

	if [ "$TEST_RESULT" = "ROW_COUNT=0" ]
	then
		echo Test $TEST_RESULT_NAME ............................... good.
	else
		echo ------------------------------------------------------
		echo test $TEST_RESULT_NAME fail
		echo TEST_RESULT is $TEST_RESULT
		echo output is
		java -cp $SYNC_CLASSPATH dq $SYNC_PROPERTIES "$1"
		echo Exiting.
		echo ------------------------------------------------------
		exit
	fi
}

derby_test_one ()
{
	# java -cp $SYNC_CLASSPATH dq $SYNC_PROPERTIES "$1" 

	TEST_RESULT=`java -cp $SYNC_CLASSPATH dq $SYNC_PROPERTIES "$1" | grep ROW_COUNT`
	
	# echo $TEST_RESULT

	if [ "$TEST_RESULT" = "ROW_COUNT=1" ]
	then
		echo Test $TEST_RESULT_NAME ............................... good.
	else
		echo ------------------------------------------------------
		echo test $TEST_RESULT_NAME fail
		echo TEST_RESULT is $TEST_RESULT
		echo output is
		java -cp $SYNC_CLASSPATH dq $SYNC_PROPERTIES "$1" 
		echo Exiting.
		echo ------------------------------------------------------
		exit
	fi
}

mysql_test_zero ()
{
	# mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 -e "$1" -E mysql_test_01_1296668217246

	TEST_RESULT=`mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 -e "$1" -E mysql_test_01_1296668217246 | grep row_count | sed 's/: /=/g'`
	
	# echo $TEST_RESULT

	if [ "$TEST_RESULT" = "row_count=0" ]
	then
		echo Test $TEST_RESULT_NAME ............................... good.
	else
		echo ------------------------------------------------------
		echo test $TEST_RESULT_NAME fail
		echo TEST_RESULT is $TEST_RESULT
		echo output is 
		mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 -e "$1" -E mysql_test_01_1296668217246
		echo Exiting.
		echo ------------------------------------------------------
		exit
	fi
}

mysql_test_one ()
{
	# mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 -e "$1" -E mysql_test_01_1296668217246

	TEST_RESULT=`mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 -e "$1" -E mysql_test_01_1296668217246 | grep row_count | sed 's/: /=/g'`
	
	# echo $TEST_RESULT

	if [ "$TEST_RESULT" = "row_count=1" ]
	then
		echo Test $TEST_RESULT_NAME ............................... good.
	else
		echo ------------------------------------------------------
		echo test $TEST_RESULT_NAME fail
		echo TEST_RESULT is $TEST_RESULT
		echo output is
		mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 -e "$1" -E mysql_test_01_1296668217246
		echo Exiting.
		echo ------------------------------------------------------
		exit
	fi
}

grep last_sync_time sync.properties

echo Starting Test.
source ./checks_sql.sh
source ./test_descriptions.sh

echo Wiping and Installing mysql database....
mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/mysql_test_01_1296668217246.sql
mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/add_mod_dates.sql
echo Done.

# Alternate method:  log in to system db and reload DB from there, saves bandwidth if needed.
#   wipe.sh:  Runs the same scripts as previous paragraph just locally instead of sending the sql over the wire.
# ssh matt@72.1.100.208

echo Wiping derby database....
cd /cygdrive/c/Program\ Files/Compendium/System/resources/Databases
rm -rf ./derby_test_01_1296666094746

# Build a derby database as a set of files, leave out all the svn files

echo Installing derby database....
mkdir derby_test_01_1296666094746
cd derby_test_01_1296666094746

cp /cygdrive/c/Users/MATT/projects/cognexus/cvs/trunk/Sync/derby_test_01_1296666094746/db.lck .
cp /cygdrive/c/Users/MATT/projects/cognexus/cvs/trunk/Sync/derby_test_01_1296666094746/service.properties .

mkdir log
mkdir seg0
mkdir tmp

cd log
cp /cygdrive/c/Users/MATT/projects/cognexus/cvs/trunk/Sync/derby_test_01_1296666094746/log/*.ctrl .
cp /cygdrive/c/Users/MATT/projects/cognexus/cvs/trunk/Sync/derby_test_01_1296666094746/log/*.dat .
cd ..

cd seg0
cp /cygdrive/c/Users/MATT/projects/cognexus/cvs/trunk/Sync/derby_test_01_1296666094746/seg0/*.dat .
cd ..

cd /cygdrive/c/Users/MATT/projects/cognexus/cvs/trunk/Sync

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/add_mod_dates.sql

echo Databases in place.

TEST_NUM=001
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_001_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_001_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_001_B_SQL"

TEST_RESULT_NAME=check${TEST_NUM}preC
derby_test_zero "$CHECK_001_C_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1296750000000
NEW_NOW=1296760000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_001_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_001_B_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postC
derby_test_one "$CHECK_001_C_SQL"

TEST_NUM=002
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_002_DESC
echo --------------------------------------------------------------------
  
TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_002_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1296760000000
NEW_NOW=1297200000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_002_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_002_B_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postC
derby_test_one "$CHECK_002_C_SQL"

TEST_NUM=003
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_003_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_003_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1296760000000
NEW_NOW=1297265000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_003_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_003_B_SQL"

TEST_NUM=004
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_004_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_004_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297265000000
NEW_NOW=1297270000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_004_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_004_B_SQL"

TEST_NUM=005
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_005_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_005_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297270000000
NEW_NOW=1297353000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_005_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_005_B_SQL"

TEST_NUM=006
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_006_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_006_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297353000000
NEW_NOW=1297356000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_006_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_006_B_SQL"

TEST_NUM=007
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_007_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_007_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297356000000
NEW_NOW=1297697500000    

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_007_A_SQL"

TEST_NUM=008
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_008_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_008_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297697500000
NEW_NOW=1297710000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_008_A_SQL"

TEST_NUM=009
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_009_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_009_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297710000000
#       1297984079787
NEW_NOW=1297984100000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_009_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_009_B_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postC
derby_test_one "$CHECK_009_C_SQL"

TEST_NUM=010
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_010_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_010_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297984100000
#       1297984765982
NEW_NOW=1297984800000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_010_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_010_B_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postC
mysql_test_one "$CHECK_010_C_SQL"

TEST_NUM=011
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_011_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_011_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297984800000
#       1297986884517
NEW_NOW=1297987000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_011_A_SQL"

TEST_NUM=012
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_012_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
# mysql_test_zero "$CHECK_012_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_${TEST_NUM}.sql

LAS_SYN=1297987000000
#       1297987771497
NEW_NOW=1297988000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
# mysql_test_one "$CHECK_012_A_SQL"

TEST_NUM=013
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_013_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_013_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1297987000000
#       1297989407461
NEW_NOW=1297990000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_013_A_SQL"

TEST_NUM=014
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_014_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
# mysql_test_zero "$CHECK_014_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1297988000000
#       1297990375118
NEW_NOW=1297991000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
# mysql_test_one "$CHECK_014_A_SQL"

TEST_NUM=015
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_015_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_015_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1297991000000
#       1298595199424
NEW_NOW=1298595200000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_015_A_SQL"

TEST_NUM=016
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_016_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_016_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1298595200000
#       1298595744592
NEW_NOW=1298595800000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_016_A_SQL"

TEST_NUM=017
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_017_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_017_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1298595800000
#       1298596339364
NEW_NOW=1298596400000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_017_A_SQL"

TEST_NUM=018
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_018_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_018_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1298596400000
#       1298596783018
NEW_NOW=1298596800000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_018_A_SQL"

TEST_NUM=019
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_019_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA

derby_test_zero "$CHECK_019_A_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1298596800000
#       1298597858311
NEW_NOW=1298597900000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_019_A_SQL"

TEST_NUM=020
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_020_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_020_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1298597900000
#       1298647887431
NEW_NOW=1298647890000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_020_A_SQL"

TEST_NUM=021
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_021_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_021_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_021_B_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1298647890000
#       1299167485718
NEW_NOW=1299167490000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW #  | grep -v LOG | grep -v select | grep -v insert | grep -v update

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_021_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_021_B_SQL"

TEST_NUM=022
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_022_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_022_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preB
mysql_test_zero "$CHECK_022_B_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1299167490000
#       1299168984970
NEW_NOW=1299168990000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v update | grep -v insert

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_022_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_022_B_SQL"

TEST_NUM=023
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_023_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_023_A_DERBY_SQL"
mysql_test_zero "$CHECK_023_A_MYSQL_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_023_B_SQL"
mysql_test_zero "$CHECK_023_B_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_Mysql_$TEST_NUM.sql
java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_Derby_$TEST_NUM.sql

LAS_SYN=1299168990000
#       1301409910691
NEW_NOW=1301410000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v update | grep -v insert

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_023_A_MYSQL_SQL"
derby_test_one "$CHECK_023_A_DERBY_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_023_B_SQL"
derby_test_one "$CHECK_023_B_SQL"

TEST_NUM=024
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_024_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_024_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_024_B_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1301410000000
#       1301504760066
NEW_NOW=1301504800000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v insert | grep -v update

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_024_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_024_B_SQL"

TEST_NUM=025
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_025_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_025_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preB
mysql_test_zero "$CHECK_025_B_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1301504800000
#       1301506110908
NEW_NOW=1301507000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v update | grep -v insert

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_025_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_025_B_SQL"

TEST_NUM=026
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_026_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_026_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_026_B_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1301507000000
#       1303139763225
NEW_NOW=1303139770000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v insert | grep -v update

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_026_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_026_B_SQL"

TEST_NUM=027
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_027_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_027_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preB
mysql_test_zero "$CHECK_027_B_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1303139770000
#       1303159202554
NEW_NOW=1303159210000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW # | grep -v LOG | grep -v select | grep -v update | grep -v insert

TEST_RESULT_NAME=check${TEST_NUM}postA
# mysql_test_one "$CHECK_027_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postB
# mysql_test_one "$CHECK_027_B_SQL"

TEST_NUM=028
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_028_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_028_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_028_B_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1303159210000
#       1303161428782
NEW_NOW=1303161430000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v insert | grep -v update

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_028_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_028_B_SQL"

TEST_NUM=029
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_029_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_zero "$CHECK_029_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preB
mysql_test_zero "$CHECK_029_B_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1303161430000
#       1303167635299
NEW_NOW=1303167640000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v update | grep -v insert

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_one "$CHECK_029_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_029_B_SQL"

TEST_NUM=030
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_030_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_zero "$CHECK_030_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_030_B_SQL"

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1303167640000
#       1303231830250
NEW_NOW=1303231840000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v insert | grep -v update

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_one "$CHECK_030_A_SQL"

TEST_RESULT_NAME=check${TEST_NUM}postB
derby_test_one "$CHECK_030_B_SQL"


# TEST_NUM=031
# echo --------------------------------------------------------------------
# echo $TEST_NUM $TEST_031_DESC
# echo --------------------------------------------------------------------

# TBD

TEST_NUM=032
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_032_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
mysql_test_one "$CHECK_032_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preB
mysql_test_zero "$CHECK_032_B_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preC
derby_test_one "$CHECK_032_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1303231840000
#       1305137210635
NEW_NOW=1305137300000

mysql -ujconklin_comp01a -p5tgb6yhn -h72.1.100.208 mysql_test_01_1296668217246 < ./sql_files/Test_$TEST_NUM.sql

TEST_RESULT_NAME=check${TEST_NUM}postA
mysql_test_zero "$CHECK_032_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_032_B_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postC
derby_test_zero "$CHECK_032_A_SQL"

TEST_NUM=033
echo --------------------------------------------------------------------
echo $TEST_NUM $TEST_033_DESC
echo --------------------------------------------------------------------

TEST_RESULT_NAME=check${TEST_NUM}preA
derby_test_one "$CHECK_033_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preB
derby_test_zero "$CHECK_033_B_SQL"
TEST_RESULT_NAME=check${TEST_NUM}preC
mysql_test_one "$CHECK_033_A_SQL"

java -cp $SYNC_CLASSPATH dl $SYNC_PROPERTIES ./sql_files/Test_$TEST_NUM.sql

LAS_SYN=1305137300000
#       1305141815700
NEW_NOW=1305140000000

java -cp $SYNC_CLASSPATH Sync2 $SYNC_PROPERTIES $LAS_SYN $NEW_NOW | grep -v LOG | grep -v select | grep -v update | grep -v insert

TEST_RESULT_NAME=check${TEST_NUM}postA
derby_test_zero "$CHECK_032_A_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postB
mysql_test_one "$CHECK_032_B_SQL"
TEST_RESULT_NAME=check${TEST_NUM}postC
mysql_test_zero "$CHECK_032_A_SQL"

echo ------------------------------------------------------------------------
echo ------------------------------------------------------------------------
echo SUCCESS:  Exiting test harness.
echo ------------------------------------------------------------------------
echo ------------------------------------------------------------------------

