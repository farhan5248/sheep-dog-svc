package org.farhan.stepdefs.codeprj.target.darmok;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.target.darmok.DarmokMojoLogFile;

public class CodePrjDarmokMojoLogFileSteps extends TestSteps {

    public CodePrjDarmokMojoLogFileSteps(DarmokMojoLogFile object) {
        super(object, "code-prj", "target/darmok/darmok.mojo.log");
    }

    @Given("^The code-prj project target/darmok/darmok.mojo.log file will be as follows$")
    public void willBeAsFollows(DataTable dataTable) {
        object.assertVertexStep("", "", "will be", "as follows", dataTable);
    }

    @Given("^The code-prj project target/darmok/darmok.mojo.log file won't be as follows$")
    public void wontBeAsFollows(DataTable dataTable) {
        object.assertVertexStep("", "", "won't be", "as follows", dataTable);
    }
}
