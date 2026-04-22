package org.farhan.mbt.maven;

public class RefactorPhase extends RgrPhase {

	public RefactorPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds) {
		super(claude, maven, mojoLog, workingDir, targetDir, artifactId,
			maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds);
	}

	@Override
	protected Phase phase() {
		return Phase.REFACTOR;
	}

	@Override
	protected boolean requiresVerifyLoop() {
		return true;
	}

	@Override
	protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception {
		int claudeExit = claude.run(workingDir, "/rgr-refactor forward " + artifactId);
		return runTimeoutRecoveryLoop(claudeExit);
	}
}
