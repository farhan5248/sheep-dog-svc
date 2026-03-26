package org.farhan.dsl.asciidoc.impl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.farhan.dsl.grammar.IRow;
import org.farhan.dsl.grammar.ITable;
import org.farhan.dsl.asciidoc.asciiDoc.Row;
import org.farhan.dsl.asciidoc.asciiDoc.Table;

public class TableImpl implements ITable {

	Table eObject;
	private EList<IRow> rowList;
	private Object container;

	public TableImpl(Table table) {
		this.eObject = table;
		rowList = new BasicEList<IRow>();
	}

	@Override
	public EList<IRow> getRowList() {
		rowList.clear();
		for (Row row : eObject.getRowList()) {
			rowList.add(new RowImpl(row));
		}
		return rowList;
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
