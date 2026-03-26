package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.farhan.dsl.asciidoc.asciiDoc.Row;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.TableElements;
import org.farhan.dsl.asciidoc.asciiDoc.Table;

public class TableFormatter extends Formatter {

	private Table theStepTable;

	public TableFormatter(Table theStepTable) {
		this.theStepTable = theStepTable;
	}

	public void format(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		TableElements a = ga.getTableAccess();
		formatKeywordNoSpace(df.getRegion(theStepTable, a.getVerticalLineEqualsSignEqualsSignEqualsSignKeyword_0()),
				doc);
		formatEOL1RuleCall(df.getRegion(theStepTable, a.getEOLTerminalRuleCall_1()), doc);

		for (Row r : theStepTable.getRowList()) {
			RowFormatter formatter = new RowFormatter(r);
			formatter.isLast(isLastElement(r, theStepTable.getRowList()));
			formatter.isFirst(isFirstElement(r, theStepTable.getRowList()));
			formatter.format(doc, ga, df);
		}
		formatKeywordNoSpace(df.getRegion(theStepTable, a.getVerticalLineEqualsSignEqualsSignEqualsSignKeyword_3()),
				doc);
		formatEOL2RuleCall(df.getRegion(theStepTable, a.getEOLTerminalRuleCall_4()), doc);
	}

}
