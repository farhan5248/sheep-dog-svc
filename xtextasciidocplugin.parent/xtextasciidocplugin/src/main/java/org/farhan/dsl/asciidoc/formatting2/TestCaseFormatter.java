package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractParserRuleElementFinder;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.TestCaseElements;
import org.farhan.dsl.asciidoc.asciiDoc.TestData;
import org.farhan.dsl.asciidoc.asciiDoc.TestCase;

public class TestCaseFormatter extends TestStepContainerFormatter {

	public TestCaseFormatter(TestCase theTestCase) {
		super(theTestCase);
	}

	@Override
	protected AbstractParserRuleElementFinder getAccess(AsciiDocGrammarAccess ga) {
		return ga.getTestCaseAccess();
	}

	@Override
	protected void formatExamples(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		for (TestData e : ((TestCase) theAbstractScenario).getTestDataList()) {
			TestDataFormatter formatter = new TestDataFormatter(e);
			formatter.format(doc, ga, df);
		}
	}

	@Override
	protected Keyword getKeyword(AbstractParserRuleElementFinder a) {
		return ((TestCaseElements) a).getTestCaseKeyword_1();
	}

	@Override
	protected RuleCall getEOLRuleCall(AbstractParserRuleElementFinder a) {
		return ((TestCaseElements) a).getEOLTerminalRuleCall_3();
	}

	@Override
	protected RuleCall getPhraseRuleCall(AbstractParserRuleElementFinder a) {
		return ((TestCaseElements) a).getNamePhraseParserRuleCall_2_0();
	}

	@Override
	protected Keyword getEqualsKeyword(AbstractParserRuleElementFinder a) {
		return ((TestCaseElements) a).getEqualsSignEqualsSignKeyword_0();
	}

}
