# Spring Data JPA Query By Example Demo

Transform your Spring Data JPA queries from complex boilerplate into elegant, type-safe search operations with [Query By Example](https://docs.spring.io/spring-data/jpa/reference/repositories/query-by-example.html). This project demonstrates how to implement dynamic, flexible queries without the overhead of writing multiple repository methods or complex JPQL statements.

## Overview

Query By Example (QBE) is a user-friendly querying technique that allows you to create dynamic queries using domain object instances as templates. This approach shines when building search functionality with multiple optional parameters, such as advanced search forms or dynamic filters.

## Project Requirements

- Java 23
- Spring Boot 3.3.5
- PostgreSQL
- Docker (for running the database)
- Maven

## Key Features

- Dynamic query generation using domain objects
- Type-safe query construction
- Minimal boilerplate code
- Integration with Spring Data JPA
- Docker-based development environment
- Comprehensive test coverage using TestContainers

## Getting Started

### Environment Setup

Ensure you have the following installed:
- Java 23 JDK
- Docker Desktop
- Maven

### Running the Application

1. Build and run the application:
```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## Understanding Query By Example

### Basic Example

Here's a simple example of how to use Query By Example:

```java
// Create a probe (example) entity
Employee probe = new Employee();
probe.setDepartment("IT");
probe.setPosition("Developer");

// Create the Example with the probe
Example<Employee> example = Example.of(probe);

// Find all matching employees
List<Employee> developers = employeeRepository.findAll(example);
```

### Advanced Usage

For more complex scenarios, you can customize the matching behavior:

```java
// Create a custom ExampleMatcher
ExampleMatcher matcher = ExampleMatcher.matching()
    .withIgnoreCase()
    .withStringMatcher(StringMatcher.CONTAINING);

Employee probe = new Employee();
probe.setDepartment("eng");  // Will match "Engineering"

Example<Employee> example = Example.of(probe, matcher);
List<Employee> engineers = employeeRepository.findAll(example);
```

## When to Use Query By Example

QBE is ideal for:

- ✅ Search forms with multiple optional filters
- ✅ Quick prototyping and development
- ✅ Simple equality-based queries
- ✅ Scenarios where search criteria are unknown at compile time

Consider alternatives when you need:

- ❌ Complex comparisons (>, <, BETWEEN)
- ❌ OR conditions
- ❌ Complex JOIN operations
- ❌ Custom SQL functions

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── dev/danvega/qbe/
│   │       ├── model/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       ├── application.yml
│       └── data.sql
└── test/
    └── java/
        └── dev/danvega/qbe/
```

## Configuration

The application's main configuration is in `application.yml`:

```yaml
spring:
  application:
    name: qbe
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
```

## Testing

The project uses TestContainers for integration testing, ensuring that tests run against a real PostgreSQL database:

```java
@SpringBootTest
@Testcontainers
class EmployeeRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine"));

    @Autowired
    private EmployeeRepository employeeRepository;
    
    // ...
}
```

## Learn More

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/reference/repositories/query-by-example.html)