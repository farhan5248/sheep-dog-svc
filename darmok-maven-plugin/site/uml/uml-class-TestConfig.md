# TestConfig

**Directory**: `src/test/java/org/farhan/impl`

Spring context configuration and Cucumber lifecycle hooks for scenario glue. Creates fresh temp directories per scenario and wires a FakeProcess ProcessStarter so no real subprocesses are spawned during tests.

## resetTestProject

**Desc**: Before hook that resets TestObject static state, creates temp directories for code-prj and spec-prj components, stores their paths as properties, and installs a FakeProcess-based ProcessStarter.

**Rule**: ONE method name follows resetTestProject pattern.
 - **Name**: `^resetTestProject$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void resetTestProject()`

## cleanupTestProject

**Desc**: After hook that restores the default ProcessStarter and deletes the scenario's temp directory tree.

**Rule**: ONE method name follows cleanupTestProject pattern.
 - **Name**: `^cleanupTestProject$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void cleanupTestProject()`
