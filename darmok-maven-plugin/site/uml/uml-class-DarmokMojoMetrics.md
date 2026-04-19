# DarmokMojoMetrics

**Directory**: `src/main/java/org/farhan/mbt/maven`

Per-scenario metrics emitter. Writes cycle-time measurements to a CSV file and reads them back for row-level test verification. Storage format is CSV today; future revisions may push to a central time-series store without changing the class boundary.

## DarmokMojoMetrics

**Desc**: Constructs a DarmokMojoMetrics bound to a target file path. No I/O happens at construction; the file and header row are created lazily on the first append.

**Rule**: ONE constructor matches DarmokMojoMetrics pattern.
 - **Name**: `^DarmokMojoMetrics$`
 - **Parameters**: `^\(Path\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public DarmokMojoMetrics(Path file)`

## getFile

**Desc**: Returns the target file path for this metrics instance.

**Rule**: ONE method name follows getFile pattern.
 - **Name**: `^getFile$`
 - **Return**: `^Path$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public Path getFile()`

## append

**Desc**: Appends one data row to the CSV. Writes the header on the first call if the file does not yet exist. Scenario names containing commas, quotes, or newlines are CSV-escaped.

**Rule**: ONE method name follows append pattern.
 - **Name**: `^append$`
 - **Return**: `^void$`
 - **Parameters**: `^\(String\s+\w+,\s*String\s+\w+,\s*long\s+\w+,\s*long\s+\w+,\s*long\s+\w+,\s*long\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void append(String commit, String scenario, long redMs, long greenMs, long refactorMs, long totalMs)`

## matchAndGet{MetricColumn}

**Desc**: Parses the CSV file and advances a sequential cursor — one row per new keyMap reference. Returns the named column value for the current row, or null if the cursor is past the last row.

**Rule**: SOME method names follow matchAndGet{MetricColumn} pattern.
 - **Name**: `^matchAndGet{MetricColumn}$`
 - **Return**: `^String$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String matchAndGetTimestamp(HashMap<String, String> keyMap)`
 - `public String matchAndGetCommit(HashMap<String, String> keyMap)`
 - `public String matchAndGetScenario(HashMap<String, String> keyMap)`
 - `public String matchAndGetPhaseRedMs(HashMap<String, String> keyMap)`
 - `public String matchAndGetPhaseGreenMs(HashMap<String, String> keyMap)`
 - `public String matchAndGetPhaseRefactorMs(HashMap<String, String> keyMap)`
 - `public String matchAndGetPhaseTotalMs(HashMap<String, String> keyMap)`
