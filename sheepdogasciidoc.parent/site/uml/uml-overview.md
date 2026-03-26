# UML Overview

## UML Patterns Referenced

1. `uml-package.md` - Package and class naming patterns
2. `uml-class-{Type}Impl.md` - Wrapper implementation patterns
3. `uml-class-{Language}Logger.md` - Logger wrapper
4. `uml-class-{Language}Validator.md` - Validator check methods and constants
5. `uml-class-{Language}IdeQuickfixProvider.md` - Quick fix methods for Language Server
6. `uml-class-{Language}IdeContentProposalProvider.md` - Content assist complete methods for Language Server
7. `uml-class-{Language}CodeActionService.md` - Code action service for LSP
8. `uml-class-{Language}CommandService.md` - Command execution service
9. `uml-class-extension.md` - VS Code extension entry point
10. `uml-class-serverLauncher.md` - Language server launcher
11. `uml-class-communicationService.md` - Client-server communication
12. `uml-class-configurationService.md` - Configuration management
13. `uml-class-constants.md` - Constants module
14. `uml-communication.md` - Collaboration patterns
15. `uml-interaction.md` - Logging and exception handling

## Technology Preferences Referenced

1. `sheep-dog-main/site/arch/arch-logging.md` - Logging patterns (SLF4J)
2. `sheep-dog-main/site/arch/arch-xtext.md` - General Xtext framework architecture
3. `sheep-dog-main/site/impl/impl-slf4j.md` - slf4J configuration
4. `sheep-dog-main/site/impl/impl-xtext.md` - Xtext domain terminology and pattern variables

## Sub-Projects

List of sub-project directories where these patterns apply:

1. **xtextasciidocplugin** - Contains core packages: impl, validation, formatting, generator, parser
2. **xtextasciidocplugin.ide** - Contains IDE/LSP packages: contentassist, quickfix, codeaction, command
3. **xtextasciidocplugin.vscode** - Contains VS Code extension: extension, serverLauncher, services

## Pattern Variables

See `sheep-dog-main/site/impl/impl-xtext.md` Domain Terminology section for pattern variable definitions.
