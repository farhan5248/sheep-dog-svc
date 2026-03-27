package org.farhan.dsl.asciidoc.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.ILine;
import org.farhan.dsl.asciidoc.asciiDoc.Description;
import org.farhan.dsl.asciidoc.asciiDoc.Line;

public class DescriptionImpl implements IDescription {

	Description eObject;
	private Object container;

	public DescriptionImpl(Description description) {
		this.eObject = description;
	}

	@Override
	public EList<ILine> getLineList() {
		EList<ILine> lineList = new BasicEList<ILine>() {
			@Override
			public boolean add(ILine element) {
				eObject.getLineList().add(((LineImpl) element).eObject);
				return super.add(element);
			}
		};
		for (Line l : eObject.getLineList()) {
			lineList.add(new LineImpl(l));
		}
		return lineList;
	}

	@Override
	public Object getContainer() {
		if (container != null) return container;
		return eObject.eContainer();
	}

	@Override
	public void setContainer(Object value) {
		this.container = value;
	}

}
