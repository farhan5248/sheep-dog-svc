package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ILine;
import org.farhan.dsl.asciidoc.asciiDoc.Line;
import org.farhan.dsl.asciidoc.asciiDoc.StepDefinition;
import org.farhan.dsl.asciidoc.asciiDoc.StepObject;

public class LineImpl implements ILine {

	Line eObject;
	private Object parent;

	public LineImpl(Line line) {
		this.eObject = line;
	}

	@Override
	public String getContent() {
		return eObject.getContent();
	}

	@Override
	public void setContent(String value) {
		this.eObject.setContent(value);
	}

	@Override
	public Object getParent() {
		if (parent == null) {
			if (eObject.eContainer() instanceof org.farhan.dsl.asciidoc.asciiDoc.impl.StepDefinitionImpl) {
				parent = new StepDefinitionImpl((StepDefinition) eObject.eContainer());
			} else if (eObject.eContainer() instanceof org.farhan.dsl.asciidoc.asciiDoc.impl.StepObjectImpl) {
				parent = new StepObjectImpl((StepObject) eObject.eContainer());
			}
		}
		return parent;
	}

}
