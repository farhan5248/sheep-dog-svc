# RedPhase

**Directory**: `src/main/java/org/farhan/mbt/maven`

First RGR phase. Runs the asciidoctor-to-uml and uml-to-cucumber-guice maven goals, generates the cucumber suite runner class, then runs `mvn test`. Returns 0 when tests fail (green phase should follow) or 100 when tests already pass (green phase should skip).

## RedPhase

**Desc**: Constructs a RedPhase with its MavenRunner collaborator, a MojoLog for phase markers, and the path/host/onlyChanges configuration needed to drive the two upstream maven goals.

**Rule**: ONE constructor matches RedPhase pattern.
 - **Name**: `^RedPhase$`
 - **Parameters**: `^\(MavenRunner\s+\w+,\s*MojoLog\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*boolean\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public RedPhase(MavenRunner maven, MojoLog mojoLog, String baseDir, String specsDir, String host, boolean onlyChanges)`

## run

**Desc**: Runs the red-phase workflow for one scenario tag. Logs phase start and completion markers, times the work, and returns a PhaseResult whose exit code is 100 when `mvn test` already passes (no failing tests to fix) or 0 when tests fail (ready for green).

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^PhaseResult$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public PhaseResult run(String pattern)`

## generateRunnerClassContent

**Desc**: Generates Java source for a Cucumber suite runner class filtered by tag.

**Rule**: ONE method name follows generateRunnerClassContent pattern.
 - **Name**: `^generateRunnerClassContent$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `String generateRunnerClassContent(String pattern, String runnerClassName)`
