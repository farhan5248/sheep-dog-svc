package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.farhan.dsl.asciidoc.asciiDoc.Cell;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.RowElements;
import org.farhan.dsl.asciidoc.asciiDoc.Row;

public class RowFormatter extends Formatter {

	private Row theRow;

	public RowFormatter(Row Row) {
		this.theRow = Row;
	}

	public void format(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		RowElements a = ga.getRowAccess();
		for (Cell c : theRow.getCellList()) {
			CellFormatter formatter = new CellFormatter(c);
			formatter.isLast(isLastElement(c, theRow.getCellList()));
			formatter.isFirst(isFirstElement(c, theRow.getCellList()));
			formatter.format(doc, ga, df);
		}
		formatEOL1RuleCall(df.getRegion(theRow, a.getEOLTerminalRuleCall_1()), doc);
	}

}
