package org.farhan.dsl.asciidoc.ide.contentassist.antlr.internal;

import org.antlr.runtime.*;

@SuppressWarnings("all")
public class AsciiDocContentAssistLexer extends InternalAsciiDocLexer {

	// Used to escape keywords
	private boolean hasNoDelimiter = false;
	private boolean hasConstantDelimiter = false;
	private boolean hasVariableDelimiter = false;

	public AsciiDocContentAssistLexer() {
	}

	public AsciiDocContentAssistLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public AsciiDocContentAssistLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public boolean isKeyword(String s) throws MismatchedTokenException {
		int i = 0;
		while (i < s.length()) {
			input.LA(i + 1);
			if (input.LA(i + 1) != s.charAt(i)) {
				return false;
			}
			i++;
		}
		return true;
	}

	// Content Assist token mapping (different from runtime):
	// T__22 = '='            T__23 = 'Step-Object:'     T__24 = '=='
	// T__25 = 'Step-Definition:'  T__26 = '==='         T__27 = 'Step-Parameters:'
	// T__28 = 'Test-Suite:'       T__29 = 'Test-Setup:'   T__30 = 'Test-Case:'
	// T__31 = 'Test-Data:'        T__32 = 'Given:'        T__33 = 'When:'
	// T__34 = 'Then:'             T__35 = 'And:'
	// T__36 = '|==='             T__37 = '|'

	@Override
	public void mTokens() throws RecognitionException {
		// whitespace
		if (isKeyword(" ") || isKeyword("\t") || isKeyword("\r")) {
			mRULE_WS();
			// multi line greedy automatic collection
		} else if (isKeyword("----")) {
			mRULE_TEXT_BLOCK();
			// delimiter reset
		} else if (isKeyword("\n")) {
			mRULE_EOL();
			hasNoDelimiter = false;
			hasConstantDelimiter = false;
			hasVariableDelimiter = false;
			// handle greedy collection by delimiter
		} else if (hasNoDelimiter) {
			mRULE_WORD();
		} else if (hasConstantDelimiter) {
			if (isKeyword("|")) {
				mT__37(); // '|'
			} else {
				mRULE_WORD();
			}
		} else if (hasVariableDelimiter) {
			super.mTokens();
			// single line no collection
		} else if (isKeyword("|===")) {
			mT__36(); // '|==='
		} else if (isKeyword("===")) {
			mT__26(); // '==='
		} else if (isKeyword("==")) {
			mT__24(); // '=='
		} else if (isKeyword("=")) {
			mT__22(); // '='
			// single line automatic collection
		} else if (isKeyword("#")) {
			mRULE_SL_COMMENT();
			// single line no delimiter collection
		} else if (isKeyword("Step-Object:")) {
			mT__23(); // 'Step-Object:'
			hasNoDelimiter = true;
		} else if (isKeyword("Step-Definition:")) {
			mT__25(); // 'Step-Definition:'
			hasNoDelimiter = true;
		} else if (isKeyword("Step-Parameters:")) {
			mT__27(); // 'Step-Parameters:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Suite:")) {
			mT__28(); // 'Test-Suite:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Setup:")) {
			mT__29(); // 'Test-Setup:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Case:")) {
			mT__30(); // 'Test-Case:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Data:")) {
			mT__31(); // 'Test-Data:'
			hasNoDelimiter = true;
			// single line constant delimiter collection
		} else if (isKeyword("|")) {
			mT__37(); // '|'
			hasConstantDelimiter = true;
			// single line variable delimiter collection
		} else if (isKeyword("Given:")) {
			mT__32(); // 'Given:'
			hasVariableDelimiter = true;
		} else if (isKeyword("When:")) {
			mT__33(); // 'When:'
			hasVariableDelimiter = true;
		} else if (isKeyword("Then:")) {
			mT__34(); // 'Then:'
			hasVariableDelimiter = true;
		} else if (isKeyword("And:")) {
			mT__35(); // 'And:'
			hasVariableDelimiter = true;
		// catch all
		} else {
			mRULE_WORD();
		}
	}
}
