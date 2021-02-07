Start postgres in docker:

docker run  -p 5432:5432 --name ken-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgr3S -e POSTGRES_DB=stratinit -d postgres

In stratinit-master:
mvn clean install -DskipTests

in stratinit-server
mvn flyway:info
mvn compile flyway:migrate

mvn clean install
cd stratinit-client-master
mvn -Pwin64 clean install
