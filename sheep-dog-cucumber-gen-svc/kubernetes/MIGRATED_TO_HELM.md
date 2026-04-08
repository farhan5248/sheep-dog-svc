# Migrated to Helm

This per-service kustomize directory is **no longer used**. The deployment of `sheep-dog-cucumber-gen-svc` is now handled by the umbrella Helm chart at `sheep-dog-ops/sheep-dog/helm/sheep-dog/templates/cucumber-gen-*.yaml`.

Kept temporarily as a reference (see #29). Will be deleted in a few weeks.

The service's `pom.xml` now runs `helm upgrade --install sheep-dog ...` with `--set images.cucumberGen.tag=<built-tag>` instead of `kubectl apply -k kubernetes/overlays/<ns>`.
