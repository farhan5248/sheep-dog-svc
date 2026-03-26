# serverLauncher

## startServer function launches the Xtext language server



Spawns the Java language server process.

Configures the server connection (stdio or socket).

**Examples**

```typescript
export function startServer(): ChildProcess
// Spawns: java process with language server JAR
// Returns: ChildProcess for server management
```

## getServerOptions function creates LSP server options



Configures how the language client connects to the language server.

Supports stdio communication between VS Code and the Java process.

**Examples**

```typescript
export function getServerOptions(): ServerOptions
// Returns: ServerOptions with command and args
```
