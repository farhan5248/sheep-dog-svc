package org.farhan.stepdefs.maven;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.maven.UmlToCucumberGuiceGoal;

public class MavenUmlToCucumberGuiceGoalSteps extends TestSteps {

    public MavenUmlToCucumberGuiceGoalSteps(UmlToCucumberGuiceGoal object) {
        super(object, "maven", "uml-to-cucumber-guice");
    }

    @Given("^The maven plugin uml-to-cucumber-guice goal is executed$")
    public void isExecuted() {
        object.doEdgeStep("", "", "is", "executed");
    }
}
