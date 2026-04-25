# {Tool}RunnerFactory

**Directory**: `src/main/java/org/farhan/mbt/maven`

Functional interface (SAM) for constructing a {Tool}Runner. Production wires the runner's public constructor via method reference (`{Tool}Runner::new`); tests substitute a lambda that binds a FakeProcessStarter to the runner's test-seam constructor.

## create

**Desc**: Single abstract method. Takes a Log (and for ClaudeRunnerFactory, additional model/retry/wait/session-id-flag/UUID-supplier parameters mirroring `ClaudeRunner`'s production constructor) and returns a configured runner instance.

**Rule**: ALL method names follow create pattern.
 - **Name**: `^create$`
 - **Return**: `^{Tool}Runner$`
 - **Parameters**: `^\(Log\s+\w+(,\s*(String|int|boolean|Supplier<String>)\s+\w+)*\)$`
 - **Modifier**: `^$`

**Examples**:
 - `GitRunner create(Log log)`
 - `MavenRunner create(Log log)`
 - `ClaudeRunner create(Log log, String model, int maxRetries, int retryWaitSeconds, int maxClaudeSeconds, boolean sessionIdEnabled, Supplier<String> uuidSupplier)`
