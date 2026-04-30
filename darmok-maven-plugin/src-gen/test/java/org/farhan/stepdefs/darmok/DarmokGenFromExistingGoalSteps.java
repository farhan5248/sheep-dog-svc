package org.farhan.stepdefs.darmok;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.darmok.GenFromExistingGoal;

public class DarmokGenFromExistingGoalSteps extends TestSteps {

    public DarmokGenFromExistingGoalSteps(GenFromExistingGoal object) {
        super(object, "darmok", "gen-from-existing");
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is executed and succeeds with$")
    public void claudeRgrGreenCommandIsExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("claude /rgr-green", "command", "is", "executed and succeeds with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is executed but fails with$")
    public void claudeRgrGreenCommandIsExecutedButFailsWith(DataTable dataTable) {
        object.doEdgeStep("claude /rgr-green", "command", "is", "executed but fails with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-green command is exited but its stdout stays open$")
    public void claudeRgrGreenCommandIsExitedButItsStdoutStaysOpen() {
        object.doEdgeStep("claude /rgr-green", "command", "is", "exited but its stdout stays open");
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

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-refactor command is executed and succeeds with$")
    public void claudeRgrRefactorCommandIsExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("claude /rgr-refactor", "command", "is", "executed and succeeds with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-refactor command is hung on every call$")
    public void claudeRgrRefactorCommandIsHungOnEveryCall() {
        object.doEdgeStep("claude /rgr-refactor", "command", "is", "hung on every call");
    }

    @Given("^The darmok plugin gen-from-existing goal claude /rgr-refactor command is hung on first call then completed on resume$")
    public void claudeRgrRefactorCommandIsHungOnFirstCallThenCompletedOnResume() {
        object.doEdgeStep("claude /rgr-refactor", "command", "is", "hung on first call then completed on resume");
    }

    @Given("^The darmok plugin gen-from-existing goal claude command is executed and succeeds with$")
    public void claudeCommandIsExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("claude", "command", "is", "executed and succeeds with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal git command is executed and succeeds with$")
    public void gitCommandIsExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("git", "command", "is", "executed and succeeds with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal is executed and succeeds$")
    public void isExecutedAndSucceeds() {
        object.doEdgeStep("", "", "is", "executed and succeeds");
    }

    @Given("^The darmok plugin gen-from-existing goal is executed and succeeds with$")
    public void isExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed and succeeds with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal is executed but fails$")
    public void isExecutedButFails() {
        object.doEdgeStep("", "", "is", "executed but fails");
    }

    @Given("^The darmok plugin gen-from-existing goal is executed but fails with$")
    public void isExecutedButFailsWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed but fails with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn asciidoctor-to-uml command is executed but failed$")
    public void mvnAsciidoctorToUmlCommandIsExecutedButFailed(DataTable dataTable) {
        object.doEdgeStep("mvn asciidoctor-to-uml", "command", "is", "executed but failed", dataTable);
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

    @Given("^The darmok plugin gen-from-existing goal mvn clean install command is executed but fails with$")
    public void mvnCleanInstallCommandIsExecutedButFailsWith(DataTable dataTable) {
        object.doEdgeStep("mvn clean install", "command", "is", "executed but fails with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean verify command is executed but fails for all attempts$")
    public void mvnCleanVerifyCommandIsExecutedButFailsForAllAttempts(DataTable dataTable) {
        object.doEdgeStep("mvn clean verify", "command", "is", "executed but fails for all attempts", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn clean verify command is executed but fails once then succeeds$")
    public void mvnCleanVerifyCommandIsExecutedButFailsOnceThenSucceeds(DataTable dataTable) {
        object.doEdgeStep("mvn clean verify", "command", "is", "executed but fails once then succeeds", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn test command is executed but failed$")
    public void mvnTestCommandIsExecutedButFailed(DataTable dataTable) {
        object.doEdgeStep("mvn test", "command", "is", "executed but failed", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn uml-to-cucumber-guice command is executed but failed$")
    public void mvnUmlToCucumberGuiceCommandIsExecutedButFailed(DataTable dataTable) {
        object.doEdgeStep("mvn uml-to-cucumber-guice", "command", "is", "executed but failed", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn asciidoctor-to-uml command is executed and succeeds with$")
    public void mvnAsciidoctorToUmlCommandIsExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("mvn asciidoctor-to-uml", "command", "is", "executed and succeeds with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn asciidoctor-to-uml command is executed but fails with$")
    public void mvnAsciidoctorToUmlCommandIsExecutedButFailsWith(DataTable dataTable) {
        object.doEdgeStep("mvn asciidoctor-to-uml", "command", "is", "executed but fails with", dataTable);
    }

    @Given("^The darmok plugin gen-from-existing goal mvn uml-to-cucumber-guice command is executed and succeeds with$")
    public void mvnUmlToCucumberGuiceCommandIsExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("mvn uml-to-cucumber-guice", "command", "is", "executed and succeeds with", dataTable);
    }
}
