package org.farhan.mbt.maven;

public class GreenPhase {

	static final String VERIFY_RESUME_MESSAGE = "mvn clean verify failures should be fixed";

	private final ClaudeRunner claude;
	private final MavenRunner maven;
	private final DarmokMojoLog mojoLog;
	private final String workingDir;
	private final String targetDir;
	private final String artifactId;
	private final int maxVerifyAttempts;

	public GreenPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog,
			String workingDir, String targetDir, String artifactId, int maxVerifyAttempts) {
		this.claude = claude;
		this.maven = maven;
		this.mojoLog = mojoLog;
		this.workingDir = workingDir;
		this.targetDir = targetDir;
		this.artifactId = artifactId;
		this.maxVerifyAttempts = maxVerifyAttempts;
	}

	public PhaseResult run(String pattern) throws Exception {
		mojoLog.info("  Green: Running...");
		long start = System.currentTimeMillis();
		int claudeExit = claude.run(workingDir, "/rgr-green " + artifactId + " " + pattern);
		long claudeDuration = System.currentTimeMillis() - start;
		mojoLog.info("  Green: Completed (" + PhaseResult.formatDuration(claudeDuration) + ")");
		if (claudeExit != 0) {
			return new PhaseResult(claudeExit, claudeDuration);
		}
		runVerifyLoop();
		long totalDuration = System.currentTimeMillis() - start;
		return new PhaseResult(0, totalDuration);
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
