package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.nio.file.Path;
import java.util.HashMap;

import org.apache.maven.project.MavenProject;
import org.farhan.common.TestObject;
import org.farhan.mbt.maven.GenFromComparisonMojo;
import org.farhan.objects.maven.GenFromComparisonGoal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class GenFromComparisonGoalImpl extends TestObject implements GenFromComparisonGoal {

	@Override
	public void setExecutedWith(HashMap<String, String> keyMap) {
		execute();
	}

	@Override
	public void setModelComparison(HashMap<String, String> keyMap) {
		setProperty("ModelComparison", keyMap.get("ModelComparison"));
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
			throw new IllegalStateException("code-prj.baseDir not set");
		}

		MavenProject project = new MavenProject();
		project.setArtifactId("code-prj");

		GenFromComparisonMojo mojo = new GenFromComparisonMojo();
		mojo.project = project;
		mojo.setBaseDir(codePrjDir.toString());
		mojo.specsDir = "../../spec-prj";
		mojo.asciidocDir = "../../spec-prj/src/test/resources/asciidoc/specs";
		mojo.scenariosFile = "scenarios-list.txt";
		mojo.host = "dev.sheepdog.io";
		mojo.modelRed = "sonnet";
		mojo.modelGreen = "sonnet";
		mojo.modelRefactor = "sonnet";
		mojo.coAuthor = "Claude Sonnet 4.5 <noreply@anthropic.com>";
		mojo.maxRetries = 3;
		mojo.retryWaitSeconds = 30;
		mojo.stage = true;
		mojo.onlyChanges = true;
		if (getProperty("ModelComparison") != null) {
			mojo.modelComparison = getProperty("ModelComparison").toString();
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
