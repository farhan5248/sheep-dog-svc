package org.farhan.stepdefs.codeprj;

import com.google.inject.Inject;
import io.cucumber.datatable.DataTable;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.ScenariosListTxtFile;

@ScenarioScoped
public class CodePrjScenariosListTxtFileSteps extends TestSteps {

    @Inject
    public CodePrjScenariosListTxtFileSteps(ScenariosListTxtFile object) {
        super(object, "code-prj", "scenarios-list.txt");
    }

    @Given("^The code-prj project scenarios-list.txt file is created as follows$")
    public void isCreatedAsFollows(String docString) {
        object.setVertexStep("", "", "is", "created as follows", docString);
    }

    @Given("^The code-prj project scenarios-list.txt file will be as follows$")
    public void willBeAsFollows(DataTable dataTable) {
        object.assertVertexStep("", "", "will be", "as follows", dataTable);
    }
}
