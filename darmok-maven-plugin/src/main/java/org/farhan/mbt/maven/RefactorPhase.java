package org.farhan.mbt.maven;

import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class RefactorPhase extends RgrPhase {

	private final String refactorSessionMode;

	public RefactorPhase(Claude claude, Maven maven, Git git, DarmokMojoLog mojoLog, Log runnerLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds,
			int maxAllowlistAttempts, int maxRetries, int retryWaitSeconds,
			List<String> allowlistPaths, String refactorSessionMode) {
		super(claude, maven, git, mojoLog, runnerLog, workingDir, targetDir, artifactId,
			maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds, maxAllowlistAttempts,
			maxRetries, retryWaitSeconds, allowlistPaths);
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

	public void prepareSession(Claude greenClaude) throws Exception {
		if (!"continue".equals(refactorSessionMode)) {
			return;
		}
		claude.setSessionId(greenClaude.getSessionId());
		resumeClaudeWithRetry(workingDir, "/compact");
	}

	@Override
	protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception {
		if ("continue".equals(refactorSessionMode)) {
			int claudeExit = resumeClaudeWithRetry(workingDir, "/rgr-refactor forward " + artifactId);
			return runTimeoutRecoveryLoop(claudeExit);
		}
		int claudeExit = runClaudeWithRetry(workingDir, "/rgr-refactor forward " + artifactId);
		return runTimeoutRecoveryLoop(claudeExit);
	}
}
