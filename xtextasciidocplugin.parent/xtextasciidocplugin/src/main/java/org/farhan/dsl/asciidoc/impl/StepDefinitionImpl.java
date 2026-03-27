package org.farhan.dsl.asciidoc.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.IStepDefinition;
import org.farhan.dsl.grammar.IStepParameters;
import org.farhan.dsl.asciidoc.asciiDoc.StepDefinition;
import org.farhan.dsl.asciidoc.asciiDoc.StepParameters;

public class StepDefinitionImpl implements IStepDefinition {

    StepDefinition eObject;
    private Object container;

    public StepDefinitionImpl(StepDefinition value) {
        this.eObject = value;
    }

    @Override
    public void setDescription(IDescription value) {
        eObject.setDescription(((DescriptionImpl) value).eObject);
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
    public EList<IStepParameters> getStepParameterList() {
        EList<IStepParameters> list = new BasicEList<IStepParameters>() {
			@Override
			public boolean add(IStepParameters element) {
				eObject.getStepParameterList().add(((StepParametersImpl) element).eObject);
				return super.add(element);
			}
		};
        for (StepParameters t : eObject.getStepParameterList()) {
            StepParametersImpl stepParameters = new StepParametersImpl((StepParameters) t);
            list.add(stepParameters);
        }
        return list;
    }

    @Override
    public void setName(String value) {
        eObject.setName(value);
    }

    @Override
    public Object getContainer() {
        if (container != null) return container;
        return new StepObjectImpl((org.farhan.dsl.asciidoc.asciiDoc.StepObject) eObject.eContainer());
    }

    @Override
    public void setContainer(Object value) {
        this.container = value;
        if (value instanceof StepObjectImpl) {
            StepObjectImpl parent = (StepObjectImpl) value;
            if (!parent.eObject.getStepDefinitionList().contains(this.eObject)) {
                addSorted(parent.eObject.getStepDefinitionList(), this.eObject,
                        org.farhan.dsl.asciidoc.asciiDoc.StepDefinition::getName);
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
