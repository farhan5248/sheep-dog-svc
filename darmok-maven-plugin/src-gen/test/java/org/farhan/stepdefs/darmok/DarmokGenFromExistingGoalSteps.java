package org.farhan.stepdefs.darmok;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.darmok.GenFromExistingGoal;

public class DarmokGenFromExistingGoalSteps extends TestSteps {

    public DarmokGenFromExistingGoalSteps(GenFromExistingGoal object) {
        super(object, "darmok", "gen-from-existing");
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is executed and successful after retries$")
    public void claudeRgrGreenCommandIsExecutedAndSuccessfulAfterRetries(DataTable dataTable) {
        object.doEdgeStep("claude /rgr-green", "command", "is", "executed and successful after retries", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is executed but failed non-retryably$")
    public void claudeRgrGreenCommandIsExecutedButFailedNonRetryably(DataTable dataTable) {
        object.doEdgeStep("claude /rgr-green", "command", "is", "executed but failed non-retryably", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is executed but fails after reaching retry limit$")
    public void claudeRgrGreenCommandIsExecutedButFailsAfterReachingRetryLimit(DataTable dataTable) {
        object.doEdgeStep("claude /rgr-green", "command", "is", "executed but fails after reaching retry limit", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-refactor command is executed and successful after retries$")
    public void claudeRgrRefactorCommandIsExecutedAndSuccessfulAfterRetries(DataTable dataTable) {
        object.doEdgeStep("claude /rgr-refactor", "command", "is", "executed and successful after retries", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal git command is executed to report the current branch$")
    public void gitCommandIsExecutedToReportTheCurrentBranch(DataTable dataTable) {
        object.doEdgeStep("git", "command", "is", "executed to report the current branch", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal git command is executed to verify the workspace is clean$")
    public void gitCommandIsExecutedToVerifyTheWorkspaceIsClean() {
        object.doEdgeStep("git", "command", "is", "executed to verify the workspace is clean");
    }

    @Given("^The darmok plugin gen-from-existing goal is executed$")
    public void isExecuted() {
        object.doEdgeStep("", "", "is", "executed");
    }

    @Given("^The darmok plugin gen-from-existing goal is executed with$")
    public void isExecutedWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn asciidoctor-to-uml command is executed but failed$")
    public void mvnAsciidoctorToUmlCommandIsExecutedButFailed(DataTable dataTable) {
        object.doEdgeStep("mvn asciidoctor-to-uml", "command", "is", "executed but failed", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn test command is executed but failed$")
    public void mvnTestCommandIsExecutedButFailed(DataTable dataTable) {
        object.doEdgeStep("mvn test", "command", "is", "executed but failed", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn uml-to-cucumber-guice command is executed but failed$")
    public void mvnUmlToCucumberGuiceCommandIsExecutedButFailed(DataTable dataTable) {
        object.doEdgeStep("mvn uml-to-cucumber-guice", "command", "is", "executed but failed", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean verify command is executed but fails for all attempts$")
    public void mvnCleanVerifyCommandIsExecutedButFailsForAllAttempts(DataTable dataTable) {
        object.doEdgeStep("mvn clean verify", "command", "is", "executed but fails for all attempts", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean verify command is executed but fails once then succeeds$")
    public void mvnCleanVerifyCommandIsExecutedButFailsOnceThenSucceeds(DataTable dataTable) {
        object.doEdgeStep("mvn clean verify", "command", "is", "executed but fails once then succeeds", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is hung on every call$")
    public void claudeRgrGreenCommandIsHungOnEveryCall() {
        object.doEdgeStep("claude /rgr-green", "command", "is", "hung on every call");
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is hung on first call then completed on resume$")
    public void claudeRgrGreenCommandIsHungOnFirstCallThenCompletedOnResume() {
        object.doEdgeStep("claude /rgr-green", "command", "is", "hung on first call then completed on resume");
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is hung until killed$")
    public void claudeRgrGreenCommandIsHungUntilKilled() {
        object.doEdgeStep("claude /rgr-green", "command", "is", "hung until killed");
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-refactor command is hung on every call$")
    public void claudeRgrRefactorCommandIsHungOnEveryCall() {
        object.doEdgeStep("claude /rgr-refactor", "command", "is", "hung on every call");
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-refactor command is hung on first call then completed on resume$")
    public void claudeRgrRefactorCommandIsHungOnFirstCallThenCompletedOnResume() {
        object.doEdgeStep("claude /rgr-refactor", "command", "is", "hung on first call then completed on resume");
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean install command is executed and fails on every call in the green phase$")
    public void mvnCleanInstallCommandIsExecutedAndFailsOnEveryCallInTheGreenPhase() {
        object.doEdgeStep("mvn clean install", "command", "is", "executed and fails on every call in the green phase");
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean install command is executed and fails on every call in the refactor phase$")
    public void mvnCleanInstallCommandIsExecutedAndFailsOnEveryCallInTheRefactorPhase() {
        object.doEdgeStep("mvn clean install", "command", "is", "executed and fails on every call in the refactor phase");
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean install command is executed and fails then passes in the green phase$")
    public void mvnCleanInstallCommandIsExecutedAndFailsThenPassesInTheGreenPhase(DataTable dataTable) {
        object.doEdgeStep("mvn clean install", "command", "is", "executed and fails then passes in the green phase", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean install command is executed and fails then passes in the refactor phase$")
    public void mvnCleanInstallCommandIsExecutedAndFailsThenPassesInTheRefactorPhase(DataTable dataTable) {
        object.doEdgeStep("mvn clean install", "command", "is", "executed and fails then passes in the refactor phase", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is exited but its stdout stays open$")
    public void claudeRgrGreenCommandIsExitedButItsStdoutStaysOpen() {
        object.doEdgeStep("claude /rgr-green", "command", "is", "exited but its stdout stays open");
    }
}
