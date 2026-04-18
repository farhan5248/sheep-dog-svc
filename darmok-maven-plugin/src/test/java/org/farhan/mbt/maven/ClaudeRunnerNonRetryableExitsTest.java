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
 * Three non-retryable-exit variants from #258:
 * <ul>
 *   <li>Exit 1 with text that looks retryable but doesn't match the patterns (e.g. "500 Server Error" without the "API Error:" prefix)</li>
 *   <li>Exit 137 (SIGKILL)</li>
 *   <li>Exit 130 (SIGINT)</li>
 * </ul>
 * All share the shape: single subprocess invocation, failing exit code passed through unchanged, no retry markers.
 */
class ClaudeRunnerNonRetryableExitsTest {

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
	 * === Test-Case: ClaudeRunner treats opaque exit 1 as non-retryable ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but failed non-retryably
	 * |===
	 * | Exit | Output
	 * | 1    | 500 Server Error (no "API Error:" prefix)
	 * |===
	 *
	 * === When: The claude runner is executed
	 *
	 * === Then: The claude runner log will be as follows with this failure
	 * |===
	 * | Level | Category | Content
	 * | DEBUG | Claude   | Claude CLI exited with code 1
	 * |===
	 *
	 * === Then: The claude runner exit code will be 1
	 * </pre>
	 */
	@Test
	void exit1_opaqueFailure_doesNotRetry() throws Exception {
		assertNoRetry(1, "500 Server Error occurred (no API Error: prefix)");
	}

	/**
	 * === Test-Case: ClaudeRunner treats exit 137 (SIGKILL) as non-retryable ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but failed non-retryably
	 * |===
	 * | Exit
	 * | 137
	 * |===
	 *
	 * === Then: The claude runner exit code will be 137
	 * </pre>
	 */
	@Test
	void exit137_sigkill_doesNotRetry() throws Exception {
		assertNoRetry(137, "");
	}

	/**
	 * === Test-Case: ClaudeRunner treats exit 130 (SIGINT) as non-retryable ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but failed non-retryably
	 * |===
	 * | Exit
	 * | 130
	 * |===
	 *
	 * === Then: The claude runner exit code will be 130
	 * </pre>
	 */
	@Test
	void exit130_sigint_doesNotRetry() throws Exception {
		assertNoRetry(130, "");
	}

	private void assertNoRetry(int exitCode, String stdout) throws Exception {
		ProcessStarter starter = pb -> new FakeProcess(stdout, exitCode);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 0, starter);

		int actualExit = claude.run(workDir.toString(), "/rgr-green project tag");

		assertThat(actualExit).isEqualTo(exitCode);
		List<String> lines = Files.readAllLines(logFile);
		long executingLines = lines.stream().filter(l -> l.contains("Executing: claude")).count();
		assertThat(executingLines)
			.as("non-retryable exit %d must produce exactly one subprocess invocation", exitCode)
			.isEqualTo(1);
		assertThat(lines)
			.as("runner log must record the exit code and omit retry markers")
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI exited with code " + exitCode))
			.noneMatch(l -> l.contains("Retry attempt"))
			.noneMatch(l -> l.contains("Retryable error detected"));
	}
}
