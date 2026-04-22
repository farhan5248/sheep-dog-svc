package org.farhan.mbt.maven;

public abstract class RgrPhase {

	static final String VERIFY_RESUME_MESSAGE = "mvn clean verify failures should be fixed";
	static final String TIMEOUT_RESUME_MESSAGE = "pls continue";

	protected final ClaudeRunner claude;
	protected final MavenRunner maven;
	protected final DarmokMojoLog mojoLog;
	protected final String workingDir;
	protected final String targetDir;
	protected final String artifactId;
	protected final int maxVerifyAttempts;
	protected final int maxTimeoutAttempts;
	protected final int maxClaudeSeconds;

	protected RgrPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog,
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

	protected abstract Phase phase();

	protected abstract int executeClaudeOrMaven(DarmokMojoState state) throws Exception;

	protected abstract boolean requiresVerifyLoop();

	public DarmokMojoState run(DarmokMojoState state) throws Exception {
		String name = phase().displayName;
		mojoLog.info("  " + name + ": Running...");
		long start = System.currentTimeMillis();
		int exitCode = executeClaudeOrMaven(state);
		long duration = System.currentTimeMillis() - start;
		mojoLog.info("  " + name + ": Completed (" + DarmokMojoState.formatDuration(duration) + ")");
		if (exitCode == 0 && requiresVerifyLoop()) {
			runVerifyLoop();
			duration = System.currentTimeMillis() - start;
		}
		state.exitCode = exitCode;
		state.setDuration(phase(), duration);
		return state;
	}

	protected int runTimeoutRecoveryLoop(int claudeExit) throws Exception {
		if (claudeExit != ClaudeRunner.TIMEOUT_EXIT_CODE) {
			return claudeExit;
		}
		String name = phase().displayName;
		mojoLog.warn("  " + name + ": Claude timed out after " + maxClaudeSeconds + "s, killing...");
		int attempt = 1;
		while (true) {
			mojoLog.info("  " + name + ": Running mvn clean install to check phase state...");
			int installExit = maven.run(targetDir, "clean", "install");
			if (installExit == 0) {
				mojoLog.info("  " + name + ": Install passed, proceeding");
				return 0;
			}
			if (attempt >= maxTimeoutAttempts) {
				mojoLog.error("  " + name + ": Timeout exhausted after " + maxTimeoutAttempts + " attempts");
				throw new Exception("rgr-" + name.toLowerCase() + " timed out after " + maxTimeoutAttempts + " attempts");
			}
			attempt++;
			mojoLog.info("  " + name + ": Install failed, resuming claude (attempt " + attempt
				+ " of " + maxTimeoutAttempts + ")...");
			claude.resume(workingDir, TIMEOUT_RESUME_MESSAGE);
		}
	}

	protected void runVerifyLoop() throws Exception {
		String name = phase().displayName;
		for (int attempt = 1; attempt <= maxVerifyAttempts; attempt++) {
			mojoLog.info("  " + name + ": Verify running...");
			long verifyStart = System.currentTimeMillis();
			int verifyExit = maven.run(targetDir, "clean", "verify");
			long verifyDuration = System.currentTimeMillis() - verifyStart;
			if (verifyExit == 0) {
				mojoLog.info("  " + name + ": Verify passed (" + DarmokMojoState.formatDuration(verifyDuration) + ")");
				return;
			}
			if (attempt < maxVerifyAttempts) {
				mojoLog.warn("  " + name + ": Verify failed (attempt " + attempt + "/" + maxVerifyAttempts + "), resuming claude...");
				claude.resume(workingDir, VERIFY_RESUME_MESSAGE);
			}
		}
		mojoLog.error("  " + name + ": Verify failed after " + maxVerifyAttempts + " attempts, aborting");
		throw new Exception("rgr-" + name.toLowerCase() + " verify failed after " + maxVerifyAttempts + " attempts");
	}
}
