package org.farhan.stepdefs.codeprj.srcgen.test.java.org.farhan.objects.blah;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.srcgen.test.java.org.farhan.objects.blah.ObjectPageJavaFile;

public class CodePrjObjectPageJavaFileSteps extends TestSteps {

    public CodePrjObjectPageJavaFileSteps(ObjectPageJavaFile object) {
        super(object, "code-prj", "src-gen/test/java/org/farhan/objects/blah/ObjectPage.java");
    }

    @Given("^The code-prj project src-gen/test/java/org/farhan/objects/blah/ObjectPage.java file is created as follows$")
    public void isCreatedAsFollows(String docString) {
        object.setVertexStep("", "", "is", "created as follows", docString);
    }

    @Given("^The code-prj project src-gen/test/java/org/farhan/objects/blah/ObjectPage.java file will be created as follows$")
    public void willBeCreatedAsFollows(String docString) {
        object.assertVertexStep("", "", "will be", "created as follows", docString);
    }
}
