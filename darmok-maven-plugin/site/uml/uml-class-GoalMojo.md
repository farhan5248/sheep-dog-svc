# {Goal}Mojo

**Directory**: `src/main/java/org/farhan/mbt/maven`

**Extends**: DarmokMojo

Concrete Maven goal that extends DarmokMojo with a specific scenario generation strategy. Separates goal-specific entry point and generation logic from shared workflow orchestration.

## @Parameter

**Desc**: Goal-specific Maven plugin configuration parameters. GenFromComparisonMojo adds modelComparison.

**Rule**: SOME attribute matches @Parameter pattern.
 - **Name**: `^(modelComparison)$`
 - **Return**: `^String$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String modelComparison`

## execute

**Desc**: Maven goal entry point. Calls init(), runs cleanup, iterates scenarios via getNextScenario/processScenario loop, logs totals, and calls cleanup() in finally block. Goal-specific generation (e.g. runGenFromComparison) runs before or within the scenario loop.

**Rule**: ALL method names follow execute pattern.
 - **Name**: `^execute$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void execute()` (GenFromComparisonMojo)
 - `public void execute()` (GenFromExistingMojo)
