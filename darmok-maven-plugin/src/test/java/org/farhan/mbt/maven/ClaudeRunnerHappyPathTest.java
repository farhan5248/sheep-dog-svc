package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Happy path — the green-phase Claude invocation that succeeds on its first attempt
 * with no retries. Pins the "Executing: claude" marker and the success marker in
 * the runner log, and confirms no ERROR lines on the clean path. Whose timestamps
 * are consumed by the PBC report skill (pbc-report-plantuml) to plot cycle durations.
 */
class ClaudeRunnerHappyPathTest {

	@TempDir
	Path workDir;

	Path logFile;
	MojoLog categoryLog;

	@BeforeEach
	void setup() throws Exception {
		logFile = workDir.resolve("runner.log");
		categoryLog = new MojoLog(new SystemStreamLog(), "Claude", logFile);
	}

	@AfterEach
	void teardown() {
		if (categoryLog != null) {
			categoryLog.close();
		}
	}

	/**
	 * === Test-Case: ClaudeRunner succeeds on first attempt ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed successfully
	 *
	 * === When: The claude runner is executed
	 *
	 * === Then: The claude runner log will be as follows
	 * |===
	 * | Level | Category | Content
	 * | DEBUG | Claude   | Executing: claude
	 * | DEBUG | Claude   | Claude CLI completed successfully
	 * |===
	 *
	 * === Then: The claude runner log won't contain any ERROR level entry
	 *
	 * === Then: The claude runner exit code will be 0
	 * </pre>
	 */
	@Test
	void firstAttemptSucceeds_logsExecutingAndSuccessMarker() throws Exception {
		ProcessStarter starter = pb -> new FakeProcess("mocked claude output line", 0);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 30, starter);

		int exit = claude.run(workDir.toString(), "/rgr-green sample-project sampleTag");

		assertThat(exit).isEqualTo(0);
		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.as("runner log must show executing command and success marker, no ERROR lines")
			.anyMatch(l -> l.contains("DEBUG [Claude] Executing: claude"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI completed successfully"))
			.noneMatch(l -> l.contains("ERROR"));
	}
}
