/**
 * AsciiDoc Logger - Centralized logging helper for the VS Code extension
 * Follows the {Language}Logger naming pattern
 */

import * as vscode from 'vscode';

/**
 * Logger interface returned by createLogger
 */
export interface Logger {
    debug: (message: string) => void;
    error: (message: string) => void;
}

/**
 * Creates a logger for a specific module with consistent formatting
 *
 * Log format: timestamp DEBUG/ERROR Xtext AsciiDoc Extension: moduleName: message
 * Example: 2026-01-11T10:30:45.123Z DEBUG Xtext AsciiDoc Extension: extension: Entering activate
 *
 * @param outputChannel - VS Code OutputChannel to write logs to
 * @param moduleName - Name of the module (e.g., 'extension', 'ServerLauncher')
 * @returns Logger object with debug() and error() methods
 */
export function createLogger(outputChannel: vscode.OutputChannel, moduleName: string): Logger {
    return {
        debug: (message: string) => {
            const timestamp = new Date().toISOString();
            outputChannel.appendLine(`${timestamp} DEBUG Xtext AsciiDoc Extension: ${moduleName}: ${message}`);
        },
        error: (message: string) => {
            const timestamp = new Date().toISOString();
            outputChannel.appendLine(`${timestamp} ERROR Xtext AsciiDoc Extension: ${moduleName}: ${message}`);
        }
    };
}
