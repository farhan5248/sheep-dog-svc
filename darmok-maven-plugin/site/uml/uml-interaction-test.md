# UML Interaction Patterns

## TestConfig

### resetTestProject

Spring/Cucumber @Before hook that resets TestObject static state, creates fresh temp directories, and stashes a FakeProcess ProcessStarter in TestObject.properties for MavenTestObject.executeMojo to wire into the mojo via runner factories.

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
    ProcessStarter starter = pb -> {
        String cmd = String.join(" ", pb.command());
        if (cmd.contains("diff") && cmd.contains("--cached") && cmd.contains("--quiet")) {
            return new FakeProcess("", 1);
        }
        return new FakeProcess("", 0);
    };
    TestObject.properties.put("processStarter", starter);
}
```

### cleanupTestProject

@After hook that deletes the scenario temp directory tree. No static state to restore — the ProcessStarter lived only in TestObject.properties, which is reset per scenario.

**Example: cleanupTestProject method body**
```java
@After
public void cleanupTestProject() throws IOException {
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

Returns observable state for assertion. Each impl delegates to MavenTestObject helpers. Log impls use getMojoLog(). File impls use getState()/getFileState().

**Example: Log column delegation (DarmokMojoLogFileImpl)**
```java
@Override
public String getLevel(HashMap<String, String> keyMap) {
    return getMojoLog("darmok.mojo").matchAndGetLevel(keyMap);
}
```

**Example: File state delegation (ScenariosListTxtFileImpl)**
```java
@Override
public String getEmpty(HashMap<String, String> keyMap) {
    return getState(keyMap);
}
```

**Example: File content (ProcessDarmokAsciidocFileImpl)**
```java
@Override
public String getContent(HashMap<String, String> keyMap) {
    String content = getState(keyMap);
    return content != null ? content.trim() : null;
}
```

### set{StateDesc}

Mutates state or triggers action. Goal impls buffer parameters then call executeMojo(). File impls delegate to createFile()/writeFile().

**Example: Parameter buffering (GenFromExistingGoalImpl)**
```java
@Override
public void setStage(HashMap<String, String> keyMap) {
    setProperty("stage", keyMap.get("Stage"));
}
```

**Example: Goal execution (GenFromExistingGoalImpl)**
```java
@Override
public void setExecuted(HashMap<String, String> keyMap) {
    executeMojo(GenFromExistingMojo.class);
}
```

**Example: File creation (LogoutHappyPathJavaFileImpl)**
```java
@Override
public void setCreated(HashMap<String, String> keyMap) {
    createFile(resolveFilePath(), (String) getProperty("stateType"));
}
```

**Example: File content write (ScenariosListTxtFileImpl)**
```java
@Override
public void setCreatedWithoutAnyScenarios(HashMap<String, String> keyMap) {
    writeFile(resolveFilePath(), "");
}
```
