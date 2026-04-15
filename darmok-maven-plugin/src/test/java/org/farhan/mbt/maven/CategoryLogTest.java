package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Characterization tests for {@link CategoryLog}.
 * <p>
 * <b>Intent:</b> lock in the file-format and lifecycle contract of the category log.
 * Every log-based assertion elsewhere in this project (ClaudeRunner, GitRunner, future
 * mojo orchestration tests, the PBC report skill) depends on CategoryLog producing
 * stable timestamped lines in the expected format. These tests assert that contract
 * directly so a regression here surfaces here rather than as confusing failures in
 * downstream tests.
 */
class CategoryLogTest {

	@TempDir
	Path workDir;

	private static final Pattern LINE_FORMAT = Pattern.compile(
		"^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} (DEBUG|INFO |WARN |ERROR) \\[\\w+\\] .*");

	/**
	 * Given: a log-file path whose parent directory does not yet exist.
	 * When: a CategoryLog is constructed targeting that path.
	 * Then: the parent directory is created and the file is ready to receive writes.
	 * <p>
	 * This characterizes the ensure-directory contract — callers (DarmokMojo.initLogs)
	 * rely on this rather than creating directories themselves.
	 */
	@Test
	void constructor_createsMissingParentDirectories() throws Exception {
		// Given: a log-file path whose parent directories do not exist
		Path nested = workDir.resolve("a/b/c/category.log");
		assertThat(Files.exists(nested.getParent())).isFalse();

		// When: a CategoryLog is created and one INFO message is written
		try (CategoryLog log = new CategoryLog(new SystemStreamLog(), "test", nested)) {
			log.info("hello");
		}

		// Then: the log file exists and contains the expected line
		assertThat(Files.exists(nested)).isTrue();
		assertThat(Files.readString(nested)).contains("INFO  [test] hello");
	}

	/**
	 * Given: a CategoryLog writing at each log level.
	 * When: debug / info / warn / error are invoked with content.
	 * Then: every emitted line matches the canonical
	 *   "yyyy-MM-dd HH:mm:ss.SSS LEVEL [category] content" format.
	 * <p>
	 * This is the contract that downstream regex-based log assertions depend on.
	 * If the format changes, every characterization test that matches on log lines
	 * needs to be updated in lock-step.
	 */
	@Test
	void writesCanonicalTimestampLevelCategoryFormat() throws Exception {
		// Given: a CategoryLog with category "Claude"
		Path file = workDir.resolve("canonical.log");

		// When: debug / info / warn / error messages are written in that order
		try (CategoryLog log = new CategoryLog(new SystemStreamLog(), "Claude", file)) {
			log.debug("debug line");
			log.info("info line");
			log.warn("warn line");
			log.error("error line");
		}

		// Then: the log file contains four canonical lines, one per level, in order
		List<String> lines = Files.readAllLines(file);
		assertThat(lines).hasSize(4);
		assertThat(lines).allMatch(l -> LINE_FORMAT.matcher(l).matches(),
			"every line must match <ts> <level> [<category>] <content>");
		assertThat(lines.get(0)).contains("DEBUG [Claude] debug line");
		assertThat(lines.get(1)).contains("INFO  [Claude] info line");
		assertThat(lines.get(2)).contains("WARN  [Claude] warn line");
		assertThat(lines.get(3)).contains("ERROR [Claude] error line");
	}

	/**
	 * Given: a CategoryLog with a Throwable passed alongside log content.
	 * When: warn(content, throwable) is invoked.
	 * Then: the file contains the content line followed by the throwable's stack trace.
	 * <p>
	 * This characterizes the failure-diagnosis contract — errors with causes must
	 * expose the cause in the log for post-hoc debugging. If omitted, production
	 * incident triage would require live reproduction.
	 */
	@Test
	void withThrowable_appendsStackTraceToLogFile() throws Exception {
		// Given: an IOException with message "boom from test"
		Path file = workDir.resolve("err.log");
		IOException boom = new IOException("boom from test");

		// When: the exception is passed to warn(content, throwable) on a CategoryLog
		try (CategoryLog log = new CategoryLog(new SystemStreamLog(), "Claude", file)) {
			log.warn("something went wrong", boom);
		}

		// Then: the log file contains the warn-level content line and the exception stack trace
		String content = Files.readString(file);
		assertThat(content)
			.contains("WARN  [Claude] something went wrong")
			.contains("java.io.IOException: boom from test")
			.contains("at org.farhan.mbt.maven.CategoryLogTest");
	}

	/**
	 * Given: two CategoryLog instances opened on the same file path at different times.
	 * When: each writes a distinct line and is closed.
	 * Then: the final file contains both lines in the order they were written.
	 * <p>
	 * This characterizes the append-mode contract — re-running Darmok across multiple
	 * invocations on the same day accumulates into the same dated log file rather than
	 * truncating the previous run's history.
	 */
	@Test
	void appendMode_preservesPreviousSessionContent() throws Exception {
		// Given: an existing log file with one line from a prior CategoryLog session
		Path file = workDir.resolve("append.log");
		try (CategoryLog first = new CategoryLog(new SystemStreamLog(), "run", file)) {
			first.info("session one line");
		}

		// When: a new CategoryLog is opened on the same file and writes a second line
		try (CategoryLog second = new CategoryLog(new SystemStreamLog(), "run", file)) {
			second.info("session two line");
		}

		// Then: the log file contains both lines in the order they were written
		List<String> lines = Files.readAllLines(file);
		assertThat(lines).hasSize(2);
		assertThat(lines.get(0)).contains("session one line");
		assertThat(lines.get(1)).contains("session two line");
	}
}
