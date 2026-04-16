package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.mbt.maven.MojoLog;
import org.farhan.objects.codeprj.target.darmok.DarmokMojoLogFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class DarmokMojoLogFileImpl extends AbstractFileImpl implements DarmokMojoLogFile {

	private MojoLog mojoLog;

	private MojoLog mojoLog() {
		if (mojoLog == null) {
			mojoLog = new MojoLog(resolveFilePath());
		}
		return mojoLog;
	}

	@Override
	public Path resolveFilePath() {
		Path logDir = (Path) getProperty("log.dir");
		if (logDir == null) {
			Object baseDir = getProperty(component + ".baseDir");
			if (baseDir == null) {
				return null;
			}
			logDir = ((Path) baseDir).resolve("target").resolve("darmok");
		}
		return MojoLog.findDatedLog(logDir, "darmok.mojo");
	}

	@Override
	public String getLevel(HashMap<String, String> keyMap) {
		return mojoLog().matchAndGetLevel(keyMap);
	}

	@Override
	public String getCategory(HashMap<String, String> keyMap) {
		return mojoLog().matchAndGetCategory(keyMap);
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		return mojoLog().matchAndGetContent(keyMap);
	}
}
