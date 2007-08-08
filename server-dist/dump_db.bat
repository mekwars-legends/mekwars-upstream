@echo off

set DUMPEXE="c:\program files\MySQL\MySQL Server 5.0\bin\mysqldump.exe"
set DBUSER=sa
set DBPASS=master
set DBDATABASE=mekwars

set FILETIME=%1

set OUTPUTFILE= \campaign\backup\db_%FILETIME%.sql

%DUMPEXE% -u %DBUSER% -p%DBPASS% %DBDATABASE% > %OUTPUTFILE%
