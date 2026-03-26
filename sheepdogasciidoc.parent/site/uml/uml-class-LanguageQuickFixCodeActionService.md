# {Language}QuickFixCodeActionService

## {Language}QuickFixCodeActionService extends QuickFixCodeActionService

Extends Xtext's QuickFixCodeActionService to handle all quick fixes via Language Server Protocol.

Handles both same-file edits and workspace edits (file creation).

**Examples**

```java
public class {Language}QuickFixCodeActionService extends QuickFixCodeActionService
```

## getCodeActions method handles LSP CodeAction requests

Receives code action requests from the VS Code client.

Iterates through diagnostics and delegates to `getCodeActionsForDiagnostic` for handled diagnostic codes.

**Examples**

```java
@Override
public List<Either<Command, CodeAction>> getCodeActions(Options options)
```

## canHandleDiagnostic checks if a diagnostic code is supported

Returns true for diagnostic codes that have corresponding IssueResolver methods.

**Examples**

```java
private boolean canHandleDiagnostic(Diagnostic diagnostic)
// Returns true for {Language}Validator.{TYPE}_{ASPECT}_ONLY and {TYPE}_{ASPECT}_WORKSPACE codes
```

## getCodeActionsForDiagnostic generates proposals for a diagnostic

Resolves the EObject from the diagnostic position using NodeModelUtils.

Calls the appropriate IssueResolver method based on the diagnostic code.

**Examples**

```java
private List<Either<Command, CodeAction>> getCodeActionsForDiagnostic(Options options, Diagnostic diagnostic)
// Calls: {Type}IssueResolver.correct{Aspect}(new {Type}Impl(eObject))
```

## createCodeActions creates CodeAction objects from proposals

Iterates through proposals and creates CodeActions with WorkspaceEdits.

For same-file edits (`p.getQualifiedName().isEmpty()`): uses `options.getURI()` and `diagnostic.getRange()`.

For workspace edits: creates file with `CreateFile` and adds content with `TextDocumentEdit`.

**Examples**

```java
private List<Either<Command, CodeAction>> createCodeActions(Options options, Diagnostic diagnostic,
        ArrayList<SheepDogIssueProposal> proposals)
```
