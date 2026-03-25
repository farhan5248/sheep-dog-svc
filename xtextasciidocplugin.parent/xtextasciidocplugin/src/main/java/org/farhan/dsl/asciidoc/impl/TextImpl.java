package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.IText;
import org.farhan.dsl.asciidoc.asciiDoc.Text;

public class TextImpl implements IText {

	Text eObject;

	public TextImpl(Text text) {
		eObject = text;
	}

	@Override
	public String getContent() {
		return eObject.getContent();
	}

	@Override
	public void setContent(String value) {
		eObject.setContent(value);
	}

}
