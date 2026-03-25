package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ICell;
import org.farhan.dsl.asciidoc.asciiDoc.Cell;

public class CellImpl implements ICell {

	Cell eObject;

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

}
