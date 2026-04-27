package org.farhan.mbt.maven;

import java.nio.file.Path;

/**
 * Maven command-line abstraction. Production: {@link MavenRunner}. Test:
 * {@code MavenRunnerFake}.
 */
public interface Maven {

	int run(String workingDirectory, String... args) throws Exception;

	int run(String workingDirectory, Path logFile, String... args) throws Exception;
}
