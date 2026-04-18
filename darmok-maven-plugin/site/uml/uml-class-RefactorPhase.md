# RefactorPhase

**Directory**: `src/main/java/org/farhan/mbt/maven`

Third RGR phase. Invokes the Claude CLI `/rgr-refactor forward` skill to refactor the freshly-green code. Returns the exit code from the Claude subprocess (including any retry loop inside ClaudeRunner).

## RefactorPhase

**Desc**: Constructs a RefactorPhase with its ClaudeRunner collaborator (pre-configured with the refactor-phase model), a MojoLog for phase start/complete markers, the working directory Claude runs in, and the maven artifact id passed as the skill's project argument.

**Rule**: ONE constructor matches RefactorPhase pattern.
 - **Name**: `^RefactorPhase$`
 - **Parameters**: `^\(ClaudeRunner\s+\w+,\s*MojoLog\s+\w+,\s*String\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public RefactorPhase(ClaudeRunner claude, MojoLog mojoLog, String workingDir, String artifactId)`

## run

**Desc**: Runs the refactor-phase Claude skill for the current scenario. Logs phase start and completion markers, times the work, and returns a PhaseResult carrying the ClaudeRunner exit code and the measured duration.

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^PhaseResult$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public PhaseResult run()`
