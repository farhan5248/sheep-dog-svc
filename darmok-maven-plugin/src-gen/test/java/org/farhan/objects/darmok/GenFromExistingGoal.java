package org.farhan.objects.darmok;

import java.util.HashMap;

public interface GenFromExistingGoal {

    public void setClaudeRgrGreenCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandPattern(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandExecutedButFailsWith(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandExit(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandOutput(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandExitedButItsStdoutStaysOpen(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandHungOnEveryCall(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandHungOnFirstCallThenCompletedOnResume(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandHungUntilKilled(HashMap<String, String> keyMap);

    public void setClaudeRgrRefactorCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap);

    public void setClaudeRgrRefactorCommandPattern(HashMap<String, String> keyMap);

    public void setClaudeRgrRefactorCommandHungOnEveryCall(HashMap<String, String> keyMap);

    public void setClaudeRgrRefactorCommandHungOnFirstCallThenCompletedOnResume(HashMap<String, String> keyMap);

    public void setClaudeCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap);

    public void setClaudeCommandCommandParameters(HashMap<String, String> keyMap);

    public void setClaudeCommandAttempt(HashMap<String, String> keyMap);

    public void setClaudeCommandPath(HashMap<String, String> keyMap);

    public void setGitCommandExecutedAndSucceedsWith(HashMap<String, String> keyMap);

    public void setGitCommandCommandParameters(HashMap<String, String> keyMap);

    public void setGitCommandGitBranch(HashMap<String, String> keyMap);

    public void setExecutedAndSucceeds(HashMap<String, String> keyMap);

    public void setExecutedAndSucceedsWith(HashMap<String, String> keyMap);

    public void setAllowlistAdditionalPaths(HashMap<String, String> keyMap);

    public void setBaselineVerifyEnabled(HashMap<String, String> keyMap);

    public void setClaudeSessionIdEnabled(HashMap<String, String> keyMap);

    public void setGitBranch(HashMap<String, String> keyMap);

    public void setMaxClaudeSeconds(HashMap<String, String> keyMap);

    public void setMaxTimeoutAttempts(HashMap<String, String> keyMap);

    public void setStage(HashMap<String, String> keyMap);

    public void setModelGreen(HashMap<String, String> keyMap);

    public void setModelRefactor(HashMap<String, String> keyMap);

    public void setRefactorSessionMode(HashMap<String, String> keyMap);

    public void setSvcMavenPluginGoal(HashMap<String, String> keyMap);

    public void setExecutedButFails(HashMap<String, String> keyMap);

    public void setExecutedButFailsWith(HashMap<String, String> keyMap);

    public void setAllowlistBasePaths(HashMap<String, String> keyMap);

    public void setMvnAsciidoctorToUmlCommandExecutedButFailed(HashMap<String, String> keyMap);

    public void setMvnAsciidoctorToUmlCommandExit(HashMap<String, String> keyMap);

    public void setMvnAsciidoctorToUmlCommandOutput(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandExecutedAndFailsOnEveryCallInTheGreenPhase(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandExecutedAndFailsOnEveryCallInTheRefactorPhase(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandExecutedAndFailsThenPassesInTheGreenPhase(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandAttempt(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandExit(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandExecutedAndFailsThenPassesInTheRefactorPhase(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandExecutedButFailsWith(HashMap<String, String> keyMap);

    public void setMvnCleanInstallCommandPhase(HashMap<String, String> keyMap);

    public void setMvnCleanVerifyCommandExecutedButFailsForAllAttempts(HashMap<String, String> keyMap);

    public void setMvnCleanVerifyCommandPhase(HashMap<String, String> keyMap);

    public void setMvnCleanVerifyCommandExecutedButFailsOnceThenSucceeds(HashMap<String, String> keyMap);

    public void setMvnTestCommandExecutedButFailed(HashMap<String, String> keyMap);

    public void setMvnTestCommandExit(HashMap<String, String> keyMap);

    public void setMvnTestCommandOutput(HashMap<String, String> keyMap);

    public void setMvnUmlToCucumberGuiceCommandExecutedButFailed(HashMap<String, String> keyMap);

    public void setMvnUmlToCucumberGuiceCommandExit(HashMap<String, String> keyMap);

    public void setMvnUmlToCucumberGuiceCommandOutput(HashMap<String, String> keyMap);

    public void setGreenPromptTemplateEnabled(HashMap<String, String> keyMap);
}
