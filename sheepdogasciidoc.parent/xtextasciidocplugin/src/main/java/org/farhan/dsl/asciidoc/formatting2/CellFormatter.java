package org.farhan.dsl.asciidoc.formatting2;

import java.util.ArrayList;

import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.eclipse.xtext.formatting2.regionaccess.ISemanticRegion;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.CellElements;
import org.farhan.dsl.asciidoc.asciiDoc.Cell;

public class CellFormatter extends Formatter {

	private static ArrayList<ArrayList<ISemanticRegion>> cells;

	// this needs to be here to separate variables tracking the last row from
	// last cell
	protected boolean isLast;
	protected boolean isFirst;
	protected boolean isLastEOLDouble = true;

	private Cell theCell;

	public CellFormatter(Cell theCell) {
		this.theCell = theCell;
	}

	public void isLast(boolean isLast) {
		this.isLast = isLast;
	}

	public void isFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public void format(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		CellElements a = ga.getCellAccess();
		formatKeyword(df.getRegion(theCell, a.getVerticalLineKeyword_0()), doc);
		formatPhraseRuleCall(df.getRegion(theCell, a.getNamePhraseParserRuleCall_1_0()), doc);
	}

	protected void formatKeyword(ISemanticRegion iSR, IFormattableDocument doc) {
		doc.prepend(iSR, it -> it.noSpace());
		doc.append(iSR, it -> it.noSpace());

		if (isFirst) {
			replace(doc, iSR, iSR.getText() + " ");
		} else {
			replace(doc, iSR, " " + iSR.getText() + " ");
		}
	}

	protected void formatPhraseRuleCall(ISemanticRegion iSR, IFormattableDocument doc) {
		if (isFirst) {
			if (RowFormatter.isFirst) {
				cells = new ArrayList<ArrayList<ISemanticRegion>>();
			}
			// add a new row of cells
			cells.add(new ArrayList<ISemanticRegion>());
		}
		// add iSR to last row of list of lists
		cells.get(cells.size() - 1).add(iSR);

		if (isLast && RowFormatter.isLast) {
			// check if it's the last row as well and if so
			formatAllCells(doc);
		}
	}

	private void formatAllCells(IFormattableDocument doc) {
		// go through list of list of iSR. first get the number of columns
		for (int i = 0; i < cells.get(0).size(); i++) {
			ArrayList<ISemanticRegion> col = new ArrayList<ISemanticRegion>();
			int maxWidth = 0;
			for (int j = 0; j < cells.size(); j++) {
				// column by column using their indices
				// make a temp list of column cells and find max width while doing so
				ISemanticRegion tempISR = cells.get(j).get(i);
				col.add(tempISR);
				if (tempISR.getText().length() > maxWidth) {
					maxWidth = tempISR.getText().length();
				}
			}
			// go over temp list again with replacements.
			for (int k = 0; k < col.size(); k++) {

				doc.append(col.get(k), it -> it.noSpace());
				replace(doc, col.get(k), String.format("%1$-" + maxWidth + "s", col.get(k).getText()));
			}
		}
	}

}
