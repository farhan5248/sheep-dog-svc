package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.objects.codeprj.ScenariosListTxtFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ScenariosListTxtFileImpl extends MavenTestObject implements ScenariosListTxtFile {

	@Override
	public void setCreated(HashMap<String, String> keyMap) {
		createFile(resolveFilePath(), (String) getProperty("stateType"));
	}

	@Override
	public void setCreatedAsFollows(HashMap<String, String> keyMap) {
		// heredoc handled by setContent
	}

	@Override
	public void setContent(HashMap<String, String> keyMap) {
		writeFile(resolveFilePath(), keyMap.get("Content"));
	}

	@Override
	public void setCreatedWithoutAnyScenarios(HashMap<String, String> keyMap) {
		writeFile(resolveFilePath(), "");
	}

	@Override
	public String getAbsent(HashMap<String, String> keyMap) {
		return getState(keyMap);
	}

	@Override
	public String getEmpty(HashMap<String, String> keyMap) {
		return getState(keyMap);
	}
}
