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
 * Retry-on-refactor variant from #258.
 * <p>
 * The retry loop is inside ClaudeRunner, so it applies identically to green and
 * refactor invocations. Tree Paths 12 / 13 covered the retry via the green phase.
 * This test stages the same retryable-then-success sequence against the refactor
 * phase's ClaudeRunner instance to prove the behavior isn't accidentally bound
 * to the green-phase prompt.
 */
class RefactorPhaseRetryTest {

	@TempDir
	Path workDir;

	Path runnerLogFile;
	MojoLog mojoLog;
	MojoLog runnerLog;

	@BeforeEach
	void setup() throws Exception {
		runnerLogFile = workDir.resolve("runner.log");
		mojoLog = new MojoLog(new SystemStreamLog(), "mojo", workDir.resolve("mojo.log"));
		runnerLog = new MojoLog(new SystemStreamLog(), "Claude", runnerLogFile);
	}

	@AfterEach
	void teardown() {
		if (mojoLog != null) mojoLog.close();
		if (runnerLog != null) runnerLog.close();
	}

	/**
	 * === Test-Case: RefactorPhase retries on API Error 500 then succeeds ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-refactor command is executed and successful after retries
	 * |===
	 * | Pattern
	 * | API Error: 500
	 * |===
	 *
	 * === When: The darmok plugin gen-from-existing goal refactor phase is executed
	 *
	 * === Then: The refactor phase result exit code will be 0
	 *
	 * === Then: The code-prj project target/darmok/darmok.runners.log file will be as follows with this warning
	 * |===
	 * | Level | Category | Content
	 * | WARN  | Claude   | Retryable error detected: API Error: 500
	 * | DEBUG | Claude   | Retry attempt 2 of 3
	 * | DEBUG | Claude   | Claude CLI completed successfully
	 * |===
	 * </pre>
	 */
	@Test
	void refactorPhase_retriesOnApiError_thenSucceeds() throws Exception {
		Deque<FakeProcessSpec> specs = new ArrayDeque<>();
		specs.add(new FakeProcessSpec("API Error: 500 Internal server error", 1));
		specs.add(new FakeProcessSpec("refactor complete", 0));
		ProcessStarter starter = pb -> {
			FakeProcessSpec s = specs.poll();
			return new FakeProcess(s.stdout(), s.exit());
		};
		ClaudeRunner claude = new ClaudeRunner(runnerLog, "sonnet", 3, 0, starter);
		RefactorPhase phase = new RefactorPhase(claude, mojoLog, workDir.toString(), "code-prj");

		PhaseResult result = phase.run();

		assertThat(result.exitCode()).isEqualTo(0);
		List<String> lines = Files.readAllLines(runnerLogFile);
		assertThat(lines)
			.as("refactor-phase retry must show 500 detection, retry attempt, then success — same contract as green-phase retry")
			.anyMatch(l -> l.contains("WARN  [Claude] Retryable error detected: API Error: 500"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Retry attempt 2 of 3"))
			.anyMatch(l -> l.contains("DEBUG [Claude] Claude CLI completed successfully"));
	}

	private record FakeProcessSpec(String stdout, int exit) {}
}
