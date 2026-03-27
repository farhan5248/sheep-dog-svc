package org.farhan.dsl.asciidoc.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.farhan.dsl.grammar.ICell;
import org.farhan.dsl.grammar.IRow;
import org.farhan.dsl.asciidoc.asciiDoc.Cell;
import org.farhan.dsl.asciidoc.asciiDoc.Row;

public class RowImpl implements IRow {

	Row eObject;
	private Object container;

	public RowImpl(Row row) {
		this.eObject = row;
	}

	@Override
	public EList<ICell> getCellList() {
		EList<ICell> cells = new BasicEList<ICell>() {
			@Override
			public boolean add(ICell element) {
				eObject.getCellList().add(((CellImpl) element).eObject);
				return super.add(element);
			}
		};
		for (Cell c : eObject.getCellList()) {
			cells.add(new CellImpl(c));
		}
		return cells;
	}

	@Override
	public boolean equals(Object object) {
		return eObject.equals(((RowImpl) object).eObject);
	}

	@Override
	public Object getContainer() {
		if (container != null) return container;
		return new TableImpl((org.farhan.dsl.asciidoc.asciiDoc.Table) eObject.eContainer());
	}

	@Override
	public void setContainer(Object value) {
		this.container = value;
		if (value instanceof TableImpl) {
			TableImpl parent = (TableImpl) value;
			if (!parent.eObject.getRowList().contains(this.eObject)) {
				parent.eObject.getRowList().add(this.eObject);
			}
		}
	}

}
