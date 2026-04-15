package org.farhan.mbt.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class ClaudeRunner extends ProcessRunner {

	// Retryable error patterns
	private static final String[] RETRYABLE_PATTERNS = {
		"API Error: 500",
		"API Error: 529",
		"Internal server error",
		"overloaded"
	};

	private int maxRetries;
	private int retryWaitSeconds;
	private String model;

	public ClaudeRunner(Log log, String model, int maxRetries, int retryWaitSeconds) {
		super(log);
		this.model = model;
		this.maxRetries = maxRetries;
		this.retryWaitSeconds = retryWaitSeconds;
	}

	@Override
	protected List<String> buildCommand(String... args) {
		List<String> command = new ArrayList<>();
		command.add(isWindows() ? "claude.cmd" : "claude");
		command.add("--print");
		command.add("--dangerously-skip-permissions");
		if (model != null && !model.isEmpty()) {
			command.add("--model");
			command.add(model);
		}
		for (String arg : args) {
			command.add(arg);
		}
		return command;
	}

	@Override
	public int run(String workingDirectory, String... args) throws Exception {
		List<String> command = buildCommand(args);
		Log log = getLog();
		int attempt = 0;
		int exitCode = -1;

		while (attempt < maxRetries) {
			attempt++;

			if (attempt > 1) {
				log.debug("Retry attempt " + attempt + " of " + maxRetries + "...");
			}

			log.debug("Executing: " + String.join(" ", command));
			log.debug("-------------------------------------------");

			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File(workingDirectory));
			pb.redirectErrorStream(true);
			Process process = starter.start(pb);
			process.getOutputStream().close();

			List<String> outputLines = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					log.debug(line);
					outputLines.add(line);
				}
			}
			exitCode = process.waitFor();

			log.debug("-------------------------------------------");

			if (exitCode == 0) {
				log.debug("Claude CLI completed successfully");
				break;
			}

			String matchedPattern = findRetryableError(outputLines);
			if (matchedPattern != null && attempt < maxRetries) {
				log.warn("Retryable error detected: " + matchedPattern);
				log.warn("Claude CLI exited with code " + exitCode);
				log.warn("Waiting " + retryWaitSeconds + " seconds before retry...");
				Thread.sleep(retryWaitSeconds * 1000L);
			} else {
				if (matchedPattern != null) {
					log.error("Retryable error detected: " + matchedPattern);
					log.error("Max retries (" + maxRetries + ") exhausted");
				}
				log.debug("Claude CLI exited with code " + exitCode);
				break;
			}
		}
		return exitCode;
	}

	private String findRetryableError(List<String> outputLines) {
		for (String line : outputLines) {
			for (String pattern : RETRYABLE_PATTERNS) {
				if (line.contains(pattern)) {
					return pattern;
				}
			}
		}
		return null;
	}
}
