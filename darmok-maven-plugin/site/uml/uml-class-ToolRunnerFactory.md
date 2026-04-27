# {Tool}RunnerFactory

**Directory**: `src/main/java/org/farhan/mbt/maven`

Functional interface (SAM) for constructing a {Tool} implementation. Production wires the runner's public constructor via method reference (`{Tool}Runner::new`) so the factory returns a concrete `{Tool}Runner`; tests substitute a lambda that returns the matching test fake (`{Tool}RunnerFake`). The factory's return type is the **interface** (`Git` / `Maven` / `Claude`) so both production and test paths satisfy it without the Mojo or phases knowing which side is wired in.

## create

**Desc**: Single abstract method. Takes a Log (and for ClaudeRunnerFactory, additional model / maxClaudeSeconds / session-id-flag / UUID-supplier parameters mirroring `ClaudeRunner`'s production constructor — retry/wait parameters live on `DarmokMojo.runClaudeWithRetry` instead of on the runner) and returns a configured implementation.

**Rule**: ALL method names follow create pattern.
 - **Name**: `^create$`
 - **Return**: `^(Claude|Git|Maven)$`
 - **Parameters**: `^\(Log\s+\w+(,\s*(String|int|boolean|Supplier<String>)\s+\w+)*\)$`
 - **Modifier**: `^$`

**Examples**:
 - `Git create(Log log)`
 - `Maven create(Log log)`
 - `Claude create(Log log, String model, int maxClaudeSeconds, boolean sessionIdEnabled, Supplier<String> uuidSupplier)`
