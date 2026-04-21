# UML Communication Patterns

These patterns describe the communication sequences between classes for key use cases. Each pattern represents a unique combination of collaborating classes and their public method calls. TestObject, TestSteps, and generated step definitions are infrastructure and not included as spec'd classes.

## Execute Goal

**Regex**: `^Execute Goal$`

This pattern covers the full lifecycle of a Darmok Maven goal invocation: initialization, cleanup, scenario iteration with RGR phases, external process execution, and structured logging.

| # | Class | Role |
|---|---|---|
| 1 | [{Goal}Mojo](uml-class-GoalMojo.md) | Entry point. Calls inherited lifecycle and scenario methods. |
| 2 | [DarmokMojo](uml-class-DarmokMojo.md) | Lifecycle, scenario iteration, and RGR phase orchestration. |
| 3 | [RedPhase](uml-class-RedPhase.md) | Drives maven goals + runner class generation + `mvn test`. Returns PhaseResult. |
| 4 | [GreenPhase](uml-class-GreenPhase.md) | Invokes the Claude `/rgr-green` skill, recovers from a timed-out claude via `mvn clean install` + `claude --resume "pls continue"`, then runs `mvn clean verify` with `claude --resume` recovery. Returns PhaseResult. |
| 5 | [RefactorPhase](uml-class-RefactorPhase.md) | Invokes the Claude `/rgr-refactor forward` skill with the same timeout-recovery + verify loops as GreenPhase. Returns PhaseResult. |
| 6 | [PhaseResult](uml-class-PhaseResult.md) | Record carrying exit code + duration from each phase, plus static duration formatter. |
| 7 | [ProcessRunner](uml-class-ProcessRunner.md) | Process lifecycle and output streaming. |
| 8 | [{Tool}Runner](uml-class-ToolRunner.md) | Tool-specific command construction. ClaudeRunner adds API-retry logic plus a per-invocation timeout (`waitFor(maxClaudeSeconds, SECONDS)` + `destroyForcibly()` → sentinel exit code) that surfaces to the phase. |
| 9 | [DarmokMojoLog](uml-class-DarmokMojoLog.md) | Structured logging to console and dated log files. |

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
