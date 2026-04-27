package org.farhan.mbt.maven;

import java.util.List;

/**
 * Claude command-line abstraction. Single-invocation primitive — callers wrap
 * it with {@link DarmokMojo#runClaudeWithRetry} (or one of {@link RgrPhase}'s
 * instance wrappers) when they need retry/timeout semantics.
 * <p>
 * Production: {@link ClaudeRunner}. Test: {@code ClaudeRunnerFake}.
 */
public interface Claude {

	int run(String workingDirectory, List<String> outputLines, String... args) throws Exception;

	int resume(String workingDirectory, List<String> outputLines, String message) throws Exception;

	String getSessionId();

	void setSessionId(String sessionId);
}
