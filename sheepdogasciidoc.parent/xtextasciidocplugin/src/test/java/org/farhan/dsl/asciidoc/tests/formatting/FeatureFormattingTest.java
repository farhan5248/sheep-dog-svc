package org.farhan.dsl.asciidoc.tests.formatting;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.formatter.FormatterTestRequest;
import org.farhan.dsl.asciidoc.tests.AsciiDocInjectorProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InjectionExtension.class)
@InjectWith(AsciiDocInjectorProvider.class)
public class FeatureFormattingTest extends FormattingTest {

	private StringBuilder getExpected() {

		StringBuilder sb = new StringBuilder();
		sb.append("= Test-Suite: This is a test feature\n");
		sb.append("\n");
		sb.append("This is the description\n");
		sb.append("This is the second line of the description\n");
		sb.append("\n");
		assertNoFeatureErrors(sb);
		return sb;
	}
	
	private StringBuilder getExpected2() {

		StringBuilder sb = new StringBuilder();
		sb.append("= Test-Suite: This is a test feature\n");
		sb.append("\n");
		sb.append("This is the description\n");
		sb.append("\n");
		sb.append("This is the second line of the description\n");
		sb.append("\n");
		assertNoFeatureErrors(sb);
		return sb;
	}

	@Test
	public void formatFeatureNoIssues() {

		assertFormatted((FormatterTestRequest it) -> {
			it.setToBeFormatted(getExpected().toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatFeatureMoreSpaces() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append(" =  Test-Suite:  This is a test feature \n");
			sb.append(" This is the description \n");
			sb.append(" This is the second line of the description \n");
			assertNoFeatureErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatFeatureFewerSpaces() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("= Test-Suite: This is a test feature\n");
			sb.append("This is the description\n");
			sb.append("This is the second line of the description\n");
			assertNoFeatureErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatFeatureMoreEOL() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("= Test-Suite: This is a test feature\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("This is the description\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("This is the second line of the description\n");
			sb.append("\n");
			sb.append("\n");
			assertNoFeatureErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected2().toString());
		});
	}
}
