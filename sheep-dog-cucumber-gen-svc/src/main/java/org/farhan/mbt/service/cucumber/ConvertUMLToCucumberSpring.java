package org.farhan.mbt.service.cucumber;

import org.farhan.dsl.grammar.IResourceRepository;

public class ConvertUMLToCucumberSpring extends org.farhan.mbt.cucumber.ConvertUMLToCucumberSpring {

	public ConvertUMLToCucumberSpring(String tags, IResourceRepository fa, String serverHost, int serverPort) {
		super(tags, fa);
	}
}
