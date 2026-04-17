package org.farhan.impl;

import org.farhan.common.MavenTestObject;

import java.util.HashMap;

import org.farhan.objects.maven.UmlToCucumberGuiceGoal;
import org.farhan.runners.failsafe.TestConfig;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class UmlToCucumberGoalGuiceImpl extends MavenTestObject implements UmlToCucumberGuiceGoal {

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		runGoal("org.farhan.mbt.maven.UMLToCucumberGuiceMojo", TestConfig.getWorkingDir() + "code-prj/");
	}
}
