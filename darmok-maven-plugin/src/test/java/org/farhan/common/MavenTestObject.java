package org.farhan.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.farhan.mbt.maven.DarmokMojo;
import org.farhan.mbt.maven.MojoLog;

public class MavenTestObject extends TestObject {

	private static final Properties mojoDefaults = new Properties();

	static {
		try (InputStream is = MavenTestObject.class.getResourceAsStream("/mojo-defaults.properties")) {
			if (is != null) {
				mojoDefaults.load(is);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load mojo-defaults.properties", e);
		}
	}

	protected void executeMojo(Class<? extends DarmokMojo> mojoClass) {
		Path codePrjDir = (Path) getProperty("code-prj.baseDir");
		if (codePrjDir == null) {
			throw new IllegalStateException("code-prj.baseDir not set");
		}

		try {
			DarmokMojo mojo = mojoClass.getConstructor().newInstance();

			MavenProject project = new MavenProject();
			project.setArtifactId("code-prj");
			mojo.project = project;
			mojo.setBaseDir(codePrjDir.toString());

			for (String key : mojoDefaults.stringPropertyNames()) {
				Object override = properties.get(key);
				String value = override != null ? override.toString() : mojoDefaults.getProperty(key);
				setField(mojo, key, value);
			}

			mojo.execute();
			setProperty("goal.exception", null);
		} catch (Exception e) {
			setProperty("goal.exception", e);
		}
	}

	private void setField(Object target, String fieldName, String value) {
		try {
			Field field = findField(target.getClass(), fieldName);
			if (field == null) {
				return;
			}
			field.setAccessible(true);
			Class<?> type = field.getType();
			if (type == String.class) {
				field.set(target, value);
			} else if (type == int.class || type == Integer.class) {
				field.set(target, Integer.parseInt(value));
			} else if (type == boolean.class || type == Boolean.class) {
				field.set(target, Boolean.parseBoolean(value));
			}
		} catch (NoSuchFieldException e) {
			// field doesn't exist on this Mojo subclass, skip
		} catch (Exception e) {
			throw new RuntimeException("Failed to set field " + fieldName, e);
		}
	}

	private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		while (clazz != null) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException(fieldName);
	}

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

	public MojoLog getMojoLog(String prefix) {
		String cacheKey = "mojoLog." + prefix;
		Object cached = getProperty(cacheKey);
		if (cached instanceof MojoLog) {
			return (MojoLog) cached;
		}
		Path logDir = (Path) getProperty("log.dir");
		if (logDir == null) {
			Object baseDir = getProperty(component + ".baseDir");
			if (baseDir == null) {
				return null;
			}
			logDir = ((Path) baseDir).resolve("target").resolve("darmok");
		}
		MojoLog mojoLog = new MojoLog(MojoLog.findDatedLog(logDir, prefix));
		setProperty(cacheKey, mojoLog);
		return mojoLog;
	}

	public Path resolveFilePath() {
		Object baseDir = getProperty(component + ".baseDir");
		if (baseDir == null) {
			return null;
		}
		return ((Path) baseDir).resolve(object);
	}

	public String getFileState(Path path) {
		if (path == null || !Files.exists(path)) {
			return null;
		}
		try {
			return Files.readString(path);
		} catch (IOException e) {
			return null;
		}
	}

	public void createFile(Path path, String stateType) {
		if (path == null) {
			return;
		}
		try {
			if ("isn't".equals(stateType)) {
				Files.deleteIfExists(path);
			} else {
				Files.createDirectories(path.getParent());
				if (!Files.exists(path)) {
					Files.writeString(path, "placeholder");
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeFile(Path path, String content) {
		if (path == null) {
			return;
		}
		try {
			Files.createDirectories(path.getParent());
			if (content == null) {
				content = "";
			}
			Files.writeString(path, content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
