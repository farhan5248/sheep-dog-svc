package org.farhan.impl;

import org.junit.jupiter.api.Assertions;

public abstract class TestObjectFileImpl extends TestObjectSheepDogImpl {

	protected SourceFileRepositoryImpl sr = new SourceFileRepositoryImpl();

	protected String getObjectExists() {
		try {
			boolean exists = sr.contains("", object);
			return exists ? "true" : null;
		} catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}

	protected void setContent(String docString) {
		try {
			sr.put("", object, docString);
		} catch (Exception e) {
			Assertions.fail(e);
		}
	}

	protected String getContent() {
		try {
			String contents = sr.get("", object);
			return contents.replaceAll("\r", "").stripTrailing();
		} catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}
}
