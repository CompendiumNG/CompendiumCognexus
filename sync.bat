echo off

set BASE_DIR=C:\Users\MATT\projects\cognexus\cvs\trunk\dist\System\lib
echo %BASE_DIR%

set SYNC_CLASSPATH="%BASE_DIR%\sync.jar;%BASE_DIR%\mysql-connector-java-5.1.6-bin.jar;%BASE_DIR%\derby.jar;"
rem echo %CLASSPATH%
echo %SYNC_CLASSPATH%

set SYNC_PROPERTIES="C:\Users\MATT\projects\cognexus\cvs\trunk\sync.properties"
echo %SYNC_PROPERTIES%

java -cp "%SYNC_CLASSPATH%" com.compendium.Sync "%SYNC_PROPERTIES%" 
