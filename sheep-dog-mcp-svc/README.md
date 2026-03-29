# Sheep Dog Development MCP Server

MCP server that exposes Sheep Dog development tools to AI assistants like Claude.

## Overview

This project provides a Model Context Protocol (MCP) server that allows AI assistants to interact with the Sheep Dog ecosystem. It acts as a bridge between AI clients and the sheep-dog-dev-svc backend service, exposing transformation tools via the MCP protocol.

## Key Functionality

- Provides MCP tools for AI assistants to trigger specification transformations
- Supports SSE (Server-Sent Events) and STDIO transports for MCP communication
- Delegates transformation work to sheep-dog-dev-svc via REST calls
- Exposes tools like "Clear objects for AsciiDoctor files" for UML model management

## Technology

- Spring Boot 3.4
- Spring AI MCP Server (WebMVC starter)
- Docker/Kubernetes deployment
- Java 21
