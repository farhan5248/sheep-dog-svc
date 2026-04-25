package org.farhan.stepdefs.codeprj.src.test.java.org.farhan.suites;

import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.src.test.java.org.farhan.suites.LoginHappyPathTestJavaFile;

public class CodePrjLoginHappyPathTestJavaFileSteps extends TestSteps {

    public CodePrjLoginHappyPathTestJavaFileSteps(LoginHappyPathTestJavaFile object) {
        super(object, "code-prj", "src/test/java/org/farhan/suites/loginHappyPathTest.java");
    }

    @Given("^The code-prj project src/test/java/org/farhan/suites/loginHappyPathTest.java file will be created as follows$")
    public void willBeCreatedAsFollows(String docString) {
        object.assertVertexStep("", "", "will be", "created as follows", docString);
    }
}
