package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ILine;
import org.farhan.dsl.asciidoc.asciiDoc.Line;

public class LineImpl implements ILine {

	Line eObject;

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

}
