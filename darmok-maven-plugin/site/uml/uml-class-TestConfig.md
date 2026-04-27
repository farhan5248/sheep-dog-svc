# TestConfig

**Directory**: `src/test/java/org/farhan/impl`

Spring context configuration and Cucumber lifecycle hooks for scenario glue. Creates fresh temp directories per scenario; per-scenario impl methods seed `TestObject.properties` with the per-command fakes (`ClaudeRunnerFake` / `MavenRunnerFake` / `GitRunnerFake`) which the Mojo's runner factories return so no real subprocesses are spawned.

## resetTestProject

**Desc**: Before hook that resets TestObject static state, creates temp directories for code-prj and spec-prj components, and stores their paths as properties. The per-scenario impl method (e.g. `setExecutedAndSucceeds`) is what stashes the per-command fakes into `TestObject.properties` later — this hook just primes the directory layout.

**Rule**: ONE method name follows resetTestProject pattern.
 - **Name**: `^resetTestProject$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void resetTestProject()`

## cleanupTestProject

**Desc**: After hook that deletes the scenario's temp directory tree. No static state to restore — the per-command fakes are scoped to `TestObject.properties`, which is reset per scenario.

**Rule**: ONE method name follows cleanupTestProject pattern.
 - **Name**: `^cleanupTestProject$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void cleanupTestProject()`
