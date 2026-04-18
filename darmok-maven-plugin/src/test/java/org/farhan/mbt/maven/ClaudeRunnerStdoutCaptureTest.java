package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;
import org.farhan.fake.FakeProcess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Partial-stdout-capture-under-failure variant from #258.
 * <p>
 * Proves ClaudeRunner mirrors every line of subprocess stdout to the runner log even
 * when the subprocess then exits non-zero — i.e. the streamed read doesn't drop data
 * just because the exit was a failure. Under the "goal tools" framing, this is a
 * claude /rgr-green command that prints partial output and then fails.
 */
class ClaudeRunnerStdoutCaptureTest {

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
	 * === Test-Case: ClaudeRunner captures partial stdout even when the subprocess fails ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but failed non-retryably
	 * |===
	 * | Exit | Output
	 * | 137  | line one of partial output\nline two of partial output\nline three of partial output
	 * |===
	 *
	 * === When: The claude runner is executed
	 *
	 * === Then: The claude runner log will be as follows with this failure
	 * |===
	 * | Level | Category | Content
	 * | DEBUG | Claude   | line one of partial output
	 * | DEBUG | Claude   | line two of partial output
	 * | DEBUG | Claude   | line three of partial output
	 * | DEBUG | Claude   | Claude CLI exited with code 137
	 * |===
	 * </pre>
	 */
	@Test
	void partialStdout_isMirroredBeforeFailureExit() throws Exception {
		String stdout = String.join("\n",
			"line one of partial output",
			"line two of partial output",
			"line three of partial output");
		ProcessStarter starter = pb -> new FakeProcess(stdout, 137);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 1, 0, starter);

		int exit = claude.run(workDir.toString(), "/rgr-green project tag");

		assertThat(exit).isEqualTo(137);
		List<String> lines = Files.readAllLines(logFile);
		int line1 = indexOfContaining(lines, "line one of partial output");
		int line2 = indexOfContaining(lines, "line two of partial output");
		int line3 = indexOfContaining(lines, "line three of partial output");
		int exitMarker = indexOfContaining(lines, "Claude CLI exited with code 137");
		assertThat(line1).as("first partial output line present").isGreaterThanOrEqualTo(0);
		assertThat(line2).isGreaterThan(line1);
		assertThat(line3).isGreaterThan(line2);
		assertThat(exitMarker).as("failure marker present AFTER the captured lines").isGreaterThan(line3);
	}

	private static int indexOfContaining(List<String> lines, String token) {
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).contains(token)) {
				return i;
			}
		}
		return -1;
	}
}
