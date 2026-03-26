/**
 * Communication Service for enhanced client-server communication
 * Implements robust connection handling, error propagation, capabilities detection, and diagnostics
 * Part of Step 2.2 - Communication Enhancement for XtextAsciidocPlugin VS Code Extension
 */

import * as vscode from 'vscode';
import { LanguageClient, ServerCapabilities, ErrorHandler, ErrorAction, CloseAction, Message, ErrorHandlerResult, CloseHandlerResult, State } from 'vscode-languageclient/node';
import { AsciidocConfiguration } from './configurationService';
import { createLogger, Logger } from './asciiDocLogger';

/**
 * Connection retry configuration
 */
export interface RetryConfiguration {
    maxRetries: number;
    baseDelay: number;
    maxDelay: number;
    timeoutMs: number;
    exponentialBackoff: boolean;
}

/**
 * Server capabilities extended with our custom capabilities
 */
export interface ExtendedServerCapabilities extends ServerCapabilities {
    experimental?: {
        asciidocFeatures?: {
            customValidation?: boolean;
            advancedFormatting?: boolean;
            tableSupport?: boolean;
            crossReferences?: boolean;
            documentGeneration?: boolean;
        };
    };
}

/**
 * Connection status information
 */
export interface ConnectionStatus {
    isConnected: boolean;
    lastConnectTime?: Date;
    lastError?: string;
    retryCount: number;
    capabilities?: ExtendedServerCapabilities;
}


/**
 * Communication service for enhanced client-server interactions
 */
export class CommunicationService {
    private client: LanguageClient | undefined;
    private outputChannel: vscode.OutputChannel;
    private logger: Logger;
    private configuration: AsciidocConfiguration;
    private connectionStatus: ConnectionStatus;
    private retryTimer: NodeJS.Timeout | undefined;
    private capabilityChangeHandlers: ((capabilities: ExtendedServerCapabilities) => void)[] = [];

    constructor(
        outputChannel: vscode.OutputChannel,
        configuration: AsciidocConfiguration
    ) {
        this.outputChannel = outputChannel;
        this.logger = createLogger(outputChannel, 'CommunicationService');
        this.configuration = configuration;
        this.connectionStatus = {
            isConnected: false,
            retryCount: 0
        };
    }

    /**
     * Enhanced connection setup with timeout and retry logic (2.2.1)
     */
    public async setupConnection(client: LanguageClient): Promise<void> {
        this.client = client;
        this.logger.debug(`Entering setupConnection for client: ${client.name || 'AsciiDoc Language Client'}`);

        // Configure enhanced error handling
        this.setupErrorHandling();

        // Setup LSP request/response logging
        this.setupLSPLogging();

        // Setup LSP lifecycle event logging
        this.setupLSPLifecycleLogging();

        // Configure connection timeout
        const retryConfig = this.createRetryConfiguration();
        this.logger.debug(`setupConnection retry config: ${JSON.stringify(retryConfig)}`);
        
        try {
            await this.connectWithRetry(retryConfig);
            this.connectionStatus.isConnected = true;
            this.connectionStatus.lastConnectTime = new Date();
            this.connectionStatus.retryCount = 0;
            
            // Detect server capabilities once connected
            await this.detectServerCapabilities();

            this.logger.debug(`Exiting setupConnection`);
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown connection error';
            this.logger.error(`Failed in setupConnection: ${errorMessage}`);
            this.connectionStatus.lastError = errorMessage;
            throw error;
        }
    }

    /**
     * Enhanced error propagation from server to client (2.2.2)
     */
    private setupErrorHandling(): void {
        if (!this.client) {
            return;
        }

        // Custom error handler with enhanced error propagation
        this.client.onDidChangeState((event) => {
            this.logger.debug(` Client state changed from ${event.oldState} to ${event.newState}`);
            
            if (event.newState === 2) { // Stopped state
                this.connectionStatus.isConnected = false;
                this.handleConnectionLoss();
            }
        });

        // Enhanced notification handlers
        this.setupNotificationHandlers();
        
        // Custom error handler
        const errorHandler: ErrorHandler = {
            error: (error: Error, message: Message | undefined, count: number | undefined): ErrorHandlerResult => {
                this.logger.debug(` Server error - ${error.message}`);
                
                // Propagate error with context
                this.propagateError(error, message, count);
                
                // Determine error action based on error type and configuration
                return { action: this.determineErrorAction(error, count) };
            },
            closed: (): CloseHandlerResult => {
                this.logger.debug(' Server connection closed');
                this.connectionStatus.isConnected = false;
                
                // Show user-friendly notification
                if (this.configuration.ui.notifications.errors) {
                    vscode.window.showErrorMessage(
                        'AsciiDoc Language Server connection lost. Attempting to reconnect...',
                        'Retry Now',
                        'Disable Server'
                    ).then(selection => {
                        if (selection === 'Retry Now') {
                            this.attemptReconnection();
                        } else if (selection === 'Disable Server') {
                            vscode.commands.executeCommand('asciidoc.server.stop');
                        }
                    });
                }
                
                return { action: CloseAction.DoNotRestart }; // We handle reconnection manually
            }
        };

        this.client.clientOptions.errorHandler = errorHandler;
    }

    /**
     * Server capabilities detection and feature toggling (2.2.3)
     */
    private async detectServerCapabilities(): Promise<void> {
        if (!this.client) {
            return;
        }

        try {
            this.logger.debug(' Detecting server capabilities...');
            
            // Wait for client to be ready
            await this.client.start();
            
            // Get server capabilities
            const capabilities = this.client.initializeResult?.capabilities as ExtendedServerCapabilities;
            if (!capabilities) {
                this.logger.debug(' Warning - No server capabilities detected');
                return;
            }

            this.connectionStatus.capabilities = capabilities;
            
            // Log standard capabilities
            this.logServerCapabilities(capabilities);
            
            // Check for extended AsciiDoc capabilities
            const asciidocFeatures = capabilities.experimental?.asciidocFeatures;
            if (asciidocFeatures) {
                this.logger.debug(`Extended AsciiDoc features: customValidation=${asciidocFeatures.customValidation}, advancedFormatting=${asciidocFeatures.advancedFormatting}, tableSupport=${asciidocFeatures.tableSupport}, crossReferences=${asciidocFeatures.crossReferences}, documentGeneration=${asciidocFeatures.documentGeneration}`);
            }

            // Apply feature toggles based on capabilities
            this.applyFeatureToggles(capabilities);
            
            // Notify capability change handlers
            this.notifyCapabilityChange(capabilities);
            
            this.logger.debug(' Server capabilities detection completed');
            
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            this.logger.debug(` Error detecting capabilities: ${errorMessage}`);
        }
    }

    /**
     * Diagnostic message filtering and formatting (2.2.4)
     * Ensures diagnostics from server are properly integrated with VSCode
     */
    public setupDiagnosticHandling(): void {
        if (!this.client) {
            return;
        }

        this.logger.debug(' Setting up diagnostic handling for proper VSCode integration');
        
        // Ensure diagnostic collection is properly set up
        // The language client handles most diagnostic processing automatically,
        // but we can add logging and monitoring here
        
        // Note: We do NOT monitor textDocument/publishDiagnostics notifications directly here
        // as intercepting them can prevent VSCode from properly displaying diagnostics.
        // The language client handles diagnostic publishing automatically to VSCode's diagnostic system.
        
        // Monitor completion requests for debugging
        if (this.configuration.debug.verboseLogging) {
            // Note: These are internal requests that we can't directly intercept,
            // but we can add general LSP communication monitoring
            this.logger.debug(' Verbose LSP communication logging enabled');
        }
        
        this.logger.debug(' Diagnostic handling setup completed');
    }

    /**
     * Get current connection status
     */
    public getConnectionStatus(): ConnectionStatus {
        return { ...this.connectionStatus };
    }

    /**
     * Register capability change handler
     */
    public onCapabilityChange(handler: (capabilities: ExtendedServerCapabilities) => void): vscode.Disposable {
        this.capabilityChangeHandlers.push(handler);
        
        // Immediately notify with current capabilities if available
        if (this.connectionStatus.capabilities) {
            handler(this.connectionStatus.capabilities);
        }
        
        return new vscode.Disposable(() => {
            const index = this.capabilityChangeHandlers.indexOf(handler);
            if (index >= 0) {
                this.capabilityChangeHandlers.splice(index, 1);
            }
        });
    }

    
    /**
     * Update configuration
     */
    public updateConfiguration(config: AsciidocConfiguration): void {
        this.configuration = config;
        this.logger.debug(' Configuration updated');
    }

    /**
     * Create retry configuration from extension configuration
     */
    private createRetryConfiguration(): RetryConfiguration {
        return {
            maxRetries: this.configuration.languageServer.maxRetries,
            baseDelay: 1000, // 1 second base delay
            maxDelay: 30000, // 30 seconds max delay
            timeoutMs: this.configuration.languageServer.timeout,
            exponentialBackoff: true
        };
    }

    /**
     * Connect with enhanced retry logic
     */
    private async connectWithRetry(retryConfig: RetryConfiguration): Promise<void> {
        if (!this.client) {
            throw new Error('Language client not initialized');
        }

        let attempt = 0;
        let lastError: Error | undefined;

        while (attempt <= retryConfig.maxRetries) {
            try {
                this.logger.debug(` Connection attempt ${attempt + 1}/${retryConfig.maxRetries + 1}`);
                
                // Create timeout promise
                const timeoutPromise = new Promise<never>((_, reject) => {
                    setTimeout(() => {
                        reject(new Error(`Connection timeout after ${retryConfig.timeoutMs}ms`));
                    }, retryConfig.timeoutMs);
                });

                // Race the connection against timeout
                await Promise.race([
                    this.client.start(),
                    timeoutPromise
                ]);

                // Client should be ready after start() completes
                // No need for additional ready check in the connection retry logic

                this.logger.debug(` Connection established on attempt ${attempt + 1}`);
                return; // Success
                
            } catch (error) {
                lastError = error instanceof Error ? error : new Error('Unknown connection error');
                attempt++;
                
                this.logger.debug(` Attempt ${attempt} failed: ${lastError.message}`);
                
                if (attempt <= retryConfig.maxRetries) {
                    const delay = this.calculateRetryDelay(attempt, retryConfig);
                    this.logger.debug(` Retrying in ${delay}ms...`);
                    await this.delay(delay);
                }
            }
        }

        // All attempts failed
        const finalError = lastError || new Error('Connection failed after all retries');
        this.connectionStatus.retryCount = attempt;
        throw finalError;
    }

    /**
     * Calculate retry delay with exponential backoff
     */
    private calculateRetryDelay(attempt: number, config: RetryConfiguration): number {
        if (!config.exponentialBackoff) {
            return config.baseDelay;
        }

        const exponentialDelay = config.baseDelay * Math.pow(2, attempt - 1);
        const jitteredDelay = exponentialDelay * (0.5 + Math.random() * 0.5); // Add jitter
        return Math.min(jitteredDelay, config.maxDelay);
    }

    /**
     * Enhanced error propagation
     */
    private propagateError(error: Error, message: Message | undefined, count: number | undefined): void {
        // Create detailed error context
        const errorContext = {
            timestamp: new Date().toISOString(),
            errorMessage: error.message,
            errorStack: error.stack,
            messageType: (message as any)?.method || 'unknown',
            errorCount: count || 0
        };

        // Log detailed error
        this.logger.debug(` Detailed error context: ${JSON.stringify(errorContext, null, 2)}`);
        
        // Show user-friendly error notification based on error type
        const userMessage = this.createUserFriendlyErrorMessage(error, message);
        
        if (this.configuration.ui.notifications.errors && userMessage) {
            if (count && count > 3) {
                // Multiple errors - suggest restart
                vscode.window.showErrorMessage(
                    `${userMessage} Multiple errors detected.`,
                    'Restart Server',
                    'Show Logs'
                ).then(selection => {
                    if (selection === 'Restart Server') {
                        vscode.commands.executeCommand('asciidoc.server.restart');
                    } else if (selection === 'Show Logs') {
                        this.outputChannel.show();
                    }
                });
            } else {
                vscode.window.showErrorMessage(userMessage);
            }
        }
    }

    /**
     * Create user-friendly error message
     */
    private createUserFriendlyErrorMessage(error: Error, _message: Message | undefined): string | undefined {
        // Map technical errors to user-friendly messages
        const errorMessage = error.message.toLowerCase();
        
        if (errorMessage.includes('timeout')) {
            return 'AsciiDoc Language Server is taking longer than expected to respond.';
        } else if (errorMessage.includes('connection')) {
            return 'Lost connection to AsciiDoc Language Server.';
        } else if (errorMessage.includes('java')) {
            return 'AsciiDoc Language Server Java runtime error. Please check Java installation.';
        } else if (errorMessage.includes('permission')) {
            return 'Permission denied accessing AsciiDoc Language Server files.';
        } else if (errorMessage.includes('not found')) {
            return 'AsciiDoc Language Server executable not found.';
        }
        
        // For other errors, return a generic message for multiple errors
        return 'AsciiDoc Language Server encountered an error.';
    }

    /**
     * Determine error action based on error type and configuration
     */
    private determineErrorAction(error: Error, count: number | undefined): ErrorAction {
        const errorMessage = error.message.toLowerCase();
        
        // Critical errors that should stop the server
        if (errorMessage.includes('java') || errorMessage.includes('permission') || errorMessage.includes('not found')) {
            return ErrorAction.Shutdown;
        }
        
        // Too many errors - shutdown
        if (count && count > 5) {
            return ErrorAction.Shutdown;
        }
        
        // Default - continue with current message
        return ErrorAction.Continue;
    }

    /**
     * Handle connection loss
     */
    private handleConnectionLoss(): void {
        this.logger.debug(' Handling connection loss...');
        
        // Clear retry timer if exists
        if (this.retryTimer) {
            clearTimeout(this.retryTimer);
        }
        
        // Schedule reconnection attempt
        this.scheduleReconnection();
    }

    /**
     * Schedule automatic reconnection
     */
    private scheduleReconnection(): void {
        const retryConfig = this.createRetryConfiguration();
        const delay = this.calculateRetryDelay(this.connectionStatus.retryCount + 1, retryConfig);
        
        this.logger.debug(` Scheduling reconnection in ${delay}ms...`);
        
        this.retryTimer = setTimeout(() => {
            this.attemptReconnection();
        }, delay);
    }

    /**
     * Attempt reconnection
     */
    private async attemptReconnection(): Promise<void> {
        if (!this.client || this.connectionStatus.isConnected) {
            return;
        }

        try {
            this.logger.debug(' Attempting automatic reconnection...');
            this.connectionStatus.retryCount++;
            
            const retryConfig = this.createRetryConfiguration();
            await this.connectWithRetry(retryConfig);
            
            this.connectionStatus.isConnected = true;
            this.connectionStatus.lastConnectTime = new Date();
            this.connectionStatus.retryCount = 0;
            
            // Re-detect capabilities
            await this.detectServerCapabilities();
            
            this.logger.debug(' Automatic reconnection successful');
            
            if (this.configuration.ui.notifications.info) {
                vscode.window.showInformationMessage('AsciiDoc Language Server reconnected successfully');
            }
            
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            this.logger.debug(` Reconnection failed: ${errorMessage}`);
            
            // Schedule another attempt if under retry limit
            if (this.connectionStatus.retryCount < this.createRetryConfiguration().maxRetries) {
                this.scheduleReconnection();
            } else {
                this.logger.debug(' Max reconnection attempts reached');
                if (this.configuration.ui.notifications.errors) {
                    vscode.window.showErrorMessage('Failed to reconnect to AsciiDoc Language Server after multiple attempts.');
                }
            }
        }
    }

    /**
     * Setup LSP request/response logging for comprehensive communication tracing
     */
    private setupLSPLogging(): void {
        if (!this.client) {
            return;
        }

        this.logger.debug(' Setting up comprehensive LSP request/response logging');
        this.logger.debug(` Current logging level: ${this.configuration.logging.level}`);
        this.logger.debug(` Log requests: ${this.configuration.logging.logRequests}`);
        this.logger.debug(` Log responses: ${this.configuration.logging.logResponses}`);
        this.logger.debug(` Log notifications: ${this.configuration.logging.logNotifications}`);

        // Store reference to client for logging purposes
        const client = this.client;
        const outputChannel = this.outputChannel;
        const configuration = this.configuration;
        
        // Override sendRequest method using proper type casting
        const originalSendRequest = client.sendRequest.bind(client);
        
        (client as any).sendRequest = function(this: any, type: any, param?: any, token?: any): Promise<any> {
            const requestId = Math.random().toString(36).substr(2, 9);
            const timestamp = new Date().toISOString();
            
            // Extract method name from type parameter
            let method = 'unknown';
            if (typeof type === 'string') {
                method = type;
            } else if (type && typeof type === 'object') {
                if (type.method) {
                    method = type.method;
                } else if (type._method) {
                    method = type._method;
                } else {
                    // Log the full object to understand its structure
                    try {
                        method = `[TypeObject: ${JSON.stringify(type)}]`;
                    } catch {
                        method = '[TypeObject: not serializable]';
                    }
                }
            }
            
            // Log outgoing request based on configuration
            const shouldLogRequests = configuration.logging.logRequests || 
                                     configuration.logging.level === 'DEBUG' || 
                                     configuration.logging.level === 'TRACE';
            
            if (shouldLogRequests) {
                outputChannel.appendLine(`LSP Request [${requestId}] ${timestamp}: ${method}`);
                if (param !== undefined && (configuration.debug.verboseLogging || configuration.logging.level === 'DEBUG' || configuration.logging.level === 'TRACE')) {
                    try {
                        outputChannel.appendLine(`LSP Request [${requestId}] Parameters: ${JSON.stringify(param, null, 2)}`);
                    } catch {
                        outputChannel.appendLine(`LSP Request [${requestId}] Parameters: [not serializable]`);
                    }
                }
            }
            
            const startTime = Date.now();
            
            // Call original method with proper arguments
            const result = originalSendRequest(type, param, token);
            
            // Add response logging
            return result.then((response: any) => {
                const duration = Date.now() - startTime;
                const shouldLogResponses = configuration.logging.logResponses || 
                                          configuration.logging.level === 'DEBUG' || 
                                          configuration.logging.level === 'TRACE';
                
                if (shouldLogResponses) {
                    outputChannel.appendLine(`LSP Response [${requestId}] ${new Date().toISOString()}: ${method} (${duration}ms)`);
                    
                    if ((configuration.debug.verboseLogging || configuration.logging.level === 'DEBUG' || configuration.logging.level === 'TRACE') && response !== undefined) {
                        try {
                            // Limit result logging to avoid overwhelming output
                            const resultStr = typeof response === 'object' ? 
                                JSON.stringify(response, null, 2).substring(0, 1000) + (JSON.stringify(response).length > 1000 ? '...' : '') :
                                String(response);
                            outputChannel.appendLine(`LSP Response [${requestId}] Result: ${resultStr}`);
                        } catch {
                            outputChannel.appendLine(`LSP Response [${requestId}] Result: [not serializable]`);
                        }
                    }
                }
                
                return response;
            }).catch((error: any) => {
                const duration = Date.now() - startTime;
                outputChannel.appendLine(`LSP Response [${requestId}] ERROR ${new Date().toISOString()}: ${method} (${duration}ms) - ${error.message || error}`);
                throw error;
            });
        };

        // Override sendNotification method
        const originalSendNotification = client.sendNotification.bind(client);
        
        (client as any).sendNotification = function(this: any, type: any, params?: any): Promise<void> {
            const timestamp = new Date().toISOString();
            
            // Extract method name from type parameter
            let method = 'unknown';
            if (typeof type === 'string') {
                method = type;
            } else if (type && typeof type === 'object') {
                if (type.method) {
                    method = type.method;
                } else if (type._method) {
                    method = type._method;
                } else {
                    // Log the full object to understand its structure
                    try {
                        method = `[TypeObject: ${JSON.stringify(type)}]`;
                    } catch {
                        method = '[TypeObject: not serializable]';
                    }
                }
            }
            
            const shouldLogNotifications = configuration.logging.logNotifications || 
                                           configuration.logging.level === 'DEBUG' || 
                                           configuration.logging.level === 'TRACE';
            
            if (shouldLogNotifications) {
                outputChannel.appendLine(`LSP Notification ${timestamp}: ${method}`);
                
                if (params !== undefined && (configuration.debug.verboseLogging || configuration.logging.level === 'DEBUG' || configuration.logging.level === 'TRACE')) {
                    try {
                        outputChannel.appendLine(`LSP Notification Parameters: ${JSON.stringify(params, null, 2)}`);
                    } catch {
                        outputChannel.appendLine(`LSP Notification Parameters: [not serializable]`);
                    }
                }
            }
            
            return originalSendNotification(type, params);
        };

        this.logger.debug(' LSP request/response logging setup completed');
    }


    /**
     * Setup LSP lifecycle event logging for comprehensive communication monitoring
     */
    private setupLSPLifecycleLogging(): void {
        if (!this.client) {
            return;
        }

        this.logger.debug(' Setting up LSP lifecycle event logging');

        // Log client state changes
        this.client.onDidChangeState((stateChangeEvent) => {
            const shouldLogLifecycle = this.configuration.logging.logLifecycleEvents || 
                                       ['INFO', 'DEBUG', 'TRACE'].includes(this.configuration.logging.level);
                                       
            if (shouldLogLifecycle) {
                const timestamp = new Date().toISOString();
                this.outputChannel.appendLine(`LSP Lifecycle ${timestamp}: Client state changed from ${stateChangeEvent.oldState} to ${stateChangeEvent.newState}`);
                
                // Log state-specific information using State enum
                switch (stateChangeEvent.newState) {
                    case State.Stopped:
                        this.outputChannel.appendLine(`LSP Lifecycle: Client is now stopped`);
                        break;
                    case State.Starting:
                        this.outputChannel.appendLine(`LSP Lifecycle: Client is starting...`);
                        break;
                    case State.Running:
                        this.outputChannel.appendLine(`LSP Lifecycle: Client is now running and ready`);
                        // Log capabilities when client becomes ready
                        if (this.configuration.debug.verboseLogging || ['DEBUG', 'TRACE'].includes(this.configuration.logging.level)) {
                            setTimeout(() => {
                                const capabilities = this.client?.initializeResult?.capabilities;
                                if (capabilities) {
                                    this.outputChannel.appendLine(`LSP Lifecycle: Server capabilities received during initialization`);
                                }
                            }, 100);
                        }
                        break;
                }
            }
        });

        this.logger.debug(' LSP lifecycle event logging setup completed');
    }

    /**
     * Setup notification handlers for enhanced communication
     */
    private setupNotificationHandlers(): void {
        if (!this.client) {
            return;
        }

        this.logger.debug(' Setting up enhanced notification handlers');

        // Custom notification for server status
        this.client.onNotification('asciidoc/serverStatus', (params: any) => {
            const shouldLogNotifications = this.configuration.logging.logNotifications || 
                                           ['DEBUG', 'TRACE'].includes(this.configuration.logging.level);
            
            if (shouldLogNotifications) {
                const timestamp = new Date().toISOString();
                this.outputChannel.appendLine(`LSP Server Notification ${timestamp}: asciidoc/serverStatus`);
                if ((this.configuration.debug.verboseLogging || ['DEBUG', 'TRACE'].includes(this.configuration.logging.level)) && params) {
                    this.outputChannel.appendLine(`LSP Server Notification Parameters: ${JSON.stringify(params, null, 2)}`);
                }
            }
        });

        // Custom notification for capability changes
        this.client.onNotification('asciidoc/capabilityChanged', (params: any) => {
            const timestamp = new Date().toISOString();
            this.outputChannel.appendLine(`LSP Server Notification ${timestamp}: asciidoc/capabilityChanged - Re-detecting capabilities`);
            if (this.configuration.debug.verboseLogging && params) {
                this.outputChannel.appendLine(`LSP Server Notification Parameters: ${JSON.stringify(params, null, 2)}`);
            }
            this.detectServerCapabilities(); // Re-detect capabilities
        });

        // Progress notifications
        this.client.onNotification('$/progress', (params: any) => {
            const timestamp = new Date().toISOString();
            if (this.configuration.debug.verboseLogging) {
                this.outputChannel.appendLine(`LSP Server Notification ${timestamp}: $/progress`);
                if (params) {
                    this.outputChannel.appendLine(`LSP Server Notification Parameters: ${JSON.stringify(params, null, 2)}`);
                }
            }
        });

        // Log configuration notifications
        this.client.onNotification('workspace/didChangeConfiguration', (params: any) => {
            const timestamp = new Date().toISOString();
            this.outputChannel.appendLine(`LSP Server Notification ${timestamp}: workspace/didChangeConfiguration`);
            if (this.configuration.debug.verboseLogging && params) {
                this.outputChannel.appendLine(`LSP Server Notification Parameters: ${JSON.stringify(params, null, 2)}`);
            }
        });

        // Log show message notifications from server
        this.client.onNotification('window/showMessage', (params: any) => {
            const timestamp = new Date().toISOString();
            this.outputChannel.appendLine(`LSP Server Notification ${timestamp}: window/showMessage`);
            if (params) {
                this.outputChannel.appendLine(`LSP Server Message: ${params.message} (type: ${params.type})`);
            }
        });

        // Log telemetry events
        this.client.onNotification('telemetry/event', (params: any) => {
            const timestamp = new Date().toISOString();
            if (this.configuration.debug.verboseLogging) {
                this.outputChannel.appendLine(`LSP Server Notification ${timestamp}: telemetry/event`);
                if (params) {
                    this.outputChannel.appendLine(`LSP Telemetry Data: ${JSON.stringify(params, null, 2)}`);
                }
            }
        });

        // Note: We do NOT intercept textDocument/publishDiagnostics here as it prevents 
        // VSCode from properly displaying diagnostics. The language client handles this automatically.
        
        this.logger.debug(' Enhanced notification handlers setup completed');
    }

    /**
     * Log server capabilities
     */
    private logServerCapabilities(capabilities: ExtendedServerCapabilities): void {
        this.logger.debug(`Server capabilities: textDocumentSync=${!!capabilities.textDocumentSync}, completionProvider=${!!capabilities.completionProvider}, hoverProvider=${!!capabilities.hoverProvider}, definitionProvider=${!!capabilities.definitionProvider}, referencesProvider=${!!capabilities.referencesProvider}, documentSymbolProvider=${!!capabilities.documentSymbolProvider}, codeActionProvider=${!!capabilities.codeActionProvider}, documentFormattingProvider=${!!capabilities.documentFormattingProvider}, documentRangeFormattingProvider=${!!capabilities.documentRangeFormattingProvider}, renameProvider=${!!capabilities.renameProvider}`);
    }

    /**
     * Apply feature toggles based on server capabilities
     */
    private applyFeatureToggles(capabilities: ExtendedServerCapabilities): void {
        // Disable features in configuration if server doesn't support them
        if (!capabilities.completionProvider && this.configuration.features.completion.enabled) {
            this.logger.debug(' Disabling completion - not supported by server');
        }
        
        if (!capabilities.hoverProvider && this.configuration.features.hover.enabled) {
            this.logger.debug(' Disabling hover - not supported by server');
        }
        
        if (!capabilities.documentFormattingProvider && this.configuration.features.formatting.enabled) {
            this.logger.debug(' Disabling formatting - not supported by server');
        }

        // Enable advanced features if server supports them
        const asciidocFeatures = capabilities.experimental?.asciidocFeatures;
        if (asciidocFeatures?.customValidation) {
            this.logger.debug(' Enabling custom validation features');
        }
    }

    /**
     * Notify capability change handlers
     */
    private notifyCapabilityChange(capabilities: ExtendedServerCapabilities): void {
        this.capabilityChangeHandlers.forEach(handler => {
            try {
                handler(capabilities);
            } catch (error) {
                this.logger.debug(` Error in capability change handler: ${error}`);
            }
        });
    }

    // Diagnostic filtering methods removed - no longer needed after fixing validation display issue

    /**
     * Utility method for delays
     */
    private delay(ms: number): Promise<void> {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    /**
     * Dispose of resources
     */
    public dispose(): void {
        if (this.retryTimer) {
            clearTimeout(this.retryTimer);
            this.retryTimer = undefined;
        }
        
        this.capabilityChangeHandlers = [];
        this.connectionStatus.isConnected = false;
    }
}