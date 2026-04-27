package org.farhan.fake;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Per-runner fake for the {@code mvn} CLI. Owns dispatch for asciidoctor-to-uml,
 * uml-to-cucumber-guice, mvn test, mvn clean install, and mvn clean verify, plus
 * the phase-aware install/verify counters.
 * <p>
 * Reads {@link FakeRunnerState#currentPhase()} to pick green vs refactor vs
 * baseline modes. Reads code-prj baseDir from state to check whether the
 * production-code impl file exists for {@code mvn test} default-success path.
 */
class MvnProcessFake {

	private final FakeRunnerState state;
	private final String asciidoctorMode;
	private final int asciidoctorExit;
	private final String asciidoctorOutput;
	private final String umlToCucumberMode;
	private final int umlToCucumberExit;
	private final String umlToCucumberOutput;
	private final String testMode;
	private final int testExit;
	private final String testOutput;
	private final String verifyModeGreen;
	private final String verifyModeRefactor;
	private final String installModeGreen;
	private final String installModeRefactor;
	private final String installModeBaseline;

	private int greenVerifyCalls = 0;
	private int refactorVerifyCalls = 0;
	private int greenInstallCalls = 0;
	private int refactorInstallCalls = 0;
	private int baselineInstallCalls = 0;

	MvnProcessFake(Map<String, Object> properties, FakeRunnerState state) {
		this.state = state;
		this.asciidoctorMode = FakeProperties.string(properties, "mvnAsciidoctorMode");
		this.asciidoctorExit = FakeProperties.intOrZero(properties, "mvnAsciidoctorExit");
		this.asciidoctorOutput = FakeProperties.string(properties, "mvnAsciidoctorOutput");
		this.umlToCucumberMode = FakeProperties.string(properties, "mvnUmlToCucumberMode");
		this.umlToCucumberExit = FakeProperties.intOrZero(properties, "mvnUmlToCucumberExit");
		this.umlToCucumberOutput = FakeProperties.string(properties, "mvnUmlToCucumberOutput");
		this.testMode = FakeProperties.string(properties, "mvnTestMode");
		this.testExit = FakeProperties.intOrZero(properties, "mvnTestExit");
		this.testOutput = FakeProperties.string(properties, "mvnTestOutput");
		this.verifyModeGreen = FakeProperties.string(properties, "mvnVerifyModeGreen");
		this.verifyModeRefactor = FakeProperties.string(properties, "mvnVerifyModeRefactor");
		this.installModeGreen = FakeProperties.string(properties, "mvnInstallModeGreen");
		this.installModeRefactor = FakeProperties.string(properties, "mvnInstallModeRefactor");
		this.installModeBaseline = FakeProperties.string(properties, "mvnInstallModeBaseline");
	}

	boolean handles(List<String> cmd) {
		if (cmd.isEmpty()) return false;
		return cmd.get(0).toLowerCase().startsWith("mvn");
	}

	Process start(ProcessBuilder pb) {
		List<String> cmd = pb.command();
		String joined = String.join(" ", cmd);

		if (joined.contains("asciidoctor-to-uml")) {
			if ("fail".equals(asciidoctorMode)) {
				return new FakeProcess(orEmpty(asciidoctorOutput), asciidoctorExit);
			}
			return new FakeProcess("", 0);
		}

		if (joined.contains("uml-to-cucumber-guice")) {
			if ("fail".equals(umlToCucumberMode)) {
				return new FakeProcess(orEmpty(umlToCucumberOutput), umlToCucumberExit);
			}
			return new FakeProcess("", 0);
		}

		if (cmd.size() >= 2 && "test".equals(cmd.get(1))) {
			if ("fail".equals(testMode)) {
				return new FakeProcess(orEmpty(testOutput), testExit);
			}
			if (!implFileExists(cmd)) {
				return new FakeProcess("tests failing: impl class missing", 1);
			}
			return new FakeProcess("BUILD SUCCESS", 0);
		}

		if (cmd.size() >= 3 && cmd.contains("clean") && cmd.contains("install")) {
			String mode;
			int count;
			if ("green".equals(state.currentPhase())) {
				mode = installModeGreen;
				count = ++greenInstallCalls;
			} else if ("refactor".equals(state.currentPhase())) {
				mode = installModeRefactor;
				count = ++refactorInstallCalls;
			} else {
				mode = installModeBaseline;
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

		if (cmd.size() >= 3 && cmd.contains("clean") && cmd.contains("verify")) {
			String mode = "green".equals(state.currentPhase()) ? verifyModeGreen : verifyModeRefactor;
			int count = "green".equals(state.currentPhase()) ? ++greenVerifyCalls : ++refactorVerifyCalls;
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

	private boolean implFileExists(List<String> cmd) {
		String testArg = cmd.stream()
			.filter(a -> a.startsWith("-Dtest="))
			.findFirst()
			.orElse("");
		Path baseDir = state.codePrjBaseDir();
		if (testArg.isEmpty() || baseDir == null) {
			return true;
		}
		String tag = testArg.replace("-Dtest=", "").replaceFirst("Test$", "");
		if (tag.isEmpty()) {
			return true;
		}
		String titleCaseTag = Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
		Path implFile = baseDir.resolve("src/main/java/org/farhan/objects/" + titleCaseTag + ".java");
		return Files.exists(implFile);
	}

	private static String orEmpty(String s) {
		return s == null ? "" : s;
	}
}
