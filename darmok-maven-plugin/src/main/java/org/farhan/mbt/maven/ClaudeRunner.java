package org.farhan.mbt.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.maven.plugin.logging.Log;

/**
 * Single-invocation primitive that runs the {@code claude} CLI once and
 * returns its exit code. Output lines are captured into the caller-supplied
 * list so callers can decide whether the failure is retryable. The retry
 * loop itself is owned by callers — see {@link DarmokMojo#runClaudeWithRetry}
 * (used directly by {@link GenFromComparisonMojo}) and {@link RgrPhase}'s
 * {@code runClaudeWithRetry} / {@code resumeClaudeWithRetry} wrappers (used
 * by the Green and Refactor phases).
 */
public class ClaudeRunner extends ProcessRunner implements Claude {

	// Sentinel exit code returned when the claude subprocess is destroyed after
	// exceeding maxClaudeSeconds. Callers (GreenPhase/RefactorPhase) recognise
	// this and enter the timeout-recovery loop rather than treating it as a
	// normal failure. Matches the convention used by GNU coreutils timeout(1).
	public static final int TIMEOUT_EXIT_CODE = -124;

	private final int maxClaudeSeconds;
	private final String model;
	private final boolean sessionIdEnabled;
	private final Supplier<String> uuidSupplier;
	private String sessionId;

	public ClaudeRunner(Log log, String model, int maxClaudeSeconds,
			boolean sessionIdEnabled, Supplier<String> uuidSupplier) {
		super(log);
		this.model = model;
		this.maxClaudeSeconds = maxClaudeSeconds;
		this.sessionIdEnabled = sessionIdEnabled;
		this.uuidSupplier = uuidSupplier;
	}

	@Override
	protected List<String> buildCommand(String... args) {
		List<String> command = new ArrayList<>();
		command.add(isWindows() ? "claude.cmd" : "claude");
		command.add("--print");
		if (sessionIdEnabled) {
			if (sessionId == null) {
				sessionId = uuidSupplier.get();
			}
			command.add("--session-id");
			command.add(sessionId);
		}
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
	public int run(String workingDirectory, List<String> outputLines, String... args) throws Exception {
		return executeCommand(buildCommand(args), workingDirectory, outputLines);
	}

	@Override
	public int resume(String workingDirectory, List<String> outputLines, String message) throws Exception {
		List<String> command = new ArrayList<>();
		command.add(isWindows() ? "claude.cmd" : "claude");
		command.add("--resume");
		if (sessionIdEnabled && sessionId != null) {
			command.add(sessionId);
		}
		command.add("--print");
		command.add("--dangerously-skip-permissions");
		if (model != null && !model.isEmpty()) {
			command.add("--model");
			command.add(model);
		}
		command.add(message);
		return executeCommand(command, workingDirectory, outputLines);
	}

	private int executeCommand(List<String> command, String workingDirectory,
			List<String> outputLines) throws Exception {
		Log log = getLog();
		log.debug("Executing: " + String.join(" ", command));
		log.debug("-------------------------------------------");

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(new File(workingDirectory));
		pb.redirectErrorStream(true);
		Process process = pb.start();
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

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
