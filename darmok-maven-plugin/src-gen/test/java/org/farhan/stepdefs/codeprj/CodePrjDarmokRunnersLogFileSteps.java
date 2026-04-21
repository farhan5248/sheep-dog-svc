package org.farhan.stepdefs.codeprj;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.DarmokRunnersLogFile;

public class CodePrjDarmokRunnersLogFileSteps extends TestSteps {

    public CodePrjDarmokRunnersLogFileSteps(DarmokRunnersLogFile object) {
        super(object, "code-prj", "darmok.runners.log");
    }

    @Given("^The code-prj project darmok.runners.log file will be as follows$")
    public void willBeAsFollows(DataTable dataTable) {
        object.assertVertexStep("", "", "will be", "as follows", dataTable);
    }

    @Given("^The code-prj project darmok.runners.log file will be as follows with this failure$")
    public void willBeAsFollowsWithThisFailure(DataTable dataTable) {
        object.assertVertexStep("", "", "will be", "as follows with this failure", dataTable);
    }

    @Given("^The code-prj project darmok.runners.log file will be empty$")
    public void willBeEmpty() {
        object.assertVertexStep("", "", "will be", "empty");
    }
}
