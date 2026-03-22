# Stage 1: Build frontend (React/Vite via frontend-maven-plugin) and backend (Spring Boot)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY stratinit-master/ ./

RUN mvn clean package -DskipTests -pl stratinit-rest -am -B -Dgit.failOnNoGitDirectory=false \
    && find stratinit-rest/target -name "stratinit-rest-*.jar" ! -name "*-plain.jar" \
       -exec cp {} /app.jar \;

# Stage 2: JRE 21 runtime — minimal image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S stratinit && adduser -S stratinit -G stratinit

COPY --from=build /app.jar app.jar
RUN chown stratinit:stratinit app.jar

USER stratinit

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
