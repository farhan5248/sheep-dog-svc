package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractParserRuleElementFinder;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.GivenElements;
import org.farhan.dsl.asciidoc.asciiDoc.Given;

public class GivenFormatter extends TestStepFormatter {

	public GivenFormatter(Given theStep) {
		super(theStep);
	}

	@Override
	protected AbstractParserRuleElementFinder getAccess(AsciiDocGrammarAccess ga) {
		return ga.getGivenAccess();
	}

	@Override
	protected Keyword getEqualsKeyword(AbstractParserRuleElementFinder a) {
		return ((GivenElements) a).getEqualsSignEqualsSignEqualsSignKeyword_0();
	}

	@Override
	protected Keyword getKeyword(AbstractParserRuleElementFinder a) {
		return ((GivenElements) a).getGivenKeyword_1();
	}

	@Override
	protected RuleCall getEOLRuleCall(AbstractParserRuleElementFinder a) {
		return ((GivenElements) a).getEOLTerminalRuleCall_4();
	}

	@Override
	protected RuleCall getObjectStepObjectRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((GivenElements) a).getStepObjectNameStepObjectRefParserRuleCall_2_0();
	}

	@Override
	protected RuleCall getStepDefinitionNameStepDefinitionRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((GivenElements) a).getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0();
	}

}
