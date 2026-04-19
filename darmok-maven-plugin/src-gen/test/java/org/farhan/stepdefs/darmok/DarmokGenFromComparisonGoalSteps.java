package org.farhan.stepdefs.darmok;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.darmok.GenFromComparisonGoal;

public class DarmokGenFromComparisonGoalSteps extends TestSteps {

    public DarmokGenFromComparisonGoalSteps(GenFromComparisonGoal object) {
        super(object, "darmok", "gen-from-comparison");
    }

    @Given("^The darmok plugin gen-from-comparison goal is executed with$")
    public void isExecutedWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed with", dataTable);
    }
}
