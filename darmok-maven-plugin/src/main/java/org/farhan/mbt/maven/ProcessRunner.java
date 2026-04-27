package org.farhan.mbt.maven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * Base for production runners that drive an external command line. Tests do not
 * extend this — the {@link Git} / {@link Maven} / {@link Claude} interfaces are
 * the swap points, and test fakes implement those interfaces directly.
 */
public abstract class ProcessRunner {

	private final Log log;

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
		return run(workingDirectory, null, args);
	}

	public int run(String workingDirectory, Path logFile, String... args) throws Exception {
		List<String> command = buildCommand(args);

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(new File(workingDirectory));
		pb.redirectErrorStream(true);

		log.debug("Running: " + String.join(" ", command));

		Process process = pb.start();
		process.getOutputStream().close();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
			 BufferedWriter writer = logFile != null
				? Files.newBufferedWriter(logFile)
				: null) {
			String line;
			while ((line = reader.readLine()) != null) {
				log.debug(line);
				if (writer != null) {
					writer.write(line);
					writer.newLine();
				}
			}
		}
		return process.waitFor();
	}

	protected String capture(String workingDirectory, String... args) throws Exception {
		List<String> command = buildCommand(args);

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(new File(workingDirectory));
		pb.redirectErrorStream(true);

		log.debug("Running: " + String.join(" ", command));

		Process process = pb.start();
		process.getOutputStream().close();
		StringBuilder output = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}
		}
		int exit = process.waitFor();
		if (exit != 0) {
			throw new IOException("Command failed (exit " + exit + "): " + String.join(" ", command));
		}
		return output.toString().trim();
	}

	protected Log getLog() {
		return log;
	}
}
