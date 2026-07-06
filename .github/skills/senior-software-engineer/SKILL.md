---
name: senior-software-engineer
description: Design and implement production-grade backend features in Spring Boot applications with PostgreSQL persistence, Redis caching, Kafka eventing, validation, transaction handling, and resilient error handling.
license: MIT
compatibility: Works with Spring Boot, PostgreSQL, Redis, and Kafka-based systems.
metadata:
  author: copilot
  version: "1.0"
---

Use this skill when the user wants to design, implement, or review a backend feature that requires strong engineering discipline across domain logic, persistence, caching, messaging, and API design.

## What this skill covers

This skill helps with:
- Spring Boot controller, service, repository, and transaction design
- PostgreSQL schema design, indexes, foreign keys, UUID usage, and migration tooling
- Redis cache strategy, TTL, eviction, and serialization choices
- Kafka producer/consumer design, topics, events, retries, idempotency, and transactional patterns
- validation, exception handling, and maintainable service-layer architecture

## Core workflow

1. Understand the feature and constraints
   - Clarify the business intent, API contract, data model, and expected scale.
   - Identify whether persistence, cache, messaging, or async processing is required.

2. Design the backend shape
   - Define the domain model and service boundaries.
   - Choose whether REST controllers, services, repositories, and DTOs are needed.
   - Decide which persistence and integration concerns are required for the feature.

3. Implement the foundation
   - Create or update controller endpoints with clear request/response contracts.
   - Put business logic in services and keep controllers thin.
   - Use repositories for data access and transactions where needed.
   - Add validation and exception handling for predictable behavior.

4. Add persistence and data reliability
   - Use PostgreSQL for durable state.
   - Add indexes, foreign keys, and UUIDs where appropriate.
   - Prefer Flyway or Liquibase for schema evolution.
   - Optimize queries and avoid unnecessary joins or N+1 patterns.

5. Add caching when it improves performance
   - Apply cache-aside patterns for read-heavy operations.
   - Choose sensible TTLs and eviction behavior.
   - Keep serialization compatible and predictable.

6. Add event-driven behavior when appropriate
   - Use Kafka topics and events for asynchronous workflows.
   - Design producers and consumers with retry, idempotency, and partitioning in mind.
   - Keep event contracts explicit and versionable.

7. Verify and harden the implementation
   - Run tests, build checks, and integration validation.
   - Review transaction boundaries, exception behavior, and failure modes.
   - Make sure the solution is maintainable and observable.

## Decision points

- If a feature is mostly transactional and stateful, prioritize Spring services, repositories, and PostgreSQL.
- If read performance matters, introduce Redis with a cache-aside strategy.
- If workflows should be decoupled and asynchronous, use Kafka events and consumers.
- If data changes must be atomic across systems, consider transactional boundaries and careful failure handling.

## Quality criteria

- The feature is implemented with a clear layered architecture.
- Validation and error handling are explicit and user-friendly.
- Persistence is efficient and schema-safe.
- Caching and messaging choices are justified and maintainable.
- The implementation is verifiable through build or test execution.

## Example prompts

- “Design and implement a new Spring Boot CRUD feature with validation and PostgreSQL persistence.”
- “Add Redis caching to this service with a cache-aside strategy and TTL.”
- “Create Kafka producer and consumer flow for click events with retries and idempotency.”
- “Review this backend feature for transaction boundaries, exception handling, and database performance.”
