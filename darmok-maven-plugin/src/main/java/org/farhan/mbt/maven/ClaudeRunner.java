package org.farhan.mbt.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.logging.Log;

public class ClaudeRunner extends ProcessRunner {

	// Sentinel exit code returned when the claude subprocess is destroyed after
	// exceeding maxClaudeSeconds. Callers (GreenPhase/RefactorPhase) recognise
	// this and enter the timeout-recovery loop rather than treating it as a
	// normal failure. Matches the convention used by GNU coreutils timeout(1).
	public static final int TIMEOUT_EXIT_CODE = -124;

	// Retryable error patterns
	private static final String[] RETRYABLE_PATTERNS = {
		"API Error: 500",
		"API Error: 529",
		"Internal server error",
		"overloaded"
	};

	private int maxRetries;
	private int retryWaitSeconds;
	private int maxClaudeSeconds;
	private String model;

	public ClaudeRunner(Log log, String model, int maxRetries, int retryWaitSeconds, int maxClaudeSeconds) {
		this(log, model, maxRetries, retryWaitSeconds, maxClaudeSeconds, ProcessBuilder::start);
	}

	public ClaudeRunner(Log log, String model, int maxRetries, int retryWaitSeconds, int maxClaudeSeconds,
			ProcessStarter starter) {
		super(log, starter);
		this.model = model;
		this.maxRetries = maxRetries;
		this.retryWaitSeconds = retryWaitSeconds;
		this.maxClaudeSeconds = maxClaudeSeconds;
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

			List<String> outputLines = new ArrayList<>();
			exitCode = executeCommand(command, workingDirectory, outputLines);

			if (exitCode == TIMEOUT_EXIT_CODE) {
				// Timeout is orthogonal to the API-retry loop; surface the sentinel
				// so the phase can run mvn clean install and decide what to do.
				return TIMEOUT_EXIT_CODE;
			}

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

	public int resume(String workingDirectory, String message) throws Exception {
		List<String> command = new ArrayList<>();
		command.add(isWindows() ? "claude.cmd" : "claude");
		command.add("--resume");
		command.add("--print");
		command.add("--dangerously-skip-permissions");
		if (model != null && !model.isEmpty()) {
			command.add("--model");
			command.add(model);
		}
		command.add(message);

		List<String> outputLines = new ArrayList<>();
		int exitCode = executeCommand(command, workingDirectory, outputLines);
		if (exitCode == TIMEOUT_EXIT_CODE) {
			return TIMEOUT_EXIT_CODE;
		}
		getLog().debug("Claude CLI exited with code " + exitCode);
		return exitCode;
	}

	private int executeCommand(List<String> command, String workingDirectory,
			List<String> outputLines) throws Exception {
		Log log = getLog();
		log.debug("Executing: " + String.join(" ", command));
		log.debug("-------------------------------------------");

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(new File(workingDirectory));
		pb.redirectErrorStream(true);
		Process process = getStarter().start(pb);
		process.getOutputStream().close();

		// Read stdout in a background thread so waitFor(timeout) can actually
		// interrupt a hung claude — if we read in this thread, readLine() blocks
		// indefinitely when claude hangs without producing output.
		Thread readerThread = new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					log.debug(line);
					synchronized (outputLines) {
						outputLines.add(line);
					}
				}
			} catch (Exception e) {
				// Stream closed by destroyForcibly — expected on timeout.
			}
		});
		readerThread.setDaemon(true);
		readerThread.start();

		boolean completed = process.waitFor(maxClaudeSeconds, TimeUnit.SECONDS);
		if (!completed) {
			log.warn("Claude CLI timed out after " + maxClaudeSeconds + "s, killing...");
			process.destroyForcibly();
			process.waitFor(5, TimeUnit.SECONDS);
			readerThread.join(5000);
			log.debug("-------------------------------------------");
			return TIMEOUT_EXIT_CODE;
		}

		// Bound the reader half of the budget too. On Windows, claude.cmd can exit
		// cleanly (waitFor returned true) while a grandchild node keeps the stdout
		// pipe open silent — without this bound we'd sit in readerThread.join() past
		// maxClaudeSeconds. See issue 290.
		readerThread.join(maxClaudeSeconds * 1000L);
		if (readerThread.isAlive()) {
			log.warn("Claude CLI timed out after " + maxClaudeSeconds + "s, killing...");
			process.destroyForcibly();
			readerThread.join(5000);
			log.debug("-------------------------------------------");
			return TIMEOUT_EXIT_CODE;
		}
		log.debug("-------------------------------------------");
		return process.exitValue();
	}

	private String findRetryableError(List<String> outputLines) {
		synchronized (outputLines) {
			for (String line : outputLines) {
				for (String pattern : RETRYABLE_PATTERNS) {
					if (line.contains(pattern)) {
						return pattern;
					}
				}
			}
		}
		return null;
	}
}
