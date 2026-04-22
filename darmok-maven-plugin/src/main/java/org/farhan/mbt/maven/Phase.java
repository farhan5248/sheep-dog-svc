package org.farhan.mbt.maven;

public enum Phase {

	RED("Red"),
	GREEN("Green"),
	REFACTOR("Refactor");

	final String displayName;

	Phase(String displayName) {
		this.displayName = displayName;
	}
}
