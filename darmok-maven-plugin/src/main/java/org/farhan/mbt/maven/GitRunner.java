package org.farhan.mbt.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

public class GitRunner extends ProcessRunner implements Git {

	public GitRunner(Log log) {
		super(log);
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

	@Override
	public String getCurrentCommit(String workingDirectory) throws Exception {
		return capture(workingDirectory, "rev-parse", "HEAD");
	}

	@Override
	public String getCurrentBranch(String workingDirectory) throws Exception {
		return capture(workingDirectory, "rev-parse", "--abbrev-ref", "HEAD");
	}

	@Override
	public String captureOutput(String workingDirectory, String... args) throws Exception {
		return capture(workingDirectory, args);
	}
}
