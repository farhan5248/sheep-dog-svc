package org.farhan.impl;

import org.farhan.mbt.maven.SourceFileRepository;
import org.junit.jupiter.api.Assertions;

public abstract class TestObjectFileImpl extends TestObjectSheepDogImpl {

	protected SourceFileRepository sr = new SourceFileRepository("target/src-gen");

	protected String getObjectExists() {
		try {
			boolean exists = sr.contains("", component + "/" + object);
			return exists ? "true" : null;
		} catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}

	protected void setContent(String docString) {
		try {
			sr.put("", component + "/" + object, docString);
		} catch (Exception e) {
			Assertions.fail(e);
		}
	}

	protected String getContent() {
		try {
			String contents = sr.get("", component + "/" + object);
			return contents.replaceAll("\r", "").trim();
		} catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}
}
