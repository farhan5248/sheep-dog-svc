package org.farhan.mbt.maven;

public class RefactorPhase {

	private final ClaudeRunner claude;
	private final DarmokMojoLog mojoLog;
	private final String workingDir;
	private final String artifactId;

	public RefactorPhase(ClaudeRunner claude, DarmokMojoLog mojoLog, String workingDir, String artifactId) {
		this.claude = claude;
		this.mojoLog = mojoLog;
		this.workingDir = workingDir;
		this.artifactId = artifactId;
	}

	public PhaseResult run() throws Exception {
		mojoLog.info("  Refactor: Running...");
		long start = System.currentTimeMillis();
		int exitCode = claude.run(workingDir, "/rgr-refactor forward " + artifactId);
		long duration = System.currentTimeMillis() - start;
		mojoLog.info("  Refactor: Completed (" + PhaseResult.formatDuration(duration) + ")");
		return new PhaseResult(exitCode, duration);
	}
}
