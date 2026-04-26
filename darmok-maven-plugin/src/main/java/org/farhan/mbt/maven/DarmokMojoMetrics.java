package org.farhan.mbt.maven;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DarmokMojoMetrics extends DarmokMojoFile<Map<String, String>> {

	private static final String HEADER =
		"timestamp,git_branch,commit,scenario,phase_red_ms,phase_green_ms,phase_refactor_ms,phase_total_ms";

	private static final DateTimeFormatter TIMESTAMP =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	public DarmokMojoMetrics(Path file) {
		super(file);
	}

	public void append(DarmokMojoState state) throws IOException {
		Files.createDirectories(file.getParent());
		if (!Files.exists(file)) {
			Files.writeString(file, HEADER + "\n", StandardCharsets.UTF_8);
		}
		String row = OffsetDateTime.now().format(TIMESTAMP) + ","
			+ state.gitBranch + ","
			+ state.commit + ","
			+ escape(state.scenarioName) + ","
			+ state.getDuration(Phase.RED) + ","
			+ state.getDuration(Phase.GREEN) + ","
			+ state.getDuration(Phase.REFACTOR) + ","
			+ state.totalDurationMs + "\n";
		Files.writeString(file, row, StandardCharsets.UTF_8,
			StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	private static String escape(String s) {
		if (s.contains(",") || s.contains("\"")) {
			return "\"" + s.replace("\"", "\"\"") + "\"";
		}
		return s;
	}

	// =========================================================================
	// Read-side: assumes the file was produced by append() — header line plus
	// N contiguous full-column rows, scenario field optionally quoted.
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
		return lastMatch.get(columnName);
	}

	@Override
	protected Map<String, String> findNext(HashMap<String, String> keyMap) {
		Map<String, String> row = entries().get(cursor());
		advanceCursor();
		return row;
	}

	@Override
	protected List<Map<String, String>> parse(Path file) {
		List<Map<String, String>> result = new ArrayList<>();
		try {
			List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
			String[] headers = lines.get(0).split(",");
			for (int i = 1; i < lines.size(); i++) {
				List<String> values = splitCsv(lines.get(i));
				Map<String, String> row = new LinkedHashMap<>();
				for (int j = 0; j < headers.length; j++) {
					row.put(headers[j].trim(), values.get(j));
				}
				result.add(row);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
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
					if (line.charAt(i + 1) == '"') {
						cur.append('"');
						i++;
					} else {
						inQuotes = false;
					}
				} else {
					cur.append(c);
				}
			} else if (c == ',') {
				values.add(cur.toString());
				cur.setLength(0);
			} else if (c == '"') {
				inQuotes = true;
			} else {
				cur.append(c);
			}
		}
		values.add(cur.toString());
		return values;
	}
}
