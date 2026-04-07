# Sheep Dog Cucumber Gen Svc

REST microservice for bidirectional transformations between Cucumber specifications and UML models.

## Overview

Spring Boot service that wraps the sheep-dog-cucumber-gen library as REST endpoints. Supports plain Cucumber, Guice, and Spring variants. Stores UML model data in MySQL and uses JMS (ActiveMQ Artemis) for asynchronous processing.

## Key Functionality

- Cucumber to UML transformation via REST
- UML to Cucumber generation via REST (plain, Guice, Spring variants)
- Asynchronous message processing via JMS
- Persistent UML model storage in MySQL

## Technology

- Spring Boot 3.4 (Web, Data JPA, Artemis, Actuator)
- ActiveMQ Artemis
- MySQL
- Depends on sheep-dog-asciidoc-api, sheep-dog-cucumber-gen
- Java 21

## Build

```
scripts/install.bat
```
