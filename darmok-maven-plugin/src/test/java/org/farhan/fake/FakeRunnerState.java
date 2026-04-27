package org.farhan.fake;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Cross-runner state shared between {@link ClaudeProcessFake}, {@link MvnProcessFake},
 * and {@link GitProcessFake}. Owns the bits one per-runner fake mutates and another reads:
 * <ul>
 *   <li>{@code currentPhase} — set by {@link ClaudeProcessFake} when /rgr-green or
 *       /rgr-refactor fires; read by {@link MvnProcessFake} and {@link GitProcessFake}
 *       to dispatch phase-specific modes and counters.</li>
 *   <li>scenarios-list snapshot — captured at scenario start, refreshed on
 *       {@code git commit}, restored on {@code git checkout HEAD -- scenarios-list.txt},
 *       compared against current content on {@code git status --porcelain} so allowlist
 *       Test-Cases can fail correctly when production code modifies scenarios-list.txt
 *       before the gate runs (issue 322).</li>
 *   <li>event-log tap — every command lands in {@code mojo.event.log} for the
 *       Then-block log assertions.</li>
 * </ul>
 */
class FakeRunnerState {

	private static final DateTimeFormatter EVENT_LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private final Path codePrjBaseDir;
	private final Path eventLogPath;
	private final Path scenariosListPath;
	private String currentPhase;
	private String scenariosListSnapshot;

	FakeRunnerState(Path codePrjBaseDir, Path eventLogPath) {
		this.codePrjBaseDir = codePrjBaseDir;
		this.eventLogPath = eventLogPath;
		this.scenariosListPath = codePrjBaseDir == null ? null : codePrjBaseDir.resolve("scenarios-list.txt");
		this.scenariosListSnapshot = readScenariosList();
	}

	Path codePrjBaseDir() {
		return codePrjBaseDir;
	}

	String currentPhase() {
		return currentPhase;
	}

	void setCurrentPhase(String phase) {
		this.currentPhase = phase;
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

	boolean scenariosListIsModified() {
		if (scenariosListPath == null) return false;
		return !Objects.equals(readScenariosList(), scenariosListSnapshot);
	}

	void refreshScenariosListSnapshot() {
		this.scenariosListSnapshot = readScenariosList();
	}

	void restoreScenariosListFromSnapshot() {
		if (scenariosListPath == null) return;
		try {
			if (scenariosListSnapshot != null) {
				Files.createDirectories(scenariosListPath.getParent());
				Files.writeString(scenariosListPath, scenariosListSnapshot, StandardCharsets.UTF_8);
			} else {
				Files.deleteIfExists(scenariosListPath);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String readScenariosList() {
		if (scenariosListPath == null || !Files.exists(scenariosListPath)) return null;
		try {
			return Files.readString(scenariosListPath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
