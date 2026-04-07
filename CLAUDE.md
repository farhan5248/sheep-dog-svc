# CLAUDE.md

See [README.md](README.md) for project descriptions and build order.

## Build Command

Run `scripts/install.bat` in each project directory.

## Development Commands

### Forward Engineering

```bash
# In sheep-dog-specs/sheep-dog-features directory
scripts/forward-engineer.bat
```

### Reverse Engineering

```bash
# In sheep-dog-specs/sheep-dog-features directory
scripts/reverse-engineer.bat <tag>
```

### Docker and Kubernetes

Each service includes Docker build files and Kubernetes manifests under `docker/` and `kubernetes/` directories.

### API Endpoints

- **sheep-dog-asciidoc-api-svc**: `POST /api/asciidoctor/to-uml`
- **sheep-dog-cucumber-gen-svc**: `POST /api/cucumber/to-uml`, `POST /api/uml/to-cucumber`, `POST /api/uml/to-cucumber-spring`, `POST /api/uml/to-cucumber-guice`

### Testing Profiles

- `surefire`: Unit testing
- `failsafe`: Integration testing against deployed services
