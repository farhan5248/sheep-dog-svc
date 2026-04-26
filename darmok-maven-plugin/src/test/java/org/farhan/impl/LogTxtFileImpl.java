package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.objects.codeprj.LogTxtFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class LogTxtFileImpl extends MavenTestObject implements LogTxtFile {

	@Override
	public String getPresent(HashMap<String, String> keyMap) {
		return getFileState(resolveFilePath());
	}
}
