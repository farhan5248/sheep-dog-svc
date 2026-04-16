package org.farhan.stepdefs.codeprj.src.main.java.org.farhan.objects;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.codeprj.src.main.java.org.farhan.objects.LogoutHappyPathJavaFile;

public class CodePrjLogoutHappyPathJavaFileSteps extends TestSteps {

    public CodePrjLogoutHappyPathJavaFileSteps(LogoutHappyPathJavaFile object) {
        super(object, "code-prj", "src/main/java/org/farhan/objects/LogoutHappyPath.java");
    }

    @Given("^The code-prj project src/main/java/org/farhan/objects/LogoutHappyPath.java file is created as follows$")
    public void isCreatedAsFollows(DataTable dataTable) {
        object.setVertexStep("", "", "is", "created as follows", dataTable);
    }
}
