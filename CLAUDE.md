# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with the sheep-dog-cloud repository.

> **📚 Complete Documentation**: See the comprehensive guidance files:
> - `../sheep-dog-main/CLAUDE.md` - Cross-repository coordination
> - `CLAUDE.architecture.md` - System architecture & design patterns
> - `CLAUDE.development.md` - Development workflows & practices  
> - `CLAUDE.testing.md` - BDD/testing methodologies

## Repository Overview

**sheep-dog-cloud** provides **cloud-native microservices** for transformation services in the sheep-dog ecosystem.

### Key Components
- **sheep-dog-dev-svc**: Spring Boot REST service for transformations
- **sheep-dog-dev-svc-maven-plugin**: Maven plugin for service-oriented transformations
- **Docker Integration**: Containerized deployment with multi-stage builds
- **Kubernetes Manifests**: Cloud orchestration configurations

### Maven Plugin Used
- **Plugin**: `sheep-dog-dev-svc-maven-plugin:1.33-SNAPSHOT`
- **Key Feature**: Does NOT require `-DrepoDir` parameter (auto-handles repositories)

## Development Commands

### Building This Repository
```bash
mvn clean install
```

### Forward Engineering (Service-Based)
```bash
# In sheep-dog-dev-svc project
scripts/forward-engineer.bat

# Or manually:
mvn clean
mvn org.farhan:sheep-dog-dev-svc-maven-plugin:uml-to-cucumber-spring \
  -Dtags="round-trip" -Dhost="dev.sheepdogdev.io"
```

### Reverse Engineering (Service-Based)
```bash
# In sheep-dog-dev-svc project  
scripts/reverse-engineer.bat

# Or manually:
mvn clean
mvn org.farhan:sheep-dog-dev-svc-maven-plugin:cucumber-to-uml \
  -Dtags="round-trip"
```

## Cloud Development Workflow

### Local Development
```bash
# Start local services stack
docker compose up -d

# Hot reload development
mvn spring-boot:run -Dspring.profiles.active=surefire
```

### Service Architecture
- **REST APIs**: Transformation endpoints at `/api/v1/`
- **JMS Messaging**: Asynchronous processing with Apache Artemis
- **Database**: MySQL for persistent UML model storage
- **Actuator**: Health checks and metrics at `/actuator/`

### API Endpoints (sheep-dog-dev-svc)
- `POST /api/asciidoctor/to-uml` - Convert AsciiDoc to UML
- `POST /api/cucumber/to-uml` - Convert Cucumber to UML  
- `POST /api/uml/to-cucumber` - Generate Cucumber from UML
- `POST /api/uml/to-cucumber-spring` - Generate Spring Cucumber from UML
- `POST /api/uml/to-cucumber-guice` - Generate Guice Cucumber from UML

## Container Development

### Docker Compose Services
```yaml
# From docker/compose.yaml
services:
  sheep-dog-dev-db:     # MySQL database
  sheep-dog-dev-mq:     # Apache Artemis message queue  
  sheep-dog-dev-svc:    # Spring Boot application
```

### Docker Images
- **Dependencies Image**: `dependencies.dockerfile` - Base image with dependencies
- **Application Image**: `springboot.dockerfile` - Application layer
- **Multi-stage Build**: Optimized for cloud deployment

### Kubernetes Deployment
```bash
# Apply base configuration
kubectl apply -k kubernetes/base/

# Apply environment-specific overlays
kubectl apply -k kubernetes/overlays/failsafe/
kubectl apply -k kubernetes/overlays/prod/
```

## Generated Code Structure

### Output Locations  
```
src-gen/test/
├── java/org/farhan/
│   ├── objects/              # Service object implementations
│   └── stepdefs/             # Service step definitions  
└── resources/cucumber/specs/ # Generated service test features
```

### Service-Specific Code Generation
- **Spring Integration**: Generated tests include Spring Boot configuration
- **Contract Testing**: Includes Spring Cloud Contract specifications
- **Database Integration**: H2 for testing, MySQL for production
- **REST Testing**: Uses REST Assured for API testing

## Cloud Deployment

### AWS EKS Integration
- **Cluster Setup**: Use scripts in `../sheep-dog-qa/sheep-dog-specs/scripts/aws-*`
- **GitHub Actions**: Automated deployment via workflows
- **Container Registry**: Uses GitHub Container Registry (ghcr.io)

### Kubernetes Configuration
```
kubernetes/
├── base/                    # Base Kubernetes manifests
│   ├── deployment.yaml     # Service deployment
│   ├── service.yaml        # Service exposure
│   ├── ingress.yaml        # External access
│   └── pvc.yaml            # Persistent volume claims
└── overlays/               # Environment-specific configurations
    ├── failsafe/           # Testing environment  
    └── prod/               # Production environment
```

### Service Configuration
- **Database**: MySQL with persistent volume claims
- **Messaging**: Apache Artemis for async processing
- **Ingress**: NGINX ingress controller for external access
- **Monitoring**: Spring Actuator endpoints for observability

## Testing in Cloud Environment

### Integration Testing
```bash
# Run integration tests against deployed services
mvn failsafe:integration-test -Dspring.profiles.active=failsafe

# Test with stub runner for contract testing
mvn test -Dspring.profiles.active=surefire
```

### Contract Testing
- **Provider**: Service endpoints generate contracts
- **Consumer**: Tests validate against provider contracts  
- **Stubs**: Auto-generated stubs for testing isolation

### Service Testing Strategy
- **Unit Tests**: `src/test/` with Spring Boot Test
- **Integration Tests**: `src-gen/test/` with full service stack
- **Contract Tests**: Spring Cloud Contract specifications
- **End-to-End Tests**: Generated Cucumber features

## Working with Transformations

### Service-Based Transformations
- **No repoDir**: Service automatically handles repository discovery
- **Host Parameter**: Use `-Dhost="<hostname>"` for environment-specific generation
- **Async Processing**: Transformations can be queued via JMS

### REST API Usage
```bash
# Transform via REST API
curl -X POST http://localhost:8080/api/asciidoctor/to-uml \
  -H "Content-Type: application/json" \
  -d '{"content": "...", "tags": ["sheep-dog-grammar"]}'
```

### Message Queue Integration
- **Producers**: Send transformation requests to queue
- **Consumers**: Process transformations asynchronously  
- **Dead Letter Queue**: Handle failed transformations

## Repository-Specific Notes

### Dependencies
- **Build Order**: This repo should be built after `sheep-dog-ops`, `sheep-dog-qa`, and `sheep-dog-local`
- **Base Images**: Depends on dependency images for efficient builds
- **Plugin Repository**: Uses GitHub Packages for Maven plugins

### Configuration
- **Profiles**: 
  - `surefire`: Unit testing configuration
  - `failsafe`: Integration testing configuration
  - `default`: Local development configuration
- **Properties**: Environment-specific settings in `application-{profile}.properties`

### Cloud-Native Features
- **Health Checks**: Kubernetes readiness and liveness probes
- **Scaling**: Horizontal pod autoscaling support
- **Configuration**: External configuration via ConfigMaps and Secrets
- **Logging**: Structured logging for cloud environments

## Integration with Other Repositories

### Upstream Dependencies
- **sheep-dog-qa**: Source of specifications and deployment configurations
- **sheep-dog-local**: May reference core transformation libraries

### Service Endpoints
- **Internal**: Services communicate via Kubernetes service discovery
- **External**: Ingress controller provides external access
- **Cross-Environment**: Different host configurations for dev/staging/prod

### Monitoring and Observability
- **Metrics**: Spring Actuator metrics exposed to Prometheus
- **Tracing**: Distributed tracing support for request flows
- **Logging**: Centralized logging with structured output