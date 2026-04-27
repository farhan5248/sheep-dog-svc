# {Tool}Runner

**Directory**: `src/main/java/org/farhan/mbt/maven`

**Extends**: ProcessRunner

**Implements**: {Tool}

Concrete subclasses of `ProcessRunner` that prepend a tool-specific executable to each command. Each subclass implements its corresponding {Tool} interface (`Git`, `Maven`, `Claude`) so test fakes can substitute for the production runner without subclassing it. This file describes the **subclass layer** (per-tool command construction); the parent file `uml-class-ProcessRunner.md` describes the **base-class layer** (process I/O lifecycle). Three subclasses today: `GitRunner`, `MavenRunner`, `ClaudeRunner`. Two of them (`Maven`, `Claude`) are platform-aware via the inherited `isWindows()` helper.

## {Tool}Runner

**Desc**: Constructs a runner by passing the Log to the ProcessRunner superclass. ClaudeRunner additionally accepts model, maxClaudeSeconds, session-id flag, and UUID supplier parameters. Retry/timeout-loop parameters live on the orchestrator (`DarmokMojo.runClaudeWithRetry` / `RgrPhase.runClaudeWithRetry`), not on the runner itself.

**Rule**: SOME constructor matches {Tool}Runner pattern.
 - **Name**: `^{Tool}Runner$`
 - **Parameters**: `^\(Log\s+\w+(,\s*(String|int|boolean|Supplier<String>)\s+\w+)*\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public GitRunner(Log log)`
 - `public MavenRunner(Log log)`
 - `public ClaudeRunner(Log log, String model, int maxClaudeSeconds, boolean sessionIdEnabled, Supplier<String> uuidSupplier)`

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

**Desc**: ClaudeRunner-only. Single-invocation primitive — runs the claude subprocess once and returns its exit code. Output lines are captured into the caller-supplied `outputLines` list so the orchestrator can decide whether the failure is retryable. The invocation is bounded by `maxClaudeSeconds` via a two-phase timeout — `waitFor(maxClaudeSeconds, SECONDS)` on the process handle followed by `readerThread.join(maxClaudeSeconds * 1000L)` on the stdout-reader thread. Hitting either bound triggers `destroyForcibly()` on the subprocess and returns the sentinel `TIMEOUT_EXIT_CODE` so the calling phase can enter timeout recovery. The reader-side bound covers the Windows failure mode (issue 290) where `claude.cmd` parent exits cleanly but a grandchild `node` keeps the stdout pipe open silently — without it the main thread would sit in `readerThread.join()` past the budget. The retry/wait-and-resume loop is owned by callers (`DarmokMojo.runClaudeWithRetry`); the runner stays single-invocation. GitRunner and MavenRunner inherit `ProcessRunner.run(String, String...)` and `ProcessRunner.run(String, Path, String...)` unchanged.

**Rule**: SOME method names follow run pattern.
 - **Name**: `^run$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*List<String>\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public int run(String workingDirectory, List<String> outputLines, String... args)` (ClaudeRunner — single-invocation)

## resume

**Desc**: ClaudeRunner-only. Single-invocation `claude --resume <message>` that nudges the latest claude session forward — used by GreenPhase and RefactorPhase both when `mvn clean verify` fails (`mvn clean verify failures should be fixed`) and when the initial claude call times out (`pls continue`, inside the timeout-recovery loop). Bounded by `maxClaudeSeconds`; output lines are captured into `outputLines` so the orchestrator can drive retries on retryable error patterns. Phase-side verify/timeout/allowlist loops control coarse retries; the orchestrator handles API-error retries.

**Rule**: SOME method names follow resume pattern.
 - **Name**: `^resume$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*List<String>\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public int resume(String workingDirectory, List<String> outputLines, String message)` (ClaudeRunner)

## getSessionId

**Desc**: ClaudeRunner-only. Public accessor returning the UUID captured on the runner's first `--print` call (or `null` until the first call). Used by `RefactorPhase.prepareSession` (issue #287) so refactor can inherit green's session: `refactorPhase.claude.setSessionId(greenPhase.claude.getSessionId())`.

**Rule**: SOME method names follow getSessionId pattern.
 - **Name**: `^getSessionId$`
 - **Return**: `^String$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String getSessionId()` (ClaudeRunner)

## setSessionId

**Desc**: ClaudeRunner-only. Public mutator that overrides the captured UUID before the next claude call so a sibling phase can inherit an existing session rather than generate a fresh one. The next `claude --resume` invocation will carry the overridden value. Used by `RefactorPhase.prepareSession` (issue #287) when `refactorSessionMode=continue` to copy green's UUID into refactor's runner.

**Rule**: SOME method names follow setSessionId pattern.
 - **Name**: `^setSessionId$`
 - **Return**: `^void$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void setSessionId(String sessionId)` (ClaudeRunner)

## getCurrentCommit

**Desc**: GitRunner convenience method that captures the current HEAD commit SHA via `git rev-parse HEAD`. Used by DarmokMojo to tag per-scenario metrics rows with the commit responsible for that cycle-time point.

**Rule**: SOME method names follow getCurrentCommit pattern.
 - **Name**: `^getCurrentCommit$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String getCurrentCommit(String workingDirectory)` (GitRunner)

## getCurrentBranch

**Desc**: GitRunner convenience method that captures the current branch name via `git rev-parse --abbrev-ref HEAD`. Used by DarmokMojo's init-time check to verify the declared `gitBranch` parameter matches the actual git HEAD. A detached HEAD returns the literal string `HEAD`, which the caller treats as a mismatch.

**Rule**: SOME method names follow getCurrentBranch pattern.
 - **Name**: `^getCurrentBranch$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String getCurrentBranch(String workingDirectory)` (GitRunner)

## captureOutput

**Desc**: GitRunner convenience method exposing `ProcessRunner.capture` through the `Git` interface so test fakes can substitute. Returns trimmed stdout; throws on non-zero exit.

**Rule**: SOME method names follow captureOutput pattern.
 - **Name**: `^captureOutput$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String captureOutput(String workingDirectory, String... args)` (GitRunner)
