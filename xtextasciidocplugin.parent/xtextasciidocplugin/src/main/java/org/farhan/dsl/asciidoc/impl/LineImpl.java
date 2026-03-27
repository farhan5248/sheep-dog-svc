package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ILine;
import org.farhan.dsl.asciidoc.asciiDoc.Line;

public class LineImpl implements ILine {

	Line eObject;
	private Object container;

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
	public Object getContainer() {
		if (container != null) return container;
		return new DescriptionImpl((org.farhan.dsl.asciidoc.asciiDoc.Description) eObject.eContainer());
	}

	@Override
	public void setContainer(Object value) {
		this.container = value;
		if (value instanceof DescriptionImpl) {
			DescriptionImpl parent = (DescriptionImpl) value;
			if (!parent.eObject.getLineList().contains(this.eObject)) {
				parent.eObject.getLineList().add(this.eObject);
			}
		}
	}

}
