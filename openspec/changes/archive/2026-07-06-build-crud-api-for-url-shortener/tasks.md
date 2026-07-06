## 1. Domain and Persistence

- [x] 1.1 Define the URL entity, status fields, metadata fields, and validation rules.
- [x] 1.2 Create repository and persistence layer support for CRUD operations.
- [x] 1.3 Add database migration or schema initialization support for the URL table.

## 2. API and Service Layer

- [x] 2.1 Implement URL creation, update, retrieval, deletion, and redirect service logic.
- [x] 2.2 Add REST controllers for CRUD endpoints and redirect resolution.
- [x] 2.3 Add request validation and error handling for invalid aliases, missing URLs, and expired links.

## 3. Caching and Integration

- [x] 3.1 Add Redis cache integration for read-heavy and redirect operations.
- [x] 3.2 Add Kafka click-event publishing for redirect activity.
- [x] 3.3 Wire configuration for PostgreSQL, Redis, and Kafka in application YAML and profile files.

## 4. Quality and Verification

- [x] 4.1 Add or update tests for URL CRUD and redirect behavior.
- [x] 4.2 Verify the application starts locally and through Docker with the new configuration.
