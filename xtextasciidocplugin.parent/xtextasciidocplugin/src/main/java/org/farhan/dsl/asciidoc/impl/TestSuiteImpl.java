package org.farhan.dsl.asciidoc.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.ITestStepContainer;
import org.farhan.dsl.grammar.ITestSuite;
import org.farhan.dsl.asciidoc.asciiDoc.TestStepContainer;
import org.farhan.dsl.asciidoc.asciiDoc.TestSuite;

public class TestSuiteImpl implements ITestSuite {

	TestSuite eObject;
	private Object container;

	public TestSuiteImpl(TestSuite testSuite) {
		this.eObject = testSuite;
	}

	@Override
	public String getName() {
		return eObject.getName();
	}

	@Override
	public String getFullName() {
		throw new UnsupportedOperationException("getFullName() is not implemented");
	}

	@Override
	public IDescription getDescription() {
		if (eObject.getDescription() != null) {
			return new DescriptionImpl(eObject.getDescription());
		}
		return null;
	}

	@Override
	public EList<ITestStepContainer> getTestStepContainerList() {
		EList<ITestStepContainer> list = new BasicEList<ITestStepContainer>();
		for (TestStepContainer tsc : eObject.getTestStepContainerList()) {
			TestStepContainerImpl tsci = new TestStepContainerImpl(tsc);
			list.add(tsci);
		}
		return list;
	}

	@Override
	public void setName(String value) {
		eObject.setName(value);
	}

	@Override
	public void setFullName(String value) {
		throw new UnsupportedOperationException("setFullName(String value) is not implemented");
	}

	@Override
	public void setDescription(IDescription value) {
		throw new UnsupportedOperationException("setDescription(IDescription value) is not implemented");
	}

	public String getContent() {
		throw new UnsupportedOperationException("getContent() is not implemented");
	}

	public void setContent(String text) {
		throw new UnsupportedOperationException("setContent(String text) is not implemented");
	}

	@Override
	public Object getContainer() {
		if (container != null) return container;
		return org.farhan.dsl.grammar.SheepDogFactory.instance.createTestProject();
	}

	@Override
	public void setContainer(Object value) {
		this.container = value;
	}

}
