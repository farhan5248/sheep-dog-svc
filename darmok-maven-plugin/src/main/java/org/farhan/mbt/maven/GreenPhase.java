package org.farhan.mbt.maven;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class GreenPhase extends RgrPhase {

	public GreenPhase(Claude claude, Maven maven, Git git, DarmokMojoLog mojoLog, Log runnerLog,
			String workingDir, String targetDir, String artifactId,
			int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds,
			int maxAllowlistAttempts, int maxRetries, int retryWaitSeconds, List<String> allowlistPaths) {
		super(claude, maven, git, mojoLog, runnerLog, workingDir, targetDir, artifactId,
			maxVerifyAttempts, maxTimeoutAttempts, maxClaudeSeconds, maxAllowlistAttempts,
			maxRetries, retryWaitSeconds, allowlistPaths);
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
		String runnerClassName = state.tag + "Test";
		String logPath = targetDir + "/log.txt";
		String jacocoPath = targetDir + "/target/site/jacoco-with-tests";
		String umlDir = targetDir + "/site/uml";

		String template;
		try (InputStream is = getClass().getResourceAsStream("/prompts/green.md")) {
			template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
		String rendered = template
			.replace("${projectPath}", targetDir)
			.replace("${runnerClassName}", runnerClassName)
			.replace("${logPath}", logPath)
			.replace("${jacocoPath}", jacocoPath)
			.replace("${umlDir}", umlDir);
		Path renderedPath = Path.of(targetDir, "target", "darmok", "green.md");
		Files.createDirectories(renderedPath.getParent());
		Files.writeString(renderedPath, rendered, StandardCharsets.UTF_8);
		String args = "@" + renderedPath.toString().replace('\\', '/');

		int claudeExit = runClaudeWithRetry(workingDir, args);
		return runTimeoutRecoveryLoop(claudeExit);
	}
}
