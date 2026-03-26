# {Language}Validator

## {Language}Validator extends Abstract{Language}Validator



Extends Xtext-generated Abstract{Language}Validator base class.

Uses @Check annotations with CheckType (FAST, NORMAL, EXPENSIVE).

Manually created validation methods delegate to {Type}IssueDetector classes.

**Examples**

```java
public class {Language}Validator extends Abstract{Language}Validator
```

## {Language}Validator has check{Type} for every validate{Issue}



Only elements with validation infrastructure (Detector/Types) have check methods.

There should be one check method for each validate method. All check methods use CheckType.FAST regardless of issue type (ONLY, FILE, or WORKSPACE).

**Examples**

```java
@Check(CheckType.FAST)
public void check{Type}{Aspect}({Type} element)
```

## {Language}Validator constants match {Type}IssueTypes enums



The Validator contains constants for issues that have quick fixes.

Missing constants are for warnings/informational messages that don't require fixes.

The pattern is: only create constants for issues that will have @Fix annotations.

**Examples**

```java
public static final String {TYPE}_{ASPECT}_{ISSUE} = "{TYPE}_{ASPECT}_{ISSUE}"
```
