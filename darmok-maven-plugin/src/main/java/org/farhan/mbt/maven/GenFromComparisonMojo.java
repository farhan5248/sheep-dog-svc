package org.farhan.mbt.maven;

import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "gen-from-comparison", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenFromComparisonMojo extends DarmokMojo {

	@Parameter(property = "modelComparison", defaultValue = "sonnet")
	public String modelComparison;

	public void execute() throws MojoExecutionException {
		try {
			init();

			mojoLog.info("RGR Automation Plugin (gen-from-comparison)");

			int cleanUpExit = runCleanUp();
			if (cleanUpExit != 0) {
				throw new MojoExecutionException("Clean up failed with exit code " + cleanUpExit);
			}

			int totalProcessed = 0;
			while (true) {
				int skillExit = runGenFromComparison();
				if (skillExit != 0) {
					throw new MojoExecutionException("rgr-gen-from-comparison failed with exit code " + skillExit);
				}

				ScenarioEntry entry = getNextScenario();
				if (entry == null) {
					break;
				}

				mojoLog.info("Processing Scenario: " + entry.file() + "/" + entry.scenario() + " [" + entry.tag() + "]");
				processScenario(entry);
				totalProcessed++;
				mojoLog.info("");
			}

			mojoLog.info("RGR Automation Complete!");
			mojoLog.info("Total scenarios processed: " + totalProcessed);
		} catch (MojoExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			cleanup();
		}
	}

	private int runGenFromComparison() throws Exception {
		ClaudeRunner claude = claudeRunnerFactory.create(runnerLog, modelComparison, maxRetries, retryWaitSeconds, maxClaudeSeconds,
			claudeSessionIdEnabled, () -> UUID.randomUUID().toString());
		return claude.run(baseDir + "/../..", "/rgr-gen-from-comparison " + project.getArtifactId());
	}
}
