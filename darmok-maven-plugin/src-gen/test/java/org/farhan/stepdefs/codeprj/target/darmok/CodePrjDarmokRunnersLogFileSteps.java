package org.farhan.stepdefs.codeprj.target.darmok;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.target.darmok.DarmokRunnersLogFile;

public class CodePrjDarmokRunnersLogFileSteps extends TestSteps {

    public CodePrjDarmokRunnersLogFileSteps(DarmokRunnersLogFile object) {
        super(object, "code-prj", "target/darmok/darmok.runners.log");
    }

    @Given("^The code-prj project target/darmok/darmok.runners.log file will be as follows$")
    public void willBeAsFollows(DataTable dataTable) {
        object.assertVertexStep("", "", "will be", "as follows", dataTable);
    }
}
