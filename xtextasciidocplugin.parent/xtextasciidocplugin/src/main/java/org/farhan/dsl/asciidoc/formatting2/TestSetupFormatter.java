package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractParserRuleElementFinder;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.TestSetupElements;
import org.farhan.dsl.asciidoc.asciiDoc.TestSetup;

public class TestSetupFormatter extends TestStepContainerFormatter {

	public TestSetupFormatter(TestSetup theBackground) {
		super(theBackground);
	}

	@Override
	protected AbstractParserRuleElementFinder getAccess(AsciiDocGrammarAccess ga) {
		return ga.getTestSetupAccess();
	}

	@Override
	protected void formatExamples(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		// TestSetup doesn't have Examples
	}

	@Override
	protected Keyword getKeyword(AbstractParserRuleElementFinder a) {
		return ((TestSetupElements) a).getTestSetupKeyword_1();
	}

	@Override
	protected RuleCall getEOLRuleCall(AbstractParserRuleElementFinder a) {
		return ((TestSetupElements) a).getEOLTerminalRuleCall_3();
	}

	@Override
	protected RuleCall getPhraseRuleCall(AbstractParserRuleElementFinder a) {
		return ((TestSetupElements) a).getNamePhraseParserRuleCall_2_0();
	}

	@Override
	protected Keyword getEqualsKeyword(AbstractParserRuleElementFinder a) {
		return ((TestSetupElements) a).getEqualsSignEqualsSignKeyword_0();
	}

}
