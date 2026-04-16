# AbstractFileImpl

**Directory**: `src/test/java/org/farhan/impl`

**Extends**: TestObject

Abstract base for file-backed test object implementations. Provides shared file path resolution, state assertion, and content manipulation. Subclasses inherit these methods and may override resolveFilePath for custom path logic (e.g. dated log files).

## resolveFilePath

**Desc**: Resolves the file path by combining the component's baseDir property with the object path.

**Rule**: ONE method name follows resolveFilePath pattern.
 - **Name**: `^resolveFilePath$`
 - **Return**: `^Path$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected Path resolveFilePath()`

## get{StateDesc}

**Desc**: Returns file state for assertion. getState returns null (Absent), empty string (Empty), or file content (Present). getAsFollows returns the file path string. getPresent/getEmpty/getAbsent delegate to getState.

**Rule**: SOME method names follow get{StateDesc} pattern.
 - **Name**: `^get{StateDesc}$`
 - **Return**: `^String$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public String getState(HashMap<String, String> keyMap)`
 - `public String getAsFollows(HashMap<String, String> keyMap)`
 - `public String getPresent(HashMap<String, String> keyMap)`
 - `public String getEmpty(HashMap<String, String> keyMap)`
 - `public String getAbsent(HashMap<String, String> keyMap)`

## set{StateDesc}

**Desc**: Mutates file state. setCreated creates or deletes based on stateType property. setContent writes keyMap content to file. setCreatedAsFollows is a no-op (delegated to setContent).

**Rule**: SOME method names follow set{StateDesc} pattern.
 - **Name**: `^set{StateDesc}$`
 - **Return**: `^void$`
 - **Parameters**: `^\(HashMap<String,\s*String>\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void setCreated(HashMap<String, String> keyMap)`
 - `public void setCreatedAsFollows(HashMap<String, String> keyMap)`
 - `public void setContent(HashMap<String, String> keyMap)`
