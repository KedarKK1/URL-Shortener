#* Testing Capability: Automated Test Requirements

This spec defines the automated testing requirements for new and modified features.

Requirements
------------
1. Every feature MUST include unit tests for business logic.
2. Features touching persistence MUST include at least one integration test using Testcontainers or an equivalent.
3. APIS MUST include MockMvc tests covering happy path and validation errors.
4. UI changes MUST include Thymeleaf rendering checks.
5. External dependencies (Redis, Kafka) MUST have mock-based tests when full integration is not feasible.
6. CI MUST run tests and quality gates before merge.