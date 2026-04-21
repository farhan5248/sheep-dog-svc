# RefactorPhase

**Directory**: `src/main/java/org/farhan/mbt/maven`

Third RGR phase. Invokes the Claude CLI `/rgr-refactor forward` skill to refactor the freshly-green code, wrapped in the same timeout-recovery + verify sub-loops as GreenPhase (with `Refactor:` log prefix and `"rgr-refactor timed out after ..."` / `"rgr-refactor verify failed after ..."` exhaustion messages). Returns the exit code from the Claude subprocess (including any retry loop inside ClaudeRunner) wrapped with the phase duration.

## RefactorPhase

**Desc**: Constructs a RefactorPhase with its ClaudeRunner collaborator (pre-configured with the refactor-phase model), a MavenRunner used for `mvn clean verify` and the `mvn clean install` check inside the timeout-recovery loop, a DarmokMojoLog for phase start/complete markers, the working directory Claude runs in, the target project directory the inner Maven commands run in, the maven artifact id passed as the skill's project argument, the maximum number of verify attempts before aborting the verify loop, the maximum number of claude-timeout attempts before aborting the phase, and the per-invocation claude timeout in seconds (used in the "timed out after Ns, killing..." log line).

**Rule**: ONE constructor matches RefactorPhase pattern.
 - **Name**: `^RefactorPhase$`
 - **Parameters**: `^\(ClaudeRunner\s+\w+,\s*MavenRunner\s+\w+,\s*DarmokMojoLog\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*int\s+\w+,\s*int\s+\w+,\s*int\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public RefactorPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog, String workingDir, String targetDir, String artifactId, int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds)`

## run

**Desc**: Runs the refactor-phase Claude skill for the current scenario, wrapped with the timeout-recovery and verify sub-loops (see class description). Logs phase start and completion markers, times the whole phase, and returns a PhaseResult carrying the final exit code (0 on normal or recovered success; the upstream non-timeout exit code on non-retryable claude failure) and the measured duration.

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^PhaseResult$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public PhaseResult run()`
