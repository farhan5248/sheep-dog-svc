# extension

## activate function initializes the VS Code extension



Entry point called when VS Code activates the extension.

Creates and starts the language client that connects to the Xtext language server.

**Examples**

```typescript
export function activate(context: vscode.ExtensionContext)
// Creates: LanguageClient instance
// Calls: serverLauncher to start language server
// Registers: disposables with context.subscriptions
```

## deactivate function cleans up the extension



Called when VS Code deactivates the extension.

Stops the language client and cleans up resources.

**Examples**

```typescript
export function deactivate(): Thenable<void> | undefined
// Calls: client.stop() if client exists
```
