package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ITable;
import org.farhan.dsl.grammar.ITestStep;
import org.farhan.dsl.grammar.IText;
import org.farhan.dsl.grammar.SheepDogUtility;
import org.farhan.dsl.asciidoc.asciiDoc.TestStep;

public class TestStepImpl implements ITestStep {

    TestStep eObject;
    private Object container;

    public TestStepImpl(TestStep testStep) {
        this.eObject = testStep;
    }

    @Override
    public String getFullName() {
        return SheepDogUtility.getTestStepFullName(this);
    }

    @Override
    public void setFullName(String value) {
        throw new UnsupportedOperationException("setFullName(String value) is not implemented");
    }

    @Override
    public ITable getTable() {
        if (eObject.getTable() != null) {
            return new TableImpl(eObject.getTable());
        } else {
            return null;
        }
    }

    @Override
    public IText getText() {
        if (eObject.getText() != null) {
            TextImpl text = new TextImpl(eObject.getText());
            return text;
        } else {
            return null;
        }
    }

    @Override
    public void setTable(ITable value) {
        throw new UnsupportedOperationException("setTable(ITable value) is not implemented");
    }

    @Override
    public void setText(IText value) {
        throw new UnsupportedOperationException("setText(IText value) is not implemented");
    }

    public boolean equals(Object object) {
        return eObject.equals(((TestStepImpl) object).eObject);
    }

    @Override
    public String getStepObjectName() {
        return eObject.getStepObjectName() != null ? eObject.getStepObjectName().trim() : "";
    }

    @Override
    public String getStepDefinitionName() {
        return eObject.getStepDefinitionName() != null ? eObject.getStepDefinitionName().trim() : "";
    }

    @Override
    public void setStepObjectName(String value) {
        eObject.setStepObjectName(value);
    }

    @Override
    public void setStepDefinitionName(String value) {
        eObject.setStepDefinitionName(value);
    }

    @Override
    public Object getContainer() {
        if (container != null) return container;
        if (eObject.eContainer() instanceof org.farhan.dsl.asciidoc.asciiDoc.TestCase) {
            return new TestCaseImpl((org.farhan.dsl.asciidoc.asciiDoc.TestCase) eObject.eContainer());
        } else {
            return new TestSetupImpl((org.farhan.dsl.asciidoc.asciiDoc.TestSetup) eObject.eContainer());
        }
    }

    @Override
    public void setContainer(Object value) {
        this.container = value;
        if (value instanceof TestStepContainerImpl) {
            TestStepContainerImpl parent = (TestStepContainerImpl) value;
            if (!parent.eObject.getTestStepList().contains(this.eObject)) {
                parent.eObject.getTestStepList().add(this.eObject);
            }
        }
    }
}
