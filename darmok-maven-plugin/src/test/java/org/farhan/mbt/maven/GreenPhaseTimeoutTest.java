package org.farhan.mbt.maven;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * #140 — 15-minute Claude timeout / kill / rollback. Disabled because the production
 * timeout logic does not exist in ClaudeRunner yet; this skeleton exists so pass-2
 * grammar harvesting sees the target spec shape, and so the TDD-red test is ready
 * to go the moment #140 lands its production code.
 * <p>
 * Implementation plan (when #140 comes up):
 * <ul>
 *   <li>ClaudeRunner gains a time-budget parameter (injected or constant).</li>
 *   <li>On waitFor() exceeding the budget, ClaudeRunner calls process.destroyForcibly() and logs a kill marker.</li>
 *   <li>DarmokMojo orchestrates a {@code git reset --hard} rollback in response to the kill marker.</li>
 *   <li>The FakeProcess test double already supports {@code withDelay(Duration)} — just pass 16+ minutes.</li>
 * </ul>
 */
@Disabled("#140 timeout/kill/rollback logic not implemented in ClaudeRunner yet")
class GreenPhaseTimeoutTest {

	@TempDir
	Path workDir;

	/**
	 * === Test-Case: GreenPhase kills claude and rolls back when budget exceeded ===
	 *
	 * <pre>
	 * === Given: The darmok plugin gen-from-existing goal claude /rgr-green command is executed but exceeded the time budget
	 * |===
	 * | Budget   | Behavior
	 * | 15 min   | sleep 16 min, then exit 0
	 * |===
	 *
	 * === When: The darmok plugin gen-from-existing goal green phase is executed
	 *
	 * === Then: The darmok plugin gen-from-existing goal green phase won't be completed
	 *
	 * === Then: The code-prj project target/darmok/darmok.runners.log file will be as follows with this failure
	 * |===
	 * | Level | Category | Content
	 * | ERROR | Claude   | Time budget exceeded — killing subprocess
	 * | INFO  | mojo     | Green: Rolling back via git reset
	 * |===
	 *
	 * === Then: The code-prj project src/main/java/.../<Tag>.java file won't be present
	 * </pre>
	 */
	@Test
	void greenPhase_claudeHangsPastBudget_killsAndRollsBack() throws Exception {
		// Pending #140 production implementation.
	}
}
