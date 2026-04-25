package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.fake.FakeProcessStarter;
import org.farhan.mbt.maven.GenFromComparisonMojo;
import org.farhan.objects.darmok.GenFromComparisonGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class GenFromComparisonGoalImpl extends MavenTestObject implements GenFromComparisonGoal {

	@Override
	public void setExecutedAndSucceedsWith(HashMap<String, String> keyMap) {
		setProperty("targetProject", "darmok-prj");
		setProperty("processStarter", new FakeProcessStarter(properties));
		executeMojo(GenFromComparisonMojo.class);
	}

	@Override
	public void setModelComparison(HashMap<String, String> keyMap) {
		setProperty("modelComparison", keyMap.get("ModelComparison"));
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
