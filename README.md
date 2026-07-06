# URL Shortner

## Getting StartedPrerequisites:
- Java 17+
- Maven

To run -
```bash
docker compose up -d postgres redis kafka    
mvn spring-boot:run
// or
docker compose up -d postgres redis kafka    
./mvnw spring-boot:run
```

or build the jar & run
```bash
docker compose up -d postgres redis kafka    
./mvnw -DskipTests package
java -jar target/*.jar
```
App port - 8080

Open thymleaf dashboard at http://localhost:8080/ in your browser

### Tech stack used 


Backend - Java Spring Boot
Database - PostgreSQL
Cache - Redis
Messaging - Kafka
Frontend - Thymeleaf
Skills - deops, senir-software-engineer
Agents - devops, se-architect, senir-software-engineer
Openspec - crud-api-for-url-shortner, server-side-thymeleaf-ui-for-url-shortener

