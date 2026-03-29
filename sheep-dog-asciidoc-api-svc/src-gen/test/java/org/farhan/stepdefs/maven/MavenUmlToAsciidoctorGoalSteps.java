package org.farhan.stepdefs.maven;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.maven.UmlToAsciidoctorGoal;

public class MavenUmlToAsciidoctorGoalSteps extends TestSteps {

    public MavenUmlToAsciidoctorGoalSteps(UmlToAsciidoctorGoal object) {
        super(object, "maven", "uml-to-asciidoctor");
    }

    @Given("^The maven plugin uml-to-asciidoctor goal is executed$")
    public void isExecuted() {
        object.doEdgeStep("", "", "is", "executed");
    }
}
