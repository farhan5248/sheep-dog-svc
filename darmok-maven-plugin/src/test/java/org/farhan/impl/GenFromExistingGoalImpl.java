package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.nio.file.Path;
import java.util.HashMap;

import org.apache.maven.project.MavenProject;
import org.farhan.common.TestObject;
import org.farhan.mbt.maven.GenFromExistingMojo;
import org.farhan.objects.maven.GenFromExistingGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Impl for the {@code gen-from-existing} Darmok goal.
 *
 * <p>
 * Programmatically instantiates {@link GenFromExistingMojo}, wires a stub
 * {@link MavenProject} pointing at the scenario's {@code code-prj} temp dir,
 * applies any buffered parameters (Stage / ModelGreen / ModelRefactor) from prior
 * setter calls, and invokes {@link GenFromExistingMojo#execute()}.
 *
 * <p>
 * Exceptions thrown by the mojo are caught and recorded in properties so that
 * failure-path Test-Cases can assert on observable log state rather than on the
 * exception itself.
 */
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class GenFromExistingGoalImpl extends TestObject implements GenFromExistingGoal {

	@Override
	public void setExecuted(HashMap<String, String> keyMap) {
		execute();
	}

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		execute();
	}

	@Override
	public void setStage(HashMap<String, String> keyMap) {
		setProperty("Stage", keyMap.get("Stage"));
	}

	@Override
	public void setModelGreen(HashMap<String, String> keyMap) {
		setProperty("ModelGreen", keyMap.get("ModelGreen"));
	}

	@Override
	public void setModelRefactor(HashMap<String, String> keyMap) {
		setProperty("ModelRefactor", keyMap.get("ModelRefactor"));
	}

	private void execute() {
		Path codePrjDir = (Path) getProperty("code-prj.baseDir");
		if (codePrjDir == null) {
			throw new IllegalStateException("code-prj.baseDir not set — TestConfig @Before didn't run?");
		}

		MavenProject project = new MavenProject();
		project.setArtifactId("code-prj");

		GenFromExistingMojo mojo = new GenFromExistingMojo();
		mojo.project = project;
		mojo.setBaseDir(codePrjDir.toString());
		if (getProperty("Stage") != null) {
			mojo.stage = Boolean.parseBoolean(getProperty("Stage").toString());
		}
		if (getProperty("ModelGreen") != null) {
			mojo.modelGreen = getProperty("ModelGreen").toString();
		}
		if (getProperty("ModelRefactor") != null) {
			mojo.modelRefactor = getProperty("ModelRefactor").toString();
		}

		try {
			mojo.execute();
			setProperty("goal.exception", null);
		} catch (Exception e) {
			setProperty("goal.exception", e);
		}
	}
}
