package org.farhan.runners.surefire;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.farhan.common.TestObject;
import org.farhan.mbt.maven.FakeProcess;
import org.farhan.mbt.maven.ProcessRunner;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Spring context configuration + Cucumber lifecycle hooks for Darmok scenario glue.
 *
 * <p>
 * {@link #resetTestProject()} runs before every scenario: resets
 * {@link TestObject} static state and creates fresh temp directories for the
 * {@code code-prj} and {@code spec-prj} components. The code-prj sits three
 * levels deep so Darmok's {@code runCleanUp} (which walks
 * {@code baseDir/../..}) stays within the scenario's temp sandbox.
 *
 * <p>
 * Unlike the sheep-dog-svc-maven-plugin TestConfig, this one has no
 * {@code @AutoConfigureStubRunner} because Darmok has no downstream HTTP services
 * to stub, and no {@code @TestPropertySource} because no env-specific config is
 * needed yet.
 */
@ComponentScan(basePackages = { "org.farhan.impl" })
@EnableAutoConfiguration
@CucumberContextConfiguration
@ContextConfiguration(classes = TestConfig.class)
@ExtendWith(SpringExtension.class)
public class TestConfig {

	@Before
	public void resetTestProject() throws Exception {
		TestObject.reset();
		Path scenarioRoot = Files.createTempDirectory("darmok-spec-");
		Path codePrjDir = scenarioRoot.resolve("sheep-dog-svc").resolve("code-prj");
		Path specPrjDir = scenarioRoot.resolve("spec-prj");
		Path logDir = scenarioRoot.resolve("logs");
		Files.createDirectories(codePrjDir);
		Files.createDirectories(specPrjDir);
		Files.createDirectories(logDir);
		TestObject.properties.put("scenario.root", scenarioRoot);
		TestObject.properties.put("code-prj.baseDir", codePrjDir);
		TestObject.properties.put("spec-prj.baseDir", specPrjDir);
		TestObject.properties.put("log.dir", logDir);
		System.setProperty("LOG_PATH", logDir.toString());
		ProcessRunner.starter = pb -> {
			// git diff --cached --quiet returns 1 when there ARE staged changes (so commitIfChanged proceeds)
			String cmd = String.join(" ", pb.command());
			if (cmd.contains("diff") && cmd.contains("--cached") && cmd.contains("--quiet")) {
				return new FakeProcess("", 1);
			}
			return new FakeProcess("", 0);
		};
	}

	@After
	public void cleanupTestProject() throws IOException {
		ProcessRunner.starter = ProcessBuilder::start;
		Path scenarioRoot = (Path) TestObject.properties.get("scenario.root");
		if (scenarioRoot != null && Files.exists(scenarioRoot)) {
			try (var stream = Files.walk(scenarioRoot)) {
				stream.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
					try {
						Files.delete(p);
					} catch (IOException ignored) {
					}
				});
			}
		}
	}
}
