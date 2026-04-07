# Sheep Dog Svc Maven Plugin

Maven plugin that integrates specification-to-UML transformations into the build lifecycle.

## Overview

Provides Maven goals for running AsciiDoc and Cucumber transformations as part of a Maven build. Communicates with the sheep-dog-svc REST services to perform the transformations, reading source files from the project and writing generated files back.

## Key Functionality

- Maven goals for AsciiDoc to UML transformation and reverse
- Maven goals for Cucumber to UML transformation and reverse (plain, Guice, Spring)
- Tag-based filtering for selective transformations
- Configurable service host, port, and timeout

## Technology

- Apache Maven Plugin API
- Spring RestTemplate (REST client)
- Depends on sheep-dog-grammar
- Java 21

## Build

```
scripts/install.bat
```

## Usage

```xml
<plugin>
  <groupId>org.farhan</groupId>
  <artifactId>sheep-dog-svc-maven-plugin</artifactId>
  <version>${sheep-dog-svc-maven-plugin.version}</version>
</plugin>
```

```
mvn org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml -Dtags="sheep-dog-grammar"
mvn org.farhan:sheep-dog-svc-maven-plugin:uml-to-cucumber-spring -Dtags="sheep-dog-grammar"
```
