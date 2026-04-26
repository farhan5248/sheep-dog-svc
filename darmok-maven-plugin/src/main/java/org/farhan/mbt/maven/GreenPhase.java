package org.farhan.mbt.maven;

import java.util.List;

public class GreenPhase extends RgrPhase {

	private final boolean greenFullPathsEnabled;

	public GreenPhase(ClaudeRunner claude, MavenRunner maven, GitRunner git, DarmokMojoLog mojoLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds,
			int maxAllowlistAttempts, List<String> allowlistPaths, boolean greenFullPathsEnabled) {
		super(claude, maven, git, mojoLog, workingDir, targetDir, artifactId,
			maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds, maxAllowlistAttempts, allowlistPaths);
		this.greenFullPathsEnabled = greenFullPathsEnabled;
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
		String args;
		if (greenFullPathsEnabled) {
			String runnerClassName = pattern + "Test";
			String logPath = targetDir + "/log.txt";
			String jacocoPath = targetDir + "/target/site/jacoco-with-tests";
			String umlDir = targetDir + "/site/uml";
			args = "/rgr-green " + targetDir + " " + runnerClassName + " " + logPath + " " + jacocoPath + " " + umlDir;
		} else {
			args = "/rgr-green " + artifactId + " " + pattern;
		}
		int claudeExit = claude.run(workingDir, args);
		return runTimeoutRecoveryLoop(claudeExit);
	}
}
