# TextMate Grammar

## TextMate Grammar defines syntax patterns

TextMate grammars use regex patterns to identify language constructs and assign scope names for coloring.

**Examples**

- ```json
  // syntaxes/asciidoc.tmLanguage.json
  {
      "name": "SheepDog AsciiDoc",
      "scopeName": "source.asciidoc.sheepdog"
  }
  ```

## TextMate Grammar uses begin/end for multi-line contexts

Begin/end blocks create isolated scopes where nested patterns don't apply. See [impl-xtext-vscode.md](sheep-dog-main/site/impl/impl-xtext-vscode.md#context-isolation-with-beginend-blocks) for pattern.

## TextMate Grammar uses captures for sub-token highlighting

Named capture groups apply different scopes to parts of a match. See [impl-xtext-vscode.md](sheep-dog-main/site/impl/impl-xtext-vscode.md#highlighting-parts-of-tokens) for pattern.
