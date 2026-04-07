# Sheep Dog MCP Svc

Model Context Protocol (MCP) server that exposes Sheep Dog transformation tools to AI assistants.

## Overview

Spring Boot MCP server that bridges AI assistants like Claude with the Sheep Dog transformation services. Delegates transformation work to sheep-dog-asciidoc-api-svc and sheep-dog-cucumber-gen-svc via REST calls. Supports SSE and STDIO transports.

## Key Functionality

- MCP tools for AsciiDoc and Cucumber transformations
- SSE and STDIO transport support
- Delegates to backend services via REST
- Spring Security integration

## Technology

- Spring Boot 3.4
- Spring AI MCP Server (WebMVC starter)
- Spring Security
- Java 21

## Build

```
scripts/install.bat
```
