package org.farhan.mbt.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class GitRunner extends ProcessRunner {

	public GitRunner(Log log) {
		super(log);
	}

	public GitRunner(Log log, ProcessStarter starter) {
		super(log, starter);
	}

	@Override
	protected List<String> buildCommand(String... args) {
		List<String> command = new ArrayList<>();
		command.add("git");
		for (String arg : args) {
			command.add(arg);
		}
		return command;
	}

	public String getCurrentCommit(String workingDirectory) throws Exception {
		return capture(workingDirectory, "rev-parse", "HEAD");
	}

	public String getCurrentBranch(String workingDirectory) throws Exception {
		return capture(workingDirectory, "rev-parse", "--abbrev-ref", "HEAD");
	}

	String captureOutput(String workingDirectory, String... args) throws Exception {
		return capture(workingDirectory, args);
	}
}
