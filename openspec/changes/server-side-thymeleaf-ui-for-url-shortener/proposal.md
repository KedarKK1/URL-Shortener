## Why

A server-rendered UI is needed so users can manage shortened URLs without relying on API clients or command-line tooling. This change adds a simple, accessible web experience for creating, reviewing, updating, and deleting links while keeping the existing backend services as the single source of truth.

## What Changes

- Add a Spring MVC web UI for URL management using Thymeleaf.
- Create dashboard, create-url, and analytics pages with shared layout and reusable fragments.
- Bind forms to model objects with server-side validation and clear feedback.
- Reuse the existing URL service layer so the UI remains thin and the backend behavior stays consistent.

## Capabilities

### New Capabilities
- `server-rendered-url-management-ui`: a Thymeleaf-based web experience for creating, viewing, editing, and deleting short URLs with validation and feedback.

### Modified Capabilities
- None.

## Impact

- New controllers and Thymeleaf templates under the application resources tree.
- Existing URL service and repository layers remain the authority for persistence and business logic.
- No breaking API changes are introduced for the current backend services.
