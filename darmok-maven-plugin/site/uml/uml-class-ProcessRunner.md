# ProcessRunner

**Directory**: `src/main/java/org/farhan/mbt/maven`

Base class for executing external processes with output streaming and logging. Separates process lifecycle management from command-specific argument construction.

## ProcessRunner

**Desc**: Constructs a ProcessRunner with a Maven Log instance for output streaming.

**Rule**: ONE constructor matches ProcessRunner pattern.
 - **Name**: `^ProcessRunner$`
 - **Parameters**: `^\(Log\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public ProcessRunner(Log log)`

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
