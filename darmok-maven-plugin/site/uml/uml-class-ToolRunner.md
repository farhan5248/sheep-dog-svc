# {Tool}Runner

**Directory**: `src/main/java/org/farhan/mbt/maven`

**Extends**: ProcessRunner

Specialized process runner that extends ProcessRunner with tool-specific command construction. Separates tool-specific command building from shared process execution logic.

## {Tool}Runner

**Desc**: Constructs a runner by passing the Log to the ProcessRunner superclass. ClaudeRunner additionally accepts model, retry, and wait parameters.

**Rule**: SOME constructor matches {Tool}Runner pattern.
 - **Name**: `^{Tool}Runner$`
 - **Parameters**: `^\(Log\s+\w+(,\s*(String|int)\s+\w+)*\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public GitRunner(Log log)`
 - `public MavenRunner(Log log)`
 - `public ClaudeRunner(Log log, String model, int maxRetries, int retryWaitSeconds)`

## buildCommand

**Desc**: Overrides ProcessRunner.buildCommand to prepend the tool-specific executable (e.g. git, mvn.cmd, claude.cmd) before the args. Platform-aware runners use isWindows() to select .cmd extensions.

**Rule**: ALL method names follow buildCommand pattern.
 - **Name**: `^buildCommand$`
 - **Return**: `^List<String>$`
 - **Parameters**: `^\(String\.\.\.\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected List<String> buildCommand(String... args)` (GitRunner — prepends "git")
 - `protected List<String> buildCommand(String... args)` (MavenRunner — prepends "mvn.cmd" or "mvn")
 - `protected List<String> buildCommand(String... args)` (ClaudeRunner — prepends "claude.cmd" or "claude" with --print and model flags)

## run

**Desc**: ClaudeRunner overrides run to add retry logic with configurable max retries and wait seconds. Retries on known API error patterns (500, 529, overloaded). GitRunner and MavenRunner inherit ProcessRunner.run unchanged.

**Rule**: SOME method names follow run pattern.
 - **Name**: `^run$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public int run(String workingDirectory, String... args)` (ClaudeRunner — with retry logic)
