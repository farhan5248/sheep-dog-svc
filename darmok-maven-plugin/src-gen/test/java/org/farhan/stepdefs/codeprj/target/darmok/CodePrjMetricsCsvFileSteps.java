package org.farhan.stepdefs.codeprj.target.darmok;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.target.darmok.MetricsCsvFile;

public class CodePrjMetricsCsvFileSteps extends TestSteps {

    public CodePrjMetricsCsvFileSteps(MetricsCsvFile object) {
        super(object, "code-prj", "target/darmok/metrics.csv");
    }

    @Given("^The code-prj project target/darmok/metrics.csv file will be as follows$")
    public void willBeAsFollows(DataTable dataTable) {
        object.assertVertexStep("", "", "will be", "as follows", dataTable);
    }
}
