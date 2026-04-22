# DarmokMojoFile

**Directory**: `src/main/java/org/farhan/mbt/maven`

Abstract base for files that Darmok writes during a run and tests inspect row-by-row. Owns the file path, lazy parse, cursor, and identity-cached `keyMap` matching template. Concrete `DarmokMojo{DataFileType}` subclasses define the row record type `R`, parse format, and `findNext` semantics. Cannot be instantiated directly.

## DarmokMojoFile

**Desc**: Constructor that captures the target file path. No I/O happens at construction; `parse` runs lazily on the first `ensureMatched` call.

**Rule**: ONE constructor matches DarmokMojoFile pattern.
 - **Name**: `^DarmokMojoFile$`
 - **Parameters**: `^\(Path\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected DarmokMojoFile(Path file)`

## file

**Desc**: Final target file path supplied at construction.

**Rule**: ONE attribute matches file pattern.
 - **Name**: `^file$`
 - **Return**: `^Path$`
 - **Modifier**: `^protected\s+final$`

**Examples**:
 - `protected final Path file`

## getFile

**Desc**: Returns the target file path. Inherited by every subclass — there is no per-subclass `getLogFile`/`getMetricsFile` accessor.

**Rule**: ONE method name follows getFile pattern.
 - **Name**: `^getFile$`
 - **Return**: `^Path$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public Path getFile()`

## ensureMatched

**Desc**: Read-side template. Lazy-parses `file` on the first call. If the supplied `keyMap` is the same identity as the previous call, returns without advancing (so consecutive `matchAndGet{Field}(km)` calls inside a single Cucumber row read from the same record). Otherwise calls the abstract `findNext(keyMap)` to advance the cursor and store the result in `lastMatch` for the subclass's accessors.

**Rule**: ONE method name follows ensureMatched pattern.
 - **Name**: `^ensureMatched$`
 - **Return**: `^void$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^protected\s+final$`

**Examples**:
 - `protected final void ensureMatched(HashMap<String, String> keyMap)`

## parse

**Desc**: Reads `file` and returns the list of typed row records. Each subclass defines its own format — `DarmokMojoLog` parses timestamped log lines into `LogEntry` records; `DarmokMojoMetrics` parses CSV rows into column-keyed maps.

**Rule**: ONE method name follows parse pattern.
 - **Name**: `^parse$`
 - **Return**: `^List<R>$`
 - **Parameters**: `^\(Path\s+\w+\)$`
 - **Modifier**: `^protected\s+abstract$`

**Examples**:
 - `protected abstract List<R> parse(Path file)`

## findNext

**Desc**: Returns the next row that satisfies the `keyMap`, advancing the cursor as it scans. `DarmokMojoLog` filters by Level/Category/Content (skipping non-matching lines); `DarmokMojoMetrics` simply returns the row at the cursor and increments (one row per call).

**Rule**: ONE method name follows findNext pattern.
 - **Name**: `^findNext$`
 - **Return**: `^R$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^protected\s+abstract$`

**Examples**:
 - `protected abstract R findNext(HashMap<String, String> keyMap)`

## lastMatch

**Desc**: Most recent row returned by `findNext`. Subclass accessors (`matchAndGet{Field}`) read from this after calling `ensureMatched`.

**Rule**: ONE attribute matches lastMatch pattern.
 - **Name**: `^lastMatch$`
 - **Return**: `^R$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected R lastMatch`

## cursor, advanceCursor, entries

**Desc**: Cursor accessors used by subclass `findNext` implementations. `cursor()` returns the current zero-based index into `entries()`; `advanceCursor()` increments it; `entries()` exposes the parsed list.

**Rule**: SOME method names follow cursor pattern.
 - **Name**: `^(cursor|advanceCursor|entries)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected int cursor()`
 - `protected void advanceCursor()`
 - `protected List<R> entries()`
