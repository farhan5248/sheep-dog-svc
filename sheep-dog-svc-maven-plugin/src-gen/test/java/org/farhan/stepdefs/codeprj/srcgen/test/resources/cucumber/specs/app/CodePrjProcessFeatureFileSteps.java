package org.farhan.stepdefs.codeprj.srcgen.test.resources.cucumber.specs.app;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.srcgen.test.resources.cucumber.specs.app.ProcessFeatureFile;

public class CodePrjProcessFeatureFileSteps extends TestSteps {

    public CodePrjProcessFeatureFileSteps(ProcessFeatureFile object) {
        super(object, "code-prj", "src-gen/test/resources/cucumber/specs/app/Process.feature");
    }

    @Given("^The code-prj project src-gen/test/resources/cucumber/specs/app/Process.feature file is created as follows$")
    public void isCreatedAsFollows(String docString) {
        object.setVertexStep("", "", "is", "created as follows", docString);
    }

    @Given("^The code-prj project src-gen/test/resources/cucumber/specs/app/Process.feature file will be created as follows$")
    public void willBeCreatedAsFollows(String docString) {
        object.assertVertexStep("", "", "will be", "created as follows", docString);
    }
}
