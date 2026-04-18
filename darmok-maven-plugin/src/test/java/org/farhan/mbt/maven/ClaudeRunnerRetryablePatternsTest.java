package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Covers the 4 entries in {@link ClaudeRunner} RETRYABLE_PATTERNS individually (#258).
 * First variant landed: "API Error: 529". Remaining 3 (500, Internal server error, overloaded)
 * to follow in the same shape once this header-comment style is confirmed.
 *
 * <p>Open question for pass 2 grammar work: ClaudeRunner-internal tests like these
 * could either become standalone asciidoc specs scoped to "the claude command", or
 * be absorbed into goal-level specs that happen to stage a 529-flavored claude call.
 * The ClaudeRunner-level JUnit form here is the Detroit-school unit test shape.
 */
class ClaudeRunnerRetryablePatternsTest {

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
	 * === Test-Case: ClaudeRunner retries on API Error 529 then succeeds ===
	 *
	 * <p>Sketch of the eventual asciidoc spec if ClaudeRunner-level specs are in scope:
	 *
	 * <pre>
	 * == Test-Case: ClaudeRunner retries on API Error 529 then succeeds
	 *
	 * === Given: The claude command produced this output on each call
	 * |===
	 * | ExitCode | Output
	 * | 1        | API Error: 529 Service overloaded
	 * | 0        | mocked claude output line
	 * |===
	 *
	 * === When: The claude runner is executed
	 *
	 * === Then: The claude runner log will be as follows
	 * |===
	 * | Level | Category | Content
	 * | WARN  | Claude   | Retryable error detected: API Error: 529
	 * | DEBUG | Claude   | Retry attempt 2 of 3
	 * | DEBUG | Claude   | Claude CLI completed successfully
	 * |===
	 *
	 * === Then: The claude runner exit code will be 0
	 * </pre>
	 */
	@Test
	void apiError529_retriesAndRecovers() throws Exception {
		assertRecoversFromPattern("API Error: 529 Service overloaded", "API Error: 529");
	}

	/**
	 * === Test-Case: ClaudeRunner retries on API Error 500 then succeeds ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed and successful after retries
	 * |===
	 * | Pattern
	 * | API Error: 500
	 * |===
	 *
	 * === Then: The claude runner log will be as follows with this warning
	 * |===
	 * | Level | Category | Content
	 * | WARN  | Claude   | Retryable error detected: API Error: 500
	 * | DEBUG | Claude   | Retry attempt 2 of 3
	 * | DEBUG | Claude   | Claude CLI completed successfully
	 * |===
	 * </pre>
	 */
	@Test
	void apiError500_retriesAndRecovers() throws Exception {
		assertRecoversFromPattern("API Error: 500 Internal server error", "API Error: 500");
	}

	/**
	 * === Test-Case: ClaudeRunner retries on "Internal server error" then succeeds ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed and successful after retries
	 * |===
	 * | Pattern
	 * | Internal server error
	 * |===
	 * </pre>
	 */
	@Test
	void internalServerError_retriesAndRecovers() throws Exception {
		assertRecoversFromPattern("Upstream: Internal server error (trace id abc)", "Internal server error");
	}

	/**
	 * === Test-Case: ClaudeRunner retries on "overloaded" then succeeds ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed and successful after retries
	 * |===
	 * | Pattern
	 * | overloaded
	 * |===
	 * </pre>
	 */
	@Test
	void overloaded_retriesAndRecovers() throws Exception {
		assertRecoversFromPattern("Service is overloaded, please try again later", "overloaded");
	}

	/**
	 * === Test-Case: ClaudeRunner exhausts retries on API Error 500 ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but fails after reaching retry limit
	 * |===
	 * | Pattern
	 * | API Error: 500
	 * |===
	 *
	 * === Then: The claude runner log will be as follows with this failure
	 * |===
	 * | Level | Category | Content
	 * | ERROR | Claude   | Retryable error detected: API Error: 500
	 * | ERROR | Claude   | Max retries (3) exhausted
	 * | DEBUG | Claude   | Claude CLI exited with code 1
	 * |===
	 * </pre>
	 */
	@Test
	void apiError500_retriesAndExhausts() throws Exception {
		assertExhaustsOnPattern("API Error: 500 Internal server error", "API Error: 500");
	}

	/**
	 * === Test-Case: ClaudeRunner exhausts retries on API Error 529 ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but fails after reaching retry limit
	 * |===
	 * | Pattern
	 * | API Error: 529
	 * |===
	 * </pre>
	 */
	@Test
	void apiError529_retriesAndExhausts() throws Exception {
		assertExhaustsOnPattern("API Error: 529 Service overloaded", "API Error: 529");
	}

	/**
	 * === Test-Case: ClaudeRunner exhausts retries on "Internal server error" ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but fails after reaching retry limit
	 * |===
	 * | Pattern
	 * | Internal server error
	 * |===
	 * </pre>
	 */
	@Test
	void internalServerError_retriesAndExhausts() throws Exception {
		assertExhaustsOnPattern("Upstream: Internal server error (trace id abc)", "Internal server error");
	}

	/**
	 * === Test-Case: ClaudeRunner exhausts retries on "overloaded" ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but fails after reaching retry limit
	 * |===
	 * | Pattern
	 * | overloaded
	 * |===
	 * </pre>
	 */
	@Test
	void overloaded_retriesAndExhausts() throws Exception {
		assertExhaustsOnPattern("Service is overloaded, please try again later", "overloaded");
	}

	private void assertRecoversFromPattern(String errorStdout, String expectedLoggedPattern) throws Exception {
		Deque<FakeProcessSpec> specs = new ArrayDeque<>();
		specs.add(new FakeProcessSpec(errorStdout, 1));
		specs.add(new FakeProcessSpec("mocked claude output line", 0));
		ProcessStarter starter = pb -> {
			FakeProcessSpec s = specs.poll();
			return new FakeProcess(s.stdout(), s.exit());
		};
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 0, starter);

		int exit = claude.run(workDir.toString(), "/rgr-green project tag");

		assertThat(exit).isEqualTo(0);
		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.as("runner log must show retry detection for '%s', retry attempt, then success", expectedLoggedPattern)
			.anyMatch(l -> l.contains("WARN  [Claude] Retryable error detected: " + expectedLoggedPattern))
			.anyMatch(l -> l.contains("DEBUG [Claude] Retry attempt 2 of 3"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI completed successfully"));
	}

	private void assertExhaustsOnPattern(String errorStdout, String expectedLoggedPattern) throws Exception {
		ProcessStarter starter = pb -> new FakeProcess(errorStdout, 1);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 0, starter);

		int exit = claude.run(workDir.toString(), "/rgr-green project tag");

		assertThat(exit).isEqualTo(1);
		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.as("runner log must escalate to ERROR with 'Max retries exhausted' for pattern '%s'", expectedLoggedPattern)
			.anyMatch(l -> l.contains("ERROR [Claude] Retryable error detected: " + expectedLoggedPattern))
			.anyMatch(l -> l.contains("ERROR [Claude] Max retries (3) exhausted"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI exited with code 1"));
	}

	private record FakeProcessSpec(String stdout, int exit) {}
}
