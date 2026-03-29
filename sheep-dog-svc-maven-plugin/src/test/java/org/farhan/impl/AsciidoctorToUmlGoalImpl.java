package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.objects.maven.AsciidoctorToUmlGoal;
import org.farhan.runners.failsafe.TestConfig;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class AsciidoctorToUmlGoalImpl extends TestObjectGoalImpl implements AsciidoctorToUmlGoal {

	@Override
	public void setTags(HashMap<String, String> keyMap) {
		setProperty("tags", keyMap.get("Tags"));
	}

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		runGoal("org.farhan.mbt.maven.AsciiDoctorToUMLMojo", TestConfig.getWorkingDir() + "spec-prj/");
	}

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		runGoal("org.farhan.mbt.maven.AsciiDoctorToUMLMojo", TestConfig.getWorkingDir() + "spec-prj/");
	}
}
