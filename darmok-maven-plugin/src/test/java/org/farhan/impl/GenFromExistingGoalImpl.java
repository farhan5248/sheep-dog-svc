package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.HashMap;

import org.farhan.fake.ClaudeRunnerFake;
import org.farhan.fake.GitRunnerFake;
import org.farhan.fake.MavenRunnerFake;
import org.farhan.common.MavenTestObject;
import org.farhan.mbt.maven.GenFromExistingMojo;
import org.farhan.objects.darmok.GenFromExistingGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class GenFromExistingGoalImpl extends MavenTestObject implements GenFromExistingGoal {

	@Override
	public void setClaudeRgrGreenCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap) {
		setProperty("claudeGreenMode", "retry-success");
	}

	@Override
	public void setClaudeRgrGreenCommandExecutedButFailsWith(HashMap<String, String> keyMap) {
		// Pattern column present → retry-exhaust; Exit column present → non-retryable
		if (getProperty("claudeGreenPattern") != null) {
			setProperty("claudeGreenMode", "retry-exhaust");
		} else {
			setProperty("claudeGreenMode", "non-retryable");
		}
	}

	@Override
	public void setClaudeRgrRefactorCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap) {
		setProperty("claudeRefactorMode", "retry-success");
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
	public void setClaudeRgrRefactorCommandPattern(HashMap<String, String> keyMap) {
		setProperty("claudeRefactorPattern", keyMap.get("Pattern"));
	}

	@Override
	public void setMvnAsciidoctorToUmlCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap) {
		// Marker step; actual state is seeded via the column-specific setters.
	}

	@Override
	public void setMvnAsciidoctorToUmlCommandPattern(HashMap<String, String> keyMap) {
		setProperty("mvnAsciidoctorPattern", keyMap.get("Pattern"));
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
	public void setExecutedAndSucceeds(HashMap<String, String> keyMap) {
		setProperty("claude", new ClaudeRunnerFake(properties));
		setProperty("maven", new MavenRunnerFake(properties));
		setProperty("git", new GitRunnerFake(properties));
		runGoal(GenFromExistingMojo.class, getProperty("code-prj.baseDir").toString());
	}

	@Override
	public void setExecutedButFails(HashMap<String, String> keyMap) {
		setProperty("claude", new ClaudeRunnerFake(properties));
		setProperty("maven", new MavenRunnerFake(properties));
		setProperty("git", new GitRunnerFake(properties));
		runGoal(GenFromExistingMojo.class, getProperty("code-prj.baseDir").toString());
	}

	@Override
	public void setExecutedAndSucceedsWith(HashMap<String, String> keyMap) {
		setProperty("claude", new ClaudeRunnerFake(properties));
		setProperty("maven", new MavenRunnerFake(properties));
		setProperty("git", new GitRunnerFake(properties));
		runGoal(GenFromExistingMojo.class, getProperty("code-prj.baseDir").toString());
	}

	@Override
	public void setExecutedButFailsWith(HashMap<String, String> keyMap) {
		setProperty("claude", new ClaudeRunnerFake(properties));
		setProperty("maven", new MavenRunnerFake(properties));
		setProperty("git", new GitRunnerFake(properties));
		runGoal(GenFromExistingMojo.class, getProperty("code-prj.baseDir").toString());
	}

	@Override
	public void setGitCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap) {
		// Marker step; the canned branch value is seeded via setGitCommandGitBranch.
	}

	@Override
	public void setGitCommandCommandParameters(HashMap<String, String> keyMap) {
		String cmd = keyMap.get("Command Parameters");
		if ("diff --cached --quiet".equals(cmd)) {
			setProperty("gitWorkspaceState", "clean");
		}
		// rev-parse --abbrev-ref HEAD: GitBranch column setter primes gitBranchCanned.
	}

	@Override
	public void setStage(HashMap<String, String> keyMap) {
		setProperty("stage", keyMap.get("Stage"));
		setProperty("targetProject", "darmok-prj");
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

	@Override
	public void setMvnCleanVerifyCommandPhase(HashMap<String, String> keyMap) {
		setProperty("mvnVerifyPhase", keyMap.get("Phase"));
	}

	@Override
	public void setMvnCleanVerifyCommandExecutedButFailsOnceThenSucceeds(HashMap<String, String> keyMap) {
		String key = "Refactor".equalsIgnoreCase((String) getProperty("mvnVerifyPhase"))
			? "mvnVerifyModeRefactor" : "mvnVerifyModeGreen";
		setProperty(key, "fail-once");
	}

	@Override
	public void setMvnCleanVerifyCommandExecutedButFailsForAllAttempts(HashMap<String, String> keyMap) {
		String key = "Refactor".equalsIgnoreCase((String) getProperty("mvnVerifyPhase"))
			? "mvnVerifyModeRefactor" : "mvnVerifyModeGreen";
		setProperty(key, "fail-all");
	}

	// =========================================================================
	// Issue 140 — phase timeout recovery
	// =========================================================================

	@Override
	public void setClaudeRgrGreenCommandHungUntilKilled(HashMap<String, String> keyMap) {
		setProperty("claudeGreenHangMode", "hung-until-killed");
	}

	@Override
	public void setClaudeRgrGreenCommandHungOnFirstCallThenCompletedOnResume(HashMap<String, String> keyMap) {
		setProperty("claudeGreenHangMode", "hung-first");
	}

	@Override
	public void setClaudeRgrGreenCommandHungOnEveryCall(HashMap<String, String> keyMap) {
		setProperty("claudeGreenHangMode", "hung-every");
	}

	@Override
	public void setClaudeRgrGreenCommandExitedButItsStdoutStaysOpen(HashMap<String, String> keyMap) {
		setProperty("claudeGreenHangMode", "exits-reader-blocked");
	}

	@Override
	public void setClaudeRgrRefactorCommandHungOnFirstCallThenCompletedOnResume(HashMap<String, String> keyMap) {
		setProperty("claudeRefactorHangMode", "hung-first");
	}

	@Override
	public void setClaudeRgrRefactorCommandHungOnEveryCall(HashMap<String, String> keyMap) {
		setProperty("claudeRefactorHangMode", "hung-every");
	}

	@Override
	public void setMvnCleanInstallCommandExecutedAndFailsThenPassesInTheGreenPhase(HashMap<String, String> keyMap) {
		setProperty("mvnInstallModeGreen", "fail-once-then-pass");
	}

	@Override
	public void setMvnCleanInstallCommandExecutedAndFailsOnEveryCallInTheGreenPhase(HashMap<String, String> keyMap) {
		setProperty("mvnInstallModeGreen", "fail-all");
	}

	@Override
	public void setMvnCleanInstallCommandExecutedAndFailsThenPassesInTheRefactorPhase(HashMap<String, String> keyMap) {
		setProperty("mvnInstallModeRefactor", "fail-once-then-pass");
	}

	@Override
	public void setMvnCleanInstallCommandExecutedAndFailsOnEveryCallInTheRefactorPhase(HashMap<String, String> keyMap) {
		setProperty("mvnInstallModeRefactor", "fail-all");
	}

	@Override
	public void setMvnCleanInstallCommandAttempt(HashMap<String, String> keyMap) {
		// No-op: the mode setter above carries all state; the Attempt/Exit columns
		// are purely documentary for spec readers.
	}

	@Override
	public void setMvnCleanInstallCommandExit(HashMap<String, String> keyMap) {
		// No-op: see setMvnCleanInstallCommandAttempt.
	}

	@Override
	public void setClaudeCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap) {
		// Marker step; actual state is seeded via the column-specific setters below.
	}

	@Override
	public void setClaudeCommandCommandParameters(HashMap<String, String> keyMap) {
		setProperty("claudeCommandParameters", keyMap.get("Command Parameters"));
	}

	@Override
	public void setClaudeCommandAttempt(HashMap<String, String> keyMap) {
		setProperty("claudeCommandAttempt", keyMap.get("Attempt"));
	}

	@Override
	public void setClaudeCommandPath(HashMap<String, String> keyMap) {
		setProperty("claudeCommandPath", keyMap.get("Path"));
	}

	@Override
	public void setMaxClaudeSeconds(HashMap<String, String> keyMap) {
		setProperty("maxClaudeSeconds", keyMap.get("MaxClaudeSeconds"));
	}

	@Override
	public void setMaxTimeoutAttempts(HashMap<String, String> keyMap) {
		setProperty("maxTimeoutAttempts", keyMap.get("MaxTimeoutAttempts"));
	}

	@Override
	public void setClaudeSessionIdEnabled(HashMap<String, String> keyMap) {
		setProperty("claudeSessionIdEnabled", keyMap.get("ClaudeSessionIdEnabled"));
	}

	@Override
	public void setBaselineVerifyEnabled(HashMap<String, String> keyMap) {
		setProperty("baselineVerifyEnabled", keyMap.get("BaselineVerifyEnabled"));
	}

	@Override
	public void setMvnCleanInstallCommandExecutedButFailsWith(HashMap<String, String> keyMap) {
		String phase = (String) getProperty("mvnInstallPhase");
		if ("Baseline".equalsIgnoreCase(phase)) {
			setProperty("mvnInstallModeBaseline", "fail-all");
		}
	}

	@Override
	public void setMvnCleanInstallCommandPhase(HashMap<String, String> keyMap) {
		setProperty("mvnInstallPhase", keyMap.get("Phase"));
	}

	@Override
	public void setRefactorSessionMode(HashMap<String, String> keyMap) {
		setProperty("refactorSessionMode", keyMap.get("RefactorSessionMode"));
	}

	@Override
	public void setAllowlistAdditionalPaths(HashMap<String, String> keyMap) {
		setProperty("allowlistAdditionalPaths", keyMap.get("AllowlistAdditionalPaths"));
	}

	@Override
	public void setAllowlistBasePaths(HashMap<String, String> keyMap) {
		setProperty("allowlistBasePaths", keyMap.get("AllowlistBasePaths"));
	}

	@Override
	public void setSvcMavenPluginGoal(HashMap<String, String> keyMap) {
		setProperty("svcMavenPluginGoal", keyMap.get("SvcMavenPluginGoal"));
	}

	@Override
	public void setScenariosFile(HashMap<String, String> keyMap) {
		String newName = keyMap.get("ScenariosFile");
		setProperty("scenariosFile", newName);
		String basePath = getProperty("code-prj.componentPath") + "/";
		String content = getFileContent(basePath + "scenarios-list.txt");
		if (content != null) {
			writeFile(basePath + newName, content);
		}
	}

}
