package org.farhan.mbt.maven;

public class GreenPhase {

	private final ClaudeRunner claude;
	private final DarmokMojoLog mojoLog;
	private final String workingDir;
	private final String artifactId;

	public GreenPhase(ClaudeRunner claude, DarmokMojoLog mojoLog, String workingDir, String artifactId) {
		this.claude = claude;
		this.mojoLog = mojoLog;
		this.workingDir = workingDir;
		this.artifactId = artifactId;
	}

	public PhaseResult run(String pattern) throws Exception {
		mojoLog.info("  Green: Running...");
		long start = System.currentTimeMillis();
		int exitCode = claude.run(workingDir, "/rgr-green " + artifactId + " " + pattern);
		long duration = System.currentTimeMillis() - start;
		mojoLog.info("  Green: Completed (" + PhaseResult.formatDuration(duration) + ")");
		return new PhaseResult(exitCode, duration);
	}
}
