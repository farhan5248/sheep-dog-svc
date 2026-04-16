# UML Package Patterns

## src/main/java/org/farhan/mbt/maven

**Desc**: Maven plugin infrastructure for the Darmok RGR automation tool. Separates goal orchestration (Mojos) from external process execution (Runners) and structured logging, enabling independent testing and extension of each concern.

### DarmokMojo

**Desc**: Abstract base Mojo providing shared lifecycle, scenario iteration, RGR phase orchestration, AsciiDoc tag insertion, and file I/O helpers. Separates common workflow logic from goal-specific entry points.

**Rule**: ONE class matches DarmokMojo pattern

**Regex**: `^DarmokMojo$`
 - `DarmokMojo`

### {Goal}Mojo

**Desc**: Concrete Maven goal that extends DarmokMojo with a specific scenario generation strategy. Separates goal-specific entry point and generation logic from shared workflow orchestration.

**Rule**: SOME class matches {Goal}Mojo pattern

**Regex**: `^{Goal}Mojo$`
 - `GenFromComparisonMojo`
 - `GenFromExistingMojo`

### ProcessRunner

**Desc**: Base class for executing external processes with output streaming and logging. Separates process lifecycle management from command-specific argument construction.

**Rule**: ONE class matches ProcessRunner pattern

**Regex**: `^ProcessRunner$`
 - `ProcessRunner`

### {Tool}Runner

**Desc**: Specialized process runner that extends ProcessRunner with tool-specific command construction. Separates tool-specific command building from shared process execution logic.

**Rule**: SOME class matches {Tool}Runner pattern

**Regex**: `^{Tool}Runner$`
 - `ClaudeRunner`
 - `GitRunner`
 - `MavenRunner`

### MojoLog

**Desc**: Logging decorator that implements Maven's Log interface, delegating to a wrapped Log while simultaneously writing timestamped, categorized entries to a file. Separates structured file logging from Maven's console logging.

**Rule**: ONE class matches MojoLog pattern

**Regex**: `^MojoLog$`
 - `MojoLog`

## src/test/java/org/farhan/impl

**Desc**: Test object implementations that bridge Cucumber step definitions to file system and Maven goal operations. Each impl class handles a specific test artifact type.

### AbstractFileImpl

**Desc**: Abstract base for file-oriented test object implementations. Provides shared file state assertion and content manipulation logic.

**Rule**: ONE class matches AbstractFileImpl pattern

**Regex**: `^AbstractFileImpl$`
 - `AbstractFileImpl`

### {ObjectName}{ObjectType}Impl

**Desc**: Test object implementation that bridges Cucumber step definitions to file system or Maven goal operations. File types assert file state and content; Goal types configure and execute Mojo instances.

**Rule**: SOME class matches {ObjectName}{ObjectType}Impl pattern

**Regex**: `^{ObjectName}{ObjectType}Impl$`
 - `DarmokMojoLogFileImpl`
 - `DarmokRunnersLogFileImpl`
 - `GenFromComparisonGoalImpl`
 - `GenFromExistingGoalImpl`
 - `LoginHappyPathJavaFileImpl`
 - `LogoutHappyPathJavaFileImpl`
 - `ProcessDarmokAsciidocFileImpl`
 - `ScenariosListTxtFileImpl`

### TestConfig

**Desc**: Guice configuration and Cucumber hooks for test dependency injection. Binds generated interfaces to impl classes and provides test lifecycle management.

**Rule**: ONE class matches TestConfig pattern

**Regex**: `^TestConfig$`
 - `TestConfig`
