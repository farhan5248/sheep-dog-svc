# {ObjectName}{ObjectType}Impl

**Directory**: `src/test/java/org/farhan/impl`

Test object implementation that bridges Cucumber step definitions to file system or Maven goal operations. File types extend AbstractFileImpl and assert file state/content. Goal types extend TestObject, buffer parameters via setters, then execute the Mojo. Log file types use MojoLog for structured log matching.

## get{StateDesc}

**Desc**: Returns observable state for assertion. File impls delegate to AbstractFileImpl.getState or read content directly. Log file impls delegate to MojoLog.matchAndGet{Field}. Goal impls are not expected to have getters.

**Rule**: SOME method names follow get{StateDesc} pattern.
 - **Name**: `^get{StateDesc}$`
 - **Return**: `^String$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String getCreatedAsFollows(HashMap<String, String> keyMap)` (ProcessDarmokAsciidocFileImpl)
 - `public String getContent(HashMap<String, String> keyMap)` (ProcessDarmokAsciidocFileImpl)
 - `public String getLevel(HashMap<String, String> keyMap)` (DarmokMojoLogFileImpl)
 - `public String getCategory(HashMap<String, String> keyMap)` (DarmokMojoLogFileImpl)
 - `public String getContent(HashMap<String, String> keyMap)` (DarmokMojoLogFileImpl)

## set{StateDesc}

**Desc**: Mutates state or triggers action. File impls delegate to AbstractFileImpl or write content. Goal impls buffer parameters (setStage, setModelGreen, etc.) then execute the Mojo on setExecuted/setExecutedWith.

**Rule**: SOME method names follow set{StateDesc} pattern.
 - **Name**: `^set{StateDesc}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void setExecuted(HashMap<String, String> keyMap)` (GenFromExistingGoalImpl)
 - `public void setExecutedWith(HashMap<String, String> keyMap)` (GenFromComparisonGoalImpl)
 - `public void setStage(HashMap<String, String> keyMap)` (GenFromExistingGoalImpl)
 - `public void setModelGreen(HashMap<String, String> keyMap)` (GenFromExistingGoalImpl)
 - `public void setModelComparison(HashMap<String, String> keyMap)` (GenFromComparisonGoalImpl)
 - `public void setCreatedWithoutAnyScenarios(HashMap<String, String> keyMap)` (ScenariosListTxtFileImpl)
