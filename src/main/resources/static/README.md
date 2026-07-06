# OpenAPI Documentation

This folder contains static OpenAPI artifacts for the URL
Shortener API.

- Primary spec: `url-shortener-openapi.yaml`
- Versioning: API spec version is tracked via `info.version`
- Authentication: bearer JWT documented under `components.securityschemes.bearerAuth`
- Error codes: includes 400, 404, 409, 410 with `ApiError` and examples

Use this spec for client generation, contract checks, and API
review.