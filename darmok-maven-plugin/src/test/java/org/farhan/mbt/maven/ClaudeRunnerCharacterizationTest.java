package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
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
		// Given: the claude command completes successfully on the first attempt
		//        (exit 0 with a single line of output)
		ProcessRunner.starter = pb -> new FakeProcess("mocked claude output line", 0);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 30);

		// When: the claude runner is executed with args "/rgr-green sample-project sampleTag"
		int exit = claude.run(workDir.toString(), "/rgr-green sample-project sampleTag");

		// Then: runner log contains the executed command and success marker; no ERROR lines
		assertThat(exit).isEqualTo(0);
		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.as("runner log contract — must expose executed command and success marker")
			.anyMatch(l -> l.contains("DEBUG [Claude] Executing: claude"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI completed successfully"))
			.noneMatch(l -> l.contains("ERROR"));
	}

	/**
	 * Given: the first Claude CLI attempt fails with a retryable API error (HTTP 500),
	 *   and the second attempt succeeds with exit code 0.
	 * When: ClaudeRunner.run() is invoked with a prompt.
	 * Then: the runner log records the retryable-error detection, the retry attempt
	 *   marker, and the final success marker; the method returns exit code 0.
	 * <p>
	 * This characterizes the automatic-retry resilience contract that keeps the
	 * green-phase robust against transient upstream overload.
	 */
	@Test
	void retryRecovers_logsRetryableErrorAndSuccess() throws Exception {
		// Given: the claude command produces a retryable HTTP 500 on its first attempt
		//        and succeeds on its second attempt
		Deque<FakeProcessSpec> specs = new ArrayDeque<>();
		specs.add(new FakeProcessSpec("API Error: 500 Internal server error", 1));
		specs.add(new FakeProcessSpec("mocked claude output line", 0));
		ProcessRunner.starter = pb -> {
			FakeProcessSpec s = specs.poll();
			return new FakeProcess(s.stdout(), s.exit());
		};
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 0);

		// When: the claude runner is executed with args "/rgr-green project tag"
		int exit = claude.run(workDir.toString(), "/rgr-green project tag");

		// Then: runner log shows retryable-error detection, retry attempt marker, and success
		assertThat(exit).isEqualTo(0);
		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.as("runner log must show retry then success")
			.anyMatch(l -> l.contains("WARN  [Claude] Retryable error detected: API Error: 500"))
			.anyMatch(l -> l.contains("WARN  [Claude] Waiting 0 seconds before retry"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Retry attempt 2 of 3"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI completed successfully"));
	}

	/**
	 * Given: every Claude CLI attempt fails with the same retryable API error,
	 *   up to the configured maxRetries (3).
	 * When: ClaudeRunner.run() is invoked.
	 * Then: the runner log records the exhausted-retry state at ERROR level and
	 *   the method returns the failing exit code from the final attempt.
	 * <p>
	 * This characterizes the bounded-retry contract: transient failures retry,
	 * persistent failures surface as ERROR so #140-style timeout/rollback logic
	 * (future) can act on them.
	 */
	@Test
	void retriesExhausted_logsMaxRetriesExhaustedAtError() throws Exception {
		// Given: the claude command returns a retryable HTTP 500 on every attempt
		//        up to the configured max retries (3)
		ProcessRunner.starter = pb -> new FakeProcess("API Error: 500 Internal server error", 1);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 0);

		// When: the claude runner is executed with args "/rgr-green project tag"
		int exit = claude.run(workDir.toString(), "/rgr-green project tag");

		// Then: runner log escalates to ERROR with "Max retries exhausted"
		assertThat(exit).isEqualTo(1);
		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.as("runner log must escalate to ERROR when retries are exhausted")
			.anyMatch(l -> l.contains("ERROR [Claude] Retryable error detected: API Error: 500"))
			.anyMatch(l -> l.contains("ERROR [Claude] Max retries (3) exhausted"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI exited with code 1"));
	}

	/**
	 * Given: Claude CLI fails with a non-retryable error (no pattern match in stdout).
	 * When: ClaudeRunner.run() is invoked.
	 * Then: no retry is attempted, the failing exit code is returned, and the log
	 *   contains exactly one "Executing:" line (proving only one subprocess invocation).
	 * <p>
	 * This characterizes the fail-fast contract for non-transient errors — we don't
	 * burn cycles retrying a user error or a permanent upstream change.
	 */
	@Test
	void nonRetryableFailure_doesNotRetry_logsExitCode() throws Exception {
		// Given: the claude command fails with a non-retryable error on its first attempt
		//        (exit 2 with "permission denied" — no retryable-pattern match)
		ProcessRunner.starter = pb -> new FakeProcess("permission denied: /foo", 2);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 3, 0);

		// When: the claude runner is executed with args "/rgr-green project tag"
		int exit = claude.run(workDir.toString(), "/rgr-green project tag");

		// Then: exactly one subprocess invocation, no retry markers, failing exit code logged
		assertThat(exit).isEqualTo(2);
		List<String> lines = Files.readAllLines(logFile);
		long executingLines = lines.stream().filter(l -> l.contains("Executing: claude")).count();
		assertThat(executingLines)
			.as("non-retryable failures must not loop — exactly one subprocess invocation expected")
			.isEqualTo(1);
		assertThat(lines)
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI exited with code 2"))
			.noneMatch(l -> l.contains("Retry attempt"))
			.noneMatch(l -> l.contains("Retryable error detected"));
	}

	/**
	 * Given: Claude CLI produces multiple lines of stdout before exiting.
	 * When: ClaudeRunner.run() is invoked.
	 * Then: every stdout line appears in the runner log, preserving order.
	 * <p>
	 * This characterizes the log-capture contract that makes downstream log-based
	 * assertions (#155 green-phase verification, PBC timestamps) meaningful — if
	 * subprocess output weren't captured, the log would be useless as a contract.
	 */
	@Test
	void subprocessStdout_isMirroredToLogInOrder() throws Exception {
		// Given: the claude command writes three newline-separated lines of stdout
		//        and exits 0
		String stdout = String.join("\n",
			"line one of output",
			"line two of output",
			"line three of output");
		ProcessRunner.starter = pb -> new FakeProcess(stdout, 0);
		ClaudeRunner claude = new ClaudeRunner(categoryLog, "sonnet", 1, 0);

		// When: the claude runner is executed with args "/rgr-green project tag"
		claude.run(workDir.toString(), "/rgr-green project tag");

		// Then: the three output lines appear in the log in the order they were produced
		List<String> lines = Files.readAllLines(logFile);
		int firstIdx = indexOfContaining(lines, "line one of output");
		int secondIdx = indexOfContaining(lines, "line two of output");
		int thirdIdx = indexOfContaining(lines, "line three of output");
		assertThat(firstIdx).isGreaterThanOrEqualTo(0);
		assertThat(secondIdx).isGreaterThan(firstIdx);
		assertThat(thirdIdx).isGreaterThan(secondIdx);
	}

	/**
	 * Given: a GitRunner (which reuses {@link ProcessRunner#run} unchanged) is invoked.
	 * When: the seam returns a FakeProcess with exit code 0.
	 * Then: the log contains the "Running: git ..." marker from the base class
	 *   and the method returns the fake's exit code.
	 * <p>
	 * This is a sanity check that the ProcessStarter seam covers every runner that
	 * inherits ProcessRunner.run(), not just ClaudeRunner. Guards against future
	 * regressions where someone adds a new runner and bypasses the seam.
	 */
	@Test
	void gitRunnerThroughSeam_logsRunningCommand() throws Exception {
		// Given: the git command writes "nothing to commit" to stdout and exits 0
		//        (a clean repo state — nothing staged, nothing modified)
		ProcessRunner.starter = pb -> new FakeProcess("nothing to commit", 0);
		GitRunner git = new GitRunner(categoryLog);

		// When: the git runner is executed with args "status --porcelain"
		int exit = git.run(workDir.toString(), "status", "--porcelain");

		// Then: runner log contains "Running: git status --porcelain"
		assertThat(exit).isEqualTo(0);
		List<String> lines = Files.readAllLines(logFile);
		assertThat(lines)
			.anyMatch(l -> l.contains("DEBUG [Claude] Running: git status --porcelain"));
	}

	private static int indexOfContaining(List<String> lines, String token) {
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).contains(token)) {
				return i;
			}
		}
		return -1;
	}

	private record FakeProcessSpec(String stdout, int exit) {}
}
