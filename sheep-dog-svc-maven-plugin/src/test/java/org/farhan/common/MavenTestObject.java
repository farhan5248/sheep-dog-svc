package org.farhan.common;

import org.farhan.mbt.maven.MBTMojo;
import org.farhan.mbt.maven.SourceFileRepository;
import org.farhan.runners.surefire.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;

/**
 * {@link TestObject} subclass for svc-maven-plugin Maven plugin tests. Holds
 * the shared source-file repository used by file-backed impls, the Spring-
 * injected connection config used by goal-backed impls, and the file content
 * normalization helper that every impl's {@code getContent(HashMap)} delegates
 * to.
 */
public abstract class MavenTestObject extends TestObject {

    protected SourceFileRepository sr = new SourceFileRepository("target/src-gen");

    @Value("${sheepdog.host:dev.sheepdog.io}")
    private String serverHost;

    @Value("${sheepdog.port:80}")
    private int serverPort;

    @Value("${sheepdog.asciidocPort:0}")
    private int asciidocPort;

    @Value("${sheepdog.cucumberPort:0}")
    private int cucumberPort;

    @Value("${sheepdog.timeout:120000}")
    private int timeout;

    public MavenTestObject() {
        setProperty("tags", "");
    }

    protected final String getObjectExists() {
        try {
            boolean exists = sr.contains("", component + "/" + object);
            return exists ? "true" : null;
        } catch (Exception e) {
            Assertions.fail(e);
            return null;
        }
    }

    protected final void setContent(String docString) {
        try {
            sr.put("", component + "/" + object, docString);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    protected final String getFileContent() {
        try {
            String raw = sr.get("", component + "/" + object);
            return raw == null ? null : raw.replaceAll("\r", "").trim();
        } catch (Exception e) {
            Assertions.fail(e);
            return null;
        }
    }

    protected final void runGoal(String goal, String baseDir) {
        try {
            Class<?> mojoClass = Class.forName(goal);
            MBTMojo mojo = (MBTMojo) mojoClass.getConstructor().newInstance();
            mojo.tags = getProperty("tags").toString();
            mojo.baseDir = baseDir;
            mojo.host = serverHost;
            mojo.port = serverPort;
            mojo.asciidocPort = asciidocPort;
            mojo.cucumberPort = cucumberPort;
            mojo.timeout = timeout;
            if (TestConfig.scenarioId != null) {
                mojo.setScenarioId(TestConfig.scenarioId);
            }
            mojo.execute();
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
