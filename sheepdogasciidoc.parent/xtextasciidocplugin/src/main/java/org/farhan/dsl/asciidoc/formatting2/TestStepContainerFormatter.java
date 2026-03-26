package org.farhan.dsl.asciidoc.formatting2;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractParserRuleElementFinder;
import org.farhan.dsl.asciidoc.services.AsciiDocGrammarAccess;
import org.farhan.dsl.asciidoc.asciiDoc.TestStepContainer;
import org.farhan.dsl.asciidoc.asciiDoc.And;
import org.farhan.dsl.asciidoc.asciiDoc.Given;
import org.farhan.dsl.asciidoc.asciiDoc.Line;
import org.farhan.dsl.asciidoc.asciiDoc.TestStep;
import org.farhan.dsl.asciidoc.asciiDoc.Then;
import org.farhan.dsl.asciidoc.asciiDoc.When;

public abstract class TestStepContainerFormatter extends Formatter {
	protected TestStepContainer theAbstractScenario;

	public TestStepContainerFormatter(TestStepContainer theAbstractScenario) {
		this.theAbstractScenario = theAbstractScenario;
	}

	protected TestStepFormatter newStepFormatter(TestStep theStep) {
		if (theStep instanceof Given) {
			return new GivenFormatter((Given) theStep);
		} else if (theStep instanceof When) {
			return new WhenFormatter((When) theStep);
		} else if (theStep instanceof Then) {
			return new ThenFormatter((Then) theStep);
		} else {
			return new AndFormatter((And) theStep);
		}
	}

	protected abstract AbstractParserRuleElementFinder getAccess(AsciiDocGrammarAccess ga);

	protected abstract void formatExamples(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df);

	protected abstract Keyword getEqualsKeyword(AbstractParserRuleElementFinder a);

	protected abstract Keyword getKeyword(AbstractParserRuleElementFinder a);

	protected abstract RuleCall getEOLRuleCall(AbstractParserRuleElementFinder a);

	protected abstract RuleCall getPhraseRuleCall(AbstractParserRuleElementFinder a);

	public void format(IFormattableDocument doc, AsciiDocGrammarAccess ga, AsciiDocFormatter df) {
		AbstractParserRuleElementFinder a = getAccess(ga);
		formatKeywordTrailingSpace(df.getRegion(theAbstractScenario, getEqualsKeyword(a)), doc);
		formatKeywordTrailingSpace(df.getRegion(theAbstractScenario, getKeyword(a)), doc);
		formatPhrase(df.getRegion(theAbstractScenario, getPhraseRuleCall(a)), doc);
		formatEOL2RuleCall(df.getRegion(theAbstractScenario, getEOLRuleCall(a)), doc);
		if (theAbstractScenario.getDescription() != null) {
			for (Line s : theAbstractScenario.getDescription().getLineList()) {
				StatementFormatter formatter = new StatementFormatter(s);
				formatter.isLast(isLastElement(s, theAbstractScenario.getDescription().getLineList()));
				formatter.format(doc, ga, df);
			}
		}
		for (TestStep s : theAbstractScenario.getTestStepList()) {
			TestStepFormatter formatter = newStepFormatter(s);
			formatter.isLast(isLastElement(s, theAbstractScenario.getTestStepList()));
			// formatter.isLastEOLDouble(s.getTheStepTable() == null && s.getTheDocString() == null);
			formatter.format(doc, ga, df);
		}
		formatExamples(doc, ga, df);
	}
}
