package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.objects.codeprj.ScenariosListTxtFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ScenariosListTxtFileImpl extends AbstractFileImpl implements ScenariosListTxtFile {

	@Override
	public void setCreatedWithoutAnyScenarios(HashMap<String, String> keyMap) {
		writeFile(resolveFilePath(), "");
	}
}
