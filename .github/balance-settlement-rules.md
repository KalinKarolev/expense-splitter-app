# Balance and Settlement Rules

## Core Rule

All expenses are split equally between all members of the selected group.

The application does not support custom percentages, weights, currencies, or partial participation in this version.

## Definitions

For a selected group:

* `totalExpenses` is the sum of all expense amounts in the group.
* `memberCount` is the number of members in the group.
* `equalShare` is `totalExpenses / memberCount`.
* `paidAmount` is the total amount paid by a specific member.
* `balance` is calculated as:

`balance = paidAmount - equalShare`

## Meaning of Balance

A positive balance means the member paid more than their share and should receive money.

A negative balance means the member paid less than their share and owes money.

A zero balance means the member is already settled.

Examples:

If total expenses are 180 and there are 3 members, each member's equal share is 60.

* Maria paid 90: `90 - 60 = +30`, so Maria should receive 30.
* Ivan paid 30: `30 - 60 = -30`, so Ivan owes 30.
* Petar paid 60: `60 - 60 = 0`, so Petar is settled.

The correct settlement is:

`Ivan pays Maria 30`

## Settlement Suggestions

Settlement suggestions should transfer money from members with negative balances to members with positive balances.

Use a simple greedy algorithm:

1. Create a list of debtors from members with negative balances.
2. Create a list of creditors from members with positive balances.
3. Match debtors to creditors.
4. The payment amount is the smaller value between:
   * how much the debtor still owes;
   * how much the creditor should still receive.
5. Continue until all balances are settled.

## Expected DTO Data

For each member balance, include:
* member id
* member name
* paid amount
* equal share
* balance

For each settlement suggestion, include:
* payer/debtor member id
* payer/debtor member name
* receiver/creditor member id
* receiver/creditor member name
* amount

## Edge Cases

If a group has no members, return an empty balance and settlement result.

If a group has members but no expenses:
* total expenses should be zero;
* each member's paid amount should be zero;
* each member's equal share should be zero;
* each member's balance should be zero;
* settlement suggestions should be empty.

If everyone is already settled, settlement suggestions should be empty.

## UI Wording

In the frontend, avoid showing only `+` or `-` amounts without explanation.

Prefer clear labels such as:

* "Maria should receive 30.00"
* "Ivan owes 30.00"
* "Petar is settled"

This makes the meaning of the balance easier to understand.
