# Xtext AsciiDoc Plugin for VS Code

VS Code extension providing language support for Sheep Dog AsciiDoc specifications.

## Overview

This is a multi-project build containing an Xtext-based language server and VS Code extension for editing AsciiDoc specification files. It provides IDE features like syntax highlighting, validation, code completion, and formatting using the Language Server Protocol (LSP).

## Sub-projects

- **xtextasciidocplugin**: Core Xtext grammar and parser for AsciiDoc DSL
- **xtextasciidocplugin.ide**: Language Server Protocol implementation
- **xtextasciidocplugin.vscode**: VS Code extension (TypeScript)

## Key Functionality

- Syntax highlighting with custom color themes
- Real-time validation with error markers
- Code completion and content assist
- Document formatting
- Go to definition and find references
- Document outline and symbol navigation
- Generate code command

## Technology

- Gradle multi-project build
- Eclipse Xtext 2.40
- TypeScript (VS Code extension)
- Language Server Protocol (LSP)
- Node.js 20
