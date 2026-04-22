# RgrPhase

**Directory**: `src/main/java/org/farhan/mbt/maven`

Abstract base for the three RGR phases. Owns the public `run(DarmokMojoState)` template, the `runVerifyLoop` (`mvn clean verify` + `claude --resume "mvn clean verify failures should be fixed"`), and the `runTimeoutRecoveryLoop` (`mvn clean install` + `claude --resume "pls continue"`). Concrete `{RgrPhase}Phase` subclasses provide the phase-specific work via `executeClaudeOrMaven` and declare whether to run verify via `requiresVerifyLoop`.

## RgrPhase

**Desc**: Constructor that captures the shared collaborators and tunables needed by the verify and timeout-recovery loops. Subclasses pass these through their own constructors. Red passes `null` / `0` for the claude-side parameters since it never calls them.

**Rule**: ONE constructor matches RgrPhase pattern.
 - **Name**: `^RgrPhase$`
 - **Parameters**: `^\(ClaudeRunner\s+\w+,\s*MavenRunner\s+\w+,\s*DarmokMojoLog\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*String\s+\w+,\s*int\s+\w+,\s*int\s+\w+,\s*int\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected RgrPhase(ClaudeRunner claude, MavenRunner maven, DarmokMojoLog mojoLog, String workingDir, String targetDir, String artifactId, int maxVerifyAttempts, int maxTimeoutAttempts, int maxClaudeSeconds)`

## phase

**Desc**: Returns the `Phase` enum constant identifying which RGR phase this concrete subclass implements. Used by the template to set the `displayName` log prefix and to record the duration on `state`.

**Rule**: ONE method name follows phase pattern.
 - **Name**: `^phase$`
 - **Return**: `^Phase$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected\s+abstract$`

**Examples**:
 - `protected abstract Phase phase()`

## executeClaudeOrMaven

**Desc**: Phase-specific work hook called by the template. Returns the exit code that the template then assigns to `state.exitCode` and uses to decide whether to invoke `runVerifyLoop`. Implementations must not mutate `state` directly.

**Rule**: ONE method name follows executeClaudeOrMaven pattern.
 - **Name**: `^executeClaudeOrMaven$`
 - **Return**: `^int$`
 - **Parameters**: `^\(DarmokMojoState\s+\w+\)$`
 - **Modifier**: `^protected\s+abstract$`

**Examples**:
 - `protected abstract int executeClaudeOrMaven(DarmokMojoState state) throws Exception`

## requiresVerifyLoop

**Desc**: Returns whether the template should invoke `runVerifyLoop` after a zero-exit `executeClaudeOrMaven`. Red returns false; Green and Refactor return true.

**Rule**: ONE method name follows requiresVerifyLoop pattern.
 - **Name**: `^requiresVerifyLoop$`
 - **Return**: `^boolean$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected\s+abstract$`

**Examples**:
 - `protected abstract boolean requiresVerifyLoop()`

## run

**Desc**: Public template that orchestrates the phase: log start, time `executeClaudeOrMaven`, log completion, optionally invoke `runVerifyLoop`, then write `exitCode` and the per-phase duration onto `state` and return it.

**Rule**: ONE method name follows run pattern.
 - **Name**: `^run$`
 - **Return**: `^DarmokMojoState$`
 - **Parameters**: `^\(DarmokMojoState\s+\w+\)$`
 - **Modifier**: `^public$`

**Examples**:
 - `public DarmokMojoState run(DarmokMojoState state) throws Exception`

## runTimeoutRecoveryLoop

**Desc**: Recovers from a `ClaudeRunner.TIMEOUT_EXIT_CODE`. Loops `mvn clean install` against `targetDir` — install exit 0 returns 0 (caller proceeds as if claude returned 0); install non-zero invokes `claude.resume(workingDir, "pls continue")` (resume's exit is intentionally ignored — the next install check is authoritative). Bounded by `maxTimeoutAttempts`; on exhaustion throws with `"rgr-<phase> timed out after N attempts"`.

**Rule**: ONE method name follows runTimeoutRecoveryLoop pattern.
 - **Name**: `^runTimeoutRecoveryLoop$`
 - **Return**: `^int$`
 - **Parameters**: `^\(int\s+\w+\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected int runTimeoutRecoveryLoop(int claudeExit) throws Exception`

## runVerifyLoop

**Desc**: Runs `mvn clean verify` against `targetDir`; on non-zero exit, invokes `claude.resume(workingDir, "mvn clean verify failures should be fixed")` and re-runs verify. Bounded by `maxVerifyAttempts`; on exhaustion throws with `"rgr-<phase> verify failed after N attempts"`.

**Rule**: ONE method name follows runVerifyLoop pattern.
 - **Name**: `^runVerifyLoop$`
 - **Return**: `^void$`
 - **Parameters**: `^\(\)$`
 - **Modifier**: `^protected$`

**Examples**:
 - `protected void runVerifyLoop() throws Exception`
