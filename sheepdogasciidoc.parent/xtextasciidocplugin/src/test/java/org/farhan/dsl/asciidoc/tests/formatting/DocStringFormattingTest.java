package org.farhan.dsl.asciidoc.tests.formatting;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.formatter.FormatterTestRequest;
import org.farhan.dsl.asciidoc.tests.AsciiDocInjectorProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InjectionExtension.class)
@InjectWith(AsciiDocInjectorProvider.class)
public class DocStringFormattingTest extends FormattingTest {

	private StringBuilder getExpected() {

		StringBuilder sb = new StringBuilder();
		sb.append("= Test-Suite: This is a test feature\n");
		sb.append("\n");
		sb.append("== Test-Case: Submit a drug claim\n");
		sb.append("\n");
		sb.append("=== Given: The one service, one file is valid\n");
		sb.append("----\n");
		sb.append("Line1 \n");
		sb.append("\n");
		sb.append("\n");
		sb.append("  Line2 \n");
		sb.append("\n");
		sb.append("\n");
		sb.append("----\n");
		sb.append("\n");
		assertNoFeatureErrors(sb);
		return sb;
	}

	@Test
	public void formatDocStringNoIssues() {

		assertFormatted((FormatterTestRequest it) -> {
			it.setToBeFormatted(getExpected().toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatDocStringMoreSpaces() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("= Test-Suite: This is a test feature\n");
			sb.append("\n");
			sb.append("== Test-Case: Submit a drug claim\n");
			sb.append("\n");
			sb.append("=== Given: The one service, one file is valid\n");
			sb.append("\n");
			sb.append("----\n");
			sb.append("Line1 \n");
			sb.append("\n");
			sb.append("\n");
			sb.append("  Line2 \n");
			sb.append("\n");
			sb.append("\n");
			sb.append("----\n");
			sb.append("\n");
			assertNoFeatureErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}
}
