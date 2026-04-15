package org.farhan.stepdefs.specprj.src.test.resources.asciidoc.specs;

import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.specprj.src.test.resources.asciidoc.specs.ProcessDarmokAsciidocFile;

@ScenarioScoped
public class SpecPrjProcessDarmokAsciidocFileSteps extends TestSteps {

    @Inject
    public SpecPrjProcessDarmokAsciidocFileSteps(ProcessDarmokAsciidocFile object) {
        super(object, "spec-prj", "src/test/resources/asciidoc/specs/ProcessDarmok.asciidoc");
    }

    @Given("^The spec-prj project src/test/resources/asciidoc/specs/ProcessDarmok.asciidoc file is created as follows$")
    public void isCreatedAsFollows(String docString) {
        object.setVertexStep("", "", "is", "created as follows", docString);
    }

    @Given("^The spec-prj project src/test/resources/asciidoc/specs/ProcessDarmok.asciidoc file will be as follows$")
    public void willBeAsFollows() {
        object.assertVertexStep("", "", "will be", "as follows");
    }

    @Given("^The spec-prj project src/test/resources/asciidoc/specs/ProcessDarmok.asciidoc file will be created as follows$")
    public void willBeCreatedAsFollows(String docString) {
        object.assertVertexStep("", "", "will be", "created as follows", docString);
    }
}
