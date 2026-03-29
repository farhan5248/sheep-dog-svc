package org.farhan.stepdefs.specprj.src.test.resources.asciidoc.specs.app;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.specprj.src.test.resources.asciidoc.specs.app.ProcessAsciidocFile;

public class SpecPrjProcessAsciidocFileSteps extends TestSteps {

    public SpecPrjProcessAsciidocFileSteps(ProcessAsciidocFile object) {
        super(object, "spec-prj", "src/test/resources/asciidoc/specs/app/Process.asciidoc");
    }

    @Given("^The spec-prj project src/test/resources/asciidoc/specs/app/Process.asciidoc file is created as follows$")
    public void isCreatedAsFollows(String docString) {
        object.setVertexStep("", "", "is", "created as follows", docString);
    }

    @Given("^The spec-prj project src/test/resources/asciidoc/specs/app/Process.asciidoc file will be created as follows$")
    public void willBeCreatedAsFollows(String docString) {
        object.assertVertexStep("", "", "will be", "created as follows", docString);
    }
}
