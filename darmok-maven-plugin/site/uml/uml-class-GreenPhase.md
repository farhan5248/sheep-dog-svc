# GreenPhase

**Directory**: `src/main/java/org/farhan/mbt/maven`

Second RGR phase. Invokes the Claude CLI `/rgr-green` skill to implement code that makes the failing tests from the red phase pass, then runs two deterministic sub-loops: the timeout-recovery loop (triggered when the claude call returned `ClaudeRunner.TIMEOUT_EXIT_CODE`; runs `mvn clean install` and resumes the session with `"pls continue"` until install passes or `maxTimeoutAttempts` is exhausted) and the verify loop (runs `mvn clean verify` and resumes the session with `"mvn clean verify failures should be fixed"` until verify passes or `maxVerifyAttempts` is exhausted). Returns the exit code from the Claude subprocess (including any retry loop inside ClaudeRunner) wrapped with the phase duration.

## GreenPhase

**Desc**: Constructs a GreenPhase with its ClaudeRunner collaborator (pre-configured with the green-phase model), a MavenRunner used for `mvn clean verify` and the `mvn clean install` check inside the timeout-recovery loop, a DarmokMojoLog for phase start/complete markers, the working directory Claude runs in, the target project directory the inner Maven commands run in, the maven artifact id passed as the skill's project argument, the maximum number of verify attempts before aborting the verify loop, the maximum number of claude-timeout attempts before aborting the phase, and the per-invocation claude timeout in seconds (used in the "timed out after Ns, killing..." log line).

**Rule**: ONE constructor matches GreenPhase pattern.
 - **Name**: `^GreenPhase$`
 - **Parameters**: `^\(ClaudeRunner\s+\w+,\s*MavenRunner\s+\w+,\s*DarmokMojoLog\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*int\s+\w+,\s*int\s+\w+,\s*int\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public GreenPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog, String workingDir, String targetDir, String artifactId, int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds)`

## run

**Desc**: Runs the green-phase Claude skill for one scenario tag, wrapped with the timeout-recovery and verify sub-loops (see class description). Logs phase start and completion markers, times the whole phase, and returns a PhaseResult carrying the final exit code (0 on normal or recovered success; the upstream non-timeout exit code on non-retryable claude failure) and the measured duration.

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^PhaseResult$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public PhaseResult run(String pattern)`
