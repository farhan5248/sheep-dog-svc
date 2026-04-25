package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.objects.codeprj.src.test.java.org.farhan.suites.LoginHappyPathTestJavaFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class LoginHappyPathTestJavaFileImpl extends MavenTestObject implements LoginHappyPathTestJavaFile {

	@Override
	public String getCreatedAsFollows(HashMap<String, String> keyMap) {
		return getFileState(resolveFilePath());
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		return getFileContent(resolveFilePath());
	}
}
