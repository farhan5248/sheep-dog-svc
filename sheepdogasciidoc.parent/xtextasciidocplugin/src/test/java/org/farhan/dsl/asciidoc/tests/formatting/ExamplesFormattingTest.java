package org.farhan.dsl.asciidoc.tests.formatting;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.formatter.FormatterTestRequest;
import org.farhan.dsl.asciidoc.tests.AsciiDocInjectorProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InjectionExtension.class)
@InjectWith(AsciiDocInjectorProvider.class)
public class ExamplesFormattingTest extends FormattingTest {

	private StringBuilder getExpected() {

		StringBuilder sb = new StringBuilder();
		sb.append("= Test-Suite: This is a test feature\n");
		sb.append("\n");
		sb.append("== Test-Case: Submit a claim\n");
		sb.append("\n");
		sb.append("=== Given: The one service, one file is valid\n");
		sb.append("\n");
		sb.append("=== Test-Data: Describe what this data is about\n");
		sb.append("@tag1\n");
		sb.append("Basic EDI claim\n");
		sb.append("No deductible\n");
		sb.append("\n");
		sb.append("|===\n");
		sb.append("| Header1 | Header2\n");
		sb.append("| data1   | data2  \n");
		sb.append("|===\n");
		sb.append("\n");
		assertNoFeatureErrors(sb);
		return sb;
	}

	@Test
	public void formatExamplesNoIssues() {

		assertFormatted((FormatterTestRequest it) -> {
			it.setToBeFormatted(getExpected().toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatExamplesMoreSpaces() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("= Test-Suite: This is a test feature\n");
			sb.append("\n");
			sb.append("== Test-Case: Submit a claim\n");
			sb.append("\n");
			sb.append("=== Given: The one service, one file is valid\n");
			sb.append("\n");
			sb.append(" ===  Test-Data:  Describe what this data is about \n");
			sb.append("@tag1\n");
			sb.append(" Basic EDI claim \n");
			sb.append(" No deductible \n");
			sb.append(" |=== \n");
			sb.append(" | Header1 | Header2 \n");
			sb.append(" | data1   | data2   \n");
			sb.append(" |=== \n");
			sb.append("\n");
			assertNoFeatureErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatExamplesFewerSpaces() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("= Test-Suite: This is a test feature\n");
			sb.append("\n");
			sb.append("== Test-Case: Submit a claim\n");
			sb.append("\n");
			sb.append("=== Given: The one service, one file is valid\n");
			sb.append("\n");
			sb.append("===Test-Data:Describe what this data is about\n");
			sb.append("@tag1\n");
			sb.append("Basic EDI claim\n");
			sb.append("No deductible\n");
			sb.append("\n");
			sb.append("|===\n");
			sb.append("|Header1 |Header2\n");
			sb.append("|data1 |data2\n");
			sb.append("|===\n");
			sb.append("\n");
			assertNoFeatureErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatExamplesMoreEOL() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("= Test-Suite: This is a test feature\n");
			sb.append("\n");
			sb.append("== Test-Case: Submit a claim\n");
			sb.append("\n");
			sb.append("=== Given: The one service, one file is valid\n");
			sb.append("\n");
			sb.append("=== Test-Data: Describe what this data is about\n");
			sb.append("\n");
			sb.append("@tag1\n");
			sb.append("Basic EDI claim\n");
			sb.append("No deductible\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("|===\n");
			sb.append("\n");
			sb.append("| Header1 | Header2\n");
			sb.append("\n");
			sb.append("| data1   | data2  \n");
			sb.append("\n");
			sb.append("|===\n");
			sb.append("\n");
			sb.append("\n");
			assertNoFeatureErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}
}
