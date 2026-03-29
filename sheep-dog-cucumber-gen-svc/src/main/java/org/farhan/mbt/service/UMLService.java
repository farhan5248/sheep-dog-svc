package org.farhan.mbt.service;

import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.mbt.core.UMLTestProject;
import org.farhan.mbt.model.UMLTestStep;
import org.farhan.mbt.model.UMLTestSuite;
import org.farhan.mbt.model.UMLStepDefinition;
import org.farhan.mbt.model.UMLStepObject;
import org.farhan.mbt.model.UMLStepParameters;
import org.farhan.mbt.model.UMLTestCase;
import org.farhan.mbt.model.UMLTestData;
import org.farhan.mbt.model.UMLTestSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UMLService {

    private static final Logger logger = LoggerFactory.getLogger(UMLService.class);
    private final IResourceRepository repository;
    private String cachedProjectId;
    private UMLTestProject cachedModel;

    @Autowired
    public UMLService(IResourceRepository repository) {
        this.repository = repository;
    }

    private synchronized UMLTestProject getModel(String projectId) throws Exception {
        if (cachedModel == null || !projectId.equals(cachedProjectId)) {
            cachedModel = new UMLTestProject(projectId, repository);
            cachedModel.init();
            cachedProjectId = projectId;
        }
        return cachedModel;
    }

    public synchronized void invalidateCache() {
        cachedModel = null;
        cachedProjectId = null;
    }

    // TODO make these int
    public UMLTestStep getUMLTestStep(String projectId, String suiteId, String caseId, String stepId)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLTestStep testStep = null;
        if (caseId == null || caseId.isEmpty()) {
            testStep = new UMLTestStep(
                    model.getTestSuiteList().get(Integer.parseInt(suiteId)).getTestSetup().getTestStepList()
                            .get(Integer.parseInt(stepId)));
        } else {
            testStep = new UMLTestStep(model.getTestSuiteList().get(Integer.parseInt(suiteId))
                    .getTestCaseList().get(Integer.parseInt(caseId)).getTestStepList()
                    .get(Integer.parseInt(stepId)));
        }
        return testStep;
    }

    public UMLTestCase getUMLTestCase(String projectId, String suiteId, String caseId)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLTestCase testCase = new UMLTestCase(model.getTestSuiteList().get(Integer.parseInt(suiteId))
                .getTestCaseList().get(Integer.parseInt(caseId)));
        return testCase;
    }

    public UMLTestSetup getUMLTestSetup(String projectId, String suiteId)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLTestSetup testSetup = new UMLTestSetup(
                model.getTestSuiteList().get(Integer.parseInt(suiteId)).getTestSetup());
        return testSetup;
    }

    public UMLTestData getUMLTestData(String projectId, String suiteId, String caseId, String dataId)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLTestData testData = new UMLTestData(model.getTestSuiteList().get(Integer.parseInt(suiteId))
                .getTestCaseList().get(Integer.parseInt(caseId)).getTestDataList()
                .get(Integer.parseInt(dataId)));
        return testData;
    }

    public org.farhan.mbt.model.UMLTestProject getUMLTestProject(String projectId)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        org.farhan.mbt.model.UMLTestProject umlTestProject = new org.farhan.mbt.model.UMLTestProject(model);
        return umlTestProject;
    }

    public UMLStepParameters getUMLStepParameters(String projectId, String objectId, String definitionId,
            String parametersId) throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLStepParameters stepParameters = new UMLStepParameters(
                model.getStepObjectList().get(Integer.parseInt(objectId)).getStepDefinitionList()
                        .get(Integer.parseInt(definitionId)).getStepParametersList()
                        .get(Integer.parseInt(parametersId)));
        return stepParameters;
    }

    public UMLStepDefinition getUMLStepDefinition(String projectId, String objectId, String definitionId)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLStepDefinition stepDefinition = new UMLStepDefinition(model.getStepObjectList()
                .get(Integer.parseInt(objectId)).getStepDefinitionList().get(Integer.parseInt(definitionId)));
        return stepDefinition;
    }

    public UMLStepObject getUMLStepObject(String projectId, String objectId, String qualifiedName)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLStepObject stepObject = new UMLStepObject(
                objectId == null ? model.getStepObject(qualifiedName)
                        : model.getStepObjectList().get(Integer.parseInt(objectId)));
        return stepObject;
    }

    public UMLTestSuite getUMLTestSuite(String projectId, String suiteId, String qualifiedName)
            throws Exception {
        UMLTestProject model = getModel(projectId);
        UMLTestSuite testSuite = new UMLTestSuite(
                suiteId == null ? model.getTestSuite(qualifiedName)
                        : model.getTestSuiteList().get(Integer.parseInt(suiteId)));
        return testSuite;
    }
}
