package org.farhan.stepdefs.codeprj;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.MetricsCsvFile;

public class CodePrjMetricsCsvFileSteps extends TestSteps {

    public CodePrjMetricsCsvFileSteps(MetricsCsvFile object) {
        super(object, "code-prj", "metrics.csv");
    }

    @Given("^The code-prj project metrics.csv file will be absent$")
    public void willBeAbsent() {
        object.assertVertexStep("", "", "will be", "absent");
    }

    @Given("^The code-prj project metrics.csv file will be as follows$")
    public void willBeAsFollows(DataTable dataTable) {
        object.assertVertexStep("", "", "will be", "as follows", dataTable);
    }
}
