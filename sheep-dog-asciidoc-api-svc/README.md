# Sheep Dog AsciiDoc API Svc

REST microservice for bidirectional transformations between AsciiDoc specifications and UML models.

## Overview

Spring Boot service that wraps the sheep-dog-asciidoc-api library as REST endpoints. Stores UML model data in MySQL and uses JMS (ActiveMQ Artemis) for asynchronous processing.

## Key Functionality

- AsciiDoc to UML transformation via REST
- UML to AsciiDoc generation via REST
- Asynchronous message processing via JMS
- Persistent UML model storage in MySQL

## Technology

- Spring Boot 3.4 (Web, Data JPA, Artemis, Actuator)
- ActiveMQ Artemis
- MySQL
- Depends on sheep-dog-asciidoc-api
- Java 21

## Build

```
scripts/install.bat
```
