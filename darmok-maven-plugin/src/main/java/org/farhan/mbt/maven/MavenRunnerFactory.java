package org.farhan.mbt.maven;

import org.apache.maven.plugin.logging.Log;

@FunctionalInterface
public interface MavenRunnerFactory {
	MavenRunner create(Log log);
}
