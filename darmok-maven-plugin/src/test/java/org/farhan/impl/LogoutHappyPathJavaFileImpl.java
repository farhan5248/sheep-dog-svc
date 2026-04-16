package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.objects.codeprj.src.main.java.org.farhan.objects.LogoutHappyPathJavaFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class LogoutHappyPathJavaFileImpl extends MavenTestObject implements LogoutHappyPathJavaFile {

	@Override
	public void setCreated(HashMap<String, String> keyMap) {
		createFile(resolveFilePath(), (String) getProperty("stateType"));
	}
}
