## Testing Spec: Expand test coverage and CI validation

### What & Why

This change adds a comprehensive testing specification and artifacts to ensure every feature in the repository is covered by a test matrix consisting of unit tests, integration tests, MockM controller tests, Thymeleaf UI checks, validation and error-case tests, and mocks for external systems (Redis, Kafka). The goal is to raise confidence for changes, enable reliable CI gates, and provide a repeatable pattern for test generation.

### Goals

- Add unit tests for service, repository, and utility logic
- Add integration tests that exercise Spring wiring and persistence (use Testcontainers in CI)
- Add Mockvc tests for API endpoints and validation errors
- Add Thymeleaf VI smoke tests for dashboard and forms
- Mock Redis and Kafka where full services aren't required
- Cover error cases and input validation thoroughly
- Wire test execution into CI and quality gates