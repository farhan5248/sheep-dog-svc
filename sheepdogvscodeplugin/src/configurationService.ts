/**
 * Configuration Service for Xtext AsciiDoc Plugin
 * Handles workspace-level configuration, file-based configuration, and configuration changes
 */

import * as vscode from 'vscode';
import * as path from 'path';
import * as fs from 'fs';
import * as constants from './constants';
import { createLogger, Logger } from './asciiDocLogger';

/**
 * Configuration interface representing all extension settings
 */
export interface AsciidocConfiguration {
    // Language Server Configuration
    languageServer: {
        enabled: boolean;
        port: number;
        timeout: number;
        maxRetries: number;
        restartOnConfigChange: boolean;
    };
    
    // Logging and Debugging
    logDirectory: string;
    debug: {
        enabled: boolean;
        port: number;
        verboseLogging: boolean;
    };
    
    // Tracing
    trace: {
        server: string;
        level: string; // Legacy support
    };
    
    // Logging Configuration
    logging: {
        level: string;
        logRequests: boolean;
        logResponses: boolean;
        logNotifications: boolean;
        logLifecycleEvents: boolean;
        logServerOutput: boolean;
        maxLogEntries: number;
    };
    
    // Language Features
    features: {
        validation: {
            enabled: boolean;
            strictMode: boolean;
        };
        completion: {
            enabled: boolean;
            snippets: boolean;
            maxSuggestions: number;
        };
        formatting: {
            enabled: boolean;
            preserveWhitespace: boolean;
            indentSize: number;
        };
        hover: {
            enabled: boolean;
        };
        references: {
            enabled: boolean;
        };
        outline: {
            enabled: boolean;
            showSections: boolean;
            showTables: boolean;
            showImages: boolean;
        };
    };
    
    // UI Configuration
    ui: {
        statusBar: {
            enabled: boolean;
        };
        notifications: {
            errors: boolean;
            warnings: boolean;
            info: boolean;
        };
    };
    
    // Workspace Configuration
    workspace: {
        enableGlobalSettings: boolean;
        configurationFiles: string[];
    };
    
    // Performance Configuration
    performance: {
        maxFileSize: number;
        enableBackgroundProcessing: boolean;
    };
}

/**
 * Configuration change event interface
 */
export interface ConfigurationChangeEvent {
    changed: string[];
    previousConfig: AsciidocConfiguration;
    newConfig: AsciidocConfiguration;
    affectsLanguageServer: boolean;
    requiresRestart: boolean;
}

/**
 * Configuration service class
 */
export class ConfigurationService {
    private disposables: vscode.Disposable[] = [];
    private changeHandlers: ((event: ConfigurationChangeEvent) => void)[] = [];
    private currentConfig: AsciidocConfiguration;
    private logger: Logger | undefined;

    constructor(outputChannel?: vscode.OutputChannel) {
        if (outputChannel) {
            this.logger = createLogger(outputChannel, 'ConfigurationService');
        }
        this.currentConfig = this.loadConfiguration();
        this.setupConfigurationWatcher();
    }

    /**
     * Get the current configuration
     */
    public getConfiguration(): AsciidocConfiguration {
        return { ...this.currentConfig };
    }

    /**
     * Load configuration from VSCode settings and workspace files
     */
    public loadConfiguration(): AsciidocConfiguration {
        const config = vscode.workspace.getConfiguration();
        
        // Load base configuration from VSCode settings
        const baseConfig = this.loadVSCodeConfiguration(config);
        
        // If workspace configuration is enabled, merge with workspace files
        if (baseConfig.workspace.enableGlobalSettings) {
            const workspaceConfig = this.loadWorkspaceConfiguration();
            return this.mergeConfigurations(baseConfig, workspaceConfig);
        }
        
        return baseConfig;
    }

    /**
     * Register a configuration change handler
     */
    public onConfigurationChange(handler: (event: ConfigurationChangeEvent) => void): vscode.Disposable {
        this.changeHandlers.push(handler);
        
        return new vscode.Disposable(() => {
            const index = this.changeHandlers.indexOf(handler);
            if (index >= 0) {
                this.changeHandlers.splice(index, 1);
            }
        });
    }

    /**
     * Check if the configuration affects the language server
     */
    public affectsLanguageServer(changedKeys: string[]): boolean {
        const languageServerKeys = [
            constants.CONFIG_KEYS.LANGUAGE_SERVER_ENABLED,
            constants.CONFIG_KEYS.LANGUAGE_SERVER_PORT,
            constants.CONFIG_KEYS.LANGUAGE_SERVER_TIMEOUT,
            constants.CONFIG_KEYS.LANGUAGE_SERVER_MAX_RETRIES,
            constants.CONFIG_KEYS.LOG_DIRECTORY,
            constants.CONFIG_KEYS.DEBUG_MODE,
            constants.CONFIG_KEYS.DEBUG_PORT,
            constants.CONFIG_KEYS.DEBUG_VERBOSE_LOGGING,
            constants.CONFIG_KEYS.TRACE_SERVER,
            constants.CONFIG_KEYS.TRACE_LEVEL
        ];
        
        return changedKeys.some(key => languageServerKeys.some(lsKey => key.startsWith(lsKey)));
    }

    /**
     * Check if the configuration requires a restart
     */
    public requiresRestart(changedKeys: string[]): boolean {
        const restartKeys = [
            constants.CONFIG_KEYS.LANGUAGE_SERVER_ENABLED,
            constants.CONFIG_KEYS.LANGUAGE_SERVER_PORT,
            constants.CONFIG_KEYS.DEBUG_MODE,
            constants.CONFIG_KEYS.DEBUG_PORT
        ];
        
        // Note: Diagnostic configuration changes don't require restart
        // They are handled dynamically by the communication service
        
        return changedKeys.some(key => restartKeys.some(rKey => key.startsWith(rKey)));
    }

    /**
     * Dispose of the configuration service
     */
    public dispose(): void {
        this.disposables.forEach(disposable => disposable.dispose());
        this.disposables = [];
        this.changeHandlers = [];
    }

    /**
     * Load configuration from VSCode settings
     */
    private loadVSCodeConfiguration(config: vscode.WorkspaceConfiguration): AsciidocConfiguration {
        return {
            languageServer: {
                enabled: config.get(constants.CONFIG_KEYS.LANGUAGE_SERVER_ENABLED, constants.DEFAULTS.LANGUAGE_SERVER_ENABLED),
                port: config.get(constants.CONFIG_KEYS.LANGUAGE_SERVER_PORT, constants.DEFAULTS.LANGUAGE_SERVER_PORT),
                timeout: config.get(constants.CONFIG_KEYS.LANGUAGE_SERVER_TIMEOUT, constants.DEFAULTS.LANGUAGE_SERVER_TIMEOUT),
                maxRetries: config.get(constants.CONFIG_KEYS.LANGUAGE_SERVER_MAX_RETRIES, constants.DEFAULTS.LANGUAGE_SERVER_MAX_RETRIES),
                restartOnConfigChange: config.get(constants.CONFIG_KEYS.LANGUAGE_SERVER_RESTART_ON_CONFIG_CHANGE, constants.DEFAULTS.LANGUAGE_SERVER_RESTART_ON_CONFIG_CHANGE)
            },
            logDirectory: config.get(constants.CONFIG_KEYS.LOG_DIRECTORY, constants.DEFAULTS.LOG_DIRECTORY),
            debug: {
                enabled: config.get(constants.CONFIG_KEYS.DEBUG_MODE, constants.DEFAULTS.DEBUG_MODE),
                port: config.get(constants.CONFIG_KEYS.DEBUG_PORT, constants.DEFAULTS.DEBUG_PORT),
                verboseLogging: config.get(constants.CONFIG_KEYS.DEBUG_VERBOSE_LOGGING, constants.DEFAULTS.DEBUG_VERBOSE_LOGGING)
            },
            trace: {
                server: config.get(constants.CONFIG_KEYS.TRACE_SERVER, constants.DEFAULTS.TRACE_SERVER),
                level: config.get(constants.CONFIG_KEYS.TRACE_LEVEL, constants.DEFAULTS.TRACE_LEVEL)
            },
            logging: {
                level: config.get(constants.CONFIG_KEYS.LOGGING_LEVEL, constants.DEFAULTS.LOGGING_LEVEL),
                logRequests: config.get(constants.CONFIG_KEYS.LOGGING_LOG_REQUESTS, constants.DEFAULTS.LOGGING_LOG_REQUESTS),
                logResponses: config.get(constants.CONFIG_KEYS.LOGGING_LOG_RESPONSES, constants.DEFAULTS.LOGGING_LOG_RESPONSES),
                logNotifications: config.get(constants.CONFIG_KEYS.LOGGING_LOG_NOTIFICATIONS, constants.DEFAULTS.LOGGING_LOG_NOTIFICATIONS),
                logLifecycleEvents: config.get(constants.CONFIG_KEYS.LOGGING_LOG_LIFECYCLE_EVENTS, constants.DEFAULTS.LOGGING_LOG_LIFECYCLE_EVENTS),
                logServerOutput: config.get(constants.CONFIG_KEYS.LOGGING_LOG_SERVER_OUTPUT, constants.DEFAULTS.LOGGING_LOG_SERVER_OUTPUT),
                maxLogEntries: config.get(constants.CONFIG_KEYS.LOGGING_MAX_LOG_ENTRIES, constants.DEFAULTS.LOGGING_MAX_LOG_ENTRIES)
            },
            features: {
                validation: {
                    enabled: config.get(constants.CONFIG_KEYS.FEATURES_VALIDATION_ENABLED, constants.DEFAULTS.FEATURES_VALIDATION_ENABLED),
                    strictMode: config.get(constants.CONFIG_KEYS.FEATURES_VALIDATION_STRICT_MODE, constants.DEFAULTS.FEATURES_VALIDATION_STRICT_MODE)
                },
                completion: {
                    enabled: config.get(constants.CONFIG_KEYS.FEATURES_COMPLETION_ENABLED, constants.DEFAULTS.FEATURES_COMPLETION_ENABLED),
                    snippets: config.get(constants.CONFIG_KEYS.FEATURES_COMPLETION_SNIPPETS, constants.DEFAULTS.FEATURES_COMPLETION_SNIPPETS),
                    maxSuggestions: config.get(constants.CONFIG_KEYS.FEATURES_COMPLETION_MAX_SUGGESTIONS, constants.DEFAULTS.FEATURES_COMPLETION_MAX_SUGGESTIONS)
                },
                formatting: {
                    enabled: config.get(constants.CONFIG_KEYS.FEATURES_FORMATTING_ENABLED, constants.DEFAULTS.FEATURES_FORMATTING_ENABLED),
                    preserveWhitespace: config.get(constants.CONFIG_KEYS.FEATURES_FORMATTING_PRESERVE_WHITESPACE, constants.DEFAULTS.FEATURES_FORMATTING_PRESERVE_WHITESPACE),
                    indentSize: config.get(constants.CONFIG_KEYS.FEATURES_FORMATTING_INDENT_SIZE, constants.DEFAULTS.FEATURES_FORMATTING_INDENT_SIZE)
                },
                hover: {
                    enabled: config.get(constants.CONFIG_KEYS.FEATURES_HOVER_ENABLED, constants.DEFAULTS.FEATURES_HOVER_ENABLED)
                },
                references: {
                    enabled: config.get(constants.CONFIG_KEYS.FEATURES_REFERENCES_ENABLED, constants.DEFAULTS.FEATURES_REFERENCES_ENABLED)
                },
                outline: {
                    enabled: config.get(constants.CONFIG_KEYS.FEATURES_OUTLINE_ENABLED, constants.DEFAULTS.FEATURES_OUTLINE_ENABLED),
                    showSections: config.get(constants.CONFIG_KEYS.FEATURES_OUTLINE_SHOW_SECTIONS, constants.DEFAULTS.FEATURES_OUTLINE_SHOW_SECTIONS),
                    showTables: config.get(constants.CONFIG_KEYS.FEATURES_OUTLINE_SHOW_TABLES, constants.DEFAULTS.FEATURES_OUTLINE_SHOW_TABLES),
                    showImages: config.get(constants.CONFIG_KEYS.FEATURES_OUTLINE_SHOW_IMAGES, constants.DEFAULTS.FEATURES_OUTLINE_SHOW_IMAGES)
                }
            },
            ui: {
                statusBar: {
                    enabled: config.get(constants.CONFIG_KEYS.UI_STATUS_BAR_ENABLED, constants.DEFAULTS.UI_STATUS_BAR_ENABLED)
                },
                notifications: {
                    errors: config.get(constants.CONFIG_KEYS.UI_NOTIFICATIONS_ERRORS, constants.DEFAULTS.UI_NOTIFICATIONS_ERRORS),
                    warnings: config.get(constants.CONFIG_KEYS.UI_NOTIFICATIONS_WARNINGS, constants.DEFAULTS.UI_NOTIFICATIONS_WARNINGS),
                    info: config.get(constants.CONFIG_KEYS.UI_NOTIFICATIONS_INFO, constants.DEFAULTS.UI_NOTIFICATIONS_INFO)
                }
            },
            workspace: {
                enableGlobalSettings: config.get(constants.CONFIG_KEYS.WORKSPACE_ENABLE_GLOBAL_SETTINGS, constants.DEFAULTS.WORKSPACE_ENABLE_GLOBAL_SETTINGS),
                configurationFiles: config.get(constants.CONFIG_KEYS.WORKSPACE_CONFIGURATION_FILES, constants.DEFAULTS.WORKSPACE_CONFIGURATION_FILES)
            },
            performance: {
                maxFileSize: config.get(constants.CONFIG_KEYS.PERFORMANCE_MAX_FILE_SIZE, constants.DEFAULTS.PERFORMANCE_MAX_FILE_SIZE),
                enableBackgroundProcessing: config.get(constants.CONFIG_KEYS.PERFORMANCE_ENABLE_BACKGROUND_PROCESSING, constants.DEFAULTS.PERFORMANCE_ENABLE_BACKGROUND_PROCESSING)
            }
        };
    }

    /**
     * Load configuration from workspace configuration files
     */
    private loadWorkspaceConfiguration(): Partial<AsciidocConfiguration> {
        const workspaces = vscode.workspace.workspaceFolders;
        if (!workspaces || workspaces.length === 0) {
            return {};
        }

        const mergedConfig: Partial<AsciidocConfiguration> = {};
        
        for (const workspace of workspaces) {
            const config = vscode.workspace.getConfiguration();
            const configFiles = config.get(constants.CONFIG_KEYS.WORKSPACE_CONFIGURATION_FILES, constants.DEFAULTS.WORKSPACE_CONFIGURATION_FILES);
            
            for (const configFile of configFiles) {
                const configPath = path.join(workspace.uri.fsPath, configFile);
                const workspaceConfig = this.loadConfigurationFile(configPath);
                if (workspaceConfig) {
                    this.mergeConfigurationDeep(mergedConfig, workspaceConfig);
                }
            }
        }

        return mergedConfig;
    }

    /**
     * Load configuration from a specific file
     */
    private loadConfigurationFile(configPath: string): Partial<AsciidocConfiguration> | null {
        try {
            if (!fs.existsSync(configPath)) {
                return null;
            }

            const configContent = fs.readFileSync(configPath, 'utf8');
            const parsedConfig = JSON.parse(configContent);
            
            this.logger?.debug(`Loaded workspace configuration from: ${configPath}`);
            return parsedConfig;
        } catch (error) {
            this.logger?.error(`Failed in loadConfigurationFile from ${configPath}: ${error instanceof Error ? error.message : 'Unknown error'}`);
            return null;
        }
    }

    /**
     * Merge configurations with deep merge strategy
     */
    private mergeConfigurations(base: AsciidocConfiguration, overlay: Partial<AsciidocConfiguration>): AsciidocConfiguration {
        const result = { ...base };
        this.mergeConfigurationDeep(result, overlay);
        return result;
    }

    /**
     * Deep merge configuration objects
     */
    private mergeConfigurationDeep(target: any, source: any): void {
        for (const key in source) {
            if (source.hasOwnProperty(key)) {
                if (typeof source[key] === 'object' && source[key] !== null && !Array.isArray(source[key])) {
                    if (!target[key]) {
                        target[key] = {};
                    }
                    this.mergeConfigurationDeep(target[key], source[key]);
                } else {
                    target[key] = source[key];
                }
            }
        }
    }

    /**
     * Setup configuration change watcher
     */
    private setupConfigurationWatcher(): void {
        // Watch VSCode configuration changes
        const configWatcher = vscode.workspace.onDidChangeConfiguration((e) => {
            if (e.affectsConfiguration('asciidoc')) {
                this.handleConfigurationChange();
            }
        });
        this.disposables.push(configWatcher);

        // Watch workspace configuration files
        if (vscode.workspace.workspaceFolders) {
            for (const workspace of vscode.workspace.workspaceFolders) {
                const config = vscode.workspace.getConfiguration();
                const configFiles = config.get(constants.CONFIG_KEYS.WORKSPACE_CONFIGURATION_FILES, constants.DEFAULTS.WORKSPACE_CONFIGURATION_FILES);
                
                for (const configFile of configFiles) {
                    const pattern = new vscode.RelativePattern(workspace, configFile);
                    const fileWatcher = vscode.workspace.createFileSystemWatcher(pattern);
                    
                    fileWatcher.onDidChange(() => this.handleConfigurationChange());
                    fileWatcher.onDidCreate(() => this.handleConfigurationChange());
                    fileWatcher.onDidDelete(() => this.handleConfigurationChange());
                    
                    this.disposables.push(fileWatcher);
                }
            }
        }
    }

    /**
     * Handle configuration changes
     */
    private handleConfigurationChange(): void {
        const previousConfig = { ...this.currentConfig };
        const newConfig = this.loadConfiguration();
        
        const changedKeys = this.getChangedKeys(previousConfig, newConfig);
        
        if (changedKeys.length === 0) {
            return; // No actual changes
        }

        this.currentConfig = newConfig;

        const changeEvent: ConfigurationChangeEvent = {
            changed: changedKeys,
            previousConfig,
            newConfig,
            affectsLanguageServer: this.affectsLanguageServer(changedKeys),
            requiresRestart: this.requiresRestart(changedKeys)
        };

        this.logger?.debug(`Configuration changed. Affected keys: ${changedKeys.join(', ')}, affectsLanguageServer: ${changeEvent.affectsLanguageServer}, requiresRestart: ${changeEvent.requiresRestart}`);

        // Notify all handlers
        this.changeHandlers.forEach(handler => {
            try {
                handler(changeEvent);
            } catch (error) {
                this.logger?.error(`Failed in configuration change handler: ${error instanceof Error ? error.message : 'Unknown error'}`);
            }
        });
    }

    /**
     * Get changed configuration keys by comparing two configurations
     */
    private getChangedKeys(oldConfig: AsciidocConfiguration, newConfig: AsciidocConfiguration, prefix: string = ''): string[] {
        const changedKeys: string[] = [];
        
        const flatOld = this.flattenObject(oldConfig, prefix);
        const flatNew = this.flattenObject(newConfig, prefix);
        
        const allKeys = new Set([...Object.keys(flatOld), ...Object.keys(flatNew)]);
        
        for (const key of allKeys) {
            if (flatOld[key] !== flatNew[key]) {
                changedKeys.push(key);
            }
        }
        
        return changedKeys;
    }

    /**
     * Flatten a nested object for comparison
     */
    private flattenObject(obj: any, prefix: string = ''): Record<string, any> {
        const flattened: Record<string, any> = {};
        
        for (const key in obj) {
            if (obj.hasOwnProperty(key)) {
                const fullKey = prefix ? `${prefix}.${key}` : key;
                
                if (typeof obj[key] === 'object' && obj[key] !== null && !Array.isArray(obj[key])) {
                    Object.assign(flattened, this.flattenObject(obj[key], fullKey));
                } else {
                    flattened[fullKey] = obj[key];
                }
            }
        }
        
        return flattened;
    }
}