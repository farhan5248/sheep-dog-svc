package org.farhan.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.farhan.common.SourceFileRepository;
import org.farhan.common.TestObject;
import org.farhan.fake.FakeProcess;
import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Spring context configuration + Cucumber lifecycle hooks for Darmok scenario glue.
 *
 * <p>
 * {@link #resetTestProject()} runs before every scenario: resets
 * {@link TestObject} static state, wipes {@code target/darmok-test/} from the
 * previous scenario, and recreates fresh directories for the {@code code-prj}
 * and {@code spec-prj} components. The code-prj sits three levels deep so
 * Darmok's {@code runCleanUp} (which walks {@code baseDir/../..}) stays within
 * the test sandbox.
 *
 * <p>
 * baseDir is a stable relative path (under {@code target/}, wiped by
 * {@code mvn clean}) rather than a {@link Files#createTempDirectory} temp dir,
 * so spec assertions can reference the literal path (issue 332).
 *
 * <p>
 * Unlike the sheep-dog-svc-maven-plugin TestConfig, this one has no
 * {@code @AutoConfigureStubRunner} because Darmok has no downstream HTTP services
 * to stub.
 */
@ComponentScan(basePackages = { "org.farhan.impl" })
@EnableAutoConfiguration
@CucumberContextConfiguration
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource("classpath:application.properties")
@ExtendWith(SpringExtension.class)
public class TestConfig {

	private static final Path SCENARIO_ROOT = Path.of("target/darmok-test");

	@Before
	public void resetTestProject(Scenario scenario) throws Exception {
		TestObject.reset();
		new SourceFileRepository(SCENARIO_ROOT.toString()).clear("");
		Path codePrjDir = SCENARIO_ROOT.resolve("sheep-dog-svc").resolve("code-prj");
		Path specPrjDir = SCENARIO_ROOT.resolve("spec-prj");
		Path logDir = SCENARIO_ROOT.resolve("logs");
		Files.createDirectories(codePrjDir);
		Files.createDirectories(specPrjDir);
		Files.createDirectories(logDir);
		TestObject.properties.put("scenario.root", SCENARIO_ROOT);
		TestObject.properties.put("scenarioName", scenario.getName());
		TestObject.properties.put("repository", new SourceFileRepository(SCENARIO_ROOT.toString()));
		TestObject.properties.put("code-prj.componentPath", "sheep-dog-svc/code-prj");
		TestObject.properties.put("spec-prj.componentPath", "spec-prj");
		TestObject.properties.put("code-prj.baseDir", codePrjDir);
		TestObject.properties.put("spec-prj.baseDir", specPrjDir);
		TestObject.properties.put("log.dir", logDir);
		System.setProperty("LOG_PATH", logDir.toString());
		ProcessStarter starter = pb -> {
			// git diff --cached --quiet returns 1 when there ARE staged changes (so commitIfChanged proceeds)
			String cmd = String.join(" ", pb.command());
			if (cmd.contains("diff") && cmd.contains("--cached") && cmd.contains("--quiet")) {
				return new FakeProcess("", 1);
			}
			return new FakeProcess("", 0);
		};
		TestObject.properties.put("processStarter", starter);
	}

	@After
	public void cleanupTestProject() throws IOException {
		new SourceFileRepository(SCENARIO_ROOT.toString()).clear("");
	}
}
