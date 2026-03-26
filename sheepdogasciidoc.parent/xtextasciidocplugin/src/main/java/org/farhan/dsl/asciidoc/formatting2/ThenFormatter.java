package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractParserRuleElementFinder;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess.ThenElements;
import org.farhan.dsl.asciidoc.asciiDoc.Then;

public class ThenFormatter extends TestStepFormatter {

	public ThenFormatter(Then theStep) {
		super(theStep);
	}

	@Override
	protected AbstractParserRuleElementFinder getAccess(AsciiDocGrammarAccess ga) {
		return ga.getThenAccess();
	}

	@Override
	protected Keyword getEqualsKeyword(AbstractParserRuleElementFinder a) {
		return ((ThenElements) a).getEqualsSignEqualsSignEqualsSignKeyword_0();
	}

	@Override
	protected Keyword getKeyword(AbstractParserRuleElementFinder a) {
		return ((ThenElements) a).getThenKeyword_1();
	}

	@Override
	protected RuleCall getEOLRuleCall(AbstractParserRuleElementFinder a) {
		return ((ThenElements) a).getEOLTerminalRuleCall_4();
	}

	@Override
	protected RuleCall getObjectStepObjectRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((ThenElements) a).getStepObjectNameStepObjectRefParserRuleCall_2_0();
	}

	@Override
	protected RuleCall getStepDefinitionNameStepDefinitionRefParserRuleCall(AbstractParserRuleElementFinder a) {
		return ((ThenElements) a).getStepDefinitionNameStepDefinitionRefParserRuleCall_3_0();
	}

}
