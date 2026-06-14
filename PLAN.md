# PLAN.md

## Repository Role

Space Service.

## Repository Type

backend

## Tech Stack

- Java 21
- Spring Boot 3.4.5
- Gradle
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Actuator
- JWT via `io.jsonwebtoken`
- PostgreSQL
- Lombok
- H2 for tests

## Main Directories

- `src/main/java/com/example/space/controller`: API controllers.
- `src/main/java/com/example/space/service`: application services.
- `src/main/java/com/example/space/repository`: persistence repositories.
- `src/main/java/com/example/space/entity`: domain entities.
- `src/main/java/com/example/space/dto/request`: request DTOs.
- `src/main/java/com/example/space/dto/response`: response DTOs.
- `src/main/java/com/example/space/global`: shared config, security, response, and utility code.
- `src/main/resources`: application configuration.
- `src/test/java/com/example/space`: tests.
- `docs`: service documentation.
- `docs/space`: space-domain documentation.

## Domain Responsibilities

- Space creation.
- Space listing.
- Space detail query.
- Space update.
- Space deletion.
- Owner space listing.
- Space schedule creation.
- Space schedule listing.
- Space schedule update.
- Space schedule deletion.

## API Source of Truth

Backend API specifications are managed in Apidog.

The local repository-side API spec is `API_SPEC.yaml`.

The SprintOps Agent compares Apidog API specs with this repository's implementation state.

## Completion Rule

A backend feature is considered completed when:

- the API exists in Apidog or OpenAPI spec
- matching backend implementation exists
- related PR is merged into the main branch

If there is an open PR but no merge, mark it as in progress.

If the API exists but no implementation evidence exists, mark it as missing.

If implementation exists but no API spec exists, mark it as spec mismatch.

## SprintOps Agent Checkpoints

The SprintOps Agent should inspect:

- `API_SPEC.yaml`
- `src/main/java/com/example/space/controller`
- `src/main/java/com/example/space/service`
- `src/main/java/com/example/space/dto`
- `src/main/java/com/example/space/repository`
- `src/main/java/com/example/space/entity`
- `src/main/java/com/example/space/global`
- `src/test/java/com/example/space`
- `docs`
- `docs/space`
- merged PRs
- open PRs
- issues
- branches
- this `PLAN.md`

## Notes

- Space routes are under `/spaces`.
- Schedule routes are nested under `/spaces/{space-id}/schedule`.
