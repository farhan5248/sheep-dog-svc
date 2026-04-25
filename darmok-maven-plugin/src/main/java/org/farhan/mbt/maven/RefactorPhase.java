package org.farhan.mbt.maven;

public class RefactorPhase extends RgrPhase {

	private final String refactorSessionMode;

	public RefactorPhase(ClaudeRunner claude, MavenRunner maven, GitRunner git, DarmokMojoLog mojoLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds,
			int maxAllowlistAttempts, String refactorSessionMode) {
		super(claude, maven, git, mojoLog, workingDir, targetDir, artifactId,
			maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds, maxAllowlistAttempts);
		this.refactorSessionMode = refactorSessionMode;
	}

	@Override
	protected Phase phase() {
		return Phase.REFACTOR;
	}

	@Override
	protected boolean requiresVerifyLoop() {
		return true;
	}

	public void prepareSession(ClaudeRunner greenClaude) throws Exception {
		if (!"continue".equals(refactorSessionMode)) {
			return;
		}
		claude.setSessionId(greenClaude.getSessionId());
		claude.resume(workingDir, "/compact");
	}

	@Override
	protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception {
		if ("continue".equals(refactorSessionMode)) {
			int claudeExit = claude.resume(workingDir, "/rgr-refactor forward " + artifactId);
			return runTimeoutRecoveryLoop(claudeExit);
		}
		int claudeExit = claude.run(workingDir, "/rgr-refactor forward " + artifactId);
		return runTimeoutRecoveryLoop(claudeExit);
	}
}
