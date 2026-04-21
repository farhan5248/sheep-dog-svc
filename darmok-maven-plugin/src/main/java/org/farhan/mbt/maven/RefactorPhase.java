package org.farhan.mbt.maven;

public class RefactorPhase {

	private final ClaudeRunner claude;
	private final MavenRunner maven;
	private final DarmokMojoLog mojoLog;
	private final String workingDir;
	private final String targetDir;
	private final String artifactId;
	private final int maxVerifyAttempts;

	public RefactorPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog,
			String workingDir, String targetDir, String artifactId, int maxVerifyAttempts) {
		this.claude = claude;
		this.maven = maven;
		this.mojoLog = mojoLog;
		this.workingDir = workingDir;
		this.targetDir = targetDir;
		this.artifactId = artifactId;
		this.maxVerifyAttempts = maxVerifyAttempts;
	}

	public PhaseResult run() throws Exception {
		mojoLog.info("  Refactor: Running...");
		long start = System.currentTimeMillis();
		int claudeExit = claude.run(workingDir, "/rgr-refactor forward " + artifactId);
		long claudeDuration = System.currentTimeMillis() - start;
		mojoLog.info("  Refactor: Completed (" + PhaseResult.formatDuration(claudeDuration) + ")");
		if (claudeExit != 0) {
			return new PhaseResult(claudeExit, claudeDuration);
		}
		runVerifyLoop();
		long totalDuration = System.currentTimeMillis() - start;
		return new PhaseResult(0, totalDuration);
	}

	private void runVerifyLoop() throws Exception {
		for (int attempt = 1; attempt <= maxVerifyAttempts; attempt++) {
			mojoLog.info("  Refactor: Verify running...");
			long verifyStart = System.currentTimeMillis();
			int verifyExit = maven.run(targetDir, "clean", "verify");
			long verifyDuration = System.currentTimeMillis() - verifyStart;
			if (verifyExit == 0) {
				mojoLog.info("  Refactor: Verify passed (" + PhaseResult.formatDuration(verifyDuration) + ")");
				return;
			}
			if (attempt < maxVerifyAttempts) {
				mojoLog.warn("  Refactor: Verify failed (attempt " + attempt + "/" + maxVerifyAttempts + "), resuming claude...");
				claude.resume(workingDir, GreenPhase.VERIFY_RESUME_MESSAGE);
			}
		}
		mojoLog.error("  Refactor: Verify failed after " + maxVerifyAttempts + " attempts, aborting");
		throw new Exception("rgr-refactor verify failed after " + maxVerifyAttempts + " attempts");
	}
}
