package org.farhan.mbt.maven;

import java.util.function.Supplier;

import org.apache.maven.plugin.logging.Log;

@FunctionalInterface
public interface ClaudeRunnerFactory {
	ClaudeRunner create(Log log, String model, int maxRetries, int retryWaitSeconds, int maxClaudeSeconds,
			boolean sessionIdEnabled, Supplier<String> uuidSupplier);
}
