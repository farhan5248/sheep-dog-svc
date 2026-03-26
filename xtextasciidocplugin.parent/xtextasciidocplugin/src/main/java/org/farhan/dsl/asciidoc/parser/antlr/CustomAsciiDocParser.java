package org.farhan.dsl.asciidoc.parser.antlr;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.TokenSource;
import org.farhan.dsl.asciidoc.parser.antlr.internal.AsciiDocLexer;

public class CustomAsciiDocParser extends AsciiDocParser {

	@Override
	protected TokenSource createLexer(CharStream stream) {
		return new AsciiDocLexer(stream);
	}
}
