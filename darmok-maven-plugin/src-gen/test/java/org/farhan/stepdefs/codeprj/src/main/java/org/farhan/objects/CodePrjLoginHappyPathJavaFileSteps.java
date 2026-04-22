package org.farhan.stepdefs.codeprj.src.main.java.org.farhan.objects;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.src.main.java.org.farhan.objects.LoginHappyPathJavaFile;

public class CodePrjLoginHappyPathJavaFileSteps extends TestSteps {

    public CodePrjLoginHappyPathJavaFileSteps(LoginHappyPathJavaFile object) {
        super(object, "code-prj", "src/main/java/org/farhan/objects/LoginHappyPath.java");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file is created$")
    public void isCreated() {
        object.setVertexStep("", "", "is", "created");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file isn't created$")
    public void isntCreated() {
        object.setVertexStep("", "", "isn't", "created");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file will be absent$")
    public void willBeAbsent() {
        object.assertVertexStep("", "", "will be", "absent");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file will be present$")
    public void willBePresent() {
        object.assertVertexStep("", "", "will be", "present");
    }
}
