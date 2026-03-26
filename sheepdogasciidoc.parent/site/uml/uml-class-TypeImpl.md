# {Type}Impl

## {Type}Impl implements I{Type} interface



Wrapper classes implement framework-independent interfaces from org.farhan.dsl.grammar.

Provides bridge pattern between Xtext EMF objects and business logic.

**Examples**

```java
public {Type}Impl({Type} eObject)
public String getName()
public void setName(String value)
public I{Parent} getParent()
public {ReturnType} get{Assignment}()
```

## {Type}Impl wraps EMF eObject



Wrapper delegates to underlying Xtext-generated EMF object for data access.

**Examples**

```java
public String getName()
// Calls: eObject.getName()

public I{Parent} getParent()
// Creates: Parent wrapper instance (lazy initialization)
// Calls: eObject.eContainer()
```
