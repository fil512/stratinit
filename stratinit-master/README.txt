Add no longer supported jars to maven:

For

   				<groupId>com.carbonfive.db-support</groupId>
   				<artifactId>db-migration</artifactId>
   				<version>0.9.9-m5</version>

Run:

mvn install:install-file -Dfile=lib/db-migration-0.9.9-m5.jar -DgroupId=com.carbonfive.db-support -DartifactId=db-migration -Dversion=0.9.9-m5 -Dpackaging=jar -DgeneratePom=true

For
				<groupId>com.carbonfive.db-support</groupId>
				<artifactId>db-migration-maven-plugin</artifactId>
				<version>0.9.9-m5</version>

Run:

mvn install:install-file -Dfile=lib/db-support-0.9.9-m5.jar -DgroupId=com.carbonfive.db-support -DartifactId=db-migration-maven-plugin -Dversion=0.9.9-m5 -Dpackaging=jar -DgeneratePom=true
