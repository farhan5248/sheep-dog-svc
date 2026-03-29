package org.farhan.mbt.service.cucumber;

import org.farhan.dsl.grammar.IResourceRepository;

public class ConvertUMLToCucumberGuice extends org.farhan.mbt.cucumber.ConvertUMLToCucumberGuice {

	public ConvertUMLToCucumberGuice(String tags, IResourceRepository fa, String serverHost, int serverPort) {
		super(tags, fa);
	}
}
