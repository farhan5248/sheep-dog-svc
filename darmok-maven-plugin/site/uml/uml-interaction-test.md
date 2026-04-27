# UML Interaction Patterns

## TestConfig

### resetTestProject

Spring/Cucumber @Before hook that resets TestObject static state and creates fresh temp directories. The per-command fakes (`ClaudeRunnerFake` / `MavenRunnerFake` / `GitRunnerFake`) are seeded later by the per-scenario impl method (e.g. `setExecutedAndSucceeds`) — this hook just primes the directory layout.

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
}
```

### cleanupTestProject

@After hook that deletes the scenario temp directory tree. No static state to restore — the per-command fakes are scoped to TestObject.properties, which is reset per scenario.

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

Returns observable state for assertion. Each impl delegates to MavenTestObject helpers. Log impls use `getDarmokMojoLog(<prefix>)` to obtain a read-only DarmokMojoLog and call `matchAndGet{Field}` for per-row sequential matching. File impls use `getFileState(path)` (raw contents, used by `will be absent` / `will be empty` / `will be as follows`) or `getFileContent(path)` (trimmed + CR-stripped, used by `getContent`).

**Example: Log column delegation (DarmokMojoLogFileImpl)**
```java
@Override
public String getLevel(HashMap<String, String> keyMap) {
    return getDarmokMojoLog("darmok.mojo").matchAndGetLevel(keyMap);
}
```

**Example: File state delegation (ScenariosListTxtFileImpl)**
```java
@Override
public String getEmpty(HashMap<String, String> keyMap) {
    return getFileState(resolveFilePath());
}
```

**Example: File content (ProcessDarmokAsciidocFileImpl)**
```java
@Override
public String getContent(HashMap<String, String> keyMap) {
    return getFileContent(resolveFilePath());
}
```

### set{StateDesc}

Mutates state or triggers action. Goal impls buffer parameters, construct the per-command fakes (`ClaudeRunnerFake` / `MavenRunnerFake` / `GitRunnerFake`) from the accumulated properties, then call `executeMojo`. File impls delegate to `createOrDeleteFile(path)` (respects the `stateType` property — creates for `is created`, deletes for `isn't created`) or `writeFile(path, content)`.

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
    setProperty("claude", new ClaudeRunnerFake(properties));
    setProperty("maven", new MavenRunnerFake(properties));
    setProperty("git", new GitRunnerFake(properties));
    executeMojo(GenFromExistingMojo.class);
}
```

**Example: File creation (LogoutHappyPathJavaFileImpl)**
```java
@Override
public void setCreated(HashMap<String, String> keyMap) {
    createOrDeleteFile(resolveFilePath());
}
```

**Example: File content write (ScenariosListTxtFileImpl)**
```java
@Override
public void setCreatedWithoutAnyScenarios(HashMap<String, String> keyMap) {
    writeFile(resolveFilePath(), "");
}
```
