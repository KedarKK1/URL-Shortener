---
name: devops-skills
description: Containerize Spring Boot projects by inspecting Maven dependencies, creating Dockerfiles and docker-compose configuration, running containers, and wiring profile-based application settings for local or container execution.
license: MIT
compatibility: Works with Spring Boot, Maven, Docker, and Docker Compose.
metadata:
  author: copilot
  version: "1.0"
---

Use this skill when the user wants to make a Spring Boot application runnable in Docker, add or refine container configuration, or connect the app to supporting services such as databases or caches.

## What this skill produces

A working containerized setup for the Spring Boot project that typically includes:
- a Dockerfile for building the application image
- a docker-compose.yaml file for app and supporting services
- environment-aware Spring configuration in application.yaml and profile-specific YAML files
- verified container startup and basic runtime validation

## Workflow

1. Review the project structure and runtime needs
   - Read pom.xml, src/main/resources/application.yaml, and any existing Docker or profile configuration.
   - Identify the Java version, packaging type, and required external services.
   - Check whether new dependencies are required for the containerized setup before changing the build.

2. Create or update the Dockerfile
   - Prefer a multi-stage build for Spring Boot jars.
   - Use a Java base image that matches the project’s target version.
   - Copy dependency files first when possible to improve build caching.
   - Expose the application port and use a JVM-friendly startup command.

3. Create or update docker-compose.yaml
   - Add the application service and any supporting services such as PostgreSQL, MySQL, Redis, or Kafka.
   - Configure ports, environment variables, volumes, restart policies, and dependency order.
   - Keep the setup simple unless the user specifically needs a richer stack.

4. Add Spring configuration for container environments
   - Use application.yaml for shared defaults.
   - Prefer profile-specific files such as application-docker.yaml or application-dev.yaml for container-specific values.
   - Drive database, server, and service URLs from environment variables when possible.

5. Build and run the stack
   - Build the jar with mvn clean package or mvn clean install -DskipTests when relevant.
   - Start services with docker compose up --build.
   - Review logs, verify the app starts successfully, and iterate on configuration as needed.

6. Validate and document the result
   - Confirm that required endpoints and service connections work.
   - Capture the commands and environment variables used so the setup is repeatable.
   - If needed, suggest follow-up improvements for production readiness.

## Decision points

- If the project has no external dependencies, start with a single-service Docker setup.
- If the app needs a database or messaging broker, add a matching container and wire it through Spring profiles.
- If the project already uses Spring profiles, prefer profile-specific YAML files over hard-coding container values in the main config.
- If the build is slow, optimize Docker caching by ordering dependency installation before copying the full source tree.

## Quality criteria

- The Spring Boot application starts successfully inside Docker.
- Required service dependencies are reachable and correctly configured.
- Configuration is environment-driven and easy to adjust.
- The Docker and Compose setup is reproducible and understandable.

## Example prompts

- "Containerize this Spring Boot app and add Docker support."
- "Analyze the Maven dependencies and update the Docker setup for this project."
- "Add docker-compose and profile-based configuration to run the app with PostgreSQL."
- "Help me run this Spring Boot service in Docker and wire its application.yaml for container mode."
