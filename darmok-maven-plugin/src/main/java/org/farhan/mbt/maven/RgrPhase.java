package org.farhan.mbt.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public abstract class RgrPhase {

	static final String VERIFY_RESUME_MESSAGE = "mvn clean verify failures should be fixed";
	static final String TIMEOUT_RESUME_MESSAGE = "pls continue";
	static final String ALLOWLIST_RESUME_MESSAGE = "only modify files under src/main/java, src/test/java/org/farhan/impl or src/test/resources";

	protected final Claude claude;
	protected final Maven maven;
	protected final Git git;
	protected final DarmokMojoLog mojoLog;
	protected final Log runnerLog;
	protected final String workingDir;
	protected final String targetDir;
	protected final String artifactId;
	protected final int maxVerifyAttempts;
	protected final int maxTimeoutAttempts;
	protected final int maxClaudeSeconds;
	protected final int maxAllowlistAttempts;
	protected final int maxRetries;
	protected final int retryWaitSeconds;
	protected final List<String> allowlistPaths;

	protected RgrPhase(Claude claude, Maven maven, Git git, DarmokMojoLog mojoLog, Log runnerLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds,
			int maxAllowlistAttempts, int maxRetries, int retryWaitSeconds, List<String> allowlistPaths) {
		this.claude = claude;
		this.maven = maven;
		this.git = git;
		this.mojoLog = mojoLog;
		this.runnerLog = runnerLog;
		this.workingDir = workingDir;
		this.targetDir = targetDir;
		this.artifactId = artifactId;
		this.maxVerifyAttempts = maxVerifyAttempts;
		this.maxTimeoutAttempts = maxTimeoutAttempts;
		this.maxClaudeSeconds = maxClaudeSeconds;
		this.maxAllowlistAttempts = maxAllowlistAttempts;
		this.maxRetries = maxRetries;
		this.retryWaitSeconds = retryWaitSeconds;
		this.allowlistPaths = allowlistPaths;
	}

	protected int runClaudeWithRetry(String wd, String... args) throws Exception {
		return DarmokMojo.runClaudeWithRetry(runnerLog, mojoLog, maxRetries, retryWaitSeconds,
			outputLines -> claude.run(wd, outputLines, args));
	}

	protected int resumeClaudeWithRetry(String wd, String message) throws Exception {
		return DarmokMojo.runClaudeWithRetry(runnerLog, mojoLog, maxRetries, retryWaitSeconds,
			outputLines -> claude.resume(wd, outputLines, message));
	}

	protected abstract Phase phase();

	protected abstract int executeClaudeOrMaven(DarmokMojoState state) throws Exception;

	protected abstract boolean requiresVerifyLoop();

	protected String allowlistResumeMessage() {
		return ALLOWLIST_RESUME_MESSAGE;
	}

	protected boolean isAllowlisted(String path) {
		for (String allowed : allowlistPaths) {
			if (path.contains(allowed)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Suffix appended to the "Running"/"Completed" log lines naming what the phase is
	 * driving — Red drives maven, so its log lines say "Red: Running maven...". Green
	 * and Refactor drive claude with no extra suffix.
	 */
	protected String workSuffix() {
		return "";
	}

	public DarmokMojoState run(DarmokMojoState state) throws Exception {
		String name = phase().displayName;
		String suffix = workSuffix();
		mojoLog.info("  " + name + ": Running" + suffix + "...");
		long start = System.currentTimeMillis();
		int exitCode = 0;
		try {
			exitCode = executeClaudeOrMaven(state);
			long execDuration = System.currentTimeMillis() - start;
			mojoLog.info("  " + name + ": Completed" + suffix + " (" + DarmokMojoState.formatDuration(execDuration) + ")");
			if (exitCode == 0 && requiresVerifyLoop()) {
				runAllowlistCheck();
				runVerifyLoop();
			}
		} finally {
			state.exitCode = exitCode;
			state.setDuration(phase(), System.currentTimeMillis() - start);
		}
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
				throw new MojoExecutionException("rgr-" + name.toLowerCase() + " timed out after " + maxTimeoutAttempts + " attempts");
			}
			attempt++;
			mojoLog.info("  " + name + ": Install failed, resuming claude (attempt " + attempt
				+ " of " + maxTimeoutAttempts + ")...");
			resumeClaudeWithRetry(workingDir, TIMEOUT_RESUME_MESSAGE);
		}
	}

	protected void runVerifyLoop() throws Exception {
		String name = phase().displayName;
		java.nio.file.Path logFile = java.nio.file.Path.of(targetDir, "log.txt");
		for (int attempt = 1; attempt <= maxVerifyAttempts; attempt++) {
			mojoLog.info("  " + name + ": Verify running...");
			long verifyStart = System.currentTimeMillis();
			int verifyExit = maven.run(targetDir, logFile, "clean", "verify");
			long verifyDuration = System.currentTimeMillis() - verifyStart;
			if (verifyExit == 0) {
				mojoLog.info("  " + name + ": Verify passed (" + DarmokMojoState.formatDuration(verifyDuration) + ")");
				return;
			}
			if (attempt < maxVerifyAttempts) {
				mojoLog.warn("  " + name + ": Verify failed (attempt " + attempt + "/" + maxVerifyAttempts + "), resuming claude...");
				mojoLog.info("  " + name + ": Running...");
				long resumeStart = System.currentTimeMillis();
				resumeClaudeWithRetry(workingDir, VERIFY_RESUME_MESSAGE);
				long resumeDuration = System.currentTimeMillis() - resumeStart;
				mojoLog.info("  " + name + ": Completed (" + DarmokMojoState.formatDuration(resumeDuration) + ")");
			}
		}
		mojoLog.error("  " + name + ": Verify failed after " + maxVerifyAttempts + " attempts, aborting");
		throw new MojoExecutionException("rgr-" + name.toLowerCase() + " verify failed after " + maxVerifyAttempts + " attempts");
	}

	protected void runAllowlistCheck() throws Exception {
		String name = phase().displayName;
		for (int attempt = 1; attempt <= maxAllowlistAttempts; attempt++) {
			mojoLog.info("  " + name + ": Allowlist check running...");
			String porcelain = git.captureOutput(targetDir, "status", "--porcelain");
			List<String> violations = findAllowlistViolations(porcelain);
			if (violations.isEmpty()) {
				mojoLog.info("  " + name + ": Allowlist check passed, proceeding");
				return;
			}
			if (attempt < maxAllowlistAttempts) {
				String violationPaths = String.join(", ", violations);
				mojoLog.warn("  " + name + ": Allowlist violation (attempt " + attempt + "/" + maxAllowlistAttempts
					+ "), reverting " + violationPaths + " and resuming claude...");
				for (String path : violations) {
					git.run(targetDir, "checkout", "HEAD", "--", path);
				}
				mojoLog.info("  " + name + ": Running...");
				long resumeStart = System.currentTimeMillis();
				resumeClaudeWithRetry(workingDir, allowlistResumeMessage());
				long resumeDuration = System.currentTimeMillis() - resumeStart;
				mojoLog.info("  " + name + ": Completed (" + DarmokMojoState.formatDuration(resumeDuration) + ")");
			}
		}
		mojoLog.error("  " + name + ": Allowlist check failed after " + maxAllowlistAttempts + " attempts, aborting");
		throw new MojoExecutionException("rgr-" + name.toLowerCase() + " allowlist check failed after " + maxAllowlistAttempts + " attempts");
	}

	private List<String> findAllowlistViolations(String porcelain) {
		List<String> violations = new ArrayList<>();
		if (porcelain == null || porcelain.isEmpty()) {
			return violations;
		}
		for (String line : porcelain.split("\n")) {
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			int spaceIdx = trimmed.indexOf(' ');
			if (spaceIdx < 0) {
				continue;
			}
			String path = trimmed.substring(spaceIdx + 1).trim();
			if (!path.isEmpty() && !isAllowlisted(path)) {
				violations.add(path);
			}
		}
		return violations;
	}

}
