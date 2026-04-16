package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.objects.codeprj.target.darmok.DarmokRunnersLogFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Impl for {@code code-prj/target/darmok/darmok.runners.log}.
 * Dated-filename resolution identical to {@link DarmokMojoLogFileImpl}; log-content
 * columns are stubs until the log-content path lands in a later Test-Case.
 */
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class DarmokRunnersLogFileImpl extends AbstractFileImpl implements DarmokRunnersLogFile {

	@Override
	protected Path resolveFilePath() {
		Path logDir = (Path) getProperty("log.dir");
		if (logDir == null) {
			Object baseDir = getProperty(component + ".baseDir");
			if (baseDir == null) {
				return null;
			}
			logDir = ((Path) baseDir).resolve("target").resolve("darmok");
		}
		return DarmokMojoLogFileImpl.findDatedLog(logDir, "darmok.runners");
	}

	@Override
	public String getLevel(HashMap<String, String> keyMap) {
		return keyMap.get("Level");
	}

	@Override
	public String getCategory(HashMap<String, String> keyMap) {
		return keyMap.get("Category");
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		return keyMap.get("Content");
	}
}
