package org.farhan.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.common.TestObject;

/**
 * Base impl for file-backed step objects in the Darmok test harness.
 *
 * <p>
 * Each scenario's {@link TestConfig} {@code @Before} hook creates temp directories
 * for the {@code code-prj} and {@code spec-prj} components and stores their roots
 * under property keys {@code <component>.baseDir}. This base impl resolves the
 * file path by combining the component's base dir with the {@link #object} path
 * set by the step-def {@code TestSteps} constructor.
 *
 * <p>
 * {@link #getState(HashMap)} returns {@code null} for Absent, {@code ""} for Empty,
 * and the file's content for Present — consumed by
 * {@code TestObject.processInputOutputsTable} and mapped to the {@code TestState}
 * enum when the column is {@code State}.
 *
 * <p>
 * {@link #getAsFollows(HashMap)} returns the file path itself so the
 * {@code processInputOutputsStepDefinitionRef} pre-check ({@code assertNotNull(actual)})
 * passes regardless of whether the file exists — actual presence is asserted via
 * {@link #getState(HashMap)}.
 */
public abstract class AbstractFileImpl extends TestObject {

	protected Path resolveFilePath() {
		Object baseDir = getProperty(component + ".baseDir");
		if (baseDir == null) {
			return null;
		}
		return ((Path) baseDir).resolve(object);
	}

	public String getAsFollows(HashMap<String, String> keyMap) {
		Path path = resolveFilePath();
		return path == null ? null : path.toString();
	}

	public String getState(HashMap<String, String> keyMap) {
		Path path = resolveFilePath();
		if (path == null || !Files.exists(path)) {
			return null;
		}
		try {
			return Files.readString(path);
		} catch (IOException e) {
			return null;
		}
	}

	public void setCreatedAsFollows(HashMap<String, String> keyMap) {
		// heredoc handled by setContent
	}

	public void setContent(HashMap<String, String> keyMap) {
		Path path = resolveFilePath();
		if (path == null) {
			return;
		}
		try {
			Files.createDirectories(path.getParent());
			String content = keyMap.get("Content");
			if (content == null) {
				content = "";
			}
			Files.writeString(path, content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
