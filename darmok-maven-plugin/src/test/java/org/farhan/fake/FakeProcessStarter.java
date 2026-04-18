package org.farhan.fake;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;

/**
 * Test-double {@link ProcessStarter} for the darmok plugin's gen-from-existing goal.
 * Reads mode + parameter values from a shared properties map (populated by the
 * step-definition impl class) and dispatches per-command at {@link #start} time.
 * <p>
 * Per-command contracts:
 * <ul>
 *   <li><b>git diff --cached --quiet</b> — exit 0 when {@code gitWorkspaceState} is
 *       {@code "clean"}, exit 1 otherwise (matching TestConfig default of dirty workspace).</li>
 *   <li><b>claude /rgr-green</b> — dispatches on {@code claudeGreenMode}: retry-success /
 *       retry-exhaust / non-retryable / (default success). Stateful across calls.</li>
 *   <li><b>claude /rgr-refactor</b> — dispatches on {@code claudeRefactorMode}: retry-success
 *       / (default success). Stateful across calls.</li>
 *   <li><b>mvn asciidoctor-to-uml / uml-to-cucumber-guice / test</b> — each takes an
 *       optional {@code *Mode="fail"} override with explicit Exit+Output values.</li>
 *   <li><b>mvn test default</b> — when no override is set, exits 0 iff the implementation
 *       file (derived from the {@code -Dtest=<Tag>Test} arg) exists in code-prj. Mirrors
 *       real darmok behavior: tests fail iff impl is absent.</li>
 *   <li>Anything else — exit 0.</li>
 * </ul>
 */
public class FakeProcessStarter implements ProcessStarter {

	private final String claudeGreenMode;
	private final String claudeGreenPattern;
	private final int claudeGreenExit;
	private final String claudeGreenOutput;
	private final String claudeRefactorMode;
	private final String claudeRefactorPattern;
	private final String gitWorkspaceState;
	private final String mvnAsciidoctorMode;
	private final int mvnAsciidoctorExit;
	private final String mvnAsciidoctorOutput;
	private final String mvnUmlToCucumberMode;
	private final int mvnUmlToCucumberExit;
	private final String mvnUmlToCucumberOutput;
	private final String mvnTestMode;
	private final int mvnTestExit;
	private final String mvnTestOutput;
	private final Path codePrjBaseDir;

	private int greenCalls = 0;
	private int refactorCalls = 0;

	public FakeProcessStarter(Map<String, Object> properties) {
		this.claudeGreenMode = string(properties, "claudeGreenMode");
		this.claudeGreenPattern = string(properties, "claudeGreenPattern");
		this.claudeGreenExit = intOrZero(properties, "claudeGreenExit");
		this.claudeGreenOutput = string(properties, "claudeGreenOutput");
		this.claudeRefactorMode = string(properties, "claudeRefactorMode");
		this.claudeRefactorPattern = string(properties, "claudeRefactorPattern");
		this.gitWorkspaceState = string(properties, "gitWorkspaceState");
		this.mvnAsciidoctorMode = string(properties, "mvnAsciidoctorMode");
		this.mvnAsciidoctorExit = intOrZero(properties, "mvnAsciidoctorExit");
		this.mvnAsciidoctorOutput = string(properties, "mvnAsciidoctorOutput");
		this.mvnUmlToCucumberMode = string(properties, "mvnUmlToCucumberMode");
		this.mvnUmlToCucumberExit = intOrZero(properties, "mvnUmlToCucumberExit");
		this.mvnUmlToCucumberOutput = string(properties, "mvnUmlToCucumberOutput");
		this.mvnTestMode = string(properties, "mvnTestMode");
		this.mvnTestExit = intOrZero(properties, "mvnTestExit");
		this.mvnTestOutput = string(properties, "mvnTestOutput");
		Object baseDir = properties.get("code-prj.baseDir");
		this.codePrjBaseDir = baseDir instanceof Path ? (Path) baseDir : null;
	}

	@Override
	public Process start(ProcessBuilder pb) {
		List<String> cmd = pb.command();
		String joined = String.join(" ", cmd);

		if (joined.contains("diff") && joined.contains("--cached") && joined.contains("--quiet")) {
			if ("clean".equals(gitWorkspaceState)) {
				return new FakeProcess("", 0);
			}
			return new FakeProcess("", 1);
		}

		if (joined.contains("claude") && cmd.stream().anyMatch(a -> a.startsWith("/rgr-green"))) {
			greenCalls++;
			if ("retry-success".equals(claudeGreenMode)) {
				return greenCalls == 1 ? new FakeProcess(orEmpty(claudeGreenPattern), 1) : new FakeProcess("", 0);
			}
			if ("retry-exhaust".equals(claudeGreenMode)) {
				return new FakeProcess(orEmpty(claudeGreenPattern), 1);
			}
			if ("non-retryable".equals(claudeGreenMode)) {
				return new FakeProcess(orEmpty(claudeGreenOutput), claudeGreenExit);
			}
			return new FakeProcess("", 0);
		}

		if (joined.contains("claude") && cmd.stream().anyMatch(a -> a.startsWith("/rgr-refactor"))) {
			refactorCalls++;
			if ("retry-success".equals(claudeRefactorMode)) {
				return refactorCalls == 1 ? new FakeProcess(orEmpty(claudeRefactorPattern), 1) : new FakeProcess("", 0);
			}
			return new FakeProcess("", 0);
		}

		if (joined.contains("asciidoctor-to-uml")) {
			if ("fail".equals(mvnAsciidoctorMode)) {
				return new FakeProcess(orEmpty(mvnAsciidoctorOutput), mvnAsciidoctorExit);
			}
			return new FakeProcess("", 0);
		}

		if (joined.contains("uml-to-cucumber-guice")) {
			if ("fail".equals(mvnUmlToCucumberMode)) {
				return new FakeProcess(orEmpty(mvnUmlToCucumberOutput), mvnUmlToCucumberExit);
			}
			return new FakeProcess("", 0);
		}

		if (cmd.size() >= 2 && cmd.get(0).toLowerCase().startsWith("mvn") && "test".equals(cmd.get(1))) {
			if ("fail".equals(mvnTestMode)) {
				return new FakeProcess(orEmpty(mvnTestOutput), mvnTestExit);
			}
			if (!implFileExists(cmd)) {
				return new FakeProcess("tests failing: impl class missing", 1);
			}
			return new FakeProcess("", 0);
		}

		return new FakeProcess("", 0);
	}

	private boolean implFileExists(List<String> cmd) {
		String testArg = cmd.stream()
			.filter(a -> a.startsWith("-Dtest="))
			.findFirst()
			.orElse("");
		if (testArg.isEmpty() || codePrjBaseDir == null) {
			return true;
		}
		String tag = testArg.replace("-Dtest=", "").replaceFirst("Test$", "");
		if (tag.isEmpty()) {
			return true;
		}
		String titleCaseTag = Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
		Path implFile = codePrjBaseDir.resolve("src/main/java/org/farhan/objects/" + titleCaseTag + ".java");
		return Files.exists(implFile);
	}

	private static String string(Map<String, Object> properties, String key) {
		Object value = properties.get(key);
		return value instanceof String ? (String) value : null;
	}

	private static int intOrZero(Map<String, Object> properties, String key) {
		Object value = properties.get(key);
		if (value instanceof String s && !s.isEmpty()) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	private static String orEmpty(String s) {
		return s == null ? "" : s;
	}

	// Reference so callers see HashMap works — Map interface accepted more broadly.
	public static FakeProcessStarter from(HashMap<String, Object> properties) {
		return new FakeProcessStarter(properties);
	}
}
