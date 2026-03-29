package org.farhan.stepdefs.maven;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.maven.UmlToCucumberSpringGoal;

public class MavenUmlToCucumberSpringGoalSteps extends TestSteps {

    public MavenUmlToCucumberSpringGoalSteps(UmlToCucumberSpringGoal object) {
        super(object, "maven", "uml-to-cucumber-spring");
    }

    @Given("^The maven plugin uml-to-cucumber-spring goal is executed$")
    public void isExecuted() {
        object.doEdgeStep("", "", "is", "executed");
    }
}
