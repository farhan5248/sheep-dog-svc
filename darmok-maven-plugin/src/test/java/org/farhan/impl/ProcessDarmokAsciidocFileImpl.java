package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.common.MavenTestObject;
import org.farhan.objects.specprj.src.test.resources.asciidoc.specs.ProcessDarmokAsciidocFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ProcessDarmokAsciidocFileImpl extends MavenTestObject implements ProcessDarmokAsciidocFile {

	@Override
	public void setCreated(HashMap<String, String> keyMap) {
		createOrDeleteFile(resolveFilePath());
	}

	@Override
	public void setCreatedAsFollows(HashMap<String, String> keyMap) {
		createOrDeleteFile(resolveFilePath());
	}

	@Override
	public void setContent(HashMap<String, String> keyMap) {
		writeFile(resolveFilePath(), keyMap.get("Content"));
	}

	@Override
	public String getCreatedAsFollows(HashMap<String, String> keyMap) {
		return getFileState(resolveFilePath());
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		return getFileContent(resolveFilePath());
	}
}
