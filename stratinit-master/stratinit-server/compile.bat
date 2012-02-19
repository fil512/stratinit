cd ..\stratinit-remote
call mvn -DskipTests=true install
cd ..\stratinit-server
call mvn gwt:eclipse
