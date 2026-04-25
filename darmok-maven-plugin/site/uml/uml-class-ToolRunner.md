# {Tool}Runner

**Directory**: `src/main/java/org/farhan/mbt/maven`

**Extends**: ProcessRunner

Concrete subclasses of `ProcessRunner` that prepend a tool-specific executable to each command. This file describes the **subclass layer** (per-tool command construction); the parent file `uml-class-ProcessRunner.md` describes the **base-class layer** (process I/O lifecycle). Three subclasses today: `GitRunner`, `MavenRunner`, `ClaudeRunner`. Two of them (`Maven`, `Claude`) are platform-aware via the inherited `isWindows()` helper.

## {Tool}Runner

**Desc**: Constructs a runner by passing the Log to the ProcessRunner superclass. ClaudeRunner additionally accepts model, retry, wait, session-id flag, and UUID supplier parameters (the latter two added in issue #311 to support `--session-id` on `--print` invocations).

**Rule**: SOME constructor matches {Tool}Runner pattern.
 - **Name**: `^{Tool}Runner$`
 - **Parameters**: `^\(Log\s+\w+(,\s*(String|int|boolean|Supplier<String>)\s+\w+)*\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public GitRunner(Log log)`
 - `public MavenRunner(Log log)`
 - `public ClaudeRunner(Log log, String model, int maxRetries, int retryWaitSeconds, int maxClaudeSeconds, boolean sessionIdEnabled, Supplier<String> uuidSupplier)`

## {Tool}Runner (test seam)

**Desc**: Test-only constructor variant that appends a ProcessStarter test seam parameter so tests can inject a FakeProcessStarter without spawning real subprocesses.

**Rule**: SOME constructors match {Tool}Runner test seam pattern.
 - **Name**: `^{Tool}Runner$`
 - **Parameters**: `^\(Log\s+\w+(,\s*(String|int|boolean|Supplier<String>)\s+\w+)*,\s*ProcessStarter\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public GitRunner(Log log, ProcessStarter starter)`
 - `public MavenRunner(Log log, ProcessStarter starter)`
 - `public ClaudeRunner(Log log, String model, int maxRetries, int retryWaitSeconds, int maxClaudeSeconds, boolean sessionIdEnabled, Supplier<String> uuidSupplier, ProcessStarter starter)`

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

**Desc**: ClaudeRunner overrides run to add retry logic with configurable max retries and wait seconds. Retries on known API error patterns (500, 529, overloaded). Each invocation is bounded by maxClaudeSeconds via a two-phase timeout — `waitFor(maxClaudeSeconds, SECONDS)` on the process handle followed by `readerThread.join(maxClaudeSeconds * 1000L)` on the stdout-reader thread. Hitting either bound triggers `destroyForcibly()` on the subprocess and returns the sentinel `TIMEOUT_EXIT_CODE` so the calling phase can enter timeout recovery. The reader-side bound covers the Windows failure mode (issue 290) where `claude.cmd` parent exits cleanly but a grandchild `node` keeps the stdout pipe open silently — without it the main thread would sit in `readerThread.join()` past the budget. GitRunner and MavenRunner inherit ProcessRunner.run unchanged.

**Rule**: SOME method names follow run pattern.
 - **Name**: `^run$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*String\.\.\.\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public int run(String workingDirectory, String... args)` (ClaudeRunner — with retry logic)

## resume

**Desc**: ClaudeRunner-only. Invokes `claude --resume` with a continuation message so the most recent session can be nudged forward — used by GreenPhase and RefactorPhase both when `mvn clean verify` fails (message `mvn clean verify failures should be fixed`) and when the initial claude call times out (message `pls continue`, inside the timeout-recovery loop). Single-shot (no API-retry loop, but still bounded by maxClaudeSeconds); the caller's outer verify/timeout loop controls retries.

**Rule**: SOME method names follow resume pattern.
 - **Name**: `^resume$`
 - **Return**: `^int$`
 - **Parameters**: `^\(String\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public int resume(String workingDirectory, String message)` (ClaudeRunner — for phase-verify recovery)

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
