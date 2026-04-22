# DarmokMojoState

**Directory**: `src/main/java/org/farhan/mbt/maven`

Mutable per-scenario state object threaded through all three RGR phases. Carries the scenario inputs (scenarioName, gitBranch, tag), accumulates per-phase durations through `setDuration(Phase, long)`, and exposes the current `exitCode` so the orchestrator can branch between phases. The orchestrator populates `commit` and `totalDurationMs` after the last phase commits. Replaces the old `PhaseResult` record. Also exposes a static `formatDuration` helper used for log lines.

## DarmokMojoState

**Desc**: Constructor that captures the scenario inputs. All other fields default to zero/null and are populated by phases or the orchestrator.

**Rule**: ONE constructor matches DarmokMojoState pattern.
 - **Name**: `^DarmokMojoState$`
 - **Parameters**: `^\(String\s+\w+,\s*String\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public DarmokMojoState(String scenarioName, String gitBranch, String tag)`

## scenarioName, gitBranch, tag

**Desc**: Final scenario inputs supplied at construction.

**Rule**: SOME attribute matches inputs pattern.
 - **Name**: `^(scenarioName|gitBranch|tag)$`
 - **Return**: `^String$`
 - **Modifier**: `^(default|package-private)\s+final$`

**Examples**:
 - `final String scenarioName`
 - `final String gitBranch`
 - `final String tag`

## exitCode, totalDurationMs, commit

**Desc**: Mutable orchestration fields. `exitCode` is set by each phase before it returns; the Mojo branches on it. `commit` and `totalDurationMs` are set by the Mojo after the last phase commits.

**Rule**: SOME attribute matches orchestration pattern.
 - **Name**: `^(exitCode|totalDurationMs|commit)$`
 - **Return**: `^(int|long|String)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `int exitCode`
 - `long totalDurationMs`
 - `String commit`

## setDuration

**Desc**: Records the elapsed milliseconds for one phase into the internal `EnumMap<Phase, Long>`.

**Rule**: ONE method name follows setDuration pattern.
 - **Name**: `^setDuration$`
 - **Return**: `^void$`
 - **Parameters**: `^\(Phase\s+\w+,\s*long\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void setDuration(Phase phase, long ms)`

## getDuration

**Desc**: Returns the elapsed milliseconds for one phase, or 0 if the phase hasn't run.

**Rule**: ONE method name follows getDuration pattern.
 - **Name**: `^getDuration$`
 - **Return**: `^long$`
 - **Parameters**: `^\(Phase\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public long getDuration(Phase phase)`

## formatDuration

**Desc**: Formats milliseconds as HH:MM:SS for log lines.

**Rule**: ONE method name follows formatDuration pattern.
 - **Name**: `^formatDuration$`
 - **Return**: `^String$`
 - **Parameters**: `^\(long\s+\w+\)$`
 - **Modifier**: `^public\s+static$`

**Examples**:
 - `public static String formatDuration(long millis)`
