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

	// Pipeline property
	@Parameter(property = "pipeline", defaultValue = "forward")
	public String pipeline;

	@Parameter(property = "onlyChanges", defaultValue = "true")
	public boolean onlyChanges;

	@Parameter(property = "stage", defaultValue = "true")
	public boolean stage;

	// Instance fields
	String baseDir;
	private GitRunner git;
	private MavenRunner maven;
	CategoryLog mojoLog;
	CategoryLog runnerLog;

	record ScenarioEntry(String file, String scenario, String tag) {}

	// =========================================================================
	// Lifecycle
	// =========================================================================

	void init() throws Exception {
		baseDir = project.getBasedir().getAbsolutePath();
		initLogs();
		git = new GitRunner(runnerLog);
		maven = new MavenRunner(runnerLog);
	}

	void cleanup() {
		closeLogs();
	}

	private Path resolveLogDir() {
		String logPath = System.getenv("LOG_PATH");
		if (logPath != null && !logPath.isEmpty()) {
			return Path.of(logPath);
		}
		return Path.of(baseDir, "target", "darmok");
	}

	private void initLogs() throws Exception {
		Path logDir = resolveLogDir();
		String date = LocalDate.now().toString();
		mojoLog = new CategoryLog(getLog(), "mojo", logDir.resolve("darmok.mojo." + date + ".log"));
		runnerLog = new CategoryLog(getLog(), "runner", logDir.resolve("darmok.runners." + date + ".log"));
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

		// === RED PHASE ===
		mojoLog.info("  Red: Running maven...");
		long redMavenStart = System.currentTimeMillis();
		int redExitCode = runRgrRed(tag);
		long redMavenDuration = System.currentTimeMillis() - redMavenStart;

		if (redExitCode != 0 && redExitCode != 100) {
			throw new MojoExecutionException("rgr-red failed with exit code " + redExitCode);
		}
		mojoLog.info("  Red: Completed maven (" + formatDuration(redMavenDuration) + ")");

		long greenDuration = 0;
		long refactorDuration = 0;

		if (redExitCode == 100) {
			// Tests already passing — include scenario removal in red commit
			mojoLog.info("  Green: Skipped (tests already passing)");
			removeFirstScenarioFromFile();
			git.run(baseDir, "add", ".");
			commitIfChanged("run-rgr red " + scenarioName, "Red");
		} else {
			// Tests failing — commit red, then run green and refactor
			git.run(baseDir, "add", ".");
			if (!stage) {
				commitIfChanged("run-rgr red " + scenarioName, "Red");
			}

			// === GREEN PHASE ===
			mojoLog.info("  Green: Running...");
			long greenStart = System.currentTimeMillis();
			int greenExitCode = runRgrGreen(tag);
			greenDuration = System.currentTimeMillis() - greenStart;
			if (greenExitCode != 0) {
				throw new MojoExecutionException("rgr-green failed with exit code " + greenExitCode);
			}
			mojoLog.info("  Green: Completed (" + formatDuration(greenDuration) + ")");

			removeFirstScenarioFromFile();
			git.run(baseDir, "add", ".");
			if (!stage) {
				commitIfChanged("run-rgr green " + scenarioName, "Green");
			}

			// === REFACTOR PHASE ===
			mojoLog.info("  Refactor: Running...");
			long refactorStart = System.currentTimeMillis();
			int refactorExit = runRgrRefactor();
			refactorDuration = System.currentTimeMillis() - refactorStart;

			if (refactorExit != 0) {
				throw new MojoExecutionException("rgr-refactor failed with exit code " + refactorExit);
			}
			mojoLog.info("  Refactor: Completed (" + formatDuration(refactorDuration) + ")");

			git.run(baseDir, "add", ".");
			String commitMessage = stage ? "run-rgr " + scenarioName : "run-rgr refactor " + scenarioName;
			commitIfChanged(commitMessage, "Refactor");
		}

		// === METRIC LINES ===
		long totalDuration = System.currentTimeMillis() - totalStart;
		mojoLog.info("  METRIC|scenario=" + scenarioName + "|phase=red-maven|duration_ms=" + redMavenDuration);
		mojoLog.info("  METRIC|scenario=" + scenarioName + "|phase=green|duration_ms=" + greenDuration);
		mojoLog.info("  METRIC|scenario=" + scenarioName + "|phase=refactor|duration_ms=" + refactorDuration);
		mojoLog.info("  METRIC|scenario=" + scenarioName + "|phase=total|duration_ms=" + totalDuration);
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

	// =========================================================================
	// RGR Workflow Methods
	// =========================================================================

	private int runRgrRed(String pattern) throws Exception {
		String runnerClassName = pattern + "Test";

		// Step 1: AsciiDoctor to UML (maven call from specsDir)
		String specsDirAbsolute = Path.of(baseDir, specsDir).normalize().toString();
		maven.run(specsDirAbsolute, "org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml",
				"-Dtags=" + pattern, "-Dhost=" + host, "-DonlyChanges=" + onlyChanges);

		// Step 2: UML to Cucumber-Guice (maven call from baseDir)
		maven.run(baseDir, "org.farhan:sheep-dog-svc-maven-plugin:uml-to-cucumber-guice",
				"-Dtags=" + pattern, "-Dhost=" + host, "-DonlyChanges=" + onlyChanges);

		// Step 3: Generate runner class
		String runnerClassPath = baseDir
			+ "/src/test/java/org/farhan/suites/" + runnerClassName + ".java";
		Files.createDirectories(Path.of(runnerClassPath).getParent());
		String runnerContent = generateRunnerClassContent(pattern, runnerClassName);
		writeFileWithLF(runnerClassPath, List.of(runnerContent.split("\n", -1)));
		mojoLog.debug("  Created runner class: " + runnerClassPath);

		// Step 4: Run tests
		int testExitCode = maven.run(baseDir, "test", "-Dtest=" + runnerClassName);

		if (testExitCode == 0) {
			mojoLog.debug("  Tests are PASSING - no failing tests to fix (returning 100)");
			return 100;
		} else {
			mojoLog.debug("  Tests are FAILING - ready for green phase (returning 0)");
			return 0;
		}
	}

	private int runRgrGreen(String pattern) throws Exception {
		ClaudeRunner claude = new ClaudeRunner(runnerLog, modelGreen, maxRetries, retryWaitSeconds);
		return claude.run(baseDir + "/../..", "/rgr-green " + project.getArtifactId() + " " + pattern);
	}

	private int runRgrRefactor() throws Exception {
		ClaudeRunner claude = new ClaudeRunner(runnerLog, modelRefactor, maxRetries, retryWaitSeconds);
		return claude.run(baseDir + "/../..", "/rgr-refactor " + pipeline + " " + project.getArtifactId());
	}

	int runCleanUp() throws Exception {
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

	String generateRunnerClassContent(String pattern, String runnerClassName) {
		return "package org.farhan.suites;\n"
			+ "\n"
			+ "import org.junit.platform.suite.api.ConfigurationParameter;\n"
			+ "import org.junit.platform.suite.api.IncludeEngines;\n"
			+ "import org.junit.platform.suite.api.IncludeTags;\n"
			+ "import org.junit.platform.suite.api.SelectClasspathResource;\n"
			+ "import org.junit.platform.suite.api.Suite;\n"
			+ "import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;\n"
			+ "import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;\n"
			+ "\n"
			+ "@Suite\n"
			+ "@IncludeEngines(\"cucumber\")\n"
			+ "@SelectClasspathResource(\"cucumber/\")\n"
			+ "@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = \"pretty\")\n"
			+ "@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = \"org.farhan.common,org.farhan.objects,org.farhan.stepdefs,org.farhan.suites\")\n"
			+ "@IncludeTags(\"" + pattern + "\")\n"
			+ "public class " + runnerClassName + " {\n"
			+ "}";
	}

	String formatDuration(long millis) {
		long seconds = millis / 1000;
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;
		long secs = seconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, secs);
	}
}
