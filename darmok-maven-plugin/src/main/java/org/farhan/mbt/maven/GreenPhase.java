package org.farhan.mbt.maven;

public class GreenPhase {

	static final String VERIFY_RESUME_MESSAGE = "mvn clean verify failures should be fixed";
	static final String TIMEOUT_RESUME_MESSAGE = "pls continue";

	private final ClaudeRunner claude;
	private final MavenRunner maven;
	private final DarmokMojoLog mojoLog;
	private final String workingDir;
	private final String targetDir;
	private final String artifactId;
	private final int maxVerifyAttempts;
	private final int maxTimeoutAttempts;
	private final int maxClaudeSeconds;

	public GreenPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds) {
		this.claude = claude;
		this.maven = maven;
		this.mojoLog = mojoLog;
		this.workingDir = workingDir;
		this.targetDir = targetDir;
		this.artifactId = artifactId;
		this.maxVerifyAttempts = maxVerifyAttempts;
		this.maxTimeoutAttempts = maxTimeoutAttempts;
		this.maxClaudeSeconds = maxClaudeSeconds;
	}

	public PhaseResult run(String pattern) throws Exception {
		mojoLog.info("  Green: Running...");
		long start = System.currentTimeMillis();
		int claudeExit = claude.run(workingDir, "/rgr-green " + artifactId + " " + pattern);
		claudeExit = runTimeoutRecoveryLoop(claudeExit);
		long claudeDuration = System.currentTimeMillis() - start;
		mojoLog.info("  Green: Completed (" + PhaseResult.formatDuration(claudeDuration) + ")");
		if (claudeExit != 0) {
			return new PhaseResult(claudeExit, claudeDuration);
		}
		runVerifyLoop();
		long totalDuration = System.currentTimeMillis() - start;
		return new PhaseResult(0, totalDuration);
	}

	private int runTimeoutRecoveryLoop(int claudeExit) throws Exception {
		if (claudeExit != ClaudeRunner.TIMEOUT_EXIT_CODE) {
			return claudeExit;
		}
		mojoLog.warn("  Green: Claude timed out after " + maxClaudeSeconds + "s, killing...");
		int attempt = 1;
		while (true) {
			mojoLog.info("  Green: Running mvn clean install to check phase state...");
			int installExit = maven.run(targetDir, "clean", "install");
			if (installExit == 0) {
				mojoLog.info("  Green: Install passed, proceeding");
				return 0;
			}
			if (attempt >= maxTimeoutAttempts) {
				mojoLog.error("  Green: Timeout exhausted after " + maxTimeoutAttempts + " attempts");
				throw new Exception("rgr-green timed out after " + maxTimeoutAttempts + " attempts");
			}
			attempt++;
			mojoLog.info("  Green: Install failed, resuming claude (attempt " + attempt
				+ " of " + maxTimeoutAttempts + ")...");
			// The resume's exit code is ignored: we re-check via mvn clean install
			// on the next loop iteration. If resume itself times out, that's fine
			// — the install check is the authoritative signal for whether claude's
			// latest session left the code in a usable state.
			claude.resume(workingDir, TIMEOUT_RESUME_MESSAGE);
		}
	}

	private void runVerifyLoop() throws Exception {
		for (int attempt = 1; attempt <= maxVerifyAttempts; attempt++) {
			mojoLog.info("  Green: Verify running...");
			long verifyStart = System.currentTimeMillis();
			int verifyExit = maven.run(targetDir, "clean", "verify");
			long verifyDuration = System.currentTimeMillis() - verifyStart;
			if (verifyExit == 0) {
				mojoLog.info("  Green: Verify passed (" + PhaseResult.formatDuration(verifyDuration) + ")");
				return;
			}
			if (attempt < maxVerifyAttempts) {
				mojoLog.warn("  Green: Verify failed (attempt " + attempt + "/" + maxVerifyAttempts + "), resuming claude...");
				claude.resume(workingDir, VERIFY_RESUME_MESSAGE);
			}
		}
		mojoLog.error("  Green: Verify failed after " + maxVerifyAttempts + " attempts, aborting");
		throw new Exception("rgr-green verify failed after " + maxVerifyAttempts + " attempts");
	}
}
