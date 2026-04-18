package org.farhan.mbt.maven;

public record PhaseResult(int exitCode, long durationMs) {

	public static String formatDuration(long millis) {
		long seconds = millis / 1000;
		long hours = seconds / 3600;
		long minutes = (seconds % 3600) / 60;
		long secs = seconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, secs);
	}
}
