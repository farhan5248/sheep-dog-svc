package org.farhan.stepdefs.codeprj.src.main.java.org.farhan.objects;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.src.main.java.org.farhan.objects.LogoutHappyPathJavaFile;

public class CodePrjLogoutHappyPathJavaFileSteps extends TestSteps {

    public CodePrjLogoutHappyPathJavaFileSteps(LogoutHappyPathJavaFile object) {
        super(object, "code-prj", "src/main/java/org/farhan/objects/LogoutHappyPath.java");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LogoutHappyPath.java file is created$")
    public void isCreated() {
        object.setVertexStep("", "", "is", "created");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LogoutHappyPath.java file isn't created$")
    public void isntCreated() {
        object.setVertexStep("", "", "isn't", "created");
    }
}
