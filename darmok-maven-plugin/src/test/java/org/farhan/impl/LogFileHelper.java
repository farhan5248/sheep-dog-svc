package org.farhan.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.farhan.common.TestObject;

/**
 * Parses CategoryLog output and matches expected rows against actual log lines
 * in order. Shared between {@link DarmokMojoLogFileImpl} and
 * {@link DarmokRunnersLogFileImpl}.
 *
 * <p>
 * Log format: {@code yyyy-MM-dd HH:mm:ss.SSS LEVEL [category] content}
 * <p>
 * Matching is sequential: each expected row finds the next actual line (at or
 * after the cursor) whose non-{@code Any} fields match. Gaps between matches
 * are allowed — intermediate lines are skipped.
 */
final class LogFileHelper {

	private static final Pattern LINE_PATTERN = Pattern.compile(
		"^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} (DEBUG|INFO |WARN |ERROR) \\[(\\w+)\\] (.*)$");

	record LogEntry(String level, String category, String content) {}

	private final List<LogEntry> entries;
	private int cursor = 0;
	private HashMap<String, String> lastKeyMap;
	private LogEntry lastMatch;

	LogFileHelper(Path logFile) {
		this.entries = parse(logFile);
	}

	String matchAndGetLevel(HashMap<String, String> keyMap) {
		ensureMatched(keyMap);
		return lastMatch != null ? lastMatch.level() : null;
	}

	String matchAndGetCategory(HashMap<String, String> keyMap) {
		ensureMatched(keyMap);
		return lastMatch != null ? lastMatch.category() : null;
	}

	String matchAndGetContent(HashMap<String, String> keyMap) {
		ensureMatched(keyMap);
		return lastMatch != null ? lastMatch.content() : null;
	}

	private void ensureMatched(HashMap<String, String> keyMap) {
		if (keyMap == lastKeyMap) {
			return;
		}
		lastKeyMap = keyMap;
		lastMatch = findNext(keyMap);
	}

	private LogEntry findNext(HashMap<String, String> keyMap) {
		String expectedLevel = keyMap.get("Level");
		String expectedCategory = keyMap.get("Category");
		String expectedContent = keyMap.get("Content");

		while (cursor < entries.size()) {
			LogEntry entry = entries.get(cursor);
			cursor++;
			if (matches(expectedLevel, entry.level())
				&& matches(expectedCategory, entry.category())
				&& matches(expectedContent, entry.content())) {
				return entry;
			}
		}
		return null;
	}

	private static boolean matches(String expected, String actual) {
		if (expected == null || TestObject.TestState.Any.name().equals(expected)) {
			return true;
		}
		return expected.trim().equals(actual.trim());
	}

	private static List<LogEntry> parse(Path logFile) {
		List<LogEntry> result = new ArrayList<>();
		if (logFile == null || !Files.exists(logFile)) {
			return result;
		}
		try {
			for (String line : Files.readAllLines(logFile)) {
				Matcher m = LINE_PATTERN.matcher(line);
				if (m.matches()) {
					result.add(new LogEntry(
						m.group(1).trim(),
						m.group(2).trim(),
						m.group(3).trim()));
				}
			}
		} catch (IOException e) {
			// return what we have
		}
		return result;
	}
}
