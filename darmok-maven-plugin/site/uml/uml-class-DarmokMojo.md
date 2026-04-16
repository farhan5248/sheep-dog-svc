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

**Desc**: Maven plugin configuration parameters with default values. Covers path properties (specsDir, asciidocDir, scenariosFile), server properties (host), Claude CLI properties (modelRed, modelGreen, modelRefactor, coAuthor, maxRetries, retryWaitSeconds), and behavior flags (onlyChanges, stage).

**Rule**: SOME attribute matches @Parameter pattern.
 - **Name**: `^(specsDir|asciidocDir|scenariosFile|host|modelRed|modelGreen|modelRefactor|coAuthor|maxRetries|retryWaitSeconds|onlyChanges|stage)$`
 - **Return**: `^(String|int|boolean)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String specsDir`
 - `public String asciidocDir`
 - `public String scenariosFile`
 - `public String host`
 - `public String modelRed`
 - `public String modelGreen`
 - `public String modelRefactor`
 - `public String coAuthor`
 - `public int maxRetries`
 - `public int retryWaitSeconds`
 - `public boolean onlyChanges`
 - `public boolean stage`

## setBaseDir

**Desc**: Test-only setter that pre-seeds baseDir before execute() so init() skips the MavenProject path.

**Rule**: ONE method name follows setBaseDir pattern.
 - **Name**: `^setBaseDir$`
 - **Return**: `^void$`
 - **Parameters**: `^\(String\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void setBaseDir(String baseDir)`

## init

**Desc**: Initializes baseDir from MavenProject, creates MojoLog instances for mojo and runner output, and instantiates GitRunner and MavenRunner.

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

**Desc**: Executes the full RGR cycle (red, green, refactor) for a single scenario entry, with git staging and commit between phases. Logs metric lines for each phase duration.

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

## generateRunnerClassContent

**Desc**: Generates Java source for a Cucumber suite runner class filtered by tag.

**Rule**: ONE method name follows generateRunnerClassContent pattern.
 - **Name**: `^generateRunnerClassContent$`
 - **Return**: `^String$`
 - **Parameters**: `^\(String\s+\w+,\s*String\s+\w+\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `String generateRunnerClassContent(String pattern, String runnerClassName)`

## formatDuration

**Desc**: Formats milliseconds as HH:MM:SS string.

**Rule**: ONE method name follows formatDuration pattern.
 - **Name**: `^formatDuration$`
 - **Return**: `^String$`
 - **Parameters**: `^\(long\s+\w+\)$`
 - **Modifier**: `^(default|package-private)$`

**Examples**:
 - `String formatDuration(long millis)`
