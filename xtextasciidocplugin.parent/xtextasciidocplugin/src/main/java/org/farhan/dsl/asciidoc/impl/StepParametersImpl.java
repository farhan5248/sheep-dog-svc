package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.IStepParameters;
import org.farhan.dsl.grammar.ITable;
import org.farhan.dsl.asciidoc.asciiDoc.StepParameters;

public class StepParametersImpl implements IStepParameters {
    StepParameters eObject;
    private Object container;

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

    @Override
    public Object getContainer() {
        if (container != null) return container;
        return new StepDefinitionImpl((org.farhan.dsl.asciidoc.asciiDoc.StepDefinition) eObject.eContainer());
    }

    @Override
    public void setContainer(Object value) {
        this.container = value;
        if (value instanceof StepDefinitionImpl) {
            StepDefinitionImpl parent = (StepDefinitionImpl) value;
            if (!parent.eObject.getStepParameterList().contains(this.eObject)) {
                addSorted(parent.eObject.getStepParameterList(), this.eObject,
                        org.farhan.dsl.asciidoc.asciiDoc.StepParameters::getName);
            }
        }
    }

    private static <T> void addSorted(java.util.List<T> list, T element,
            java.util.function.Function<T, String> nameExtractor) {
        String name = nameExtractor.apply(element);
        int insertIndex = java.util.Collections.binarySearch(
                list.stream().map(nameExtractor).toList(), name);
        if (insertIndex < 0) {
            insertIndex = -(insertIndex + 1);
        }
        list.add(insertIndex, element);
    }

}
