# URL Shortner

## Getting StartedPrerequisites:
- Java 21+
- Maven

To run -
```bash
mvn clean install -DskipTests
docker compose up -d postgres redis kafka
mvn spring-boot:run
# or
mvn clean install -DskipTests
docker compose up -d postgres redis kafka    
./mvnw spring-boot:run
```

### Swagger Documentation

```bash
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

or build the jar & run
```bash
docker compose up -d postgres redis kafka    
./mvnw -DskipTests package
java -jar target/*.jar
```

for compiling & testing
```bash
./mvnw test -DskipTests=false 
./mvnw -q -DskipTests compile
```

App port - `8080`

Open thymleaf dashboard at `http://localhost:8080/` in your browser

### Tech stack used 

- Backend - Java (21) Spring Boot
- Database - PostgreSQL
- Cache - Redis
- Messaging - Kafka
- Frontend - Thymeleaf
- Testing - Junit
- Containerization - Docker
- Skills - devops, senir-software-engineer
- Agents - devops, se-architect, senir-software-engineer
- Openspec for spec-driven-development - crud-api-for-url-shortner, server-side-thymeleaf-ui-for-url-shortener, testing-spec

### Project Structure, Architecture, System Design, Scenarios & Testing Approach

- See [docs/copilot/architecture-and-testing.md](docs/copilot/architecture-and-testing.md) for Mermaid diagrams covering structure, architecture, system design, scenarios, testing, Limitations, and validation.

### Openspec commands

In OpenSpec, the typical workflow for Spec-Driven Development follows a logical lifecycle from ideation to implementation. Depending on your setup (Core profile vs. Expanded workflows), the recommended order of these actions is: Explore → New → Propose → Apply → Sync → Archive

1. Initialize
```bash
# in command terminal
openspec init
```

3. explore using an agent - : An optional initial phase used to brainstorm, refine requirements, and clarify ambiguities before committing to a specific design
```bash
# in github copilot terminal,
/opsx:explore <enter-your-change-or-specs>
```

Note: New: The command used to kick off a brand new feature. It creates the initial change folder and file structure for your planned work.

2. Propose using an agent - : The step where a change proposal is drafted. It creates the markdown specifications (the "delta specs"), design documents, and task lists needed for the AI
```bash
# in github copilot terminal
/opsx:propose <enter-your-change-or-specs>
```

4. Apply -  execution step. The AI coding assistant follows the specification and step-by-step tasks to write and implement the code
```bash
# in github copilot terminal,
/opsx:apply <enter-your-change-or-specs>
```


5. Sync delta changes/specs into main - This command is used to merge or preview your spec deltas with the main/source-of-truth specification, ensuring alignment
```bash
# in github copilot terminal,
/opsx:sync <enter-your-change-or-specs>
```

6. Archive - The final step. It records the completion of the feature, officially moves the change into the /archived folder, and permanently updates the definitive source-of-truth documentation
```bash
# in github copilot terminal,
/opsx:archive <enter-your-change-or-specs>
```
openspec validate 
openspec archive
openspec list 
openspec list --specs
openspec list --changes
openspec status --change <name-of-change-spec>
openspec view
openspec update

## Project Features
- Create, read, update, delete short URLs
- Optional custon alias and expiration time
- Redirect resolution with click counting
- Redis-backed caching (optional, auto-falls back to noop)
- Kafka click-event publishing (optional, auto-falls back to noop)
- PostgreSQL persistence (configured via application YAML)
- Thymeleaf server-rendered UI for dashboard and forms
- Comprehensive unit/integration tests under `src/test`
to validate behavior

## Configuration
Edit `src/main/resources/application.yaml` to provide profile-specific files
`application-docker.yaml` to configure database, Redis, and Kafka settings.

## Running Tests

``bash
./mvnw test
``

## Notes
- If Redis or Kafka are not available, the application uses no-op implementations so core functionality still works.
- See the openspec/changes/
folder for change proposals, tasks, and specs used during
development.
