/**
 * Production-ready server launcher for XtextAsciidocPlugin VSCode extension
 * Implements robust server launch, health monitoring, and lifecycle management
 */

import * as vscode from 'vscode';
import * as path from 'path';
import * as fs from 'fs';
import * as os from 'os';
import * as cp from 'child_process';
import { LanguageClient, LanguageClientOptions, ServerOptions, State } from 'vscode-languageclient/node';
import * as constants from './constants';
import { AsciidocConfiguration } from './configurationService';
import { CommunicationService } from './communicationService';
import { createLogger, Logger } from './asciiDocLogger';

export enum ServerLaunchMode {
    JAR_BASED = 'jar',
    SCRIPT_BASED = 'script'
}

export enum ServerStatus {
    STARTING = 'starting',
    RUNNING = 'running',
    STOPPING = 'stopping',
    STOPPED = 'stopped',
    ERROR = 'error',
    TIMEOUT = 'timeout'
}

export interface ServerHealthInfo {
    status: ServerStatus;
    pid?: number;
    startTime?: Date;
    lastHealthCheck?: Date;
    errorMessage?: string;
    memoryUsage?: number;
    uptime?: number;
}

export interface ServerLaunchOptions {
    mode: ServerLaunchMode;
    timeout: number;
    maxRetries: number;
    healthCheckInterval: number;
    enableHealthMonitoring: boolean;
    javaExecutable?: string;
    jvmArgs: string[];
    serverArgs: string[];
    workingDirectory?: string;
    environment: NodeJS.ProcessEnv;
}

export class ServerLauncher {
    private client: LanguageClient | undefined;
    private serverProcess: cp.ChildProcess | undefined;
    private healthCheckTimer: NodeJS.Timeout | undefined;
    private healthInfo: ServerHealthInfo;
    private logger: Logger;
    private statusBarItem: vscode.StatusBarItem | undefined;
    private readonly extensionContext: vscode.ExtensionContext;
    private readonly configuration: AsciidocConfiguration;
    private communicationService: CommunicationService;

    constructor(
        extensionContext: vscode.ExtensionContext,
        configuration: AsciidocConfiguration,
        outputChannel: vscode.OutputChannel,
        statusBarItem?: vscode.StatusBarItem
    ) {
        this.extensionContext = extensionContext;
        this.configuration = configuration;
        this.logger = createLogger(outputChannel, 'ServerLauncher');
        this.statusBarItem = statusBarItem;
        this.healthInfo = {
            status: ServerStatus.STOPPED
        };

        // Initialize communication service with enhanced features
        this.communicationService = new CommunicationService(outputChannel, configuration);
    }

    /**
     * Start the language server with intelligent launch mode detection and robust error handling
     */
    public async startServer(clientOptions: LanguageClientOptions): Promise<void> {
        try {
            this.logger.debug(`Entering startServer for timeout: ${this.configuration.languageServer.timeout}`);
            this.updateHealthInfo({ status: ServerStatus.STARTING, startTime: new Date() });

            const launchOptions = this.createLaunchOptions();
            this.logger.debug(`startServer launch options: {mode: ${launchOptions.mode}, timeout: ${launchOptions.timeout}, javaExecutable: ${launchOptions.javaExecutable}}`);
            
            const serverOptions = await this.createServerOptions(launchOptions);
            
            // Create language client
            this.client = new LanguageClient(
                'xtextAsciiDocLanguageServer',
                constants.LANGUAGE_SERVER_NAME,
                serverOptions,
                clientOptions
            );

            // Setup enhanced communication with timeout and retry logic
            await this.communicationService.setupConnection(this.client);
            
            // Setup diagnostic handling to ensure proper VSCode integration
            this.communicationService.setupDiagnosticHandling();
            
            // Start server with retry logic and timeout handling
            await this.startWithRetryAndTimeout(launchOptions);
            
            // Setup health monitoring
            if (launchOptions.enableHealthMonitoring) {
                this.startHealthMonitoring(launchOptions.healthCheckInterval);
            }
            
            this.updateHealthInfo({ status: ServerStatus.RUNNING });
            this.updateStatusBar();

            this.logger.debug(`Exiting startServer`);

        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            this.logger.error(`Failed in startServer: ${errorMessage}`);
            this.updateHealthInfo({ status: ServerStatus.ERROR, errorMessage });
            this.updateStatusBar();
            throw error;
        }
    }

    /**
     * Stop the language server gracefully
     */
    public async stopServer(): Promise<void> {
        try {
            this.logger.debug(`Entering stopServer`);
            this.updateHealthInfo({ status: ServerStatus.STOPPING });
            this.updateStatusBar();

            // Stop health monitoring
            this.stopHealthMonitoring();

            // Stop language client
            if (this.client) {
                this.logger.debug(`stopServer stopping language client...`);
                await this.client.stop();
                this.client.dispose();
                this.client = undefined;
            }
            
            // Terminate server process if it exists
            if (this.serverProcess && !this.serverProcess.killed) {
                this.logger.debug(`stopServer terminating server process (PID: ${this.serverProcess.pid})...`);
                
                if (os.platform() === 'win32') {
                    // Windows: use taskkill for graceful shutdown
                    cp.exec(`taskkill /pid ${this.serverProcess.pid} /t /f`);
                } else {
                    // Unix: send SIGTERM then SIGKILL if needed
                    this.serverProcess.kill('SIGTERM');
                    setTimeout(() => {
                        if (this.serverProcess && !this.serverProcess.killed) {
                            this.serverProcess.kill('SIGKILL');
                        }
                    }, 5000);
                }
                
                this.serverProcess = undefined;
            }
            
            this.updateHealthInfo({ status: ServerStatus.STOPPED });
            this.updateStatusBar();

            this.logger.debug(`Exiting stopServer`);

        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            this.logger.error(`Failed in stopServer: ${errorMessage}`);
            this.updateHealthInfo({ status: ServerStatus.ERROR, errorMessage });
            this.updateStatusBar();
        }
    }

    /**
     * Restart the language server
     */
    public async restartServer(clientOptions: LanguageClientOptions): Promise<void> {
        try {
            this.logger.debug(`Entering restartServer`);

            await this.stopServer();

            // Wait a moment before restarting
            this.logger.debug(`Waiting 2000ms before restart...`);
            await new Promise(resolve => setTimeout(resolve, 2000));

            await this.startServer(clientOptions);

            this.logger.debug(`Exiting restartServer`);
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            this.logger.error(`Failed in restartServer: ${errorMessage}`);
            throw error;
        }
    }

    /**
     * Get current server health information
     */
    public getHealthInfo(): ServerHealthInfo {
        return { ...this.healthInfo };
    }

    /**
     * Get the language client instance
     */
    public getClient(): LanguageClient | undefined {
        return this.client;
    }

    /**
     * Get the communication service instance
     */
    public getCommunicationService(): CommunicationService {
        return this.communicationService;
    }

    /**
     * Get connection status from communication service
     */
    public getConnectionStatus() {
        return this.communicationService.getConnectionStatus();
    }

    /**
     * Create launch options based on configuration and system capabilities
     */
    private createLaunchOptions(): ServerLaunchOptions {
        const mode = this.detectOptimalLaunchMode();
        
        return {
            mode,
            timeout: this.configuration.languageServer.timeout,
            maxRetries: this.configuration.languageServer.maxRetries,
            healthCheckInterval: 30000, // 30 seconds
            enableHealthMonitoring: true,
            javaExecutable: this.findJavaExecutable(),
            jvmArgs: this.createJvmArgs(),
            serverArgs: [],
            workingDirectory: this.extensionContext.extensionPath,
            environment: this.createEnvironment()
        };
    }

    /**
     * Detect the optimal launch mode based on available resources
     */
    private detectOptimalLaunchMode(): ServerLaunchMode {
        const jarPath = this.getServerJarPath();
        const scriptPath = this.getServerScriptPath();
        
        // Prefer JAR-based launch if available (more reliable and faster)
        if (jarPath && fs.existsSync(jarPath)) {
            this.logger.debug(`Using JAR-based launch mode: ${jarPath}`);
            return ServerLaunchMode.JAR_BASED;
        }

        // Fall back to script-based launch
        if (scriptPath && fs.existsSync(scriptPath)) {
            this.logger.debug(`Using script-based launch mode: ${scriptPath}`);
            return ServerLaunchMode.SCRIPT_BASED;
        }
        
        throw new Error('No suitable server launch method found. Neither JAR nor script is available.');
    }

    /**
     * Get the path to the server JAR file
     */
    private getServerJarPath(): string {
        const jarName = 'xtextasciidocplugin.ide-1.0.0-SNAPSHOT.jar';
        return this.extensionContext.asAbsolutePath(path.join('src', 'asciidoc', 'lib', jarName));
    }

    /**
     * Get the path to the server script
     */
    private getServerScriptPath(): string {
        if (os.platform() === 'win32') {
            return this.extensionContext.asAbsolutePath(
                path.join(...constants.SERVER_PATHS.RELATIVE_PATH, constants.SERVER_EXECUTABLES.WINDOWS)
            );
        } else {
            return this.extensionContext.asAbsolutePath(
                path.join(...constants.SERVER_PATHS.RELATIVE_PATH, constants.SERVER_EXECUTABLES.UNIX)
            );
        }
    }

    /**
     * Create server options based on launch mode
     */
    private async createServerOptions(options: ServerLaunchOptions): Promise<ServerOptions> {
        if (options.mode === ServerLaunchMode.JAR_BASED) {
            return this.createJarBasedServerOptions(options);
        } else {
            return this.createScriptBasedServerOptions(options);
        }
    }

    /**
     * Create JAR-based server options
     */
    private createJarBasedServerOptions(options: ServerLaunchOptions): ServerOptions {
        const jarPath = this.getServerJarPath();
        const allJars = this.collectAllJars();
        const classpath = [jarPath, ...allJars].join(path.delimiter);
        
        const args = [
            ...options.jvmArgs,
            '-classpath',
            classpath,
            'org.eclipse.xtext.ide.server.ServerLauncher',
            ...options.serverArgs
        ];

        return {
            run: {
                command: options.javaExecutable!,
                args: args,
                options: {
                    env: options.environment,
                    cwd: options.workingDirectory
                }
            },
            debug: {
                command: options.javaExecutable!,
                args: args,
                options: {
                    env: options.environment,
                    cwd: options.workingDirectory
                }
            }
        };
    }

    /**
     * Create script-based server options
     */
    private createScriptBasedServerOptions(options: ServerLaunchOptions): ServerOptions {
        const scriptPath = this.getServerScriptPath();
        
        if (os.platform() === 'win32') {
            return {
                run: {
                    command: scriptPath,
                    args: options.serverArgs,
                    options: {
                        shell: true,
                        env: options.environment,
                        cwd: options.workingDirectory
                    }
                },
                debug: {
                    command: scriptPath,
                    args: options.serverArgs,
                    options: {
                        shell: true,
                        env: options.environment,
                        cwd: options.workingDirectory
                    }
                }
            };
        } else {
            return {
                run: {
                    command: scriptPath,
                    args: options.serverArgs,
                    options: {
                        env: options.environment,
                        cwd: options.workingDirectory
                    }
                },
                debug: {
                    command: scriptPath,
                    args: options.serverArgs,
                    options: {
                        env: options.environment,
                        cwd: options.workingDirectory
                    }
                }
            };
        }
    }

    /**
     * Collect all JAR files for classpath construction
     */
    private collectAllJars(): string[] {
        const libDir = this.extensionContext.asAbsolutePath(path.join('src', 'asciidoc', 'lib'));
        const jars: string[] = [];
        
        try {
            if (fs.existsSync(libDir)) {
                const files = fs.readdirSync(libDir);
                for (const file of files) {
                    if (file.endsWith('.jar')) {
                        jars.push(path.join(libDir, file));
                    }
                }
            }
        } catch (error) {
            this.logger.debug(`Warning - Could not collect JAR files: ${error}`);
        }
        
        return jars;
    }

    /**
     * Find Java executable
     */
    private findJavaExecutable(): string {
        // Check JAVA_HOME first
        const javaHome = process.env.JAVA_HOME;
        if (javaHome) {
            const javaPath = path.join(javaHome, 'bin', os.platform() === 'win32' ? 'java.exe' : 'java');
            if (fs.existsSync(javaPath)) {
                return javaPath;
            }
        }
        
        // Fall back to system PATH
        return os.platform() === 'win32' ? 'java.exe' : 'java';
    }

    /**
     * Create JVM arguments
     */
    private createJvmArgs(): string[] {
        const args: string[] = [];
        
        // Debug options
        if (this.configuration.debug.enabled) {
            const debugPort = this.configuration.debug.port;
            args.push(`-Xdebug`);
            args.push(`-Xrunjdwp:server=y,transport=dt_socket,address=${debugPort},suspend=n,quiet=y`);
        }
        
        // Performance tuning
        args.push('-Xms256m');
        args.push('-Xmx1024m');
        args.push('-XX:+UseG1GC');
        
        return args;
    }

    /**
     * Create environment variables
     */
    private createEnvironment(): NodeJS.ProcessEnv {
        const env = { ...process.env };
        
        // Set log directory
        env[constants.SERVER_PATHS.LOG_DIR_KEY] = this.configuration.logDirectory;
        
        // Set server configuration
        env.SERVER_PORT = this.configuration.languageServer.port.toString();
        env.SERVER_TIMEOUT = this.configuration.languageServer.timeout.toString();
        
        // Set performance options
        env.MAX_FILE_SIZE = this.configuration.performance.maxFileSize.toString();
        env.BACKGROUND_PROCESSING = this.configuration.performance.enableBackgroundProcessing.toString();
        
        // Debug options
        if (this.configuration.debug.verboseLogging) {
            env.DEBUG_VERBOSE = 'true';
        }
        
        return env;
    }

    /**
     * Start server with retry logic and timeout handling
     */
    private async startWithRetryAndTimeout(options: ServerLaunchOptions): Promise<void> {
        if (!this.client) {
            throw new Error('Language client not initialized');
        }

        const maxRetries = options.maxRetries;
        let retryCount = 0;

        while (retryCount <= maxRetries) {
            try {
                this.logger.debug(`Starting server (attempt ${retryCount + 1}/${maxRetries + 1})`);
                
                // Start with timeout
                const startPromise = this.client.start();
                const timeoutPromise = new Promise<never>((_, reject) => {
                    setTimeout(() => reject(new Error(`Server start timeout after ${options.timeout}ms`)), options.timeout);
                });
                
                await Promise.race([startPromise, timeoutPromise]);
                
                // Verify server is actually responding
                await this.waitForServerReady();
                
                return; // Success
            } catch (error) {
                retryCount++;
                const errorMessage = error instanceof Error ? error.message : 'Unknown error';
                this.logger.error(`Failed in startWithRetryAndTimeout attempt ${retryCount}: ${errorMessage}`);

                if (retryCount <= maxRetries) {
                    const delayMs = Math.min(1000 * Math.pow(2, retryCount - 1), 10000); // Exponential backoff, max 10s
                    this.logger.debug(`Retrying in ${delayMs}ms...`);
                    await new Promise(resolve => setTimeout(resolve, delayMs));
                } else {
                    throw error; // Re-throw after all retries exhausted
                }
            }
        }
    }

    /**
     * Wait for server to be ready and responding
     */
    private async waitForServerReady(): Promise<void> {
        const maxWaitTime = 30000; // 30 seconds
        const checkInterval = 1000; // 1 second
        const startTime = Date.now();
        
        while (Date.now() - startTime < maxWaitTime) {
            if (this.client && this.client.state === State.Running) {
                return; // Server is ready
            }
            
            await new Promise(resolve => setTimeout(resolve, checkInterval));
        }
        
        throw new Error('Server failed to become ready within timeout period');
    }

    /**
     * Start health monitoring
     */
    private startHealthMonitoring(interval: number): void {
        this.stopHealthMonitoring(); // Ensure no duplicate timers
        
        this.healthCheckTimer = setInterval(() => {
            this.performHealthCheck();
        }, interval);

        this.logger.debug(`Health monitoring started (interval: ${interval}ms)`);
    }

    /**
     * Stop health monitoring
     */
    private stopHealthMonitoring(): void {
        if (this.healthCheckTimer) {
            clearInterval(this.healthCheckTimer);
            this.healthCheckTimer = undefined;
        }
    }

    /**
     * Perform health check
     */
    private async performHealthCheck(): Promise<void> {
        try {
            const now = new Date();
            let isHealthy = false;
            
            if (this.client) {
                isHealthy = this.client.state === State.Running;
            }
            
            // Update health info
            const healthInfo: Partial<ServerHealthInfo> = {
                lastHealthCheck: now,
                status: isHealthy ? ServerStatus.RUNNING : ServerStatus.ERROR
            };
            
            if (this.healthInfo.startTime) {
                healthInfo.uptime = now.getTime() - this.healthInfo.startTime.getTime();
            }
            
            this.updateHealthInfo(healthInfo);
            this.updateStatusBar();
            
            // Log health status periodically
            if (!isHealthy) {
                this.logger.error(`Failed in performHealthCheck - server not running`);
            }

        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            this.logger.error(`Failed in performHealthCheck: ${errorMessage}`);
            this.updateHealthInfo({ status: ServerStatus.ERROR, errorMessage });
        }
    }

    /**
     * Update health information
     */
    private updateHealthInfo(update: Partial<ServerHealthInfo>): void {
        this.healthInfo = { ...this.healthInfo, ...update };
    }

    /**
     * Update status bar based on current health status
     */
    private updateStatusBar(): void {
        if (!this.statusBarItem || !this.configuration.ui.statusBar.enabled) {
            return;
        }
        
        switch (this.healthInfo.status) {
            case ServerStatus.STARTING:
                this.statusBarItem.text = '$(loading~spin) AsciiDoc Starting';
                this.statusBarItem.color = 'yellow';
                this.statusBarItem.tooltip = 'AsciiDoc Language Server is starting...';
                break;
                
            case ServerStatus.RUNNING:
                this.statusBarItem.text = constants.STATUS_BAR.TEXT;
                this.statusBarItem.color = undefined;
                this.statusBarItem.tooltip = constants.STATUS_BAR.TOOLTIP;
                break;
                
            case ServerStatus.STOPPING:
                this.statusBarItem.text = '$(loading~spin) AsciiDoc Stopping';
                this.statusBarItem.color = 'yellow';
                this.statusBarItem.tooltip = 'AsciiDoc Language Server is stopping...';
                break;
                
            case ServerStatus.STOPPED:
                this.statusBarItem.text = '$(circle-large-outline) AsciiDoc Stopped';
                this.statusBarItem.color = 'gray';
                this.statusBarItem.tooltip = 'AsciiDoc Language Server is stopped';
                break;
                
            case ServerStatus.ERROR:
                this.statusBarItem.text = '$(error) AsciiDoc Error';
                this.statusBarItem.color = 'red';
                this.statusBarItem.tooltip = `AsciiDoc Language Server error: ${this.healthInfo.errorMessage || 'Unknown error'}`;
                break;
                
            case ServerStatus.TIMEOUT:
                this.statusBarItem.text = '$(clock) AsciiDoc Timeout';
                this.statusBarItem.color = 'orange';
                this.statusBarItem.tooltip = 'AsciiDoc Language Server timed out';
                break;
                
            default:
                this.statusBarItem.text = '$(question) AsciiDoc Unknown';
                this.statusBarItem.color = 'gray';
                this.statusBarItem.tooltip = 'AsciiDoc Language Server status unknown';
                break;
        }
        
        this.statusBarItem.show();
    }

    /**
     * Dispose of resources
     */
    public dispose(): void {
        this.stopHealthMonitoring();
        
        // Dispose communication service
        if (this.communicationService) {
            this.communicationService.dispose();
        }
        
        if (this.client) {
            this.client.dispose();
            this.client = undefined;
        }
        
        if (this.serverProcess && !this.serverProcess.killed) {
            this.serverProcess.kill();
            this.serverProcess = undefined;
        }
    }
}