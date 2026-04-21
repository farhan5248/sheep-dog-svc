package org.farhan.mbt.maven;

import org.apache.maven.plugin.logging.Log;

@FunctionalInterface
public interface ClaudeRunnerFactory {
	ClaudeRunner create(Log log, String model, int maxRetries, int retryWaitSeconds, int maxClaudeSeconds);
}
