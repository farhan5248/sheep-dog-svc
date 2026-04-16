package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.objects.codeprj.target.darmok.DarmokMojoLogFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class DarmokMojoLogFileImpl extends MavenTestObject implements DarmokMojoLogFile {

	@Override
	public String getAsFollows(HashMap<String, String> keyMap) {
		String stateType = (String) getProperty("stateType");
		if ("won't be".equals(stateType)) {
			return null;
		}
		return getMojoLog("darmok.mojo") != null ? "present" : null;
	}

	@Override
	public String getState(HashMap<String, String> keyMap) {
		return getFileState(getMojoLog("darmok.mojo").getLogFile());
	}

	@Override
	public String getPresent(HashMap<String, String> keyMap) {
		return getState(keyMap);
	}

	@Override
	public String getLevel(HashMap<String, String> keyMap) {
		return getMojoLog("darmok.mojo").matchAndGetLevel(keyMap);
	}

	@Override
	public String getCategory(HashMap<String, String> keyMap) {
		return getMojoLog("darmok.mojo").matchAndGetCategory(keyMap);
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		return getMojoLog("darmok.mojo").matchAndGetContent(keyMap);
	}
}
