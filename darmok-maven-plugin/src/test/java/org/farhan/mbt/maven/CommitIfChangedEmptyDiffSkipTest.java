package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.farhan.mbt.maven.DarmokMojo.ScenarioEntry;
import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * === Test-Case: Red commit skips when git detects no staged changes ===
 *
 * <p>Forces the already-passing branch of processScenario so only the red-phase
 * commit attempt fires. The stub ProcessStarter returns exit 0 for
 * {@code git diff --cached --quiet} (meaning no staged changes), so
 * {@code commitIfChanged} must skip the git commit entirely.
 *
 * <p>Sketch of the eventual asciidoc spec once the Command grammar lands
 * (see #258 §1 — syntax not finalized, pass 1 use only):
 *
 * <pre>
 * == Test-Case: Red commit skips when git detects no staged changes
 *
 * === Given: The mvn test command produced this output
 * |===
 * | ExitCode
 * | 0
 * |===
 *
 * === Given: The git diff --cached --quiet command produced this output
 * |===
 * | ExitCode
 * | 0
 * |===
 *
 * === When: The gen-from-existing goal processes a single scenario
 *
 * === Then: The code-prj project target/darmok/darmok.mojo log file will be as follows
 * |===
 * | Level | Category | Content
 * | INFO  | mojo     | Red: Committing     ← must NOT appear
 * |===
 *
 * === Then: No git commit command will have been invoked
 * </pre>
 */
class CommitIfChangedEmptyDiffSkipTest {

	@TempDir
	Path sheepDogRoot;

	@Test
	void redCommit_whenNoStagedChanges_skipsGitCommit() throws Exception {
		// ---- Fixture ----
		Path codePrj = sheepDogRoot.resolve("sheep-dog-svc/code-prj");
		Files.createDirectories(codePrj);
		Path logDir = sheepDogRoot.resolve("logs");
		Files.createDirectories(logDir);
		System.setProperty("LOG_PATH", logDir.toString());
		Files.writeString(codePrj.resolve("scenarios-list.txt"),
			"File: login.asciidoc\n  Scenario: Happy path\n    Tag: happyPath\n");

		// ---- Given: every subprocess succeeds; git diff --cached --quiet returns 0 (no staged changes) ----
		List<String> executedCommands = new ArrayList<>();
		ProcessStarter stubStarter = pb -> {
			String cmd = String.join(" ", pb.command());
			executedCommands.add(cmd);
			return new FakeProcess("", 0);
		};

		GenFromExistingMojo mojo = newMojo(codePrj, stubStarter);

		// ---- When: processScenario runs for one scenario ----
		try {
			mojo.init();
			mojo.processScenario(new ScenarioEntry("login.asciidoc", "Happy path", "happyPath"));
		} finally {
			mojo.cleanup();
		}

		// ---- Then: no "Committing" marker in the mojo log AND no git commit executed ----
		List<String> mojoLogLines = Files.readAllLines(findDatedLog(logDir, "darmok.mojo"));
		assertThat(mojoLogLines)
			.as("commitIfChanged must skip when git diff --cached --quiet returns 0")
			.noneMatch(line -> line.contains("Committing"));

		assertThat(executedCommands)
			.as("no git commit command should have been executed")
			.noneMatch(cmd -> cmd.matches("^git commit( |$).*"));
	}

	private static GenFromExistingMojo newMojo(Path codePrj, ProcessStarter stubStarter) {
		GenFromExistingMojo mojo = new GenFromExistingMojo();
		MavenProject project = new MavenProject();
		project.setArtifactId("code-prj");
		mojo.project = project;
		mojo.setBaseDir(codePrj.toString());
		mojo.specsDir = "../../spec-prj";
		mojo.asciidocDir = "asciidoc";
		mojo.scenariosFile = "scenarios-list.txt";
		mojo.host = "localhost";
		mojo.modelGreen = "sonnet";
		mojo.modelRefactor = "sonnet";
		mojo.coAuthor = "Test <test@example.com>";
		mojo.maxRetries = 1;
		mojo.retryWaitSeconds = 0;
		mojo.onlyChanges = true;
		mojo.stage = false;
		mojo.setGitRunnerFactory(log -> new GitRunner(log, stubStarter));
		mojo.setMavenRunnerFactory(log -> new MavenRunner(log, stubStarter));
		mojo.setClaudeRunnerFactory((log, model, r, w) -> new ClaudeRunner(log, model, r, w, stubStarter));
		return mojo;
	}

	private static Path findDatedLog(Path logDir, String prefix) throws Exception {
		try (var stream = Files.list(logDir)) {
			return stream
				.filter(p -> p.getFileName().toString().startsWith(prefix + "."))
				.findFirst()
				.orElseThrow(() -> new AssertionError("no " + prefix + ".<date>.log in " + logDir));
		}
	}
}
