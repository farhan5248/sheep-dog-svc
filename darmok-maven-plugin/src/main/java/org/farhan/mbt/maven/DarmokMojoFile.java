package org.farhan.mbt.maven;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Read-side base for files that Darmok writes during a run and tests inspect
 * row-by-row. Subclasses define the row record type and how to parse + match
 * one row from the file. Identity-cached keyMap so consecutive
 * matchAndGet{Field}(km) calls against the same map return fields from the
 * same row.
 */
public abstract class DarmokMojoFile<R> {

	protected final Path file;

	private List<R> entries;
	private int cursor = 0;
	private HashMap<String, String> lastKeyMap;
	protected R lastMatch;

	protected DarmokMojoFile(Path file) {
		this.file = file;
	}

	public Path getFile() {
		return file;
	}

	protected abstract List<R> parse(Path file);

	protected abstract R findNext(HashMap<String, String> keyMap);

	protected int cursor() {
		return cursor;
	}

	protected void advanceCursor() {
		cursor++;
	}

	protected List<R> entries() {
		return entries;
	}

	protected final void ensureMatched(HashMap<String, String> keyMap) {
		if (entries == null) {
			entries = parse(file);
		}
		if (keyMap == lastKeyMap) {
			return;
		}
		lastKeyMap = keyMap;
		lastMatch = findNext(keyMap);
	}
}
