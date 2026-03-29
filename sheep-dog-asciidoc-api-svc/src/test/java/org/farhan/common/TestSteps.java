package org.farhan.common;

public class TestSteps {

	protected TestObject object;

	public TestSteps(Object object, String component, String objectName) {
		this.object = (TestObject) object;
		this.object.setComponent(component);
		this.object.setObject(objectName);
	}
}
