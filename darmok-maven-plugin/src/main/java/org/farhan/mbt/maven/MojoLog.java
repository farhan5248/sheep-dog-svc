package org.farhan.mbt.maven;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;
public class MojoLog implements Log, Closeable {

	private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private static final Pattern LINE_PATTERN = Pattern.compile(
		"^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} (DEBUG|INFO |WARN |ERROR) \\[(\\w+)\\] (.*)$");

	record LogEntry(String level, String category, String content) {}

	private final Log delegate;
	private final PrintWriter writer;
	private final String category;
	private final Path logFile;

	// Read-side state
	private List<LogEntry> entries;
	private int cursor = 0;
	private HashMap<String, String> lastKeyMap;
	private LogEntry lastMatch;

	public MojoLog(Log delegate, String category, Path logFile) throws IOException {
		this.delegate = delegate;
		this.category = category;
		this.logFile = logFile;
		Files.createDirectories(logFile.getParent());
		this.writer = new PrintWriter(new FileWriter(logFile.toFile(), true), true);
	}

	/** Read-only constructor for log inspection (no writing). */
	public MojoLog(Path logFile) {
		this.delegate = null;
		this.category = null;
		this.logFile = logFile;
		this.writer = null;
	}

	public Path getLogFile() {
		return logFile;
	}

	public static Path findDatedLog(Path logDir, String prefix) {
		if (!Files.isDirectory(logDir)) {
			return logDir.resolve(prefix + ".log");
		}
		try (var stream = Files.list(logDir)) {
			return stream
				.filter(p -> {
					String name = p.getFileName().toString();
					return name.startsWith(prefix + ".") && name.endsWith(".log");
				})
				.findFirst()
				.orElse(logDir.resolve(prefix + ".log"));
		} catch (IOException e) {
			return logDir.resolve(prefix + ".log");
		}
	}

	private void writeLine(String level, CharSequence content) {
		writer.println(LocalDateTime.now().format(TIMESTAMP) + " " + level + " [" + category + "] " + content);
	}

	private void writeLine(String level, CharSequence content, Throwable error) {
		writeLine(level, content);
		error.printStackTrace(writer);
	}

	@Override
	public boolean isDebugEnabled() {
		return delegate.isDebugEnabled();
	}

	@Override
	public void debug(CharSequence content) {
		delegate.debug(content);
		writeLine("DEBUG", content);
	}

	@Override
	public void debug(CharSequence content, Throwable error) {
		delegate.debug(content, error);
		writeLine("DEBUG", content, error);
	}

	@Override
	public void debug(Throwable error) {
		delegate.debug(error);
		writeLine("DEBUG", "", error);
	}

	@Override
	public boolean isInfoEnabled() {
		return delegate.isInfoEnabled();
	}

	@Override
	public void info(CharSequence content) {
		delegate.info(content);
		writeLine("INFO ", content);
	}

	@Override
	public void info(CharSequence content, Throwable error) {
		delegate.info(content, error);
		writeLine("INFO ", content, error);
	}

	@Override
	public void info(Throwable error) {
		delegate.info(error);
		writeLine("INFO ", "", error);
	}

	@Override
	public boolean isWarnEnabled() {
		return delegate.isWarnEnabled();
	}

	@Override
	public void warn(CharSequence content) {
		delegate.warn(content);
		writeLine("WARN ", content);
	}

	@Override
	public void warn(CharSequence content, Throwable error) {
		delegate.warn(content, error);
		writeLine("WARN ", content, error);
	}

	@Override
	public void warn(Throwable error) {
		delegate.warn(error);
		writeLine("WARN ", "", error);
	}

	@Override
	public boolean isErrorEnabled() {
		return delegate.isErrorEnabled();
	}

	@Override
	public void error(CharSequence content) {
		delegate.error(content);
		writeLine("ERROR", content);
	}

	@Override
	public void error(CharSequence content, Throwable error) {
		delegate.error(content, error);
		writeLine("ERROR", content, error);
	}

	@Override
	public void error(Throwable error) {
		delegate.error(error);
		writeLine("ERROR", "", error);
	}

	@Override
	public void close() {
		writer.close();
	}

	// =========================================================================
	// Read-side: log parsing and sequential matching
	// =========================================================================

	public String matchAndGetLevel(HashMap<String, String> keyMap) {
		ensureMatched(keyMap);
		return lastMatch != null ? lastMatch.level() : null;
	}

	public String matchAndGetCategory(HashMap<String, String> keyMap) {
		ensureMatched(keyMap);
		return lastMatch != null ? lastMatch.category() : null;
	}

	public String matchAndGetContent(HashMap<String, String> keyMap) {
		ensureMatched(keyMap);
		return lastMatch != null ? normalizeCommandExtensions(lastMatch.content()) : null;
	}

	private void ensureMatched(HashMap<String, String> keyMap) {
		if (entries == null) {
			entries = parse(logFile);
		}
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
		if (expected == null || "Any".equals(expected)) {
			return true;
		}
		return expected.trim().equals(normalizeCommandExtensions(actual.trim()));
	}

	private static String normalizeCommandExtensions(String s) {
		return s.replace("mvn.cmd", "mvn")
			.replace("claude.cmd", "claude");
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
