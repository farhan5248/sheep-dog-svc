# {Language}IdeModule

## {Language}IdeModule extends Abstract{Language}IdeModule



Extends Xtext-generated Abstract{Language}IdeModule base class.

Configures dependency injection bindings for IDE/LSP services.

**Examples**

```java
public class {Language}IdeModule extends Abstract{Language}IdeModule
```

## {Language}IdeModule binds custom IDE services



Overrides default bindings to provide custom implementations for:
- Content assist (IdeContentProposalProvider)
- Quick fixes (IdeQuickfixProvider)
- Code actions (QuickFixCodeActionService)
- Commands (CommandService)

**Examples**

```java
public Class<? extends IdeContentProposalProvider> bindIdeContentProposalProvider() {
    return {Language}IdeContentProposalProvider.class;
}
```
