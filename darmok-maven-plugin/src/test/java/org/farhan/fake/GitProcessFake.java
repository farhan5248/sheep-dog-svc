package org.farhan.fake;

import java.util.List;
import java.util.Map;

/**
 * Per-runner fake for the {@code git} CLI. Owns dispatch for diff, rev-parse,
 * status --porcelain, checkout, and commit, plus the phase-aware allowlist
 * counters that drive the {@code claudeCommandPath} single-shot violation pattern.
 * <p>
 * Reads {@link FakeRunnerState#currentPhase()} for green/refactor counter selection
 * and delegates scenarios-list snapshot/restore to the shared state so commit and
 * checkout stay coherent with the porcelain-modified check.
 */
class GitProcessFake {

	private final FakeRunnerState state;
	private final String workspaceState;
	private final String branchCanned;
	private final String claudeCommandPath;
	private final String claudeCommandAttempt;
	private final String claudeCommandPhase;

	private int greenAllowlistChecks = 0;
	private int refactorAllowlistChecks = 0;

	GitProcessFake(Map<String, Object> properties, FakeRunnerState state) {
		this.state = state;
		this.workspaceState = FakeProperties.string(properties, "gitWorkspaceState");
		// Default matches mojo-defaults.properties gitBranch=main so specs that don't
		// configure either side still pass the init-time branch check.
		String cannedBranch = FakeProperties.string(properties, "gitBranchCanned");
		this.branchCanned = cannedBranch == null || cannedBranch.isEmpty() ? "main" : cannedBranch;
		this.claudeCommandPath = FakeProperties.string(properties, "claudeCommandPath");
		this.claudeCommandAttempt = FakeProperties.string(properties, "claudeCommandAttempt");
		String params = FakeProperties.string(properties, "claudeCommandParameters");
		if (params != null && params.contains("/rgr-refactor")) {
			this.claudeCommandPhase = "refactor";
		} else if (params != null && params.startsWith("@") && params.endsWith("green.md")) {
			this.claudeCommandPhase = "green";
		} else {
			this.claudeCommandPhase = null;
		}
	}

	boolean handles(List<String> cmd) {
		if (cmd.isEmpty()) return false;
		return cmd.get(0).toLowerCase().startsWith("git");
	}

	Process start(ProcessBuilder pb) {
		List<String> cmd = pb.command();
		String joined = String.join(" ", cmd);

		if (joined.contains("diff") && joined.contains("--cached") && joined.contains("--quiet")) {
			if ("clean".equals(workspaceState)) {
				return new FakeProcess("", 0);
			}
			return new FakeProcess("", 1);
		}

		if (joined.contains("rev-parse") && joined.contains("--abbrev-ref") && joined.contains("HEAD")) {
			return new FakeProcess(branchCanned, 0);
		}

		if (joined.contains("rev-parse") && joined.contains("HEAD")) {
			return new FakeProcess("abc1234567890abcdef1234567890abcdef12345", 0);
		}

		if (joined.contains("status") && joined.contains("--porcelain")) {
			int count = "green".equals(state.currentPhase()) ? ++greenAllowlistChecks : ++refactorAllowlistChecks;
			StringBuilder out = new StringBuilder();
			if (claudeCommandPath != null
					&& (claudeCommandPhase == null || claudeCommandPhase.equals(state.currentPhase()))
					&& (claudeCommandAttempt == null || String.valueOf(count).equals(claudeCommandAttempt))) {
				out.append(" M ").append(claudeCommandPath);
			}
			if (state.scenariosListIsModified()) {
				if (out.length() > 0) out.append("\n");
				out.append(" M scenarios-list.txt");
			}
			return new FakeProcess(out.toString(), 0);
		}

		if (joined.contains("checkout") && joined.contains("HEAD") && joined.contains("--")) {
			if (cmd.stream().anyMatch(a -> a.endsWith("scenarios-list.txt"))) {
				state.restoreScenariosListFromSnapshot();
			}
			return new FakeProcess("", 0);
		}

		if (cmd.size() >= 2 && "commit".equals(cmd.get(1))) {
			state.refreshScenariosListSnapshot();
			return new FakeProcess("", 0);
		}

		return new FakeProcess("", 0);
	}
}
