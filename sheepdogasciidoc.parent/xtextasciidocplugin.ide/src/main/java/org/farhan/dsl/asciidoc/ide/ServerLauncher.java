/**
 * Copyright (c) 2016, 2022 itemis AG (http://www.itemis.de) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.farhan.dsl.asciidoc.ide;

import org.slf4j.Logger;
import org.farhan.dsl.grammar.SheepDogLoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.xtext.ide.server.LanguageServerImpl;
import org.eclipse.xtext.ide.server.ServerModule;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * @author Sven Efftinge - Initial contribution and API
 * @since 2.11
 */
public class ServerLauncher {
	
	private static final Logger logger = SheepDogLoggerFactory.getLogger(ServerLauncher.class);
	
	// TODO this has hardcoded names
	private static boolean IS_DEBUG = true;

	public static void main(final String[] args) throws Exception {
		logger.debug("Entering {}", "main");
		InputStream stdin = System.in;
		PrintStream stdout = System.out;
		ServerLauncher.redirectStandardStreams();
		ServerLauncher launcher = Guice.createInjector(new ServerModule()).getInstance(ServerLauncher.class);
		launcher.start(stdin, stdout);
		logger.debug("Exiting {}", "main");
	}

	@Inject
	private LanguageServerImpl languageServer;

	public void start(final InputStream in, final OutputStream out) throws Exception {
		logger.debug("Entering {}", "start");
		logger.info("Starting Xtext Language Server.");
		String id = ServerLauncher.class.getName() + "-"
				+ new Timestamp(System.currentTimeMillis()).toString().replaceAll(" ", "_");
		Launcher<LanguageClient> launcher = Launcher.createLauncher(languageServer, LanguageClient.class, in, out, true,
				new PrintWriter(new FileOutputStream((("/Users/Farhan/logs/xxx-" + id) + ".log")), true));
		languageServer.connect(launcher.getRemoteProxy());
		Future<Void> future = launcher.startListening();
		logger.info("started.");
		while (!future.isDone()) {
			Thread.sleep(10_000l);
		}
		logger.debug("Exiting {}", "start");
	}

	public static void redirectStandardStreams() throws Exception {
		logger.debug("Entering {}", "redirectStandardStreams");
		System.setIn(new ByteArrayInputStream(new byte[0]));
		String id = ServerLauncher.class.getName() + "-"
				+ new Timestamp(System.currentTimeMillis()).toString().replaceAll(" ", "_");
		if (ServerLauncher.IS_DEBUG) {
			FileOutputStream stdFileOut = new FileOutputStream((("/Users/Farhan/logs/out-" + id) + ".log"));
			System.setOut(new PrintStream(stdFileOut, true));
			FileOutputStream stdFileErr = new FileOutputStream((("/Users/Farhan/logs/error-" + id) + ".log"));
			System.setErr(new PrintStream(stdFileErr, true));
		} else {
			System.setOut(new PrintStream(new ByteArrayOutputStream()));
			System.setErr(new PrintStream(new ByteArrayOutputStream()));
		}
		logger.debug("Exiting {}", "redirectStandardStreams");
	}
}
