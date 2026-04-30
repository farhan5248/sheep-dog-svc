package org.farhan.common;

import org.farhan.mbt.maven.MBTMojo;
import org.farhan.mbt.maven.SourceFileRepository;
import org.farhan.runners.surefire.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * {@link TestObject} subclass for svc-maven-plugin Maven plugin tests. Holds
 * the shared source-file repository used by file-backed impls, the Spring-
 * injected connection config used by goal-backed impls, and the file content
 * normalization helper that every impl's {@code getContent(HashMap)} delegates
 * to.
 */
public abstract class MavenTestObject extends TestObject {

    private static final Logger logger = LoggerFactory.getLogger(MavenTestObject.class);

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
        String key = component + "/" + object;
        try {
            String raw = sr.get("", key);
            if (raw == null) {
                logger.debug("getFileContent null key={}", key);
            }
            return raw == null ? null : raw.replaceAll("\r", "").trim();
        } catch (Exception e) {
            logger.debug("getFileContent failed key={} cause={}", key, e.toString());
            Assertions.fail(e);
            return null;
        }
    }

    protected final void runGoal(String goal, String baseDir) {
        logger.debug("runGoal goal={} baseDir={} host={} port={} tags={}",
                goal, baseDir, serverHost, serverPort, getProperty("tags"));
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
            logger.debug("runGoal goal={} executed successfully", goal);
        } catch (Exception e) {
            logger.debug("runGoal goal={} threw {}: {}", goal, e.getClass().getSimpleName(), e.getMessage());
            Assertions.fail(e);
        }
    }
}
