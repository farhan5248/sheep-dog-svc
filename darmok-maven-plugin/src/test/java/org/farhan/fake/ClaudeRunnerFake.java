package org.farhan.fake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.farhan.common.CommandFake;
import org.farhan.mbt.maven.Claude;
import org.farhan.mbt.maven.ClaudeRunner;

/**
 * Replay-driven fake for the production {@code ClaudeRunner}. Walks the
 * {@code captures/claude.yaml} path declared for the active scenario.
 * <p>
 * Models the production primitive's contract: each invocation captures stdout
 * lines into the caller-supplied list and returns the exit code (or
 * {@link ClaudeRunner#TIMEOUT_EXIT_CODE} for vertices flagged with
 * {@code wait_for_destroy} or {@code blocked_reader}).
 */
public class ClaudeRunnerFake extends CommandFake implements Claude {

	private String sessionId;

	public ClaudeRunnerFake(Map<String, Object> properties) {
		super(properties);
	}

	public static ClaudeRunnerFake from(HashMap<String, Object> properties) {
		return new ClaudeRunnerFake(properties);
	}

	@Override
	protected String commandKey() {
		return "claude";
	}

	@Override
	public int run(String workingDirectory, List<String> outputLines, String... args) {
		simulateSubprocessTime();
		Vertex v = consume();
		logExecute(v);
		appendStdoutLines(outputLines, v.stdout());
		if (v.hangs() || v.blocked()) {
			return ClaudeRunner.TIMEOUT_EXIT_CODE;
		}
		return v.exit();
	}

	@Override
	public int resume(String workingDirectory, List<String> outputLines, String message) {
		simulateSubprocessTime();
		Vertex v = consume();
		logExecute(v);
		appendStdoutLines(outputLines, v.stdout());
		if (v.hangs() || v.blocked()) {
			return ClaudeRunner.TIMEOUT_EXIT_CODE;
		}
		return v.exit();
	}

	/**
	 * Real claude invocations take seconds; the phase metric needs a non-zero
	 * duration so tests can distinguish "ran" from "didn't run". 1ms is enough.
	 */
	private static void simulateSubprocessTime() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	private static void appendStdoutLines(List<String> outputLines, String stdout) {
		if (stdout == null || stdout.isEmpty()) return;
		for (String line : stdout.split("\\r?\\n")) {
			outputLines.add(line);
		}
	}
}
