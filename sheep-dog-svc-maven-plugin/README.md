# Sheep Dog Development Service Maven Plugin

Maven plugin that integrates the cloud transformation service into the build lifecycle.

## Overview

This plugin allows developers to run specification-to-UML transformations as part of their Maven build. It communicates with the sheep-dog-svc REST service to perform transformations, reading source files from the project and writing generated files back.

## Key Functionality

- Maven goals for AsciiDoctor to UML transformation
- Maven goals for UML to AsciiDoctor generation
- Maven goals for Cucumber to UML transformation
- Maven goals for UML to Cucumber generation (plain, Guice, Spring variants)
- Configurable service host, port, and timeout settings
- Tag-based filtering for selective transformations

## Technology

- Maven Plugin API
- REST client (Spring RestTemplate)
- Depends on sheep-dog-grammar library
- Java 21
