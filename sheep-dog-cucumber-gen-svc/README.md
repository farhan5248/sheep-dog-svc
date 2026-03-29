# Sheep Dog Development Service

REST microservice that performs bidirectional transformations between specifications and UML models.

## Overview

This is the core transformation engine of the Sheep Dog ecosystem. It provides REST endpoints for converting AsciiDoctor and Cucumber specifications to UML models, and vice versa. The service stores model data in a MySQL database and uses JMS for asynchronous processing.

## Key Functionality

- AsciiDoctor to UML transformation and reverse
- Cucumber to UML transformation and reverse
- Supports multiple Cucumber frameworks: plain, Guice, and Spring
- Asynchronous message processing via JMS (ActiveMQ Artemis)
- Persists UML model data in MySQL

## Technology

- Spring Boot 3.4
- Spring Data JPA, Spring JMS
- ActiveMQ Artemis
- MySQL
- Depends on sheep-dog-dev library
- Docker/Kubernetes deployment
- Java 21
