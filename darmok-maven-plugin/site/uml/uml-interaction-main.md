# UML Interaction Patterns

## All

### Logging Pattern

DarmokMojo logs via MojoLog instances (mojoLog for orchestration, runnerLog for process output). ProcessRunner and its subclasses log via the injected Log instance. No SLF4J loggers — all logging goes through Maven's Log interface wrapped by MojoLog.

**Example: Mojo phase logging**
```java
mojoLog.info("  Red: Running maven...");
// ... do work ...
mojoLog.info("  Red: Completed maven (" + PhaseResult.formatDuration(duration) + ")");
```

**Example: Runner output streaming**
```java
log.debug("Running: " + String.join(" ", command));
while ((line = reader.readLine()) != null) {
    log.debug(line);
}
```

### Exception Pattern

{Goal}Mojo.execute() catches MojoExecutionException directly and wraps all other exceptions. DarmokMojo methods throw Exception. No try-catch in ProcessRunner or {Tool}Runner — exceptions propagate.

**Example: Goal exception handling**
```java
public void execute() throws MojoExecutionException {
    try {
        init();
        // ... workflow ...
    } catch (MojoExecutionException e) {
        throw e;
    } catch (Exception e) {
        throw new MojoExecutionException(e.getMessage(), e);
    } finally {
        cleanup();
    }
}
```

## {Goal}Mojo

### execute

Maven goal entry point. Calls init(), runs cleanup, iterates scenarios, and calls cleanup() in finally block.

**Example: Goal execution**
```java
public void execute() throws MojoExecutionException {
    try {
        init();
        mojoLog.info("RGR Automation Plugin (gen-from-existing)");
        int cleanUpExit = runCleanUp();
        if (cleanUpExit != 0) {
            throw new MojoExecutionException("Clean up failed with exit code " + cleanUpExit);
        }
        int totalProcessed = 0;
        ScenarioEntry entry;
        while ((entry = getNextScenario()) != null) {
            mojoLog.info("Processing Scenario: " + entry.file() + "/" + entry.scenario() + " [" + entry.tag() + "]");
            processScenario(entry);
            totalProcessed++;
        }
        mojoLog.info("RGR Automation Complete!");
        mojoLog.info("Total scenarios processed: " + totalProcessed);
    } catch (MojoExecutionException e) {
        throw e;
    } catch (Exception e) {
        throw new MojoExecutionException(e.getMessage(), e);
    } finally {
        cleanup();
    }
}
```

## DarmokMojo

### setBaseDir

Test-only setter that pre-seeds baseDir so init() skips the MavenProject path.

**Example: setBaseDir method body**
```java
public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
}
```

### init

Initializes baseDir, creates MojoLog instances, instantiates runners via their factories, and constructs the three RGR phase classes.

**Example: init method body**
```java
void init() throws Exception {
    if (baseDir == null) {
        baseDir = project.getBasedir().getAbsolutePath();
    }
    initLogs();
    git = gitRunnerFactory.create(runnerLog);
    MavenRunner maven = mavenRunnerFactory.create(runnerLog);
    String sheepDogRoot = baseDir + "/../..";
    String artifactId = project.getArtifactId();
    redPhase = new RedPhase(maven, mojoLog, baseDir, specsDir, host, onlyChanges);
    greenPhase = new GreenPhase(
        claudeRunnerFactory.create(runnerLog, modelGreen, maxRetries, retryWaitSeconds),
        sheepDogRoot, artifactId);
    refactorPhase = new RefactorPhase(
        claudeRunnerFactory.create(runnerLog, modelRefactor, maxRetries, retryWaitSeconds),
        sheepDogRoot, artifactId);
}
```

### cleanup

Closes MojoLog instances.

**Example: cleanup method body**
```java
void cleanup() {
    if (mojoLog != null) mojoLog.close();
    if (runnerLog != null) runnerLog.close();
}
```

### getNextScenario

Reads the scenarios file and returns the first entry, or null if empty or missing.

**Example: getNextScenario method body**
```java
ScenarioEntry getNextScenario() throws Exception {
    Path scenariosPath = Path.of(baseDir, scenariosFile);
    if (!Files.exists(scenariosPath)) {
        return null;
    }
    List<ScenarioEntry> scenarios = parseScenarios(scenariosPath.toString());
    if (scenarios.isEmpty()) {
        return null;
    }
    return scenarios.get(0);
}
```

### processScenario

Orchestrates the RGR cycle for a single scenario. Delegates each phase to its phase class (which owns its own start/complete log markers and timing), branches on the returned PhaseResult, handles git staging + commit policy, and emits METRIC-scenario lines using the phase durations.

**Example: Phase delegation shape**
```java
void processScenario(ScenarioEntry entry) throws MojoExecutionException, Exception {
    String scenarioName = entry.scenario();
    String tag = entry.tag();
    // ... addTagToAsciidoc ...
    PhaseResult redResult = redPhase.run(tag);
    if (redResult.exitCode() != 0 && redResult.exitCode() != 100) { /* throw */ }
    // ... branch on red, run green+refactor, commit per stage policy ...
    mojoLog.info("  METRIC-scenario=" + scenarioName + "-phase=total-duration_ms=" + totalDuration);
}
```

### removeFirstScenarioFromFile

Removes the first scenario entry from the scenarios file, preserving the File header if subsequent scenarios share it.

**Example: removeFirstScenarioFromFile method body**
```java
void removeFirstScenarioFromFile() throws Exception {
    Path scenariosPath = Path.of(baseDir, scenariosFile);
    List<String> lines = Files.readAllLines(scenariosPath, StandardCharsets.UTF_8);
    // Find end of first File/Scenario/Tag block, write remaining
    if (remaining.isEmpty()) {
        Files.writeString(scenariosPath, "", StandardCharsets.UTF_8);
    } else {
        writeFileWithLF(scenariosPath.toString(), remaining);
    }
}
```

### addTagToAsciidoc

Reads an AsciiDoc file, finds the matching Test-Case header, and inserts or appends a tag annotation.

**Example: addTagToAsciidoc method body**
```java
boolean addTagToAsciidoc(String fileName, String scenarioName, String tag) throws Exception {
    String filePath = baseDir + "/" + asciidocDir + "/" + fileName + ".asciidoc";
    File file = new File(filePath);
    if (!file.exists()) {
        mojoLog.warn("File not found: " + fileName + ".asciidoc");
        return false;
    }
    // Read content, find Test-Case header, insert/append tag
    return true;
}
```

### parseScenarios

Parses the indented File/Scenario/Tag format into ScenarioEntry records.

**Example: parseScenarios method body**
```java
List<ScenarioEntry> parseScenarios(String scenariosFilePath) throws Exception {
    List<String> lines = Files.readAllLines(Path.of(scenariosFilePath), StandardCharsets.UTF_8);
    List<ScenarioEntry> result = new ArrayList<>();
    String currentFile = "";
    String currentScenario = "";
    for (String line : lines) {
        if (line.startsWith("File: ")) {
            currentFile = line.substring("File: ".length());
        } else if (line.startsWith("  Scenario: ")) {
            currentScenario = line.substring("  Scenario: ".length());
        } else if (line.startsWith("    Tag: ")) {
            String tag = line.substring("    Tag: ".length());
            result.add(new ScenarioEntry(currentFile, currentScenario, tag));
        }
    }
    return result;
}
```

### runCleanUp

Deletes stale NUL files and the target directory.

**Example: runCleanUp method body**
```java
int runCleanUp() throws Exception {
    Path sheepDogMain = Path.of(baseDir, "../..").normalize();
    int deleted = deleteNulFiles(sheepDogMain);
    mojoLog.debug("  Cleanup: Deleted " + deleted + " NUL files");
    deleteDirectory(Path.of(baseDir, "target"));
    mojoLog.debug("  Cleanup: Deleted target directory");
    return 0;
}
```

### writeFileWithLF

Writes lines joined with LF line endings.

**Example: writeFileWithLF method body**
```java
void writeFileWithLF(String filePath, List<String> lines) throws Exception {
    String content = String.join("\n", lines) + "\n";
    Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8);
}
```

## RedPhase

### run

Runs the red-phase workflow: invoke upstream maven goals, generate the cucumber runner class, run `mvn test`. Logs phase start/complete markers, times the work, returns a PhaseResult whose exit code is 100 if tests already pass, 0 if they fail.

**Example: run method body**
```java
public PhaseResult run(String pattern) throws Exception {
    mojoLog.info("  Red: Running maven...");
    long start = System.currentTimeMillis();
    // asciidoctor-to-uml, uml-to-cucumber-guice, generate runner class, mvn test ...
    int exitCode = testExitCode == 0 ? 100 : 0;
    long duration = System.currentTimeMillis() - start;
    mojoLog.info("  Red: Completed maven (" + PhaseResult.formatDuration(duration) + ")");
    return new PhaseResult(exitCode, duration);
}
```

### generateRunnerClassContent

Generates Java source for a Cucumber suite runner class filtered by tag.

**Example: generateRunnerClassContent method body**
```java
String generateRunnerClassContent(String pattern, String runnerClassName) {
    return "package org.farhan.suites;\n"
        + "import org.junit.platform.suite.api.*;\n"
        + "@Suite\n@IncludeEngines(\"cucumber\")\n"
        + "@IncludeTags(\"" + pattern + "\")\n"
        + "public class " + runnerClassName + " {\n}";
}
```

## GreenPhase

### run

Invokes the Claude `/rgr-green` skill for the current scenario tag. Logs start/complete markers, times the work, returns a PhaseResult with the ClaudeRunner exit code and duration.

**Example: run method body**
```java
public PhaseResult run(String pattern) throws Exception {
    mojoLog.info("  Green: Running...");
    long start = System.currentTimeMillis();
    int exitCode = claude.run(workingDir, "/rgr-green " + artifactId + " " + pattern);
    long duration = System.currentTimeMillis() - start;
    mojoLog.info("  Green: Completed (" + PhaseResult.formatDuration(duration) + ")");
    return new PhaseResult(exitCode, duration);
}
```

## RefactorPhase

### run

Invokes the Claude `/rgr-refactor forward` skill for the current scenario. Logs start/complete markers, times the work, returns a PhaseResult with the ClaudeRunner exit code and duration.

**Example: run method body**
```java
public PhaseResult run() throws Exception {
    mojoLog.info("  Refactor: Running...");
    long start = System.currentTimeMillis();
    int exitCode = claude.run(workingDir, "/rgr-refactor forward " + artifactId);
    long duration = System.currentTimeMillis() - start;
    mojoLog.info("  Refactor: Completed (" + PhaseResult.formatDuration(duration) + ")");
    return new PhaseResult(exitCode, duration);
}
```

## PhaseResult

### formatDuration

Formats milliseconds as HH:MM:SS.

**Example: formatDuration method body**
```java
public static String formatDuration(long millis) {
    long seconds = millis / 1000;
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, secs);
}
```

## ProcessRunner

### buildCommand

Constructs the command list from arguments. Base implementation passes args through.

**Example: buildCommand method body**
```java
protected List<String> buildCommand(String... args) {
    List<String> command = new ArrayList<>();
    for (String arg : args) {
        command.add(arg);
    }
    return command;
}
```

### run

Starts process via ProcessStarter seam, streams output to log, returns exit code.

**Example: run method body**
```java
public int run(String workingDirectory, String... args) throws Exception {
    List<String> command = buildCommand(args);
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.directory(new File(workingDirectory));
    pb.redirectErrorStream(true);
    log.debug("Running: " + String.join(" ", command));
    Process process = starter.start(pb);
    process.getOutputStream().close();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
            log.debug(line);
        }
    }
    return process.waitFor();
}
```

### getLog

Returns the Maven Log instance.

**Example: getLog method body**
```java
protected Log getLog() {
    return log;
}
```

### isWindows

Returns true if the OS is Windows.

**Example: isWindows method body**
```java
protected static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
}
```

## {Tool}Runner

### buildCommand

Each runner prepends its tool executable. Platform-aware runners check isWindows() for .cmd extension.

**Example: Simple runner (GitRunner)**
```java
@Override
protected List<String> buildCommand(String... args) {
    List<String> command = new ArrayList<>();
    command.add("git");
    for (String arg : args) {
        command.add(arg);
    }
    return command;
}
```

### run

ClaudeRunner overrides run() to add retry logic on known API error patterns. GitRunner and MavenRunner inherit ProcessRunner.run unchanged.

**Example: ClaudeRunner retry loop**
```java
@Override
public int run(String workingDirectory, String... args) throws Exception {
    List<String> command = buildCommand(args);
    int attempt = 0;
    int exitCode = -1;
    while (attempt < maxRetries) {
        attempt++;
        Process process = starter.start(pb);
        // ... read output ...
        exitCode = process.waitFor();
        if (exitCode == 0) break;
        String matchedPattern = findRetryableError(outputLines);
        if (matchedPattern != null && attempt < maxRetries) {
            Thread.sleep(retryWaitSeconds * 1000L);
        } else {
            break;
        }
    }
    return exitCode;
}
```

## MojoLog

### getLogFile

Returns the log file path for this MojoLog instance.

**Example: getLogFile method body**
```java
public Path getLogFile() {
    return logFile;
}
```

### findDatedLog

Finds a dated log file by prefix in a directory.

**Example: findDatedLog method body**
```java
public static Path findDatedLog(Path logDir, String prefix) {
    if (!Files.isDirectory(logDir)) {
        return logDir.resolve(prefix + ".log");
    }
    try (var stream = Files.list(logDir)) {
        return stream
            .filter(p -> {
                String name = p.getFileName().toString();
                return name.startsWith(prefix + ".") && name.endsWith(".log");
            })
            .findFirst()
            .orElse(logDir.resolve(prefix + ".log"));
    } catch (IOException e) {
        return logDir.resolve(prefix + ".log");
    }
}
```

### is{LogLevel}Enabled

Delegates to the wrapped Log instance.

**Example: Delegation**
```java
@Override
public boolean isDebugEnabled() {
    return delegate.isDebugEnabled();
}
```

### {logLevel}(CharSequence)

Forwards to wrapped Log, then writes timestamped line to file.

**Example: Delegation pattern**
```java
@Override
public void info(CharSequence content) {
    delegate.info(content);
    writeLine("INFO ", content);
}
```

### {logLevel}(CharSequence, Throwable)

Forwards to wrapped Log, then writes timestamped line plus stack trace to file.

**Example: Delegation with throwable**
```java
@Override
public void info(CharSequence content, Throwable error) {
    delegate.info(content, error);
    writeLine("INFO ", content, error);
}
```

### {logLevel}(Throwable)

Forwards to wrapped Log, then writes stack trace to file.

**Example: Delegation with throwable only**
```java
@Override
public void info(Throwable error) {
    delegate.info(error);
    writeLine("INFO ", "", error);
}
```

### close

Closes the underlying PrintWriter.

**Example: close method body**
```java
@Override
public void close() {
    writer.close();
}
```
