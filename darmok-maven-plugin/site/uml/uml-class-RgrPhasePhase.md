# {RgrPhase}Phase

**Directory**: `src/main/java/org/farhan/mbt/maven`

**Extends**: RgrPhase

Concrete RGR phase. `RedPhase` runs the upstream sheep-dog-svc-maven-plugin goals (asciidoctor-to-uml + uml-to-cucumber-guice), generates a Cucumber suite runner class, and runs `mvn test` — no claude, no verify loop. `GreenPhase` invokes `/rgr-green` and verifies. `RefactorPhase` invokes `/rgr-refactor forward` and verifies.

## {RgrPhase}Phase

**Desc**: Constructor for one concrete phase. Red has its own shape (no claude, plus `specsDir`/`host`/`onlyChanges`/`baseDir`); Green and Refactor share the full claude-driven shape and forward to `super(...)`.

**Rule**: SOME constructor matches {RgrPhase}Phase pattern.
 - **Name**: `^{RgrPhase}Phase$`
 - **Modifier**: `^public$`

**Examples**:
 - `public RedPhase(MavenRunner maven, DarmokMojoLog mojoLog, String baseDir, String specsDir, String host, boolean onlyChanges)`
 - `public GreenPhase(ClaudeRunner claude, MavenRunner maven, GitRunner git, DarmokMojoLog mojoLog, String workingDir, String targetDir, String artifactId, int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds, int maxAllowlistAttempts)`
 - `public RefactorPhase(ClaudeRunner claude, MavenRunner maven, GitRunner git, DarmokMojoLog mojoLog, String workingDir, String targetDir, String artifactId, int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds, int maxAllowlistAttempts)`

## phase

**Desc**: Returns the `Phase` constant identifying this concrete phase.

**Rule**: SOME method names follow phase pattern.
 - **Name**: `^phase$`
 - **Return**: `^Phase$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected Phase phase()` (RedPhase — returns `Phase.RED`)
 - `protected Phase phase()` (GreenPhase — returns `Phase.GREEN`)
 - `protected Phase phase()` (RefactorPhase — returns `Phase.REFACTOR`)

## requiresVerifyLoop

**Desc**: Whether the template should invoke `runVerifyLoop` after `executeClaudeOrMaven`. Red returns false; Green and Refactor return true.

**Rule**: SOME method names follow requiresVerifyLoop pattern.
 - **Name**: `^requiresVerifyLoop$`
 - **Return**: `^boolean$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected boolean requiresVerifyLoop()` (RedPhase — returns false)
 - `protected boolean requiresVerifyLoop()` (GreenPhase — returns true)
 - `protected boolean requiresVerifyLoop()` (RefactorPhase — returns true)

## prepareSession

**Desc**: RefactorPhase-only. Pre-phase hook called by `DarmokMojo.processScenario` **before** `refactorPhase.run(state)` — i.e. outside the `phase_refactor_ms` timing window — so the `/compact` resume and the session-inheritance step don't inflate the refactor metric. No-op when `refactorSessionMode=fresh`. When `refactorSessionMode=continue` (issue #287), copies green's UUID into refactor's `ClaudeRunner` via `setSessionId`, then issues `claude --resume <green-uuid> /compact` to scope refactor's review to the files green just touched.

**Rule**: SOME method names follow prepareSession pattern.
 - **Name**: `^prepareSession$`
 - **Return**: `^void$`
 - **Parameters**: `^\(ClaudeRunner\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public void prepareSession(ClaudeRunner greenClaude) throws Exception` (RefactorPhase)

## executeClaudeOrMaven

**Desc**: Phase-specific work. Red runs the maven goals + generates the runner class + `mvn test`, returning 100 when tests already pass and 0 when they fail. Green calls `claude.run("/rgr-green " + artifactId + " " + state.tag)` then `runTimeoutRecoveryLoop`. Refactor calls `claude.run("/rgr-refactor forward " + artifactId)` then `runTimeoutRecoveryLoop`.

**Rule**: SOME method names follow executeClaudeOrMaven pattern.
 - **Name**: `^executeClaudeOrMaven$`
 - **Return**: `^int$`
 - **Parameters**: `^\(DarmokMojoState\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception` (RedPhase)
 - `protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception` (GreenPhase)
 - `protected int executeClaudeOrMaven(DarmokMojoState state) throws Exception` (RefactorPhase)

