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
	 */
	@FunctionalInterface
	public interface ProcessStarter {
		Process start(ProcessBuilder pb) throws IOException;
	}

	private final Log log;
	private final ProcessStarter starter;

	public ProcessRunner(Log log) {
		this(log, ProcessBuilder::start);
	}

	public ProcessRunner(Log log, ProcessStarter starter) {
		this.log = log;
		this.starter = starter;
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

	protected ProcessStarter getStarter() {
		return starter;
	}
}
