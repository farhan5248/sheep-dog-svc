package org.farhan.mbt.maven;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "gen-from-existing", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenFromExistingMojo extends DarmokMojo {

	@Override
	protected String goalName() {
		return "gen-from-existing";
	}

	@Override
	protected int doExecute() throws Exception {
		int totalProcessed = 0;
		ScenarioEntry entry;
		while ((entry = getNextScenario()) != null) {
			mojoLog.info("Processing Scenario: " + entry.file() + "/" + entry.scenario() + " [" + entry.tag() + "]");
			processScenario(entry);
			totalProcessed++;
			mojoLog.info("");
		}
		return totalProcessed;
	}
}
