package org.farhan.fake;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Per-runner fake for the {@code claude} CLI. Owns dispatch for /rgr-green,
 * /rgr-refactor, and --resume invocations plus the in-flight call counters
 * that drive retry-success / retry-exhaust / hang behaviors.
 * <p>
 * Mutates {@link FakeRunnerState#setCurrentPhase(String)} so {@link MvnProcessFake}
 * and {@link GitProcessFake} can dispatch phase-specific modes.
 */
class ClaudeProcessFake {

	private final FakeRunnerState state;
	private final String greenMode;
	private final String greenPattern;
	private final int greenExit;
	private final String greenOutput;
	private final String greenHangMode;
	private final String refactorMode;
	private final String refactorPattern;
	private final String refactorHangMode;

	private int greenCalls = 0;
	private int refactorCalls = 0;

	ClaudeProcessFake(Map<String, Object> properties, FakeRunnerState state) {
		this.state = state;
		this.greenMode = FakeProperties.string(properties, "claudeGreenMode");
		this.greenPattern = FakeProperties.string(properties, "claudeGreenPattern");
		this.greenExit = FakeProperties.intOrZero(properties, "claudeGreenExit");
		this.greenOutput = FakeProperties.string(properties, "claudeGreenOutput");
		this.greenHangMode = FakeProperties.string(properties, "claudeGreenHangMode");
		this.refactorMode = FakeProperties.string(properties, "claudeRefactorMode");
		this.refactorPattern = FakeProperties.string(properties, "claudeRefactorPattern");
		this.refactorHangMode = FakeProperties.string(properties, "claudeRefactorHangMode");
	}

	boolean handles(List<String> cmd) {
		if (cmd.isEmpty()) return false;
		return cmd.get(0).toLowerCase().startsWith("claude");
	}

	Process start(ProcessBuilder pb) {
		List<String> cmd = pb.command();
		String joined = String.join(" ", cmd);

		if (cmd.stream().anyMatch(a -> a.startsWith("@") && a.endsWith("green.md"))) {
			greenCalls++;
			state.setCurrentPhase("green");
			if ("exits-reader-blocked".equals(greenHangMode)) {
				return new FakeProcess("", 0).withBlockedReader();
			}
			if (shouldHangInitialCall(greenHangMode)) {
				return new FakeProcess("", 0).withHang();
			}
			if ("retry-success".equals(greenMode)) {
				if (greenCalls == 1) {
					return new FakeProcess(orEmpty(greenPattern), 1);
				}
				createImplFile(cmd);
				return new FakeProcess("", 0);
			}
			if ("retry-exhaust".equals(greenMode)) {
				return new FakeProcess(orEmpty(greenPattern), 1);
			}
			if ("non-retryable".equals(greenMode)) {
				return new FakeProcess(orEmpty(greenOutput), greenExit);
			}
			createImplFile(cmd);
			return new FakeProcess("", 0);
		}

		if (cmd.stream().anyMatch(a -> a.startsWith("/rgr-refactor"))) {
			refactorCalls++;
			state.setCurrentPhase("refactor");
			if (shouldHangInitialCall(refactorHangMode)) {
				return new FakeProcess("", 0).withHang();
			}
			if ("retry-success".equals(refactorMode)) {
				return refactorCalls == 1 ? new FakeProcess(orEmpty(refactorPattern), 1) : new FakeProcess("", 0);
			}
			return new FakeProcess("", 0);
		}

		if (joined.contains("--resume")) {
			if (joined.contains(" pls continue")) {
				String hangMode = "green".equals(state.currentPhase()) ? greenHangMode : refactorHangMode;
				if ("hung-every".equals(hangMode)) {
					return new FakeProcess("", 0).withHang();
				}
			}
			return new FakeProcess("", 0);
		}

		return new FakeProcess("", 0);
	}

	private void createImplFile(List<String> cmd) {
		Path baseDir = state.codePrjBaseDir();
		if (baseDir == null) return;
		String tag = extractTagFromGreenInvocation(cmd);
		if (tag == null) return;
		String titleCaseTag = Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
		Path implFile = baseDir.resolve("src/main/java/org/farhan/objects/" + titleCaseTag + ".java");
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

	private static String orEmpty(String s) {
		return s == null ? "" : s;
	}
}
