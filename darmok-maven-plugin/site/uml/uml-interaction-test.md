# UML Interaction Patterns

## TestConfig

### resetTestProject

Spring/Cucumber @Before hook that resets TestObject static state, creates fresh temp directories, and installs FakeProcess ProcessStarter.

**Example: resetTestProject method body**
```java
@Before
public void resetTestProject() throws Exception {
    TestObject.reset();
    Path scenarioRoot = Files.createTempDirectory("darmok-spec-");
    Path codePrjDir = scenarioRoot.resolve("sheep-dog-svc").resolve("code-prj");
    Path specPrjDir = scenarioRoot.resolve("spec-prj");
    Path logDir = scenarioRoot.resolve("logs");
    Files.createDirectories(codePrjDir);
    Files.createDirectories(specPrjDir);
    Files.createDirectories(logDir);
    TestObject.properties.put("scenario.root", scenarioRoot);
    TestObject.properties.put("code-prj.baseDir", codePrjDir);
    TestObject.properties.put("spec-prj.baseDir", specPrjDir);
    TestObject.properties.put("log.dir", logDir);
    System.setProperty("LOG_PATH", logDir.toString());
    ProcessRunner.starter = pb -> {
        String cmd = String.join(" ", pb.command());
        if (cmd.contains("diff") && cmd.contains("--cached") && cmd.contains("--quiet")) {
            return new FakeProcess("", 1);
        }
        return new FakeProcess("", 0);
    };
}
```

### cleanupTestProject

@After hook that restores the default ProcessStarter and deletes the scenario temp directory tree.

**Example: cleanupTestProject method body**
```java
@After
public void cleanupTestProject() throws IOException {
    ProcessRunner.starter = ProcessBuilder::start;
    Path scenarioRoot = (Path) TestObject.properties.get("scenario.root");
    if (scenarioRoot != null && Files.exists(scenarioRoot)) {
        try (var stream = Files.walk(scenarioRoot)) {
            stream.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.delete(p); } catch (IOException ignored) {}
            });
        }
    }
}
```

## {ObjectName}{ObjectType}Impl

### get{StateDesc}

Returns observable state for assertion. File impls delegate to AbstractFileImpl. Log impls delegate to MojoLog. Goal impls are not expected to have getters.

**Example: Log column delegation (DarmokMojoLogFileImpl)**
```java
@Override
public String getLevel(HashMap<String, String> keyMap) {
    return helper().matchAndGetLevel(keyMap);
}
```

**Example: File content read (ProcessDarmokAsciidocFileImpl)**
```java
@Override
public String getContent(HashMap<String, String> keyMap) {
    Path path = resolveFilePath();
    if (path == null || !Files.exists(path)) return null;
    try {
        return Files.readString(path).trim();
    } catch (IOException e) {
        return null;
    }
}
```

### set{StateDesc}

Mutates state or triggers action. Goal impls buffer parameters then execute. File impls delegate to AbstractFileImpl or write content.

**Example: Parameter buffering (GenFromExistingGoalImpl)**
```java
@Override
public void setStage(HashMap<String, String> keyMap) {
    setProperty("Stage", keyMap.get("Stage"));
}
```

**Example: Goal execution (GenFromExistingGoalImpl)**
```java
@Override
public void setExecuted(HashMap<String, String> keyMap) {
    Path codePrjDir = (Path) getProperty("code-prj.baseDir");
    MavenProject project = new MavenProject();
    project.setArtifactId("code-prj");
    GenFromExistingMojo mojo = new GenFromExistingMojo();
    mojo.project = project;
    mojo.setBaseDir(codePrjDir.toString());
    // ... wire @Parameter defaults, apply buffered properties ...
    try {
        mojo.execute();
        setProperty("goal.exception", null);
    } catch (Exception e) {
        setProperty("goal.exception", e);
    }
}
```

**Example: Custom file setup (ScenariosListTxtFileImpl)**
```java
@Override
public void setCreatedWithoutAnyScenarios(HashMap<String, String> keyMap) {
    Path path = resolveFilePath();
    if (path == null) return;
    try {
        Files.createDirectories(path.getParent());
        Files.writeString(path, "");
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

## AbstractFileImpl

### resolveFilePath

Combines component baseDir property with the object path.

**Example: Default path resolution**
```java
protected Path resolveFilePath() {
    Object baseDir = getProperty(component + ".baseDir");
    if (baseDir == null) return null;
    return ((Path) baseDir).resolve(object);
}
```

### get{StateDesc}

Returns file state for assertion. getState returns null (Absent), "" (Empty), or content (Present). getPresent/getEmpty/getAbsent delegate to getState.

**Example: State reading (getState)**
```java
public String getState(HashMap<String, String> keyMap) {
    Path path = resolveFilePath();
    if (path == null || !Files.exists(path)) return null;
    try {
        return Files.readString(path);
    } catch (IOException e) {
        return null;
    }
}
```

### set{StateDesc}

Creates or deletes file based on stateType property.

**Example: Conditional create/delete (setCreated)**
```java
public void setCreated(HashMap<String, String> keyMap) {
    Path path = resolveFilePath();
    if (path == null) return;
    String stateType = (String) getProperty("stateType");
    if ("isn't".equals(stateType)) {
        Files.deleteIfExists(path);
    } else {
        Files.createDirectories(path.getParent());
        if (!Files.exists(path)) {
            Files.writeString(path, "placeholder");
        }
    }
}
```

