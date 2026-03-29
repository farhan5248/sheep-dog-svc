package org.farhan.stepdefs.maven;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.maven.CucumberToUmlGoal;

public class MavenCucumberToUmlGoalSteps extends TestSteps {

    public MavenCucumberToUmlGoalSteps(CucumberToUmlGoal object) {
        super(object, "maven", "cucumber-to-uml");
    }

    @Given("^The maven plugin cucumber-to-uml goal is executed with$")
    public void isExecutedWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed with", dataTable);
    }
}
