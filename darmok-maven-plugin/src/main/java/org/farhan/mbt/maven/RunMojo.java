package org.farhan.mbt.maven;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "run", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RunMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	public MavenProject project;

	// Path properties
	@Parameter(property = "specsDir", defaultValue = "../../sheep-dog-qa/sheep-dog-specs")
	public String specsDir;

	@Parameter(property = "asciidocDir", defaultValue = "../../sheep-dog-qa/sheep-dog-specs/src/test/resources/asciidoc/specs")
	public String asciidocDir;

	@Parameter(property = "scenariosFile", defaultValue = "scenarios-list.txt")
	public String scenariosFile;

	// Server properties
	@Parameter(property = "host", defaultValue = "dev.sheepdogdev.io")
	public String host;

	@Parameter(property = "port", defaultValue = "80")
	public int port;

	@Parameter(property = "timeout", defaultValue = "300000")
	public int timeout;

	// Claude CLI properties
	@Parameter(property = "modelRed", defaultValue = "opus")
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

	// Instance fields
	private String baseDir;
	private GitRunner git;
	private MavenRunner maven;
	private CategoryLog mojoLog;
	private CategoryLog runnerLog;
	private CategoryLog clientLog;

	private record ScenarioEntry(String file, String scenario, String tag) {}

	// =========================================================================
	// Execute
	// =========================================================================

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
		clientLog = new CategoryLog(getLog(), "client", logDir.resolve("darmok.clients." + date + ".log"));
	}

	private void closeLogs() {
		if (mojoLog != null) mojoLog.close();
		if (runnerLog != null) runnerLog.close();
		if (clientLog != null) clientLog.close();
	}

	public void execute() throws MojoExecutionException {
		try {
			baseDir = project.getBasedir().getAbsolutePath();
			initLogs();

			git = new GitRunner(runnerLog);
			maven = new MavenRunner(runnerLog);

			mojoLog.info("RGR Automation Plugin");
			mojoLog.info("Log files: " + resolveLogDir());

			// Parse scenarios
			List<ScenarioEntry> scenarios = parseScenarios(baseDir + "/" + scenariosFile);
			mojoLog.info("Found " + scenarios.size() + " scenarios to process");
			mojoLog.info("");

			// Clean up
			mojoLog.info("Running clean up...");
			int cleanUpExit = runCleanUp();
			if (cleanUpExit != 0) {
				throw new MojoExecutionException("Clean up failed with exit code " + cleanUpExit);
			}
			mojoLog.info("");

			// Group scenarios by file (preserving order)
			LinkedHashMap<String, List<ScenarioEntry>> scenariosByFile = new LinkedHashMap<>();
			for (ScenarioEntry entry : scenarios) {
				scenariosByFile.computeIfAbsent(entry.file(), k -> new ArrayList<>()).add(entry);
			}

			mojoLog.info("Found " + scenariosByFile.size() + " feature files to process");
			mojoLog.info("");

			int totalProcessed = 0;
			int totalTagsAdded = 0;
			int totalFilesProcessed = 0;

			// Outer loop: each feature file
			for (Map.Entry<String, List<ScenarioEntry>> fileGroup : scenariosByFile.entrySet()) {
				String fileName = fileGroup.getKey();
				List<ScenarioEntry> fileScenarios = fileGroup.getValue();

				mojoLog.info("FEATURE FILE: " + fileName + " (" + fileScenarios.size() + " scenarios)");

				// Inner loop: each scenario
				for (ScenarioEntry entry : fileScenarios) {
					String scenarioName = entry.scenario();
					String tag = entry.tag();

					mojoLog.info("Processing Scenario: " + scenarioName + " [" + tag + "]");

					if ("NoTag".equals(tag)) {
						mojoLog.info("  Skipping (NoTag)");
						mojoLog.info("");
						continue;
					}

					// Add tag to asciidoc file
					boolean tagAdded = addTagToAsciidoc(fileName, scenarioName, tag);
					if (tagAdded) {
						totalTagsAdded++;
					}

					// Run rgr-red
					mojoLog.info("");
					mojoLog.info("Running Red-Green workflow for tag: " + tag);
					mojoLog.info("  [1/2] Running rgr-red...");
					long redStart = System.currentTimeMillis();
					int redExitCode = runRgrRed(tag);
					long redDuration = System.currentTimeMillis() - redStart;

					if (redExitCode != 0 && redExitCode != 100) {
						throw new MojoExecutionException("rgr-red failed with exit code " + redExitCode);
					}
					mojoLog.info("  Completed rgr-red (Duration: " + formatDuration(redDuration) + ")");

					// Run Claude rgr-red skill
					mojoLog.info("  Running Claude rgr-red...");
					long claudeRedStart = System.currentTimeMillis();
					int claudeRedExitCode = runClaudeRgrRed(tag);
					long claudeRedDuration = System.currentTimeMillis() - claudeRedStart;
					if (claudeRedExitCode != 0) {
						throw new MojoExecutionException("Claude rgr-red failed with exit code " + claudeRedExitCode);
					}
					mojoLog.info("  Completed Claude rgr-red (Duration: " + formatDuration(claudeRedDuration) + ")");

					// Stage changes
					git.run(baseDir, "add", ".");

					// Run rgr-green if tests are failing
					long greenDuration = 0;
					if (redExitCode == 0) {
						mojoLog.info("  [2/2] Running rgr-green...");
						long greenStart = System.currentTimeMillis();
						int greenExitCode = runRgrGreen(tag);
						greenDuration = System.currentTimeMillis() - greenStart;
						if (greenExitCode != 0) {
							throw new MojoExecutionException("rgr-green failed with exit code " + greenExitCode);
						}
						mojoLog.info("  Completed rgr-green (Duration: " + formatDuration(greenDuration) + ")");
						git.run(baseDir, "add", ".");
					} else {
						mojoLog.info("  [2/2] Skipping rgr-green (tests already passing)");
					}

					long totalDuration = redDuration + claudeRedDuration + greenDuration;
					mojoLog.info("");
					mojoLog.info("  Red-Green workflow completed for tag: " + tag + " (Total Duration: " + formatDuration(totalDuration) + ")");
					totalProcessed++;
					mojoLog.info("");
				}

				// After all scenarios in file: commit and refactor
				mojoLog.info("All scenarios in file " + fileName + " processed");

				int diffQuietExit = git.run(baseDir, "diff", "--cached", "--quiet");
				boolean hasStagedChanges = (diffQuietExit != 0);

				if (hasStagedChanges) {
					// Commit red-green changes
					String commitMsg = "run-rgr red-green " + fileName + "\n\nCo-Authored-By: " + coAuthor;
					mojoLog.info("Committing all red-green changes for file: " + commitMsg.split("\n")[0]);
					git.run(baseDir, "add", ".");
					git.run(baseDir, "commit", "-m", commitMsg);
					mojoLog.info("");

					// Check if src/main has changes (code was modified, not just tests)
					int mainDiffExit = git.run(baseDir, "diff", "HEAD~1", "--quiet", "--", "src/main");
					boolean hasMainChanges = (mainDiffExit != 0);

					if (hasMainChanges) {
						// Run rgr-refactor
						mojoLog.info("Running rgr-refactor for file: " + fileName);
						mojoLog.info("");
						long refactorStart = System.currentTimeMillis();
						int refactorExit = runRgrRefactor();
						long refactorDuration = System.currentTimeMillis() - refactorStart;

						if (refactorExit != 0) {
							throw new MojoExecutionException("rgr-refactor failed with exit code " + refactorExit);
						}

						// Commit refactor changes
						String refactorCommitMsg = "run-rgr-refactor " + fileName + "\n\nCo-Authored-By: " + coAuthor;
						mojoLog.info("Committing changes: " + refactorCommitMsg.split("\n")[0] + " (Duration: " + formatDuration(refactorDuration) + ")");
						git.run(baseDir, "add", ".");
						git.run(baseDir, "commit", "-m", refactorCommitMsg);
					} else {
						mojoLog.info("Skipping rgr-refactor (no src/main changes)");
					}
				} else {
					mojoLog.info("Skipping commit (nothing staged for this file)");
				}

				totalFilesProcessed++;
				mojoLog.info("");
			}

			mojoLog.info("RGR Automation Complete!");
			mojoLog.info("Total feature files processed: " + totalFilesProcessed);
			mojoLog.info("Total scenarios processed: " + totalProcessed);
			mojoLog.info("Total tags added: " + totalTagsAdded);
		} catch (MojoExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			closeLogs();
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
		ServiceClient service = new ServiceClient(clientLog, host, port, timeout);

		mojoLog.debug("RGR-Red: Pattern=" + pattern + ", Runner=" + runnerClassName);

		// Step 1: AsciiDoctor to UML (direct REST call to service)
		mojoLog.debug("  STEP 1: AsciiDoctor to UML Conversion");
		service.executeToUML("asciidoctor/", "ConvertAsciidoctorToUML", pattern,
				baseDir + "/" + specsDir, ".asciidoc");

		// Step 2: UML to Cucumber-Guice (direct REST call to service)
		mojoLog.debug("  STEP 2: UML to Cucumber-Guice Conversion");
		service.executeFromUML("cucumber/", "ConvertUMLToCucumberGuice", pattern, baseDir);

		// Step 3: Generate runner class
		mojoLog.debug("  STEP 3: Generate Runner Class");
		String runnerClassPath = baseDir
			+ "/src/test/java/org/farhan/suites/" + runnerClassName + ".java";
		Files.createDirectories(Path.of(runnerClassPath).getParent());
		String runnerContent = generateRunnerClassContent(pattern, runnerClassName);
		writeFileWithLF(runnerClassPath, List.of(runnerContent.split("\n", -1)));
		mojoLog.debug("  Created runner class: " + runnerClassPath);

		// Step 4: Run tests
		mojoLog.debug("  STEP 4: Running tests with " + runnerClassName);
		int testExitCode = maven.run(baseDir, "test", "-Dtest=" + runnerClassName);

		if (testExitCode == 0) {
			mojoLog.debug("  Tests are PASSING - no failing tests to fix (returning 100)");
			return 100;
		} else {
			mojoLog.debug("  Tests are FAILING - ready for green phase (returning 0)");
			return 0;
		}
	}

	private int runClaudeRgrRed(String pattern) throws Exception {
		mojoLog.info("RGR-Red (Claude): Pattern=" + pattern);
		ClaudeRunner claude = new ClaudeRunner(runnerLog, modelRed, maxRetries, retryWaitSeconds);
		return claude.run(baseDir + "/../..", "/rgr-red " + project.getArtifactId() + " " + pattern);
	}

	private int runRgrGreen(String pattern) throws Exception {
		mojoLog.info("RGR-Green: Pattern=" + pattern);
		ClaudeRunner claude = new ClaudeRunner(runnerLog, modelGreen, maxRetries, retryWaitSeconds);
		return claude.run(baseDir + "/../..", "/rgr-green " + project.getArtifactId() + " " + pattern);
	}

	private int runRgrRefactor() throws Exception {
		mojoLog.info("RGR-Refactor: " + pipeline + " " + project.getArtifactId());
		ClaudeRunner claude = new ClaudeRunner(runnerLog, modelRefactor, maxRetries, retryWaitSeconds);
		return claude.run(baseDir + "/../..", "/rgr-refactor " + pipeline + " " + project.getArtifactId());
	}

	private int runCleanUp() throws Exception {
		Path sheepDogMain = Path.of(baseDir, "../..").normalize();

		mojoLog.info("Deleting NUL files...");
		int deleted = deleteNulFiles(sheepDogMain);
		mojoLog.info("Deleted " + deleted + " NUL files");

		mojoLog.info("Deleting target directory...");
		deleteDirectory(Path.of(baseDir, "target"));
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

	private void writeFileWithLF(String filePath, List<String> lines) throws Exception {
		String content = String.join("\n", lines) + "\n";
		Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8);
	}

	private String generateRunnerClassContent(String pattern, String runnerClassName) {
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

	private String formatDuration(long millis) {
		long seconds = millis / 1000;
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;
		long secs = seconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, secs);
	}
}
