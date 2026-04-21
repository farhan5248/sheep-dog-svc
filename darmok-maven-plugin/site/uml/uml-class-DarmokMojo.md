# DarmokMojo

**Directory**: `src/main/java/org/farhan/mbt/maven`

**Extends**: AbstractMojo

Abstract base Mojo providing shared lifecycle, scenario iteration, RGR phase orchestration, AsciiDoc tag insertion, and file I/O helpers. Separates common workflow logic from goal-specific entry points.

## project

**Desc**: Maven project reference injected by the plugin framework.

**Rule**: ONE attribute matches project pattern.
 - **Name**: `^project$`
 - **Return**: `^MavenProject$`
 - **Modifier**: `^public$`

**Examples**:
 - `public MavenProject project`

## @Parameter

**Desc**: Maven plugin configuration parameters with default values. Covers path properties (specsDir, asciidocDir, scenariosFile, metricsDir), server properties (host), Claude CLI properties (modelRed, modelGreen, modelRefactor, coAuthor, maxRetries, retryWaitSeconds), phase-verify policy (maxVerifyAttempts — cap on `mvn clean verify` + `claude --resume` cycles inside each of GreenPhase and RefactorPhase before the phase aborts), behavior flags (onlyChanges, stage), and run identity (gitBranch — the branch this Darmok run is configured for; verified against git HEAD at init-time and written to every metrics.csv row as `git_branch`).

**Rule**: SOME attribute matches @Parameter pattern.
 - **Name**: `^(specsDir|asciidocDir|scenariosFile|metricsDir|host|modelRed|modelGreen|modelRefactor|coAuthor|maxRetries|maxVerifyAttempts|retryWaitSeconds|onlyChanges|stage|gitBranch)$`
 - **Return**: `^(String|int|boolean)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String specsDir`
 - `public String asciidocDir`
 - `public String scenariosFile`
 - `public String metricsDir`
 - `public String host`
 - `public String modelRed`
 - `public String modelGreen`
 - `public String modelRefactor`
 - `public String coAuthor`
 - `public int maxRetries`
 - `public int maxVerifyAttempts`
 - `public int retryWaitSeconds`
 - `public boolean onlyChanges`
 - `public boolean stage`
 - `public String gitBranch`

## setBaseDir

**Desc**: Test-only setter that pre-seeds baseDir before execute() so init() skips the MavenProject path.

**Rule**: ONE method name follows setBaseDir pattern.
 - **Name**: `^setBaseDir$`
 - **Return**: `^void$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void setBaseDir(String baseDir)`

## set{Tool}RunnerFactory

**Desc**: Test-only setter that substitutes a runner factory so tests can inject a FakeProcessStarter-backed runner. Default factories use the production runner constructors (e.g. `GitRunner::new`).

**Rule**: SOME method names follow set{Tool}RunnerFactory pattern.
 - **Name**: `^set{Tool}RunnerFactory$`
 - **Return**: `^void$`
 - **Parameters**: `^\({Tool}RunnerFactory\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void setGitRunnerFactory(GitRunnerFactory factory)`
 - `public void setMavenRunnerFactory(MavenRunnerFactory factory)`
 - `public void setClaudeRunnerFactory(ClaudeRunnerFactory factory)`

## init

**Desc**: Initializes baseDir from MavenProject, creates MojoLog instances for mojo and runner output, instantiates GitRunner and MavenRunner via their factories, and constructs the RedPhase, GreenPhase, and RefactorPhase with their wired runners.

**Rule**: ONE method name follows init pattern.
 - **Name**: `^init$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `void init()`

## cleanup

**Desc**: Closes mojo and runner MojoLog instances.

**Rule**: ONE method name follows cleanup pattern.
 - **Name**: `^cleanup$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `void cleanup()`

## getNextScenario

**Desc**: Reads the scenarios file and returns the first ScenarioEntry, or null if empty or missing.

**Rule**: ONE method name follows getNextScenario pattern.
 - **Name**: `^getNextScenario$`
 - **Return**: `^ScenarioEntry$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `ScenarioEntry getNextScenario()`

## processScenario

**Desc**: Orchestrates the full RGR cycle for a single scenario entry by delegating to RedPhase, GreenPhase, and RefactorPhase in sequence, with git staging and commit between phases. Logs metric lines for each phase duration.

**Rule**: ONE method name follows processScenario pattern.
 - **Name**: `^processScenario$`
 - **Return**: `^void$`
 - **Parameters**: `^\(ScenarioEntry\s+\w+\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `void processScenario(ScenarioEntry entry)`

## removeFirstScenarioFromFile

**Desc**: Removes the first scenario entry (File + Scenario + Tag block) from the scenarios file, preserving the File header if subsequent scenarios share it.

**Rule**: ONE method name follows removeFirstScenarioFromFile pattern.
 - **Name**: `^removeFirstScenarioFromFile$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `void removeFirstScenarioFromFile()`

## parseScenarios

**Desc**: Parses a scenarios file into a list of ScenarioEntry records (file, scenario, tag).

**Rule**: ONE method name follows parseScenarios pattern.
 - **Name**: `^parseScenarios$`
 - **Return**: `^List<ScenarioEntry>$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `List<ScenarioEntry> parseScenarios(String scenariosFilePath)`

## addTagToAsciidoc

**Desc**: Inserts a tag annotation into an AsciiDoc file after the matching Test-Case header. Appends to existing tag line or creates a new one. Returns true if the tag was added.

**Rule**: ONE method name follows addTagToAsciidoc pattern.
 - **Name**: `^addTagToAsciidoc$`
 - **Return**: `^boolean$`
 - **Parameters**: `^\(String\s+\w+,\s*String\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `boolean addTagToAsciidoc(String fileName, String scenarioName, String tag)`

## runCleanUp

**Desc**: Deletes stale NUL files from the project tree and removes the target directory.

**Rule**: ONE method name follows runCleanUp pattern.
 - **Name**: `^runCleanUp$`
 - **Return**: `^int$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `int runCleanUp()`

## writeFileWithLF

**Desc**: Writes lines to a file joined with LF line endings.

**Rule**: ONE method name follows writeFileWithLF pattern.
 - **Name**: `^writeFileWithLF$`
 - **Return**: `^void$`
 - **Parameters**: `^\(String\s+\w+,\s*List<String>\s+\w+\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `void writeFileWithLF(String filePath, List<String> lines)`

