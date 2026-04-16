package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.nio.file.Path;

import org.farhan.objects.specprj.src.test.resources.asciidoc.specs.ProcessDarmokAsciidocFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ProcessDarmokAsciidocFileImpl extends AbstractFileImpl implements ProcessDarmokAsciidocFile {

	@Override
	public String getCreatedAsFollows(java.util.HashMap<String, String> keyMap) {
		return getAsFollows(keyMap);
	}

	@Override
	public String getContent(java.util.HashMap<String, String> keyMap) {
		Path path = resolveFilePath();
		if (path == null || !java.nio.file.Files.exists(path)) {
			return null;
		}
		try {
			return java.nio.file.Files.readString(path);
		} catch (java.io.IOException e) {
			return null;
		}
	}
}
