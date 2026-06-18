# Naming Conventions

## General Rules

- Use clear, explicit, and consistent names.
- Use PascalCase for Java classes.
- Use camelCase for fields, variables, and methods.
- Prefer names that describe the business concept and the class responsibility.

## Entities

Entities should be singular nouns. Use:
- `ExpenseGroup`
- `Member`
- `Expense`

Do not use plural entity names such as:
- `ExpenseGroups`
- `Members`
- `Expenses`

## Repositories

Repositories should use the entity name followed by `Repository`, like `ExpenseGroupRepository` or `MemberRepository`.

## Services

Services should use the entity name or business area followed by `Service`, like `ExpenseGroupService` or `MemberService`.

Avoid generic service names such as:
- `GroupManager`
- `ExpenseHandler`
- `CalculationProcessor`

## Controllers

Controllers should use the entity name or API area followed by `Controller`. Use:
- `ExpenseGroupController`
- `MemberController`
- `ExpenseController`
- `BalanceController`

Prefer `ExpenseGroupController` over `GroupController`, because the main entity is named `ExpenseGroup`.

## DTOs

DTOs should describe their exact purpose.

For create requests, use:
- `CreateExpenseGroupRequest`
- `CreateMemberRequest`
- `CreateExpenseRequest`

For responses, use:
- `ExpenseGroupResponse`
- `MemberResponse`
- `ExpenseResponse`
- `MemberBalanceResponse`
- `SettlementResponse`

## Methods

Use action-based method names that describe what the method does. Examples:
- `createGroup`
- `getAllGroups`
- `getGroupById`
- `addMemberToGroup`
- `addExpenseToGroup`
- `calculateBalances`
- `calculateSettlements`

Avoid unclear method names such as:
- `process`
- `handle`
- `execute`
- `doCalculation`

## REST Endpoint Naming

Use plural nouns in REST paths.

Use:
- `GET /api/groups`
- `POST /api/groups`
- `GET /api/groups/{id}`
- `POST /api/groups/{id}/members`
- `POST /api/groups/{id}/expenses`
- `GET /api/groups/{id}/balances`
- `GET /api/groups/{id}/settlements`

Do not use verbs in endpoint paths such as:
- `/api/createGroup`
- `/api/addExpense`
- `/api/calculateBalances`
