package org.farhan.mbt.maven;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class RedPhase extends RgrPhase {

	private final String baseDir;
	private final String specsDir;
	private final String host;
	private final boolean onlyChanges;
	private final String svcMavenPluginGoal;

	public RedPhase(Maven maven, DarmokMojoLog mojoLog, Log runnerLog, String baseDir, String specsDir, String host, boolean onlyChanges, String svcMavenPluginGoal, int maxRetries, int retryWaitSeconds) {
		super(null, maven, null, mojoLog, runnerLog, null, null, null, 0, 0, 0, 0, maxRetries, retryWaitSeconds, List.of());
		this.baseDir = baseDir;
		this.specsDir = specsDir;
		this.host = host;
		this.onlyChanges = onlyChanges;
		this.svcMavenPluginGoal = svcMavenPluginGoal;
	}

	@Override
	protected Phase phase() {
		return Phase.RED;
	}

	@Override
	protected boolean requiresVerifyLoop() {
		return false;
	}

	@Override
	protected String workSuffix() {
		return " maven";
	}

	private static final String[] MAVEN_RETRYABLE_PATTERNS = {
		"Service did not become available within"
	};

	@Override
	protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception {
		String pattern = state.tag;
		String runnerClassName = pattern + "Test";

		String specsDirAbsolute = Path.of(baseDir, specsDir).normalize().toString();
		Path mvnLog = Path.of(baseDir, "target", "darmok-mvn-stdout.log");
		Files.createDirectories(mvnLog.getParent());

		int asciidocExit = runMavenWithRetry(specsDirAbsolute, mvnLog,
				"org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml",
				"-Dtags=" + pattern, "-Dhost=" + host, "-DonlyChanges=" + onlyChanges);
		if (asciidocExit != 0) {
			return asciidocExit;
		}

		int cucumberExit = runMavenWithRetry(baseDir, mvnLog,
				"org.farhan:sheep-dog-svc-maven-plugin:" + svcMavenPluginGoal,
				"-Dtags=" + pattern, "-Dhost=" + host, "-DonlyChanges=" + onlyChanges);
		if (cucumberExit != 0) {
			return cucumberExit;
		}

		String runnerClassPath = baseDir
			+ "/src/test/java/org/farhan/suites/" + runnerClassName + ".java";
		Files.createDirectories(Path.of(runnerClassPath).getParent());
		String runnerContent = generateRunnerClassContent(pattern, runnerClassName);
		Files.writeString(Path.of(runnerClassPath), runnerContent + "\n", StandardCharsets.UTF_8);
		mojoLog.debug("  Created runner class: " + runnerClassPath);

		int testExitCode = maven.run(baseDir, Path.of(baseDir, "log.txt"), "test", "-Dtest=" + runnerClassName);

		if (testExitCode == 0) {
			mojoLog.debug("  Tests are PASSING - no failing tests to fix (returning 100)");
			return 100;
		}
		mojoLog.debug("  Tests are FAILING - ready for green phase (returning 0)");
		return 0;
	}

	private int runMavenWithRetry(String workingDirectory, Path logFile, String... args) throws Exception {
		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			if (attempt > 1) {
				runnerLog.debug("Retry attempt " + attempt + " of " + maxRetries + "...");
			}
			int exitCode = maven.run(workingDirectory, logFile, args);
			if (exitCode == 0) {
				runnerLog.debug("Maven CLI completed successfully");
				return 0;
			}
			String matched = findMavenRetryableError(logFile);
			if (matched != null && attempt < maxRetries) {
				runnerLog.warn("Retryable error detected: " + matched);
				runnerLog.warn("Maven CLI exited with code " + exitCode);
				runnerLog.warn("Waiting " + retryWaitSeconds + " seconds before retry...");
				Thread.sleep(retryWaitSeconds * 1000L);
			} else {
				return handleMavenFailure(exitCode, logFile);
			}
		}
		return -1;
	}

	private static String findMavenRetryableError(Path logFile) throws Exception {
		if (!Files.exists(logFile)) return null;
		List<String> lines = Files.readAllLines(logFile);
		for (String line : lines) {
			for (String pattern : MAVEN_RETRYABLE_PATTERNS) {
				if (line.contains(pattern)) {
					return pattern;
				}
			}
		}
		return null;
	}

	private int handleMavenFailure(int exitCode, Path logFile) throws Exception {
		String lastLine = "no output";
		if (Files.exists(logFile)) {
			List<String> lines = Files.readAllLines(logFile);
			for (int i = lines.size() - 1; i >= 0; i--) {
				String line = lines.get(i).trim();
				if (!line.isEmpty()) {
					lastLine = line;
					break;
				}
			}
		}
		runnerLog.debug("Maven CLI exited with code " + exitCode);
		mojoLog.error("Maven failed (exit " + exitCode + "): " + lastLine);
		return exitCode;
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
			+ "@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = \"org.farhan\")\n"
			+ "@IncludeTags(\"" + pattern + "\")\n"
			+ "public class " + runnerClassName + " {\n"
			+ "}";
	}
}
