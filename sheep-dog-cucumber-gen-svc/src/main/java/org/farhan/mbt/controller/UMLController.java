package org.farhan.mbt.controller;

import org.farhan.mbt.model.UMLTestProject;
import org.farhan.mbt.model.UMLStepObject;
import org.farhan.mbt.model.UMLStepDefinition;
import org.farhan.mbt.model.UMLStepParameters;
import org.farhan.mbt.model.UMLTestCase;
import org.farhan.mbt.model.UMLTestData;
import org.farhan.mbt.model.UMLTestSetup;
import org.farhan.mbt.model.UMLTestStep;
import org.farhan.mbt.model.UMLTestSuite;
import org.farhan.mbt.service.UMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@ConfigurationProperties(prefix = "sheepdog")
@RestController
@RequestMapping("/uml")
public class UMLController {

    Logger logger = LoggerFactory.getLogger(UMLController.class);
    private final UMLService service;

    @Autowired
    public UMLController(UMLService service) {
        this.service = service;
    }

    @GetMapping("/project/{projectId}/suite/{suiteId}/setup/step/{stepId}")
    public UMLTestStep getUMLTestStepTestSetup(
            @PathVariable String projectId,
            @PathVariable String suiteId,
            @PathVariable String stepId) throws Exception {
        logger.info("Starting getUMLTestStepTestSetup");
        UMLTestStep testStep = service.getUMLTestStep(projectId.contentEquals("default") ? "" : projectId, suiteId,
                null,
                stepId);
        logger.info("Ending getUMLTestStepTestSetup");
        return testStep;
    }

    @GetMapping("/project/{projectId}/suite/{suiteId}/case/{caseId}/data/{dataId}")
    public UMLTestData getUMLTestData(
            @PathVariable String projectId,
            @PathVariable String suiteId,
            @PathVariable String caseId,
            @PathVariable String dataId) throws Exception {
        logger.info("Starting getUMLTestStepTestCase");
        UMLTestData testData = service.getUMLTestData(projectId.contentEquals("default") ? "" : projectId, suiteId,
                caseId,
                dataId);
        logger.info("Ending getUMLTestStepTestCase");
        return testData;
    }

    @GetMapping("/project/{projectId}/object/{objectId}/definition/{definitionId}/parameters/{parametersId}")
    public UMLStepParameters getUMLStepParameters(
            @PathVariable String projectId,
            @PathVariable String objectId,
            @PathVariable String definitionId,
            @PathVariable String parametersId) throws Exception {
        logger.info("Starting getUMLStepParameters");
        UMLStepParameters testStep = service.getUMLStepParameters(projectId.contentEquals("default") ? "" : projectId,
                objectId,
                definitionId,
                parametersId);
        logger.info("Ending getUMLStepParameters");
        return testStep;
    }

    @GetMapping("/project/{projectId}/object/{objectId}/definition/{definitionId}")
    public UMLStepDefinition getUMLStepDefinition(
            @PathVariable String projectId,
            @PathVariable String objectId,
            @PathVariable String definitionId) throws Exception {
        logger.info("Starting getUMLStepDefinition");
        UMLStepDefinition testStep = service.getUMLStepDefinition(projectId.contentEquals("default") ? "" : projectId,
                objectId,
                definitionId);
        logger.info("Ending getUMLStepDefinition");
        return testStep;
    }

    @GetMapping("/project/{projectId}/object")
    public UMLStepObject getUMLStepObjectByName(
            @PathVariable String projectId,
            @RequestParam(value = "qualifiedName") String qualifiedName) throws Exception {
        logger.info("Starting getUMLStepObjectByName");
        UMLStepObject stepObject = service.getUMLStepObject(projectId.contentEquals("default") ? "" : projectId,
                null, qualifiedName);
        logger.info("Ending getUMLStepObjectByName");
        return stepObject;
    }

    @GetMapping("/project/{projectId}/object/{objectId}")
    public UMLStepObject getUMLStepObjectById(
            @PathVariable String projectId,
            @PathVariable String objectId) throws Exception {
        logger.info("Starting getUMLStepObjectById");
        UMLStepObject stepObject = service.getUMLStepObject(projectId.contentEquals("default") ? "" : projectId,
                objectId, null);
        logger.info("Ending getUMLStepObjectById");
        return stepObject;
    }

    @GetMapping("/project/{projectId}/suite/{suiteId}/case/{caseId}/step/{stepId}")
    public UMLTestStep getUMLTestStepTestCase(
            @PathVariable String projectId,
            @PathVariable String suiteId,
            @PathVariable String caseId,
            @PathVariable String stepId) throws Exception {
        logger.info("Starting getUMLTestStepTestCase");
        UMLTestStep testStep = service.getUMLTestStep(projectId.contentEquals("default") ? "" : projectId, suiteId,
                caseId,
                stepId);
        logger.info("Ending getUMLTestStepTestCase");
        return testStep;
    }

    @GetMapping("/project/{projectId}/suite/{suiteId}/case/{caseId}")
    public UMLTestCase getUMLTestCase(
            @PathVariable String projectId,
            @PathVariable String suiteId,
            @PathVariable String caseId) throws Exception {
        logger.info("Starting getUMLTestCase");
        UMLTestCase testCase = service.getUMLTestCase(projectId.contentEquals("default") ? "" : projectId, suiteId,
                caseId);
        logger.info("Ending getUMLTestCase");
        return testCase;
    }

    @GetMapping("/project/{projectId}/suite/{suiteId}/setup")
    public UMLTestSetup getUMLTestSetup(
            @PathVariable String projectId,
            @PathVariable String suiteId) throws Exception {
        logger.info("Starting getUMLTestSetup");
        UMLTestSetup testSetup = service.getUMLTestSetup(projectId.contentEquals("default") ? "" : projectId, suiteId);
        logger.info("Ending getUMLTestSetup");
        return testSetup;
    }

    @GetMapping("/project/{projectId}/suite")
    public UMLTestSuite getUMLTestSuiteByName(
            @PathVariable String projectId,
            @RequestParam(value = "qualifiedName") String qualifiedName) throws Exception {
        logger.info("Starting getUMLTestSuiteByName");
        UMLTestSuite testSuite = service.getUMLTestSuite(projectId.contentEquals("default") ? "" : projectId,
                null, qualifiedName);
        logger.info("Ending getUMLTestSuiteByName");
        return testSuite;
    }

    @GetMapping("/project/{projectId}/suite/{suiteId}")
    public UMLTestSuite getUMLTestSuiteById(
            @PathVariable String projectId,
            @PathVariable String suiteId) throws Exception {
        logger.info("Starting getUMLTestSuiteById");
        UMLTestSuite testSuite = service.getUMLTestSuite(projectId.contentEquals("default") ? "" : projectId,
                suiteId, null);
        logger.info("Ending getUMLTestSuiteById");
        return testSuite;
    }

    @GetMapping("/project/{projectId}")
    public UMLTestProject getUMLTestProject(
            @PathVariable String projectId) throws Exception {
        logger.info("Starting getUMLTestProject");
        UMLTestProject testProject = service.getUMLTestProject(projectId.contentEquals("default") ? "" : projectId);
        logger.info("Ending getUMLTestProject");
        return testProject;
    }
}
