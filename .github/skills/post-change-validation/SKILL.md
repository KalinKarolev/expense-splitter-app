name: Post-Change Validation Skill
description: Use this skill after an AI agent has made changes to the Expense Splitter project. The goal is to verify that the change works correctly, does not break existing functionality, follows the project rules, and is ready to be reviewed or committed.

## When to Use This Skill

Use this skill after:
* adding a new module;
* changing backend logic;
* changing frontend behavior;
* fixing a bug;
* refactoring code;
* updating business rules;
* updating DTOs, endpoints, or UI behavior.

Do not use this skill to add new functionality. Use it only to validate changes that were already made.

## Validation Workflow

Before testing:
1. Read `AGENTS.md`.
2. Read `.github/instructions/naming-conventions.md` if Java classes, methods, DTOs, or endpoints were changed.
3. Read `.github/instructions/balance-settlement-rules.md` if balance or settlement logic was changed.
4. Inspect the changed files.
5. Summarize what was changed before running validation.

## Backend Validation

Check that:
* the application starts successfully;
* the changed REST endpoints work as expected;
* request and response DTOs are used instead of exposing JPA entities;
* validation annotations are still applied where needed;
* HTTP status codes are reasonable;
* unrelated backend behavior was not changed.

If applicable, test endpoints manually using browser, curl, IntelliJ HTTP client, or another simple tool.

## Frontend Validation

Check that:
* the page loads correctly;
* the changed UI flow works in the browser;
* forms submit correctly;
* lists refresh after create/delete actions;
* no JavaScript console errors appear;
* the UI remains consistent with the existing design;
* the app still uses only static HTML, CSS, and vanilla JavaScript.

## Business Rule Validation

If the change affects expenses, balances, or settlements:
1. Read `.github/instructions/balance-settlement-rules.md`.
2. Create realistic test data.
3. Verify that paid amounts, equal shares, balances, and settlement suggestions match the documented rules.
4. Do not change the business rules unless explicitly requested.

## Regression Check

Verify that existing important flows still work:
1. Create a group.
2. Open the group details page.
3. Add members.
4. Add expenses.
5. View balances.
6. View settlement suggestions.

For smaller changes, test only the relevant part plus any directly affected existing functionality.

## Automated Tests

Run or suggest the relevant Maven command, usually:

`mvn test`

If a test fails:
* investigate the production behavior first;
* do not weaken assertions only to make the test pass;
* explain the cause before changing code.
