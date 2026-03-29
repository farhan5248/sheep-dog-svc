package org.farhan.runners.failsafe;

import java.io.File;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;

@ComponentScan(basePackages = {"org.farhan.impl"})
@EnableAutoConfiguration
@ActiveProfiles("failsafe")
@CucumberContextConfiguration
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource("classpath:application-failsafe.properties")
public class TestConfig {

	public static String getWorkingDir() {
		return "target/src-gen/";
	}

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
	public void resetTestProject() {
		deleteDir(new File(getWorkingDir()));
	}

}