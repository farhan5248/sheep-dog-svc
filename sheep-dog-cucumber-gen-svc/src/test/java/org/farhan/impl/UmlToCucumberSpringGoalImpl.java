package org.farhan.impl;

import java.util.HashMap;

import org.farhan.objects.maven.UmlToCucumberSpringGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class UmlToCucumberSpringGoalImpl extends TestObjectGoalImpl implements UmlToCucumberSpringGoal {

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		runGoal("cucumber", "ConvertUMLToCucumberSpring");
	}
}
