package org.farhan.impl;

import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.common.MavenTestObject;

public abstract class AbstractFileImpl extends MavenTestObject {

	public String getAsFollows(HashMap<String, String> keyMap) {
		String stateType = (String) getProperty("stateType");
		if ("won't be".equals(stateType)) {
			return null;
		}
		Path path = resolveFilePath();
		return path == null ? null : path.toString();
	}

	public String getPresent(HashMap<String, String> keyMap) {
		return getState(keyMap);
	}

	public String getEmpty(HashMap<String, String> keyMap) {
		return getState(keyMap);
	}

	public String getAbsent(HashMap<String, String> keyMap) {
		return getState(keyMap);
	}

	public String getState(HashMap<String, String> keyMap) {
		return getFileState(resolveFilePath());
	}

	public void setCreated(HashMap<String, String> keyMap) {
		createFile(resolveFilePath(), (String) getProperty("stateType"));
	}

	public void setCreatedAsFollows(HashMap<String, String> keyMap) {
		// heredoc handled by setContent
	}

	public void setContent(HashMap<String, String> keyMap) {
		writeFile(resolveFilePath(), keyMap.get("Content"));
	}
}
