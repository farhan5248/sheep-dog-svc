package org.farhan.mbt.maven;

public class GreenPhase extends RgrPhase {

	public GreenPhase(ClaudeRunner claude, MavenRunner maven, GitRunner git, DarmokMojoLog mojoLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds,
			int maxAllowlistAttempts) {
		super(claude, maven, git, mojoLog, workingDir, targetDir, artifactId,
			maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds, maxAllowlistAttempts);
	}

	@Override
	protected Phase phase() {
		return Phase.GREEN;
	}

	@Override
	protected boolean requiresVerifyLoop() {
		return true;
	}

	@Override
	protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception {
		String pattern = state.tag;
		int claudeExit = claude.run(workingDir, "/rgr-green " + artifactId + " " + pattern);
		return runTimeoutRecoveryLoop(claudeExit);
	}
}
