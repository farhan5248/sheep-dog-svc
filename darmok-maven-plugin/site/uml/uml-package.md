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

**Desc**: Base class for executing external processes with output streaming and logging. Separates process lifecycle management from command-specific argument construction.

**Rule**: ONE class matches ProcessRunner pattern

**Regex**: `^ProcessRunner$`
 - `ProcessRunner`

### {Tool}Runner

**Desc**: Specialized process runner that extends ProcessRunner with tool-specific command construction. Separates tool-specific command building from shared process execution logic.

**Rule**: SOME class matches {Tool}Runner pattern

**Regex**: `^{Tool}Runner$`
 - `ClaudeRunner`
 - `GitRunner`
 - `MavenRunner`

### {Tool}RunnerFactory

**Desc**: Functional interface (SAM) for constructing a {Tool}Runner. Production wires the runner's public constructor via method reference; tests substitute a lambda that binds a FakeProcessStarter to the runner's test-seam constructor.

**Rule**: SOME class matches {Tool}RunnerFactory pattern

**Regex**: `^{Tool}RunnerFactory$`
 - `ClaudeRunnerFactory`
 - `GitRunnerFactory`
 - `MavenRunnerFactory`

### DarmokMojoLog

**Desc**: Logging decorator that implements Maven's Log interface, delegating to a wrapped Log while simultaneously writing timestamped, categorized entries to a file. Separates structured file logging from Maven's console logging.

**Rule**: ONE class matches DarmokMojoLog pattern

**Regex**: `^DarmokMojoLog$`
 - `DarmokMojoLog`

### DarmokMojoMetrics

**Desc**: Per-scenario metrics emitter. Writes cycle-time measurements (Red / Green / Refactor / Total durations) tagged with scenario name, git commit SHA, and configured git branch to a CSV file for downstream SPC analysis, and reads them back for test verification. Storage format is CSV today; future revisions may push to a central time-series store without changing the class boundary.

**Rule**: ONE class matches DarmokMojoMetrics pattern

**Regex**: `^DarmokMojoMetrics$`
 - `DarmokMojoMetrics`

### RedPhase

**Desc**: RGR Red phase — generates failing test artifacts by running the sheep-dog-svc-maven-plugin asciidoctor-to-uml + uml-to-cucumber-guice goals, then executing mvn test to verify the generated tests fail.

**Rule**: ONE class matches RedPhase pattern

**Regex**: `^RedPhase$`
 - `RedPhase`

### GreenPhase

**Desc**: RGR Green phase — invokes the Claude /rgr-green skill to implement code that makes the red-phase tests pass, then runs deterministic sub-steps (timeout recovery via `mvn clean install` + `claude --resume "pls continue"`, then a verify loop via `mvn clean verify` + `claude --resume "mvn clean verify failures should be fixed"`) that keep the resulting code buildable and test-passing.

**Rule**: ONE class matches GreenPhase pattern

**Regex**: `^GreenPhase$`
 - `GreenPhase`

### RefactorPhase

**Desc**: RGR Refactor phase — invokes the Claude `/rgr-refactor forward` skill to refactor the freshly-green code, wrapped in the same timeout-recovery + verify loops as GreenPhase.

**Rule**: ONE class matches RefactorPhase pattern

**Regex**: `^RefactorPhase$`
 - `RefactorPhase`

### PhaseResult

**Desc**: Value record capturing the exit code and elapsed duration of an RGR phase execution.

**Rule**: ONE class matches PhaseResult pattern

**Regex**: `^PhaseResult$`
 - `PhaseResult`

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

**Desc**: Spring context configuration and Cucumber lifecycle hooks for scenario glue. Creates fresh temp directories per scenario, stashes their paths in TestObject.properties, and wires a FakeProcess-based ProcessStarter so no real subprocesses are spawned.

**Rule**: ONE class matches TestConfig pattern

**Regex**: `^TestConfig$`
 - `TestConfig`
