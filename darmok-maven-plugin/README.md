# Darmok Maven Plugin

Maven plugin automating the Red-Green-Refactor (RGR) TDD cycle using Claude AI.

## Overview

Generates RGR scenarios from existing test specifications or by comparing branches. Integrates with Claude AI for code generation and uses Git for branch-based comparisons. Orchestrates Maven builds and process execution as part of the TDD workflow.

## Key Functionality

- Generate RGR scenarios from existing tests (gen-from-existing goal)
- Generate scenarios by comparing branches (gen-from-comparison goal)
- Claude AI integration for code generation
- Git-based branch comparison
- Maven build orchestration

## Technology

- Apache Maven Plugin API
- Java 21

## Build

```
scripts/install.bat
```

## Usage

```xml
<plugin>
  <groupId>org.farhan</groupId>
  <artifactId>darmok-maven-plugin</artifactId>
  <version>${darmok-maven-plugin.version}</version>
</plugin>
```
