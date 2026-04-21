package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.fake.FakeProcessStarter;
import org.farhan.common.MavenTestObject;
import org.farhan.mbt.maven.GenFromExistingMojo;
import org.farhan.objects.darmok.GenFromExistingGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class GenFromExistingGoalImpl extends MavenTestObject implements GenFromExistingGoal {

	@Override
	public void setClaudeRgrGreenCommandExecutedAndSuccessfulAfterRetries(HashMap<String, String> keyMap) {
		setProperty("claudeGreenMode", "retry-success");
	}

	@Override
	public void setClaudeRgrGreenCommandExecutedButFailsAfterReachingRetryLimit(HashMap<String, String> keyMap) {
		setProperty("claudeGreenMode", "retry-exhaust");
	}

	@Override
	public void setClaudeRgrGreenCommandExecutedButFailedNonRetryably(HashMap<String, String> keyMap) {
		setProperty("claudeGreenMode", "non-retryable");
	}

	@Override
	public void setClaudeRgrGreenCommandPattern(HashMap<String, String> keyMap) {
		setProperty("claudeGreenPattern", keyMap.get("Pattern"));
	}

	@Override
	public void setClaudeRgrGreenCommandExit(HashMap<String, String> keyMap) {
		setProperty("claudeGreenExit", keyMap.get("Exit"));
	}

	@Override
	public void setClaudeRgrGreenCommandOutput(HashMap<String, String> keyMap) {
		setProperty("claudeGreenOutput", keyMap.get("Output"));
	}

	@Override
	public void setClaudeRgrRefactorCommandExecutedAndSuccessfulAfterRetries(HashMap<String, String> keyMap) {
		setProperty("claudeRefactorMode", "retry-success");
	}

	@Override
	public void setClaudeRgrRefactorCommandPattern(HashMap<String, String> keyMap) {
		setProperty("claudeRefactorPattern", keyMap.get("Pattern"));
	}

	@Override
	public void setGitCommandExecutedToVerifyTheWorkspaceIsClean(HashMap<String, String> keyMap) {
		setProperty("gitWorkspaceState", "clean");
	}

	@Override
	public void setMvnAsciidoctorToUmlCommandExecutedButFailed(HashMap<String, String> keyMap) {
		setProperty("mvnAsciidoctorMode", "fail");
	}

	@Override
	public void setMvnAsciidoctorToUmlCommandExit(HashMap<String, String> keyMap) {
		setProperty("mvnAsciidoctorExit", keyMap.get("Exit"));
	}

	@Override
	public void setMvnAsciidoctorToUmlCommandOutput(HashMap<String, String> keyMap) {
		setProperty("mvnAsciidoctorOutput", keyMap.get("Output"));
	}

	@Override
	public void setMvnUmlToCucumberGuiceCommandExecutedButFailed(HashMap<String, String> keyMap) {
		setProperty("mvnUmlToCucumberMode", "fail");
	}

	@Override
	public void setMvnUmlToCucumberGuiceCommandExit(HashMap<String, String> keyMap) {
		setProperty("mvnUmlToCucumberExit", keyMap.get("Exit"));
	}

	@Override
	public void setMvnUmlToCucumberGuiceCommandOutput(HashMap<String, String> keyMap) {
		setProperty("mvnUmlToCucumberOutput", keyMap.get("Output"));
	}

	@Override
	public void setMvnTestCommandExecutedButFailed(HashMap<String, String> keyMap) {
		setProperty("mvnTestMode", "fail");
	}

	@Override
	public void setMvnTestCommandExit(HashMap<String, String> keyMap) {
		setProperty("mvnTestExit", keyMap.get("Exit"));
	}

	@Override
	public void setMvnTestCommandOutput(HashMap<String, String> keyMap) {
		setProperty("mvnTestOutput", keyMap.get("Output"));
	}

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		setProperty("processStarter", new FakeProcessStarter(properties));
		executeMojo(GenFromExistingMojo.class);
	}

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		setProperty("processStarter", new FakeProcessStarter(properties));
		executeMojo(GenFromExistingMojo.class);
	}

	@Override
	public void setStage(HashMap<String, String> keyMap) {
		setProperty("stage", keyMap.get("Stage"));
	}

	@Override
	public void setModelGreen(HashMap<String, String> keyMap) {
		setProperty("modelGreen", keyMap.get("ModelGreen"));
	}

	@Override
	public void setModelRefactor(HashMap<String, String> keyMap) {
		setProperty("modelRefactor", keyMap.get("ModelRefactor"));
	}

	@Override
	public void setGitCommandExecutedToReportTheCurrentBranch(HashMap<String, String> keyMap) {
		// Marker step; the canned branch value is seeded via setGitCommandGitBranch.
	}

	@Override
	public void setGitCommandGitBranch(HashMap<String, String> keyMap) {
		setProperty("gitBranchCanned", keyMap.get("GitBranch"));
	}

	@Override
	public void setGitBranch(HashMap<String, String> keyMap) {
		String value = keyMap.get("GitBranch");
		// "Empty" is the DSL sentinel for an unset value; the init-time check
		// treats null/empty identically and fails with "gitBranch parameter is required".
		if ("Empty".equals(value)) {
			value = "";
		}
		setProperty("gitBranch", value);
	}

}
