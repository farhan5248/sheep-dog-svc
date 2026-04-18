# GreenPhase

**Directory**: `src/main/java/org/farhan/mbt/maven`

Second RGR phase. Invokes the Claude CLI `/rgr-green` skill to implement code that makes the failing tests from the red phase pass. Returns the exit code from the Claude subprocess (including any retry loop inside ClaudeRunner).

## GreenPhase

**Desc**: Constructs a GreenPhase with its ClaudeRunner collaborator (pre-configured with the green-phase model), a MojoLog for phase start/complete markers, the working directory Claude runs in, and the maven artifact id passed as the skill's project argument.

**Rule**: ONE constructor matches GreenPhase pattern.
 - **Name**: `^GreenPhase$`
 - **Parameters**: `^\(ClaudeRunner\s+\w+,\s*MojoLog\s+\w+,\s*String\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public GreenPhase(ClaudeRunner claude, MojoLog mojoLog, String workingDir, String artifactId)`

## run

**Desc**: Runs the green-phase Claude skill for one scenario tag. Logs phase start and completion markers, times the work, and returns a PhaseResult carrying the ClaudeRunner exit code and the measured duration.

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^PhaseResult$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public PhaseResult run(String pattern)`
