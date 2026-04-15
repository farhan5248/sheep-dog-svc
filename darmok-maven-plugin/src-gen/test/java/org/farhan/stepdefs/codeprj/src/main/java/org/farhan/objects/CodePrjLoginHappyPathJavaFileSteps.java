package org.farhan.stepdefs.codeprj.src.main.java.org.farhan.objects;

import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.src.main.java.org.farhan.objects.LoginHappyPathJavaFile;

@ScenarioScoped
public class CodePrjLoginHappyPathJavaFileSteps extends TestSteps {

    @Inject
    public CodePrjLoginHappyPathJavaFileSteps(LoginHappyPathJavaFile object) {
        super(object, "code-prj", "src/main/java/org/farhan/objects/LoginHappyPath.java");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file is created as follows$")
    public void isCreatedAsFollows() {
        object.setVertexStep("", "", "is", "created as follows");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LoginHappyPath.java file will be as follows$")
    public void willBeAsFollows() {
        object.assertVertexStep("", "", "will be", "as follows");
    }
}
