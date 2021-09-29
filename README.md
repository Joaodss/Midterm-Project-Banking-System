# IH-Midterm-Project-Banking-System

Ironhack Bootcamp Midterm Project: Banking System  
by: [Joaodss](https://github.com/Joaodss)

## Goals

The main purpose of this project is to create a functional banking system java application. Has a sum up of the
requirements, the project must:

- Have a variety of account types: Checking Account, Student Checking Account, Savings Account, and Credit Card;
- Be accessible for 3 types of users: Admin, Account Holder, and Third Parties;
- Be protected for each type of user;
- Allow the users to manage their accounts and create transactions between accounts, for account holders and third
  parties;
- Manage automatically some specific account functions:
    - Verify fraudulent transactions;
    - Add interest to savings accounts and credit cards;
    - Withdraw maintenance and penalty fees.

## The Project

### Functionality

The program works correctly and it implements its goal.

It allows for the creation of account holders, admins, and third parties, and the creation of multiple accounts:
checking, student checking, savings, credit card. The transactions are saved in a transaction model and in a receipt
model to be easier to read.  
For account management, it automatically adds interest on the correct dates (1 per month for credit card, and 1 per year
for savings account), it withdraws penalty fees when the account is below the minimum amount, and it withdraws
maintenance fees every month for checking accounts. Additionally, it checks each transaction for unusual behavior, such
as a fast sequence of transactions or suspicious large transactions, and it will freeze the account for security
reasons.

### Set up

Before running the program a database and a user must be created. The database in use is from MySQL. To create the
database and user, it is possible to modify the application.properties files, or use the following queries:

#### Main program

```
CREATE DATABASE IF NOT EXISTS BankingSystem;
CREATE USER IF NOT EXISTS 'user'@'localhost' IDENTIFIED BY 'User-123';
GRANT ALL PRIVILEGES ON BankingSystem.* TO 'user'@'localhost';
FLUSH PRIVILEGES;
```

#### Dev and Testing program

```
CREATE DATABASE IF NOT EXISTS BankingSystem_dev;
CREATE DATABASE IF NOT EXISTS BankingSystem_test;
CREATE USER IF NOT EXISTS 'dev'@'localhost' IDENTIFIED BY 'Development-123';
GRANT ALL PRIVILEGES ON BankingSystem_dev.* TO 'dev'@'localhost';
GRANT ALL PRIVILEGES ON BankingSystem_test.* TO 'dev'@'localhost';
FLUSH PRIVILEGES;
```

### Run

The application can be run with two different profiles: mysqlFinal or mysqlDev.

The default profile is mysqlFinal and initializes the database only on the first run. The following runs it will only
read from the database and update it. The development profile resets the database every time the application starts. All
the data will be deleted o the start.

Both profiles initialize with an admin entity: {username=admin, password=admin, name=Admin}, to allow the use of all
endpoints.

The application can be started from an IDE such as Intellij, or from the command line, by running:

- mysqlFinal profile: `mvn spring-boot:run`
- mysqlDev profile: `mvn spring-boot:run -Dspring-boot.run.profiles=mysqlDev`

## Endpoints

It is important to note that all endpoints available to the user are dependent on its own profile. In sum, a user will
only be able to access its own information and its accounts and transactions.

### Users

| Request |                 Route                 | Permissions |                                 Description                                  |
|---------|---------------------------------------|:-----------:|------------------------------------------------------------------------------|
| GET     | /api/users                            | ADMIN       | Returns list of all users. (admins, account holders, and third parties)      |
| GET     | /api/users/admins                     | ADMIN       | Returns list of all admins.                                                  |
| GET     | /api/users/account_holders            | ADMIN       | Returns list of all account holders.                                         |
| GET     | /api/users/third_parties              | ADMIN       | Returns list of all third parties.                                           |
| GET     | /api/users/id/{id}                    | ADMIN       | Return a single user by id.                                                  |
| GET     | /api/users/{username}                 | ADMIN, USER | Return a single user by username.                                            |
| POST    | /api/users/new_admin                  | ADMIN       | Requires a payload(1), creates and saves an admin, and returns 201.          |
| POST    | /api/users/new                        | PUBLIC      | Requires a payload(2), creates and saves an account holder, and returns 201. |
| POST    | /api/users/new_third_party            | ADMIN       | Requires a payload(1), creates and saves a third party, and returns 201.     |
| PATCH   | /api/users/{username}/change_password | ADMIN, USER | Requires a payload(3), changes user password, and returns 200.               |
| PATCH   | /api/users/edit/user/{username}       | ADMIN       | Requires a payload(4), changes any user properties, and returns 200.         |

#### Payload 1 example

```
{
"username":"string",
"password":"string",
"name":"string"
}
```

#### Payload 2 examples

```
{
    "username":"string",
    "password":"string",
    "name":"string",
    "dateOfBirth":"YYYY-MM-DD",
    "paStreetAddress":"string",
    "paPostalCode":"string",
    "paCity":"string",
    "paCountry":"string",
    "maStreetAddress":"string",
    "maPostalCode":"string",
    "maCity":"string",
    "maCountry":"string"
}
```

Notes: The password will be encrypted for safer storage.  
The mailing address (ma) is optional. Being the following payload is also valid:

```
{
    "username":"string",
    "password":"string",
    "name":"string",
    "dateOfBirth":"YYYY-MM-DD",
    "paStreetAddress":"string",
    "paPostalCode":"string",
    "paCity":"string",
    "paCountry":"string"
}
```

#### Payload 3 example

```
{
    "currentPassword":"string",
    "newPassword":"string",
    "repeatedNewPassword":"string"
}
```

Notes: The current password must match the users' password.  
The newPassword and repeatedNewPassword must be identical.

#### Payload 4 example

```
{
    "username":"string",
    "password":"string",
    "name":"string",
    "dateOfBirth":"YYYY-MM-DD",
    "paStreetAddress":"string",
    "paPostalCode":"string",
    "paCity":"string",
    "paCountry":"string",
    "maStreetAddress":"string",
    "maPostalCode":"string",
    "maCity":"string",
    "maCountry":"string"
}
```

Notes: All the values are optional and will only take effect if they exist. For example, a postal code is not available
on admins.  
To add a new mailing address to a user without a mailing address, all the ma properties must be present.

### Accounts

| Request |                  Route                  | Permissions |                                                          Description                                                          |
|---------|-----------------------------------------|:-----------:|-------------------------------------------------------------------------------------------------------------------------------|
| GET     | /api/accounts                           | ADMIN, USER | Returns list of all accounts. For users, only the owned accounts.                                                             |
| GET     | /api/accounts/checking_accounts         | ADMIN       | Returns list of all checking accounts.                                                                                        |
| GET     | /api/accounts/student_checking_accounts | ADMIN       | Returns list of all student checking accounts.                                                                                |
| GET     | /api/accounts/savings_accounts          | ADMIN       | Returns list of all savings accounts.                                                                                         |
| GET     | /api/accounts/credit_cards              | ADMIN       | Returns list of all credit cards.                                                                                             |
| GET     | /api/accounts/{id}                      | ADMIN, USER | Return a single account by id.                                                                                                |
| GET     | /api/accounts/{id}/balance              | ADMIN, USER | Return the balance for a single account by id.                                                                                |
| POST    | /api/accounts/new_checking_account      | ADMIN       | Requires a payload(5), creates and saves a checking account or a student savings account (depending on age), and returns 201. |
| POST    | /api/accounts/new_savings_account       | ADMIN       | Requires a payload(5), creates and saves a savings account, and returns 201.                                                  |
| POST    | /api/accounts/new_credit_card           | ADMIN       | Requires a payload(5), creates and saves a credit card, and returns 201.                                                      |
| PATCH   | /api/accounts/edit/account/{id}         | ADMIN       | Requires a payload(6), changes any account property, and returns 200.                                                         |

#### Payload 5 example

```
{
    "initialBalance":"decimal",
    "currency":"string (3 charactes)",
    "primaryOwnerId":"integer",
    "primaryOwnerUsername":"string",
    "secondaryOwnerId":"integer",
    "secondaryOwnerUsername":"string"
}
```

Notes: The balance must be positive.  
The currency value must have 3 digits.  
The id and username must correspond to the same user, and it needs to be an account holder.  
The secondary owner is optional. Being the following payload is also valid:

```
{
    "initialBalance":"decimal (positive)",
    "currency":"string (3 digits)",
    "primaryOwnerId":"integer",
    "primaryOwnerUsername":"string"
}
```

#### Payload 6 example

```
{
    "primaryOwnerUsername":"string",
    "secondaryOwnerUsername":"string",
    "accountStatus":"string (active or frozen)",
    "currency":"string (3 charactes)",
    "accountBalance":"decimal (positive)",
    "penaltyFee":"decimal (positive)",
    "lastPenaltyFee":"(YYYY-MM-DD)",
    "minimumBalance":"decimal (positive)",
    "creditLimit":"decimal (positive)",
    "monthlyMaintenanceFee":"decimal (positive)",
    "lastMaintenanceFee":"(YYYY-MM-DD)",
    "savingsAccountInterestRate":"decimal (positive, smaler than 0.5)",
    "creditCardInterestRate":"decimal (greater than 0.1)",
    "lastInterestUpdate":"(YYYY-MM-DD)"
}
```

Notes: All the values are optional and will only take effect if they exist.  
Account status should only be used on checking, student, and savings accounts.  
The minimum balance should only be used on checking and savings accounts.  
The credit limit is specific to credit cards.  
Maintenance Fees are specific to checking accounts.  
The interest rate should only be used on savings accounts and credit cards. The specific values of
savingsAccountInterestRate and creditCardInterestRate must be used for each one separately.

### Transactions

| Request |                               Route                              | Permissions |                                            Description                                                                                          |
|---------|------------------------------------------------------------------|:-----------:|-------------------------------------------------------------------------------------------------------------------------------------------------|
| GET     | /api/accounts/{account_id}/transactions/                         | ADMIN, USER | Returns list of all transaction, from a given account by id.                                                                                    |
| GET     | /api/accounts/{account_id}/transactions/{transaction_id}         | ADMIN, USER | Returns a single transaction by id, from a given account by id.                                                                                 |
| GET     | /api/accounts/{account_id}/transactions/{transaction_id}/receipt | ADMIN, USER | Returns the receipt of a single transaction by id, from a given account by id.                                                                  |
| POST    | /api/accounts/{account_id}/transactions/new_local_transaction    | ADMIN, USER | Requires a payload(7), creates, processes, and saves a local transaction, and returns 201.                                                      |
| POST    | /api/accounts/transactions/new_third_party_transaction           | PUBLIC      | Requires a payload(8) and the third party's hashed key on the header. Creates, processes, and saves a third party transaction, and returns 201. |

#### Payload 7 example

```
{
    "transferValue":"decimal (positive)",
    "currency":"string (3 charactes)",
    "targetAccountId":"integer",
    "targetOwnerName":"string"
}
```

Notes: The id and name must correspond to the same user, and it needs to be an account holder.

#### Payload 8 example

```
{
    "transferValue":"decimal (positive)",
    "currency":"string (3 charactes)",
    "targetAccountId":"integer",
    "secretKey":"string (account's secret key)",
    "transactionPurpose":"string (send or request)"
}
```

Notes: The id and secretKey must correspond to the same user and account. It needs to be an account holder and the
account cannot be a credit card.  
The header must have a valid hashed key to identify the third party.  
Send will send funds to the targeted account. Request will withdraw funds from the targeted account.

## Testing

For the unit testing, it was used Jacoco to verify logic branches and Mockito to isolate the process of testing
services.

The automated integration testing for the controllers was not yet implemented. To test the whole application, manual
integration testing was done in Postman. Most of the test cases and Postman testing was saved and can be accessed
here ([Manual integration testing](extras/postman/%5BJoão%20Afonso%5D%20Midterm%20-%20Manual%20Testing.postman_collection.json))
, and imported to postman.

## Diagrams

### UML Use Case Diagram

![sql relations diagram](extras/diagrams/UML%20Diagram.svg)

### SQL Relations Diagram

![sql relations diagram](extras/diagrams/SQL_Model.png)

## Next Steps

- Finish controllers' integration testing;
- Remodel Credit card to make it more realistic (at the moment it behaves as an account);
- Create a Request model to connect the admins with the account holders (request account alterations, user alterations);
- Implement an initial save for the cached files of the currency exchange rates. Make the application update daily.

