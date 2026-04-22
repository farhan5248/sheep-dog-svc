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

**Desc**: Maven plugin configuration parameters with default values. Covers path properties (specsDir, asciidocDir, scenariosFile, metricsDir), server properties (host), Claude CLI properties (modelRed, modelGreen, modelRefactor, coAuthor, maxRetries, retryWaitSeconds), phase-verify policy (maxVerifyAttempts — cap on `mvn clean verify` + `claude --resume` cycles inside each of GreenPhase and RefactorPhase before the phase aborts), phase-timeout policy (maxClaudeSeconds — per-invocation bound on any claude subprocess; maxTimeoutAttempts — cap on `mvn clean install` + `claude --resume "pls continue"` cycles inside the phase's timeout-recovery loop), behavior flags (onlyChanges, stage), and run identity (gitBranch — the branch this Darmok run is configured for; verified against git HEAD at init-time and written to every metrics.csv row as `git_branch`).

**Rule**: SOME attribute matches @Parameter pattern.
 - **Name**: `^(specsDir|asciidocDir|scenariosFile|metricsDir|host|modelRed|modelGreen|modelRefactor|coAuthor|maxRetries|maxVerifyAttempts|maxClaudeSeconds|maxTimeoutAttempts|retryWaitSeconds|onlyChanges|stage|gitBranch)$`
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
 - `public int maxClaudeSeconds`
 - `public int maxTimeoutAttempts`
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
 - **Modifier**: `^protected$`

**Examples**:
 - `protected void init()`

## cleanup

**Desc**: Closes mojo and runner MojoLog instances.

**Rule**: ONE method name follows cleanup pattern.
 - **Name**: `^cleanup$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected void cleanup()`

## getNextScenario

**Desc**: Reads the scenarios file and returns the first ScenarioEntry, or null if empty or missing.

**Rule**: ONE method name follows getNextScenario pattern.
 - **Name**: `^getNextScenario$`
 - **Return**: `^ScenarioEntry$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected ScenarioEntry getNextScenario()`

## processScenario

**Desc**: Orchestrates the full RGR cycle for a single scenario entry by delegating to RedPhase, GreenPhase, and RefactorPhase in sequence, with git staging and commit between phases. After all commits are made, captures the HEAD commit SHA via `git.getCurrentCommit`, logs it as `Commit: <sha>`, and writes a row to `metrics.csv` via `DarmokMojoMetrics.append` carrying the configured gitBranch, the captured commit, the scenario name, and the four phase durations (red/green/refactor/total).

**Rule**: ONE method name follows processScenario pattern.
 - **Name**: `^processScenario$`
 - **Return**: `^void$`
 - **Parameters**: `^\(ScenarioEntry\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected void processScenario(ScenarioEntry entry)`

## runCleanUp

**Desc**: Deletes stale NUL files from the project tree and removes the target directory. Called by `{Goal}Mojo.execute` subclasses before scenario iteration.

**Rule**: ONE method name follows runCleanUp pattern.
 - **Name**: `^runCleanUp$`
 - **Return**: `^int$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected int runCleanUp()`

