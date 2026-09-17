# Core Banking Simulation

A backend-focused banking simulation developed using Spring Boot, JDBC, and MySQL.

## Features

- Account balance management
- Deposit and withdrawal operations
- Account-to-account money transfer
- Transaction history
- Transfer reference generation
- Audit logging
- Database transaction management
- Input validation
- Row-level locking using `SELECT ... FOR UPDATE`
- REST APIs tested with Postman

## Tech Stack

- Java
- Spring Boot
- Spring JDBC
- MySQL
- REST API
- Maven
- Postman

## Architecture

The application follows a layered backend structure:

Controller ? Service ? Repository ? MySQL

- Controllers expose REST APIs.
- Services contain business logic and transaction management.
- Repositories handle database operations using JdbcTemplate.
- MySQL stores accounts, transactions, transfers and audit records.

## Key API Endpoints

### Transfer

POST `/api/transfers`

```json
{
  "senderAccountId": 1,
  "receiverAccountId": 2,
  "amount": 1000
}

