# Run Application With Realistic Test Data

Use this prompt when the user wants to demonstrate that the Expense Splitter application works with realistic real-life data.

## Task

Run the application locally and fill it with realistic testing data that demonstrates the main user flows:

* creating groups;
* opening a group details page;
* adding members;
* adding expenses;
* viewing balances;
* viewing settlement suggestions.

## Required Steps

1. Read `AGENTS.md`.
2. Read `.github/instructions/general-instructions.md`.
3. Read `.github/instructions/balance-settlement-rules.md`.
4. Start the Spring Boot application.
5. Use realistic test data, not placeholder data.
6. Create at least three groups, for example:
   * a weekend trip;
   * a team dinner;
   * a shared apartment or household costs group.
7. Add several members to each group.
8. Add multiple realistic expenses to each group.
9. Verify that:
   * groups appear on the home page;
   * each group opens on its details page;
   * members are visible;
   * expenses are visible;
   * balances match the equal-split business rules;
   * settlement suggestions are shown correctly;
   * amounts are displayed in Euro;
   * there are no JavaScript console errors.
10. Open the application in Google Chrome for visual verification.

## Validation

For each seeded group, report:

* group name;
* member count;
* expense count;
* total expenses;
* equal share;
* member balances;
* settlement suggestions.

If possible, verify the API responses and the rendered frontend.

## Output Summary

After finishing, summarize:

* the application URL;
* what realistic data was created;
* what balances and settlements were verified;
* whether the frontend was checked in Chrome;
* whether there were JavaScript console errors;
* any assumptions or limitations.
