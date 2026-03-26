package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ICell;
import org.farhan.dsl.asciidoc.asciiDoc.Cell;

public class CellImpl implements ICell {

	Cell eObject;
	private Object container;

	public CellImpl(Cell cell) {
		this.eObject = cell;
	}

	@Override
	public String getName() {
		return eObject.getName();
	}

	@Override
	public void setName(String value) {
		eObject.setName(value);
	}

	@Override
	public Object getContainer() {
		if (container != null) return container;
		return new RowImpl((org.farhan.dsl.asciidoc.asciiDoc.Row) eObject.eContainer());
	}

	@Override
	public void setContainer(Object value) {
		this.container = value;
	}

}
