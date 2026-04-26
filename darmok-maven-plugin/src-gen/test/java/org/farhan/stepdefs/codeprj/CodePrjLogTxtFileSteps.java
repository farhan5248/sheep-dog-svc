package org.farhan.stepdefs.codeprj;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.LogTxtFile;

public class CodePrjLogTxtFileSteps extends TestSteps {

    public CodePrjLogTxtFileSteps(LogTxtFile object) {
        super(object, "code-prj", "log.txt");
    }

    @Given("^The code-prj project log.txt file will be present$")
    public void willBePresent() {
        object.assertVertexStep("", "", "will be", "present");
    }
}
