package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.objects.maven.UmlToAsciidoctorGoal;
import org.farhan.runners.failsafe.TestConfig;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class UmlToAsciidoctorGoalImpl extends TestObjectGoalImpl implements UmlToAsciidoctorGoal {

	@Override
	public void setTags(HashMap<String, String> keyMap) {
		setProperty("tags", keyMap.get("Tags"));
	}

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		runGoal("org.farhan.mbt.maven.UMLToAsciiDoctorMojo", TestConfig.getWorkingDir() + "spec-prj/");
	}

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		runGoal("org.farhan.mbt.maven.UMLToAsciiDoctorMojo", TestConfig.getWorkingDir() + "spec-prj/");
	}
}
