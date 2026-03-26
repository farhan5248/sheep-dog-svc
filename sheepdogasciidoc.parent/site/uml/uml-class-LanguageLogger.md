# {Language}Logger

## Logger interface defines debug and error methods

The `{Language}Logger` pattern provides centralized logging for VS Code extensions with consistent formatting.

Log format: `{timestamp} {level} Xtext {Language} Extension: {moduleName}: {message}`

**Examples**

- Logger interface with debug and error methods:
  ```typescript
  export interface Logger {
      debug: (message: string) => void;
      error: (message: string) => void;
  }
  ```

## createLogger factory function returns Logger instance

Creates a Logger bound to a VS Code OutputChannel and module name.

**Examples**

- Factory function signature:
  ```typescript
  export function createLogger(outputChannel: vscode.OutputChannel, moduleName: string): Logger
  ```

## Classes store logger as private field initialized in constructor

Each class creates its logger in the constructor using createLogger with the class name as module name.

**Examples**

- ServerLauncher class with logger field:
  ```typescript
  class ServerLauncher {
      private logger: Logger;

      constructor(outputChannel: vscode.OutputChannel) {
          this.logger = createLogger(outputChannel, 'ServerLauncher');
      }
  }
  ```

## Methods log entry and exit with debug level

All public methods log "Entering {methodName}" at start and "Exiting {methodName}" at end using debug level.

**Examples**

- Entry/exit logging pattern:
  ```typescript
  async startServer(): Promise<void> {
      this.logger.debug('Entering startServer');
      // ...
      this.logger.debug('Exiting startServer');
  }
  ```

## Methods log failures with error level

Catch blocks log "Failed in {methodName}: {errorMessage}" using error level.

**Examples**

- Error logging in catch block:
  ```typescript
  } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      this.logger.error(`Failed in startServer: ${errorMessage}`);
  }
  ```

## Module-level logger uses global variable

Modules without classes use a module-level logger variable initialized in activate function.

**Examples**

- Global logger in extension module:
  ```typescript
  let logger: Logger | undefined;

  export function activate(context: vscode.ExtensionContext): void {
      const outputChannel = vscode.window.createOutputChannel('Extension');
      logger = createLogger(outputChannel, 'extension');
      logger.debug('Entering activate');
  }
  ```