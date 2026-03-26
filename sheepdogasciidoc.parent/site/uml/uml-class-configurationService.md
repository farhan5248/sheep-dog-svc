# configurationService

## getConfiguration function reads VS Code settings



Retrieves extension configuration from VS Code workspace settings.

Returns typed configuration values.

**Examples**

```typescript
export function getConfiguration(): ExtensionConfiguration
// Calls: vscode.workspace.getConfiguration(EXTENSION_ID)
// Returns: Configuration object with extension settings
```

## onConfigurationChanged function handles settings updates



Listens for configuration changes and applies updates.

May restart the language server if required settings change.

**Examples**

```typescript
export function onConfigurationChanged(event: vscode.ConfigurationChangeEvent)
// Checks: if relevant settings changed
// Calls: restartServer() if needed
```
