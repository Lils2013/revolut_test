# MoneyTransfer
A RESTful API for money transfers between accounts.

## Requirements
* Java 8
* Maven 3+

## Usage
Build application and run integration tests with:
```
    mvn clean package
```
Then execute the application with:
```
    java -jar target/revolut_test-1.0-SNAPSHOT-fat.jar
```
The application runs on port 8080

## Endpoints
### Accounts
#### Create an account
Request:
```
    POST localhost:8080/accounts
    {
        "username": "Morrissey",
        "balance": 120.20
    }
```
Response:
```
    HTTP 201 Created
    {
        "id": 2,
        "username": "Morrissey",
        "balance": 120.2
    }
```
#### Get all accounts
Request:
```
    GET localhost:8080/accounts
```
Response:
```
    HTTP 200 OK
    [
        {
            "id": 0,
            "username": "John",
            "balance": 100
        },
        {
            "id": 1,
            "username": "Susan",
            "balance": 200
        }
    ]
```
#### Get one account
Request:
```
    GET localhost:8080/accounts/0
```
Response:
```
    HTTP 200 OK
    {
        "id": 1,
        "username": "Susan",
        "balance": 200
    }
```
### Transfers
#### Create a transfer
Request:
```
    POST localhost:8080/transfers
    {
        "sourceAccountId": 1,
        "destinationAccountId": 0,
        "amount": 10.00
    }
```
Response:
```
    HTTP 201 Created
    {
        "id": 0,
        "sourceAccountId": 1,
        "destinationAccountId": 0,
        "amount": 10,
        "result": "SUCCESSFUL"
    }
```
Necessary conditions for successful transfer:
* Both accounts exist
* The accounts are different
* The amount is positive
* The balance on the source account is big enough
#### Get all transfers
Request:
```
    GET localhost:8080/transfers
    {
        "sourceAccountId": 1,
        "destinationAccountId": 0,
        "amount": 10.00
    }
```
Response:
```
    HTTP 200 OK
    [
        {
            "id": 0,
            "sourceAccountId": 1,
            "destinationAccountId": 0,
            "amount": 10,
            "result": "SUCCESSFUL"
        }
    ]
```
## Architecture
The application consists of MainVerticle, which runs the server, and DAOs, that hide the implementation of the datastore (in-memory maps).

