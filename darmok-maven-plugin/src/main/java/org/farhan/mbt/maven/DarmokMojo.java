package org.farhan.mbt.maven;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class DarmokMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	public MavenProject project;

	// Path properties
	@Parameter(property = "specsDir", defaultValue = "../../sheep-dog-specs/sheep-dog-features")
	public String specsDir;

	@Parameter(property = "asciidocDir", defaultValue = "../../sheep-dog-specs/sheep-dog-features/src/test/resources/asciidoc/specs")
	public String asciidocDir;

	@Parameter(property = "scenariosFile", defaultValue = "scenarios-list.txt")
	public String scenariosFile;

	// Server properties
	@Parameter(property = "host", defaultValue = "dev.sheepdog.io")
	public String host;

	// Claude CLI properties
	@Parameter(property = "modelGreen", defaultValue = "sonnet")
	public String modelGreen;

	@Parameter(property = "modelRefactor", defaultValue = "sonnet")
	public String modelRefactor;

	@Parameter(property = "coAuthor", defaultValue = "Claude Sonnet 4.5 <noreply@anthropic.com>")
	public String coAuthor;

	@Parameter(property = "maxRetries", defaultValue = "3")
	public int maxRetries;

	@Parameter(property = "maxVerifyAttempts", defaultValue = "3")
	public int maxVerifyAttempts;

	@Parameter(property = "retryWaitSeconds", defaultValue = "30")
	public int retryWaitSeconds;

	// Upper bound on a single claude invocation (initial call or --resume).
	// Default 720s (12 min) is the UCL of the per-scenario runtime distribution
	// on the SPC dashboard; override via -DmaxClaudeSeconds=... until the
	// grafana read-back wiring exists. Timeout recovery lives in the phase
	// classes (GreenPhase/RefactorPhase); see issue 140.
	@Parameter(property = "maxClaudeSeconds", defaultValue = "720")
	public int maxClaudeSeconds;

	// How many times a phase will run claude + mvn clean install before
	// aborting. Counts the initial claude call; a value of 2 means "initial
	// call + one --resume retry".
	@Parameter(property = "maxTimeoutAttempts", defaultValue = "2")
	public int maxTimeoutAttempts;

	@Parameter(property = "maxAllowlistAttempts", defaultValue = "2")
	public int maxAllowlistAttempts;

	@Parameter(property = "onlyChanges", defaultValue = "true")
	public boolean onlyChanges;

	@Parameter(property = "claudeSessionIdEnabled", defaultValue = "true")
	public boolean claudeSessionIdEnabled;

	@Parameter(property = "baselineVerifyEnabled", defaultValue = "false")
	public boolean baselineVerifyEnabled;

	@Parameter(property = "stage", defaultValue = "true")
	public boolean stage;

	// Branch this Darmok run was configured for. Stored on every metrics row as
	// `git_branch` so SPC dashboards can overlay runs. No default — verified at
	// init-time against the actual git HEAD; mismatch or absence aborts the run.
	@Parameter(property = "gitBranch")
	public String gitBranch;

	// Metrics CSV output directory. Separate from LOG_PATH (which is shared with other
	// log-collecting processes and must stay their property) so metrics can be routed
	// independently — e.g. into the Grafana-readable hostPath for SPC dashboards.
	// Default: project baseDir, so metrics.csv survives `runCleanUp` out of the box.
	@Parameter(property = "targetProject")
	String targetProject;

	@Parameter(property = "metricsDir")
	public String metricsDir;

	// Instance fields
	String baseDir;
	private GitRunner git;
	private MavenRunner maven;
	DarmokMojoLog mojoLog;
	DarmokMojoLog runnerLog;
	DarmokMojoMetrics metrics;
	RedPhase redPhase;
	GreenPhase greenPhase;
	RefactorPhase refactorPhase;

	// Runner factories — default to production constructors; tests override via setters.
	GitRunnerFactory gitRunnerFactory = GitRunner::new;
	MavenRunnerFactory mavenRunnerFactory = MavenRunner::new;
	ClaudeRunnerFactory claudeRunnerFactory = ClaudeRunner::new;

	record ScenarioEntry(String file, String scenario, String tag) {}

	// =========================================================================
	// Lifecycle
	// =========================================================================

	public final void execute() throws MojoExecutionException {
		try {
			init();
			mojoLog.info("RGR Automation Plugin (" + goalName() + ")");

			int cleanUpExit = cleanWorkspace();
			if (cleanUpExit != 0) {
				throw new MojoExecutionException("Clean up failed with exit code " + cleanUpExit);
			}

			verifyBaseline();

			int totalProcessed = doExecute();

			mojoLog.info("RGR Automation Complete!");
			mojoLog.info("Total scenarios processed: " + totalProcessed);
		} catch (MojoExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			closeLogs();
		}
	}

	/** Goal-specific scenario iteration. Returns the number of scenarios processed. */
	protected abstract int doExecute() throws Exception;

	/** Short goal name for the "RGR Automation Plugin (&lt;name&gt;)" banner log line. */
	protected abstract String goalName();

	private void init() throws Exception {
		if (baseDir == null) {
			baseDir = project.getBasedir().getAbsolutePath();
		}
		initLogs();
		git = gitRunnerFactory.create(runnerLog);
		verifyGitBranch();
		maven = mavenRunnerFactory.create(runnerLog);
		String sheepDogRoot = baseDir + "/../..";
		String artifactId = targetProject != null && !targetProject.isEmpty()
			? targetProject : project.getArtifactId();
		GitRunner phaseGit = gitRunnerFactory.create(runnerLog);
		redPhase = new RedPhase(maven, mojoLog, baseDir, specsDir, host, onlyChanges);
		greenPhase = new GreenPhase(
			claudeRunnerFactory.create(runnerLog, modelGreen, maxRetries, retryWaitSeconds, maxClaudeSeconds,
				claudeSessionIdEnabled, () -> UUID.randomUUID().toString()),
			maven, phaseGit, mojoLog, sheepDogRoot, baseDir, artifactId, maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds,
			maxAllowlistAttempts);
		refactorPhase = new RefactorPhase(
			claudeRunnerFactory.create(runnerLog, modelRefactor, maxRetries, retryWaitSeconds, maxClaudeSeconds,
				claudeSessionIdEnabled, () -> UUID.randomUUID().toString()),
			maven, phaseGit, mojoLog, sheepDogRoot, baseDir, artifactId, maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds,
			maxAllowlistAttempts);
	}

	/** Test-only setter. Lets tests pre-seed baseDir before execute() so init() skips the MavenProject path. */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	/** Test-only setter. Substitutes a mock GitRunner factory; default is {@code GitRunner::new}. */
	public void setGitRunnerFactory(GitRunnerFactory factory) {
		this.gitRunnerFactory = factory;
	}

	/** Test-only setter. Substitutes a mock MavenRunner factory; default is {@code MavenRunner::new}. */
	public void setMavenRunnerFactory(MavenRunnerFactory factory) {
		this.mavenRunnerFactory = factory;
	}

	/** Test-only setter. Substitutes a mock ClaudeRunner factory; default is {@code ClaudeRunner::new}. */
	public void setClaudeRunnerFactory(ClaudeRunnerFactory factory) {
		this.claudeRunnerFactory = factory;
	}

	private void verifyGitBranch() throws MojoExecutionException, Exception {
		if (gitBranch == null || gitBranch.isEmpty()) {
			String msg = "gitBranch parameter is required";
			mojoLog.error(msg);
			throw new MojoExecutionException(msg);
		}
		String actualBranch = git.getCurrentBranch(baseDir);
		if (!gitBranch.equals(actualBranch)) {
			String msg = "Darmok configured for branch '" + gitBranch
				+ "' but current HEAD is on '" + actualBranch + "'. Aborting.";
			mojoLog.error(msg);
			throw new MojoExecutionException(msg);
		}
	}

	private void verifyBaseline() throws MojoExecutionException, Exception {
		if (!baselineVerifyEnabled) {
			return;
		}
		int exit = maven.run(baseDir, "clean", "install");
		if (exit != 0) {
			String msg = "Baseline build failed. Aborting.";
			mojoLog.error(msg);
			throw new MojoExecutionException(msg);
		}
	}

	private Path resolveLogDir() {
		String logPath = System.getProperty("LOG_PATH");
		if (logPath == null || logPath.isEmpty()) {
			logPath = System.getenv("LOG_PATH");
		}
		if (logPath != null && !logPath.isEmpty()) {
			return Path.of(logPath);
		}
		// Default: project root, sibling of target/ so runCleanUp doesn't delete the file.
		return Path.of(baseDir);
	}

	private Path resolveMetricsDir() {
		if (metricsDir != null && !metricsDir.isEmpty()) {
			return Path.of(metricsDir);
		}
		// Default: project root. Override via -DmetricsDir=... to route metrics.csv
		// into a hostPath shared with the Grafana pod.
		return Path.of(baseDir);
	}

	private void initLogs() throws Exception {
		Path logDir = resolveLogDir();
		String date = LocalDate.now().toString();
		mojoLog = new DarmokMojoLog(getLog(), "mojo", logDir.resolve("darmok.mojo." + date + ".log"));
		runnerLog = new DarmokMojoLog(getLog(), "runner", logDir.resolve("darmok.runners." + date + ".log"));
		metrics = new DarmokMojoMetrics(resolveMetricsDir().resolve("metrics.csv"));
	}

	private void closeLogs() {
		if (mojoLog != null) mojoLog.close();
		if (runnerLog != null) runnerLog.close();
	}

	// =========================================================================
	// Scenario Iterator and Processor
	// =========================================================================

	protected ScenarioEntry getNextScenario() throws Exception {
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

	protected void processScenario(ScenarioEntry entry) throws MojoExecutionException, Exception {
		String scenarioName = entry.scenario();
		String tag = entry.tag();
		String fileName = entry.file();
		long totalStart = System.currentTimeMillis();

		if ("NoTag".equals(tag)) {
			mojoLog.info("  Skipping (NoTag)");
			removeFirstScenarioFromFile();
			return;
		}

		// Add tag to asciidoc file
		addTagToAsciidoc(fileName, scenarioName, tag);

		DarmokMojoState state = new DarmokMojoState(scenarioName, gitBranch, tag);

		state = redPhase.run(state);
		if (state.exitCode != 0 && state.exitCode != 100) {
			throw new MojoExecutionException("rgr-red failed with exit code " + state.exitCode);
		}

		if (state.exitCode == 100) {
			// Tests already passing — no green/refactor phase runs, so the commit
			// message omits the phase suffix (matches the stage=true combined case).
			mojoLog.info("  Green: Skipped (tests already passing)");
			removeFirstScenarioFromFile();
			git.run(baseDir, "add", ".");
			commitIfChanged("run-rgr " + scenarioName, "Red");
		} else {
			// Tests failing — commit red, then run green and refactor
			git.run(baseDir, "add", ".");
			if (stage) {
				commitIfChanged("run-rgr " + scenarioName, "Red");
			} else {
				commitIfChanged("run-rgr red " + scenarioName, "Red");
			}

			state = greenPhase.run(state);
			if (state.exitCode != 0) {
				throw new MojoExecutionException("rgr-green failed with exit code " + state.exitCode);
			}

			removeFirstScenarioFromFile();
			git.run(baseDir, "add", ".");
			if (!stage) {
				commitIfChanged("run-rgr green " + scenarioName, "Green");
			}

			state = refactorPhase.run(state);
			if (state.exitCode != 0) {
				throw new MojoExecutionException("rgr-refactor failed with exit code " + state.exitCode);
			}

			git.run(baseDir, "add", ".");
			if (stage) {
				amendIfChanged("Refactor");
			} else {
				commitIfChanged("run-rgr refactor " + scenarioName, "Refactor");
			}
		}

		// Capture the HEAD commit AFTER all scenario commits have been made.
		// This is the commit this scenario produced (stage=true single commit,
		// stage=false refactor commit, or the skip-path red commit). The metric
		// row is attributable to this commit.
		state.commit = git.getCurrentCommit(baseDir);
		mojoLog.info("  Commit: " + state.commit);

		state.totalDurationMs = System.currentTimeMillis() - totalStart;
		metrics.append(state);
	}

	private void commitIfChanged(String message, String phase) throws Exception {
		int diffQuietExit = git.run(baseDir, "diff", "--cached", "--quiet");
		if (diffQuietExit != 0) {
			String commitMsg = message + "\n\nCo-Authored-By: " + coAuthor;
			mojoLog.info("  " + phase + ": Committing");
			git.run(baseDir, "commit", "-m", commitMsg);
		}
	}

	private void amendIfChanged(String phase) throws Exception {
		int diffQuietExit = git.run(baseDir, "diff", "--cached", "--quiet");
		if (diffQuietExit != 0) {
			mojoLog.info("  " + phase + ": Committing");
			git.run(baseDir, "commit", "--amend", "--no-edit");
		}
	}

	private void removeFirstScenarioFromFile() throws Exception {
		Path scenariosPath = Path.of(baseDir, scenariosFile);
		List<String> lines = Files.readAllLines(scenariosPath, StandardCharsets.UTF_8);

		// Find the end of the first scenario entry (File + Scenario + Tag block)
		int removeUntil = 0;
		boolean foundScenario = false;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("    Tag: ") && !foundScenario) {
				foundScenario = true;
				removeUntil = i + 1;
				// Skip any blank lines after the tag
				while (removeUntil < lines.size() && lines.get(removeUntil).trim().isEmpty()) {
					removeUntil++;
				}
				break;
			}
		}

		if (!foundScenario) {
			// File has no valid entries, clear it
			Files.writeString(scenariosPath, "", StandardCharsets.UTF_8);
			return;
		}

		// Check if the next entry has a different File: header or another Scenario: under same file
		List<String> remaining = new ArrayList<>(lines.subList(removeUntil, lines.size()));

		// If next line is "  Scenario:" (same file), we need to keep the File: header
		if (!remaining.isEmpty() && remaining.get(0).startsWith("  Scenario: ")) {
			// Re-add the File: header from the removed entry
			String fileHeader = "";
			for (String line : lines) {
				if (line.startsWith("File: ")) {
					fileHeader = line;
					break;
				}
			}
			remaining.add(0, fileHeader);
		}

		if (remaining.isEmpty()) {
			Files.writeString(scenariosPath, "", StandardCharsets.UTF_8);
		} else {
			writeFileWithLF(scenariosPath.toString(), remaining);
		}
	}

	// =========================================================================
	// Scenario Parsing
	// =========================================================================

	private List<ScenarioEntry> parseScenarios(String scenariosFilePath) throws Exception {
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

	// =========================================================================
	// AsciiDoc Tag Insertion
	// =========================================================================

	private boolean addTagToAsciidoc(String fileName, String scenarioName, String tag) throws Exception {
		String filePath = baseDir + "/" + asciidocDir + "/" + fileName + ".asciidoc";
		File file = new File(filePath);
		if (!file.exists()) {
			mojoLog.warn("File not found: " + fileName + ".asciidoc");
			return false;
		}

		List<String> content = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			if (!line.matches("^== Test-Case: .+$")) {
				continue;
			}
			String testCaseName = line.substring("== Test-Case: ".length());
			if (!testCaseName.equals(scenarioName)) {
				continue;
			}
			return insertTagAtTestCase(content, i, tag, filePath);
		}

		mojoLog.warn("Scenario not found in file: " + scenarioName);
		return false;
	}

	private boolean insertTagAtTestCase(List<String> content, int headerIndex, String tag, String filePath) throws Exception {
		int nextLineIndex = headerIndex + 1;
		while (nextLineIndex < content.size() && content.get(nextLineIndex).trim().isEmpty()) {
			nextLineIndex++;
		}

		if (nextLineIndex < content.size() && content.get(nextLineIndex).trim().startsWith("@")) {
			String tagLine = content.get(nextLineIndex).trim();
			for (String existingTag : tagLine.split(" ")) {
				if (existingTag.equals("@" + tag)) {
					mojoLog.debug("  Tag @" + tag + " already present in file");
					return false;
				}
			}
			content.set(nextLineIndex, tagLine + " @" + tag);
		} else {
			content.add(headerIndex + 1, "@" + tag);
			content.add(headerIndex + 1, "");
		}

		mojoLog.debug("  Added tag @" + tag + " to file");
		writeFileWithLF(filePath, content);
		return true;
	}

	private int cleanWorkspace() throws Exception {
		Path sheepDogMain = Path.of(baseDir, "../..").normalize();

		int deleted = deleteNulFiles(sheepDogMain);
		mojoLog.debug("  Cleanup: Deleted " + deleted + " NUL files");

		deleteDirectory(Path.of(baseDir, "target"));
		mojoLog.debug("  Cleanup: Deleted target directory");
		return 0;
	}

	private int deleteNulFiles(Path root) throws Exception {
		int[] count = { 0 };
		Files.walk(root)
			.filter(Files::isRegularFile)
			.filter(p -> {
				String name = p.getFileName().toString();
				return "NUL".equals(name) || "nul".equals(name);
			})
			.forEach(p -> {
				try {
					mojoLog.debug("  Deleting: " + p);
					Files.delete(p);
					count[0]++;
				} catch (Exception e) {
					mojoLog.debug("  Warning: Could not delete " + p + ": " + e.getMessage());
				}
			});
		return count[0];
	}

	private void deleteDirectory(Path dir) throws Exception {
		if (!Files.exists(dir)) {
			return;
		}
		Files.walk(dir)
			.sorted(Comparator.reverseOrder())
			.forEach(p -> {
				try {
					Files.delete(p);
				} catch (Exception e) {
					mojoLog.debug("  Warning: Could not delete " + p + ": " + e.getMessage());
				}
			});
	}

	// =========================================================================
	// File I/O Helpers
	// =========================================================================

	private void writeFileWithLF(String filePath, List<String> lines) throws Exception {
		String content = String.join("\n", lines) + "\n";
		Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8);
	}
}
