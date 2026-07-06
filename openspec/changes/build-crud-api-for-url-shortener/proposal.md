## Why

The current project is a minimal Spring Boot starter and needs a production-ready URL shortener backend with CRUD operations, redirect behavior, persistence, caching, click-event analytics, expiration, and custom aliases. This change establishes the core product requirements and implementation scope so the app can evolve from a scaffold into a usable service.

## What Changes

- Add a URL-shortening CRUD API for creating, reading, updating, and deleting short links.
- Support redirecting short aliases to their target URLs.
- Persist URLs and metadata in a relational database.
- Add Redis-backed caching for hot lookups and redirect acceleration.
- Publish click-event data to Kafka for downstream analytics.
- Support link expiration and custom aliases.
- Provide an architecture-ready foundation for future analytics and observability enhancements.

## Capabilities

### New Capabilities
- `url-management`: Core CRUD lifecycle for short URLs, including validation, persistence, and metadata.
- `redirect-resolution`: Short-link resolution and redirect behavior with alias handling.
- `analytics-events`: Click event capture and Kafka emission for analytics workflows.
- `expiration-and-aliasing`: Expiry rules and custom alias assignment for shortened links.

### Modified Capabilities
- None.

## Impact

- New Spring Boot REST endpoints for URL management and redirect flows.
- New persistence model and repository layer backed by PostgreSQL.
- Redis integration for cache reads and write-through behavior.
- Kafka producer integration for click-event streaming.
- Configuration updates in application YAML and profile-specific files for local and container execution.
