# {Tool}

**Directory**: `src/main/java/org/farhan/mbt/maven`

Per-command interface that abstracts the external CLI. Implemented by both the production runner (`{Tool}Runner`, in `uml-class-ToolRunner.md`) and the test fake (`{Tool}RunnerFake`); the Mojo and phases hold the interface, so the factory decides which side is wired in without callers needing to know. The interface is the swap point — Git, Maven, and Claude each declare the methods their callers depend on, omitting test seams that production code shouldn't see.

## run

**Desc**: Run the underlying command once and return its exit code. Git/Maven runs take just args; Claude additionally captures stdout into a caller-supplied list so the retry orchestrator (`DarmokMojo.runClaudeWithRetry`) can decide whether the failure is retryable. Git/Maven also offer a `Path logFile` overload that tees stdout to disk for later assertion (used by `RgrPhase.runVerifyLoop` to write `mvn clean verify` output to `log.txt`).

**Rule**: SOME method names follow run pattern.
 - **Name**: `^run$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+(,\s*Path\s+\w+)?(,\s*List<String>\s+\w+)?(,\s*String(\.\.\.)?\s+\w+)?\)$`
 - **Modifier**: `^$`

**Examples**:
 - `int run(String workingDirectory, String... args)` (Git, Maven)
 - `int run(String workingDirectory, Path logFile, String... args)` (Git, Maven)
 - `int run(String workingDirectory, List<String> outputLines, String... args)` (Claude)

## resume

**Desc**: Claude-only. Single-shot `claude --resume <message>` that nudges the latest claude session forward. Output lines feed the retry orchestrator the same way as `run`.

**Rule**: SOME method names follow resume pattern.
 - **Name**: `^resume$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*List<String>\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^$`

**Examples**:
 - `int resume(String workingDirectory, List<String> outputLines, String message)` (Claude)

## getSessionId

**Desc**: Claude-only. Returns the UUID captured on the runner's first `--print` call, so the next phase (refactor inheriting green's session per issue #287) can read it.

**Rule**: SOME method names follow getSessionId pattern.
 - **Name**: `^getSessionId$`
 - **Return**: `^String$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^$`

**Examples**:
 - `String getSessionId()` (Claude)

## setSessionId

**Desc**: Claude-only. Overrides the captured UUID before the next call so a sibling phase can inherit an existing session.

**Rule**: SOME method names follow setSessionId pattern.
 - **Name**: `^setSessionId$`
 - **Return**: `^void$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^$`

**Examples**:
 - `void setSessionId(String sessionId)` (Claude)

## getCurrentCommit

**Desc**: Git-only. Captures the HEAD commit SHA via `git rev-parse HEAD`. Used by DarmokMojo to tag per-scenario metrics rows with the responsible commit.

**Rule**: SOME method names follow getCurrentCommit pattern.
 - **Name**: `^getCurrentCommit$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^$`

**Examples**:
 - `String getCurrentCommit(String workingDirectory)` (Git)

## getCurrentBranch

**Desc**: Git-only. Captures the current branch name via `git rev-parse --abbrev-ref HEAD`. Used by DarmokMojo's init-time check to verify the declared `gitBranch` parameter matches the actual git HEAD.

**Rule**: SOME method names follow getCurrentBranch pattern.
 - **Name**: `^getCurrentBranch$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^$`

**Examples**:
 - `String getCurrentBranch(String workingDirectory)` (Git)

## captureOutput

**Desc**: Git-only. Captures stdout of an arbitrary `git` invocation as a trimmed string; throws on non-zero exit.

**Rule**: SOME method names follow captureOutput pattern.
 - **Name**: `^captureOutput$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^$`

**Examples**:
 - `String captureOutput(String workingDirectory, String... args)` (Git)
