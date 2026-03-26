/**
 * Constants for the XtextAsciidocPlugin VSCode extension
 */

// Extension identifiers
export const EXTENSION_ID = 'xtext-asciidoc-plugin';
export const EXTENSION_NAME = 'Xtext AsciiDoc Plugin';

// Language Server
export const LANGUAGE_SERVER_NAME = 'Xtext AsciiDoc Server';
export const LANGUAGE_ID = 'asciidoc';

// Commands
export const COMMANDS = {
    // Server management commands
    SERVER_RESTART: 'asciidoc.server.restart',
    SERVER_START: 'asciidoc.server.start',
    SERVER_STOP: 'asciidoc.server.stop',
    SERVER_SHOW_HEALTH: 'asciidoc.server.showHealth',
    SERVER_SHOW_CAPABILITIES: 'asciidoc.server.showCapabilities',
    
    // Logging management commands
    LOGGING_SET_LEVEL: 'asciidoc.logging.setLevel',
    LOGGING_SHOW_OUTPUT: 'asciidoc.logging.showOutput',
    LOGGING_SHOW_SERVER_OUTPUT: 'asciidoc.logging.showServerOutput',
    LOGGING_CLEAR_LOGS: 'asciidoc.logging.clearLogs',
    LOGGING_EXPORT_LOGS: 'asciidoc.logging.exportLogs'
} as const;

// File patterns and paths
export const FILE_PATTERNS = {
    ALL_FILES: '**/*.*',
    ASCIIDOC_FILES: '**/*.asciidoc'
} as const;

// Server executables
export const SERVER_EXECUTABLES = {
    WINDOWS: 'asciidoc-standalone.bat',
    UNIX: 'asciidoc-standalone'
} as const;

export const SERVER_PATHS = {
    RELATIVE_PATH: ['src', 'asciidoc', 'bin'],
    LOG_DIR_KEY: 'logDirectory'
} as const;

// Debug configuration
export const DEBUG_CONFIG = {
    JAVA_OPTS: '-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n,quiet=y'
} as const;

// Output channels
export const OUTPUT_CHANNELS = {
    EXTENSION: 'Xtext AsciiDoc Extension',
    LANGUAGE_SERVER: 'Xtext AsciiDoc Language Server',
    TRACE: 'Xtext AsciiDoc Language Server Trace'
} as const;

// Configuration keys
export const CONFIG_KEYS = {
    // Language Server Configuration
    LANGUAGE_SERVER_ENABLED: 'asciidoc.languageServer.enabled',
    LANGUAGE_SERVER_PORT: 'asciidoc.languageServer.port',
    LANGUAGE_SERVER_TIMEOUT: 'asciidoc.languageServer.timeout',
    LANGUAGE_SERVER_MAX_RETRIES: 'asciidoc.languageServer.maxRetries',
    LANGUAGE_SERVER_RESTART_ON_CONFIG_CHANGE: 'asciidoc.languageServer.restartOnConfigChange',
    
    // Logging and Debugging
    LOG_DIRECTORY: 'asciidoc.logDirectory',
    DEBUG_MODE: 'asciidoc.debug.enabled',
    DEBUG_PORT: 'asciidoc.debug.port',
    DEBUG_VERBOSE_LOGGING: 'asciidoc.debug.verboseLogging',
    
    // Tracing
    TRACE_SERVER: 'asciidoc.trace.server',
    TRACE_LEVEL: 'asciidoc.trace.level', // Legacy support
    
    // Logging Configuration
    LOGGING_LEVEL: 'asciidoc.logging.level',
    LOGGING_LOG_REQUESTS: 'asciidoc.logging.logRequests',
    LOGGING_LOG_RESPONSES: 'asciidoc.logging.logResponses',
    LOGGING_LOG_NOTIFICATIONS: 'asciidoc.logging.logNotifications',
    LOGGING_LOG_LIFECYCLE_EVENTS: 'asciidoc.logging.logLifecycleEvents',
    LOGGING_LOG_SERVER_OUTPUT: 'asciidoc.logging.logServerOutput',
    LOGGING_MAX_LOG_ENTRIES: 'asciidoc.logging.maxLogEntries',
    
    // Language Features - Validation
    FEATURES_VALIDATION_ENABLED: 'asciidoc.features.validation.enabled',
    FEATURES_VALIDATION_STRICT_MODE: 'asciidoc.features.validation.strictMode',
    
    // Language Features - Completion
    FEATURES_COMPLETION_ENABLED: 'asciidoc.features.completion.enabled',
    FEATURES_COMPLETION_SNIPPETS: 'asciidoc.features.completion.snippets',
    FEATURES_COMPLETION_MAX_SUGGESTIONS: 'asciidoc.features.completion.maxSuggestions',
    
    // Language Features - Formatting
    FEATURES_FORMATTING_ENABLED: 'asciidoc.features.formatting.enabled',
    FEATURES_FORMATTING_PRESERVE_WHITESPACE: 'asciidoc.features.formatting.preserveWhitespace',
    FEATURES_FORMATTING_INDENT_SIZE: 'asciidoc.features.formatting.indentSize',
    
    // Language Features - Hover
    FEATURES_HOVER_ENABLED: 'asciidoc.features.hover.enabled',
    
    // Language Features - References
    FEATURES_REFERENCES_ENABLED: 'asciidoc.features.references.enabled',
    
    // Language Features - Outline
    FEATURES_OUTLINE_ENABLED: 'asciidoc.features.outline.enabled',
    FEATURES_OUTLINE_SHOW_SECTIONS: 'asciidoc.features.outline.showSections',
    FEATURES_OUTLINE_SHOW_TABLES: 'asciidoc.features.outline.showTables',
    FEATURES_OUTLINE_SHOW_IMAGES: 'asciidoc.features.outline.showImages',
    
    // UI Configuration
    UI_STATUS_BAR_ENABLED: 'asciidoc.ui.statusBar.enabled',
    UI_NOTIFICATIONS_ERRORS: 'asciidoc.ui.notifications.errors',
    UI_NOTIFICATIONS_WARNINGS: 'asciidoc.ui.notifications.warnings',
    UI_NOTIFICATIONS_INFO: 'asciidoc.ui.notifications.info',
    
    // Workspace Configuration
    WORKSPACE_ENABLE_GLOBAL_SETTINGS: 'asciidoc.workspace.enableGlobalSettings',
    WORKSPACE_CONFIGURATION_FILES: 'asciidoc.workspace.configurationFiles',
    
    // Performance Configuration
    PERFORMANCE_MAX_FILE_SIZE: 'asciidoc.performance.maxFileSize',
    PERFORMANCE_ENABLE_BACKGROUND_PROCESSING: 'asciidoc.performance.enableBackgroundProcessing',
} as const;

// Default values
export const DEFAULTS = {
    // Language Server
    LANGUAGE_SERVER_ENABLED: true,
    LANGUAGE_SERVER_PORT: 5008,
    LANGUAGE_SERVER_TIMEOUT: 30000,
    LANGUAGE_SERVER_MAX_RETRIES: 3,
    LANGUAGE_SERVER_RESTART_ON_CONFIG_CHANGE: true,
    
    // Logging and Debugging
    LOG_DIRECTORY: process.platform === 'win32' ? 'C:\\temp\\logs' : '/tmp/logs',
    DEBUG_MODE: false,
    DEBUG_PORT: 8000,
    DEBUG_VERBOSE_LOGGING: false,
    
    // Tracing
    TRACE_SERVER: 'off',
    TRACE_LEVEL: 'Messages', // Legacy support
    
    // Logging Configuration
    LOGGING_LEVEL: 'INFO',
    LOGGING_LOG_REQUESTS: false,
    LOGGING_LOG_RESPONSES: false,
    LOGGING_LOG_NOTIFICATIONS: false,
    LOGGING_LOG_LIFECYCLE_EVENTS: true,
    LOGGING_LOG_SERVER_OUTPUT: false,
    LOGGING_MAX_LOG_ENTRIES: 1000,
    
    // Language Features - Validation
    FEATURES_VALIDATION_ENABLED: true,
    FEATURES_VALIDATION_STRICT_MODE: false,
    
    // Language Features - Completion
    FEATURES_COMPLETION_ENABLED: true,
    FEATURES_COMPLETION_SNIPPETS: true,
    FEATURES_COMPLETION_MAX_SUGGESTIONS: 50,
    
    // Language Features - Formatting
    FEATURES_FORMATTING_ENABLED: true,
    FEATURES_FORMATTING_PRESERVE_WHITESPACE: false,
    FEATURES_FORMATTING_INDENT_SIZE: 2,
    
    // Language Features - Hover
    FEATURES_HOVER_ENABLED: true,
    
    // Language Features - References
    FEATURES_REFERENCES_ENABLED: true,
    
    // Language Features - Outline
    FEATURES_OUTLINE_ENABLED: true,
    FEATURES_OUTLINE_SHOW_SECTIONS: true,
    FEATURES_OUTLINE_SHOW_TABLES: true,
    FEATURES_OUTLINE_SHOW_IMAGES: false,
    
    // UI Configuration
    UI_STATUS_BAR_ENABLED: true,
    UI_NOTIFICATIONS_ERRORS: true,
    UI_NOTIFICATIONS_WARNINGS: true,
    UI_NOTIFICATIONS_INFO: false,
    
    // Workspace Configuration
    WORKSPACE_ENABLE_GLOBAL_SETTINGS: true,
    WORKSPACE_CONFIGURATION_FILES: ['.asciidoc.json', 'asciidoc.config.json'] as string[],
    
    // Performance Configuration
    PERFORMANCE_MAX_FILE_SIZE: 10485760, // 10MB
    PERFORMANCE_ENABLE_BACKGROUND_PROCESSING: true,
} as const;

// Status bar
export const STATUS_BAR = {
    TEXT: '$(check) AsciiDoc',
    TOOLTIP: 'Xtext AsciiDoc Language Server is running',
    PRIORITY: 100
} as const;