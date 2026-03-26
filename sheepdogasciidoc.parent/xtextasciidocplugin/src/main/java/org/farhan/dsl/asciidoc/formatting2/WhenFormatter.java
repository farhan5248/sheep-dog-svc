package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractParserRuleElementFinder;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.WhenElements;
import org.farhan.dsl.asciidoc.asciiDoc.When;

public class WhenFormatter extends TestStepFormatter {

	public WhenFormatter(When theStep) {
		super(theStep);
	}

	@Override
	protected AbstractParserRuleElementFinder getAccess(AsciiDocGrammarAccess ga) {
		return ga.getWhenAccess();
	}

	@Override
	protected Keyword getEqualsKeyword(AbstractParserRuleElementFinder a) {
		return ((WhenElements) a).getEqualsSignEqualsSignEqualsSignKeyword_0();
	}

	@Override
	protected Keyword getKeyword(AbstractParserRuleElementFinder a) {
		return ((WhenElements) a).getWhenKeyword_1();
	}

	@Override
	protected RuleCall getEOLRuleCall(AbstractParserRuleElementFinder a) {
		return ((WhenElements) a).getEOLTerminalRuleCall_4();
	}

	@Override
	protected RuleCall getObjectStepObjectRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((WhenElements) a).getStepObjectNameStepObjectRefParserRuleCall_2_0();
	}

	@Override
	protected RuleCall getStepDefinitionNameStepDefinitionRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((WhenElements) a).getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0();
	}

}
