package org.farhan.fake;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;
import org.yaml.snakeyaml.Yaml;

/**
 * Pure replay {@link ProcessStarter} — drives every {@link ProcessBuilder} call
 * from the captured YAMLs under {@code src/test/resources/captures/}.
 * <p>
 * Loads per-runner FSM catalogs ({@code claude.yaml}, {@code mvn.yaml},
 * {@code git.yaml}) once at first use and the active scenario's sequence YAML
 * at construction. Each {@link #start(ProcessBuilder)} call walks the matching
 * runner's sequence by index, resolves the vertex id against the catalog, and
 * returns the captured response — exit, stdout, hangs, blocked-reader, plus
 * any {@code after} file actions.
 * <p>
 * Indices are static-keyed by scenario name so a Test-Case that runs the goal
 * twice (e.g. Init and Cleanup's "A second run on the same day...") walks the
 * captured sequence once across both invocations.
 */
public class FakeProcessStarter implements ProcessStarter {

	private static final String CAPTURES_ROOT = "/captures/";

	private static final Map<String, Map<String, Vertex>> CATALOGS = new ConcurrentHashMap<>();
	private static final Map<String, Map<String, Integer>> INDICES = new ConcurrentHashMap<>();

	private final Map<String, List<String>> sequences;
	private final Map<String, Integer> indices;
	private final Path codePrjBaseDir;
	private final String scenarioName;
	private final FakeRunnerState state;

	public FakeProcessStarter(Map<String, Object> properties) {
		String name = FakeProperties.string(properties, "scenarioName");
		this.scenarioName = name == null ? "<unset>" : name;
		Object baseDirObj = properties.get("code-prj.baseDir");
		this.codePrjBaseDir = baseDirObj instanceof Path ? (Path) baseDirObj : null;
		Object logDirObj = properties.get("log.dir");
		Path eventLogPath = logDirObj instanceof Path p ? p.resolve("mojo.event.log") : null;
		this.state = new FakeRunnerState(codePrjBaseDir, eventLogPath, this.scenarioName);
		this.sequences = loadSequences(this.scenarioName);
		this.indices = INDICES.computeIfAbsent(this.scenarioName, k -> new HashMap<>());
	}

	@Override
	public Process start(ProcessBuilder pb) {
		List<String> cmd = pb.command();
		state.appendEventLog(String.join(" ", cmd));
		if (cmd.isEmpty()) return new FakeProcess("", 0);
		String runner = classify(cmd.get(0));
		if (runner == null) return new FakeProcess("", 0);

		List<String> seq = sequences.get(runner);
		if (seq == null) {
			throw new IllegalStateException("captures/" + scenarioName
				+ ".yaml has no " + runner + " sequence");
		}
		int idx = indices.getOrDefault(runner, 0);
		if (idx >= seq.size()) {
			throw new IllegalStateException("captures/" + scenarioName
				+ ".yaml " + runner + " sequence exhausted at call " + (idx + 1)
				+ " — declared " + seq.size() + " calls");
		}
		String vid = seq.get(idx);
		indices.put(runner, idx + 1);

		Map<String, Vertex> catalog = catalogFor(runner);
		Vertex v = catalog.get(vid);
		if (v == null) {
			throw new IllegalStateException("Vertex '" + vid + "' not found in /captures/"
				+ runner + ".yaml (referenced by scenario " + scenarioName + ")");
		}
		applyAfter(v);
		FakeProcess fp = new FakeProcess(v.stdout, v.exit);
		if (v.hangs) fp.withHang();
		if (v.blocked) fp.withBlockedReader();
		return fp;
	}

	// Reference so callers can construct via HashMap.
	public static FakeProcessStarter from(HashMap<String, Object> properties) {
		return new FakeProcessStarter(properties);
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

	private static String classify(String cmd0) {
		String c = cmd0.toLowerCase();
		if (c.startsWith("claude")) return "claude";
		if (c.startsWith("mvn")) return "mvn";
		if (c.startsWith("git")) return "git";
		return null;
	}

	private static Map<String, List<String>> loadSequences(String scenarioName) {
		String resource = CAPTURES_ROOT + scenarioName + ".yaml";
		try (InputStream is = FakeProcessStarter.class.getResourceAsStream(resource)) {
			if (is == null) return Collections.emptyMap();
			Object root = new Yaml().load(is);
			if (!(root instanceof Map<?, ?> rootMap)) return Collections.emptyMap();
			Object seqs = rootMap.get("sequences");
			if (!(seqs instanceof Map<?, ?> seqsMap)) return Collections.emptyMap();
			Map<String, List<String>> out = new LinkedHashMap<>();
			for (Map.Entry<?, ?> e : seqsMap.entrySet()) {
				if (!(e.getValue() instanceof List<?> rawList)) continue;
				List<String> ids = new ArrayList<>();
				for (Object id : rawList) ids.add(String.valueOf(id));
				out.put(String.valueOf(e.getKey()), ids);
			}
			return out;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Map<String, Vertex> catalogFor(String runner) {
		return CATALOGS.computeIfAbsent(runner, FakeProcessStarter::loadCatalog);
	}

	private static Map<String, Vertex> loadCatalog(String runner) {
		String resource = CAPTURES_ROOT + runner + ".yaml";
		try (InputStream is = FakeProcessStarter.class.getResourceAsStream(resource)) {
			if (is == null) return Collections.emptyMap();
			Object root = new Yaml().load(is);
			if (!(root instanceof Map<?, ?> rootMap)) return Collections.emptyMap();
			Map<String, Vertex> out = new LinkedHashMap<>();
			for (Map.Entry<?, ?> e : rootMap.entrySet()) {
				if (e.getValue() instanceof Map<?, ?> vMap) {
					out.put(String.valueOf(e.getKey()), Vertex.parse(vMap));
				}
			}
			return out;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static final class Vertex {
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
