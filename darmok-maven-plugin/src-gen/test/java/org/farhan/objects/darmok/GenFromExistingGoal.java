package org.farhan.objects.darmok;

import java.util.HashMap;

public interface GenFromExistingGoal {

    public void setClaudeRgrGreenCommandExecutedAndSuccessfulAfterRetries(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandPattern(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandExecutedButFailedNonRetryably(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandExit(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandOutput(HashMap<String, String> keyMap);

    public void setClaudeRgrGreenCommandExecutedButFailsAfterReachingRetryLimit(HashMap<String, String> keyMap);

    public void setClaudeRgrRefactorCommandExecutedAndSuccessfulAfterRetries(HashMap<String, String> keyMap);

    public void setClaudeRgrRefactorCommandPattern(HashMap<String, String> keyMap);

    public void setGitCommandExecutedToVerifyTheWorkspaceIsClean(HashMap<String, String> keyMap);

    public void setExecuted(HashMap<String, String> keyMap);

    public void setMvnAsciidoctorToUmlCommandExecutedButFailed(HashMap<String, String> keyMap);

    public void setMvnAsciidoctorToUmlCommandExit(HashMap<String, String> keyMap);

    public void setMvnAsciidoctorToUmlCommandOutput(HashMap<String, String> keyMap);

    public void setMvnTestCommandExecutedButFailed(HashMap<String, String> keyMap);

    public void setMvnTestCommandExit(HashMap<String, String> keyMap);

    public void setMvnTestCommandOutput(HashMap<String, String> keyMap);

    public void setMvnUmlToCucumberGuiceCommandExecutedButFailed(HashMap<String, String> keyMap);

    public void setMvnUmlToCucumberGuiceCommandExit(HashMap<String, String> keyMap);

    public void setMvnUmlToCucumberGuiceCommandOutput(HashMap<String, String> keyMap);

    public void setExecutedWith(HashMap<String, String> keyMap);

    public void setStage(HashMap<String, String> keyMap);

    public void setModelGreen(HashMap<String, String> keyMap);

    public void setModelRefactor(HashMap<String, String> keyMap);
}
