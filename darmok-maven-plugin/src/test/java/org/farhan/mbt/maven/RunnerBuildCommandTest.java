package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Characterization tests for the {@code buildCommand} contract of each runner.
 * <p>
 * <b>Intent:</b> lock in the exact shape of the command line each runner assembles
 * before handing it to {@link ProcessBuilder#start()}. These assertions run against
 * the command the seam would spawn, not against the on-disk process, so they are
 * deterministic across OSes (we accept either {@code claude}/{@code claude.cmd} and
 * {@code mvn}/{@code mvn.cmd} to stay portable).
 * <p>
 * <b>Mechanism:</b> the {@link ProcessRunner#starter} seam captures the
 * {@link ProcessBuilder} each runner hands it, which lets these tests read
 * {@code pb.command()} without bumping the visibility of {@code buildCommand} itself.
 * Asserting on the captured command is a Detroit-school observation of the runner's
 * outward behavior rather than a London-school verification of an internal method call.
 */
@Execution(ExecutionMode.SAME_THREAD)
class RunnerBuildCommandTest {

	@TempDir
	Path workDir;

	Path logFile;
	CategoryLog categoryLog;

	@BeforeEach
	void setup() throws Exception {
		logFile = workDir.resolve("runner.log");
		categoryLog = new CategoryLog(new SystemStreamLog(), "test", logFile);
	}

	@AfterEach
	void teardown() {
		if (categoryLog != null) {
			categoryLog.close();
		}
		ProcessRunner.starter = ProcessBuilder::start;
	}

	/**
	 * Given: a ClaudeRunner configured with a non-empty model.
	 * When: run() is invoked with a single prompt argument.
	 * Then: the spawned command is the claude CLI binary, followed by
	 *   {@code --print}, {@code --dangerously-skip-permissions},
	 *   {@code --model <model>}, then the prompt argument — in that exact order.
	 * <p>
	 * This is the contract every real Claude invocation depends on. Regressions here
	 * would silently change the CLI semantics (e.g. dropping --dangerously-skip-permissions
	 * would prompt for confirmation and hang the plugin).
	 */
	@Test
	void claudeRunner_withModel_assemblesFullCommandLine() throws Exception {
		AtomicReference<List<String>> captured = new AtomicReference<>();
		ProcessRunner.starter = pb -> {
			captured.set(List.copyOf(pb.command()));
			return new FakeProcess("", 0);
		};
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 1, 0);

		claude.run(workDir.toString(), "/rgr-green project tag");

		List<String> command = captured.get();
		assertThat(command).hasSize(6);
		assertThat(command.get(0)).startsWith("claude");
		assertThat(command.subList(1, 6)).containsExactly(
			"--print",
			"--dangerously-skip-permissions",
			"--model", "sonnet",
			"/rgr-green project tag");
	}

	/**
	 * Given: a ClaudeRunner configured with an empty-string model.
	 * When: run() is invoked with a prompt argument.
	 * Then: the spawned command omits the {@code --model} flag entirely.
	 * <p>
	 * This characterizes the opt-in-only-if-configured contract — an empty model
	 * string means "let the CLI pick its default", not "pass --model ''".
	 */
	@Test
	void claudeRunner_emptyModel_omitsModelFlag() throws Exception {
		AtomicReference<List<String>> captured = new AtomicReference<>();
		ProcessRunner.starter = pb -> {
			captured.set(List.copyOf(pb.command()));
			return new FakeProcess("", 0);
		};
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "", 1, 0);

		claude.run(workDir.toString(), "/rgr-green project tag");

		List<String> command = captured.get();
		assertThat(command)
			.doesNotContain("--model")
			.containsExactly(
				command.get(0),
				"--print",
				"--dangerously-skip-permissions",
				"/rgr-green project tag");
		assertThat(command.get(0)).startsWith("claude");
	}

	/**
	 * Given: a MavenRunner.
	 * When: run() is invoked with a goal and a parameter flag.
	 * Then: the spawned command is the mvn binary followed by the supplied args verbatim
	 *   (no extra decorations).
	 * <p>
	 * This characterizes the pass-through contract — MavenRunner does not modify or
	 * reorder arguments, which keeps {@code runRgrRed}'s expectations correct.
	 */
	@Test
	void mavenRunner_prependsMvnBinary_andPassesArgsVerbatim() throws Exception {
		AtomicReference<List<String>> captured = new AtomicReference<>();
		ProcessRunner.starter = pb -> {
			captured.set(List.copyOf(pb.command()));
			return new FakeProcess("", 0);
		};
		MavenRunner maven = new MavenRunner(categoryLog);

		maven.run(workDir.toString(),
			"org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml",
			"-Dtags=loginHappyPath",
			"-Dhost=dev.sheepdog.io");

		List<String> command = captured.get();
		assertThat(command.get(0)).startsWith("mvn");
		assertThat(command.subList(1, command.size())).containsExactly(
			"org.farhan:sheep-dog-svc-maven-plugin:asciidoctor-to-uml",
			"-Dtags=loginHappyPath",
			"-Dhost=dev.sheepdog.io");
	}

	/**
	 * Given: a GitRunner.
	 * When: run() is invoked with a git subcommand and flags.
	 * Then: the spawned command is {@code git} followed by the supplied args verbatim.
	 * <p>
	 * Same pass-through contract as MavenRunner — the base {@link ProcessRunner#run}
	 * handles execution and both override only {@code buildCommand}, so proving it
	 * once per runner is enough.
	 */
	@Test
	void gitRunner_prependsGitBinary_andPassesArgsVerbatim() throws Exception {
		AtomicReference<List<String>> captured = new AtomicReference<>();
		ProcessRunner.starter = pb -> {
			captured.set(List.copyOf(pb.command()));
			return new FakeProcess("", 0);
		};
		GitRunner git = new GitRunner(categoryLog);

		git.run(workDir.toString(), "commit", "-m", "run-rgr green someScenario");

		List<String> command = captured.get();
		assertThat(command).containsExactly(
			"git", "commit", "-m", "run-rgr green someScenario");
	}
}
