package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.mbt.maven.GenFromExistingMojo;
import org.farhan.objects.maven.GenFromExistingGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class GenFromExistingGoalImpl extends MavenTestObject implements GenFromExistingGoal {

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		executeMojo(GenFromExistingMojo.class);
	}

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		executeMojo(GenFromExistingMojo.class);
	}

	@Override
	public void setStage(HashMap<String, String> keyMap) {
		setProperty("stage", keyMap.get("Stage"));
	}

	@Override
	public void setModelGreen(HashMap<String, String> keyMap) {
		setProperty("modelGreen", keyMap.get("ModelGreen"));
	}

	@Override
	public void setModelRefactor(HashMap<String, String> keyMap) {
		setProperty("modelRefactor", keyMap.get("ModelRefactor"));
	}
}
