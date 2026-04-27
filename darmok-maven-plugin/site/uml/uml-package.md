# UML Package Patterns

## src/main/java/org/farhan/mbt/maven

**Desc**: Maven plugin infrastructure for the Darmok RGR automation tool. Separates goal orchestration (Mojos) from external process execution (Runners) and structured logging, enabling independent testing and extension of each concern.

### DarmokMojo

**Desc**: Abstract base Mojo providing shared lifecycle, scenario iteration, RGR phase orchestration, AsciiDoc tag insertion, and file I/O helpers. Separates common workflow logic from goal-specific entry points.

**Rule**: ONE class matches DarmokMojo pattern

**Regex**: `^DarmokMojo$`
 - `DarmokMojo`

### {Goal}Mojo

**Desc**: Concrete Maven goal that extends DarmokMojo with a specific scenario generation strategy. Separates goal-specific entry point and generation logic from shared workflow orchestration.

**Rule**: SOME class matches {Goal}Mojo pattern

**Regex**: `^{Goal}Mojo$`
 - `GenFromComparisonMojo`
 - `GenFromExistingMojo`

### ProcessRunner

**Desc**: Abstract base class that owns the external-process lifecycle — stream stdout to log, capture stdout to string, return exit code. Concrete `{Tool}Runner` subclasses provide command-building. Cannot be instantiated directly.

**Rule**: ONE class matches ProcessRunner pattern

**Regex**: `^ProcessRunner$`
 - `ProcessRunner`

### {Tool}

**Desc**: Per-command interface that abstracts the external CLI. Implemented by both the production runner (`{Tool}Runner`) and the test fake (`{Tool}RunnerFake`); the Mojo and phases hold the interface, so the factory decides which side is wired in without callers needing to know.

**Rule**: SOME class matches {Tool} pattern

**Regex**: `^{Tool}$`
 - `Claude`
 - `Git`
 - `Maven`

### {Tool}Runner

**Desc**: Specialized process runner that extends ProcessRunner with tool-specific command construction and implements its `{Tool}` interface. Separates tool-specific command building from shared process execution logic.

**Rule**: SOME class matches {Tool}Runner pattern

**Regex**: `^{Tool}Runner$`
 - `ClaudeRunner`
 - `GitRunner`
 - `MavenRunner`

### {Tool}RunnerFactory

**Desc**: Functional interface (SAM) for constructing a `{Tool}` implementation. Production wires the runner's public constructor via method reference (`{Tool}Runner::new`); tests substitute a lambda that returns the matching test fake. Returning the interface type lets either side satisfy the Mojo without it knowing which.

**Rule**: SOME class matches {Tool}RunnerFactory pattern

**Regex**: `^{Tool}RunnerFactory$`
 - `ClaudeRunnerFactory`
 - `GitRunnerFactory`
 - `MavenRunnerFactory`

### DarmokMojoFile

**Desc**: Abstract base class for files that Darmok writes during a run and tests inspect row-by-row. Owns the file path, lazy parse, cursor, and identity-cached `keyMap` matching template. Concrete subclasses (`DarmokMojoLog`, `DarmokMojoMetrics`) define the row record type, parse format, and `findNext` semantics.

**Rule**: ONE class matches DarmokMojoFile pattern

**Regex**: `^DarmokMojoFile$`
 - `DarmokMojoFile`

### DarmokMojo{DataFileType}

**Desc**: Concrete `DarmokMojoFile` subclass for one storage format. `DarmokMojoLog` decorates Maven's `Log` interface and writes timestamped lines (parses back into `LogEntry` records, filters by Level/Category/Content). `DarmokMojoMetrics` writes one CSV row per scenario (parses back into column-keyed maps, one row per keyMap).

**Rule**: SOME class matches DarmokMojo{DataFileType} pattern

**Regex**: `^DarmokMojo{DataFileType}$`
 - `DarmokMojoLog`
 - `DarmokMojoMetrics`

### RgrPhase

**Desc**: Abstract base for the three RGR phases. Owns the public `run(DarmokMojoState)` template, the verify loop (`mvn clean verify` + `claude --resume "mvn clean verify failures should be fixed"`), and the timeout-recovery loop (`mvn clean install` + `claude --resume "pls continue"`). Concrete `{RgrPhase}Phase` subclasses provide the phase-specific work via `executeClaudeOrMaven` and declare whether they need verify via `requiresVerifyLoop`. Cannot be instantiated directly.

**Rule**: ONE class matches RgrPhase pattern

**Regex**: `^RgrPhase$`
 - `RgrPhase`

### {RgrPhase}Phase

**Desc**: Concrete RGR phase. `RedPhase` runs sheep-dog-svc-maven-plugin goals + `mvn test` (no claude, no verify loop). `GreenPhase` invokes `/rgr-green` then verifies. `RefactorPhase` invokes `/rgr-refactor forward` then verifies.

**Rule**: SOME class matches {RgrPhase}Phase pattern

**Regex**: `^{RgrPhase}Phase$`
 - `RedPhase`
 - `GreenPhase`
 - `RefactorPhase`

### Phase

**Desc**: Enum identifying which RGR phase a `DarmokMojoState.setDuration`/`getDuration` call refers to.

**Rule**: ONE class matches Phase pattern

**Regex**: `^Phase$`
 - `Phase`

### DarmokMojoState

**Desc**: Mutable per-scenario state object threaded through all three phases. Carries inputs (scenarioName, gitBranch, tag), accumulating phase durations (via `setDuration(Phase, long)`), the current `exitCode` for branching, and post-scenario fields (`commit`, `totalDurationMs`) populated by the orchestrator. Replaces the old `PhaseResult` record.

**Rule**: ONE class matches DarmokMojoState pattern

**Regex**: `^DarmokMojoState$`
 - `DarmokMojoState`

## src/test/java/org/farhan/impl

**Desc**: Test object implementations that bridge Cucumber step definitions to file system and Maven goal operations. Each impl class handles a specific test artifact type.

### {ObjectName}{ObjectType}Impl

**Desc**: Test object implementation that bridges Cucumber step definitions to file system or Maven goal operations. File types assert file state and content; Goal types configure and execute Mojo instances.

**Rule**: SOME class matches {ObjectName}{ObjectType}Impl pattern

**Regex**: `^{ObjectName}{ObjectType}Impl$`
 - `DarmokMojoLogFileImpl`
 - `DarmokRunnersLogFileImpl`
 - `GenFromComparisonGoalImpl`
 - `GenFromExistingGoalImpl`
 - `LoginHappyPathJavaFileImpl`
 - `LogoutHappyPathJavaFileImpl`
 - `ProcessDarmokAsciidocFileImpl`
 - `ScenariosListTxtFileImpl`

### TestConfig

**Desc**: Spring context configuration and Cucumber lifecycle hooks for scenario glue. Creates fresh temp directories per scenario and stashes their paths in TestObject.properties; the per-scenario impl methods seed the property bag with the per-command fakes (`ClaudeRunnerFake` / `MavenRunnerFake` / `GitRunnerFake`) which the Mojo's runner factories return so no real subprocesses are spawned.

**Rule**: ONE class matches TestConfig pattern

**Regex**: `^TestConfig$`
 - `TestConfig`
