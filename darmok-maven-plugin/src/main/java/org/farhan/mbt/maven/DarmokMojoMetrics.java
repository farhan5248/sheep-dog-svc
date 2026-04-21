package org.farhan.mbt.maven;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DarmokMojoMetrics {

	private static final String HEADER =
		"timestamp,git_branch,commit,scenario,phase_red_ms,phase_green_ms,phase_refactor_ms,phase_total_ms";

	private static final DateTimeFormatter TIMESTAMP =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private final Path file;

	// Read-side state
	private List<Map<String, String>> rows;
	private int cursor = 0;
	private HashMap<String, String> lastKeyMap;
	private Map<String, String> lastRow;

	public DarmokMojoMetrics(Path file) {
		this.file = file;
	}

	public Path getFile() {
		return file;
	}

	public void append(String gitBranch, String commit, String scenario,
			long redMs, long greenMs, long refactorMs, long totalMs) throws IOException {
		Files.createDirectories(file.getParent());
		if (!Files.exists(file)) {
			Files.writeString(file, HEADER + "\n", StandardCharsets.UTF_8);
		}
		String row = LocalDateTime.now().format(TIMESTAMP) + ","
			+ gitBranch + ","
			+ commit + ","
			+ escape(scenario) + ","
			+ redMs + "," + greenMs + "," + refactorMs + "," + totalMs + "\n";
		Files.writeString(file, row, StandardCharsets.UTF_8,
			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	private static String escape(String s) {
		if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
			return "\"" + s.replace("\"", "\"\"") + "\"";
		}
		return s;
	}

	// =========================================================================
	// Read-side: CSV parsing and sequential row matching
	// =========================================================================

	public String matchAndGetTimestamp(HashMap<String, String> keyMap) {
		return column(keyMap, "timestamp");
	}

	public String matchAndGetGitBranch(HashMap<String, String> keyMap) {
		return column(keyMap, "git_branch");
	}

	public String matchAndGetCommit(HashMap<String, String> keyMap) {
		return column(keyMap, "commit");
	}

	public String matchAndGetScenario(HashMap<String, String> keyMap) {
		return column(keyMap, "scenario");
	}

	public String matchAndGetPhaseRedMs(HashMap<String, String> keyMap) {
		return column(keyMap, "phase_red_ms");
	}

	public String matchAndGetPhaseGreenMs(HashMap<String, String> keyMap) {
		return column(keyMap, "phase_green_ms");
	}

	public String matchAndGetPhaseRefactorMs(HashMap<String, String> keyMap) {
		return column(keyMap, "phase_refactor_ms");
	}

	public String matchAndGetPhaseTotalMs(HashMap<String, String> keyMap) {
		return column(keyMap, "phase_total_ms");
	}

	private String column(HashMap<String, String> keyMap, String columnName) {
		ensureMatched(keyMap);
		return lastRow == null ? null : lastRow.get(columnName);
	}

	private void ensureMatched(HashMap<String, String> keyMap) {
		if (rows == null) {
			rows = parse(file);
		}
		if (keyMap == lastKeyMap) {
			return;
		}
		lastKeyMap = keyMap;
		if (cursor < rows.size()) {
			lastRow = rows.get(cursor);
			cursor++;
		} else {
			lastRow = null;
		}
	}

	private static List<Map<String, String>> parse(Path file) {
		List<Map<String, String>> result = new ArrayList<>();
		if (file == null || !Files.exists(file)) {
			return result;
		}
		try {
			List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
			if (lines.isEmpty()) {
				return result;
			}
			String[] headers = lines.get(0).split(",");
			for (int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				if (line.isEmpty()) continue;
				List<String> values = splitCsv(line);
				Map<String, String> row = new LinkedHashMap<>();
				for (int j = 0; j < headers.length; j++) {
					row.put(headers[j].trim(), j < values.size() ? values.get(j) : "");
				}
				result.add(row);
			}
		} catch (IOException e) {
			// return what we have
		}
		return result;
	}

	private static List<String> splitCsv(String line) {
		List<String> values = new ArrayList<>();
		StringBuilder cur = new StringBuilder();
		boolean inQuotes = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (inQuotes) {
				if (c == '"') {
					if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
						cur.append('"');
						i++;
					} else {
						inQuotes = false;
					}
				} else {
					cur.append(c);
				}
			} else {
				if (c == ',') {
					values.add(cur.toString());
					cur.setLength(0);
				} else if (c == '"' && cur.length() == 0) {
					inQuotes = true;
				} else {
					cur.append(c);
				}
			}
		}
		values.add(cur.toString());
		return values;
	}
}
