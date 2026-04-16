package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.objects.specprj.src.test.resources.asciidoc.specs.ProcessDarmokAsciidocFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ProcessDarmokAsciidocFileImpl extends AbstractFileImpl implements ProcessDarmokAsciidocFile {

	@Override
	public String getCreatedAsFollows(HashMap<String, String> keyMap) {
		return getAsFollows(keyMap);
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		String content = getState(keyMap);
		return content != null ? content.trim() : null;
	}
}
