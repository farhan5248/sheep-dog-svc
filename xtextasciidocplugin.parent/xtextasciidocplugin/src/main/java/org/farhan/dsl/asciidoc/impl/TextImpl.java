package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ITestStep;
import org.farhan.dsl.grammar.IText;
import org.farhan.dsl.asciidoc.asciiDoc.TestStep;
import org.farhan.dsl.asciidoc.asciiDoc.Text;

public class TextImpl implements IText {

	private TestStepImpl parent;
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

	@Override
	public ITestStep getParent() {
		if (parent == null) {
			parent = new TestStepImpl((TestStep) eObject.eContainer());
		}
		return parent;
	}

}
