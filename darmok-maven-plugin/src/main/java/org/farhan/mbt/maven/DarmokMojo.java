package org.farhan.mbt.maven;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
	@Parameter(property = "modelRed", defaultValue = "sonnet")
	public String modelRed;

	@Parameter(property = "modelGreen", defaultValue = "sonnet")
	public String modelGreen;

	@Parameter(property = "modelRefactor", defaultValue = "sonnet")
	public String modelRefactor;

	@Parameter(property = "coAuthor", defaultValue = "Claude Sonnet 4.5 <noreply@anthropic.com>")
	public String coAuthor;

	@Parameter(property = "maxRetries", defaultValue = "3")
	public int maxRetries;

	@Parameter(property = "retryWaitSeconds", defaultValue = "30")
	public int retryWaitSeconds;

	@Parameter(property = "onlyChanges", defaultValue = "true")
	public boolean onlyChanges;

	@Parameter(property = "stage", defaultValue = "true")
	public boolean stage;

	// Metrics CSV output directory. Separate from LOG_PATH (which is shared with other
	// log-collecting processes and must stay their property) so metrics can be routed
	// independently — e.g. into the Grafana-readable hostPath for SPC dashboards.
	// Default: project baseDir, so metrics.csv survives `runCleanUp` out of the box.
	@Parameter(property = "metricsDir")
	public String metricsDir;

	// Instance fields
	String baseDir;
	private GitRunner git;
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
			mojoLog, sheepDogRoot, artifactId);
		refactorPhase = new RefactorPhase(
			claudeRunnerFactory.create(runnerLog, modelRefactor, maxRetries, retryWaitSeconds),
			mojoLog, sheepDogRoot, artifactId);
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

	void cleanup() {
		closeLogs();
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

	void processScenario(ScenarioEntry entry) throws MojoExecutionException, Exception {
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

		PhaseResult redResult = redPhase.run(tag);
		if (redResult.exitCode() != 0 && redResult.exitCode() != 100) {
			throw new MojoExecutionException("rgr-red failed with exit code " + redResult.exitCode());
		}

		long greenDuration = 0;
		long refactorDuration = 0;

		if (redResult.exitCode() == 100) {
			// Tests already passing — no green/refactor phase runs, so the commit
			// message omits the phase suffix (matches the stage=true combined case).
			mojoLog.info("  Green: Skipped (tests already passing)");
			removeFirstScenarioFromFile();
			git.run(baseDir, "add", ".");
			commitIfChanged("run-rgr " + scenarioName, "Red");
		} else {
			// Tests failing — commit red, then run green and refactor
			git.run(baseDir, "add", ".");
			if (!stage) {
				commitIfChanged("run-rgr red " + scenarioName, "Red");
			}

			PhaseResult greenResult = greenPhase.run(tag);
			greenDuration = greenResult.durationMs();
			if (greenResult.exitCode() != 0) {
				throw new MojoExecutionException("rgr-green failed with exit code " + greenResult.exitCode());
			}

			removeFirstScenarioFromFile();
			git.run(baseDir, "add", ".");
			if (!stage) {
				commitIfChanged("run-rgr green " + scenarioName, "Green");
			}

			PhaseResult refactorResult = refactorPhase.run();
			refactorDuration = refactorResult.durationMs();
			if (refactorResult.exitCode() != 0) {
				throw new MojoExecutionException("rgr-refactor failed with exit code " + refactorResult.exitCode());
			}

			git.run(baseDir, "add", ".");
			String commitMessage = stage ? "run-rgr " + scenarioName : "run-rgr refactor " + scenarioName;
			commitIfChanged(commitMessage, "Refactor");
		}

		// Capture the HEAD commit AFTER all scenario commits have been made.
		// This is the commit this scenario produced (stage=true single commit,
		// stage=false refactor commit, or the skip-path red commit). The metric
		// row is attributable to this commit.
		String commit = git.getCurrentCommit(baseDir);
		mojoLog.info("  Commit: " + commit);

		long totalDuration = System.currentTimeMillis() - totalStart;
		metrics.append(commit, scenarioName,
			redResult.durationMs(), greenDuration, refactorDuration, totalDuration);
	}

	private void commitIfChanged(String message, String phase) throws Exception {
		int diffQuietExit = git.run(baseDir, "diff", "--cached", "--quiet");
		if (diffQuietExit != 0) {
			String commitMsg = message + "\n\nCo-Authored-By: " + coAuthor;
			mojoLog.info("  " + phase + ": Committing");
			git.run(baseDir, "commit", "-m", commitMsg);
		}
	}

	void removeFirstScenarioFromFile() throws Exception {
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

	// =========================================================================
	// AsciiDoc Tag Insertion
	// =========================================================================

	boolean addTagToAsciidoc(String fileName, String scenarioName, String tag) throws Exception {
		String filePath = baseDir + "/" + asciidocDir + "/" + fileName + ".asciidoc";
		File file = new File(filePath);
		if (!file.exists()) {
			mojoLog.warn("File not found: " + fileName + ".asciidoc");
			return false;
		}

		List<String> content = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
		List<String> newContent = new ArrayList<>();

		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			newContent.add(line);

			if (line.matches("^== Test-Case: .+$")) {
				String testCaseName = line.substring("== Test-Case: ".length());
				if (testCaseName.equals(scenarioName)) {
					// Find the first non-empty line after the Test-Case header
					int nextLineIndex = i + 1;
					while (nextLineIndex < content.size() && content.get(nextLineIndex).trim().isEmpty()) {
						nextLineIndex++;
					}

					boolean hasTagLine = false;
					boolean alreadyHasTag = false;
					if (nextLineIndex < content.size()) {
						String nextLine = content.get(nextLineIndex).trim();
						if (nextLine.startsWith("@")) {
							hasTagLine = true;
							for (String existingTag : nextLine.split(" ")) {
								if (existingTag.equals("@" + tag)) {
									alreadyHasTag = true;
									break;
								}
							}
						}
					}

					if (!alreadyHasTag) {
						if (hasTagLine) {
							// Append tag to existing tag line
							for (int k = i + 1; k < nextLineIndex; k++) {
								newContent.add(content.get(k));
							}
							newContent.add(content.get(nextLineIndex).trim() + " @" + tag);
							for (int j = nextLineIndex + 1; j < content.size(); j++) {
								newContent.add(content.get(j));
							}
						} else {
							newContent.add("");
							newContent.add("@" + tag);
							for (int j = i + 1; j < content.size(); j++) {
								newContent.add(content.get(j));
							}
						}
						mojoLog.debug("  Added tag @" + tag + " to file");
						writeFileWithLF(filePath, newContent);
						return true;
					} else {
						mojoLog.debug("  Tag @" + tag + " already present in file");
						return false;
					}
				}
			}
		}

		mojoLog.warn("Scenario not found in file: " + scenarioName);
		return false;
	}

	int runCleanUp() throws Exception {
		Path sheepDogMain = Path.of(baseDir, "../..").normalize();

		int deleted = deleteNulFiles(sheepDogMain);
		mojoLog.debug("  Cleanup: Deleted " + deleted + " NUL files");

		deleteDirectory(Path.of(baseDir, "target"));
		mojoLog.debug("  Cleanup: Deleted target directory");
		return 0;
	}

	int deleteNulFiles(Path root) throws Exception {
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

	void deleteDirectory(Path dir) throws Exception {
		if (!Files.exists(dir)) {
			return;
		}
		Files.walk(dir)
			.sorted(java.util.Comparator.reverseOrder())
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

	void writeFileWithLF(String filePath, List<String> lines) throws Exception {
		String content = String.join("\n", lines) + "\n";
		Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8);
	}
}
