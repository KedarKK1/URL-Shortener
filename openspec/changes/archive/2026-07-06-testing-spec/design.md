## Design: Testing strategy and implementation notes

Overview
--------
Use JUnit 5+ Spring Boot Test for unit and integration tests. Prefer Testcontainers for CI integration tests that require PostgresQL/Redis/Kafka. Use Mockito for unit-level mocking and Spring's test support (`@MockBean`) to replace Redis/Kafka in tests when appropriate.

Controller tests
----------------
Use `MockMvc` to test API endpoints, validation, and response shapes. Include tests for happy path and validation error responses.

Integration tests
-----------------
Use Testcontainers (Postgres) to run repository and service integration tests. Optionally, use embedded kafka or Testcontainers Kafka for publisher tests, but provide Mockito-based fallbacks for faster local runs.

Mocking external systems
------------------------
- Redis: use @MockBean for `StringRedistemplate` or provide a `NoOpCacheoperations` bean in test config.
- Kafka: use @MockBean for `Kafkatemplate` or `NoopClickEventPublisher`.

Thymeleaf tests
----------------
Render templates with `SpringBootTest` + `MockMVC` and assert key content and form fields are present.

CI
--
Run tests in stages: unit - integration - UI. Use Testcontainers when running integration stage in CI.