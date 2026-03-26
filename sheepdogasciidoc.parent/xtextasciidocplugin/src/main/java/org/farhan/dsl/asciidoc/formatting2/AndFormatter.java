package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractParserRuleElementFinder;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.AndElements;
import org.farhan.dsl.asciidoc.asciiDoc.And;

public class AndFormatter extends TestStepFormatter {

	public AndFormatter(And theStep) {
		super(theStep);
	}

	@Override
	protected AbstractParserRuleElementFinder getAccess(AsciiDocGrammarAccess ga) {
		return ga.getAndAccess();
	}

	@Override
	protected Keyword getEqualsKeyword(AbstractParserRuleElementFinder a) {
		return ((AndElements) a).getEqualsSignEqualsSignEqualsSignKeyword_0();
	}

	@Override
	protected Keyword getKeyword(AbstractParserRuleElementFinder a) {
		return ((AndElements) a).getAndKeyword_1();
	}

	@Override
	protected RuleCall getEOLRuleCall(AbstractParserRuleElementFinder a) {
		return ((AndElements) a).getEOLTerminalRuleCall_4();
	}

	@Override
	protected RuleCall getObjectStepObjectRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((AndElements) a).getStepObjectNameStepObjectRefParserRuleCall_2_0();
	}

	@Override
	protected RuleCall getStepDefinitionNameStepDefinitionRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((AndElements) a).getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0();
	}

}
