package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.IStepParameters;
import org.farhan.dsl.grammar.ITable;
import org.farhan.dsl.asciidoc.asciiDoc.StepParameters;

public class StepParametersImpl implements IStepParameters {
    StepParameters eObject;

    public StepParametersImpl(StepParameters stepParameters) {
        eObject = stepParameters;
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
    public ITable getTable() {
        return new TableImpl(eObject.getTable());
    }

    @Override
    public void setName(String value) {
        eObject.setName(value);
    }

    @Override
    public void setTable(ITable value) {
        eObject.setTable(((TableImpl) value).eObject);
    }

    @Override
    public void setDescription(IDescription value) {
        eObject.setDescription(((DescriptionImpl) value).eObject);
    }

}
