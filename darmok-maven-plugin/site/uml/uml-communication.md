# UML Communication Patterns

These patterns describe the communication sequences between classes for key use cases. Each pattern represents a unique combination of collaborating classes and their public method calls. TestObject, TestSteps, and generated step definitions are infrastructure and not included as spec'd classes.

## Execute Goal

**Regex**: `^Execute Goal$`

This pattern covers the full lifecycle of a Darmok Maven goal invocation: initialization, cleanup, scenario iteration with RGR phases, external process execution, and structured logging.

| # | Class | Role |
|---|---|---|
| 1 | [{Goal}Mojo](uml-class-GoalMojo.md) | Entry point. Calls inherited lifecycle and scenario methods. |
| 2 | [DarmokMojo](uml-class-DarmokMojo.md) | Lifecycle, scenario iteration, and RGR phase orchestration. |
| 3 | [ProcessRunner](uml-class-ProcessRunner.md) | Process lifecycle and output streaming. |
| 4 | [{Tool}Runner](uml-class-ToolRunner.md) | Tool-specific command construction. ClaudeRunner adds retry logic. |
| 5 | [MojoLog](uml-class-MojoLog.md) | Structured logging to console and dated log files. |

### Sequence

1. **{Goal}Mojo**.execute()
2. **DarmokMojo**.init()
3. **DarmokMojo**.runCleanUp()
4. **DarmokMojo**.getNextScenario() — loop until null
5. **DarmokMojo**.processScenario(entry)
6. **{Tool}Runner**.run(workingDirectory, args) — called for maven, git, and claude across RGR phases
7. **MojoLog**.info(...) / .debug(...) / .warn(...) — throughout
8. **DarmokMojo**.cleanup()
