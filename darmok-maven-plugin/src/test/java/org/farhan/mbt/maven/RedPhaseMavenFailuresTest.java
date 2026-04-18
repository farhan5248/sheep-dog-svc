package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;
import org.farhan.fake.FakeProcess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Three maven-subprocess-failure variants from #258, all exercised against RedPhase
 * directly (the factory seam is unnecessary here — RedPhase takes its collaborators
 * at construction time, so we build the phase with a stub-powered MavenRunner).
 * <p>
 * Documents CURRENT behavior. Note: RedPhase does not branch on the exit codes of
 * asciidoctor-to-uml or uml-to-cucumber-guice today; it only branches on {@code mvn test}.
 * These tests pin that behavior so a future change to halt on intermediate failures
 * will surface as an expected test break.
 */
class RedPhaseMavenFailuresTest {

	@TempDir
	Path baseDir;

	Path runnerLogFile;
	MojoLog mojoLog;
	MojoLog runnerLog;

	@BeforeEach
	void setup() throws Exception {
		Path mojoLogFile = baseDir.resolve("mojo.log");
		runnerLogFile = baseDir.resolve("runner.log");
		mojoLog = new MojoLog(new SystemStreamLog(), "mojo", mojoLogFile);
		runnerLog = new MojoLog(new SystemStreamLog(), "runner", runnerLogFile);
	}

	@AfterEach
	void teardown() {
		if (mojoLog != null) mojoLog.close();
		if (runnerLog != null) runnerLog.close();
	}

	/**
	 * === Test-Case: RedPhase captures asciidoctor-to-uml failure in runner log ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal mvn asciidoctor-to-uml command is executed but failed
	 * |===
	 * | Exit | Output
	 * | 1    | svc unreachable: connection refused
	 * |===
	 *
	 * === When: The darmok plugin gen-from-existing goal red phase is executed
	 *
	 * === Then: The code-prj project target/darmok/darmok.runners.log file will be as follows with this failure
	 * |===
	 * | Level | Category | Content
	 * | DEBUG | runner   | svc unreachable: connection refused
	 * |===
	 * </pre>
	 */
	@Test
	void asciidoctorToUml_failing_isCapturedInRunnerLog() throws Exception {
		MavenRunner maven = mavenRunnerStubbing("asciidoctor-to-uml", 1, "svc unreachable: connection refused");
		RedPhase phase = new RedPhase(maven, mojoLog, baseDir.toString(), "../specs", "dev.host", true);

		phase.run("someTag");

		assertThat(Files.readAllLines(runnerLogFile))
			.as("asciidoctor-to-uml failure output should be captured")
			.anyMatch(l -> l.contains("svc unreachable: connection refused"));
	}

	/**
	 * === Test-Case: RedPhase captures uml-to-cucumber-guice failure in runner log ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal mvn uml-to-cucumber-guice command is executed but failed
	 * |===
	 * | Exit | Output
	 * | 1    | TooManyRequests: upstream rate limit
	 * |===
	 *
	 * === Then: The code-prj project target/darmok/darmok.runners.log file will be as follows with this failure
	 * |===
	 * | Level | Category | Content
	 * | DEBUG | runner   | TooManyRequests: upstream rate limit
	 * |===
	 * </pre>
	 */
	@Test
	void umlToCucumberGuice_failing_isCapturedInRunnerLog() throws Exception {
		MavenRunner maven = mavenRunnerStubbing("uml-to-cucumber-guice", 1, "TooManyRequests: upstream rate limit");
		RedPhase phase = new RedPhase(maven, mojoLog, baseDir.toString(), "../specs", "dev.host", true);

		phase.run("someTag");

		assertThat(Files.readAllLines(runnerLogFile))
			.anyMatch(l -> l.contains("TooManyRequests: upstream rate limit"));
	}

	/**
	 * === Test-Case: RedPhase returns 0 (proceed to green) when mvn test fails with compile error ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal mvn test command is executed but failed
	 * |===
	 * | Exit | Output
	 * | 1    | COMPILATION ERROR : [ERROR] /suites/someTagTest.java:[3,1] cannot find symbol
	 * |===
	 *
	 * === When: The darmok plugin gen-from-existing goal red phase is executed
	 *
	 * === Then: The red phase result exit code will be 0
	 *
	 * === Then: The code-prj project target/darmok/darmok.runners.log file will be as follows with this failure
	 * |===
	 * | Level | Category | Content
	 * | DEBUG | runner   | COMPILATION ERROR
	 * |===
	 * </pre>
	 */
	@Test
	void mvnTest_compileError_returnsZeroAndCapturesOutput() throws Exception {
		MavenRunner maven = mavenRunnerStubbing("test ", 1,
			"COMPILATION ERROR : [ERROR] /suites/someTagTest.java:[3,1] cannot find symbol");
		RedPhase phase = new RedPhase(maven, mojoLog, baseDir.toString(), "../specs", "dev.host", true);

		PhaseResult result = phase.run("someTag");

		assertThat(result.exitCode())
			.as("mvn test failure triggers the 'proceed to green' branch (exit 0)")
			.isEqualTo(0);
		assertThat(Files.readAllLines(runnerLogFile))
			.anyMatch(l -> l.contains("COMPILATION ERROR"));
	}

	/**
	 * Returns a MavenRunner whose stub ProcessStarter returns the given exit code + stdout only
	 * when the command line contains {@code failingArgMatch}, and exit 0 with empty stdout otherwise.
	 */
	private MavenRunner mavenRunnerStubbing(String failingArgMatch, int failingExit, String failingStdout) {
		List<String> commands = new ArrayList<>();
		ProcessStarter starter = pb -> {
			String cmd = String.join(" ", pb.command());
			commands.add(cmd);
			if (cmd.contains(failingArgMatch)) {
				return new FakeProcess(failingStdout, failingExit);
			}
			return new FakeProcess("", 0);
		};
		return new MavenRunner(runnerLog, starter);
	}
}
