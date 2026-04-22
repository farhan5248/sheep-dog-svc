package org.farhan.stepdefs.darmok;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.darmok.GenFromComparisonGoal;

public class DarmokGenFromComparisonGoalSteps extends TestSteps {

    public DarmokGenFromComparisonGoalSteps(GenFromComparisonGoal object) {
        super(object, "darmok", "gen-from-comparison");
    }

    @Given("^The darmok plugin gen-from-comparison goal is executed and succeeds with$")
    public void isExecutedAndSucceedsWith(DataTable dataTable) {
        object.doEdgeStep("", "", "is", "executed and succeeds with", dataTable);
    }
}
