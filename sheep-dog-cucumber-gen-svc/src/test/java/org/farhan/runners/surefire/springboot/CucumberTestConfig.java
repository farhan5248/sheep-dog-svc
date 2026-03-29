package org.farhan.runners.surefire.springboot;

import java.io.File;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
public class CucumberTestConfig extends SpringBootTestConfig {

	public static String scenarioId;

	public void deleteDir(File aDir) {
		if (aDir.exists()) {
			for (String s : aDir.list()) {
				File f = new File(aDir.getAbsolutePath() + File.separator + s);
				if (f.isDirectory()) {
					deleteDir(f);
				}
				f.delete();
			}
		}
	}

	@Before
	public void before(Scenario scenario) {
		deleteDir(new File("target/src-gen/"));
		scenarioId = scenario.getName();
	}
}