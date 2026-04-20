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

## SPC observability (`#252`)

Each scenario processed by `gen-from-existing` / `gen-from-comparison` emits a row to `<baseDir>/target/darmok/metrics.csv`:

```
timestamp,commit,scenario,phase_red_ms,phase_green_ms,phase_refactor_ms,phase_total_ms
```

The commit column holds `git rev-parse HEAD` captured at the start of the scenario so every data point is attributable to the change that produced it.

### Routing metrics into the Grafana dashboard

The observability Helm chart (`sheep-dog-ops/infra/grafana/`) runs a Grafana pod that reads `metrics.csv` from a hostPath-backed PVC. To route your Darmok run's CSV into that pipeline instead of the per-project `target/darmok/` directory, point `LOG_PATH` at the shared folder before running the plugin.

**One-time setup** on windows-minipc (the Darmok host):
```bat
mkdir C:\Users\Farhan\minikube-data\darmok-metrics
```

This directory is mounted into the minikube VM at `/mnt/darmok-metrics` via the `minikube start --mount-string` branch in `sheep-dog-ops/infra/minikube/setup-cluster-local.bat`. If minikube is already running without that mount, `minikube delete` and rerun `setup-cluster-local.bat` to pick it up.

**Per-run** (cmd):
```bat
set LOG_PATH=C:\Users\Farhan\minikube-data\darmok-metrics
mvn org.farhan:darmok-maven-plugin:gen-from-existing
```

`DarmokMojo.resolveLogDir` honours `LOG_PATH` and writes `metrics.csv` (plus the dated log files) there.

### Viewing the dashboard

Open **http://grafana.sheepdog.io**, log in as `admin` (password is the plaintext value in the `grafana` Secret — `kubectl -n observability get secret grafana -o jsonpath="{.data.admin-password}" | base64 -d`), and navigate to **Dashboards → Darmok RGR cycle time (SPC)** (direct: http://grafana.sheepdog.io/d/darmok-spc).

Requires `minikube tunnel` running and `127.0.0.1 grafana.sheepdog.io` in your hosts file — same pattern as the other `*.sheepdog.io` services.

The dashboard renders:
- **X chart** — each scenario's green-phase duration with mean + UCL/LCL (Nelson's 2.66·mR̄ bounds).
- **mR chart** — moving range between consecutive green-phase durations, with UCL (3.267·mR̄) to flag shifts in process variability.

Points outside the control limits indicate **special-cause variation** — the commit in that row is the first place to look. Points inside the limits represent **common-cause variation** — normal process noise, not actionable on a per-point basis.

Set the time range to cover your run history — "Last 7 days" is a reasonable default for ongoing SPC work.
