package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.StepParametersElements;
import org.farhan.dsl.asciidoc.asciiDoc.StepParameters;

public class StepParametersFormatter extends Formatter {

	private StepParameters theStepParameters;

	public StepParametersFormatter(StepParameters theStepParameters) {
		this.theStepParameters = theStepParameters;
	}

	public void format(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		StepParametersElements a = ga.getStepParametersAccess();

		formatKeywordTrailingSpace(df.getRegion(theStepParameters, a.getEqualsSignEqualsSignEqualsSignKeyword_0()), doc);
		formatKeywordTrailingSpace(df.getRegion(theStepParameters, a.getStepParametersKeyword_1()), doc);
		formatPhrase(df.getRegion(theStepParameters, a.getNamePhraseParserRuleCall_2_0()), doc);

		if (theStepParameters.getTable() != null || theStepParameters.getDescription() != null) {
			formatEOL1RuleCall(df.getRegion(theStepParameters, a.getEOLTerminalRuleCall_3()), doc);
		} else {
			formatEOL2RuleCall(df.getRegion(theStepParameters, a.getEOLTerminalRuleCall_3()), doc);
		}

		if (theStepParameters.getDescription() != null) {
			NestedStatementListFormatter formatter = new NestedStatementListFormatter(theStepParameters.getDescription());
			formatter.format(doc, ga, df);
		}

		if (theStepParameters.getTable() != null) {
			TableFormatter formatter = new TableFormatter(theStepParameters.getTable());
			formatter.format(doc, ga, df);
		}
	}

}
