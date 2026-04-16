package org.farhan.stepdefs.codeprj;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.ScenariosListTxtFile;

public class CodePrjScenariosListTxtFileSteps extends TestSteps {

    public CodePrjScenariosListTxtFileSteps(ScenariosListTxtFile object) {
        super(object, "code-prj", "scenarios-list.txt");
    }

    @Given("^The code-prj project scenarios-list.txt file is created as follows$")
    public void isCreatedAsFollows(String docString) {
        object.setVertexStep("", "", "is", "created as follows", docString);
    }

    @Given("^The code-prj project scenarios-list.txt file is created without any scenarios$")
    public void isCreatedWithoutAnyScenarios() {
        object.setVertexStep("", "", "is", "created without any scenarios");
    }

    @Given("^The code-prj project scenarios-list.txt file isn't created$")
    public void isntCreated() {
        object.setVertexStep("", "", "isn't", "created");
    }

    @Given("^The code-prj project scenarios-list.txt file will be absent$")
    public void willBeAbsent() {
        object.assertVertexStep("", "", "will be", "absent");
    }

    @Given("^The code-prj project scenarios-list.txt file will be empty$")
    public void willBeEmpty() {
        object.assertVertexStep("", "", "will be", "empty");
    }
}
