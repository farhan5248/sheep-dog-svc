package org.farhan.stepdefs.maven;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.maven.GenFromExistingGoal;

public class MavenGenFromExistingGoalSteps extends TestSteps {

    public MavenGenFromExistingGoalSteps(GenFromExistingGoal object) {
        super(object, "maven", "gen-from-existing");
    }

    @Given("^The maven plugin gen-from-existing goal is executed$")
    public void isExecuted() {
        object.doEdgeStep("", "", "is", "executed");
    }

    @Given("^The maven plugin gen-from-existing goal is executed with$")
    public void isExecutedWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed with", dataTable);
    }
}
