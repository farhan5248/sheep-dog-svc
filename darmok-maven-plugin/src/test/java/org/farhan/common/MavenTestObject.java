package org.farhan.common;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.project.MavenProject;
import org.farhan.mbt.maven.Claude;
import org.farhan.mbt.maven.DarmokMojo;
import org.farhan.mbt.maven.DarmokMojoLog;
import org.farhan.mbt.maven.Git;
import org.farhan.mbt.maven.Maven;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * {@link TestObject} subclass that adds Maven plugin test scaffolding:
 * Mojo execution via reflection, Spring-loaded parameter defaults
 * (application.properties), file I/O helpers via {@link SourceFileRepository},
 * and dated-log inspection.
 */
public abstract class MavenTestObject extends TestObject {

    @Autowired
    private Environment env;

    protected final void runGoal(Class<? extends DarmokMojo> mojoClass, String baseDir) {
        try {
            DarmokMojo mojo = mojoClass.getConstructor().newInstance();

            MavenProject project = new MavenProject();
            project.setArtifactId("code-prj");
            mojo.project = project;
            mojo.setBaseDir(baseDir);

            Object claudeProp = getProperty("claude");
            Object mvnProp = getProperty("maven");
            Object gitProp = getProperty("git");
            if (claudeProp instanceof Claude claudeFake
                    && mvnProp instanceof Maven mavenFake
                    && gitProp instanceof Git gitFake) {
                mojo.setGitRunnerFactory(log -> {
                    if (gitFake instanceof org.farhan.common.CommandFake fake) fake.setRunnerLog(log);
                    return gitFake;
                });
                mojo.setMavenRunnerFactory(log -> {
                    if (mavenFake instanceof org.farhan.common.CommandFake fake) fake.setRunnerLog(log);
                    return mavenFake;
                });
                mojo.setClaudeRunnerFactory((log, model, maxSeconds, sessionEnabled, uuidSupplier) -> {
                    if (claudeFake instanceof org.farhan.common.CommandFake fake) fake.setRunnerLog(log);
                    return claudeFake;
                });
            }

            // Iterate public fields (Mojo @Parameter fields are public). The Maven
            // @Parameter annotation has CLASS retention, so reflection can't filter
            // by it at runtime — using public visibility as the proxy works because
            // internal state fields are private/protected.
            for (Class<?> c = mojoClass; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    String name = field.getName();
                    Object override = properties.get(name);
                    String value = override != null ? override.toString() : env.getProperty(name);
                    if (value != null) setField(mojo, name, value);
                }
            }

            mojo.execute();
            setProperty("goal.exception", null);
        } catch (Exception e) {
            setProperty("goal.exception", e);
        }
    }

    protected final SourceFileRepository sr() {
        return (SourceFileRepository) getProperty("repository");
    }

    /** Convert a production-yielded absolute Path to an SFR-relative String. */
    protected final String relativize(Path absolute) {
        Path scenarioRoot = (Path) getProperty("scenario.root");
        return scenarioRoot.relativize(absolute).toString().replace('\\', '/');
    }

    /** Resolve the SFR-relative path back to an absolute Path (for production-code consumers like DarmokMojoMetrics). */
    protected final Path resolveFullPath() {
        Path scenarioRoot = (Path) getProperty("scenario.root");
        return scenarioRoot.resolve(resolveFilePath());
    }

    protected final void createFile(String path) {
        if (path == null) {
            return;
        }
        try {
            if (!sr().contains("", path)) {
                sr().put("", path, "placeholder");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final void deleteFile(String path) {
        if (path == null) {
            return;
        }
        sr().delete("", path);
    }

    protected final void createOrDeleteFile(String path) {
        String stateType = (String) getProperty("stateType");
        if ("isn't".equals(stateType)) {
            deleteFile(path);
        } else {
            createFile(path);
        }
    }

    protected final DarmokMojoLog getDarmokMojoLog(String prefix) {
        String cacheKey = "mojoLog." + prefix;
        Object cached = getProperty(cacheKey);
        if (cached instanceof DarmokMojoLog) {
            return (DarmokMojoLog) cached;
        }
        Path logDir = (Path) getProperty("log.dir");
        if (logDir == null) {
            Object baseDir = getProperty(component + ".baseDir");
            if (baseDir == null) {
                return null;
            }
            logDir = (Path) baseDir;
        }
        DarmokMojoLog mojoLog = new DarmokMojoLog(DarmokMojoLog.findDatedLog(logDir, prefix));
        setProperty(cacheKey, mojoLog);
        return mojoLog;
    }

    protected final String resolveFilePath() {
        Object componentPath = getProperty(component + ".componentPath");
        if (componentPath == null) {
            return null;
        }
        return componentPath + "/" + object;
    }

    protected final String getFileContent(String path) {
        String state = getFileState(path);
        return state != null ? state.replaceAll("\r", "").trim() : null;
    }

    protected final String getFileState(String path) {
        if (path == null) {
            return null;
        }
        SourceFileRepository sr = sr();
        if (!sr.contains("", path)) {
            return null;
        }
        try {
            return sr.get("", path);
        } catch (Exception e) {
            return null;
        }
    }

    protected final void writeFile(String path, String content) {
        if (path == null) {
            return;
        }
        try {
            sr().put("", path, content == null ? "" : content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
