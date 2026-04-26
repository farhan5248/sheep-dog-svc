# ProcessRunner

**Directory**: `src/main/java/org/farhan/mbt/maven`

Abstract base class that owns the external-process lifecycle — stream stdout to log, capture stdout to string, return exit code. The concrete `{Tool}Runner` subclasses (see `uml-class-ToolRunner.md`) provide the command-building. This file describes the **base-class layer** (process I/O); the sibling file describes the **subclass layer** (per-tool command construction). Cannot be instantiated directly.

## ProcessRunner

**Desc**: Constructs a ProcessRunner with a Maven Log instance for output streaming.

**Rule**: ONE constructor matches ProcessRunner pattern.
 - **Name**: `^ProcessRunner$`
 - **Parameters**: `^\(Log\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public ProcessRunner(Log log)`

## ProcessRunner (test seam)

**Desc**: Test-only constructor that accepts a ProcessStarter test seam so tests can inject a FakeProcessStarter without spawning real subprocesses.

**Rule**: ONE constructor matches ProcessRunner test seam pattern.
 - **Name**: `^ProcessRunner$`
 - **Parameters**: `^\(Log\s+\w+,\s*ProcessStarter\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public ProcessRunner(Log log, ProcessStarter starter)`

## buildCommand

**Desc**: Constructs the command list from arguments. Subclasses override to prepend tool-specific executable.

**Rule**: ONE method name follows buildCommand pattern.
 - **Name**: `^buildCommand$`
 - **Return**: `^List<String>$`
 - **Parameters**: `^\(String\.\.\.\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected List<String> buildCommand(String... args)`

## run

**Desc**: Builds command, starts process via ProcessStarter seam, streams output lines to log, and returns exit code.

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public int run(String workingDirectory, String... args)`

## run (with file output)

**Desc**: Like `run`, but additionally tees each stdout line to `logFile`. When `logFile` is null, behavior is identical to `run`. Used by Red Phase's `mvn test` and Phase Verification's `mvn clean verify` so the green-phase claude prompt can read the most recent mvn failure detail directly from the file (issue 325).

**Rule**: ONE method name follows run-with-file pattern.
 - **Name**: `^run$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*Path\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public int run(String workingDirectory, Path logFile, String... args)`

## capture

**Desc**: Builds the command, starts the process via the ProcessStarter seam, reads stdout to a string, and returns the trimmed output. Throws IOException on non-zero exit. Used by runners that need the subprocess's output as a value (e.g. GitRunner.getCurrentCommit) rather than streamed to the log.

**Rule**: ONE method name follows capture pattern.
 - **Name**: `^capture$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected String capture(String workingDirectory, String... args)`

## getLog

**Desc**: Returns the Maven Log instance for subclass use.

**Rule**: ONE method name follows getLog pattern.
 - **Name**: `^getLog$`
 - **Return**: `^Log$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected Log getLog()`

## isWindows

**Desc**: Returns true if the OS is Windows. Used by subclasses to select command extensions (.cmd vs bare).

**Rule**: ONE method name follows isWindows pattern.
 - **Name**: `^isWindows$`
 - **Return**: `^boolean$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected\s+static$`

**Examples**:
 - `protected static boolean isWindows()`
