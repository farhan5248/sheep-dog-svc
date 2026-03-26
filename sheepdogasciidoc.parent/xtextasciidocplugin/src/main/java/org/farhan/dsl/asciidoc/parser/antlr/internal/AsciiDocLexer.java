package org.farhan.dsl.asciidoc.parser.antlr.internal;

import org.eclipse.xtext.parser.antlr.Lexer;
import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class AsciiDocLexer extends InternalAsciiDocLexer {

	// Used to escape keywords
	private boolean hasNoDelimiter = false;
	private boolean hasConstantDelimiter = false;
	private boolean hasVariableDelimiter = false;

	public AsciiDocLexer() {
	}

	public AsciiDocLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public AsciiDocLexer(CharStream input, RecognizerSharedState state) {
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
		// System.out.println("isKeyword >>>" + s + "<<<");
		return true;
	}

	public void printNextToken() throws MismatchedTokenException {
		int i = 0;
		try {
			String s = "";
			while (!Character.isWhitespace(input.LA(i))) {
				s += Character.toString(input.LA(i));
				i++;
			}
			System.out.println("Token >>>" + s + "<<<");
		} catch (Exception e) {
			System.out.println("Error >>>" + input.LA(i) + "<<<");
		}

	}

	public void mTokensNew() throws RecognitionException {
		// printNextToken();
		if (isKeyword("\n")) {
			hasNoDelimiter = false;
			super.mTokens();
		} else if (isKeyword("|") && !isKeyword("|===")) {
			hasNoDelimiter = true;
			super.mTokens();
		} else if (isKeyword(" ") || isKeyword("\t") || isKeyword("\r")) {
			super.mTokens();
		} else if (hasNoDelimiter) {
			mRULE_WORD();
		} else {
			super.mTokens();
		}
	}

	@Override
	public void mTokens() throws RecognitionException {
		// The general idea for this parser is
		// 1 identify initial keyword
		// 2 greedily handle tokens that follow till end of line

		// Some rules are multi line, like TEXT_BLOCK or a multiline comment if it were
		// implemented.
		// They automatically handle the greedy collection of tokens

		// Other rules are single line, like = or Given
		// These are broken up into 5. Anything after the token
		// 1. is not a WORD like |===, * And or = Test-Suite
		// 2. is anything until a newline like with #
		// 3. is a WORD like Test-Suite. These are list elements which have no delimiter
		// keywords
		// 4. is a WORD or a constant delimiter like |. These are list elements which
		// might have the same delimiter keyword.
		// 5. is a WORD or a variable delimiter like ( file | page). These are list
		// elements which might have one of a set of delimiter keywords.

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
				mT__24();// '|'
			} else {
				mRULE_WORD();
			}
		} else if (hasVariableDelimiter) {
			super.mTokens();
			// single line no collection
		} else if (isKeyword("|===")) {
			mT__23();// '|==='
		} else if (isKeyword("===")) {
			mT__13();// '==='
		} else if (isKeyword("==")) {
			mT__11();// '=='
		} else if (isKeyword("=")) {
			mT__9();// '='
			// single line automatic collection
		} else if (isKeyword("#")) {
			mRULE_SL_COMMENT();
			// single line no delimiter collection
		} else if (isKeyword("Step-Object:")) {
			mT__10();// 'Step-Object:'
			hasNoDelimiter = true;
		} else if (isKeyword("Step-Definition:")) {
			mT__12();// 'Step-Definition:'
			hasNoDelimiter = true;
		} else if (isKeyword("Step-Parameters:")) {
			mT__14();// 'Step-Parameters:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Suite:")) {
			mT__15();// 'Test-Suite:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Setup:")) {
			mT__16();// 'Test-Setup:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Case:")) {
			mT__17();// 'Test-Case:'
			hasNoDelimiter = true;
		} else if (isKeyword("Test-Data:")) {
			mT__18();// 'Test-Data:'
			hasNoDelimiter = true;
			// single line constant delimiter collection
		} else if (isKeyword("|")) {
			mT__24();// '|'
			hasConstantDelimiter = true;
			// single line variable delimiter collection
		} else if (isKeyword("Given:")) {
			mT__19();// 'Given:'
			hasVariableDelimiter = true;
		} else if (isKeyword("When:")) {
			mT__20();// 'When:'
			hasVariableDelimiter = true;
		} else if (isKeyword("Then:")) {
			mT__21();// 'Then:'
			hasVariableDelimiter = true;
		} else if (isKeyword("And:")) {
			mT__22();// 'And:'
			hasVariableDelimiter = true;
			// catch all
		} else {
			mRULE_WORD();
		}
	}
}