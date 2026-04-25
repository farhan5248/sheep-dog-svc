package org.farhan.mbt.maven;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RedPhase extends RgrPhase {

	private final String baseDir;
	private final String specsDir;
	private final String host;
	private final boolean onlyChanges;

	public RedPhase(MavenRunner maven, DarmokMojoLog mojoLog, String baseDir, String specsDir, String host, boolean onlyChanges) {
		super(null, maven, null, mojoLog, null, null, null, 0, 0, 0, 0, List.of());
		this.baseDir = baseDir;
		this.specsDir = specsDir;
		this.host = host;
		this.onlyChanges = onlyChanges;
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

	@Override
	protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception {
		String pattern = state.tag;
		String runnerClassName = pattern + "Test";

		String specsDirAbsolute = Path.of(baseDir, specsDir).normalize().toString();
		maven.run(specsDirAbsolute, "org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml",
				"-Dtags=" + pattern, "-Dhost=" + host, "-DonlyChanges=" + onlyChanges);

		maven.run(baseDir, "org.farhan:sheep-dog-svc-maven-plugin:uml-to-cucumber-guice",
				"-Dtags=" + pattern, "-Dhost=" + host, "-DonlyChanges=" + onlyChanges);

		String runnerClassPath = baseDir
			+ "/src/test/java/org/farhan/suites/" + runnerClassName + ".java";
		Files.createDirectories(Path.of(runnerClassPath).getParent());
		String runnerContent = generateRunnerClassContent(pattern, runnerClassName);
		Files.writeString(Path.of(runnerClassPath), runnerContent + "\n", StandardCharsets.UTF_8);
		mojoLog.debug("  Created runner class: " + runnerClassPath);

		int testExitCode = maven.run(baseDir, "test", "-Dtest=" + runnerClassName);

		if (testExitCode == 0) {
			mojoLog.debug("  Tests are PASSING - no failing tests to fix (returning 100)");
			return 100;
		}
		mojoLog.debug("  Tests are FAILING - ready for green phase (returning 0)");
		return 0;
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
