package org.farhan.mbt.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "gen-from-comparison", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenFromComparisonMojo extends DarmokMojo {

	@Parameter(property = "modelComparison", defaultValue = "opus")
	public String modelComparison;

	@Override
	protected String goalName() {
		return "gen-from-comparison";
	}

	@Override
	protected int doExecute() throws Exception {
		int totalProcessed = 0;
		ScenarioEntry entry;
		while ((entry = getNextScenario()) != null) {
			int skillExit = runGenFromComparison();
			if (skillExit != 0) {
				throw new MojoExecutionException("rgr-gen-from-comparison failed with exit code " + skillExit);
			}
			mojoLog.info("Processing Scenario: " + entry.file() + "/" + entry.scenario() + " [" + entry.tag() + "]");
			processScenario(entry);
			totalProcessed++;
			mojoLog.info("");
		}
		return totalProcessed;
	}

	private int runGenFromComparison() throws Exception {
		Claude runner = makeClaudeRunner(modelComparison);
		return runClaudeWithRetry(runnerLog, maxRetries, retryWaitSeconds,
			outputLines -> runner.run(sheepDogRoot, outputLines, "/rgr-gen-from-comparison " + artifactId));
	}
}
