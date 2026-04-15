package org.farhan.stepdefs.maven;

import com.google.inject.Inject;
import io.cucumber.datatable.DataTable;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.maven.GenFromComparisonGoal;

@ScenarioScoped
public class MavenGenFromComparisonGoalSteps extends TestSteps {

    @Inject
    public MavenGenFromComparisonGoalSteps(GenFromComparisonGoal object) {
        super(object, "maven", "gen-from-comparison");
    }

    @Given("^The maven plugin gen-from-comparison goal is executed with$")
    public void isExecutedWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed with", dataTable);
    }
}
