## Context

The existing project is a minimal Spring Boot application with PostgreSQL, Redis, and Kafka dependencies already present. The new change will turn it into a URL shortener service with a CRUD API, redirect handling, persistence, cache integration, analytics streaming, expiration support, and custom aliases.

## Goals / Non-Goals

**Goals:**
- Define a practical architecture for URL creation, lookup, update, delete, and redirect flows.
- Choose clear persistence, cache, and event-streaming strategies that fit the current stack.
- Provide implementation guidance for the next engineering step.

**Non-Goals:**
- Building a full multi-tenant enterprise platform.
- Introducing complex queues, dead-letter handling, or distributed tracing beyond the initial use case.
- Creating a frontend UI for link management.

## Decisions

- Use Spring MVC controllers with service-layer orchestration for the API surface.
  - Rationale: This keeps the application simple and aligns with the existing Spring Boot starter setup.
  - Alternative considered: A reactive stack; rejected because it would add significant complexity for the initial CRUD and redirect use case.

- Persist links in PostgreSQL using JPA entities and repositories.
  - Rationale: A relational model fits the structured metadata needed for aliases, expiration, visibility, and audit fields.
  - Alternative considered: NoSQL storage; rejected because relational constraints and queryability are simpler for this domain.

- Use Redis as a cache for hot redirect and lookup operations.
  - Rationale: Redis reduces load on PostgreSQL and improves redirect latency for frequently accessed aliases.
  - Alternative considered: Caching in-memory only; rejected because it is less portable and less useful across multiple app instances.

- Publish click events to Kafka when redirects occur.
  - Rationale: Kafka supports asynchronous analytics ingestion without blocking the redirect response.
  - Alternative considered: Synchronous analytics writes to the database; rejected because it introduces latency and couples the redirect flow to analytics.

- Support custom aliases and expiration through explicit fields on the URL entity.
  - Rationale: This keeps the rules simple and manageable without introducing a separate domain model.
  - Alternative considered: Separate tables for alias rules and expiration policies; rejected as over-designed for the initial scope.

- Use profile-based configuration for local and container execution.
  - Rationale: The app can run locally and in Docker with the same code while using environment-driven settings for database and service hosts.
  - Alternative considered: Hard-coded environment values; rejected because it harms portability.

## Risks / Trade-offs

- [Cache staleness] → Use short TTLs and invalidate cache entries on mutation events.
- [Kafka delivery failures] → Use best-effort publishing first and log failures for later recovery.
- [Alias collisions] → Enforce uniqueness at the database level and return a conflict response when a custom alias is already taken.
- [Expired links] → Reject access after expiry and optionally soft-delete or mark inactive.

## Migration Plan

1. Introduce the URL entity, repository, and service layer.
2. Add REST endpoints for CRUD management and redirect resolution.
3. Add Redis caching and Kafka click-event publishing behind the service layer.
4. Validate behavior in local and Docker environments with PostgreSQL, Redis, and Kafka dependencies.

## Open Questions

- Whether analytics consumers will require a dedicated event payload schema beyond the initial click metadata.
- Whether redirect responses should include a 301 or 302 status code by default.
- Whether expiration should be enforced at read time, write time, or via scheduled cleanup.
