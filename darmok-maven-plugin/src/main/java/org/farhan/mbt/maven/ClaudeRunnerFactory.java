package org.farhan.mbt.maven;

import java.util.function.Supplier;

import org.apache.maven.plugin.logging.Log;

@FunctionalInterface
public interface ClaudeRunnerFactory {
	Claude create(Log log, String model, int maxClaudeSeconds,
			boolean sessionIdEnabled, Supplier<String> uuidSupplier);
}
