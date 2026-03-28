package org.farhan.sheepdog.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Future;

import org.eclipse.lsp4e.server.StreamConnectionProvider;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.xtext.ide.server.LanguageServerImpl;
import org.eclipse.xtext.ide.server.ServerModule;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import org.farhan.dsl.asciidoc.ide.AsciiDocIdeSetup;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

/**
 * Launches the Xtext-based SheepDog AsciiDoc language server in-process
 * and connects it to Eclipse via LSP4E.
 */
public class SheepDogConnectionProvider implements StreamConnectionProvider {

	private InputStream inputStream;
	private OutputStream outputStream;
	private Future<Void> future;

	@Override
	public void start() throws IOException {
		try {
			int bufferSize = 1024 * 1024; // 1MB buffer for large LSP messages
			PipedInputStream in = new PipedInputStream(bufferSize);
			PipedOutputStream out = new PipedOutputStream();
			PipedInputStream in2 = new PipedInputStream(bufferSize);
			PipedOutputStream out2 = new PipedOutputStream();

			in.connect(out2);
			out.connect(in2);

			// Register the AsciiDoc language with Xtext before starting the server
			System.err.println("SheepDog: Registering AsciiDoc language...");
			Injector langInjector = new AsciiDocIdeSetup().createInjectorAndDoEMFRegistration();

			// Manually register the language in the global registry since
			// ServiceLoader doesn't work across OSGi bundle classloaders
			IResourceServiceProvider resourceServiceProvider = langInjector.getInstance(IResourceServiceProvider.class);
			FileExtensionProvider extensionProvider = langInjector.getInstance(FileExtensionProvider.class);
			for (String ext : extensionProvider.getFileExtensions()) {
				IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().put(ext, resourceServiceProvider);
			}
			System.err.println("SheepDog: AsciiDoc language registered.");

			Injector injector = Guice.createInjector(Modules.override(new ServerModule()).with(new AbstractModule() {
				@Override
				protected void configure() {
					bind(IResourceServiceProvider.Registry.class).toInstance(IResourceServiceProvider.Registry.INSTANCE);
				}
			}));
			LanguageServerImpl languageServer = injector.getInstance(LanguageServerImpl.class);
			System.err.println("SheepDog: Language server created.");

			Launcher<LanguageClient> launcher = Launcher.createLauncher(
					languageServer, LanguageClient.class, in2, out2);
			languageServer.connect(launcher.getRemoteProxy());
			future = launcher.startListening();

			inputStream = in;
			outputStream = out;
			System.err.println("SheepDog: Server started successfully.");
		} catch (Exception e) {
			System.err.println("SheepDog: Server start failed:");
			e.printStackTrace(System.err);
			throw new IOException("Failed to start language server", e);
		}
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public InputStream getErrorStream() {
		return null;
	}

	@Override
	public void stop() {
		if (future != null) {
			future.cancel(true);
		}
		try {
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();
		} catch (IOException e) {
			// ignore
		}
	}
}
