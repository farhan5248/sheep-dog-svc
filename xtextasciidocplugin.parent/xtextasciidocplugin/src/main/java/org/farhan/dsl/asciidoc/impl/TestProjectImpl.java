package org.farhan.dsl.asciidoc.impl;

import java.io.File;
import org.slf4j.Logger;
import org.farhan.dsl.grammar.SheepDogLoggerFactory;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.dsl.grammar.IStepObject;
import org.farhan.dsl.grammar.ITestDocument;
import org.farhan.dsl.grammar.ITestProject;
import org.farhan.dsl.grammar.ITestSuite;
import org.farhan.dsl.grammar.SheepDogFactory;

public class TestProjectImpl implements ITestProject {

	private static Logger logger = SheepDogLoggerFactory.getLogger(TestProjectImpl.class);

	private IResourceRepository sr;
	private String projectPath;
	public final String baseDir;
	public final String layer1dir;
	public final String layer2dir;

	TestProjectImpl(IResourceRepository sr) {
		// TODO In the future the project name should be accessible here. The
		// constructor should get a path that includes the project name. It
		// can then intercept the project name and save it.
		this.sr = sr;
		baseDir = "src/test/resources/asciidoc";
		layer1dir = "src/test/resources/asciidoc/specs";
		layer2dir = "src/test/resources/asciidoc/stepdefs";
		projectPath = null;
	}

	public boolean addStepObject(IStepObject value) {
		try {
			sr.put("", projectPath + "/" + baseDir + "/" + value.getFullName(), ((StepObjectImpl) value).getContent());
			return true;
		} catch (Exception e) {
			logger.error("Couldn't write step object:", e);
			return false;
		}
	}

	public boolean addTestSuite(ITestSuite value) {
		try {
			sr.put("", projectPath + "/" + baseDir + "/" + value.getFullName(), ((TestSuiteImpl) value).getContent());
			return true;
		} catch (Exception e) {
			logger.error("Couldn't write test suite:", e);
			return false;
		}
	}

	@Override
	public void setFileExtension(String value) {
		// file extension is fixed for this implementation
	}

	@Override
	public String getFileExtension() {
		// TODO make a static config object for this and they layer 2 directory
		return ".asciidoc";
	}

	public String getName() {
		return projectPath;
	}

	public ITestDocument getTestDocument(String fullName) {
		try {
			if (fullName.startsWith("stepdefs/")) {
				String filePath = projectPath + "/" + baseDir + "/" + fullName;
				if (sr.contains("", filePath)) {
					String text = sr.get("", filePath);
					if (text.isEmpty()) {
						logger.error("Couldn't load TestDocument for, file is empty: " + fullName);
					} else {
						StepObjectImpl stepObject = (StepObjectImpl) SheepDogFactory.instance.createStepObject();
						stepObject.setFullName(fullName);
						stepObject.setContent(text);
						return stepObject;
					}
				}
			} else if (fullName.startsWith("specs/")) {
				String filePath = projectPath + "/" + baseDir + "/" + fullName;
				if (sr.contains("", filePath)) {
					String text = sr.get("", filePath);
					if (text.isEmpty()) {
						logger.error("Couldn't load TestDocument for, file is empty: " + fullName);
					} else {
						TestSuiteImpl testSuite = (TestSuiteImpl) SheepDogFactory.instance.createTestSuite();
						testSuite.setFullName(fullName);
						testSuite.setContent(text);
						return testSuite;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Couldn't load TestDocument for: " + fullName, e);
		}
		return null;
	}

	@Override
	public EList<ITestDocument> getTestDocumentList() {
		EList<ITestDocument> objects = new BasicEList<ITestDocument>();
		try {
			// TODO instead of empty tags, append it to the prefix?
			for (String stepObjectFileName : sr.list("", projectPath + "/" + layer2dir, getFileExtension())) {
				objects.add(getTestDocument("stepdefs/" + stepObjectFileName.replace(projectPath + "/" + layer2dir + "/", "")));
			}
			for (String testSuiteFileName : sr.list("", projectPath + "/" + layer1dir, getFileExtension())) {
				objects.add(getTestDocument("specs/" + testSuiteFileName.replace(projectPath + "/" + layer1dir + "/", "")));
			}
		} catch (Exception e) {
			logger.error("Couldn't get TestDocument list:", e);
		}
		return objects;
	}

	public void setName(String projectPath) {
		this.projectPath = projectPath.replace(File.separator, "/");
	}
}
