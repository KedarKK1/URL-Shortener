# Architecture, Execution, and Testing Guide

This document explains the repository layout, runtine architecture, control flow, and test strategy for the URL Shortener project. It also covers brownfield, greenfield, and ambiguous implementation enarios.


## Folder Structure

## Architecture Overview
```mermaid 
flowchart LR
    U[User / Browser] --> T[Thymeleaf UI]
    U --> A[REST API /api]

    T --> C[UrlController]
    A --> C

    C --> S[UrlService]
    S --> R[UrlRepository]
    S --› D[(PostgreSQL)]
    S --> K[ClickEventPublisher]
    5--> X[CacheOperations]

    K --> E[(Kafka)]
    X --> M[(Redis)]

    K -. no-op fallback .-> NK[NoOpClickEventPublisher]
    X -. no-op fallback .-> NX[NoOpCacheOperations]

    C --> H[ApiExceptionHandler]
    H --> J[Structured error response]
```

## System Design And Key Decisions 

```mermaid 
flowchart TB
    A[Request enters controller] --> B{Input valid?}
    B -- no --> C[Return validation error]
    B -- yes --> D[Service layer applies business rules]
    D --> E{Alias exists or expired?}
    E -- yes --> F[Raise domain exception]
    E -- no --> G[Persist / update entity]
    G --> H{Cache enabled?}
    H -- yes --> I[Update Redis cache]
    H -- no --> J[Use no-op cache]
    G --> K{Kafka enabled?}
    K -- yes --> L[Publish click event]
    K -- no --> M[Use no-op publisher]
    F --> N[Exception handler maps to API error]
    N --> O[Stable HTTP response]
```



## Key decisions:
- Keep HTTP handling thin and push business rules into `UrlService`.
- Use repository-backed persistence with Postgres as the source of truth.
- Make Redis and Kafka optional so local development and tests remain runnable without external services.
- Keep UI and API on the same domain model and DTO boundary to reduce duplication.
- Centralize error translation in `ApiExceptionHandler` for consistent responses.

## Control Flow

```mermaid 
sequenceDiagram
    autonumber
    actor User
    participant Browser as Browser / UI
    participant Controller as UriController
    participant Service as UrlService
    participant Repo as UrlRepository
    participant Db as PostgreSQL
    participant Cache as Redis/No-op cache
    participant Kafka as Kafka/No-op publisher

    User->>Browser: Submit create/update form or API request
    Browser->>Controller: POST/api/urls or UI submission
    Controller->>Service: Validate and transform request
    Service->>Repo: Save or load URL entity
    Repo->>Db: Execute SQL / JPA operation
    Db-->>Repo: Entity row
    Repo-->>Service: Domain object
    Service->>Cache: Store or invalidate cached lookup
    Service->>afka: Publish click/usage event when applicable
    Service-->>Controller: Response DTO
    Controller-->>Browser: JSON or redirect response
```

## Sequence Diagrams By Scenario

### Brownfield Change
```mermaid 
sequenceDiagram 
    autonumber
    actor Developer
    participant Spec as Existing spec / codebase
    participant Code as Source files
    participant Tests as Automated tests 
    participant CI as CI pipeline

    Developer->>Spec: Read existing behavior and constraints
    Developer->>Code: Decompose affected packages and touchpoints
    Developer->>Code: Implement the smallest safe change
    Developer->>Tests: Run focused unit, integration, and UI tests 
    Tests-->>Developer: Pass or reveal regressions 
    Developer->>CI: Push branch and validate gates 
    CI-->>Developer: Merge-ready or failure feedback
```

### Greenfield Change
```mermaid 
sequenceDiagram
    actor Developer
    participant Requirements as New requirements 
    participant Design as Architecture / API design 
    participant Code as New modules 
    participant Tests as Test suite 
    participant CI as CI pipeline
    
    Developer->>Requirements: Gather scope and success criteria
    Developer-->>Design: Decompose into controller, service, repository, and Ul slices 
    Developer->>Code: Build model, API, persistence, and templates
    Developer->>Tests: Add unit, integration, MockMvc, and UI smoke tests 
    Tests-->>Developer: Validate behavior and boundaries Developer->>CI: Run quality gates and release checks CI-->>Developer: Accept or reject based on gates
```


### Ambiguous Change
```mermaid 
sequenceDiagram 
    autonumber
    actor Developer
    participant Request as Ambiguous request
    participant Clarify as Questions / assumptions
    participant Plan as Decomposition plan
    participant Implement as Code changes
    participant Validate as Validation Loop

    Developer->>Request: Inspect request for missing constraints
    Developer->>Clarify: Identify unknowns, risks, and dependencies Clarify-->>Developer: Provide assumptions or ask for clarification
    Developer->>Plan: Select the safest decomposition path
    Developer->>Implement: Make minimal, reversible changes
    Developer->>Validate: Run targeted tests and review outputs
    Validate-->>Developer: Confirm fit or expose gaps
```
# Testing Approach
```mermaid 
flowchart LR
    A[Unit tests] --> B[Service logic, utilities, validation helpers]
    C[MockMvc tests] --> D[Controller contracts and error responses]
    E[Integration tests] --> F[Repository and persistence wiring]
    G[UI smoke tests] --> H[Thymeleaf rendering and form presence]
    I[No-op or mocked infra] --> J[Redis and Kafka behavior without external services]
    K[CI gates] --> L[Run tests before merge]
```

## Recommended coverage layers:
- Unit tests for service logic, converters, and edge-case handling.
- MockMvc tests for request validation, response codes, and error shapes.
- Integration tests for persistence wiring and transactional behavior.
- VI smoke tests for dashboard, create, edit, and analytics templates.
- Mocks or no-op implementations for Redis and Kafka when full infrastructure is not needed.

## Limitations
- Redis and Kafka are modeled as optional dependencies, so no-op fallbacks can hide environment-specific problems until integration testing.
- UI tests validate rendering and key form fields, not pixel-perfect layout or browser-specific behavior.
- Sequence diagrams describe the intended execution path, not every exception branch or concurrent race.
- Testcontainers-based checks add runtime cost and require Docker in CI or Local development.
- The diagrams reflect the current repository shape and may need updates when package boundaries change.

## Validation Checklist

```mermaid 
flowchart TB
    A[Start validation] --> B[Run unit tests]
    B --> C[Run controller tests]
    C --> D[Run integration tests]
    D --> E[Run UI smoke tests]
    E --> F[Verify OpenAPI and docs expectations]
    F --> G[Confirm CI gates pass]
    G --> H[Review failures and iterate]
```

Validation should confirm:
- The code compiles and tests pass locally-
- API and UI behavior still matches the documented flow.
- Optional infrastructure can be disabled without breaking the core path.
- CI reproduces the same outcome with the same test matrix.


