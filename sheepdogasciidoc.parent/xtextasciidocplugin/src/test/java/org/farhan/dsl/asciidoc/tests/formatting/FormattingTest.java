package org.farhan.dsl.asciidoc.tests.formatting;

import org.eclipse.xtext.formatting2.FormatterPreferenceKeys;
import org.eclipse.xtext.testing.formatter.FormatterTestHelper;
import org.eclipse.xtext.testing.formatter.FormatterTestRequest;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.farhan.dsl.asciidoc.tests.parsing.ParsingTest;

import com.google.inject.Inject;

public class FormattingTest extends ParsingTest {
	@Inject
	protected FormatterTestHelper formatterTestHelper;

	protected void assertFormatted(Procedure1<FormatterTestRequest> init) {
		formatterTestHelper.assertFormatted(new Procedure1<FormatterTestRequest>() {
			@Override
			public void apply(FormatterTestRequest p) {
				p.preferences(m -> m.put(FormatterPreferenceKeys.lineSeparator, "\n"));
				init.apply(p);
			}
		});
	}
}
