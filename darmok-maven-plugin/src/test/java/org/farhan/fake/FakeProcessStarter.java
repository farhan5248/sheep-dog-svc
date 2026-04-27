package org.farhan.fake;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.farhan.mbt.maven.ProcessRunner.ProcessStarter;

/**
 * Test-double {@link ProcessStarter} for the darmok plugin's gen-from-existing
 * goal. Acts as a router that dispatches each {@link ProcessBuilder#start()} call
 * to the matching per-runner fake ({@link ClaudeProcessFake} / {@link MvnProcessFake}
 * / {@link GitProcessFake}) by first-arg tool name. Anything unrecognised gets a
 * neutral {@code FakeProcess("", 0)}.
 * <p>
 * Cross-runner state (current TDD phase, scenarios-list snapshot, event-log tap)
 * lives in {@link FakeRunnerState}, shared by all three per-runner fakes so claude
 * setting {@code currentPhase=green} is visible to mvn and git.
 * <p>
 * Phase 1 of issue 330 — pure refactor of the prior monolithic if-chain into
 * per-runner fakes behind this router. Step-defs and impl classes still construct
 * {@code new FakeProcessStarter(properties)}; properties Map keys are unchanged.
 */
public class FakeProcessStarter implements ProcessStarter {

	private final FakeRunnerState state;
	private final ClaudeProcessFake claude;
	private final MvnProcessFake mvn;
	private final GitProcessFake git;

	public FakeProcessStarter(Map<String, Object> properties) {
		Object baseDirObj = properties.get("code-prj.baseDir");
		Path baseDir = baseDirObj instanceof Path ? (Path) baseDirObj : null;
		Object logDirObj = properties.get("log.dir");
		Path eventLogPath = logDirObj instanceof Path p ? p.resolve("mojo.event.log") : null;
		String scenarioName = FakeProperties.string(properties, "scenarioName");
		this.state = new FakeRunnerState(baseDir, eventLogPath, scenarioName);
		this.claude = new ClaudeProcessFake(properties, state);
		this.mvn = new MvnProcessFake(properties, state);
		this.git = new GitProcessFake(properties, state);
	}

	@Override
	public Process start(ProcessBuilder pb) {
		List<String> cmd = pb.command();
		state.appendEventLog(String.join(" ", cmd));

		if (claude.handles(cmd)) return claude.start(pb);
		if (mvn.handles(cmd)) return mvn.start(pb);
		if (git.handles(cmd)) return git.start(pb);
		return new FakeProcess("", 0);
	}

	// Reference so callers see HashMap works — Map interface accepted more broadly.
	public static FakeProcessStarter from(HashMap<String, Object> properties) {
		return new FakeProcessStarter(properties);
	}
}
