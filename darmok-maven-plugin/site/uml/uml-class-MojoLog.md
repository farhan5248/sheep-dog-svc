# MojoLog

**Directory**: `src/main/java/org/farhan/mbt/maven`

Complete mojo log — writes timestamped, structured entries to file while delegating to Maven's console Log, and reads them back for verification. Implements Maven's Log interface.

## MojoLog

**Desc**: Constructs a MojoLog with a delegate Log, category name, and log file path. Creates parent directories if needed.

**Rule**: ONE constructor matches MojoLog pattern.
 - **Name**: `^MojoLog$`
 - **Parameters**: `^\(Log\s+\w+,\s*String\s+\w+,\s*Path\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public MojoLog(Log delegate, String category, Path logFile)`

## MojoLog (read-only)

**Desc**: Constructs a read-only MojoLog for log inspection without writing.

**Rule**: ONE constructor matches MojoLog read-only pattern.
 - **Name**: `^MojoLog$`
 - **Parameters**: `^\(Path\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public MojoLog(Path logFile)`

## findDatedLog

**Desc**: Finds a dated log file in a directory by prefix. Returns the first matching file or a default path.

**Rule**: ONE method name follows findDatedLog pattern.
 - **Name**: `^findDatedLog$`
 - **Return**: `^Path$`
 - **Parameters**: `^\(Path\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^public\s+static$`

**Examples**:
 - `public static Path findDatedLog(Path logDir, String prefix)`

## is{LogLevel}Enabled

**Desc**: Delegates to the wrapped Log instance to check if the level is enabled.

**Rule**: SOME method names follow is{LogLevel}Enabled pattern.
 - **Name**: `^is{LogLevel}Enabled$`
 - **Return**: `^boolean$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public boolean isDebugEnabled()`
 - `public boolean isInfoEnabled()`
 - `public boolean isWarnEnabled()`
 - `public boolean isErrorEnabled()`

## {logLevel}(CharSequence)

**Desc**: Delegates to the wrapped Log and writes a timestamped line to the log file.

**Rule**: SOME method names follow {logLevel} pattern with CharSequence parameter.
 - **Name**: `^{logLevel}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(CharSequence\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void debug(CharSequence content)`
 - `public void info(CharSequence content)`
 - `public void warn(CharSequence content)`
 - `public void error(CharSequence content)`

## {logLevel}(CharSequence, Throwable)

**Desc**: Delegates to the wrapped Log and writes a timestamped line plus stack trace to the log file.

**Rule**: SOME method names follow {logLevel} pattern with CharSequence and Throwable parameters.
 - **Name**: `^{logLevel}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(CharSequence\s+\w+,\s*Throwable\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void debug(CharSequence content, Throwable error)`
 - `public void info(CharSequence content, Throwable error)`
 - `public void warn(CharSequence content, Throwable error)`
 - `public void error(CharSequence content, Throwable error)`

## {logLevel}(Throwable)

**Desc**: Delegates to the wrapped Log and writes the stack trace to the log file.

**Rule**: SOME method names follow {logLevel} pattern with Throwable parameter.
 - **Name**: `^{logLevel}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(Throwable\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void debug(Throwable error)`
 - `public void info(Throwable error)`
 - `public void warn(Throwable error)`
 - `public void error(Throwable error)`

## close

**Desc**: Closes the underlying PrintWriter.

**Rule**: ONE method name follows close pattern.
 - **Name**: `^close$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void close()`

## matchAndGet{Field}

**Desc**: Parses the log file and matches expected rows sequentially against actual log lines. Each call scans forward from the current cursor position. Returns the matched field value (Level, Category, or Content), or null if no match found.

**Rule**: SOME method names follow matchAndGet{Field} pattern.
 - **Name**: `^matchAndGet{Field}$`
 - **Return**: `^String$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String matchAndGetLevel(HashMap<String, String> keyMap)`
 - `public String matchAndGetCategory(HashMap<String, String> keyMap)`
 - `public String matchAndGetContent(HashMap<String, String> keyMap)`
