package org.farhan.impl;

import org.farhan.common.MavenTestObject;

import java.util.HashMap;

import org.farhan.objects.codeprj.srcgen.test.java.org.farhan.stepdefs.blah.BlahObjectPageStepsJavaFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class BlahObjectPageStepsJavaFileImpl extends MavenTestObject implements BlahObjectPageStepsJavaFile {

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		return getFileContent();
	}

	@Override
	public void setContent(HashMap<String, String> keyMap) {
		setContent(keyMap.get("Content"));
	}

    @Override
    public void setCreatedAsFollows(HashMap<String, String> keyMap) {
        // TODO implement later
    }

    @Override
    public String getCreatedAsFollows(HashMap<String, String> keyMap) {
        return getObjectExists();
    }

}
