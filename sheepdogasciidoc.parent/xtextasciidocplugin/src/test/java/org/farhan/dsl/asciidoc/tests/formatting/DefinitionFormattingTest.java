package org.farhan.dsl.asciidoc.tests.formatting;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.formatter.FormatterTestRequest;
import org.farhan.dsl.asciidoc.tests.AsciiDocInjectorProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InjectionExtension.class)
@InjectWith(AsciiDocInjectorProvider.class)
public class DefinitionFormattingTest extends FormattingTest {

	private StringBuilder getExpected() {

		StringBuilder sb = new StringBuilder();
		sb.append("= Step-Object: This is a test StepObject\n");
		sb.append("\n");
		sb.append("== Step-Definition: Submit a claim\n");
		sb.append("\n");
		sb.append("Basic EDI claim\n");
		sb.append("No deductible\n");
		sb.append("\n");
		assertNoStepObjectErrors(sb);
		return sb;
	}

	@Test
	public void formatScenarioNoIssues() {

		assertFormatted((FormatterTestRequest it) -> {
			it.setToBeFormatted(getExpected().toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatScenarioMoreSpaces() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("=  Step-Object: This is a test StepObject\n");
			sb.append("\n");
			sb.append(" ==  Step-Definition:  Submit a claim \n");
			sb.append("\n");
			sb.append(" Basic EDI claim \n");
			sb.append(" No deductible \n");
			sb.append("\n");
			assertNoStepObjectErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatScenarioFewerSpaces() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("=Step-Object:This is a test StepObject\n");
			sb.append("\n");
			sb.append("==Step-Definition:Submit a claim\n");
			sb.append("\n");
			sb.append("Basic EDI claim\n");
			sb.append("No deductible\n");
			sb.append("\n");
			assertNoStepObjectErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}

	@Test
	public void formatScenarioMoreEOL() {

		assertFormatted((FormatterTestRequest it) -> {
			StringBuilder sb = new StringBuilder();
			sb.append("= Step-Object: This is a test StepObject\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("== Step-Definition: Submit a claim\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("Basic EDI claim\n");
			sb.append("No deductible\n");
			sb.append("\n");
			sb.append("\n");
			assertNoStepObjectErrors(sb);
			it.setToBeFormatted(sb.toString());
			it.setExpectation(getExpected().toString());
		});
	}
}
