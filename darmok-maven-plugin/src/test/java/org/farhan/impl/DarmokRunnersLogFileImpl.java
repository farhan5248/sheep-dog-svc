package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.objects.codeprj.DarmokRunnersLogFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class DarmokRunnersLogFileImpl extends MavenTestObject implements DarmokRunnersLogFile {

	@Override
	public String getAsFollows(HashMap<String, String> keyMap) {
		return getFileState(relativize(getDarmokMojoLog("darmok.runners").getFile()));
	}

	@Override
	public String getAsFollowsWithThisFailure(HashMap<String, String> keyMap) {
		return getFileState(relativize(getDarmokMojoLog("darmok.runners").getFile()));
	}

	@Override
	public String getEmpty(HashMap<String, String> keyMap) {
		return getFileState(relativize(getDarmokMojoLog("darmok.runners").getFile()));
	}

	@Override
	public String getLevel(HashMap<String, String> keyMap) {
		String uuid = this.toString();
		setUuidProperty(uuid, "default", getDarmokMojoLog("darmok.runners").matchAndGetLevel(keyMap));
		if (keyMap.get("Content").startsWith("Running:")) {
			setUuidProperty(uuid, "eventlog", getDarmokMojoLog("mojo.event").matchAndGetLevel(keyMap));
		}
		return uuid;
	}

	@Override
	public String getCategory(HashMap<String, String> keyMap) {
		String uuid = this.toString();
		setUuidProperty(uuid, "default", getDarmokMojoLog("darmok.runners").matchAndGetCategory(keyMap));
		if (keyMap.get("Content").startsWith("Running:")) {
			setUuidProperty(uuid, "eventlog", getDarmokMojoLog("mojo.event").matchAndGetCategory(keyMap));
		}
		return uuid;
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		String uuid = this.toString();
		setUuidProperty(uuid, "default", getDarmokMojoLog("darmok.runners").matchAndGetContent(keyMap));
		if (keyMap.get("Content").startsWith("Running:")) {
			setUuidProperty(uuid, "eventlog", getDarmokMojoLog("mojo.event").matchAndGetContent(keyMap));
		}
		return uuid;
	}
}
