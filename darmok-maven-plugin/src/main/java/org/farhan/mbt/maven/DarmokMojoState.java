package org.farhan.mbt.maven;

import java.util.EnumMap;

public class DarmokMojoState {

	final String scenarioName;
	final String gitBranch;
	final String tag;

	int exitCode;
	long totalDurationMs;
	String commit;

	private final EnumMap<Phase, Long> durations = new EnumMap<>(Phase.class);

	public DarmokMojoState(String scenarioName, String gitBranch, String tag) {
		this.scenarioName = scenarioName;
		this.gitBranch = gitBranch;
		this.tag = tag;
	}

	public void setDuration(Phase phase, long ms) {
		durations.put(phase, ms);
	}

	public long getDuration(Phase phase) {
		return durations.getOrDefault(phase, 0L);
	}

	public static String formatDuration(long millis) {
		long seconds = millis / 1000;
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;
		long secs = seconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, secs);
	}
}
