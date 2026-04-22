# DarmokMojo{DataFileType}

**Directory**: `src/main/java/org/farhan/mbt/maven`

**Extends**: DarmokMojoFile

Concrete `DarmokMojoFile` subclass for one storage format. `DarmokMojoLog` decorates Maven's `Log` interface and writes timestamped, categorised lines to disk (parses back into `LogEntry(level, category, content)` records; filters rows by Level/Category/Content from the keyMap). `DarmokMojoMetrics` writes one CSV row per scenario (parses back into column-keyed maps; one row per keyMap reference, no filtering — the keyMap dispatches to a column accessor downstream).

## DarmokMojo{DataFileType}

**Desc**: Public constructor. Each subclass has its own ctor shape: `DarmokMojoLog` has a write ctor `(Log delegate, String category, Path logFile)` plus a read-only ctor `(Path logFile)`; `DarmokMojoMetrics` has a single ctor `(Path file)` that handles both write and read.

**Rule**: SOME constructor matches DarmokMojo{DataFileType} pattern.
 - **Name**: `^DarmokMojo{DataFileType}$`
 - **Modifier**: `^public$`

**Examples**:
 - `public DarmokMojoLog(Log delegate, String category, Path logFile)`
 - `public DarmokMojoLog(Path logFile)` (read-only)
 - `public DarmokMojoMetrics(Path file)`

## matchAndGet{Field}

**Desc**: Inherits the `ensureMatched` template from `DarmokMojoFile`. Each call advances the cursor on a new keyMap (identity-cached) and returns the named field of the matched row. `DarmokMojoLog` exposes Level/Category/Content from a `LogEntry` record; `DarmokMojoMetrics` exposes the eight CSV columns from the row map. The `{Field}` value set is documented under `uml-overview.md`.

**Rule**: SOME method names follow matchAndGet{Field} pattern.
 - **Name**: `^matchAndGet{Field}$`
 - **Return**: `^String$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String matchAndGetLevel(HashMap<String, String> keyMap)` (DarmokMojoLog)
 - `public String matchAndGetCategory(HashMap<String, String> keyMap)` (DarmokMojoLog)
 - `public String matchAndGetContent(HashMap<String, String> keyMap)` (DarmokMojoLog)
 - `public String matchAndGetTimestamp(HashMap<String, String> keyMap)` (DarmokMojoMetrics)
 - `public String matchAndGetGitBranch(HashMap<String, String> keyMap)` (DarmokMojoMetrics)
 - `public String matchAndGetCommit(HashMap<String, String> keyMap)` (DarmokMojoMetrics)
 - `public String matchAndGetScenario(HashMap<String, String> keyMap)` (DarmokMojoMetrics)
 - `public String matchAndGetPhaseRedMs(HashMap<String, String> keyMap)` (DarmokMojoMetrics)
 - `public String matchAndGetPhaseGreenMs(HashMap<String, String> keyMap)` (DarmokMojoMetrics)
 - `public String matchAndGetPhaseRefactorMs(HashMap<String, String> keyMap)` (DarmokMojoMetrics)
 - `public String matchAndGetPhaseTotalMs(HashMap<String, String> keyMap)` (DarmokMojoMetrics)

## parse

**Desc**: Overrides the abstract `DarmokMojoFile.parse(Path)`. `DarmokMojoLog` parses each line via the `LINE_PATTERN` regex into `LogEntry` records. `DarmokMojoMetrics` parses the first line as CSV headers and each subsequent line into a column-keyed `LinkedHashMap<String, String>`.

**Rule**: SOME method names follow parse pattern.
 - **Name**: `^parse$`
 - **Return**: `^List<\w[\w<>,\s]*>$`
 - **Parameters**: `^\(Path\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected List<LogEntry> parse(Path logFile)` (DarmokMojoLog)
 - `protected List<Map<String, String>> parse(Path file)` (DarmokMojoMetrics)

## findNext

**Desc**: Overrides the abstract `DarmokMojoFile.findNext(HashMap)`. `DarmokMojoLog` filters rows by `Level`/`Category`/`Content` from the keyMap, advancing past non-matching lines. `DarmokMojoMetrics` returns the row at the cursor and increments — keyMap is consumed by the column accessor, not by row selection.

**Rule**: SOME method names follow findNext pattern.
 - **Name**: `^findNext$`
 - **Return**: `^\w[\w<>,\s]*$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected LogEntry findNext(HashMap<String, String> keyMap)` (DarmokMojoLog)
 - `protected Map<String, String> findNext(HashMap<String, String> keyMap)` (DarmokMojoMetrics)

## append

**Desc**: `DarmokMojoMetrics`-only. Appends one CSV row from a `DarmokMojoState`. Writes the header on the first call if the file does not yet exist. Pulls `gitBranch`, `commit`, `scenarioName`, per-phase durations (via `state.getDuration(Phase.X)`), and `totalDurationMs` from `state`. Scenario names containing commas, quotes, or newlines are CSV-escaped.

**Rule**: SOME method name follows append pattern.
 - **Name**: `^append$`
 - **Return**: `^void$`
 - **Parameters**: `^\(DarmokMojoState\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void append(DarmokMojoState state)` (DarmokMojoMetrics)

## findDatedLog

**Desc**: `DarmokMojoLog`-only static helper. Finds a dated log file in a directory by prefix; returns the first matching file or a default path.

**Rule**: SOME method name follows findDatedLog pattern.
 - **Name**: `^findDatedLog$`
 - **Return**: `^Path$`
 - **Parameters**: `^\(Path\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^public\s+static$`

**Examples**:
 - `public static Path findDatedLog(Path logDir, String prefix)` (DarmokMojoLog)

## is{LogLevel}Enabled

**Desc**: `DarmokMojoLog`-only. Delegates to the wrapped `Log` instance to check if the level is enabled. Implements the Maven `Log` interface.

**Rule**: SOME method names follow is{LogLevel}Enabled pattern.
 - **Name**: `^is{LogLevel}Enabled$`
 - **Return**: `^boolean$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public boolean isDebugEnabled()` (DarmokMojoLog)
 - `public boolean isInfoEnabled()` (DarmokMojoLog)
 - `public boolean isWarnEnabled()` (DarmokMojoLog)
 - `public boolean isErrorEnabled()` (DarmokMojoLog)

## {logLevel}(CharSequence)

**Desc**: `DarmokMojoLog`-only. Delegates to the wrapped `Log` and writes a timestamped line to the log file. Implements the Maven `Log` interface.

**Rule**: SOME method names follow {logLevel} pattern with CharSequence parameter.
 - **Name**: `^{logLevel}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(CharSequence\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void debug(CharSequence content)` (DarmokMojoLog)
 - `public void info(CharSequence content)` (DarmokMojoLog)
 - `public void warn(CharSequence content)` (DarmokMojoLog)
 - `public void error(CharSequence content)` (DarmokMojoLog)

## {logLevel}(CharSequence, Throwable)

**Desc**: `DarmokMojoLog`-only. Delegates to the wrapped `Log` and writes a timestamped line plus stack trace to the log file.

**Rule**: SOME method names follow {logLevel} pattern with CharSequence and Throwable parameters.
 - **Name**: `^{logLevel}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(CharSequence\s+\w+,\s*Throwable\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void debug(CharSequence content, Throwable error)` (DarmokMojoLog)
 - `public void info(CharSequence content, Throwable error)` (DarmokMojoLog)
 - `public void warn(CharSequence content, Throwable error)` (DarmokMojoLog)
 - `public void error(CharSequence content, Throwable error)` (DarmokMojoLog)

## {logLevel}(Throwable)

**Desc**: `DarmokMojoLog`-only. Delegates to the wrapped `Log` and writes the stack trace to the log file.

**Rule**: SOME method names follow {logLevel} pattern with Throwable parameter.
 - **Name**: `^{logLevel}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(Throwable\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void debug(Throwable error)` (DarmokMojoLog)
 - `public void info(Throwable error)` (DarmokMojoLog)
 - `public void warn(Throwable error)` (DarmokMojoLog)
 - `public void error(Throwable error)` (DarmokMojoLog)

## close

**Desc**: `DarmokMojoLog`-only. Closes the underlying `PrintWriter`. Implements `Closeable`.

**Rule**: SOME method name follows close pattern.
 - **Name**: `^close$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void close()` (DarmokMojoLog)
