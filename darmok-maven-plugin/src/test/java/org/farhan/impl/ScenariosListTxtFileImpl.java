package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.objects.codeprj.ScenariosListTxtFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Impl for {@code code-prj/scenarios-list.txt}. Inherits file-level state
 * assertions from {@link AbstractFileImpl}.
 */
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ScenariosListTxtFileImpl extends AbstractFileImpl implements ScenariosListTxtFile {

	@Override
	public void setCreatedWithoutAnyScenarios(HashMap<String, String> keyMap) {
		Path path = resolveFilePath();
		if (path == null) {
			return;
		}
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, "");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
