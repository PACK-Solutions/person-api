# Person API Project Guidelines

## Project Overview

The Person API is a RESTful service for managing person records. It provides CRUD operations for person entities with the following attributes:
- First name
- Last name
- Date of birth
- City of birth
- Country of birth
- Nationality

The application is built using:
- Spring Boot 3.4.4
- Kotlin 1.9.25
- Spring Data JDBC
- PostgreSQL database
- Docker and Docker Compose for containerization

## Spring Boot Best Practices

### Architecture
- Follow a layered architecture with clear separation of concerns:
  - Controller layer for handling HTTP requests
  - Service layer for business logic (when needed)
  - Repository layer for data access
  - Model layer for domain entities

### API Design
- Use RESTful principles for API design
- Use appropriate HTTP methods (GET, POST, PUT, DELETE)
- Return appropriate HTTP status codes
- Use DTOs (Data Transfer Objects) to separate API contracts from domain models when needed
- Implement proper error handling and validation

### Configuration
- Use application.properties or application.yml for configuration
- Externalize configuration for different environments
- Use profiles for environment-specific configurations

### Testing
- Write unit tests for all components
- Use Spring Boot Test for integration testing
- Aim for high test coverage

### Security
- Implement proper authentication and authorization
- Secure sensitive endpoints
- Validate and sanitize input data
- Use HTTPS in production

### Performance
- Use pagination for large result sets
- Implement caching where appropriate
- Optimize database queries

## Kotlin Best Practices

### Language Features
- Use data classes for model entities
- Leverage Kotlin's null safety features
- Use immutable properties (val) when possible
- Use extension functions to extend existing classes
- Use function expressions for concise code

### Functional Programming
- Use collection operations like map, filter, and reduce
- Use sequence for large collections to improve performance
- Leverage Kotlin's scope functions (let, apply, run, with, also)

### Coroutines
- Use coroutines for asynchronous programming when needed
- Use structured concurrency patterns
- Handle exceptions properly in coroutines

### Interoperability
- Be mindful of Java interoperability
- Use @JvmField, @JvmStatic, and @JvmOverloads annotations when needed for Java interop

### Code Style
- Follow the official Kotlin coding conventions
- Use meaningful names for variables, functions, and classes
- Keep functions small and focused on a single responsibility
- Use proper documentation and comments

## Development Workflow

### Version Control
- Use Git for version control
- Follow a branching strategy (e.g., GitFlow or GitHub Flow)
- Write meaningful commit messages
- Use pull requests for code reviews

### CI/CD
- Implement continuous integration
- Automate testing and deployment
- Use Docker for consistent environments

### Documentation
- Keep README.md up to date
- Document API endpoints
- Use code comments for complex logic

By following these guidelines, we aim to maintain a high-quality, maintainable, and scalable application.