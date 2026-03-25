# UML Communication Patterns

Collaboration patterns for xtextasciidocplugin IDE integration via Language Server Protocol. Business logic patterns are documented in sheep-dog-grammar/site/uml/uml-communication.md.

## Suggest

This pattern applies when providing content assist proposals for absent grammar elements via LSP. The IdeContentProposalProvider delegates to business logic resolvers.

### {Language}IdeContentProposalProvider

Provides content assist proposals via LSP by delegating to business logic resolvers.

**Methods**
- `complete{Type}_{Assignment}({TypeClass} model, Assignment, ContentAssistContext, IIdeContentProposalAcceptor acceptor)`
- `complete{Assignment}({Type} model, Assignment, ContentAssistContext, IIdeContentProposalAcceptor acceptor)`
- `initProject(Resource resource)`

### {Type}Impl

Wrapper class that adapts EMF objects to business logic interfaces by delegating attribute access.

**Methods**
- `{Type}Impl({Type} eObject)`
- `get{Assignment}()`
- `getParent()`

### {Type}IssueResolver

Business logic class from sheep-dog-grammar (not in xtextasciidocplugin) that suggests alternatives for absent grammar elements.

**Methods**
- `suggest{Assignment}(I{Type} type)`

### {Language}Factory

Singleton from sheep-dog-grammar (not in xtextasciidocplugin) that creates and manages project instances.

**Methods**
- `createTestProject()`

### {Language}IssueProposal

Data container from sheep-dog-grammar (not in xtextasciidocplugin) that holds proposal information for content assist.

**Properties:** id, description, value, qualifiedName

## Validate

This pattern applies when validating existing grammar elements via LSP. The Validator delegates to business logic detectors.

### {Language}Validator

Validates grammar elements in the language server by wrapping EMF objects and delegating to business logic detectors.

**Methods**
- `check{Type}{Aspect}({Type} eObject)`
- `initProject(Resource resource)`
- `logError(Exception e, String name)`

### {Type}Impl

Wrapper class that adapts EMF objects to business logic interfaces by delegating attribute access.

**Methods**
- `{Type}Impl({Type} eObject)`
- `get{Assignment}()`
- `getParent()`

### {Type}IssueDetector

Business logic class from sheep-dog-grammar (not in xtextasciidocplugin) that provides pure validation logic for grammar elements.

**Methods**
- `validate{Aspect}(I{Type} type)`

### {Language}Factory

Singleton from sheep-dog-grammar (not in xtextasciidocplugin) that creates and manages project instances.

**Methods**
- `createTestProject()`

## Correct

This pattern applies when providing quick fixes for invalid grammar elements via LSP. The Validator detects issues and IdeQuickfixProvider provides corrections.

### {Language}Validator

Validates grammar elements in the language server by wrapping EMF objects and delegating to business logic detectors.

**Methods**
- `check{Type}{Aspect}({Type} eObject)`
- `initProject(Resource resource)`
- `logError(Exception e, String name)`

### {Language}IdeQuickfixProvider

Provides quick fix corrections via LSP by delegating to business logic resolvers and applying modifications to EMF objects.

**Methods**
- `fix{Type}{Aspect}(Issue issue, IssueResolutionAcceptor acceptor)`
- `createAcceptor(Issue issue, IssueResolutionAcceptor acceptor, ArrayList<{Language}IssueProposal> proposals)`
- `getEObject(Issue issue)`

### {Language}QuickFixCodeActionService

Bridges LSP CodeAction requests to IdeQuickfixProvider.

**Methods**
- `getCodeActions(ICodeActionService2.Options options)`

### {Type}Impl

Wrapper class that adapts EMF objects to business logic interfaces by delegating attribute access.

**Methods**
- `{Type}Impl({Type} eObject)`
- `get{Assignment}()`
- `getParent()`

### {Type}IssueResolver

Business logic class from sheep-dog-grammar (not in xtextasciidocplugin) that generates corrections for invalid grammar elements.

**Methods**
- `correct{Aspect}(I{Type} type)`

### {Language}Factory

Singleton from sheep-dog-grammar (not in xtextasciidocplugin) that creates and manages project instances.

**Methods**
- `createTestProject()`

### {Language}IssueProposal

Data container from sheep-dog-grammar (not in xtextasciidocplugin) that holds proposal information for quick fixes.

**Properties:** id, description, value, qualifiedName