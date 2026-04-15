package org.farhan.stepdefs.codeprj.src.main.java.org.farhan.objects;

import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.src.main.java.org.farhan.objects.LogoutHappyPathJavaFile;

@ScenarioScoped
public class CodePrjLogoutHappyPathJavaFileSteps extends TestSteps {

    @Inject
    public CodePrjLogoutHappyPathJavaFileSteps(LogoutHappyPathJavaFile object) {
        super(object, "code-prj", "src/main/java/org/farhan/objects/LogoutHappyPath.java");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LogoutHappyPath.java file will be as follows$")
    public void willBeAsFollows() {
        object.assertVertexStep("", "", "will be", "as follows");
    }
}
