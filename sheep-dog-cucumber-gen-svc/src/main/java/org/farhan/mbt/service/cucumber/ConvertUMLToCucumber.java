package org.farhan.mbt.service.cucumber;

import java.util.ArrayList;

import org.farhan.dsl.cucumber.cucumber.Background;
import org.farhan.dsl.cucumber.cucumber.Examples;
import org.farhan.dsl.cucumber.cucumber.ExamplesTable;
import org.farhan.dsl.cucumber.cucumber.Row;
import org.farhan.dsl.cucumber.cucumber.Scenario;
import org.farhan.dsl.cucumber.cucumber.ScenarioOutline;
import org.farhan.dsl.cucumber.cucumber.Step;
import org.farhan.dsl.cucumber.cucumber.StepTable;
import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.mbt.cucumber.CucumberClass;
import org.farhan.mbt.cucumber.CucumberFeature;
import org.farhan.mbt.cucumber.CucumberInterface;
import org.farhan.mbt.model.UMLStepDefinition;
import org.farhan.mbt.model.UMLStepObject;
import org.farhan.mbt.model.UMLStepParameters;
import org.farhan.mbt.model.UMLTestCase;
import org.farhan.mbt.model.UMLTestData;
import org.farhan.mbt.model.UMLTestSetup;
import org.farhan.mbt.model.UMLTestStep;
import org.farhan.mbt.model.UMLTestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

public class ConvertUMLToCucumber extends org.farhan.mbt.cucumber.ConvertUMLToCucumber {

	private static final Logger log = LoggerFactory.getLogger(ConvertUMLToCucumber.class);
	private static final int RETRY_COUNT = 10;
	private final RestClient restClient;

	public ConvertUMLToCucumber(String tags, IResourceRepository fa, String serverHost, int serverPort) {
		super(tags, fa);
		this.restClient = RestClient.builder()
				.baseUrl("http://" + serverHost + ":" + serverPort)
				.build();
	}

	@Override
	public String convertFile(String path, String content) throws Exception {
		initProjects();
		String projectId = tags.isEmpty() ? "default" : tags;
		if (path.startsWith(project.getDir(project.TEST_CASES))) {
			String qualifiedName = pathConverter.findUMLPath(path);
			UMLTestSuite srcTestSuite = getResource(
					"/uml/project/" + projectId + "/suite?qualifiedName=" + qualifiedName,
					UMLTestSuite.class);

			tgtObjTestSuite = (CucumberFeature) project.addFile(path);
			tgtObjTestSuite.parse(content);
			tgtObjTestSuite.addFeatureName(srcTestSuite.getName());
			convertTestSuite(srcTestSuite);
			return tgtObjTestSuite.toString();
		} else {
			String qualifiedName = pathConverter.findUMLPath(path);
			UMLStepObject srcStepObject = getResource(
					"/uml/project/" + projectId + "/object?qualifiedName=" + qualifiedName,
					UMLStepObject.class);
			if (path.startsWith(project.getDir(project.TEST_STEPS))) {
				tgtObjStepObject = (CucumberClass) project.addFile(path);
			} else {
				tgtObjStepObject = (CucumberInterface) project.addFile(path);
			}
			tgtObjStepObject.parse(content);
			convertStepObject(srcStepObject);
			return tgtObjStepObject.toString();
		}
	}

	private void convertStepObject(UMLStepObject srcStepObject) throws Exception {
		log.debug("step object: " + srcStepObject.getName());
		for (String umlStepDefinitionLink : srcStepObject.getStepDefinitionList()) {
			ArrayList<String> parametersListMerged = new ArrayList<String>();
			UMLStepDefinition srcStepDefinition = getResource(
					"/uml/" + umlStepDefinitionLink, UMLStepDefinition.class);

			for (String umlStepParametersLink : srcStepDefinition.getStepParametersList()) {
				UMLStepParameters srcStepParameters = getResource(
						"/uml/" + umlStepParametersLink, UMLStepParameters.class);
				for (ArrayList<String> srcRow : srcStepParameters.getStepParametersTable()) {
					for (String srcCell : srcRow) {
						if (!parametersListMerged.contains(srcCell.trim())) {
							parametersListMerged.add(srcCell.trim());
						}
					}
				}
			}
			tgtObjStepObject.addStepDefinition(srcStepDefinition.getNameLong(), parametersListMerged);
		}
	}

	private void convertTestCase(Scenario scenario, UMLTestCase srcTestCase) throws Exception {
		log.debug("test case: " + srcTestCase.getName());

		for (String tag : srcTestCase.getTags()) {
			tgtObjTestSuite.addScenarioTag(scenario, tag);
		}

		if (!srcTestCase.getDescription().isEmpty()) {
			for (String statement : srcTestCase.getDescription()) {
				tgtObjTestSuite.addScenarioStatement(scenario, statement);
			}
		}

		for (String umlTestStepLink : srcTestCase.getTestStepList()) {
			UMLTestStep srcStep = getResource("/uml/" + umlTestStepLink, UMLTestStep.class);
			convertTestStep(tgtObjTestSuite.addStep(scenario, srcStep.getNameLong()), srcStep);
		}
	}

	private void convertTestCaseWithData(ScenarioOutline scenarioOutline, UMLTestCase srcTestCase) throws Exception {
		log.debug("test case: " + srcTestCase.getName());
		for (String tag : srcTestCase.getTags()) {
			tgtObjTestSuite.addScenarioOutlineTag(scenarioOutline, tag);
		}
		if (!srcTestCase.getDescription().isEmpty()) {
			for (String statement : srcTestCase.getDescription()) {
				tgtObjTestSuite.addScenarioOutlineStatement(scenarioOutline, statement);
			}
		}

		for (String umlTestStepLink : srcTestCase.getTestStepList()) {
			UMLTestStep srcStep = getResource("/uml/" + umlTestStepLink, UMLTestStep.class);
			convertTestStep(tgtObjTestSuite.addStep(scenarioOutline, srcStep.getNameLong()), srcStep);
		}
		for (String umlTestDataLink : srcTestCase.getTestDataList()) {
			UMLTestData srcTestData = getResource("/uml/" + umlTestDataLink, UMLTestData.class);
			convertTestData(tgtObjTestSuite.addExamples(scenarioOutline, srcTestData.getName()), srcTestData);
		}
	}

	private void convertTestData(Examples examples, UMLTestData srcTestData) {
		log.debug("test data: " + srcTestData.getName());
		for (String c : srcTestData.getTags()) {
			tgtObjTestSuite.addExamplesTag(examples, c);
		}

		if (!srcTestData.getDescription().isEmpty()) {
			for (String statement : srcTestData.getDescription()) {
				tgtObjTestSuite.addExamplesStatement(examples, statement);
			}
		}
		ExamplesTable examplesTable = tgtObjTestSuite.addExamplesTable(examples);
		for (ArrayList<String> srcRow : srcTestData.getDataTable()) {
			Row row = tgtObjTestSuite.addExamplesTableRow(examplesTable);
			for (String srcCell : srcRow) {
				tgtObjTestSuite.addCell(row.getCells(), srcCell);
			}
		}
	}

	private void convertTestSetup(Background background, UMLTestSetup srcTestSetup) throws Exception {
		log.debug("test setup: " + srcTestSetup.getName());
		if (!srcTestSetup.getDescription().isEmpty()) {
			for (String statement : srcTestSetup.getDescription()) {
				tgtObjTestSuite.addBackgroundStatement(background, statement);
			}
		}

		for (String umlTestStepLink : srcTestSetup.getTestStepList()) {
			UMLTestStep srcStep = getResource("/uml/" + umlTestStepLink, UMLTestStep.class);
			convertTestStep(tgtObjTestSuite.addStep(background, srcStep.getNameLong()), srcStep);
		}
	}

	private void convertTestStep(Step step, UMLTestStep srcStep) throws Exception {
		log.debug("test step: " + srcStep.getName());
		if (srcStep.getStepText() != null) {
			tgtObjTestSuite.setDocString(step, srcStep.getStepText());
		} else if (srcStep.getStepTable() != null) {
			StepTable stepTable = tgtObjTestSuite.addStepTable(step);
			for (ArrayList<String> srcRow : srcStep.getStepTable()) {
				Row row = tgtObjTestSuite.addStepTableRow(stepTable);
				for (String srcCell : srcRow) {
					tgtObjTestSuite.addCell(row.getCells(), srcCell);
				}
			}
		}
	}

	private void convertTestSuite(UMLTestSuite srcTestSuite) throws Exception {
		log.debug("test suite: " + srcTestSuite.getName());

		for (String tag : srcTestSuite.getTags()) {
			tgtObjTestSuite.addFeatureTag(tag);
		}

		if (!srcTestSuite.getDescription().isEmpty()
				&& !String.join("\n", srcTestSuite.getDescription()).equals(tgtObjTestSuite.getFeatureDescription())) {
			tgtObjTestSuite.clearFeatureStatement();
			for (String statement : srcTestSuite.getDescription()) {
				tgtObjTestSuite.addFeatureStatement(statement);
			}
		}
		if (srcTestSuite.getTestSetup() != null) {
			UMLTestSetup srcTestSetup = getResource(
					"/uml/" + srcTestSuite.getTestSetup(), UMLTestSetup.class);
			convertTestSetup(tgtObjTestSuite.addBackground(srcTestSetup.getName()), srcTestSetup);
		}
		for (String testCaseLink : srcTestSuite.getTestCaseList()) {
			UMLTestCase srcTestCase = getResource("/uml/" + testCaseLink, UMLTestCase.class);
			if (!srcTestCase.getTestDataList().isEmpty()) {
				convertTestCaseWithData(tgtObjTestSuite.addScenarioOutline(srcTestCase.getName()), srcTestCase);
			} else {
				convertTestCase(tgtObjTestSuite.addScenario(srcTestCase.getName()), srcTestCase);
			}
		}
	}

	@Override
	public ArrayList<String> getFileNames() throws Exception {
		initProjects();
		ArrayList<String> objects = new ArrayList<String>();
		String projectId = tags.isEmpty() ? "default" : tags;

		org.farhan.mbt.model.UMLTestProject srcTestProject = getResource(
				"/uml/project/" + projectId, org.farhan.mbt.model.UMLTestProject.class);
		for (String testSuiteLink : srcTestProject.getTestSuiteList()) {
			UMLTestSuite srcTestSuite = getResource("/uml/" + testSuiteLink, UMLTestSuite.class);
			objects.add(pathConverter.convertFilePath(srcTestSuite.getQualifiedName(), project.TEST_CASES));
		}

		for (String stepObjectLink : srcTestProject.getStepObjectList()) {
			UMLStepObject srcStepObject = getResource("/uml/" + stepObjectLink, UMLStepObject.class);
			objects.add(pathConverter.convertFilePath(srcStepObject.getQualifiedName(), project.TEST_STEPS));
			objects.add(pathConverter.convertFilePath(srcStepObject.getQualifiedName(), project.TEST_OBJECTS));
		}
		return objects;
	}

	private <T> T getResource(String uri, Class<T> type) throws Exception {
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.get().uri(uri).retrieve().body(type);
			} catch (Exception e) {
				log.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while getting resource: " + uri);
	}
}
