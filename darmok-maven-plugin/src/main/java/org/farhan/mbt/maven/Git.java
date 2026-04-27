package org.farhan.mbt.maven;

import java.nio.file.Path;

/**
 * Git command-line abstraction. Production: {@link GitRunner} (spawns the
 * {@code git} subprocess). Test: {@code GitRunnerFake} (replays a captured
 * FSM path from {@code captures/git.yaml}). The Mojo holds whichever
 * implementation is wired in by the active context.
 */
public interface Git {

	int run(String workingDirectory, String... args) throws Exception;

	int run(String workingDirectory, Path logFile, String... args) throws Exception;

	String getCurrentCommit(String workingDirectory) throws Exception;

	String getCurrentBranch(String workingDirectory) throws Exception;

	String captureOutput(String workingDirectory, String... args) throws Exception;
}
