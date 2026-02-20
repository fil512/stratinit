Start postgres in docker:

docker run  -p 5432:5432 --name ken-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgr3S -e POSTGRES_DB=stratinit -d postgres

Build:
cd stratinit-master
mvn clean install -DskipTests

Database migration:
mvn compile flyway:migrate -pl stratinit-dao
mvn flyway:info -pl stratinit-dao

Run the server:
mvn spring-boot:run -pl stratinit-rest

Run the React dev server (hot reload):
cd stratinit-ui && npm run dev

Access the app:
- Development: http://localhost:5173 (React dev server, proxies API to backend)
- Production: http://localhost:8081 (bundled static assets served by Spring Boot)

Run all tests:
mvn clean install

Legacy SWT client (being retired):
cd stratinit-client-master
mvn -Pwin64 clean install
