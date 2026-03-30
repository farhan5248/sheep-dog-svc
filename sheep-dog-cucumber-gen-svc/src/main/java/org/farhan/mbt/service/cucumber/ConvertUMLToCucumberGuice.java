package org.farhan.mbt.service.cucumber;

import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.mbt.core.UMLTestProject;
import org.farhan.mbt.cucumber.CucumberGuiceTestProject;
import org.farhan.mbt.cucumber.CucumberPathConverter;
import org.farhan.mbt.cucumber.CucumberTestProject;

public class ConvertUMLToCucumberGuice extends ConvertUMLToCucumber {

	public ConvertUMLToCucumberGuice(String tags, IResourceRepository fa, String serverHost, int serverPort) {
		super(tags, fa, serverHost, serverPort);
	}

	@Override
	public void initProjects() throws Exception {
		if (model != null && project != null) {
			return;
		}
		model = new UMLTestProject(this.tags, this.fa);
		project = new CucumberGuiceTestProject(this.tags, this.fa);
		model.init();
		project.init();
		this.pathConverter = new CucumberPathConverter(model, (CucumberTestProject) project);
	}
}
