package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

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
	public String getAbsent(HashMap<String, String> keyMap) {
		return getFileState(resolveFilePath());
	}

	@Override
	public String getAsFollows(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = new DarmokMojoMetrics(resolveFullPath());
		setProperty("metrics", metrics);
		return getFileState(relativize(metrics.getFile()));
	}

	@Override
	public String getTimestamp(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetTimestamp(keyMap);
	}

	@Override
	public String getGitBranch(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetGitBranch(keyMap);
	}

	@Override
	public String getCommit(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetCommit(keyMap);
	}

	@Override
	public String getScenario(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetScenario(keyMap);
	}

	@Override
	public String getPhaseRedMs(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetPhaseRedMs(keyMap);
	}

	@Override
	public String getPhaseGreenMs(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetPhaseGreenMs(keyMap);
	}

	@Override
	public String getPhaseRefactorMs(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetPhaseRefactorMs(keyMap);
	}

	@Override
	public String getPhaseTotalMs(HashMap<String, String> keyMap) {
		DarmokMojoMetrics metrics = (DarmokMojoMetrics) getProperty("metrics");
		return metrics.matchAndGetPhaseTotalMs(keyMap);
	}
}
