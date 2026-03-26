# {Language}CommandService

## {Language}CommandService extends IExecutableCommandService

Handles commands sent from VS Code client to the language server.

**Examples**

```java
public class {Language}CommandService implements IExecutableCommandService
```

## execute method handles custom LSP commands

Receives command requests from VS Code and executes the appropriate action.

Commands are registered in the language server capabilities.

**Examples**

```java
@Override
public Object execute(ExecuteCommandParams params, ILanguageServerAccess access, CancelIndicator cancelIndicator)
```

## getCommands method returns supported command IDs

Returns a list of command IDs that this service can handle.

These commands are advertised to the VS Code client.

**Examples**

```java
@Override
public List<String> getCommands()
```
