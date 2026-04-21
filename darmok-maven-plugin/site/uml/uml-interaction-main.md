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

Initializes baseDir, creates MojoLog instances, instantiates runners via their factories, and constructs the three RGR phase classes. The same MavenRunner instance is shared between RedPhase (for red-phase maven goals) and GreenPhase / RefactorPhase (for the `mvn clean verify` sub-step inside each phase's verify loop).

**Example: init method body**
```java
void init() throws Exception {
    if (baseDir == null) {
        baseDir = project.getBasedir().getAbsolutePath();
    }
    initLogs();
    git = gitRunnerFactory.create(runnerLog);
    verifyGitBranch();
    MavenRunner maven = mavenRunnerFactory.create(runnerLog);
    String sheepDogRoot = baseDir + "/../..";
    String artifactId = project.getArtifactId();
    redPhase = new RedPhase(maven, mojoLog, baseDir, specsDir, host, onlyChanges);
    greenPhase = new GreenPhase(
        claudeRunnerFactory.create(runnerLog, modelGreen, maxRetries, retryWaitSeconds, maxClaudeSeconds),
        maven, mojoLog, sheepDogRoot, baseDir, artifactId, maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds);
    refactorPhase = new RefactorPhase(
        claudeRunnerFactory.create(runnerLog, modelRefactor, maxRetries, retryWaitSeconds, maxClaudeSeconds),
        maven, mojoLog, sheepDogRoot, baseDir, artifactId, maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds);
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

Orchestrates the RGR cycle for a single scenario. Delegates each phase to its phase class (which owns its own start/complete log markers and timing), branches on the returned PhaseResult, handles git staging + commit policy, captures the post-scenario HEAD commit, and appends a row to the metrics.csv file carrying the configured gitBranch, the captured commit, the scenario name, and the four phase durations.

**Example: Phase delegation shape**
```java
void processScenario(ScenarioEntry entry) throws MojoExecutionException, Exception {
    String scenarioName = entry.scenario();
    String tag = entry.tag();
    long totalStart = System.currentTimeMillis();
    // ... addTagToAsciidoc ...
    PhaseResult redResult = redPhase.run(tag);
    if (redResult.exitCode() != 0 && redResult.exitCode() != 100) { /* throw */ }
    // ... branch on red.exitCode(), run green+refactor, commit per stage policy ...

    String commit = git.getCurrentCommit(baseDir);
    mojoLog.info("  Commit: " + commit);
    long totalDuration = System.currentTimeMillis() - totalStart;
    metrics.append(gitBranch, commit, scenarioName,
        redResult.durationMs(), greenDuration, refactorDuration, totalDuration);
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

Invokes the Claude `/rgr-green` skill for the current scenario tag. If the claude call times out (returns `ClaudeRunner.TIMEOUT_EXIT_CODE`), delegates to `runTimeoutRecoveryLoop` which calls `mvn clean install` and, on failure, resumes the session with `"pls continue"` — bounded by `maxTimeoutAttempts`. On successful recovery (or normal exit 0), then runs the inner verify loop (`mvn clean verify` + `claude --resume "mvn clean verify failures should be fixed"` retries up to `maxVerifyAttempts`). Logs start/complete markers, times the whole phase, returns a PhaseResult. A non-zero non-timeout claude exit short-circuits before verify runs; verify-loop or timeout-loop exhaustion throws with a message naming the phase and attempt count.

**Example: run method body**
```java
public PhaseResult run(String pattern) throws Exception {
    mojoLog.info("  Green: Running...");
    long start = System.currentTimeMillis();
    int claudeExit = claude.run(workingDir, "/rgr-green " + artifactId + " " + pattern);
    claudeExit = runTimeoutRecoveryLoop(claudeExit);
    long claudeDuration = System.currentTimeMillis() - start;
    mojoLog.info("  Green: Completed (" + PhaseResult.formatDuration(claudeDuration) + ")");
    if (claudeExit != 0) {
        return new PhaseResult(claudeExit, claudeDuration);
    }
    runVerifyLoop();
    long totalDuration = System.currentTimeMillis() - start;
    return new PhaseResult(0, totalDuration);
}
```

### runTimeoutRecoveryLoop

Timeout-recovery sub-step of the green phase. No-op unless `claude.run` returned the sentinel `ClaudeRunner.TIMEOUT_EXIT_CODE`. Logs a single `Claude timed out after <N>s, killing...` (WARN) on entry; then loops running `mvn clean install` against the target project. Install exit 0 → log `Install passed, proceeding` and return 0 (phase continues into the verify loop as though claude had returned 0). Install non-zero → log `Install failed, resuming claude (attempt N of M)...` and call `claude.resume(workingDir, "pls continue")` — the resume's exit code is intentionally ignored; the next install check is authoritative. Bounded by `maxTimeoutAttempts`; on exhaustion logs `Timeout exhausted after M attempts` (ERROR) and throws `"rgr-green timed out after M attempts"`.

**Example: runTimeoutRecoveryLoop method body**
```java
private int runTimeoutRecoveryLoop(int claudeExit) throws Exception {
    if (claudeExit != ClaudeRunner.TIMEOUT_EXIT_CODE) {
        return claudeExit;
    }
    mojoLog.warn("  Green: Claude timed out after " + maxClaudeSeconds + "s, killing...");
    int attempt = 1;
    while (true) {
        mojoLog.info("  Green: Running mvn clean install to check phase state...");
        int installExit = maven.run(targetDir, "clean", "install");
        if (installExit == 0) {
            mojoLog.info("  Green: Install passed, proceeding");
            return 0;
        }
        if (attempt >= maxTimeoutAttempts) {
            mojoLog.error("  Green: Timeout exhausted after " + maxTimeoutAttempts + " attempts");
            throw new Exception("rgr-green timed out after " + maxTimeoutAttempts + " attempts");
        }
        attempt++;
        mojoLog.info("  Green: Install failed, resuming claude (attempt " + attempt
            + " of " + maxTimeoutAttempts + ")...");
        claude.resume(workingDir, TIMEOUT_RESUME_MESSAGE);
    }
}
```

### runVerifyLoop

Verify sub-step of the green phase. Runs `mvn clean verify` against the target project; on non-zero exit, invokes `claude --resume` with the literal continuation message `"mvn clean verify failures should be fixed"` and re-runs verify. Bounded by `maxVerifyAttempts`. Logs `Verify running...` / `Verify passed (<duration>)` on success, `Verify failed (attempt N/M), resuming claude...` on recoverable failure, and `Verify failed after M attempts, aborting` + throws on exhaustion.

**Example: runVerifyLoop method body**
```java
private void runVerifyLoop() throws Exception {
    for (int attempt = 1; attempt <= maxVerifyAttempts; attempt++) {
        mojoLog.info("  Green: Verify running...");
        long verifyStart = System.currentTimeMillis();
        int verifyExit = maven.run(targetDir, "clean", "verify");
        long verifyDuration = System.currentTimeMillis() - verifyStart;
        if (verifyExit == 0) {
            mojoLog.info("  Green: Verify passed (" + PhaseResult.formatDuration(verifyDuration) + ")");
            return;
        }
        if (attempt < maxVerifyAttempts) {
            mojoLog.warn("  Green: Verify failed (attempt " + attempt + "/" + maxVerifyAttempts + "), resuming claude...");
            claude.resume(workingDir, VERIFY_RESUME_MESSAGE);
        }
    }
    mojoLog.error("  Green: Verify failed after " + maxVerifyAttempts + " attempts, aborting");
    throw new Exception("rgr-green verify failed after " + maxVerifyAttempts + " attempts");
}
```

## RefactorPhase

### run

Invokes the Claude `/rgr-refactor forward` skill for the current scenario, then runs the same timeout-recovery + verify loops as GreenPhase. Logs start/complete markers, times the whole phase, returns a PhaseResult. Timeout-loop exhaustion throws `"rgr-refactor timed out after <N> attempts"`; verify-loop exhaustion throws `"rgr-refactor verify failed after <N> attempts"`.

**Example: run method body**
```java
public PhaseResult run() throws Exception {
    mojoLog.info("  Refactor: Running...");
    long start = System.currentTimeMillis();
    int claudeExit = claude.run(workingDir, "/rgr-refactor forward " + artifactId);
    claudeExit = runTimeoutRecoveryLoop(claudeExit);
    long claudeDuration = System.currentTimeMillis() - start;
    mojoLog.info("  Refactor: Completed (" + PhaseResult.formatDuration(claudeDuration) + ")");
    if (claudeExit != 0) {
        return new PhaseResult(claudeExit, claudeDuration);
    }
    runVerifyLoop();
    long totalDuration = System.currentTimeMillis() - start;
    return new PhaseResult(0, totalDuration);
}
```

### runTimeoutRecoveryLoop

Symmetric with GreenPhase.runTimeoutRecoveryLoop — see that entry for the full log/flow contract. Differences: log category prefix is `Refactor:`, shared `TIMEOUT_RESUME_MESSAGE` constant (`GreenPhase.TIMEOUT_RESUME_MESSAGE = "pls continue"`), exhaustion message is `"rgr-refactor timed out after M attempts"`.

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

### capture

Builds the command, starts the process, reads stdout to a string, and returns the trimmed output. Throws IOException on non-zero exit. Used by runners that need the subprocess's output as a value rather than streamed to the log.

**Example: capture method body**
```java
public String capture(String workingDirectory, String... args) throws Exception {
    List<String> command = buildCommand(args);
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.directory(new File(workingDirectory));
    pb.redirectErrorStream(true);
    log.debug("Running: " + String.join(" ", command));
    Process process = starter.start(pb);
    process.getOutputStream().close();
    StringBuilder output = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
    }
    int exit = process.waitFor();
    if (exit != 0) {
        throw new IOException("Command failed (exit " + exit + "): " + String.join(" ", command));
    }
    return output.toString().trim();
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

ClaudeRunner overrides run() to add (a) an API-retry loop on known transient error patterns (500/529/overloaded) and (b) a per-invocation timeout via `executeCommand`, which wraps each attempt in `waitFor(maxClaudeSeconds, SECONDS)` with a background stdout reader. On timeout the subprocess is destroyed and the sentinel `TIMEOUT_EXIT_CODE` is returned immediately — no API-retry is attempted, because the caller (GreenPhase/RefactorPhase) owns timeout recovery. GitRunner and MavenRunner inherit ProcessRunner.run unchanged.

**Example: ClaudeRunner retry + timeout loop**
```java
@Override
public int run(String workingDirectory, String... args) throws Exception {
    List<String> command = buildCommand(args);
    int attempt = 0;
    int exitCode = -1;
    while (attempt < maxRetries) {
        attempt++;
        List<String> outputLines = new ArrayList<>();
        exitCode = executeCommand(command, workingDirectory, outputLines);
        if (exitCode == TIMEOUT_EXIT_CODE) return TIMEOUT_EXIT_CODE;  // phase owns recovery
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

### resume

ClaudeRunner-only. Single-shot invocation of `claude --resume` with a continuation message; the most recent claude session gets the message and the caller's outer loop (verify loop for `"mvn clean verify failures should be fixed"`, timeout loop for `"pls continue"`) decides whether to retry. Bounded by the same `maxClaudeSeconds` timeout as `run`; returns `TIMEOUT_EXIT_CODE` on timeout. No API-retry loop — different failure domain from `run`'s API-error retries.

**Example: ClaudeRunner.resume method body**
```java
public int resume(String workingDirectory, String message) throws Exception {
    List<String> command = new ArrayList<>();
    command.add(isWindows() ? "claude.cmd" : "claude");
    command.add("--resume");
    command.add("--print");
    command.add("--dangerously-skip-permissions");
    if (model != null && !model.isEmpty()) {
        command.add("--model");
        command.add(model);
    }
    command.add(message);
    List<String> outputLines = new ArrayList<>();
    int exitCode = executeCommand(command, workingDirectory, outputLines);
    if (exitCode == TIMEOUT_EXIT_CODE) return TIMEOUT_EXIT_CODE;
    getLog().debug("Claude CLI exited with code " + exitCode);
    return exitCode;
}
```

### executeCommand

Private helper shared by `run` and `resume` that owns the subprocess lifecycle: starts the process, streams stdout on a background daemon thread (so a hung claude can't block the main thread's `waitFor(timeout)`), calls `waitFor(maxClaudeSeconds, SECONDS)`, and on timeout calls `destroyForcibly()` + reaps + joins the reader thread before returning `TIMEOUT_EXIT_CODE`. The background reader is why stdout-based retryable-error detection still works: `outputLines` is populated as the process writes, and `readerThread.join()` before returning the exit code ensures the list is complete.

**Example: executeCommand body shape**
```java
private int executeCommand(List<String> command, String workingDirectory,
        List<String> outputLines) throws Exception {
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.directory(new File(workingDirectory));
    pb.redirectErrorStream(true);
    Process process = getStarter().start(pb);
    process.getOutputStream().close();

    Thread readerThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                synchronized (outputLines) { outputLines.add(line); }
            }
        } catch (Exception ignored) {}
    });
    readerThread.setDaemon(true);
    readerThread.start();

    boolean completed = process.waitFor(maxClaudeSeconds, TimeUnit.SECONDS);
    if (!completed) {
        process.destroyForcibly();
        process.waitFor(5, TimeUnit.SECONDS);
        readerThread.join(5000);
        return TIMEOUT_EXIT_CODE;
    }
    readerThread.join();
    return process.exitValue();
}
```

### getCurrentCommit

GitRunner convenience method that captures the current HEAD commit SHA via `git rev-parse HEAD`. Used by DarmokMojo to tag per-scenario metrics rows with the commit responsible for that cycle-time point.

**Example: GitRunner.getCurrentCommit method body**
```java
public String getCurrentCommit(String workingDirectory) throws Exception {
    return capture(workingDirectory, "rev-parse", "HEAD");
}
```

## DarmokMojoLog

### getLogFile

Returns the log file path for this DarmokMojoLog instance.

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

## DarmokMojoMetrics

### getFile

Returns the target file path for this metrics instance.

**Example: getFile method body**
```java
public Path getFile() {
    return file;
}
```

### append

Appends one data row to the CSV. Writes the header on the first call if the file does not yet exist. The gitBranch argument is stored verbatim in the `git_branch` column so SPC dashboards can group by run. Scenario names containing commas, quotes, or newlines are CSV-escaped.

**Example: append method body**
```java
public void append(String gitBranch, String commit, String scenario,
        long redMs, long greenMs, long refactorMs, long totalMs) throws IOException {
    Files.createDirectories(file.getParent());
    if (!Files.exists(file)) {
        Files.writeString(file, HEADER + "\n", StandardCharsets.UTF_8);
    }
    String row = LocalDateTime.now().format(TIMESTAMP) + ","
        + gitBranch + ","
        + commit + ","
        + escape(scenario) + ","
        + redMs + "," + greenMs + "," + refactorMs + "," + totalMs + "\n";
    Files.writeString(file, row, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
}
```

## {Tool}RunnerFactory

### create

Single abstract method of a functional interface. Interface body shown as its functional equivalent — production wires this via a constructor method reference (`{Tool}Runner::new`); tests substitute a lambda that binds a FakeProcessStarter.

**Example: create method body (functional equivalent)**
```java
GitRunner create(Log log) { return new GitRunner(log); }
```

**Example: Production wiring via constructor reference**
```java
GitRunnerFactory gitRunnerFactory = GitRunner::new;
```

**Example: Test wiring via FakeProcessStarter**
```java
mojo.setGitRunnerFactory(log -> new GitRunner(log, starter));
```
