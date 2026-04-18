package org.farhan.mbt.maven;

import org.apache.maven.plugin.logging.Log;

@FunctionalInterface
public interface GitRunnerFactory {
	GitRunner create(Log log);
}
