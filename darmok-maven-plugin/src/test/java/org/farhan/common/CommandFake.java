package org.farhan.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Abstract per-command fake. Each concrete subclass owns one
 * {@code captures/<command>.yaml} containing both a vertex catalog and a
 * {@code paths:} block keyed by scenario name, and implements its production
 * interface ({@code Git} / {@code Maven} / {@code Claude}) by delegating each
 * method to {@link #consume(String)} which walks the next vertex.
 * <p>
 * Indices are static-keyed by {@code scenarioName + ":" + commandKey()} so a
 * scenario that runs the goal twice walks the captured path once across both
 * invocations.
 */
public abstract class CommandFake {

	private static final Logger logger = LoggerFactory.getLogger(CommandFake.class);

	private static final String CAPTURES_ROOT = "/captures/";

	private static final Map<String, Map<String, Vertex>> CATALOGS = new ConcurrentHashMap<>();
	private static final Map<String, Map<String, List<String>>> PATHS = new ConcurrentHashMap<>();
	private static final Map<String, Integer> INDICES = new ConcurrentHashMap<>();

	/**
	 * Clear per-scenario replay cursors. Called between Cucumber scenarios so a
	 * same-named scenario re-run across suite runners (multi-tag scenarios in a
	 * single JVM) starts each path from index 0 rather than picking up the
	 * exhausted cursor from the previous run. Catalog and path maps are
	 * load-once YAML caches and are not cleared.
	 */
	public static void resetIndices() {
		INDICES.clear();
	}

	private final Path codePrjBaseDir;
	private final String scenarioName;
	private final FakeRunnerState state;
	private final Map<String, Vertex> catalog;
	private final List<String> path;
	private final String indexKey;
	private Log runnerLog;

	protected CommandFake(Map<String, Object> properties) {
		String name = FakeProperties.string(properties, "scenarioName");
		this.scenarioName = name == null ? "<unset>" : name;
		Object baseDirObj = properties.get("code-prj.baseDir");
		this.codePrjBaseDir = baseDirObj instanceof Path p ? p : null;
		Object logDirObj = properties.get("log.dir");
		Path eventLogPath = logDirObj instanceof Path p ? p.resolve("mojo.event.log") : null;
		this.state = new FakeRunnerState(codePrjBaseDir, eventLogPath, this.scenarioName);
		ensureLoaded(commandKey());
		this.catalog = CATALOGS.get(commandKey());
		Map<String, List<String>> paths = PATHS.get(commandKey());
		this.path = paths == null ? null : paths.get(this.scenarioName);
		this.indexKey = this.scenarioName + ":" + commandKey();
		if (this.path == null && logger.isDebugEnabled()) {
			List<String> available = paths == null ? List.of() : new ArrayList<>(paths.keySet());
			logger.debug("CommandFake({}) no path for scenario={} available={}",
					commandKey(), this.scenarioName, available);
		} else {
			logger.debug("CommandFake({}) scenario={} pathLen={} catalogSize={}",
					commandKey(), this.scenarioName, this.path == null ? -1 : this.path.size(),
					catalog == null ? -1 : catalog.size());
		}
	}

	/** "claude", "mvn", or "git" — matches the YAML resource name and vertex id prefix. */
	protected abstract String commandKey();

	/**
	 * Set by the test harness (factory lambda in {@code MavenTestObject.runGoal})
	 * once {@code DarmokMojo.init()} has created the runner log. Used by
	 * {@link #consume()} to mirror the production runner's log.debug entries
	 * into the dated runner log so spec assertions on its content still match
	 * after fakes replace the production runners.
	 */
	public final void setRunnerLog(Log runnerLog) {
		this.runnerLog = runnerLog;
	}

	/**
	 * Walk the next vertex in this command's path. Writes the captured command
	 * to the event log (cross-checked by {@code DarmokRunnersLogFileImpl} for
	 * {@code Running:}-prefixed assertions) and returns the vertex so the
	 * subclass can write its own runner-log entries via {@link #logRun(Vertex)}
	 * / {@link #logExecute(Vertex)} and return the right interface value.
	 */
	protected final Vertex consume() {
		if (path == null) {
			Map<String, List<String>> paths = PATHS.get(commandKey());
			List<String> available = paths == null ? List.of() : new ArrayList<>(paths.keySet());
			logger.debug("consume() abort: no path for scenario={} command={} available={}",
					scenarioName, commandKey(), available);
			throw new IllegalStateException("captures/" + commandKey() + ".yaml has no path for scenario \""
				+ scenarioName + "\"");
		}
		int idx = INDICES.getOrDefault(indexKey, 0);
		if (idx >= path.size()) {
			logger.debug("consume() abort: exhausted scenario={} command={} idx={} pathLen={}",
					scenarioName, commandKey(), idx, path.size());
			throw new IllegalStateException("captures/" + commandKey() + ".yaml path \"" + scenarioName
				+ "\" exhausted at call " + (idx + 1) + " — declared " + path.size() + " calls");
		}
		String vid = path.get(idx);
		INDICES.put(indexKey, idx + 1);

		Vertex v = catalog.get(vid);
		if (v == null) {
			logger.debug("consume() abort: vertex {} missing in catalog command={} catalogKeys={}",
					vid, commandKey(), new ArrayList<>(catalog.keySet()));
			throw new IllegalStateException("Vertex '" + vid + "' not found in /captures/"
				+ commandKey() + ".yaml (referenced by scenario " + scenarioName + ")");
		}
		logger.debug("consume() command={} scenario={} idx={} vid={} exit={} stdoutLen={} remaining={}",
				commandKey(), scenarioName, idx, vid, v.exit,
				v.stdout == null ? 0 : v.stdout.length(), path.size() - idx - 1);
		state.appendEventLog(v.in);
		applyAfter(v);
		return v;
	}

	/** Mimic {@code ProcessRunner.run}'s "Running: &lt;cmd&gt;" + per-line stdout entries. */
	protected final void logRun(Vertex v) {
		if (runnerLog == null) return;
		runnerLog.debug("Running: " + v.in);
		emitStdoutLines(v);
	}

	/** Mimic {@code ProcessRunner.capture}'s "Running: &lt;cmd&gt;" without per-line stdout. */
	protected final void logCapture(Vertex v) {
		if (runnerLog == null) return;
		runnerLog.debug("Running: " + v.in);
	}

	/** Mimic {@code ClaudeRunner.executeCommand}'s "Executing: &lt;cmd&gt;" + per-line stdout. */
	protected final void logExecute(Vertex v) {
		if (runnerLog == null) return;
		runnerLog.debug("Executing: " + v.in);
		emitStdoutLines(v);
	}

	private void emitStdoutLines(Vertex v) {
		if (v.stdout == null || v.stdout.isEmpty()) return;
		for (String line : v.stdout.split("\\r?\\n")) {
			runnerLog.debug(line);
		}
	}

	private void applyAfter(Vertex v) {
		if (codePrjBaseDir == null || v.after.isEmpty()) return;
		for (FileAction a : v.after) {
			Path target = codePrjBaseDir.resolve(a.path);
			try {
				if (a.kind == FileAction.Kind.WRITE) {
					Files.createDirectories(target.getParent());
					Files.writeString(target, a.content == null ? "" : a.content,
						StandardCharsets.UTF_8);
				} else {
					Files.deleteIfExists(target);
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	private static synchronized void ensureLoaded(String runner) {
		if (CATALOGS.containsKey(runner)) return;
		String resource = CAPTURES_ROOT + runner + ".yaml";
		Map<String, Vertex> vertices = new LinkedHashMap<>();
		Map<String, List<String>> paths = new LinkedHashMap<>();
		try (InputStream is = CommandFake.class.getResourceAsStream(resource)) {
			if (is != null) {
				Object root = new Yaml().load(is);
				if (root instanceof Map<?, ?> rootMap) {
					for (Map.Entry<?, ?> e : rootMap.entrySet()) {
						String key = String.valueOf(e.getKey());
						if ("paths".equals(key)) {
							if (e.getValue() instanceof Map<?, ?> pathsMap) {
								for (Map.Entry<?, ?> p : pathsMap.entrySet()) {
									if (!(p.getValue() instanceof List<?> rawList)) continue;
									List<String> ids = new ArrayList<>();
									for (Object id : rawList) ids.add(String.valueOf(id));
									paths.put(String.valueOf(p.getKey()), ids);
								}
							}
						} else if (e.getValue() instanceof Map<?, ?> vMap) {
							vertices.put(key, Vertex.parse(vMap));
						}
					}
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		CATALOGS.put(runner, vertices);
		PATHS.put(runner, paths);
	}

	protected static final class Vertex {
		final String in;
		final int exit;
		final String stdout;
		final boolean hangs;
		final boolean blocked;
		final List<FileAction> after;

		Vertex(String in, int exit, String stdout, boolean hangs, boolean blocked,
				List<FileAction> after) {
			this.in = in;
			this.exit = exit;
			this.stdout = stdout;
			this.hangs = hangs;
			this.blocked = blocked;
			this.after = after;
		}

		public int exit() { return exit; }
		public String stdout() { return stdout; }
		public boolean hangs() { return hangs; }
		public boolean blocked() { return blocked; }

		static Vertex parse(Map<?, ?> m) {
			Object inRaw = m.get("in");
			String in = inRaw == null ? "" : String.valueOf(inRaw);
			int exit = 0;
			String stdout = "";
			boolean blocked = false;
			Object outRaw = m.get("out");
			if (outRaw instanceof Map<?, ?> outMap) {
				Object exitRaw = outMap.get("exit");
				if (exitRaw instanceof Number n) exit = n.intValue();
				Object stdoutRaw = outMap.get("stdout");
				if (stdoutRaw != null) stdout = String.valueOf(stdoutRaw);
				Object br = outMap.get("blocked_reader");
				if (br instanceof Boolean b) blocked = b;
			}
			boolean hangs = false;
			Object duringRaw = m.get("during");
			if (duringRaw instanceof Map<?, ?> dMap) {
				Object wfd = dMap.get("wait_for_destroy");
				if (wfd instanceof Boolean b) hangs = b;
			}
			List<FileAction> after = new ArrayList<>();
			Object afterRaw = m.get("after");
			if (afterRaw instanceof List<?> list) {
				for (Object a : list) {
					if (a instanceof Map<?, ?> aMap) after.add(FileAction.parse(aMap));
				}
			}
			return new Vertex(in, exit, stdout, hangs, blocked, after);
		}
	}

	private static final class FileAction {
		enum Kind { WRITE, RM }

		final Kind kind;
		final String path;
		final String content;

		FileAction(Kind kind, String path, String content) {
			this.kind = kind;
			this.path = path;
			this.content = content;
		}

		static FileAction parse(Map<?, ?> m) {
			if (m.containsKey("write")) {
				return new FileAction(Kind.WRITE,
					String.valueOf(m.get("write")),
					m.get("content") == null ? "" : String.valueOf(m.get("content")));
			}
			if (m.containsKey("rm")) {
				return new FileAction(Kind.RM, String.valueOf(m.get("rm")), null);
			}
			throw new IllegalArgumentException("Unrecognised file action: " + m.keySet());
		}
	}
}
