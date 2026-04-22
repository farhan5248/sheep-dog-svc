# Phase

**Directory**: `src/main/java/org/farhan/mbt/maven`

Enum naming the three RGR phases. Used as the key in `DarmokMojoState`'s duration map and as the discriminator returned by `RgrPhase.phase()`. Each constant carries a `displayName` used in log lines and exception messages.

## RED, GREEN, REFACTOR

**Desc**: The three RGR phase identifiers.

**Rule**: ALL constants follow {RGR_PHASE} pattern.
 - **Name**: `^(RED|GREEN|REFACTOR)$`
 - **Modifier**: `^public$`

**Examples**:
 - `RED("Red")`
 - `GREEN("Green")`
 - `REFACTOR("Refactor")`

## displayName

**Desc**: Human-readable phase name (`"Red"` / `"Green"` / `"Refactor"`) used in log line prefixes and exception messages.

**Rule**: ONE attribute matches displayName pattern.
 - **Name**: `^displayName$`
 - **Return**: `^String$`
 - **Modifier**: `^(default|package-private)\s+final$`

**Examples**:
 - `final String displayName`
