# UML Interaction Patterns

## Java Logging

Logging patterns specific to xtextasciidocplugin that supplement [arch-logging.md](sheep-dog-main/site/arch/arch-logging.md).

**Framework**: VS Code Xtext uses SLF4J directly, which supports parameterized logging with `{}` placeholders.

See [arch-xtext-logging.md](sheep-dog-main/site/arch/arch-xtext-logging.md) "When to Add Logging" section for when to add logging and entry/exit logging requirements.

### Logger Initialization

Standard SLF4J logger initialization in each class:

**Example**
```java
private static final Logger logger = LoggerFactory.getLogger({ClassName}.class);
```

### Entry/Exit Logging Pattern

Methods that delegate to sheep-dog-grammar business logic use entry/exit debug logging.

See [impl-slf4j.md](sheep-dog-main/site/impl/impl-slf4j.md) for entry, exit, and error logging patterns.

### Error Logging Pattern

See [impl-slf4j.md](sheep-dog-main/site/impl/impl-slf4j.md) "Error Logging Pattern" section.

## Java Exceptions

Exception handling for Xtext Language Server integration. See [arch-xtext-logging.md](sheep-dog-main/site/arch/arch-xtext-logging.md) "Xtext IDE Exception Handling" section for rationale and patterns.

## TypeScript Logging

Logging patterns for the VS Code extension (xtextasciidocplugin.vscode).

**Framework**: VS Code OutputChannel API with `asciiDocLogger.ts` helper module

See [arch-xtext-logging.md](sheep-dog-main/site/arch/arch-xtext-logging.md) "VS Code Extension Logging" section for when to add logging.

### Logger Initialization

Use the `createLogger` factory function from `asciiDocLogger.ts`:

**Example**
```typescript
import { createLogger, Logger } from './asciiDocLogger';

// In class:
private logger: Logger;

// In constructor:
this.logger = createLogger(outputChannel, 'ModuleName');
```

### Entry/Exit Logging Pattern

Methods use entry/exit logging with `logger.debug()`:

**Example**
```typescript
this.logger.debug(`Entering startServer for timeout: ${configuration.timeout}`);
// ... method body
this.logger.debug(`Exiting startServer`);
```

See [impl-vscode-outputchannel.md](sheep-dog-main/site/impl/impl-vscode-outputchannel.md) for complete entry, exit, and error logging patterns.

### Error Logging Pattern

Use `logger.error()` for Failed messages:

**Example**
```typescript
const errorMessage = error instanceof Error ? error.message : 'Unknown error';
this.logger.error(`Failed in startServer: ${errorMessage}`);
```

See [impl-vscode-outputchannel.md](sheep-dog-main/site/impl/impl-vscode-outputchannel.md) "Error Logging Pattern" section.

## TypeScript Exceptions

Exception handling for VS Code extension. See [arch-xtext-logging.md](sheep-dog-main/site/arch/arch-xtext-logging.md) "VS Code Extension Exception Handling" section for patterns.
