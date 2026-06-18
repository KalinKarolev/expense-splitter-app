# Expense Splitter - AI Agent Instructions

## Project Overview

The application is called Expense Splitter. It is a small full-stack web application for managing shared expenses inside a group.

The main use case is the following: several people participate in a trip, team dinner, shared accommodation, or event, and different people pay for different expenses. 
The application stores the group members and expenses, calculates each member's balance, and generates settlement suggestions showing who should pay whom.

## Technology Stack

Use:
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Bean Validation
- Maven
- JUnit 5
- Static HTML/CSS/JavaScript frontend

Do not use:
- Thymeleaf
- React
- authentication or login
- Docker
- PostgreSQL
- currency conversion
- complex split percentages
- email notifications
- PDF export

## Architecture Rules

The base package is:

`com.softuni.expensesplitter`

Backend REST endpoints must be under:

`/api`

Frontend files must be placed under:

`src/main/resources/static`

Use this frontend structure:

`src/main/resources/static/index.html`  
`src/main/resources/static/css/styles.css`  
`src/main/resources/static/js/app.js`

Do not place frontend files under `src/main/resources/templates`.

Do not use server-side templates.

Use JavaScript `fetch()` calls from the static frontend to communicate with the backend API.

## Development Style

Keep the project simple and suitable for a small exam project.

Use a clean layered structure:
- entities for persistence
- repositories for database access
- services for business logic
- controllers for REST endpoints
- DTOs for request and response objects

Use packages such as:
- `entity`
- `repository`
- `service`
- `controller`
- `dto`

Use `BigDecimal` for money values.

Use validation annotations where appropriate, for example `@NotBlank`, `@NotNull`, and `@Positive`.

Validate request DTOs in controller methods with `@Valid`.

Use JSON request and response bodies.

Return appropriate HTTP status codes.

Do not expose JPA entities directly from REST controllers. Use DTOs for API input and output.

The balance and settlement calculation logic should be isolated in a dedicated service so it can be tested independently.

Add unit tests for balance and settlement calculations.

Prefer focused service tests over broad end-to-end tests unless necessary.

Use H2 for local development.

Do not add database migration tools unless explicitly requested.

## Additional Instruction Files

Always read and follow:

`.github/general-instructions.md`

Before creating or renaming Java classes, DTOs, services, repositories, controllers, methods, or REST endpoints, read:

`docs/naming-conventions.md`

Follow the naming rules from that file unless I explicitly request otherwise.

## Before Making Code Changes

Before making code changes:
- briefly explain the planned changes;
- keep the implementation minimal;
- avoid unnecessary complexity;
- do not introduce technologies that are not listed in this file.

## After Making Code Changes

After making code changes:
- summarize which files were created or modified;
- explain how to run or manually test the change;
- mention any assumptions or limitations;
- run or suggest the relevant Maven test command when applicable.
