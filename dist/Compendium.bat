@echo off

REM empty the classpath, it's specified on the command line below
set CLASSPATH=

REM prepend the window system dir to the PATH
PATH=c:\WINDOWS\system32;C:\WINDOWS\SysWOW64;%APPDATA%;%PATH%

cd C:\Program Files (x86)\Compendium

start /b javaw -Xmx1024m -Xms1024m -classpath "System\lib\compendiumcore.jar;System\lib\compendium.jar;System\lib\AppleJavaExtensions.jar;System\lib\jhall.jar;System\lib\kunststoff.jar;System\lib\jabberbeans.jar;System\lib\mysql-connector-java-5.1.6-bin.jar;System\lib\derby.jar;System\lib\triplestore.jar;System\lib\xml.jar;system\lib\sync.jar" com.compendium.ProjectCompendium %1 %2 %3 %4 %5 %6 %7 %8 %9
