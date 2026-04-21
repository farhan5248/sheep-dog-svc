# RefactorPhase

**Directory**: `src/main/java/org/farhan/mbt/maven`

Third RGR phase. Invokes the Claude CLI `/rgr-refactor forward` skill to refactor the freshly-green code. Returns the exit code from the Claude subprocess (including any retry loop inside ClaudeRunner).

## RefactorPhase

**Desc**: Constructs a RefactorPhase with its ClaudeRunner collaborator (pre-configured with the refactor-phase model), a MavenRunner used for `mvn clean verify` in the inner verify loop, a DarmokMojoLog for phase start/complete markers, the working directory Claude runs in, the target project directory `mvn clean verify` runs in, the maven artifact id passed as the skill's project argument, and the maximum number of verify attempts before aborting the phase.

**Rule**: ONE constructor matches RefactorPhase pattern.
 - **Name**: `^RefactorPhase$`
 - **Parameters**: `^\(ClaudeRunner\s+\w+,\s*MavenRunner\s+\w+,\s*DarmokMojoLog\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*int\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public RefactorPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog, String workingDir, String targetDir, String artifactId, int maxVerifyAttempts)`

## run

**Desc**: Runs the refactor-phase Claude skill for the current scenario. Logs phase start and completion markers, times the work, and returns a PhaseResult carrying the ClaudeRunner exit code and the measured duration.

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^PhaseResult$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public PhaseResult run()`
