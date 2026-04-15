package org.farhan.mbt.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class ProcessRunner {

	/**
	 * Test seam: abstraction over {@link ProcessBuilder#start()} so tests can
	 * substitute a fake {@link Process} without spawning real subprocesses.
	 * <p>
	 * This is intentionally a static field to keep Stage 1 tests minimally invasive
	 * on the production code. It moves to instance injection once enough tests exist
	 * to refactor safely (sheep-dog-main#253 Stage 2). Tests that reassign this
	 * field must run sequentially and restore the default in teardown.
	 */
	@FunctionalInterface
	public interface ProcessStarter {
		Process start(ProcessBuilder pb) throws IOException;
	}

	public static volatile ProcessStarter starter = ProcessBuilder::start;

	private Log log;

	public ProcessRunner(Log log) {
		this.log = log;
	}

	protected static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	protected List<String> buildCommand(String... args) {
		List<String> command = new ArrayList<>();
		for (String arg : args) {
			command.add(arg);
		}
		return command;
	}

	public int run(String workingDirectory, String... args) throws Exception {
		List<String> command = buildCommand(args);

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(new File(workingDirectory));
		pb.redirectErrorStream(true);

		log.debug("Running: " + String.join(" ", command));

		Process process = starter.start(pb);
		process.getOutputStream().close();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				log.debug(line);
			}
		}
		return process.waitFor();
	}

	protected Log getLog() {
		return log;
	}
}
