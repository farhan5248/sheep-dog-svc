package org.farhan.impl;

import org.farhan.common.MavenTestObject;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.objects.maven.UmlToCucumberGoal;
import org.farhan.runners.failsafe.TestConfig;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class UmlToCucumberGoalImpl extends MavenTestObject implements UmlToCucumberGoal {

	@Override
	public void setTags(HashMap<String, String> keyMap) {
		setProperty("tags", keyMap.get("Tags"));
	}

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		runGoal("org.farhan.mbt.maven.UMLToCucumberMojo", TestConfig.getWorkingDir() + "code-prj/");
	}

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		runGoal("org.farhan.mbt.maven.UMLToCucumberMojo", TestConfig.getWorkingDir() + "code-prj/");
	}
}
