package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Characterization tests for {@link ClaudeRunner}.
 * <p>
 * <b>Intent:</b> lock in the observable behavior of a single Claude CLI invocation as
 * recorded in the runner log file. These tests treat the log file as the contract
 * surface — they do not inspect internal fields or verify method-level interactions.
 * The Javadoc on each test method is the plain-language intent that feeds downstream
 * asciidoc spec and DSL grammar generation (sheep-dog-main#253).
 * <p>
 * <b>Isolation:</b> uses the static {@link ProcessRunner#starter} seam to substitute a
 * {@link FakeProcess} for the real Claude subprocess. Tests run sequentially
 * ({@link ExecutionMode#SAME_THREAD}) because the seam is a static field. Per-class
 * instance injection is a later refactor (#253 Stage 2).
 */
@Execution(ExecutionMode.SAME_THREAD)
class ClaudeRunnerCharacterizationTest {

	@TempDir
	Path workDir;

	Path logFile;
	CategoryLog categoryLog;

	@BeforeEach
	void setup() throws Exception {
		logFile = workDir.resolve("runner.log");
		categoryLog = new CategoryLog(new SystemStreamLog(), "Claude", logFile);
	}

	@AfterEach
	void teardown() {
		if (categoryLog != null) {
			categoryLog.close();
		}
		ProcessRunner.starter = ProcessBuilder::start;
	}

	/**
	 * Given: Claude CLI returns exit code 0 on the first attempt with no retryable error.
	 * When: ClaudeRunner.run() is invoked with a single prompt argument against a working directory.
	 * Then: the runner log records
	 * <ul>
	 *   <li>the executed command starting with "Executing: claude"</li>
	 *   <li>the success marker "Claude CLI completed successfully"</li>
	 *   <li>no ERROR-level lines</li>
	 * </ul>
	 * and the method returns exit code 0.
	 * <p>
	 * This characterizes the green-phase happy path whose timestamps are consumed by
	 * the PBC report skill (pbc-report-plantuml) to plot cycle durations.
	 */
	@Test
	void happyPath_logsExecutingCommand_andSuccessMarker() throws Exception {
		ProcessRunner.starter = pb -> new FakeProcess("mocked claude output line", 0);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 30);

		int exit = claude.run(workDir.toString(), "/rgr-green sample-project sampleTag");

		assertThat(exit).isEqualTo(0);

		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.as("runner log contract — must expose executed command and success marker")
			.anyMatch(l -> l.contains("DEBUG [Claude] Executing: claude"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI completed successfully"))
			.noneMatch(l -> l.contains("ERROR"));
	}
}
