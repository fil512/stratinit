cd ..\stratinit-core
call mvn install
cd ..\stratinit-dao
call mvn install
cd ..\stratinit-remote
call mvn -DskipTests=true install
cd ..\stratinit-server
call mvn gwt:eclipse
