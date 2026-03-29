package org.farhan.impl;

import java.util.HashMap;

import org.farhan.objects.maven.CucumberToUmlGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class CucumberToUmlGoalImpl extends TestObjectGoalImpl implements CucumberToUmlGoal {

	public void setTags(HashMap<String, String> keyMap) {
		setProperty("tags", keyMap.get("Tags"));
	}

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		runGoal("cucumber", "ConvertCucumberToUML");
	}
}
