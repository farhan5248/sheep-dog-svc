# UML Overview

## Pattern Variables

Pattern variables used in UML files to represent families of classes following similar patterns.

**Case Sensitivity**: Pattern variable names are case-sensitive and automatically convert values:
- **PascalCase** variable (e.g., `{Tool}`) -> PascalCase values (as stored)
- **lowercase** variable (e.g., `{tool}`) -> camelCase values (auto-converted)
- **UPPERCASE** variable (e.g., `{TOOL}`) -> UPPER_SNAKE_CASE values (auto-converted)

1. **{Tool}** - External tools the agent harness invokes via process runners
   - Values: `(Claude|Git|Maven)`
   - Used in: {Tool}Runner
   - Case variants: `{tool}` (camelCase), `{TOOL}` (UPPER_SNAKE_CASE)

2. **{Goal}** - Maven goal implementations that extend DarmokMojo
   - Values: `(GenFromComparison|GenFromExisting)`
   - Used in: {Goal}Mojo
   - Case variants: `{goal}` (camelCase), `{GOAL}` (UPPER_SNAKE_CASE)

3. **{LogLevel}** - Log levels for tool and harness output
   - Values: `(Debug|Info|Warn|Error)`
   - Used in: is{LogLevel}Enabled(), {logLevel}(CharSequence), {logLevel}(CharSequence, Throwable), {logLevel}(Throwable)
   - Case variants: `{logLevel}` (camelCase), `{LOG_LEVEL}` (UPPER_SNAKE_CASE)

4. **{RgrPhase}** - RGR workflow phases executed by DarmokMojo
   - Values: `(Red|Green|Refactor)`
   - Used in: runRgr{RgrPhase}(), phase logging and commit messages

5. **{ObjectName}** - Test object name from AsciiDoc specs, free-form text identifying the specific test object
   - Values: `.+` (e.g., DarmokMojoLog, DarmokRunnersLog, ScenariosListTxt, ProcessDarmokAsciidoc, LoginHappyPathJava, LogoutHappyPathJava)
   - Used in: {ObjectName}{ObjectType}Impl

6. **{Field}** - Log entry field names returned by LogFileHelper
   - Values: `(Level|Category|Content)`
   - Used in: matchAndGet{Field}()
   - Case variants: `{field}` (camelCase), `{FIELD}` (UPPER_SNAKE_CASE)

7. **{ObjectType}** - Test object type describing the kind of artifact under test
   - Values: `(File|Goal)`
   - Used in: {ObjectName}{ObjectType}Impl
   - Case variants: `{objectType}` (camelCase), `{OBJECT_TYPE}` (UPPER_SNAKE_CASE)
