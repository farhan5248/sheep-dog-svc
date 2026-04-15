package org.farhan.stepdefs.maven;

import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import org.farhan.common.TestSteps;
import org.farhan.objects.maven.GenFromExistingGoal;

@ScenarioScoped
public class MavenGenFromExistingGoalSteps extends TestSteps {

    @Inject
    public MavenGenFromExistingGoalSteps(GenFromExistingGoal object) {
        super(object, "maven", "gen-from-existing");
    }

    @Given("^The maven plugin gen-from-existing goal is executed$")
    public void isExecuted() {
        object.doEdgeStep("", "", "is", "executed");
    }

    @Given("^The maven plugin gen-from-existing goal is executed with$")
    public void isExecutedWith() {
        object.doEdgeStep("", "", "is", "executed with");
    }
}
