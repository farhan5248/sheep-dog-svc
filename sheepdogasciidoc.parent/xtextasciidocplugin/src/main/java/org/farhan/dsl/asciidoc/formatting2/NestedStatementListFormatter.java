package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.asciiDoc.Description;
import org.farhan.dsl.asciidoc.asciiDoc.Line;

public class NestedStatementListFormatter extends Formatter {

	private Description theStatementList;

	public NestedStatementListFormatter(Description theStatementList) {
		this.theStatementList = theStatementList;
	}

	public void format(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		for (Line s : theStatementList.getLineList()) {
			StatementFormatter formatter = new StatementFormatter(s);
			formatter.isLast(isLastElement(s, theStatementList.getLineList()));
			formatter.format(doc, ga, df);
		}
	}
}
