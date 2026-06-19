# Expense Splitter

Expense Splitter is a small full-stack web application for managing shared group expenses.

Users can create expense groups, add members, register expenses, view each member's balance, and see settlement suggestions showing who should pay whom.

## Tech Stack

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* H2 Database
* Bean Validation
* Maven
* JUnit 5
* Static HTML, CSS, and JavaScript

## Features

* Create and view groups
* Open a dedicated group details page
* Edit group description
* Add and remove group members
* Add expenses with a payer selected from group members
* View group balances
* View settlement suggestions
* Display amounts in Euro

## Notes

The application uses an H2 in-memory database for local development, so data is reset when the application restarts.

All expenses are split equally between all members of the selected group using simple greedy algorithm for the settlements.
