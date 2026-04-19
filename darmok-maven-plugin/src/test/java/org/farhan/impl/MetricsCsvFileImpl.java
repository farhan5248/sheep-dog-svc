package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.mbt.maven.MetricsCsv;
import org.farhan.objects.codeprj.target.darmok.MetricsCsvFile;
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
		return getFileState(metricsCsv().getFile());
	}

	@Override
	public String getTimestamp(HashMap<String, String> keyMap) {
		return metricsCsv().matchAndGetTimestamp(keyMap);
	}

	@Override
	public String getCommit(HashMap<String, String> keyMap) {
		return metricsCsv().matchAndGetCommit(keyMap);
	}

	@Override
	public String getScenario(HashMap<String, String> keyMap) {
		return metricsCsv().matchAndGetScenario(keyMap);
	}

	@Override
	public String getPhaseRedMs(HashMap<String, String> keyMap) {
		return metricsCsv().matchAndGetPhaseRedMs(keyMap);
	}

	@Override
	public String getPhaseGreenMs(HashMap<String, String> keyMap) {
		return metricsCsv().matchAndGetPhaseGreenMs(keyMap);
	}

	@Override
	public String getPhaseRefactorMs(HashMap<String, String> keyMap) {
		return metricsCsv().matchAndGetPhaseRefactorMs(keyMap);
	}

	@Override
	public String getPhaseTotalMs(HashMap<String, String> keyMap) {
		return metricsCsv().matchAndGetPhaseTotalMs(keyMap);
	}

	private MetricsCsv metricsCsv() {
		MetricsCsv cached = (MetricsCsv) getProperty("metricsCsv");
		if (cached != null) {
			return cached;
		}
		Path logDir = (Path) getProperty("log.dir");
		if (logDir == null) {
			Path baseDir = (Path) getProperty(component + ".baseDir");
			logDir = baseDir.resolve("target").resolve("darmok");
		}
		MetricsCsv fresh = new MetricsCsv(logDir.resolve("metrics.csv"));
		setProperty("metricsCsv", fresh);
		return fresh;
	}
}
