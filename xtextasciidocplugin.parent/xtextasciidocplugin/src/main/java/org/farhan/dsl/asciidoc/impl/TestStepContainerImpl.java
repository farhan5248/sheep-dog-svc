package org.farhan.dsl.asciidoc.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.ITestStep;
import org.farhan.dsl.grammar.ITestStepContainer;
import org.farhan.dsl.asciidoc.asciiDoc.TestStep;
import org.farhan.dsl.asciidoc.asciiDoc.TestStepContainer;

public class TestStepContainerImpl implements ITestStepContainer {

    TestStepContainer eObject;
    private Object container;

    public TestStepContainerImpl(TestStepContainer testCase) {
        this.eObject = testCase;
    }

    @Override
    public String getName() {
        return eObject.getName();
    }

    @Override
    public IDescription getDescription() {
        if (eObject.getDescription() != null) {
            return new DescriptionImpl(eObject.getDescription());
        }
        return null;
    }

    @Override
    public EList<ITestStep> getTestStepList() {
        EList<ITestStep> testStepList = new BasicEList<ITestStep>() {
			@Override
			public boolean add(ITestStep element) {
				eObject.getTestStepList().add(((TestStepImpl) element).eObject);
				return super.add(element);
			}
		};
        for (TestStep s : eObject.getTestStepList()) {
            TestStepImpl testStep = new TestStepImpl(s);
            testStepList.add(testStep);
        }
        return testStepList;
    }

    @Override
    public void setName(String value) {
        throw new UnsupportedOperationException("setName(String value) is not implemented");
    }

    @Override
    public void setDescription(IDescription value) {
        throw new UnsupportedOperationException("setDescription(IDescription value) is not implemented");
    }

    @Override
    public Object getContainer() {
        if (container != null) return container;
        return new TestSuiteImpl((org.farhan.dsl.asciidoc.asciiDoc.TestSuite) eObject.eContainer());
    }

    @Override
    public void setContainer(Object value) {
        this.container = value;
        if (value instanceof TestSuiteImpl) {
            TestSuiteImpl parent = (TestSuiteImpl) value;
            if (!parent.eObject.getTestStepContainerList().contains(this.eObject)) {
                parent.eObject.getTestStepContainerList().add(this.eObject);
            }
        }
    }

}
