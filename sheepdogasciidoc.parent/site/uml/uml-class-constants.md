# constants

## LANGUAGE_ID constant identifies the language



Unique identifier for the DSL language.

Used to register the language with VS Code.

**Examples**

```typescript
export const LANGUAGE_ID = 'asciidoc'
```

## FILE_EXTENSIONS constant lists supported file extensions



Array of file extensions associated with the language.

VS Code uses these to activate the extension.

**Examples**

```typescript
export const FILE_EXTENSIONS = ['.adoc', '.asciidoc']
```

## COMMAND_IDS constant defines custom command identifiers



Constants for custom commands exposed by the extension.

Used consistently between client and server.

**Examples**

```typescript
export const COMMAND_IDS = {
    FORMAT_DOCUMENT: 'asciidoc.formatDocument',
    VALIDATE: 'asciidoc.validate'
}
```
