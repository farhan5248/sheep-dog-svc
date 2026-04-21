package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.mbt.maven.DarmokMojoMetrics;
import org.farhan.objects.codeprj.MetricsCsvFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class MetricsCsvFileImpl extends MavenTestObject implements MetricsCsvFile {

	@Override
	public String getAsFollows(HashMap<String, String> keyMap) {
		String stateType = (String) getProperty("stateType");
		if ("won't be".equals(stateType)) {
			return null;
		}
		return getFileState(metrics().getFile());
	}

	@Override
	public String getTimestamp(HashMap<String, String> keyMap) {
		return metrics().matchAndGetTimestamp(keyMap);
	}

	@Override
	public String getCommit(HashMap<String, String> keyMap) {
		return metrics().matchAndGetCommit(keyMap);
	}

	@Override
	public String getScenario(HashMap<String, String> keyMap) {
		return metrics().matchAndGetScenario(keyMap);
	}

	@Override
	public String getPhaseRedMs(HashMap<String, String> keyMap) {
		return metrics().matchAndGetPhaseRedMs(keyMap);
	}

	@Override
	public String getPhaseGreenMs(HashMap<String, String> keyMap) {
		return metrics().matchAndGetPhaseGreenMs(keyMap);
	}

	@Override
	public String getPhaseRefactorMs(HashMap<String, String> keyMap) {
		return metrics().matchAndGetPhaseRefactorMs(keyMap);
	}

	@Override
	public String getPhaseTotalMs(HashMap<String, String> keyMap) {
		return metrics().matchAndGetPhaseTotalMs(keyMap);
	}

	private DarmokMojoMetrics metrics() {
		DarmokMojoMetrics cached = (DarmokMojoMetrics) getProperty("metrics");
		if (cached != null) {
			return cached;
		}
		// Metrics live under the project baseDir (DarmokMojo.resolveMetricsDir default),
		// independent of the log.dir / LOG_PATH that controls log file location.
		Path metricsDir = (Path) getProperty(component + ".baseDir");
		DarmokMojoMetrics fresh = new DarmokMojoMetrics(metricsDir.resolve("metrics.csv"));
		setProperty("metrics", fresh);
		return fresh;
	}
}
