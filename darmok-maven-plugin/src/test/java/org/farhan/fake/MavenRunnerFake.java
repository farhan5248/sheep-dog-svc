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
import org.farhan.mbt.maven.Maven;

/**
 * Replay-driven fake for the production {@code MavenRunner}. Walks the
 * {@code captures/mvn.yaml} path declared for the active scenario.
 */
public class MavenRunnerFake extends CommandFake implements Maven {

	public MavenRunnerFake(Map<String, Object> properties) {
		super(properties);
	}

	public static MavenRunnerFake from(HashMap<String, Object> properties) {
		return new MavenRunnerFake(properties);
	}

	@Override
	protected String commandKey() {
		return "mvn";
	}

	@Override
	public int run(String workingDirectory, String... args) {
		simulateSubprocessTime();
		Vertex v = consume();
		logRun(v);
		return v.exit();
	}

	@Override
	public int run(String workingDirectory, Path logFile, String... args) {
		simulateSubprocessTime();
		Vertex v = consume();
		logRun(v);
		writeStdoutToLogFile(logFile, v.stdout());
		return v.exit();
	}

	/**
	 * Real maven invocations take seconds; the phase metric needs a non-zero
	 * duration so tests can distinguish "ran" from "didn't run". 1ms is enough.
	 */
	private static void simulateSubprocessTime() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
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
