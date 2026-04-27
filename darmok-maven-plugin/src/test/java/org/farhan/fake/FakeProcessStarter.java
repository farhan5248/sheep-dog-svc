package org.farhan.fake;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 *   <li><b>git status --porcelain</b> — synthesized from in-memory filesystem state:
 *       reports {@code  M scenarios-list.txt} when current content differs from the
 *       last-committed snapshot. Combined with the {@code claudeCommandPath} override
 *       which forces a single-shot violation on a configured path. The constructor
 *       snapshots scenarios-list.txt content; {@code git commit} refreshes the
 *       snapshot; {@code git checkout HEAD -- scenarios-list.txt} restores from it.
 *       Lets allowlist Test-Cases fail correctly when production code modifies
 *       scenarios-list.txt before the gate runs (issue #322).</li>
 *   <li><b>claude /rgr-green</b> — dispatches on {@code claudeGreenMode} (retry-success /
 *       retry-exhaust / non-retryable / default success) then on {@code claudeGreenHangMode}
 *       (hung-until-killed / hung-first / hung-every).</li>
 *   <li><b>claude /rgr-refactor</b> — parallel to green; {@code claudeRefactorMode} +
 *       {@code claudeRefactorHangMode}.</li>
 *   <li><b>claude --resume</b> — matches on the trailing message: the #140 timeout resume
 *       (<code>"pls continue"</code>) honours the current phase's hang mode so the resume can
 *       itself time out; the #155 verify resume returns exit 0.</li>
 *   <li><b>mvn asciidoctor-to-uml / uml-to-cucumber-guice / test</b> — each takes an
 *       optional {@code *Mode="fail"} override with explicit Exit+Output values.</li>
 *   <li><b>mvn test default</b> — when no override is set, exits 0 iff the implementation
 *       file (derived from the {@code -Dtest=<Tag>Test} arg) exists in code-prj. Mirrors
 *       real darmok behavior: tests fail iff impl is absent.</li>
 *   <li><b>mvn clean verify</b> — dispatches on {@code mvnVerifyModeGreen} or
 *       {@code mvnVerifyModeRefactor} depending on current phase (fail-once / fail-all).</li>
 *   <li><b>mvn clean install</b> — same per-phase pattern via {@code mvnInstallModeGreen} /
 *       {@code mvnInstallModeRefactor} / {@code mvnInstallModeBaseline} (fail-once-then-pass /
 *       fail-all). The baseline mode applies when no claude phase is active yet — the #312
 *       pre-scenario check. Also used by the #140 timeout-recovery check on the green/refactor side.</li>
 *   <li>Anything else — exit 0.</li>
 * </ul>
 */
public class FakeProcessStarter implements ProcessStarter {

	private final String claudeGreenMode;
	private final String claudeGreenPattern;
	private final int claudeGreenExit;
	private final String claudeGreenOutput;
	private final String claudeGreenHangMode;
	private final String claudeRefactorMode;
	private final String claudeRefactorPattern;
	private final String claudeRefactorHangMode;
	private final String gitWorkspaceState;
	private final String gitBranchCanned;
	private final String mvnAsciidoctorMode;
	private final int mvnAsciidoctorExit;
	private final String mvnAsciidoctorOutput;
	private final String mvnUmlToCucumberMode;
	private final int mvnUmlToCucumberExit;
	private final String mvnUmlToCucumberOutput;
	private final String mvnTestMode;
	private final int mvnTestExit;
	private final String mvnTestOutput;
	private final String mvnVerifyModeGreen;
	private final String mvnVerifyModeRefactor;
	private final String mvnInstallModeGreen;
	private final String mvnInstallModeRefactor;
	private final String mvnInstallModeBaseline;
	private final String claudeCommandPath;
	private final String claudeCommandAttempt;
	private final String claudeCommandPhase;
	private final Path codePrjBaseDir;
	private final Path eventLogPath;
	private final Path scenariosListPath;
	private static final DateTimeFormatter EVENT_LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private int greenCalls = 0;
	private int refactorCalls = 0;
	private int greenVerifyCalls = 0;
	private int refactorVerifyCalls = 0;
	private int greenInstallCalls = 0;
	private int refactorInstallCalls = 0;
	private int baselineInstallCalls = 0;
	private int greenAllowlistChecks = 0;
	private int refactorAllowlistChecks = 0;
	private String currentPhase;
	private String scenariosListSnapshot;

	public FakeProcessStarter(Map<String, Object> properties) {
		this.claudeGreenMode = string(properties, "claudeGreenMode");
		this.claudeGreenPattern = string(properties, "claudeGreenPattern");
		this.claudeGreenExit = intOrZero(properties, "claudeGreenExit");
		this.claudeGreenOutput = string(properties, "claudeGreenOutput");
		this.claudeGreenHangMode = string(properties, "claudeGreenHangMode");
		this.claudeRefactorMode = string(properties, "claudeRefactorMode");
		this.claudeRefactorPattern = string(properties, "claudeRefactorPattern");
		this.claudeRefactorHangMode = string(properties, "claudeRefactorHangMode");
		this.gitWorkspaceState = string(properties, "gitWorkspaceState");
		// Default matches mojo-defaults.properties gitBranch=main so specs that don't
		// configure either side still pass the init-time branch check.
		String cannedBranch = string(properties, "gitBranchCanned");
		this.gitBranchCanned = cannedBranch == null || cannedBranch.isEmpty() ? "main" : cannedBranch;
		this.mvnAsciidoctorMode = string(properties, "mvnAsciidoctorMode");
		this.mvnAsciidoctorExit = intOrZero(properties, "mvnAsciidoctorExit");
		this.mvnAsciidoctorOutput = string(properties, "mvnAsciidoctorOutput");
		this.mvnUmlToCucumberMode = string(properties, "mvnUmlToCucumberMode");
		this.mvnUmlToCucumberExit = intOrZero(properties, "mvnUmlToCucumberExit");
		this.mvnUmlToCucumberOutput = string(properties, "mvnUmlToCucumberOutput");
		this.mvnTestMode = string(properties, "mvnTestMode");
		this.mvnTestExit = intOrZero(properties, "mvnTestExit");
		this.mvnTestOutput = string(properties, "mvnTestOutput");
		this.mvnVerifyModeGreen = string(properties, "mvnVerifyModeGreen");
		this.mvnVerifyModeRefactor = string(properties, "mvnVerifyModeRefactor");
		this.mvnInstallModeGreen = string(properties, "mvnInstallModeGreen");
		this.mvnInstallModeRefactor = string(properties, "mvnInstallModeRefactor");
		this.mvnInstallModeBaseline = string(properties, "mvnInstallModeBaseline");
		this.claudeCommandPath = string(properties, "claudeCommandPath");
		this.claudeCommandAttempt = string(properties, "claudeCommandAttempt");
		String params = string(properties, "claudeCommandParameters");
		if (params != null && params.contains("/rgr-refactor")) {
			this.claudeCommandPhase = "refactor";
		} else if (params != null && params.startsWith("@") && params.endsWith("green.md")) {
			this.claudeCommandPhase = "green";
		} else {
			this.claudeCommandPhase = null;
		}
		Object baseDir = properties.get("code-prj.baseDir");
		this.codePrjBaseDir = baseDir instanceof Path ? (Path) baseDir : null;
		Object logDir = properties.get("log.dir");
		this.eventLogPath = logDir instanceof Path p ? p.resolve("mojo.event.log") : null;
		this.scenariosListPath = this.codePrjBaseDir == null ? null
			: this.codePrjBaseDir.resolve("scenarios-list.txt");
		this.scenariosListSnapshot = readScenariosList();
	}

	@Override
	public Process start(ProcessBuilder pb) {
		List<String> cmd = pb.command();
		String joined = String.join(" ", cmd);
		appendEventLog(joined);

		if (joined.contains("diff") && joined.contains("--cached") && joined.contains("--quiet")) {
			if ("clean".equals(gitWorkspaceState)) {
				return new FakeProcess("", 0);
			}
			return new FakeProcess("", 1);
		}

		if (joined.contains("rev-parse") && joined.contains("--abbrev-ref") && joined.contains("HEAD")) {
			return new FakeProcess(gitBranchCanned, 0);
		}

		if (joined.contains("rev-parse") && joined.contains("HEAD")) {
			return new FakeProcess("abc1234567890abcdef1234567890abcdef12345", 0);
		}

		if (joined.contains("status") && joined.contains("--porcelain")) {
			int count = "green".equals(currentPhase) ? ++greenAllowlistChecks : ++refactorAllowlistChecks;
			StringBuilder out = new StringBuilder();
			if (claudeCommandPath != null
					&& (claudeCommandPhase == null || claudeCommandPhase.equals(currentPhase))
					&& (claudeCommandAttempt == null || String.valueOf(count).equals(claudeCommandAttempt))) {
				out.append(" M ").append(claudeCommandPath);
			}
			if (scenariosListIsModified()) {
				if (out.length() > 0) out.append("\n");
				out.append(" M scenarios-list.txt");
			}
			return new FakeProcess(out.toString(), 0);
		}

		if (joined.contains("checkout") && joined.contains("HEAD") && joined.contains("--")) {
			if (cmd.stream().anyMatch(a -> a.endsWith("scenarios-list.txt"))) {
				restoreScenariosListFromSnapshot();
			}
			return new FakeProcess("", 0);
		}

		if (cmd.size() >= 2 && cmd.get(0).toLowerCase().startsWith("git") && "commit".equals(cmd.get(1))) {
			scenariosListSnapshot = readScenariosList();
			return new FakeProcess("", 0);
		}

		if (joined.contains("claude") && cmd.stream().anyMatch(a ->
				a.startsWith("@") && a.endsWith("green.md"))) {
			greenCalls++;
			currentPhase = "green";
			if ("exits-reader-blocked".equals(claudeGreenHangMode)) {
				// Issue 290: process exits promptly, stdout pipe stays open silent.
				return new FakeProcess("", 0).withBlockedReader();
			}
			if (shouldHangInitialCall(claudeGreenHangMode)) {
				return new FakeProcess("", 0).withHang();
			}
			if ("retry-success".equals(claudeGreenMode)) {
				if (greenCalls == 1) {
					return new FakeProcess(orEmpty(claudeGreenPattern), 1);
				}
				createImplFile(cmd);
				return new FakeProcess("", 0);
			}
			if ("retry-exhaust".equals(claudeGreenMode)) {
				return new FakeProcess(orEmpty(claudeGreenPattern), 1);
			}
			if ("non-retryable".equals(claudeGreenMode)) {
				return new FakeProcess(orEmpty(claudeGreenOutput), claudeGreenExit);
			}
			createImplFile(cmd);
			return new FakeProcess("", 0);
		}

		if (joined.contains("claude") && cmd.stream().anyMatch(a -> a.startsWith("/rgr-refactor"))) {
			refactorCalls++;
			currentPhase = "refactor";
			if (shouldHangInitialCall(claudeRefactorHangMode)) {
				return new FakeProcess("", 0).withHang();
			}
			if ("retry-success".equals(claudeRefactorMode)) {
				return refactorCalls == 1 ? new FakeProcess(orEmpty(claudeRefactorPattern), 1) : new FakeProcess("", 0);
			}
			return new FakeProcess("", 0);
		}

		if (joined.contains("claude") && joined.contains("--resume")) {
			if (joined.contains(" pls continue")) {
				String hangMode = "green".equals(currentPhase) ? claudeGreenHangMode : claudeRefactorHangMode;
				if ("hung-every".equals(hangMode)) {
					return new FakeProcess("", 0).withHang();
				}
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
			return new FakeProcess("BUILD SUCCESS", 0);
		}

		if (cmd.size() >= 3 && cmd.get(0).toLowerCase().startsWith("mvn")
				&& cmd.contains("clean") && cmd.contains("install")) {
			String mode;
			int count;
			if ("green".equals(currentPhase)) {
				mode = mvnInstallModeGreen;
				count = ++greenInstallCalls;
			} else if ("refactor".equals(currentPhase)) {
				mode = mvnInstallModeRefactor;
				count = ++refactorInstallCalls;
			} else {
				mode = mvnInstallModeBaseline;
				count = ++baselineInstallCalls;
			}
			if ("fail-once-then-pass".equals(mode)) {
				return count == 1 ? new FakeProcess("", 1) : new FakeProcess("", 0);
			}
			if ("fail-all".equals(mode)) {
				return new FakeProcess("", 1);
			}
			return new FakeProcess("", 0);
		}

		if (cmd.size() >= 3 && cmd.get(0).toLowerCase().startsWith("mvn")
				&& cmd.contains("clean") && cmd.contains("verify")) {
			String mode = "green".equals(currentPhase) ? mvnVerifyModeGreen : mvnVerifyModeRefactor;
			int count = "green".equals(currentPhase) ? ++greenVerifyCalls : ++refactorVerifyCalls;
			if ("fail-once".equals(mode)) {
				return count == 1 ? new FakeProcess("BUILD FAILURE", 1) : new FakeProcess("BUILD SUCCESS", 0);
			}
			if ("fail-all".equals(mode)) {
				return new FakeProcess("BUILD FAILURE", 1);
			}
			return new FakeProcess("BUILD SUCCESS", 0);
		}

		return new FakeProcess("", 0);
	}

	private String readScenariosList() {
		if (scenariosListPath == null || !Files.exists(scenariosListPath)) return null;
		try {
			return Files.readString(scenariosListPath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private boolean scenariosListIsModified() {
		if (scenariosListPath == null) return false;
		String current = readScenariosList();
		return !java.util.Objects.equals(current, scenariosListSnapshot);
	}

	private void restoreScenariosListFromSnapshot() {
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

	private void appendEventLog(String joinedCmd) {
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

	private void createImplFile(List<String> cmd) {
		if (codePrjBaseDir == null) return;
		String tag = extractTagFromGreenInvocation(cmd);
		if (tag == null) return;
		String titleCaseTag = Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
		Path implFile = codePrjBaseDir.resolve("src/main/java/org/farhan/objects/" + titleCaseTag + ".java");
		try {
			Files.createDirectories(implFile.getParent());
			Files.writeString(implFile, "placeholder", StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String extractTagFromGreenInvocation(List<String> cmd) {
		String atArg = cmd.stream()
			.filter(a -> a.startsWith("@") && a.endsWith("green.md"))
			.findFirst()
			.orElse(null);
		if (atArg == null) return null;
		Path renderedPath = Path.of(atArg.substring(1));
		try {
			String content = Files.readString(renderedPath, StandardCharsets.UTF_8);
			java.util.regex.Matcher m = java.util.regex.Pattern
				.compile("\\*\\*Runner class\\*\\*:\\s*`(\\w+)`")
				.matcher(content);
			if (m.find()) {
				return m.group(1).replaceFirst("Test$", "");
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return null;
	}

	private static boolean shouldHangInitialCall(String hangMode) {
		return "hung-until-killed".equals(hangMode)
			|| "hung-first".equals(hangMode)
			|| "hung-every".equals(hangMode);
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
