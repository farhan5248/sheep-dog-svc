package org.farhan.fake;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.farhan.common.CommandFake;
import org.farhan.mbt.maven.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replay-driven fake for the production {@code GitRunner}. Walks the
 * {@code captures/git.yaml} path declared for the active scenario; each
 * {@link Git} method consumes one vertex.
 */
public class GitRunnerFake extends CommandFake implements Git {

	private static final Logger logger = LoggerFactory.getLogger(GitRunnerFake.class);

	public GitRunnerFake(Map<String, Object> properties) {
		super(properties);
	}

	public static GitRunnerFake from(HashMap<String, Object> properties) {
		return new GitRunnerFake(properties);
	}

	@Override
	protected String commandKey() {
		return "git";
	}

	@Override
	public int run(String workingDirectory, String... args) {
		logger.debug("run cwd={} args={}", workingDirectory, java.util.Arrays.asList(args));
		Vertex v = consume();
		logRun(v);
		logger.debug("run exit={} stdoutLen={}", v.exit(), v.stdout() == null ? 0 : v.stdout().length());
		return v.exit();
	}

	@Override
	public int run(String workingDirectory, Path logFile, String... args) {
		logger.debug("run cwd={} logFile={} args={}", workingDirectory, logFile, java.util.Arrays.asList(args));
		Vertex v = consume();
		logRun(v);
		writeStdoutToLogFile(logFile, v.stdout());
		logger.debug("run exit={} stdoutLen={}", v.exit(), v.stdout() == null ? 0 : v.stdout().length());
		return v.exit();
	}

	@Override
	public String getCurrentCommit(String workingDirectory) {
		return captureOutput(workingDirectory, "rev-parse", "HEAD");
	}

	@Override
	public String getCurrentBranch(String workingDirectory) {
		return captureOutput(workingDirectory, "rev-parse", "--abbrev-ref", "HEAD");
	}

	@Override
	public String captureOutput(String workingDirectory, String... args) {
		logger.debug("captureOutput cwd={} args={}", workingDirectory, java.util.Arrays.asList(args));
		Vertex v = consume();
		logCapture(v);
		String output = v.stdout().trim();
		logger.debug("captureOutput outputLen={}", output.length());
		return output;
	}

	private static void writeStdoutToLogFile(Path logFile, String stdout) {
		if (logFile == null) return;
		try {
			Files.createDirectories(logFile.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8)) {
				if (!stdout.isEmpty()) {
					for (String line : stdout.split("\\r?\\n")) {
						writer.write(line);
						writer.newLine();
					}
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
