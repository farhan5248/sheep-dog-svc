package org.farhan.mbt.maven;

import org.apache.maven.plugin.logging.Log;

@FunctionalInterface
public interface MavenRunnerFactory {
	Maven create(Log log);
}
