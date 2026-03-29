package org.farhan.mbt.service.cucumber;

import org.farhan.dsl.grammar.IResourceRepository;

public class ConvertUMLToCucumber extends org.farhan.mbt.cucumber.ConvertUMLToCucumber {

	public ConvertUMLToCucumber(String tags, IResourceRepository fa, String serverHost, int serverPort) {
		super(tags, fa);
	}
}
