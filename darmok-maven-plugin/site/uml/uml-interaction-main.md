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

Maven goal entry point — invoked by the Maven framework via the `@Mojo(name = ...)` annotation. Tests exercise it through `MavenTestObject.executeMojo`, which constructs the mojo, wires the project + test seams, and calls `execute()`. The actual implementation lives on `DarmokMojo` (final); see that class's `### execute` below.

**Example: test-side invocation in `MavenTestObject.executeMojo`** (`src/test/java/org/farhan/common/MavenTestObject.java`)
```java
DarmokMojo mojo = mojoClass.getConstructor().newInstance();
MavenProject project = new MavenProject();
project.setArtifactId("code-prj");
mojo.project = project;
mojo.setBaseDir(codePrjDir.toString());
// ... wire runner factories with FakeProcessStarter, populate @Parameter fields ...
mojo.execute();
```

## DarmokMojo

The same `MavenTestObject.executeMojo` snippet shown under `{Goal}Mojo.execute` exercises every public method in `DarmokMojo` (the `@Parameter` fields, `setBaseDir`, and the three runner-factory setters). Each section below points back to that snippet and shows the relevant call line.

### execute

Maven plugin lifecycle entry point. Marked `final` on `DarmokMojo` so concrete `{Goal}Mojo` subclasses contribute via `doExecute()` and `goalName()` rather than overriding `execute()`. The Maven framework calls `mojo.execute()` on the goal subclass; the polymorphic dispatch lands on `DarmokMojo`'s implementation, which wraps `init()` / `cleanWorkspace()` / `verifyBaseline()` / `doExecute()` / log close in the canonical try/finally.

**Example: lifecycle wrapper in `DarmokMojo.execute`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
public final void execute() throws MojoExecutionException {
    try {
        init();
        // ... cleanWorkspace, verifyBaseline, doExecute, log totals ...
    } catch (MojoExecutionException e) {
        throw e;
    } catch (Exception e) {
        throw new MojoExecutionException(e.getMessage(), e);
    } finally {
        closeLogs();
    }
}
```

### setBaseDir

Test-only setter that pre-seeds `baseDir` so `init()` skips the `MavenProject` path. Called by the test harness before `execute()`.

**Example: pre-seed baseDir in `MavenTestObject.executeMojo`**
```java
mojo.setBaseDir(codePrjDir.toString());
```

### set{Tool}RunnerFactory

Test-only setters that swap the production runner factories (`{Tool}Runner::new`) for lambdas that bind a `FakeProcessStarter`. Called by `MavenTestObject.executeMojo` so no real subprocesses are spawned. Three setters: `setGitRunnerFactory`, `setMavenRunnerFactory`, `setClaudeRunnerFactory`.

**Example: factory wiring in `MavenTestObject.executeMojo`**
```java
ProcessStarter starter = (ProcessStarter) getProperty("processStarter");
mojo.setGitRunnerFactory(log -> new GitRunner(log, starter));
mojo.setMavenRunnerFactory(log -> new MavenRunner(log, starter));
mojo.setClaudeRunnerFactory((log, model, retries, wait, maxSeconds, sessionEnabled, uuidSupplier) ->
    new ClaudeRunner(log, model, retries, wait, maxSeconds, sessionEnabled, testUuidSupplier, starter));
```

## RgrPhase

### run

Public template invoked by `DarmokMojo.processScenario`. The template times `executeClaudeOrMaven`, optionally invokes `runVerifyLoop`, writes `exitCode` and the per-phase duration onto `state`, and returns it. Sample usage from the orchestrator's perspective:

**Example: phase chaining in `DarmokMojo.processScenario`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
DarmokMojoState state = new DarmokMojoState(scenarioName, gitBranch, tag);

state = redPhase.run(state);
if (state.exitCode != 0 && state.exitCode != 100) {
    throw new MojoExecutionException("rgr-red failed with exit code " + state.exitCode);
}
if (state.exitCode != 100) {
    state = greenPhase.run(state);
    if (state.exitCode != 0) throw new MojoExecutionException(...);
    state = refactorPhase.run(state);
    if (state.exitCode != 0) throw new MojoExecutionException(...);
}
```

## {RgrPhase}Phase

### prepareSession

`RefactorPhase`-only. Pre-phase hook called by `DarmokMojo.processScenario` **before** `refactorPhase.run(state)` so the work it performs is excluded from the timed `phase_refactor_ms` window. No-op when `refactorSessionMode=fresh`. When `refactorSessionMode=continue` (issue #287) it copies green's UUID into refactor's `ClaudeRunner` and issues `claude --resume <green-uuid> /compact` to scope refactor's review to the files green just touched.

**Example: untimed session inheritance in `DarmokMojo.processScenario`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
refactorPhase.prepareSession(greenPhase.claude);
state = refactorPhase.run(state);
```

## ProcessRunner

### run

Public entry point shared by all `{Tool}Runner` subclasses (Claude overrides; Git/Maven inherit unchanged). Callers pass a working directory and command arguments; the runner returns the subprocess exit code with stdout streamed to the wrapped `Log`.

**Example: maven goal invocation in `RgrPhase.runVerifyLoop`** (`src/main/java/org/farhan/mbt/maven/RgrPhase.java`)
```java
int verifyExit = maven.run(targetDir, "clean", "verify");
```

**Example: git invocation in `DarmokMojo.commitIfChanged`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
git.run(baseDir, "add", ".");
int diffQuietExit = git.run(baseDir, "diff", "--cached", "--quiet");
git.run(baseDir, "commit", "-m", commitMsg);
```

## {Tool}Runner

### run

`ClaudeRunner` overrides `ProcessRunner.run` to add an API-retry loop (500/529/overloaded patterns) and a per-invocation `maxClaudeSeconds` timeout that returns the sentinel `TIMEOUT_EXIT_CODE` on expiry. The timeout is two-phase: `waitFor(maxClaudeSeconds, SECONDS)` on the process handle, then `readerThread.join(maxClaudeSeconds * 1000L)` on the stdout reader. Hitting either bound forces `destroyForcibly()` on the subprocess (which also releases the reader's blocking read) and returns the sentinel. The reader-half bound is what prevents the issue 290 failure mode — parent `claude.cmd` exits cleanly while a grandchild holds stdout open silent — from blocking the main thread past the budget. Phases call `run` via the polymorphic interface; the timeout sentinel is what triggers phase-side recovery.

**Example: claude invocation in `GreenPhase.executeClaudeOrMaven`** (`src/main/java/org/farhan/mbt/maven/GreenPhase.java`)
```java
int claudeExit = claude.run(workingDir, "/rgr-green " + artifactId + " " + pattern);
return runTimeoutRecoveryLoop(claudeExit);
```

**Example: claude invocation in `RefactorPhase.executeClaudeOrMaven`** (`src/main/java/org/farhan/mbt/maven/RefactorPhase.java`)
```java
int claudeExit = claude.run(workingDir, "/rgr-refactor forward " + artifactId);
return runTimeoutRecoveryLoop(claudeExit);
```

### resume

`ClaudeRunner`-only. Single-shot `claude --resume <message>` that nudges the latest claude session forward. Called by the verify and timeout-recovery loops; the resume's exit code is intentionally ignored (the next `mvn clean install` / `mvn clean verify` is the authoritative signal).

**Example: verify-failure recovery in `RgrPhase.runVerifyLoop`** (`src/main/java/org/farhan/mbt/maven/RgrPhase.java`)
```java
claude.resume(workingDir, VERIFY_RESUME_MESSAGE);  // "mvn clean verify failures should be fixed"
```

**Example: timeout recovery in `RgrPhase.runTimeoutRecoveryLoop`** (`src/main/java/org/farhan/mbt/maven/RgrPhase.java`)
```java
claude.resume(workingDir, TIMEOUT_RESUME_MESSAGE);  // "pls continue"
```

### getSessionId

`ClaudeRunner`-only. Returns the UUID captured on the runner's first `--print` call. Read by `RefactorPhase.prepareSession` (issue #287) when `refactorSessionMode=continue` so refactor can inherit green's session.

**Example: read green's UUID in `RefactorPhase.prepareSession`** (`src/main/java/org/farhan/mbt/maven/RefactorPhase.java`)
```java
claude.setSessionId(greenClaude.getSessionId());
```

### setSessionId

`ClaudeRunner`-only. Overrides the captured UUID before the next claude call so a sibling phase can inherit an existing session. Called by `RefactorPhase.prepareSession` (issue #287) to copy green's UUID into refactor's runner; the next `claude --resume` (issued for `/compact` and then `/rgr-refactor`) carries the inherited UUID.

**Example: inherit green's session in `RefactorPhase.prepareSession`** (`src/main/java/org/farhan/mbt/maven/RefactorPhase.java`)
```java
public void prepareSession(ClaudeRunner greenClaude) throws Exception {
    if (!"continue".equals(refactorSessionMode)) {
        return;
    }
    claude.setSessionId(greenClaude.getSessionId());
    claude.resume(workingDir, "/compact");
}
```

### getCurrentCommit

`GitRunner`-only. Captures the HEAD commit SHA via `git rev-parse HEAD`. Called once per scenario after all commits are made, to tag the metrics row with the responsible commit.

**Example: per-scenario commit capture in `DarmokMojo.processScenario`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
state.commit = git.getCurrentCommit(baseDir);
mojoLog.info("  Commit: " + state.commit);
```

### getCurrentBranch

`GitRunner`-only. Captures the current branch name via `git rev-parse --abbrev-ref HEAD`. Called by `DarmokMojo.verifyGitBranch` (init-time) to confirm the configured `gitBranch` parameter matches the actual git HEAD.

**Example: init-time branch verification in `DarmokMojo.verifyGitBranch`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
String actualBranch = git.getCurrentBranch(baseDir);
if (!gitBranch.equals(actualBranch)) {
    throw new MojoExecutionException("Darmok configured for branch '" + gitBranch
        + "' but current HEAD is on '" + actualBranch + "'. Aborting.");
}
```

## DarmokMojoState

### setDuration

Called by `RgrPhase.run` template at the end of every phase to record the elapsed milliseconds for that phase.

**Example: phase template recording its slot in `RgrPhase.run`** (`src/main/java/org/farhan/mbt/maven/RgrPhase.java`)
```java
state.exitCode = exitCode;
state.setDuration(phase(), duration);
return state;
```

### getDuration

Called by `DarmokMojoMetrics.append` to read each phase's duration into the CSV row.

**Example: per-phase column read in `DarmokMojoMetrics.append`** (`src/main/java/org/farhan/mbt/maven/DarmokMojoMetrics.java`)
```java
+ state.getDuration(Phase.RED) + ","
+ state.getDuration(Phase.GREEN) + ","
+ state.getDuration(Phase.REFACTOR) + ","
```

### formatDuration

Static helper used to format millisecond counts as HH:MM:SS for log lines. Called by `RgrPhase.run` (phase completion) and `RgrPhase.runVerifyLoop` (verify completion).

**Example: phase-completion log line in `RgrPhase.run`** (`src/main/java/org/farhan/mbt/maven/RgrPhase.java`)
```java
mojoLog.info("  " + name + ": Completed (" + DarmokMojoState.formatDuration(duration) + ")");
```

## DarmokMojoFile

### getFile

Returns the target file path. Inherited by both subclasses; called by test impls to feed `getFileState`.

**Example: file-state read in `DarmokMojoLogFileImpl`** (`src/test/java/org/farhan/impl/DarmokMojoLogFileImpl.java`)
```java
return getFileState(getDarmokMojoLog("darmok.mojo").getFile());
```

## DarmokMojo{DataFileType}

### findDatedLog

`DarmokMojoLog`-only static helper. Resolves a dated log file by prefix in a directory.

**Example: log discovery in `MavenTestObject.getDarmokMojoLog`** (`src/test/java/org/farhan/common/MavenTestObject.java`)
```java
DarmokMojoLog mojoLog = new DarmokMojoLog(DarmokMojoLog.findDatedLog(logDir, prefix));
```

### is{LogLevel}Enabled

`DarmokMojoLog`-only. Delegates to the wrapped `Log`. Used by Maven's logging machinery to skip computing log-line content when the level is disabled.

**Example: level-gated logging (called by Maven framework)**
```java
if (mojoLog.isDebugEnabled()) {
    mojoLog.debug("expensive: " + computeDetail());
}
```

### {logLevel}(CharSequence)

`DarmokMojoLog`-only. Forwards to the wrapped `Log` and writes a timestamped line to the log file. Called by every phase to log progress markers.

**Example: phase progress logging in `RgrPhase.run`** (`src/main/java/org/farhan/mbt/maven/RgrPhase.java`)
```java
mojoLog.info("  " + name + ": Running...");
mojoLog.info("  " + name + ": Completed (" + DarmokMojoState.formatDuration(duration) + ")");
mojoLog.warn("  " + name + ": Verify failed (attempt " + attempt + "/" + maxVerifyAttempts + "), resuming claude...");
mojoLog.error("  " + name + ": Verify failed after " + maxVerifyAttempts + " attempts, aborting");
```

### {logLevel}(CharSequence, Throwable)

`DarmokMojoLog`-only. Forwards to the wrapped `Log` and writes timestamped line plus stack trace.

**Example: contextual error log**
```java
mojoLog.error("Failed in checkTestStep for : " + e.getMessage(), e);
```

### {logLevel}(Throwable)

`DarmokMojoLog`-only. Forwards to the wrapped `Log` and writes the stack trace.

**Example: bare-throwable error log**
```java
mojoLog.error(e);
```

### close

`DarmokMojoLog`-only. Closes the underlying `PrintWriter`. Called by `DarmokMojo.cleanup` for both logs.

**Example: lifecycle close in `DarmokMojo.cleanup`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
if (mojoLog != null) mojoLog.close();
if (runnerLog != null) runnerLog.close();
```

### matchAndGet{Field}

Inherits `ensureMatched` from `DarmokMojoFile`. Each subclass exposes its own field set — `DarmokMojoLog` returns Level/Category/Content from the matched `LogEntry`; `DarmokMojoMetrics` returns one of the eight CSV columns from the matched row map. Test impls dispatch the cucumber row's `keyMap` to the right column accessor.

**Example: log-row inspection in `DarmokMojoLogFileImpl`** (`src/test/java/org/farhan/impl/DarmokMojoLogFileImpl.java`)
```java
@Override public String getLevel(HashMap<String, String> keyMap) {
    return getDarmokMojoLog("darmok.mojo").matchAndGetLevel(keyMap);
}
@Override public String getCategory(HashMap<String, String> keyMap) {
    return getDarmokMojoLog("darmok.mojo").matchAndGetCategory(keyMap);
}
@Override public String getContent(HashMap<String, String> keyMap) {
    return getDarmokMojoLog("darmok.mojo").matchAndGetContent(keyMap);
}
```

**Example: metrics-row inspection in `MetricsCsvFileImpl`** (`src/test/java/org/farhan/impl/MetricsCsvFileImpl.java`)
```java
@Override public String getCommit(HashMap<String, String> keyMap) {
    return metrics().matchAndGetCommit(keyMap);
}
@Override public String getPhaseRedMs(HashMap<String, String> keyMap) {
    return metrics().matchAndGetPhaseRedMs(keyMap);
}
```

### append

`DarmokMojoMetrics`-only. Called once per scenario by `DarmokMojo.processScenario` to write the cycle-time row.

**Example: per-scenario metrics row in `DarmokMojo.processScenario`** (`src/main/java/org/farhan/mbt/maven/DarmokMojo.java`)
```java
state.commit = git.getCurrentCommit(baseDir);
state.totalDurationMs = System.currentTimeMillis() - totalStart;
metrics.append(state);
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
