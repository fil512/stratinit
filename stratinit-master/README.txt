Add no longer supported jars to maven:

For

   				<groupId>com.carbonfive.db-support</groupId>
   				<artifactId>db-migration</artifactId>
   				<version>0.9.9-m5</version>

Run:

mvn install:install-file -Dfile=lib/db-migration-0.9.9-m5.jar -DgroupId=com.carbonfive.db-support -DartifactId=db-migration -Dversion=0.9.9-m5 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=lib/db-support-0.9.9-m5.jar -DgroupId=com.carbonfive.db-support -DartifactId=db-support -Dversion=0.9.9-m5 -Dpackaging=jar -DgeneratePom=true

Start postgres in docker:

docker run  -p 5432:5432 --name ken-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgr3S -e POSTGRES_DB=stratinit -d postgres