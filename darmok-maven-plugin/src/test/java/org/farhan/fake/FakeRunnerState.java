package org.farhan.fake;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Per-scenario fake-side state. Owns the {@code mojo.event.log} tap that
 * {@link DarmokRunnersLogFileImpl} cross-checks against the production runner
 * log when asserting on {@code Running:} lines.
 */
class FakeRunnerState {

	private static final DateTimeFormatter EVENT_LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private final Path codePrjBaseDir;
	private final Path eventLogPath;
	private final String scenarioName;

	FakeRunnerState(Path codePrjBaseDir, Path eventLogPath, String scenarioName) {
		this.codePrjBaseDir = codePrjBaseDir;
		this.eventLogPath = eventLogPath;
		this.scenarioName = scenarioName;
		appendEventLog("[fake] Scenario: " + (scenarioName == null ? "<unset>" : scenarioName));
	}

	Path codePrjBaseDir() {
		return codePrjBaseDir;
	}

	String scenarioName() {
		return scenarioName;
	}

	void appendEventLog(String joinedCmd) {
		if (eventLogPath == null) return;
		String line = LocalDateTime.now().format(EVENT_LOG_TS)
			+ " DEBUG [runner] Running: " + joinedCmd + System.lineSeparator();
		try {
			Files.createDirectories(eventLogPath.getParent());
			Files.writeString(eventLogPath, line, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
