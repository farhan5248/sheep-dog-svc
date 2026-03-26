# {Language}IdeContentProposalProvider

## complete{Type}_{Assignment} methods invoke helper complete{Assignment} methods



All `complete{Type}_{Assignment}` methods either invoke a helper method or directly invoke the IssueResolver.

The goal is to avoid duplicating business logic across multiple complete methods.

**Examples**

```java
public void complete{Type}_{Assignment}({TypeClass} model, Assignment assignment, ContentAssistContext context, IIdeContentProposalAcceptor acceptor)
// Calls: complete{Assignment}(model, assignment, context, acceptor)
```

## complete{Assignment} helper methods invoke {Type}IssueResolver.suggest{Assignment} methods



Both helper/complete methods properly delegate to their corresponding `IssueResolver.suggest*` methods.

The pattern ensures that proposal logic is centralized in Resolver classes and reused across content assist and quick fixes.

**Examples**

```java
private void complete{Assignment}({Type} model, Assignment assignment, ContentAssistContext context, IIdeContentProposalAcceptor acceptor)
// Calls: {Type}IssueResolver.suggest{Assignment}Workspace(new {Type}Impl(model))
```
