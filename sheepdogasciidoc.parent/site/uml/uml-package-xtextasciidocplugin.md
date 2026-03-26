# UML Package Patterns

## org.farhan.dsl.asciidoc

### {Language}RuntimeModule

**Examples:**
- AsciiDocRuntimeModule

## org.farhan.dsl.asciidoc.impl

### {Type}Impl

- Always has one class per **{Type}** named **{Type}Impl**

**Examples:**
- StatementImpl
- StepDefinitionImpl
- StepObjectImpl
- StepParametersImpl
- TestCaseImpl
- TestProjectImpl
- TestSetupImpl
- TestStepContainerImpl
- TestStepImpl
- TestSuiteImpl
- AsciiDocFactoryImpl

### {IDE}FileRepository

**Examples:**
- VsCodeFileRepository

## org.farhan.dsl.asciidoc.validation

### {Language}Validator
- Contains custom validation class {Language}Validator

**Examples:**
- AsciiDocValidator

## org.farhan.dsl.asciidoc.formatting2

### {Language}Formatter

- Contains {Language}Formatter as main entry point

**Examples:**
- AsciiDocFormatter

### {Type}Formatter

- Contains one {Type}Formatter per grammar type
- Contains TextReplacer for formatting hidden regions
- Uses polymorphic pattern with factory methods for union types
- Manually created to define formatting rules

**Examples:**
- AndFormatter
- CellFormatter
- GivenFormatter
- NestedStatementListFormatter
- RowFormatter
- StatementFormatter
- StepDefinitionFormatter
- StepObjectFormatter
- StepParametersFormatter
- TableFormatter
- TestCaseFormatter
- TestDataFormatter
- TestSetupFormatter
- TestStepContainerFormatter
- TestStepFormatter
- TestSuiteFormatter
- TextFormatter
- ThenFormatter
- WhenFormatter

### Formatter

- Formatter is the base class for **{Type}Formatter**

### TextReplacer

- TextReplacer for formatting hidden regions

## org.farhan.dsl.asciidoc.generator

### {Language}Generator

**Examples:**
- AsciiDocGenerator

### {Language}OutputConfigurationProvider

**Examples:**
- AsciiDocOutputConfigurationProvider

## org.farhan.dsl.asciidoc.parser.antlr

### Custom{Language}Parser

**Examples:**
- CustomAsciiDocParser

## org.farhan.dsl.asciidoc.parser.antlr.internal

### {Language}Lexer

**Examples:**
- AsciiDocLexer
