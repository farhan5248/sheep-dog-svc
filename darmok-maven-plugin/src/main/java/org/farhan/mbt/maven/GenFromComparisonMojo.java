package org.farhan.mbt.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "gen-from-comparison", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenFromComparisonMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	public MavenProject project;

	// Path properties
	@Parameter(property = "specsDir", defaultValue = "../../sheep-dog-qa/sheep-dog-specs")
	public String specsDir;

	@Parameter(property = "asciidocDir", defaultValue = "../../sheep-dog-qa/sheep-dog-specs/src/test/resources/asciidoc/specs")
	public String asciidocDir;

	@Parameter(property = "scenariosFile", defaultValue = "scenarios-list.txt")
	public String scenariosFile;

	// Server properties
	@Parameter(property = "host", defaultValue = "dev.sheepdogdev.io")
	public String host;

	// Claude CLI properties
	@Parameter(property = "modelRed", defaultValue = "sonnet")
	public String modelRed;

	@Parameter(property = "modelGreen", defaultValue = "sonnet")
	public String modelGreen;

	@Parameter(property = "modelRefactor", defaultValue = "sonnet")
	public String modelRefactor;

	@Parameter(property = "modelComparison", defaultValue = "sonnet")
	public String modelComparison;

	@Parameter(property = "coAuthor", defaultValue = "Claude Sonnet 4.5 <noreply@anthropic.com>")
	public String coAuthor;

	@Parameter(property = "maxRetries", defaultValue = "3")
	public int maxRetries;

	@Parameter(property = "retryWaitSeconds", defaultValue = "30")
	public int retryWaitSeconds;

	// Pipeline property
	@Parameter(property = "pipeline", defaultValue = "forward")
	public String pipeline;

	@Parameter(property = "onlyChanges", defaultValue = "true")
	public boolean onlyChanges;

	@Parameter(property = "stage", defaultValue = "true")
	public boolean stage;

	public void execute() throws MojoExecutionException {
		GenFromExistingMojo processor = new GenFromExistingMojo();
		processor.project = project;
		processor.specsDir = specsDir;
		processor.asciidocDir = asciidocDir;
		processor.scenariosFile = scenariosFile;
		processor.host = host;
		processor.modelRed = modelRed;
		processor.modelGreen = modelGreen;
		processor.modelRefactor = modelRefactor;
		processor.coAuthor = coAuthor;
		processor.maxRetries = maxRetries;
		processor.retryWaitSeconds = retryWaitSeconds;
		processor.pipeline = pipeline;
		processor.onlyChanges = onlyChanges;
		processor.stage = stage;
		processor.setLog(getLog());

		try {
			processor.init();

			processor.mojoLog.info("RGR Automation Plugin (gen-from-comparison)");

			// Clean up
			int cleanUpExit = processor.runCleanUp();
			if (cleanUpExit != 0) {
				throw new MojoExecutionException("Clean up failed with exit code " + cleanUpExit);
			}

			// Loop: call skill to populate file, then process scenario
			int totalProcessed = 0;
			while (true) {
				// Call skill to write next scenario to file
				int skillExit = runGenFromComparison(processor);
				if (skillExit != 0) {
					throw new MojoExecutionException("rgr-gen-from-comparison failed with exit code " + skillExit);
				}

				// Check if skill produced a scenario
				GenFromExistingMojo.ScenarioEntry entry = processor.getNextScenario();
				if (entry == null) {
					break;
				}

				processor.mojoLog.info("Processing Scenario: " + entry.scenario() + " [" + entry.tag() + "]");
				processor.processScenario(entry);
				totalProcessed++;
				processor.mojoLog.info("");
			}

			processor.mojoLog.info("RGR Automation Complete!");
			processor.mojoLog.info("Total scenarios processed: " + totalProcessed);
		} catch (MojoExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			processor.cleanup();
		}
	}

	private int runGenFromComparison(GenFromExistingMojo processor) throws Exception {
		ClaudeRunner claude = new ClaudeRunner(processor.runnerLog, modelComparison, maxRetries, retryWaitSeconds);
		return claude.run(processor.baseDir + "/../..", "/rgr-gen-from-comparison " + project.getArtifactId());
	}
}
