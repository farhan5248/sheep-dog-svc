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

import org.farhan.dsl.asciidoc.ide.AsciiDocIdeSetup;

import com.google.inject.Guice;
import com.google.inject.Injector;

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
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in2 = new PipedInputStream();
		PipedOutputStream out2 = new PipedOutputStream();

		in.connect(out2);
		out.connect(in2);

		// Register the AsciiDoc language with Xtext before starting the server
		new AsciiDocIdeSetup().createInjectorAndDoEMFRegistration();

		Injector injector = Guice.createInjector(new ServerModule());
		LanguageServerImpl languageServer = injector.getInstance(LanguageServerImpl.class);

		Launcher<LanguageClient> launcher = Launcher.createLauncher(
				languageServer, LanguageClient.class, in2, out2);
		languageServer.connect(launcher.getRemoteProxy());
		future = launcher.startListening();

		inputStream = in;
		outputStream = out;
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
