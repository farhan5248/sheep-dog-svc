# UML Communication Patterns

These patterns describe the communication sequences between classes for key use cases. Each pattern represents a unique combination of collaborating classes and their public method calls. TestObject, TestSteps, and generated step definitions are infrastructure and not included as spec'd classes.

## Execute Goal

**Regex**: `^Execute Goal$`

This pattern covers the full lifecycle of a Darmok Maven goal invocation: initialization, cleanup, scenario iteration with RGR phases, external process execution, and structured logging.

| # | Class | Role |
|---|---|---|
| 1 | [{Goal}Mojo](uml-class-GoalMojo.md) | Entry point. Calls inherited lifecycle and scenario methods. |
| 2 | [DarmokMojo](uml-class-DarmokMojo.md) | Lifecycle, scenario iteration, and RGR phase orchestration. |
| 3 | [RgrPhase](uml-class-RgrPhase.md) | Abstract base. Owns the public `run(state)` template, `runVerifyLoop`, and `runTimeoutRecoveryLoop`. |
| 4 | [{RgrPhase}Phase](uml-class-RgrPhasePhase.md) | Concrete RGR phases — `RedPhase` drives maven goals + runner class generation + `mvn test`; `GreenPhase` invokes `/rgr-green` then verifies; `RefactorPhase` invokes `/rgr-refactor forward` then verifies. |
| 5 | [DarmokMojoState](uml-class-DarmokMojoState.md) | Mutable per-scenario state threaded through all three phases. Carries exit code, per-phase durations, scenario/branch/tag, and post-scenario commit. |
| 6 | [Phase](uml-class-Phase.md) | Enum keying `DarmokMojoState`'s duration map. |
| 7 | [ProcessRunner](uml-class-ProcessRunner.md) | Process lifecycle and output streaming. |
| 8 | [{Tool}Runner](uml-class-ToolRunner.md) | Tool-specific command construction. ClaudeRunner adds API-retry logic plus a per-invocation timeout (`waitFor(maxClaudeSeconds, SECONDS)` + `destroyForcibly()` → sentinel exit code) that surfaces to the phase. |
| 9 | [DarmokMojo{DataFileType}](uml-class-DarmokMojoDataFileType.md) | Structured per-row file emitter. `DarmokMojoLog` writes timestamped log lines; `DarmokMojoMetrics` writes per-scenario CSV rows. |

### Sequence

1. **{Goal}Mojo**.execute()
2. **DarmokMojo**.init() — constructs RedPhase, GreenPhase, RefactorPhase with wired runners
3. **DarmokMojo**.runCleanUp()
4. **DarmokMojo**.getNextScenario() — loop until null
5. **DarmokMojo**.processScenario(entry) — delegates to the three phases, branches on each PhaseResult
6. **RedPhase**.run(tag) / **GreenPhase**.run(tag) / **RefactorPhase**.run() — each returns PhaseResult after logging its own start/complete markers and measuring duration. GreenPhase and RefactorPhase additionally run (a) a timeout-recovery loop that kicks in when **ClaudeRunner**.run returns the timeout sentinel — `mvn clean install` in the target project plus up to `maxTimeoutAttempts - 1` **ClaudeRunner**.resume(workingDir, "pls continue") cycles — and (b) the existing verify loop (`mvn clean verify` + up to `maxVerifyAttempts - 1` **ClaudeRunner**.resume(workingDir, "mvn clean verify failures should be fixed") cycles)
7. **{Tool}Runner**.run(workingDirectory, args) / **ClaudeRunner**.resume(workingDir, message) — called by each phase
8. **MojoLog**.info(...) / .debug(...) / .warn(...) / .error(...) — throughout
9. **DarmokMojo**.cleanup()
