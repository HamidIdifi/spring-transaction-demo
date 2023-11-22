# Implementing Transactions in a Spring Boot Application

## Introduction

When building an application, handling transactions is a crucial aspect of ensuring data integrity and consistency. Spring Boot offers built-in support for implementing transactions in combination with Spring Data JPA. In this repository, we will show how to implement transactions in a Spring Boot application using Spring Data JPA.

## Understanding Transactions

A **transaction** simply represents a unit of work that is performed against a database.
A transaction is a sequence of one or more operations (database queries, updates, or other operations) that are executed as a single, indivisible unit.
A transaction ensures that all the database operations are executed as a single unit of work. If any operation in a transaction fails, the entire transaction is rolled back, ensuring that the data remains consistent.
The key properties of a transaction are often referred to as the **ACID** properties:

> **Atomicity**: This property ensures that a transaction is treated as a single.Either all the operations within the transaction are executed, or none of them are. If any part of the transaction fails, the entire transaction is rolled back, and the database is left in its previous consistent state.
>
> **Consistency**: This property ensures that a transaction brings the database from one consistent state to another. The database must satisfy certain integrity constraints before and after the transaction, ensuring that the data remains in a valid state.
>
> **Isolation**: Isolation ensures that the intermediate state of a transaction is not visible to other transactions until the transaction is committed.
>
> **Durability**: Once a transaction is committed, its effects on the database are permanent, even in the event of a system failure. Durability guarantees that the changes made by a committed transaction survive any subsequent failures.

Spring Boot supports both programmatic and declarative transaction management. In programmatic transaction management, the developer has to manually define the transaction boundaries and manage the transaction programmatically. In declarative transaction management, the transaction management is handled by the container, and the developer only has to define the transactional boundaries using annotations.

## Getting Started

### Prerequisites

- Java 17
- Maven

### Installation

1. Clone the repository:
  ```bash
  git clone https://github.com/HamidIdifi/spring-transaction-demo.git
  ```
2. Navigate to the project directory:
  ```bash
  cd spring-transaction-demo
  ```
3. Build the project:
  ```bash
  mvn clean install
  ```
4. Run the application:
  ```bash
  mvn spring-boot:run
  ```
## Dependencies

We can use the Spring Initializr to generate a new Spring Boot project with the following dependencies for our project:

- Spring Web
- Spring Data JPA
- H2 Database
  Or:

```bash
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

Once we have created the project, we need to configure our application properties to connect to the database. We can do this by adding the following properties in our application.properties file:

```bash
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true

```

## Implement Transactions

To implement transactions in our Spring Boot application, we will be using Spring Data JPA. Spring Data JPA provides a @Transactional annotation that we can use to declare the transaction boundaries. When a method is annotated with @Transactional, Spring will start a transaction before the method is executed and commit the transaction after the method completes. If the method throws an exception, Spring will automatically rollback the transaction.
In the context of Spring managing transactions involves starting, committing, and rolling back transactions as needed. This is crucial for maintaining data integrity and consistency in applications, particularly in scenarios where multiple operations need to be executed as a single, cohesive unit.
Let's consider a simple banking scenario where you need to transfer money from one account to another. In this case, you want to ensure that both the withdrawal from the source account and the deposit into the destination account occur atomically as a single transaction.

```bash
@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, Double amount) {
        Account sourceAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds in the source account.");
        }
        accountRepository.withdraw(fromAccountId, amount);
        accountRepository.deposit(toAccountId, amount);
    }
}
```

The `transfer` method is annotated with `@Transactional`, which means t the entire transfer method will be executed within a transaction. Within the `transfer` method, we call the `withdraw` method to decrease the balance of the `fromAccount`, and then we call the `deposit` method to increase the balance of the toAccount. If either of these methods throws an exception, the entire transaction will be rolled back.

That’s it! With just a few lines of code, we have implemented transactions in our Spring Boot application using Spring Data JPA.

## Best Practices

Here are some best practices to keep in mind when implementing transactions with Spring Data JPA in a Spring Boot application:

### Keep Transaction scopes as small as possible

Transactions should only be used for the smallest possible unit of work to minimize the potential for locking or blocking other database transactions.

Let’s say we have a service that performs two operations on our database: it updates a user’s email address and then sends an email to the user to confirm the change. To keep the transaction scope as small as possible, we should split this service into two separate services, each with its own transactional boundary.

Here’s an example of what our original service might look like:

```bash @Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void updateUserEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        user.setEmail(newEmail);
        userRepository.save(user);
        emailService.sendEmail(user.getEmail(), "Email address updated.");
    }
}
```

To keep the transaction scope as small as possible, we can split our `UserService` into two separate services, each with its own transactional boundary. Here's an example of what our updated services might look like:

```bash
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateUserEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        user.setEmail(newEmail);
        userRepository.save(user);
    }
}

@Service
@Transactional
public class EmailService {

    public void sendEmail(String email, String message) {
        // send email code
    }
}
```

### Catch and handle Exceptions

When an exception occurs within a transaction, it’s important to catch and handle it appropriately. Failing to handle exceptions correctly can cause the transaction to fail and leave the database in an inconsistent state.

To handle exceptions correctly, we can catch the exception and handle it appropriately. For example, if one of the accounts cannot be found, we can throw a custom exception that provides more information about the error.

### Use Read-only Transactions for Read Operations

For read-only operations that don’t modify the database, it’s best to use read-only transactions to improve performance. Read-only transactions do not acquire any database locks, and they can be executed more efficiently than read-write transactions.

Let’s say we have a service that retrieves a user’s order history from the database. The `getUserOrders` method of our `UserService` retrieves the user's order history using the `orderRepository`.

This tells Spring Data JPA that we’re only performing read operations within this transaction, and it can optimize the transaction accordingly.

```bash
@Service
@Transactional(readOnly = true)
public class UserService {

    private final OrderRepository orderRepository;

    public UserService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        return orderRepository.findByUser(user);
    }
}
```

### Use the @Modifying annotation for Update and Delete Operations

When using update or delete queries, use the `@Modifying` annotation to indicate to Spring that the query is modifying the database.

Let’s say we have a service that updates a user’s email address in the database. The `updateUserEmail` method of our `UserService` updates the user's email address using the `userRepository`.

```bash
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateUserEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        user.setEmail(newEmail);
        userRepository.save(user);
    }
}
```

To use the `@Modifying` annotation, we can add a custom update query to our `UserRepository` interface using the `@Query` annotation.

```bash
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("update User u set u.email = :newEmail where u.id = :userId")
    void updateUserEmail(@Param("userId") Long userId, @Param("newEmail") String newEmail);
}
```

We can then update our `updateUserEmail` method in `UserServiceclass` to call the `updateUserEmail` method of our `UserRepository`.

By following these best practices, you can ensure that your transactions are implemented efficiently, safely, and effectively in your Spring Boot application using Spring Data JPA.

## Usage

The application exposes one endpoint:

- `POST http://localhost:8080/api/transfers/transfer` : to perform transfer money operation
