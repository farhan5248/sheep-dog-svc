# Sheep Dog Svc

Spring Boot microservices that expose the core transformation engine as REST APIs, deployed to Kubernetes. The Maven plugins call these services to perform code generation, and the MCP server integrates with Claude for AI-assisted development.

Services communicate via REST and JMS (Apache Artemis), with MySQL for persistent UML model storage. Each service includes Docker multi-stage builds and Kubernetes manifests for dev/qa environments.

## Projects

| Project | Description |
|---------|-------------|
| sheep-dog-asciidoc-api-svc | REST microservice for AsciiDoc-to-UML transformations |
| sheep-dog-cucumber-gen-svc | REST microservice for Cucumber-to-UML transformations |
| sheep-dog-mcp-svc | MCP server exposing transformation tools to AI assistants |
| sheep-dog-svc-maven-plugin | Maven plugin integrating transformations into the build lifecycle |
| darmok-maven-plugin | Maven plugin automating the Red-Green-Refactor TDD cycle |

## Build Order

```
sheep-dog-asciidoc-api-svc
  -> sheep-dog-cucumber-gen-svc
  -> sheep-dog-mcp-svc
  -> sheep-dog-svc-maven-plugin
  -> darmok-maven-plugin
```

## Build Command

Run `scripts/install.bat` in each project directory.

## Docker and Kubernetes

Each service includes Docker build files and Kubernetes manifests under `docker/` and `kubernetes/` directories.
